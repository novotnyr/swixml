package com.github.novotnyr.swixml.dsig;

public class SignatureValidationException extends RuntimeException {

    public SignatureValidationException() {
        super();
    }

    public SignatureValidationException(String msg) {
        super(msg);
    }

    public SignatureValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureValidationException(Throwable cause) {
        super(cause);
    }
}