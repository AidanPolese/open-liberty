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

#include "include/angel_server_pc_stub.h"

#include "include/angel_bgvt_services.h"
#include "include/angel_dynamic_replaceable_module.h"
#include "include/mvs_estae.h"

#define MAX_SLOTS = 31

/*-------------------------------------------------------------------*/
/* Calls PC that binds a Lexington server to the angel.              */
/*-------------------------------------------------------------------*/
int angel_register_pc_client_stub(char* server_authorized_function_module_name, AngelAnchor_t* angelAnchor_p) {

    int register_rc = ANGEL_REGISTER_UNDEFINED;
    int lx = 0;

    if (angelAnchor_p == NULL) {
        //------------------------------------------------------------------
        // Look up the PC number: bgvt->cgoo->lx
        //------------------------------------------------------------------
        bgvt* __ptr32 bgvt_p = findBGVT();
        if (bgvt_p == NULL) {
            register_rc = ANGEL_REGISTER_NO_BGVT;
            return register_rc;
        }

        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p == NULL) {
            register_rc = ANGEL_REGISTER_NO_CGOO;
            return register_rc;
        }

        if (!cgoo_p->bbgzcgoo_flags.angel_active) {
            register_rc = ANGEL_REGISTER_ANGEL_INACTIVE;
            return register_rc;
        }

        lx = cgoo_p->bbgzcgoo_rsvd_lx;
    } else {
        if (!angelAnchor_p->flags.angel_active) {
            register_rc = ANGEL_REGISTER_ANGEL_INACTIVE;
            return register_rc;
        }

        lx = angelAnchor_p->pclx;
    }

    if (lx == 0) {
        register_rc = ANGEL_REGISTER_NO_LX;
        return register_rc;
    }

    // TODO: Setup an ESTAE to catch S0D6 after this

    //------------------------------------------------------------------
    // Load the parameter list into R1 (the parameter list of
    // the PC target is the same as the stub, minus the angel anchor).
    // Load the number of parameters into R0 so that the target can do
    // a MVCSK and read the parameter list (the individual parameters
    // will still need to be copied out with MVCSK later).
    //------------------------------------------------------------------
    int pc = lx + 0; /* Register */
    __asm(" LG 1,128(13)\n"
          " LG 1,32(1)\n"
          " LGHI 0,1\n"
          " PC 0(%1)\n"
          " ST 15,%0" :
          "=m"(register_rc) :
          "r"(pc) :
          "r1");

    return register_rc;
}


int angel_invoke_pc_client_stub(int function_index, int arg_struct_size, void* arg_struct_p, AngelAnchor_t* angelAnchor_p) {
    int register_rc = ANGEL_REGISTER_UNDEFINED;
    int lx = 0;

    // Go find the PC number.
    if (angelAnchor_p == NULL) {
        //------------------------------------------------------------------
        // Look up the PC number: bgvt->cgoo->lx
        //------------------------------------------------------------------
        bgvt* __ptr32 bgvt_p = findBGVT();
        if (bgvt_p == NULL) {
            register_rc = ANGEL_INVOKE_NO_BGVT;
            return register_rc;
        }

        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p == NULL) {
            register_rc = ANGEL_INVOKE_NO_CGOO;
            return register_rc;
        }

        lx = cgoo_p->bbgzcgoo_rsvd_lx;
        if (lx == 0) {
            register_rc = ANGEL_INVOKE_NO_LX;
            return register_rc;
        }
    } else {
        lx = angelAnchor_p->pclx;
    }

    //-----------------------------------------------------------
    // Load the parameter list into R1 (the parameter list of
    // the PC target is the same as the stub, minus the angel
    // anchor).  Load the number of parameters into R0 so that
    // the target can do a MVCSK and read the parameter list
    // (the individual parameters will still need to be copied
    // out with MVCSK later).
    //-----------------------------------------------------------
    int pc = lx + 1; /* Invoke */
    __asm(" LG 15,128(13)\n"
          " LG 1,32(15)\n"
          " LGHI 0,3\n"
          " PC 0(%1)\n"
          " ST 15,%0" :
          "=m"(register_rc) :
          "r"(pc) :
          "r1","r15");

    return register_rc;
}

