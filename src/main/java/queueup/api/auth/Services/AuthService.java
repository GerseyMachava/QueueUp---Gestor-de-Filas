package queueup.api.auth.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import queueup.api.auth.dtos.RegisterRequestDto;
import queueup.api.auth.principal.UserPrincipal;
import queueup.api.entities.Business;
import queueup.api.entities.User;
import queueup.api.repositories.UserRepository;
import queueup.api.services.BusinessService;
import queueup.api.shared.exceptions.BusinessException;
import queueup.api.shared.exceptions.ResourceNotFoundException;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BusinessService businessService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return UserPrincipal.from(user);
    }

    public UserPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
        throw new BusinessException("User not authenticated", HttpStatus.UNAUTHORIZED);
    }
    System.out.println("### PRINCIPAL CLASS: " + auth.getPrincipal().getClass().getName());
    if (!(auth.getPrincipal() instanceof UserPrincipal principal)) {
        throw new BusinessException("User not authenticated", HttpStatus.UNAUTHORIZED);
    }
    return principal;
    }

    public Optional<User> getLoggedInUser() {
        try {
            UserPrincipal principal = getCurrentPrincipal();
            return Optional.ofNullable(userRepository.findById(principal.getUserId()).orElse(null));
        } catch (BusinessException ex) {
            return Optional.empty();
        }
    }

    public Long getLoggedInUserId() {
        return getCurrentPrincipal().getUserId();
    }

    public Long getLoggedInBusinessIdOrNull() {
        return getCurrentPrincipal().getBusinessId();
    }

    public Long getLoggedInBusinessId() {
        Long businessId = getLoggedInBusinessIdOrNull();
        if (businessId == null) {
            throw new BusinessException(
                    "User has no business assigned", HttpStatus.FORBIDDEN);
        }
        return businessId;
    }

    public boolean isSuperAdmin() {
        try {
            return getCurrentPrincipal().isSuperAdmin();
        } catch (BusinessException ex) {
            return false;
        }
    }

    public User registerUser(RegisterRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.email()) != null) {
            throw new ResourceNotFoundException("User already exists with this email " + requestDto.email());
        }

        Business business = businessService.returnBusinessEntityById(requestDto.businessId());
        String encryptedPassword = new BCryptPasswordEncoder().encode(requestDto.password());

        User newUser = new User(
                requestDto.name(),
                requestDto.email(),
                encryptedPassword,
                requestDto.role(),
                business, true);
        return userRepository.save(newUser);
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
