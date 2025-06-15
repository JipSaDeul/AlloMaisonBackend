package com.example.allomaison.DTOs.Projections;

public interface CityDistanceProjection {
    Integer getZipcode();
    String getPlace();
    String getProvince();
    Double getDistanceKm();
}
