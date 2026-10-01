package queueup.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.BusinessRequestDto;
import queueup.api.dtos.responses.BusinessResponseDto;
import queueup.api.services.BusinessService;
import queueup.api.shared.apiResponse.ApiResponse;

@RequestMapping("/api/businesses")
@RestController
@AllArgsConstructor
public class BusinessController {

    private final BusinessService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BusinessResponseDto>>> index(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        Page<BusinessResponseDto> businesses = service.getAllBusinesses(pageable);
        String message = businesses.isEmpty() ? "No businesses found" : "Business fetched";
        return ResponseEntity.ok(ApiResponse.success(message, businesses, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BusinessResponseDto>> create(
            BusinessRequestDto requestDto) {
        return ResponseEntity
                .ok(ApiResponse.success("Business created", service.createBusiness(requestDto), HttpStatus.OK));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessResponseDto>> update(
            @PathVariable Long id,
            BusinessRequestDto requestDto) {
        return ResponseEntity
                .ok(ApiResponse.success("Business updated", service.updateBusiness(requestDto, id), HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessResponseDto>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Business fetched", service.getBusinessById(id), HttpStatus.OK));
    }

    @DeleteMapping ("/{id}") 
    public ResponseEntity<ApiResponse<?>> delete(
        @PathVariable Long id
          ) { 
            service.deleteBusiness(id);
        return ResponseEntity.ok(ApiResponse.success("Business fetched", "", HttpStatus.OK));
    }
}
