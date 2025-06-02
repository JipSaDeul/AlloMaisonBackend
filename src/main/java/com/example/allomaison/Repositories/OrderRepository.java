package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.Order;
import com.example.allomaison.Entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("""
                SELECT o FROM Order o
                JOIN Task t ON o.orderId = t.taskId
                WHERE o.provider.providerId = :providerId
                  AND t.status = :status
            """)
    List<Order> findByProviderIdAndTaskStatus(@Param("providerId") Long providerId,
                                              @Param("status") Task.Status status);

    @Query("""
                SELECT o FROM Order o
                JOIN FETCH Task t ON o.orderId = t.taskId
                WHERE t.customerId = :customerId
            """)
    List<Order> findByCustomerId(@Param("customerId") Long customerId);


    List<Order> findByProviderProviderId(Long providerId);
}
