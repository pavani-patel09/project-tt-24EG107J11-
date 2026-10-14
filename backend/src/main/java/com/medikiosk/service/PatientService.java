package com.medikiosk.service;

import com.medikiosk.dto.PatientDTOs.*;
import com.medikiosk.model.*;
import com.medikiosk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AllergyRepository allergyRepository;

    @Autowired
    private ChronicConditionRepository chronicConditionRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuthService authService;

    public String generatePatientUid() {
        int year = LocalDateTime.now().getYear();
        Random random = new Random();
        while (true) {
            int num = 10000 + random.nextInt(90000);
            String uid = "PAT-" + year + "-" + num;
            if (patientRepository.findByPatientUid(uid).isEmpty()) {
                return uid;
            }
        }
    }

    public List<PatientSearchResult> searchPatients(String query) {
        String q = query != null ? query.trim() : "";
        User currentUser = authService.getCurrentUser();
        auditService.log(currentUser != null ? currentUser.getId() : null, null, "PATIENT_SEARCH", "Search query: " + q);

        List<Patient> patients = patientRepository.searchPatients(q);
        List<PatientSearchResult> results = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (Patient p : patients) {
            List<Visit> visits = visitRepository.findByPatientIdOrderByVisitDateDesc(p.getId());
            String lastVisitStr = !visits.isEmpty() ? visits.get(0).getVisitDate().format(fmt) : "First visit";

            List<String> condNames = new ArrayList<>();
            for (ChronicCondition c : p.getChronicConditions()) {
                condNames.add(c.getConditionName());
            }
            String condSummary = !condNames.isEmpty() ? String.join(", ", condNames) : "None recorded";

            results.add(new PatientSearchResult(
                    p.getId(),
                    p.getPatientUid(),
                    p.getFullName(),
                    p.getAge(),
                    p.getGender(),
                    p.getPhone(),
                    p.getBloodGroup() != null ? p.getBloodGroup() : "Not recorded",
                    lastVisitStr,
                    condSummary
            ));
        }

        return results;
    }

    public DuplicateCheckResponse checkDuplicates(String phone, String name) {
        String cleanPhone = phone != null ? phone.trim() : "";
        List<Patient> exactMatches = patientRepository.findByPhone(cleanPhone);
        List<PatientMatchItem> phoneMatches = new ArrayList<>();
        for (Patient p : exactMatches) {
            phoneMatches.add(new PatientMatchItem(p.getId(), p.getPatientUid(), p.getFullName(), p.getAge(), p.getGender(), p.getPhone()));
        }

        List<PatientMatchItem> nameMatches = new ArrayList<>();
        if (name != null && !name.trim().isEmpty()) {
            List<Patient> matches = patientRepository.findByNameMatchesExcludingPhone(name.trim(), cleanPhone);
            for (Patient nm : matches) {
                nameMatches.add(new PatientMatchItem(nm.getId(), nm.getPatientUid(), nm.getFullName(), nm.getAge(), nm.getGender(), nm.getPhone()));
            }
        }

        return new DuplicateCheckResponse(!phoneMatches.isEmpty(), phoneMatches, nameMatches);
    }

    @Transactional
    public Patient registerPatient(PatientCreateRequest req) {
        Patient p = new Patient();
        p.setPatientUid(generatePatientUid());
        p.setFullName(req.getFullName());
        p.setDob(req.getDob());
        p.setAge(req.getAge());
        p.setGender(req.getGender());
        p.setBloodGroup(req.getBloodGroup() != null ? req.getBloodGroup() : "Not recorded");
        p.setPhone(req.getPhone());
        p.setEmail(req.getEmail());
        p.setEmergencyContactName(req.getEmergencyContactName());
        p.setEmergencyContactPhone(req.getEmergencyContactPhone());
        p.setAddress(req.getAddress());
        p.setConsentAgreed(req.getConsentAgreed() != null ? req.getConsentAgreed() : true);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());

        Patient saved = patientRepository.save(p);

        // Add Initial Allergies
        if (req.getAllergies() != null) {
            for (AllergyInput a : req.getAllergies()) {
                Allergy allergy = new Allergy(saved, a.getAllergen(), a.getReaction(), a.getSeverity(), a.getIdentifiedDate());
                allergyRepository.save(allergy);
                saved.getAllergies().add(allergy);
            }
        }

        // Add Chronic Conditions
        if (req.getChronicConditions() != null) {
            for (ConditionInput c : req.getChronicConditions()) {
                ChronicCondition cond = new ChronicCondition(saved, c.getConditionName(), c.getIcd10Code(), c.getDiagnosedDate(), c.getStatus());
                chronicConditionRepository.save(cond);
                saved.getChronicConditions().add(cond);
            }
        }

        // Add Medications
        if (req.getMedications() != null) {
            for (MedicationInput m : req.getMedications()) {
                Medication med = new Medication(saved, m.getMedicineName(), m.getDosage(), m.getFrequency(), m.getStartDate(), m.getEndDate(), m.getPrescribingDoctor(), m.getIsActive());
                medicationRepository.save(med);
                saved.getMedications().add(med);
            }
        }

        User currentUser = authService.getCurrentUser();
        auditService.log(currentUser != null ? currentUser.getId() : null, saved.getId(), "PATIENT_REGISTERED", "Registered patient " + saved.getFullName() + " (" + saved.getPatientUid() + ")");

        return saved;
    }

    public Patient getPatientProfile(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

        User currentUser = authService.getCurrentUser();
        auditService.log(currentUser != null ? currentUser.getId() : null, patient.getId(), "RECORD_RETRIEVED", "Retrieved profile for " + patient.getPatientUid());

        return patient;
    }

    public Allergy addAllergy(Long patientId, AllergyInput input) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        Allergy allergy = new Allergy(patient, input.getAllergen(), input.getReaction(), input.getSeverity(), input.getIdentifiedDate());
        return allergyRepository.save(allergy);
    }

    public Medication addMedication(Long patientId, MedicationInput input) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        Medication med = new Medication(patient, input.getMedicineName(), input.getDosage(), input.getFrequency(), input.getStartDate(), input.getEndDate(), input.getPrescribingDoctor(), input.getIsActive());
        return medicationRepository.save(med);
    }
}
