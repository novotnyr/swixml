package com.github.novotnyr.swixml.dsig;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.List;

/**
 * KeySelector which retrieves the public key out of the
 * KeyValue element and returns it.
 * NOTE: If the key algorithm doesn't match signature algorithm,
 * then the public key will be ignored.
 */
public class KeyValueElementKeySelector extends KeySelector {
    public KeySelectorResult select(KeyInfo keyInfo,
                                    KeySelector.Purpose purpose,
                                    AlgorithmMethod method,
                                    XMLCryptoContext context)
            throws KeySelectorException {
        if (keyInfo == null) {
            throw new KeySelectorException("Null KeyInfo object!");
        }
        SignatureMethod signatureMethod = (SignatureMethod) method;
        @SuppressWarnings("unchecked") List<XMLStructure> xmlStructures = keyInfo.getContent();
        for (XMLStructure xmlStructure : xmlStructures) {
            if (xmlStructure instanceof KeyValue) {
                PublicKey publicKey = null;
                try {
                    publicKey = ((KeyValue) xmlStructure).getPublicKey();
                } catch (KeyException ke) {
                    throw new KeySelectorException(ke);
                }
                // make sure algorithm is compatible with method
                if (algEquals(signatureMethod.getAlgorithm(), publicKey.getAlgorithm())) {
                    return new SimpleKeySelectorResult(publicKey);
                }
            }
        }
        throw new KeySelectorException("No KeyValue element found!");
    }

    static boolean algEquals(String algURI, String algName) {
        if ("DSA".equalsIgnoreCase(algName) &&
                algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
            return true;
        } else if ("RSA".equalsIgnoreCase(algName) &&
                algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
            return true;
        } else {
            return false;
        }
    }
}
