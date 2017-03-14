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
//@(#) 1.2 SERV1/ws/code/channelfw/src/com/ibm/wsspi/channel/InboundChannel.java, WAS.channelfw, WASX.SERV1 5/10/04 22:20:21 [8/28/04 13:41:23]

package com.ibm.wsspi.channelfw;

/**
 * Channel extended interface for Inbound (server side) channels.
 * <p>
 * This interface adds the necessary methods to perform Discrimination on the
 * data to allow for channel sharing on the inbound side.
 * 
 * @see com.ibm.wsspi.channelfw.DiscriminationProcess
 */
public interface InboundChannel extends Channel {
    /**
     * Returns the discriminator which needs to be matched in order to
     * route a request to this channel.
     * 
     * @return Discriminator
     */
    Discriminator getDiscriminator();

    /**
     * Fetch the instance of the DiscriminationProcess currently assigned
     * to this channel chain. This is called by the channel
     * framework to map a discrimination group and algorithm with this
     * channel. It is the responsiblity of the channel implementor
     * to maintain a DiscriminationProcess in the channel implementation
     * which can be get and set by the framework.
     * <p>
     * It also may be used by individual channel implementors to get their
     * discriminationProcess to call.
     * <p>
     * Synchronization is not needed while the most up to date process is not
     * guaranteed.
     * 
     * @return DiscriminationProcess
     */
    DiscriminationProcess getDiscriminationProcess();

    /**
     * The ChannelFramework uses this to set a particular instance of
     * the DiscriminationProcess to a specific channel. This is called by the
     * channel
     * framework to assign the appropriate algorithm for doing
     * discrimination for inbound chains.
     * <p>
     * Synchronization is not needed while the most up to date process is not
     * guaranteed.
     * 
     * @param dp
     *            The DiscriminationProcess that should be used from
     *            the point of this method being called forward.
     */
    void setDiscriminationProcess(DiscriminationProcess dp);

    /**
     * Returns a class representing the type of discriminatory data which
     * all the upstream channels must be able to discriminate on.
     * <p>
     * In some cases, the channel below may use an array of these objects. This
     * should be clear via its documented behavior.
     * 
     * @return Class<?>
     */
    Class<?> getDiscriminatoryType();
}
