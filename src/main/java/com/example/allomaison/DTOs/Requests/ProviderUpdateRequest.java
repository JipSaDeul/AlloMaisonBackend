package com.example.allomaison.DTOs.Requests;

import lombok.Data;

import java.util.List;

@Data
public class ProviderUpdateRequest {
    private String city; // "{place}, {province}"
    private String description;
    private List<String> servicesOffered;
    private String serviceArea;
    private List<String> providerLabels;
    private String priceRange;
}
