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
 * 
 * ============================================================================
 */

/**
 * Thrown when the object manager is asked to instantiate with a log file
 * of an unknown type.
 */
public final class InvalidLogFileTypeException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 189513734879231033L;

    /**
     * An invalid log file type was passed to the ObjectManager constructor.
     * 
     * @param Object which is throwing this Exception.
     * @param Exception which was caught.
     */
    protected InvalidLogFileTypeException(Object source,
                                          int logFileType)
    {
        super(source,
              InvalidLogFileTypeException.class,
              new Integer(logFileType));

    } // InvalidLogFileTypeException().

} // End of class InvalidLogFileTypeException.
