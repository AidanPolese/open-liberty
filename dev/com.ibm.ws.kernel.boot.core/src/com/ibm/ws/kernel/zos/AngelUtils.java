/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.zos;

import java.util.Set;

/**
 * This service provides Angel related utilities. Such as verifying Service registration for native associated
 * functions.
 * 
 */
public interface AngelUtils {

    /**
     * Check that each service name in the input list is an Server service.
     * 
     * @param services Set of Server services.
     * @return true if all supplied services are Server services. Otherwise, false.
     */
    public boolean areServicesAvailable(Set<String> services);

    /**
     * Return a Set of all native registered services.
     * 
     * @return Set of all available services
     */
    public Set<String> getAvailableServices();
}
