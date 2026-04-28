package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<SensorReadingResponse> getTimeline(@PathVariable Long greenhouseId) {
        return sensorReadingService.getTimeline(greenhouseId);
    }

    @PostMapping
    public SensorReadingResponse acceptReading(
            @PathVariable Long greenhouseId,
            @RequestBody SensorReadingRequest request) {
        return sensorReadingService.acceptReading(greenhouseId, request);
    }
}
