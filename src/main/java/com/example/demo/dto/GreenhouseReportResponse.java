package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class GreenhouseReportResponse {
    private final Long greenhouseId;
    private final String greenhouseName;
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final long readingsCount;
    private final long anomaliesCount;
    private final String status;
    private final ReadingMetricStats airTemperature;
    private final ReadingMetricStats airHumidity;
    private final ReadingMetricStats soilMoisture;
    private final ReadingMetricStats lightLevel;
    private final ReadingMetricStats co2Level;

    public GreenhouseReportResponse(
            Long greenhouseId,
            String greenhouseName,
            LocalDateTime from,
            LocalDateTime to,
            long readingsCount,
            long anomaliesCount,
            String status,
            ReadingMetricStats airTemperature,
            ReadingMetricStats airHumidity,
            ReadingMetricStats soilMoisture,
            ReadingMetricStats lightLevel,
            ReadingMetricStats co2Level) {
        this.greenhouseId = greenhouseId;
        this.greenhouseName = greenhouseName;
        this.from = from;
        this.to = to;
        this.readingsCount = readingsCount;
        this.anomaliesCount = anomaliesCount;
        this.status = status;
        this.airTemperature = airTemperature;
        this.airHumidity = airHumidity;
        this.soilMoisture = soilMoisture;
        this.lightLevel = lightLevel;
        this.co2Level = co2Level;
    }
}
