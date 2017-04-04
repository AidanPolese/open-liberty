package com.ibm.ws.sib.msgstore.task;
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
 *                 17/03/04 drphill  Original
 * LIDB3706-5.239  19/01/05 gareth   Add Serialization support
 * 515543.2        08/07/08 gareth   Change runtime exceptions to caught exception
 * ============================================================================
 */

import com.ibm.ws.sib.msgstore.SevereMessageStoreException;

public class TaskListException extends SevereMessageStoreException
{
    private static final long serialVersionUID = 6923446797671545643L;

    public TaskListException()
    {
        super();
    }

    public TaskListException(String message)
    {
        super(message);
    }

    public TaskListException(Throwable exception)
    {
        super(exception);
    }

    public TaskListException(String message, Throwable exception)
    {
        super(message, exception);
    }

    public TaskListException(String message, Object[] inserts)
    {
        super(message, inserts);
    }

    public TaskListException(String message, Object[] inserts, Throwable exception)
    {
        super(message, inserts, exception);
    }
}
