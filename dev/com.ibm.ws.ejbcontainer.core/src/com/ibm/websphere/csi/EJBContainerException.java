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
package com.ibm.websphere.csi;

import com.ibm.ws.exception.WsException;

/**
 * This subclass of com.ibm.ws.exception.WsException is intended to
 * be used by the methods in EJBContainer inteface.
 */
public class EJBContainerException extends WsException
{
    private static final long serialVersionUID = -9161278349757795225L;

    /**
     * Constructs a new <code>EJBContainerException</code> with null
     * as its detail message and cause.
     */
    public EJBContainerException()
    {
        super();
    }

    /**
     * Constructs a new <code>EJBContainerException</code> with a
     * specified detail message and null as the cause.
     * 
     * @param message - the detail message (which is saved for later
     *            retrieval by the getMessage() method).
     */
    public EJBContainerException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>EJBContainerException</code> with the
     * specified detail message and cause.
     * 
     * @param message - the detail message (which is saved for later
     *            retrieval by the getMessage() method).
     * @param cause - the cause (which is saved for later retrieval by
     *            the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public EJBContainerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new <code>EJBContainerException</code> with the
     * specified detail message and cause.
     * 
     * @param cause - the cause (which is saved for later retrieval by
     *            the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public EJBContainerException(Throwable cause)
    {
        super(cause);
    }

} // end of class
