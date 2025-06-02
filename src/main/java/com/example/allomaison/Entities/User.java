package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Table(name = "Users")
public class User {

    @Id
    private Long userId;  /// Backend generate uuid -> long

    private String userFirstName;

    private String userLastName;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    private String avatarUrl;

    @Column(nullable = false)
    private String passwordHash;

    private Boolean gender;  // null / false / true

    private Date birthDate;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp loginTime;

    private Timestamp lastLoginTime;
}
