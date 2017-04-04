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
 * Thrown when the object manager catches NIOException which cannot safely be retried.
 */
public final class PermanentNIOException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -6397836846919935377L;

    /**
     * Permanent NIO Exception.
     * 
     * @param Object which is throwing this PermanentNIOException.
     * @param Exception which was caught.
     */
    protected PermanentNIOException(Object source,
                                    Exception exception)
    {
        super(source,
              PermanentNIOException.class,
              exception);

    } // PermanentNIOException().

} // End of class PermanentNIOException.
