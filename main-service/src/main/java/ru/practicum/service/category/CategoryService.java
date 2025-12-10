package ru.practicum.service.category;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getCategoryList(Pageable pageable);

    CategoryDto getCategory(Long categoryId);
}
