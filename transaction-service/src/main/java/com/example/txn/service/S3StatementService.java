package com.example.txn.service;

import com.example.txn.entity.StatementObject;
import com.example.txn.repo.StatementObjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class S3StatementService {

    private final StatementObjectRepository repo;
    private final S3Client s3Client;
    private final String bucket;

    public S3StatementService(
            StatementObjectRepository repo,
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket
    ) {
        this.repo = repo;
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @Transactional
    public StatementObject upload(Long userId, String filename, byte[] bytes) {

        String key = "statements/" + userId + "/" + UUID.randomUUID() + "_" + filename;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("text/csv")
                        .build(),
                RequestBody.fromBytes(bytes)
        );

        StatementObject obj = new StatementObject();
        obj.setUserId(userId);
        obj.setS3Key(key);
        obj.setOriginalFilename(filename);

        return repo.save(obj);
    }

    public byte[] download(Long userId, Long objectId) {

        StatementObject obj = repo.findByIdAndUserId(objectId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        return s3Client
                .getObjectAsBytes(
                        GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(obj.getS3Key())
                                .build()
                )
                .asByteArray();
    }
}
