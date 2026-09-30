package queeup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queeup.api.entities.Notification;


public interface NotificationRepository extends JpaRepository<Notification,Long>{

}
