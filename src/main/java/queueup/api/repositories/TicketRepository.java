package queueup.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
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
	List<Ticket> findNextWaitingTicketByBusinessAndCategory(
			@Param("businessId") Long businessId,
			@Param("categoryId") Long categoryId,
			Pageable pageable);

	/*
	 * calcular o próximo número contando os tickets existentes daquele
	 * business+category+dia (COUNT(*) + 1),
	 */
	@Query("""
			SELECT COUNT(t) + 1 FROM Ticket t
			WHERE t.business.id = :businessId
				AND t.category.id = :categoryId
				AND FUNCTION('DATE', t.createdAt) = :today
			""")
	int calculateNextTicketNumber(
			@Param("businessId") Long businessId,
			@Param("categoryId") Long categoryId,
			@Param("today") LocalDate today);

	Page<Ticket> findAllByBusinessId(Long businessId, Pageable pageable);

	Optional<Ticket> findByIdAndBusinessId(Long id, Long businessId);

	@Query("""
			    SELECT t FROM Ticket t
			    WHERE (:businessId IS NULL OR t.business.id = :businessId)
			""")
	Page<Ticket> findAllFilteredByBusiness(@Param("businessId") Long businessId, Pageable pageable);

}
