package com.example.allomaison.DTOs.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityResponse {
    private String city;
    private String zipcode;
}
