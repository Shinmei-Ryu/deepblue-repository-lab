package com.deepblue.rescue.controller;

import com.deepblue.rescue.service.RescueCaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rescue-cases")
public class RescueCaseController {
    private final RescueCaseService service;
    public RescueCaseController(
            RescueCaseService service) {
        this.service = service;
    }
}
