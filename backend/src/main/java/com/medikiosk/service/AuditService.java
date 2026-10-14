package com.medikiosk.service;

import com.medikiosk.model.AuditLog;
import com.medikiosk.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(Long userId, Long patientId, String action, String details, String ipAddress) {
        try {
            AuditLog log = new AuditLog(userId, patientId, action, details, ipAddress);
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Do not break clinical flow on audit logging error
            System.err.println("Audit log error: " + e.getMessage());
        }
    }

    public void log(Long userId, Long patientId, String action, String details) {
        log(userId, patientId, action, details, "127.0.0.1");
    }
}
