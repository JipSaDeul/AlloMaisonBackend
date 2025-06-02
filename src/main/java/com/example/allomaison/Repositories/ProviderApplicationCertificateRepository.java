package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ProviderApplicationCertificate;
import com.example.allomaison.Entities.ProviderApplicationCertificate.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderApplicationCertificateRepository extends CrudRepository<ProviderApplicationCertificate, Id> {

    List<ProviderApplicationCertificate> findByIdApplicationId(Long applicationId);
}
