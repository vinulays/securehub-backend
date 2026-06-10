package org.securehub.organizationservice.dto;

import java.util.Map;

public record ErrorResponse(
        String message,
        int status,
        Map<String, String> errors
) {
}
