package queueup.api.dtos.responses;

import java.time.Instant;

public record CurrentCalledTicket(
    String ticketNumber,
    String customerNameMasked,
    String categoryName,
    Instant calledAt
) {

}
