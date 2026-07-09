package org.example.carrent.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            AuthenticationProvider authProvider
    ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        // publiczne
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/vehicles/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**").permitAll()

                        // zalogowany
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/me").authenticated()

                        // admin
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/vehicles").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/rentals").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/{id}").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/rentals/users/**").hasRole("ADMIN")

                        // zalogowany
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}