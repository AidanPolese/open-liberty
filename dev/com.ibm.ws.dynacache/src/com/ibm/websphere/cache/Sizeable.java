// 1.2, 3/16/10
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.cache;

/**
 * 
 * All the objects put into the cache must implement this interface
 * if the application wants to control the size of a cache instance
 * in terms of heapsize.
 * @ibm-spi
 * @ibm-api 
 * 
 */
public interface Sizeable {

    /**
     * Returns an implementation-specific size of the object.
     * 
     * @return estimated size of <code>object</code>
     * @ibm-spi
     * @ibm-api  
     * 
     */
    public long getObjectSize();
}
