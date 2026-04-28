package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.GreenhouseRequest;
import com.example.demo.model.Greenhouse;
import com.example.demo.repository.GreenhouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GreenhouseService {
    private final GreenhouseRepository greenhouseRepository;

    public List<Greenhouse> getAll() {
        return greenhouseRepository.findAll();
    }

    public Greenhouse getById(Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Greenhouse not found"));
    }

    public Greenhouse create(GreenhouseRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Greenhouse name is required");
        }
        if (request.getLocation() == null || request.getLocation().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Greenhouse location is required");
        }
        greenhouseRepository.findByName(request.getName()).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Greenhouse with this name already exists");
        });

        Greenhouse greenhouse = new Greenhouse();
        greenhouse.setName(request.getName());
        greenhouse.setLocation(request.getLocation());
        return greenhouseRepository.save(greenhouse);
    }
}
