package com.medikiosk.service;

import com.medikiosk.dto.DocumentDTOs.*;
import com.medikiosk.model.*;
import com.medikiosk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentService {

    @Autowired
    private MedicalDocumentRepository documentRepository;

    @Autowired
    private ExtractedFieldRepository extractedFieldRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private OcrExtractionService ocrExtractionService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuthService authService;

    @Value("${medikiosk.upload.dir:./uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList(".pdf", ".jpg", ".jpeg", ".png", ".webp")
    );

    @Transactional
    public MedicalDocument uploadDocument(Long patientId, String docType, Long visitId, MultipartFile file) throws IOException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file uploaded");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) originalFilename = "document";
        String ext = "";
        int dotIdx = originalFilename.lastIndexOf('.');
        if (dotIdx >= 0) ext = originalFilename.substring(dotIdx).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file format. Supported: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String uniqueFilename = UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;
        Path targetPath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Run Real OCR / PDF extraction
        String rawText = "";
        if (".pdf".equals(ext)) {
            rawText = ocrExtractionService.extractTextFromPdf(targetPath.toFile());
        }

        List<OcrExtractionService.ExtractedFieldResult> fieldResults =
                ocrExtractionService.processExtractedFields(rawText, originalFilename, docType);

        // Save Document
        MedicalDocument doc = new MedicalDocument();
        doc.setPatient(patient);
        if (visitId != null) {
            visitRepository.findById(visitId).ifPresent(doc::setVisit);
        }
        doc.setDocType(docType != null ? docType : "prescription");
        doc.setOriginalFilename(originalFilename);
        doc.setFilePath(targetPath.toString());
        doc.setFileSizeBytes(file.getSize());
        doc.setRawExtractedText(rawText);
        doc.setOcrStatus("processed");
        doc.setIsVerified(false);
        doc.setCreatedAt(LocalDateTime.now());

        MedicalDocument savedDoc = documentRepository.save(doc);

        // Save Extracted Fields
        for (OcrExtractionService.ExtractedFieldResult fr : fieldResults) {
            ExtractedField ef = new ExtractedField(savedDoc, fr.getFieldName(), fr.getExtractedValue(), fr.getConfidence(), false);
            extractedFieldRepository.save(ef);
            savedDoc.getExtractedFields().add(ef);
        }

        User currentUser = authService.getCurrentUser();
        auditService.log(currentUser != null ? currentUser.getId() : null, patient.getId(), "DOCUMENT_UPLOADED_OCR", "Uploaded " + originalFilename + " (" + docType + "), " + fieldResults.size() + " fields extracted");

        return savedDoc;
    }

    @Transactional
    public Map<String, String> verifyDocument(DocumentVerifyRequest req) {
        MedicalDocument doc = documentRepository.findById(req.getDocumentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        User currentUser = authService.getCurrentUser();

        if (req.getFields() != null) {
            for (VerifiedFieldItem item : req.getFields()) {
                Optional<ExtractedField> efOpt = extractedFieldRepository.findByDocumentIdAndFieldName(doc.getId(), item.getFieldName());
                if (efOpt.isPresent()) {
                    ExtractedField ef = efOpt.get();
                    ef.setCorrectedValue(item.getCorrectedValue());
                    ef.setIsVerified(true);
                    if (currentUser != null) ef.setVerifiedByUserId(currentUser.getId());
                    extractedFieldRepository.save(ef);
                }
            }
        }

        doc.setIsVerified(true);
        doc.setOcrStatus("verified");
        documentRepository.save(doc);

        auditService.log(currentUser != null ? currentUser.getId() : null, doc.getPatient().getId(), "DOCUMENT_FIELDS_VERIFIED", "Verified fields for document #" + doc.getId());

        return Collections.singletonMap("message", "Document fields verified and updated successfully");
    }

    public List<MedicalDocument> getPatientDocuments(Long patientId) {
        return documentRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }
}
