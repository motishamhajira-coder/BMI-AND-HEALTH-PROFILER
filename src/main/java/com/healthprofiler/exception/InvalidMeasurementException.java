package com.healthprofiler.exception;

/**
 * Thrown when a user-supplied measurement value is invalid,
 * out of a realistic range, or would cause a calculation error.
 */
public class InvalidMeasurementException extends RuntimeException {

    public InvalidMeasurementException(String message) {
        super(message);
    }

    public InvalidMeasurementException(String message, Throwable cause) {
        super(message, cause);
    }
}
