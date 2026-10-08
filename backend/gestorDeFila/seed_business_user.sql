-- MySQL: cria uma empresa e um utilizador ADMIN associado a ela.
-- Preencha os valores antes de executar. A password deve ser um hash BCrypt,
-- nunca a password em texto simples.

SET @business_name = 'QueueUp Demo';
SET @business_address = 'Maputo';
SET @business_contact = '+258 840000000';
SET @business_type = 'OTHER'; -- FAST_FOOD, SALON, CLINIC, BANK ou OTHER

SET @user_name = 'Administrador';
SET @user_email = 'admin@queueup-demo.com';
SET @password_hash = 'SUBSTITUA_POR_UM_HASH_BCRYPT_VALIDO';

START TRANSACTION;

INSERT INTO businesses (name, address, contact, `type`)
VALUES (@business_name, @business_address, @business_contact, @business_type);

SET @business_id = LAST_INSERT_ID();

INSERT INTO users (business_id, name, email, password_hash, role)
VALUES (@business_id, @user_name, @user_email, @password_hash, 'ADMIN');

COMMIT;

SELECT
    b.id AS business_id,
    b.name AS business_name,
    u.id AS user_id,
    u.name AS user_name,
    u.email AS user_email,
    u.role AS user_role
FROM businesses b
JOIN users u ON u.business_id = b.id
WHERE b.id = @business_id;
