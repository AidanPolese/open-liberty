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
public class XidStillAssociatedException extends TransactionException
{
    private static final long serialVersionUID = -4504349232943302987L;

    public XidStillAssociatedException()
    {
        super();
    }

    public XidStillAssociatedException(String message)
    {
        super(message);
    }

    public XidStillAssociatedException(Throwable exception)
    {
        super(exception);
    }

    public XidStillAssociatedException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public XidStillAssociatedException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public XidStillAssociatedException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

