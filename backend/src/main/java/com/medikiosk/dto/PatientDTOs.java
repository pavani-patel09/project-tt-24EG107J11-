package com.medikiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientDTOs {

    public static class PatientSearchResult {
        private Long id;
        @JsonProperty("patient_uid")
        private String patientUid;
        @JsonProperty("full_name")
        private String fullName;
        private Integer age;
        private String gender;
        private String phone;
        @JsonProperty("blood_group")
        private String bloodGroup;
        @JsonProperty("last_visit_date")
        private String lastVisitDate;
        @JsonProperty("chronic_conditions_summary")
        private String chronicConditionsSummary;

        public PatientSearchResult() {}

        public PatientSearchResult(Long id, String patientUid, String fullName, Integer age, String gender, String phone, String bloodGroup, String lastVisitDate, String chronicConditionsSummary) {
            this.id = id;
            this.patientUid = patientUid;
            this.fullName = fullName;
            this.age = age;
            this.gender = gender;
            this.phone = phone;
            this.bloodGroup = bloodGroup;
            this.lastVisitDate = lastVisitDate;
            this.chronicConditionsSummary = chronicConditionsSummary;
        }

        public Long getId() { return id; }
        public String getPatientUid() { return patientUid; }
        public String getFullName() { return fullName; }
        public Integer getAge() { return age; }
        public String getGender() { return gender; }
        public String getPhone() { return phone; }
        public String getBloodGroup() { return bloodGroup; }
        public String getLastVisitDate() { return lastVisitDate; }
        public String getChronicConditionsSummary() { return chronicConditionsSummary; }
    }

    public static class PatientMatchItem {
        private Long id;
        @JsonProperty("patient_uid")
        private String patientUid;
        @JsonProperty("full_name")
        private String fullName;
        private Integer age;
        private String gender;
        private String phone;

        public PatientMatchItem(Long id, String patientUid, String fullName, Integer age, String gender, String phone) {
            this.id = id;
            this.patientUid = patientUid;
            this.fullName = fullName;
            this.age = age;
            this.gender = gender;
            this.phone = phone;
        }

        public Long getId() { return id; }
        public String getPatientUid() { return patientUid; }
        public String getFullName() { return fullName; }
        public Integer getAge() { return age; }
        public String getGender() { return gender; }
        public String getPhone() { return phone; }
    }

    public static class DuplicateCheckResponse {
        @JsonProperty("has_exact_phone_match")
        private boolean hasExactPhoneMatch;
        @JsonProperty("phone_matches")
        private List<PatientMatchItem> phoneMatches;
        @JsonProperty("potential_name_matches")
        private List<PatientMatchItem> potentialNameMatches;

        public DuplicateCheckResponse(boolean hasExactPhoneMatch, List<PatientMatchItem> phoneMatches, List<PatientMatchItem> potentialNameMatches) {
            this.hasExactPhoneMatch = hasExactPhoneMatch;
            this.phoneMatches = phoneMatches;
            this.potentialNameMatches = potentialNameMatches;
        }

        public boolean isHasExactPhoneMatch() { return hasExactPhoneMatch; }
        public List<PatientMatchItem> getPhoneMatches() { return phoneMatches; }
        public List<PatientMatchItem> getPotentialNameMatches() { return potentialNameMatches; }
    }

    public static class AllergyInput {
        private String allergen;
        private String reaction;
        private String severity;
        @JsonProperty("identified_date")
        private String identifiedDate;

        public String getAllergen() { return allergen; }
        public void setAllergen(String allergen) { this.allergen = allergen; }
        public String getReaction() { return reaction; }
        public void setReaction(String reaction) { this.reaction = reaction; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getIdentifiedDate() { return identifiedDate; }
        public void setIdentifiedDate(String identifiedDate) { this.identifiedDate = identifiedDate; }
    }

    public static class ConditionInput {
        @JsonProperty("condition_name")
        private String conditionName;
        @JsonProperty("icd10_code")
        private String icd10Code;
        @JsonProperty("diagnosed_date")
        private String diagnosedDate;
        private String status;

        public String getConditionName() { return conditionName; }
        public void setConditionName(String conditionName) { this.conditionName = conditionName; }
        public String getIcd10Code() { return icd10Code; }
        public void setIcd10Code(String icd10Code) { this.icd10Code = icd10Code; }
        public String getDiagnosedDate() { return diagnosedDate; }
        public void setDiagnosedDate(String diagnosedDate) { this.diagnosedDate = diagnosedDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class MedicationInput {
        @JsonProperty("medicine_name")
        private String medicineName;
        private String dosage;
        private String frequency;
        @JsonProperty("start_date")
        private String startDate;
        @JsonProperty("end_date")
        private String endDate;
        @JsonProperty("prescribing_doctor")
        private String prescribingDoctor;
        @JsonProperty("is_active")
        private Boolean isActive = true;

        public String getMedicineName() { return medicineName; }
        public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getPrescribingDoctor() { return prescribingDoctor; }
        public void setPrescribingDoctor(String prescribingDoctor) { this.prescribingDoctor = prescribingDoctor; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean active) { isActive = active; }
    }

    public static class PatientCreateRequest {
        @JsonProperty("full_name")
        private String fullName;
        private LocalDate dob;
        private Integer age;
        private String gender;
        @JsonProperty("blood_group")
        private String bloodGroup;
        private String phone;
        private String email;
        @JsonProperty("emergency_contact_name")
        private String emergencyContactName;
        @JsonProperty("emergency_contact_phone")
        private String emergencyContactPhone;
        private String address;
        @JsonProperty("consent_agreed")
        private Boolean consentAgreed = true;

        private List<AllergyInput> allergies = new ArrayList<>();
        @JsonProperty("chronic_conditions")
        private List<ConditionInput> chronicConditions = new ArrayList<>();
        private List<MedicationInput> medications = new ArrayList<>();

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public LocalDate getDob() { return dob; }
        public void setDob(LocalDate dob) { this.dob = dob; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getBloodGroup() { return bloodGroup; }
        public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getEmergencyContactName() { return emergencyContactName; }
        public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
        public String getEmergencyContactPhone() { return emergencyContactPhone; }
        public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public Boolean getConsentAgreed() { return consentAgreed; }
        public void setConsentAgreed(Boolean consentAgreed) { this.consentAgreed = consentAgreed; }
        public List<AllergyInput> getAllergies() { return allergies; }
        public void setAllergies(List<AllergyInput> allergies) { this.allergies = allergies; }
        public List<ConditionInput> getChronicConditions() { return chronicConditions; }
        public void setChronicConditions(List<ConditionInput> chronicConditions) { this.chronicConditions = chronicConditions; }
        public List<MedicationInput> getMedications() { return medications; }
        public void setMedications(List<MedicationInput> medications) { this.medications = medications; }
    }
}
