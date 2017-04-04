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
 * Reason          Date     Origin   Description
 * --------------- -------- -------- ------------------------------------------
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

public class SevereMessageStoreException extends MessageStoreException
{
    private static final long serialVersionUID = -3790027338845641878L;

    public SevereMessageStoreException()
    {
        super();
    }

    public SevereMessageStoreException(String message)
    {
        super(message);
    }

    public SevereMessageStoreException(Throwable exception)
    {
        super(exception);
    }

    public SevereMessageStoreException(String message, Throwable exception)
    {
        super(message, exception);
    }

    /**
     * Provide a key and use formatted string
     * @param arg0
     * @param args
     */
    public SevereMessageStoreException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    /**
     * Provide a key and use formatted string
     * @param arg0
     * @param args
     */
    public SevereMessageStoreException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}