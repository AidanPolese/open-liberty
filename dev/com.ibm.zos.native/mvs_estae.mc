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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_estae.h"
#include "include/mvs_storage.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/gen/ihasdwa.h"

//---------------------------------------------------------------------
// List form of ESTAEX macro
//---------------------------------------------------------------------
__asm(" ESTAEX MF=L" : "DS"(listesta));

void default_estaex_retry_routine(sdwa* __ptr32 sdwa_p, void* user_parms_p);

void
establish_estaex_with_retry(retry_parms* retry_p, int* estaex_rc_p, int* estaex_rsn_p) {
    //---------------------------------------------------------------------
    // Parameter list for ESTAEX needs to be below the bar, even though it
    // can be called in 64 bit mode.
    //---------------------------------------------------------------------
    struct parm31 {
        char buffesta[sizeof(listesta)];
    };

    int estaex_rc = -1;
    int estaex_rsn = -1;

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p == NULL) {
        return;
    }

    memcpy(parm_p->buffesta, &listesta, sizeof(parm_p->buffesta));

    __asm(" ESTAEX (%2),CT,PARAM=(%4),MF=(E,%3)\n"
          " ST 15,%0\n"
          " ST 0,%1" :
          "=m"(estaex_rc),"=m"(estaex_rsn) :
          "r"(default_estaex_retry_routine), "m"(parm_p->buffesta), "r"(retry_p) :
          "r0","r1","r14","r15");

    *estaex_rc_p = estaex_rc;
    *estaex_rsn_p = estaex_rsn;

    free(parm_p);
}

void
remove_estaex(int* estaex_rc_p, int* estaex_rsn_p) {
    int estaex_rc = -1;
    int estaex_rsn = -1;

    __asm(" ESTAEX 0\n"
          " ST 15,%0\n"
          " ST 0,%1" :
          "=m"(estaex_rc),"=m"(estaex_rsn) : :
          "r0","r1","r14","r15");

    *estaex_rc_p = estaex_rc;
    *estaex_rsn_p = estaex_rsn;
}

/**
 * Default ESTAE retry routine for the angel environment.  This will retry to
 * the retry point set up by the code which registered the ESTAE.
 *
 * @param sdwa_p A pointer to the SDWA provided by z/OS.
 * @param user_parms_p The user parms pointer provided by the caller.
 */
#ifdef ANGEL_COMPILE
#pragma prolog(default_estaex_retry_routine," RETRPROL ENVIRON=ANGEL")
#elif SERVER_COMPILE
#pragma prolog(default_estaex_retry_routine," RETRPROL ENVIRON=SERVER")
#else
#warning "Compiling in an unsupported environment"
#endif
#pragma epilog(default_estaex_retry_routine,"RETREPIL")
void
default_estaex_retry_routine(sdwa* __ptr32 sdwa_p, void* user_parms_p) {
    //---------------------------------------------------------------------
    // The trace pointer is not set in the prefix area for this stack;
    // don't bother as we're not in here very long.
    //
    // Also, note that there is no Metal C environment here, so malloc
    // or __malloc31 will not work without creating a new environment
    // via initenv(...) and cleaning it up at the end of this function
    // via termenv().
    //---------------------------------------------------------------------
    retry_parms* retry_p = (retry_parms*)user_parms_p;

    // Copy the retry regs into the SDWA 
    sdwaptrs* sdwa_ext_p = (sdwaptrs*) sdwa_p->sdwaxpad;
    sdwarc4*  sdwa_ext64_p = (sdwarc4*) sdwa_ext_p->sdwaxeme;
    memcpy(&(sdwa_ext64_p->sdwag64), retry_p->regs64, sizeof(retry_p->regs64));

    // Retry
    if (retry_p->setrp_opts.nodump == 1) {
        __asm(" SETRP WKAREA=(%0),DUMP=NO,RETADDR=(%1),RC=4,RETREGS=64,FRESDWA=YES,RETRYAMODE=64,RECORD=YES" : :
              "r"(sdwa_p),"r"(retry_p->retry_addr) :
              "r0","r1","r14","r15");
    } else {
        __asm(" SETRP WKAREA=(%0),DUMP=IGNORE,RETADDR=(%1),RC=4,RETREGS=64,FRESDWA=YES,RETRYAMODE=64,RECORD=YES" : :
              "r"(sdwa_p),"r"(retry_p->retry_addr) :
              "r0","r1","r14","r15");
    }
}

#pragma insert_asm(" IHASDWA")
#pragma insert_asm(" IEANTASM")
#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")

