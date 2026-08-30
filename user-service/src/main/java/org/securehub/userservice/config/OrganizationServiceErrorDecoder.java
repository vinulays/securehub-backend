package org.securehub.userservice.config;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.securehub.userservice.exception.OrganizationMembershipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

public class OrganizationServiceErrorDecoder implements ErrorDecoder {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        logger.error("Feign client error. Method: {}, Status: {}", methodKey, status);

        return switch (status) {
            case BAD_REQUEST -> new OrganizationMembershipException("Bad request sent to organization-service");
            case UNAUTHORIZED -> new SecurityException("Unauthorized access");
            case FORBIDDEN -> new AccessDeniedException("Access forbidden");
            case NOT_FOUND -> new OrganizationMembershipException("Organization not found");
            case SERVICE_UNAVAILABLE,
                 BAD_GATEWAY,
                 INTERNAL_SERVER_ERROR,
                 GATEWAY_TIMEOUT -> new RetryableException(
                    response.status(),
                    "Organization service unavailable, retrying...",
                    response.request().httpMethod(),
                    (Long) null,
                    response.request()
            );

            default -> new OrganizationMembershipException(
                    "Organization-service call failed with status: " + response.status()
            );
        };
    }
}
