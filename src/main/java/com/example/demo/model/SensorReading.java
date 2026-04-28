package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SensorReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "greenhouse_id", nullable = false)
    private Greenhouse greenhouse;

    @Column(nullable = false)
    private LocalDateTime measuredAt;

    @Column(nullable = false)
    private double airTemperature;

    @Column(nullable = false)
    private double airHumidity;

    @Column(nullable = false)
    private double soilMoisture;

    @Column(nullable = false)
    private double lightLevel;

    @Column(nullable = false)
    private double co2Level;

    @Column(nullable = false)
    private boolean anomalous;

    @Column(length = 500)
    private String anomalyDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private DeviceCommand command;
}
