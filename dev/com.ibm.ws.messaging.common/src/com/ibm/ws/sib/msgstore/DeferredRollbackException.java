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
 * PK81848.2       08/07/09 gareth   Allow deferred rollback processing to take place
 * ============================================================================
 */


/**
 * This exception is a result of a rollback being requested on a
 * separate thread to the prepare/commit thread that is 
 * currently in the middle of tran completion.
 */
public class DeferredRollbackException extends TransactionException
{
    private static final long serialVersionUID = 335280537236623108L;

    public DeferredRollbackException()
    {
        super();
    }

    public DeferredRollbackException(String message)
    {
        super(message);
    }

    public DeferredRollbackException(Throwable exception)
    {
        super(exception);
    }

    public DeferredRollbackException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public DeferredRollbackException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public DeferredRollbackException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}

