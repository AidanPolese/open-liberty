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
 * Defines general MVS utility like functions
 */

#include "include/mvs_utils.h"

#include <stdlib.h>
#include <string.h>

#include "include/angel_task_data.h"
#include "include/common_mc_defines.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/heap_management.h"
#include "include/server_task_data.h"
#include "include/mvs_storage.h"

//---------------------------------------------------------------------
// Put the Metal C environment in R12
//---------------------------------------------------------------------
register void* myenvtkn __asm("r12");

//---------------------------------------------------------------------
// Initialize the Metal C environment
//---------------------------------------------------------------------
#pragma prolog(initenv,"R12PROL")
#pragma epilog(initenv,"R12EPIL")
void*
initenv(struct __csysenv_s* mysysenv_p, long long usertoken, void* heapAnchor_p){
    memset(mysysenv_p, 0x00, sizeof(struct __csysenv_s));
    mysysenv_p->__cseversion = __CSE_VERSION_2;

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    mysysenv_p->__csesubpool = 131;
    mysysenv_p->__cseheap64usertoken = usertoken;
    mysysenv_p->__cseheap31initsize = 256;
    mysysenv_p->__cseheap31incrsize = 256;

    // Associate the 64-bit dynamic area with the jobstep task, so that
    // it doesn't get deleted on us.
    //
    // TODO: We should really be associating it with the IPT TToken, since
    // all our recovery is tied to the IPT. However at the moment we see no
    // easy way to obtain the IPT token from unauthorized code.  */
    getJobstepTToken((TToken*)mysysenv_p->__csettknowner);

    // If we are managing our own heap, tell the runtime library where the heap
    // management methods are.
    if (heapAnchor_p != NULL) {
        mysysenv_p->__cseamode64malloc = obtainHeapStorage;
        mysysenv_p->__cseamode64malloc31 = obtainHeapStorage31;
        mysysenv_p->__cseamode64free = freeHeapStorage;
        mysysenv_p->__cseheapuserdata = heapAnchor_p;
    }

    /* Create a Metal C environment. */
    myenvtkn = (void * ) __cinit(mysysenv_p);

    return myenvtkn;
}

//---------------------------------------------------------------------
// Terminate the Metal C environment
//---------------------------------------------------------------------
#pragma prolog(termenv,"R12PROL")
#pragma epilog(termenv,"R12EPIL")
void termenv(void) {
    // ----------------------------------------------------------------
    // Save a reference to our heap so that we can free it after
    // destroying the metal C environment.  We do this because we
    // don't know if the metal C environment has obtained any storage
    // from our heap that would get freed when the environment is
    // destroyed.
    // ----------------------------------------------------------------
    struct __csysenvtoken_s* cenv_p = (struct __csysenvtoken_s*)myenvtkn;
    void* heapAnchor_p = (cenv_p != NULL) ? (void*)(cenv_p->__csetheapuserdata) : NULL;

    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);

    // ----------------------------------------------------------------
    // Destroy the heap.
    // ----------------------------------------------------------------
    if (heapAnchor_p != NULL) {
        destroyHeap(heapAnchor_p);
    }
}

/* Sleep utility */
void
sleep(int seconds) {

    // STIMER supports 64 bit callers but requires below the bar storage
    int* hundreths_p = __malloc31(sizeof(int));

    if (hundreths_p != NULL) {
        *hundreths_p = seconds * 100;

        __asm(" STIMER WAIT,BINTVL=(%0)" : : "r"(hundreths_p) :
              "r0","r1","r14","r15");

        free(hundreths_p);
    }
}

/* Gets the key and fetch protection of the input storage */
void
getStorageKey(void* storage_p, bbgz_ivsk* output_p) {
    unsigned char key = 0xFF;

    __asm(" IVSK %0,%1" :
          "=r"(key) :
          "r"(storage_p));

    memcpy(output_p, &key, sizeof(bbgz_ivsk));
}

ascb*
getAscbFromStoken(void* stoken_p) {
    ascb* ascb_p = NULL;

    struct parm31 {
        char stoken[8];
        ascb* __ptr32 ascb_p;
        int rc;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));

    if (parm_p != NULL) {
        memcpy(parm_p->stoken, stoken_p, sizeof(parm_p->stoken));

        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " LOCASCB STOKEN=%2\n"
              " ST 1,%0\n"
              " ST 15,%1\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64" :
              "=m"(parm_p->ascb_p),"=m"(parm_p->rc) :
              "m"(parm_p->stoken[0]) :
              "r0","r1","r14","r15");

        if (parm_p->rc == 0) {
            ascb_p = parm_p->ascb_p;
        }

        free(parm_p);
    }

    return ascb_p;
}

/* Switches to supervisor state */
void
switchToSupervisorState(void) {
    __asm(" MODESET MODE=SUP" : : : "r0","r1","r14","r15");
}

/* Switches to problem state */
void
switchToProblemState(void) {
    __asm(" MODESET MODE=PROB" : : : "r0","r1","r14","r15");
}

/* Switches to key 0 */
unsigned char
switchToKey0(void) {
    unsigned char old_key;

    /*-----------------------------------------------------------------*/
    /* We are storing the original key manually before the MODESET     */
    /* instead of letting the MODESET do it, to avoid having to        */
    /* specify another register to use on the MODESET as a work        */
    /* register.                                                       */
    /*-----------------------------------------------------------------*/
    __asm(" IPK\n"
          " STC 2,%0\n"
          " MODESET EXTKEY=ZERO" :
          "=m"(old_key) : :
          "r1","r2","r15");

    return old_key;
}

/* Switches to the saved key */
void
switchToSavedKey(unsigned char key) {
    __asm(" MODESET KEYADDR=%0,WORKREG=11" : :
          "m"(key) : "r0","r1","r11","r14","r15");
}


// List form of TCBTOKEN.
__asm(" TCBTOKEN MF=L" : "DS"(tcbtoken_list));

/**
 * See mvs_utils.h for function description.
 */
int
getJobstepTToken(TToken* ttoken) {
    struct tcbtoken_parms {
        int rc;
        TToken ttoken;
        char tcbtokenDynamic[sizeof(tcbtoken_list)];
    };

    int local_rc = 0;
    bbgz_psw my_psw;
    extractPSW(&my_psw);
    int subpool = (my_psw.key == 8) ? 0 : 229;

    // We are doing a storage obtain here instead of __malloc31 because
    // this method is called prior to obtaining a metal C environment (from initenv).
    struct tcbtoken_parms* parms = storageObtain(sizeof(struct tcbtoken_parms), subpool, my_psw.key, &local_rc);

    // storageObtain OK.
    if (local_rc == 0) {
        __asm(AMODE_31(" TCBTOKEN TYPE=JOBSTEP,TTOKEN=%0,MF=(E,(%2))\n"
                       " ST 15,%1\n")
              : "=m"(parms->ttoken),"=m"(parms->rc)
              : "r"(&parms->tcbtokenDynamic)
              : "r0","r1","r14","r15");

        local_rc = parms->rc;

        if (local_rc == 0) {
            // Copy the ttoken back to the caller via the output parm.
            memcpy(ttoken, &parms->ttoken, sizeof(TToken));
        }

        storageRelease(parms, sizeof(struct tcbtoken_parms), subpool, my_psw.key);
    }

    return local_rc;
}
