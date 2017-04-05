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
 * Thrown when the log file is to full to write a logRecord or reserve space.
 * 
 * @param source the Object throwing the exception.
 * @param newSpaceAllocatedInLogFile the number of bytes needed in the log file including any logRecord and overhead.
 * @param reservedDelta the number of bytes requested to be reserved.
 * @param the abbount of space that is currently available in the log.
 */
public final class LogFileFullException
                extends ObjectManagerException
{
    private static final long serialVersionUID = 7458663820401900531L;

    protected LogFileFullException(Object source,
                                   long newSpaceAllocatedInLogFile,
                                   long reservedDelta,
                                   long available)
    {
        super(source,
              LogFileFullException.class,
              new Object[] { new Long(newSpaceAllocatedInLogFile), new Long(reservedDelta), new Long(available) });
    } // LogFileFullException(). 
} // class LogFileFullException.
