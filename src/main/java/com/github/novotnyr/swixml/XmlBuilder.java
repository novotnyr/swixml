package com.github.novotnyr.swixml;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class XmlBuilder {
    private InputSource inputSource;

    private boolean isNamespaceAware;

    private boolean isValidating;

    public static XmlBuilder from(String xmlString) {
        XmlBuilder xmlBuilder = new XmlBuilder();
        xmlBuilder.inputSource = new InputSource(new StringReader(xmlString));
        return xmlBuilder;
    }

    public static XmlBuilder fromClasspath(String classpathLocation) {
        XmlBuilder xmlBuilder = new XmlBuilder();
        xmlBuilder.inputSource = new InputSource(XmlBuilder.class.getResourceAsStream(classpathLocation));
        return xmlBuilder;
    }

    public XmlBuilder withNamespaces() {
        this.isNamespaceAware = true;
        return this;
    }

    public XmlBuilder validate() {
        this.isValidating = true;
        return this;
    }

    public Document build() throws XmlException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(this.isNamespaceAware);
            documentBuilderFactory.setValidating(this.isValidating);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputSource);

            return document;
        } catch (ParserConfigurationException e) {
            throw new XmlException("Cannot create parser.", e);
        } catch (SAXException e) {
            throw new XmlException("Cannot parse XML due to syntax errors. ", e);
        } catch (IOException e) {
            throw new XmlException("Cannot parse XML due to I/O errors. ", e);
        }
    }


}
