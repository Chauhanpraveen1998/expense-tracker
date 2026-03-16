package com.expensetracker.service;

import com.expensetracker.dto.AccountRequest;
import com.expensetracker.dto.AccountResponse;
import com.expensetracker.dto.AccountSummaryResponse;
import com.expensetracker.entity.Account;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.AccountRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountSummaryResponse getAccountSummary(UUID userId) {
        List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
        BigDecimal totalBalance = accountRepository.getTotalBalanceByUserId(userId);
        Long accountCount = accountRepository.countActiveByUserId(userId);

        return AccountSummaryResponse.builder()
                .totalBalance(totalBalance)
                .accountCount(accountCount)
                .accounts(accounts.stream()
                        .map(AccountResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<AccountResponse> getAllAccounts(UUID userId) {
        return accountRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(AccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(UUID userId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return AccountResponse.fromEntity(account);
    }

    @Transactional
    public AccountResponse createAccount(UUID userId, AccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = Account.builder()
                .user(user)
                .name(request.getName())
                .type(request.getType())
                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                .bankName(request.getBankName())
                .lastFourDigits(request.getLastFourDigits())
                .colorPrimary(request.getColorPrimary())
                .colorSecondary(request.getColorSecondary())
                .isActive(true)
                .build();

        Account savedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(savedAccount);
    }

    @Transactional
    public AccountResponse updateAccount(UUID userId, UUID accountId, AccountRequest request) {
        Account account = accountRepository.findById(accountId)
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setName(request.getName());
        account.setType(request.getType());
        account.setBalance(request.getBalance());
        account.setBankName(request.getBankName());
        account.setLastFourDigits(request.getLastFourDigits());
        account.setColorPrimary(request.getColorPrimary());
        account.setColorSecondary(request.getColorSecondary());

        Account updatedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(updatedAccount);
    }

    @Transactional
    public void deleteAccount(UUID userId, UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        account.setIsActive(false);
        accountRepository.save(account);
    }

    @Transactional
    public AccountResponse updateBalance(UUID userId, UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .filter(a -> a.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setBalance(amount);
        Account updatedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(updatedAccount);
    }
}
