package queueup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queueup.api.entities.User;


public interface UserRepository extends JpaRepository<User,Long> {

}
