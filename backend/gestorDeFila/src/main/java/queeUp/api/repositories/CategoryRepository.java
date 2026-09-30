package queeup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queeup.api.entities.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}
