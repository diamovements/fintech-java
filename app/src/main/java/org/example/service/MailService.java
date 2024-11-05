package org.example.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setSubject(subject);
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setText(body, true);
            log.info("Receiver: {}", to);
            mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }
    }
}
