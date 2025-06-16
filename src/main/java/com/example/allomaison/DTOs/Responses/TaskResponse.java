package com.example.allomaison.DTOs.Responses;

import lombok.Data;

@Data
public class TaskResponse {
    private Long taskId;
    private Long customerId;
    private String category;
    private String startTime;
    private String endTime;
    private String address;
    private String description;
    private String title;
    private String frequency;
    private String city;
    private String budget;
    private String customerContact;
    private OrderResponse.OrderStatus status;
    private String createdAt;

    public enum Status {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }
}
