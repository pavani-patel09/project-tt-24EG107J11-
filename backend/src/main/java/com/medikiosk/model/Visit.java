package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    @JsonProperty("visit_token")
    @Column(name = "visit_token", nullable = false, unique = true, length = 20)
    private String visitToken; // e.g. A-108

    @Column(length = 50)
    private String department = "General Medicine";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @JsonProperty("visit_date")
    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate = LocalDateTime.now();

    @Column(length = 30)
    private String status = "in_kiosk"; // registered, in_kiosk, awaiting_review, with_doctor, completed

    @Column(length = 20)
    private String priority = "routine"; // routine, monitor, attention, red_flag

    @JsonProperty("is_red_flag")
    @Column(name = "is_red_flag")
    private Boolean isRedFlag = false;

    @JsonProperty("red_flag_reason")
    @Column(name = "red_flag_reason", columnDefinition = "TEXT")
    private String redFlagReason;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relationships
    @OneToOne(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private IntakeRecord intake;

    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MedicalDocument> documents = new ArrayList<>();

    @JsonProperty("clinical_summary")
    @OneToOne(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private ClinicalSummary clinicalSummary;

    public Visit() {}

    public Visit(Patient patient, String visitToken, String department, String status, String priority) {
        this.patient = patient;
        this.visitToken = visitToken;
        this.department = department != null ? department : "General Medicine";
        this.status = status != null ? status : "in_kiosk";
        this.priority = priority != null ? priority : "routine";
        this.visitDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.isRedFlag = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getVisitToken() { return visitToken; }
    public void setVisitToken(String visitToken) { this.visitToken = visitToken; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }

    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Boolean getIsRedFlag() { return isRedFlag; }
    public void setIsRedFlag(Boolean redFlag) { isRedFlag = redFlag; }

    public String getRedFlagReason() { return redFlagReason; }
    public void setRedFlagReason(String redFlagReason) { this.redFlagReason = redFlagReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public IntakeRecord getIntake() { return intake; }
    public void setIntake(IntakeRecord intake) { this.intake = intake; }

    public List<MedicalDocument> getDocuments() { return documents; }
    public void setDocuments(List<MedicalDocument> documents) { this.documents = documents; }

    public ClinicalSummary getClinicalSummary() { return clinicalSummary; }
    public void setClinicalSummary(ClinicalSummary clinicalSummary) { this.clinicalSummary = clinicalSummary; }
}
