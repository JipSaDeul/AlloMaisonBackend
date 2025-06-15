package com.example.allomaison.DTOs.Requests;

import lombok.Data;

import java.util.Date;

@Data
public class UserUpdateRequest {
    private String userFirstName;
    private String userLastName;
    private Boolean gender;
    private Date birthDate;
}
