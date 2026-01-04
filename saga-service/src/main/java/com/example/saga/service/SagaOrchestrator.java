package com.example.saga.service;

import com.example.saga.dto.DebitRequest;
import com.example.saga.dto.SagaResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class SagaOrchestrator {

    private final RestClient accountClient;
    private final RestClient transactionClient;

    public SagaOrchestrator(RestClient.Builder builder) {
        this.accountClient = builder
                .baseUrl("http://account-service")
                .build();

        this.transactionClient = builder
                .baseUrl("http://transaction-service")
                .build();
    }

    public SagaResult debit(Long userId, String email, DebitRequest request) {

        String sagaId = UUID.randomUUID().toString();

        try {
            // 1️⃣ Reserve / debit account
            debitAccount(userId, sagaId, request);

            // 2️⃣ Create transaction
            createTransaction(userId, sagaId, email, request);

            // 3️⃣ Simulate failure (for testing rollback)
            if (request.simulateFailureAfterCharge()) {
                throw new RuntimeException("Simulated failure after charge");
            }

            return new SagaResult(
                    sagaId,
                    "SUCCESS",
                    "Debit completed successfully"
            );

        } catch (Exception ex) {

            // 4️⃣ Compensation
            refundAccount(userId, sagaId, request);

            return new SagaResult(
                    sagaId,
                    "FAILED",
                    "Debit failed and rolled back"
            );
        }
    }

    /* ================== Saga Steps ================== */

    private void debitAccount(Long userId, String sagaId, DebitRequest req) {
        accountClient.post()
                .uri("/api/accounts/{cardId}/debit", req.cardId())
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Saga-Id", sagaId)
                .body(req.amountYen())
                .retrieve()
                .toBodilessEntity();
    }

    private void createTransaction(Long userId, String sagaId, String email, DebitRequest req) {
        transactionClient.post()
                .uri("/api/transactions")
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Email", email)
                .header("X-Saga-Id", sagaId)
                .body(req)
                .retrieve()
                .toBodilessEntity();
    }

    private void refundAccount(Long userId, String sagaId, DebitRequest req) {
        accountClient.post()
                .uri("/api/accounts/{cardId}/refund", req.cardId())
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Saga-Id", sagaId)
                .body(req.amountYen())
                .retrieve()
                .toBodilessEntity();
    }
}
