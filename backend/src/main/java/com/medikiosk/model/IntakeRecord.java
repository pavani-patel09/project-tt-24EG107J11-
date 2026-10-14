package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "intake_records")
public class IntakeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    @JsonBackReference
    private Visit visit;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @JsonProperty("chief_complaint")
    @Column(name = "chief_complaint", nullable = false, columnDefinition = "TEXT")
    private String chiefComplaint;

    @JsonProperty("symptom_onset")
    @Column(name = "symptom_onset", length = 100)
    private String symptomOnset;

    @JsonProperty("symptom_duration")
    @Column(name = "symptom_duration", length = 100)
    private String symptomDuration;

    @Column(length = 30)
    private String severity = "Moderate"; // Mild, Moderate, Severe

    @JsonProperty("associated_symptoms")
    @Column(name = "associated_symptoms", columnDefinition = "TEXT")
    private String associatedSymptoms;

    @JsonProperty("changes_since_last_visit")
    @Column(name = "changes_since_last_visit", columnDefinition = "TEXT")
    private String changesSinceLastVisit;

    @JsonProperty("recently_taken_meds")
    @Column(name = "recently_taken_meds", columnDefinition = "TEXT")
    private String recentlyTakenMeds;

    @JsonProperty("newly_reported_allergies")
    @Column(name = "newly_reported_allergies", columnDefinition = "TEXT")
    private String newlyReportedAllergies;

    @JsonProperty("language_used")
    @Column(name = "language_used", length = 10)
    private String languageUsed = "en"; // en, hi, te

    @JsonProperty("submitted_at")
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    public IntakeRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Visit getVisit() { return visit; }
    public void setVisit(Visit visit) { this.visit = visit; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

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

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
