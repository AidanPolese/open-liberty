/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/angel_client_pc_stub.h"
#include "include/angel_server_pc_stub.h"

#include "include/angel_bgvt_services.h"
#include "include/bbgzsgoo.h"
#include "include/mvs_enq.h"

// -------------------------------------------------------------------
// rname_p is the null terminated rname.  Extract the angel name from
// it.  rname_p should be null terminated after the 54 character
// angel name.
// -------------------------------------------------------------------
static char* extractAngelNameFromEnq(char* rname_p) {
    // Angel name is after the fourth colon of the ENQ RNAME.  We
    // can't just search for the last colon because the angel name
    // might contain a colon.
    char* cur_p = rname_p;
    for (int x = 0; ((x < 4) && (cur_p != NULL)); x++) {
        int len = strlen(cur_p);
        cur_p = memchr(cur_p, ':', len);
        if (cur_p != NULL) {
            cur_p = cur_p + 1; // Advance to next char after ':'
        }
    }

    return cur_p;
}

// -------------------------------------------------------------------
// Lookup the angel registration ENQ based on the input stoken.
// Return the angel anchor corresponding to the angel which that
// server is registered with, or NULL if registered with the default
// angel.
// -------------------------------------------------------------------
static AngelAnchor_t* lookupAngelAnchor(SToken* stoken_p) {
    AngelAnchor_t* angelAnchor_p = NULL;
    char serverEnqRname[255];

    // We are going to get back an ENQ that contains the angel name we want to bind to.
    snprintf(serverEnqRname, sizeof(serverEnqRname), ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_QUERY,
             *((long long*)(stoken_p)));

    int enqRc, enqRsn;
    isgyquaahdr* enqData_p = scan_enq_system(BBGZ_ENQ_QNAME, serverEnqRname, &enqRc, &enqRsn);

    // We should get one ENQ back if the server is connected to a named angel, and zero
    // back if the server is connected to the default angel.  We're assuming that the
    // server exists and is connected to some angel since the client was able to get far
    // enough to find the server and get its stoken.  In the event that it's not, we'll
    // assume it's using the default angel which is the same behavior we had before the
    // named angel support.
    if (enqData_p != NULL) {
        isgyquaars* serverEnq_p = (isgyquaars*) enqData_p->isgyquaahdrfirstrecord31;

        // Look at the first ENQ.
        if (serverEnq_p != NULL) {
            char rname[256]; // This is the complete RNAME with angel name filled in.
            memcpy(rname, serverEnq_p->isgyquaarsrname31, serverEnq_p->isgyquaarsrnamelen);
            rname[serverEnq_p->isgyquaarsrnamelen] = 0;

            char* angelName_p = extractAngelNameFromEnq(rname);
            angelAnchor_p = findAngelAnchor(angelName_p);
        }

        // ENQ scan obtains storage for us, so free it here.
        free(enqData_p);
    }

    return angelAnchor_p;
}

// -------------------------------------------------------------------
// Calls PC that binds a client to a Liberty server.
// -------------------------------------------------------------------
int angelClientBindStub(SToken* targetServerStoken_p, bbgzasvt_header** clientFunctionTablePtr_p, LibertyBindToken_t* bindToken_p, AngelAnchor_t** angelAnchorPtr_p) {

    int bind_rc = ANGEL_CLIENT_BIND_UNDEFINED;

    //------------------------------------------------------------------
    // Check to see if the server we're binding to is connected to a
    // named angel.
    //------------------------------------------------------------------
    int lx = 0;
    AngelStatusFlags_t* flags_p = NULL;
    AngelAnchor_t* angelAnchor_p = lookupAngelAnchor(targetServerStoken_p);
    if (angelAnchor_p == NULL) {
        //------------------------------------------------------------------
        // Look up the PC number: bgvt->cgoo->lx
        //------------------------------------------------------------------
        bgvt* __ptr32 bgvt_p = findBGVT();
        if (bgvt_p == NULL) {
            return ANGEL_CLIENT_BIND_NO_BGVT;
        }

        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p == NULL) {
            return ANGEL_CLIENT_BIND_NO_CGOO;
        }

        flags_p = &(cgoo_p->bbgzcgoo_flags);
        lx = cgoo_p->bbgzcgoo_rsvd_lx;
    } else {
        flags_p = &(angelAnchor_p->flags);
        lx = angelAnchor_p->pclx;
    }

    if (!(flags_p->angel_active)) {
        return ANGEL_CLIENT_BIND_ANGEL_INACTIVE;
    }

    if (lx == 0) {
        return ANGEL_CLIENT_BIND_NO_LX;
    }

    //------------------------------------------------------------------
    // Load the parameter list into R1 (the parameter list of the PC
    // target is the same as the stub, minus the angel anchor pointer).
    // Load the number of parameters into R0 so that the target can do
    // a MVCSK and read the parameter list (the individual parameters
    // will still need to be copied out with MVCSK later).
    //------------------------------------------------------------------
    int pc = lx + 3; /* Client Bind */
    __asm(" LG 15,128(13)\n"
          " LG 1,32(15)\n"
          " LGHI 0,3\n"
          " PC 0(%1)\n"
          " ST 15,%0" :
          "=m"(bind_rc) :
          "r"(pc) :
          "r1");

    // Copy the angel anchor pointer back to the client.
    if (bind_rc == ANGEL_CLIENT_BIND_OK) {
        memcpy(angelAnchorPtr_p, &(angelAnchor_p), sizeof(angelAnchor_p));
    }

    return bind_rc;
}


