package com.example.saga.controller;

import com.example.saga.dto.DebitRequest;
import com.example.saga.dto.SagaResult;
import com.example.saga.service.SagaOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saga")
public class SagaController {

    private final SagaOrchestrator orchestrator;

    public SagaController(SagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/debit")
    public ResponseEntity<SagaResult> debit(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestBody DebitRequest request
    ) {
        String resolvedEmail = (email != null) ? email : "user" + userId;
        return ResponseEntity.ok(
                orchestrator.debit(userId, resolvedEmail, request)
        );
    }
}
