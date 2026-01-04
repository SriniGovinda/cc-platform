package com.example.saga.dto;

public record SagaResult(
        String sagaId,
        String status,
        String message
) {}
