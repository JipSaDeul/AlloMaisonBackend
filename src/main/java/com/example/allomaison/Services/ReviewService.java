package com.example.allomaison.Services;

import com.example.allomaison.DTOs.Requests.ReviewRequest;
import com.example.allomaison.Entities.Order;
import com.example.allomaison.Entities.Review;
import com.example.allomaison.Entities.Task;
import com.example.allomaison.Repositories.OrderRepository;
import com.example.allomaison.Repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    public boolean submitReview(Long userId, ReviewRequest request) {
        Optional<Order> orderOpt = orderRepository.findById(request.getOrderId());

        if (orderOpt.isEmpty()) return false;

        Order order = orderOpt.get();
        Task task = order.getTask();

        if (task == null || !task.getCustomerId().equals(userId) || task.getStatus() != Task.Status.COMPLETED) {
            return false;
        }

        Review review = new Review();
        review.setOrderId(order.getOrderId());
        review.setReviewText(request.getReviewText());
        review.setRanking(request.getRating());

        reviewRepository.save(review);
        return true;
    }
}
