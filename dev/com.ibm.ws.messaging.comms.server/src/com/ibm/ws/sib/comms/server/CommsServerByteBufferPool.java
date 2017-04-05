/*
 * @start_prolog@
 * Version: @(#) 1.2 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/CommsServerByteBufferPool.java, SIB.comms, WASX.SIB, aa1225.01 06/09/12 04:35:19 [7/2/12 05:59:06]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
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
 * SIB0048b.com.1  060901 mattheg  Allow better client / server code seperation
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.common.CommsByteBufferPool;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * The comms server byte buffer pool.
 * 
 * @author Gareth Matthews
 */
public class CommsServerByteBufferPool extends CommsByteBufferPool {
    /** Trace */
    private static final TraceComponent tc = SibTr.register(CommsServerByteBufferPool.class,
                                                            CommsConstants.MSG_GROUP,
                                                            CommsConstants.MSG_BUNDLE);

    /** The singleton instance of this class */
    private static CommsServerByteBufferPool instance = null;

    /**
     * @return Returns the byte buffer pool.
     */
    public static synchronized CommsServerByteBufferPool getInstance() {
        if (instance == null) {
            instance = new CommsServerByteBufferPool();
        }
        return instance;
    }

    /**
     * Gets a CommsString from the pool. Any CommsString returned
     * will be initially null.
     * 
     * @return CommsString
     */
    @Override
    public synchronized CommsServerByteBuffer allocate() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "allocate");

        CommsServerByteBuffer buff = (CommsServerByteBuffer) super.allocate();

        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "allocate", buff);
        return buff;
    }

    /**
     * Creates a new server buffer.
     * 
     * @return Returns the new buffer.
     */
    @Override
    protected CommsByteBuffer createNew() {
        return new CommsServerByteBuffer(this);
    }

    /**
     * @return Returns the name for the pool.
     */
    @Override
    protected String getPoolName() {
        return "CommsServerByteBufferPool";
    }
}
