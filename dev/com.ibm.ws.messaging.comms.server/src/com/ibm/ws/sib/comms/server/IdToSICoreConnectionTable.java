/*
 * @start_prolog@
 * Version: @(#) 1.10 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/IdToSICoreConnectionTable.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 07:54:34 [7/2/12 05:58:58]
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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030811 prestona    Original
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SICoreConnection;

/**
 * A table which maps between conversation ID's and SICoreConnection object references.
 */
public class IdToSICoreConnectionTable {
    private static final TraceComponent tc = SibTr.register(IdToSICoreConnectionTable.class,
                                                            CommsConstants.MSG_GROUP,
                                                            CommsConstants.MSG_BUNDLE);

    static {
        if (tc.isDebugEnabled())
            SibTr.debug(tc, "@(#) SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/IdToSICoreConnectionTable.java, SIB.comms, WASX.SIB, aa1225.01 1.10");
    }

    // Maps integer id's to objects.
    private final IdToObjectMap map = new IdToObjectMap();

    /**
     * Adds an SICoreConnection into this map with the specified ID.
     * 
     * @param id
     * @param connection
     */
    public synchronized void add(int id, SICoreConnection connection) {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "add", "" + id);
        map.put(id, connection);
        if (tc.isEntryEnabled())
            SibTr.exit(tc, "add");
    }

    /**
     * Returns the SICoreConnection previously stored with the specified ID.
     * 
     * @param id
     * @return SICoreConnection
     */
    public synchronized SICoreConnection get(int id) {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "get", "" + id);
        SICoreConnection retValue = (SICoreConnection) map.get(id);
        if (tc.isEntryEnabled())
            SibTr.exit(tc, "get", retValue);
        return retValue;
    }

    /**
     * Removes an SICoreConnection from the map.
     * 
     * @param id
     */
    public synchronized void remove(int id) {
        if (tc.isEntryEnabled())
            SibTr.entry(tc, "remove", "" + id);
        if (tc.isEntryEnabled())
            SibTr.exit(tc, "remove", "" + id);
        map.remove(id);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
