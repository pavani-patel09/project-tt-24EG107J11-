package com.medikiosk.config;

import com.medikiosk.model.*;
import com.medikiosk.repository.*;
import com.medikiosk.service.ClinicalSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

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
    private IntakeRecordRepository intakeRecordRepository;

    @Autowired
    private MedicalDocumentRepository documentRepository;

    @Autowired
    private ExtractedFieldRepository extractedFieldRepository;

    @Autowired
    private ClinicalSummaryRepository clinicalSummaryRepository;

    @Autowired
    private ClinicalSummaryService clinicalSummaryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("MediKiosk database already contains data. Skipping re-seed.");
            return;
        }

        System.out.println("Seeding MediKiosk AI clinical data into SQL database...");

        // 1. Seed Users
        User doc = new User("doc_sharma", "dr.sharma@medikiosk.hospital", passwordEncoder.encode("DoctorPassword123!"), "Dr. A. Sharma", "doctor", "General Medicine", "MCI-2018-99420");
        User staff = new User("staff_meena", "meena.rao@medikiosk.hospital", passwordEncoder.encode("StaffPassword123!"), "Meena Rao", "staff", "Triage & Registration", "STF-0091");
        User admin = new User("admin", "admin@medikiosk.hospital", passwordEncoder.encode("AdminPassword123!"), "Administrator", "admin", "Hospital Administration", null);
        User patientUser = new User("patient_sunita", "sunita.devi@example.com", passwordEncoder.encode("PatientPassword123!"), "Sunita Devi", "patient", "Patient Portal", null);

        userRepository.save(doc);
        userRepository.save(staff);
        userRepository.save(admin);
        userRepository.save(patientUser);

        // 2. Patient 1: Sunita Devi
        Patient p1 = new Patient();
        p1.setPatientUid("PAT-2026-00124");
        p1.setFullName("Sunita Devi");
        p1.setDob(LocalDate.of(1974, 5, 12));
        p1.setAge(52);
        p1.setGender("Female");
        p1.setBloodGroup("B+");
        p1.setPhone("9876543210");
        p1.setEmail("sunita.devi@example.com");
        p1.setEmergencyContactName("Ramesh Devi (Husband)");
        p1.setEmergencyContactPhone("9876543211");
        p1.setAddress("42/B Civil Lines, Sector 4, New Delhi 110054");
        p1.setConsentAgreed(true);
        p1.setCreatedAt(LocalDateTime.now().minusDays(180));
        p1.setUpdatedAt(LocalDateTime.now().minusDays(2));
        patientRepository.save(p1);

        Allergy a1 = new Allergy(p1, "Penicillin", "Maculopapular rash, mild urticaria", "Moderate", "14 Feb 2021");
        a1.setVerifiedByDoctor(true);
        Allergy a2 = new Allergy(p1, "Sulfa Drugs", "Facial swelling and localized erythema", "Severe", "05 Sep 2023");
        a2.setVerifiedByDoctor(true);
        allergyRepository.save(a1);
        allergyRepository.save(a2);

        ChronicCondition c1 = new ChronicCondition(p1, "Type 2 Diabetes Mellitus", "E11.9", "12 Jan 2018", "Active");
        ChronicCondition c2 = new ChronicCondition(p1, "Essential Hypertension", "I10", "04 Aug 2020", "Active");
        chronicConditionRepository.save(c1);
        chronicConditionRepository.save(c2);

        Medication m1 = new Medication(p1, "Metformin", "500 mg", "Twice daily after meals", "12 Jan 2018", null, "Dr. A. Sharma", true);
        Medication m2 = new Medication(p1, "Amlodipine", "5 mg", "Once daily morning", "04 Aug 2020", null, "Dr. A. Sharma", true);
        medicationRepository.save(m1);
        medicationRepository.save(m2);

        // Past Visit
        Visit v1Past = new Visit(p1, "A-042", "General Medicine", "completed", "routine");
        v1Past.setVisitDate(LocalDateTime.now().minusDays(90));
        v1Past.setDoctor(doc);
        visitRepository.save(v1Past);

        IntakeRecord in1Past = new IntakeRecord();
        in1Past.setVisit(v1Past);
        in1Past.setPatientId(p1.getId());
        in1Past.setChiefComplaint("Routine quarterly diabetes and blood pressure checkup");
        in1Past.setSymptomOnset("1 week");
        in1Past.setSymptomDuration("3 months");
        in1Past.setSeverity("Mild");
        in1Past.setAssociatedSymptoms("Mild fatigue in evenings");
        in1Past.setChangesSinceLastVisit("Blood sugar levels relatively stable");
        in1Past.setRecentlyTakenMeds("Metformin 500mg, Amlodipine 5mg");
        in1Past.setLanguageUsed("hi");
        in1Past.setSubmittedAt(LocalDateTime.now().minusDays(90));
        intakeRecordRepository.save(in1Past);

        // Today's Visit (In Queue)
        Visit v1Today = new Visit(p1, "A-108", "General Medicine", "awaiting_review", "attention");
        v1Today.setVisitDate(LocalDateTime.now().minusMinutes(25));
        v1Today.setIsRedFlag(false);
        v1Today.setRedFlagReason(null);
        visitRepository.save(v1Today);

        IntakeRecord in1Today = new IntakeRecord();
        in1Today.setVisit(v1Today);
        in1Today.setPatientId(p1.getId());
        in1Today.setChiefComplaint("Persistent dizziness and morning headaches for the past 5 days");
        in1Today.setSymptomOnset("5 days ago");
        in1Today.setSymptomDuration("5 days");
        in1Today.setSeverity("Moderate");
        in1Today.setAssociatedSymptoms("Mild blurred vision when standing up abruptly");
        in1Today.setChangesSinceLastVisit("Headache frequency increased compared to 3 months ago");
        in1Today.setRecentlyTakenMeds("Paracetamol 650mg yesterday, Metformin 500mg morning");
        in1Today.setNewlyReportedAllergies("None");
        in1Today.setLanguageUsed("en");
        in1Today.setSubmittedAt(LocalDateTime.now().minusMinutes(22));
        intakeRecordRepository.save(in1Today);

        // Generate Clinical Summary for Sunita Devi
        ClinicalSummaryService.SummaryData sData = clinicalSummaryService.generateSummary(p1, in1Today, new ArrayList<>());
        ClinicalSummary sum1 = new ClinicalSummary();
        sum1.setVisit(v1Today);
        sum1.setPatient(p1);
        sum1.setChiefComplaint(sData.chiefComplaint);
        sum1.setHpi(sData.hpi);
        sum1.setPastMedicalHistory(sData.pastMedicalHistory);
        sum1.setCurrentMedications(sData.currentMedications);
        sum1.setAllergies(sData.allergies);
        sum1.setKeyFindingsFromDocs(sData.keyFindingsFromDocs);
        sum1.setMissingClinicalInfo(sData.missingClinicalInfo);
        sum1.setFollowUpRecommendations(sData.followUpRecommendations);
        sum1.setAiModelUsed(sData.aiModelUsed);
        clinicalSummaryRepository.save(sum1);

        // Medical Document for Sunita Devi
        MedicalDocument doc1 = new MedicalDocument();
        doc1.setPatient(p1);
        doc1.setVisit(v1Past);
        doc1.setDocType("lab_report");
        doc1.setOriginalFilename("Apollo_Diagnostics_HbA1c_Nov2023.pdf");
        doc1.setFilePath("./uploads/sample_apollo_report.pdf");
        doc1.setFileSizeBytes(145020L);
        doc1.setOcrStatus("verified");
        doc1.setIsVerified(true);
        doc1.setRawExtractedText("Apollo Diagnostics Center. Patient: Sunita Devi. HbA1c: 7.2%. Fasting Blood Sugar: 138 mg/dL. Dr. A. Sharma.");
        documentRepository.save(doc1);

        extractedFieldRepository.save(new ExtractedField(doc1, "Hospital / Facility", "Apollo Diagnostics Center", 0.96, true));
        extractedFieldRepository.save(new ExtractedField(doc1, "Date", "15 Nov 2023", 0.95, true));
        extractedFieldRepository.save(new ExtractedField(doc1, "Doctor", "Dr. A. Sharma", 0.94, true));
        extractedFieldRepository.save(new ExtractedField(doc1, "Lab Test & Result", "HbA1c: 7.2%, FBS: 138 mg/dL", 0.92, true));
        extractedFieldRepository.save(new ExtractedField(doc1, "Diagnosis", "Type 2 Diabetes Mellitus", 0.93, true));

        // 3. Patient 2: Rajesh Kumar (Red Flag)
        Patient p2 = new Patient();
        p2.setPatientUid("PAT-2026-00412");
        p2.setFullName("Rajesh Kumar");
        p2.setDob(LocalDate.of(1981, 8, 20));
        p2.setAge(45);
        p2.setGender("Male");
        p2.setBloodGroup("O+");
        p2.setPhone("9123456780");
        p2.setEmail("rajesh.k@example.com");
        p2.setEmergencyContactName("Pooja Kumar (Wife)");
        p2.setEmergencyContactPhone("9123456781");
        p2.setAddress("Flat 304, Green Heights, Bangalore 560034");
        p2.setConsentAgreed(true);
        patientRepository.save(p2);

        chronicConditionRepository.save(new ChronicCondition(p2, "Bronchial Asthma", "J45.9", "18 Oct 2019", "Active"));
        medicationRepository.save(new Medication(p2, "Salbutamol Inhaler", "100 mcg", "As needed", "18 Oct 2019", null, "Dr. K. Nair", true));

        Visit v2 = new Visit(p2, "P-204", "Paediatrics", "awaiting_review", "red_flag");
        v2.setVisitDate(LocalDateTime.now().minusMinutes(12));
        v2.setIsRedFlag(true);
        v2.setRedFlagReason("Acute chest pain and shortness of breath reported. Immediate triage escalation.");
        visitRepository.save(v2);

        IntakeRecord in2 = new IntakeRecord();
        in2.setVisit(v2);
        in2.setPatientId(p2.getId());
        in2.setChiefComplaint("Sudden severe chest pressure and shortness of breath since 2 hours");
        in2.setSymptomOnset("2 hours ago");
        in2.setSymptomDuration("2 hours");
        in2.setSeverity("Severe");
        in2.setAssociatedSymptoms("Cold sweating, left shoulder radiation");
        in2.setLanguageUsed("en");
        in2.setSubmittedAt(LocalDateTime.now().minusMinutes(10));
        intakeRecordRepository.save(in2);

        // 4. Patient 3: Priya Patel
        Patient p3 = new Patient();
        p3.setPatientUid("PAT-2026-00891");
        p3.setFullName("Priya Patel");
        p3.setDob(LocalDate.of(1997, 2, 14));
        p3.setAge(29);
        p3.setGender("Female");
        p3.setBloodGroup("A+");
        p3.setPhone("9988776655");
        p3.setEmail("priya.patel@example.com");
        p3.setAddress("Sector 15, Gandhinagar, Gujarat 382016");
        p3.setConsentAgreed(true);
        patientRepository.save(p3);

        Visit v3 = new Visit(p3, "S-305", "Surgery", "in_kiosk", "routine");
        v3.setVisitDate(LocalDateTime.now().minusMinutes(40));
        visitRepository.save(v3);

        // 5. Patient 4: Mohammad Farooq
        Patient p4 = new Patient();
        p4.setPatientUid("PAT-2026-01055");
        p4.setFullName("Mohammad Farooq");
        p4.setDob(LocalDate.of(1965, 11, 3));
        p4.setAge(61);
        p4.setGender("Male");
        p4.setBloodGroup("AB+");
        p4.setPhone("9845123456");
        p4.setAddress("Banjara Hills, Road No 12, Hyderabad 500034");
        p4.setConsentAgreed(true);
        patientRepository.save(p4);

        chronicConditionRepository.save(new ChronicCondition(p4, "Coronary Artery Disease", "I25.1", "10 Mar 2022", "Active"));
        medicationRepository.save(new Medication(p4, "Atorvastatin", "20 mg", "Once daily at night", "10 Mar 2022", null, "Dr. S. Reddy", true));

        Visit v4 = new Visit(p4, "A-419", "General Medicine", "with_doctor", "monitor");
        v4.setVisitDate(LocalDateTime.now().minusHours(1));
        v4.setDoctor(doc);
        visitRepository.save(v4);

        // 6. Patient 5: Ananya Sen
        Patient p5 = new Patient();
        p5.setPatientUid("PAT-2026-01280");
        p5.setFullName("Ananya Sen");
        p5.setDob(LocalDate.of(2002, 7, 19));
        p5.setAge(24);
        p5.setGender("Female");
        p5.setBloodGroup("O-");
        p5.setPhone("9711223344");
        p5.setAddress("Salt Lake City, Sector 2, Kolkata 700091");
        p5.setConsentAgreed(true);
        patientRepository.save(p5);

        Visit v5 = new Visit(p5, "Y-112", "Ayurveda", "completed", "routine");
        v5.setVisitDate(LocalDateTime.now().minusHours(2));
        visitRepository.save(v5);

        System.out.println("MediKiosk AI seed data successfully loaded into MySQL!");
    }
}
