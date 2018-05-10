package com.github.novotnyr.swixml.ser;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

public class XmlToStringSerializerTest {
    @Test
    public void testSimpleSerialize() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        Element child = document.createElement("child");
        root.appendChild(child);

        String result = XmlSerializer
                .toString(document)
                .serialize();

        Assert.assertEquals("<root>\n<child/>\n</root>\n", result);
    }

    @Test
    public void testCreation() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        Element child = document.createElement("child");
        root.appendChild(child);

        String result = XmlSerializer
                .toString(document)
                .doNotIndent()
                .includeXmlDeclaration()
                .serialize();

        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root><child/></root>", result);
    }

    @Test
    public void testSerializeWithPrettyPrint() throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = document.createElement("root");
        document.appendChild(root);

        Element child = document.createElement("child");
        root.appendChild(child);

        String result = XmlSerializer
                .toString(document)
                .includeXmlDeclaration()
                .indent()
                .serialize();

        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<root>\n<child/>\n</root>\n", result);
    }
}