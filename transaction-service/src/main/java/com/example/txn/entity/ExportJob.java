package com.example.txn.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "export_jobs")
public class ExportJob {

  public enum Status { PENDING, RUNNING, COMPLETED, FAILED }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "job_id")
  private Long jobId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "card_id")
  private Long cardId;

  @Column(name = "from_date", nullable = false)
  private LocalDate fromDate;

  @Column(name = "to_date", nullable = false)
  private LocalDate toDate;

  @Column(name = "filter_status", length = 20)
  private String filterStatus;

  @Column(name = "status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private Status status = Status.PENDING;

  @Column(name = "record_count")
  private Integer recordCount;

  @Column(name = "s3_key", length = 512)
  private String s3Key;

  @Column(name = "error_message", length = 1000)
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "completed_at")
  private OffsetDateTime completedAt;

  public Long getJobId() { return jobId; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public Long getCardId() { return cardId; }
  public void setCardId(Long cardId) { this.cardId = cardId; }
  public LocalDate getFromDate() { return fromDate; }
  public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
  public LocalDate getToDate() { return toDate; }
  public void setToDate(LocalDate toDate) { this.toDate = toDate; }
  public String getFilterStatus() { return filterStatus; }
  public void setFilterStatus(String filterStatus) { this.filterStatus = filterStatus; }
  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
  public Integer getRecordCount() { return recordCount; }
  public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }
  public String getS3Key() { return s3Key; }
  public void setS3Key(String s3Key) { this.s3Key = s3Key; }
  public String getErrorMessage() { return errorMessage; }
  public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public OffsetDateTime getCompletedAt() { return completedAt; }
  public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }
}
