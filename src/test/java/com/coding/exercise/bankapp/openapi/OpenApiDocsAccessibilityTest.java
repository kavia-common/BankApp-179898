package com.coding.exercise.bankapp.openapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration-style test to verify that the generated OpenAPI JSON is exposed
 * at /bank-api/v3/api-docs and is publicly accessible (no authentication).
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocsAccessibilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /bank-api/v3/api-docs returns 200 and JSON without authentication")
    void openApiDocsAreAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/bank-api/v3/api-docs"))
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.openapi").exists());
    }
}
