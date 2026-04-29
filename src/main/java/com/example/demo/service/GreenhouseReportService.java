package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.ToDoubleFunction;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.GreenhouseReportResponse;
import com.example.demo.dto.ReadingMetricStats;
import com.example.demo.model.Greenhouse;
import com.example.demo.model.SensorReading;
import com.example.demo.repository.SensorReadingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GreenhouseReportService {
    private final GreenhouseService greenhouseService;
    private final SensorReadingRepository sensorReadingRepository;

    public GreenhouseReportResponse buildReport(Long greenhouseId, LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report period is required");
        }
        if (!from.isBefore(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report period start must be before end");
        }

        Greenhouse greenhouse = greenhouseService.getById(greenhouseId);
        List<SensorReading> readings = sensorReadingRepository
                .findByGreenhouseIdAndMeasuredAtBetweenOrderByMeasuredAtAsc(greenhouseId, from, to);
        long anomaliesCount = readings.stream().filter(SensorReading::isAnomalous).count();

        return new GreenhouseReportResponse(
                greenhouse.getId(),
                greenhouse.getName(),
                from,
                to,
                readings.size(),
                anomaliesCount,
                buildStatus(readings.size(), anomaliesCount),
                stats(readings, SensorReading::getAirTemperature),
                stats(readings, SensorReading::getAirHumidity),
                stats(readings, SensorReading::getSoilMoisture),
                stats(readings, SensorReading::getLightLevel),
                stats(readings, SensorReading::getCo2Level));
    }

    private ReadingMetricStats stats(List<SensorReading> readings, ToDoubleFunction<SensorReading> extractor) {
        if (readings.isEmpty()) {
            return new ReadingMetricStats(0, 0, 0);
        }
        DoubleSummaryStatistics summary = readings.stream()
                .mapToDouble(extractor)
                .summaryStatistics();
        return new ReadingMetricStats(summary.getAverage(), summary.getMin(), summary.getMax());
    }

    private String buildStatus(long readingsCount, long anomaliesCount) {
        if (readingsCount == 0) {
            return "NO_DATA";
        }
        if (anomaliesCount == 0) {
            return "NORMAL";
        }
        if (anomaliesCount * 2 >= readingsCount) {
            return "CRITICAL";
        }
        return "WARNING";
    }
}
