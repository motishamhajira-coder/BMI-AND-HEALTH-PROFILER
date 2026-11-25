package com.healthprofiler.enums;
public enum ExerciseFrequency {
 NEVER(0), ONE_TO_TWO_DAYS(1.5), THREE_TO_FOUR_DAYS(3.5), FIVE_TO_SIX_DAYS(5.5), DAILY(7);
 private final double days; ExerciseFrequency(double days){this.days=days;} public double getDays(){return days;}
}
