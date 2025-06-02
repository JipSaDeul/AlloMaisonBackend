package com.example.allomaison.DTOs;

import java.sql.Timestamp;

public record OrderDTO(
        Long providerId,
        Timestamp confirmedAt,
        TaskDTO task,
        ReviewDTO review
) {}
