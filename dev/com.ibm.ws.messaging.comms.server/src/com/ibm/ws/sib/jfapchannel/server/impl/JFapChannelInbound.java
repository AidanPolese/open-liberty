/*
 * @start_prolog@
 * Version: @(#) 1.15 SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/JFapChannelInbound.java, SIB.comms, WASX.SIB, aa1225.01 05/02/04 08:59:57 [7/2/12 05:59:07]
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
 * Creation        030521 prestona Original
 * F167363			 030523 prestona Rebase on LIBD_1891_2255 CF + TCP Channel
 * F177053         030917 prestona Rebase JFAP Channel on pre-M4 CF + TCP
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F184828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * F188491         040128 prestona Migrate to M6 CF + TCP Channel
 * D194678         040317 mattheg  Migrate to M7 CF + TCP Channel
 * D196678.10.1    040525 prestona Insufficient chain data passed to TRM
 * D199145         040812 prestona Fix Javadoc
 * ============================================================================ 
 */

// NOTE: D181601 is not changed flagged as it modifies every line of trace and FFDC.

package com.ibm.ws.sib.jfapchannel.server.impl;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.ChannelFactoryData;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.DiscriminationProcess;
import com.ibm.wsspi.channelfw.Discriminator;
import com.ibm.wsspi.channelfw.InboundChannel;
import com.ibm.wsspi.channelfw.VirtualConnection;
import com.ibm.wsspi.tcpchannel.TCPConnectionContext;

/**
 * Inbound JFAP Channel. Participates as a channel in the channel framework.
 * 
 * @author prestona
 */
public class JFapChannelInbound implements InboundChannel {
    private static final TraceComponent tc = SibTr.register(JFapChannelInbound.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

    static {
        if (tc.isDebugEnabled())
            SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.server.impl/src/com/ibm/ws/sib/jfapchannel/impl/JFapChannelInbound.java, SIB.comms, WASX.SIB, aa1225.01 1.15");
    }

    // The discriminator for this channel.
    private final Discriminator discriminator;

    private final ChannelFactoryData channelFactoryData; // D196678.10.1

    private ChannelData chfwConfig = null;

    /**
     * Creates a new inbound channel.
     * 
     * @param cc
     */
    public JFapChannelInbound(ChannelFactoryData factoryData, ChannelData cc) // F177053, D196678.10.1
    {
        update(cc);
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>", new Object[] { factoryData, cc }); // D196678.10.1
        discriminator = new JFapDiscriminator(this);
        channelFactoryData = factoryData; // D196678.10.1
        chfwConfig = cc;
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "<init>");
    }

    /**
     * Returns the discriminator for this channel.
     */
    public Discriminator getDiscriminator() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getDiscriminator");
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "getDiscriminator", discriminator);
        return discriminator;
    }

    // end F177053

    /**
     * Returns an acceptable device side interface for this channel. This will
     * always be the TCP Channel context.
     */
    // begin F177053
    public Class getDeviceInterface() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getDeviceInterface");
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "getDeviceInterface");
        return TCPConnectionContext.class;
    }

    // end F177053

    /**
     * Receives notification of a channel configuration change.
     */
    public void update(ChannelData cc) // F177053
    {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "update", cc); // F177053
        // TODO: do we respond to this in any way?		
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "update"); // F177053
        this.chfwConfig = cc;
    }

    // begin F177053
    public void start() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "start");
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "start");
    }

    // end F177053

    // begin F177053
    public void stop(long millisec) {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "stop", "" + millisec);
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "stop");
    }

    // end F177053

    // begin F177053
    public void init() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "init");
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "init");
    }

    // end F177053

    // begin F177053
    public void destroy() {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "destroy");
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "destroy");
    }

    // end F177053

    /** {@inheritDoc} */
    @Override
    public DiscriminationProcess getDiscriminationProcess() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getDiscriminatoryType() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setDiscriminationProcess(DiscriminationProcess dp) {
    // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public Class<?> getApplicationInterface() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return this.chfwConfig.getName();
    }

    /** {@inheritDoc} */
    @Override
    public ConnectionLink getConnectionLink(VirtualConnection vc) {

        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getConnectionLink", vc);
        ConnectionLink retValue = new JFapInboundConnLink(vc, channelFactoryData, chfwConfig);
        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "getConnectionLink", retValue);
        return retValue;
    }
    /** {@inheritDoc} */

}
