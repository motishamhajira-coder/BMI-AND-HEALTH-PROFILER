package com.healthprofiler.dto;
import com.healthprofiler.enums.*;
import jakarta.validation.constraints.*;
import java.util.*;
public class LifestyleAssessmentRequest {
 @NotNull private PhysicalActivity physicalActivity;
 @NotNull private ExerciseFrequency exerciseFrequency;
 @NotNull @Min(0) @Max(value=600, message="Average exercise duration must be 600 minutes or less.") private Integer averageExerciseDurationMinutes;
 @NotNull private ExerciseType exerciseType;
 @NotNull @Min(0) @Max(100000) private Integer dailySteps;
 @NotEmpty private Set<HealthCondition> healthConditions=new HashSet<>();
 @Size(max=200) private String otherHealthCondition;
 @NotEmpty private Set<DietaryRestriction> dietaryRestrictions=new HashSet<>();
 @Size(max=200) private String otherDietaryRestriction;
 @NotEmpty private Set<FoodAllergy> foodAllergies=new HashSet<>();
 @Size(max=200) private String otherFoodAllergy;
 @AssertTrue(message="NO_KNOWN_HEALTH_CONDITIONS must be selected alone") public boolean isHealthSelectionValid(){return healthConditions==null||healthConditions.size()<=1||!healthConditions.contains(HealthCondition.NO_KNOWN_HEALTH_CONDITIONS);}
 @AssertTrue(message="NO_SPECIFIC_RESTRICTIONS must be selected alone") public boolean isDietSelectionValid(){return dietaryRestrictions==null||dietaryRestrictions.size()<=1||!dietaryRestrictions.contains(DietaryRestriction.NO_SPECIFIC_RESTRICTIONS);}
 @AssertTrue(message="NO_KNOWN_FOOD_ALLERGIES must be selected alone") public boolean isAllergySelectionValid(){return foodAllergies==null||foodAllergies.size()<=1||!foodAllergies.contains(FoodAllergy.NO_KNOWN_FOOD_ALLERGIES);}
 public PhysicalActivity getPhysicalActivity(){return physicalActivity;} public void setPhysicalActivity(PhysicalActivity v){physicalActivity=v;}
 public ExerciseFrequency getExerciseFrequency(){return exerciseFrequency;} public void setExerciseFrequency(ExerciseFrequency v){exerciseFrequency=v;}
 public Integer getAverageExerciseDurationMinutes(){return averageExerciseDurationMinutes;} public void setAverageExerciseDurationMinutes(Integer v){averageExerciseDurationMinutes=v;}
 public ExerciseType getExerciseType(){return exerciseType;} public void setExerciseType(ExerciseType v){exerciseType=v;}
 public Integer getDailySteps(){return dailySteps;} public void setDailySteps(Integer v){dailySteps=v;}
 public Set<HealthCondition> getHealthConditions(){return healthConditions;} public void setHealthConditions(Set<HealthCondition> v){healthConditions=v;}
 public String getOtherHealthCondition(){return otherHealthCondition;} public void setOtherHealthCondition(String v){otherHealthCondition=v;}
 public Set<DietaryRestriction> getDietaryRestrictions(){return dietaryRestrictions;} public void setDietaryRestrictions(Set<DietaryRestriction> v){dietaryRestrictions=v;}
 public String getOtherDietaryRestriction(){return otherDietaryRestriction;} public void setOtherDietaryRestriction(String v){otherDietaryRestriction=v;}
 public Set<FoodAllergy> getFoodAllergies(){return foodAllergies;} public void setFoodAllergies(Set<FoodAllergy> v){foodAllergies=v;}
 public String getOtherFoodAllergy(){return otherFoodAllergy;} public void setOtherFoodAllergy(String v){otherFoodAllergy=v;}
}
