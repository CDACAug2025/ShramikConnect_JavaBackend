package com.shramikconnect.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Verify your email - ShramikConnect");
        mail.setText(
                "Welcome to ShramikConnect!\n\n" +
                "Click below to verify your email:\n" +
                link + "\n\n" +
                "This link expires in 24 hours."
        );
        mailSender.send(mail);
    }
}
