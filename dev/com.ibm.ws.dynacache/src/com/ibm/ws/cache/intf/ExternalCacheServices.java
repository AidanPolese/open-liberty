// 1.1, 9/5/07
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.intf;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the underlying ExternalCacheServices mechanism which is used by the
 * BatchUpdateDaemon.
 */
public interface ExternalCacheServices {

	/**
	 * This is called by the local BatchUpdateDaemon when it wakes up to process
	 * invalidations and sets.
	 * 
	 * @param invalidateIdEvents
	 *            A HashMap of invalidate by id events.
	 * @param invalidateTemplateEvents
	 *            A HashMap of invalidate by template events.
	 * @param pushECFEvents
	 *            A ArrayList of external cache fragment events.
	 */
	public void batchUpdate(HashMap invalidateIdEvents,	HashMap invalidateTemplateEvents, ArrayList pushECFEvents);

}
