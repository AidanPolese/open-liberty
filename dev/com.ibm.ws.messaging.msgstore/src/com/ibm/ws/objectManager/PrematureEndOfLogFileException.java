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
 * Thrown when the physical size of the log file is found to be shorter than the
 * LogHeader claims it should be.
 * 
 * @param Object throwing the exception.
 * @param String the name of the log file.
 * @param long the expected size of the log file.
 * @param long the byte found to be beyond the physical end of the file.
 */
public final class PrematureEndOfLogFileException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -5814027892097475805L;

    protected PrematureEndOfLogFileException(Object source,
                                             String logFileName
                                             , long expectedSize
                                             , long byteAccessed)
    {
        super(source,
              PrematureEndOfLogFileException.class,
              new Object[] { logFileName,
                            new Long(expectedSize),
                            new Long(byteAccessed) });

    } // PrematureEndOfLogFileException.
} // class PrematureEndOfLogFileException.