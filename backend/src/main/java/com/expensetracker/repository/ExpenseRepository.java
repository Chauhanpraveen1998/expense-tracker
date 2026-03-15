package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.category WHERE e.user.id = :userId ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdOrderByExpenseDateDesc(@Param("userId") UUID userId);

    /** Top-N recent with category eagerly fetched to avoid LazyInitializationException. */
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.category WHERE e.user.id = :userId ORDER BY e.expenseDate DESC")
    List<Expense> findByUserIdOrderByExpenseDateDesc(@Param("userId") UUID userId, Pageable pageable);

    /** Sum of all expense amounts in a date range [startDate, endDate). */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND e.expenseDate >= :startDate AND e.expenseDate < :endDate")
    BigDecimal sumByUserAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Per-category totals for a date range.
     * Returns Object[]{categoryName (String|null), total (BigDecimal)}.
     */
    @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND e.expenseDate >= :startDate AND e.expenseDate < :endDate " +
           "GROUP BY e.category.name")
    List<Object[]> sumByCategoryAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /** Sum for a specific category in a date range (used for budget status). */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.category.id = :categoryId " +
           "AND e.expenseDate >= :startDate AND e.expenseDate < :endDate")
    BigDecimal sumByUserCategoryAndDateRange(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /** Daily totals from startDate onward, grouped by expense_date. */
    @Query("SELECT e.expenseDate, SUM(e.amount) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.expenseDate >= :startDate " +
           "GROUP BY e.expenseDate ORDER BY e.expenseDate")
    List<Object[]> sumByDayFrom(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate);
}
