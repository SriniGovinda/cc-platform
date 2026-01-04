package com.example.account.controller;

import com.example.account.entity.Card;
import com.example.account.entity.Customer;
import com.example.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public ResponseEntity<Customer> me(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Email", required = false) String email
    ) {
        return ResponseEntity.ok(
                accountService.getOrCreateCustomer(
                        userId,
                        email == null ? "user" + userId : email
                )
        );
    }

    @GetMapping("/me/cards")
    public ResponseEntity<List<Card>> myCards(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Email", required = false) String email
    ) {
        Customer c = accountService.getOrCreateCustomer(
                userId,
                email == null ? "user" + userId : email
        );
        return ResponseEntity.ok(accountService.getCards(c.getCustomerId()));
    }

    @PostMapping("/me/cards")
    public ResponseEntity<Card> addCard(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Email", required = false) String email
    ) {
        Customer c = accountService.getOrCreateCustomer(
                userId,
                email == null ? "user" + userId : email
        );
        return ResponseEntity.ok(accountService.provisionNewCard(c.getCustomerId()));
    }
}
