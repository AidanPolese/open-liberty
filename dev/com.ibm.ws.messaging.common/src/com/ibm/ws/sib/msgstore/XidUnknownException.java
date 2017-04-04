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
 * 168081          01/09/03  gareth   GlobalTransaction Support (Local Clients)
 * 189573          05/02/04  gareth   Add NLS support to transaction code
 * LIDB3706-5.241  19/01/05  gareth   Add Serialization support
 * ============================================================================
 */


/**
 * A Message Store sub-exception that defines exceptions that occur during
 * transaction processing.
 */
public class XidUnknownException extends TransactionException
{
    private static final long serialVersionUID = 6662550753206235517L;

    public XidUnknownException()
    {
        super();
    }

    public XidUnknownException(String message)
    {
        super(message);
    }

    public XidUnknownException(Throwable exception)
    {
        super(exception);
    }

    public XidUnknownException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public XidUnknownException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public XidUnknownException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

