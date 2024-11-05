package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.response.AuthenticationResponse;
import org.example.entity.UserEntity;
import org.example.entity.request.SignInRequest;
import org.example.entity.request.SignUpRequest;
import org.example.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponse signUp(SignUpRequest request) {
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .role("USER")
                .password(passwordEncoder.encode(request.password()))
                .build();
        user = userRepository.save(user);
        log.info("User saved: {}", user.getUsername());

        String token = jwtService.generateToken(user, false);

        return new AuthenticationResponse(token);
    }

    @Transactional
    public AuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Неверный пароль или почта"));

        log.info("User role: {}", user.getRole());

        String token = jwtService.generateToken(user, request.remember());

        return new AuthenticationResponse(token);
    }

    @Transactional
    public void logout(String token) {
        log.info("Token: {}", token);
        String trimmed = token.replace("Bearer ", "");
        jwtService.invalidateToken(trimmed);
        log.info("Token invalidated: {}", trimmed);
    }
}
