package com.github.novotnyr.swixml.dsig;

public class XmlSignatureParameters {
    private String referenceUri;

    private String objectId;

    private String keyName;

    public String getReferenceUri() {
        return this.referenceUri;
    }

    public void setReferenceUri(String referenceUri) {
        this.referenceUri = referenceUri;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getKeyName() {
        return this.keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public XmlSignatureParameters withReferenceUri(final String referenceUri) {
        this.referenceUri = referenceUri;
        return this;
    }

    public XmlSignatureParameters withObjectId(final String objectId) {
        this.objectId = objectId;
        return this;
    }

    public XmlSignatureParameters withKeyName(final String keyName) {
        this.keyName = keyName;
        return this;
    }
}
