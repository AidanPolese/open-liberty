/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "include/gen/ihapsa.h"
#include "include/angel_client_pc_stub.h"
#include "include/bbgzasvt.h"
#include "include/client_wola_stubs.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/mvs_enq.h"
#include "include/mvs_storage.h"
#include "include/mvs_plo.h"
#include "include/mvs_user_token_manager.h"
#include "include/server_common_function_module_stub.h"
#include "include/server_wola_client.h"
#include "include/server_wola_nametoken_utility.h"
#include "include/server_wola_shared_memory_anchor.h"

//-----------------------------------------------------------
// CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!!
//-----------------------------------------------------------
//  ANY CHANGES TO THIS PART need to have the Client Vector
//  Slot level in bboacall incremented. If this is not done,
//  the code in server_wola_services.mc that loads BBOACALL
//  will NOT reload this module if there is already a BBOACALL
//  at an older level loaded.
//  While testing changes to modules in BBOACALL, you can
//  set this value to 9999 and server_wola_services.mc will
//  always replace an old BBOACALL with this one.
//  We started with level 101 for WAS 9 where this code
//  is introduced.
//-----------------------------------------------------------
// CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!!
//-----------------------------------------------------------

//
// The following is an attempt at an explanation as to what types of servers
// this client can connect to.  The client attempts to connect to both Liberty
// and tWAS servers.  In order to be able to do this, the server-side stubs
// need to be loaded for the server type you are connecting to.  For tWAS
// this means the cell must have the enable_adapter property set, and BBOACALL
// is loaded.  For Liberty this means the server is configured for WOLA and
// its version of BBOACALL is loaded.  Of course the client must be linked
// with this version of the WOLA stub for any of this to work (ie CICS is
// running with BBOATRUE, BBOACNTL, BBOACSRV and BBOACLNK provided by Liberty,
// or standalone program is linked with BBOA1* provided by Liberty).
//
// When BBOA1REG is invoked, we'll see if the Liberty stubs are loaded.  If
// they are, call REGISTER.  If they are not, or if the RC/RSN is 12/10, see
// if the tWAS stubs are loaded.  If they are, try the tWAS register.  If that
// worked, great, but if not, report the RC/RSN from the Liberty stubs.
//
// At this point if things went OK, we have a registration.  For the services
// that take a register name, we'll look to see if there is a register name
// token for either version.  The tWAS name token is prefixed with 'BBOA' so
// if that exists, call the tWAS version.  For 'BBOZ' call the Liberty version.
// For the services that take a connection handle, we'll use the registration
// pointed to by the connection handle to determine what version the
// registration is.  V1 is tWAS, V2 is Libert
//

/**
 * Structure used by the client to keep track of the information
 * necessary to call into a Liberty server.
 */
typedef struct wolaClientBindInformation {
    unsigned char eyecatcher[8];  //!< Eye catcher
    unsigned short version; //!< Version of this control block
    unsigned short length;  //!< Length of this control block
    unsigned char unregistered; //!< Unregistered.
    unsigned char _available[3]; //!< Available for use
    LibertyBindToken_t clientBindToken; //!< Current bind token
    WolaClientPCIndexes_t clientServiceIndexes; //!< Authorized services
    SToken libertyServerStoken; //!< SToken for current bound server
    char wolaGroup[8]; //!< The WOLA group name we're connected to
    char wolaNamePart2[8]; //!< Second part of WOLA name
    char wolaNamePart3[8]; //!< Third part of WOLA name
    AngelAnchor_t* angelAnchor_p; //!< The angel anchor, or NULL if default angel.
} WolaClientBindInformation_t;

/** Prefix to use when creating client name token for a registration */
#define WOLA_CLIENT_REGISTRATION_NAMETOKEN_PREFIX "BBOa"

/** Subpool to obtain heap storage in */
#define WOLA_CLIENT_SUBPOOL 131

/** Key to obtain heap storage in */
#define WOLA_CLIENT_KEY 8

register void* myenvtkn __asm("r12");

/**
 * Create the name token used in the client to cache bind information.
 *
 * @param bindInfo_p The information the client will use to communicate with the
 *        liberty server.
 * @param regname The 12 character registration name.
 *
 * @return 0 if the name token was created successfully.
 */
static int createWolaClientBindNameToken(WolaClientBindInformation_t* bindInfo_p, char* regname) {
    char nameTokenName[16];
    char nameTokenToken[16];

    memcpy(&(nameTokenName[0]), WOLA_CLIENT_REGISTRATION_NAMETOKEN_PREFIX, 4);
    memcpy(&(nameTokenName[4]), regname, 12);
    memset(nameTokenToken, 0, sizeof(nameTokenToken));
    memcpy(nameTokenToken, &(bindInfo_p), sizeof(bindInfo_p));

    int rc = -1;
    iean4cr(IEANT_HOME_LEVEL,
            nameTokenName,
            nameTokenToken,
            IEANT_NOPERSIST,
            &rc);

    return rc;
}

/**
 * Retrieve the bind information stored in the name token for a registration.
 *
 * @param regname The 12 character registration name.
 *
 * @return The bind information, or NULL if not found.
 */
static WolaClientBindInformation_t* retrieveWolaClientBindNameToken(char* regname) {
    char nameTokenName[16];
    char nameTokenToken[16];

    memcpy(&(nameTokenName[0]), WOLA_CLIENT_REGISTRATION_NAMETOKEN_PREFIX, 4);
    memcpy(&(nameTokenName[4]), regname, 12);

    int rc = -1;
    iean4rt(IEANT_HOME_LEVEL,
            nameTokenName,
            nameTokenToken,
            &rc);

    WolaClientBindInformation_t* bindInfo_p = NULL;
    if (rc == 0) {
        memcpy(&bindInfo_p, nameTokenToken, sizeof(bindInfo_p));
}

    return bindInfo_p;
}

/**
 * Destroy the name token containing the bind information for a registration.
 *
 * @param regname The 12 character registration name.
 */
static void destroyWolaClientBindNameToken(char* regname) {
    char nameTokenName[16];

    memcpy(&(nameTokenName[0]), WOLA_CLIENT_REGISTRATION_NAMETOKEN_PREFIX, 4);
    memcpy(&(nameTokenName[4]), regname, 12);

    int rc = -1;
    iean4dl(IEANT_HOME_LEVEL,
            nameTokenName,
            &rc);
}

// See if this is a tWAS registration based on the register name token.
static unsigned char isTWasRegistration(char* registername, void* metalCEnv_p) {
    // ----------------------------------------------------------------------
    // Create a metal C environment if we don't already have one.
    // ----------------------------------------------------------------------
    void* prevR12_p = myenvtkn;
    if (metalCEnv_p == NULL) {
        struct __csysenv_s mysysenv;
        memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
        mysysenv.__cseversion = __CSE_VERSION_2;

        // Tell the metal C runtime library what parameters it should use if it has
        // to obtain storage on our behalf.
        mysysenv.__csesubpool = 0;
        mysysenv.__cseheap64usertoken = getTaskProblemStateUserToken();

        // Create a Metal C environment.  Save the old R12 for later.
        myenvtkn = (void * ) __cinit(&mysysenv);
    }

    char token_name[16];
    char token_token[16];
    memcpy(token_name, "BBOA", 4);
    memcpy(&(token_name[4]), registername, 12);

    // ----------------------------------------------------------------------
    // A tWAS registration should have a primary authorized name token with
    // this name.  However we're also checking for an unauthorized name
    // token to support our test bucket, which has to pretend to be a tWAS
    // registration, and can't make an authorized name token.  This is not
    // a security exposure, because the code on the other side of the PC
    // routine that does run authorized always checks for the authorized
    // version of the name token, and will fail if the name token is not
    // authorized.
    // ----------------------------------------------------------------------
    unsigned char lookupRC = ((lookupPrimaryNameToken(token_name, token_token, 1) == 0) ||
                              (lookupPrimaryNameToken(token_name, token_token, 0) == 0));

    if (metalCEnv_p == NULL) {
        __cterm((__csysenv_t) myenvtkn);
        myenvtkn = prevR12_p;
    }

    return lookupRC;
}

