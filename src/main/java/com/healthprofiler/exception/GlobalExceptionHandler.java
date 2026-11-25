package com.healthprofiler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler providing consistent, structured JSON error responses
 * for all exceptions thrown within the BMI Health Profiler API.
 *
 * <p>All error responses share the following structure:</p>
 * <pre>
 * {
 *   "timestamp": "2024-01-01T12:00:00",
 *   "status":    400,
 *   "error":     "Bad Request",
 *   "message":   "...",
 *   "details":   [ "..." ]   // only present for validation errors
 * }
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -----------------------------------------------------------------------
    // Bean Validation errors (@Valid failures)
    // -----------------------------------------------------------------------

    /**
     * Handles {@link MethodArgumentNotValidException} thrown when a request body
     * fails Jakarta Bean Validation constraints.
     *
     * <p>Returns HTTP 400 with a list of field-level error messages.</p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> body = buildErrorBody(
                HttpStatus.BAD_REQUEST,
                "Input validation failed. Please check the provided values.",
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // -----------------------------------------------------------------------
    // Invalid measurement (domain-level validation)
    // -----------------------------------------------------------------------

    /**
     * Handles {@link InvalidMeasurementException} thrown by the service layer
     * when a measurement is unrealistic or would cause a calculation error.
     *
     * <p>Returns HTTP 422 Unprocessable Entity.</p>
     */
    @ExceptionHandler(InvalidMeasurementException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMeasurement(
            InvalidMeasurementException ex) {

        Map<String, Object> body = buildErrorBody(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    // -----------------------------------------------------------------------
    // Malformed JSON / unknown enum values
    // -----------------------------------------------------------------------

    /**
     * Handles {@link HttpMessageNotReadableException} thrown when the request body
     * cannot be parsed (e.g., malformed JSON or unrecognised enum value).
     *
     * <p>Returns HTTP 400 with guidance on accepted values.</p>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(
            HttpMessageNotReadableException ex) {

        String message = "Request body is malformed or contains an unrecognised enum value. " +
                "Check the accepted values documented for this endpoint.";

        Map<String, Object> body = buildErrorBody(HttpStatus.BAD_REQUEST, message, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // -----------------------------------------------------------------------
    // Catch-all – unexpected server errors
    // -----------------------------------------------------------------------

    /**
     * Catch-all handler for any unexpected runtime exceptions.
     *
     * <p>Returns HTTP 500 Internal Server Error.
     * The original exception message is intentionally suppressed
     * in the response to avoid leaking implementation details.</p>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> body = buildErrorBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again or contact support.",
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private Map<String, Object> buildErrorBody(
            HttpStatus status,
            String message,
            List<String> details) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        if (details != null && !details.isEmpty()) {
            body.put("details", details);
        }
        return body;
    }
}
