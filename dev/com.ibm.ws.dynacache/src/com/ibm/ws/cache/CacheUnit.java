// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import java.util.ArrayList;
import java.util.HashMap;

import com.ibm.websphere.cache.exception.DynamicCacheServiceNotStarted;
import com.ibm.ws.cache.intf.CommandCache;
import com.ibm.ws.cache.intf.ExternalInvalidation;
import com.ibm.ws.cache.intf.JSPCache;
import com.ibm.ws.cache.intf.ObjectCacheUnit;
import com.ibm.ws.cache.intf.ServletCacheUnit;
import com.ibm.wsspi.cache.EventSource;

public interface CacheUnit {
	
	public void initialize(CacheConfig cc);
	public void batchUpdate(String cacheId,HashMap invalidateIdEvents, HashMap invalidateTemplateEvents, ArrayList pushEntryEvents);
	public CacheEntry getEntry(String cacheName, Object id, boolean ignoreCounting); 
	public void setEntry(String cacheName, CacheEntry cacheEntry);
	public void setExternalCacheFragment(ExternalInvalidation externalCacheFragment);
	public void addExternalCacheAdapter(String groupId, String address, String beanName) throws DynamicCacheServiceNotStarted;
	public void removeExternalCacheAdapter(String groupId, String address) throws DynamicCacheServiceNotStarted;
	public void addAlias(String cacheName, Object id, Object[] aliasArray);
	public void removeAlias(String cacheName, Object alias);
    public BatchUpdateDaemon getBatchUpdateDaemon();
    public TimeLimitDaemon getTimeLimitDaemon();
    public CommandCache getCommandCache(String cacheName) throws DynamicCacheServiceNotStarted;
    public JSPCache getJSPCache(String cacheName) throws DynamicCacheServiceNotStarted;
    public Object createObjectCache(String cacheName) throws DynamicCacheServiceNotStarted;
    public void setServletCacheUnit(ServletCacheUnit servletCacheUnit);
    public ServletCacheUnit getServletCacheUnit();
    public void setObjectCacheUnit(ObjectCacheUnit objectCacheUnit);
    public EventSource createEventSource(boolean createAsyncEventSource, String cacheName) throws DynamicCacheServiceNotStarted ;
	public void startServices(boolean startTLD);
	public String getUniqueServerNameFQ();
	public RemoteServices getRemoteService();
	public InvalidationAuditDaemon getInvalidationAuditDaemon();
	
}
