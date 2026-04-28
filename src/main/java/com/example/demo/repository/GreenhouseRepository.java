package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Greenhouse;

@Repository
public interface GreenhouseRepository extends JpaRepository<Greenhouse, Long> {
    Optional<Greenhouse> findByName(String name);
}
