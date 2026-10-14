package com.medikiosk.service;

import com.medikiosk.dto.StatsDTOs.*;
import com.medikiosk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardStatsService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private ClinicalSummaryRepository clinicalSummaryRepository;

    @Autowired
    private MedicalDocumentRepository documentRepository;

    public DoctorStatsResponse getDoctorStats() {
        long totalPatients = patientRepository.count();
        long highPriority = visitRepository.countByPriority("red_flag");
        long attention = visitRepository.countByPriority("attention");
        long monitor = visitRepository.countByPriority("monitor");
        long routine = visitRepository.countByPriority("routine");

        long aiSummariesReady = clinicalSummaryRepository.count();
        long aiSummariesVerified = clinicalSummaryRepository.countByIsDoctorVerifiedTrue();

        List<Object[]> deptGroups = visitRepository.countByDepartmentGroup();
        List<DeptCount> departments = new ArrayList<>();
        for (Object[] g : deptGroups) {
            String dept = g[0] != null ? (String) g[0] : "General";
            Long count = g[1] != null ? ((Number) g[1]).longValue() : 0L;
            departments.add(new DeptCount(dept, count));
        }

        List<PriorityCount> priorities = new ArrayList<>();
        priorities.add(new PriorityCount("High Priority", highPriority, "#A83A2D"));
        priorities.add(new PriorityCount("Attention", attention, "#B9740E"));
        priorities.add(new PriorityCount("Monitoring", monitor, "#9C7F14"));
        priorities.add(new PriorityCount("Routine", routine, "#2E7D5B"));

        return new DoctorStatsResponse(
                totalPatients,
                highPriority,
                attention,
                monitor,
                routine,
                aiSummariesReady,
                aiSummariesVerified,
                departments,
                priorities
        );
    }

    public StaffStatsResponse getStaffStats() {
        long inKiosk = visitRepository.countByStatus("in_kiosk");
        long redFlags = visitRepository.countByIsRedFlagTrue();
        long awaitingReview = visitRepository.countByStatus("awaiting_review");
        long completed = visitRepository.countByStatus("completed");
        long totalDocs = documentRepository.count();

        return new StaffStatsResponse(inKiosk, redFlags, awaitingReview, completed, totalDocs);
    }
}
