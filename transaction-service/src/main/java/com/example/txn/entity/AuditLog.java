package com.example.txn.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "audit_id")
  private Long auditId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "action", nullable = false, length = 50)
  private String action;

  @Column(name = "resource", length = 100)
  private String resource;

  @Column(name = "details", length = 2000)
  private String details;

  @Column(name = "success", nullable = false)
  private boolean success;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  public AuditLog() {}

  public AuditLog(Long userId, String action, String resource, String details, boolean success) {
    this.userId = userId;
    this.action = action;
    this.resource = resource;
    this.details = details;
    this.success = success;
    this.createdAt = OffsetDateTime.now();
  }

  public Long getAuditId() { return auditId; }
  public Long getUserId() { return userId; }
  public String getAction() { return action; }
  public String getResource() { return resource; }
  public String getDetails() { return details; }
  public boolean isSuccess() { return success; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
}
