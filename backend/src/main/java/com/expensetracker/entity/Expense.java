package com.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expenses", indexes = {
    @Index(name = "idx_expense_user", columnList = "user_id"),
    @Index(name = "idx_expense_date", columnList = "date"),
    @Index(name = "idx_expense_category", columnList = "category_id"),
    @Index(name = "idx_expense_type", columnList = "transaction_type"),
    @Index(name = "idx_expense_merchant", columnList = "merchant_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer"})
    private Category category;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    @Builder.Default
    private TransactionType transactionType = TransactionType.EXPENSE;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String note;

    @Column(name = "merchant_logo_url")
    private String merchantLogoUrl;

    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(name = "sms_hash")
    private String smsHash;

    @ElementCollection
    @CollectionTable(name = "expense_tags", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum TransactionType {
        INCOME, EXPENSE
    }
}
