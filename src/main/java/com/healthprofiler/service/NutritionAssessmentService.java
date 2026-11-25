package com.healthprofiler.service;
import com.healthprofiler.dto.*;
import com.healthprofiler.enums.*;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class NutritionAssessmentService {
 public NutritionAssessmentResponse assess(NutritionAssessmentRequest r){
  double[] ranges=switch(r.getWaterIntake()){case LESS_THAN_1_LITRE->new double[]{0,1};case ONE_TO_TWO_LITRES->new double[]{1,2};case TWO_TO_THREE_LITRES->new double[]{2,3};case MORE_THAN_3_LITRES->new double[]{3,4};};
  final double target=2.0; // General 2 L/day screening estimate; no weight or clinical context supplied.
  String hyd=ranges[0]>=target?"AT_OR_ABOVE_ESTIMATE":ranges[1]<target?"BELOW_ESTIMATE":"MAY_BE_BELOW_ESTIMATE";
  String comparison=hyd.equals("AT_OR_ABOVE_ESTIMATE")?"Reported range meets or exceeds the general estimate.":"Reported range may be below the general estimate.";
  String sleep=switch(r.getSleepDuration()){case LESS_THAN_5_HOURS->"Short sleep duration";case FIVE_TO_SIX_HOURS->"Below the commonly recommended adult range";case SEVEN_TO_EIGHT_HOURS->"Within the commonly recommended adult range";case MORE_THAN_8_HOURS->"Longer sleep duration; needs vary by individual";};
  List<String> rec=new ArrayList<>(); if(!hyd.equals("AT_OR_ABOVE_ESTIMATE"))rec.add("Consider drinking water regularly through the day; individual fluid needs vary.");
  if(r.getDailyCalories()!=null)rec.add("Compare calorie intake with your BMI assessment estimate in context; one day is not a diagnosis.");else rec.add("No TDEE was supplied; calorie comparison is unavailable. Complete BMI with sex and activity level for an estimate.");
  if(!r.getRegularSleepSchedule())rec.add("A consistent sleep and wake time may support a regular sleep routine.");
  if(r.getSleepDuration()==SleepDuration.LESS_THAN_5_HOURS||r.getSleepDuration()==SleepDuration.FIVE_TO_SIX_HOURS)rec.add("Consider sleep habits and seek professional advice if sleep concerns persist.");
  String calorieCompare=r.getDailyCalories()==null?"UNAVAILABLE_NO_TDEE":Math.abs(r.getDailyCaloriesConsumed()-r.getDailyCalories())<=r.getDailyCalories()*.1?"CLOSE_TO_ESTIMATE":r.getDailyCaloriesConsumed()>r.getDailyCalories()?"ABOVE_ESTIMATE":"BELOW_ESTIMATE";
  return new NutritionAssessmentResponse(r.getWaterIntake().name(),target,comparison,hyd,r.getDailyCaloriesConsumed(),r.getDailyCalories(),calorieCompare,r.getSleepDuration().name(),sleep,r.getRegularSleepSchedule(),List.of("Calorie needs and intake vary; this comparison is not a diagnosis."),rec);
 }
}
