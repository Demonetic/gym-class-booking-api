package se.edugrade._5_java_enterprice_assignment_4_individual.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.AuthRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.AuthResponse;
import se.edugrade._5_java_enterprice_assignment_4_individual.dto.RegisterRequest;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.Role;
import se.edugrade._5_java_enterprice_assignment_4_individual.model.User;
import se.edugrade._5_java_enterprice_assignment_4_individual.repository.UserRepository;
import se.edugrade._5_java_enterprice_assignment_4_individual.security.JwtUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password())
            );

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                    .map(auth -> auth.replace("ROLE_", ""))
                    .toList();

            String accessToken = jwtUtil.generateAccessToken(req.username(), roles);
            return ResponseEntity.ok(AuthResponse.of(accessToken, jwtUtil.getAccessTokenValiditySeconds()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
        }

        User user = new User(
                req.username(),
                passwordEncoder.encode(req.password()),
                Role.USER
        );
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully", "username", user.getUsername()));
    }
}
