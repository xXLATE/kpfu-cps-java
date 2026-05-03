package com.example.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.LogImportResponse;
import com.example.demo.dto.SensorReadingRequest;
import com.example.demo.dto.SensorReadingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadingLogImportService {
    private static final int EXPECTED_COLUMNS = 6;

    private final GreenhouseService greenhouseService;
    private final SensorReadingService sensorReadingService;

    public LogImportResponse importCsv(Long greenhouseId, MultipartFile file) {
        greenhouseService.getById(greenhouseId);
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file is required");
        }

        int totalRows = 0;
        int savedRows = 0;
        int anomaliesCount = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || isHeader(line)) {
                    continue;
                }

                totalRows++;
                try {
                    SensorReadingResponse response = sensorReadingService.acceptReading(
                            greenhouseId,
                            parseLine(line));
                    savedRows++;
                    if (response.isAnomalous()) {
                        anomaliesCount++;
                    }
                } catch (RuntimeException exception) {
                    errors.add("Line " + lineNumber + ": " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read uploaded file", exception);
        }

        return new LogImportResponse(
                file.getOriginalFilename(),
                totalRows,
                savedRows,
                totalRows - savedRows,
                anomaliesCount,
                errors);
    }

    private boolean isHeader(String line) {
        return line.toLowerCase().contains("measuredat")
                || line.toLowerCase().contains("airtemperature");
    }

    private SensorReadingRequest parseLine(String line) {
        String[] columns = line.split(",");
        if (columns.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException("Expected 6 CSV columns");
        }

        SensorReadingRequest request = new SensorReadingRequest();
        request.setMeasuredAt(parseDateTime(columns[0]));
        request.setAirTemperature(parseDouble(columns[1], "airTemperature"));
        request.setAirHumidity(parseDouble(columns[2], "airHumidity"));
        request.setSoilMoisture(parseDouble(columns[3], "soilMoisture"));
        request.setLightLevel(parseDouble(columns[4], "lightLevel"));
        request.setCo2Level(parseDouble(columns[5], "co2Level"));
        return request;
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid measuredAt value");
        }
    }

    private double parseDouble(String value, String columnName) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid " + columnName + " value");
        }
    }
}
