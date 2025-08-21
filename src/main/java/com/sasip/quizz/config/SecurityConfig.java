package com.sasip.quizz.config;

import com.sasip.quizz.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private com.sasip.quizz.security.CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/user/auth/**",         // for user login
                                "/api/admin/auth/**",        // for admin login
                                "/api/users/register",       // user registration
                                "/api/admin/users/register", // admin registration
                                "/v3/api-docs/**",           // Swagger OpenAPI docs
                                "/swagger-ui/**",            // Swagger UI
                                "/swagger-ui.html",           // Swagger UI HTML entry
                                "/swagger-ui/favicon-32x32.png",  // Favicon
                                "/v3/api-docs/swagger-config", // Swagger Config
                                "/api/users/request-forgot-password-otp",
                                "/api/users/confirm-forgot-password",
                                "/api/users/confirm-registration-otp",
                                "/api/users/check-username/**",
                                // --- ADD THESE TWO LINES ---
                                "/api/masterdata/districts", // Allow access to districts for registration
                                "/api/alyears"               // Allow access to A/L years for registration
                                // --- END OF ADDITION ---
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }
}