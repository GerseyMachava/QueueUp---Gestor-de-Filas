package queueup.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.auth.Services.AuthService;
import queueup.api.dtos.requests.CategoryRequestDto;
import queueup.api.dtos.responses.CategoryResponseDto;
import queueup.api.entities.Business;
import queueup.api.entities.Category;
import queueup.api.mappers.CategoryMapper;
import queueup.api.repositories.CategoryRepository;
import queueup.api.shared.exceptions.BusinessException;
import queueup.api.shared.exceptions.ResourceNotFoundException;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BusinessService businessService;
    private final CategoryMapper mapper;
    private final AuthService authService;

    private void validateCategory(CategoryRequestDto requestDto, Long id) {
        boolean isUpdate = (id != null);

        boolean nameExists = isUpdate
                ? categoryRepository.existsByNameAndBusinessIdAndIdNot(
                        requestDto.name(), requestDto.businessId(), id)
                : categoryRepository.existsByNameAndBusinessId(
                        requestDto.name(), requestDto.businessId());

        if (nameExists) {
            throw new BusinessException("A category with this name already exists for this business");
        }

        boolean prefixExists = isUpdate
                ? categoryRepository.existsByPrefixAndBusinessIdAndIdNot(
                        requestDto.prefix(), requestDto.businessId(), id)
                : categoryRepository.existsByPrefixAndBusinessId(
                        requestDto.prefix(), requestDto.businessId());

        if (prefixExists) {
            throw new BusinessException("A category with this prefix already exists for this business");
        }
    }

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        authService.ensureCanAccess(requestDto.businessId());

        validateCategory(requestDto, null);

        Business business = businessService.returnBusinessEntityById(requestDto.businessId());
        return mapper.toResponseDto(categoryRepository.save(mapper.toEntity(requestDto, business)));
    }

    public CategoryResponseDto updateCategory(CategoryRequestDto requestDto, Long id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id " + id));

        authService.ensureCanAccess(existingCategory.getBusiness().getId());

        validateCategoryForExisting(requestDto, existingCategory);

        mapper.updateEntityFromRequestDto(
                requestDto, existingCategory, existingCategory.getBusiness());

        return mapper.toResponseDto(categoryRepository.save(existingCategory));
    }

    private void validateCategoryForExisting(CategoryRequestDto requestDto, Category existing) {
        Long businessId = existing.getBusiness().getId();

        if (categoryRepository.existsByNameAndBusinessIdAndIdNot(
                requestDto.name(), businessId, existing.getId())) {
            throw new BusinessException("A category with this name already exists for this business");
        }
        if (categoryRepository.existsByPrefixAndBusinessIdAndIdNot(
                requestDto.prefix(), businessId, existing.getId())) {
            throw new BusinessException("A category with this prefix already exists for this business");
        }
    }

    public Page<CategoryResponseDto> getAllCategories(Pageable page) {
        Long businessId = authService.isSuperAdmin()
                ? null
                : authService.getLoggedInBusinessId();
        return mapper.toResponseDtoPage(
                categoryRepository.findAllFilteredByBusiness(businessId, page));
    }

    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with the id " + id));

        authService.ensureCanAccess(category.getBusiness().getId());

        return mapper.toResponseDto(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id " + id));

        authService.ensureCanAccess(category.getBusiness().getId());

        categoryRepository.delete(category);
    }
}