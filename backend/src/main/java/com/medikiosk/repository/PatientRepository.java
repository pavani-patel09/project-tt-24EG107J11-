package com.medikiosk.repository;

import com.medikiosk.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientUid(String patientUid);
    List<Patient> findByPhone(String phone);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.patientUid) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.phone LIKE CONCAT('%', :query, '%') OR " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Patient> searchPatients(@Param("query") String query);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%')) AND p.phone <> :phone")
    List<Patient> findByNameMatchesExcludingPhone(@Param("name") String name, @Param("phone") String phone);
}
