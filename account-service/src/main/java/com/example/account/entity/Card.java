package com.example.account.entity;

import jakarta.persistence.*;

@Entity
@Table(name="cards", indexes = {
        @Index(name="idx_cards_customer_id", columnList = "customer_id"),
        @Index(name="idx_cards_last4", columnList = "last4")
})
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @ManyToOne(optional=false)
    @JoinColumn(name="customer_id")
    private Customer customer;

    @Column(nullable=false, length=4)
    private String last4;

    @Column(nullable=false, length=30)
    private String brand = "VISA";

    @Column(nullable=false, length=30)
    private String status = "ACTIVE";

    public Long getCardId() { return cardId; }
    public Customer getCustomer() { return customer; }
    public String getLast4() { return last4; }
    public String getBrand() { return brand; }
    public String getStatus() { return status; }

    public void setCardId(Long cardId) { this.cardId = cardId; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setLast4(String last4) { this.last4 = last4; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setStatus(String status) { this.status = status; }
}
