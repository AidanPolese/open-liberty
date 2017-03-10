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

#ifndef _BBOZ_SERVER_WOLA_SHARED_MEMORY_ANCHOR_SERVER_H
#define _BBOZ_SERVER_WOLA_SHARED_MEMORY_ANCHOR_SERVER_H

#include "server_wola_shared_memory_anchor.h"
#include "util_registry.h"

/**
 * @file Server-side WOLA shared memory anchor functions.
 */

/**
 *
 * IEAN4CR: http://pic.dhe.ibm.com/infocenter/zos/v2r1/index.jsp?topic=%2Fcom.ibm.zos.v2r1.ieaa200%2Fiean4cr.htm
 *
 * @param wola_group - the WOLA group name
 * @param bboashr_p - the shared memory area address
 *
 * @return The rc from iean4cr.
 */
int createBboashrNameToken( char * wola_group, void * bboashr_p ) ;

/**
 * Initialize the BBOASHR structure.
 *
 * This method should only be called upon first creating the WOLA shared memory area
 * and BBOASHR anchor.
 *
 * @param bboashr_p - The shared memory area
 * @param wola_group - The wola group name.
 */
void initializeBboashr( void * bboashr_p, char * wola_group ) ;

/**
 * Allocate a RegistryToken for the given bboashr_p.  The RegistryToken is
 * passed back to the Java caller for later use under pc_detachFromBboashr.
 * We can't pass back the actual address of bboashr_p, because then the detach
 * routine would take the pointer, and we'd effectively be giving the Java caller
 * access to an authorized routine that they could use to detach from any old
 * piece of shared memory.  Not kosher.
 *
 * @param bboashr_p - The pointer to BBOASHR
 * @param registry_token - Output - The RegistryToken
 *
 * @return the RC from util_registry::registryPut
 */
int allocateRegistryTokenForBboashr( void * bboashr_p, RegistryToken * registry_token ) ;

/**
 * Get the bboashr_p from the given registry token.
 *
 * @param registry_token - The registry token
 * @param rc - Output - the rc from util_registry::registryGetAndSetUsed
 *
 * @return the bboashr_p from the registry token.
 */
void * getBboashrFromRegistryToken(RegistryToken * registry_token, int * rc) ;


#endif
