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
 * A very basic implementation of a cache of strings.  The cache performs
 * best when dealing with a small number of entries (in the 10s of entries).
 *
 * The cache is backed by an array of char* pointers. The strings are cached 
 * alphabetically to improve search performance (O(n/2)).  Insert performance 
 * is at worst O(n) (linear), as entries need to be shifted around to maintain 
 * alphabetical order. 
 *
 * NOTE: The SortedCache is NOT THREAD SAFE. Concurrency must be managed externally.
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/ras_tracing.h"
#include "include/util_sortedcache.h"

#define RAS_MODULE_CONST RAS_MODULE_SERVER_UTIL_SORTEDCACHE

/**
 * Expand the cache.  If first expansion, the new capacity is CACHE_INITIAL_CAPACITY.
 * For all subsequent expansions, the cache capacity is doubled.
 *
 * NOTE: The SortedCache is NOT THREAD SAFE. Concurrency must be managed externally.
 *
 * @param cache The cache to expand.
 * 
 * @return 0 if all is well; non-zero otherwise.
 */
int expandCache(SortedCache* cache) {
    char** oldEntryArray = cache->entryArray;
    int oldCapacity = cache->capacity;

    int newCapacity = (oldCapacity == 0) ? CACHE_INITIAL_CAPACITY : oldCapacity * 2;  // Double the length of the array.
    char** newEntryArray = malloc(sizeof(char*) * newCapacity);
    if (newEntryArray == NULL) {
        return CACHE_NO_STORAGE;    
    }
    memset(newEntryArray, 0, sizeof(char*) * newCapacity);

    if (oldEntryArray != NULL) {
        // oldEntryArray will be NULL for the initial expansion.
        memcpy(newEntryArray, oldEntryArray, sizeof(char*) * oldCapacity);
        free(oldEntryArray);
    }
    cache->capacity = newCapacity;
    cache->entryArray = newEntryArray;

    if (TraceActive(trc_level_detailed)) {
        char** tmpArray[32];    // Trace a portion of the entryArray.
        memcpy(tmpArray, cache->entryArray, sizeof(char*) * 32);
        int numEntriesToTrace = (cache->capacity < 32) ? cache->capacity : 32;
        TraceRecord(trc_level_detailed,
                    TP(1), 
                    "Expanded cache",
                    TRACE_DATA_PTR(cache, "cache ptr"),
                    TRACE_DATA_PTR(oldEntryArray, "old entry array"),
                    TRACE_DATA_PTR(cache->entryArray, "new entry array"),
                    TRACE_DATA_INT(cache->capacity, "new entry array length"),
                    TRACE_DATA_RAWDATA(sizeof(char*) * numEntriesToTrace, tmpArray, "cache->entryArray (temp copy)"),
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

/**
 * Insert new entry into the cache at the given index.
 * All entries subsequent to the index are shifted down in the array.
 *
 * NOTE: The SortedCache is NOT THREAD SAFE. Concurrency must be managed externally.
 *
 * @param cache     The cache.
 * @param str       The value to insert.
 * @param i         The index to insert at.
 *
 * @return 0 if all is well; non-zero otherwise.
 */
int insertAt(SortedCache* cache, const char* str, int i) {
    char* newEntry = malloc(strlen(str) + 1);
    if (newEntry == NULL) {
        return CACHE_NO_STORAGE;   // No storage available.
    }
    strcpy(newEntry, str);

    // Shift all entries after index i down 1 slot.
    for (int j = cache->numEntries; j > i; --j) {
        cache->entryArray[j] = cache->entryArray[j-1];
    }
    cache->entryArray[i] = newEntry;    
    cache->numEntries++;

    if (TraceActive(trc_level_detailed)) {
        char** tmpArray[32];    // Trace a portion of the entryArray.
        memcpy(tmpArray, cache->entryArray, sizeof(char*) * 32);
        int numEntriesToTrace = (cache->capacity < 32) ? cache->capacity : 32;
        char tmpStr[9] = {0};
        strncpy(tmpStr, str, 8);
        TraceRecord(trc_level_detailed,
                    TP(2), 
                    "Inserted value into cache",
                    TRACE_DATA_STRING(tmpStr, "inserted str (temp copy)"),
                    TRACE_DATA_INT(i, "insertAt index"),
                    TRACE_DATA_PTR(cache, "cache ptr"),
                    TRACE_DATA_INT(cache->numEntries, "cache->numEntries"),
                    TRACE_DATA_INT(cache->capacity, "cache->capacity"),
                    TRACE_DATA_RAWDATA(sizeof(char*) * numEntriesToTrace, tmpArray, "cache->entryArray (temp copy)"),
                    TRACE_DATA_END_PARMS);
    }
    return 0;
}

/**
 * See util_sortedcache.h for function description.
 */
SortedCache* allocateSortedCache(void) {
    SortedCache* cache = (SortedCache*) malloc(sizeof(SortedCache));
    if (cache != NULL) {
        memset(cache, 0, sizeof(SortedCache));
    }
    return cache;
}

/**
 * See util_sortedcache.h for function description.
 */
int freeSortedCache(SortedCache* cache) {
    if (cache == NULL) {
        return CACHE_IS_NULL;
    } 

    // Free each entry.
    for (int i = 0; i < cache->numEntries; ++i) {
        free(cache->entryArray[i]);
    }
    
    free(cache->entryArray);        // Free entry array.
    free(cache);                    // Free SortedCache struct.

    return 0;
}

/**
 * See util_sortedcache.h for function description.
 */
int sortedCacheInsert(SortedCache* cache, const char* str) {
    if (cache == NULL) {
        return CACHE_IS_NULL;
    } 
    
    // Expand cache if necessary.
    if (cache->numEntries == cache->capacity) {
        expandCache(cache);
    }

    // Look for the first entry at which the input str is alphabetically before
    // the entry. Insert the string at that slot, and shift all subsequent entries
    // down 1 slot.
    for (int i = 0; i < cache->capacity; ++i) {
        if (cache->entryArray[i] == NULL) {
            return insertAt(cache, str, i);   
        } else {
            int sc = strcmp(str,cache->entryArray[i]);
            if (sc == 0) {
                return 4;   // Entry already exists.
            } else if (sc < 0) {
                // The input str is alphabetically before the current entry.
                // Insert here (before the current entry), and shift the rest down.
                return insertAt(cache, str, i);
            } else {
                // The input str is alphabetically after the current entry.
                // Try the next entry.
            }
        }
    }
    // huh?  how'd we get here??
    return 8;
}

/**
 * See util_sortedcache.h for function description.
 */
int sortedCacheSearch(SortedCache* cache, const char* str) {
    if (cache == NULL) {
        return CACHE_IS_NULL;
    }

    // Search the cache for either the input str or the first entry that
    // is alphabetically "greater than" the input str.
    int sc = 1; // The strcmp value. Init to positive value to pass initial check in loop.
    for (int i = 0; i < cache->numEntries && sc > 0; ++i) {
        sc = strcmp(str,cache->entryArray[i]);

        if (TraceActive(trc_level_detailed)) {
            char tmpStr[9] = {0};
            char tmpEntry[9] = {0};
            strncpy(tmpStr, str, 8);
            strncpy(tmpEntry, cache->entryArray[i], 8);
            TraceRecord(trc_level_detailed,
                        TP(3), 
                        "Searching cache",
                        TRACE_DATA_STRING(tmpStr, "str we're searching for (temp copy)"),
                        TRACE_DATA_STRING(tmpEntry, "current entry (temp copy)"),
                        TRACE_DATA_INT(i, "current entry index"),
                        TRACE_DATA_INT(sc, "strcmp result"),
                        TRACE_DATA_PTR(cache, "cache ptr"),
                        TRACE_DATA_END_PARMS);
        }
    }

    return sc; // Either sc is 0 (str was found) or not (str was not found).
}

