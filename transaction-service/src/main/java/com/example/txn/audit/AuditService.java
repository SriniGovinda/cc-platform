package com.example.txn.audit;

import com.example.txn.entity.AuditLog;
import com.example.txn.repo.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

  private final AuditLogRepository repo;

  public AuditService(AuditLogRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public void log(Long userId, String action, String resource, String details, boolean success) {
    repo.save(new AuditLog(userId, action, resource, details, success));
  }
}
