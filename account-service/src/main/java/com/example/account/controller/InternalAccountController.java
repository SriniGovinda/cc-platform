package com.example.account.controller;

import com.example.account.entity.Customer;
import com.example.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/account")
public class InternalAccountController {

    private final AccountService accountService;
    public InternalAccountController(AccountService accountService) { this.accountService = accountService; }

    @PostMapping("/limit/reserve")
    public ResponseEntity<Void> reserve(@RequestParam String sagaId,
                                        @RequestParam Long userId,
                                        @RequestParam Integer amountYen,
                                        @RequestParam(required=false) String email) {
        Customer c = accountService.getOrCreateCustomer(userId, email == null ? "user"+userId : email);
        accountService.reserveLimit(sagaId, c.getCustomerId(), amountYen);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/limit/release")
    public ResponseEntity<Void> release(@RequestParam String sagaId) {
        accountService.releaseLimit(sagaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/card/validate")
    public ResponseEntity<Void> validateCard(@RequestParam Long userId, @RequestParam Long cardId, @RequestParam(required=false) String email) {
        Customer c = accountService.getOrCreateCustomer(userId, email == null ? "user"+userId : email);
        accountService.validateCardOwnership(c.getCustomerId(), cardId);
        return ResponseEntity.ok().build();
    }
}
