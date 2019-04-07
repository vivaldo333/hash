package com.ks.exceptions;

public class MobileNotFoundException extends RuntimeException {
    public MobileNotFoundException(final String message) {
        super(message);
    }

    public MobileNotFoundException(final Throwable cause) {
        super(cause);
    }

    public MobileNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
