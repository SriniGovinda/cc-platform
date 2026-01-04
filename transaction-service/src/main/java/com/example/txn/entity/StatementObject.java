package com.example.txn.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="statement_objects", indexes = {
        @Index(name="idx_stmt_user", columnList = "user_id"),
        @Index(name="idx_stmt_key", columnList = "s3_key", unique = true)
})
public class StatementObject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="s3_key", nullable=false, unique=true, length=300)
    private String s3Key;

    @Column(nullable=false, length=200)
    private String originalFilename;

    @Column(nullable=false)
    private Instant uploadedAt = Instant.now();

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getS3Key() { return s3Key; }
    public String getOriginalFilename() { return originalFilename; }
    public Instant getUploadedAt() { return uploadedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
}
