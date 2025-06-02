package com.example.allomaison.Mapper;

import com.example.allomaison.Entities.City;
import com.example.allomaison.DTOs.CityDTO;

public class CityMapper {
    public static CityDTO toDTO(City city) {
        return new CityDTO(city.getZipcode(), city.getPlace(), city.getProvince());
    }
}
