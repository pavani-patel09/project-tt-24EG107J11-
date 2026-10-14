package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_summaries")
public class ClinicalSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    @JsonBackReference
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    // Structured Clinical Summary Fields
    @JsonProperty("chief_complaint")
    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(columnDefinition = "TEXT")
    private String hpi;

    @JsonProperty("past_medical_history")
    @Column(name = "past_medical_history", columnDefinition = "TEXT")
    private String pastMedicalHistory;

    @JsonProperty("current_medications")
    @Column(name = "current_medications", columnDefinition = "TEXT")
    private String currentMedications;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @JsonProperty("key_findings_from_docs")
    @Column(name = "key_findings_from_docs", columnDefinition = "TEXT")
    private String keyFindingsFromDocs;

    @JsonProperty("missing_clinical_info")
    @Column(name = "missing_clinical_info", columnDefinition = "TEXT")
    private String missingClinicalInfo;

    @JsonProperty("follow_up_recommendations")
    @Column(name = "follow_up_recommendations", columnDefinition = "TEXT")
    private String followUpRecommendations;

    @JsonProperty("ai_model_used")
    @Column(name = "ai_model_used", length = 50)
    private String aiModelUsed = "MediKiosk Clinical Rule Engine v2.0";

    // Doctor Review & Verification
    @JsonProperty("is_doctor_verified")
    @Column(name = "is_doctor_verified")
    private Boolean isDoctorVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_doctor_id")
    private User verifiedByDoctor;

    @JsonProperty("doctor_notes")
    @Column(name = "doctor_notes", columnDefinition = "TEXT")
    private String doctorNotes;

    @JsonProperty("verified_at")
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ClinicalSummary() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Visit getVisit() { return visit; }
    public void setVisit(Visit visit) { this.visit = visit; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

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

    public String getKeyFindingsFromDocs() { return keyFindingsFromDocs; }
    public void setKeyFindingsFromDocs(String keyFindingsFromDocs) { this.keyFindingsFromDocs = keyFindingsFromDocs; }

    public String getMissingClinicalInfo() { return missingClinicalInfo; }
    public void setMissingClinicalInfo(String missingClinicalInfo) { this.missingClinicalInfo = missingClinicalInfo; }

    public String getFollowUpRecommendations() { return followUpRecommendations; }
    public void setFollowUpRecommendations(String followUpRecommendations) { this.followUpRecommendations = followUpRecommendations; }

    public String getAiModelUsed() { return aiModelUsed; }
    public void setAiModelUsed(String aiModelUsed) { this.aiModelUsed = aiModelUsed; }

    public Boolean getIsDoctorVerified() { return isDoctorVerified; }
    public void setIsDoctorVerified(Boolean doctorVerified) { isDoctorVerified = doctorVerified; }

    public User getVerifiedByDoctor() { return verifiedByDoctor; }
    public void setVerifiedByDoctor(User verifiedByDoctor) { this.verifiedByDoctor = verifiedByDoctor; }

    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
