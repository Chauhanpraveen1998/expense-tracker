package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    /** Nested user object — required by the Android app. */
    private UserDto user;

    public record UserDto(UUID id, String name, String email) {}
}
