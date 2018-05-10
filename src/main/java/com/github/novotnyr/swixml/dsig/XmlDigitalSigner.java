package com.github.novotnyr.swixml.dsig;

import org.w3c.dom.Document;

public interface XmlDigitalSigner {
    Document sign(Document document, XmlSignatureParameters parameters) throws XmlDigitalSignerException;
}
