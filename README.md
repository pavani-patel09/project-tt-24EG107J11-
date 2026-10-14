# MediKiosk AI — Java Full-Stack Patient History & Clinical Intake Management System

**MediKiosk AI** is an enterprise-grade Java full-stack clinical intake and medical record management platform. It automates returning patient identification, performs duplicate prevention, processes uploaded medical prescriptions and lab reports with Apache PDFBox OCR, provides multilingual guided patient triage with speech input, generates longitudinal health timelines, and delivers AI-assisted clinical summaries with clinician review and digital approval.

---

## 🏗️ Technology Stack

- **Backend**: Java 21+ / Spring Boot 3.3.4, Spring Data JPA, Hibernate ORM, Spring Security with JJWT (JWT), Apache PDFBox (OCR/PDF Parsing), Jakarta Validation.
- **Frontend**: React 18, Vite, Lucide Icons, Custom MediChrono CSS Design System.
- **Database**: MySQL 8.0 (Database: `medikiosk_db`, user: `root`).
- **Speech**: Web Speech API (`SpeechRecognition` & `SpeechSynthesis`) supporting English, Hindi (हिन्दी), and Telugu (తెలుగు).

---

## 📁 Project Structure

```
project tt(24EG107J11)/
├── backend/
│   ├── pom.xml                                  # Maven dependencies & build configuration
│   ├── mvnw & mvnw.cmd                          # Maven Wrapper (no separate Maven install required)
│   ├── uploads/                                 # Secure uploaded file storage directory
│   └── src/main/
│       ├── resources/
│       │   └── application.properties           # Spring Boot, MySQL, JPA & JWT configuration
│       └── java/com/medikiosk/
│           ├── MediKioskApplication.java        # Main Spring Boot Application entrypoint
│           ├── config/
│           │   ├── SecurityConfig.java          # Spring Security, CORS, stateless filter chain
│           │   ├── JwtService.java              # JWT generation, signing & token validation
│           │   ├── JwtAuthFilter.java           # Bearer token authentication filter
│           │   └── DataSeeder.java              # Realistic Indian patient demographic & clinical seeder
│           ├── model/
│           │   ├── User.java                    # Users, RBAC roles (doctor, staff, admin, patient)
│           │   ├── Patient.java                 # Demographics, contact info, clinical relations
│           │   ├── Allergy.java                 # Patient allergies & severity
│           │   ├── ChronicCondition.java        # Chronic illnesses & ICD-10 codes
│           │   ├── Medication.java              # Active prescriptions & dosages
│           │   ├── Visit.java                   # Tokens (e.g. A-108), departments, red flags
│           │   ├── IntakeRecord.java            # Chief complaints, onset, duration, severity
│           │   ├── MedicalDocument.java         # Uploaded documents & OCR status
│           │   ├── ExtractedField.java          # Confidence-scored extracted medical fields
│           │   ├── ClinicalSummary.java         # Longitudinal summary & doctor verification
│           │   └── AuditLog.java                # Regulatory audit logging
│           ├── dto/                             # Clean request/response data transfer objects
│           ├── repository/                      # Spring Data JPA repositories
│           ├── service/                         # Clinical business logic & OCR parser
│           └── controller/                      # REST API endpoints (/api/auth, /api/patients, etc.)
│
├── frontend/
│   ├── index.html                               # HTML root with fonts & metadata
│   ├── package.json                             # React 18, Vite, Lucide dependencies
│   ├── vite.config.js                           # Vite config with backend proxy (port 8080)
│   └── src/
│       ├── main.jsx                             # React root entrypoint
│       ├── App.jsx                              # View navigation & state management
│       ├── index.css                            # Modern medical design system CSS
│       ├── context/
│       │   ├── AuthContext.jsx                  # JWT authentication state
│       │   └── LanguageContext.jsx              # English, Hindi, Telugu translations
│       ├── services/
│       │   └── api.js                           # Centralized API client
│       ├── components/                          # Navbar, Search, Voice, TTS, OCR Verification
│       └── pages/                               # Doctor Dashboard, Kiosk, Registration, Case View
│
├── run.bat                                      # 1-Click launcher for Windows
├── .vscode/                                     # VS Code Tasks & Launch configurations
└── README.md
```

---

## 🚀 Running the Project

### Option A: 1-Click Launcher (Easiest)
Double-click `run.bat` or run in terminal:
```powershell
.\run.bat
```

### Option B: In VS Code (Ctrl + Shift + B)
1. Press **`Ctrl + Shift + B`** in VS Code.
2. Select **`Run Full Stack (MediKiosk AI)`**.
3. It starts both the Spring Boot backend and React frontend automatically.

### Option C: Manual via Two Terminals

#### Terminal 1 — Start Spring Boot Backend:
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
- API Base: `http://localhost:8080/api`

#### Terminal 2 — Start React Frontend:
```powershell
cd frontend
npm.cmd run dev
```
- Web Application: `http://localhost:5173`

---

## 🔑 Test Credentials & Demo Personas

| Role | Username | Password | Full Name | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Doctor** | `doc_sharma` | `DoctorPassword123!` | Dr. A. Sharma | General Medicine • Lic: MCI-2018-99420 |
| **Triage Staff** | `staff_meena` | `StaffPassword123!` | Meena Rao | Triage Staff • Lic: STF-0091 |
| **Administrator** | `admin` | `AdminPassword123!` | Administrator | System Admin |
| **Patient** | `patient_sunita` | `PatientPassword123!` | Sunita Devi | Patient Portal |
