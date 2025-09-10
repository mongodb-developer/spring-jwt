package com.mongodb.springjwt.controller;

import com.mongodb.springjwt.model.UserAccount;
import com.mongodb.springjwt.repository.UserAccountRepository;
import com.mongodb.springjwt.service.TokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final TokenService tokens;

    public AuthController(UserAccountRepository users, PasswordEncoder encoder,
                          AuthenticationManager authManager, TokenService tokens) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.tokens = tokens;
    }

    public record RegisterRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record JwtResponse(String token) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        if (users.existsByUsername(req.username())) return ResponseEntity.badRequest().body("Username taken");
        var u = new UserAccount();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        u.setRoles(Set.of("USER"));
        users.save(u);
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> token(@RequestBody @Valid LoginRequest req) {
        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        return ResponseEntity.ok(new JwtResponse(tokens.generate(auth)));
    }
}