/* Go find the target server WOLA ENQ */
static SToken* findTargetServerStoken(char* wolaGroup, char* wolaNamePart2, char* wolaNamePart3) {
    SToken* serverStoken_p = NULL;
    char clientEnqRname[255];

    // The wolaGroup is null-padded to 8 bytes.  The ENQ assumes the wolaGroup is blank padded
    // to 8 bytes, so we need to convert here.  The wola name part 2 & 3 should already be blank
    // padded to 8 bytes.
    char wolaGroupBlankPadded[8];
    for (int x = 0; x < sizeof(wolaGroupBlankPadded); x++) {
        wolaGroupBlankPadded[x] = ((*(wolaGroup + x)) == 0) ? ' ' : (*(wolaGroup + x));
    }
    snprintf(clientEnqRname, sizeof(clientEnqRname), ADVERTISE_WOLA_SERVER_ENQ_RNAME_PATTERN,
             wolaGroupBlankPadded, wolaNamePart2, wolaNamePart3);

    int enqRc, enqRsn;
    isgyquaahdr* enqData_p = scan_enq_system(BBGZ_ENQ_QNAME, clientEnqRname, &enqRc, &enqRsn);

    // We should only get one ENQ back.
    if (enqData_p != NULL) {
        isgyquaars* serverEnq_p = (isgyquaars*) enqData_p->isgyquaahdrfirstrecord31;

        // Iterate while there are more ENQs to look at.
        if (serverEnq_p != NULL) {
            isgyquaarq* serverEnqRQ_p = (isgyquaarq*) serverEnq_p->isgyquaarsfirstrq31;
            isgyquaarqx* serverEnqRQX_p = (isgyquaarqx*) serverEnqRQ_p->isgyquaarqrqx31;

            serverStoken_p = malloc(sizeof(SToken));
            if (serverStoken_p != NULL) {
                memcpy(serverStoken_p, serverEnqRQX_p->isgyquaarqxstoken, sizeof(*serverStoken_p));
            }
        }

        // ENQ scan obtains storage for us, so free it here.
        free(enqData_p);
    }

    return serverStoken_p;
}

static void populateFunctionIndexes(bbgzasvt_header* scfmHeader_p, WolaClientBindInformation_t* bindInfo_p) {
    // Initialize the indexes to -1 so that we can tell if we found them.
    memset(&(bindInfo_p->clientServiceIndexes), 0xFF, sizeof(bindInfo_p->clientServiceIndexes));

    // Find the WOLA functions and load the function index table.
    bbgzasve* curEntry_p = (bbgzasve*)(scfmHeader_p + 1);
    for (int x = 0; x < scfmHeader_p->num_entries; x++) {
        if (memcmp(curEntry_p->bbgzasve_name, WOLA_REGISTER_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_register = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_UNREGISTER_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_unregister = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_CONNGET_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_getConnection = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_CONNREL_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_releaseConnection = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_SENDREQ_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_sendRequest = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_SENDRESP_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_sendResponse = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_SENDEXC_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_sendResponseException = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_RECEIVEANY_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_receiveRequestAny = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_RECEIVESPC_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_receiveRequestSpecific = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_RECEIVELEN_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_receiveResponseLength = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_GETDATA_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_getData = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_INVOKE_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_invokeService = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_HOST_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_hostService = x;
        } else if (memcmp(curEntry_p->bbgzasve_name, WOLA_GETCTX_FUNCTION_STRING, 8) == 0) {
            bindInfo_p->clientServiceIndexes.wola_getContext = x;
        }
        curEntry_p = curEntry_p + 1;
    }
}

/** Rebind was successful. */
#define WOLA_CLIENT_REBIND_RC_OK 0
#define WOLA_CLIENT_REBIND_RC_REG_CHANGED 1
#define WOLA_CLIENT_REBIND_RC_INVOKE_PC_FAILED 2
#define WOLA_CLIENT_REBIND_RC_REBIND_NOT_FOUND 3
#define WOLA_CLIENT_REBIND_RC_BIND_FAILED 4
#define WOLA_CLIENT_REBIND_RC_SERVER_DOWN 5

/**
 * Perform a rebind with the Liberty server.  There are two reasons why a rebind might be required:
 *
 * 1) The server stopped / died, and is now restarted.
 * 2) The zosLocalAdapters feature was restarted on the server.
 *
 * Depending on which case we have, and other circumstances, there are three possible outcomes:
 *
 * 1) Everything is still "OK".  No action is necessary.
 * 2) The bind is still OK but the feature was restarted, so the client registration has been
 *    updated to reflect that.
 * 3) The bind is no good and should be replaced.
 *
 * The rebind procedure is as follows:
 *
 * 1) Obtain a lock (ENQ) so that only one task per client process is attempting a rebind to any
 *    Liberty server at any time.  This is not a performance path and it's easier to serialize
 *    the rebind than it is to try to serialize the checking and updating of the client RGE fields.
 * 2) Look up the STOKEN of the target Liberty server, and bind to it.  We refer to this as the new
 *    bind.
 * 3) Call the rebind routine.
 * 4) If the existing bind was OK, then unbind the new bind and continue.
 *    If the existing bind was not good, then replace it with the new bind information, and unbind the
 *    existing bind.
 *
 * The primary need for rebind is the CICS link server, which must be able to survive a Liberty
 * server recycle.  The process was much easier in tWAS, when the authorized code was loaded per
 * node.  In Liberty the code is loaded per-server, and can change when a server is recycled.
 */
static int doRebind(WolaClientBindInformation_t* bindInfo_p, char* regname) {
    // This method runs with its own metal C environment.  This is not a performance path.
    // Some of the client routines do not run with a metal C environment because they do
    // not need one.  It would be a waste to create one all the time, on the off-chance that
    // a rebind is required.
    struct __csysenv_s mysysenv;
    memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
    mysysenv.__cseversion = __CSE_VERSION_2;

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    mysysenv.__csesubpool = 0;
    mysysenv.__cseheap64usertoken = getTaskProblemStateUserToken();

    // Create a Metal C environment.  Save the old R12 for later.
    void* oldR12 = myenvtkn;
    myenvtkn = (void * ) __cinit(&mysysenv);

    // Obtain an ENQ which ensures only one caller in this address space is trying
    // to resync at the same time.
    enqtoken enqToken;
    get_enq_exclusive_step(BBGZ_UNAUTH_ENQ_QNAME, CLIENT_WOLA_REBIND_ENQ_RNAME, &enqToken);

    // See what WOLA thinks the target server STOKEN is now.
    int rebindRC = -1;
    SToken* newStoken_p = findTargetServerStoken(bindInfo_p->wolaGroup, bindInfo_p->wolaNamePart2, bindInfo_p->wolaNamePart3);
    if (newStoken_p != NULL) {

        LibertyBindToken_t newBindToken;
        bbgzasvt_header* scfmHeader_p = NULL;
        AngelAnchor_t* angelAnchor_p = NULL;
        if (angelClientBindStub(newStoken_p, &scfmHeader_p, &(newBindToken), &angelAnchor_p) == 0) {
            // Call the rebind routine.
            int rebindIndex = -1;
            bbgzasve* curEntry_p = (bbgzasve*)(scfmHeader_p + 1);
            for (int x = 0; x < scfmHeader_p->num_entries; x++) {
                if (memcmp(curEntry_p->bbgzasve_name, WOLA_REBIND_FUNCTION_STRING, 8) == 0) {
                    rebindIndex = x;
                }
                curEntry_p = curEntry_p + 1;
            }

            if (rebindIndex != -1) {
                WOLARebindParms_t rebindParms;
                unsigned int rebindPC_RC = 0;

                memcpy(&rebindParms.daemonGroupName, bindInfo_p->wolaGroup, sizeof(rebindParms.daemonGroupName));
                memcpy(&rebindParms.nodeName, bindInfo_p->wolaNamePart2, sizeof(rebindParms.nodeName));
                memcpy(&rebindParms.serverName, bindInfo_p->wolaNamePart3, sizeof(rebindParms.serverName));
                memcpy(&rebindParms.registerName, regname, sizeof(rebindParms.registerName));
                memcpy(&rebindParms.serverStoken, newStoken_p, sizeof(rebindParms.serverStoken));
                rebindParms.rc_p = &rebindPC_RC;

                int stubRC = wolaClientRebind_stub(&(newBindToken), rebindIndex, &rebindParms, angelAnchor_p);
                if (stubRC == 0) {
                    if (rebindPC_RC == WOLA_RC_INTERNAL_REBIND_NOT_NECESSARY) {
                        angelClientUnbindStub(&(newBindToken), angelAnchor_p);
                        rebindRC = WOLA_CLIENT_REBIND_RC_OK;
                    } else if (rebindPC_RC == WOLA_RC_INTERNAL_REBIND_OK) {
                        // Save the old bind token and angel anchor.  We're going to replace them.
                        LibertyBindToken_t oldBindToken;
                        AngelAnchor_t* oldAngelAnchor_p = bindInfo_p->angelAnchor_p;
                        memcpy(&oldBindToken, &(bindInfo_p->clientBindToken), sizeof(oldBindToken));

                        // Update the bindInfo with new function indexes, etc.
                        populateFunctionIndexes(scfmHeader_p, bindInfo_p);
                        memcpy(&(bindInfo_p->libertyServerStoken), newStoken_p, sizeof(*newStoken_p));
                        memcpy(&(bindInfo_p->clientBindToken), &(newBindToken), sizeof(newBindToken));
                        bindInfo_p->angelAnchor_p = angelAnchor_p;

                        // Unbind from the old server.  Note some other TCB could be using the old
                        // bind token right now, and would get a 'bad bind token' return code.
                        angelClientUnbindStub(&(oldBindToken), oldAngelAnchor_p);
                        rebindRC = WOLA_CLIENT_REBIND_RC_OK;
                    } else {
                        // Bad STOKEN or bad 3 part name.  Nothing we can do.
                        // TODO: This unbind (and others) are causing the client's LOCL to be detached.
                        //       The LSCL for the 'old' server is no longer in the chain hung off the LOCL.
                        //       When unbind cleanup runs it sees no more LSCLs and then cleans up the LOCL.
                        //       There are still connections allocated that point to the LOCL.  We need to
                        //       figure out how to tell local comm to skip LOCL cleanup if this is the case.
                        //       See also the TODO comment in server_wola_client_rebind.mc.
                        angelClientUnbindStub(&(newBindToken), angelAnchor_p);
                        rebindRC = WOLA_CLIENT_REBIND_RC_REG_CHANGED;
                    }
                } else {
                    // There was a problem calling rebind.
                    angelClientUnbindStub(&(newBindToken), angelAnchor_p);
                    rebindRC = WOLA_CLIENT_REBIND_RC_INVOKE_PC_FAILED;
                }
            } else {
                // We could not find the rebind method.  Is the server downlevel?
                // WOLA must be enabled or we would not have found the new server STOKEN.
                angelClientUnbindStub(&(newBindToken), angelAnchor_p);
                rebindRC = WOLA_CLIENT_REBIND_RC_REBIND_NOT_FOUND;
            }
        } else {
            // Something was wrong and we could not bind to the new STOKEN.
            rebindRC = WOLA_CLIENT_REBIND_RC_BIND_FAILED;
        }
    } else {
        // We could not find the new STOKEN.  Either the server is down (again) or WOLA is not
        // enabled anymore.
        rebindRC = WOLA_CLIENT_REBIND_RC_SERVER_DOWN;
    }

    // Drop the ENQ
    release_enq(&enqToken);

    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);
    myenvtkn = oldR12;

    return rebindRC;
}

