package com.deepblue.rescue.controller;

import com.deepblue.rescue.domain.AnimalSex;
import com.deepblue.rescue.domain.RescueStatus;
import com.deepblue.rescue.dto.response.AnimalResponse;
import com.deepblue.rescue.exception.GlobalExceptionHandler;
import com.deepblue.rescue.exception.ResourceNotFoundException;
import com.deepblue.rescue.service.AnimalService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
@Import(GlobalExceptionHandler.class)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnimalService animalService;

    // ------------------------------------------------------------------
    // GET /api/animals/{animalCode}
    // ------------------------------------------------------------------

    @Test
    void shouldReturnAnimalByCode() throws Exception {
        AnimalResponse response = new AnimalResponse(
                1L, "AN-001", "Green Sea Turtle", "Chelonia mydas",
                AnimalSex.FEMALE, "RES-001", RescueStatus.IN_REHABILITATION
        );

        when(animalService.findByCode("AN-001")).thenReturn(response);

        mockMvc.perform(get("/api/animals/{animalCode}", "AN-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalCode").value("AN-001"))
                .andExpect(jsonPath("$.commonName").value("Green Sea Turtle"))
                .andExpect(jsonPath("$.rescueStatus").value("IN_REHABILITATION"));

        verify(animalService).findByCode("AN-001");
    }

    @Test
    void shouldReturn404WhenAnimalDoesNotExist() throws Exception {
        when(animalService.findByCode("AN-999"))
                .thenThrow(new ResourceNotFoundException("Animal not found: AN-999"));

        mockMvc.perform(get("/api/animals/{animalCode}", "AN-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Animal not found: AN-999"))
                .andExpect(jsonPath("$.details").isMap());
    }

    // ------------------------------------------------------------------
    // GET /api/animals/in-rehabilitation
    // ------------------------------------------------------------------

    @Test
    void shouldReturnAnimalsInRehabilitation() throws Exception {
        AnimalResponse turtle = new AnimalResponse(
                1L, "AN-001", "Green Sea Turtle", "Chelonia mydas",
                AnimalSex.FEMALE, "RES-001", RescueStatus.IN_REHABILITATION
        );
        AnimalResponse dolphin = new AnimalResponse(
                2L, "AN-002", "Bottlenose Dolphin", "Tursiops truncatus",
                AnimalSex.MALE, "RES-002", RescueStatus.IN_REHABILITATION
        );

        when(animalService.findAnimalsInRehabilitation()).thenReturn(List.of(turtle, dolphin));

        mockMvc.perform(get("/api/animals/in-rehabilitation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].animalCode").value("AN-001"))
                .andExpect(jsonPath("$[1].animalCode").value("AN-002"));

        verify(animalService).findAnimalsInRehabilitation();
    }

    @Test
    void shouldReturnEmptyListWhenNoAnimalsInRehabilitation() throws Exception {
        when(animalService.findAnimalsInRehabilitation()).thenReturn(List.of());

        mockMvc.perform(get("/api/animals/in-rehabilitation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ------------------------------------------------------------------
    // GET /api/animals/{animalCode}/treatment-eligibility
    // ------------------------------------------------------------------

    @Test
    void shouldReturnTreatmentEligibilityTrue() throws Exception {
        when(animalService.canReceiveTreatment("AN-001")).thenReturn(true);

        mockMvc.perform(get("/api/animals/{animalCode}/treatment-eligibility", "AN-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalCode").value("AN-001"))
                .andExpect(jsonPath("$.eligible").value(true));

        verify(animalService).canReceiveTreatment("AN-001");
    }

    @Test
    void shouldReturnTreatmentEligibilityFalse() throws Exception {
        when(animalService.canReceiveTreatment("AN-002")).thenReturn(false);

        mockMvc.perform(get("/api/animals/{animalCode}/treatment-eligibility", "AN-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(false));

        verify(animalService).canReceiveTreatment("AN-002");
    }

    @Test
    void shouldReturn404WhenCheckingEligibilityForMissingAnimal() throws Exception {
        when(animalService.canReceiveTreatment("AN-999"))
                .thenThrow(new ResourceNotFoundException("Animal not found: AN-999"));

        mockMvc.perform(get("/api/animals/{animalCode}/treatment-eligibility", "AN-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Animal not found: AN-999"));
    }
}