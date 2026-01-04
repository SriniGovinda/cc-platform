package com.example.txn.controller;

import com.example.txn.entity.Transaction;
import com.example.txn.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/transactions")
public class InternalTransactionController {

    private final TransactionService service;
    public InternalTransactionController(TransactionService service) { this.service = service; }

    @PostMapping("/charge")
    public ResponseEntity<Transaction> charge(@RequestParam String sagaId,
                                              @RequestParam Long userId,
                                              @RequestParam Long cardId,
                                              @RequestParam Integer amountYen,
                                              @RequestParam String merchant,
                                              @RequestParam String category) {
        return ResponseEntity.ok(service.charge(sagaId, userId, cardId, amountYen, merchant, category));
    }

    @PostMapping("/reverse")
    public ResponseEntity<Void> reverse(@RequestParam String sagaId) {
        service.reverse(sagaId);
        return ResponseEntity.ok().build();
    }
}
