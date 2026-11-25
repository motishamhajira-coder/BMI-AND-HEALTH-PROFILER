package com.healthprofiler.controller;
import com.healthprofiler.dto.*; import com.healthprofiler.service.LifestyleAssessmentService;
import jakarta.validation.Valid; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/lifestyle") public class LifestyleAssessmentController {
 private final LifestyleAssessmentService service; public LifestyleAssessmentController(LifestyleAssessmentService s){service=s;}
 @PostMapping("/assess") public ResponseEntity<LifestyleAssessmentResponse> assess(@Valid @RequestBody LifestyleAssessmentRequest request){return ResponseEntity.ok(service.assess(request));}
}
