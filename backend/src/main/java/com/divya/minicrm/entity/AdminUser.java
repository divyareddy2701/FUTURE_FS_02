package com.divya.minicrm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    // Stored as a BCrypt hash — never plain text.
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "ROLE_ADMIN";
}
