package com.healthprofiler.enums;

/**
 * BMI classification categories with associated display colour and explanation.
 *
 * <p>Based on WHO standard BMI classification ranges.</p>
 */
public enum BMICategory {

    /**
     * BMI below 18.5.
     * Indicates insufficient body mass relative to height.
     */
    UNDERWEIGHT(
            "BLUE",
            "Your BMI indicates that you are underweight. This may suggest insufficient body mass " +
            "relative to your height. Consider consulting a healthcare professional for personalised guidance."
    ),

    /**
     * BMI 18.5 – 24.9.
     * Indicates a healthy proportion of body mass relative to height.
     */
    NORMAL_WEIGHT(
            "GREEN",
            "Your BMI falls within the normal weight category. This generally suggests a healthy " +
            "proportion of body mass relative to your height. Continue maintaining a balanced lifestyle."
    ),

    /**
     * BMI 25.0 – 29.9.
     * Indicates body mass above the healthy range.
     */
    OVERWEIGHT(
            "ORANGE",
            "Your BMI indicates that you are overweight. This suggests your body mass is above the " +
            "healthy range for your height. Modest lifestyle changes may be beneficial."
    ),

    /**
     * BMI 30.0 and above.
     * Indicates significantly elevated body mass that may pose health risks.
     */
    OBESE(
            "RED",
            "Your BMI indicates obesity. This suggests significantly elevated body mass that may " +
            "pose health risks. It is recommended to consult a healthcare professional for guidance."
    );

    private final String colour;
    private final String explanation;

    BMICategory(String colour, String explanation) {
        this.colour = colour;
        this.explanation = explanation;
    }

    public String getColour() {
        return colour;
    }

    public String getExplanation() {
        return explanation;
    }
}
