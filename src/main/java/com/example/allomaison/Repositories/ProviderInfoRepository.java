package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ProviderInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderInfoRepository extends CrudRepository<ProviderInfo, Long> {

    // Find by providerId (userId) — inherited from CrudRepository: findById(Long id)

    // Check if a user is a provider (i.e. has a ProviderInfo record)
    boolean existsByProviderId(Long providerId);
}
