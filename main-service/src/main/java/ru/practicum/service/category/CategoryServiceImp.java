package ru.practicum.service.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.CategoryEntity;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {
        log.info("Запрос на создание новой категории с названием {}", dto.getName());

        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            log.warn("Категория с названием {} уже существует", dto.getName());
            throw new ConflictException("Категория уже существует");
        }

        CategoryEntity createCategory = categoryRepository.save(CategoryMapper.toEntity(dto));

        log.info("Категория с названием {} и id = {} успешна создана", createCategory.getName(), createCategory.getId());
        return CategoryMapper.toDto(createCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        log.info("Запрос на удаление категории с id = {}", categoryId);

        if (eventRepository.existsByCategory_Id(categoryId)) {
            log.warn("Удаление категория не возможно в связи с наличием связанных с ней событий");
            throw new ConflictException("Категория связанна с событиями");
        }

        log.info("Успешное удаление категории с id = {}", categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategory) {
        log.info("Получен запрос на изменение категории с id = {} на {}", categoryId, newCategory.getName());

        CategoryEntity findCategory = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.warn("Категория с id = {} не найдена", categoryId);
            return new NotFoundException("Категория не найдена");
        });

        if (!findCategory.getName().equals(newCategory.getName()) && categoryRepository.existsByNameIgnoreCase(newCategory.getName())) {
            log.warn("Категория с названием {} уже существует", newCategory.getName());
            throw new ConflictException("Категория уже существует");
        }

        findCategory.setName(newCategory.getName());

        CategoryEntity updateCategory = categoryRepository.save(findCategory);

        log.info("Категория с id = {} успешно обновлена", updateCategory.getId());
        return CategoryMapper.toDto(updateCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategoryList(Pageable pageable) {
        log.info("Запрос на список категорий");

        List<CategoryEntity> entityList = categoryRepository.findAll(pageable).toList();

        log.info("Найден список категорий в размере {}", entityList.size());
        return CategoryMapper.toDtoList(entityList);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategory(Long categoryId) {
        log.info("Запрос на получение информации категории с id = {}", categoryId);

        CategoryEntity findCategory = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.warn("Категория с id = {} не найдена", categoryId);
            return new NotFoundException("Category was not found with id " + categoryId);
        });

        log.info("Категория с id = {} и названием {} успешно найдена", findCategory.getId(), findCategory.getName());
        return CategoryMapper.toDto(findCategory);
    }
}