package queueup.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import queueup.api.dtos.responses.PublicTicketStatusDto;
import queueup.api.services.TicketService;
import queueup.api.shared.apiResponse.ApiResponse;

@RequestMapping("/api/public")
@RestController
@AllArgsConstructor
public class PublicAcessController {

    private final TicketService service;

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
