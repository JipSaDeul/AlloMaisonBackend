package com.example.allomaison.DTOs.Responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderResponse {
    private Long providerId;
    private String category;
    private String city;
    private String providerName;
    private String description;
    private String avatarUrl;
    private List<String> servicesOffered;
    private String serviceArea;
    private List<String> providerLabels;
    private String priceRange;
}
