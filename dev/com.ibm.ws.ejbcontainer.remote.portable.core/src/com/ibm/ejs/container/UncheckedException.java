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
package com.ibm.ejs.container;

/**
 * An unchecked exception is thrown whenever the invocation of a bean
 * method raises an exception that the bean's method signature did
 * not explicitly declare. <p>
 */

public class UncheckedException
                extends java.rmi.RemoteException
{
    private static final long serialVersionUID = 4008328554878328030L;

    /**
     * Create a new <code>UncheckedException</code> instance. <p>
     * 
     * @param s the <code>String</code> describing the unchecked
     *            exception that was raised <p>
     * 
     * @param ex the <code>Throwable</code> that is the unchecked
     *            exception <p>
     */

    public UncheckedException(String s, Throwable ex) {
        super(s, ex);
    } // UncheckedException

} // UncheckedException
