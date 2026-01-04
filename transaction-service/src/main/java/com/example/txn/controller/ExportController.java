package com.example.txn.controller;

import com.example.txn.audit.AuditService;
import com.example.txn.entity.ExportJob;
import com.example.txn.export.ExportJobService;
import com.example.txn.export.S3ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/exports")
public class ExportController {

    private final ExportJobService exportJobService;
    private final S3ExportService s3;
    private final AuditService audit;

    public ExportController(
            ExportJobService exportJobService,
            S3ExportService s3,
            AuditService audit
    ) {
        this.exportJobService = exportJobService;
        this.s3 = s3;
        this.audit = audit;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createExport(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate,
            @RequestParam(required = false) Long cardId,
            @RequestParam(required = false) String status
    ) {
        ExportJob job = exportJobService.createJob(
                userId, cardId, fromDate, toDate, status
        );

        return ResponseEntity.accepted().body(Map.of(
                "jobId", job.getJobId(),
                "status", job.getStatus().name()
        ));
    }

    @GetMapping("/{jobId}")
    public ExportJob getStatus(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long jobId
    ) {
        ExportJob job = exportJobService.getJob(jobId);
        if (!job.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Forbidden");
        }
        return job;
    }

    @GetMapping("/{jobId}/download")
    public ResponseEntity<StreamingResponseBody> download(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long jobId
    ) {
        ExportJob job = exportJobService.getJob(jobId);

        if (!job.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Forbidden");
        }
        if (job.getStatus() != ExportJob.Status.COMPLETED) {
            throw new IllegalArgumentException("Job not completed");
        }

        audit.log(
                userId,
                "EXPORT_DOWNLOAD",
                "export_jobs",
                "jobId=" + jobId,
                true
        );

        ResponseInputStream<GetObjectResponse> in =
                s3.openDownloadStream(job.getS3Key());

        StreamingResponseBody body = outputStream -> {
            try (in) {
                in.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=transactions_export_" + jobId + ".csv"
                )
                .contentType(MediaType.valueOf("text/csv"))
                .body(body);
    }
}
