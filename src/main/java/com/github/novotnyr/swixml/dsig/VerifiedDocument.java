package com.github.novotnyr.swixml.dsig;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerifiedDocument {
    private boolean validSignature;

    private boolean validSignatureValue;

    private List<Pair> referenceValidities = new ArrayList<>();

    private XMLSignature xmlSignature;

    public boolean hasValidSignature() {
        return this.validSignature;
    }

    public void setValidSignature(boolean validSignature) {
        this.validSignature = validSignature;
    }

    public boolean hasValidSignatureValue() {
        return this.validSignatureValue;
    }

    public void setValidSignatureValue(boolean validSignatureValue) {
        this.validSignatureValue = validSignatureValue;
    }

    public void addValidity(Reference reference, boolean isValid) {
        this.referenceValidities.add(new Pair(reference, validSignature));
    }

    public boolean isValid(int referenceIndex) {
        return this.referenceValidities.get(referenceIndex).valid;
    }

    public List<Boolean> getReferenceValidations() {
        List<Boolean> result = new ArrayList<>();
        for (Pair referenceValidity : this.referenceValidities) {
            result.add(referenceValidity.isValid());
        }
        return result;
    }

    public boolean hasAllValidReferences() {
        for (Boolean isValid : getReferenceValidations()) {
            if (!isValid) {
                return false;
            }
        }
        return true;
    }

    public List<SignedXmlObject> getSignedObjects() {
        if (this.xmlSignature == null) {
            return Collections.emptyList();
        }
        @SuppressWarnings("unchecked")
        List<SignedXmlObject> signedXmlObjects = new ArrayList<>();
        @SuppressWarnings("unchecked") List<XMLObject> xmlObjects = this.xmlSignature.getObjects();
        for (XMLObject xmlObject : xmlObjects) {
            signedXmlObjects.add(SignedXmlObject.of(xmlObject));
        }
        return signedXmlObjects;
    }

    public void setXmlSignature(XMLSignature xmlSignature) {
        this.xmlSignature = xmlSignature;
    }

    private static class Pair {
        Reference reference;

        boolean valid;

        Pair(Reference reference, boolean valid) {
            this.reference = reference;
            this.valid = valid;
        }

        public boolean isValid() {
            return this.valid;
        }
    }
}
