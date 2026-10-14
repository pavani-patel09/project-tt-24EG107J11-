package com.medikiosk.controller;

import com.medikiosk.dto.DocumentDTOs.DocumentVerifyRequest;
import com.medikiosk.model.MedicalDocument;
import com.medikiosk.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicalDocument> uploadDocument(
            @RequestParam("patient_id") Long patientId,
            @RequestParam(value = "doc_type", defaultValue = "prescription") String docType,
            @RequestParam(value = "visit_id", required = false) Long visitId,
            @RequestParam("file") MultipartFile file) throws IOException {

        MedicalDocument doc = documentService.uploadDocument(patientId, docType, visitId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyDocument(@RequestBody DocumentVerifyRequest request) {
        return ResponseEntity.ok(documentService.verifyDocument(request));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalDocument>> getPatientDocuments(@PathVariable("patientId") Long patientId) {
        return ResponseEntity.ok(documentService.getPatientDocuments(patientId));
    }
}
