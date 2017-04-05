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
 *                 01/05/03  gareth    Original
 * 189573          05/02/04  gareth    Add NLS support to transaction code
 * LIDB3706-5.241  19/01/05  gareth    Add Serialization support
 * ============================================================================
 */


/**
 * A Message Store sub-exception that defines exceptions that occur during
 * transaction processing.
 */
public class TransactionException extends MessageStoreException
{
    private static final long serialVersionUID = 2892598558684430048L;

    public TransactionException()
    {
        super();
    }

    public TransactionException(String message)
    {
        super(message);
    }

    public TransactionException(Throwable exception)
    {
        super(exception);
    }

    public TransactionException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public TransactionException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public TransactionException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

