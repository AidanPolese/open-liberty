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
 * Thrown when one of the AccessIntent consistency rules is violated
 * by a transaction.
 */
public class InconsistentAccessIntentException extends WsException
{
    private static final long serialVersionUID = -3793473681323788903L;

    /**
     * Constructs a new <code>InconsisentAcessIntentException</code> with null
     * as its detail message and cause.
     */
    public InconsistentAccessIntentException()
    {}

    /**
     * Constructs a new <code>InconsisentAcessIntentException</code> with a
     * specified detail message and null as the cause.
     * 
     * @param message - the detail message (which is saved for later
     *            retrieval by the getMessage() method).
     */
    public InconsistentAccessIntentException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new <code>InconsisentAcessIntentException</code> with the
     * specified detail message and cause.
     * 
     * @param message - the detail message (which is saved for later
     *            retrieval by the getMessage() method).
     * @param cause - the cause (which is saved for later retrieval by
     *            the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public InconsistentAccessIntentException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Construct a new WsException with a null detail message and a cause field
     * set to the specified Throwable. Subsequent calls to the {@link #initCause} method on this instance will result in an exception. The value of the cause
     * field may be retrieved at any time via the {@link #getCause()} method.
     * <p>
     * 
     * @param cause - the Throwable that was caught and is considered the root cause
     *            of this exception. Null is tolerated.
     */
    public InconsistentAccessIntentException(Throwable cause)
    {
        super(cause);
    }

} // end of class

