package queueup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queueup.api.entities.Business;

public interface BusinessRepository extends JpaRepository<Business,Long> {

    boolean existsByNameAndAddressAndContact(String name, String address, String contact);

}
