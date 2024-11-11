package com.arthall.modam.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name= "password", nullable = false)
    private String password;

    @Column(name= "name", nullable = false)
    private String name;

    @Column(name= "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.user;

    @Column(name = "status", nullable = false)
    private String status = "active";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Enum for role
    public enum Role {
        user, admin
    }

}
