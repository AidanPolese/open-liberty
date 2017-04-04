/*
 * @start_prolog@
 * Version: @(#) 1.8 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/ListPool.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 07:56:22 [7/2/12 05:58:59]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2003, 2005
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * CORE API 0.6 Implementation
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030717 mattheg  Original
 * D199177         040816 mattheg  JavaDoc
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

import java.util.ArrayList;
import java.util.List;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.ws.util.ObjectPool;

/**
 * This class is used to pool List objects that we used when sending
 * messages to the client.
 * 
 * @author Gareth Matthews
 */
public class ListPool {
    /** Our object pool */
    //...Romil Liberty change
    private ObjectPool listPool = null;

    /** Trace */
    private static final TraceComponent tc = SibTr.register(ListPool.class,
                                                            CommsConstants.MSG_GROUP,
                                                            CommsConstants.MSG_BUNDLE);

    /**
     * Constructs the list pool. Creates an ObjectPool with the
     * capacity to store 10 lists.
     */
    public ListPool() {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "<init>");

        listPool = new ObjectPool("ListPool", 10);

        if (tc.isEntryEnabled())
            SibTr.exit(tc, "<init>");
    }

    /**
     * Gets a list from the pool.
     * 
     * @return List
     */
    public synchronized List getList() {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "getList");

        // Remove a List from the pool
        List list = (List) listPool.remove();

        // If the list is null then there was none available in the pool
        // So create a new one
        if (list == null) {
            if (tc.isDebugEnabled())
                SibTr.debug(tc, "No list available from pool - creating a new one");

            // We could change the list implementation here if we wanted         
            list = new ArrayList(5);
        }

        if (tc.isEntryEnabled())
            SibTr.exit(tc, "getList");

        return list;
    }

    /**
     * Returns a list back to the pool so that it can be re-used
     * 
     * @param list
     */
    public synchronized void returnList(List list) {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "returnList");

        list.clear();
        listPool.add(list);

        if (tc.isEntryEnabled())
            SibTr.exit(tc, "returnList");
    }
}
