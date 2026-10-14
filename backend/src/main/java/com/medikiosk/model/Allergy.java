package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "allergies")
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    @Column(nullable = false, length = 100)
    private String allergen;

    @Column(length = 200)
    private String reaction;

    @Column(length = 30)
    private String severity = "Moderate"; // Mild, Moderate, Severe

    @JsonProperty("identified_date")
    @Column(name = "identified_date", length = 50)
    private String identifiedDate;

    @JsonProperty("verified_by_doctor")
    @Column(name = "verified_by_doctor")
    private Boolean verifiedByDoctor = false;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Allergy() {}

    public Allergy(Patient patient, String allergen, String reaction, String severity, String identifiedDate) {
        this.patient = patient;
        this.allergen = allergen;
        this.reaction = reaction;
        this.severity = severity != null ? severity : "Moderate";
        this.identifiedDate = identifiedDate;
        this.verifiedByDoctor = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getAllergen() { return allergen; }
    public void setAllergen(String allergen) { this.allergen = allergen; }

    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getIdentifiedDate() { return identifiedDate; }
    public void setIdentifiedDate(String identifiedDate) { this.identifiedDate = identifiedDate; }

    public Boolean getVerifiedByDoctor() { return verifiedByDoctor; }
    public void setVerifiedByDoctor(Boolean verifiedByDoctor) { this.verifiedByDoctor = verifiedByDoctor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
