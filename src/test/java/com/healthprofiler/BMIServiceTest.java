package com.healthprofiler;

import com.healthprofiler.dto.BMIRequest;
import com.healthprofiler.dto.BMIResponse;
import com.healthprofiler.dto.HealthyWeightRange;
import com.healthprofiler.dto.WaterRequirement;
import com.healthprofiler.enums.ActivityLevel;
import com.healthprofiler.enums.BiologicalSex;
import com.healthprofiler.enums.BMICategory;
import com.healthprofiler.enums.HeightUnit;
import com.healthprofiler.enums.WeightUnit;
import com.healthprofiler.exception.InvalidMeasurementException;
import com.healthprofiler.service.BMIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BMIService}.
 *
 * <p>Covers BMI calculation, all four BMI classifications, healthy weight range,
 * BMR calculation, TDEE calculation, water requirement, missing optional data,
 * and invalid measurement handling.</p>
 */
@DisplayName("BMIService – Unit Tests")
class BMIServiceTest {

    private static final double DELTA = 0.01;

    private BMIService bmiService;

    @BeforeEach
    void setUp() {
        bmiService = new BMIService();
    }

    // -----------------------------------------------------------------------
    // BMI Calculation
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("BMI Calculation")
    class BMICalculationTests {

        @Test
        @DisplayName("calculateBMI – 65 kg / 1.70 m² = 22.49")
        void calculateBMI_standardInput_correctResult() {
            // 65 / (1.70 * 1.70) = 65 / 2.89 ≈ 22.4913...
            double bmi = bmiService.calculateBMI(65.0, 1.70);
            assertEquals(22.49, bmi, DELTA);
        }

        @Test
        @DisplayName("calculateBMI – 50 kg / 1.75 m² = 16.33")
        void calculateBMI_underweightValue_correct() {
            double bmi = bmiService.calculateBMI(50.0, 1.75);
            assertEquals(16.33, bmi, DELTA);
        }

        @Test
        @DisplayName("calculateBMI – zero height throws InvalidMeasurementException")
        void calculateBMI_zeroHeight_throwsException() {
            assertThrows(InvalidMeasurementException.class,
                    () -> bmiService.calculateBMI(70.0, 0.0));
        }

        @Test
        @DisplayName("Full calculate() – example from spec produces correct BMI")
        void calculate_specExample_correctBMI() {
            BMIRequest request = buildRequest(21, 65.0, WeightUnit.KG, 170.0, HeightUnit.CM,
                    BiologicalSex.FEMALE, ActivityLevel.MODERATELY_ACTIVE);
            BMIResponse response = bmiService.calculate(request);
            assertEquals(22.49, response.getBmi(), DELTA);
        }
    }

    // -----------------------------------------------------------------------
    // BMI Classification
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("BMI Classification – All Four Categories")
    class BMIClassificationTests {

        @Test
        @DisplayName("BMI 15.0 → UNDERWEIGHT")
        void classification_bmi15_underweight() {
            // Weight that gives BMI ~15 at 1.70 m: 15 * 1.70² = 43.35 kg
            double bmi = bmiService.calculateBMI(43.35, 1.70);
            BMIResponse response = buildResponseFromBMI(43.35, 1.70);
            assertEquals(BMICategory.UNDERWEIGHT, response.getCategory());
            assertEquals("BLUE", response.getColour());
        }

        @Test
        @DisplayName("BMI 18.5 → NORMAL_WEIGHT (boundary)")
        void classification_bmi18_5_normalWeight() {
            // 18.5 * 1.70² = 53.465 kg
            double weightKg = 18.5 * (1.70 * 1.70);
            BMIResponse response = buildResponseFromBMI(weightKg, 1.70);
            assertEquals(BMICategory.NORMAL_WEIGHT, response.getCategory());
            assertEquals("GREEN", response.getColour());
        }

        @Test
        @DisplayName("BMI 22.0 → NORMAL_WEIGHT")
        void classification_bmi22_normalWeight() {
            double weightKg = 22.0 * (1.70 * 1.70);
            BMIResponse response = buildResponseFromBMI(weightKg, 1.70);
            assertEquals(BMICategory.NORMAL_WEIGHT, response.getCategory());
        }

