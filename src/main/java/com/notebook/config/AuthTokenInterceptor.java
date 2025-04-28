package com.notebook.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Interceptor that adds the authentication token to all outgoing requests
 */
@Component
public class AuthTokenInterceptor implements ClientHttpRequestInterceptor {
    
    private String authToken;
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        if (authToken != null && !authToken.isEmpty()) {
            request.getHeaders().set("Authorization", "Bearer " + authToken);
        }
        return execution.execute(request, body);
    }
}
