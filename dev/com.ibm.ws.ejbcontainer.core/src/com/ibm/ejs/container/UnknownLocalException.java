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
package com.ibm.ejs.container;

/**
 * This exception is thrown to indicate a unknown container error has occurred.
 **/
public class UnknownLocalException extends ContainerLocalException
{
    private static final long serialVersionUID = -2906017836291024122L;

    public UnknownLocalException()
    {}

    public UnknownLocalException(java.lang.String message)
    {
        super(message);
    }

    public UnknownLocalException(java.lang.String message, java.lang.Exception ex)
    {
        super(message, ex);
    }
} // UnknownLocalException
