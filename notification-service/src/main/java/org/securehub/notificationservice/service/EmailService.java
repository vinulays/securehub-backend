package org.securehub.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.notificationservice.dto.EmailRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailRequest request) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(request.to());
        message.setSubject(request.subject());
        message.setText(request.body());

        mailSender.send(message);
    }
}
