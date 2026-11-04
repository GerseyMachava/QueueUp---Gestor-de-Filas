package queueup.api.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import queueup.api.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameAndBusinessAndPrefix(String name, Long businessId, String prefix);

    boolean existsByNameAndBusinessAndPrefixAndIdNot(String name, Long businessId, String prefix, Long id);

    boolean existsByNameAndBusinessIdAndIdNot(String name, Long businessId, Long id);

    boolean existsByNameAndBusinessId(String name, Long businessId);

    boolean existsByPrefixAndBusinessIdAndIdNot(String prefix, Long businessId, Long id);
    boolean existsByPrefixAndBusinessId(String prefix, Long businessId);

    Optional<Category> findByIdAndBusinessId(Long id, Long businessId);

    Page<Category> findAllByBusinessId(Long businessId, Pageable page);

    @Query("""
                SELECT c FROM Category c
                WHERE (:businessId IS NULL OR c.business.id = :businessId)
            """)
    Page<Category> findAllFilteredByBusiness(@Param("businessId") Long businessId, Pageable page);

}
