package com.deepblue.rescue.controller;

import com.deepblue.rescue.dto.response.AnimalResponse;
import com.deepblue.rescue.service.AnimalService;
import com.deepblue.rescue.service.TreatmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(
            AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/{animalCode}")
    public ResponseEntity<AnimalResponse>
    findByCode(
            @PathVariable String animalCode) {
        return ResponseEntity.ok(
                animalService.findByCode(animalCode)
        );
    }

    @GetMapping("/in-rehabilitation")
    public ResponseEntity<List<AnimalResponse>>
    findAnimalsInRehabilitation() {
        return ResponseEntity.ok(
                animalService
                        .findAnimalsInRehabilitation()
        );
    }


}