/**
 *  Client Register
 *  This is called from bboasreg.s
 */
int ClientRegister(char* dgname, char* nodename, char* servername, char* registername,
                   int* minconn_p, int* maxconn_p, int* regflags_p, struct bboapc1p* cicsParms_p,
                   int* rc_p, int* rsn_p) {

    // TDK TODO: Think about handling the case where both a tWAS and Liberty registration can exit
    //           with the same register name.  IE register AAA comes in with Liberty names, then
    //           comes in with tWAS names (or vice versa).  I don't think we'll handle this case
    //           correctly, since the name token prefixes are different.
    // TDK TODO: Think about changing the BBOZ name token prefix back to BBOA.  tWAS doesn't know
    //           about the BBOZ prefix, and can't enforce naming collisions.

    // Create a metal C environment for use within this function.
    struct __csysenv_s mysysenv;
    memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
    mysysenv.__cseversion = __CSE_VERSION_2;

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    mysysenv.__csesubpool = 0;
    mysysenv.__cseheap64usertoken = getTaskProblemStateUserToken();

    // Create a Metal C environment.
    myenvtkn = (void * ) __cinit(&mysysenv);

    // Go find the server's stoken that we want to connect to.
    LibertyBindToken_t bindToken;
    WOLARegisterParms_t registerParms;

    unsigned int reg_rc = 0;
    unsigned int reg_rsn = 0;
    int bindRC;
    SToken* targetServerStoken_p = findTargetServerStoken(dgname, nodename, servername);
    if (targetServerStoken_p != NULL) {
        // Create a place to cache the information we'll retrieve from bind.
        WolaClientBindInformation_t* bindInfo_p = storageObtain(sizeof(WolaClientBindInformation_t),
                                                                    WOLA_CLIENT_SUBPOOL,
                                                                    WOLA_CLIENT_KEY,
                                                                    NULL);
        if (bindInfo_p != NULL) {
            memset(bindInfo_p, 0, sizeof(*bindInfo_p));
            bbgzasvt_header* scfmHeader_p = NULL;
            AngelAnchor_t* angelAnchor_p = NULL;
            bindRC = angelClientBindStub(targetServerStoken_p, &scfmHeader_p, &(bindToken), &angelAnchor_p);
            if (bindRC == 0) {
                populateFunctionIndexes(scfmHeader_p, bindInfo_p);
                int registerIndex = bindInfo_p->clientServiceIndexes.wola_register;

                if(registerIndex != -1 ){
                    memcpy(&registerParms.daemonGroupName, dgname, sizeof(registerParms.daemonGroupName));
                    memcpy(&registerParms.nodeName, nodename, sizeof(registerParms.nodeName));
                    memcpy(&registerParms.serverName, servername, sizeof(registerParms.serverName));
                    memcpy(&registerParms.registerName, registername, sizeof(registerParms.registerName));

                    registerParms.minConn = *minconn_p;
                    registerParms.maxConn = *maxconn_p;
                    registerParms.registerFlags = *regflags_p;
                    registerParms.cicsParms_p = cicsParms_p;
                    registerParms.rc_p = &reg_rc;
                    registerParms.rsn_p = &reg_rsn;

                    // now call the register stub
                    int stubRC = wolaRegister_stub(&(bindToken), registerIndex, &registerParms, angelAnchor_p);
                    if ((stubRC == 0) && ((reg_rc == 0) || (reg_rc == 4))) {
                        // Create the name token which will cache this information for future
                        // use.  In the event a partial unregister occurred, the old name token
                        // will still be set.  We need to delete it first.
                        memcpy(bindInfo_p->eyecatcher, "BBGZCWS_", 8);
                        bindInfo_p->version = 1;
                        bindInfo_p->length = sizeof(*bindInfo_p);
                        memcpy(&(bindInfo_p->clientBindToken), &bindToken, sizeof(bindToken));
                        memcpy(&(bindInfo_p->libertyServerStoken), targetServerStoken_p, sizeof(SToken));
                        memcpy(bindInfo_p->wolaGroup, dgname, sizeof(bindInfo_p->wolaGroup));
                        memcpy(bindInfo_p->wolaNamePart2, nodename, sizeof(bindInfo_p->wolaNamePart2));
                        memcpy(bindInfo_p->wolaNamePart3, servername, sizeof(bindInfo_p->wolaNamePart3));
                        bindInfo_p->angelAnchor_p = angelAnchor_p;

                        WolaClientBindInformation_t* oldBindInfo_p = retrieveWolaClientBindNameToken(registername);
                        if (oldBindInfo_p != NULL) {
                            destroyWolaClientBindNameToken(registername);
                            storageRelease(oldBindInfo_p,
                                           sizeof(WolaClientBindInformation_t),
                                           WOLA_CLIENT_SUBPOOL,
                                           WOLA_CLIENT_KEY);
                        }

                        createWolaClientBindNameToken(bindInfo_p, registername);
                    }else {
                        storageRelease(bindInfo_p,
                                       sizeof(WolaClientBindInformation_t),
                                       WOLA_CLIENT_SUBPOOL,
                                       WOLA_CLIENT_KEY);

                        if (stubRC != 0) {
                            reg_rc = WOLA_RC_ERROR8;
                            reg_rsn = WOLA_RSN_LIBERTY_INVOKE_ERROR;
                        }
                    }
                } else {
                    // Register index not found - server is not enabled for WOLA.
                    storageRelease(bindInfo_p,
                                   sizeof(WolaClientBindInformation_t),
                                   WOLA_CLIENT_SUBPOOL,
                                   WOLA_CLIENT_KEY);

                    reg_rc = WOLA_RC_ERROR8;
                    reg_rsn = WOLA_RSN_LIBERTY_SERVER_NOT_ENABLED_FOR_WOLA;
                }
            } else {
                // Bind failure.
                storageRelease(bindInfo_p,
                               sizeof(WolaClientBindInformation_t),
                               WOLA_CLIENT_SUBPOOL,
                               WOLA_CLIENT_KEY);
                reg_rc = WOLA_RC_SEVERE12;
                reg_rsn = WOLA_RSN_BIND_FAILED;
            }
        } else {
            reg_rc = WOLA_RC_SEVERE12;
            reg_rsn = WOLA_RSN_BIND_INFO_OOM;
        }
    } else {
        // If something went wrong, try to be more specific.
        // Lookup the bboashr pointer in the name token.
        int iean4rt_rc;
        WolaSharedMemoryAnchor_t* bboashr_p = getBboashrForWolaGroup(dgname, &iean4rt_rc);
        if(iean4rt_rc == 0){ // found the token (wolaGroup is correct) but server not up
            reg_rc = WOLA_RC_SEVERE12;
            reg_rsn = WOLA_RSN_BADSRVR;
        } else { // did not find the token, probably wolaGroup name is wrong.
            reg_rc  = WOLA_RC_SEVERE12;
            reg_rsn = BBOAAPI_RSN_BADBGVT;
        }
    }

    *rc_p = (int)reg_rc;
    *rsn_p = (int)reg_rsn;

    //    ----------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);
}

