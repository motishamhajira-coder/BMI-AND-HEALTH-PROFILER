package com.healthprofiler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthprofiler.dto.*; import com.healthprofiler.enums.*;
import com.healthprofiler.service.*;
import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType; import org.springframework.test.web.servlet.MockMvc;
import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Assertions;
@SpringBootTest @AutoConfigureMockMvc class AssessmentControllerIntegrationTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper mapper;
 @Test void allNutritionChoiceOptionsAreHandled() {
  NutritionAssessmentService service=new NutritionAssessmentService();
  for(WaterIntake water:WaterIntake.values()) for(SleepDuration sleep:SleepDuration.values()) {
   NutritionAssessmentRequest r=new NutritionAssessmentRequest();r.setWaterIntake(water);r.setDailyCaloriesConsumed(1800d);r.setSleepDuration(sleep);r.setRegularSleepSchedule(false);
   Assertions.assertNotNull(service.assess(r).hydrationStatus());
  }
 }
 @Test void allLifestyleActivityFrequencyAndExerciseOptionsAreHandled() {
  LifestyleAssessmentService service=new LifestyleAssessmentService();
  for(PhysicalActivity activity:PhysicalActivity.values()) for(ExerciseFrequency frequency:ExerciseFrequency.values()) for(ExerciseType type:ExerciseType.values()) {
   LifestyleAssessmentRequest r=new LifestyleAssessmentRequest();r.setPhysicalActivity(activity);r.setExerciseFrequency(frequency);r.setAverageExerciseDurationMinutes(20);r.setExerciseType(type);r.setDailySteps(3000);r.setHealthConditions(Set.of(HealthCondition.NO_KNOWN_HEALTH_CONDITIONS));r.setDietaryRestrictions(Set.of(DietaryRestriction.NO_SPECIFIC_RESTRICTIONS));r.setFoodAllergies(Set.of(FoodAllergy.NO_KNOWN_FOOD_ALLERGIES));
   Assertions.assertEquals((int)Math.round(frequency.getDays()*20),service.assess(r).weeklyExerciseDurationMinutes());
  }
 }
 @Test void nutritionIndependentWithOptionalTdee() throws Exception {NutritionAssessmentRequest r=new NutritionAssessmentRequest();r.setWaterIntake(WaterIntake.ONE_TO_TWO_LITRES);r.setDailyCaloriesConsumed(2100d);r.setSleepDuration(SleepDuration.SEVEN_TO_EIGHT_HOURS);r.setRegularSleepSchedule(true);r.setDailyCalories(2200d);
 mvc.perform(post("/api/nutrition/assess").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(r))).andExpect(status().isOk()).andExpect(jsonPath("$.estimatedHydrationRequirementLitres").value(2.0)).andExpect(jsonPath("$.calorieComparison").value("CLOSE_TO_ESTIMATE")); }
 @Test void nutritionAllowsMissingTdee() throws Exception {NutritionAssessmentRequest r=new NutritionAssessmentRequest();r.setWaterIntake(WaterIntake.TWO_TO_THREE_LITRES);r.setDailyCaloriesConsumed(2000d);r.setSleepDuration(SleepDuration.FIVE_TO_SIX_HOURS);r.setRegularSleepSchedule(false);
 mvc.perform(post("/api/nutrition/assess").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(r))).andExpect(status().isOk()).andExpect(jsonPath("$.calorieComparison").value("UNAVAILABLE_NO_TDEE")); }
 @Test void lifestyleReturnsWeeklyExerciseAndPrecautions() throws Exception {LifestyleAssessmentRequest r=new LifestyleAssessmentRequest();r.setPhysicalActivity(PhysicalActivity.MODERATE);r.setExerciseFrequency(ExerciseFrequency.THREE_TO_FOUR_DAYS);r.setAverageExerciseDurationMinutes(30);r.setExerciseType(ExerciseType.WALKING);r.setDailySteps(7000);r.setHealthConditions(Set.of(HealthCondition.DIABETES));r.setDietaryRestrictions(Set.of(DietaryRestriction.VEGETARIAN));r.setFoodAllergies(Set.of(FoodAllergy.NUTS));
 mvc.perform(post("/api/lifestyle/assess").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(r))).andExpect(status().isOk()).andExpect(jsonPath("$.weeklyExerciseDurationMinutes").value(105)).andExpect(jsonPath("$.precautions").isNotEmpty()).andExpect(jsonPath("$.stepGoalProgressPercent").value(70.0)); }
 @Test void lifestyleRejectsConflictingAllergyChoice() throws Exception {LifestyleAssessmentRequest r=new LifestyleAssessmentRequest();r.setPhysicalActivity(PhysicalActivity.LIGHT);r.setExerciseFrequency(ExerciseFrequency.NEVER);r.setAverageExerciseDurationMinutes(0);r.setExerciseType(ExerciseType.OTHER);r.setDailySteps(5000);r.setHealthConditions(Set.of(HealthCondition.NO_KNOWN_HEALTH_CONDITIONS));r.setDietaryRestrictions(Set.of(DietaryRestriction.NO_SPECIFIC_RESTRICTIONS));r.setFoodAllergies(Set.of(FoodAllergy.NUTS,FoodAllergy.NO_KNOWN_FOOD_ALLERGIES));
 mvc.perform(post("/api/lifestyle/assess").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(r))).andExpect(status().isBadRequest()); }
}
