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
 * Reason          Date     Origin       Description
 * --------------  -------- --------  ----------------------------------------
 * 199334.1        27/05/04  gareth   Add transaction size counter
 * LIDB3706-5.241  19/01/05  gareth   Add Serialization support
 * ============================================================================
 */


/**
 * This exception is thrown when the administered size limit for 
 * a transaction is reached upon calling Transaction.incrementCurrentSize()
 */
public class TransactionMaxSizeExceededException extends TransactionException
{
    private static final long serialVersionUID = -2900534940556773209L;

    public TransactionMaxSizeExceededException()
    {
        super();
    }

    public TransactionMaxSizeExceededException(String message)
    {
        super(message);
    }

    public TransactionMaxSizeExceededException(Throwable exception)
    {
        super(exception);
    }

    public TransactionMaxSizeExceededException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public TransactionMaxSizeExceededException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public TransactionMaxSizeExceededException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