/**
 *  Client UnRegister
 *  This is called from bboasurg.s
 */
int ClientUnRegister(char* registername, int* unregflags_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {
    struct __csysenv_s mysysenv;
    memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
    mysysenv.__cseversion = __CSE_VERSION_2;

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    mysysenv.__csesubpool = 0;
    mysysenv.__cseheap64usertoken = getTaskProblemStateUserToken();

    // Create a Metal C environment for the duration of unregister.
    myenvtkn = (void * ) __cinit(&mysysenv);

    WOLAUnregisterParms_t unregisterParms;

    unsigned int urg_rc = 0;
    unsigned int urg_rsn = 0;

    // lookup the register name token
    WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
    if (bindInfo_p != NULL) {
        unsigned char isForce = (((*unregflags_p) & WOLA_UNREGISTER_FLAGS_FORCE) == WOLA_UNREGISTER_FLAGS_FORCE);
        memcpy(&unregisterParms.registerName, registername, sizeof(unregisterParms.registerName));
        unregisterParms.unregisterFlags = *unregflags_p;
        unregisterParms.cicsParms_p = cicsParms_p;
        unregisterParms.rc_p  = &urg_rc;
        unregisterParms.rsn_p = &urg_rsn;
        // now call the unregister stub
        int stubRC = wolaUnregister_stub(&(bindInfo_p->clientBindToken),
                                         bindInfo_p->clientServiceIndexes.wola_unregister,
                                         &unregisterParms,
                                         bindInfo_p->angelAnchor_p);


        // If unregister passed, then un-bind from the server.
        // TODO: there is still work to do for the case when the unregister is delayed due
        //       to connections being in-use.
        if (stubRC != 0) {
            // Something went wrong before we got into the WOLA authorized service.
            urg_rc  = WOLA_RC_ERROR8;
            urg_rsn = WOLA_RSN_LIBERTY_INVOKE_ERROR;
        } else if (urg_rc == 0) {
            // Unregister was successful.  Destroy the name token we used.
            LibertyBindToken_t tempBindToken;
            AngelAnchor_t* tempAngelAnchor_p = bindInfo_p->angelAnchor_p;
            memcpy(&tempBindToken, &(bindInfo_p->clientBindToken), sizeof(tempBindToken));
            destroyWolaClientBindNameToken(registername);
            storageRelease(bindInfo_p,
                           sizeof(WolaClientBindInformation_t),
                           WOLA_CLIENT_SUBPOOL,
                           WOLA_CLIENT_KEY);
            angelClientUnbindStub(&tempBindToken, tempAngelAnchor_p);
        } else if ((urg_rc == WOLA_RC_WARN4) && (urg_rsn == WOLA_RSN_FORCE_CONN_ACTIVE)) {
            // Unregister will finish when the remaining connections are returned
            // to the pool.  We want to destroy the name token but that will prevent
            // the connections from being able to be returned to the pool.  Mark the
            // name token as unregistered instead.  This just lets us know we were here.
            bindInfo_p->unregistered = 1;
        } else if ((bindInfo_p->unregistered == 1) && (isForce) && (urg_rc == WOLA_RC_ERROR8) && (urg_rsn == WOLA_RSN_MISSING_BBOARGE_NAMETOK)) {
            // We called regular unregister already, this is a force.  We couldn't find
            // the registration.  It's already gone.  We should be cleaning up in this path as well.
            LibertyBindToken_t tempBindToken;
            AngelAnchor_t* tempAngelAnchor_p = bindInfo_p->angelAnchor_p;
            memcpy(&tempBindToken, &(bindInfo_p->clientBindToken), sizeof(tempBindToken));
            destroyWolaClientBindNameToken(registername);
            storageRelease(bindInfo_p,
                           sizeof(WolaClientBindInformation_t),
                           WOLA_CLIENT_SUBPOOL,
                           WOLA_CLIENT_KEY);
            angelClientUnbindStub(&tempBindToken, tempAngelAnchor_p);
            urg_rsn = WOLA_RSN_REGISTER; // Yes, this is a double-use.
        }
    } else if (isTWasRegistration(registername, myenvtkn) == TRUE) {
        // If this is a tWAS registration, tell the previous layer to try calling the tWAS stub.
        // The previous layer is either bboasurg.s or bboatrue.cicsasm.
        urg_rc = WOLA_RC_ERROR8;
        urg_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
    } else {
        urg_rc  = WOLA_RC_ERROR8;
        urg_rsn = WOLA_RSN_REGISTER;
    }

    *rc_p  = (int)urg_rc;
    *rsn_p = (int)urg_rsn;
    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);
}

