package com.github.novotnyr.swixml.dsig;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLObject;
import java.util.List;
import java.util.Optional;

public class SignedXmlObject {
    private String id;

    private String mimeType;

    private String encoding;

    private XMLObject rawDsigXmlObject;

    public String getId() {
        return this.id;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Return a first proper Element, corresponding to root element of signed object.
     */
    public Optional<Element> getFirstElement() {
        if (this.rawDsigXmlObject == null || this.rawDsigXmlObject.getContent().isEmpty()) {
            return Optional.empty();
        }
        List<XMLStructure> xmlStructures = this.rawDsigXmlObject.getContent();
        for (XMLStructure xmlStructure : xmlStructures) {
            if (xmlStructure instanceof DOMStructure) {
                DOMStructure domStructure = (DOMStructure) xmlStructure;
                Node node = domStructure.getNode();
                if (node instanceof Element) {
                    return Optional.of((Element) node);
                }
            }
        }
        return Optional.empty();
    }

    public static SignedXmlObject of(XMLObject dsigXmlObject) {
        SignedXmlObject o = new SignedXmlObject();
        o.id = dsigXmlObject.getId();
        o.encoding = dsigXmlObject.getEncoding();
        o.mimeType = dsigXmlObject.getMimeType();
        o.rawDsigXmlObject = dsigXmlObject;

        return o;
    }
}
