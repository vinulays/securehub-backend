package org.securehub.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.securehub.notificationservice.dto.EmailRequest;
import org.securehub.notificationservice.dto.UserCreatedEvent;
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

    public void sendInvitationEmail(UserCreatedEvent event) {

        String invitationUrl = "http://localhost:3000/invite?token=" + event.token();

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.email());
        message.setSubject("Welcome to SecureHub");

        message.setText("""
                Hello %s,
                
                You have been invited to SecureHub.
                
                Click the link below to activate your account:
                
                %s
                
                This link expires in 7 days.
                """
                .formatted(event.firstName(), invitationUrl));

        mailSender.send(message);
    }
}
