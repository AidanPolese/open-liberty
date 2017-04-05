//1.1, 9/5/07
//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.cache.intf;

import java.util.Enumeration;
import com.ibm.ws.cache.InvalidationEvent;

/**
 * This is the underlying ExternalCacheFragment mechanism which is used by the
 * BatchUpdateDaemon, InvalidationAuditDaemon and ExternalCacheService.
 */
public interface ExternalInvalidation extends InvalidationEvent {

	/**
	 * Returns the emumeration of invalidation IDs.
	 *
	 * @return the Emumeration of invalidation IDs.
	 */
	public Enumeration getInvalidationIds();
	
	/**
	 * Returns the enumeration of URIs.
	 *
	 * @return the Enumeration of URIs.
	 */
	public Enumeration getTemplates();
	
	/**
	 * Returns the URI.
	 *
	 * @return The URI.
	 */
	public String getUri();
	
}
