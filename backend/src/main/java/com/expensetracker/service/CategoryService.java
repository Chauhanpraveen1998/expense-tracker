package com.expensetracker.service;

import com.expensetracker.dto.CategoryRequest;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll(User user) {
        return categoryRepository.findByUserIdOrUserIsNull(user.getId());
    }

    public Category create(User user, CategoryRequest req) {
        Category category = Category.builder()
                .name(req.getName())
                .color(req.getColor())
                .icon(req.getIcon())
                .user(user)
                .build();
        return categoryRepository.save(category);
    }
}
