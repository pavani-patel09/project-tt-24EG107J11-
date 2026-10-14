package com.medikiosk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medical_documents")
public class MedicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    @JsonBackReference
    private Visit visit;

    @JsonProperty("doc_type")
    @Column(name = "doc_type", length = 50)
    private String docType = "prescription"; // prescription, lab_report, discharge_summary, imaging, other

    @JsonProperty("original_filename")
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @JsonProperty("file_path")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @JsonProperty("file_size_bytes")
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes = 0L;

    @JsonProperty("raw_extracted_text")
    @Column(name = "raw_extracted_text", columnDefinition = "TEXT")
    private String rawExtractedText;

    @JsonProperty("ocr_status")
    @Column(name = "ocr_status", length = 30)
    private String ocrStatus = "processed"; // pending, processed, verified, failed

    @JsonProperty("is_verified")
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("extracted_fields")
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ExtractedField> extractedFields = new ArrayList<>();

    public MedicalDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Visit getVisit() { return visit; }
    public void setVisit(Visit visit) { this.visit = visit; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public String getRawExtractedText() { return rawExtractedText; }
    public void setRawExtractedText(String rawExtractedText) { this.rawExtractedText = rawExtractedText; }

    public String getOcrStatus() { return ocrStatus; }
    public void setOcrStatus(String ocrStatus) { this.ocrStatus = ocrStatus; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean verified) { isVerified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ExtractedField> getExtractedFields() { return extractedFields; }
    public void setExtractedFields(List<ExtractedField> extractedFields) { this.extractedFields = extractedFields; }
}
