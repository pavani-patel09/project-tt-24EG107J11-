package com.medikiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VisitDTOs {

    public static class VisitCreateRequest {
        @JsonProperty("patient_id")
        private Long patientId;
        private String department = "General Medicine";
        private String priority = "routine";

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }

    public static class IntakeCreateRequest {
        @JsonProperty("patient_id")
        private Long patientId;
        @JsonProperty("visit_id")
        private Long visitId;
        private String department = "General Medicine";

        @JsonProperty("chief_complaint")
        private String chiefComplaint;
        @JsonProperty("symptom_onset")
        private String symptomOnset;
        @JsonProperty("symptom_duration")
        private String symptomDuration;
        private String severity = "Moderate";
        @JsonProperty("associated_symptoms")
        private String associatedSymptoms;
        @JsonProperty("changes_since_last_visit")
        private String changesSinceLastVisit;
        @JsonProperty("recently_taken_meds")
        private String recentlyTakenMeds;
        @JsonProperty("newly_reported_allergies")
        private String newlyReportedAllergies;
        @JsonProperty("language_used")
        private String languageUsed = "en";

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public Long getVisitId() { return visitId; }
        public void setVisitId(Long visitId) { this.visitId = visitId; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getChiefComplaint() { return chiefComplaint; }
        public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
        public String getSymptomOnset() { return symptomOnset; }
        public void setSymptomOnset(String symptomOnset) { this.symptomOnset = symptomOnset; }
        public String getSymptomDuration() { return symptomDuration; }
        public void setSymptomDuration(String symptomDuration) { this.symptomDuration = symptomDuration; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getAssociatedSymptoms() { return associatedSymptoms; }
        public void setAssociatedSymptoms(String associatedSymptoms) { this.associatedSymptoms = associatedSymptoms; }
        public String getChangesSinceLastVisit() { return changesSinceLastVisit; }
        public void setChangesSinceLastVisit(String changesSinceLastVisit) { this.changesSinceLastVisit = changesSinceLastVisit; }
        public String getRecentlyTakenMeds() { return recentlyTakenMeds; }
        public void setRecentlyTakenMeds(String recentlyTakenMeds) { this.recentlyTakenMeds = recentlyTakenMeds; }
        public String getNewlyReportedAllergies() { return newlyReportedAllergies; }
        public void setNewlyReportedAllergies(String newlyReportedAllergies) { this.newlyReportedAllergies = newlyReportedAllergies; }
        public String getLanguageUsed() { return languageUsed; }
        public void setLanguageUsed(String languageUsed) { this.languageUsed = languageUsed; }
    }

    public static class VisitStatusUpdateRequest {
        private String status;
        @JsonProperty("doctor_id")
        private Long doctorId;
        private String priority;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }

    public static class VisitQueueItem {
        private Long id;
        @JsonProperty("patient_id")
        private Long patientId;
        @JsonProperty("patient_uid")
        private String patientUid;
        @JsonProperty("patient_name")
        private String patientName;
        private Integer age;
        private String gender;
        @JsonProperty("blood_group")
        private String bloodGroup;
        private String phone;
        private String token;
        private String department;
        @JsonProperty("visit_date")
        private String visitDate;
        private String status;
        private String priority;
        @JsonProperty("is_red_flag")
        private Boolean isRedFlag;
        @JsonProperty("red_flag_reason")
        private String redFlagReason;
        @JsonProperty("chief_complaint")
        private String chiefComplaint;
        @JsonProperty("has_ai_summary")
        private Boolean hasAiSummary;
        @JsonProperty("is_summary_verified")
        private Boolean isSummaryVerified;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }
        public String getPatientUid() { return patientUid; }
        public void setPatientUid(String patientUid) { this.patientUid = patientUid; }
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getBloodGroup() { return bloodGroup; }
        public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getVisitDate() { return visitDate; }
        public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public Boolean getIsRedFlag() { return isRedFlag; }
        public void setIsRedFlag(Boolean isRedFlag) { this.isRedFlag = isRedFlag; }
        public String getRedFlagReason() { return redFlagReason; }
        public void setRedFlagReason(String redFlagReason) { this.redFlagReason = redFlagReason; }
        public String getChiefComplaint() { return chiefComplaint; }
        public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
        public Boolean getHasAiSummary() { return hasAiSummary; }
        public void setHasAiSummary(Boolean hasAiSummary) { this.hasAiSummary = hasAiSummary; }
        public Boolean getIsSummaryVerified() { return isSummaryVerified; }
        public void setIsSummaryVerified(Boolean isSummaryVerified) { this.isSummaryVerified = isSummaryVerified; }
    }
}
