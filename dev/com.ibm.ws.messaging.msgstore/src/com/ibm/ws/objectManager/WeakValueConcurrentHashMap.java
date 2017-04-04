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
 *  607710         20/08/09 gareth    Add isAnyTracingEnabled() check around trace
 * ============================================================================
 */

import com.ibm.ws.objectManager.utils.Trace;
import com.ibm.ws.objectManager.utils.Tracing;

public class WeakValueConcurrentHashMap extends ConcurrentHashMap
{
    private static final Class cclass = WeakValueConcurrentHashMap.class;
    private static Trace trace = ObjectManager.traceFactory.getTrace(cclass,
                                                                     ObjectManagerConstants.MSG_GROUP_MAPS);

    public WeakValueConcurrentHashMap(int subMapCount)
    {
        super(subMapCount);
        if (Tracing.isAnyTracingEnabled() && trace.isEntryEnabled())
        {
            trace.entry(this, cclass, "<init>", new Integer(subMapCount));
            trace.exit(this, cclass, "<init>");
        }
    }

    java.util.Map makeSubMap()
    {
        return new WeakValueHashMap();
    }
} // class WeakValueConcurrentHashMap.
