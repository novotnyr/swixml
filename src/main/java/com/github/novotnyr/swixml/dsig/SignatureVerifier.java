package com.github.novotnyr.swixml.dsig;

import org.w3c.dom.Document;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;

public interface SignatureVerifier {
    VerifiedDocumentCollection verifySignatures(Document signedDocumentDom) throws XMLSignatureException, MarshalException;
}
