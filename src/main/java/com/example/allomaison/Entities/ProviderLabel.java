package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ProviderLabels")
public class ProviderLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labelId;

    @Column(unique = true, nullable = false, length = 64)
    private String name;
}
