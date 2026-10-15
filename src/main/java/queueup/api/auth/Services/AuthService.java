package queueup.api.auth.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import queueup.api.auth.dtos.RegisterRequestDto;
import queueup.api.entities.Business;
import queueup.api.entities.User;
import queueup.api.entities.enums.Role;
import queueup.api.repositories.UserRepository;
import queueup.api.services.BusinessService;
import queueup.api.shared.exceptions.ResourceNotFoundException;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BusinessService businessService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }

    public Optional<User> getLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.of((User) authentication.getPrincipal());
    }

    public Long getLoggedInBusinessId() {
        Optional<User> loggedInUser = getLoggedInUser();
        if (loggedInUser.isPresent()) {
            User user = loggedInUser.get();
            Business business = user.getBusiness();
            if (business != null) {
                return business.getId();
            }
        }
        return null;
    }

    public User registeUser(RegisterRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.email()) != null) {
            throw new ResourceNotFoundException("User not found with this email " + requestDto.email());
        }

        Business business = businessService.returnBusinessEntityById(requestDto.businessId());
        String encryptedPassword = new BCryptPasswordEncoder().encode(requestDto.password());

        User newUser = new User(requestDto.name(), requestDto.email(), encryptedPassword, requestDto.role(),
                business);
        return userRepository.save(newUser);

    }

    public boolean isSuperAdmin() {
        return getLoggedInUser()
                .map(user -> user.getRole() == Role.SUPER_ADMIN)
                .orElse(false);
    }

    public void ensureCanAccess(Long targetBusinessId) {
        if (isSuperAdmin())
            return;
        Long current = getLoggedInBusinessId();
        if (!targetBusinessId.equals(current)) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

}
