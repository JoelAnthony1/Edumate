package com.example.userservice.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // Load user by username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Direct password comparison (without encoding)
        if (userDetails != null && userDetails.getPassword().equals(password)) {
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
