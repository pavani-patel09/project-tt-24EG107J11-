package com.medikiosk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medikiosk.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ClinicalSummaryService {

    @Value("${medikiosk.gemini.api-key:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class SummaryData {
        public String chiefComplaint;
        public String hpi;
        public String pastMedicalHistory;
        public String currentMedications;
        public String allergies;
        public String keyFindingsFromDocs;
        public String missingClinicalInfo;
        public String followUpRecommendations;
        public String aiModelUsed;
    }

    public SummaryData generateSummary(Patient patient, IntakeRecord intake, List<MedicalDocument> documents) {
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty()) {
            try {
                SummaryData llmData = callGeminiApi(patient, intake, documents);
                if (llmData != null) return llmData;
            } catch (Exception e) {
                System.err.println("Gemini LLM call failed, falling back to rule engine: " + e.getMessage());
            }
        }
        return generateRuleBasedSummary(patient, intake, documents);
    }

    public SummaryData generateRuleBasedSummary(Patient patient, IntakeRecord intake, List<MedicalDocument> documents) {
        SummaryData data = new SummaryData();

        String complaint = (intake != null && intake.getChiefComplaint() != null) ? intake.getChiefComplaint() : "Not recorded";
        String duration = (intake != null && intake.getSymptomDuration() != null) ? intake.getSymptomDuration() : "unspecified duration";
        String onset = (intake != null && intake.getSymptomOnset() != null) ? intake.getSymptomOnset() : "recent";
        String severity = (intake != null && intake.getSeverity() != null) ? intake.getSeverity() : "Moderate";
        String associated = (intake != null && intake.getAssociatedSymptoms() != null) ? intake.getAssociatedSymptoms() : "None reported";
        String recentMeds = (intake != null && intake.getRecentlyTakenMeds() != null) ? intake.getRecentlyTakenMeds() : "None";
        String changes = (intake != null && intake.getChangesSinceLastVisit() != null) ? intake.getChangesSinceLastVisit() : "No prior visit changes recorded";

        // HPI
        StringBuilder hpiSb = new StringBuilder();
        hpiSb.append("Patient reports ").append(complaint.toLowerCase())
             .append(" with onset ").append(onset)
             .append(" and duration of ").append(duration).append(". ")
             .append("Severity is described as ").append(severity).append(". ");
        if (!"None reported".equalsIgnoreCase(associated) && !associated.isEmpty()) {
            hpiSb.append("Associated symptoms: ").append(associated).append(". ");
        }
        if (!"No prior visit changes recorded".equalsIgnoreCase(changes) && !changes.isEmpty()) {
            hpiSb.append("Changes noted since previous visit: ").append(changes).append(". ");
        }
        data.hpi = hpiSb.toString().trim();
        data.chiefComplaint = complaint;

        // Past Medical History
        List<ChronicCondition> conditions = patient.getChronicConditions();
        if (conditions != null && !conditions.isEmpty()) {
            List<String> condList = new ArrayList<>();
            for (ChronicCondition c : conditions) {
                condList.add(c.getConditionName() + " (Status: " + c.getStatus() + ")");
            }
            data.pastMedicalHistory = String.join(", ", condList);
        } else {
            data.pastMedicalHistory = "No documented chronic illnesses on record.";
        }

        // Medications
        List<Medication> meds = patient.getMedications();
        if (meds != null && !meds.isEmpty()) {
            List<String> medList = new ArrayList<>();
            for (Medication m : meds) {
                if (Boolean.TRUE.equals(m.getIsActive())) {
                    medList.add(m.getMedicineName() + " " + m.getDosage() + " (" + m.getFrequency() + ")");
                }
            }
            data.currentMedications = !medList.isEmpty() ? String.join(", ", medList) : "None currently active";
        } else {
            data.currentMedications = "No regular medications on record.";
        }
        if (recentMeds != null && !recentMeds.equalsIgnoreCase("None") && !recentMeds.equalsIgnoreCase("Not recorded") && !recentMeds.isEmpty()) {
            data.currentMedications += " | Patient also reported taking recently: " + recentMeds + ".";
        }

        // Allergies
        List<Allergy> allergies = patient.getAllergies();
        if (allergies != null && !allergies.isEmpty()) {
            List<String> allList = new ArrayList<>();
            for (Allergy a : allergies) {
                allList.add(a.getAllergen() + " (Severity: " + a.getSeverity() + ")");
            }
            data.allergies = String.join(", ", allList);
        } else {
            data.allergies = "No known drug or environmental allergies recorded.";
        }
        if (intake != null && intake.getNewlyReportedAllergies() != null && !intake.getNewlyReportedAllergies().equalsIgnoreCase("None") && !intake.getNewlyReportedAllergies().isEmpty()) {
            data.allergies += " | Newly reported at intake: " + intake.getNewlyReportedAllergies() + " (Requires clinician confirmation).";
        }

        // Documents Findings
        if (documents != null && !documents.isEmpty()) {
            List<String> docFindings = new ArrayList<>();
            for (MedicalDocument doc : documents) {
                StringBuilder part = new StringBuilder();
                String docType = doc.getDocType() != null ? doc.getDocType().replace("_", " ") : "Document";
                part.append(Character.toUpperCase(docType.charAt(0))).append(docType.substring(1)).append(": ");

                Map<String, String> fieldMap = new HashMap<>();
                if (doc.getExtractedFields() != null) {
                    for (ExtractedField ef : doc.getExtractedFields()) {
                        String val = ef.getCorrectedValue() != null ? ef.getCorrectedValue() : ef.getExtractedValue();
                        fieldMap.put(ef.getFieldName(), val);
                    }
                }
                List<String> details = new ArrayList<>();
                if (fieldMap.containsKey("Diagnosis") && !"Not recorded".equals(fieldMap.get("Diagnosis"))) {
                    details.add("Diagnosis: " + fieldMap.get("Diagnosis"));
                }
                if (fieldMap.containsKey("Medicines & Dosages") && !"Not recorded".equals(fieldMap.get("Medicines & Dosages"))) {
                    details.add("Meds: " + fieldMap.get("Medicines & Dosages"));
                }
                if (fieldMap.containsKey("Lab Test & Result") && !"Not recorded".equals(fieldMap.get("Lab Test & Result"))) {
                    details.add("Result: " + fieldMap.get("Lab Test & Result"));
                }
                if (!details.isEmpty()) {
                    part.append(String.join("; ", details));
                } else {
                    part.append("Record verified on file.");
                }
                docFindings.add(part.toString());
            }
            data.keyFindingsFromDocs = String.join(" \n", docFindings);
        } else {
            data.keyFindingsFromDocs = "No previous medical documents uploaded for this visit.";
        }

        // Missing Clinical Information
        List<String> missing = new ArrayList<>();
        if (patient.getBloodGroup() == null || "Not recorded".equalsIgnoreCase(patient.getBloodGroup())) {
            missing.add("Blood group not verified.");
        }
        if (patient.getEmergencyContactPhone() == null || patient.getEmergencyContactPhone().isEmpty()) {
            missing.add("Emergency contact phone missing.");
        }
        if ("Severe".equalsIgnoreCase(severity)) {
            missing.add("Urgent clinician examination recommended due to reported severity.");
        }
        data.missingClinicalInfo = !missing.isEmpty() ? String.join(" | ", missing) : "Standard baseline intake parameters collected.";

        // Recommendations
        String dept = (intake != null && intake.getVisit() != null && intake.getVisit().getDepartment() != null)
                ? intake.getVisit().getDepartment() : "General Medicine";
        data.followUpRecommendations = "Clinical review with Dr. in " + dept + ". Physical exam and diagnostic evaluation indicated.";
        data.aiModelUsed = "MediKiosk Clinical Rule Engine v2.0";

        return data;
    }

    private SummaryData callGeminiApi(Patient patient, IntakeRecord intake, List<MedicalDocument> documents) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

        String prompt = "You are a clinical decision support assistant. Return a JSON object with keys: " +
                "chief_complaint, hpi, past_medical_history, current_medications, allergies, key_findings_from_docs, missing_clinical_info, follow_up_recommendations. " +
                "Patient: " + patient.getFullName() + ", Age: " + patient.getAge() + ", Chief Complaint: " + (intake != null ? intake.getChiefComplaint() : "");

        Map<String, Object> part = Collections.singletonMap("text", prompt);
        Map<String, Object> content = Collections.singletonMap("parts", Collections.singletonList(part));
        Map<String, Object> body = new HashMap<>();
        body.put("contents", Collections.singletonList(content));
        body.put("generationConfig", Collections.singletonMap("response_mime_type", "application/json"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                JsonNode summaryJson = objectMapper.readTree(text);

                SummaryData data = new SummaryData();
                data.chiefComplaint = summaryJson.path("chief_complaint").asText();
                data.hpi = summaryJson.path("hpi").asText();
                data.pastMedicalHistory = summaryJson.path("past_medical_history").asText();
                data.currentMedications = summaryJson.path("current_medications").asText();
                data.allergies = summaryJson.path("allergies").asText();
                data.keyFindingsFromDocs = summaryJson.path("key_findings_from_docs").asText();
                data.missingClinicalInfo = summaryJson.path("missing_clinical_info").asText();
                data.followUpRecommendations = summaryJson.path("follow_up_recommendations").asText();
                data.aiModelUsed = "Google Gemini 1.5 Flash";
                return data;
            } catch (Exception e) {
                System.err.println("JSON parse error from Gemini response: " + e.getMessage());
            }
        }
        return null;
    }
}
