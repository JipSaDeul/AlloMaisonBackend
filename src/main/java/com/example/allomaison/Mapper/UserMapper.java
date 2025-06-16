package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.Responses.UserInfoResponse;
import com.example.allomaison.Entities.User;
import com.example.allomaison.DTOs.UserDTO;

import java.text.SimpleDateFormat;

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
    @SuppressWarnings("unused")
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

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static UserInfoResponse toUserInfoResponse(UserDTO dto, Boolean isProvider) {
        UserInfoResponse.UserInfoResponseBuilder builder = UserInfoResponse.builder();

        builder
                .userId(dto.getUserId())
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .firstName(dto.getUserFirstName())
                .lastName(dto.getUserLastName())
                .gender(dto.getGender() == null ? null : (dto.getGender() ? UserInfoResponse.Gender.male : UserInfoResponse.Gender.female))
                .birthday(dto.getBirthDate() == null ? null : DATE_FORMAT.format(dto.getBirthDate()))
                .createdAt(dto.getCreatedAt() == null ? null : dto.getCreatedAt().toInstant().toString())
                .avatarUrl(dto.getAvatarUrl())
                .role(isProvider ? UserInfoResponse.Role.provider : UserInfoResponse.Role.customer);

        return builder.build();
    }

}
