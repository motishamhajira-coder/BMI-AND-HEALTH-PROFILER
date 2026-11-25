package com.healthprofiler.model;

import com.healthprofiler.enums.BMICategory;

/**
 * Domain model holding the classification result for a calculated BMI value.
 *
 * <p>Encapsulates the category, its display colour, and an explanation
 * of what the category means for the user.</p>
 */
public class BMIClassification {

    private final BMICategory category;
    private final String colour;
    private final String explanation;

    public BMIClassification(BMICategory category) {
        this.category    = category;
        this.colour      = category.getColour();
        this.explanation = category.getExplanation();
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public BMICategory getCategory() {
        return category;
    }

    public String getColour() {
        return colour;
    }

    public String getExplanation() {
        return explanation;
    }

    // -----------------------------------------------------------------------
    // Static factory – classify a raw BMI value
    // -----------------------------------------------------------------------

    /**
     * Determines the {@link BMIClassification} for a given BMI value
     * according to WHO standard ranges.
     *
     * <table border="1">
     *   <tr><th>BMI Range</th><th>Category</th></tr>
     *   <tr><td>&lt; 18.5</td><td>UNDERWEIGHT</td></tr>
     *   <tr><td>18.5 – 24.9</td><td>NORMAL_WEIGHT</td></tr>
     *   <tr><td>25.0 – 29.9</td><td>OVERWEIGHT</td></tr>
     *   <tr><td>≥ 30.0</td><td>OBESE</td></tr>
     * </table>
     *
     * @param bmi the calculated BMI value (must be a valid positive number)
     * @return the matching {@link BMIClassification}
     */
    public static BMIClassification classify(double bmi) {
        BMICategory category;

        if (bmi < 18.5) {
            category = BMICategory.UNDERWEIGHT;
        } else if (bmi < 25.0) {
            category = BMICategory.NORMAL_WEIGHT;
        } else if (bmi < 30.0) {
            category = BMICategory.OVERWEIGHT;
        } else {
            category = BMICategory.OBESE;
        }

        return new BMIClassification(category);
    }
}
