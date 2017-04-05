// 1.5, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

/**
 * This interface provides the standard way that the InvalidationAuditDaemon
 * gets information from invalidation events and set events. 
 * It is implemented by the InvalidationByIdEvent, InvalidationByTemplateEvent,
 * CacheEntry and ExternalCacheFragment classes.
 */
public interface InvalidationEvent {
   /**
    * This gets the creation timestamp of the event.
    *
    * @return The creation timestamp.
    */
   public long getTimeStamp();
}
