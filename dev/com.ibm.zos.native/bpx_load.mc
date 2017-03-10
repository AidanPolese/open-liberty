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
#include <metal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "include/bpx_load.h"

#include "include/mvs_contents_supervisor.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"

#include "include/ras_tracing.h"

#include "include/gen/csvlpret.h"
#include "include/gen/bpxycons.h"

#define RAS_MODULE_CONST  RAS_MODULE_BPX_LOAD
#define _LOAD_FROM_HFS_BPX4LDX_CALL             1
#define _LOAD_FROM_HFS_BPX4LDX_RETURN           2
#define _LOAD_FROM_HFS_PRIVATE_BPX4LDX_CALL     5
#define _LOAD_FROM_HFS_PRIVATE_BPX4LDX_RETURN   6
#define _UNLOAD_FROM_HFS_PRIVATE_BPX4DEL_CALL   7
#define _UNLOAD_FROM_HFS_PRIVATE_BPX4DEL_RETURN 8
#define _LOAD_FROM_HFS_PRIVATE_CSVQUERY_CALL    9
#define _LOAD_FROM_HFS_PRIVATE_CSVQUERY_RETURN 10

#pragma linkage(BPX4LDX,OS64_NOSTACK)
extern void BPX4LDX(int filename_length,
                    char* filename,
                    int flags,
                    int libpath_length,
                    char* libpath,
                    void* entry_point,
                    int* rv,
                    int* rc,
                    int* rsn);

#pragma linkage(BPX4DEL,OS64_NOSTACK)
extern void BPX4DEL(void* entry_point,
                    int* rv,
                    int* rc,
                    int* rsn);

#define DIRECTED_LOAD_SUBPOOL 241
#define DIRECTED_LOAD_KEY 0

loadhfs_details* load_from_hfs(char* pathname) {
    directedloadreturnedparms* entry_point = NULL;
    loadhfs_details* return_p = NULL;

    int filel;
    int flags = 0;
    int lpathl = 0;
    char* lpathn = NULL;
    int rc = 0;
    int rsn = 0;
    int rv = 0;
    char plist[64];
    const int LOD_DIRECTED = 0x20000000; // Directed loadhfs

    filel = strlen(pathname);


    flags = flags | LOD_DIRECTED;
    flags = flags + DIRECTED_LOAD_SUBPOOL; /* Subpool */

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_LOAD_FROM_HFS_BPX4LDX_CALL),
                    "bpx_load load_from_hfs call BPX4LDX",
                    TRACE_DATA_RAWDATA(filel, pathname, "Name"),
                    TRACE_DATA_RAWDATA(lpathl, lpathn, "Libpath"),
                    TRACE_DATA_HEX_INT(flags, "Flags (hex)"),
                    TRACE_DATA_END_PARMS);
    }

    BPX4LDX(filel, pathname, flags, lpathl, lpathn, &entry_point, &rv, &rc, &rsn);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_LOAD_FROM_HFS_BPX4LDX_RETURN),
                    "bpx_load load_from_hfs return BPX4LDX",
                    TRACE_DATA_HEX_INT(rv, "RV"),
                    TRACE_DATA_HEX_INT((rv == -1) ? rc : 0, "RC"),
                    TRACE_DATA_HEX_INT((rv == -1) ? rsn : 0, "RSN"),
                    TRACE_DATA_RAWDATA((rv == -1) ? 0 : sizeof(directedloadreturnedparms), entry_point, "Entry point structure"),
                    TRACE_DATA_END_PARMS);
    }

    if (rv == 0) {
        return_p = malloc(sizeof(*return_p));

        if (return_p != NULL) {
            return_p->mod_len = entry_point->directedloadmodulelength;
            return_p->mod_p = entry_point->directedloadmodulestart;
            return_p->entry_p = entry_point->directedloadmoduleentrypt;
            memset(return_p->delete_token, 0, sizeof(return_p->delete_token));
        }
    }

    return return_p;
}

