package com.github.novotnyr.swixml.dsig;

public class XmlDigitalSignerException extends RuntimeException {

    public XmlDigitalSignerException() {
        super();
    }

    public XmlDigitalSignerException(String msg) {
        super(msg);
    }

    public XmlDigitalSignerException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlDigitalSignerException(Throwable cause) {
        super(cause);
    }
}