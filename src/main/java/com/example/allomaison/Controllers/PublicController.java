package com.example.allomaison.Controllers;

import com.example.allomaison.DTOs.CategoryDTO;
import com.example.allomaison.DTOs.ProviderDTO;
import com.example.allomaison.DTOs.Responses.CategoryResponse;
import com.example.allomaison.DTOs.Responses.CityResponse;
import com.example.allomaison.DTOs.Responses.ErrorResponse;
import com.example.allomaison.DTOs.Responses.ProviderResponse;
import com.example.allomaison.Entities.Task;
import com.example.allomaison.Mapper.ProviderMapper;
import com.example.allomaison.Services.CategoryService;
import com.example.allomaison.Services.CityService;
import com.example.allomaison.Services.ProviderService;
import com.example.allomaison.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PublicController {
    private final CategoryService categoryService;
    private final CityService cityService;
    private final ProviderService providerService;
    private final UserService userService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories().stream()
                .sorted(Comparator.comparingInt(CategoryDTO::catId))
                .map(dto -> new CategoryResponse(dto.name()))
                .toList();

        return ResponseEntity.ok(categories);
    }

    @GetMapping(value = "/cities", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CityResponse>> getAllCities() {
        List<CityResponse> cities = cityService.getAllCities().stream()
                .map(dto -> new CityResponse(
                        dto.place() + ", " + dto.province(),
                        String.valueOf(dto.zipcode())
                ))
                .toList();

        return ResponseEntity.ok(cities);
    }

    @GetMapping(value = "/providers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProviderResponse>> getAllProviders() {
        List<ProviderDTO> providers = providerService.getAllProviders();

        List<ProviderResponse> responses = providers.stream()
                .map(dto -> userService.getUserById(dto.providerId())
                        .map(userDTO -> ProviderMapper.toResponse(dto, userDTO))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/review-summary")
    public ResponseEntity<?> getReviewSummary(@RequestParam("providerId") Long providerId)
    {
        System.out.println("Received request for review summary for providerId: " + providerId);
        if (!providerService.isProvider(providerId)) {
            return ResponseEntity.status(403).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_FORBIDDEN)
                            .message("Not a provider")
                            .build()
            );
        }

        return providerService.getProviderReviewSummary(providerId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.INPUT_NOT_FOUND)
                                .message("No reviews found for this provider")
                                .build()
                ));
    }

    @GetMapping("/task/frequency-options")
    public ResponseEntity<?> getFrequencyOptions() {
        return ResponseEntity.ok(Arrays.stream(Task.Frequency.values()).map(Enum::name).toList());
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserById(@RequestParam("userId") Long userId) {
        return userService.getUserById(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.INPUT_NOT_FOUND)
                                .message("User not found")
                                .build()));
    }

}