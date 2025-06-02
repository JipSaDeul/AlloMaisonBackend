package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ProviderInfoLabel;
import com.example.allomaison.Entities.ProviderInfoLabel.Id;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProviderInfoLabelRepository extends CrudRepository<ProviderInfoLabel, Id> {
    List<ProviderInfoLabel> findByIdProviderId(Long providerId);
}
