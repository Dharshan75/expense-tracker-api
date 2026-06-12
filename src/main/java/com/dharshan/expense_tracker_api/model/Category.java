package com.dharshan.expense_tracker_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    // true = built-in category
    // false = user-created category
    private boolean isDefaultCategory;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}