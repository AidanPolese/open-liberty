/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.xml.internal;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 *
 */
@Trivial
public class DepthAwareXMLStreamReader extends StreamReaderDelegate {

    private int depth = 0;

    public DepthAwareXMLStreamReader(XMLStreamReader reader) {
        super(reader);
    }

    @Override
    public int next() throws XMLStreamException {
        final int event = super.next();
        if (event == XMLStreamConstants.START_ELEMENT) {
            ++depth;
        } else if (event == XMLStreamConstants.END_ELEMENT) {
            --depth;
        }
        return event;
    }

    @Override
    public int nextTag() throws XMLStreamException {
        final int event = super.nextTag();
        if (event == XMLStreamConstants.START_ELEMENT) {
            ++depth;
        } else if (event == XMLStreamConstants.END_ELEMENT) {
            --depth;
        }
        return event;
    }

    public boolean hasNext(int currentDepth) throws XMLStreamException {
        return super.hasNext() && depth >= currentDepth;
    }

    public int getDepth() {
        return depth;
    }
}
