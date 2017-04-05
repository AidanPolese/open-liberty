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
 */
/**
 * Thrown when an attempt is made to locate a log file which does not exist or could not be created.
 * 
 * @version @(#) 1/25/13
 * @author Andrew_Banks
 */
public final class NonExistentLogFileException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 8292128603593836549L;

    /**
     * @param objectManagerState that is unable to locate the log file.
     * @param exception caught by the ObjectManagerstate.
     * @param logFileName the log file name.
     */
    protected NonExistentLogFileException(ObjectManagerState objectManagerState,
                                          Exception exception,
                                          String logFileName)
    {
        super(objectManagerState,
              NonExistentLogFileException.class,
              exception,
              new Object[] { objectManagerState,
                            exception,
                            logFileName });
    } // NonExistentLogFileException().
} // class NonExistentLogFileException.