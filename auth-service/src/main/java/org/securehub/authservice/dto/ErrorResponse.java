package org.securehub.authservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        int status,
        Map<String, String> errors
) {
}
