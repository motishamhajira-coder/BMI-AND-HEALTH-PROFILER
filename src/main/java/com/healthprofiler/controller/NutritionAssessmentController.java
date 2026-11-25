package com.healthprofiler.controller;
import com.healthprofiler.dto.*; import com.healthprofiler.service.NutritionAssessmentService;
import jakarta.validation.Valid; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/nutrition") public class NutritionAssessmentController {
 private final NutritionAssessmentService service; public NutritionAssessmentController(NutritionAssessmentService s){service=s;}
 @PostMapping("/assess") public ResponseEntity<NutritionAssessmentResponse> assess(@Valid @RequestBody NutritionAssessmentRequest request){return ResponseEntity.ok(service.assess(request));}
}
