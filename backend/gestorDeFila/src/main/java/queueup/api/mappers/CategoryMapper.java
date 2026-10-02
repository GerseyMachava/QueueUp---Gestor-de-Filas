package queueup.api.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import queueup.api.dtos.requests.CategoryRequestDto;

import queueup.api.dtos.responses.CategoryResponseDto;
import queueup.api.entities.Business;
import queueup.api.entities.Category;

import queueup.api.entities.enums.Priority;

@Component 
@AllArgsConstructor 
public class CategoryMapper {

     public CategoryResponseDto toResponseDto(Category entity) {
        return new CategoryResponseDto(
                entity.getName(),
                entity.getDefaultPriority().toString(),
                entity.getAverageServiceTimeMinutes(),
                entity.getPrefix(),
                entity.getBusiness().getId()

        );
    }

    public Category toEntity (CategoryRequestDto requestDto, Business business){
        return  Category.builder()
        .name(requestDto.name())
        .defaultPriority(Enum.valueOf( Priority.class, requestDto.defaultPriority()))
        .averageServiceTimeMinutes(requestDto.averageServiceTimeMinutes())
        .business(business)
        .prefix(requestDto.prefix())
        .build();
              
}

    public void updateEntityFromRequestDto(CategoryRequestDto requestDto, Category existingEntity, Business business) {
        existingEntity.setName(requestDto.name());;
        existingEntity.setDefaultPriority(Enum.valueOf( Priority.class, requestDto.defaultPriority()));
        existingEntity.setAverageServiceTimeMinutes(requestDto.averageServiceTimeMinutes());
        existingEntity.setBusiness(business);
        existingEntity.setPrefix(requestDto.prefix());
    }

    public Page<CategoryResponseDto> toResponseDtoPage(Page<Category> entityCategory) {
        return entityCategory.map(this::toResponseDto);
    }
}
