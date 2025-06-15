package com.example.allomaison.DTOs.Requests;

import com.example.allomaison.Entities.Task.Frequency;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TaskRequest {
    private Long customerId;
    private String title;
    private Integer catId;
    private Frequency frequency;
    private Integer cityZipcode;
    private Timestamp startTime;
    private Timestamp endTime;
    private String address;
    private Integer budget;
    private String customerContact;
    private String description;
}
