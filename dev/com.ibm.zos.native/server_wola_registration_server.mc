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

/**
 * @file
 *
 * Assorted authorized routines used by the WOLA code for accessing/manipulating
 * the BBOARGE chain inside the BBOASHR.
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_plo.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/server_process_data.h"
#include "include/server_wola_shared_memory_anchor.h"
#include "include/server_wola_registration_server.h"
#include "include/util_registry.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_WOLA_REGISTRATION_SERVER


/**
 * Map out the RegistryDataArea to take just the bboarge_p.
 */
struct RegistryDataArea_BboargeMap {
    void * bboarge_p;
    char unused[sizeof(RegistryDataArea)-sizeof(void *)];
};

/**
 * Allocate a RegistryToken for the given bboarge_p.  The RegistryToken is
 * passed back to the Java caller for later use under pc_deactivateWolaRegistration.
 *
 * We don't pass back the bboarge_p directly because it lives in protected 
 * shared memory area that we don't want to give unauthorized callers direct 
 * access to.
 *
 * @param bboarge_p - The pointer to BBOarge
 * @param registry_token - Output - The RegistryToken
 *
 * @return the RC from util_registry::registryPut
 */
int allocateRegistryTokenForBboarge( void * bboarge_p, RegistryToken * registry_token ) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(31),
                    "allocateRegistryTokenForBboarge entry",
                    TRACE_DATA_PTR(bboarge_p, "bboarge_p"),
                    TRACE_DATA_END_PARMS);
    }

    struct RegistryDataArea_BboargeMap dataArea;
    memset(&dataArea, 0, sizeof(RegistryDataArea));
    dataArea.bboarge_p = bboarge_p;

    // Put it in there.
    int rc = registryPut(WOLATKN, (RegistryDataArea *)&dataArea, registry_token);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(32),
                    "allocateRegistryTokenForBboarge exit",
                    TRACE_DATA_INT(rc, "return code"),
                    TRACE_DATA_RAWDATA(sizeof(RegistryToken), registry_token, "registry token"),
                    TRACE_DATA_END_PARMS);
    }

    return rc;
}

/**
 * Get the bboarge_p from the given registry token.
 *
 * @param registry_token - The registry token
 * @param rc - Output - the rc from util_registry::registryGetAndSetUsed
 *
 * @return the bboarge_p from the registry token.
 */
void * getBboargeFromRegistryToken(RegistryToken * registry_token, int * rc) {

    struct RegistryDataArea_BboargeMap dataArea;

    *rc = registryGetAndSetUsed(registry_token, (RegistryDataArea *)&dataArea);

    int setUnused_rc = -1;

    if (*rc == 0) {
        // Note: not checking RC.  No need.  It gets traced, just in case.
        setUnused_rc = registrySetUnused(registry_token, 0);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(71),
                    "getBboargeFromRegistryToken",
                    TRACE_DATA_INT(*rc, "registryGetAndSetUsed rc"),
                    TRACE_DATA_INT(setUnused_rc, "registrySetUnused rc"),
                    TRACE_DATA_RAWDATA(sizeof(struct RegistryDataArea_BboargeMap), &dataArea, "RegistryDataArea"),
                    TRACE_DATA_PTR(dataArea.bboarge_p, "bboarge_p (in RegistryDataArea)"),
                    TRACE_DATA_END_PARMS);
    }

    if (*rc == 0) {
        return dataArea.bboarge_p;
    } else {
        return NULL;
    }
}

/**
 * Find a client registration in a given shared memory anchor by a given name.
 *
 * @param registrationName - char[16] - the name of the registration to find 
 * @param bboashr_p - The BBOASHR in which to find the registration
 *
 * @return regPtr - The found registration or NULL if one was not found
 */
WolaRegistration_t * findClientRegistrationByName(unsigned char * registrationName, struct wolaSharedMemoryAnchor * bboashr_p){

    struct wolaRegistration* regPtr = bboashr_p->firstRge_p;

    // Note: can't use strcmp - registrationName isn't guaranteed to be null termed.
    while(regPtr != NULL && memcmp(registrationName, regPtr->registrationName, sizeof(regPtr->registrationName)) != 0){
        regPtr = regPtr->nextRegistration_p;
    }


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(91),
                    "findClientRegistrationByName",
                    TRACE_DATA_RAWDATA(16, registrationName, "registration name"),
                    TRACE_DATA_RAWDATA( ((regPtr != NULL) ? sizeof(struct wolaRegistration) : 0), regPtr, "wolaRegistration element"),
                    TRACE_DATA_END_PARMS);
    }

    return regPtr;
}

/**
 *
 * @param bboarge_p - The BBOARGE area to initialize
 * @param wola_name2 - The 2nd part of the WOLA 3-part identity
 * @param wola_name3 - The 3rd part of the WOLA 3-part identity
 *
 * @return bboarge_p
 */
