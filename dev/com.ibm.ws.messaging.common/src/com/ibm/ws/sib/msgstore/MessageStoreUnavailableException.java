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
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *  326323         02/12/05  gareth     Throw checked exception when MS is stopped
 * ============================================================================
 */


public class MessageStoreUnavailableException extends PersistenceException
{
    private static final long serialVersionUID = 8140943749856118198L;

    public MessageStoreUnavailableException()
    {
        super();
    }

    public MessageStoreUnavailableException(String message)
    {
        super(message);
    }

    public MessageStoreUnavailableException(Throwable exception)
    {
        super(exception);
    }

    public MessageStoreUnavailableException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public MessageStoreUnavailableException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public MessageStoreUnavailableException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

