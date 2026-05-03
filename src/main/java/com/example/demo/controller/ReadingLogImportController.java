package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.LogImportResponse;
import com.example.demo.service.ReadingLogImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/greenhouses/{greenhouseId}/readings/import")
@RequiredArgsConstructor
public class ReadingLogImportController {
    private final ReadingLogImportService readingLogImportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LogImportResponse importCsv(
            @PathVariable Long greenhouseId,
            @RequestParam("file") MultipartFile file) {
        return readingLogImportService.importCsv(greenhouseId, file);
    }
}
