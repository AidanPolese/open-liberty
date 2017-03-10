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

#ifndef SERVER_WOLA_CLIENT_CONNECTION_GET_H_
#define SERVER_WOLA_CLIENT_CONNECTION_GET_H_

#include "gen/bboapc1p.h"
#include "server_wola_connection_handle.h"
#include "server_wola_registration.h"

/**
 * Get a connection.
 *
 * @param regEntry_p   Pointer to registration.
 * @param waitTime     An integer containing the number of seconds to wait for the
 *                     connection to complete before returning
 *                     a connection unavailable reason code. A value of 0 implies
 *                     there is no wait time and the API waits indefinitely.
 * @param handle_p     Output area to get client connection handle.
 * @param cicsParms_p  A pointer to CICS parameters in the client's key.
 * @param reasonCode_p Output area to get the reason code.
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaConnectionGetCommon(WolaRegistration_t* regEntry_p, unsigned int waitTime,
                                     WolaClientConnectionHandle_t* handle_p,
                                     struct bboapc1p* cicsParms_p,
                                     unsigned int*  reasonCode_p);


#endif /* SERVER_WOLA_CLIENT_CONNECTION_GET_H_ */
