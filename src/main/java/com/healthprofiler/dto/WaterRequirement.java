package com.healthprofiler.dto;

/**
 * Represents the estimated daily water intake requirement.
 *
 * <p>Calculated using:
 * <ul>
 *   <li>Minimum: weight(kg) × 30 ml → converted to litres</li>
 *   <li>Maximum: weight(kg) × 35 ml → converted to litres</li>
 * </ul>
 * </p>
 *
 * <p><strong>Important:</strong> This is a general estimate.
 * Actual hydration needs vary based on climate, activity level, health status,
 * diet composition, and other individual factors.</p>
 */
public class WaterRequirement {

    private double minimumLitres;
    private double maximumLitres;
    private String note;

    public WaterRequirement() {}

    public WaterRequirement(double minimumLitres, double maximumLitres) {
        this.minimumLitres = minimumLitres;
        this.maximumLitres = maximumLitres;
        this.note = "This is a general estimate. Actual hydration needs vary based on climate, " +
                    "activity level, health status, and individual factors.";
    }

    public double getMinimumLitres() { return minimumLitres; }
    public void setMinimumLitres(double minimumLitres) { this.minimumLitres = minimumLitres; }

    public double getMaximumLitres() { return maximumLitres; }
    public void setMaximumLitres(double maximumLitres) { this.maximumLitres = maximumLitres; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
