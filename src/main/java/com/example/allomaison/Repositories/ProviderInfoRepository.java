package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ProviderInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderInfoRepository extends CrudRepository<ProviderInfo, Long> {

    // Find by providerId (userId) — inherited from CrudRepository: findById(Long id)

    // Check if a user is a provider (i.e. has a ProviderInfo record)
    boolean existsByProviderId(Long providerId);

    @NonNull
    @Override
    List<ProviderInfo> findAll();
}
