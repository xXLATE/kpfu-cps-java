package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.GreenhouseRequest;
import com.example.demo.model.Greenhouse;
import com.example.demo.repository.GreenhouseRepository;
import com.example.demo.repository.SensorReadingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GreenhouseService {
    private final GreenhouseRepository greenhouseRepository;
    private final SensorReadingRepository sensorReadingRepository;

    public List<Greenhouse> getAll() {
        return greenhouseRepository.findAll();
    }

    public Greenhouse getById(Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Greenhouse not found"));
    }

    public Greenhouse create(GreenhouseRequest request) {
        validate(request);
        greenhouseRepository.findByName(request.getName()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Greenhouse with this name already exists");
        });

        Greenhouse greenhouse = new Greenhouse();
        greenhouse.setName(request.getName());
        greenhouse.setLocation(request.getLocation());
        return greenhouseRepository.save(greenhouse);
    }

    public Greenhouse update(Long id, GreenhouseRequest request) {
        validate(request);
        Greenhouse greenhouse = getById(id);
        greenhouseRepository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Greenhouse with this name already exists");
                });

        greenhouse.setName(request.getName());
        greenhouse.setLocation(request.getLocation());
        return greenhouseRepository.save(greenhouse);
    }

    @Transactional
    public void delete(Long id) {
        Greenhouse greenhouse = getById(id);
        sensorReadingRepository.deleteByGreenhouseId(id);
        greenhouseRepository.delete(greenhouse);
    }

    private void validate(GreenhouseRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Greenhouse request is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Greenhouse name is required");
        }
        if (request.getLocation() == null || request.getLocation().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Greenhouse location is required");
        }
    }
}
