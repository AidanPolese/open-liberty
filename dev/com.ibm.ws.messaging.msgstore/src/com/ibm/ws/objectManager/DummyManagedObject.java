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
 * A dummy ManagedObject used during recovery where the original object has been deleted.
 */
final class DummyManagedObject extends ManagedObject
{
    private static final Class cclass = DummyManagedObject.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(cclass,
                                                                     ObjectManagerConstants.MSG_GROUP_OBJECTS);
    private static final long serialVersionUID = -4813510654778615613L;

    protected String name; // Identifies the dummy.

    /**
     * Constructor creates an dummy ManagedObject.
     * 
     * @param name identifies the dummy ManagedObject.
     * @throws ObjectManagerException
     */
    public DummyManagedObject(String name)
        throws ObjectManagerException
    {
        final String methodName = "<init>";
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        methodName,
                        name);

        this.name = name;

        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       methodName);
    } // DummyManagdObject().

    // --------------------------------------------------------------------------
    // extends ManagedObject.
    // --------------------------------------------------------------------------
    /**
     * Replace the state of this object with the same object in some other state. Used for to restore the before immage if
     * a transaction rolls back or is read from the log during restart.
     * 
     * @param other the object this object is to become a clone of.
     */
    public void becomeCloneOf(ManagedObject other)
    {
        final String methodName = "becomeCloneOf";
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.entry(this,
                        cclass,
                        methodName,
                        other);

        //!! See transactionDeleteLogRecord as to why this is necessary.
        //!! The dummy is used where we need to delete something in recovery that is already deleted.
        //!! Unfortunately a rollback of the original after an OptimisticReplace
        //!! during recovery will cause classCastException
        /*
         * !! DummyManagedObject dummyManagedObject = (DummyManagedObject)other; name = dummyManagedObject.name; !!
         */
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
            trace.exit(this,
                       cclass,
                       methodName);
    } // becomeCloneOf().
} // class DummyManagedObject.
