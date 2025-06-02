package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ReviewDTO;
import com.example.allomaison.Entities.Review;

public class ReviewMapper {
    public static ReviewDTO toDTO(Review entity) {
        return new ReviewDTO(entity.getRanking(), entity.getReviewText());
    }
}
