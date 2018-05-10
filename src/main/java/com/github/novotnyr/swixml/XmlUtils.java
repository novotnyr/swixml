package com.github.novotnyr.swixml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class XmlUtils {

    public static String toString(Node node) {
        SerializationOptions options = new SerializationOptions()
                .omitXmlDeclaration()
                .indent();

        return toString(node, options);
    }

    public static String toString(Node node, SerializationOptions serializationOptions) {
        StringWriter stringWriter = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, serializationOptions.omitXmlDeclaration ? "yes" : "no");
            t.setOutputProperty(OutputKeys.INDENT, serializationOptions.indenting ? "yes" : "no");
            t.transform(new DOMSource(node), new StreamResult(stringWriter));
        } catch (TransformerException e) {
            throw new XmlException("Cannot convert DOM Node to String", e);
        }
        return stringWriter.toString();
    }

    public static List<Node> toList(NodeList nodeList) {
        List<Node> nodes = new ArrayList<Node>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }
        return nodes;
    }

    public static String findNodeValue(Document document, String namespace, String localName) {
        NodeList nodeList = document.getElementsByTagNameNS(namespace, localName);
        List<Node> nodes = XmlUtils.toList(nodeList);
        if (nodes.isEmpty()) {
            return null;
        } else {
            return nodes.get(0).getTextContent();
        }
    }

    public static class SerializationOptions {
        private boolean omitXmlDeclaration = true;

        private boolean indenting = true;

        public SerializationOptions includeXmlDeclaration() {
            this.omitXmlDeclaration = false;
            return this;
        }

        public SerializationOptions doNotIndent() {
            this.indenting = false;
            return this;
        }

        public SerializationOptions omitXmlDeclaration() {
            this.omitXmlDeclaration = true;
            return this;
        }

        public SerializationOptions indent() {
            this.indenting = true;
            return this;
        }
    }
}
