package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    @NonNull
    Iterable<Category> findAll();
}
