package com.medikiosk.repository;

import com.medikiosk.model.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long> {
    List<MedicalDocument> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<MedicalDocument> findByVisitId(Long visitId);
}
