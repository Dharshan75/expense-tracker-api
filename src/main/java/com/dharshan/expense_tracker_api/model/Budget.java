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
public class Budget {

    @Id
    @GeneratedValue
    private UUID id;

    private String category;

    private Double amount;

    private Integer month;

    private Integer year;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}