package com.example.allomaison.DTOs.Projections;

public interface ReviewWithCustomerId {
    Long getOrderId();        // from Review.orderId
    Integer getRanking();     // from Review.ranking
    String getReviewText();   // from Review.reviewText
    Long getCustomerId();     // from Review.order.task.customerId
}
