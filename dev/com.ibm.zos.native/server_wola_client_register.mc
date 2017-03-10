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
#include <metal.h>
#include <builtins.h>

#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_user_token_manager.h"
#include "include/server_wola_client.h"
#include "include/server_wola_nametoken_utility.h"
#include "include/server_wola_shared_memory_anchor.h"
#include "include/server_wola_registration.h"

#include "include/gen/cvt.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"
#include "include/gen/ihapsa.h"

/**
 * Blank pad the given src string up to the given to_len and place the
 * result in the given target.
 *
 * Note: this method will blank-pad IF it finds a null-term char in src
 * before reaching to_len.  If not, then it simply copies to_len bytes from
 * src to target.
 *
 * Note: target is NOT null terminated by this method.
 *
 * @param target - output - the blank-padded string is copied here (storage area must be to_len long)
 * @param src - the src string to be blank-padded
 * @param to_len - the length of the resulting target str, plus the blank pad
 *
 * @return target
 */
char * blankPad(char * target, char * src, int to_len) {

    int foundNull = 0;
    for(int i = 0; i < to_len; ++i) {
        foundNull = (foundNull || (src[i] == 0));
        target[i] = ((foundNull) ? ' ' : src[i]);    // keep copying from src until we find a null.
    }

    return target;
}

/**
 *
 * TODO: should this be moved to server_wola_registration.mc?
 *
 * @return a brand new client BBOARGE entry 
 */
WolaRegistration_t* createClientRegistration(WOLARegisterParms_t* parms_p,
                                             WolaSharedMemoryAnchor_t* bboashr_p,
                                             WolaRegistration_t* serverRegistration_p) {

    WolaRegistration_t* newRegistration_p = getCellPoolCell( bboashr_p->registationCellPoolID);

    if (newRegistration_p != 0) {
        memset(newRegistration_p, 0, sizeof(WolaRegistration_t)); // clear rge
        memcpy(newRegistration_p->eye, BBOARGE_EYE, sizeof(newRegistration_p->eye));
        newRegistration_p->size = sizeof(WolaRegistration_t);
        newRegistration_p->version = BBOARGE_VERSION_2;
        newRegistration_p->this_p = newRegistration_p;

        psa* psa_p = 0;
        ascb* ascb_p = psa_p->psaaold;
        assb* assb_p = ascb_p->ascbassb;

        if (ascb_p->ascbjbni != 0) {
            memcpy(newRegistration_p->clientJobName, ascb_p->ascbjbni , sizeof(newRegistration_p->clientJobName));
        } else {
            memcpy(newRegistration_p->clientJobName, ascb_p->ascbjbns , sizeof(newRegistration_p->clientJobName));
        }

        newRegistration_p->ascb_p = psa_p->psaaold;
        memcpy(newRegistration_p->stoken, &(assb_p->assbstkn), sizeof(newRegistration_p->stoken));
        newRegistration_p->flags.clientRegistration = 1;
        newRegistration_p->flags.active = 1;
        newRegistration_p->serverRegistration_p = serverRegistration_p;

        // Save the STCK from when the server RGE was created.  If the server goes down and comes
        // back up, this is how we detect that the client needs to perform a rebind.
        newRegistration_p->serverStartSTCK = serverRegistration_p->stckLastStateChange;

        blankPad(newRegistration_p->registrationName, parms_p->registerName, sizeof(newRegistration_p->registrationName));

        newRegistration_p->minConns = parms_p->minConn;
        newRegistration_p->maxConns = parms_p->maxConn;
        newRegistration_p->connPoolState = BBOARGE_CONNPOOL_READY;

        if ((parms_p->registerFlags & WOLA_REGISTER_FLAGS_TRANS) == WOLA_REGISTER_FLAGS_TRANS) {
            newRegistration_p->flags.transactionFlag = 1;
        }

        if ((parms_p->registerFlags & WOLA_REGISTER_FLAGS_C2WPROP) == WOLA_REGISTER_FLAGS_C2WPROP) {
            newRegistration_p->flags.propAceeFromTrueIntoServer = 1;
        }

        newRegistration_p->flags.rrsTranProp = serverRegistration_p->flags.rrsTranProp;
        // TODO new header has rrsOtmaTranProp:1, need to compare most recent bbgapc1 to mine to see it if missing anything.

        newRegistration_p->wolaAnchor_p = bboashr_p;
        // newRegistration_p->aascb_p   Needed?  TODO

        // argeserv_pc = ServerPCNUM;     TODO
        // argetrclevel = LocalTraceLvl;  TODO
        // argeflags.argeflg_W2Csec = aregflg_W2Csec;  no flag for this in rge comment makes it sound unneeded TODO

        __stck(&(newRegistration_p->stckLastStateChange));

    }

    return newRegistration_p;
}

