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
 *  327709         06/12/05  gareth     Output NLS messages when OM files are full
 * ============================================================================
 */


public class PersistenceFullException extends PersistenceException
{
    private static final long serialVersionUID = -2879621386417154464L;

    public PersistenceFullException()
    {
        super();
    }

    public PersistenceFullException(String message)
    {
        super(message);
    }

    public PersistenceFullException(Throwable exception)
    {
        super(exception);
    }

    public PersistenceFullException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public PersistenceFullException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public PersistenceFullException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}


