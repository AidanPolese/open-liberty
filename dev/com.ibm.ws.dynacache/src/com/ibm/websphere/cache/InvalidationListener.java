// 1.3, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.cache;

import com.ibm.websphere.cache.InvalidationEvent;

/**
 * The listener interface for removing cache entry from the cache.
 * @ibm-api 
 */
public interface InvalidationListener extends java.util.EventListener
{
    /**
     * Invoked when the cache is removed from the cache
     * @ibm-api 
     */
    public void fireEvent(InvalidationEvent e);
}
