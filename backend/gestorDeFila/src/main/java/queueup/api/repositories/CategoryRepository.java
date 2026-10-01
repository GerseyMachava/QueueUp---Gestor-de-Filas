package queueup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queueup.api.entities.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}
