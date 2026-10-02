package queueup.api.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import queueup.api.dtos.requests.TicketRequestDto;
import queueup.api.dtos.responses.TicketResponseDto;
import queueup.api.entities.Category;
import queueup.api.entities.Ticket;
import queueup.api.entities.enums.Priority;
import queueup.api.entities.enums.Status;

@Component
@AllArgsConstructor
public class TicketMapper {

    public TicketResponseDto toResponseDto(Ticket entity) {
        return new TicketResponseDto(
                entity.getId(),
                entity.getTicketNumber(),
                entity.getCustomerName(),
                entity.getCustomerContact(),
                entity.getPriority().toString(),
                entity.getStatus().toString(),
                entity.getCalledAt(),
                entity.getServedAt(),
                entity.getFinishedAt(),
                entity.getBusiness().getId(),
                entity.getCategory().getId(),
                entity.getCategory().getName()
        );
    }

    public Ticket toEntity(TicketRequestDto requestDto, Category category, String ticketNumber) {
        return Ticket.builder()
                .ticketNumber(ticketNumber)
                .customerName(requestDto.customerName())
                .customerContact(requestDto.customerContact())
                .priority(Enum.valueOf(Priority.class, requestDto.priority()))
                .status(Status.WAITING)
                .business(category.getBusiness())
                .category(category)
                .build();
    }

    public void updateEntityFromRequestDto(TicketRequestDto requestDto, Ticket existingEntity) {
        existingEntity.setCustomerName(requestDto.customerName());
        existingEntity.setCustomerContact(requestDto.customerContact());
        existingEntity.setPriority(Enum.valueOf(Priority.class, requestDto.priority()));
    }

    public Page<TicketResponseDto> toResponseDtoPage(Page<Ticket> entityTicket) {
        return entityTicket.map(this::toResponseDto);
    }
}