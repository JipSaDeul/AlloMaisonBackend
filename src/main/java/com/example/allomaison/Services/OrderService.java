package com.example.allomaison.Services;

import com.example.allomaison.DTOs.OrderDTO;
import com.example.allomaison.Entities.Order;
import com.example.allomaison.Entities.Review;
import com.example.allomaison.Entities.Task;
import com.example.allomaison.Mapper.OrderMapper;
import com.example.allomaison.Mapper.TaskMapper;
import com.example.allomaison.Repositories.OrderRepository;
import com.example.allomaison.Repositories.ReviewRepository;
import com.example.allomaison.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TaskRepository taskRepository;
    private final ReviewRepository reviewRepository;

    // Get order by task ID witch is also the order ID
    public Optional<OrderDTO> getOrderByTaskId(Long taskId) {
        return orderRepository.findById(taskId)
                .flatMap(order -> taskRepository.findById(taskId)
                        .map(task -> {
                            Review review = reviewRepository.findById(taskId).orElse(null);
                            return OrderMapper.toDTO(order, TaskMapper.toDTO(task), review);
                        })
                );
    }

    // Get all orders by provider ID
    public List<OrderDTO> getOrdersByProviderId(Long providerId) {
        return orderRepository.findByProviderProviderId(providerId).stream()
                .map(order -> {
                    Task task = taskRepository.findById(order.getOrderId()).orElse(null);
                    if (task == null) return null;
                    Review review = reviewRepository.findById(order.getOrderId()).orElse(null);
                    return OrderMapper.toDTO(order, TaskMapper.toDTO(task), review);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // Get all orders by customer ID
    public List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        return orders.stream()
                .map(order -> {
                    Task task = order.getTask();
                    Review review = (task.getStatus() == Task.Status.COMPLETED)
                            ? reviewRepository.findById(order.getOrderId()).orElse(null)
                            : null;
                    return OrderMapper.toDTO(order, TaskMapper.toDTO(task), review);
                })
                .toList();
    }

    // get orders by provider ID and status
    public List<OrderDTO> getOrdersByProviderAndStatus(Long providerId, Task.Status status) {
        List<Order> orders = orderRepository.findByProviderIdAndTaskStatus(providerId, status);
        final boolean needReview = status == Task.Status.COMPLETED;

        return orders.stream()
                .map(order -> {
                    Task task = order.getTask();
                    if (task == null) return null;
                    Review review = needReview ? reviewRepository.findById(order.getOrderId()).orElse(null) : null;
                    return OrderMapper.toDTO(order, TaskMapper.toDTO(task), review);
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
