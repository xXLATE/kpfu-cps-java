package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorReadingRequest {
    private LocalDateTime measuredAt;
    private Double airTemperature;
    private Double airHumidity;
    private Double soilMoisture;
    private Double lightLevel;
    private Double co2Level;
}
