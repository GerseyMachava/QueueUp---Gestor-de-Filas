package queueup.api.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.BusinessRequestDto;
import queueup.api.dtos.responses.BusinessResponseDto;
import queueup.api.entities.Business;
import queueup.api.entities.enums.BusinessType;

@Component
@AllArgsConstructor
public class BusinessMapper {

    public BusinessResponseDto toResponseDto(Business entity) {
        return new BusinessResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getContact(),
                entity.getType().toString()

        );
    }

    public Business toEntity (BusinessRequestDto requestDto){
        return  Business.builder()
        .name(requestDto.name())
        .address(requestDto.address())
        .contact(requestDto.contact())
        .type(Enum.valueOf(BusinessType.class, requestDto.type()))
        .build();
              
}

    public void updateEntityFromRequestDto(BusinessRequestDto requestDto, Business existingEntity) {
        existingEntity.setName(requestDto.name());
        existingEntity.setAddress(requestDto.address());
        existingEntity.setContact(requestDto.contact());
        existingEntity.setType(Enum.valueOf(BusinessType.class, requestDto.type()));
    }

    public Page<BusinessResponseDto> toResponseDtoPage(Page<Business> entityPage) {
        return entityPage.map(this::toResponseDto);
    }



}