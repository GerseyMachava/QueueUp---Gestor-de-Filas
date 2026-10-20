package queueup.api.auth.Services;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import queueup.api.auth.principal.UserPrincipal;
import queueup.api.entities.User;
import queueup.api.repositories.UserRepository;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = recoverToken(request);

            if (token != null) {
                String email = tokenService.validateToken(token);

                if (email != null && !email.isBlank()) {
                    User user = userRepository.findByEmail(email);

                    if (user != null) {
                        UserPrincipal principal = UserPrincipal.from(user);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        principal, null, principal.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);  
    }
}