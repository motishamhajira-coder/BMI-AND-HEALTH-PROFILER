package com.healthprofiler.service;

import com.healthprofiler.dto.BMIRequest;
import com.healthprofiler.dto.BMIResponse;
import com.healthprofiler.dto.HealthyWeightRange;
import com.healthprofiler.dto.WaterRequirement;
import com.healthprofiler.enums.ActivityLevel;
import com.healthprofiler.enums.BiologicalSex;
import com.healthprofiler.enums.HeightUnit;
import com.healthprofiler.exception.InvalidMeasurementException;
import com.healthprofiler.model.BMIClassification;
import com.healthprofiler.util.MeasurementConverter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Core service for BMI calculation and body analysis.
 *
 * <p>This service implements all calculations for Module 1:
 * <ol>
 *   <li>Measurement conversion (using {@link MeasurementConverter})</li>
 *   <li>BMI calculation – Standard formula: BMI = kg / m²</li>
 *   <li>BMI classification – WHO standard ranges</li>
 *   <li>Healthy weight range – based on BMI thresholds 18.5 and 24.9</li>
 *   <li>BMR calculation – Mifflin-St Jeor formula</li>
 *   <li>TDEE calculation – BMR × activity multiplier</li>
 *   <li>Water requirement – 30–35 ml per kg of body weight</li>
 *   <li>Category-based health recommendations</li>
 * </ol>
 * </p>
 *
 * <p><strong>Note:</strong> This service does NOT implement nutrition assessment
 * or lifestyle profiling (assigned to Person 2).</p>
 */
@Service
public class BMIService {

    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Lower BMI boundary for the healthy weight range. */
    private static final double HEALTHY_BMI_MIN = 18.5;

    /** Upper BMI boundary for the healthy weight range. */
    private static final double HEALTHY_BMI_MAX = 24.9;

    /** Millilitres of water per kilogram of body weight (minimum estimate). */
    private static final double WATER_ML_PER_KG_MIN = 30.0;

    /** Millilitres of water per kilogram of body weight (maximum estimate). */
    private static final double WATER_ML_PER_KG_MAX = 35.0;

    /** Millilitres per litre – conversion constant. */
    private static final double ML_PER_LITRE = 1000.0;

    /** Standard BMI disclaimer text. */
    private static final String DISCLAIMER =
            "BMI is a population-level screening tool, not a clinical diagnostic measure. " +
            "It may not accurately reflect body composition for children, adolescents, " +
            "pregnant or breastfeeding individuals, the elderly, or highly muscular individuals. " +
            "Please consult a qualified healthcare professional for personalised health advice.";

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Calculates a full BMI analysis from the provided request.
     *
     * @param request validated request containing user measurements
     * @return fully populated {@link BMIResponse}
     * @throws InvalidMeasurementException if any measurement produces an invalid result
     */
    public BMIResponse calculate(BMIRequest request) {

        // 1. Convert measurements to SI units
        double weightKg = convertWeight(request);
        double heightM  = convertHeight(request);
        double heightCm = MeasurementConverter.metresToCentimetres(heightM);

        // 2. Calculate BMI
        double rawBmi  = calculateBMI(weightKg, heightM);
        double roundedBmi = round(rawBmi, 2);

        // 3. Classify BMI
        BMIClassification classification = BMIClassification.classify(rawBmi);

        // 4. Calculate healthy weight range
        HealthyWeightRange healthyWeightRange = calculateHealthyWeightRange(heightM);

        // 5. BMR and TDEE calculation (optional – depends on sex / activity level)
        CalorieResult calorieResult = calculateCalories(
                request.getSex(),
                request.getActivityLevel(),
                weightKg,
                heightCm,
                request.getAge()
        );

        // 6. Water requirement
        WaterRequirement waterRequirement = calculateWaterRequirement(weightKg);

        // 7. Recommendations
        List<String> recommendations = generateRecommendations(classification);

        // 8. Assemble response
        BMIResponse response = new BMIResponse();
        response.setBmi(roundedBmi);
        response.setCategory(classification.getCategory());
        response.setColour(classification.getColour());
        response.setExplanation(classification.getExplanation());
        response.setHealthyWeightRange(healthyWeightRange);
        response.setBmr(calorieResult.bmr());
        response.setDailyCalories(calorieResult.tdee());
        response.setCalorieStatus(calorieResult.status());
        response.setDailyWater(waterRequirement);
        response.setRecommendations(recommendations);
        response.setDisclaimer(DISCLAIMER);

        return response;
    }

