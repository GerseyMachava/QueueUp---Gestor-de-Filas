package queueup.api.services;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import queueup.api.auth.Services.AuthService;
import queueup.api.dtos.requests.TicketRequestDto;
import queueup.api.dtos.responses.TicketResponseDto;
import queueup.api.dtos.responses.CurrentCalledTicket;
import queueup.api.dtos.responses.PublicDashboardResponse;
import queueup.api.dtos.responses.PublicTicketStatusDto;
import queueup.api.entities.Category;
import queueup.api.entities.Ticket;
import queueup.api.entities.enums.Status;
import queueup.api.mappers.TicketMapper;
import queueup.api.repositories.CategoryRepository;
import queueup.api.repositories.TicketRepository;
import queueup.api.shared.exceptions.BusinessException;
import queueup.api.shared.exceptions.ResourceNotFoundException;
import queueup.api.shared.util.EnumUtils;

@Slf4j
@Service
@AllArgsConstructor
public class TicketService {

    private static final String TICKET_NUMBER_FORMAT = "%s%03d";
    private static final int MAX_RETRIES = 3;
    private static final long BASE_BACKOFF_MS = 20L;

    private static final Map<Status, Set<Status>> ALLOWED_TRANSITIONS = Map.of(
            Status.WAITING, Set.of(Status.CALLED, Status.ABANDONED, Status.NO_SHOW),
            Status.CALLED, Set.of(Status.IN_SERVICE, Status.WAITING, Status.COMPLETED, Status.ABANDONED, Status.NO_SHOW),
            Status.IN_SERVICE, Set.of(Status.COMPLETED, Status.ABANDONED),
            Status.COMPLETED, Set.of(),
            Status.ABANDONED, Set.of(),
            Status.NO_SHOW, Set.of());

    private final AuthService authService;
    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final TicketMapper mapper;

    public String generateTicketNumber(Category category) {
        LocalDate today = LocalDate.now();
        int nextTicketNumber = ticketRepository.calculateNextTicketNumber(
                category.getBusiness().getId(),
                category.getId(),
                today);
        return String.format(TICKET_NUMBER_FORMAT, category.getPrefix(), nextTicketNumber);
    }

    public TicketResponseDto createTicket(TicketRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with this id " + requestDto.categoryId()));

        authService.ensureCanAccess(category.getBusiness().getId());

        return tryCreateWithRetry(requestDto, category, 0);
    }

    private TicketResponseDto tryCreateWithRetry(
            TicketRequestDto requestDto, Category category, int attempt) {

        String ticketNumber = generateTicketNumber(category);
        Ticket ticket = mapper.toEntity(requestDto, category, ticketNumber);

        try {
            Ticket saved = ticketRepository.saveAndFlush(ticket);
            if (attempt > 0) {
                log.warn("Ticket number collision resolved on attempt {} (categoryId={}, businessId={})",
                        attempt, category.getId(), category.getBusiness().getId());
            }
            return mapper.toResponseDto(saved);

        } catch (DataIntegrityViolationException ex) {
            if (attempt >= MAX_RETRIES) {
                log.error("Failed to generate unique ticket number after {} attempts (categoryId={}, businessId={})",
                        MAX_RETRIES, category.getId(), category.getBusiness().getId(), ex);
                throw new BusinessException(
                        "Could not generate a unique ticket number. Please try again.",
                        HttpStatus.CONFLICT);
            }

            long backoff = BASE_BACKOFF_MS * (attempt + 1)
                    + ThreadLocalRandom.current().nextLong(0, BASE_BACKOFF_MS);
            try {
                Thread.sleep(backoff);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new BusinessException(
                        "Interrupted while retrying ticket creation",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return tryCreateWithRetry(requestDto, category, attempt + 1);
        }
    }

    public TicketResponseDto updateTicket(TicketRequestDto requestDto, Long id) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));

        authService.ensureCanAccess(existingTicket.getBusiness().getId());

        validateStatusTransition(existingTicket, requestDto.status());

        mapper.updateEntityFromRequestDto(requestDto, existingTicket);
        applyStatusTransition(existingTicket, requestDto.status());

