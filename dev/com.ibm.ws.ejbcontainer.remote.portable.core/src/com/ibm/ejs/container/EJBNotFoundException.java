/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2004, 2005
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

/**
 * This exception is thrown to indicate an attempt has been made to use
 * an EJB that is not known to the EJB Container. Most likely, the application
 * is either not currently installed, or has not been started.
 */
public class EJBNotFoundException
                extends ClassNotFoundException
{
    private static final long serialVersionUID = -7732975938299597918L;

    /**
     * Create a new <code>EJBNotFoundException</code> instance. <p>
     * 
     * @param j2eeName unique j2ee name representing the EJB.
     */
    public EJBNotFoundException(String j2eeName)
    {
        super(j2eeName);
    }

    /**
     * Create a new <code>EJBNotFoundException</code> with the
     * specified detail message and cause.
     * 
     * @param message - the detail message (which is saved for later
     *            retrieval by the getMessage() method).
     * @param cause - the cause (which is saved for later retrieval by
     *            the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public EJBNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
