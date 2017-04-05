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
 * <p>checkpointEndLogRecord marks the end of a checkpoint.
 */
class CheckpointEndLogRecord extends LogRecord
{
    private static final Class cclass = CheckpointEndLogRecord.class;
    private static final long serialVersionUID = -2254428502415067295L;

    private static Trace trace = ObjectManager.traceFactory.getTrace(CheckpointEndLogRecord.class,
                                                                     ObjectManagerConstants.MSG_GROUP_TRAN);

    /**
     * Construct a LogRecord and prepare its buffers ready to write to the log.
     * 
     * @throws ObjectManagerException
     */
    protected CheckpointEndLogRecord()
        throws ObjectManagerException
    {
        final String methodName = "<init>";
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        methodName);

        // Get the buffers that the log record wants to write.
        buffers = getBuffers();

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       methodName);
    } // CheckpointEndLogRecord(). 

    /**
     * Constructor
     * 
     * @param dataInputStream from which to construct the log record.
     * @throws ObjectManagerException
     */
    protected CheckpointEndLogRecord(java.io.DataInputStream dataInputStream)
        throws ObjectManagerException {
        final String methodName = "<init>";
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        methodName,
                        dataInputStream);

        // LogRecord.type already read.
        // Nothing left to do. 

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass
                       , methodName
                            );
    } // CheckpointEndLogRecord().  

    /**
     * Gives back the serialized LogRecord as arrays of bytes.
     * 
     * @return ObjectManagerByteArrayOutputStream[] the buffers containing the serialized LogRecord.
     */
    public ObjectManagerByteArrayOutputStream[] getBuffers()
                    throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this, cclass
                        , "getBuffers"
                            );

        ObjectManagerByteArrayOutputStream[] buffers = new ObjectManagerByteArrayOutputStream[1];

        // Create the buffer to contain the header for this log record. 
        buffers[0] = new ObjectManagerByteArrayOutputStream(4);
        buffers[0].writeInt(LogRecord.TYPE_CHECKPOINT_END);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass
                       , "getBuffers"
                       , new Object[] { buffers }
                            );
        return buffers;
    } // getBuffers(). 

    /**
     * Called to perform recovery action during a warm start of the ObjectManager.
     * 
     * @param ObjectManagerState of the ObjectManager performing recovery.
     * 
     */
    public void performRecovery(ObjectManagerState objectManagerState)
                    throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this, cclass
                        , "performRecovery"
                        , " objectManager=" + objectManagerState + "(ObjectManagerState)"
                          + ")"
                            );

        objectManagerState.checkpointEndSeen = true;

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass
                       , "performRecovery"
                            );
    } // Of method performRecovery. 
} // End of class CheckpointEndLogRecord.
