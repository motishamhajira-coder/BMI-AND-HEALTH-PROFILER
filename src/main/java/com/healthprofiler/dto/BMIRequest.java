package com.healthprofiler.dto;

import com.healthprofiler.enums.ActivityLevel;
import com.healthprofiler.enums.BiologicalSex;
import com.healthprofiler.enums.HeightUnit;
import com.healthprofiler.enums.WeightUnit;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object representing the JSON body of a BMI calculation request.
 *
 * <p>Required fields: {@code age}, {@code weight}, {@code weightUnit},
 * {@code height}, {@code heightUnit}.</p>
 *
 * <p>Optional fields (needed only for calorie estimation):
 * {@code sex}, {@code activityLevel}, {@code heightInches}.</p>
 *
 * <h3>Example JSON request:</h3>
 * <pre>
 * {
 *   "age": 21,
 *   "weight": 65,
 *   "weightUnit": "KG",
 *   "height": 170,
 *   "heightUnit": "CM",
 *   "sex": "FEMALE",
 *   "activityLevel": "MODERATELY_ACTIVE"
 * }
 * </pre>
 *
 * <p>When {@code heightUnit} is {@code FEET}, you may supply an optional
 * {@code heightInches} value (0–11) for sub-foot precision, e.g.:
 * {@code "height": 5, "heightInches": 9}.</p>
 */
public class BMIRequest {

    // -----------------------------------------------------------------------
    // Required fields
    // -----------------------------------------------------------------------

    /**
     * Age in years. Must be between 1 and 120.
     */
    @NotNull(message = "Age is required.")
    @Min(value = 1,   message = "Age must be at least 1 year.")
    @Max(value = 120, message = "Age must be 120 years or below.")
    private Integer age;

    /**
     * Body weight in the unit specified by {@link #weightUnit}.
     * Must be between 0.5 and 700 (accommodates both kg and lbs ranges).
     */
    @NotNull(message = "Weight is required.")
    @DecimalMin(value = "0.5",  message = "Weight must be greater than 0.5.")
    @DecimalMax(value = "700.0", message = "Weight must be 700 or below (unrealistic beyond this value).")
    private Double weight;

    /**
     * Unit of the provided {@link #weight}.
     */
    @NotNull(message = "Weight unit is required. Accepted values: KG, LBS.")
    private WeightUnit weightUnit;

    /**
     * Body height in the unit specified by {@link #heightUnit}.
     * When {@link #heightUnit} is FEET, this is the whole feet component.
     */
    @NotNull(message = "Height is required.")
    @DecimalMin(value = "0.1",   message = "Height must be greater than 0.1.")
    @DecimalMax(value = "300.0", message = "Height must be 300 or below (unrealistic beyond this value).")
    private Double height;

    /**
     * Unit of the provided {@link #height}.
     */
    @NotNull(message = "Height unit is required. Accepted values: CM, FEET.")
    private HeightUnit heightUnit;

    // -----------------------------------------------------------------------
    // Optional fields
    // -----------------------------------------------------------------------

    /**
     * Inches component of the height when {@link #heightUnit} is FEET.
     * Optional; must be in the range 0–11 when provided.
     */
    @DecimalMin(value = "0.0",  message = "heightInches must be 0 or greater.")
    @DecimalMax(value = "11.9", message = "heightInches must be less than 12.")
    private Double heightInches;

    /**
     * Biological sex – required for BMR and TDEE calculation.
     * Optional; if absent, calorie estimates will not be calculated.
     */
    private BiologicalSex sex;

    /**
     * Physical activity level – required for TDEE calculation.
     * Optional; if absent, only BMR will be calculated (not TDEE).
     */
    private ActivityLevel activityLevel;

    // -----------------------------------------------------------------------
    // Getters and setters
    // -----------------------------------------------------------------------

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public WeightUnit getWeightUnit() { return weightUnit; }
    public void setWeightUnit(WeightUnit weightUnit) { this.weightUnit = weightUnit; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public HeightUnit getHeightUnit() { return heightUnit; }
    public void setHeightUnit(HeightUnit heightUnit) { this.heightUnit = heightUnit; }

    public Double getHeightInches() { return heightInches; }
    public void setHeightInches(Double heightInches) { this.heightInches = heightInches; }

    public BiologicalSex getSex() { return sex; }
    public void setSex(BiologicalSex sex) { this.sex = sex; }

    public ActivityLevel getActivityLevel() { return activityLevel; }
    public void setActivityLevel(ActivityLevel activityLevel) { this.activityLevel = activityLevel; }
}
