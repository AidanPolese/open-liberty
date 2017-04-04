/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.cpmi;

public class ASNnameNotUniqueException extends CPMIException
{
    private static final long serialVersionUID = -5462731871331978475L;

    /**
     * Create a new CPMIException with an empty description string.
     */
    public ASNnameNotUniqueException()
    {
        super();
    }

    /**
     * Create a new CPMIException with the associated string description. <p.
     * 
     * @param s the <code>String</code> describing the exception <p>
     */
    public ASNnameNotUniqueException(String s)
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
    public ASNnameNotUniqueException(String s, Throwable ex)
    {
        super(s, ex);
    }
}
