package com.deepblue.rescue.controller;


import com.deepblue.rescue.domain.RescueStatus;
import com.deepblue.rescue.dto.request.ChangeRescueStatusRequest;
import com.deepblue.rescue.dto.response.RescueCaseResponse;
import com.deepblue.rescue.exception.GlobalExceptionHandler;
import com.deepblue.rescue.exception.ResourceNotFoundException;
import com.deepblue.rescue.service.RescueCaseService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        RescueCaseController.class
)
@Import(
        GlobalExceptionHandler.class
)
class RescueCaseControllerTest {

    @MockitoBean
    private RescueCaseService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRescueCaseByCode()
            throws Exception {
        RescueCaseResponse response =
                new RescueCaseResponse(
                        1L,
                        "RES-2026-001",
                        LocalDate.of(
                                2026,
                                8,
                                20
                        ),
                        "Bahia Concha",
                        RescueStatus
                                .IN_REHABILITATION,
                        "DB-CAR",
                        "AN-2026-001"
                );
        when(
                service.findByCode(
                        "RES-2026-001"
                )
        ).thenReturn(response);
        mockMvc.perform(
                        get(
                                "/api/rescue-cases/{code}",
                                "RES-2026-001"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.caseCode")
                                .value("RES-2026-001")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        "IN_REHABILITATION"
                                )
                );
        verify(service)
                .findByCode(
                        "RES-2026-001"
                );
    }

    @Test
    void shouldReturn404WhenCaseDoesNotExist()
            throws Exception {
        when(
                service.findByCode("RES-999")
        ).thenThrow(
                new ResourceNotFoundException(
                        "Rescue case not found: RES-999"
                )
        );
        mockMvc.perform(
                        get(
                                "/api/rescue-cases/{code}",
                                "RES-999"
                        )
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Rescue case not found: RES-999"
                                )
                )
                .andExpect(
                        jsonPath("$.details")
                                .isMap()
                );
    }

    @Test
    void shouldReturnCasesByStatus() throws Exception {
        // ARRANGE
        when(service.findByStatus(RescueStatus.IN_REHABILITATION)).thenReturn(List.of(
                rescueCase("RES-2026-001", RescueStatus.IN_REHABILITATION),
                rescueCase("RES-2026-002", RescueStatus.IN_REHABILITATION)));

        // ACT + ASSERT
        mockMvc.perform(get("/api/rescue-cases").param("status", "IN_REHABILITATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].caseCode").value("RES-2026-001"))
                .andExpect(jsonPath("$[0].status").value("IN_REHABILITATION"))
                .andExpect(jsonPath("$[1].caseCode").value("RES-2026-002"))
                .andExpect(jsonPath("$[1].status").value("IN_REHABILITATION"));

        verify(service).findByStatus(RescueStatus.IN_REHABILITATION);
    }

    @Test
    void shouldReturn400WhenStatusQueryParamIsInvalid() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(get("/api/rescue-cases").param("status", "FLYING"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.details.status").exists());

        verify(service, never()).findByStatus(any(RescueStatus.class));
    }

    @Test
    void shouldChangeStatus() throws Exception {
        // ARRANGE
        when(service.changeStatus(eq("RES-001"), any(ChangeRescueStatusRequest.class)))
                .thenReturn(rescueCase("RES-001", RescueStatus.READY_FOR_RELEASE));

        // ACT + ASSERT
        mockMvc.perform(patch("/api/rescue-cases/{code}/status", "RES-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "READY_FOR_RELEASE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseCode").value("RES-001"))
                .andExpect(jsonPath("$.status").value("READY_FOR_RELEASE"));

        ArgumentCaptor<ChangeRescueStatusRequest> captor =
                ArgumentCaptor.forClass(ChangeRescueStatusRequest.class);
        verify(service).changeStatus(eq("RES-001"), captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(RescueStatus.READY_FOR_RELEASE);
    }

    @Test
    void shouldReturn400WhenStatusIsMissing() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(patch("/api/rescue-cases/{code}/status", "RES-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.details.status").value("Status is required"));

        verify(service, never()).changeStatus(anyString(), any());
    }

    @Test
    void shouldReturn400WhenStatusIsNull() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(patch("/api/rescue-cases/{code}/status", "RES-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": null
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.details.status").value("Status is required"));

        verify(service, never()).changeStatus(anyString(), any());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private RescueCaseResponse rescueCase(String caseCode, RescueStatus status) {
        return new RescueCaseResponse(
                1L,
                caseCode,
                LocalDate.of(2026, 8, 20),
                "Bahia Concha",
                status,
                "DB-CAR",
                "AN-2026-001");
    }



}


