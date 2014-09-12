package com.firebase.security.token;

/**
 * Firebase JWT token generator.
 */
public class JwtEncodingException extends RuntimeException {
    /**
     * Constructs a new JWT encoding exception with the specified detail message.
     * @param message
     */
    public JwtEncodingException(String message) {
        super(message);
    }
}