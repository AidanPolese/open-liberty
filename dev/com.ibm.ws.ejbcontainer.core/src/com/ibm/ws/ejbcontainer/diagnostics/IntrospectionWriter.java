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

/**
 * Common interface for writing introspection (dump) data using a variety of
 * different output resources; such as an OutputSream, Tr.dump, and IncidentStream.
 */
public interface IntrospectionWriter {
    /**
     * Begin a section of output.
     * 
     * @param title the section title, or null
     */
    public void begin(String title);

    /**
     * End a section of output.
     */
    public void end();

    /**
     * Prints a String and then terminates the line.
     * 
     * @param line the String value to be printed
     */
    public void println(String line);

    /**
     * Convenience method for dumping an array of introspection data. <p>
     * 
     * @param dumpData introspection data to be printed
     */
    public void dump(String[] dumpData);
}
