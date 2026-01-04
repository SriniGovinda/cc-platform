package com.example.txn.repo;

import com.example.txn.entity.ExportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExportJobRepository extends JpaRepository<ExportJob, Long> {
  List<ExportJob> findTop10ByStatusOrderByCreatedAtAsc(ExportJob.Status status);
}
