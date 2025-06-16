package com.example.allomaison.DTOs.Responses;


import lombok.Data;

@Data
public class OrderResponse {
    private Long orderId;
    private Long taskId;
    private Long providerId;
    private String providerName;
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
    private String providerContact;
    private OrderStatus status;
    private String createdAt;

    public enum OrderStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }
}
