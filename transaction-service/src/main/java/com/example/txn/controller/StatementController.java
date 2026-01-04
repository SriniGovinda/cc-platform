package com.example.txn.controller;

import com.example.txn.entity.StatementObject;
import com.example.txn.service.S3StatementService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final S3StatementService service;
    public StatementController(S3StatementService service) { this.service = service; }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StatementObject> upload(@RequestHeader("X-User-Id") Long userId,
                                                  @RequestPart("file") MultipartFile file) throws Exception {
        StatementObject saved = service.upload(userId, file.getOriginalFilename(), file.getBytes());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@RequestHeader("X-User-Id") Long userId,
                                           @PathVariable Long id) {
        byte[] bytes = service.download(userId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statement_" + id + ".csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}
