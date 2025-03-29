// package com.example.security;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(auth -> auth
//                 .anyRequest().permitAll()  // Allow all requests without authentication
//             )
//             .csrf(csrf -> csrf.disable());  // Disable CSRF protection
        
//         return http.build();
//     }
// }
// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.http.HttpMethod;
// // import org.springframework.http.HttpStatus;
// // import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// // import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// // import org.springframework.security.config.http.SessionCreationPolicy;
// // import org.springframework.security.web.SecurityFilterChain;
// // import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// // import org.springframework.security.web.authentication.HttpStatusEntryPoint;

// // @Configuration
// // @EnableWebSecurity // Critical annotation for security configuration
// // public class SecurityConfig {

// //     private final JwtTokenProvider tokenProvider;

// //     // Constructor injection (no need for @Autowired in newer Spring versions)
// //     public SecurityConfig(JwtTokenProvider tokenProvider) {
// //         this.tokenProvider = tokenProvider;
// //     }

// //     @Bean
// //     public JwtAuthenticationFilter jwtAuthenticationFilter() {
// //         return new JwtAuthenticationFilter(tokenProvider);
// //     }

// //     @Bean
// //     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
// //         http
// //             // Disable CSRF for API endpoints
// //             .csrf(csrf -> csrf.disable())
            
// //             // Set session management to stateless
// //             .sessionManagement(session -> session
// //                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
// //             )
            
// //             // Exception handling configuration
// //             .exceptionHandling(exceptions -> exceptions
// //                 .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
// //                 .accessDeniedHandler((request, response, accessDeniedException) -> {
// //                     response.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
// //                 })
// //             )
            
// //             // Authorization rules
// //             .authorizeHttpRequests(auth -> auth
// //                 // Public endpoints
// //                 .requestMatchers(HttpMethod.POST, "/rubrics/**").permitAll()
                
// //                 // Authenticated endpoints
// //                 .requestMatchers(HttpMethod.PUT, "/rubrics/**").authenticated()
// //                 .requestMatchers(HttpMethod.GET, "/rubrics/**").authenticated()
// //                 .requestMatchers(HttpMethod.POST, "/submission/**").authenticated()
// //                 .requestMatchers(HttpMethod.GET, "/submission/**").authenticated()
// //                 .requestMatchers(HttpMethod.PUT, "/submission/**").authenticated()
// //                 .requestMatchers(HttpMethod.DELETE, "/submission/**").authenticated()
                
// //                 // All other requests require authentication
// //                 .anyRequest().authenticated()
// //             );

// //         // Add JWT filter before the default security filter
// //         http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
// //         return http.build();
// //     }
// // }