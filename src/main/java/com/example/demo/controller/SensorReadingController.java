package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.SensorReadingRequest;
import com.example.demo.dto.SensorReadingResponse;
import com.example.demo.service.SensorReadingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/greenhouses/{greenhouseId}/readings")
@RequiredArgsConstructor
public class SensorReadingController {
    private final SensorReadingService sensorReadingService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'READING_READ')")
    public List<SensorReadingResponse> getTimeline(@PathVariable Long greenhouseId) {
        return sensorReadingService.getTimeline(greenhouseId);
    }

    @GetMapping("/{readingId}")
    @PreAuthorize("hasPermission(null, 'READING_READ')")
    public SensorReadingResponse getById(
            @PathVariable Long greenhouseId,
            @PathVariable Long readingId) {
        return sensorReadingService.getById(greenhouseId, readingId);
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'READING_WRITE')")
    public SensorReadingResponse acceptReading(
            @PathVariable Long greenhouseId,
            @RequestBody SensorReadingRequest request) {
        return sensorReadingService.acceptReading(greenhouseId, request);
    }

    @PutMapping("/{readingId}")
    @PreAuthorize("hasPermission(null, 'READING_WRITE')")
    public SensorReadingResponse update(
            @PathVariable Long greenhouseId,
            @PathVariable Long readingId,
            @RequestBody SensorReadingRequest request) {
        return sensorReadingService.update(greenhouseId, readingId, request);
    }

    @DeleteMapping("/{readingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(null, 'READING_WRITE')")
    public void delete(
            @PathVariable Long greenhouseId,
            @PathVariable Long readingId) {
        sensorReadingService.delete(greenhouseId, readingId);
    }
}
