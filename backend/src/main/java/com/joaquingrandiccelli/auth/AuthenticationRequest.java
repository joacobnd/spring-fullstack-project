package com.joaquingrandiccelli.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
