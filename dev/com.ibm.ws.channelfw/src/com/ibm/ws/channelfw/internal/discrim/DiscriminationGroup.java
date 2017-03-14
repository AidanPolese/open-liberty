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
//@(#) 1.5 CF/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/internals/DiscriminationGroup.java, WAS.channelfw, CCX.CF 5/10/04 22:24:35 [5/11/05 12:15:35]

package com.ibm.ws.channelfw.internal.discrim;

import java.util.List;

import com.ibm.wsspi.channelfw.DiscriminationProcess;
import com.ibm.wsspi.channelfw.Discriminator;
import com.ibm.wsspi.channelfw.exception.DiscriminationProcessException;

/**
 * An internal extension to the DiscriminationProcess given to users of the
 * channel framework. This object stores all information about connecting
 * channels
 * above.
 */
public interface DiscriminationGroup extends DiscriminationProcess, Comparable<DiscriminationGroup> {
    /**
     * Adds a discriminator to the group
     * 
     * @param d
     *            The discriminator to add
     * @param weight
     *            for discriminator
     * @throws DiscriminationProcessException
     */
    void addDiscriminator(Discriminator d, int weight) throws DiscriminationProcessException;

    /**
     * Removes a discriminator from the group
     * 
     * @param d
     *            The discriminator to remove
     * @throws DiscriminationProcessException
     */
    void removeDiscriminator(Discriminator d) throws DiscriminationProcessException;

    /**
     * Method getDiscriminators.
     * 
     * @return ArrayList
     */
    List<Discriminator> getDiscriminators();

    /**
     * Method getDiscriminationAlgorithm.
     * 
     * @return DiscriminationAlgorithm
     */
    DiscriminationAlgorithm getDiscriminationAlgorithm();

    /**
     * Method setDiscriminationAlgorithm.
     * 
     * @param da
     */
    void setDiscriminationAlgorithm(DiscriminationAlgorithm da);

    /**
     * Method start
     * 
     * Prepare this process to be run. This allows no more changes
     * to the DiscriminationGroup.
     */
    void start();

    /**
     * Method getChannelName
     * 
     * returns the channel name for this process.
     * 
     * @return String
     */
    String getChannelName();

    /**
     * Method getDiscriminatorNodes
     * 
     * gets the node chain for this discriminator. Internal use only.
     * 
     * @return Object
     */
    Object getDiscriminatorNodes();
}
