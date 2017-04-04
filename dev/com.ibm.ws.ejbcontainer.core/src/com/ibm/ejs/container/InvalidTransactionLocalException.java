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
 * This exception is thrown to indicate a container transaction error has
 * occurred of a Local Interface. This functions in parallel to the
 * InvalidTransactionException of the remote interface.
 **/
public class InvalidTransactionLocalException
                extends ContainerLocalException
{
    private static final long serialVersionUID = -7960288866356505362L;

    public InvalidTransactionLocalException()
    {}

    public InvalidTransactionLocalException(java.lang.String message)
    {
        super(message);
    }

    public InvalidTransactionLocalException(java.lang.String message,
                                            java.lang.Exception ex)
    {
        super(message, ex);
    }

} // InvalidTransactionLocalException
