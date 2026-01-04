package com.example.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "app_users", indexes = {
        @Index(name="idx_app_users_email", columnList = "email", unique = true)
})
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=200)
    private String email;

    @Column(nullable=false, length=200)
    private String passwordHash;

    @Column(nullable=false, length=50)
    private String role = "CUSTOMER";

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
