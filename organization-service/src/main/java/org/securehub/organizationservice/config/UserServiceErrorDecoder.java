package org.securehub.organizationservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.securehub.organizationservice.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

public class UserServiceErrorDecoder implements ErrorDecoder {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        logger.error("Feign client error. Method: {}, Status: {}", methodKey, status);

        return switch (status) {
            case BAD_REQUEST -> new IllegalArgumentException("Bad request sent to user-service");

            case UNAUTHORIZED -> new SecurityException("Unauthorized access");

            case FORBIDDEN -> new AccessDeniedException("Access forbidden");

            case NOT_FOUND -> new UserNotFoundException("User not found in user-service");

            case INTERNAL_SERVER_ERROR -> new RuntimeException("Internal server error");

            case SERVICE_UNAVAILABLE ->
                    new RuntimeException("User service is currently unavailable. Please try again later.");

            default -> new RuntimeException("User-service call failed with status: " + response.status());
        };
    }
}