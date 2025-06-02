package com.example.allomaison.Mapper;

import com.example.allomaison.Entities.User;
import com.example.allomaison.DTOs.UserDTO;

public class UserMapper {

    // Entity → DTO
    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUserFirstName(),
                user.getUserLastName(),
                user.getAvatarUrl(),
                user.getGender(),
                user.getBirthDate(),
                user.getLoginTime(),
                user.getLastLoginTime()
        );
    }

    // DTO → Entity for full copy (e.g., registration)
    public static User toEntity(UserDTO dto, String passwordHash) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setCreatedAt(dto.getCreatedAt());
        user.setPasswordHash(passwordHash); // password handled separately
        updateEntity(user, dto);
        return user;
    }

    // DTO → updateEntity
    public static void updateEntity(User user, UserDTO dto) {
        user.setUserFirstName(dto.getUserFirstName());
        user.setUserLastName(dto.getUserLastName());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setGender(dto.getGender());
        user.setBirthDate(dto.getBirthDate());
        user.setLoginTime(dto.getLoginTime());
        user.setLastLoginTime(dto.getLastLoginTime());
    }
}
