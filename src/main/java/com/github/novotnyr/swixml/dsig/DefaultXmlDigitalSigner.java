package com.github.novotnyr.swixml.dsig;

import org.w3c.dom.Document;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Collections;

// code is based on the example found here https://docs.oracle.com/javase/8/docs/technotes/guides/security/xmldsig/GenEnveloping.java
public class DefaultXmlDigitalSigner implements XmlDigitalSigner {
    public static final String DEFAULT_DIGITAL_SIGNATURE_NAMESPACE_PREFIX = "ds";

    private XMLSignatureFactory xmlSignatureFactory;

    private ReferenceFactory referenceFactory;

    private PrivateKey privateKey;

    private String signatureAlgorithm = SignatureMethods.RSA_SHA256;

    private String digitalSignatureNamespacePrefix = DEFAULT_DIGITAL_SIGNATURE_NAMESPACE_PREFIX;

    public DefaultXmlDigitalSigner(PrivateKey privateKey) throws XmlDigitalSignerException {
        this.privateKey = privateKey;
        this.xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
        this.referenceFactory = new ReferenceFactory(this.xmlSignatureFactory);
    }

    public Document sign(Document document, XmlSignatureParameters parameters) throws XmlDigitalSignerException {
        Reference reference = this.referenceFactory.createSha256Reference(parameters.getReferenceUri());

        SignedInfo signedInfo = createSignedInfo(reference);
        KeyInfo keyInfo = newKeyInfo(parameters.getKeyName());

        XMLObject xmlObject = newXmlObject(document, parameters.getObjectId());

        // Create the XMLSignature (but don't sign it yet)
        XMLSignature signature = this.xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo, Collections.singletonList(xmlObject), null, null);

        // Create a DOMSignContext and specify the RSA PrivateKey for signing
        // and the document location of the XMLSignature
        DOMSignContext dsc = new DOMSignContext(this.privateKey, document);
        // Configure 'ds' or similar namespace prefix for elements
        dsc.setDefaultNamespacePrefix(this.digitalSignatureNamespacePrefix);

        try {
            signature.sign(dsc);
        } catch (MarshalException | XMLSignatureException e) {
            throw new XmlDigitalSignerException("Failed to sign document due to " + e.getClass().getSimpleName(), e);
        }

        return document;
    }

    protected KeyInfo newKeyInfo(String keyName) {
        KeyInfoFactory keyInfoFactory = this.xmlSignatureFactory.getKeyInfoFactory();
        KeyName keyNameObject = keyInfoFactory.newKeyName(keyName);
        return keyInfoFactory.newKeyInfo(Collections.singletonList(keyNameObject));
    }

    protected XMLObject newXmlObject(Document document, String objectId) {
        XMLStructure xmlStructure = new DOMStructure(document.getDocumentElement());
        return this.xmlSignatureFactory.newXMLObject(Collections.singletonList(xmlStructure), objectId, null, null);
    }

    protected SignedInfo createSignedInfo(Reference reference) {
        SignatureMethod signatureMethod = createSignatureMethod();
        CanonicalizationMethod canonicalizationMethod = createCanonicalizationMethod();

        return this.xmlSignatureFactory
                .newSignedInfo(canonicalizationMethod,
                        signatureMethod,
                        Collections.singletonList(reference));
    }

    private CanonicalizationMethod createCanonicalizationMethod() {
        try {
            return this.xmlSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        } catch (NoSuchAlgorithmException e) {
            throw new XmlDigitalSignerException("Cannot create canonicalization method (CanonicalizationMethod): unknown algorithm.", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new XmlDigitalSignerException("Cannot create canonicalization method (CanonicalizationMethod): invalid algorithm parameters", e);
        }
    }

    private SignatureMethod createSignatureMethod() {
        try {
            return this.xmlSignatureFactory.newSignatureMethod(this.signatureAlgorithm, null);
        } catch (NoSuchAlgorithmException e) {
            throw new XmlDigitalSignerException("Cannot create signature method (SignatureMethod): unknown algorithm.", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new XmlDigitalSignerException("Cannot create signature method (SignatureMethod): invalid algorithm parameters.", e);
        }
    }

    public void setDigitalSignatureNamespacePrefix(String digitalSignatureNamespacePrefix) {
        this.digitalSignatureNamespacePrefix = digitalSignatureNamespacePrefix;
    }
}