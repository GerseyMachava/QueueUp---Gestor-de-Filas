package queueup.api.services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import queueup.api.entities.Category;
import queueup.api.repositories.TicketRepository;
import queueup.api.repositories.UserRepository;

@Service 
@AllArgsConstructor 
public class TicketService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    

        public String generateTicketNumber(Category category) {
        LocalDate today = LocalDate.now();
        int nextTicketNumber = ticketRepository.calculateNextTicketNumber(
                category.getBusiness().getId(),
                category.getId(),
                today
        );
        return String.format("%s%03d", category.getPrefix(), nextTicketNumber);
    }
}
