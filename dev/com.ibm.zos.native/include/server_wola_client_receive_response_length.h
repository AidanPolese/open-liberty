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

#ifndef SERVER_WOLA_CLIENT_RECEIVE_RESPONSE_LENGTH_H_
#define SERVER_WOLA_CLIENT_RECEIVE_RESPONSE_LENGTH_H_

#include "server_wola_connection_handle.h"

/**
 * Receive response length for the requested connection.
 *
 * @param wolaClientConnectionHandle_p  Client connection handle.
 * @param waitForData                   Set to one to wait for data.
 * @param responseDataLength_p          Pointer to output response data length.
 * @param reasonCode_p                  Pointer to output reason code.
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaReceiveResponseLengthCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, unsigned int waitForData,
                                             unsigned long long* responseDataLength_p, unsigned int* reasonCode_p);


#endif /* SERVER_WOLA_CLIENT_RECEIVE_RESPONSE_LENGTH_H_ */
