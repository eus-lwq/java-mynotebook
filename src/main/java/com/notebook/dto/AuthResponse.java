package com.notebook.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AuthResponse {
    private Long id;
    private String username;
    private String token;
}
