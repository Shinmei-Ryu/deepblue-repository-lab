package com.deepblue.rescue.controller;

import com.deepblue.rescue.dto.request.CreateTreatmentRequest;
import com.deepblue.rescue.dto.response.TreatmentResponse;
import com.deepblue.rescue.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TreatmentController {
    private final TreatmentService treatmentService;
    public TreatmentController(
            TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @PostMapping("/treatments")
    public ResponseEntity<TreatmentResponse>
    register(
            @Valid
            @RequestBody
            CreateTreatmentRequest request) {
        TreatmentResponse response =
                treatmentService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping("/animals/{animalCode}/treatments")
    public ResponseEntity<List<TreatmentResponse>>
    findTreatments(
            @PathVariable String animalCode) {
        return ResponseEntity.ok(
                treatmentService
                        .findByAnimalCode(animalCode)
        );
    }

}
