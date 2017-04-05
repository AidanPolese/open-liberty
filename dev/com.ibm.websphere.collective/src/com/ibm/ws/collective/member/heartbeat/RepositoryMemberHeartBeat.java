/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.collective.member.heartbeat;

import java.io.IOException;

/**
 *
 */
public interface RepositoryMemberHeartBeat {

    /**
     * The sendHeartBeat operation sends a heart beat for the specified member
     * to the repository. This operation must be invoked at at least as
     * frequently as the heart beat interval to ensure the member remains
     * considered an active member.
     * 
     * @throws IOException if there was any problem completing the request
     * @throws IllegalArgumentException if the memberId is not valid
     */
    void sendHeartBeat() throws IOException, IllegalArgumentException;

    /**
     * This form of the sendHeartBeat operation allows the member to specify
     * a new heart beat interval. Otherwise, this operation behaves like {@link #sendHeartBeat(String)}.
     * 
     * @param newHeartBeatInterval the new heart beat interval, in seconds,
     *            for this member.
     * @throws IOException if there was any problem completing the request
     * @throws IllegalArgumentException if the memberId or newHeartBeatInterval is not valid
     */
    void sendHeartBeat(int newHeartBeatInterval) throws IOException, IllegalArgumentException;

    /**
     * Register the member to the repository.
     * This method can schedule a new heartbeat timer.
     * 
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public void registerMember() throws IOException, IllegalArgumentException;

    /**
     * Deregister the member with the repository.
     * This method can cancel the current heartbeat timer.
     */
    public void deregisterMember();

}
