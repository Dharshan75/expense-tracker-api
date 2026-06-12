package com.dharshan.expense_tracker_api.repository;

import com.dharshan.expense_tracker_api.model.Category;
import com.dharshan.expense_tracker_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByUser(User user);

    List<Category> findByIsDefaultCategoryTrue();

}