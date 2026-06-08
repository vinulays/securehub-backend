package org.securehub.userservice.dto;

import java.util.Map;

public record ErrorResponse(
        String message,
        int status,
        Map<String, String> errors
) {
}
