package com.github.novotnyr.swixml.ser;

import com.github.novotnyr.swixml.XmlException;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.function.Function;

public class XmlSerializer {
    public static ToStringBuilder toString(Node node) {
        return new ToStringBuilder(node);
    }

    public static class ToStringBuilder {
        private boolean omitXmlDeclaration = true;

        private boolean indenting = true;

        private Node node;

        private ToStringBuilder(Node node) {
            this.node = node;
        }

        public ToStringBuilder includeXmlDeclaration() {
            this.omitXmlDeclaration = false;
            return this;
        }

        public ToStringBuilder doNotIndent() {
            this.indenting = false;
            return this;
        }

        public ToStringBuilder omitXmlDeclaration() {
            this.omitXmlDeclaration = true;
            return this;
        }

        public ToStringBuilder indent() {
            this.indenting = true;
            return this;
        }

        public NodeToStringSerializer build() {
            return new NodeToStringSerializer(this);
        }

        public String serialize() {
            return new NodeToStringSerializer(this)
                    .apply(this.node);
        }
    }


    public static class NodeToStringSerializer implements Function<Node, String> {
        private final ToStringBuilder xmlToStringSerializerBuilder;

        public NodeToStringSerializer(ToStringBuilder xmlToStringSerializerBuilder) {
            this.xmlToStringSerializerBuilder = xmlToStringSerializerBuilder;
        }

        @Override
        public String apply(Node node) {
            return serialize(node);
        }

        public String serialize(Node node) {
            StringWriter stringWriter = new StringWriter();
            try {
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, this.xmlToStringSerializerBuilder.omitXmlDeclaration ? "yes" : "no");
                t.setOutputProperty(OutputKeys.INDENT, this.xmlToStringSerializerBuilder.indenting ? "yes" : "no");
                t.transform(new DOMSource(node), new StreamResult(stringWriter));
            } catch (TransformerException e) {
                throw new XmlException("Cannot convert DOM Node to String", e);
            }
            return stringWriter.toString();
        }
    }


}
