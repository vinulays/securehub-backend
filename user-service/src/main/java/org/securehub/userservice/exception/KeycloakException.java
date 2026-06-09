package org.securehub.userservice.exception;

public class KeycloakException extends RuntimeException {
    public KeycloakException(String message) {
        super(message);
    }
}
