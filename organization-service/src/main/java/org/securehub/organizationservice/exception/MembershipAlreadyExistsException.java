package org.securehub.organizationservice.exception;

public class MembershipAlreadyExistsException extends RuntimeException {
    public MembershipAlreadyExistsException(String message) {
        super(message);
    }
}
