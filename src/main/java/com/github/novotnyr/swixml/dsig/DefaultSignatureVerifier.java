package com.github.novotnyr.swixml.dsig;

import com.github.novotnyr.swixml.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import java.util.List;

public class DefaultSignatureVerifier implements SignatureVerifier {
    private final XMLSignatureFactory xmlSignatureFactory;

    private final KeySelector keySelector;

    public DefaultSignatureVerifier() {
        this(XMLSignatureFactory.getInstance("DOM"), new KeyValueElementKeySelector());
    }

    public DefaultSignatureVerifier(KeySelector keySelector) {
        this(XMLSignatureFactory.getInstance("DOM"), keySelector);
    }

    public DefaultSignatureVerifier(XMLSignatureFactory xmlSignatureFactory, KeySelector keySelector) {
        this.xmlSignatureFactory = xmlSignatureFactory;
        this.keySelector = keySelector;
    }

    @Override
    public VerifiedDocumentCollection verifySignatures(Document signedDocumentDom) throws XMLSignatureException, MarshalException {
        // Find Signature element
        NodeList nl = signedDocumentDom.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new SignatureValidationException("Document does not contain <Signature> element");
        }
        VerifiedDocumentCollection verifiedDocumentCollection = new VerifiedDocumentCollection();
        for (Node signatureNode : XmlUtils.toList(nl)) {
            // Create a DOMValidateContext and specify a KeyValue KeySelector
            // and document context
            DOMValidateContext valContext = new DOMValidateContext(getKeySelector(), signatureNode);

            // unmarshal the XMLSignature
            XMLSignature signature = this.xmlSignatureFactory.unmarshalXMLSignature(valContext);

            // Validate the XMLSignature (generated above)
            boolean coreValidity = signature.validate(valContext);

            VerifiedDocument verifiedDocument = new VerifiedDocument();
            verifiedDocument.setValidSignature(coreValidity);
            verifiedDocument.setValidSignatureValue(signature.getSignatureValue().validate(valContext));
            verifiedDocument.setXmlSignature(signature);
            @SuppressWarnings("unchecked") List<Reference> references = signature.getSignedInfo().getReferences();
            for (Reference reference : references) {
                verifiedDocument.addValidity(reference, reference.validate(valContext));
            }
            verifiedDocumentCollection.add(verifiedDocument);
        }
        return verifiedDocumentCollection;
    }

    private KeySelector getKeySelector() {
        return this.keySelector;
    }
}
