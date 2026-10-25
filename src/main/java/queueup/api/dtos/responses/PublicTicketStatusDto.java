package queueup.api.dtos.responses;

import java.time.Instant;
import java.time.LocalDateTime;

public record PublicTicketStatusDto(
    String ticketNumber,
    String custormerName,
    String categoryName,
    String status,
    long positionInQueue,
    Integer estimatedWaitingTime,
    Instant calledAt,
    Instant servedAt,
    Instant finishedAts
) {

}
