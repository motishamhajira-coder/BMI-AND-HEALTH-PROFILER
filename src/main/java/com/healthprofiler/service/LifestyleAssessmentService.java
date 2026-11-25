package com.healthprofiler.service;
import com.healthprofiler.dto.*;
import com.healthprofiler.enums.*;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class LifestyleAssessmentService {
 public LifestyleAssessmentResponse assess(LifestyleAssessmentRequest r){
  int weekly=(int)Math.round(r.getExerciseFrequency().getDays()*r.getAverageExerciseDurationMinutes());
  int goal=10000, steps=r.getDailySteps();
  Set<String> precautions=new LinkedHashSet<>();
  for(HealthCondition c:r.getHealthConditions())switch(c){case DIABETES->precautions.add("For diabetes, discuss exercise and nutrition changes with your care team, especially if medication affects blood glucose.");case HIGH_BLOOD_PRESSURE->precautions.add("For high blood pressure, seek clinician guidance before major activity changes.");case HEART_DISEASE->precautions.add("With heart disease, follow your clinician's activity guidance.");case ASTHMA->precautions.add("For asthma, follow your prescribed action plan during activity.");case THYROID_DISORDERS->precautions.add("For thyroid disorders, discuss significant diet or activity changes with your care team.");case PCOS->precautions.add("For PCOS, seek individualized clinical guidance for treatment decisions.");case OTHER->precautions.add("Discuss condition-specific precautions with a qualified healthcare professional.");default->{}}
  for(FoodAllergy a:r.getFoodAllergies())if(a!=FoodAllergy.NO_KNOWN_FOOD_ALLERGIES)precautions.add("Avoid reported allergen: "+(a==FoodAllergy.OTHER?r.getOtherFoodAllergy():a.name())+"; check ingredient and cross-contact information.");
  Set<String> foods=new LinkedHashSet<>(); if(!r.getDietaryRestrictions().contains(DietaryRestriction.VEGAN)&&!r.getDietaryRestrictions().contains(DietaryRestriction.VEGETARIAN)&&!r.getFoodAllergies().contains(FoodAllergy.EGGS))foods.add("Eggs (if otherwise suitable)");
  foods.add("Lentils and pulses selected to avoid reported allergens");
  if(!r.getDietaryRestrictions().contains(DietaryRestriction.VEGAN)&&!r.getFoodAllergies().contains(FoodAllergy.MILK)&&!r.getDietaryRestrictions().contains(DietaryRestriction.DAIRY_FREE)&&!r.getDietaryRestrictions().contains(DietaryRestriction.LACTOSE_FREE))foods.add("Dairy or fortified alternatives (if otherwise suitable)");
  else foods.add("Fortified plant alternatives (check ingredients against your allergies)");
  String suggestions=steps<goal?"Add movement in manageable increments, such as short walks or taking movement breaks; adapt to ability and clinician advice.":"Maintain a comfortable routine with varied movement and recovery; the illustrative step goal is not a medical prescription.";
  return new LifestyleAssessmentResponse(r.getPhysicalActivity(),r.getExerciseFrequency(),weekly,r.getExerciseType(),steps,goal,Math.min(100.0,steps*100.0/goal),"Illustrative 10,000-step goal; not a universal medical requirement.",suggestions,Set.copyOf(r.getHealthConditions()),Set.copyOf(r.getDietaryRestrictions()),Set.copyOf(r.getFoodAllergies()),r.getOtherHealthCondition(),r.getOtherDietaryRestriction(),r.getOtherFoodAllergy(),precautions,foods);
 }
}
