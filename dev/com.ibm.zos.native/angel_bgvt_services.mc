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
#include "include/angel_bgvt_services.h"

#include "include/mvs_cpool_services.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"

#include <metal.h>
#include <stdlib.h>

//---------------------------------------------------------------------
// Basic constants
//---------------------------------------------------------------------
#define BGVT_STORAGE_KEY 2
#define BGVT_SUBPOOL     241

//---------------------------------------------------------------------
// Internal function prototypes
//---------------------------------------------------------------------
bgvt* __ptr32 createSystemBGVT(void);

/**
 * Inlined object code to find the BGVT hung off of classic WAS BACBs and
 * delegete to the ESTAE exit for that environment.
 *
 * PSAAOLD->ASCBASSB->ASSBBCBA->bacb_bgvt_ptr->BBODBGVT_BBORLEXT_PTR
 */
const char ESTAE_EXTENSION_GLUE_ROUTINE[] = {
    0x1F, 0xFF,                 // 1FFF        SLR   @15,@15
    0xB2, 0x4E,   0x00, 0xFF,   // B24E 00FF   SAR   @15,@15      clear access register 15
    0x58, 0xF0,   0x02, 0x24,   // 58F0 0024   get ascbptr
    0x58, 0xF0,   0xF1, 0x50,   // 58F0 F150   get assbptr
    0x58, 0xF0,   0xF1, 0xAC,   // 58F0 F1AC   get bacbptr
    0x58, 0xF0,   0xF1, 0x9C,   // 58F0 F19C   get bgvtptr
    0x58, 0xF0,   0xF3, 0x58,   // 58F0 F358   get estae routine
    0x07, 0xFF,                 // 07FF        branch to estae
    0x00, 0x00,   0x00, 0x00,   //             extra room
    0x00, 0x00,   0x00, 0x00,   //             extra room
    0x00, 0x00,   0x00, 0x00,   //             extra room
    0x00, 0x00,   0x00, 0x00,   //             extra room
    0x00, 0x00,   0x00, 0x00,   //             extra room
    0x00, 0x00,   0x00, 0x00,   //             extra room
    0x00, 0x00,   0x00, 0x00    //             extra room
};

/**
 * Locate the system BGVT or create one if necessary.
 */
bgvt* __ptr32 findOrCreateBGVT(void) {
    bgvt* __ptr32 bgvt_p = findBGVT();
    if (bgvt_p == NULL) {
        createSystemBGVT();
        bgvt_p = findBGVT();
    }

    return bgvt_p;
}

/**
 * Create and initialize a BGVT and hang it off the ECVT.
 */
bgvt* __ptr32 createSystemBGVT(void) {

    // Obtain storage for the new BGVT
    bgvt* __ptr32 bgvt_p = (bgvt* __ptr32) storageObtain(sizeof(bgvt), BGVT_SUBPOOL, BGVT_STORAGE_KEY, NULL);
    if (bgvt_p == NULL) {
        return NULL;
    }

    // Clear and initialize BGVT eye catcher and version
    memset(bgvt_p, 0, sizeof(bgvt));
    memcpy(bgvt_p->bbodbgvt_eyecatcher, "BBODBGVT", sizeof(bgvt_p->bbodbgvt_eyecatcher));
    bgvt_p->bbodbgvt_version = 2;

    // Setup the ESTAE glue routine for classic WAS
    bgvt_p->bbodbgvt_glue_rtn_to_estae = 1;
    bgvt_p->bbodbgvt_estae_extension = (void* __ptr32) storageObtain(sizeof(ESTAE_EXTENSION_GLUE_ROUTINE), BGVT_SUBPOOL, BGVT_STORAGE_KEY, NULL);
    if (bgvt_p->bbodbgvt_estae_extension == NULL) {
        storageRelease(bgvt_p, sizeof(bgvt), BGVT_SUBPOOL, BGVT_STORAGE_KEY);
        return NULL;
    }
    memcpy(bgvt_p->bbodbgvt_estae_extension, ESTAE_EXTENSION_GLUE_ROUTINE, sizeof(ESTAE_EXTENSION_GLUE_ROUTINE));
    bgvt_p->bbodbgvt_srbf_dyna_cpool = mvs_cpool_build(1, 100, 8192, BGVT_SUBPOOL, BGVT_STORAGE_KEY, "SRBF Dynamic Area CPOOL");

    // Setup for the BGVT compare and swap
    bgvt* oldBgvt_p = NULL;
    psa*  psa_p = NULL;
    cvt*  cvt_p = (cvt* __ptr32) psa_p->flccvt;
    ecvt* ecvt_p = (ecvt* __ptr32) cvt_p->cvtecvt;

    // Compare and swap on the new BGVT as key 0
    unsigned char oldKey = switchToKey0();
    __cs1(&oldBgvt_p, &ecvt_p->ecvtbcba, &bgvt_p);
    switchToSavedKey(oldKey);

    // If we lost the CS, cleanup
    if (bgvt_p != findBGVT()) {
        mvs_cpool_delete(bgvt_p->bbodbgvt_srbf_dyna_cpool);
        storageRelease(bgvt_p->bbodbgvt_estae_extension, sizeof(ESTAE_EXTENSION_GLUE_ROUTINE), BGVT_SUBPOOL, BGVT_STORAGE_KEY);
        storageRelease(bgvt_p, sizeof(*bgvt_p), BGVT_SUBPOOL, BGVT_STORAGE_KEY);
        bgvt_p = NULL;
    }

    return bgvt_p;
}
