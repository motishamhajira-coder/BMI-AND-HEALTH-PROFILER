package com.healthprofiler.util;

import com.healthprofiler.enums.HeightUnit;
import com.healthprofiler.enums.WeightUnit;
import com.healthprofiler.exception.InvalidMeasurementException;

/**
 * Utility class providing static methods to convert weight and height
 * between supported units into the standard SI units used for calculations.
 *
 * <p>All calculations in the application operate on:
 * <ul>
 *   <li>Weight in <strong>kilograms (kg)</strong></li>
 *   <li>Height in <strong>metres (m)</strong></li>
 *   <li>Height in <strong>centimetres (cm)</strong> for the BMR formula</li>
 * </ul>
 * </p>
 *
 * <p>Conversion constants used:
 * <ul>
 *   <li>1 lb  = 0.45359237 kg (exact NIST definition)</li>
 *   <li>1 ft  = 0.3048 m    (exact international foot)</li>
 *   <li>1 in  = 0.0254 m    (exact; 1 ft / 12)</li>
 * </ul>
 * </p>
 */
public final class MeasurementConverter {

    // -----------------------------------------------------------------------
    // Conversion constants
    // -----------------------------------------------------------------------

    /** Exact pounds-to-kilograms conversion factor (NIST definition). */
    public static final double LBS_TO_KG = 0.45359237;

    /** Exact feet-to-metres conversion factor (international foot). */
    public static final double FEET_TO_METRES = 0.3048;

    /** Exact inches-to-metres conversion factor. */
    public static final double INCHES_TO_METRES = 0.0254;

    /** Metres-to-centimetres multiplier. */
    public static final double METRES_TO_CM = 100.0;

    // -----------------------------------------------------------------------
    // Private constructor – utility class, not intended to be instantiated.
    // -----------------------------------------------------------------------
    private MeasurementConverter() {
        throw new UnsupportedOperationException("MeasurementConverter is a utility class.");
    }

    // -----------------------------------------------------------------------
    // Weight conversion
    // -----------------------------------------------------------------------

    /**
     * Converts the given weight to kilograms.
     *
     * @param weight     the weight value as entered by the user
     * @param weightUnit the unit of the provided weight
     * @return weight expressed in kilograms
     * @throws InvalidMeasurementException if the weight is not positive
     */
    public static double toKilograms(double weight, WeightUnit weightUnit) {
        validatePositive(weight, "Weight");
        return switch (weightUnit) {
            case KG  -> weight;
            case LBS -> weight * LBS_TO_KG;
        };
    }

    // -----------------------------------------------------------------------
    // Height conversion
    // -----------------------------------------------------------------------

    /**
     * Converts the given height (expressed as a single decimal value) to metres.
     *
     * <p>When {@code heightUnit} is {@link HeightUnit#FEET}, the value is treated
     * as decimal feet (e.g. {@code 5.75} means 5 ft 9 in).
     * To provide feet and inches separately, use
     * {@link #feetAndInchesToMetres(double, double)} instead.</p>
     *
     * @param height     the height value as entered by the user
     * @param heightUnit the unit of the provided height
     * @return height expressed in metres
     * @throws InvalidMeasurementException if the height is not positive
     */
    public static double toMetres(double height, HeightUnit heightUnit) {
        validatePositive(height, "Height");
        return switch (heightUnit) {
            case CM   -> height / METRES_TO_CM;
            case FEET -> height * FEET_TO_METRES;
        };
    }

    /**
     * Converts a height given as separate feet and inches values to metres.
     *
     * <p>Example: {@code feetAndInchesToMetres(5, 9)} → 1.7526 m</p>
     *
     * @param feet   the whole-feet component (must be ≥ 0)
     * @param inches the inches component (must be ≥ 0 and &lt; 12)
     * @return height expressed in metres
     * @throws InvalidMeasurementException if the resulting total is not positive
     *                                     or if inches ≥ 12
     */
    public static double feetAndInchesToMetres(double feet, double inches) {
        if (inches < 0 || inches >= 12) {
            throw new InvalidMeasurementException(
                    "Inches must be in the range 0–11 when providing feet and inches separately.");
        }
        double totalMetres = (feet * FEET_TO_METRES) + (inches * INCHES_TO_METRES);
        if (totalMetres <= 0) {
            throw new InvalidMeasurementException("Height must be greater than zero.");
        }
        return totalMetres;
    }

    /**
     * Converts metres to centimetres.
     *
     * <p>Required by the Mifflin-St Jeor BMR formula, which expects height in cm.</p>
     *
     * @param metres height in metres
     * @return height in centimetres
     */
    public static double metresToCentimetres(double metres) {
        return metres * METRES_TO_CM;
    }

    // -----------------------------------------------------------------------
    // Internal helper
    // -----------------------------------------------------------------------

    /**
     * Validates that a measurement value is strictly positive.
     *
     * @param value     the measurement value to check
     * @param fieldName the human-readable field name for error messaging
     * @throws InvalidMeasurementException if the value is ≤ 0
     */
    private static void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new InvalidMeasurementException(fieldName + " must be greater than zero.");
        }
    }
}
