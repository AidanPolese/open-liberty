/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdio.h>
#include <string.h>

#include "include/angel_bgvt_services.h"
#include "include/angel_check_main.h"
#include "include/bbgzsgoo.h"

// Return codes from the angel check main.
#define ANGEL_CHECK_ANGEL_FOUND_RC       0
#define ANGEL_CHECK_ANGEL_NOT_FOUND_RC   4

/**
 * The angel check started main.
 *
 * @param angelName The name of the angel we want to check on, or NULL for the default angel
 *
 * @return 0 if the angel is up, non-zero if the angel is down or there was a problem
 */
#pragma prolog(angel_check, main)
#pragma epilog(angel_check, main)
int angel_check(char* angel_name) {

    int angel_check_rc = ANGEL_CHECK_ANGEL_NOT_FOUND_RC;

    for (int i = 0; i < 54; i++) {
        if ('\0' == angel_name[i]) {
            break;
        }
        if (54 == i) {
            angel_name[54] = '\0';
        }
    }

    // -----------------------------------------------------------------
    // Load the BGVT control block.
    // -----------------------------------------------------------------
    bgvt* __ptr32 bgvt_p = findBGVT();

    if (bgvt_p != NULL) {
        // Get the CGOO.
        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p != NULL) {
            if (strlen(angel_name) == 0) {
                // Check for the default angel.
                if (cgoo_p->bbgzcgoo_flags.angel_active) {
                    angel_check_rc = ANGEL_CHECK_ANGEL_FOUND_RC;
                }
            } else {
                // We got a name. Find its respective anchor.
                AngelAnchor_t* angelAnchor_p = NULL;
                AngelAnchorSet_t* angelAnchorSet_p = cgoo_p->firstAaSet_p;
                while (angelAnchorSet_p != NULL) {
                    for (int x = 0; x < angelAnchorSet_p->nextAvailableSlot; x++) {
                        if (strcmp(angel_name, angelAnchorSet_p->namedAngelInfo[x].name) == 0) {
                            angelAnchor_p = &(angelAnchorSet_p->namedAngelInfo[x]);
                            break;
                        }
                    }

                    if (angelAnchor_p != NULL) {
                        break;
                    } else {
                        angelAnchorSet_p = angelAnchorSet_p->next_p;
                    }
                }
                if (NULL != angelAnchor_p) {
                    // We got an anchor. Check the flag.
                    AngelStatusFlags_t flags = angelAnchor_p->flags;
                    if (flags.angel_active) {
                        angel_check_rc = ANGEL_CHECK_ANGEL_FOUND_RC;
                    }
                }
            }
        }
    }

    return angel_check_rc;
}

#pragma insert_asm(" IEANTASM")
