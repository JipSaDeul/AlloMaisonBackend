package com.example.allomaison.DTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@ToString
@AllArgsConstructor
public class UserDTO {

    private final Long userId;
    private final String userName;
    private final String email;
    private final Timestamp createdAt;

    @Setter private String userFirstName;
    @Setter private String userLastName;
    @Setter private String avatarUrl;
    @Setter private Boolean gender;
    @Setter private Date birthDate;
    @Setter private Timestamp loginTime;
    @Setter private Timestamp lastLoginTime;
}
