package queueup.api.auth.principal;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import queueup.api.entities.User;
import queueup.api.entities.enums.Role;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final Long businessId; 
    private final String email;
    private final String password;
    private final Role role;
    private final boolean enabled;

    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getBusiness() != null ? user.getBusiness().getId() : null,
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.isEnabled());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isSuperAdmin() {
        return role == Role.SUPER_ADMIN;
    }
}