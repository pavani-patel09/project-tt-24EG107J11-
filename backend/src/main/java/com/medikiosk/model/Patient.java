package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("patient_uid")
    @Column(name = "patient_uid", nullable = false, unique = true, length = 50)
    private String patientUid; // e.g. PAT-2026-00124

    @JsonProperty("full_name")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    private LocalDate dob;
    private Integer age;

    @Column(nullable = false, length = 20)
    private String gender;

    @JsonProperty("blood_group")
    @Column(name = "blood_group", length = 20)
    private String bloodGroup = "Not recorded";

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @JsonProperty("emergency_contact_name")
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @JsonProperty("emergency_contact_phone")
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @JsonProperty("consent_agreed")
    @Column(name = "consent_agreed")
    private Boolean consentAgreed = true;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relationships
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Allergy> allergies = new ArrayList<>();

    @JsonProperty("chronic_conditions")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ChronicCondition> chronicConditions = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Medication> medications = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Visit> visits = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MedicalDocument> documents = new ArrayList<>();

    @JsonProperty("clinical_summaries")
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ClinicalSummary> clinicalSummaries = new ArrayList<>();

    public Patient() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatientUid() { return patientUid; }
    public void setPatientUid(String patientUid) { this.patientUid = patientUid; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Allergy> getAllergies() { return allergies; }
    public void setAllergies(List<Allergy> allergies) { this.allergies = allergies; }

    public List<ChronicCondition> getChronicConditions() { return chronicConditions; }
    public void setChronicConditions(List<ChronicCondition> chronicConditions) { this.chronicConditions = chronicConditions; }

    public List<Medication> getMedications() { return medications; }
    public void setMedications(List<Medication> medications) { this.medications = medications; }

    public List<Visit> getVisits() { return visits; }
    public void setVisits(List<Visit> visits) { this.visits = visits; }

    public List<MedicalDocument> getDocuments() { return documents; }
    public void setDocuments(List<MedicalDocument> documents) { this.documents = documents; }

    public List<ClinicalSummary> getClinicalSummaries() { return clinicalSummaries; }
    public void setClinicalSummaries(List<ClinicalSummary> clinicalSummaries) { this.clinicalSummaries = clinicalSummaries; }
}
