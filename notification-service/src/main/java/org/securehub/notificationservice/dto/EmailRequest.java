package org.securehub.notificationservice.dto;

public record EmailRequest(
        String to,
        String subject,
        String body
) {
}
