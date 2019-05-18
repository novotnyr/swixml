package com.github.novotnyr.swixml.dsig;

import javax.xml.crypto.KeySelectorResult;
import java.security.Key;
import java.security.PublicKey;

public class SimpleKeySelectorResult implements KeySelectorResult {
    private final PublicKey publicKey;

    public SimpleKeySelectorResult(PublicKey pk) {
        this.publicKey = pk;
    }

    public Key getKey() {
        return this.publicKey;
    }
}