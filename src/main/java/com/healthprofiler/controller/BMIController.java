package com.healthprofiler.controller;

import com.healthprofiler.dto.BMIRequest;
import com.healthprofiler.dto.BMIResponse;
import com.healthprofiler.service.BMIService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the BMI Calculator API endpoints.
 *
 * <p>Base path: {@code /api/bmi}</p>
 *
 * <p>This controller delegates all business logic to {@link BMIService}
 * and handles only HTTP concerns (request binding, validation trigger,
 * and response wrapping).</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li>{@code POST /api/bmi/calculate} – Calculate BMI and full body analysis</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/bmi")
public class BMIController {

    private final BMIService bmiService;

    public BMIController(BMIService bmiService) {
        this.bmiService = bmiService;
    }

    /**
     * Calculates BMI and returns a comprehensive body analysis result.
     *
     * <p><strong>HTTP Method:</strong> POST</p>
     * <p><strong>Path:</strong> /api/bmi/calculate</p>
     * <p><strong>Content-Type:</strong> application/json</p>
     *
     * <p>Returns HTTP 200 OK on success, or HTTP 400 Bad Request if
     * the input fails validation or contains unrealistic measurements.</p>
     *
     * <h4>Minimal request example:</h4>
     * <pre>
     * {
     *   "age": 21,
     *   "weight": 65,
     *   "weightUnit": "KG",
     *   "height": 170,
     *   "heightUnit": "CM"
     * }
     * </pre>
     *
     * <h4>Full request example:</h4>
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
     * @param request validated request body containing user measurements
     * @return {@link ResponseEntity} wrapping the {@link BMIResponse}
     */
    @PostMapping("/calculate")
    public ResponseEntity<BMIResponse> calculate(@Valid @RequestBody BMIRequest request) {
        BMIResponse response = bmiService.calculate(request);
        return ResponseEntity.ok(response);
    }
}
