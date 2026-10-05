package com.deepblue.rescue.controller;

import com.deepblue.rescue.service.AnimalService;
import com.deepblue.rescue.service.TreatmentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {
    private final AnimalService animalService;
    private final TreatmentService
            treatmentService;
    public AnimalController(
            AnimalService animalService,
            TreatmentService treatmentService) {
        this.animalService = animalService;
        this.treatmentService = treatmentService;
    }
}
