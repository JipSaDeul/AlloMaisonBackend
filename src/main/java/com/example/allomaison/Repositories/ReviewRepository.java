package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
            SELECT r FROM Review r
            WHERE r.order.provider.providerId = :providerId
              AND r.order.task.status = 'COMPLETED'
            """)
    List<Review> findCompletedReviewsByProviderId(@Param("providerId") Long providerId);
}
