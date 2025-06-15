package com.example.allomaison.DTOs.Requests;

import lombok.Data;

import java.util.List;

@Data
public class ProviderUpdateRequest {
    private String category;
    private String city; // zipcode
    private String providerName;
    private String description;
    private List<String> servicesOffered;
    private String serviceArea;
    private List<String> providerLabels;
    private String priceRange;
}
