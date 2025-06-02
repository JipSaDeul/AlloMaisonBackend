package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ProviderLabel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderLabelRepository extends CrudRepository<ProviderLabel, Long> {
    Optional<ProviderLabel> findByName(String name);
}
