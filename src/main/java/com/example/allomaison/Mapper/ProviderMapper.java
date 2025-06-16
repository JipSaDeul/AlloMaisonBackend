package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ProviderDTO;
import com.example.allomaison.DTOs.ProviderLabelDTO;
import com.example.allomaison.DTOs.Responses.ProviderResponse;
import com.example.allomaison.DTOs.UserDTO;

import java.util.stream.Collectors;

public class ProviderMapper {

    public static ProviderResponse toResponse(ProviderDTO dto, UserDTO userDTO) {
        return ProviderResponse.builder()
                .providerId(dto.providerId())
                .category(dto.category().name())
                .city(dto.city().place())
                .providerName(userDTO.getUserName())
                .description(dto.description())
                .avatarUrl(userDTO.getAvatarUrl())
                .servicesOffered(dto.serviceOffered())
                .serviceArea(dto.serviceArea())
                .priceRange(dto.priceRange())
                .providerLabels(dto.labels().stream()
                        .map(ProviderLabelDTO::name)
                        .collect(Collectors.toList()))
                .build();
    }
}
