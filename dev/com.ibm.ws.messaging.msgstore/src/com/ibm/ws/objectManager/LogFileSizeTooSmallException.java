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
 * Thrown when you request that the log file size be reduced and it cannot contain the
 * existing log data.
 * 
 * @param LogOutput throwing this exception.
 * @param long the current size of the log file.
 * @param long the requested size of the log.
 * @param long the available space remaining in the log file.
 * @param float the ocupancy of the log if the new size were to be applied.
 * @param float the maximum occupancy before the ObjectManager triggers a checkpoint.
 */
public final class LogFileSizeTooSmallException
                extends ObjectManagerException
{
    private static final long serialVersionUID = -8054778811799273018L;

    protected LogFileSizeTooSmallException(LogOutput logOutput
                                           , long currentSize
                                           , long requestedSize
                                           , long availableSize
                                           , float newOcupany
                                           , float logFullPostCheckpointThreshold)
    {
        super(logOutput,
              LogFileSizeTooSmallException.class,
              new Object[] { new Long(currentSize)
                            , new Long(requestedSize)
                            , new Long(availableSize)
                            , new Float(newOcupany)
                            , new Float(logFullPostCheckpointThreshold) });

    } // LogFileSizeTooSmallException().
} // class LogFileSizeTooSmallException.