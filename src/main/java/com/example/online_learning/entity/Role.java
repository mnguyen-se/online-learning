package com.example.online_learning.entity;

import jakarta.persistence.*;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
