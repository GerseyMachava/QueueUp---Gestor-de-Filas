package queueup.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.BusinessRequestDto;
import queueup.api.dtos.responses.BusinessResponseDto;
import queueup.api.entities.Business;
import queueup.api.entities.enums.BusinessType;
import queueup.api.mappers.BusinessMapper;
import queueup.api.repositories.BusinessRepository;

@Service
@AllArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessMapper mapper;

    private void validateBusiness(BusinessRequestDto requestDto) {
        if (requestDto == null) {
            throw new RuntimeException("Business request cannot be null");
        }
        if (requestDto.name() == null || requestDto.name().isBlank()) {
            throw new RuntimeException("Business name cannot be empty");
        }
        if (requestDto.contact() == null || requestDto.contact().isBlank()) {
            throw new RuntimeException("Business contact cannot be empty");
        }
        if (requestDto.type() == null || requestDto.type().isBlank()) {
            throw new RuntimeException("Business type cannot be empty");
        }
        if (businessRepository.existsByNameAndAddressAndContact(requestDto.name(), requestDto.address(), requestDto.contact())) {
            throw new RuntimeException("Business with the same name, address, and contact already exists");
        }
        try {
            BusinessType.valueOf(requestDto.type().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid business type: " + requestDto.type());
        }
    }

    public BusinessResponseDto createBusiness(BusinessRequestDto requestDto) {
        validateBusiness(requestDto);
        return mapper.toResponseDto(businessRepository.save(mapper.toEntity(requestDto)));
    }

    public BusinessResponseDto updateBusiness(BusinessRequestDto requestDto, Long id) {
        validateBusiness(requestDto);
        Business existingBusiness = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("business not found with this id " + id));
        mapper.updateEntityFromRequestDto(requestDto, existingBusiness);
        return mapper.toResponseDto(businessRepository.save(existingBusiness));
    }

    public Page <BusinessResponseDto> getAllBusinesses(Pageable page) {
        return mapper.toResponseDtoPage(businessRepository.findAll(page));
    }
    public BusinessResponseDto getBusinessById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("business not found with this id " + id));
        return mapper.toResponseDto(business);
    }

    public void deleteBusiness(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("business not found with this id " + id));
        businessRepository.delete(business);
    }

    public Business returnBusinessEntityById(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("business not found with this id " + id));
    }
}
