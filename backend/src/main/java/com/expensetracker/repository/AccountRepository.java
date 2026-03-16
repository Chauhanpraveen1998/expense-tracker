package com.expensetracker.repository;

import com.expensetracker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Account> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    BigDecimal getTotalBalanceByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    Long countActiveByUserId(@Param("userId") UUID userId);

    boolean existsByUserIdAndName(UUID userId, String name);
}
