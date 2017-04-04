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
 * Reason            Date    Origin    Description
 * --------------- -------- --------  ----------------------------------------
 * 168080          18/07/03  gareth    Local Transaction Support (Local Clients)
 * 189573          05/02/04  gareth    Add NLS support to transaction code
 * LIDB3706-5.241  19/01/05  gareth    Add Serialization support
 * ============================================================================
 */


/**
 * This exception is a result of work associated with a transaction being rolled-back 
 * despite the completion direction of the transaction being commit. This is 
 * usually caused by an unrecoverable error occuring during completion of the
 * transaction.
 */
public class RollbackException extends TransactionException
{
    private static final long serialVersionUID = 8183924275251354462L;

    public RollbackException()
    {
        super();
    }

    public RollbackException(String message)
    {
        super(message);
    }

    public RollbackException(Throwable exception)
    {
        super(exception);
    }

    public RollbackException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public RollbackException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public RollbackException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

