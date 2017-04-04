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
 * The base exception type for all runtime exceptions thrown by the
 * container-server interface. <p>
 */
public class CSIRuntimeException
                extends RuntimeException
{
    private static final long serialVersionUID = 2377655548638069491L;

    /**
     * Create a new CSIRuntimeException with an empty description string. <p>
     */
    public CSIRuntimeException() {

    } // CSIRuntimeException

    /**
     * Create a new CSIRuntimeException with the associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public CSIRuntimeException(String s) {

        super(s);

    } // CSIRuntimeException

    /**
     * Print backtrace for this exception and any nested exception as well.
     */
    public void printStackTrace(PrintWriter s) {

        super.printStackTrace(s);

    } // printStackTrace

} // CSIRuntimeException
