package com.example.allomaison.DTOs.Requests;
import lombok.Data;

@Data
public class UserRegisterJsonRequest {
    private String firstName;
    private String lastName;
    private String userName;
    private String gender;
    private String birthday;
    private String email;
    private String password;
    private boolean agree;
}
