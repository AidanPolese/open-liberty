//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
//@(#) 1.6 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/internals/impl/SingleDiscriminatorAlgorithm.java, WAS.channelfw, CCX.CF 5/10/04 22:24:33 [3/2/05 08:02:24]

package com.ibm.ws.channelfw.internal.discrim;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.channelfw.internal.ChannelFrameworkConstants;
import com.ibm.ws.channelfw.internal.InboundVirtualConnection;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.DiscriminationProcess;

/**
 * This is the single discriminator algorithm that will handle the required
 * steps for discrimination but always assumes success of that call.
 */
public class SingleDiscriminatorAlgorithm implements DiscriminationAlgorithm {
    /**
     * TraceComponent
     */
    private static final TraceComponent tc = Tr.register(SingleDiscriminatorAlgorithm.class, ChannelFrameworkConstants.BASE_TRACE_NAME, ChannelFrameworkConstants.BASE_BUNDLE);
    /**
     * The discrimination group this algorithm is associated with
     */
    private DiscriminationGroup discriminationGroup = null;
    /**
     * Channel above
     */
    private Channel nextChannel = null;

    /**
     * Constructor.
     * 
     * @param discGroup
     *            Set of discriminators
     */
    SingleDiscriminatorAlgorithm(DiscriminationGroup discGroup) {
        // CONN_RUNTIME: channels with one discriminator attached, this links the
        // channels.
        this.discriminationGroup = discGroup;
        // get the single discriminator and get its Channel
        this.nextChannel = discriminationGroup.getDiscriminators().get(0).getChannel();
    }

    /**
     * @see com.ibm.ws.channelfw.internal.discrim.DiscriminationAlgorithm#discriminate(InboundVirtualConnection, Object, ConnectionLink)
     */
    public int discriminate(InboundVirtualConnection vc, Object discrimData, ConnectionLink prevChannelLink) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "discriminate: " + vc);
        }
        ConnectionLink nextChannelLink = nextChannel.getConnectionLink(vc);
        prevChannelLink.setApplicationCallback(nextChannelLink);
        nextChannelLink.setDeviceLink(prevChannelLink);
        return DiscriminationProcess.SUCCESS;
    }

}