/**
*  Client Invoke
*  This is called from bboasinv.s
*/
int ClientInvoke(char* registername, int* reqtype_p, char* servicename, int* servicenamelen_p, char* reqdata_p, int* reqdatalen_p,
                 char* rspdata_p, int* rspdatalen_p, int* waittime_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p, int* rval_p) {
    WOLAInvokeParms_t invokeParms;

    unsigned int inv_rc = 0;
    unsigned int inv_rsn = 0;
    unsigned int inv_rv = 0;

    // lookup the register name token
    volatile WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
    if (bindInfo_p != NULL) {
        memcpy(&invokeParms.registerName, registername, sizeof(invokeParms.registerName));
        memcpy(&invokeParms.requestServiceName_p, &servicename, sizeof(invokeParms.requestServiceName_p));
        memcpy(&invokeParms.requestData_p, &reqdata_p, sizeof(invokeParms.requestData_p));
        memcpy(&invokeParms.responseData_p, &rspdata_p, sizeof(invokeParms.responseData_p));

        invokeParms.requestType = *reqtype_p;
        invokeParms.requestServiceNameLength = *servicenamelen_p;
        invokeParms.requestDataLength = *reqdatalen_p;
        invokeParms.responseDataLength = *rspdatalen_p;
        invokeParms.waitTime = *waittime_p;
        invokeParms.cicsParms_p = cicsParms_p;
        invokeParms.rc_p  = &inv_rc;
        invokeParms.rsn_p = &inv_rsn;
        invokeParms.rv_p  = &inv_rv;
        // now call the invoke stub
        int stubRC = wolaInvoke_stub((void*)(&(bindInfo_p->clientBindToken)),
                                     bindInfo_p->clientServiceIndexes.wola_invokeService,
                                     &invokeParms,
                                     bindInfo_p->angelAnchor_p);

        /* We think a rebind is required.  We also think that the target server is back up. */
        if ((inv_rc == WOLA_RC_SEVERE12) && (inv_rsn == WOLA_RSN_INTERNAL_REBIND_REQUIRED)) {
            int rebindRC = doRebind((WolaClientBindInformation_t*)bindInfo_p, registername);
            if (rebindRC == WOLA_CLIENT_REBIND_RC_OK) {
                inv_rc = 0;
                inv_rsn = 0;
                stubRC = wolaInvoke_stub((void*)(&(bindInfo_p->clientBindToken)),
                                         bindInfo_p->clientServiceIndexes.wola_invokeService,
                                         &invokeParms,
                                         bindInfo_p->angelAnchor_p);
                if ((inv_rc != 0) && (inv_rsn >= WOLA_RSN_INTERNAL_BASE) && (inv_rsn <= WOLA_RSN_INTERNAL_END)) {
                    inv_rc = WOLA_RC_ERROR8;
                    inv_rsn = WOLA_RSN_INTERNAL_AFTER_REBIND;
                }
            } else {
                /* Our rebind failed.  Tell the caller.  There may be nothing they can do */
                /* but at least they will have a specific reason code.                    */
                inv_rc = WOLA_RC_ERROR8;
                inv_rsn = WOLA_RSN_REBIND_FAILED;
            }
        }


    } else if (isTWasRegistration(registername, NULL) == TRUE) {
        // If this is a tWAS registration, tell the previous layer to try calling the tWAS stub.
        // The previous layer is either bboasinv.s or bboatrue.cicsasm.
        inv_rc = WOLA_RC_ERROR8;
        inv_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
    } else {
        inv_rc  = WOLA_RC_ERROR8;
        inv_rsn = WOLA_RSN_REGISTER;
    }

    *rc_p  = (int)inv_rc;
    *rsn_p = (int)inv_rsn;
    *rval_p = (int)inv_rv;
}

/**
*  Client Host a service
*  This is called from bboassrv.s
*/
int ClientHostService(char* registername, char* servicename, int* servicenamelen_p, char* reqdata_p, int* reqdatalen_p,
                 char* connhandle_p, int* waittime_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p, int* rval_p) {
    WOLAHostServiceParms_t hostServiceParms;

    unsigned int hostSrv_rc = 0;
    unsigned int hostSrv_rsn = 0;
    unsigned int hostSrv_rv = 0;

    // lookup the register name token
    volatile WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
    if (bindInfo_p != NULL) {
        memcpy(&hostServiceParms.registerName, registername, sizeof(hostServiceParms.registerName));
        memcpy(&hostServiceParms.requestServiceName_p, &servicename, sizeof(hostServiceParms.requestServiceName_p));
        memcpy(&hostServiceParms.requestData_p, &reqdata_p, sizeof(hostServiceParms.requestData_p));
        memcpy(&hostServiceParms.connectionHandle_p, &connhandle_p, sizeof(hostServiceParms.connectionHandle_p));

        //requestServiceNameLength_p is an int* 'coz it is an input/output paramater
        memcpy(&hostServiceParms.requestServiceNameLength_p, &servicenamelen_p, sizeof(hostServiceParms.requestServiceNameLength_p));
        hostServiceParms.requestDataLength = *reqdatalen_p;

        hostServiceParms.waitTime = *waittime_p;
        hostServiceParms.cicsParms_p = cicsParms_p;
        hostServiceParms.rc_p  = &hostSrv_rc;
        hostServiceParms.rsn_p = &hostSrv_rsn;
        hostServiceParms.rv_p  = &hostSrv_rv;

            // now call the hostService stub
        int stubRC = wolaHostService_stub((void*)(&(bindInfo_p->clientBindToken)),
                                          bindInfo_p->clientServiceIndexes.wola_hostService,
                                          &hostServiceParms,
                                          bindInfo_p->angelAnchor_p);
        /* We think a rebind is required.  We also think that the target server is back up. */
        if ((hostSrv_rc == WOLA_RC_SEVERE12) && (hostSrv_rsn == WOLA_RSN_INTERNAL_REBIND_REQUIRED)) {
            int rebindRC = doRebind((WolaClientBindInformation_t*)bindInfo_p, registername);
            if (rebindRC == WOLA_CLIENT_REBIND_RC_OK) {
                hostSrv_rc = 0;
                hostSrv_rsn = 0;
                stubRC = wolaHostService_stub((void*)(&(bindInfo_p->clientBindToken)),
                                              bindInfo_p->clientServiceIndexes.wola_hostService,
                                              &hostServiceParms,
                                              bindInfo_p->angelAnchor_p);
                if ((hostSrv_rc != 0) && (hostSrv_rsn >= WOLA_RSN_INTERNAL_BASE) && (hostSrv_rsn <= WOLA_RSN_INTERNAL_END)) {
                    hostSrv_rc = WOLA_RC_ERROR8;
                    hostSrv_rsn = WOLA_RSN_INTERNAL_AFTER_REBIND;
                }
            } else {
                /* Our rebind failed.  Tell the caller.  There may be nothing they can do */
                /* but at least they will have a specific reason code.                    */
                hostSrv_rc = WOLA_RC_ERROR8;
                hostSrv_rsn = WOLA_RSN_REBIND_FAILED;
            }
        }
    } else if (isTWasRegistration(registername, NULL) == TRUE) {
        // If this is a tWAS registration, tell the previous layer to try calling the tWAS stub.
        // The previous layer is either bboasurg.s or bboatrue.cicsasm.
        hostSrv_rc = WOLA_RC_ERROR8;
        hostSrv_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
    }else {
        hostSrv_rc  = WOLA_RC_ERROR8;
        hostSrv_rsn = WOLA_RSN_REGISTER;
    }

    *rc_p  = (int)hostSrv_rc;
    *rsn_p = (int)hostSrv_rsn;
    *rval_p = (int)hostSrv_rv;
}

/**
 * Retrieve the registername from the registration associated with the input connection handle.
 *
 * @param connHdl_p The input connection handle.
 * @param regname A 12 byte area where the register name should be copied into.
 *
 * @return 0 if the regname was set, non-zero if not.
 */