    // -----------------------------------------------------------------------
    // 1. Measurement conversion helpers
    // -----------------------------------------------------------------------

    /**
     * Converts the request's weight to kilograms using {@link MeasurementConverter}.
     */
    double convertWeight(BMIRequest request) {
        return MeasurementConverter.toKilograms(request.getWeight(), request.getWeightUnit());
    }

    /**
     * Converts the request's height to metres.
     *
     * <p>When the height unit is FEET and {@code heightInches} is provided,
     * the feet+inches form of conversion is used for sub-foot precision.</p>
     */
    double convertHeight(BMIRequest request) {
        if (request.getHeightUnit() == HeightUnit.FEET && request.getHeightInches() != null) {
            return MeasurementConverter.feetAndInchesToMetres(
                    request.getHeight(),
                    request.getHeightInches()
            );
        }
        return MeasurementConverter.toMetres(request.getHeight(), request.getHeightUnit());
    }

    // -----------------------------------------------------------------------
    // 2. BMI calculation
    // -----------------------------------------------------------------------

    /**
     * Calculates BMI using the standard WHO formula.
     *
     * <p><strong>Formula:</strong> BMI = weight(kg) / height(m)²</p>
     *
     * @param weightKg body weight in kilograms
     * @param heightM  height in metres
     * @return raw BMI value with full precision
     * @throws InvalidMeasurementException if height squared would be zero (division by zero)
     */
    public double calculateBMI(double weightKg, double heightM) {
        double heightSquared = heightM * heightM;
        if (heightSquared == 0) {
            throw new InvalidMeasurementException(
                    "Height must be greater than zero to calculate BMI.");
        }
        return weightKg / heightSquared;
    }

    // -----------------------------------------------------------------------
    // 3. Healthy weight range
    // -----------------------------------------------------------------------

    /**
     * Calculates the estimated healthy weight range.
     *
     * <p><strong>Formula:</strong>
     * <ul>
     *   <li>Minimum = 18.5 × height(m)²</li>
     *   <li>Maximum = 24.9 × height(m)²</li>
     * </ul>
     * Both values are rounded to 2 decimal places and expressed in kilograms.</p>
     *
     * @param heightM height in metres
     * @return {@link HealthyWeightRange} with min and max in kg
     */
    public HealthyWeightRange calculateHealthyWeightRange(double heightM) {
        double heightSquared = heightM * heightM;
        double minKg = round(HEALTHY_BMI_MIN * heightSquared, 2);
        double maxKg = round(HEALTHY_BMI_MAX * heightSquared, 2);
        return new HealthyWeightRange(minKg, maxKg);
    }

    // -----------------------------------------------------------------------
    // 4. Calorie estimation (BMR + TDEE)
    // -----------------------------------------------------------------------

    /**
     * Calculates BMR and optionally TDEE, depending on available user data.
     *
     * <p><strong>Mifflin-St Jeor BMR formula:</strong>
     * <ul>
     *   <li>Male:   BMR = (10 × kg) + (6.25 × cm) − (5 × age) + 5</li>
     *   <li>Female: BMR = (10 × kg) + (6.25 × cm) − (5 × age) − 161</li>
     * </ul>
     * </p>
     *
     * <p><strong>TDEE:</strong> BMR × activity multiplier</p>
     *
     * @param sex           biological sex (may be null)
     * @param activityLevel activity level (may be null)
     * @param weightKg      weight in kilograms
     * @param heightCm      height in centimetres
     * @param age           age in years
     * @return {@link CalorieResult} record with bmr, tdee, and status
     */
    CalorieResult calculateCalories(
            BiologicalSex sex,
            ActivityLevel activityLevel,
            double weightKg,
            double heightCm,
            int age) {

        if (sex == null) {
            return new CalorieResult(null, null, "PENDING_SEX");
        }

        double bmr = calculateBMR(sex, weightKg, heightCm, age);
        double roundedBmr = round(bmr, 2);

        if (activityLevel == null) {
            return new CalorieResult(roundedBmr, null, "BMR_ONLY");
        }

        double tdee = round(bmr * activityLevel.getMultiplier(), 2);
        return new CalorieResult(roundedBmr, tdee, "CALCULATED");
    }

