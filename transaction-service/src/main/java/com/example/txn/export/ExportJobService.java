package com.example.txn.export;

import com.example.txn.audit.AuditService;
import com.example.txn.entity.ExportJob;
import com.example.txn.entity.Transaction;
import com.example.txn.repo.ExportJobRepository;
import com.example.txn.repo.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ExportJobService {

	private final ExportJobRepository jobRepo;
	private final TransactionRepository txnRepo;
	private final S3ExportService s3;
	private final AuditService audit;

	private final int maxRows;
	private final int pageSize;

	public ExportJobService(ExportJobRepository jobRepo, TransactionRepository txnRepo, S3ExportService s3,
			AuditService audit, @Value("${export.maxRows:50000}") int maxRows,
			@Value("${export.pageSize:1000}") int pageSize) {
		this.jobRepo = jobRepo;
		this.txnRepo = txnRepo;
		this.s3 = s3;
		this.audit = audit;
		this.maxRows = maxRows;
		this.pageSize = pageSize;
	}

	public ExportJob createJob(Long userId, Long cardId, LocalDate fromDate, LocalDate toDate, String status) {
		validateDates(fromDate, toDate);

		Instant from = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant to = toDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1);

		long count = txnRepo.countSearch(userId, from, to, cardId, status);
		if (count > maxRows) {
			audit.log(userId, "EXPORT_REQUEST", "transactions", "rejected: too many rows=" + count, false);
			throw new IllegalArgumentException("Too many rows for export (" + count + "). Max is " + maxRows);
		}

		ExportJob job = new ExportJob();
		job.setUserId(userId);
		job.setCardId(cardId);
		job.setFromDate(fromDate);
		job.setToDate(toDate);
		job.setFilterStatus(status);
		job.setStatus(ExportJob.Status.PENDING);

		ExportJob saved = jobRepo.save(job);
		audit.log(userId, "EXPORT_REQUEST", "export_jobs", "jobId=" + saved.getJobId() + ", rows=" + count, true);
		return saved;
	}

	public ExportJob getJob(Long jobId) {
		return jobRepo.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
	}

	@Transactional
	@Scheduled(fixedDelayString = "${export.workerDelayMs:5000}")
	public void processPendingJobs() {
		List<ExportJob> jobs = jobRepo.findTop10ByStatusOrderByCreatedAtAsc(ExportJob.Status.PENDING);
		for (ExportJob job : jobs) {
			processOne(job.getJobId());
		}
	}

	@Transactional
	public void processOne(Long jobId) {
		ExportJob job = jobRepo.findById(jobId).orElse(null);
		if (job == null || job.getStatus() != ExportJob.Status.PENDING)
			return;

		job.setStatus(ExportJob.Status.RUNNING);
		jobRepo.save(job);

		File tmp = null;
		try {
			Instant from = job.getFromDate().atStartOfDay().toInstant(ZoneOffset.UTC);
			Instant to = job.getToDate().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusMillis(1);

			tmp = File.createTempFile("export_" + job.getJobId() + "_", ".csv");
			int total = writeCsv(tmp.toPath(), job.getUserId(), from, to, job.getCardId(), job.getFilterStatus());

			String key = "exports/" + job.getUserId() + "/" + job.getJobId() + "/" + UUID.randomUUID() + ".csv";
			s3.upload(tmp.toPath(), key);

			job.setS3Key(key);
			job.setRecordCount(total);
			job.setStatus(ExportJob.Status.COMPLETED);
			job.setCompletedAt(java.time.OffsetDateTime.now());
			jobRepo.save(job);

			audit.log(job.getUserId(), "EXPORT_COMPLETED", "export_jobs", "jobId=" + job.getJobId() + ", rows=" + total,
					true);
		} catch (Exception e) {
			job.setStatus(ExportJob.Status.FAILED);
			job.setErrorMessage(e.getMessage());
			job.setCompletedAt(java.time.OffsetDateTime.now());
			jobRepo.save(job);
			audit.log(job.getUserId(), "EXPORT_FAILED", "export_jobs",
					"jobId=" + job.getJobId() + ", error=" + e.getMessage(), false);
		} finally {
			if (tmp != null)
				tmp.delete();
		}
	}

	private int writeCsv(Path path, Long userId, Instant from, Instant to, Long cardId, String status)
			throws Exception {
		int page = 0;
		int total = 0;
		DateTimeFormatter df = DateTimeFormatter.ISO_INSTANT;

		try (BufferedWriter w = new BufferedWriter(
				new FileWriter(path.toFile(), java.nio.charset.StandardCharsets.UTF_8))) {

			w.write("TXN_ID,CARD_ID,AMOUNT_YEN,STATUS,TXN_DATE,MERCHANT_NAME,CATEGORY");
			w.newLine();

			while (true) {
				List<Transaction> batch = txnRepo.search(userId, from, to, cardId, status,
						PageRequest.of(page, pageSize));

				if (batch.isEmpty())
					break;

				for (Transaction t : batch) {
					w.write(t.getTxnId() + "," + t.getCardId() + "," + t.getAmountYen() + "," + safe(t.getStatus())
							+ "," + df.format(t.getTxnDate()) + "," + safe(t.getMerchantName()) + ","
							+ safe(t.getCategory()));
					w.newLine();
					total++;
				}
				page++;
			}
		}
		return total;
	}

	private String safe(String s) {
		if (s == null)
			return "";
		return s.replaceAll("[\r\n]+", " ").replace(",", " ");
	}

	private void validateDates(LocalDate from, LocalDate to) {
		if (from == null || to == null)
			throw new IllegalArgumentException("fromDate/toDate required");
		if (from.isAfter(to))
			throw new IllegalArgumentException("fromDate must be <= toDate");
		if (from.isBefore(to.minusMonths(12))) { // max 12 months
			throw new IllegalArgumentException("Date range must be within 12 months");
		}
	}
}