static int getRegisterNameFromConnHdl(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, char* regname) {
    // Use the connection handle to get at the registration so that we can copy the register name.
    // Note that we're copying the register name before we validate the connection handle.
    // We'll ignore it if the PLO fails.
    PloCompareAndSwapAreaDoubleWord_t compareArea;
    PloLoadAreaDoubleWord_t loadArea;

    compareArea.compare_p = &(wolaClientConnectionHandle_p->handle_p->ploArea);
    compareArea.expectedValue = wolaClientConnectionHandle_p->instanceCount;

    loadArea.loadLocation_p = &(wolaClientConnectionHandle_p->handle_p->registration_p);

    // Copy the regname, then issue the PLO.
    WolaRegistration_t* unvalidatedRegistration_p = wolaClientConnectionHandle_p->handle_p->registration_p;
    memcpy(regname, unvalidatedRegistration_p->registrationName, 12);

    int rc = ploCompareAndLoadDoubleWord(&(wolaClientConnectionHandle_p->handle_p->ploArea), &compareArea, &loadArea);

    // Just make sure the register name we loaded was what was in the handle.
    if (loadArea.loadValue != ((unsigned long long) unvalidatedRegistration_p)) {
        rc = -1;
    }

    return rc;
}

/**
*  Client Send Response
*  This is called from bboassrp.s
*/
int ClientSendResponse(char* connhandle_p, char* rspdata_p, int* rspdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {

    WOLASendResponseParms_t sendResponseParms;

    unsigned int sendRsp_rc = 0;
    unsigned int sendRsp_rsn = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connhandle_p, registername);

    if (regnameRC != 0) {
        //send the error back
        sendRsp_rc  = WOLA_RC_ERROR8;
        sendRsp_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {
            memcpy(&sendResponseParms.responseData_p, &rspdata_p, sizeof(sendResponseParms.responseData_p));
            memcpy(&sendResponseParms.connectionHandle, connhandle_p, sizeof(sendResponseParms.connectionHandle));

            sendResponseParms.responseDataLength = *rspdatalen_p;
            sendResponseParms.cicsParms_p = cicsParms_p;
            sendResponseParms.rc_p  = &sendRsp_rc;
            sendResponseParms.rsn_p = &sendRsp_rsn;

            // now call the sendResponse stub
            int stubRC = wolaSendResponse_stub(&(bindInfo_p->clientBindToken),
                                               bindInfo_p->clientServiceIndexes.wola_sendResponse,
                                               &sendResponseParms,
                                               bindInfo_p->angelAnchor_p);

            // TODO: Check stubRC
        } else {
            sendRsp_rc = WOLA_RC_ERROR8;
            sendRsp_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }

    }

    *rc_p  = (int)sendRsp_rc;
    *rsn_p = (int)sendRsp_rsn;
}

/**
*  Client Connection Release
*  This is called from bboascnr.s
*/
int ClientConnectionRelease(char* connhandle_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {

    WOLAConnectionReleaseParms_t connReleaseParms;

    unsigned int conn_rc = 0;
    unsigned int conn_rsn = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connhandle_p, registername);

    if (regnameRC != 0) {
        //send the error back
        conn_rc  = WOLA_RC_ERROR8;
        conn_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {

            memcpy(&connReleaseParms.connectionHandle, connhandle_p, sizeof(connReleaseParms.connectionHandle));

            connReleaseParms.cicsParms_p = cicsParms_p;
            connReleaseParms.rc_p  = &conn_rc;
            connReleaseParms.rsn_p = &conn_rsn;

            // now call the connectionRelease stub
            int stubRC = wolaConnectionRelease_stub(&(bindInfo_p->clientBindToken),
                                                    bindInfo_p->clientServiceIndexes.wola_releaseConnection,
                                                    &connReleaseParms,
                                                    bindInfo_p->angelAnchor_p);

            // TODO: Check stubRC
        } else {
            conn_rc = WOLA_RC_ERROR8;
            conn_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }
    }

    *rc_p  = (int)conn_rc;
    *rsn_p = (int)conn_rsn;
}

/**
*  Client Connection Get
*  This is called from bboascng.s
*/
int ClientConnectionGet(char* registername, char* connhandle_p, int* waittime_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {

    WOLAConnectionGetParms_t connGetParms;

    unsigned int conn_rc = 0;
    unsigned int conn_rsn = 0;

    volatile WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
    if (bindInfo_p != NULL) {
        memcpy(&connGetParms.registerName, registername, sizeof(connGetParms.registerName));
        memcpy(&connGetParms.connectionHandle_p, &connhandle_p, sizeof(connGetParms.connectionHandle_p));

        connGetParms.waitTime = *waittime_p;
        connGetParms.cicsParms_p = cicsParms_p;
        connGetParms.rc_p  = &conn_rc;
        connGetParms.rsn_p = &conn_rsn;

        // now call the connectionget stub
        int stubRC = wolaConnectionGet_stub((void*)(&(bindInfo_p->clientBindToken)),
                                            bindInfo_p->clientServiceIndexes.wola_getConnection,
                                            &connGetParms,
                                            bindInfo_p->angelAnchor_p);

        /* We think a rebind is required.  We also think that the target server is back up. */
        if ((conn_rc == WOLA_RC_SEVERE12) && (conn_rsn == WOLA_RSN_INTERNAL_REBIND_REQUIRED)) {
            int rebindRC = doRebind((WolaClientBindInformation_t*)bindInfo_p, registername);
            if (rebindRC == WOLA_CLIENT_REBIND_RC_OK) {
                conn_rc = 0;
                conn_rsn = 0;
                stubRC = wolaConnectionGet_stub((void*)(&(bindInfo_p->clientBindToken)),
                                                bindInfo_p->clientServiceIndexes.wola_getConnection,
                                                &connGetParms,
                                                bindInfo_p->angelAnchor_p);
                if ((conn_rc != 0) && (conn_rsn >= WOLA_RSN_INTERNAL_BASE) && (conn_rsn <= WOLA_RSN_INTERNAL_END)) {
                    conn_rc = WOLA_RC_ERROR8;
                    conn_rsn = WOLA_RSN_INTERNAL_AFTER_REBIND;
                }
            } else {
                /* Our rebind failed.  Tell the caller.  There may be nothing they can do */
                /* but at least they will have a specific reason code.                    */
                conn_rc = WOLA_RC_ERROR8;
                conn_rsn = WOLA_RSN_REBIND_FAILED;
            }
        }
    } else if (isTWasRegistration(registername, NULL) == TRUE) {
        // If this is a tWAS registration, tell the previous layer to try calling the tWAS stub.
        // The previous layer is either bboascng.s or bboatrue.cicsasm.
        conn_rc = WOLA_RC_ERROR8;
        conn_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
    }else {
        conn_rc  = WOLA_RC_ERROR8;
        conn_rsn = WOLA_RSN_REGISTER;
    }

    *rc_p  = (int)conn_rc;
    *rsn_p = (int)conn_rsn;
}

/**
*  Client Send Request
*  This is called from bboassrq.s
*/
int ClientSendRequest(char* connectionhdl_p, int* reqtype_p, char* servicename, int* servicenamelen_p, char* reqdata_p, int* reqdatalen_p,
                      int* async_p, int* rspdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {
    WOLASendRequestParms_t sendReqParms;

    unsigned int sreq_rc = 0;
    unsigned int sreq_rsn = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connectionhdl_p, registername);

    if (regnameRC != 0) {
        //send the error back
        sreq_rc  = WOLA_RC_ERROR8;
        sreq_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {
            memcpy(&sendReqParms.connectionHandle, connectionhdl_p, sizeof(sendReqParms.connectionHandle));
            memcpy(&sendReqParms.requestServiceName_p, &servicename, sizeof(sendReqParms.requestServiceName_p));
            memcpy(&sendReqParms.requestData_p, &reqdata_p, sizeof(sendReqParms.requestData_p));

            sendReqParms.requestType = *reqtype_p;
            sendReqParms.requestServiceNameLength = *servicenamelen_p;
            sendReqParms.requestDataLength = *reqdatalen_p;
            sendReqParms.async = *async_p;

            // Authorized PC takes an 8 byte length, we were passed a 4 byte length.
            // TODO: When we support 64 bit callers, we'll need to do something differently here...
            unsigned long long localRespLen;
            sendReqParms.responseDataLength_p = &localRespLen;

            sendReqParms.cicsParms_p = cicsParms_p;
            sendReqParms.rc_p  = &sreq_rc;
            sendReqParms.rsn_p = &sreq_rsn;

            // now call the sendRequest stub
            int stubRC = wolaSendRequest_stub(&(bindInfo_p->clientBindToken),
                                              bindInfo_p->clientServiceIndexes.wola_sendRequest,
                                              &sendReqParms,
                                              bindInfo_p->angelAnchor_p);

            *rspdatalen_p = (unsigned int)localRespLen;

            // TODO: Check stubRC
        } else {
            sreq_rc = WOLA_RC_ERROR8;
            sreq_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }
    }

    *rc_p  = (int)sreq_rc;
    *rsn_p = (int)sreq_rsn;
}

/**
*  Client Get Context
*  This is called from bboasgtx.s
*/
int ClientGetContext(char* connhandle_p, char* msgCtx_p, int* msgCtxLen_p,  struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p, int* rval_p) {
    WOLAGetContextParms_t getContextParms;

    unsigned int get_rc = 0;
    unsigned int get_rsn = 0;
    unsigned int get_rval = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connhandle_p, registername);

    if (regnameRC != 0) {
        //send the error back
        get_rc  = WOLA_RC_ERROR8;
        get_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {
            memcpy(&getContextParms.connectionHandle, connhandle_p, sizeof(getContextParms.connectionHandle));
            memcpy(&getContextParms.messageContext_p, &msgCtx_p, sizeof(getContextParms.messageContext_p));

            getContextParms.messageContextLength = *msgCtxLen_p;
            getContextParms.cicsParms_p = cicsParms_p;
            getContextParms.rc_p  = &get_rc;
            getContextParms.rsn_p = &get_rsn;
            getContextParms.rv_p  = &get_rval;


            // now call the GetData stub
            int stubRC = wolaGetContext_stub(&(bindInfo_p->clientBindToken),
                                             bindInfo_p->clientServiceIndexes.wola_getContext,
                                             &getContextParms,
                                             bindInfo_p->angelAnchor_p);
 
            // TODO: Check stubRC
        } else {
            get_rc = WOLA_RC_ERROR8;
            get_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }
    }

    *rc_p  = (int)get_rc;
    *rsn_p = (int)get_rsn;
    *rval_p = (int)get_rval;
}

