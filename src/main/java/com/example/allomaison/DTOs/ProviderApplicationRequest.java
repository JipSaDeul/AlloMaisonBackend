package com.example.allomaison.DTOs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class ProviderApplicationRequest {
    private Long userId;
    private String category; // category.name
    private Integer city;    // zipcode
    private String description;
    private List<MultipartFile> certificates;
}
