package queueup.api.auth.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import queueup.api.auth.dtos.RegisterRequestDto;
import queueup.api.entities.Business;
import queueup.api.entities.User;
import queueup.api.repositories.UserRepository;
import queueup.api.services.BusinessService;
import queueup.api.shared.apiResponse.ApiResponse;
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

}
