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
 * Thrown when the object manager detects an IO error which can safely be retried
 * without the need to restart the ObjectManager.
 */
public final class TemporaryIOException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 819675519281478532L;

    /**
     * Temporary IO Exception.
     * 
     * @param Object which is throwing this TemporaryIOException.
     * @param java.io.IOException which was caught.
     */
    protected TemporaryIOException(Object source,
                                   java.io.IOException ioException)
    {
        super(source,
              TemporaryIOException.class,
              ioException,
              ioException);

    } // TemporaryIOException().

} // class TemporaryIOException.
