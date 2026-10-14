package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chronic_conditions")
public class ChronicCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    @JsonProperty("condition_name")
    @Column(name = "condition_name", nullable = false, length = 150)
    private String conditionName;

    @JsonProperty("icd10_code")
    @Column(name = "icd10_code", length = 30)
    private String icd10Code;

    @JsonProperty("diagnosed_date")
    @Column(name = "diagnosed_date", length = 50)
    private String diagnosedDate;

    @Column(length = 30)
    private String status = "Active"; // Active, Inactive, Resolved

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ChronicCondition() {}

    public ChronicCondition(Patient patient, String conditionName, String icd10Code, String diagnosedDate, String status) {
        this.patient = patient;
        this.conditionName = conditionName;
        this.icd10Code = icd10Code;
        this.diagnosedDate = diagnosedDate;
        this.status = status != null ? status : "Active";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getConditionName() { return conditionName; }
    public void setConditionName(String conditionName) { this.conditionName = conditionName; }

    public String getIcd10Code() { return icd10Code; }
    public void setIcd10Code(String icd10Code) { this.icd10Code = icd10Code; }

    public String getDiagnosedDate() { return diagnosedDate; }
    public void setDiagnosedDate(String diagnosedDate) { this.diagnosedDate = diagnosedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
