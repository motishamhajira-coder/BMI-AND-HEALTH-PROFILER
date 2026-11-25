# Personal BMI & Health Profiler

A Java and Spring Boot REST API that provides BMI calculation, body analysis, nutrition assessment and lifestyle profiling. It returns structured JSON responses for frontend and recommendation-system integration.

## Features

| Module | Features |
|---|---|
| Person 1 – BMI Calculator & Body Analysis | BMI calculation, classification, healthy weight range, BMR, TDEE, water estimation and health recommendations |
| Person 2 – Nutrition & Lifestyle Assessment | Hydration, calorie intake, sleep, physical activity, exercise, daily steps, health conditions, dietary restrictions, food allergies and general recommendations |
| Person 3 – Recommendation System | Python-based recommendations |
| Person 4 – Frontend | User interface and assessment flow |

## Technologies Used

- Java 17
- Spring Boot 3.2
- Maven
- Spring Web
- Jakarta Bean Validation
- JUnit 5
- Jackson JSON

No database is used. Requests are processed in memory.

## Project Structure

```text
src/
├── main/
│   ├── java/com/healthprofiler/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dto/
│   │   ├── enums/
│   │   ├── model/
│   │   ├── exception/
│   │   └── util/
│   └── resources/
└── test/
    └── java/com/healthprofiler/
```

## Getting Started

### Prerequisites

- Java JDK 17 or higher
- Apache Maven 3.8 or higher

Verify installation:

```bash
java -version
mvn -version
```

### Run the Application

Clone the repository and open the project folder in VS Code.

Run from the directory containing `pom.xml`:

```bash
mvn spring-boot:run
```

The backend starts at:

`http://localhost:8080`

## How to Use the Application

The backend can be tested using Postman without requiring a frontend.

For each API request:

1. Select the `POST` method.
2. Enter the endpoint URL.
3. Set `Content-Type: application/json`.
4. Select Body → raw → JSON.
5. Enter the request data and click Send.
6. View the JSON response.

### 1. BMI Assessment – Person 1

**Endpoint:** `POST /api/bmi/calculate`

Example request:

```json
{
  "age": 21,
  "weight": 65,
  "weightUnit": "KG",
  "height": 170,
  "heightUnit": "CM",
  "sex": "FEMALE",
  "activityLevel": "MODERATELY_ACTIVE"
}
```

Returns:
- BMI value and classification.
- Healthy weight range.
- Basal Metabolic Rate (BMR).
- Total Daily Energy Expenditure (TDEE).
- General daily water estimate.
- BMI-based recommendations.

### 2. Nutrition & Lifestyle Assessment – Person 2

This module provides two assessments through separate API endpoints.

#### A. Nutrition Assessment

**Endpoint:** `POST /api/nutrition/assess`

Evaluates:
- Reported daily water intake.
- Estimated hydration requirement and hydration status.
- Reported calorie intake compared with TDEE from Person 1, when available.
- Sleep duration and regularity.
- General nutrition and hydration recommendations.

**Sample Request:**

```json
{
  "waterIntake": "ONE_TO_TWO_LITRES",
  "dailyCaloriesConsumed": 2100,
  "dailyCalories": 2242.08,
  "sleepDuration": "SEVEN_TO_EIGHT_HOURS",
  "regularSleepSchedule": true
}
```

The optional `dailyCalories` field accepts the TDEE returned by the BMI endpoint. If TDEE is unavailable, it can be omitted or set to `null`.

#### B. Lifestyle Assessment

**Endpoint:** `POST /api/lifestyle/assess`

Evaluates:
- Physical activity level.
- Exercise frequency, duration and type.
- Daily step count and illustrative step-goal progress.
- Existing health conditions.
- Dietary restrictions and food allergies.
- General health precautions, movement suggestions and suitable food guidance.

**Sample Request:**

```json
{
  "physicalActivity": "MODERATE",
  "exerciseFrequency": "THREE_TO_FOUR_DAYS",
  "averageExerciseDurationMinutes": 30,
  "exerciseType": "WALKING",
  "dailySteps": 7000,
  "healthConditions": ["DIABETES"],
  "dietaryRestrictions": ["VEGETARIAN"],
  "foodAllergies": ["NUTS"]
}
```

Both assessments return structured JSON responses containing their respective results and recommendations.

**Integration:**
- Nutrition can use Person 1's TDEE for calorie comparison.
- Hydration estimation is calculated independently by Person 2 and does not depend on Person 1's water estimate.
- Both assessments are optional in the intended user flow and follow the mandatory BMI assessment.

## Application Flow

```text
        Start Application
               |
               v
         BMI Assessment
               |
               v
          BMI Results
               |
               v
    Optional Assessments
               |
       +-------+-------+
       |               |
       v               v
   Nutrition        Lifestyle
   Assessment       Assessment
       |               |
       +-------+-------+
               |
               v
         JSON Results
               |
               v
    Frontend / Recommendation
          Integration
```

**Note:** The backend is stateless and does not store user sessions. It cannot independently enforce BMI completion before the optional assessments. The frontend or calling module must control this sequence.

## API Reference

| Module | HTTP Method | Endpoint |
|---|---|---|
| BMI Calculator | POST | `/api/bmi/calculate` |
| Nutrition Assessment | POST | `/api/nutrition/assess` |
| Lifestyle Assessment | POST | `/api/lifestyle/assess` |

All endpoints accept JSON requests and return JSON responses.

## Error Handling

Invalid inputs, validation failures, malformed JSON and invalid enum values return structured HTTP `400 Bad Request` responses using the shared exception-handling format.

## Testing

Run all unit and integration tests:

```bash
mvn test
```

The latest reported verification during development:

- 65 tests passed.
- 0 failures.
- 0 errors.
- Includes the 14 existing BMI controller integration tests.

Re-run the tests after any code changes.

## Disclaimer

This project provides general health estimates and informational assessments, not medical diagnoses or personalised medical advice. Actual health and nutritional requirements vary between individuals. Consult a qualified healthcare professional for personalised guidance.