package queueup.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import queueup.api.dtos.responses.TicketResponseDto;
import queueup.api.services.TicketService;
import queueup.api.shared.apiResponse.ApiResponse;

@RestController
@RequestMapping("/api/queue")
@AllArgsConstructor
public class QueueController {

    private final TicketService ticketService;

    @PreAuthorize("hasRole('ATTENDANT') or hasRole('ADMIN')")

    @PostMapping("/next")
    public ResponseEntity<ApiResponse<TicketResponseDto>> callNext(
            @RequestParam(required = false) Long categoryId) {

        TicketResponseDto ticket = ticketService.callNext(categoryId);
        return ResponseEntity.ok(
                ApiResponse.success("Next Ticket called", ticket, HttpStatus.OK));
    }

}
