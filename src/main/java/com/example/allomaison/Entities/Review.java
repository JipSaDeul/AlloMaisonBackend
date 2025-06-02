package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Reviews")
@Data
public class Review {

    @Id
    @Column(name = "orderId", nullable = false)
    private Long orderId; // Links to the order

    @OneToOne
    @JoinColumn(name = "orderId", referencedColumnName = "orderId", insertable = false, updatable = false)
    private Order order;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "reviewText", columnDefinition = "TEXT")
    private String reviewText;
}
