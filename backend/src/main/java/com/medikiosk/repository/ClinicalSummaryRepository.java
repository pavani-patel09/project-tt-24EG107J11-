package com.medikiosk.repository;

import com.medikiosk.model.ClinicalSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClinicalSummaryRepository extends JpaRepository<ClinicalSummary, Long> {
    Optional<ClinicalSummary> findByVisitId(Long visitId);
    long countByIsDoctorVerifiedTrue();
}
