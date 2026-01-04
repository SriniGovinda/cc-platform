package com.example.account.entity;

import jakarta.persistence.*;

@Entity
@Table(name="customers", indexes = @Index(name="idx_customers_user_id", columnList = "user_id", unique = true))
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name="user_id", nullable=false, unique=true)
    private Long userId;

    @Column(nullable=false, length=200)
    private String fullName;

    @Column(nullable=false)
    private Integer monthlyLimitYen = 200000;

    public Long getCustomerId() { return customerId; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public Integer getMonthlyLimitYen() { return monthlyLimitYen; }

    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setMonthlyLimitYen(Integer monthlyLimitYen) { this.monthlyLimitYen = monthlyLimitYen; }
}
