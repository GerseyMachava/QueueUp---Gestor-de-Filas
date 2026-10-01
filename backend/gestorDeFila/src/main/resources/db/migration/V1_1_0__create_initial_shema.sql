CREATE TABLE IF NOT EXISTS businesses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    `type` ENUM('FAST_FOOD', 'SALON', 'CLINIC', 'BANK', 'OTHER') NOT NULL,
    contact VARCHAR(100),
    address VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_business PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'ATTENDANT') NOT NULL DEFAULT 'ATTENDANT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_business FOREIGN KEY (business_id)
        REFERENCES businesses (id) ON DELETE CASCADE,
    INDEX idx_users_business_id (business_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    default_priority ENUM('NORMAL', 'PRIORITY') NOT NULL DEFAULT 'NORMAL',
    average_service_time INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT fk_categories_business FOREIGN KEY (business_id)
        REFERENCES businesses (id) ON DELETE CASCADE,
    INDEX idx_categories_business_id (business_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    ticket_number VARCHAR(50) NOT NULL,
    customer_name VARCHAR(150),
    customer_contact VARCHAR(150),
    priority ENUM('NORMAL', 'PRIORITY') NOT NULL DEFAULT 'NORMAL',
    status ENUM('WAITING', 'CALLED', 'IN_SERVICE', 'COMPLETED', 'ABANDONED', 'NO_SHOW') NOT NULL DEFAULT 'WAITING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    called_at TIMESTAMP NULL,
    served_at TIMESTAMP NULL,
    finished_at TIMESTAMP NULL,
    CONSTRAINT pk_tickets PRIMARY KEY (id),
    CONSTRAINT fk_tickets_business FOREIGN KEY (business_id)
        REFERENCES businesses (id) ON DELETE CASCADE,
    CONSTRAINT fk_tickets_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE RESTRICT,
    INDEX idx_tickets_business_id (business_id),
    INDEX idx_tickets_category_id (category_id),
    INDEX idx_tickets_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ticket_id BIGINT NOT NULL,
    channel ENUM('SMS', 'WHATSAPP') NOT NULL,
    delivery_status ENUM('PENDING', 'SENT', 'FAILED') NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_tickets FOREIGN KEY (ticket_id)
        REFERENCES tickets (id) ON DELETE CASCADE,
    INDEX idx_notifications_ticket_id (ticket_id)
) ENGINE=InnoDB;



