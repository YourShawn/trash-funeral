package com.trashfuneral.auth.service;

import com.trashfuneral.auth.domain.User;
import com.trashfuneral.auth.dto.LoginRequest;
import com.trashfuneral.auth.dto.RegisterRequest;
import com.trashfuneral.auth.dto.TokenResponse;
import com.trashfuneral.auth.dto.UserResponse;
import com.trashfuneral.auth.repo.UserRepository;
import com.trashfuneral.common.exception.ApiException;
import com.trashfuneral.common.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase();
        if (users.existsByUsername(username)) {
            throw ApiException.conflict("Username already taken / 用户名已被占用");
        }
        if (users.existsByEmail(email)) {
            throw ApiException.conflict("Email already registered / 邮箱已注册");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        String display = request.displayName() == null || request.displayName().isBlank()
                ? username
                : request.displayName().trim();
        user.setDisplayName(display);
        users.save(user);
        return tokenFor(user);
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username().trim(), request.password())
        );
        User user = users.findByUsername(request.username().trim())
                .orElseThrow(() -> ApiException.unauthorized("Invalid credentials"));
        return tokenFor(user);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getDisplayName());
    }

    public UserResponse me(UserPrincipal principal) {
        User user = users.findById(principal.getId()).orElseThrow(() -> ApiException.notFound("User not found"));
        return toResponse(user);
    }

    private TokenResponse tokenFor(User user) {
        String token = jwtService.issue(user.getId(), user.getUsername());
        return new TokenResponse(token, "Bearer", toResponse(user));
    }
}