// -------------------------------------------------------------------
// Calls PC that invokes a service in the Liberty common module.
// -------------------------------------------------------------------
int angelClientInvokeStub(LibertyBindToken_t* bindToken_p, unsigned int serviceIndex, int parm_len, void* parm_p, AngelAnchor_t* angelAnchor_p) {
    int invoke_rc = ANGEL_CLIENT_INVOKE_UNDEFINED;
    int lx = 0;

    if (angelAnchor_p == NULL) {
        //------------------------------------------------------------------
        // Look up the PC number: bgvt->cgoo->lx
        //------------------------------------------------------------------
        bgvt* __ptr32 bgvt_p = findBGVT();
        if (bgvt_p == NULL) {
            return ANGEL_CLIENT_INVOKE_NO_BGVT;
        }

        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p == NULL) {
            return ANGEL_CLIENT_INVOKE_NO_CGOO;
        }

        lx = cgoo_p->bbgzcgoo_rsvd_lx;
    } else {
        lx = angelAnchor_p->pclx;
    }

    if (lx == 0) {
        return ANGEL_CLIENT_INVOKE_NO_LX;
    }

    //-----------------------------------------------------------
    // Load the parameter list into R1 (the parameter list of
    // the PC target is the same as the stub, minus the angel
    // anchor).  Load the number of parameters into R0 so that
    // the target can do a MVCSK and read the parameter list
    // (the individual parameters will still need to be copied
    // out with MVCSK later).
    //-----------------------------------------------------------
    int pc = lx + 4; /* Invoke */
    __asm(" LG 15,128(13)\n"
          " LG 1,32(15)\n"
          " LGHI 0,4\n"
          " PC 0(%1)\n"
          " ST 15,%0" :
          "=m"(invoke_rc) :
          "r"(pc) :
          "r1","r15");

    return invoke_rc;
}

// -------------------------------------------------------------------
// Calls PC that unbinds a client from a Liberty server.
// -------------------------------------------------------------------
int angelClientUnbindStub(LibertyBindToken_t* bindToken, AngelAnchor_t* angelAnchor_p) {

    int unbind_rc = ANGEL_CLIENT_UNBIND_UNDEFINED;
    int lx = 0;

    if (angelAnchor_p == NULL) {
        //------------------------------------------------------------------
        // Look up the PC number: bgvt->cgoo->lx
        //------------------------------------------------------------------
        bgvt* __ptr32 bgvt_p = findBGVT();
        if (bgvt_p == NULL) {
            return ANGEL_CLIENT_UNBIND_NO_BGVT;
        }

        bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
        if (cgoo_p == NULL) {
            return ANGEL_CLIENT_UNBIND_NO_CGOO;
        }

        lx = cgoo_p->bbgzcgoo_rsvd_lx;
    } else {
        lx = angelAnchor_p->pclx;
    }

    if (lx == 0) {
        return ANGEL_CLIENT_UNBIND_NO_LX;
    }

    //-----------------------------------------------------------
    // Load the parameter list into R1 (the parameter list of
    // the PC target is the same as the stub, minus the angel
    // anchor).  Load the number of parameters into R0 so that
    // the target can do a MVCSK and read the parameter list
    // (the individual parameters will still need to be copied
    // out with MVCSK later).
    //-----------------------------------------------------------
    int pc = lx + 5; /* Unbind */
    __asm(" LG 15,128(13)\n"
          " LG 1,32(15)\n"
          " LGHI 0,1\n"
          " PC 0(%1)\n"
          " ST 15,%0" :
          "=m"(unbind_rc) :
          "r"(pc) :
          "r1","r15");

    return unbind_rc;
}

