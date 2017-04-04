/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.persistence;

public class EnumeratorException extends Exception
{
    private static final long serialVersionUID = 4412754110898535663L;

    public EnumeratorException()
    {
        this.detail = null;
    }

    public EnumeratorException(String s)
    {
        super(s);
        this.detail = null;
    }

    public EnumeratorException(String s, Throwable detail)
    {
        super(s);
        this.detail = detail;
    }

    public String toString()
    {
        String s = "com.ibm.ejs.persitence.EnumeratorException";
        if (detail != null) {
            s += "\n\toriginal exception:\n";
            s += detail.toString();
        }
        return s;
    }

    private final Throwable detail;
}
