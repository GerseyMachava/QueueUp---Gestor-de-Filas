package queueup.api.dtos.responses;

public record NextCalledTickets(
    String ticketNumber,
    String customerNameMasked,
    String status,
    int positionInQueue
) {

}
