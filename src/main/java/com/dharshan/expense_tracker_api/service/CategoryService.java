package com.dharshan.expense_tracker_api.service;

import com.dharshan.expense_tracker_api.dto.CategoryRequest;
import com.dharshan.expense_tracker_api.model.Category;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // CREATE CATEGORY
    public Category createCategory(CategoryRequest request, User user) {

        Category category = Category.builder()
                .name(request.getName())
                .isDefaultCategory(false)
                .user(user)
                .build();

        return categoryRepository.save(category);
    }

    // GET ALL CATEGORIES
    public List<Category> getCategories(User user) {

        List<Category> categories = new ArrayList<>();

        categories.addAll(categoryRepository.findByIsDefaultCategoryTrue());

        categories.addAll(categoryRepository.findByUser(user));

        return categories;
    }

    // DELETE CATEGORY
    public void deleteCategory(UUID id, User user) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        categoryRepository.delete(category);
    }
}