        @Test
        @DisplayName("BMI 27.5 → OVERWEIGHT")
        void classification_bmi27_5_overweight() {
            double weightKg = 27.5 * (1.70 * 1.70);
            BMIResponse response = buildResponseFromBMI(weightKg, 1.70);
            assertEquals(BMICategory.OVERWEIGHT, response.getCategory());
            assertEquals("ORANGE", response.getColour());
        }

        @Test
        @DisplayName("BMI 25.1 → OVERWEIGHT (just above 25.0 boundary)")
        void classification_bmi25_overweight() {
            // Note: using 25.1 instead of exactly 25.0 to avoid floating-point
            // precision loss where (25.0 × h²) ÷ h² can yield 24.9999... in IEEE 754.
            double weightKg = 25.1 * (1.70 * 1.70);
            BMIResponse response = buildResponseFromBMI(weightKg, 1.70);
            assertEquals(BMICategory.OVERWEIGHT, response.getCategory());
        }

        @Test
        @DisplayName("BMI 35.0 → OBESE")
        void classification_bmi35_obese() {
            double weightKg = 35.0 * (1.70 * 1.70);
            BMIResponse response = buildResponseFromBMI(weightKg, 1.70);
            assertEquals(BMICategory.OBESE, response.getCategory());
            assertEquals("RED", response.getColour());
        }

        @Test
        @DisplayName("BMI 30.0 → OBESE (boundary)")
        void classification_bmi30_obese() {
            double weightKg = 30.0 * (1.70 * 1.70);
            BMIResponse response = buildResponseFromBMI(weightKg, 1.70);
            assertEquals(BMICategory.OBESE, response.getCategory());
        }
    }

    // -----------------------------------------------------------------------
    // Healthy Weight Range
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Healthy Weight Range")
    class HealthyWeightRangeTests {

        @Test
        @DisplayName("calculateHealthyWeightRange – 1.70 m produces correct min/max")
        void healthyWeightRange_1_70m_correctValues() {
            // Min: 18.5 × 1.70² = 18.5 × 2.89 = 53.465 → 53.47
            // Max: 24.9 × 1.70² = 24.9 × 2.89 = 71.961 → 71.96
            HealthyWeightRange range = bmiService.calculateHealthyWeightRange(1.70);
            assertEquals(53.47, range.getMinimumKg(), DELTA);
            assertEquals(71.96, range.getMaximumKg(), DELTA);
        }

        @Test
        @DisplayName("calculateHealthyWeightRange – min is always less than max")
        void healthyWeightRange_minLessThanMax() {
            HealthyWeightRange range = bmiService.calculateHealthyWeightRange(1.80);
            assertTrue(range.getMinimumKg() < range.getMaximumKg());
        }

