package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.*;
import com.example.allomaison.Entities.ProviderInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ProviderInfoMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ProviderInfoDTO toDTO(ProviderInfo entity, List<String> labelNames) {
        return new ProviderInfoDTO() {{
            setProviderId(entity.getProviderId());
            setDescription(entity.getDescription());
            setServiceOffered(fromJsonArray(entity.getServiceOffered()));
            setCityZipcode(entity.getCityZipcode());
            setServiceArea(entity.getServiceArea());
            setPriceRange(entity.getPriceRange());
            setLabels(labelNames);
        }};
    }

    public static ProviderDTO toFullDTO(
            ProviderInfo entity,
            CategoryDTO category,
            CityDTO city,
            List<ProviderLabelDTO> labels,
            List<ProviderCertificateDTO> certificates
    ) {
        return new ProviderDTO(
                entity.getProviderId(),
                category,
                entity.getDescription(),
                fromJsonArray(entity.getServiceOffered()),
                city,
                entity.getServiceArea(),
                entity.getPriceRange(),
                entity.getAuthorizedAt().toString(),
                labels,
                certificates
        );
    }

    public static void updateEntity(ProviderInfo entity, ProviderInfoDTO dto) {
        // only update mutable fields
        entity.setDescription(dto.getDescription());
        entity.setServiceOffered(toJsonArray(dto.getServiceOffered()));
        entity.setCityZipcode(dto.getCityZipcode());
        entity.setServiceArea(dto.getServiceArea());
        entity.setPriceRange(dto.getPriceRange());
    }

    // === JSON utility ===

    private static String toJsonArray(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private static List<String> fromJsonArray(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
