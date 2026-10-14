package com.medikiosk.controller;

import com.medikiosk.dto.StatsDTOs.DoctorStatsResponse;
import com.medikiosk.dto.StatsDTOs.StaffStatsResponse;
import com.medikiosk.service.DashboardStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class StatsController {

    @Autowired
    private DashboardStatsService statsService;

    @GetMapping("/doctor")
    public ResponseEntity<DoctorStatsResponse> getDoctorStats() {
        return ResponseEntity.ok(statsService.getDoctorStats());
    }

    @GetMapping("/staff")
    public ResponseEntity<StaffStatsResponse> getStaffStats() {
        return ResponseEntity.ok(statsService.getStaffStats());
    }
}
