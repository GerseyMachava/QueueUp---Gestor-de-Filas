package queueup.api.dtos.responses;

public record NextCalledTickets(
    String ticketNumber,
    String customerNamedMasked,
    String status,
    int positionInQueue
) {

}
