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
 * <p>
 * TransactionBackoutLogRecord contains log information to redo a backout of a Transaction.
 * 
 * @author IBM Corporation
 */
class TransactionBackoutLogRecord
                extends LogRecord {
    private static final Class cclass = TransactionBackoutLogRecord.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(cclass,
                                                                     ObjectManagerConstants.MSG_GROUP_TRAN);
    private static final long serialVersionUID = 4921554013027150249L;

    // The logicalUnitOfWork that the backout operation belongs to.
    protected LogicalUnitOfWork logicalUnitOfWork;

    /**
     * Construct a LogRecord and prepare its buffers ready to write to the log.
     * 
     * @param internalTransaction performing the backout.
     * @throws ObjectManagerException
     */
    protected TransactionBackoutLogRecord(InternalTransaction internalTransaction)
        throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "<init>",
                        new Object[] { internalTransaction });

        this.logicalUnitOfWork = internalTransaction.getLogicalUnitOfWork();

        // Get the buffers that the log record wants to write.
        buffers = getBuffers(internalTransaction.logRecordByteArrayOutputStream);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       "<init>");
    } // Of constructor.

    /**
     * Constructor
     * 
     * @param dataInputStream from which to construct the log record.
     * @throws ObjectManagerException
     */
    protected TransactionBackoutLogRecord(java.io.DataInputStream dataInputStream)
        throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "<init>",
                        dataInputStream);

        // LogRecord.type already read.
        logicalUnitOfWork = new LogicalUnitOfWork(dataInputStream);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       "<init>");
    } // Constructor.

    /**
     * Gives back the serialized LogRecord as arrays of bytes.
     * 
     * @param byteArrayOutputStream used to hold the log record.
     * @return ObjectManagerByteArrayOutputStream[] the buffers containing the serialized LogRecord.
     * @throws ObjectManagerException
     */
    public ObjectManagerByteArrayOutputStream[] getBuffers(ObjectManagerByteArrayOutputStream byteArrayOutputStream)
                    throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "getBuffers");

        ObjectManagerByteArrayOutputStream[] buffers = new ObjectManagerByteArrayOutputStream[1];

        //  Create the buffer to contain the header for this log record.
        byteArrayOutputStream.reset();
        buffers[0] = byteArrayOutputStream;

        buffers[0].writeInt(LogRecord.TYPE_BACKOUT);
        logicalUnitOfWork.writeSerializedBytes(buffers[0]);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       "getBuffers",
                       new Object[] { buffers });
        return buffers;
    } // getBuffers().

    /**
     * Called to perform recovery action during a warm start of the ObjectManager.
     * 
     * @param objectManagerState of the ObjectManager performing recovery.
     * @throws ObjectManagerException
     */
    public void performRecovery(ObjectManagerState objectManagerState)
                    throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "performRecovery",
                        new Object[] { objectManagerState, logicalUnitOfWork });

        Transaction transactionForRecovery = objectManagerState.getTransaction(logicalUnitOfWork);
        transactionForRecovery.backout(false); // Do not reuse this Transaction.

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       "performRecovery");
    } // performRecovery().

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.objectManager.LogRecord#maximumSerializedSize()
     */
    protected static long maximumSerializedSize()
    {
        return 4 // Log Record Type.
        + LogicalUnitOfWork.maximumSerializedSize();
    } // maximumSerializedSize().

} // class TransactionBackoutLogRecord.
