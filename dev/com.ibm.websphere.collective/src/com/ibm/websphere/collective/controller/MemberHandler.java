/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.collective.controller;

import java.io.IOException;
import java.util.Map;

/**
 * The MemberHandler interface is used by the CollectiveRepositoryMBean to handle the monitoring requirements of a registered member
 */
public interface MemberHandler {

    /**
     * Validate a member.
     * <p>
     * An invalid member id is null or empty
     * 
     * @param operation the repository operation being invoked
     * @param memberData, check if the memberData tuple provided contains valid member id
     * @param checkExists if {@code true}, check that the memberID exists
     * @throws IOException if there was any problem completing the operation
     **/
    void validateMember(String operation, Map<String, Object> memberData, boolean checkExist) throws IOException;

    /**
     * Validate a member.
     * <p>
     * An invalid member id is null or empty
     * 
     * @param operation the repository operation being invoked
     * @param memberID to validate
     * @param checkExists if {@code true}, check that the memberID exists
     * @throws IOException if there was any problem completing the operation
     **/
    void validateMember(String operation, String memberID, boolean checkExist) throws IOException;

    /**
     * Start monitoring a member when it is registering for the first time or re-registering.
     * Also create or update the necessary nodes in the repository that allow a member to function
     * properly in the collective and be able to be monitored by the Liveness Monitor.
     * 
     * @param heartbeatInterval the member's heart beat interval (seconds)
     * @param memberData from the repository invocation, containing the member identity
     * @throws IOException if there was any problem completing the operation
     **/
    String startMonitoring(int heartbeatInterval, Map<String, Object> memberData) throws IOException;

    /**
     * 
     * Updates flags in the repository to indicate that this member
     * has been unregistered.
     * 
     * Stop monitoring a member when it is unregistered. Also remove the member from
     * any monitoring list.
     * 
     * @param memberID the collective ID for the member
     * @throws IOException if there was any problem completing the operation
     **/
    void stopMonitoring(String memberID) throws IOException;

    /**
     * Get number of monitored members from the collective repository.
     * 
     * @throws IOException if there was any problem completing the operation
     */
    long getMonitorCount() throws IOException;

}
