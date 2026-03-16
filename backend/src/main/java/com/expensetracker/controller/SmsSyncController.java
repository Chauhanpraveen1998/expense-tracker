package com.expensetracker.controller;

import com.expensetracker.dto.SmsTransactionRequest;
import com.expensetracker.dto.SmsTransactionResponse;
import com.expensetracker.entity.User;
import com.expensetracker.service.SmsSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsSyncController {

    private final SmsSyncService smsSyncService;

    @PostMapping("/sync")
    public ResponseEntity<SmsTransactionResponse> syncSmsTransaction(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SmsTransactionRequest request) {
        return ResponseEntity.ok(smsSyncService.processSmsTransaction(user.getId(), request));
    }

    @PostMapping("/sync/batch")
    public ResponseEntity<List<SmsTransactionResponse>> syncBatchSmsTransactions(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody List<SmsTransactionRequest> requests) {
        return ResponseEntity.ok(smsSyncService.processBatchSmsTransactions(user.getId(), requests));
    }
}
