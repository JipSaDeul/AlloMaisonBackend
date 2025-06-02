package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ProviderApplications")
@Data
public class ProviderApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer catId;

    @Lob
    private String description;

    @Column(nullable = false)
    private Integer cityZipcode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }
}
