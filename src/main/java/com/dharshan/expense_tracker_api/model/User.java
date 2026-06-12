package com.dharshan.expense_tracker_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String currency;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user")
    private Subscription subscription;

    @OneToMany(mappedBy = "user")
    private List<Budget> budgets;

    // Automatically set created time
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}