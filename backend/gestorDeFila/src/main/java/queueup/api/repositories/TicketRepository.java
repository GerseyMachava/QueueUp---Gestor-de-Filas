package queueup.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import queueup.api.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

		@Query("""
						SELECT t FROM Ticket t
						WHERE t.business.id = :businessId
							AND t.category.id = :categoryId
							AND t.status = queueup.api.entities.enums.Status.WAITING
						ORDER BY CASE
								WHEN t.priority = queueup.api.entities.enums.Priority.PRIORITY THEN 0
								ELSE 1
						END, t.createdAt ASC
						""")
		List<Ticket> findWaitingTicketsByBusinessAndCategory(
						@Param("businessId") Long businessId,
						@Param("categoryId") Long categoryId);

		@Query("""
						SELECT COUNT(t) FROM Ticket t, Ticket target
						WHERE target.id = :ticketId
							AND target.status = queueup.api.entities.enums.Status.WAITING
							AND t.business = target.business
							AND t.category = target.category
							AND t.status = queueup.api.entities.enums.Status.WAITING
							AND (
									(t.priority = queueup.api.entities.enums.Priority.PRIORITY
											AND target.priority = queueup.api.entities.enums.Priority.NORMAL)
									OR (t.priority = target.priority AND t.createdAt < target.createdAt)
							)
						""")
		long countWaitingTicketsAheadOf(@Param("ticketId") Long ticketId);

		@Query("""
						SELECT t FROM Ticket t
						WHERE t.business.id = :businessId
							AND t.category.id = :categoryId
							AND t.status = queueup.api.entities.enums.Status.WAITING
						ORDER BY CASE
								WHEN t.priority = queueup.api.entities.enums.Priority.PRIORITY THEN 0
								ELSE 1
						END, t.createdAt ASC
						""")
		Optional<Ticket> findNextWaitingTicketByBusinessAndCategory(
						@Param("businessId") Long businessId,
						@Param("categoryId") Long categoryId,
						Pageable pageable);

}
