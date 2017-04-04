/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

import java.io.PrintWriter;

/**
 * A <code>StreamUnavailable</code> exception is thrown by a
 * <code>SessionBeanStore</code> instance when a stream for a given key
 * does not exist. <p>
 */

public class StreamUnavailableException
                extends CSIException
{
    private static final long serialVersionUID = 1818480400678509044L;

    /**
     * Create a new StreamUnavailableException with the associated string
     * description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public StreamUnavailableException(String s) {

        super(s);

    } // StreamUnavailableException

    /**
     * Create a new StreamUnavailable exception with the associated string
     * description and nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     */
    public StreamUnavailableException(String s, Throwable ex) {

        super(s, ex);

    } // StreamUnavailableException

    /**
     * Print backtrace for this exception and any nested exception as well.
     */
    public void printStackTrace(PrintWriter s) {

        super.printStackTrace(s);

    } // printStackTrace

} // StreamUnavailableException
