package queeup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queeup.api.entities.User;


public interface UserRepository extends JpaRepository<User,Long> {

}
