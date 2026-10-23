package queueup.api.shared;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import queueup.api.entities.User;
import queueup.api.entities.enums.Role;
import queueup.api.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuperAdminSeed implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    @Override
    public void run(String... args) {
        String email = env.getProperty("queueup.seed.super-admin.email", "admin@queueup.local");
        String password = env.getProperty("queueup.seed.super-admin.password", "admin123");

        if (userRepository.findByEmail(email) != null) {
            log.info("Super admin already exists: {}", email);
            return;
        }

        User admin = User.builder()
                .name("Super Admin")
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.SUPER_ADMIN)
                .business(null)
                .enabled(true)
                .build();

        userRepository.save(admin);
        log.warn("Super admin created: {} — CHANGE THE PASSWORD", email);
    }
}