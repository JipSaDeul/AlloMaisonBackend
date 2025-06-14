package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.Admin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Long> {

    Optional<Admin> findByAdminName(String adminName);
}
