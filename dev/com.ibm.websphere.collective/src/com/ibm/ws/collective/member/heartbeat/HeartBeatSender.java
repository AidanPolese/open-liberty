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

/**
 * Simple service component for managing heart beats, used by collective member
 * servers to indicate liveness to the collective controller.
 * 
 * Note: This interface is NOT API or SPI. We put it in
 * com.ibm.websphere.collective because we needed a common place for it, that is
 * accessible to both com.ibm.ws.collective.repository and
 * com.ibm.ws.collective.repository.client. This is because the service is used
 * by both LocalRepositoryConnection and RemoteRepositoryConnection, which are
 * in those two projects, respectively.
 * 
 * The implementation, HeartBeatSenderImpl, is in yet a third project
 * (com.ibm.ws.collective.member). That is the logical place for it, since heart
 * beating is a member function.
 * 
 */
public interface HeartBeatSender {

    /**
     * Cancel the scheduled task.
     * 
     * @return The result of canceling the scheduled task (via
     *         ScheduledFuture.cancel(true)); or false if there's no outstanding
     *         task for the member.
     */
    public boolean cancelHeartBeat(RepositoryMemberHeartBeat repositoryMember);

    /**
     * Schedule a recurring task for heart beating.
     * 
     * If the repositoryMember had a previously scheduled heart beat task, it is
     * canceled.
     * 
     * @param repositoryMember
     *            - the RepositoryMember. sendHeartBeat() is called against this
     *            object.
     * @param heartBeatInterval
     *            - the heart beat interval in seconds.
     */
    public void startHeartBeat(RepositoryMemberHeartBeat repositoryMember,
                               int heartBeatInterval);

}
