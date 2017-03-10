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

#ifndef SERVER_NATIVE_SERVICE_TRACKER_H_
#define SERVER_NATIVE_SERVICE_TRACKER_H_

#include "server_function_module_stub.h"

/**
 * Get the server function stub vector tables.
 */
const server_function_stubs* getServerFunctionStubs();

/**
 * Get the authorized server function stubs.
 */
const server_authorized_function_stubs* getServerAuthorizedFunctionStubs();

/**
 * Get the unauthorized server function stubs.
 */
const server_unauthorized_function_stubs* getServerUnauthorizedFunctionStubs();


#endif /* SERVER_NATIVE_SERVICE_TRACKER_H_ */
