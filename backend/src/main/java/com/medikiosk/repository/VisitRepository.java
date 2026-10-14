package com.medikiosk.repository;

import com.medikiosk.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    Optional<Visit> findByVisitToken(String visitToken);
    List<Visit> findByPatientIdOrderByVisitDateDesc(Long patientId);

    @Query("SELECT v FROM Visit v WHERE " +
           "(:dept IS NULL OR :dept = 'all' OR v.department = :dept) AND " +
           "(:priority IS NULL OR :priority = 'all' OR v.priority = :priority) AND " +
           "(:status IS NULL OR :status = 'all' OR v.status = :status) " +
           "ORDER BY v.visitDate DESC")
    List<Visit> findFilteredQueue(@Param("dept") String dept,
                                 @Param("priority") String priority,
                                 @Param("status") String status);

    long countByPriority(String priority);
    long countByStatus(String status);
    long countByIsRedFlagTrue();

    @Query("SELECT v.department, COUNT(v) FROM Visit v GROUP BY v.department")
    List<Object[]> countByDepartmentGroup();
}
