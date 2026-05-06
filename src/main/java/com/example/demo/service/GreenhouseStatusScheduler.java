package com.example.demo.service;

import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.model.Greenhouse;
import com.example.demo.model.SensorReading;
import com.example.demo.repository.GreenhouseRepository;
import com.example.demo.repository.SensorReadingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GreenhouseStatusScheduler {
    private final GreenhouseRepository greenhouseRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final TelegramNotificationService telegramNotificationService;

    @Scheduled(
            fixedDelayString = "${app.telegram.status-delay-ms:300000}",
            initialDelayString = "${app.telegram.status-delay-ms:300000}")
    public void sendPeriodicStatus() {
        if (!telegramNotificationService.isEnabled()) {
            return;
        }

        for (Greenhouse greenhouse : greenhouseRepository.findAll()) {
            Optional<SensorReading> lastReading = sensorReadingRepository
                    .findTopByGreenhouseIdOrderByMeasuredAtDesc(greenhouse.getId());
            telegramNotificationService.sendMessage(buildStatusMessage(greenhouse, lastReading));
        }
    }

    private String buildStatusMessage(Greenhouse greenhouse, Optional<SensorReading> lastReading) {
        if (lastReading.isEmpty()) {
            return "Greenhouse status\n"
                    + "Name: " + greenhouse.getName() + "\n"
                    + "Location: " + greenhouse.getLocation() + "\n"
                    + "State: no sensor data yet";
        }

        SensorReading reading = lastReading.get();
        return "Greenhouse status\n"
                + "Name: " + greenhouse.getName() + "\n"
                + "Measured at: " + reading.getMeasuredAt() + "\n"
                + "Temperature: " + reading.getAirTemperature() + "\n"
                + "Humidity: " + reading.getAirHumidity() + "\n"
                + "Soil moisture: " + reading.getSoilMoisture() + "\n"
                + "Light: " + reading.getLightLevel() + "\n"
                + "CO2: " + reading.getCo2Level() + "\n"
                + "Anomalous: " + reading.isAnomalous() + "\n"
                + "Command: " + reading.getCommand();
    }
}
