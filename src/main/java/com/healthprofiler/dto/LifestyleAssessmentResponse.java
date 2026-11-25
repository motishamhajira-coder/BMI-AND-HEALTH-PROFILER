package com.healthprofiler.dto;
import com.healthprofiler.enums.*;
import java.util.Set;
public record LifestyleAssessmentResponse(PhysicalActivity physicalActivity, ExerciseFrequency exerciseFrequency,
 int weeklyExerciseDurationMinutes, ExerciseType exerciseType, int averageDailySteps, int illustrativeStepGoal,
 double stepGoalProgressPercent, String activityOverview, String movementSuggestions, Set<HealthCondition> healthConditions,
 Set<DietaryRestriction> dietaryRestrictions, Set<FoodAllergy> foodAllergies,
 String otherHealthCondition, String otherDietaryRestriction, String otherFoodAllergy,
 Set<String> precautions, Set<String> suitableFoodGuidance) {}
