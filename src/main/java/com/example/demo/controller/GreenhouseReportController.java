package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.GreenhouseReportResponse;
import com.example.demo.service.GreenhouseReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/greenhouses/{greenhouseId}/reports")
@RequiredArgsConstructor
public class GreenhouseReportController {
    private final GreenhouseReportService greenhouseReportService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'REPORT_READ')")
    public GreenhouseReportResponse getReport(
            @PathVariable Long greenhouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return greenhouseReportService.buildReport(greenhouseId, from, to);
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @PreAuthorize("hasPermission(null, 'REPORT_READ')")
    public ResponseEntity<String> exportReport(
            @PathVariable Long greenhouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        String csv = greenhouseReportService.buildReportCsv(greenhouseId, from, to);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"greenhouse-" + greenhouseId + "-report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