/**
*  Client Get Data
*  This is called from bboasget.s
*/
int ClientGetData(char* connhandle_p, char* msgdata_p, int* msgdatalen_p,  struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p, int* rval_p) {

    WOLAGetDataParms_t getDataParms;

    unsigned int get_rc = 0;
    unsigned int get_rsn = 0;
    unsigned int get_rval = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connhandle_p, registername);

    if (regnameRC != 0) {
        //send the error back
        get_rc  = WOLA_RC_ERROR8;
        get_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {
            memcpy(&getDataParms.connectionHandle, connhandle_p, sizeof(getDataParms.connectionHandle));
            memcpy(&getDataParms.messageData_p, &msgdata_p, sizeof(getDataParms.messageData_p));

            getDataParms.messageDataLength = *msgdatalen_p;
            getDataParms.cicsParms_p = cicsParms_p;
            getDataParms.rc_p  = &get_rc;
            getDataParms.rsn_p = &get_rsn;
            getDataParms.rv_p  = &get_rval;

            // now call the GetData stub
            int stubRC = wolaGetData_stub(&(bindInfo_p->clientBindToken),
                                          bindInfo_p->clientServiceIndexes.wola_getData,
                                          &getDataParms,
                                          bindInfo_p->angelAnchor_p);

            // TODO: Check stubRC.
        } else {
            get_rc = WOLA_RC_ERROR8;
            get_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }
    }

    *rc_p  = (int)get_rc;
    *rsn_p = (int)get_rsn;
    *rval_p = (int)get_rval;
}

/**
*  Client Receive Response Data Length
*  This is called from bboasrcl.s
*/
int ClientReceiveResponseLength(char* connhandle_p, int* async_p, int* rspdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {

    WOLAReceiveResponseLengthParms_t receiveResponseParms;

    unsigned int rcvRsp_rc = 0;
    unsigned int rcvRsp_rsn = 0;
    unsigned long long rspdatalength = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connhandle_p, registername);

    if (regnameRC != 0) {
        //send the error back
        rcvRsp_rc  = WOLA_RC_ERROR8;
        rcvRsp_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {
            memcpy(&receiveResponseParms.connectionHandle, connhandle_p, sizeof(receiveResponseParms.connectionHandle));
            receiveResponseParms.responseDataLength_p  = &rspdatalength;

            receiveResponseParms.async = *async_p;
            receiveResponseParms.cicsParms_p = cicsParms_p;
            receiveResponseParms.rc_p  = &rcvRsp_rc;
            receiveResponseParms.rsn_p = &rcvRsp_rsn;

            // now call the receiveResponseLength stub
            int stubRC = wolaReceiveResponseLength_stub(&(bindInfo_p->clientBindToken),
                                                        bindInfo_p->clientServiceIndexes.wola_receiveResponseLength,
                                                        &receiveResponseParms,
                                                        bindInfo_p->angelAnchor_p);
        } else {
            rcvRsp_rc = WOLA_RC_ERROR8;
            rcvRsp_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }
    }

    *rspdatalen_p = (unsigned int)rspdatalength;
    *rc_p  = (int)rcvRsp_rc;
    *rsn_p = (int)rcvRsp_rsn;
}

