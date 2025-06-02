package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ProviderCertificate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderCertificateRepository extends CrudRepository<ProviderCertificate, Long> {
}
