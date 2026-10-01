package queueup.api.dtos.requests;


public record TicketRequestDto(
    String customerName,
    String customerContact,
    String priority,
    String status,
    long categoryId



) {

}
