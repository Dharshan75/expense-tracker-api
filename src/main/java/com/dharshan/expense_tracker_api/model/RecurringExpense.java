package com.dharshan.expense_tracker_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringExpense {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    private String category;

    private Double amount;

    private String frequency;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}