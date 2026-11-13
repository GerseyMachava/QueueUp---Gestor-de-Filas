package queueup.api.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import queueup.api.dtos.requests.TicketRequestDto;
import queueup.api.dtos.responses.CurrentCalledTicket;
import queueup.api.dtos.responses.NextCalledTickets;
import queueup.api.dtos.responses.PublicDashboardResponse;
import queueup.api.dtos.responses.PublicTicketStatusDto;
import queueup.api.dtos.responses.TicketResponseDto;
import queueup.api.entities.Category;
import queueup.api.entities.Ticket;
import queueup.api.entities.enums.Priority;
import queueup.api.entities.enums.Status;
import queueup.api.shared.util.EnumUtils;
import queueup.api.shared.util.PublicCodeGenerator;

@Component
@AllArgsConstructor
public class TicketMapper {

    private String maskName(String name) {
        if (name == null || name.isBlank())
            return "";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1)
            return parts[0];
        return parts[0] + " " + parts[parts.length - 1].charAt(0) + ".";
    }

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
                entity.getCreatedAt(),
                entity.getBusiness().getId(),
                entity.getCategory().getId(),
                entity.getCategory().getName());
    }

    public Ticket toEntity(TicketRequestDto requestDto, Category category, String ticketNumber) {
        return Ticket.builder()
                .ticketNumber(ticketNumber)
                .publicCode(PublicCodeGenerator.generate())
                .customerName(requestDto.customerName())
                .customerContact(requestDto.customerContact())
                .priority(EnumUtils.parse(Priority.class, requestDto.priority(), "priority"))
                .status(Status.WAITING)
                .business(category.getBusiness())
                .category(category)
                .build();
    }

    public void updateEntityFromRequestDto(TicketRequestDto requestDto, Ticket existingEntity) {
        existingEntity.setCustomerName(requestDto.customerName());
        existingEntity.setCustomerContact(requestDto.customerContact());
        existingEntity.setPriority(EnumUtils.parse(Priority.class, requestDto.priority(), "priority"));
    }

    public Page<TicketResponseDto> toResponseDtoPage(Page<Ticket> entityTicket) {
        return entityTicket.map(this::toResponseDto);
    }

    private CurrentCalledTicket toCurrentCalledTicket(Ticket currentTicket) {
        return new CurrentCalledTicket(
                currentTicket.getTicketNumber(),
                maskName(currentTicket.getCustomerName()),
                currentTicket.getCategory().getName(),
                currentTicket.getCalledAt());
    }

    public PublicDashboardResponse toPublicDashboardResponse(Ticket currentCalledTicket,
            List<Ticket> nextCalledTickets) {
        CurrentCalledTicket current = toCurrentCalledTicket(currentCalledTicket);
        List<NextCalledTickets> next = nextCalledTickets.stream()
                .map(ticket -> new NextCalledTickets(
                        ticket.getTicketNumber(),
                        maskName(ticket.getCustomerName()),
                        ticket.getStatus().name(),
                        0))
                .toList();

        return new PublicDashboardResponse(current, next);

    }

    public PublicTicketStatusDto toPublicTicketStatusDto(Ticket ticket, Integer estimatedWaitMinutes, long position) {
        return new PublicTicketStatusDto(
                ticket.getTicketNumber(),
                maskName(ticket.getCustomerName()),
                ticket.getCategory().getName(),
                ticket.getStatus().name(),
                position,
                estimatedWaitMinutes,
                ticket.getCalledAt(),
                ticket.getServedAt(),
                ticket.getFinishedAt());

    }

}