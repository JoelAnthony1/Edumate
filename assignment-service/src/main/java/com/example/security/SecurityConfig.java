package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for Postman testing
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/rubrics/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/rubrics/**").authenticated()  // Require authentication for PUT

                .requestMatchers(HttpMethod.POST, "/api/submission/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/submission/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/submission/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/submission/**").authenticated()

                .anyRequest().authenticated()
            )
            .httpBasic(); // Enable Basic Authentication

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("password")
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user);
    }
}
