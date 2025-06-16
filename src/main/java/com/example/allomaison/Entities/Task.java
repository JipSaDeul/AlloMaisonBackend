package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;


import java.sql.Timestamp;

@Entity
@Table(name = "Tasks")
@Data
public class Task {

    @Id
    @Column(name = "taskId", nullable = false)
    private Long taskId; // generated manually (e.g., UUID mapped to long)

    @Column(nullable = false)
    private Long customerId; // Foreign key to Users.userId

    @Column(length = 128, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer catId; // Foreign key to `Categories.catId`

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency = Frequency.ONCE;

    @Column(nullable = false)
    private Integer cityZipcode;

    @Column(nullable = false)
    private Timestamp startTime;

    @Column(nullable = false)
    private Timestamp endTime;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer budget; // in cents

    @Column(columnDefinition = "TEXT", nullable = false)
    private String customerContact;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    private Timestamp createdAt;


    public enum Frequency {
        ONCE, DAILY, WEEKLY, MONTHLY
    }

    public enum Status {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }
}
