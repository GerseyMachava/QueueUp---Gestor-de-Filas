package queueup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queueup.api.entities.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByNameAndBusinessAndPrefix(String name,Long businessId,String prefix);
    boolean existsByNameAndBusinessAndPrefixAndIdNot(String name,Long businessId,String prefix,Long id);

}
