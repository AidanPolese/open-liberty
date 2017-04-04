/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.diagnostics;

import com.ibm.ws.ffdc.IncidentStream;

/**
 * IntrospectionWriter implementation that prints all introspection data using
 * IncidentStream. <p>
 */
public class IncidentStreamWriter extends TextIntrospectionWriter {
    private final IncidentStream is;

    public IncidentStreamWriter(IncidentStream is) {
        this.is = is;
    }

    /** {@inheritDoc} */
    @Override
    public void writeln(String line) {
        is.writeLine("", line);
    }
}
