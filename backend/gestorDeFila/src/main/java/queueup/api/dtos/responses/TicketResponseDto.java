package queueup.api.dtos.responses;

import java.time.LocalDateTime;

public record TicketResponseDto(
     Long id,
    String ticketNumber,
    String customerName,
    String customerContact,
    String priority,
    String status,
    LocalDateTime calledAt,
    LocalDateTime servedAt,
    LocalDateTime finishedAt,
    Long businessId,
    Long categoryId,
    String categoryName
) {
   
}
