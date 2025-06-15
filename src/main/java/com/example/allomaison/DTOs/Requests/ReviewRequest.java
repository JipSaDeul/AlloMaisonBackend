package com.example.allomaison.DTOs.Requests;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long orderId;
    private String reviewText;
    private Integer rating;
}
