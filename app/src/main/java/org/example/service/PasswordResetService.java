package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    @Value("${security.jwt.reset}")
    private String resetCode;

    @Transactional
    public void requestReset(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        mailService.sendMail(user.getEmail(), "Сброс пароля", "Код для сброса пароля: " + resetCode);

    }

    @Transactional
    public void confirmReset(String email, String code, String newPassword) {
        if (!resetCode.equals(code)) {
            throw new IllegalArgumentException("Invalid reset code");
        }
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        log.info("User password updated: {}", user.getEmail());
        userRepository.save(user);
    }
}
