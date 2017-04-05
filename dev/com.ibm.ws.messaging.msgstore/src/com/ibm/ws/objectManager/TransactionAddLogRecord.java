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
 * TransactionAddLogRecord contains log information to redo an add of a
 * ManagedObject.
 * 
 * @author IBM Corporation
 */
class TransactionAddLogRecord extends LogRecord
{
    private static final Class cclass = TransactionAddLogRecord.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(cclass,
                                                                     ObjectManagerConstants.MSG_GROUP_TRAN);

    private static final long serialVersionUID = 3306878921970180238L;
    // The logicalUnitOfWork that the add operation belongs to.
    protected LogicalUnitOfWork logicalUnitOfWork;
    // The state of the transaction.
    protected int transactionState;
    // The Token queue manager object to add.
    protected Token token;
    // The serialized form of the ManagedObject.
    protected byte managedObjectBytes[];
    ObjectManagerByteArrayOutputStream serializedBytes;

    /**
     * Construct a LogRecord and prepare its buffers ready to write to the log
     * for a ManagedObject that is being added persistently as part of a Transaction.
     * 
     * @param internalTransaction performing the addition.
     * @param token of the ManagedObject being added.
     * @param serializedBytes containing the serialized form of the ManagedObject
     *            to be added.
     * @throws ObjectManagerException
     */
    protected TransactionAddLogRecord(InternalTransaction internalTransaction,
                                      Token token,
                                      ObjectManagerByteArrayOutputStream serializedBytes)
        throws ObjectManagerException {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "<init>",
                        new Object[] { logicalUnitOfWork, new Integer(transactionState), token, serializedBytes });

        this.logicalUnitOfWork = internalTransaction.getLogicalUnitOfWork();
        this.transactionState = internalTransaction.getState();
        this.token = token;
        this.serializedBytes = serializedBytes;

        // Get the buffers that the log record wants to write.
        buffers = getBuffers(internalTransaction.logRecordByteArrayOutputStream);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass,
                       "<init>");
    } // Constructor.

    /**
     * Constructor
     * 
     * @param dataInputStream from which to construct the LogRecord.
     * @param objectManagerState of the objectManager reconstructing the LogRecord.
     * @throws ObjectManagerException
     */
    protected TransactionAddLogRecord(java.io.DataInputStream dataInputStream,
                                      ObjectManagerState objectManagerState)
        throws ObjectManagerException {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this, cclass,
                        "<init>",
                        new Object[] { dataInputStream, objectManagerState });

        try {
            // LogRecord.type already read.
            logicalUnitOfWork = new LogicalUnitOfWork(dataInputStream);
            transactionState = dataInputStream.readInt();
            token = Token.restore(dataInputStream,
                                  objectManagerState);

            long managedObjectLength = dataInputStream.readLong();

            // Now get the serialized form of the ManagedObject.
            managedObjectBytes = new byte[(int) managedObjectLength];
            dataInputStream.read(managedObjectBytes);

        } catch (java.io.IOException exception) {
            // No FFDC Code Needed.
            ObjectManager.ffdc.processException(this, cclass, "<init>", exception, "1:115:1.8");

            if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
                trace.exit(this,
                           cclass,
                           "<init>"
                           , exception);
            throw new PermanentIOException(this,
                                           exception);
        } //  catch (java.io.IOException exception).

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
    protected ObjectManagerByteArrayOutputStream[] getBuffers(ObjectManagerByteArrayOutputStream byteArrayOutputStream)
                    throws ObjectManagerException {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        "getBuffers",
                        new Object[] { byteArrayOutputStream });

        ObjectManagerByteArrayOutputStream[] buffers = new ObjectManagerByteArrayOutputStream[2];

        // Create the buffer to contain the header for this log record.
        byteArrayOutputStream.reset();
        buffers[0] = byteArrayOutputStream;

        buffers[0].writeInt(LogRecord.TYPE_ADD);
        logicalUnitOfWork.writeSerializedBytes(buffers[0]);
        buffers[0].writeInt(transactionState);
        token.writeSerializedBytes(buffers[0]);
        buffers[0].writeLong(serializedBytes.getCount());

        // Now add a buffer containing the serialzed ManagedObject itself.
        buffers[1] = serializedBytes;

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass,
                       "getBuffers",
                       new Object[] { buffers });
        return buffers;
    } // getBuffers().

    /**
     * Called to perform recovery action during a warm start of the ObjectManager.
     * 
     * @param ObjectManagerState
     *            of the ObjectManager performing recovery.
     */
    public void performRecovery(ObjectManagerState objectManagerState)
                    throws ObjectManagerException
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this, cclass,
                        "performRecovery",
                        "objectManagerState=" + objectManagerState + "(ObjectManagerState)");

        if (Tracing.isAnyTracingEnabled() && trace.isDebugEnabled())
            trace.debug(this, cclass,
                        "logicalUnitOfWork=" + logicalUnitOfWork + "(LogicalUnitOfWork)"
                                        + " transactionState=" + transactionState + "(int)"
                                        + " token=" + token + "(token)"
                                        + " managedObjectBytes.length=" + managedObjectBytes.length + "(int)");

        // Recover the ManagedObject from its serialized bytes.
        ManagedObject addedManagedObject = ManagedObject.restoreFromSerializedBytes(managedObjectBytes,
                                                                                    objectManagerState);

        // Redo the add of the ManagedObject using its original Transaction.
        Transaction transactionForRecovery = objectManagerState.getTransaction(logicalUnitOfWork);
        // Reestablish the restored ManagedObject.
        ManagedObject managedObject = token.setManagedObject(addedManagedObject);
        managedObject.state = ManagedObject.stateConstructed;

        transactionForRecovery.add(managedObject); // Redo the add.
        // No need to reset the transaction state because Add can only be executed before
        // the transaction is prepared.
        // transactionForRecovery.internalTransaction.resetState(transactionState);

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this, cclass,
                       "performRecovery");
    } // Of method performRecovery.

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.objectManager.LogRecord#maximumSerializedSize()
     */
    protected static long maximumSerializedSize()
    {
        return 4 // Log Record Type.
               + LogicalUnitOfWork.maximumSerializedSize()
               + 4 // Transaction State.
               + Token.maximumSerializedSize()
               + 8 // Size of serialized ManagedObject.
        ;
    } // Of maximumSerializedSize().

} // End of class TransactionAddLogRecord.
