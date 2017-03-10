/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Routines used by the WOLA code for adding/removing/finding a
 * BBOARGE to/from/in the chain inside the BBOASHR.
 *
 */

#include <metal.h>

#include "include/common_defines.h"
#include "include/mvs_plo.h"
#include "include/ras_tracing.h"
#include "include/server_wola_shared_memory_anchor.h"
#include "include/server_wola_registration.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_REGISTRATION

/**
 * Adds an RGE to the RGE chain hung off of the BBOASHR (server_wola_shared_memory_anchor).
 *
 * TODO: This is implemented the same way that it was in tWAS.  The registration is added
 *       the list regardless of whether the list has changed between the time the registration
 *       was created and the time it is added.  When the list is modified, there is no check
 *       to see if this registration is still unique.
 *
 * @param bboashr_p Pointer to the BBOASHR
 * @param bboarge_p Pointer to the registration which should be added to the chain.
 */
void addBboargeToChain( struct wolaSharedMemoryAnchor * bboashr_p, WolaRegistration_t * bboarge_p ) {

    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloStoreAreaDoubleWord_t storeArea1, storeArea2;
    void* lockWord_p = &(bboashr_p->rgeChainCounter);
    int ploRc = -1;

    swapArea.compare_p = &(bboashr_p->rgeChainCounter);
    swapArea.expectedValue = bboashr_p->rgeChainCounter;

    while (ploRc != 0) {
        swapArea.replaceValue = swapArea.expectedValue + 1;

        // First area to modify is always the head of the list.
        storeArea1.storeLocation_p = &(bboashr_p->firstRge_p);
        storeArea1.storeValue = (unsigned long long) bboarge_p;

        // Second area could be the previous pointer of the RGE
        // that is currently at the head of the list, if one exists.
        WolaRegistration_t* head_p = bboashr_p->firstRge_p;
        bboarge_p->nextRegistration_p = head_p;
        bboarge_p->previousRegistration_p = NULL;

        if (head_p == NULL) {
            ploRc = ploCompareAndSwapAndStoreDoubleWord(lockWord_p, &swapArea, &storeArea1);
        } else {
            storeArea2.storeLocation_p = &(head_p->previousRegistration_p);
            storeArea2.storeValue = (unsigned long long) bboarge_p;

            ploRc = ploCompareAndSwapAndDoubleStoreDoubleWord(lockWord_p, &swapArea, &storeArea1, &storeArea2);
        }
    }
}

/**
 * Removes an RGE from the RGE chain hung off of the BBOASHR (server_wola_shared_memory_anchor).
 *
 * TODO: This is implemented the same way that it was in tWAS.  The registration pointer is passed
 *       to this method, and it's removed.  There is no check to make sure that the registration
 *       being removed is the correct one.  The caller would need to pass in the rgeChainCounter at
 *       the time they found the registration that they wanted to remove in order to be sure that
 *       the correct registration is removed.
 *
 * @param bboashr_p Pointer to the BBOASHR
 * @param bboarge_p Pointer to the registration which should be removed from the chain.
 *
 * @return zero if the BBOARGE was successfully removed; non-zero otherwise.
 */
int removeBboargeFromChain( struct wolaSharedMemoryAnchor * bboashr_p, WolaRegistration_t * bboarge_p ) {

    PloCompareAndSwapAreaDoubleWord_t swapArea;
    PloStoreAreaDoubleWord_t storeArea1, storeArea2;
    void* lockWord_p = &(bboashr_p->rgeChainCounter);

    swapArea.compare_p = &(bboashr_p->rgeChainCounter);
    swapArea.expectedValue = bboashr_p->rgeChainCounter;

    unsigned char foundRGE = TRUE, removedRGE = FALSE;
    while ((foundRGE == TRUE) && (removedRGE == FALSE)) {
        foundRGE = FALSE;

        // See if we can find the registration we're looking for.
        WolaRegistration_t *prevRGE_p = NULL, *curRGE_p = bboashr_p->firstRge_p;
        while ((curRGE_p != bboarge_p) && (curRGE_p != NULL)) {
            prevRGE_p = curRGE_p;
            curRGE_p = curRGE_p->nextRegistration_p;
        }

        // Try to remove the RGE if we found it.
        if (curRGE_p != NULL) {
            foundRGE = TRUE;
            swapArea.replaceValue = swapArea.expectedValue + 1;

            // First area to modify is the next pointer of the previous
            // RGE, or the head of the list if there was no previous RGE.
            if (prevRGE_p == NULL) {
                storeArea1.storeLocation_p = &(bboashr_p->firstRge_p);
            } else {
                storeArea1.storeLocation_p = &(prevRGE_p->nextRegistration_p);
            }
            storeArea1.storeValue = (unsigned long long) curRGE_p->nextRegistration_p;

            // Second area to modify is the previous pointer of the next
            // RGE, if one exists.
            WolaRegistration_t* nextRGE_p = curRGE_p->nextRegistration_p;
            int ploRc = -1;
            if (nextRGE_p == NULL) {
                ploRc = ploCompareAndSwapAndStoreDoubleWord(lockWord_p, &swapArea, &storeArea1);
            } else {
                storeArea2.storeLocation_p = &(nextRGE_p->previousRegistration_p);
                storeArea2.storeValue = (unsigned long long) curRGE_p->previousRegistration_p;

                ploRc = ploCompareAndSwapAndDoubleStoreDoubleWord(lockWord_p, &swapArea, &storeArea1, &storeArea2);
            }

            removedRGE = (ploRc == 0);
        }
    }

    return (removedRGE == TRUE ? 0 : 1);
}

/**
 * @param bboarge_p - The BBOARGE to test
 * @param wola_name2 - The 2nd part of the WOLA 3-part identity 
 * @param wola_name3 - The 3rd part of the WOLA 3-part identity
 *
 * @return 1 if the given bboarge_p is for the given wola identity names.
 */
static int isMatch(WolaRegistration_t * bboarge_p, char * wola_name2, char * wola_name3) {

    return ! ( memcmp( bboarge_p->serverNameSecondPart, wola_name2, sizeof(bboarge_p->serverNameSecondPart) )
               || memcmp( bboarge_p->serverNameThirdPart, wola_name3, sizeof(bboarge_p->serverNameThirdPart) ) ) ;
}

/**
 *
 * @param chain_p - The head of the BBOARGE chain
 * @param wola_name2 - The 2nd WOLA name
 * @param wola_name3 - The 3rd WOLA name
 *
 * @return the bboarge in the chain for the given wola_name2/wola_name3
 */
WolaRegistration_t * findServerBboargeInChain( WolaRegistration_t * chain_p, char * wola_name2, char * wola_name3) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(81),
                    "findServerBboargeInChain",
                    TRACE_DATA_STRING(wola_name2, "wola name 2"),
                    TRACE_DATA_STRING(wola_name3, "wola name 3"),
                    TRACE_DATA_RAWDATA(((chain_p == NULL) ? 0 : sizeof(WolaRegistration_t)), chain_p, "current bboarge"),
                    TRACE_DATA_END_PARMS);
    }

    if (chain_p == NULL) {
        return NULL;
    } else if ( chain_p->flags.serverRegistration && isMatch( chain_p, wola_name2, wola_name3) ) {  
        return chain_p;
    } else {
        return findServerBboargeInChain(chain_p->nextRegistration_p, wola_name2, wola_name3);
    }
}

