package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.SensorReading;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findByGreenhouseIdOrderByMeasuredAtDesc(Long greenhouseId);

    List<SensorReading> findByGreenhouseIdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
            Long greenhouseId,
            java.time.LocalDateTime from,
            java.time.LocalDateTime to);
}
