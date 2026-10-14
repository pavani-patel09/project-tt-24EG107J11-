package com.medikiosk.service;

import com.medikiosk.dto.VisitDTOs.*;
import com.medikiosk.model.*;
import com.medikiosk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class VisitService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private IntakeRecordRepository intakeRecordRepository;

    @Autowired
    private ClinicalSummaryRepository clinicalSummaryRepository;

    @Autowired
    private ClinicalSummaryService clinicalSummaryService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuthService authService;

    private static final List<Pattern> RED_FLAG_PATTERNS = Arrays.asList(
            Pattern.compile("(?i)\\bchest\\s+(?:pain|pressure|discomfort|tightness)\\b"),
            Pattern.compile("(?i)\\b(?:shortness\\s+of\\s+breath|difficulty\\s+breathing|breathless|gasping)\\b"),
            Pattern.compile("(?i)\\b(?:fainting|syncope|unconscious|blackout)\\b"),
            Pattern.compile("(?i)\\b(?:paralysis|stroke|facial\\s+droop|slurred\\s+speech)\\b"),
            Pattern.compile("(?i)\\b(?:hemoptysis|coughing\\s+blood|vomiting\\s+blood)\\b"),
            Pattern.compile("(?i)\\b(?:severe\\s+headache|thunderclap|worst\\s+headache)\\b")
    );

    public String generateVisitToken(String department) {
        String dept = department != null ? department.toLowerCase() : "";
        String prefix = "A";
        if (dept.contains("surg")) prefix = "S";
        else if (dept.contains("paed")) prefix = "P";
        else if (dept.contains("ayush") || dept.contains("ayur")) prefix = "Y";

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int num = 100 + random.nextInt(900);
            String token = prefix + "-" + num;
            if (visitRepository.findByVisitToken(token).isEmpty()) {
                return token;
            }
        }
        return prefix + "-" + (1000 + random.nextInt(9000));
    }

    public static class RedFlagResult {
        public boolean isRedFlag;
        public String priority;
        public String reason;

        public RedFlagResult(boolean isRedFlag, String priority, String reason) {
            this.isRedFlag = isRedFlag;
            this.priority = priority;
            this.reason = reason;
        }
    }

    public RedFlagResult evaluateRedFlags(String complaint, String associated, String severity) {
        String text = ((complaint != null ? complaint : "") + " " + (associated != null ? associated : "")).toLowerCase();
        for (Pattern p : RED_FLAG_PATTERNS) {
            if (p.matcher(text).find()) {
                return new RedFlagResult(true, "red_flag", "Red flag detected from reported symptoms. Prioritized for clinical safety.");
            }
        }
        if ("Severe".equalsIgnoreCase(severity)) {
            return new RedFlagResult(false, "attention", "Reported symptom severity marked as severe. Needs prompt doctor evaluation.");
        }
        return new RedFlagResult(false, "routine", "Standard screening profile.");
    }

    public Visit createVisit(VisitCreateRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        String token = generateVisitToken(req.getDepartment());
        Visit visit = new Visit(patient, token, req.getDepartment(), "in_kiosk", req.getPriority() != null ? req.getPriority() : "routine");
        return visitRepository.save(visit);
    }

    @Transactional
    public Visit submitClinicalIntake(IntakeCreateRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        Visit visit = null;
        if (req.getVisitId() != null) {
            visit = visitRepository.findById(req.getVisitId()).orElse(null);
        }

        if (visit == null) {
            String token = generateVisitToken(req.getDepartment());
            visit = new Visit(patient, token, req.getDepartment(), "in_kiosk", "routine");
            visit = visitRepository.save(visit);
        }

        // Screen Red Flags
        RedFlagResult rf = evaluateRedFlags(req.getChiefComplaint(), req.getAssociatedSymptoms(), req.getSeverity());
        visit.setIsRedFlag(rf.isRedFlag);
        visit.setPriority(rf.priority);
        visit.setRedFlagReason(rf.reason);
        visit.setStatus("awaiting_review");

        // Save Intake Record
        IntakeRecord intake = new IntakeRecord();
        intake.setVisit(visit);
        intake.setPatientId(patient.getId());
        intake.setChiefComplaint(req.getChiefComplaint());
        intake.setSymptomOnset(req.getSymptomOnset());
        intake.setSymptomDuration(req.getSymptomDuration());
        intake.setSeverity(req.getSeverity() != null ? req.getSeverity() : "Moderate");
        intake.setAssociatedSymptoms(req.getAssociatedSymptoms());
        intake.setChangesSinceLastVisit(req.getChangesSinceLastVisit());
        intake.setRecentlyTakenMeds(req.getRecentlyTakenMeds());
        intake.setNewlyReportedAllergies(req.getNewlyReportedAllergies());
        intake.setLanguageUsed(req.getLanguageUsed() != null ? req.getLanguageUsed() : "en");
        intake.setSubmittedAt(LocalDateTime.now());
        intake = intakeRecordRepository.save(intake);
        visit.setIntake(intake);

        // Auto-generate Clinical Summary
        ClinicalSummaryService.SummaryData summaryData = clinicalSummaryService.generateSummary(patient, intake, patient.getDocuments());
        ClinicalSummary summary = clinicalSummaryRepository.findByVisitId(visit.getId()).orElse(new ClinicalSummary());
        summary.setVisit(visit);
        summary.setPatient(patient);
        summary.setChiefComplaint(summaryData.chiefComplaint);
        summary.setHpi(summaryData.hpi);
        summary.setPastMedicalHistory(summaryData.pastMedicalHistory);
        summary.setCurrentMedications(summaryData.currentMedications);
        summary.setAllergies(summaryData.allergies);
        summary.setKeyFindingsFromDocs(summaryData.keyFindingsFromDocs);
        summary.setMissingClinicalInfo(summaryData.missingClinicalInfo);
        summary.setFollowUpRecommendations(summaryData.followUpRecommendations);
        summary.setAiModelUsed(summaryData.aiModelUsed);
        clinicalSummaryRepository.save(summary);
        visit.setClinicalSummary(summary);

        visit = visitRepository.save(visit);

        User currentUser = authService.getCurrentUser();
        auditService.log(currentUser != null ? currentUser.getId() : null, patient.getId(), "INTAKE_SUBMITTED", "Intake submitted for token " + visit.getVisitToken() + ", priority " + visit.getPriority());

        return visit;
    }

    public List<VisitQueueItem> getVisitsQueue(String dept, String priority, String status) {
        List<Visit> visits = visitRepository.findFilteredQueue(dept, priority, status);
        List<VisitQueueItem> list = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        for (Visit v : visits) {
            Patient p = v.getPatient();
            IntakeRecord in = v.getIntake();
            ClinicalSummary s = v.getClinicalSummary();

            VisitQueueItem item = new VisitQueueItem();
            item.setId(v.getId());
            item.setPatientId(p != null ? p.getId() : null);
            item.setPatientUid(p != null ? p.getPatientUid() : "N/A");
            item.setPatientName(p != null ? p.getFullName() : "Unknown");
            item.setAge(p != null ? p.getAge() : null);
            item.setGender(p != null ? p.getGender() : null);
            item.setBloodGroup(p != null ? p.getBloodGroup() : null);
            item.setPhone(p != null ? p.getPhone() : null);
            item.setToken(v.getVisitToken());
            item.setDepartment(v.getDepartment());
            item.setVisitDate(v.getVisitDate() != null ? v.getVisitDate().format(fmt) : "");
            item.setStatus(v.getStatus());
            item.setPriority(v.getPriority());
            item.setIsRedFlag(v.getIsRedFlag());
            item.setRedFlagReason(v.getRedFlagReason());
            item.setChiefComplaint(in != null ? in.getChiefComplaint() : "Waiting for intake");
            item.setHasAiSummary(s != null);
            item.setIsSummaryVerified(s != null && Boolean.TRUE.equals(s.getIsDoctorVerified()));

            list.add(item);
        }

        return list;
    }

    public Map<String, Object> updateVisitStatus(Long id, VisitStatusUpdateRequest req) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));

        if (req.getStatus() != null) visit.setStatus(req.getStatus());
        if (req.getPriority() != null) visit.setPriority(req.getPriority());

        visitRepository.save(visit);

        User currentUser = authService.getCurrentUser();
        auditService.log(currentUser != null ? currentUser.getId() : null, visit.getPatient() != null ? visit.getPatient().getId() : null, "VISIT_STATUS_UPDATED", "Visit " + visit.getVisitToken() + " updated to " + visit.getStatus());

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Status updated successfully");
        resp.put("visit_id", visit.getId());
        resp.put("status", visit.getStatus());
        return resp;
    }
}
