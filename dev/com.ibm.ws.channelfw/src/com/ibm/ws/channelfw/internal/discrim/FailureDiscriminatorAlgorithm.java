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
//@(#) 1.4 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/internals/impl/FailureDiscriminatorAlgorithm.java, WAS.channelfw, CCX.CF 5/10/04 22:24:29 [5/11/05 12:15:37]

package com.ibm.ws.channelfw.internal.discrim;

import com.ibm.ws.channelfw.internal.InboundVirtualConnection;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.DiscriminationProcess;

/**
 * 
 * This is the Failure algorithm. Fail all the time
 */
public class FailureDiscriminatorAlgorithm implements DiscriminationAlgorithm {

    /**
     * Constructor
     */
    FailureDiscriminatorAlgorithm() {
        // Nothing needed here at this time.
    }

    /**
     * @see com.ibm.ws.channelfw.internal.discrim.DiscriminationAlgorithm#discriminate(InboundVirtualConnection,Object, ConnectionLink)
     */
    public int discriminate(InboundVirtualConnection vc, Object discrimData, ConnectionLink prevChannelLink) {
        return DiscriminationProcess.FAILURE;
    }

}
