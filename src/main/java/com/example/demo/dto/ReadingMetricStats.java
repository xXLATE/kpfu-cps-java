package com.example.demo.dto;

import lombok.Getter;

@Getter
public class ReadingMetricStats {
    private final double average;
    private final double min;
    private final double max;

    public ReadingMetricStats(double average, double min, double max) {
        this.average = average;
        this.min = min;
        this.max = max;
    }
}
