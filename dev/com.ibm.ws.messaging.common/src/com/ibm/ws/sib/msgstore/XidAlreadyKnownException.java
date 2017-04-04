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
public class XidAlreadyKnownException extends TransactionException
{
    private static final long serialVersionUID = -7548775616524029862L;

    public XidAlreadyKnownException()
    {
        super();
    }

    public XidAlreadyKnownException(String message)
    {
        super(message);
    }

    public XidAlreadyKnownException(Throwable exception)
    {
        super(exception);
    }

    public XidAlreadyKnownException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public XidAlreadyKnownException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public XidAlreadyKnownException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

