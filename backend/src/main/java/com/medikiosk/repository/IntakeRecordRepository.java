package com.medikiosk.repository;

import com.medikiosk.model.IntakeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IntakeRecordRepository extends JpaRepository<IntakeRecord, Long> {
    Optional<IntakeRecord> findByVisitId(Long visitId);
}
