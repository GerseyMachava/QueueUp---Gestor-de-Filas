package queueup.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import queueup.api.dtos.responses.PublicTicketStatusDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import queueup.api.dtos.requests.TicketRequestDto;
import queueup.api.dtos.responses.TicketResponseDto;
import queueup.api.services.TicketService;
import queueup.api.shared.apiResponse.ApiResponse;

@RequestMapping("/api/tickets")
@RestController
@AllArgsConstructor
public class TicketController {

    private final TicketService service;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TicketResponseDto>>> index(
            @PageableDefault(sort = "ticketNumber", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        Page<TicketResponseDto> tickets = service.getAllBusinessTickets(pageable);
        String message = tickets.isEmpty() ? "No tickets found" : "tickets fetched";
        return ResponseEntity.ok(ApiResponse.success(message, tickets, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponseDto>> create(
            @Valid @RequestBody TicketRequestDto requestDto) {
        return ResponseEntity
                .ok(ApiResponse.success("Ticket created", service.createTicket(requestDto), HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody TicketRequestDto requestDto) {
        return ResponseEntity
                .ok(ApiResponse.success("Ticket updated", service.updateTicket(requestDto, id), HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponseDto>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Ticket fetched", service.getTicketById(id), HttpStatus.OK));
    }

    @PostMapping("/api/queue/next")
    public ResponseEntity<ApiResponse<TicketResponseDto>> callNext(
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(
                ApiResponse.success("Next ticket called",
                        service.callNext(categoryId), HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(
            @PathVariable Long id) {
        service.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket deleted", "", HttpStatus.OK));
    }

    @GetMapping("/{businessSlug}/queue/{ticketNumber}")
    public ResponseEntity<ApiResponse<PublicTicketStatusDto>> getStatus(
            @PathVariable String businessSlug,
            @PathVariable String ticketNumber) {
        return ResponseEntity.ok(
                ApiResponse.success("Ticket status",
                        service.getPublicStatus(businessSlug, ticketNumber),
                        HttpStatus.OK));
    }
}