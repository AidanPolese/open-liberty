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

#include "include/common_defines.h"
#include "include/mvs_svcdump_services.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"

#define RAS_MODULE_CONST RAS_MODULE_MVS_SVCDUMP_SERVICES

/**
 * Fixed component string used in dump titles.
 */
#define DUMP_TITLE_COMPONENT "COMPON=WEBSPHERE Z/OS, COMPID=5655I3500"

/**
 * Maximum length of a dump title.  This is documented in the @c SDUMPX
 * macro.
 */
#define MAX_SDUMP_TITLE 100

/**
 * Structure filled in by the @c buildSvcDumpTitle routine.
 */
#pragma pack(1)
typedef struct sdump_title {
    char titleLength;                 //<! Title length
    char title[MAX_SDUMP_TITLE + 1];  //<! Title data
} sdump_title;
#pragma pack(reset)

/**
 * Builds a title to be used when issuing a svcdump.
 *
 * @param id pointer to the null terminated id of the requester. Only the first 56 bytes will end up in the dump title.
 * @param dumpTitle pointer to the @c sdump_title structure to be updated.
 */
static void
buildSvcDumpTitle(const char* id, sdump_title* dumpTitle) {
    // Build the title string (up to MAX_SDUMP_TITLE characters)
    // do not think I need the - 1 but it writes 101 bytes if I do not subtract 1 and svcdump fails because the title is too long
    snprintf(dumpTitle->title, sizeof(dumpTitle->title) - 1, "%s, ID=%s", DUMP_TITLE_COMPONENT, id);
    dumpTitle->titleLength = strlen(dumpTitle->title);
}

// TODO: Fix the dump dataset pattern before using this function.  This code was tested but the
//       dump dataset pattern is hard-coded for VICOM.
int takeTDump(const char* id) {
    int returnCode = 0;

    // Below the bar parameter list mapping
    struct sdump_parmlist {
        int rc;
        char tdumpbuf[256];
        sdump_title tdumpTitle;
        sdump_title dumpDatasetPattern;
    };

    // Allocate the below the bar parameter list (requires AMODE 31).
    struct sdump_parmlist* parmlist_p = __malloc31(sizeof(struct sdump_parmlist));
    if (parmlist_p == NULL) {
        return SVCDUMP_MALLOC31_FAILURE;
    }

    // Populate the parameter list structure
    parmlist_p->rc = 0;
    memset(parmlist_p->tdumpbuf, 0, sizeof(parmlist_p->tdumpbuf));
    buildSvcDumpTitle(id, &parmlist_p->tdumpTitle);

    // Build the dataset name pattern.
    // TODO: Before using this you MUST come up with a dataset pattern, don't use this one.
    strcpy((parmlist_p->dumpDatasetPattern).title, "NOONE.DUMP");
    (parmlist_p->dumpDatasetPattern).titleLength = strlen((parmlist_p->dumpDatasetPattern).title);

    // Drive the service
    __asm(" SAM31\n"
        " SYSSTATE AMODE64=NO\n"
        " IEATDUMP SDATA=(ALLNUC,CSA,GRSQ,LPA,LSQA,PSA,RGN,SQA,SUM,SWA,TRT),DSN=%3,HDR=%2,PLISTVER=1,MF=(E,(%1),COMPLETE)\n"
        " ST 15,%0\n"
        " SYSSTATE AMODE64=YES\n"
        " SAM64":
          "=m"(parmlist_p->rc) :
          "r"(parmlist_p->tdumpbuf), "m"(parmlist_p->tdumpTitle.titleLength), "m"(parmlist_p->dumpDatasetPattern.titleLength) :
          "r0","r1","r14","r15");

    // Copy the return code and release the storage
    returnCode = parmlist_p->rc;
    free(parmlist_p);

    return returnCode;
}

// move this inside takeSvcdump instead of here sdumpx gets a return code 4.
// doc says that inside a routine it only allocates storage and does not initialize it
__asm(" SDUMPX SDATA=(ALLNUC,CSA,GRSQ,LPA,LSQA,PSA,RGN,SQA,SUM,SWA,TRT),HDRAD=,MF=L" : "DS"(sdumpx_dynamic));

//---------------------------------------------------------------------
// Issue SDUMPX from authorized code
//---------------------------------------------------------------------
int
takeSvcdump(const char* id) {
    int returnCode = 0;

    // Below the bar parameter list mapping
    struct sdump_parmlist {
        int rc;
        char sdumpbuf[sizeof(sdumpx_dynamic)];
        sdump_title sdumpTitle;
    };

    // Allocate the below the bar parameter list.  The doc indicates all parameters
    // need to be below the bar but that the caller can be in AMODE(64).
    struct sdump_parmlist* parmlist_p = __malloc31(sizeof(struct sdump_parmlist));
    if (parmlist_p == NULL) {
        return SVCDUMP_MALLOC31_FAILURE;
    }

    // Populate the parameter list structure
    parmlist_p->rc = 0;
    memcpy(parmlist_p->sdumpbuf, &sdumpx_dynamic, sizeof(sdumpx_dynamic));
    buildSvcDumpTitle(id, &parmlist_p->sdumpTitle);

    // Drive the service
    __asm(" SDUMPX SDATA=(ALLNUC,CSA,GRSQ,LPA,LSQA,PSA,RGN,SQA,SUM,SWA,TRT),HDRAD=(%2),MF=(E,(%1))\n"
        " ST 15,%0":
          "=m"(parmlist_p->rc) :
          "r"(parmlist_p->sdumpbuf), "r"(&(parmlist_p->sdumpTitle)) :
          "r0","r1","r14","r15");

    // Copy the return code and release the storage
    returnCode = parmlist_p->rc;
    free(parmlist_p);

    return returnCode;
}

//---------------------------------------------------------------------
// Issue SDUMPX from unauthorized caller via PC
//---------------------------------------------------------------------
void
takeSvcDumpAuthorizedPc(SvcDumpParms* parms_p) {

    int localRC = 0;

    char id[sizeof(sdump_title) + 1];

    // check id for null string
    if (parms_p->id == NULL) {
        memset(id, 0, 1);
    } else { // copy input id
        if ((parms_p->idLength + 1) <= sizeof(id)) {
            memcpy_sk(id, parms_p->id, parms_p->idLength + 1, 8);
        } else { // passed too much truncate it.
            memcpy_sk(id, parms_p->id, sizeof(id), 8);
        }
        // ensure there is a null
        id[sizeof(id) - 1] = '\0';
    }

    localRC = takeSvcdump(id);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(localRC, "return code"),
                    TRACE_DATA_END_PARMS);
    }

    memcpy_dk(parms_p->outRC,
              &localRC,
              sizeof(localRC),
              8);

}
