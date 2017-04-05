// 1.8, 8/31/07
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.cache;

import com.ibm.websphere.cache.DistributedNioMap;


/**
 * Abstract class implementing
 * DistributedMap and DistributedNioMap.
 * 
 * When doing a JNDI lookup on a DistributedMap
 * or a DistributedNioMap, the actual object
 * type returned from the lookup is
 * DistributedObjectCache.  If you absolutly
 * do not know map type to be returned from the
 * JNDI lookup, use the getMapType() to verify
 * the type.  Otherwise, you can directly cast
 * to DistributedMap or DistributedNioMap.
 * 
 * @see DistributedMap
 * @see DistributedNioMap
 * @since v6.0
 * @ibm-api 
 */
public abstract class DistributedObjectCache implements DistributedNioMap, DistributedMap {
    
    /**
     * The underlying map represented by this
     * DistributedObjectCache is of type DistributedMap.
     * 
     * @see DistributedMap
     * @since v6.0
     * @ibm-api 
     */
    public static final int    TYPE_DISTRIBUTED_MAP             = 0x01;
    
    
    /**
     * The underlying map represented by this
     * DistributedObjectCache is of type DistributedLockingMap.
     * 
     * @see DistributedMap
     * @since v6.0
     * @ibm-api 
     * @deprecated 
     * TYPE_DISTRIBUTED_LOCKING_MAP is no longer used.
     */
    public static final int    TYPE_DISTRIBUTED_LOCKING_MAP     = 0x02;


    /**
     * The underlying map represented by this
     * DistributedObjectCache is of type DistributedNioMap.
     * 
     * @see DistributedMap
     * @since v6.0
     * @ibm-api 
     */
    public static final int    TYPE_DISTRIBUTED_NIO_MAP         = 0x03;


    /**
     * Returns the underlying map type for this
     * DistribuedObjectCache.
     * 
     * @return mapType
     *         <br>TYPE_DISTRIBUTED_MAP
     *         <br>TYPE_DISTRIBUTED_NIO_MAP
     * @see DistributedMap
     * @see DistributedNioMap
     * @since v6.0
     * @ibm-api 
     */
    abstract public int getMapType();

}



