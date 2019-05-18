package com.github.novotnyr.swixml.dsig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VerifiedDocumentCollection implements Iterable<VerifiedDocument> {
    private List<VerifiedDocument> allValidationErrors = new ArrayList<>();

    public VerifiedDocumentCollection add(VerifiedDocument validatedDocument) {
        this.allValidationErrors.add(validatedDocument);
        return this;
    }

    @Override
    public Iterator<VerifiedDocument> iterator() {
        return this.allValidationErrors.iterator();
    }

    public boolean hasAllValidSignatures() {
        return this.allValidationErrors
                .stream()
                .allMatch(VerifiedDocument::hasValidSignature);
    }

    public boolean hasAllValidSignatureValues() {
        return this.allValidationErrors
                .stream()
                .allMatch(VerifiedDocument::hasValidSignatureValue);
    }

    public boolean hasAllValidReferences() {
        return this.allValidationErrors
                .stream()
                .allMatch(VerifiedDocument::hasAllValidReferences);
    }

    public int size() {
        return this.allValidationErrors.size();
    }
}
