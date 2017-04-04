// %I, %G
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.wsspi.cache;

/**
 * This abstract class is used to indicate the features that are supported
 * by a cache provider. Functionality such as servlet caching, JSP,
 * webservices, command cache, DistributedMap and DistributedNioMap
 * checks with this class before invoking a particular function.
 *
 * <p>
 * Methods need to be extended by CacheProviders for each of the features listed below.
 * All cache providers other than the default (Dynacache) will
 * return <code>false</code> for ALL the methods in this abstract class. In subsequent releases
 * of WebSphere, CacheProviders may be allowed to support the features listed below.
 *
 * @ibm-spi
 * @since WAS 6.1.0.27
 */
public abstract class CacheFeatureSupport {

	/**
	 * Indicates if the cache alias ID is supported.
	 *
	 * @return true - the cache alias feature is supported.
	 */
	public abstract boolean isAliasSupported();

	/**
	 * Indicates if WebSphere Data Replication Services (DRS) style cache replication is supported.
	 *
	 * @return true - the cache replication feature is support.
	 */
	public abstract boolean isReplicationSupported();

	/**
	 * Indicates if WebSphere disk cache feature is supported.
	 *
	 * @return true - the disk cache feature is support.
	 */
	public abstract boolean isDiskCacheSupported();

}
