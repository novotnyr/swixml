package com.github.novotnyr.swixml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public abstract class XmlUtils {
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
}
