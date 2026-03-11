package com.skillstorm.animalshelter.exceptions;

/**
 * Thrown when a user with STAFF or FOSTER role attempts to sign in via Google OAuth.
 * They must sign in with username/password first; they can then link Google from account settings.
 */
public class OAuthUsePasswordRequiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OAuthUsePasswordRequiredException(String message) {
        super(message);
    }
}
