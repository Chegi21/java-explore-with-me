package ru.practicum.mapper;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.CategoryEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {
    public static CategoryEntity toEntity(NewCategoryDto dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName());
        return entity;
    }

    public static CategoryDto toDto(CategoryEntity entity) {
        return new CategoryDto(entity.getId(), entity.getName());
    }

    public static List<CategoryDto> toDtoList(List<CategoryEntity> dtoList) {
        return dtoList.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }
}
