package com.github.novotnyr.swixml.dsig;

import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class VerifiedDocumentUtils {
    public static Optional<Element> getFirstSignedXmlObjectDomElement(VerifiedDocumentCollection verifiedDocuments) {
        return VerifiedDocumentUtils.getFirstSignedXmlObject(verifiedDocuments)
                .flatMap(SignedXmlObject::getFirstElement);
    }

    public static Optional<SignedXmlObject> getFirstSignedXmlObject(VerifiedDocumentCollection verifiedDocuments) {
        Iterator<VerifiedDocument> iterator = verifiedDocuments.iterator();
        if (iterator.hasNext()) {
            VerifiedDocument firstDocument = iterator.next();
            return VerifiedDocumentUtils.getFirstSignedDocument(firstDocument);
        }
        return Optional.empty();
    }

    public static Optional<SignedXmlObject> getFirstSignedDocument(VerifiedDocument verifiedDocument) {
        List<SignedXmlObject> signedObjects = verifiedDocument.getSignedObjects();
        if (signedObjects.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(signedObjects.get(0));
    }
}
