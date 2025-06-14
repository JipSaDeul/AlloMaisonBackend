package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Admins")
public class Admin {

    @Id
    private Long adminId;  // Backend generate uuid -> long

    @Column(unique = true, nullable = false)
    private String adminName;

    @Column(nullable = false)
    private String passwordHash;
}
