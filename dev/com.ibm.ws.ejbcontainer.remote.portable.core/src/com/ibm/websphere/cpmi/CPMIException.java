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
package com.ibm.websphere.cpmi;

public class CPMIException extends java.rmi.RemoteException
{
    private static final long serialVersionUID = 7080138616122049935L;

    /**
     * Create a new CPMIException with an empty description string.
     */
    public CPMIException()
    {}

    /**
     * Create a new CPMIException with the associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public CPMIException(String s)
    {
        super(s);
    }

    /**
     * Create a new CPMIException with the associated string description and
     * nested exception. <p>
     * 
     * @param s the <code>String</code> describing the exception <p>
     * 
     * @param ex the nested <code>Throwable</code>
     */
    public CPMIException(String s, Throwable ex)
    {
        super(s, ex);
    }
}
