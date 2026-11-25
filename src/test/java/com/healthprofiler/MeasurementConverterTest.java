package com.healthprofiler;

import com.healthprofiler.enums.HeightUnit;
import com.healthprofiler.enums.WeightUnit;
import com.healthprofiler.exception.InvalidMeasurementException;
import com.healthprofiler.util.MeasurementConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MeasurementConverter}.
 *
 * <p>Covers weight conversion, height conversion (CM and FEET),
 * feet-and-inches combined conversion, and validation error cases.</p>
 */
@DisplayName("MeasurementConverter – Unit Tests")
class MeasurementConverterTest {

    private static final double DELTA = 0.0001;

    // -----------------------------------------------------------------------
    // Weight conversion
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("toKilograms – KG input returns unchanged value")
    void toKilograms_kg_returnsUnchanged() {
        double result = MeasurementConverter.toKilograms(70.0, WeightUnit.KG);
        assertEquals(70.0, result, DELTA);
    }

    @Test
    @DisplayName("toKilograms – 1 LBS converts to 0.45359237 KG")
    void toKilograms_onePound_correctConversion() {
        double result = MeasurementConverter.toKilograms(1.0, WeightUnit.LBS);
        assertEquals(0.45359237, result, DELTA);
    }

    @Test
    @DisplayName("toKilograms – 150 LBS converts correctly")
    void toKilograms_150Pounds_correctConversion() {
        double result = MeasurementConverter.toKilograms(150.0, WeightUnit.LBS);
        assertEquals(68.0389, result, DELTA);
    }

    @Test
    @DisplayName("toKilograms – zero weight throws InvalidMeasurementException")
    void toKilograms_zeroWeight_throwsException() {
        assertThrows(InvalidMeasurementException.class,
                () -> MeasurementConverter.toKilograms(0.0, WeightUnit.KG));
    }

    @Test
    @DisplayName("toKilograms – negative weight throws InvalidMeasurementException")
    void toKilograms_negativeWeight_throwsException() {
        assertThrows(InvalidMeasurementException.class,
                () -> MeasurementConverter.toKilograms(-10.0, WeightUnit.LBS));
    }

    // -----------------------------------------------------------------------
    // Height conversion – CM
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("toMetres – 170 CM converts to 1.70 m")
    void toMetres_cm_correctConversion() {
        double result = MeasurementConverter.toMetres(170.0, HeightUnit.CM);
        assertEquals(1.70, result, DELTA);
    }

    @Test
    @DisplayName("toMetres – 100 CM converts to 1.00 m")
    void toMetres_100cm_correctConversion() {
        double result = MeasurementConverter.toMetres(100.0, HeightUnit.CM);
        assertEquals(1.00, result, DELTA);
    }

    // -----------------------------------------------------------------------
    // Height conversion – FEET (decimal)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("toMetres – 5.0 FEET converts to 1.524 m")
    void toMetres_fiveFootDecimal_correctConversion() {
        double result = MeasurementConverter.toMetres(5.0, HeightUnit.FEET);
        assertEquals(1.524, result, DELTA);
    }

    @Test
    @DisplayName("toMetres – 6.0 FEET converts to 1.8288 m")
    void toMetres_sixFeet_correctConversion() {
        double result = MeasurementConverter.toMetres(6.0, HeightUnit.FEET);
        assertEquals(1.8288, result, DELTA);
    }

    // -----------------------------------------------------------------------
    // Height conversion – FEET + INCHES
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("feetAndInchesToMetres – 5 ft 9 in converts correctly")
    void feetAndInchesToMetres_5ft9in_correctConversion() {
        // 5 ft = 1.524 m, 9 in = 0.2286 m → 1.7526 m
        double result = MeasurementConverter.feetAndInchesToMetres(5.0, 9.0);
        assertEquals(1.7526, result, DELTA);
    }

    @Test
    @DisplayName("feetAndInchesToMetres – 6 ft 0 in converts correctly")
    void feetAndInchesToMetres_6ft0in_correctConversion() {
        double result = MeasurementConverter.feetAndInchesToMetres(6.0, 0.0);
        assertEquals(1.8288, result, DELTA);
    }

    @Test
    @DisplayName("feetAndInchesToMetres – inches >= 12 throws InvalidMeasurementException")
    void feetAndInchesToMetres_invalidInches_throwsException() {
        assertThrows(InvalidMeasurementException.class,
                () -> MeasurementConverter.feetAndInchesToMetres(5.0, 12.0));
    }

    @Test
    @DisplayName("feetAndInchesToMetres – negative inches throws InvalidMeasurementException")
    void feetAndInchesToMetres_negativeInches_throwsException() {
        assertThrows(InvalidMeasurementException.class,
                () -> MeasurementConverter.feetAndInchesToMetres(5.0, -1.0));
    }

    // -----------------------------------------------------------------------
    // Height – validation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("toMetres – zero height throws InvalidMeasurementException")
    void toMetres_zeroHeight_throwsException() {
        assertThrows(InvalidMeasurementException.class,
                () -> MeasurementConverter.toMetres(0.0, HeightUnit.CM));
    }

    // -----------------------------------------------------------------------
    // Metres to centimetres
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("metresToCentimetres – 1.70 m converts to 170.0 cm")
    void metresToCentimetres_correctConversion() {
        double result = MeasurementConverter.metresToCentimetres(1.70);
        assertEquals(170.0, result, DELTA);
    }
}
