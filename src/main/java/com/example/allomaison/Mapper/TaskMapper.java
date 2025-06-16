package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.Responses.OrderResponse;
import com.example.allomaison.DTOs.Responses.TaskResponse;
import com.example.allomaison.DTOs.TaskDTO;
import com.example.allomaison.Entities.Task;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TaskMapper {

    public static TaskDTO toDTO(Task task) {
        return new TaskDTO(
                task.getTaskId(),
                task.getCustomerId(),
                task.getTitle(),
                task.getCatId(),
                task.getFrequency(),
                task.getCityZipcode(),
                task.getStartTime(),
                task.getEndTime(),
                task.getAddress(),
                task.getBudget(),
                task.getCustomerContact(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt()
        );
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static TaskResponse toResponse(
            TaskDTO dto,
            String category,
            String city
    ) {
        TaskResponse response = new TaskResponse();
        response.setTaskId(dto.taskId());
        response.setCustomerId(dto.customerId());
        response.setCategory(category);
        response.setTitle(dto.title());
        response.setFrequency(dto.frequency().name());
        response.setCity(city);
        response.setStartTime(format(dto.startTime()));
        response.setEndTime(format(dto.endTime()));
        response.setAddress(dto.address());
        response.setDescription(dto.description());
        response.setBudget(String.valueOf(dto.budget()));
        response.setCustomerContact(dto.customerContact());
        response.setCreatedAt(format(dto.createdAt()));
        response.setStatus(convertStatus(dto.status()));

        return response;
    }

    private static String format(java.sql.Timestamp timestamp) {
        return Optional.ofNullable(timestamp)
                .map(ts -> ts.toLocalDateTime().format(formatter))
                .orElse(null);
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
