// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.cache;

import com.ibm.websphere.cache.ChangeEvent;

/**
 * Implement this interface to receive the ChangeEvent notifications.
 * @ibm-api 
 */
public interface ChangeListener extends java.util.EventListener
{
    /**
     * This method is invoked when there is a change to a cache	entry.
     * @ibm-api 
     */
    public void cacheEntryChanged(ChangeEvent e);
}

