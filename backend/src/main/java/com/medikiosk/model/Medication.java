package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medications")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    @JsonProperty("medicine_name")
    @Column(name = "medicine_name", nullable = false, length = 150)
    private String medicineName;

    @Column(nullable = false, length = 50)
    private String dosage;

    @Column(length = 50)
    private String frequency = "Once daily";

    @JsonProperty("start_date")
    @Column(name = "start_date", length = 50)
    private String startDate;

    @JsonProperty("end_date")
    @Column(name = "end_date", length = 50)
    private String endDate;

    @JsonProperty("prescribing_doctor")
    @Column(name = "prescribing_doctor", length = 100)
    private String prescribingDoctor;

    @JsonProperty("is_active")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Medication() {}

    public Medication(Patient patient, String medicineName, String dosage, String frequency, String startDate, String endDate, String prescribingDoctor, Boolean isActive) {
        this.patient = patient;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency != null ? frequency : "Once daily";
        this.startDate = startDate;
        this.endDate = endDate;
        this.prescribingDoctor = prescribingDoctor;
        this.isActive = isActive != null ? isActive : true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
