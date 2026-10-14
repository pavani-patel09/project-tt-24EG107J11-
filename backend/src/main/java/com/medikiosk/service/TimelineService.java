package com.medikiosk.service;

import com.medikiosk.dto.SummaryDTOs.TimelineEventDTO;
import com.medikiosk.model.*;
import com.medikiosk.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TimelineService {

    @Autowired
    private PatientRepository patientRepository;

    public List<TimelineEventDTO> getPatientTimeline(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        List<TimelineEventDTO> events = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // 1. Chronic Conditions
        for (ChronicCondition c : patient.getChronicConditions()) {
            String dateStr = c.getDiagnosedDate() != null ? c.getDiagnosedDate() : (c.getCreatedAt() != null ? c.getCreatedAt().format(fmt) : "Historical");
            events.add(new TimelineEventDTO(
                    "cond-" + c.getId(),
                    dateStr,
                    "diagnosis",
                    "Diagnosis: " + c.getConditionName(),
                    "Status: " + c.getStatus() + ". ICD: " + (c.getIcd10Code() != null ? c.getIcd10Code() : "N/A"),
                    "chronic_condition",
                    c.getId(),
                    false,
                    true
            ));
        }

        // 2. Medications
        for (Medication m : patient.getMedications()) {
            String dateStr = m.getStartDate() != null ? m.getStartDate() : (m.getCreatedAt() != null ? m.getCreatedAt().format(fmt) : "Ongoing");
            events.add(new TimelineEventDTO(
                    "med-" + m.getId(),
                    dateStr,
                    "medication",
                    "Medication: " + m.getMedicineName(),
                    m.getDosage() + ", " + m.getFrequency() + ". Prescribed by: " + (m.getPrescribingDoctor() != null ? m.getPrescribingDoctor() : "Not recorded"),
                    "medication",
                    m.getId(),
                    false,
                    true
            ));
        }

        // 3. Documents
        for (MedicalDocument d : patient.getDocuments()) {
            Map<String, String> fieldMap = new HashMap<>();
            if (d.getExtractedFields() != null) {
                for (ExtractedField ef : d.getExtractedFields()) {
                    String val = ef.getCorrectedValue() != null ? ef.getCorrectedValue() : ef.getExtractedValue();
                    fieldMap.put(ef.getFieldName(), val);
                }
            }
            String dateStr = fieldMap.getOrDefault("Date", d.getCreatedAt() != null ? d.getCreatedAt().format(fmt) : "Uploaded");

            List<String> details = new ArrayList<>();
            if (fieldMap.containsKey("Hospital / Facility")) details.add("Facility: " + fieldMap.get("Hospital / Facility"));
            if (fieldMap.containsKey("Doctor")) details.add("Doctor: " + fieldMap.get("Doctor"));
            if (fieldMap.containsKey("Diagnosis") && !"Not recorded".equals(fieldMap.get("Diagnosis"))) details.add("Diagnosis: " + fieldMap.get("Diagnosis"));
            if (fieldMap.containsKey("Medicines & Dosages") && !"Not recorded".equals(fieldMap.get("Medicines & Dosages"))) details.add("Medicines: " + fieldMap.get("Medicines & Dosages"));
            if (fieldMap.containsKey("Lab Test & Result") && !"Not recorded".equals(fieldMap.get("Lab Test & Result"))) details.add("Lab Result: " + fieldMap.get("Lab Test & Result"));

            String desc = !details.isEmpty() ? String.join(" | ", details) : "Uploaded " + d.getOriginalFilename();
            String cat = d.getDocType() != null && d.getDocType().toLowerCase().contains("lab") ? "lab" : "document";

            events.add(new TimelineEventDTO(
                    "doc-" + d.getId(),
                    dateStr,
                    cat,
                    d.getDocType() != null ? d.getDocType().replace("_", " ").toUpperCase() + ": " + d.getOriginalFilename() : "DOCUMENT: " + d.getOriginalFilename(),
                    desc,
                    "medical_document",
                    d.getId(),
                    false,
                    d.getIsVerified()
            ));
        }

        // 4. Visits
        LocalDate today = LocalDate.now();
        for (Visit v : patient.getVisits()) {
            boolean isToday = v.getVisitDate() != null && v.getVisitDate().toLocalDate().isEqual(today);
            String dateStr = isToday ? "Today's Visit" : (v.getVisitDate() != null ? v.getVisitDate().format(fmt) : "Past Visit");

            IntakeRecord in = v.getIntake();
            StringBuilder desc = new StringBuilder("Token: " + v.getVisitToken() + " (" + v.getDepartment() + "). Status: " + v.getStatus() + ".");
            if (in != null) {
                desc.append(" Complaint: ").append(in.getChiefComplaint()).append(" (").append(in.getSeverity()).append(").");
            }
            if (Boolean.TRUE.equals(v.getIsRedFlag())) {
                desc.append(" [SCREENING FLAG: ").append(v.getRedFlagReason()).append("]");
            }

            events.add(new TimelineEventDTO(
                    "visit-" + v.getId(),
                    dateStr,
                    "visit",
                    "Clinical Consultation (" + v.getDepartment() + ")",
                    desc.toString(),
                    "visit",
                    v.getId(),
                    isToday,
                    "completed".equals(v.getStatus())
            ));
        }

        // Sort events: Current visit first
        events.sort((a, b) -> {
            if (Boolean.TRUE.equals(a.getIsCurrentVisit())) return -1;
            if (Boolean.TRUE.equals(b.getIsCurrentVisit())) return 1;
            return 0;
        });

        return events;
    }
}