/**
 *
 */
int createRegistrationNameToken(WolaRegistration_t* registration_p, char* registerName_p) {
    int rc = -1;
    struct register_name_token_map name_token;
    char token_name[16];
    memset(&name_token, 0, sizeof(struct register_name_token_map));
    name_token.registration_p = registration_p;
    getRegisterTokenName(token_name, registerName_p);

    iean4cr(IEANT_PRIMARY_LEVEL,    // TODO twas used this
            token_name,
            (char *)&name_token,
            IEANT_NOPERSIST,       // TODO twas used this
            &rc);

    return rc;
}

/**
 * Register with server.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaRegister(WOLARegisterParms_t* parms_p) {
    unsigned int localRC = 0;
    unsigned int localRSN = 0;

    if (parms_p->rc_p == 0) {
        return;
    }
    bbgz_psw pswFromLinkageStack;
    extractPSWFromLinkageStack(&pswFromLinkageStack);
    if (parms_p->rsn_p == 0) {
        localRC =  WOLA_RC_BAD_PARM;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        return;
    }

    // Check node name and server name.  In practice this should never happen since the
    // client had to use the node and server name to look up the server STOKEN to get
    // authorized to get this far.  If one or both were blank, we wouldn't be here.
    if ((memcmp(parms_p->nodeName, zero_wola_register_name, sizeof(parms_p->nodeName)) == 0) ||
        (memcmp(parms_p->serverName, zero_wola_register_name, sizeof(parms_p->serverName)) == 0)) {
        localRC  = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_NAME_PART_MISSING;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // Get upset if register name has a null
    if (memchr(parms_p->registerName, 0, sizeof(parms_p->registerName)) != 0) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_REG_NAMENULL;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    struct register_name_token_map token;
    // If already registered get out.
    if (getRegisterNameToken(parms_p->registerName, (char*)&token) == 0) {
        localRC  = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_REGDONE;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    if (parms_p->maxConn < parms_p->minConn) {
        localRC = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_REGCONNERR;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    WolaSharedMemoryAnchor_t* bboashr_p = clientConnectToWolaSharedMemoryAnchor(parms_p->daemonGroupName);
    if (bboashr_p == 0) {
        localRC = WOLA_RC_SEVERE12;
        localRSN = WOLA_RSN_ATTACH_FAIL;
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    WolaRegistration_t* serverRegistration_p = findServerBboargeInChain(bboashr_p->firstRge_p, parms_p->nodeName, parms_p->serverName);
    if (serverRegistration_p == 0) {
        localRC  = WOLA_RC_SEVERE12;
        localRSN = WOLA_RSN_REGISTER_NO_SERVER_RGE;
        // ArrData.aarr_att_dec = 0;         TODO estae flag
        // Call Detach_Share_Mem(ShrMem@, AASCBPtr, Addr(ArrData));  TODO
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    /* If CICS security propagation is requested by task yet that method is not
      allowed as documented in the server's RGE, return with an error. */
    if (((parms_p->registerFlags & WOLA_REGISTER_FLAGS_C2WPROP) == WOLA_REGISTER_FLAGS_C2WPROP) &&
        !(serverRegistration_p->flags.propAceeFromTrueIntoServer)) {
        localRC  = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_REGBADPROP;
        // ArrData.aarr_att_dec = 0;         TODO estae flag
        // Call Detach_Share_Mem(ShrMem@, AASCBPtr, Addr(ArrData));  TODO
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // TODO: figure out how to get maxconnectionsPerRegisteration
  //  if (parms_p->maxConn > bboashr_p->maxConnectionsPerRegistration) {
  //      localRC  = WOLA_RC_ERROR8;
  //      localRSN = WOLA_RSN_REGMXCONN;
        // ArrData.aarr_att_dec = 0;                                 TODO estae flag
        // Call Detach_Share_Mem(ShrMem@, AASCBPtr, Addr(ArrData));  TODO
  //      memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
  //      memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
  //      return;
  //  }

    // TODO add tran stuff here when support trans.
    // if requested trans but server does not support it, warn the caller.
    if (((parms_p->registerFlags & WOLA_REGISTER_FLAGS_TRANS) == WOLA_REGISTER_FLAGS_TRANS) &&
        !(serverRegistration_p->flags.rrsTranProp)) {
        localRC = WOLA_RC_WARN4;
        localRSN = WOLA_RSN_NO_TRANS;
    }

    // Create a registry entry for this address space.
    //TODO: use the BBOA+registername(16 bytes) in the registrationName of the WolaRegistration_t object
    WolaRegistration_t* newRegistration_p = createClientRegistration(parms_p, bboashr_p, serverRegistration_p);
    if (newRegistration_p == 0) {
        localRC  = WOLA_RC_ERROR8;
        localRSN = WOLA_RSN_RGEOOM;
        // ArrData.aarr_att_dec = 0;                                 TODO estae flag
        // Call Detach_Share_Mem(ShrMem@, AASCBPtr, Addr(ArrData));  TODO
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // TODO what is this for
    //if (aapiflg_CICS = 1) Then
    //Do;
    //  argevector@ = aregvector@;             /* @LI4798I6C*/
    //End;
    //Else
    //Do;
    //  GlobalBGVTPtr =                         /* @F003703A*/
    //    CVTPTR_MAP->CVTMAP.CVTECVT->ECVT.ECVTBCBA;
    //  Rfy BGVT Based(GlobalBGVTPtr) Push;     /* @F003703A*/
    //  Rfy BBOAMCST Based(BBODBGVT_BBOAMCST_PTR) Push;
    //  ClientStubPtr = amcst_slots(1); /* Use 1st slot */
    //  Rfy BBOAMCSS Based(ClientStubPtr) Push; /* @F003703A*/
    //  argevector@ = amcss_vector_ptr;         /* @F003703A*/
    //  Rfy BBOAMCSS Pop;                       /* @F003703A*/
    //  Rfy BBOAMCST Pop;                       /* @F003703A*/
    //  Rfy BGVT Pop;                           /* @F003703A*/
    //End;

    // TODO create min connections
    // TODO create resmgrs
    addBboargeToChain(bboashr_p, newRegistration_p);
    /******************************************************/
    /* Mark the RGE 'active' just before creating the name*/
    /* token for it.  We test this during other API calls */
    /* to ensure they are running with an active reg.     */
    /******************************************************/
    newRegistration_p->flags.active = 1;
    int rc = createRegistrationNameToken(newRegistration_p, parms_p->registerName);
    if ( rc != 0) {
      // TODO RemoveFromRGETable
      // TODO delete resmgrs
      // TODO Cleanup Connection Pool
        /****************************************************/
        /* A RC=4 indicates this was a race condition and   */
        /* another thread must have already created the RGE */
        /* and associated name token for this name in this  */
        /* address space between this point and the point   */
        /* above where we called name/tok retrieve to locate*/
        /* the name token.                                  */
        /****************************************************/
        if (rc == 4) {
            localRC  = WOLA_RC_SEVERE12;
            localRSN = WOLA_RSN_NAMTOK_RGE_ERR;
        } else {
            localRC  = WOLA_RC_SEVERE12;
            localRSN = WOLA_RSN_BADNAME_TOKN;
        }
        // ArrData.aarr_att_dec = 0;                                 TODO estae flag
        // Call Detach_Share_Mem(ShrMem@, AASCBPtr, Addr(ArrData));  TODO
        memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
        memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
        return;
    }

    // TODO bind?

    // ----------------------------------------------------------------
    // Push output parameters back to caller
    // ----------------------------------------------------------------
    memcpy_dk(parms_p->rc_p, &localRC, sizeof(localRC), pswFromLinkageStack.key);
    memcpy_dk(parms_p->rsn_p, &localRSN, sizeof(localRSN), pswFromLinkageStack.key);
}
