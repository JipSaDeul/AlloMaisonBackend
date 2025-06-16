package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "ProviderInfos")
@Data
public class ProviderInfo {

    @Id
    @Column(name = "providerId", updatable = false, nullable = false)
    private Long providerId; // Foreign key to Users.userId - immutable

    @Column(name = "catId", nullable = false, updatable = false)
    private Integer catId; // Foreign key to `Categories.catId` - immutable

    @Column(columnDefinition = "TEXT")
    private String description; // Application description

    @Column(name = "serviceOffered", columnDefinition = "TEXT")
    private String serviceOffered; // Detailed services in JSON string format

    @Column(name = "cityZipcode", nullable = false)
    private Integer cityZipcode; // Foreign key to Cities.zipcode

    @Column(name = "serviceArea", columnDefinition = "TEXT")
    private String serviceArea; // Service area as descriptive text

    @Column(name = "priceRange", length = 64)
    private String priceRange; // Price range as a string

    @Column(name = "authorizedAt", insertable = false, nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp authorizedAt; // Set by database - immutable
}
