package queueup.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.BusinessRequestDto;
import queueup.api.dtos.responses.BusinessResponseDto;
import queueup.api.entities.Business;
import queueup.api.mappers.BusinessMapper;
import queueup.api.repositories.BusinessRepository;

@Service
@AllArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessMapper mapper;

    // TODO
    private void validateBusiness(BusinessRequestDto requestDto) {

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
}
