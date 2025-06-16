package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.OrderDTO;
import com.example.allomaison.DTOs.Responses.OrderResponse;
import com.example.allomaison.DTOs.ReviewDTO;
import com.example.allomaison.DTOs.TaskDTO;
import com.example.allomaison.Entities.Order;
import com.example.allomaison.Entities.Review;

import java.time.format.DateTimeFormatter;

import static com.example.allomaison.Entities.Task.Status.COMPLETED;

public class OrderMapper {

    public static OrderDTO toDTO(Order order, TaskDTO taskDTO, Review reviewOpt) {
        ReviewDTO reviewDTO = null;
        if (reviewOpt != null && taskDTO.status() == COMPLETED) {
            reviewDTO = ReviewMapper.toDTO(reviewOpt);
        }

        return new OrderDTO(
                order.getProvider().getProviderId(),
                order.getConfirmedAt(),
                taskDTO,
                reviewDTO
        );
    }
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static OrderResponse toResponse(OrderDTO dto, String providerName, String category, String city) {
        TaskDTO task = dto.task();

        OrderResponse response = new OrderResponse();
        response.setOrderId(dto.task().taskId());
        response.setTaskId(task.taskId());
        response.setProviderId(dto.providerId());
        response.setProviderName(providerName);
        response.setCustomerId(task.customerId());
        response.setCategory(category);
        response.setTitle(task.title());
        response.setFrequency(task.frequency().name());
        response.setCity(city);
        response.setStartTime(formatTimestamp(task.startTime()));
        response.setEndTime(formatTimestamp(task.endTime()));
        response.setAddress(task.address());
        response.setDescription(task.description());
        response.setBudget(String.valueOf(task.budget()));
        response.setProviderContact(dto.task().customerContact());
        response.setStatus(convertStatus(task.status()));
        response.setCreatedAt(formatTimestamp(task.createdAt()));
        return response;
    }

    private static String formatTimestamp(java.sql.Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime().format(formatter) : null;
    }

    private static OrderResponse.OrderStatus convertStatus(com.example.allomaison.Entities.Task.Status status) {
        return switch (status) {
            case PENDING -> OrderResponse.OrderStatus.PENDING;
            case CONFIRMED -> OrderResponse.OrderStatus.CONFIRMED;
            case COMPLETED -> OrderResponse.OrderStatus.COMPLETED;
            case CANCELLED -> OrderResponse.OrderStatus.CANCELLED;
        };
    }

}
