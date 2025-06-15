package com.example.allomaison.Services;

import com.example.allomaison.DTOs.RegisterResult;
import com.example.allomaison.DTOs.Requests.UserRegisterRequest;
import com.example.allomaison.Entities.User;
import com.example.allomaison.Mapper.UserMapper;
import com.example.allomaison.Repositories.UserRepository;
import com.example.allomaison.Utils.FileStorageUtil;
import com.example.allomaison.Utils.FileUploadResult;
import com.example.allomaison.Utils.UUIDUtil;
import com.example.allomaison.DTOs.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResult register(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent() ||
            userRepository.findByUserName(request.getUserName()).isPresent()) {
            return RegisterResult.failure(RegisterResult.ErrorReason.DUPLICATE_EMAIL_OR_USERNAME);
        }

        User user = new User();
        Long userId = UUIDUtil.uuidToLong();
        user.setUserId(userId);
        user.setUserFirstName(request.getFirstName());
        user.setUserLastName(request.getLastName());
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setGender(parseGender(request.getGender()));
        user.setBirthDate(parseBirthDate(request.getBirthday()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setLoginTime(Timestamp.from(Instant.now()));

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            if (request.getAvatar().getSize() > 5 * 1024 * 1024) {
                return RegisterResult.failure(RegisterResult.ErrorReason.AVATAR_TOO_LARGE);
            }

            FileUploadResult avatarResult = FileStorageUtil.saveAvatarFile(request.getAvatar(), userId);
            if (avatarResult.isSuccessful()) {
                user.setAvatarUrl(avatarResult.getUrl());
            } else {
                System.err.println("Avatar upload failed: " + avatarResult.getError());
                return RegisterResult.failure(RegisterResult.ErrorReason.AVATAR_UPLOAD_FAILED);
            }
        }

        User saved = userRepository.save(user);
        return userRepository.findById(saved.getUserId())
                .map(UserMapper::toDTO)
                .map(RegisterResult::success)
                .orElse(RegisterResult.failure(RegisterResult.ErrorReason.UNKNOWN));
    }


    public Optional<UserDTO> getUserById(Long userId) {
        return userRepository.findById(userId).map(UserMapper::toDTO);
    }

    @SuppressWarnings("unused")
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(UserMapper::toDTO);
    }

    @SuppressWarnings("unused")
    public Optional<UserDTO> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName).map(UserMapper::toDTO);
    }

    public boolean updateUserName(Long userId, String newUserName) {
        if (userRepository.findByUserName(newUserName).isPresent()) return false;
        return userRepository.findById(userId).map(user -> {
            user.setUserName(newUserName);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    @SuppressWarnings("unused")
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        return userRepository.findById(userId).map(user -> {
            if (passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                user.setPasswordHash(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public boolean updateProfile(UserDTO dto) {
        return userRepository.findById(dto.getUserId()).map(user -> {
            UserMapper.updateEntity(user, dto);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public Optional<UserDTO> login(String email, String rawPassword) {
        return userRepository.findByEmail(email).flatMap(user -> {
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                updateLoginTime(user.getUserId());
                userRepository.save(user);
                return Optional.of(UserMapper.toDTO(user));
            }
            return Optional.empty();
        });
    }

    private void updateLoginTime(Long userId) {
        userRepository.findById(userId).map(user -> {
            Timestamp now = Timestamp.from(Instant.now());
            user.setLastLoginTime(user.getLoginTime());
            user.setLoginTime(now);
            userRepository.save(user);
            return true;
        });
    }

    private Boolean parseGender(String gender) {
        if ("male".equalsIgnoreCase(gender)) return false;
        if ("female".equalsIgnoreCase(gender)) return true;
        return null;
    }

    private Date parseBirthDate(String birthday) {
        try {
            if (birthday != null && !birthday.isBlank()) {
                return java.sql.Date.valueOf(birthday); // ISO 8601: yyyy-MM-dd
            }
        } catch (Exception e) {
            // Invalid date format, return null
            System.err.println("Invalid birthday format: " + birthday);
        }
        return null;
    }

}
