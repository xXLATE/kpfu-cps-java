package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.DeviceCommand;
import com.example.demo.model.SensorReading;

import lombok.Getter;

@Getter
public class SensorReadingResponse {
    private final Long id;
    private final Long greenhouseId;
    private final LocalDateTime measuredAt;
    private final double airTemperature;
    private final double airHumidity;
    private final double soilMoisture;
    private final double lightLevel;
    private final double co2Level;
    private final boolean anomalous;
    private final String anomalyDescription;
    private final DeviceCommand command;

    public SensorReadingResponse(SensorReading reading) {
        this.id = reading.getId();
        this.greenhouseId = reading.getGreenhouse().getId();
        this.measuredAt = reading.getMeasuredAt();
        this.airTemperature = reading.getAirTemperature();
        this.airHumidity = reading.getAirHumidity();
        this.soilMoisture = reading.getSoilMoisture();
        this.lightLevel = reading.getLightLevel();
        this.co2Level = reading.getCo2Level();
        this.anomalous = reading.isAnomalous();
        this.anomalyDescription = reading.getAnomalyDescription();
        this.command = reading.getCommand();
    }
}
