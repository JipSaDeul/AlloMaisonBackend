package com.example.allomaison.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ProviderInfoDTO {
    private Long providerId; // Foreign key to Users.userId - immutable
    private String description;
    private List<String> serviceOffered; // List of services offered by the provider
    private Integer cityZipcode;
    private String serviceArea;
    private String priceRange;
    private List<String> labels;
}
