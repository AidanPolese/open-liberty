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

/** @file
 * Defines server-side WOLA registration functions.
 */

#ifndef _BBOZ_SERVER_WOLA_REGISTRATION_SERVER_H
#define _BBOZ_SERVER_WOLA_REGISTRATION_SERVER_H

#include "server_process_data.h"
#include "server_wola_registration.h"
#include "server_wola_shared_memory_anchor.h"
#include "util_registry.h"

/**
 * Find a client registration in a given shared memory anchor by a given name.
 *
 * @param registrationName - The name of the registration to find
 * @param bboashr_p - The BBOASHR in which to find the registration
 *
 * @return regPtr - The found registration or NULL if one was not found
 */
WolaRegistration_t * findClientRegistrationByName(unsigned char registrationName[16], struct wolaSharedMemoryAnchor * bboashr_p);

/**
 * Allocate a RegistryToken for the given bboarge_p.  The RegistryToken is
 * passed back to the Java caller for later use under pc_deactivateWolaRegistration.
 *
 * We don't pass back the bboarge_p directly because it lives in protected 
 * shared memory area that we don't want to give unauthorized callers direct 
 * access to. Unauthorized callers are restricted to read-only access of the storage.
 *
 * @param bboarge_p - The pointer to BBOarge
 * @param registry_token - Output - The RegistryToken
 *
 * @return the RC from util_registry::registryPut
 */
int allocateRegistryTokenForBboarge( void * bboarge_p, RegistryToken * registry_token ) ;

/**
 * Get the bboarge_p from the given registry token.
 *
 * @param registry_token - The registry token
 * @param rc - Output - the rc from util_registry::registryGetAndSetUsed
 *
 * @return the bboarge_p from the registry token.
 */
void * getBboargeFromRegistryToken(RegistryToken * registry_token, int * rc) ;

/**
 *
 * @param bboarge_p - The BBOARGE area to initialize
 * @param wola_name2 - The 2nd part of the WOLA 3-part identity
 * @param wola_name3 - The 3rd part of the WOLA 3-part identity
 * @param bboashr_p - The BBOASHR anchor addr
 *
 * @return bboarge_p
 */
WolaRegistration_t * initializeServerBboarge(WolaRegistration_t * bboarge_p, 
                                             char * wola_name2, 
                                             char * wola_name3, 
                                             struct wolaSharedMemoryAnchor * bboashr_p) ;

/**
 * Activation involves:
 *
 * 1) set the 'active' flag  (done last)
 * 2) set fields specific to this server instance (stoken, ascb, etc)
 * 3) set configurable fields (propAcee, etc)
 *
 * @param bboarge_p - The BBOARGE to activate
 * @param useCicsTaskUserId - Flag indicating whether we're going to allow CICS to pass an alternate ACEE
 *
 * @return bboarge_p
 */
WolaRegistration_t * activateBboarge(WolaRegistration_t * bboarge_p, unsigned char useCicsTaskUserId) ;

/**
 * Deactivation involves
 *
 * 1) reset the 'active' flag
 *
 * @param bboarge_p  - The BBOARGE to deactivate
 *
 * @return bboarge_p
 */
WolaRegistration_t * deactivateBboarge( WolaRegistration_t * bboarge_p) ;

/**
 * If the server process data has a registration set, deactivate that registration
 * and clear the pointer.
 *
 * @param spd_p The server process data to clean up
 */
void removeBboargeFromSpd(server_process_data * spd_p);

#endif
