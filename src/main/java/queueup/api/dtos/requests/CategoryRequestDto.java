package queueup.api.dtos.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(
        @NotBlank(message = "The name can not be null")
        String name,

        @NotBlank(message = "The default priority can not be null")
        String defaultPriority,

        @Min(value = 1, message = "The average service time must be at least 1 minute")
        int averageServiceTimeMinutes,

        @NotBlank(message = "The prefix can not be null")
        @Size(min = 2, message = "The prefix must be at least 2 characters")
        String prefix,

        @NotNull(message = "The business id can not be null")
        Long businessId
) {
}