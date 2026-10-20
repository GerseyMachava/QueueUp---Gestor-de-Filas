package queueup.api.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import queueup.api.auth.Services.AuthService;
import queueup.api.auth.Services.TokenService;
import queueup.api.auth.dtos.AuthRequestDto;
import queueup.api.auth.dtos.LoginResponseDto;
import queueup.api.auth.dtos.RegisterRequestDto;
import queueup.api.auth.principal.UserPrincipal;
import queueup.api.entities.User;
import queueup.api.repositories.UserRepository;
import queueup.api.shared.apiResponse.ApiResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody AuthRequestDto requestDto) {
        var emailAndPassword = new UsernamePasswordAuthenticationToken(
                requestDto.email(), requestDto.password());

        var auth = this.authenticationManager.authenticate(emailAndPassword);

        var principal = (UserPrincipal) auth.getPrincipal();
        var token = tokenService.generateToken(principal);

        return ResponseEntity.ok(new LoginResponseDto(requestDto.email(), token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDto requestDto) {
        System.out.println("Registering user: " + requestDto.name() + ", Email: " + requestDto.email() + ", Role: "
                + requestDto.role() + ", Business ID: " + requestDto.businessId());
        return ResponseEntity.ok(authService.registerUser(requestDto));
    }

}
