package com.github.novotnyr.swixml.dsig;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

public class ReferenceFactory {
    private static final String CANONICAL_XML_1_0_URI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    private XMLSignatureFactory xmlSignatureFactory;

    public ReferenceFactory(XMLSignatureFactory xmlSignatureFactory) {
        this.xmlSignatureFactory = xmlSignatureFactory;
    }

    public Reference createSha256Reference(String referenceUri) {
        DigestMethod digestMethod = newDigestMethod(DigestMethod.SHA256);
        List<Transform> transforms = Collections.singletonList(newC14NTransform());

        return this.xmlSignatureFactory
                .newReference(referenceUri, digestMethod,
                        transforms, null, null);
    }

    protected Transform newC14NTransform() {
        try {
            return this.xmlSignatureFactory.newTransform(CANONICAL_XML_1_0_URI, (TransformParameterSpec) null);
        } catch (NoSuchAlgorithmException e) {
            throw new XmlDigitalSignerException("Cannot create Canonicalization Transform: unknown algorithm " + CANONICAL_XML_1_0_URI, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new XmlDigitalSignerException("Cannot create Canonicalization Transform: unknown algorithm parameters", e);
        }
    }

    protected DigestMethod newDigestMethod(String digestMethodUri) {
        try {
            return this.xmlSignatureFactory.newDigestMethod(digestMethodUri, null);
        } catch (NoSuchAlgorithmException e) {
            throw new XmlDigitalSignerException("Cannot create digest method (DigestMethod): unknown algorithm " + digestMethodUri, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new XmlDigitalSignerException("Cannot create digest method (DigestMethod): unknown algorithm parameters", e);
        }
    }
}
