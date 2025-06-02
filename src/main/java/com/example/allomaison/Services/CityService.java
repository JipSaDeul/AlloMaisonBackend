package com.example.allomaison.Services;

import com.example.allomaison.Repositories.CityRepository;
import com.example.allomaison.DTOs.CityDTO;
import com.example.allomaison.DTOs.CityDistanceProjection;
import com.example.allomaison.Mapper.CityMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public Optional<CityDTO> getCityByZipcode(Integer zipcode) {
        return cityRepository.findByZipcode(zipcode)
                .map(CityMapper::toDTO);
    }

    public Optional<CityDTO> getCityByPlaceAndProvince(String place, String province) {
        return cityRepository.findByPlaceAndProvince(place, province)
                .map(CityMapper::toDTO);
    }

    public List<Pair<CityDTO, Double>> getNearbyCities(Integer originZipcode, Double maxDistanceKm) {
        List<CityDistanceProjection> projections = cityRepository.findNearbyCities(originZipcode, maxDistanceKm);
        return projections.stream()
                .map(p -> Pair.of(
                        new CityDTO(p.getZipcode(), p.getPlace(), p.getProvince()),
                        p.getDistanceKm()))
                .collect(Collectors.toList());
    }
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
                .map(CityMapper::toDTO)
                .collect(Collectors.toList());
    }
}
