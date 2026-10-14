package com.medikiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class StatsDTOs {

    public static class DeptCount {
        private String department;
        private Long count;

        public DeptCount(String department, Long count) {
            this.department = department;
            this.count = count;
        }

        public String getDepartment() { return department; }
        public Long getCount() { return count; }
    }

    public static class PriorityCount {
        private String name;
        private Long count;
        private String color;

        public PriorityCount(String name, Long count, String color) {
            this.name = name;
            this.count = count;
            this.color = color;
        }

        public String getName() { return name; }
        public Long getCount() { return count; }
        public String getColor() { return color; }
    }

    public static class DoctorStatsResponse {
        @JsonProperty("total_patients")
        private Long totalPatients;
        @JsonProperty("high_priority")
        private Long highPriority;
        @JsonProperty("needs_review")
        private Long needsReview;
        private Long monitoring;
        private Long routine;
        @JsonProperty("ai_summaries_ready")
        private Long aiSummariesReady;
        @JsonProperty("ai_summaries_verified")
        private Long aiSummariesVerified;
        private List<DeptCount> departments;
        private List<PriorityCount> priorities;

        public DoctorStatsResponse(Long totalPatients, Long highPriority, Long needsReview, Long monitoring, Long routine, Long aiSummariesReady, Long aiSummariesVerified, List<DeptCount> departments, List<PriorityCount> priorities) {
            this.totalPatients = totalPatients;
            this.highPriority = highPriority;
            this.needsReview = needsReview;
            this.monitoring = monitoring;
            this.routine = routine;
            this.aiSummariesReady = aiSummariesReady;
            this.aiSummariesVerified = aiSummariesVerified;
            this.departments = departments;
            this.priorities = priorities;
        }

        public Long getTotalPatients() { return totalPatients; }
        public Long getHighPriority() { return highPriority; }
        public Long getNeedsReview() { return needsReview; }
        public Long getMonitoring() { return monitoring; }
        public Long getRoutine() { return routine; }
        public Long getAiSummariesReady() { return aiSummariesReady; }
        public Long getAiSummariesVerified() { return aiSummariesVerified; }
        public List<DeptCount> getDepartments() { return departments; }
        public List<PriorityCount> getPriorities() { return priorities; }
    }

    public static class StaffStatsResponse {
        @JsonProperty("in_kiosk")
        private Long inKiosk;
        @JsonProperty("red_flag_alerts")
        private Long redFlagAlerts;
        @JsonProperty("awaiting_doctor_review")
        private Long awaitingDoctorReview;
        @JsonProperty("completed_today")
        private Long completedToday;
        @JsonProperty("total_documents")
        private Long totalDocuments;

        public StaffStatsResponse(Long inKiosk, Long redFlagAlerts, Long awaitingDoctorReview, Long completedToday, Long totalDocuments) {
            this.inKiosk = inKiosk;
            this.redFlagAlerts = redFlagAlerts;
            this.awaitingDoctorReview = awaitingDoctorReview;
            this.completedToday = completedToday;
            this.totalDocuments = totalDocuments;
        }

        public Long getInKiosk() { return inKiosk; }
        public Long getRedFlagAlerts() { return redFlagAlerts; }
        public Long getAwaitingDoctorReview() { return awaitingDoctorReview; }
        public Long getCompletedToday() { return completedToday; }
        public Long getTotalDocuments() { return totalDocuments; }
    }
}
