package org.caching.server.rcs.exceptions;

public class CacheException extends Exception {
    private String message;
    public CacheException(String message) {
        super(message);
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }
}
