package com.ks.exceptions;

public class InvalidHashAlgorithmConfiguration extends RuntimeException {
    public InvalidHashAlgorithmConfiguration(final String message) {
        super(message);
    }

    public InvalidHashAlgorithmConfiguration(final Throwable cause) {
        super(cause);
    }

    public InvalidHashAlgorithmConfiguration(final String message, final Throwable cause) {
        super(message, cause);
    }
}
