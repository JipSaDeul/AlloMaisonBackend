package com.example.allomaison.Mapper;

import com.example.allomaison.Entities.Category;
import com.example.allomaison.DTOs.CategoryDTO;

public class CategoryMapper {
    public static CategoryDTO toDTO(Category category) {
        return new CategoryDTO(category.getCatId(), category.getName());
    }
}
