package uz.ids.homenetwork.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ids.homenetwork.config.JwtUtils;
import uz.ids.homenetwork.dto.JwtResponse;
import uz.ids.homenetwork.dto.LoginRequest;
import uz.ids.homenetwork.dto.RegisterRequest;
import uz.ids.homenetwork.model.User;
import uz.ids.homenetwork.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    
    /**
     * Register new user
     */
    @Transactional
    public User registerUser(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Create new user
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .build();
        
        return userRepository.save(user);
    }
    
    /**
     * Authenticate user and generate JWT token
     */
    public JwtResponse authenticateUser(LoginRequest request) {
        log.info("Authenticating user: {}", request.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        return new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail());
    }
    
    /**
     * Get current authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
