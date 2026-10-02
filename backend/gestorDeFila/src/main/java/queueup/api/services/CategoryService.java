package queueup.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.CategoryRequestDto;
import queueup.api.dtos.responses.CategoryResponseDto;
import queueup.api.entities.Business;
import queueup.api.entities.Category;
import queueup.api.mappers.CategoryMapper;
import queueup.api.repositories.CategoryRepository;
import queueup.api.shared.exceptions.ResourceNotFoundException;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BusinessService businessService;
    private final CategoryMapper mapper;

    private void validateCategory(CategoryRequestDto requestDto) {
        // TODO: B3 — implementar validações
    }

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        validateCategory(requestDto);
        Business business = businessService.returnBusinessEntityById(requestDto.businessId());
        return mapper.toResponseDto(categoryRepository.save(mapper.toEntity(requestDto, business)));
    }

    public CategoryResponseDto updateCategory(CategoryRequestDto requestDto, Long id) {
        validateCategory(requestDto);
        Business business = businessService.returnBusinessEntityById(requestDto.businessId());
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id " + id));
        mapper.updateEntityFromRequestDto(requestDto, existingCategory, business);
        return mapper.toResponseDto(categoryRepository.save(existingCategory));
    }

    public Page<CategoryResponseDto> getAllCategories(Pageable page) {
        return mapper.toResponseDtoPage(categoryRepository.findAll(page));
    }

    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id " + id));
        return mapper.toResponseDto(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id " + id));
        categoryRepository.delete(category);
    }
}