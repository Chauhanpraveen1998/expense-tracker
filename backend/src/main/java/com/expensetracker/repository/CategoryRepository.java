package com.expensetracker.repository;

import com.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    /** Returns categories owned by the user plus system-level categories (user IS NULL). */
    List<Category> findByUserIdOrUserIsNull(UUID userId);
}
