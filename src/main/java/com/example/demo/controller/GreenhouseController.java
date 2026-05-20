package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.example.demo.dto.GreenhouseRequest;
import com.example.demo.model.Greenhouse;
import com.example.demo.service.GreenhouseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/greenhouses")
@RequiredArgsConstructor
public class GreenhouseController {
    private final GreenhouseService greenhouseService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'GREENHOUSE_READ')")
    public List<Greenhouse> getAll() {
        return greenhouseService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'GREENHOUSE_READ')")
    public Greenhouse getById(@PathVariable Long id) {
        return greenhouseService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'GREENHOUSE_WRITE')")
    public Greenhouse create(@RequestBody GreenhouseRequest request) {
        return greenhouseService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'GREENHOUSE_WRITE')")
    public Greenhouse update(@PathVariable Long id, @RequestBody GreenhouseRequest request) {
        return greenhouseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission(null, 'GREENHOUSE_WRITE')")
    public void delete(@PathVariable Long id) {
        greenhouseService.delete(id);
    }
}
