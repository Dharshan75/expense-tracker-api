package com.dharshan.expense_tracker_api.controller;

import com.dharshan.expense_tracker_api.dto.CategoryRequest;
import com.dharshan.expense_tracker_api.dto.CategoryResponse;
import com.dharshan.expense_tracker_api.model.Category;
import com.dharshan.expense_tracker_api.model.User;
import com.dharshan.expense_tracker_api.service.CategoryService;
import com.dharshan.expense_tracker_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    // ==========================
    // GET CURRENT USER
    // ==========================
    private User getCurrentUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userService.getUserByEmail(email);
    }

    // ==========================
    // CREATE CATEGORY
    // ==========================
    @PostMapping
    public CategoryResponse createCategory(
            @RequestBody CategoryRequest request) {

        User user = getCurrentUser();

        Category category = categoryService.createCategory(request, user);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .defaultCategory(category.isDefaultCategory())
                .build();
    }

    // ==========================
    // GET ALL CATEGORIES
    // ==========================
    @GetMapping
    public List<CategoryResponse> getCategories() {

        User user = getCurrentUser();

        return categoryService.getCategories(user)
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .defaultCategory(category.isDefaultCategory())
                        .build())
                .toList();
    }

    // ==========================
    // DELETE CATEGORY
    // ==========================
    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable UUID id) {

        User user = getCurrentUser();

        categoryService.deleteCategory(id, user);

        return "Category deleted successfully";
    }
}