package queeup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queeup.api.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
