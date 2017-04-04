package com.ibm.ws.sib.msgstore;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason            Date    Origin   Description
 * --------------- -------- -------- ----------------------------------------
 * 188054.1        16/03/04  gareth   Enhanced JDBC Exception handling
 * LIDB3706-5.241  19/01/05  gareth   Add Serialization support
 * ============================================================================
 */


public class ProtocolException extends TransactionException
{
    private static final long serialVersionUID = -7192283248493760657L;

    public ProtocolException()
    {
        super();
    }

    public ProtocolException(String message)
    {
        super(message);
    }

    public ProtocolException(Throwable exception)
    {
        super(exception);
    }

    public ProtocolException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public ProtocolException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public ProtocolException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

