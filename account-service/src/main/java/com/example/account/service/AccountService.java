package com.example.account.service;

import com.example.account.entity.Card;
import com.example.account.entity.Customer;
import com.example.account.entity.LimitReservation;
import com.example.account.repo.CardRepository;
import com.example.account.repo.CustomerRepository;
import com.example.account.repo.LimitReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class AccountService {

    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final LimitReservationRepository reservationRepository;

    public AccountService(CustomerRepository customerRepository, CardRepository cardRepository, LimitReservationRepository reservationRepository) {
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Customer getOrCreateCustomer(Long userId, String email) {
        return customerRepository.findByUserId(userId).orElseGet(() -> {
            Customer c = new Customer();
            c.setUserId(userId);
            c.setFullName(email.split("@")[0]);
            Customer saved = customerRepository.save(c);

            // provision one starter card
            Card card = new Card();
            card.setCustomer(saved);
            card.setLast4(String.format("%04d", new Random().nextInt(10000)));
            cardRepository.save(card);

            return saved;
        });
    }

    public List<Card> getCards(Long customerId) {
        return cardRepository.findByCustomer_CustomerId(customerId);
    }

    @Transactional
    public Card provisionNewCard(Long customerId) {
        Customer c = customerRepository.findById(customerId).orElseThrow();
        Card card = new Card();
        card.setCustomer(c);
        card.setLast4(String.format("%04d", new Random().nextInt(10000)));
        return cardRepository.save(card);
    }

    public void validateCardOwnership(Long customerId, Long cardId) {
        cardRepository.findByCardIdAndCustomer_CustomerId(cardId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Card does not belong to customer")); 
    }

    @Transactional
    public void reserveLimit(String sagaId, Long customerId, Integer amountYen) {
        if (reservationRepository.findBySagaId(sagaId).isPresent()) return; // idempotent
        LimitReservation r = new LimitReservation();
        r.setSagaId(sagaId);
        r.setCustomerId(customerId);
        r.setAmountYen(amountYen);
        r.setStatus("RESERVED");
        reservationRepository.save(r);
    }

    @Transactional
    public void releaseLimit(String sagaId) {
        LimitReservation r = reservationRepository.findBySagaId(sagaId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown sagaId")); 
        r.setStatus("RELEASED");
        reservationRepository.save(r);
    }
}
