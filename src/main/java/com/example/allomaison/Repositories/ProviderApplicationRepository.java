package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ProviderApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderApplicationRepository extends CrudRepository<ProviderApplication, Long> {
    List<ProviderApplication> findByStatus(ProviderApplication.ApplicationStatus status);

    List<ProviderApplication> findByUserId(Long userId);
}
