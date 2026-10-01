package queueup.api.entities.base;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        //TODO implement logic to retrieve the current user from the security context or session    
        return Optional.of("SYSTEM");
        }
       
    }


