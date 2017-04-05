// 1.4, 1/9/08
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import java.util.Map;

import com.ibm.wsspi.cache.CacheStatistics;

/**
 * This class is used to build cache statistics which implements CacheStatistics used by cache providers.
 */
public class CacheStatisticsImpl implements CacheStatistics {

	long cacheHits = -1;
	long cacheLruRemoves = -1;
	long cacheMisses = -1;
	long cacheRemoves = -1;
	long explicitInvalidationsFromMemory = -1;
	long memoryCacheEntries = -1;
	float memoryCacheSizeInMB = -1;
	long timeoutInvalidationsFromMemory = -1;
	
	Map <String, Number> extendedStats = null;
	
	public long getCacheHitsCount() {
		return this.cacheHits;
	}
	
	public long getCacheLruRemovesCount() {
		return this.cacheLruRemoves;
	}

	public long getCacheMissesCount() {
		return this.cacheMisses;
	}

	public long getCacheRemovesCount() {
		return this.cacheRemoves;
	}
	
	public long getExplicitInvalidationsFromMemoryCount() {
		return this.explicitInvalidationsFromMemory;
	}

	public Map <String, Number> getExtendedStats() {
		return this.extendedStats;
	}
	
	public long getMemoryCacheEntriesCount() {
		return this.memoryCacheEntries;
	}

	public float getMemoryCacheSizeInMBCount() {
		return this.memoryCacheSizeInMB;
	}
	
	public long getTimeoutInvalidationsFromMemoryCount() {
		return this.timeoutInvalidationsFromMemory;
	}

	public void setCacheHitsCount(long cacheHits) {
		this.cacheHits = cacheHits;
	}
	
	public void setCacheLruRemovesCount(long cacheLruRemoves) {
		this.cacheLruRemoves = cacheLruRemoves;
	}

	public void setCacheMissesCount(long cacheMisses) {
		this.cacheMisses = cacheMisses;
	}
	
	public void setCacheRemovesCount(long cacheRemoves) {
		this.cacheRemoves = cacheRemoves;
	}

	public void setExplicitInvalidationsFromMemoryCount(long explicitInvalidationsFromMemory) {
		this.explicitInvalidationsFromMemory = explicitInvalidationsFromMemory;
	}
	
	public void setExtendedStats(Map <String, Number> extendedStats) {
		this.extendedStats = extendedStats;
	}

	public void setMemoryCacheEntriesCount(long memoryCacheEntries) {
		this.memoryCacheEntries = memoryCacheEntries;
	}
	
	public void setMemoryCacheSizeInMBCount(float memoryCacheSizeInMB) {
		this.memoryCacheSizeInMB = memoryCacheSizeInMB;
	}
	
	public void setTimeoutInvalidationsFromMemoryCount(long timeoutInvalidationsFromMemory) {
		this.timeoutInvalidationsFromMemory = timeoutInvalidationsFromMemory;
	}
	
	public void reset() {
	}
}
