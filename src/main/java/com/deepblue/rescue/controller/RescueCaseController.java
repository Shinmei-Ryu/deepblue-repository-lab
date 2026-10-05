package com.deepblue.rescue.controller;

import com.deepblue.rescue.dto.response.RescueCaseResponse;
import com.deepblue.rescue.service.RescueCaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{caseCode}")
    public ResponseEntity<RescueCaseResponse>
    findByCode(
            @PathVariable String caseCode) {
        return ResponseEntity.ok(
                service.findByCode(caseCode)
        );
    }
}
