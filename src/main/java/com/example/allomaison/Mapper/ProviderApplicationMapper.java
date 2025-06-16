package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ProviderApplicationDTO;
import com.example.allomaison.DTOs.ProviderCertificateDTO;
import com.example.allomaison.DTOs.Responses.ProviderApplicationResponse;
import com.example.allomaison.Entities.ProviderApplication;

import java.util.List;
import java.util.stream.Collectors;

public class ProviderApplicationMapper {

    public static ProviderApplicationDTO toDTO(
            ProviderApplication app,
            List<ProviderCertificateDTO> certDTOs
    ) {

        return new ProviderApplicationDTO(
                app.getApplicationId(),
                app.getUserId(),
                app.getCatId(),
                app.getDescription(),
                app.getCityZipcode(),
                app.getStatus(),
                certDTOs
        );
    }

    public static ProviderApplicationResponse toResponse(ProviderApplicationDTO dto, String cityName, String categoryName) {
        return ProviderApplicationResponse.builder()
                .applicationId(dto.applicationId())
                .customerId(dto.userId())
                .city(cityName)
                .category(categoryName)
                .description(dto.description())
                .status(dto.status())
                .certifications(mapCertificateUrls(dto.certificates()))
                .build();
    }

    private static List<String> mapCertificateUrls(List<ProviderCertificateDTO> certificates) {
        return certificates.stream()
                .map(ProviderCertificateDTO::fileUrl)
                .collect(Collectors.toList());
    }
}
