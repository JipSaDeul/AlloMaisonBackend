package com.example.allomaison.DTOs;


import java.util.List;

public record ProviderDTO(
        Long providerId,
        CategoryDTO category,
        String description,
        List<String> serviceOffered,
        CityDTO city,
        String serviceArea,
        String priceRange,
        String authorizedAt,
        List<ProviderLabelDTO> labels,
        List<ProviderCertificateDTO> certificates
) {}