loadhfs_details* load_from_hfs_private(char* pathname) {
    loadhfs_details* return_p = NULL;

    int filel;
    int flags = 0;
    int lpathl = 0;
    char* lpathn = NULL;
    int rc = 0;
    int rsn = 0;
    int rv = 0;
    void* entry_point;
    char plist[64];

    filel = strlen(pathname);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_LOAD_FROM_HFS_PRIVATE_BPX4LDX_CALL),
                    "bpx_load load_from_hfs_private call BPX4LDX",
                    TRACE_DATA_RAWDATA(filel, pathname, "Name"),
                    TRACE_DATA_RAWDATA(lpathl, lpathn, "Libpath"),
                    TRACE_DATA_HEX_INT(flags, "Flags (hex)"),
                    TRACE_DATA_END_PARMS);
    }

    BPX4LDX(filel, pathname, flags, lpathl, lpathn, &entry_point, &rv, &rc, &rsn);

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(_LOAD_FROM_HFS_PRIVATE_BPX4LDX_RETURN),
                    "bpx_load load_from_hfs_private return BPX4LDX",
                    TRACE_DATA_HEX_INT(rv, "RV"),
                    TRACE_DATA_HEX_INT((rv == -1) ? rc : 0, "RC"),
                    TRACE_DATA_HEX_INT((rv == -1) ? rsn : 0, "RSN"),
                    TRACE_DATA_PTR(entry_point, "Entry point"),
                    TRACE_DATA_END_PARMS);
    }

    if (rv == 0) {
        void* module_addr_p = NULL;
        unsigned long long module_len = 0L;
        int csv_rc = 0;

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_LOAD_FROM_HFS_PRIVATE_CSVQUERY_CALL),
                        "bpx_load load_from_hfs_private call CSVQUERY",
                        TRACE_DATA_PTR(entry_point, "Entry Point"),
                        TRACE_DATA_END_PARMS);
        }

        csv_rc = contentsSupervisorQueryFromEntryPoint(entry_point, &module_len, &module_addr_p);

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_LOAD_FROM_HFS_PRIVATE_CSVQUERY_RETURN),
                        "bpx_load load_from_hfs_private return from CSVQUERY",
                        TRACE_DATA_HEX_INT(csv_rc, "Return Code"),
                        TRACE_DATA_LONG(module_len, "Module len"),
                        TRACE_DATA_PTR(module_addr_p, "Module address"),
                        TRACE_DATA_END_PARMS);
        }

        if (csv_rc == 0) {
            return_p = (loadhfs_details*) malloc(sizeof(*return_p));

            if (return_p != NULL) {
                return_p->mod_len = (int) module_len;
                return_p->mod_p = module_addr_p;
                return_p->entry_p = entry_point;
                memset(return_p->delete_token, 0, sizeof(return_p->delete_token));
            } else {
                BPX4DEL(&entry_point, &rv, &rc, &rsn);
            }
        } else {
            BPX4DEL(&entry_point, &rv, &rc, &rsn);
        }
    }
    return return_p;
}

void unload_from_hfs(loadhfs_details* details) {
    loadhfs_details empty_details;
    memset(&empty_details, 0, sizeof(empty_details));

    // Only drive delete if we have a delete token
    if (memcmp(details->delete_token, empty_details.delete_token, sizeof(details->delete_token))) {
        contentsSupervisorDeleteFromDynamicLPA(details->delete_token, NULL);
    }
    if (details->mod_p != NULL) {
        storageRelease(details->mod_p, details->mod_len, DIRECTED_LOAD_SUBPOOL, DIRECTED_LOAD_KEY);
    }
}

void unload_from_hfs_private(void* entrypt_p, int* rc_p, int* rsn_p, int* rv_p) {
    if (entrypt_p != NULL) {
        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_UNLOAD_FROM_HFS_PRIVATE_BPX4DEL_CALL),
                        "bpx_load unload_from_hfs_private call BPX4DEL",
                        TRACE_DATA_PTR(entrypt_p, "Entry point"),
                        TRACE_DATA_END_PARMS);
        }

        BPX4DEL(&entrypt_p, rv_p, rc_p, rsn_p);

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(_UNLOAD_FROM_HFS_PRIVATE_BPX4DEL_RETURN),
                        "bpx_load unload_from_hfs_private return BPX4DEL",
                        TRACE_DATA_HEX_INT(*rv_p, "RV"),
                        TRACE_DATA_HEX_INT((*rv_p == -1) ? *rc_p : 0, "RC"),
                        TRACE_DATA_HEX_INT((*rv_p == -1) ? *rsn_p : 0, "RSN"),
                        TRACE_DATA_END_PARMS);
        }
    }
}
