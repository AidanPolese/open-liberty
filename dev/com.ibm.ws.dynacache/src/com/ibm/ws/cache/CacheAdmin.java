// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2013
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import com.ibm.ws.cache.intf.DCache;

/**
 * Extend the public CacheAdminMBean to add a method which relies on an internal class.
 * It isn't clear this is actually being used by anyone, but for now we want to
 * both hold onto it and keep it out of the actual MBean interface.
 */
public interface CacheAdmin extends com.ibm.websphere.cache.CacheAdminMBean {

    /**
     * Retrieves the names of the available cache statistics.
     * 
     * @return The names of the available cache statistics.
     */
    public abstract String[] getCacheStatisticNames(DCache cache);

}