package com.example.account.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="limit_reservations", indexes = {
        @Index(name="idx_limit_res_customer", columnList = "customer_id"),
        @Index(name="idx_limit_res_saga", columnList = "saga_id", unique = true)
})
public class LimitReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="saga_id", nullable=false, unique=true, length=80)
    private String sagaId;

    @Column(name="customer_id", nullable=false)
    private Long customerId;

    @Column(nullable=false)
    private Integer amountYen;

    @Column(nullable=false, length=30)
    private String status = "RESERVED"; // RESERVED/RELEASED

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getSagaId() { return sagaId; }
    public Long getCustomerId() { return customerId; }
    public Integer getAmountYen() { return amountYen; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setAmountYen(Integer amountYen) { this.amountYen = amountYen; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
