package org.example.carrent.web;

import lombok.RequiredArgsConstructor;
import org.example.carrent.dto.LoginRequest;
import org.example.carrent.dto.LoginResponse;
import org.example.carrent.dto.RegisterRequest;
import org.example.carrent.models.User;
import org.example.carrent.security.JwtUtil;
import org.example.carrent.services.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserServiceInterface userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.login(),
                            loginRequest.password()
                    )
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.login() == null || request.password() == null || request.login().isBlank() || request.password().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Login i hasło są wymagane"));
        }

        User newUser = userService.registerUser(
                request.login(),
                request.password(),
                request.address()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Konto utworzone pomyślnie", "userId", newUser.getId()));
    }
}