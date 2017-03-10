/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.schemagen.internal;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Simple XMLStreamWriter wrapper that removes white space, new lines & comments
 */
public class CompactOutputXMLStreamWriter extends DelegatingXMLStreamWriter {

    public CompactOutputXMLStreamWriter(XMLStreamWriter xmlWriter) {
        super(xmlWriter);
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        super.writeEndDocument();
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        super.writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        super.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        super.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        super.writeEndElement();
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        super.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        super.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        super.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        super.writeCharacters(text, start, len);
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        super.writeCharacters(text);
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        super.writeCData(data);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {}

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        super.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        super.writeProcessingInstruction(target);
    }
}
