package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.City;
import com.example.allomaison.DTOs.CityDistanceProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends CrudRepository<City, Integer> {

    Optional<City> findByZipcode(Integer zipcode);

    Optional<City> findByPlaceAndProvince(String place, String province);

    @Query(value = """
            SELECT
                target.zipcode AS zipcode,
                target.place AS place,
                target.province AS province,
                ROUND(6371 * 2 * ASIN(SQRT(
                    POWER(SIN(RADIANS((target.latitude - origin.latitude) / 10000 / 2)), 2) +
                    COS(RADIANS(origin.latitude / 10000)) * COS(RADIANS(target.latitude / 10000)) *
                    POWER(SIN(RADIANS((target.longitude - origin.longitude) / 10000 / 2)), 2)
                )), 2) AS distanceKm
            FROM Cities origin, Cities target
            WHERE origin.zipcode = :originZipcode
              AND 6371 * 2 * ASIN(SQRT(
                    POWER(SIN(RADIANS((target.latitude - origin.latitude) / 10000 / 2)), 2) +
                    COS(RADIANS(origin.latitude / 10000)) * COS(RADIANS(target.latitude / 10000)) *
                    POWER(SIN(RADIANS((target.longitude - origin.longitude) / 10000 / 2)), 2)
                )) <= :maxDistance
            ORDER BY distanceKm
            """, nativeQuery = true)
    List<CityDistanceProjection> findNearbyCities(
            @Param("originZipcode") Integer originZipcode,
            @Param("maxDistance") Double maxDistance
    );

    List<City> findAll();
}
