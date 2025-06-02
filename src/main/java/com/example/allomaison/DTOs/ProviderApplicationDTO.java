package com.example.allomaison.DTOs;

import com.example.allomaison.Entities.ProviderApplication.ApplicationStatus;

import java.util.List;

public record ProviderApplicationDTO(
        Long applicationId,
        Long userId,
        Integer catId,
        String description,
        Integer cityZipcode,
        ApplicationStatus status,
        List<ProviderCertificateDTO> certificates
) {}
