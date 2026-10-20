package queueup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import queueup.api.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
