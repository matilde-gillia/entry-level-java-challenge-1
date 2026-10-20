package com.challenge.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllEmployeesReturnsEmployees() throws Exception {
        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].uuid").isNotEmpty())
                .andExpect(jsonPath("$[0].fullName").isNotEmpty());
    }

    @Test
    void getEmployeeByUuidReturnsEmployee() throws Exception {
        String uuid = mockMvc.perform(get("/api/v1/employee"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"uuid\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/v1/employee/{uuid}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid));
    }

    @Test
    void unknownEmployeeReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/employee/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidUuidReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/employee/not-a-uuid")).andExpect(status().isBadRequest());
    }

    @Test
    void createEmployeeReturnsCreatedEmployee() throws Exception {
        String request =
                """
                {
                  "firstName": "Katherine",
                  "lastName": "Johnson",
                  "salary": 90000,
                  "age": 35,
                  "jobTitle": "Mathematician",
                  "email": "katherine@example.com",
                  "contractHireDate": "2025-01-01T00:00:00Z"
                }
                """;

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("Katherine Johnson"))
                .andExpect(jsonPath("$.contractTerminationDate").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void missingRequiredFieldReturnsBadRequest() throws Exception {
        String request =
                """
                {
                  "firstName": "Katherine",
                  "lastName": "Johnson",
                  "salary": 90000,
                  "age": 35,
                  "jobTitle": "Mathematician",
                  "email": "katherine@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void countEndpointReturnsEmployeeCount() throws Exception {
        mockMvc.perform(get("/api/v1/employee/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }
}
