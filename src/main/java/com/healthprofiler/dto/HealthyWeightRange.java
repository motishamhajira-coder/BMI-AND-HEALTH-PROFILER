package com.healthprofiler.dto;

/**
 * Represents the estimated healthy weight range based on BMI thresholds.
 *
 * <p>Calculated using the formula:
 * <ul>
 *   <li>Minimum: 18.5 × height(m)²</li>
 *   <li>Maximum: 24.9 × height(m)²</li>
 * </ul>
 * Both values are in <strong>kilograms</strong>.
 * </p>
 */
public class HealthyWeightRange {

    private double minimumKg;
    private double maximumKg;

    public HealthyWeightRange() {}

    public HealthyWeightRange(double minimumKg, double maximumKg) {
        this.minimumKg = minimumKg;
        this.maximumKg = maximumKg;
    }

    public double getMinimumKg() { return minimumKg; }
    public void setMinimumKg(double minimumKg) { this.minimumKg = minimumKg; }

    public double getMaximumKg() { return maximumKg; }
    public void setMaximumKg(double maximumKg) { this.maximumKg = maximumKg; }
}
