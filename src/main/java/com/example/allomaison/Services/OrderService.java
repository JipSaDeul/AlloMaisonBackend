package com.example.allomaison.Services;

import com.example.allomaison.DTOs.OrderDTO;
import com.example.allomaison.Entities.Order;
import com.example.allomaison.Entities.ProviderInfo;
import com.example.allomaison.Entities.Review;
import com.example.allomaison.Entities.Task;
import com.example.allomaison.Mapper.OrderMapper;
import com.example.allomaison.Mapper.TaskMapper;
import com.example.allomaison.Repositories.OrderRepository;
import com.example.allomaison.Repositories.ProviderInfoRepository;
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
    private final ProviderInfoRepository providerInfoRepository;

    // Get order by task ID witch is also the order ID
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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

    public boolean changeOrderStatus(Long orderId, Long customerId, Task.Status newStatus) {
        return orderRepository.findById(orderId).flatMap(order ->
                taskRepository.findById(order.getOrderId()).map(task -> {
                    if (!Objects.equals(task.getCustomerId(), customerId)) {
                        return false;
                    }
                    task.setStatus(newStatus);
                    taskRepository.save(task);
                    return true;
                })
        ).orElse(false);
    }

    public boolean createOrderIfEligible(Long taskId, Long providerId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) return false;

        Task task = taskOpt.get();

        if (orderRepository.existsById(taskId)) return false;

        Optional<ProviderInfo> providerOpt = Optional.ofNullable(task.getStatus())
                .filter(status -> status == Task.Status.PENDING)
                .flatMap(s -> providerId != null ? Optional.of(providerId) : Optional.empty())
                .flatMap(id -> Optional.ofNullable(task.getCatId())
                        .flatMap(catId -> providerInfoRepository.findById(providerId)
                                .filter(p -> p.getCatId().equals(catId)))
                );

        if (providerOpt.isEmpty()) return false;

        task.setStatus(Task.Status.CONFIRMED);
        taskRepository.save(task);

        Order order = new Order();
        order.setOrderId(task.getTaskId());
        order.setTask(task);
        order.setProvider(providerOpt.get());
        orderRepository.save(order);

        return true;
    }

    public boolean checkOrdered(Long taskId) {
        return orderRepository.findById(taskId).isPresent();
    }

    public boolean cancelOrderByProvider(Long orderId, Long providerId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) return false;

        Order order = orderOpt.get();
        if (!order.getProvider().getProviderId().equals(providerId)) return false;

        Task task = taskRepository.findById(orderId).orElse(null);
        if (task == null || task.getStatus() != Task.Status.CONFIRMED) return false;

        task.setStatus(Task.Status.PENDING);
        taskRepository.save(task);

        orderRepository.delete(order);

        return true;
    }

}
