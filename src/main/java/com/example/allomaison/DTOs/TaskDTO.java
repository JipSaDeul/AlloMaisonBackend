package com.example.allomaison.DTOs;

import com.example.allomaison.Entities.Task.Status;
import com.example.allomaison.Entities.Task.Frequency;

import java.sql.Timestamp;

public record TaskDTO(
        Long taskId,
        Long customerId,
        String title,
        Integer catId,
        Frequency frequency,
        Integer cityZipcode,
        Timestamp startTime,
        Timestamp endTime,
        String address,
        Integer budget,
        String customerContact,
        String description,
        Status status,
        Timestamp createdAt
) {}
