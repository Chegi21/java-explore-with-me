package ru.practicum.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Validated
public class PubCategoryController {
    private final CategoryService categoryService;

    @Autowired
    public PubCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategoryList(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return categoryService.getCategoryList(PageRequest.of(from / size, size));
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable Long catId) {
        return categoryService.getCategory(catId);
    }
}
