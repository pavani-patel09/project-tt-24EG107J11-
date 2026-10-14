package com.medikiosk.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrExtractionService {

    private static final List<Pattern> HOSPITAL_PATTERNS = Arrays.asList(
            Pattern.compile("(?i)(?:Hospital|Clinic|Medical Center|Healthcare|Diagnostics|Lab|Dispensary)[\\s\\w.,-]+"),
            Pattern.compile("(?i)[\\w\\s.,-]+(?:Hospital|Clinic|Diagnostics|Lab)")
    );

    private static final List<Pattern> DOCTOR_PATTERNS = Arrays.asList(
            Pattern.compile("Dr\\.?\\s+[A-Z][a-zA-Z.\\s]+"),
            Pattern.compile("Doctor\\s+[A-Z][a-zA-Z.\\s]+")
    );

    private static final List<Pattern> DATE_PATTERNS = Arrays.asList(
            Pattern.compile("\\b\\d{1,2}[/\\-.]\\d{1,2}[/\\-.]\\d{2,4}\\b"),
            Pattern.compile("(?i)\\b\\d{1,2}\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*[\\s,]+\\d{2,4}\\b")
    );

    private static final List<Pattern> MEDICATION_PATTERNS = Arrays.asList(
            Pattern.compile("(?i)(?:Tab|Cap|Syp|Inj)?\\.?\\s*([A-Za-z]+(?:ine|ol|ide|in|cin|pam|zole|pril|sartan|formin|mab|xacin))\\s+(\\d+\\s*(?:mg|ml|mcg|gm))"),
            Pattern.compile("(?i)\\b(Amlodipine|Metformin|Paracetamol|Telmisartan|Atorvastatin|Omeprazole|Azithromycin|Pantoprazole|Losartan|Cetirizine)\\s*(\\d+\\s*(?:mg|ml|mcg))?")
    );

    private static final List<String> DIAGNOSIS_KEYWORDS = Arrays.asList(
            "Hypertension", "Type 2 Diabetes", "Diabetes Mellitus", "Essential Hypertension",
            "Asthma", "Acute Bronchitis", "Osteoarthritis", "Cholecystectomy", "Gastritis",
            "Migraine", "Coronary Artery Disease", "Hypothyroidism", "Fever", "Viral Illness",
            "Gastroenteritis", "URTI", "Upper Respiratory Tract Infection"
    );

    private static final List<Pattern> LAB_PATTERNS = Collections.singletonList(
            Pattern.compile("(?i)(?:HbA1c|FBS|Fasting Blood Sugar|PPBS|Serum Creatinine|Hemoglobin|Platelets|Total Cholesterol|TSH|Bilirubin)\\s*[:\\-=]?\\s*([0-9.]+\\s*(?:mg/dL|%|g/dL|mcIU/mL|/mcL)?)")
    );

    public String extractTextFromPdf(File pdfFile) {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).trim();
        } catch (IOException e) {
            System.err.println("PDFBox extraction failed: " + e.getMessage());
            return "";
        }
    }

    public static class ExtractedFieldResult {
        private String fieldName;
        private String extractedValue;
        private Double confidence;

        public ExtractedFieldResult(String fieldName, String extractedValue, Double confidence) {
            this.fieldName = fieldName;
            this.extractedValue = extractedValue;
            this.confidence = confidence;
        }

        public String getFieldName() { return fieldName; }
        public String getExtractedValue() { return extractedValue; }
        public Double getConfidence() { return confidence; }
    }

    public List<ExtractedFieldResult> processExtractedFields(String rawText, String originalFilename, String docTypeHint) {
        List<ExtractedFieldResult> fields = new ArrayList<>();

        // 1. Document Type
        String cleanType = docTypeHint != null ? docTypeHint.replace("_", " ") : "Prescription";
        cleanType = Character.toUpperCase(cleanType.charAt(0)) + cleanType.substring(1);
        fields.add(new ExtractedFieldResult("Document Type", cleanType, 0.98));

        // 2. Date
        String foundDate = null;
        if (rawText != null && !rawText.isEmpty()) {
            for (Pattern p : DATE_PATTERNS) {
                Matcher m = p.matcher(rawText);
                if (m.find()) {
                    foundDate = m.group(0);
                    break;
                }
            }
        }
        fields.add(new ExtractedFieldResult("Date", foundDate != null ? foundDate : "Not recorded (Needs review)", foundDate != null ? 0.95 : 0.50));

        // 3. Doctor
        String foundDoctor = null;
        if (rawText != null && !rawText.isEmpty()) {
            for (Pattern p : DOCTOR_PATTERNS) {
                Matcher m = p.matcher(rawText);
                if (m.find()) {
                    foundDoctor = m.group(0).trim();
                    break;
                }
            }
        }
        fields.add(new ExtractedFieldResult("Doctor", foundDoctor != null ? foundDoctor : "Not clearly identified", foundDoctor != null ? 0.92 : 0.60));

        // 4. Hospital / Facility
        String foundHospital = null;
        if (rawText != null && !rawText.isEmpty()) {
            for (Pattern p : HOSPITAL_PATTERNS) {
                Matcher m = p.matcher(rawText);
                if (m.find()) {
                    foundHospital = m.group(0).trim();
                    break;
                }
            }
        }
        fields.add(new ExtractedFieldResult("Hospital / Facility", foundHospital != null ? foundHospital : "Not clearly identified", foundHospital != null ? 0.88 : 0.55));

        // 5. Diagnosis
        String foundDiagnosis = null;
        if (rawText != null && !rawText.isEmpty()) {
            for (String diag : DIAGNOSIS_KEYWORDS) {
                if (Pattern.compile("(?i)\\b" + Pattern.quote(diag) + "\\b").matcher(rawText).find()) {
                    foundDiagnosis = diag;
                    break;
                }
            }
        }
        fields.add(new ExtractedFieldResult("Diagnosis", foundDiagnosis != null ? foundDiagnosis : "Not recorded", foundDiagnosis != null ? 0.93 : 0.50));

        // 6. Medicines or Lab Results
        boolean isLab = docTypeHint != null && docTypeHint.toLowerCase().contains("lab");
        if (isLab) {
            String foundLab = null;
            if (rawText != null && !rawText.isEmpty()) {
                for (Pattern p : LAB_PATTERNS) {
                    Matcher m = p.matcher(rawText);
                    if (m.find()) {
                        foundLab = m.group(0).trim();
                        break;
                    }
                }
            }
            fields.add(new ExtractedFieldResult("Lab Test & Result", foundLab != null ? foundLab : "Not recorded", foundLab != null ? 0.90 : 0.50));
        } else {
            Set<String> foundMeds = new LinkedHashSet<>();
            if (rawText != null && !rawText.isEmpty()) {
                for (Pattern p : MEDICATION_PATTERNS) {
                    Matcher m = p.matcher(rawText);
                    while (m.find()) {
                        String med = m.group(0).trim();
                        foundMeds.add(med);
                    }
                }
            }
            String medsVal = !foundMeds.isEmpty() ? String.join(", ", foundMeds) : "Not recorded";
            fields.add(new ExtractedFieldResult("Medicines & Dosages", medsVal, !foundMeds.isEmpty() ? 0.91 : 0.50));
        }

        return fields;
    }
}
