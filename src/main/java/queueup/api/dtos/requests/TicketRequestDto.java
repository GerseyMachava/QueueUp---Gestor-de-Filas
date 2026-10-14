package queueup.api.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRequestDto(
        @NotBlank(message = "The customer name can not be null") String customerName,

        @NotBlank(message = "The customer contact can not be null") String customerContact,

        @NotBlank(message = "The priority can not be null") String priority,

        String status,

        @NotNull(message = "The category id can not be null") Long categoryId) {
}