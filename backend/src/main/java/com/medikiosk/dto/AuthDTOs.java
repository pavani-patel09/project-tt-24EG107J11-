package com.medikiosk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class AuthDTOs {

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType = "bearer";
        private String role;
        private String username;
        @JsonProperty("full_name")
        private String fullName;
        @JsonProperty("user_id")
        private Long userId;
        private String department;

        public TokenResponse(String accessToken, String role, String username, String fullName, Long userId, String department) {
            this.accessToken = accessToken;
            this.tokenType = "bearer";
            this.role = role;
            this.username = username;
            this.fullName = fullName;
            this.userId = userId;
            this.department = department;
        }

        public String getAccessToken() { return accessToken; }
        public String getTokenType() { return tokenType; }
        public String getRole() { return role; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public Long getUserId() { return userId; }
        public String getDepartment() { return department; }
    }

    public static class UserRegisterRequest {
        private String username;
        private String email;
        private String password;
        @JsonProperty("full_name")
        private String fullName;
        private String role = "staff";
        private String department = "General Medicine";
        @JsonProperty("license_no")
        private String licenseNo;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getLicenseNo() { return licenseNo; }
        public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    }

    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        @JsonProperty("full_name")
        private String fullName;
        private String role;
        private String department;
        @JsonProperty("license_no")
        private String licenseNo;
        @JsonProperty("is_active")
        private Boolean isActive;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getLicenseNo() { return licenseNo; }
        public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean active) { isActive = active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
