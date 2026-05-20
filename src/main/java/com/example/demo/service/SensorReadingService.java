package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.SensorReadingRequest;
import com.example.demo.dto.SensorReadingResponse;
import com.example.demo.model.DeviceCommand;
import com.example.demo.model.Greenhouse;
import com.example.demo.model.SensorReading;
import com.example.demo.repository.SensorReadingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorReadingService {
    private final GreenhouseService greenhouseService;
    private final SensorReadingRepository sensorReadingRepository;
    private final TelegramNotificationService telegramNotificationService;

    public List<SensorReadingResponse> getTimeline(Long greenhouseId) {
        greenhouseService.getById(greenhouseId);
        return sensorReadingRepository.findByGreenhouseIdOrderByMeasuredAtDesc(greenhouseId).stream()
                .map(SensorReadingResponse::new)
                .toList();
    }

    public SensorReadingResponse getById(Long greenhouseId, Long readingId) {
        greenhouseService.getById(greenhouseId);
        return new SensorReadingResponse(getReadingForGreenhouse(greenhouseId, readingId));
    }

    public SensorReadingResponse acceptReading(Long greenhouseId, SensorReadingRequest request) {
        Greenhouse greenhouse = greenhouseService.getById(greenhouseId);
        validate(request);

        SensorReading reading = new SensorReading();
        reading.setGreenhouse(greenhouse);
        reading.setMeasuredAt(request.getMeasuredAt() == null ? LocalDateTime.now() : request.getMeasuredAt());
        reading.setAirTemperature(request.getAirTemperature());
        reading.setAirHumidity(request.getAirHumidity());
        reading.setSoilMoisture(request.getSoilMoisture());
        reading.setLightLevel(request.getLightLevel());
        reading.setCo2Level(request.getCo2Level());

        AnomalyDecision decision = analyze(request);
        reading.setAnomalous(decision.anomalous());
        reading.setAnomalyDescription(decision.description());
        reading.setCommand(decision.command());

        SensorReading savedReading = sensorReadingRepository.save(reading);
        if (savedReading.isAnomalous()) {
            telegramNotificationService.sendMessage(buildAnomalyMessage(savedReading));
        }
        return new SensorReadingResponse(savedReading);
    }

    public SensorReadingResponse update(Long greenhouseId, Long readingId, SensorReadingRequest request) {
        greenhouseService.getById(greenhouseId);
        validate(request);

        SensorReading reading = getReadingForGreenhouse(greenhouseId, readingId);
        reading.setMeasuredAt(request.getMeasuredAt() == null ? reading.getMeasuredAt() : request.getMeasuredAt());
        reading.setAirTemperature(request.getAirTemperature());
        reading.setAirHumidity(request.getAirHumidity());
        reading.setSoilMoisture(request.getSoilMoisture());
        reading.setLightLevel(request.getLightLevel());
        reading.setCo2Level(request.getCo2Level());

        AnomalyDecision decision = analyze(request);
        reading.setAnomalous(decision.anomalous());
        reading.setAnomalyDescription(decision.description());
        reading.setCommand(decision.command());

        SensorReading savedReading = sensorReadingRepository.save(reading);
        return new SensorReadingResponse(savedReading);
    }

    public void delete(Long greenhouseId, Long readingId) {
        greenhouseService.getById(greenhouseId);
        sensorReadingRepository.delete(getReadingForGreenhouse(greenhouseId, readingId));
    }

    private SensorReading getReadingForGreenhouse(Long greenhouseId, Long readingId) {
        return sensorReadingRepository.findByIdAndGreenhouseId(readingId, greenhouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor reading not found"));
    }

    private void validate(SensorReadingRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sensor reading request is required");
        }
        if (request.getAirTemperature() == null || request.getAirHumidity() == null
                || request.getSoilMoisture() == null || request.getLightLevel() == null
                || request.getCo2Level() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All sensor values are required");
        }
    }

    private AnomalyDecision analyze(SensorReadingRequest request) {
        List<String> problems = new ArrayList<>();
        DeviceCommand command = DeviceCommand.KEEP_WORKING;

        if (request.getAirTemperature() > 35) {
            problems.add("Air temperature is too high");
            command = DeviceCommand.OPEN_VENT;
        } else if (request.getAirTemperature() < 8) {
            problems.add("Air temperature is too low");
            command = DeviceCommand.TURN_ON_HEATING;
        }

        if (request.getSoilMoisture() < 20) {
            problems.add("Soil moisture is too low");
            command = DeviceCommand.START_WATERING;
        }

        if (request.getLightLevel() < 100) {
            problems.add("Light level is too low");
            command = DeviceCommand.TURN_ON_LIGHT;
        }

        if (request.getAirHumidity() > 90 || request.getCo2Level() > 1500) {
            problems.add("Ventilation is required");
            command = DeviceCommand.OPEN_VENT;
        }

        if (problems.size() >= 3) {
            command = DeviceCommand.EMERGENCY_INSPECTION;
        }

        return new AnomalyDecision(!problems.isEmpty(), String.join("; ", problems), command);
    }

    private record AnomalyDecision(boolean anomalous, String description, DeviceCommand command) {
    }

    private String buildAnomalyMessage(SensorReading reading) {
        return "Greenhouse anomaly detected\n"
                + "Greenhouse: " + reading.getGreenhouse().getName() + "\n"
                + "Measured at: " + reading.getMeasuredAt() + "\n"
                + "Problems: " + reading.getAnomalyDescription() + "\n"
                + "Command: " + reading.getCommand() + "\n"
                + "Temperature: " + reading.getAirTemperature() + "\n"
                + "Humidity: " + reading.getAirHumidity() + "\n"
                + "Soil moisture: " + reading.getSoilMoisture() + "\n"
                + "Light: " + reading.getLightLevel() + "\n"
                + "CO2: " + reading.getCo2Level();
    }
}
