package com.example.allomaison.Services;

import com.example.allomaison.DTOs.AdminDTO;
import com.example.allomaison.Entities.Admin;
import com.example.allomaison.Mapper.AdminMapper;
import com.example.allomaison.Repositories.AdminRepository;
import com.example.allomaison.Utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean isAuthorizedCreator(AdminDTO creator) {
        // Can be replaced by a role check, permission system, etc.
        return adminRepository.findById(creator.getAdminId()).isPresent();
        // Only check existence now.
    }

    public Optional<AdminDTO> register(String adminName, String rawPassword, AdminDTO creator) {
        // Check uniqueness
        if (adminRepository.findByAdminName(adminName).isPresent()) {
            return Optional.empty(); // Already exists
        }

        // Check creator authorization
        if (!isAuthorizedCreator(creator)) {
            return Optional.empty(); // Not allowed to create
        }

        // Create new admin
        Admin admin = new Admin();
        admin.setAdminId(UUIDUtil.uuidToLong());
        admin.setAdminName(adminName);
        admin.setPasswordHash(passwordEncoder.encode(rawPassword));

        Admin saved = adminRepository.save(admin);
        return Optional.of(AdminMapper.toDTO(saved));
    }

    public Optional<AdminDTO> login(String adminName, String rawPassword) {
        return adminRepository.findByAdminName(adminName).flatMap(admin -> {
            if (passwordEncoder.matches(rawPassword, admin.getPasswordHash())) {
                return Optional.of(AdminMapper.toDTO(admin));
            }
            return Optional.empty();
        });
    }

    @SuppressWarnings("unused")
    public Optional<AdminDTO> getAdminById(Long adminId) {
        return adminRepository.findById(adminId).map(AdminMapper::toDTO);
    }

    @SuppressWarnings("unused")
    public Optional<AdminDTO> getAdminByName(String adminName) {
        return adminRepository.findByAdminName(adminName).map(AdminMapper::toDTO);
    }
}
