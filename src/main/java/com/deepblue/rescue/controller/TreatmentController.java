package com.deepblue.rescue.controller;

import com.deepblue.rescue.service.TreatmentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
    private final TreatmentService service;
    public TreatmentController(
            TreatmentService service) {
        this.service = service;
    }
}
