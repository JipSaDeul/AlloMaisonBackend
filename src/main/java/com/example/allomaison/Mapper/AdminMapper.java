package com.example.allomaison.Mapper;

import com.example.allomaison.Entities.Admin;
import com.example.allomaison.DTOs.AdminDTO;

public class AdminMapper {

    // Entity → DTO
    public static AdminDTO toDTO(Admin admin) {
        return new AdminDTO(
                admin.getAdminId(),
                admin.getAdminName()
        );
    }

    // DTO → Entity for full copy (e.g., registration)
    @SuppressWarnings("unused")
    public static Admin toEntity(AdminDTO dto, String passwordHash) {
        Admin admin = new Admin();
        admin.setAdminId(dto.getAdminId());
        admin.setAdminName(dto.getAdminName());
        admin.setPasswordHash(passwordHash);  // password handled separately
        return admin;
    }
}
