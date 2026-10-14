package com.medikiosk.controller;

import com.medikiosk.dto.SummaryDTOs.TimelineEventDTO;
import com.medikiosk.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@CrossOrigin
public class TimelineController {

    @Autowired
    private TimelineService timelineService;

    @GetMapping("/{patientId}")
    public ResponseEntity<List<TimelineEventDTO>> getTimeline(@PathVariable("patientId") Long patientId) {
        return ResponseEntity.ok(timelineService.getPatientTimeline(patientId));
    }
}
