package queueup.api.repositories;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import queueup.api.entities.Ticket;
import queueup.api.entities.enums.Priority;
import queueup.api.entities.enums.Status;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	@Query("""
			SELECT COUNT(t) FROM Ticket t
			WHERE t.business.id = :businessId
				AND t.category.id = :categoryId
				AND t.status = queueup.api.entities.enums.Status.WAITING
				AND (
					(t.priority = queueup.api.entities.enums.Priority.PRIORITY
						AND :priority = queueup.api.entities.enums.Priority.NORMAL)
					OR (t.priority = :priority
						AND (t.createdAt < :createdAt
							OR (t.createdAt = :createdAt AND t.id < :ticketId)))
				)
			""")
	long countWaitingTicketsAheadOf(
			@Param("businessId") Long businessId,
			@Param("categoryId") Long categoryId,
			@Param("priority") Priority priority,
			@Param("createdAt") Instant createdAt,
			@Param("ticketId") Long ticketId);

	@Query("""
			SELECT t FROM Ticket t
			WHERE t.business.id = :businessId
				AND t.category.id = :categoryId
				AND t.status = queueup.api.entities.enums.Status.WAITING
			ORDER BY CASE
					WHEN t.priority = queueup.api.entities.enums.Priority.PRIORITY THEN 0
					ELSE 1
			END, t.createdAt ASC, t.id ASC
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

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT t FROM Ticket t
			WHERE t.business.id = :businessId
			    AND (:categoryId IS NULL OR t.category.id = :categoryId)
			    AND t.status = queueup.api.entities.enums.Status.WAITING
			ORDER BY CASE
			        WHEN t.priority = queueup.api.entities.enums.Priority.PRIORITY THEN 0
			        ELSE 1
			    END,
			    t.createdAt ASC,
			    t.id ASC
			""")
	List<Ticket> findNextWaiting(
			@Param("businessId") Long businessId,
			@Param("categoryId") Long categoryId,
			Pageable pageable);

	@Query("""
			SELECT t FROM Ticket t
			WHERE t.ticketNumber = :ticketNumber
			    AND t.business.slug = :slug
			""")
	Optional<Ticket> findByTicketNumberAndBusinessSlug(
			@Param("ticketNumber") String ticketNumber,
			@Param("slug") String slug);

	@Query("""
			SELECT t FROM Ticket t
			WHERE t.publicCode = :publicCode
				AND t.business.slug = :slug
				""")
	Optional<Ticket> findByPublicCodeAndBusinessSlug(@Param("publicCode") String publicCode,
			@Param("slug") String slug);





@Query("""
    SELECT COUNT(t) FROM Ticket t
    WHERE t.business.id = :businessId
      AND t.status = :status
      AND t.finishedAt >= :start
      AND t.finishedAt <  :end
    """)
long countByBusinessAndStatusAndFinishedAtBetween(
        @Param("businessId") Long businessId,
        @Param("status") Status status,
        @Param("start") Instant start,
        @Param("end") Instant end);

long countByBusinessIdAndStatus(Long businessId, Status status);

@Query(value = """
    SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, called_at))
    FROM tickets
    WHERE business_id = :businessId
      AND called_at IS NOT NULL
      AND created_at >= :start
      AND created_at <  :end
    """, nativeQuery = true)
Double avgWaitTimeMinutes(
        @Param("businessId") Long businessId,
        @Param("start") Instant start,
        @Param("end") Instant end);

@Query(value = """
    SELECT
        c.id, c.name, COUNT(t.id),
        AVG(TIMESTAMPDIFF(MINUTE, t.created_at, t.called_at))
    FROM tickets t
    JOIN categories c ON c.id = t.category_id
    WHERE t.business_id = :businessId
      AND t.status = 'COMPLETED'
      AND t.finished_at >= :start
      AND t.finished_at <  :end
    GROUP BY c.id, c.name
    """, nativeQuery = true)
List<Object[]> getCategoryMetricsRaw(
        @Param("businessId") Long businessId,
        @Param("start") Instant start,
        @Param("end") Instant end);

@Query("""
    SELECT SUM(CASE WHEN t.status = :abandonedStatus THEN 1 ELSE 0 END) * 100.0 / COUNT(t)
    FROM Ticket t
    WHERE t.business.id = :businessId
      AND t.createdAt >= :start
      AND t.createdAt <  :end
    """)
Double abandonmentRate(
        @Param("businessId") Long businessId,
        @Param("abandonedStatus") Status abandonedStatus,
        @Param("start") Instant start,
        @Param("end") Instant end);

@Query("""
    SELECT COUNT(t) FROM Ticket t
    WHERE t.business.id = :businessId
      AND t.createdAt >= :start
      AND t.createdAt <  :end
    """)
long countByBusinessAndCreatedAtBetween(
        @Param("businessId") Long businessId,
        @Param("start") Instant start,
        @Param("end") Instant end);
}