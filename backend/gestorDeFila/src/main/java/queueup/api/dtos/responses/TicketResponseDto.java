package queueup.api.dtos.responses;

import java.time.Instant;
import java.time.LocalDateTime;

public record TicketResponseDto(
        Long id,
        String ticketNumber,
        String customerName,
        String customerContact,
        String priority,
        String status,
        Instant calledAt,
        Instant servedAt,
        Instant finishedAt,
        Instant createdAt,
        Long businessId,
        Long categoryId,
        String categoryName) {

}
