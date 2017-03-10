/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.schemagen.internal;

import javax.xml.stream.XMLStreamException;

/**
 *
 */
public interface DocumentationWriter {
    public void writeDoc() throws XMLStreamException;
}
