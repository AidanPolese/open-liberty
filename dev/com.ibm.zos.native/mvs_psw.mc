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
#include <string.h>

#include "include/ieantc.h"
#include "include/mvs_psw.h"

#pragma prolog(extractThePSW, " SUAUTHPR SAVEDYNA=NO")
#pragma epilog(extractThePSW, "SUAUTHEP")

/**
 * Stub to extract the psw from LE C.  Can't call this from metal C.
 * Use regular extractPSW instead.
 *
 * @param psw_p A pointer to a bbgz_psw struct to fill in.
 *
 * @return psw_p
 */
bbgz_psw * extractThePSW(bbgz_psw* psw_p) {
    int psw_int = 0;
    __asm(" EPSW 2,0\n"
          " ST   2,%0" :
          "=m"(psw_int) : : "r2");
    memcpy(psw_p, &psw_int, sizeof(psw_int));
    return psw_p;
}

/**
 * Extracts the first part of the PSW.  For metal C callers. 
 *
 * @param psw A pointer to a bbgz_psw struct to fill in.
 * 
 * @return psw
 */
bbgz_psw*
extractPSW(bbgz_psw* psw) {
    int psw_int = 0;
    __asm(" EPSW 2,0\n"
          " ST   2,%0" :
          "=m"(psw_int) : : "r2");
    memcpy(psw, &psw_int, sizeof(psw_int));
    return psw;
}

/**
 * Extracts the first part of the PSW from the linkage stack.  For metal C callers. 
 *
 * @param psw A pointer to a bbgz_psw struct to fill in.
 * 
 * @return psw
 */
bbgz_psw*
extractPSWFromLinkageStack(bbgz_psw* psw) {
    __asm(" LGHI 15,4\n"
          " ESTA 4,15\n"
          " STCMH 4,B'1111',%0" :
          "=m"(*psw) : : "r4","r5","r15");
    return psw;
}


#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")
#pragma insert_asm(" IEANTASM")
