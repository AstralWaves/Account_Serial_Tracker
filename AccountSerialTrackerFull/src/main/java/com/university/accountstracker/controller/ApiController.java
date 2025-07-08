package com.university.accountstracker.controller;

import com.university.accountstracker.model.QueueSerials;
import com.university.accountstracker.service.SerialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SerialService serialService;

    @Autowired
    public ApiController(SerialService serialService) {
        this.serialService = serialService;
    }

    @GetMapping("/serials")
    public ResponseEntity<QueueSerials> getCurrentSerials() {
        QueueSerials currentSerials = serialService.getCurrentQueueSerials();
        return ResponseEntity.ok(currentSerials);
    }
}
