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
 * Change activity:
 *
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 *   251161        07/04/05   gareth    Add ObjectManager code to CMVC
 * ============================================================================
 */

/**
 * Thrown when there are no more log records to read from the Log File.
 * 
 * @param Object
 *            throwing this exception.
 * @param Exception
 *            causing this one to be raised.
 */
public final class LogFileExhaustedException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 6570268559070389810L;

    protected LogFileExhaustedException(Object source,
                                        Exception underlyingException)
    {
        super(source,
              LogFileExhaustedException.class,
              underlyingException,
              new Object[] { source,
                            underlyingException });
    } // LogFileExhaustedException().
} // class LogFileExhaustyedException.
