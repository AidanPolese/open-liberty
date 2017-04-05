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
 * A <code>IllegalOperation</code> exception is thrown by a
 * CSI plugin instance whenever an illegal operation is attempted.<p>
 */

public class IllegalOperationException
                extends CSIRuntimeException
{
    private static final long serialVersionUID = 3730695908019323766L;

    /**
     * Create a new IllegalOperationException with the associated string
     * description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public IllegalOperationException(String s) {

        super(s);

    } // IllegalOperationException

    /**
     * Print backtrace for this exception and any nested exception as well.
     */
    public void printStackTrace(PrintWriter s) {

        super.printStackTrace(s);

    } // printStackTrace

} // IllegalOperationException
