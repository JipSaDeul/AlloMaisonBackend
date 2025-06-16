package com.example.allomaison.DTOs.Responses;
import com.example.allomaison.Entities.ProviderApplication.ApplicationStatus;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderApplicationResponse {
    Long applicationId;
    Long customerId;
    String city;
    String category;
    String description;
    ApplicationStatus status;
    List<String> certifications;
}
