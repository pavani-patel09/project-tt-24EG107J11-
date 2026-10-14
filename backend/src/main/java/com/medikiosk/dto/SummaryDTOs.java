package com.medikiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SummaryDTOs {

    public static class ClinicalSummaryVerifyRequest {
        @JsonProperty("doctor_notes")
        private String doctorNotes;
        @JsonProperty("chief_complaint")
        private String chiefComplaint;
        private String hpi;
        @JsonProperty("past_medical_history")
        private String pastMedicalHistory;
        @JsonProperty("current_medications")
        private String currentMedications;
        private String allergies;

        public String getDoctorNotes() { return doctorNotes; }
        public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }
        public String getChiefComplaint() { return chiefComplaint; }
        public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
        public String getHpi() { return hpi; }
        public void setHpi(String hpi) { this.hpi = hpi; }
        public String getPastMedicalHistory() { return pastMedicalHistory; }
        public void setPastMedicalHistory(String pastMedicalHistory) { this.pastMedicalHistory = pastMedicalHistory; }
        public String getCurrentMedications() { return currentMedications; }
        public void setCurrentMedications(String currentMedications) { this.currentMedications = currentMedications; }
        public String getAllergies() { return allergies; }
        public void setAllergies(String allergies) { this.allergies = allergies; }
    }

    public static class TimelineEventDTO {
        private String id;
        private String date;
        private String category;
        private String title;
        private String description;
        @JsonProperty("source_type")
        private String sourceType;
        @JsonProperty("source_id")
        private Long sourceId;
        @JsonProperty("is_current_visit")
        private Boolean isCurrentVisit;
        private Boolean verified;

        public TimelineEventDTO() {}

        public TimelineEventDTO(String id, String date, String category, String title, String description, String sourceType, Long sourceId, Boolean isCurrentVisit, Boolean verified) {
            this.id = id;
            this.date = date;
            this.category = category;
            this.title = title;
            this.description = description;
            this.sourceType = sourceType;
            this.sourceId = sourceId;
            this.isCurrentVisit = isCurrentVisit;
            this.verified = verified;
        }

        public String getId() { return id; }
        public String getDate() { return date; }
        public String getCategory() { return category; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getSourceType() { return sourceType; }
        public Long getSourceId() { return sourceId; }
        public Boolean getIsCurrentVisit() { return isCurrentVisit; }
        public Boolean getVerified() { return verified; }
    }
}
