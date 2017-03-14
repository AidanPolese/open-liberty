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
//@(#) 1.5 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/internals/DiscriminationAlgorithm.java, WAS.channelfw, CCX.CF 5/10/04 22:23:54 [5/11/05 12:15:34]

package com.ibm.ws.channelfw.internal.discrim;

import com.ibm.ws.channelfw.internal.InboundVirtualConnection;
import com.ibm.wsspi.channelfw.ConnectionLink;
import com.ibm.wsspi.channelfw.exception.DiscriminationProcessException;

/**
 * An algorithm to choose a channel on the application side given some data.
 */
public interface DiscriminationAlgorithm {

    /**
     * Discriminate is called when a channel is ready to hand off a connection to
     * another
     * channel above it in the chain. This involves handing context specific
     * discrimination
     * data to the various channels that may exist above and have each of them
     * respond
     * after reviewing that data. The various instance types of the algorithm
     * handle the
     * logic for passing data to those channels.
     * 
     * @param vcx
     * @param discrimData
     * @param cl
     * @return int
     * @throws DiscriminationProcessException
     */
    int discriminate(InboundVirtualConnection vcx, Object discrimData, ConnectionLink cl) throws DiscriminationProcessException;

}