        return mapper.toResponseDto(ticketRepository.save(existingTicket));
    }

    private void validateStatusTransition(Ticket existing, String newStatus) {
        if (newStatus == null || newStatus.isBlank())
            return;

        Status current = existing.getStatus();
        Status next = EnumUtils.parse(Status.class, newStatus, "status");

        if (current == next)
            return;

        Set<Status> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new BusinessException(
                    "Invalid status transition: " + current + " → " + next
                            + ". Allowed from " + current + ": " + allowed,
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void applyStatusTransition(Ticket ticket, String newStatus) {
        if (newStatus == null || newStatus.isBlank())
            return;

        Status status = EnumUtils.parse(Status.class, newStatus, "status");
        Instant now = Instant.now();

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

    @Transactional(readOnly = true)
    public Page<TicketResponseDto> getAllBusinessTickets(Pageable page) {
        Long businessId = authService.isSuperAdmin()
                ? null
                : authService.getLoggedInBusinessId();
        return mapper.toResponseDtoPage(
                ticketRepository.findAllFilteredByBusiness(businessId, page));
    }

    public TicketResponseDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));

        authService.ensureCanAccess(ticket.getBusiness().getId());

        return mapper.toResponseDto(ticket);
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with this id " + id));

        authService.ensureCanAccess(ticket.getBusiness().getId());

        ticketRepository.delete(ticket);
    }

    @Transactional(readOnly = true)
    public PublicTicketStatusDto getPublicStatus(String businessSlug, String publicCode) {
        Ticket ticket = ticketRepository.findByPublicCodeAndBusinessSlug(publicCode, businessSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        long position = 0;
        Integer estimatedWaitMinutes = null;

        if (ticket.getStatus() == Status.WAITING) {
            position = ticketRepository.countWaitingTicketsAheadOf(
                    ticket.getBusiness().getId(),
                    ticket.getCategory().getId(),
                    ticket.getPriority(),
                    ticket.getCreatedAt(),
                    ticket.getId()) + 1;
            estimatedWaitMinutes = (int) (position * ticket.getCategory().getAverageServiceTimeMinutes());
        }

        return mapper.toPublicTicketStatusDto(ticket, estimatedWaitMinutes, position);

    }

    @Transactional
    public TicketResponseDto callNext(Long categoryId) {
        if (authService.isSuperAdmin()) {
            throw new BusinessException("Super Admin cannot call tickets", HttpStatus.FORBIDDEN);
        }

        Long businessId = authService.getLoggedInBusinessId();
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id " + categoryId));
            authService.ensureCanAccess(category.getBusiness().getId());
        }

        List<Ticket> next = ticketRepository.findNextWaiting(businessId, categoryId, PageRequest.of(0, 1));

        if (next.isEmpty()) {
            log.warn("No waiting tickets (businessId={}, categoryId={})", businessId, categoryId);

            throw new BusinessException("No waiting tickets in this queue", HttpStatus.NO_CONTENT);
        }

        List<Ticket> currentlyCalled = ticketRepository.findCalledTickets(businessId, categoryId);
        for (Ticket calledTicket : currentlyCalled) {
            applyStatusTransition(calledTicket, Status.COMPLETED.name());
        }
        ticketRepository.saveAll(currentlyCalled);

        Ticket ticket = next.get(0);
        applyStatusTransition(ticket, Status.CALLED.name());
        return mapper.toResponseDto(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponseDto findCurrentCalledTicket() {
        Ticket currentCalledTicket = ticketRepository.findByStatus(Status.CALLED)
                .orElseThrow(() -> new BusinessException("No called tickets in this queue", HttpStatus.NO_CONTENT));
        return mapper.toResponseDto(currentCalledTicket);
    }

    @Transactional(readOnly = true)
    public PublicDashboardResponse getPublicDashboardTicket(String businessSlug) {
        Ticket ticket = ticketRepository.findFirstByBusinessSlugAndStatusOrderByCalledAtDesc(
                businessSlug, Status.CALLED)
                .orElseThrow(() -> new BusinessException("No called tickets in this queue", HttpStatus.NO_CONTENT));
        List<Ticket> nextCalledTickets = ticketRepository.findMostRecentTicketsfrombusinessSlug(businessSlug);

        return mapper.toPublicDashboardResponse(ticket, nextCalledTickets);
    }
}