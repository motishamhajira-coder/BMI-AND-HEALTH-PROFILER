package com.healthprofiler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthprofiler.enums.BMICategory;

import java.util.List;

/**
 * Data Transfer Object representing the JSON body of a BMI calculation response.
 *
 * <p>All fields that may be absent (e.g., {@link #bmr} when sex is not provided)
 * are annotated with {@link JsonInclude} to be included as {@code null} in the JSON
 * so that integrating modules always see a consistent structure.</p>
 *
 * <h3>Calorie status values:</h3>
 * <ul>
 *   <li>{@code "CALCULATED"}        – both BMR and TDEE have been calculated</li>
 *   <li>{@code "BMR_ONLY"}          – BMR calculated but no activity level provided</li>
 *   <li>{@code "PENDING_SEX"}       – sex not provided; calorie estimate unavailable</li>
 * </ul>
 *
 * <h3>BMI Disclaimer:</h3>
 * <p>BMI is a screening tool, not a diagnostic measure. It may not accurately reflect
 * body composition for children, adolescents, pregnant individuals, the elderly,
 * or highly muscular individuals. Consult a healthcare professional for personalised advice.</p>
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class BMIResponse {

    // -----------------------------------------------------------------------
    // Core BMI results
    // -----------------------------------------------------------------------

    /** Rounded BMI value (2 decimal places). */
    private double bmi;

    /** WHO classification category. */
    private BMICategory category;

    /** Display colour associated with the category. */
    private String colour;

    /** Short textual explanation of what the category means. */
    private String explanation;

    // -----------------------------------------------------------------------
    // Healthy weight range
    // -----------------------------------------------------------------------

    /** Estimated healthy weight range in kilograms. */
    private HealthyWeightRange healthyWeightRange;

    // -----------------------------------------------------------------------
    // Calorie estimates
    // -----------------------------------------------------------------------

    /**
     * Basal Metabolic Rate in kcal/day (Mifflin-St Jeor).
     * {@code null} if sex was not provided.
     */
    private Double bmr;

    /**
     * Total Daily Energy Expenditure in kcal/day.
     * {@code null} if sex or activity level was not provided.
     */
    private Double dailyCalories;

    /**
     * Indicates whether calorie figures were fully calculated or are pending.
     * Values: {@code CALCULATED}, {@code BMR_ONLY}, {@code PENDING_SEX}.
     */
    private String calorieStatus;

    // -----------------------------------------------------------------------
    // Water requirement
    // -----------------------------------------------------------------------

    /** Estimated daily water intake range in litres. */
    private WaterRequirement dailyWater;

    // -----------------------------------------------------------------------
    // Recommendations
    // -----------------------------------------------------------------------

    /**
     * Initial general health recommendations based on BMI category.
     * Does NOT include nutrition plans or lifestyle assessments (Person 2).
     */
    private List<String> recommendations;

    // -----------------------------------------------------------------------
    // BMI disclaimer
    // -----------------------------------------------------------------------

    /** Standard BMI limitations disclaimer. */
    private String disclaimer;

    // -----------------------------------------------------------------------
    // Getters and setters
    // -----------------------------------------------------------------------

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public BMICategory getCategory() { return category; }
    public void setCategory(BMICategory category) { this.category = category; }

    public String getColour() { return colour; }
    public void setColour(String colour) { this.colour = colour; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public HealthyWeightRange getHealthyWeightRange() { return healthyWeightRange; }
    public void setHealthyWeightRange(HealthyWeightRange healthyWeightRange) { this.healthyWeightRange = healthyWeightRange; }

    public Double getBmr() { return bmr; }
    public void setBmr(Double bmr) { this.bmr = bmr; }

    public Double getDailyCalories() { return dailyCalories; }
    public void setDailyCalories(Double dailyCalories) { this.dailyCalories = dailyCalories; }

    public String getCalorieStatus() { return calorieStatus; }
    public void setCalorieStatus(String calorieStatus) { this.calorieStatus = calorieStatus; }

    public WaterRequirement getDailyWater() { return dailyWater; }
    public void setDailyWater(WaterRequirement dailyWater) { this.dailyWater = dailyWater; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}
