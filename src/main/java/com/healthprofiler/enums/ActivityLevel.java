package com.healthprofiler.enums;

/**
 * Activity level options for TDEE (Total Daily Energy Expenditure) calculation.
 *
 * <p>Each level carries a PAL (Physical Activity Level) multiplier
 * applied to the calculated BMR to estimate daily calorie requirements.</p>
 */
public enum ActivityLevel {

    /**
     * Little or no exercise; desk job.
     * Multiplier: 1.2
     */
    SEDENTARY(1.2, "Little or no exercise, desk job"),

    /**
     * Light exercise 1–3 days per week.
     * Multiplier: 1.375
     */
    LIGHTLY_ACTIVE(1.375, "Light exercise 1–3 days per week"),

    /**
     * Moderate exercise 3–5 days per week.
     * Multiplier: 1.55
     */
    MODERATELY_ACTIVE(1.55, "Moderate exercise 3–5 days per week"),

    /**
     * Hard exercise 6–7 days per week.
     * Multiplier: 1.725
     */
    VERY_ACTIVE(1.725, "Hard exercise 6–7 days per week"),

    /**
     * Very hard exercise; physical job or twice-daily training.
     * Multiplier: 1.9
     */
    EXTRA_ACTIVE(1.9, "Very hard exercise, physical job or twice-daily training");

    private final double multiplier;
    private final String description;

    ActivityLevel(double multiplier, String description) {
        this.multiplier = multiplier;
        this.description = description;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getDescription() {
        return description;
    }
}
