package com.ibm.ws.sib.msgstore;
/*  
 *==========================================================================
 * IBM Confidential OCO Source Material
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U. S. Copyright Office.
 *================================================================================
 * 
 * Change activity:
 *
 * Reason            Date    Origin   Description
 * --------------- -------- -------- ----------------------------------------
 * 93022            27/02/13 kavitha  XidInvalidException for invalid XID values
 * ============================================================================
 */


/**
 * A Message Store sub-exception that defines exceptions that occur during
 * transaction processing.
 */
public class XidInvalidException extends TransactionException
{
    private static final long serialVersionUID = 6662550753206235517L;

    public XidInvalidException()
    {
        super();
    }

    public XidInvalidException(String message)
    {
        super(message);
    }

    public XidInvalidException(Throwable exception)
    {
        super(exception);
    }

    public XidInvalidException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public XidInvalidException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public XidInvalidException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

