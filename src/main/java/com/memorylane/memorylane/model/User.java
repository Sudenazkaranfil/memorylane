package com.memorylane.memorylane.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @JsonIgnore
    private String passwordHash;

    private String firstName;
    private String lastName;
    private String bio;
    private String profileImageUrl;
    private String location;
    private String website;
    private String favoriteDestination;

    @Column(name = "is_verified")
    private Boolean verified = false;

    @JsonIgnore
    private String verificationCode;

    @JsonIgnore
    private LocalDateTime verificationCodeExpiry;

    private LocalDateTime createdAt;

    private String resetPasswordCode;
    private LocalDateTime resetPasswordCodeExpiry;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}