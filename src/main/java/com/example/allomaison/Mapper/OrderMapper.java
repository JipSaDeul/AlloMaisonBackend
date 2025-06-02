package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.OrderDTO;
import com.example.allomaison.DTOs.ReviewDTO;
import com.example.allomaison.DTOs.TaskDTO;
import com.example.allomaison.Entities.Order;
import com.example.allomaison.Entities.Review;

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
}
