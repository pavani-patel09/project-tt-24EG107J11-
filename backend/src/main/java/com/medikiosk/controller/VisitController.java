package com.medikiosk.controller;

import com.medikiosk.dto.VisitDTOs.*;
import com.medikiosk.model.Visit;
import com.medikiosk.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@CrossOrigin
public class VisitController {

    @Autowired
    private VisitService visitService;

    @PostMapping({"", "/"})
    public ResponseEntity<Visit> createVisit(@RequestBody VisitCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.createVisit(request));
    }

    @PostMapping("/intake")
    public ResponseEntity<Visit> submitIntake(@RequestBody IntakeCreateRequest request) {
        return ResponseEntity.ok(visitService.submitClinicalIntake(request));
    }

    @GetMapping("/queue")
    public ResponseEntity<List<VisitQueueItem>> getVisitsQueue(
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "status_filter", required = false) String statusFilter) {
        return ResponseEntity.ok(visitService.getVisitsQueue(department, priority, statusFilter));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable("id") Long id,
            @RequestBody VisitStatusUpdateRequest request) {
        return ResponseEntity.ok(visitService.updateVisitStatus(id, request));
    }
}
