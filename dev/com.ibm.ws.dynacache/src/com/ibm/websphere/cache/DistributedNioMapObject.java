// 1.5, 10/12/06
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.cache;

/**
 * Implement this interface in your cacheable
 * object before placing it into cache if you
 * need to get control at the time the object
 * is being removed from cache.
 * 
 * @see DistributedNioMap
 * @ibm-api 
 */
public interface DistributedNioMapObject {
    /**
     * Release the cached object(ByteBuffers/MetaData) to the NIO buffer management.
     * 
     * @see DistributedNioMap
     * @ibm-api 
     */
	public void release();

	/**
	 * toString() method used to display.
     * @ibm-api 
	 */
	public String toString();
	
	/**
	 * This determines the best-effort size of the DistributedNioMapObject's value.

	 * @return The best-effort determination of the size of the DistributedNioMapObject's value. 
	 *  If the size cannot be determined, the return value is -1;
	 * @ibm-api
	 */
	public long getCacheValueSize();
}

