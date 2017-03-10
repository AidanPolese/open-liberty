/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * A very basic implementation of a cache of strings.
 */

#ifndef UTIL_SORTEDCACHE_H_
#define UTIL_SORTEDCACHE_H_

#define CACHE_NO_STORAGE            8   //!< RC indicates no storage availble for cache operation.
#define CACHE_IS_NULL               16  //!< RC indicates cache object is NULL.
#define CACHE_ENTRY_ALREADY_EXISTS  4   //!< RC indicates the value could not be inserted because it already exists in the cache.
#define CACHE_INITIAL_CAPACITY      8   //!< Capacity used for first expansion.

/**
 * A SortedCache struct.
 */
typedef struct {
    char** entryArray;              //!< The array of string entries in the cache.
    int capacity;                   //!< Number of slots in the entryArray.
    int numEntries;                 //!< Number of entries (used slots) in the entryArray.
} SortedCache;

/**
 * Allocate a SortedCache.  The SortedCache initially has a capacity of 0.
 *
 * @return A newly allocated SortedCache.
 */
SortedCache* allocateSortedCache(void) ;

/**
 * Free a SortedCache. Deletes all entries in the cache, then deletes
 * the SortedCache structure itself.
 *
 * NOTE: The SortedCache is NOT THREAD SAFE. Concurrency must be managed externally.
 */
int freeSortedCache(SortedCache* cache);

/**
 * Insert a new value into the cache.  The cache is automatically expanded,
 * if necessary.
 *
 * NOTE: The SortedCache is NOT THREAD SAFE. Concurrency must be managed externally.
 *
 * @param cache The cache to insert into.
 * @param str   The value to insert.
 *
 * @return 0 if str was succesfully inserted.
 *         A non-zero error code if str could not be inserted.
 */
int sortedCacheInsert(SortedCache* cache, const char* str) ;

/**
 * Search for a value in the cache. 
 *
 * NOTE: The SortedCache is NOT THREAD SAFE. Concurrency must be managed externally.
 *
 * @param cache The cache to search.
 * @param str   The value to search for.
 *
 * @return 0 if str was found.
 *         Non-zero if str was not found.
 */
int sortedCacheSearch(SortedCache* cache, const char* str) ;

#endif // UTIL_SORTEDCACHE_H_
