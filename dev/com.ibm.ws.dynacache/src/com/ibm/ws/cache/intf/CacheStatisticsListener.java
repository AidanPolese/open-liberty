// 1.2, 11/6/07
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.intf;

/**
 * This is the MOTHER interface for all cache statistics to get, reset and increment the statistics counters. 
 * The classes implements this interface are:
 * <ol>
 * <li>CacheStatisticsListenerImpl for dynamic cache 
 * <li>CacheStatisticsListenerWrapper to support for any cache provider. 
 * </ol>
 */
public interface CacheStatisticsListener {

	public long getCacheHitsCount();

	public long getCacheLruRemovesCount();

	public long getCacheMissesCount();

	public long getCacheRemovesCount();

	public long getDepIdBasedInvalidationsFromDiskCount();

	public long getDepIdsOffloadedToDiskCount();

	public long getDiskCacheHitsCount();

	public long getExplicitInvalidationsFromDiskCount();

	public long getExplicitInvalidationsFromMemoryCount();

	public long getExplicitInvalidationsLocalCount();

	public long getExplicitInvalidationsRemoteCount();

	public long getGarbageCollectorInvalidationsFromDiskCount();

	public long getMemoryCacheHitsCount();

	public long getObjectsAsyncLruToDiskCount();

	public long getObjectsDeleteFromDiskCount();

	public long getObjectsDeleteFromDisk4000KCount();

	public long getObjectsDeleteFromDisk400KCount();

	public long getObjectsDeleteFromDisk40KCount();

	public long getObjectsDeleteFromDisk4KCount();

	public long getObjectsDeleteFromDiskSizeCount();

	public long getObjectsReadFromDiskCount();

	public long getObjectsReadFromDisk4000KCount();

	public long getObjectsReadFromDisk400KCount();

	public long getObjectsReadFromDisk40KCount();

	public long getObjectsReadFromDisk4KCount();

	public long getObjectsReadFromDiskSizeCount();

	public long getObjectsWriteToDiskCount();

	public long getObjectsWriteToDisk4000KCount();

	public long getObjectsWriteToDisk400KCount();

	public long getObjectsWriteToDisk40KCount();

	public long getObjectsWriteToDisk4KCount();

	public long getObjectsWriteToDiskSizeCount();

	public long getOverflowEntriesFromMemoryCount();

	public long getOverflowInvalidationsFromDiskCount();

	public long getRemoteInvalidationNotificationsCount();

	public long getRemoteObjectFetchSizeCount();

	public long getRemoteObjectHitsCount();

	public long getRemoteObjectMissesCount();

	public long getRemoteObjectUpdatesCount();

	public long getRemoteObjectUpdateSizeCount();

	public long getRemoteUpdateNotificationsCount();

	public long getTemplateBasedInvalidationsFromDiskCount();

	public long getTemplatesOffloadedToDiskCount();

	public long getTimeoutInvalidationsFromDiskCount();

	public long getTimeoutInvalidationsFromMemoryCount();

    public void reset();

    public void resetMemory();

    public void resetDisk();

    public void deleteEntryFromDisk(Object id, int valueSize);

    public void depIdBasedInvalidationsFromDisk(Object id);

    public void depIdsOffloadedToDisk(Object id);

    public void localCacheHit(Object id, int locality);

    public void cacheMiss(Object id);

    public void objectsAsyncLruToDisk();

    public void overflowEntriesFromMemory();  //PK33017  

    public void readEntryFromDisk(Object id, int valueSize);

    public void remoteInvalidationNotifications(Object id);

    public void remoteObjectHits(Object id, int valueSize);

    public void remoteObjectMisses(Object id);

    public void remoteObjectUpdates(Object id, int valueSize);

    public void remoteUpdateNotifications(int numNotifications);

    public void remoteUpdateNotifications(Object id);

    public void remove(Object id, int cause, int locality, int source);

    public void start();

    public void templateBasedInvalidationsFromDisk(Object id);

    public void templatesOffloadedToDisk(Object id);

    public void writeEntryToDisk(Object id, int valueSize);
}