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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.CategoryRequestDto;
import queueup.api.dtos.responses.CategoryResponseDto;
import queueup.api.services.CategoryService;
import queueup.api.shared.apiResponse.ApiResponse;
import org.springframework.data.domain.Sort;

@RequestMapping("/api/categories")
@RestController
@AllArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponseDto>>> index(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        Page<CategoryResponseDto> categories = service.getAllCategories(pageable);
        String message = categories.isEmpty() ? "No categories found" : "categories fetched";
        return ResponseEntity.ok(ApiResponse.success(message, categories, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(
            @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity
                .ok(ApiResponse.success("Category created", service.createCategory(requestDto), HttpStatus.OK));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> update(
            @PathVariable Long id,
            @RequestBody CategoryRequestDto requestDto) {
        return ResponseEntity
                .ok(ApiResponse.success("Category updated", service.updateCategory(requestDto, id), HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Category fetched", service.getCategoryById(id), HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(
            @PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", "", HttpStatus.OK));
    }

}
