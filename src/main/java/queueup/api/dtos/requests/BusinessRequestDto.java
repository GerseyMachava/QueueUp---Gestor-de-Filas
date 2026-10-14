package queueup.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record BusinessRequestDto(
        @NotBlank(message = "The business name can not be null")
        String name,
        String address,
        String contact,
        @NotBlank(message = "The business type can not be null")
        String type
) {
}