/*-------------------------------------------------------------------*/
/* Calls PC that unbinds a Lexington server from the angel.          */
/*-------------------------------------------------------------------*/
int angel_deregister_pc_client_stub(AngelAnchor_t* angelAnchor_p) {

    int deregister_rc = ANGEL_DEREGISTER_UNDEFINED;
    int lx = 0;

    if (angelAnchor_p == NULL) {
        //------------------------------------------------------------------
        // Look up the PC number: bgvt->cgoo->lx
        //------------------------------------------------------------------
        bgvt* __ptr32 bgvt_p = findBGVT();
        if (bgvt_p == NULL) {
            deregister_rc = ANGEL_DEREGISTER_NO_BGVT;
            return deregister_rc;
        }

        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p == NULL) {
            deregister_rc = ANGEL_DEREGISTER_NO_CGOO;
            return deregister_rc;
        }

        lx = cgoo_p->bbgzcgoo_rsvd_lx;
        if (lx == 0) {
            deregister_rc = ANGEL_DEREGISTER_NO_LX;
            return deregister_rc;
        }
    } else {
        lx = angelAnchor_p->pclx;
    }

    //------------------------------------------------------------------
    // We have no parameters, so just load the number
    // of parameters into R0 and drive the PC.
    //------------------------------------------------------------------
    int pc = lx + 2; /* Deregister */
    __asm(" LGHI 0,0\n" /* No parameters */
          " PC 0(%1)\n"
          " ST 15,%0" :
          "=m"(deregister_rc) :
          "r"(pc) :
          "r1");

    return deregister_rc;
}

/*-------------------------------------------------------------------*/
/* Get the version of the Angel DRM that is currently loaded by the  */
/* angel.  Note that this function is called by the server, and does */
/* traverse some angel control block structures.                     */
/*-------------------------------------------------------------------*/
int getAngelVersion(AngelAnchor_t* angelAnchor_p) {
    int version = -1;

    // Set up an ESTAE in case we somehow are not connected to the angel,
    // and abend trying to traverse this control structure (probably
    // because we're not connected to the shared above the bar storage).
    int estaex_rc = -1, estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);
    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            bbgzsgoo* sgoo_p = NULL;
            if (angelAnchor_p == NULL) {
                bgvt* __ptr32 bgvt_p = findBGVT();
                if (bgvt_p != NULL) {
                    bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
                    if (cgoo_p != NULL) {
                        sgoo_p = (bbgzsgoo*) cgoo_p->bbgzcgoo_sgoo_p;
                    }
                }
            } else {
                sgoo_p = (bbgzsgoo*) angelAnchor_p->sgoo_p;
            }

            if (sgoo_p != NULL) {
                bbgzarmv* armv_p = (bbgzarmv*) sgoo_p->bbgzsgoo_armv;
                if (armv_p != NULL) {
                    struct bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
                    if (drm_p != NULL) {
                        version = drm_p->getVersionNumber();
                    }
                }
            }
        }

        remove_estaex(&estaex_rc, &estaex_rsn);
    }

    return version;
}

/*-------------------------------------------------------------------*/
/* Find an angel anchor.  The angel anchor is required if using a    */
/* named angel.                                                      */
/*-------------------------------------------------------------------*/
AngelAnchor_t* findAngelAnchor(char* angelName) {
    AngelAnchor_t* angelAnchor_p = NULL;

    bgvt* __ptr32 bgvt_p = findBGVT();
    if (bgvt_p != NULL) {
        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p != NULL) {
            AngelAnchorSet_t* angelAnchorSet_p = cgoo_p->firstAaSet_p;
            while(angelAnchorSet_p != NULL) {
                for (int x = 0; x < angelAnchorSet_p->nextAvailableSlot; x++) {
                    if (strcmp(angelName, angelAnchorSet_p->namedAngelInfo[x].name) == 0) {
                        angelAnchor_p = &(angelAnchorSet_p->namedAngelInfo[x]);
                        break;
                    }
                }
                if( angelAnchor_p != NULL)
                  break;
                else {
                    angelAnchorSet_p = angelAnchorSet_p->next_p;
                }
            }
        }
    }

    return angelAnchor_p;
}
