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
 * Functions for verifying the server is properly contained within it's
 * "penalty box" -- i.e the server has been granted the proper authority
 * to perform certain types of authentication and authorization checks
 * against SAF appl-ids, classes, and resource profiles.
 *
 * The penalty box is defined by the following SAF profiles:
 *
 *     BBG.SECPFX.<profile-prefix>:
 * Indicates which SAF APPLs the server is authorized to create credentials
 * for, and which profiles in the EJBROLE CLASS the server is allowed to perform
 * authorization against.
 *
 * For example, BBG.SECPFX.DEV allows the server to create credentials in
 * the DEV appl-id, and allows the server to perform authorization against
 * EJBROLE profiles that begin with "DEV.".
 *
 * BBG.SECPFX.<profile-prefix> profiles are defined in the SERVER class.
 * The userId of the server must have read access to the profiles.
 *
 *     BBG.SECCLASS.<saf-class>
 * Indicates which SAF CLASSes (other than EJBROLE) the server is authorized
 * to perform authorization against.
 *
 * For example, BBG.SECCLASS.SERVER allows the server to perform authorization
 * against profiles in the SERVER CLASS.
 *
 * BBG.SECCLASS.<saf-class> profiles are defined in the SERVER class.
 * The userId of the server must have read access to the profiles.
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "include/gen/ihaasxb.h"
#include "include/ras_tracing.h"
#include "include/security_saf_sandbox.h"
#include "include/security_saf_authorization.h"
#include "include/security_saf_common.h"
#include "include/server_process_data.h"
#include "include/util_sortedcache.h"
#include "include/mvs_latch.h"
#include "include/mvs_utils.h"

#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_SANDBOX

#define SECCLASS_PROFILE_STEM   "BBG.SECCLASS"      //!< Profile name stem for SECCLASS checks.
#define SECPFX_PROFILE_STEM     "BBG.SECPFX"        //!< Profile name stem for SECPFX checks.

#define PBC_LATCH_SET_NAME      "BBG.LATCHSET.PBC"  //!< The name of the latch set for the PBC and its caches.
#define PBC_LATCH_COUNT             3               //!< The number of latches in the PBC latch set.
#define PBC_LATCH_INDEX             0               //!< The latch index for the PBC latch.
#define PBC_SECPFX_LATCH_INDEX      1               //!< The latch index for the SECPFX cache latch.
#define PBC_SECCLASS_LATCH_INDEX    2               //!< The latch index for the SECCLASS cache latch.

/**
 * Penalty-box RC filters. This filters are applied to RCs from checkPenaltyBox,
 * so that the caller can tell which penalty-box check failed.
 *
 * Note: These values are read in Java and must be kept in sync with those listed
 * in SAFServiceResult.java.
 */
#define PENALTY_BOX_APPL_ERROR      0x100           //!< RC filter for APPL failures.
#define PENALTY_BOX_PROFILE_ERROR   0x200           //!< RC filter for EJBROLE PROFILE HLQ failures.
#define PENALTY_BOX_CLASS_ERROR     0x400           //!< RC filter for CLASS failures.

/**
 * The PenaltyBoxCache is hung off the server GOO at penalty_box_cache_p.
 * It contains refs to two caches: one for SECPFX profiles, another for
 * SECCLASS profiles.
 */
typedef struct {
    SortedCache* secpfxCache;           //!< The SECPFX cache.
    SortedCache* secclassCache;         //!< The SECCLASS cache.
    LatchSetToken latchSetToken;        //!< The latch set for the PBC and its caches.
} PenaltyBoxCache;

/**
 * Retrieve the PenaltyBoxCache from the server GOO.
 * If it doesn't exist, create it.
 *
 * @return A ref to the PenaltyBoxCache.
 */
