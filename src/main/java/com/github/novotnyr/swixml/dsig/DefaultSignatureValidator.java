package com.github.novotnyr.swixml.dsig;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.security.Key;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.List;

public class DefaultSignatureValidator {
    private final XMLSignatureFactory xmlSignatureFactory;

    private final KeySelector keySelector;

    public DefaultSignatureValidator() {
        this(XMLSignatureFactory.getInstance("DOM"), new KeyValueKeySelector());
    }

    public DefaultSignatureValidator(KeySelector keySelector) {
        this(XMLSignatureFactory.getInstance("DOM"), keySelector);
    }

    public DefaultSignatureValidator(XMLSignatureFactory xmlSignatureFactory, KeySelector keySelector) {
        this.xmlSignatureFactory = xmlSignatureFactory;
        this.keySelector = keySelector;
    }

    public ValidationErrors validate(Document signedDocument) throws XMLSignatureException, MarshalException {
        // Find Signature element
        NodeList nl = signedDocument.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new SignatureValidationException("Document does not contain <Signature> element");
        }
        Node signatureNode = nl.item(0);


        // Create a DOMValidateContext and specify a KeyValue KeySelector
        // and document context
        DOMValidateContext valContext = new DOMValidateContext(getKeySelector(), signatureNode);

        // unmarshal the XMLSignature
        XMLSignature signature = this.xmlSignatureFactory.unmarshalXMLSignature(valContext);

        // Validate the XMLSignature (generated above)
        boolean coreValidity = signature.validate(valContext);

        ValidationErrors errors = new ValidationErrors();
        errors.setValidSignature(coreValidity);
        errors.setValidSignatureValue(signature.getSignatureValue().validate(valContext));
        List<Reference> references = signature.getSignedInfo().getReferences();
        for (Reference reference : references) {
            errors.addValidity(reference, reference.validate(valContext));
        }
        return errors;
    }

    private KeySelector getKeySelector() {
        return this.keySelector;
    }

    /**
     * KeySelector which retrieves the public key out of the
     * KeyValue element and returns it.
     * NOTE: If the key algorithm doesn't match signature algorithm,
     * then the public key will be ignored.
     */
    private static class KeyValueKeySelector extends KeySelector {
        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
                throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = (SignatureMethod) method;
            List list = keyInfo.getContent();

            for (int i = 0; i < list.size(); i++) {
                XMLStructure xmlStructure = (XMLStructure) list.get(i);
                if (xmlStructure instanceof KeyValue) {
                    PublicKey pk = null;
                    try {
                        pk = ((KeyValue)xmlStructure).getPublicKey();
                    } catch (KeyException ke) {
                        throw new KeySelectorException(ke);
                    }
                    // make sure algorithm is compatible with method
                    if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                        return new SimpleKeySelectorResult(pk);
                    }
                }
            }
            throw new KeySelectorException("No KeyValue element found!");
        }

        //@@@FIXME: this should also work for key types other than DSA/RSA
        static boolean algEquals(String algURI, String algName) {
            if (algName.equalsIgnoreCase("DSA") &&
                    algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
                return true;
            } else if (algName.equalsIgnoreCase("RSA") &&
                    algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private PublicKey pk;
        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        public Key getKey() { return pk; }
    }
}
