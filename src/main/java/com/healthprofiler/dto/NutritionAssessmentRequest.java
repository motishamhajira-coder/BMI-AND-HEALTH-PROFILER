package com.healthprofiler.dto;
import com.healthprofiler.enums.*;
import jakarta.validation.constraints.*;
public class NutritionAssessmentRequest {
 @NotNull private WaterIntake waterIntake;
 @NotNull @DecimalMin("0") @DecimalMax("10000") private Double dailyCaloriesConsumed;
 @DecimalMin("0") @DecimalMax("10000") private Double dailyCalories;
 @NotNull private SleepDuration sleepDuration;
 @NotNull private Boolean regularSleepSchedule;
 public WaterIntake getWaterIntake(){return waterIntake;} public void setWaterIntake(WaterIntake v){waterIntake=v;}
 public Double getDailyCaloriesConsumed(){return dailyCaloriesConsumed;} public void setDailyCaloriesConsumed(Double v){dailyCaloriesConsumed=v;}
 public Double getDailyCalories(){return dailyCalories;} public void setDailyCalories(Double v){dailyCalories=v;}
 public SleepDuration getSleepDuration(){return sleepDuration;} public void setSleepDuration(SleepDuration v){sleepDuration=v;}
 public Boolean getRegularSleepSchedule(){return regularSleepSchedule;} public void setRegularSleepSchedule(Boolean v){regularSleepSchedule=v;}
}
