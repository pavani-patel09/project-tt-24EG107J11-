package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "extracted_fields")
public class ExtractedField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @JsonBackReference
    private MedicalDocument document;

    @JsonProperty("field_name")
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName; // 'Document Type', 'Date', 'Doctor', 'Hospital / Facility', 'Diagnosis', 'Medicines & Dosages'

    @JsonProperty("extracted_value")
    @Column(name = "extracted_value", columnDefinition = "TEXT")
    private String extractedValue;

    private Double confidence = 0.90;

    @JsonProperty("is_verified")
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @JsonProperty("corrected_value")
    @Column(name = "corrected_value", columnDefinition = "TEXT")
    private String correctedValue;

    @JsonProperty("verified_by_user_id")
    @Column(name = "verified_by_user_id")
    private Long verifiedByUserId;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ExtractedField() {}

    public ExtractedField(MedicalDocument document, String fieldName, String extractedValue, Double confidence, Boolean isVerified) {
        this.document = document;
        this.fieldName = fieldName;
        this.extractedValue = extractedValue;
        this.confidence = confidence != null ? confidence : 0.90;
        this.isVerified = isVerified != null ? isVerified : false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedicalDocument getDocument() { return document; }
    public void setDocument(MedicalDocument document) { this.document = document; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getExtractedValue() { return extractedValue; }
    public void setExtractedValue(String extractedValue) { this.extractedValue = extractedValue; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean verified) { isVerified = verified; }

    public String getCorrectedValue() { return correctedValue; }
    public void setCorrectedValue(String correctedValue) { this.correctedValue = correctedValue; }

    public Long getVerifiedByUserId() { return verifiedByUserId; }
    public void setVerifiedByUserId(Long verifiedByUserId) { this.verifiedByUserId = verifiedByUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
