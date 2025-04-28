package com.notebook.service;

import com.notebook.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service to handle security-related operations
 */
@Service
public class SecurityService {
    
    private final UserRepository userRepository;
    
    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Sets the authentication in the security context for the given username
     * @param username The username to authenticate
     * @return true if authentication was set, false otherwise
     */
    public boolean setAuthenticationForUser(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        return userRepository.findByUsername(username)
            .map(user -> {
                // Create authentication token
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    user.getAuthorities()
                );
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("Authentication set in security context for user: " + username);
                return true;
            })
            .orElse(false);
    }
    
    /**
     * Checks if a user is authenticated
     * @return true if a user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }
    
    /**
     * Gets the current authenticated username
     * @return The username or null if not authenticated
     */
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName();
        }
        return null;
    }
}
