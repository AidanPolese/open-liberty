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
 *  Reason         Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * 410652          12/04/07 gareth   Check Transactions ME at add time
 * ============================================================================
 */


/**
 * This exception will be thrown if the transaction being used
 * to add an Item (or ItemStream) to the MessageStore originates
 * from a different MS to that where the ItemStream being added
 * to resides.
 */
public class MismatchedMessageStoreException extends TransactionException
{
    private static final long serialVersionUID = -3504065490008776065L;

    public MismatchedMessageStoreException()
    {
        super();
    }

    public MismatchedMessageStoreException(String message)
    {
        super(message);
    }

    public MismatchedMessageStoreException(Throwable exception)
    {
        super(exception);
    }

    public MismatchedMessageStoreException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public MismatchedMessageStoreException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public MismatchedMessageStoreException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

