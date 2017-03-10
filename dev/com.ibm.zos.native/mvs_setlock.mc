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
#include <string.h>

#include "include/mvs_setlock.h"
#include "include/mvs_utils.h"

int getLocalLock(void* saveArea_p)
{
    int setlock_rc = -1;

    if (saveArea_p != NULL) {

        unsigned char saved_key = switchToKey0();
        __asm(" LGR 2,13 Save dynamic area address\n"
              " LA  13,%1\n"
              " SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " SETLOCK OBTAIN,TYPE=LOCAL,MODE=UNCOND,REGS=STDSAVE\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64\n"
              " LGR 13,2 Restore dynamic area address\n"
              " ST 15,%0 Save return code":
              "=m"(setlock_rc) :
              "m"(*((char*)saveArea_p)) :
              "r0","r1","r2","r14","r15");
        switchToSavedKey(saved_key);
    }

    return setlock_rc;
}

int releaseLocalLock(void* saveArea_p)
{
    int setlock_rc = -1;

    if (saveArea_p != NULL) {

        unsigned char saved_key = switchToKey0();
        __asm(" LGR 2,13 Save dynamic area address\n"
              " LA  13,%1\n"
              " SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " SETLOCK RELEASE,TYPE=LOCAL,REGS=STDSAVE\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64\n"
              " LGR 13,2 Restore dynamic area address\n"
              " ST 15,%0 Store return code":
              "=m"(setlock_rc) :
              "m"(*((char*)saveArea_p)) :
              "r0","r1","r2","r14","r15");
        switchToSavedKey(saved_key);
    }

    return setlock_rc;
}

#pragma insert_asm(" IHAPSA")