PenaltyBoxCache* getPenaltyBoxCache(void) {
    server_process_data* spd = getServerProcessData();
    if (spd == NULL) {
        return NULL;
    }

    PenaltyBoxCache* pbc = (PenaltyBoxCache*) spd->penalty_box_cache_p;

    if (pbc == NULL) {
        // Create the PenaltyBoxCache. Note: multiple threads may hit this
        // code concurrently, so we must use mutex code (via latches) to ensure
        // that only one PBC is created and anchored into the PBC.

        pbc = malloc(sizeof(PenaltyBoxCache));
        if (pbc == NULL) {
            return NULL;
        }

        // Create a latch set for the PBC.  Note: if another thread has already
        // created the latch set, then this method will return the previously created
        // latch set (a new latch set will NOT be created).
        int rc = createLatchSet(PBC_LATCH_SET_NAME, PBC_LATCH_COUNT, &pbc->latchSetToken);

        // Obtain exclusive access to the PBC.
        LatchToken latchToken;
        obtainLatch(&pbc->latchSetToken, PBC_LATCH_INDEX, ISGLOBT_EXCLUSIVE, &latchToken);

        // We are now in a synchronized code block.  Check if another thread has beaten
        // us to the punch and already created and anchored the PBC.
        if (spd->penalty_box_cache_p != NULL) {
            free(pbc); // The PBC has already been created. Delete the one this thread created.
            pbc = spd->penalty_box_cache_p;
        } else {
            // We're the first to create the PBC. Allocate the SortedCaches and
            // anchor the PBC into the SPD control block.
            pbc->secpfxCache = allocateSortedCache();
            pbc->secclassCache = allocateSortedCache();

            spd->penalty_box_cache_p = (void*) pbc; // Anchor it.
        }

        // Release the latch.
        releaseLatch(&pbc->latchSetToken, &latchToken);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    "Retrieved PenaltyBoxCache",
                    TRACE_DATA_PTR(pbc, "PenaltyBoxCache ptr"),
                    TRACE_DATA_END_PARMS);
    }

    return pbc;
}

/**
 * Obtain a latch for a cache from the PenaltyBoxCache.
 *
 * @param cacheLatchIndex   The latch index for the requested cache.
 * @param accessOption      The obtain access, either exclusive or shared.
 * @param latchToken        Output parm contains latchToken returned by obtainLatch.
 *
 * @return The requested cache.
 */
SortedCache* obtainCache(int cacheLatchIndex, LatchAccessOption accessOption, LatchToken* latchToken) {
    SortedCache* sc = NULL;
    PenaltyBoxCache* pbc = getPenaltyBoxCache();
    if (pbc != NULL) {
        obtainLatch(&pbc->latchSetToken, cacheLatchIndex, accessOption, latchToken);

        if (cacheLatchIndex == PBC_SECPFX_LATCH_INDEX) {
            sc = pbc->secpfxCache;
        } else if (cacheLatchIndex == PBC_SECCLASS_LATCH_INDEX) {
            sc = pbc->secclassCache;
        }
    }

    if (TraceActive(trc_level_detailed)) {
        char* cacheName = (cacheLatchIndex == PBC_SECPFX_LATCH_INDEX) ? "SECPFX" : "SECCLASS";
        TraceRecord(trc_level_detailed,
                    TP(12),
                    "Obtained access to PB cache",
                    TRACE_DATA_STRING(cacheName, "cache name"),
                    TRACE_DATA_PTR(sc, "cache ptr"),
                    TRACE_DATA_INT((int)accessOption, "LatchAccessOption (0=exclusive;1=shared)"),
                    TRACE_DATA_INT(cacheLatchIndex, "catchLatchIndex"),
                    TRACE_DATA_END_PARMS);
    }

    return sc;
}

/**
 * Release a latch from the PBC latch set.
 *
 * @param latchToken    The previously obtained latch.
 */
int releaseCache(LatchToken* latchToken) {
    PenaltyBoxCache* pbc = getPenaltyBoxCache();
    if (pbc != NULL) {
        return releaseLatch(&pbc->latchSetToken, latchToken);
    }
    return -1;
}

/**
 * see function description in security_saf_sandbox.h.
 */
