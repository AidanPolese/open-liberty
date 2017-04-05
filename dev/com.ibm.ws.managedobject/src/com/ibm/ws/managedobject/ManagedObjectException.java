/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.managedobject;

/**
 * Thrown if an error occur while creating a managed object or managed object
 * factory.
 */
public class ManagedObjectException
                extends Exception
{
    private static final long serialVersionUID = 2189215815982016336L;

    public ManagedObjectException(Throwable t)
    {
        super(t);
    }
}
