/*
 * @start_prolog@
 * Version: @(#) 1.17 SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/ListenerPortImpl.java, SIB.comms, WASX.SIB, aa1225.01 06/10/02 04:33:44 [7/2/12 05:59:07]
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * F173069         030730 prestona Update CF + TCP Channel to M3 CVS head.
 * F177053         030917 prestona Rebase JFAP Channel on pre-M4 CF + TCP
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * F196678.10      040426 prestona JS Client Administration
 * D199145         040812 prestona Fix Javadoc
 * D232185         041007 mattheg  Serviceability improvements
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel.server.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.jfapchannel.AcceptListener;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.server.ListenerPort;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.ChannelFramework;
import com.ibm.wsspi.channelfw.ChannelFrameworkFactory;
import com.ibm.wsspi.channelfw.exception.ChainException;
import com.ibm.wsspi.channelfw.exception.ChannelException;

/**
 * Implementation of the listener port interface. Tracks information about
 * a specific port number we are listening on.
 * 
 * @author prestona
 */
public class ListenerPortImpl implements ListenerPort {
    private static final TraceComponent tc = SibTr.register(ListenerPortImpl.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

    private static final long CHAIN_STOP_TIME = 5000; // F177053

    static {
        if (tc.isDebugEnabled())
            SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/ListenerPortImpl.java, SIB.comms, WASX.SIB, aa1225.01 1.17");
    }

    // Name of inbound chain we are listening on with this port.   
    private String chainInbound = null;

    // The accept listener associated with this port.
    private AcceptListener acceptListener = null;

    // The port number this listener is using.
    private int portNumber = 0;

    /**
     * Create a new listener port
     * 
     * @param chainInbound The inbound chain the listener port is using.
     * @param acceptListener The accept listener the listener port is using.
     * @param portNumber The port number the listener port is using.
     */
    public ListenerPortImpl(String chainInbound,
                              AcceptListener acceptListener,
                              int portNumber) {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>", new Object[] { chainInbound, acceptListener, "" + portNumber });
        this.chainInbound = chainInbound;
        this.acceptListener = acceptListener;
        this.portNumber = portNumber;
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    /**
     * Stops the listener port listening.
     * 
     * @see ListenerPort#close()
     */
    public void close() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "close");

        // begin F177053
        ChannelFramework framework = ChannelFrameworkFactory.getChannelFramework(); // F196678.10
        try {
            framework.stopChain(chainInbound, CHAIN_STOP_TIME);
        } catch (ChainException e) {
            FFDCFilter.processException(e, "com.ibm.ws.sib.jfapchannel.impl.ListenerPortImpl.close",
                                        JFapChannelConstants.LISTENERPORTIMPL_CLOSE_01,
                                        new Object[] { framework, chainInbound }); // D232185
            if (tc.isEventEnabled())
                SibTr.exception(this, tc, e);
        } catch (ChannelException e) {
            FFDCFilter.processException(e, "com.ibm.ws.sib.jfapchannel.impl.ListenerPortImpl.close",
                                        JFapChannelConstants.LISTENERPORTIMPL_CLOSE_02,
                                        new Object[] { framework, chainInbound }); // D232185
            if (tc.isEventEnabled())
                SibTr.exception(this, tc, e);
        }
        // end F177053

        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "close");
    }

    /**
     * Returns the accept listener associated with this listener port.
     * 
     * @see ListenerPort#getAcceptListener()
     */
    public AcceptListener getAcceptListener() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getAcceptListener");
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "getAcceptListener", acceptListener);
        return acceptListener;
    }

    /**
     * Returns the port number associated with this listener port.
     * 
     * @see ListenerPort#getPortNumber()
     */
    public int getPortNumber() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getPortNumber");
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "getPortNumber", "" + portNumber);
        return portNumber;
    }

}
