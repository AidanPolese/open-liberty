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
 * --------------- -------- -------- ------------------------------------------
 *  251161         07/04/05 gareth    Add ObjectManager code to CMVC
 *  343689         04/04/06 gareth    Modify trace output cont.
 *  607710         21/08/09 gareth    Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

import com.ibm.ws.objectManager.utils.Trace;
import com.ibm.ws.objectManager.utils.Tracing;

/**
 * <p>LogInput, performs reading of log records.<\p>
 * 
 * @author IBM Corporation.
 */
public abstract class LogInput
{
    private static final Class cclass = LogInput.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(cclass,
                                                                     ObjectManagerConstants.MSG_GROUP_LOG);

    // The ObjectManagerState this LogInput is instantiated by.
    protected ObjectManagerState objectManagerState;

    /**
     * Constructor
     * 
     * @param objectManagerState creating the LogInput.
     * @throws ObjectManagerException
     */
    protected LogInput(ObjectManagerState objectManagerState)
        throws ObjectManagerException {
        final String methodName = "<init>";
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        methodName,
                        objectManagerState);

        this.objectManagerState = objectManagerState;

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       methodName);
    } // LogInput().

    /**
     * Prevents further operations on the log file.
     * 
     * @throws ObjectManagerException
     */
    public abstract void close()
                    throws ObjectManagerException;

    /**
     * Gives the size of the log file in use.
     * 
     * @return long the size of the log file in bytes.
     */
    protected abstract long getLogFileSize();

    /**
     * Reads the next record from the log.
     * 
     * @return LogRecord read.
     * @throws ObjectManagerException
     * @throws LogFileExhaustedException if there are no more logRecords left to read.
     */
    public abstract LogRecord readNext()
                    throws ObjectManagerException;

} // End of class LogInput.
