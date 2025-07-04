package com.example.allomaison.Services;

import com.example.allomaison.Repositories.CityRepository;
import com.example.allomaison.DTOs.CityDTO;
import com.example.allomaison.DTOs.Projections.CityDistanceProjection;
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

    @SuppressWarnings("unused")
    public Optional<CityDTO> getCityByZipcode(Integer zipcode) {
        return cityRepository.findByZipcode(zipcode)
                .map(CityMapper::toDTO);
    }

    @SuppressWarnings("unused")
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
    public Optional<CityDTO> getCityFromFormattedString(String cityString) {
        if (cityString == null || !cityString.contains(",")) {
            return Optional.empty();
        }

        String[] parts = cityString.split(",", 2);
        if (parts.length < 2) {
            return Optional.empty();
        }

        String place = parts[0].trim();
        String province = parts[1].trim();

        return getCityByPlaceAndProvince(place, province);
    }

}