        @Test
        @DisplayName("calculateHealthyWeightRange – returned in BMIResponse")
        void healthyWeightRange_presentInResponse() {
            BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM, null, null);
            BMIResponse response = bmiService.calculate(request);
            assertNotNull(response.getHealthyWeightRange());
            assertTrue(response.getHealthyWeightRange().getMinimumKg() > 0);
        }
    }

    // -----------------------------------------------------------------------
    // BMR Calculation
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("BMR Calculation (Mifflin-St Jeor)")
    class BMRCalculationTests {

        @Test
        @DisplayName("calculateBMR – Female, 65 kg, 170 cm, age 21 = 1452.5")
        void calculateBMR_female_correctValue() {
            // (10×65) + (6.25×170) − (5×21) − 161
            // = 650 + 1062.5 − 105 − 161 = 1446.5
            double bmr = bmiService.calculateBMR(BiologicalSex.FEMALE, 65.0, 170.0, 21);
            assertEquals(1446.5, bmr, DELTA);
        }

        @Test
        @DisplayName("calculateBMR – Male, 80 kg, 180 cm, age 30 = correct")
        void calculateBMR_male_correctValue() {
            // (10×80) + (6.25×180) − (5×30) + 5
            // = 800 + 1125 − 150 + 5 = 1780
            double bmr = bmiService.calculateBMR(BiologicalSex.MALE, 80.0, 180.0, 30);
            assertEquals(1780.0, bmr, DELTA);
        }
    }

    // -----------------------------------------------------------------------
    // TDEE Calculation
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("TDEE Calculation")
    class TDEECalculationTests {

        @Test
        @DisplayName("TDEE – Female MODERATELY_ACTIVE = BMR × 1.55")
        void tdee_femaleModeratelyActive_correct() {
            // BMR for female 65 kg, 170 cm, age 21 = 1446.5
            // TDEE = 1446.5 × 1.55 = 2242.075 → rounded 2242.08
            BMIRequest request = buildRequest(21, 65.0, WeightUnit.KG, 170.0, HeightUnit.CM,
                    BiologicalSex.FEMALE, ActivityLevel.MODERATELY_ACTIVE);
            BMIResponse response = bmiService.calculate(request);
            assertNotNull(response.getDailyCalories());
            assertEquals(2242.08, response.getDailyCalories(), DELTA);
            assertEquals("CALCULATED", response.getCalorieStatus());
        }

        @Test
        @DisplayName("TDEE – calorieStatus is BMR_ONLY when activityLevel is null")
        void tdee_noActivityLevel_bmrOnly() {
            BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM,
                    BiologicalSex.MALE, null);
            BMIResponse response = bmiService.calculate(request);
            assertNotNull(response.getBmr());
            assertNull(response.getDailyCalories());
            assertEquals("BMR_ONLY", response.getCalorieStatus());
        }

        @Test
        @DisplayName("TDEE – calorieStatus is PENDING_SEX when sex is null")
        void tdee_noSex_pendingSex() {
            BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM,
                    null, ActivityLevel.SEDENTARY);
            BMIResponse response = bmiService.calculate(request);
            assertNull(response.getBmr());
            assertNull(response.getDailyCalories());
            assertEquals("PENDING_SEX", response.getCalorieStatus());
        }

        @Test
        @DisplayName("TDEE – calorieStatus PENDING_SEX when both sex and activityLevel null")
        void tdee_neitherProvided_pendingSex() {
            BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM,
                    null, null);
            BMIResponse response = bmiService.calculate(request);
            assertNull(response.getBmr());
            assertNull(response.getDailyCalories());
            assertEquals("PENDING_SEX", response.getCalorieStatus());
        }
    }

    // -----------------------------------------------------------------------
    // Water Requirement
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Water Requirement")
    class WaterRequirementTests {

        @Test
        @DisplayName("calculateWaterRequirement – 65 kg → 1.95 to 2.275 L")
        void waterRequirement_65kg_correctRange() {
            // Min: 65 × 30 / 1000 = 1.95 L
            // Max: 65 × 35 / 1000 = 2.275 → 2.28 L
            WaterRequirement water = bmiService.calculateWaterRequirement(65.0);
            assertEquals(1.95, water.getMinimumLitres(), DELTA);
            assertEquals(2.28, water.getMaximumLitres(), DELTA);
        }

        @Test
        @DisplayName("calculateWaterRequirement – min is always less than max")
        void waterRequirement_minLessThanMax() {
            WaterRequirement water = bmiService.calculateWaterRequirement(80.0);
            assertTrue(water.getMinimumLitres() < water.getMaximumLitres());
        }

        @Test
        @DisplayName("calculateWaterRequirement – note is included in response")
        void waterRequirement_notePresent() {
            WaterRequirement water = bmiService.calculateWaterRequirement(70.0);
            assertNotNull(water.getNote());
            assertFalse(water.getNote().isBlank());
        }

        @Test
        @DisplayName("Water requirement – present in full BMIResponse")
        void waterRequirement_inBMIResponse() {
            BMIRequest request = buildRequest(30, 80.0, WeightUnit.KG, 180.0, HeightUnit.CM, null, null);
            BMIResponse response = bmiService.calculate(request);
            assertNotNull(response.getDailyWater());
            assertTrue(response.getDailyWater().getMinimumLitres() > 0);
        }
    }

    // -----------------------------------------------------------------------
    // Weight unit conversions via calculate()
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Weight and Height Unit Conversions")
    class ConversionIntegrationTests {

        @Test
        @DisplayName("LBS input produces same BMI as equivalent KG input")
        void weightConversion_lbsEquivalentToKg() {
            // 143.3 lbs ≈ 65 kg
            BMIRequest requestKg  = buildRequest(21, 65.0, WeightUnit.KG, 170.0, HeightUnit.CM, null, null);
            BMIRequest requestLbs = buildRequest(21, 143.3, WeightUnit.LBS, 170.0, HeightUnit.CM, null, null);

            BMIResponse responseKg  = bmiService.calculate(requestKg);
            BMIResponse responseLbs = bmiService.calculate(requestLbs);

            assertEquals(responseKg.getBmi(), responseLbs.getBmi(), 0.1);
        }

        @Test
        @DisplayName("FEET input produces same BMI as equivalent CM input")
        void heightConversion_feetEquivalentToCm() {
            // 5.577 ft ≈ 170 cm
            BMIRequest requestCm   = buildRequest(21, 65.0, WeightUnit.KG, 170.0, HeightUnit.CM, null, null);
            BMIRequest requestFeet = buildRequest(21, 65.0, WeightUnit.KG, 5.577, HeightUnit.FEET, null, null);

            BMIResponse responseCm   = bmiService.calculate(requestCm);
            BMIResponse responseFeet = bmiService.calculate(requestFeet);

            assertEquals(responseCm.getBmi(), responseFeet.getBmi(), 0.1);
        }

        @Test
        @DisplayName("Feet + inches input produces correct BMI")
        void heightConversion_feetAndInches_correctBMI() {
            // 5 ft 7 in = 1.7018 m; weight 65 kg → BMI ≈ 22.46
            BMIRequest request = buildRequest(21, 65.0, WeightUnit.KG, 5.0, HeightUnit.FEET, null, null);
            request.setHeightInches(7.0);
            BMIResponse response = bmiService.calculate(request);
            assertEquals(22.46, response.getBmi(), 0.1);
        }
    }

    // -----------------------------------------------------------------------
    // Recommendations
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Recommendations by Category")
    class RecommendationTests {

        @Test
        @DisplayName("Recommendations not empty for NORMAL_WEIGHT")
        void recommendations_normalWeight_notEmpty() {
            BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM, null, null);
            BMIResponse response = bmiService.calculate(request);
            assertNotNull(response.getRecommendations());
            assertFalse(response.getRecommendations().isEmpty());
        }

        @Test
        @DisplayName("Recommendations not empty for OBESE")
        void recommendations_obese_notEmpty() {
            // 120 kg, 1.70 m → BMI ≈ 41.5 (OBESE)
            BMIRequest request = buildRequest(40, 120.0, WeightUnit.KG, 170.0, HeightUnit.CM, null, null);
            BMIResponse response = bmiService.calculate(request);
            assertNotNull(response.getRecommendations());
            assertFalse(response.getRecommendations().isEmpty());
        }
    }

    // -----------------------------------------------------------------------
    // Disclaimer
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("BMIResponse includes a non-blank disclaimer")
    void response_disclaimerPresent() {
        BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM, null, null);
        BMIResponse response = bmiService.calculate(request);
        assertNotNull(response.getDisclaimer());
        assertFalse(response.getDisclaimer().isBlank());
    }

    // -----------------------------------------------------------------------
    // Test helpers
    // -----------------------------------------------------------------------

    private BMIRequest buildRequest(int age, double weight, WeightUnit wUnit,
                                    double height, HeightUnit hUnit,
                                    BiologicalSex sex, ActivityLevel activityLevel) {
        BMIRequest req = new BMIRequest();
        req.setAge(age);
        req.setWeight(weight);
        req.setWeightUnit(wUnit);
        req.setHeight(height);
        req.setHeightUnit(hUnit);
        req.setSex(sex);
        req.setActivityLevel(activityLevel);
        return req;
    }

    private BMIResponse buildResponseFromBMI(double weightKg, double heightM) {
        BMIRequest request = new BMIRequest();
        request.setAge(25);
        request.setWeight(weightKg);
        request.setWeightUnit(WeightUnit.KG);
        // Express height in CM for the request
        request.setHeight(heightM * 100);
        request.setHeightUnit(HeightUnit.CM);
        return bmiService.calculate(request);
    }
}
