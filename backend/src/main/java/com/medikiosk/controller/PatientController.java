package com.medikiosk.controller;

import com.medikiosk.dto.PatientDTOs.*;
import com.medikiosk.model.Allergy;
import com.medikiosk.model.Medication;
import com.medikiosk.model.Patient;
import com.medikiosk.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/search")
    public ResponseEntity<List<PatientSearchResult>> searchPatients(@RequestParam("query") String query) {
        return ResponseEntity.ok(patientService.searchPatients(query));
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicate(
            @RequestParam("phone") String phone,
            @RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(patientService.checkDuplicates(phone, name));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Patient> registerPatient(@RequestBody PatientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.registerPatient(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable("id") Long id) {
        return ResponseEntity.ok(patientService.getPatientProfile(id));
    }

    @PostMapping("/{id}/allergies")
    public ResponseEntity<Allergy> addAllergy(@PathVariable("id") Long id, @RequestBody AllergyInput input) {
        return ResponseEntity.ok(patientService.addAllergy(id, input));
    }

    @PostMapping("/{id}/medications")
    public ResponseEntity<Medication> addMedication(@PathVariable("id") Long id, @RequestBody MedicationInput input) {
        return ResponseEntity.ok(patientService.addMedication(id, input));
    }
}
