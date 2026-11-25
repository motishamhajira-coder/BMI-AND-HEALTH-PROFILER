package com.healthprofiler.dto;
import java.util.List;
public record NutritionAssessmentResponse(String reportedWaterIntake, double estimatedHydrationRequirementLitres,
 String hydrationComparison, String hydrationStatus, Double reportedCalories, Double estimatedDailyCalories,
 String calorieComparison, String sleepDuration, String sleepAssessment, boolean regularSleepSchedule,
 List<String> observations, List<String> recommendations) {}
