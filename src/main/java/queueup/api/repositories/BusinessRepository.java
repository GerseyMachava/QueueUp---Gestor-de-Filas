package queueup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queueup.api.entities.Business;

public interface BusinessRepository extends JpaRepository<Business,Long> {

}
