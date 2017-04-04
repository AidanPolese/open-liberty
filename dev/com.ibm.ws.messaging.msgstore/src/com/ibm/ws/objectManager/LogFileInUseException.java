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
 * Thrown when an attempt is made to use a logFile that is already being used by another program.
 * 
 * @param String the name of the log file.
 */
public final class LogFileInUseException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -795499354868379899L;

    protected LogFileInUseException(Object source,
                                    String logFileName)
    {
        super(source,
              LogFileInUseException.class,
              new Object[] { logFileName });
    } // LogFileInUseException.
} // class LogFileInUseException.
