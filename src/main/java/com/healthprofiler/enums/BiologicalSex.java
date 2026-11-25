package com.healthprofiler.enums;

/**
 * Biological sex options used for BMR calculation (Mifflin-St Jeor formula).
 *
 * <p>This field is optional. If not provided, BMR and TDEE will not be calculated.</p>
 */
public enum BiologicalSex {
    /** Uses the male variant of the Mifflin-St Jeor BMR formula: BMR + 5. */
    MALE,
    /** Uses the female variant of the Mifflin-St Jeor BMR formula: BMR − 161. */
    FEMALE
}
