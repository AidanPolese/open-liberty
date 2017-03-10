/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal.generator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 */
public class FeatureListWriter {
    private final XMLStreamWriter writer;
    private final Indenter i;

    public FeatureListWriter(FeatureListUtils utils) throws XMLStreamException, UnsupportedEncodingException {
        this.writer = utils.getXMLStreamWriter();
        this.i = utils.getIndenter();
    }

    public void writeTextElement(String nodeName, String text) throws IOException, XMLStreamException {
        i.indent(2);
        writer.writeStartElement(nodeName);
        writer.writeCharacters(text);
        writer.writeEndElement();
    }

    public void writeTextElementWithAttributes(String nodeName, String text, Map<String, String> attrs) throws IOException, XMLStreamException {
        i.indent(2);
        writer.writeStartElement(nodeName);
        for (Map.Entry<String, String> attr : attrs.entrySet()) {
            writer.writeAttribute(attr.getKey(), attr.getValue());
        }
        writer.writeCharacters(text);
        writer.writeEndElement();
    }

    public void startFeature(String nodeName, String name) throws IOException, XMLStreamException {
        i.indent(1);
        writer.writeStartElement(nodeName);
        if (name != null) {
            writer.writeAttribute("name", name);
        }
    }

    public void endFeature() throws IOException, XMLStreamException {
        i.indent(1);
        writer.writeEndElement();
    }

    /**
     * @param nodeName
     * @throws XMLStreamException
     * @throws IOException
     */
    public void startFeature(String nodeName) throws IOException, XMLStreamException {
        startFeature(nodeName, null);
    }

    public void writeIncludeFeature(String preferred, List<String> tolerates, String shortName) throws IOException, XMLStreamException {
        i.indent(2);
        writer.writeStartElement("include");
        writer.writeAttribute("symbolicName", preferred);
        if (shortName != null)
            writer.writeAttribute("shortName", shortName);
        if (tolerates != null) {
            StringBuilder toleratesValue = new StringBuilder();
            for (String tolerate : tolerates) {
                if (toleratesValue.length() > 0) {
                    toleratesValue.append(',');
                }
                toleratesValue.append(tolerate);
            }
            writer.writeAttribute("tolerates", toleratesValue.toString());
        }
        writer.writeEndElement();
    }
}