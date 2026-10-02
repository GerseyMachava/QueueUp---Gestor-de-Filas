package queueup.api.services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.TicketRequestDto;
import queueup.api.dtos.responses.TicketResponseDto;
import queueup.api.entities.Category;
import queueup.api.entities.Ticket;
import queueup.api.entities.enums.Status;
import queueup.api.mappers.TicketMapper;
import queueup.api.repositories.CategoryRepository;
import queueup.api.repositories.TicketRepository;
import queueup.api.repositories.UserRepository;
import queueup.api.shared.exceptions.ResourceNotFoundException;

@Service
@AllArgsConstructor
public class TicketService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final TicketMapper mapper;

    private void validateTicket(TicketRequestDto requestDto) {
        // TODO: B3 — implementar validações
    }

    // TODO: B1 — proteger contra race condition
    public String generateTicketNumber(Category category) {
        LocalDate today = LocalDate.now();
        int nextTicketNumber = ticketRepository.calculateNextTicketNumber(
                category.getBusiness().getId(),
                category.getId(),
                today
        );
        return String.format("%s%03d", category.getPrefix(), nextTicketNumber);
    }

    public TicketResponseDto createTicket(TicketRequestDto requestDto) {
        validateTicket(requestDto);
        Category category = categoryRepository.findById(requestDto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id " + requestDto.categoryId()));
        String ticketNumber = generateTicketNumber(category);
        Ticket ticket = mapper.toEntity(requestDto, category, ticketNumber);
        return mapper.toResponseDto(ticketRepository.save(ticket));
    }

    public TicketResponseDto updateTicket(TicketRequestDto requestDto, Long id) {
        validateTicket(requestDto);
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));

        // categoryId do request é ignorado — ticket não troca de categoria/business (multi-tenancy)
        mapper.updateEntityFromRequestDto(requestDto, existingTicket);
        applyStatusTransition(existingTicket, requestDto.status());

        return mapper.toResponseDto(ticketRepository.save(existingTicket));
    }

    private void applyStatusTransition(Ticket ticket, String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            return;
        }
        Status status = Enum.valueOf(Status.class, newStatus);
        LocalDateTime now = LocalDateTime.now();

        if (status == Status.CALLED && ticket.getCalledAt() == null) {
            ticket.setCalledAt(now);
        } else if (status == Status.IN_SERVICE && ticket.getServedAt() == null) {
            ticket.setServedAt(now);
        } else if (status == Status.COMPLETED && ticket.getFinishedAt() == null) {
            ticket.setFinishedAt(now);
        }

        ticket.setStatus(status);
    }

    public Page<TicketResponseDto> getAllTickets(Pageable page) {
        return mapper.toResponseDtoPage(ticketRepository.findAll(page));
    }

    public TicketResponseDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));
        return mapper.toResponseDto(ticket);
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));
        ticketRepository.delete(ticket);
    }
}