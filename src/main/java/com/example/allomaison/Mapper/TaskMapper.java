package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.TaskDTO;
import com.example.allomaison.Entities.Task;

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
}
