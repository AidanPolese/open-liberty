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
package com.ibm.ws.ejbcontainer.osgi.internal.diagnostics;

import java.io.PrintWriter;

import com.ibm.ws.ejbcontainer.diagnostics.TextIntrospectionWriter;

/**
 * IntrospectionWriter implementation that prints all introspection data using
 * an OutputStream. <p>
 */
public class IntrospectionWriterImpl extends TextIntrospectionWriter {
    private final PrintWriter writer;

    public IntrospectionWriterImpl(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    protected void writeln(String line) {
        writer.println(line);
    }
}
