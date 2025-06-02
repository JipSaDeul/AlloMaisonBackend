package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Orders")
@Data
public class Order {

    @Id
    @Column(name = "orderId", nullable = false)
    private Long orderId; // same as taskId

    @OneToOne
    @MapsId // use this because orderId == taskId
    @JoinColumn(name = "orderId")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "providerId", nullable = false)
    private ProviderInfo provider;

    @Column(name = "confirmedAt", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp confirmedAt;
}

