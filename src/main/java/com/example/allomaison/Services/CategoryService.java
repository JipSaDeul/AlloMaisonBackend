package com.example.allomaison.Services;

import com.example.allomaison.Repositories.CategoryRepository;
import com.example.allomaison.DTOs.CategoryDTO;
import com.example.allomaison.Mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
                .map(CategoryMapper::toDTO)
                .toList();
    }

    @SuppressWarnings("unused")
    public Optional<CategoryDTO> getById(Integer id) {
        return categoryRepository.findById(id)
                .map(CategoryMapper::toDTO);
    }

    @SuppressWarnings("unused")
    public Optional<CategoryDTO> getByName(String name) {
        return categoryRepository.findByName(name)
                .map(CategoryMapper::toDTO);
    }
}
