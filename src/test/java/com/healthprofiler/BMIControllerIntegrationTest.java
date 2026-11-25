package com.healthprofiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthprofiler.dto.BMIRequest;
import com.healthprofiler.dto.BMIResponse;
import com.healthprofiler.enums.ActivityLevel;
import com.healthprofiler.enums.BiologicalSex;
import com.healthprofiler.enums.BMICategory;
import com.healthprofiler.enums.HeightUnit;
import com.healthprofiler.enums.WeightUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the BMI Calculator REST API.
 *
 * <p>Tests the full HTTP request/response cycle through the Spring context,
 * verifying HTTP status codes, JSON structure, calculated values, and
 * error handling.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BMIController – Integration Tests")
class BMIControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ENDPOINT = "/api/bmi/calculate";
    private static final double DELTA = 0.05;

    // -----------------------------------------------------------------------
    // HTTP 200 – Successful calculations
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/bmi/calculate – full request returns HTTP 200 and correct BMI")
    void calculate_fullRequest_returns200AndCorrectBMI() throws Exception {
        BMIRequest request = buildRequest(21, 65.0, WeightUnit.KG, 170.0, HeightUnit.CM,
                BiologicalSex.FEMALE, ActivityLevel.MODERATELY_ACTIVE);

        MvcResult result = mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bmi").exists())
                .andExpect(jsonPath("$.category").value("NORMAL_WEIGHT"))
                .andExpect(jsonPath("$.colour").value("GREEN"))
                .andExpect(jsonPath("$.calorieStatus").value("CALCULATED"))
                .andExpect(jsonPath("$.dailyWater").exists())
                .andExpect(jsonPath("$.healthyWeightRange").exists())
                .andExpect(jsonPath("$.recommendations").isArray())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        BMIResponse response = objectMapper.readValue(json, BMIResponse.class);
        assertEquals(22.49, response.getBmi(), DELTA);
        assertEquals(BMICategory.NORMAL_WEIGHT, response.getCategory());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – minimal request (no sex/activity) returns HTTP 200")
    void calculate_minimalRequest_returns200() throws Exception {
        BMIRequest request = buildRequest(30, 80.0, WeightUnit.KG, 180.0, HeightUnit.CM, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmi").exists())
                .andExpect(jsonPath("$.calorieStatus").value("PENDING_SEX"));
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – LBS and FEET units accepted")
    void calculate_imperialUnits_returns200() throws Exception {
        BMIRequest request = buildRequest(25, 143.3, WeightUnit.LBS, 5.57, HeightUnit.FEET, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmi").exists());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – sex provided, no activity → BMR_ONLY")
    void calculate_sexButNoActivity_bmrOnly() throws Exception {
        BMIRequest request = buildRequest(28, 72.0, WeightUnit.KG, 175.0, HeightUnit.CM,
                BiologicalSex.MALE, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calorieStatus").value("BMR_ONLY"))
                .andExpect(jsonPath("$.bmr").exists())
                .andExpect(jsonPath("$.dailyCalories").isEmpty());
    }

    // -----------------------------------------------------------------------
    // HTTP 400 – Input validation failures
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/bmi/calculate – missing age returns HTTP 400")
    void calculate_missingAge_returns400() throws Exception {
        String json = """
                {
                    "weight": 65,
                    "weightUnit": "KG",
                    "height": 170,
                    "heightUnit": "CM"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – missing weight returns HTTP 400")
    void calculate_missingWeight_returns400() throws Exception {
        String json = """
                {
                    "age": 25,
                    "weightUnit": "KG",
                    "height": 170,
                    "heightUnit": "CM"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – age = 0 returns HTTP 400")
    void calculate_zeroAge_returns400() throws Exception {
        String json = """
                {
                    "age": 0,
                    "weight": 65,
                    "weightUnit": "KG",
                    "height": 170,
                    "heightUnit": "CM"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – negative weight returns HTTP 400")
    void calculate_negativeWeight_returns400() throws Exception {
        String json = """
                {
                    "age": 25,
                    "weight": -10,
                    "weightUnit": "KG",
                    "height": 170,
                    "heightUnit": "CM"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – invalid weightUnit returns HTTP 400")
    void calculate_invalidWeightUnit_returns400() throws Exception {
        String json = """
                {
                    "age": 25,
                    "weight": 65,
                    "weightUnit": "STONE",
                    "height": 170,
                    "heightUnit": "CM"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – invalid heightUnit returns HTTP 400")
    void calculate_invalidHeightUnit_returns400() throws Exception {
        String json = """
                {
                    "age": 25,
                    "weight": 65,
                    "weightUnit": "KG",
                    "height": 170,
                    "heightUnit": "METERS"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – age over 120 returns HTTP 400")
    void calculate_excessiveAge_returns400() throws Exception {
        String json = """
                {
                    "age": 150,
                    "weight": 65,
                    "weightUnit": "KG",
                    "height": 170,
                    "heightUnit": "CM"
                }
                """;

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bmi/calculate – empty JSON body returns HTTP 400")
    void calculate_emptyBody_returns400() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------------------------------------------------
    // Response structure validation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Response – disclaimer field is always present")
    void response_disclaimerAlwaysPresent() throws Exception {
        BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM, null, null);

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disclaimer").exists())
                .andExpect(jsonPath("$.disclaimer").isNotEmpty());
    }

    @Test
    @DisplayName("Response – healthyWeightRange min is less than max")
    void response_healthyWeightRange_minLessThanMax() throws Exception {
        BMIRequest request = buildRequest(25, 70.0, WeightUnit.KG, 175.0, HeightUnit.CM, null, null);

        MvcResult result = mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        BMIResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), BMIResponse.class);
        assertTrue(response.getHealthyWeightRange().getMinimumKg()
                < response.getHealthyWeightRange().getMaximumKg());
    }

    // -----------------------------------------------------------------------
    // Test helpers
    // -----------------------------------------------------------------------

    private BMIRequest buildRequest(int age, double weight, WeightUnit wUnit,
                                    double height, HeightUnit hUnit,
                                    BiologicalSex sex, ActivityLevel activityLevel) {
        BMIRequest req = new BMIRequest();
        req.setAge(age);
        req.setWeight(weight);
        req.setWeightUnit(wUnit);
        req.setHeight(height);
        req.setHeightUnit(hUnit);
        req.setSex(sex);
        req.setActivityLevel(activityLevel);
        return req;
    }
}
