package queueup.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import queueup.api.entities.Notification;


public interface NotificationRepository extends JpaRepository<Notification,Long>{

}