/**
*  Client Receive Request Any
*  This is called from bboasrca.s
*/
int ClientReceiveRequestAny(char* registername, char* connhandle_p, char* servicename, int* servicenamelen_p, int* reqdatalen_p,
                            int* waittime_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {
    WOLAReceiveRequestAnyParms_t rcvReqAnyParms;

    unsigned int rcvAny_rc = 0;
    unsigned int rcvAny_rsn = 0;
    unsigned long long reqdatalength = 0;

    volatile WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
    if (bindInfo_p != NULL) {
        memcpy(&rcvReqAnyParms.registerName, registername, sizeof(rcvReqAnyParms.registerName));
        memcpy(&rcvReqAnyParms.requestServiceName_p, &servicename, sizeof(rcvReqAnyParms.requestServiceName_p));
        memcpy(&rcvReqAnyParms.connectionHandle_p, &connhandle_p, sizeof(rcvReqAnyParms.connectionHandle_p));

        //requestServiceNameLength_p is an int* 'coz it is an input/output paramater
        memcpy(&rcvReqAnyParms.requestServiceNameLength_p, &servicenamelen_p, sizeof(rcvReqAnyParms.requestServiceNameLength_p));

        rcvReqAnyParms.requestDataLength_p = &reqdatalength;
        rcvReqAnyParms.waitTime = *waittime_p;
        rcvReqAnyParms.cicsParms_p = cicsParms_p;
        rcvReqAnyParms.rc_p  = &rcvAny_rc;
        rcvReqAnyParms.rsn_p = &rcvAny_rsn;

        // now call the ReceiveRequestAny stub
        int stubRC = wolaReceiveRequestAny_stub((void*)(&(bindInfo_p->clientBindToken)),
                                                bindInfo_p->clientServiceIndexes.wola_receiveRequestAny,
                                                &rcvReqAnyParms,
                                                bindInfo_p->angelAnchor_p);

        if ((rcvAny_rc == WOLA_RC_SEVERE12) && (rcvAny_rsn == WOLA_RSN_INTERNAL_REBIND_REQUIRED)) {
            int rebindRC = doRebind((WolaClientBindInformation_t*)bindInfo_p, registername);
            if (rebindRC == WOLA_CLIENT_REBIND_RC_OK) {
                rcvAny_rc = 0;
                rcvAny_rsn = 0;
                stubRC = wolaReceiveRequestAny_stub((void*)(&(bindInfo_p->clientBindToken)),
                                                        bindInfo_p->clientServiceIndexes.wola_receiveRequestAny,
                                                        &rcvReqAnyParms,
                                                        bindInfo_p->angelAnchor_p);
                if ((rcvAny_rc != 0) && (rcvAny_rsn >= WOLA_RSN_INTERNAL_BASE) && (rcvAny_rsn <= WOLA_RSN_INTERNAL_END)) {
                    rcvAny_rc = WOLA_RC_ERROR8;
                    rcvAny_rsn = WOLA_RSN_INTERNAL_AFTER_REBIND;
                }
            } else {
                /* Our rebind failed.  Tell the caller.  There may be nothing they can do */
                /* but at least they will have a specific reason code.                    */
                rcvAny_rc = WOLA_RC_ERROR8;
                rcvAny_rsn = WOLA_RSN_REBIND_FAILED;
            }

        }
    } else if (isTWasRegistration(registername, NULL) == TRUE) {
        // If this is a tWAS registration, tell the previous layer to try calling the tWAS stub.
        // The previous layer is either bboasrca.s or bboatrue.cicsasm.
        rcvAny_rc = WOLA_RC_ERROR8;
        rcvAny_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
    }else {
        rcvAny_rc  = WOLA_RC_ERROR8;
        rcvAny_rsn = WOLA_RSN_REGISTER;
    }

    *reqdatalen_p = (unsigned int)reqdatalength;
    *rc_p  = (int)rcvAny_rc;
    *rsn_p = (int)rcvAny_rsn;
}

/**
*  Client Receive Request Specific
*  This is called from bboasrcs.s
*/
int ClientReceiveRequestSpecific(char* connectionhdl_p, char* servicename, int* servicenamelen_p, int* reqdatalen_p,
                                 int* async_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {
    WOLAReceiveRequestSpecificParms_t rcvReqSpecParms;

    unsigned int rcv_rc = 0;
    unsigned int rcv_rsn = 0;
    unsigned long long reqdatalength = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connectionhdl_p, registername);

    if (regnameRC != 0) {
        //send the error back
        rcv_rc  = WOLA_RC_ERROR8;
        rcv_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {
            memcpy(&rcvReqSpecParms.connectionHandle, connectionhdl_p, sizeof(rcvReqSpecParms.connectionHandle));
            memcpy(&rcvReqSpecParms.requestServiceName_p, &servicename, sizeof(rcvReqSpecParms.requestServiceName_p));
            memcpy(&rcvReqSpecParms.requestServiceNameLength_p, &servicenamelen_p, sizeof(rcvReqSpecParms.requestServiceNameLength_p));

            rcvReqSpecParms.requestDataLength_p = &reqdatalength;
            rcvReqSpecParms.async = *async_p;
            rcvReqSpecParms.cicsParms_p = cicsParms_p;
            rcvReqSpecParms.rc_p  = &rcv_rc;
            rcvReqSpecParms.rsn_p = &rcv_rsn;

            // now call the REceiveRequestSpecific stub
            int stubRC = wolaReceiveRequestSpecific_stub(&(bindInfo_p->clientBindToken),
                                                         bindInfo_p->clientServiceIndexes.wola_receiveRequestSpecific,
                                                         &rcvReqSpecParms,
                                                         bindInfo_p->angelAnchor_p);

            // TODO: Check stubRC
        } else {
            rcv_rc = WOLA_RC_ERROR8;
            rcv_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }
    }

    *reqdatalen_p = (unsigned int)reqdatalength;
    *rc_p  = (int)rcv_rc;
    *rsn_p = (int)rcv_rsn;
}

/**
*  Client Send Response Exception
*  This is called from bboassrx.s
*/
int ClientSendResponseException(char* connhandle_p, char* excRspdata_p, int* excRspdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {

    WOLASendResponseExceptionParms_t sendResponseExcParms;

    unsigned int sendRsp_rc = 0;
    unsigned int sendRsp_rsn = 0;

    char registername[12];

    // Get the register name from the connection handle.
    int regnameRC = getRegisterNameFromConnHdl((WolaClientConnectionHandle_t*)connhandle_p, registername);

    if (regnameRC != 0) {
        //send the error back
        sendRsp_rc  = WOLA_RC_ERROR8;
        sendRsp_rsn = WOLA_RSN_INVALID_CONN_HDL;
    } else {
        WolaClientBindInformation_t* bindInfo_p = retrieveWolaClientBindNameToken(registername);
        if (bindInfo_p != NULL) {
            memcpy(&sendResponseExcParms.excResponseData_p, &excRspdata_p, sizeof(sendResponseExcParms.excResponseData_p));
            memcpy(&sendResponseExcParms.connectionHandle, connhandle_p, sizeof(sendResponseExcParms.connectionHandle));

            sendResponseExcParms.excResponseDataLength = *excRspdatalen_p;
            sendResponseExcParms.cicsParms_p = cicsParms_p;
            sendResponseExcParms.rc_p  = &sendRsp_rc;
            sendResponseExcParms.rsn_p = &sendRsp_rsn;

            // now call the sendResponse stub
            int stubRC = wolaSendResponseException_stub(&(bindInfo_p->clientBindToken),
                                                        bindInfo_p->clientServiceIndexes.wola_sendResponseException,
                                                        &sendResponseExcParms,
                                                        bindInfo_p->angelAnchor_p);

            // TODO: check stubRC.
        } else {
                sendRsp_rc = WOLA_RC_ERROR8;
                sendRsp_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
        }
    }

    *rc_p  = (int)sendRsp_rc;
    *rsn_p = (int)sendRsp_rsn;
}

/**
 * Client Connection Get Info
 * This is called from bboasinf.s
 */
int ClientInfoGet(char* registername, char* wolaGroup, char* wolaName2, char* wolaName3, struct bboaconn* connInfo_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p) {

    struct __csysenv_s mysysenv;
    memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
    mysysenv.__cseversion = __CSE_VERSION_2;

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    mysysenv.__csesubpool = 0;
    mysysenv.__cseheap64usertoken = getTaskProblemStateUserToken();

    // Create a Metal C environment.
    myenvtkn = (void * ) __cinit(&mysysenv);

    LibertyBindToken_t bindToken;

    unsigned int conn_rc = 0;
    unsigned int conn_rsn = 0;
    // lookup the register name token
    struct register_name_token_map reg_token_map;
    WolaRegistration_t* registration_p;
    int regtokenrc = -1;

    regtokenrc = getRegisterNameToken(registername, (char*)&reg_token_map);
    if ( regtokenrc == 0) {
        registration_p = reg_token_map.registration_p;

        memcpy(wolaGroup, registration_p->wolaAnchor_p->wolaGroup,
               sizeof(registration_p->wolaAnchor_p->wolaGroup));
        memcpy(wolaName2, registration_p->serverRegistration_p->serverNameSecondPart,
               sizeof(registration_p->serverRegistration_p->serverNameSecondPart));
        memcpy(wolaName3, registration_p->serverRegistration_p->serverNameThirdPart,
               sizeof(registration_p->serverRegistration_p->serverNameThirdPart));

        memcpy(connInfo_p->aconn_eye, "BBOACONN", sizeof(connInfo_p->aconn_eye));
        connInfo_p->aconn_version = 1;
        connInfo_p->aconn_size = sizeof(*connInfo_p);
        connInfo_p->aconn_reserved = 0;
        connInfo_p->aconn_min = registration_p->minConns;
        connInfo_p->aconn_max = registration_p->maxConns;
        connInfo_p->aconn_info_state = registration_p->connPoolState;
        connInfo_p->aconn_info_active = registration_p->activeConnCount;
        memset(connInfo_p->aconn_reserved2, 0, sizeof(connInfo_p->aconn_reserved2));
    } else if (isTWasRegistration(registername, myenvtkn) == TRUE) {
        // If this is a tWAS registration, tell the previous layer to try calling the tWAS stub.
        // The previous layer is either bboascng.s or bboatrue.cicsasm.
        conn_rc = WOLA_RC_ERROR8;
        conn_rsn = WOLA_RSN_INTERNAL_TRY_TWAS_STUB;
    }else {
        conn_rc  = WOLA_RC_ERROR8;
        conn_rsn = WOLA_RSN_REGISTER;
    }

    *rc_p  = (int)conn_rc;
    *rsn_p = (int)conn_rsn;

    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);
}