void flushPenaltyBoxCache(FlushPenaltyBoxParms* parms) {
    int returnCode = 0;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(13),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    PenaltyBoxCache* pbc = getPenaltyBoxCache();

    LatchToken latchToken;
    LatchToken pbcLatchToken;

    // Obtain exclusive access to the PBC.
    obtainLatch(&pbc->latchSetToken, PBC_LATCH_INDEX, ISGLOBT_EXCLUSIVE, &pbcLatchToken);

    // We are now in a synchronized code block. Only 1 thread at a time
    // can flush the caches.

    // Obtain exclusive access to the SECPFX cache (to make sure no other threads
    // are reading from it), then swap in a new cache.
    SortedCache* oldSecpfxCache = obtainCache(PBC_SECPFX_LATCH_INDEX, ISGLOBT_EXCLUSIVE, &latchToken);
    pbc->secpfxCache = allocateSortedCache();
    releaseCache(&latchToken);

    // Obtain exclusive access to the SECCLASS cache (to make sure no other threads
    // are reading from it), then swap in a new cache.
    SortedCache* oldSecclassCache = obtainCache(PBC_SECCLASS_LATCH_INDEX, ISGLOBT_EXCLUSIVE, &latchToken);
    pbc->secclassCache = allocateSortedCache();
    releaseCache(&latchToken);

    // Release the PBC latch.
    releaseLatch(&pbc->latchSetToken, &pbcLatchToken);

    // At this point, no other threads should be accessing the old caches.
    freeSortedCache(oldSecpfxCache);
    freeSortedCache(oldSecclassCache);

    // Set the return code
    memcpy_dk(parms->returnCodePtr, &returnCode, sizeof(returnCode), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(14),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Check if the server's userId has READ access to the penalty box profile
 * name in the SERVER class.  The penalty box profile name is constructed
 * from the given stem and profile (e.g "BBG.SECPFX" and "DEV" produces the
 * penalty box profile name "BBG.SECPFX.DEV").
 *
 * @param profileStem   Stem of penalty box profile name (e.g "BBG.SECPFX").
 * @param profile       Appended to stem (e.g "DEV")
 *
 * @return 0 if server's userId has the requisite READ access.
 *         Non-zero otherwise.
 */
int checkPenaltyBoxProfile(const char* profileStem, const char* profile) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(3),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    if (strlen(profileStem) + strlen(profile) + 1 > 64) {
        return 64; // too long for racf_entity_name buffer.
    }

    // Make the RACF entity name that we'll be using.
    char racf_entity_name[64];
    snprintf(racf_entity_name, sizeof(racf_entity_name), "%s.%s", profileStem, profile);

    saf_results results;
    asxb* asxb_p = ((ascb*)(((psa*)0)->psaaold))->ascbasxb;
    int rc = checkAuthorization(&results,
                                1,                          // Suppress messages
                                ASIS,                       // Log option
                                NULL,                       // Requestor
                                asxb_p->asxbsenv,           // ACEE
                                READ,                       // Access level
                                NULL,                       // Application name  // TODO
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    if (rc == 0) {
        rc = results.safReturnCode;
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(4),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_RAWDATA(sizeof(saf_results), &results, "saf_results"),
                    TRACE_DATA_END_PARMS);
    }
    return rc;
}

/**
 * Thread-safe search for an entry in the SECPFX cache.
 *
 * @param pfx The entry to search for.
 *
 * @return 0 if the entry exists in the cache; non-zero otherwise.
 */
int checkSecpfxCache(const char* pfx) {
    LatchToken latchToken;
    SortedCache* cache = obtainCache(PBC_SECPFX_LATCH_INDEX, ISGLOBT_SHARED, &latchToken);
    int rc = sortedCacheSearch(cache, pfx);
    releaseCache(&latchToken);
    return rc;
}

/**
 * Thread-safe update of the SECPFX cache.
 *
 * @param pfx The entry to add.
 *
 * @return The result of the sortedCacheInsert.
 */
int addToSecpfxCache(const char* pfx) {
    LatchToken latchToken;
    SortedCache* cache = obtainCache(PBC_SECPFX_LATCH_INDEX, ISGLOBT_EXCLUSIVE, &latchToken);
    int rc = sortedCacheInsert(cache, pfx);
    releaseCache(&latchToken);
    return rc;
}

/**
 * Thread-safe search for an entry in the SECCLASS cache.
 *
 * @param className The entry to search for.
 *
 * @return 0 if the entry exists in the cache; non-zero otherwise.
 */
int checkSecclasCache(const char* className) {
    LatchToken latchToken;
    SortedCache* cache = obtainCache(PBC_SECCLASS_LATCH_INDEX, ISGLOBT_SHARED, &latchToken);
    int rc = sortedCacheSearch(cache, className);
    releaseCache(&latchToken);
    return rc;
}

/**
 * Thread-safe update of the SECCLASS cache.
 *
 * @param className The entry to add.
 *
 * @return The result of the sortedCacheInsert.
 */
int addToSecclassCache(const char* className) {
    LatchToken latchToken;
    SortedCache* cache = obtainCache(PBC_SECCLASS_LATCH_INDEX, ISGLOBT_EXCLUSIVE, &latchToken);
    int rc = sortedCacheInsert(cache, className);
    releaseCache(&latchToken);
    return rc;
}

/**
 * Check whether the given APPL is within the server's penalty box.
 * This involves checking whether the server's userId has READ access
 * to the BBG.SECPFX.<APPL> profile in the SERVER class.
 *
 * @param appl  The APPL to check.
 *
 * @return 0 if APPL is within the penalty box; non-zero otherwise.
 */
int checkAppl(const char* appl) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(5),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = checkSecpfxCache(appl);    // Check the cache first.
    if (rc != 0) {
        rc = checkPenaltyBoxProfile(SECPFX_PROFILE_STEM, appl);
        if (rc == 0) {
            addToSecpfxCache(appl);     // Cache the successful result.
        }
    }

    // If non-zero rc, OR with APPL_ERROR to distinguish from other RCs.
    rc = (rc == 0) ? rc : rc | PENALTY_BOX_APPL_ERROR;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(6),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Check whether the HLQ of the given resource profile is within the
 * server's penalty box. This involves checking whether the server's
 * userId has READ access to the BBG.SECPFX.<HLQ> profile in the SERVER
 * class.
 *
 * @param resource The resource for which the HLQ is checked.

 * @return 0 if HLQ is within the penalty box; non-zero otherwise.
 */
int checkResourceHLQ(char* resource) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(7),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    // Parse HLQ from resource.  This function will temporarily modify
    // the resource IN-PLACE by substituting a null for the first '.'.
    // The '.' will of course be put back at the end.
    int i = 0;
    for (; i < strlen(resource) && resource[i] != '.'; ++i); // find first '.' (or null).
    char savechar = resource[i];    // save the sub'd char (could be '.' or null).
    resource[i] = 0;                // temporary null-term to parse out the HLQ.

    int rc = checkSecpfxCache(resource);    // Check the cache first.
    if (rc != 0) {
        rc = checkPenaltyBoxProfile(SECPFX_PROFILE_STEM, resource);
        if (rc == 0) {
            addToSecpfxCache(resource);     // Cache the successful result.
        }
    }

    resource[i] = savechar; // replace the savechar (as if we were never here...).

    // If non-zero rc, OR with PROFILE_ERROR to distinguish from other RCs.
    rc = (rc == 0) ? rc : rc | PENALTY_BOX_PROFILE_ERROR;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(8),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Check whether the given CLASS is within the server's penalty box.
 * This involves checking whether the server's userId has READ access
 * to the BBG.SECCLASS.<CLASS> profile in the SERVER class.
 *
 * @param className The CLASS to check.
 *
 * @return 0 if the CLASS is within the penalty box; non-zero otherwise.
 */
int checkClass(const char* className) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = checkSecclasCache(className);  // Check the cache first.
    if (rc != 0) {
        rc = checkPenaltyBoxProfile(SECCLASS_PROFILE_STEM, className);
        if (rc == 0) {
            addToSecclassCache(className);  // Cache the successful result.
        }
    }

    // If non-zero rc, OR with CLASS_ERROR to distinguish from other RCs.
    rc = (rc == 0) ? rc : rc | PENALTY_BOX_CLASS_ERROR;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(10),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Helper function for folding a string to uppercase.
 *
 * @param upperMe The string to fold.
 *
 * @return 0
 */
int toUpper(char* upperMe) {
    if (upperMe != NULL) {
        for (int i=0; upperMe[i] != 0; ++i) {
            upperMe[i] |= 0x40;     // fold to upper case.
        }
    }
    return 0;
}

/**
 * see function description in security_saf_sandbox.h.
 */
int checkPenaltyBox(const char* appl, const char* className, char* resource) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_END_PARMS);
    }

    int rc = 0;

    // Always check APPL (if one is provided).
    if (appl != NULL && strlen(appl) != 0) {
        rc = checkAppl(appl);
    }

    if (rc == 0 && className != NULL && strlen(className) != 0) {
        // Convert className to UPPERCASE for the strcmp with "EJBROLE".
        // SAF class names are not case sensitive.
        char classNameToUpper[SAF_CLASSNAME_LENGTH + 1] = {0};
        strncpy(classNameToUpper, className, SAF_CLASSNAME_LENGTH);
        toUpper(classNameToUpper);

        if (!strncmp(classNameToUpper, EJBROLE_CLASS, SAF_CLASSNAME_LENGTH)) {
            // For EJBROLE, check that the resource profile has an acceptable HLQ (profile-prefix).
            rc = checkResourceHLQ(resource);

        } else {
            // For NON-EJBROLE, check that the server has authority to perform checks against the CLASS.
            // We don't care about the resource HLQ for classes outside of EJBROLE.
            rc = checkClass(className);
        }
    }

    // If non-zero rc, OR with RAS_MODULE_CONST to distinguish from other RCs.
    rc = (rc == 0) ? rc : rc | RAS_MODULE_CONST;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(2),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(rc, "rc"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}


