package com.ks.exceptions;

public class InvalidHashAlgorithmConfigurationException extends RuntimeException {
    public InvalidHashAlgorithmConfigurationException(final String message) {
        super(message);
    }

    public InvalidHashAlgorithmConfigurationException(final Throwable cause) {
        super(cause);
    }

    public InvalidHashAlgorithmConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
