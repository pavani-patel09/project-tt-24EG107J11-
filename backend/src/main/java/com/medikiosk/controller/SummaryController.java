package com.medikiosk.controller;

import com.medikiosk.dto.SummaryDTOs.ClinicalSummaryVerifyRequest;
import com.medikiosk.model.*;
import com.medikiosk.repository.ClinicalSummaryRepository;
import com.medikiosk.repository.VisitRepository;
import com.medikiosk.service.AuditService;
import com.medikiosk.service.AuthService;
import com.medikiosk.service.ClinicalSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/summaries")
@CrossOrigin
public class SummaryController {

    @Autowired
    private ClinicalSummaryRepository clinicalSummaryRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private ClinicalSummaryService clinicalSummaryService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuditService auditService;

    @PostMapping("/generate/{visitId}")
    public ResponseEntity<ClinicalSummary> generateSummary(@PathVariable("visitId") Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found"));

        if (visit.getIntake() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot generate clinical summary before clinical intake is recorded");
        }

        Patient patient = visit.getPatient();
        ClinicalSummaryService.SummaryData data = clinicalSummaryService.generateSummary(patient, visit.getIntake(), patient.getDocuments());

        ClinicalSummary summary = clinicalSummaryRepository.findByVisitId(visit.getId()).orElse(new ClinicalSummary());
        summary.setVisit(visit);
        summary.setPatient(patient);
        summary.setChiefComplaint(data.chiefComplaint);
        summary.setHpi(data.hpi);
        summary.setPastMedicalHistory(data.pastMedicalHistory);
        summary.setCurrentMedications(data.currentMedications);
        summary.setAllergies(data.allergies);
        summary.setKeyFindingsFromDocs(data.keyFindingsFromDocs);
        summary.setMissingClinicalInfo(data.missingClinicalInfo);
        summary.setFollowUpRecommendations(data.followUpRecommendations);
        summary.setAiModelUsed(data.aiModelUsed);
        summary.setUpdatedAt(LocalDateTime.now());

        ClinicalSummary saved = clinicalSummaryRepository.save(summary);

        User currentUser = authService.getCurrentUser();
        auditService.log(currentUser != null ? currentUser.getId() : null, patient.getId(), "SUMMARY_GENERATED", "Clinical summary generated for visit " + visit.getVisitToken() + " using " + saved.getAiModelUsed());

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<ClinicalSummary> getSummaryByVisit(@PathVariable("visitId") Long visitId) {
        return ResponseEntity.ok(clinicalSummaryRepository.findByVisitId(visitId).orElse(null));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<ClinicalSummary> verifySummary(
            @PathVariable("id") Long id,
            @RequestBody ClinicalSummaryVerifyRequest request) {

        ClinicalSummary summary = clinicalSummaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Summary not found"));

        User doctor = authService.getCurrentUser();
        if (doctor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required for clinical verification");
        }

        summary.setIsDoctorVerified(true);
        summary.setVerifiedByDoctor(doctor);
        summary.setVerifiedAt(LocalDateTime.now());
        summary.setDoctorNotes(request.getDoctorNotes());

        if (request.getChiefComplaint() != null) summary.setChiefComplaint(request.getChiefComplaint());
        if (request.getHpi() != null) summary.setHpi(request.getHpi());
        if (request.getPastMedicalHistory() != null) summary.setPastMedicalHistory(request.getPastMedicalHistory());
        if (request.getCurrentMedications() != null) summary.setCurrentMedications(request.getCurrentMedications());
        if (request.getAllergies() != null) summary.setAllergies(request.getAllergies());
        summary.setUpdatedAt(LocalDateTime.now());

        Visit v = summary.getVisit();
        if (v != null && "awaiting_review".equals(v.getStatus())) {
            v.setStatus("with_doctor");
            visitRepository.save(v);
        }

        ClinicalSummary saved = clinicalSummaryRepository.save(summary);
        auditService.log(doctor.getId(), summary.getPatient().getId(), "SUMMARY_VERIFIED", "Summary #" + summary.getId() + " verified by Dr. " + doctor.getFullName());

        return ResponseEntity.ok(saved);
    }
}
