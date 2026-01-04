package com.example.txn.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="transactions", indexes = {
        @Index(name="idx_txn_user_date", columnList = "user_id, txn_date"),
        @Index(name="idx_txn_card_date", columnList = "card_id, txn_date")
})
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnId;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="card_id", nullable=false)
    private Long cardId;

    @Column(nullable=false)
    private Integer amountYen;

    @Column(nullable=false, length=30)
    private String status = "SUCCESS"; // SUCCESS/FAILED/REVERSED

    @Column(nullable=false)
    private Instant txnDate = Instant.now();

    @Column(nullable=false, length=200)
    private String merchantName;

    @Column(nullable=false, length=50)
    private String category;

    @Column(name="saga_id", length=80, unique = true)
    private String sagaId;

    public Long getTxnId() { return txnId; }
    public Long getUserId() { return userId; }
    public Long getCardId() { return cardId; }
    public Integer getAmountYen() { return amountYen; }
    public String getStatus() { return status; }
    public Instant getTxnDate() { return txnDate; }
    public String getMerchantName() { return merchantName; }
    public String getCategory() { return category; }
    public String getSagaId() { return sagaId; }

    public void setTxnId(Long txnId) { this.txnId = txnId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public void setAmountYen(Integer amountYen) { this.amountYen = amountYen; }
    public void setStatus(String status) { this.status = status; }
    public void setTxnDate(Instant txnDate) { this.txnDate = txnDate; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public void setCategory(String category) { this.category = category; }
    public void setSagaId(String sagaId) { this.sagaId = sagaId; }
}
