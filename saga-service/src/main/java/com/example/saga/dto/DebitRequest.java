package com.example.saga.dto;

public record DebitRequest(
        Long cardId,
        Integer amountYen,
        String merchant,
        String category,
        boolean simulateFailureAfterCharge
) {}
