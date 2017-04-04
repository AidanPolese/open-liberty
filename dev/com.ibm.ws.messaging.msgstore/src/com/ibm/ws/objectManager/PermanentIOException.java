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
 * Thrown when the object manager catches IOException which cannot safely be retried.
 */
public final class PermanentIOException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -3084699571860961761L;

    /**
     * Permanent IO Exception.
     * 
     * @param Class of static which throws this PermanentIOException.
     * @param java.io.IOException which was caught.
     */
    protected PermanentIOException(Class sourceClass,
                                   java.io.IOException ioException)
    {
        super(sourceClass,
              PermanentIOException.class,
              ioException,
              ioException);

    } // PermanentIOException().

    /**
     * Permanent IO Exception.
     * 
     * @param Object which is throwing this PermanentIOException.
     * @param java.io.IOException which was caught.
     */
    protected PermanentIOException(Object source,
                                   java.io.IOException ioException)
    {
        super(source,
              PermanentIOException.class,
              ioException,
              ioException);

    } // PermanentIOException().

} // End of class PermanentIOException.
