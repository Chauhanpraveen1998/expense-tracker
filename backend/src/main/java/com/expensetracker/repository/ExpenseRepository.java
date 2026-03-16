package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.Expense.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByUserIdOrderByDateDesc(UUID userId);

    Page<Expense> findByUserIdOrderByDateDesc(UUID userId, Pageable pageable);

    Page<Expense> findByUserIdAndTransactionTypeOrderByDateDesc(
            UUID userId, TransactionType type, Pageable pageable);

    Page<Expense> findByUserIdAndCategoryIdOrderByDateDesc(
            UUID userId, UUID categoryId, Pageable pageable);

    Page<Expense> findByUserIdAndAccountIdOrderByDateDesc(
            UUID userId, UUID accountId, Pageable pageable);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(
            UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    Page<Expense> findByUserIdAndDateBetweenOrderByDateDesc(
            UUID userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
           "AND LOWER(e.merchantName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY e.date DESC")
    Page<Expense> searchByMerchant(
            @Param("userId") UUID userId,
            @Param("query") String query,
            Pageable pageable);

    boolean existsBySmsHash(String smsHash);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.transactionType = :type " +
           "AND e.date BETWEEN :startDate AND :endDate")
    BigDecimal sumByTypeAndDateRange(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e.category.id, e.category.name, SUM(e.amount) as total, COUNT(e) as count " +
           "FROM Expense e " +
           "WHERE e.user.id = :userId AND e.transactionType = 'EXPENSE' " +
           "AND e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category.id, e.category.name ORDER BY total DESC")
    List<Object[]> getCategorySpending(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CAST(e.date AS LocalDate), SUM(e.amount) " +
           "FROM Expense e " +
           "WHERE e.user.id = :userId AND e.transactionType = 'EXPENSE' " +
           "AND e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(e.date AS LocalDate) " +
           "ORDER BY CAST(e.date AS LocalDate)")
    List<Object[]> getDailySpending(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e.merchantName, SUM(e.amount) as total, COUNT(e) as count " +
           "FROM Expense e " +
           "WHERE e.user.id = :userId AND e.transactionType = 'EXPENSE' " +
           "AND e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY e.merchantName ORDER BY total DESC")
    List<Object[]> getTopMerchants(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<Expense> findTop5ByUserIdOrderByDateDesc(UUID userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.transactionType = :type " +
           "AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
    BigDecimal getMonthlyTotal(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            @Param("month") int month,
            @Param("year") int year);

    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.category WHERE e.user.id = :userId ORDER BY e.date DESC")
    List<Expense> findByUserIdOrderByExpenseDateDesc(@Param("userId") UUID userId);

    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.category WHERE e.user.id = :userId ORDER BY e.date DESC")
    List<Expense> findByUserIdOrderByExpenseDateDesc(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND e.date >= :startDate AND e.date < :endDate")
    BigDecimal sumByUserAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND e.date >= :startDate AND e.date < :endDate " +
           "GROUP BY e.category.name")
    List<Object[]> sumByCategoryAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.category.id = :categoryId " +
           "AND e.date >= :startDate AND e.date < :endDate")
    BigDecimal sumByUserCategoryAndDateRange(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e.date, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.date >= :startDate " +
           "GROUP BY e.date ORDER BY e.date")
    List<Object[]> sumByDayFrom(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate);
}
