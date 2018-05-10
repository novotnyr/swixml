package com.github.novotnyr.swixml.key;

public class KeystoreException extends RuntimeException {

    public KeystoreException() {
        super();
    }

    public KeystoreException(String msg) {
        super(msg);
    }

    public KeystoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeystoreException(Throwable cause) {
        super(cause);
    }
}