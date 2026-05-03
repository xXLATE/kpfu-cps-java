package com.example.demo.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class LogImportResponse {
    private final String fileName;
    private final int totalRows;
    private final int savedRows;
    private final int failedRows;
    private final int anomaliesCount;
    private final List<String> errors;

    public LogImportResponse(
            String fileName,
            int totalRows,
            int savedRows,
            int failedRows,
            int anomaliesCount,
            List<String> errors) {
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.savedRows = savedRows;
        this.failedRows = failedRows;
        this.anomaliesCount = anomaliesCount;
        this.errors = errors;
    }
}
