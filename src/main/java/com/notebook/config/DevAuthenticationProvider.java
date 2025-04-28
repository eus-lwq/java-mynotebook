package com.notebook.config;

import com.notebook.model.User;
import com.notebook.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.Collections;

/**
 * Development authentication provider that automatically creates and authenticates
 * a default user for testing purposes.
 */
@Component
@Profile("dev")
public class DevAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private User defaultUser;

    public DevAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        // Create a default user if it doesn't exist
        defaultUser = userRepository.findByUsername("dev_user")
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername("dev_user");
                newUser.setPasswordHash("$2a$10$dL4az.3o9Z6jFIVL0eoT3.HK9EVY9D9.QMJRgNK.nFX9fyL5/5Bqa"); // "password"
                newUser.setRole(User.Role.USER);
                return userRepository.save(newUser);
            });
        
        // Automatically authenticate with the default user
        Authentication auth = new UsernamePasswordAuthenticationToken(
            defaultUser.getUsername(),
            null,
            Collections.singletonList(() -> "ROLE_USER")
        );
        
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Always authenticate as the default user in dev mode
        return new UsernamePasswordAuthenticationToken(
            defaultUser.getUsername(),
            null,
            Collections.singletonList(() -> "ROLE_USER")
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
