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
 * A starter implementation of the Set interface.
 * This is a new Set because AbstractCollection extends ManagedObject.
 * 
 * @see Set
 * @see AbstractCollection
 * @see AbstractSetView
 */
public abstract class AbstractSet
                extends AbstractCollection
                implements Set
{
    private static final Class cclass = AbstractSet.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(AbstractSet.class,
                                                                     ObjectManagerConstants.MSG_GROUP_MAPS);

    /**
     * Default no argument constructor.
     */
    protected AbstractSet()
    {
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
        {
            trace.entry(this, cclass, "<init>");
            trace.exit(this, cclass, "<init>");
        }
    } // AbstractSet().
} // AbstractSet.

