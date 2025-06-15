package com.example.allomaison.DTOs.Responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderReviewSummary {
    private Double rating;
    private List<CustomerReview> customerReviews;

    @Data
    @Builder
    public static class CustomerReview {
        private String author;  // userName of customer
        private String content; // review text
    }
}