    /**
     * Applies the Mifflin-St Jeor formula for a given biological sex.
     *
     * @param sex      biological sex (MALE or FEMALE)
     * @param weightKg weight in kilograms
     * @param heightCm height in centimetres
     * @param age      age in years
     * @return raw BMR in kcal/day
     */
    public double calculateBMR(BiologicalSex sex, double weightKg, double heightCm, int age) {
        // Common part of the formula
        double base = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age);
        return switch (sex) {
            case MALE   -> base + 5.0;
            case FEMALE -> base - 161.0;
        };
    }

    // -----------------------------------------------------------------------
    // 5. Water requirement
    // -----------------------------------------------------------------------

    /**
     * Calculates the estimated daily water requirement.
     *
     * <p><strong>Formula:</strong>
     * <ul>
     *   <li>Minimum: weight(kg) × 30 ml ÷ 1000 (litres)</li>
     *   <li>Maximum: weight(kg) × 35 ml ÷ 1000 (litres)</li>
     * </ul>
     * </p>
     *
     * @param weightKg weight in kilograms
     * @return {@link WaterRequirement} with min and max in litres
     */
    public WaterRequirement calculateWaterRequirement(double weightKg) {
        double minLitres = round((weightKg * WATER_ML_PER_KG_MIN) / ML_PER_LITRE, 2);
        double maxLitres = round((weightKg * WATER_ML_PER_KG_MAX) / ML_PER_LITRE, 2);
        return new WaterRequirement(minLitres, maxLitres);
    }

    // -----------------------------------------------------------------------
    // 6. Recommendations
    // -----------------------------------------------------------------------

    /**
     * Generates initial general health recommendations based on the BMI category.
     *
     * <p><strong>Scope:</strong> These are general, evidence-informed starting points.
     * Detailed nutrition plans, dietary restriction guidance, food allergy management,
     * sleep analysis, and lifestyle profiling are handled by Person 2's module.</p>
     *
     * @param classification the BMI classification result
     * @return list of recommendation strings
     */
    List<String> generateRecommendations(BMIClassification classification) {
        List<String> recommendations = new ArrayList<>();

        switch (classification.getCategory()) {
            case UNDERWEIGHT -> {
                recommendations.add("Consider consulting a healthcare professional or registered dietitian.");
                recommendations.add("Focus on gradual, sustainable increases in caloric intake.");
                recommendations.add("Include nutrient-dense foods such as whole grains, lean proteins, and healthy fats.");
                recommendations.add("Engage in strength training to support healthy weight gain.");
                recommendations.add("Ensure adequate hydration throughout the day.");
            }
            case NORMAL_WEIGHT -> {
                recommendations.add("Maintain a balanced and varied diet.");
                recommendations.add("Stay physically active with regular aerobic and resistance exercise.");
                recommendations.add("Maintain adequate hydration – aim for the estimated daily water intake.");
                recommendations.add("Schedule regular health check-ups to maintain your current status.");
                recommendations.add("Prioritise consistent sleep and stress management habits.");
            }
            case OVERWEIGHT -> {
                recommendations.add("Consider a moderate, sustainable reduction in daily caloric intake.");
                recommendations.add("Increase physical activity with a combination of cardio and strength training.");
                recommendations.add("Prioritise whole, unprocessed foods over high-calorie, low-nutrient options.");
                recommendations.add("Stay well-hydrated – drinking water before meals may help manage portion sizes.");
                recommendations.add("Consult a healthcare professional or dietitian for personalised guidance.");
            }
            case OBESE -> {
                recommendations.add("Consult a qualified healthcare professional as a priority.");
                recommendations.add("Work with a registered dietitian to develop a safe, personalised eating plan.");
                recommendations.add("Engage in low-impact physical activity such as walking or swimming to begin safely.");
                recommendations.add("Monitor and reduce consumption of high-sugar and high-fat processed foods.");
                recommendations.add("Address contributing lifestyle factors with appropriate professional support.");
            }
        }

        return recommendations;
    }

    // -----------------------------------------------------------------------
    // Internal helper – rounding
    // -----------------------------------------------------------------------

    /**
     * Rounds a double to the specified number of decimal places using HALF_UP rounding.
     *
     * @param value  the value to round
     * @param places the number of decimal places
     * @return rounded value
     */
    public double round(double value, int places) {
        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // -----------------------------------------------------------------------
    // Internal record for calorie results
    // -----------------------------------------------------------------------

    /**
     * Immutable record holding calorie calculation results.
     *
     * @param bmr    Basal Metabolic Rate (null if sex not provided)
     * @param tdee   Total Daily Energy Expenditure (null if activity level not provided)
     * @param status one of CALCULATED, BMR_ONLY, PENDING_SEX
     */
    record CalorieResult(Double bmr, Double tdee, String status) {}
}
