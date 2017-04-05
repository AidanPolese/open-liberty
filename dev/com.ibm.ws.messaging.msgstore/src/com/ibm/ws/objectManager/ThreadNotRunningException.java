package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * ============================================================================
 */

/**
 * Thrown when a helper thread is requested to perform an action while th thread is not running.
 * This might happen because the thread is asked to perform an operation after it has been asked
 * to shut down.
 * 
 * @param source the Object throwing the exception.
 * @param threadName the name of the thread which is being asked to perform the request.
 * @param requestName describes the request being made.
 */
public final class ThreadNotRunningException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 3843616808311058994L;

    protected ThreadNotRunningException(Object source,
                                        String threadName,
                                        String requestName)
    {
        super(source,
              ThreadNotRunningException.class,
              new Object[] { threadName, requestName });
    } // ThreadNotRunningException(). 
} // class ThreadNotRunningException.
