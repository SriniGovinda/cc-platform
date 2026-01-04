package com.example.txn.export;

import com.example.txn.audit.AuditService;
import com.example.txn.entity.ExportJob;
import com.example.txn.repo.ExportJobRepository;
import com.example.txn.repo.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportJobServiceTest {

  @Mock ExportJobRepository jobRepo;
  @Mock TransactionRepository txnRepo;
  @Mock S3ExportService s3;
  @Mock AuditService audit;

  @Test
  void createJob_rejectsTooManyRows() {
    ExportJobService svc = new ExportJobService(jobRepo, txnRepo, s3, audit, 10, 5);

    when(txnRepo.countSearch(anyLong(), any(), any(), any(), any())).thenReturn(11L);

    var ex = assertThrows(IllegalArgumentException.class,
        () -> svc.createJob(1L, null, LocalDate.of(2025,1,1), LocalDate.of(2025,1,31), "SUCCESS"));

    assertTrue(ex.getMessage().contains("Too many rows"));
    verify(jobRepo, never()).save(any());
    verify(audit, atLeastOnce()).log(eq(1L), eq("EXPORT_REQUEST"), any(), contains("rejected"), eq(false));
  }

  @Test
  void createJob_savesPendingJob() {
    ExportJobService svc = new ExportJobService(jobRepo, txnRepo, s3, audit, 50000, 1000);

    when(txnRepo.countSearch(anyLong(), any(), any(), any(), any())).thenReturn(5L);
    when(jobRepo.save(any())).thenAnswer(inv -> {
      ExportJob j = inv.getArgument(0);
      // emulate db id
      try {
        var f = ExportJob.class.getDeclaredField("jobId");
        f.setAccessible(true);
        f.set(j, 99L);
      } catch (Exception ignored) {}
      return j;
    });

    ExportJob job = svc.createJob(1L, 2L, LocalDate.of(2025,1,1), LocalDate.of(2025,1,2), "FAILED");
    assertEquals(ExportJob.Status.PENDING, job.getStatus());
    assertEquals(99L, job.getJobId());
    assertEquals("FAILED", job.getFilterStatus());

    ArgumentCaptor<ExportJob> cap = ArgumentCaptor.forClass(ExportJob.class);
    verify(jobRepo).save(cap.capture());
    assertEquals(1L, cap.getValue().getUserId());
  }
}
