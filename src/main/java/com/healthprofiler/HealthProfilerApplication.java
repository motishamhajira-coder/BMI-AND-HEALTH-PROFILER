package com.healthprofiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the BMI Health Profiler application.
 *
 * <p>Module 1 – BMI Calculator and Body Analysis
 * Provides a REST API for calculating BMI, classifying results,
 * estimating healthy weight ranges, daily calorie requirements,
 * and daily water requirements.
 * </p>
 */
@SpringBootApplication
public class HealthProfilerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthProfilerApplication.class, args);
    }
}
