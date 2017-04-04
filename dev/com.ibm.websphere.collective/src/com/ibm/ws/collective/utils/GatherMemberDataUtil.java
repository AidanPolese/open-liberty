/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.collective.utils;

import java.util.Map;

/**
 * 
 */
public interface GatherMemberDataUtil {

    /**
     * Operation to gather member data values for the calling server
     */
    public Map<String, Object> getMemberData();

    /**
     * Operation to return if member is a controller from memberData
     * 
     * @param memberData
     * @return boolean if member is a controller
     */
    public boolean isController(Map<String, Object> memberData);
}
