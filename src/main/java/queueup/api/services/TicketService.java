package queueup.api.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.auth.Services.AuthService;
import queueup.api.dtos.requests.TicketRequestDto;
import queueup.api.dtos.responses.TicketResponseDto;
import queueup.api.entities.Category;
import queueup.api.entities.Ticket;
import queueup.api.entities.enums.Status;
import queueup.api.mappers.TicketMapper;
import queueup.api.repositories.CategoryRepository;
import queueup.api.repositories.TicketRepository;
import queueup.api.shared.exceptions.BusinessException;
import queueup.api.shared.exceptions.ResourceNotFoundException;
import queueup.api.shared.util.EnumUtils;

@Service
@AllArgsConstructor
public class TicketService {

    private static final String TICKET_NUMBER_FORMAT = "%s%03d";

    private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS = Map.of(
            Status.WAITING, Set.of(Status.CALLED, Status.ABANDONED, Status.NO_SHOW),
            Status.CALLED, Set.of(Status.IN_SERVICE, Status.WAITING, Status.ABANDONED, Status.NO_SHOW),
            Status.IN_SERVICE, Set.of(Status.COMPLETED, Status.ABANDONED),
            Status.COMPLETED, Set.of(),
            Status.ABANDONED, Set.of(),
            Status.NO_SHOW, Set.of());

    private final AuthService authService;
    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final TicketMapper mapper;

    private void validateStatusTransition(Ticket existing, String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            return;
        }
        Status current = existing.getStatus();
        Status next = EnumUtils.parse(Status.class, newStatus, "status");

        if (current == next) {
            return;
        }

        Set<Status> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new BusinessException(
                    "Invalid status transition: " + current + " → " + next
                            + ". Allowed from " + current + ": " + allowed,
                    HttpStatus.BAD_REQUEST);
        }
    }

    public String generateTicketNumber(Category category) {
        LocalDate today = LocalDate.now();
        int nextTicketNumber = ticketRepository.calculateNextTicketNumber(
                category.getBusiness().getId(),
                category.getId(),
                today);
        return String.format(TICKET_NUMBER_FORMAT, category.getPrefix(), nextTicketNumber);
    }

    public TicketResponseDto createTicket(TicketRequestDto requestDto) {
        Long businessId = authService.getLoggedInBusinessId();

        Category category = categoryRepository.findByIdAndBusinessId(
                requestDto.categoryId(), businessId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with this id " + requestDto.categoryId()));

        String ticketNumber = generateTicketNumber(category);
        Ticket ticket = mapper.toEntity(requestDto, category, ticketNumber);
        return mapper.toResponseDto(ticketRepository.save(ticket));
    }

    public TicketResponseDto updateTicket(TicketRequestDto requestDto, Long id) {
        Long businessId = authService.getLoggedInBusinessId();

        Ticket existingTicket = ticketRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));

        validateStatusTransition(existingTicket, requestDto.status());

        // categoryId do request é ignorado — troca exige cancelar + criar novo ticket
        mapper.updateEntityFromRequestDto(requestDto, existingTicket);
        applyStatusTransition(existingTicket, requestDto.status());

        return mapper.toResponseDto(ticketRepository.save(existingTicket));
    }

    public Page<TicketResponseDto> getAllBusinessTickets(Pageable page) {
        Long businessId = authService.getLoggedInBusinessId();
        return mapper.toResponseDtoPage(
                ticketRepository.findAllByBusinessId(businessId, page));
    }

    public TicketResponseDto getTicketById(Long id) {
        Long businessId = authService.getLoggedInBusinessId();
        Ticket ticket = ticketRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));
        return mapper.toResponseDto(ticket);
    }

    public void deleteTicket(Long id) {
        Long businessId = authService.getLoggedInBusinessId();
        Ticket ticket = ticketRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));
        ticketRepository.delete(ticket);
    }

    private void applyStatusTransition(Ticket ticket, String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            return;
        }

        Status status = EnumUtils.parse(Status.class, newStatus, "status");
        LocalDateTime now = LocalDateTime.now();

        if (status == Status.WAITING) {
            ticket.setCalledAt(null);
            ticket.setServedAt(null);
            ticket.setFinishedAt(null);
        } else if (status == Status.CALLED) {
            ticket.setServedAt(null);
            ticket.setFinishedAt(null);
            if (ticket.getCalledAt() == null)
                ticket.setCalledAt(now);
        } else if (status == Status.IN_SERVICE) {
            ticket.setFinishedAt(null);
            if (ticket.getCalledAt() == null)
                ticket.setCalledAt(now);
            if (ticket.getServedAt() == null)
                ticket.setServedAt(now);
        } else if (status == Status.COMPLETED) {
            if (ticket.getCalledAt() == null)
                ticket.setCalledAt(now);
            if (ticket.getServedAt() == null)
                ticket.setServedAt(now);
            if (ticket.getFinishedAt() == null)
                ticket.setFinishedAt(now);
        } else if (status == Status.ABANDONED || status == Status.NO_SHOW) {
            if (ticket.getFinishedAt() == null)
                ticket.setFinishedAt(now);
        }

        ticket.setStatus(status);
    }
}