# Personal BMI & Health Profiler – Module 1: BMI Calculator & Body Analysis

> **Developer:** Person 1  
> **Technology:** Java 17 · Spring Boot 3.2 · Maven · Jakarta Bean Validation · JUnit 5

---

## Table of Contents

1. [Module Overview](#module-overview)
2. [Technologies Used](#technologies-used)
3. [Project Architecture](#project-architecture)
4. [Formulas and Calculations](#formulas-and-calculations)
   - [Measurement Conversion](#measurement-conversion)
   - [BMI Formula](#bmi-formula)
   - [BMI Classification](#bmi-classification)
   - [Healthy Weight Range](#healthy-weight-range)
   - [BMR Formula (Mifflin-St Jeor)](#bmr-formula-mifflin-st-jeor)
   - [TDEE Calculation](#tdee-calculation)
   - [Water Requirement Estimation](#water-requirement-estimation)
5. [API Endpoint](#api-endpoint)
6. [Sample JSON Request & Response](#sample-json-request--response)
7. [How to Run the Application](#how-to-run-the-application)
8. [How to Test Using Postman](#how-to-test-using-postman)
9. [Running the Tests](#running-the-tests)
10. [Integration Instructions](#integration-instructions)
    - [Person 2 – Nutrition & Lifestyle Assessment](#person-2--nutrition--lifestyle-assessment)
    - [Person 3 – Python Recommendation System](#person-3--python-recommendation-system)
    - [Person 4 – Frontend](#person-4--frontend)
11. [BMI Limitations Disclaimer](#bmi-limitations-disclaimer)

---

## Module Overview

This module provides a complete **BMI Calculator and Body Analysis** REST API built with Java and Spring Boot.

It is responsible for:

- Accepting user measurements (age, weight, height) in multiple units
- Converting measurements to SI units
- Calculating BMI using the standard WHO formula
- Classifying BMI into one of four categories
- Estimating a healthy weight range
- Calculating BMR (Basal Metabolic Rate) using the Mifflin-St Jeor formula
- Estimating TDEE (Total Daily Energy Expenditure) based on activity level
- Estimating daily water requirements
- Providing initial general health recommendations per BMI category
- Returning all results as structured JSON for integration with the other three modules

**This module does NOT implement:**
- Nutrition assessment or dietary restriction guidance (Person 2)
- Lifestyle profiling or sleep analysis (Person 2)
- Recommendation engine logic (Person 3)
- Frontend interface (Person 4)

---

## Technologies Used

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Core programming language |
| Spring Boot | 3.2.0 | Application framework |
| Spring Web | (included) | REST API layer |
| Jakarta Bean Validation | (included) | Input validation |
| JUnit 5 | (included) | Unit and integration testing |
| Maven | 3.x | Build and dependency management |
| Jackson | (included) | JSON serialisation |

No database is used. All requests are processed in memory.

---

## Project Architecture

```
src/
├── main/
│   ├── java/com/healthprofiler/
│   │   ├── HealthProfilerApplication.java   ← Spring Boot entry point
│   │   ├── controller/
│   │   │   └── BMIController.java           ← REST endpoint handler
│   │   ├── service/
│   │   │   └── BMIService.java              ← All business logic and calculations
│   │   ├── model/
│   │   │   └── BMIClassification.java       ← Domain model for BMI classification
│   │   ├── dto/
│   │   │   ├── BMIRequest.java              ← Incoming JSON (with validation)
│   │   │   ├── BMIResponse.java             ← Outgoing JSON
│   │   │   ├── HealthyWeightRange.java      ← Min/max weight in kg
│   │   │   └── WaterRequirement.java        ← Min/max water in litres
│   │   ├── enums/
│   │   │   ├── WeightUnit.java              ← KG | LBS
│   │   │   ├── HeightUnit.java              ← CM | FEET
│   │   │   ├── BMICategory.java             ← UNDERWEIGHT | NORMAL_WEIGHT | OVERWEIGHT | OBESE
│   │   │   ├── ActivityLevel.java           ← SEDENTARY → EXTRA_ACTIVE (with multipliers)
│   │   │   └── BiologicalSex.java           ← MALE | FEMALE
│   │   ├── exception/
│   │   │   ├── InvalidMeasurementException.java
│   │   │   └── GlobalExceptionHandler.java  ← Structured JSON error responses
│   │   └── util/
│   │       └── MeasurementConverter.java    ← Reusable unit conversion methods
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/healthprofiler/
        ├── BMIServiceTest.java              ← Unit tests for all calculations
        ├── MeasurementConverterTest.java    ← Unit tests for conversions
        └── BMIControllerIntegrationTest.java← Full HTTP integration tests
```

**Layered architecture:**

```
HTTP Request → BMIController → BMIService → MeasurementConverter
                                          → BMIClassification
                                          ↓
HTTP Response ← BMIController ← BMIResponse DTO
```

---

## Formulas and Calculations

### Measurement Conversion

#### Pounds to Kilograms
```
Weight (kg) = Weight (lbs) × 0.45359237
```
_Source: NIST exact definition of the international pound._

#### Centimetres to Metres
```
Height (m) = Height (cm) ÷ 100
```

#### Feet to Metres (decimal feet)
```
Height (m) = Height (feet) × 0.3048
```
_Source: Exact international foot definition._

#### Feet and Inches to Metres (combined)
```
Height (m) = (feet × 0.3048) + (inches × 0.0254)
```

---

### BMI Formula

```
BMI = Weight (kg) ÷ Height (m)²
```

- Weight must be in **kilograms**
- Height must be in **metres**
- Result is displayed rounded to **2 decimal places**
- Full precision is retained internally for all dependent calculations

---

### BMI Classification

Based on the World Health Organisation (WHO) standard ranges:

| BMI Range | Category | Colour |
|---|---|---|
| Below 18.5 | UNDERWEIGHT | BLUE |
| 18.5 – 24.9 | NORMAL_WEIGHT | GREEN |
| 25.0 – 29.9 | OVERWEIGHT | ORANGE |
| 30.0 and above | OBESE | RED |

---

### Healthy Weight Range

Calculated using the standard BMI boundary values applied to the user's height:

```
Minimum Healthy Weight (kg) = 18.5 × Height (m)²
Maximum Healthy Weight (kg) = 24.9 × Height (m)²
```

Both values are returned in **kilograms**, rounded to 2 decimal places.

---

### BMR Formula (Mifflin-St Jeor)

Requires: biological sex, weight in kg, height in **cm**, age in years.

**Male:**
```
BMR = (10 × weight_kg) + (6.25 × height_cm) − (5 × age) + 5
```

**Female:**
```
BMR = (10 × weight_kg) + (6.25 × height_cm) − (5 × age) − 161
```

If biological sex is not provided, BMR will not be calculated.  
The response field `calorieStatus` will be set to `"PENDING_SEX"`.

---

### TDEE Calculation

```
TDEE = BMR × Activity Multiplier
```

| Activity Level | Multiplier | Description |
|---|---|---|
| SEDENTARY | 1.2 | Little or no exercise |
| LIGHTLY_ACTIVE | 1.375 | Light exercise 1–3 days/week |
| MODERATELY_ACTIVE | 1.55 | Moderate exercise 3–5 days/week |
| VERY_ACTIVE | 1.725 | Hard exercise 6–7 days/week |
| EXTRA_ACTIVE | 1.9 | Very hard exercise / physical job |

**`calorieStatus` values in the response:**

| Status | Meaning |
|---|---|
| `CALCULATED` | Both BMR and TDEE are calculated |
| `BMR_ONLY` | BMR calculated; activity level not provided |
| `PENDING_SEX` | Sex not provided; no calorie estimates available |

---

### Water Requirement Estimation

```
Minimum daily water (litres) = (Weight (kg) × 30) ÷ 1000
Maximum daily water (litres) = (Weight (kg) × 35) ÷ 1000
```

> ⚠️ **This is a general estimate.** Actual hydration needs vary based on climate, activity level, health status, diet, and individual factors.

---

## API Endpoint

### `POST /api/bmi/calculate`

| Property | Value |
|---|---|
| Method | POST |
| URL | `http://localhost:8080/api/bmi/calculate` |
| Content-Type | `application/json` |
| Response | `200 OK` with JSON body |

#### Required Request Fields

| Field | Type | Description |
|---|---|---|
| `age` | Integer | Age in years (1–120) |
| `weight` | Double | Body weight |
| `weightUnit` | String | `"KG"` or `"LBS"` |
| `height` | Double | Height (or feet component if `heightUnit` is `"FEET"`) |
| `heightUnit` | String | `"CM"` or `"FEET"` |

#### Optional Request Fields

| Field | Type | Description |
|---|---|---|
| `heightInches` | Double | Inches component (0–11) when `heightUnit` is `"FEET"` |
| `sex` | String | `"MALE"` or `"FEMALE"` – required for calorie estimates |
| `activityLevel` | String | See activity levels above – required for TDEE |

---

## Sample JSON Request & Response

### Full Request
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

### Full Response
```json
{
  "bmi": 22.49,
  "category": "NORMAL_WEIGHT",
  "colour": "GREEN",
  "explanation": "Your BMI falls within the normal weight category...",
  "healthyWeightRange": {
    "minimumKg": 53.47,
    "maximumKg": 71.96
  },
  "bmr": 1446.5,
  "dailyCalories": 2242.08,
  "calorieStatus": "CALCULATED",
  "dailyWater": {
    "minimumLitres": 1.95,
    "maximumLitres": 2.28,
    "note": "This is a general estimate..."
  },
  "recommendations": [
    "Maintain a balanced and varied diet.",
    "Stay physically active with regular aerobic and resistance exercise.",
    "..."
  ],
  "disclaimer": "BMI is a population-level screening tool, not a clinical diagnostic measure..."
}
```

### Minimal Request (no optional fields)
```json
{
  "age": 25,
  "weight": 70,
  "weightUnit": "KG",
  "height": 175,
  "heightUnit": "CM"
}
```

### Feet + Inches Request
```json
{
  "age": 25,
  "weight": 154,
  "weightUnit": "LBS",
  "height": 5,
  "heightInches": 9,
  "heightUnit": "FEET"
}
```

### Validation Error Response (HTTP 400)
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Input validation failed. Please check the provided values.",
  "details": [
    "age: Age is required.",
    "weight: Weight must be greater than 0.5."
  ]
}
```

---

## How to Run the Application

### Prerequisites

- Java JDK 17 or higher  
  Download: https://adoptium.net
- Apache Maven 3.8+  
  Download: https://maven.apache.org/download.cgi
- Ensure both are in your system PATH

### Verify installation

```bash
java -version
mvn -version
```

### Run the application

```bash
# From the project root directory:
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

### Build as JAR and run

```bash
mvn clean package
java -jar target/bmi-health-profiler-1.0.0.jar
```

---

## How to Test Using Postman

1. Open **Postman**
2. Create a new **POST** request
3. Set URL to: `http://localhost:8080/api/bmi/calculate`
4. Under **Headers**, add:  
   `Content-Type: application/json`
5. Under **Body**, select **raw** → **JSON**, then paste a sample request

### Quick Tests

| Scenario | Weight | Height | Expected Category |
|---|---|---|---|
| Underweight | 45 kg | 170 cm | UNDERWEIGHT |
| Normal weight | 65 kg | 170 cm | NORMAL_WEIGHT |
| Overweight | 85 kg | 170 cm | OVERWEIGHT |
| Obese | 110 kg | 170 cm | OBESE |

---

## Running the Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BMIServiceTest
mvn test -Dtest=MeasurementConverterTest
mvn test -Dtest=BMIControllerIntegrationTest
```

**Test coverage:**

| Test Class | Coverage |
|---|---|
| `BMIServiceTest` | BMI calculation, all 4 categories, healthy weight, BMR, TDEE, water, missing optional fields |
| `MeasurementConverterTest` | KG/LBS, CM/FEET, feet+inches, edge cases, error cases |
| `BMIControllerIntegrationTest` | HTTP 200 success, HTTP 400 validation failures, response structure |

---

## Integration Instructions

### Person 2 – Nutrition & Lifestyle Assessment

**Endpoint to call:**
```
POST http://localhost:8080/api/bmi/calculate
```

**Fields relevant to Person 2:**

| Field | JSON Path | Description |
|---|---|---|
| BMI value | `$.bmi` | Rounded to 2 decimal places |
| BMI category | `$.category` | UNDERWEIGHT / NORMAL_WEIGHT / OVERWEIGHT / OBESE |
| Daily calories | `$.dailyCalories` | TDEE in kcal/day (null if not calculated) |
| Calorie status | `$.calorieStatus` | CALCULATED / BMR_ONLY / PENDING_SEX |
| BMR | `$.bmr` | Basal Metabolic Rate in kcal/day |
| Water minimum | `$.dailyWater.minimumLitres` | Minimum daily water in litres |
| Water maximum | `$.dailyWater.maximumLitres` | Maximum daily water in litres |
| Healthy weight min | `$.healthyWeightRange.minimumKg` | Minimum healthy weight in kg |
| Healthy weight max | `$.healthyWeightRange.maximumKg` | Maximum healthy weight in kg |

> **Note:** Do not duplicate the BMI calculation. Call this API and use the returned values.

---

### Person 3 – Python Recommendation System

**Endpoint to call:**
```
POST http://localhost:8080/api/bmi/calculate
```

**Fields relevant to Person 3:**

| Field | JSON Path | Type |
|---|---|---|
| BMI value | `$.bmi` | Double |
| BMI category | `$.category` | String (enum) |
| Healthy weight range | `$.healthyWeightRange` | Object |
| Daily calories | `$.dailyCalories` | Double or null |
| Water requirement | `$.dailyWater` | Object |

**Python example (requests library):**
```python
import requests

payload = {
    "age": 25,
    "weight": 70,
    "weightUnit": "KG",
    "height": 175,
    "heightUnit": "CM",
    "sex": "MALE",
    "activityLevel": "MODERATELY_ACTIVE"
}

response = requests.post("http://localhost:8080/api/bmi/calculate", json=payload)
data = response.json()

bmi        = data["bmi"]
category   = data["category"]
calories   = data["dailyCalories"]
water_min  = data["dailyWater"]["minimumLitres"]
water_max  = data["dailyWater"]["maximumLitres"]
```

---

### Person 4 – Frontend

**Endpoint:**
```
POST http://localhost:8080/api/bmi/calculate
Content-Type: application/json
```

**All JSON property names are stable and will not change between builds.**

**Complete response field reference:**

| Property | Type | Always Present | Description |
|---|---|---|---|
| `bmi` | Double | ✅ | BMI value rounded to 2dp |
| `category` | String | ✅ | BMI category enum |
| `colour` | String | ✅ | Display colour for the category |
| `explanation` | String | ✅ | Category explanation text |
| `healthyWeightRange.minimumKg` | Double | ✅ | Min healthy weight (kg) |
| `healthyWeightRange.maximumKg` | Double | ✅ | Max healthy weight (kg) |
| `bmr` | Double or null | ✅ | BMR in kcal/day |
| `dailyCalories` | Double or null | ✅ | TDEE in kcal/day |
| `calorieStatus` | String | ✅ | CALCULATED / BMR_ONLY / PENDING_SEX |
| `dailyWater.minimumLitres` | Double | ✅ | Min water (litres) |
| `dailyWater.maximumLitres` | Double | ✅ | Max water (litres) |
| `dailyWater.note` | String | ✅ | Hydration disclaimer |
| `recommendations` | Array of String | ✅ | Category-based recommendations |
| `disclaimer` | String | ✅ | BMI limitations note |

**Error responses** always include: `timestamp`, `status`, `error`, `message`, and `details` (array, only on validation errors).

**CORS:** If you need to call this from a browser, add your frontend origin to the allowed origins. Contact Person 1 to configure CORS.

---

## BMI Limitations Disclaimer

> BMI is a population-level screening tool, not a clinical diagnostic measure. It may not accurately reflect body composition for children, adolescents, pregnant or breastfeeding individuals, the elderly, or highly muscular individuals. This module does not provide medical advice. Please consult a qualified healthcare professional for personalised health guidance.
