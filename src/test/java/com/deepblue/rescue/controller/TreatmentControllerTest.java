package com.deepblue.rescue.controller;

import com.deepblue.rescue.domain.TreatmentType;
import com.deepblue.rescue.dto.request.CreateTreatmentRequest;
import com.deepblue.rescue.dto.response.TreatmentResponse;
import com.deepblue.rescue.exception.BusinessRuleException;
import com.deepblue.rescue.exception.GlobalExceptionHandler;
import com.deepblue.rescue.exception.ResourceNotFoundException;
import com.deepblue.rescue.service.TreatmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        TreatmentController.class
)
@Import(
        GlobalExceptionHandler.class
)
class TreatmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreatmentService service;

    // ------------------------------------------------------------------
    // POST /api/treatments
    // ------------------------------------------------------------------

    @Test
    void shouldCreateTreatment() throws Exception {
        TreatmentResponse response = new TreatmentResponse(
                100L, "AN-2026-001", "SPEC-001",
                LocalDateTime.of(2026, 8, 21, 9, 30), TreatmentType.WOUND_CARE,
                "Cleaning and evaluation of left front flipper injury."
        );

        when(service.register(any(CreateTreatmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/treatments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "animalCode": "AN-2026-001",
                                  "specialistCode": "SPEC-001",
                                  "performedAt": "2026-08-21T09:30:00",
                                  "type": "WOUND_CARE",
                                  "description": "Cleaning and evaluation of left front flipper injury."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.animalCode").value("AN-2026-001"))
                .andExpect(jsonPath("$.type").value("WOUND_CARE"));

        verify(service).register(any(CreateTreatmentRequest.class));
    }

    @Test
    void shouldReturn400WhenTreatmentRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/treatments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "animalCode": "",
                                  "specialistCode": "",
                                  "type": null,
                                  "description": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.details.animalCode").value("Animal code is required"))
                .andExpect(jsonPath("$.details.specialistCode").value("Specialist code is required"))
                .andExpect(jsonPath("$.details.type").value("Treatment type is required"))
                .andExpect(jsonPath("$.details.description").value("Description is required"));

        verify(service, never()).register(any());
    }

    @Test
    void shouldReturn404WhenAnimalDoesNotExist() throws Exception {
        when(service.register(any(CreateTreatmentRequest.class)))
                .thenThrow(new ResourceNotFoundException("Animal not found: AN-999"));

        mockMvc.perform(post("/api/treatments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "animalCode": "AN-999",
                                  "specialistCode": "SPEC-001",
                                  "performedAt": "2026-08-21T09:30:00",
                                  "type": "WOUND_CARE",
                                  "description": "Cleaning and evaluation of left front flipper injury."
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Animal not found: AN-999"))
                .andExpect(jsonPath("$.details").isMap());
    }

    @Test
    void shouldReturn409WhenBusinessRuleIsViolated() throws Exception {
        when(service.register(any(CreateTreatmentRequest.class)))
                .thenThrow(new BusinessRuleException("Released animals cannot receive treatments"));

        mockMvc.perform(post("/api/treatments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "animalCode": "AN-2026-001",
                                  "specialistCode": "SPEC-001",
                                  "performedAt": "2026-08-21T09:30:00",
                                  "type": "WOUND_CARE",
                                  "description": "Cleaning and evaluation of left front flipper injury."
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Released animals cannot receive treatments"));
    }

    // ------------------------------------------------------------------
    // GET /api/animals/{animalCode}/treatments — movido aquí por decisión del grupo
    // ------------------------------------------------------------------

    @Test
    void shouldReturnAnimalTreatments() throws Exception {
        TreatmentResponse treatment1 = new TreatmentResponse(
                100L, "AN-001", "SPEC-001",
                LocalDateTime.of(2026, 8, 21, 9, 0), TreatmentType.WOUND_CARE,
                "Cleaning and treatment of flipper injury."
        );
        TreatmentResponse treatment2 = new TreatmentResponse(
                101L, "AN-001", "SPEC-002",
                LocalDateTime.of(2026, 8, 22, 10, 0), TreatmentType.HYDRATION,
                "Rehydration therapy."
        );

        when(service.findByAnimalCode("AN-001")).thenReturn(List.of(treatment1, treatment2));

        mockMvc.perform(get("/api/animals/{animalCode}/treatments", "AN-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[1].id").value(101));

        verify(service).findByAnimalCode("AN-001");
    }

    @Test
    void shouldReturnEmptyListWhenAnimalHasNoTreatments() throws Exception {
        when(service.findByAnimalCode("AN-002")).thenReturn(List.of());

        mockMvc.perform(get("/api/animals/{animalCode}/treatments", "AN-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).findByAnimalCode("AN-002");
    }

}