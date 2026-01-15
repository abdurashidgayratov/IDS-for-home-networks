package uz.ids.homenetwork.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ids.homenetwork.dto.JwtResponse;
import uz.ids.homenetwork.dto.LoginRequest;
import uz.ids.homenetwork.dto.RegisterRequest;
import uz.ids.homenetwork.model.User;
import uz.ids.homenetwork.service.AuthService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.registerUser(request);
            return ResponseEntity.ok().body(Map.of(
                "message", "User registered successfully!",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail()
                )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(request);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            log.error("Authentication failed", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = authService.getCurrentUser();
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName() != null ? user.getFullName() : "",
                "createdAt", user.getCreatedAt(),
                "lastLogin", user.getLastLogin()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