WolaRegistration_t * initializeServerBboarge(WolaRegistration_t * bboarge_p, 
                                             char * wola_name2, 
                                             char * wola_name3, 
                                             struct wolaSharedMemoryAnchor * bboashr_p) {
    
    memset( bboarge_p, 0, sizeof(WolaRegistration_t) );

    memcpy(bboarge_p->eye, BBOARGE_EYE, sizeof(bboarge_p->eye));
    bboarge_p->size = sizeof(WolaRegistration_t);
    bboarge_p->flags.serverRegistration = 1;
    bboarge_p->version = BBOARGE_VERSION_2;
    bboarge_p->this_p = bboarge_p;

    memcpy( bboarge_p->registrationName, "LibertyServer   ", sizeof(bboarge_p->registrationName) );
    memcpy( bboarge_p->serverNameSecondPart, wola_name2, sizeof(bboarge_p->serverNameSecondPart) );
    memcpy( bboarge_p->serverNameThirdPart, wola_name3, sizeof(bboarge_p->serverNameThirdPart) );     

    bboarge_p->wolaAnchor_p = bboashr_p;

    //Set the service queues state to ready
    bboarge_p->serviceQueuesState = BBOARGE_SERVICE_QUEUE_READY;

    // memcpy( serverStartSTCK[8] ... )// TODO

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(41),
                    "initializeServerBboarge",
                    TRACE_DATA_RAWDATA(sizeof(WolaRegistration_t), bboarge_p, "bboarge_p"),
                    TRACE_DATA_END_PARMS);
    }

    return bboarge_p;
}

/**
 * Copy the address space SToken into the given dest.
 *
 * @param dest - a char[8] to contain the stoken
 */
static void copySToken(char * dest) {
    psa* psa_p = NULL;
    ascb* ascb_p = (ascb*) psa_p->psaaold;
    assb* assb_p = (assb*) ascb_p->ascbassb;

    memcpy(dest, &(assb_p->assbstkn), 8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(11),
                    "copySToken",
                    TRACE_DATA_RAWDATA(8, dest, "dest"),
                    TRACE_DATA_END_PARMS);
    }
}

/**
 * Activation involves:
 *
 * 1) set the 'active' flag  (done last)
 * 2) set a new STCK to tell clients we have a new instance
 * 3) set fields specific to this server instance (stoken, ascb, etc)
 * 4) set configurable fields (propAcee, etc)
 *
 * @param bboarge_p - The BBOARGE to activate
 * @param useCicsTaskUserId - Flag indicating whether we're going to allow CICS to pass an alternate ACEE
 *
 * @return bboarge_p
 */
WolaRegistration_t * activateBboarge(WolaRegistration_t * bboarge_p, unsigned char useCicsTaskUserId) {

    copySToken(bboarge_p->stoken);
    bboarge_p->ascb_p = (ascb *) ((psa *)NULL)->psaaold;

    // Clients use the STCK to tell when connection handles become invalid
    // due to the server bouncing.
    __stck(&(bboarge_p->stckLastStateChange));
    bboarge_p->flags.active = 1;
    bboarge_p->flags.propAceeFromTrueIntoServer = useCicsTaskUserId;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(51),
                    "activateBboarge",
                    TRACE_DATA_RAWDATA(sizeof(WolaRegistration_t), bboarge_p, "bboarge_p"),
                    TRACE_DATA_END_PARMS);
    }

    return bboarge_p;
}

/**
 * Deactivation involves
 *
 * 1) reset the 'active' flag
 * 2) set a new STCK to tell clients to look for a new instance.
 *
 * @param bboarge_p  - The BBOARGE to deactivate
 *
 * @return bboarge_p
 */
WolaRegistration_t * deactivateBboarge( WolaRegistration_t * bboarge_p) {
    bboarge_p->flags.active = 0;
    __stck(&(bboarge_p->stckLastStateChange));

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(21),
                    "deactivateBboarge",
                    TRACE_DATA_RAWDATA(sizeof(WolaRegistration_t), bboarge_p, "bboarge_p"),
                    TRACE_DATA_END_PARMS);
    }

    return bboarge_p;
}

/**
 * If the server process data has a registration set, deactivate that registration
 * and clear the pointer.
 *
 * @param spd_p The server process data to clean up
 */
void removeBboargeFromSpd(server_process_data * spd_p) {
    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(61),
                    "removeBboargeFromSpd",
                    TRACE_DATA_PTR(spd_p->wola_bboarge_p, "spd_p->wola_bboarge_p"),
                    TRACE_DATA_END_PARMS);
    }

    if (spd_p->wola_bboarge_p != NULL) {
        deactivateBboarge(spd_p->wola_bboarge_p);
        spd_p->wola_bboarge_p = NULL;
    }
}
