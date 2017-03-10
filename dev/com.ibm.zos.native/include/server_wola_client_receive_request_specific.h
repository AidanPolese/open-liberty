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
#ifndef SERVER_WOLA_CLIENT_RECEIVE_REQUEST_SPECIFIC_H_
#define SERVER_WOLA_CLIENT_RECEIVE_REQUEST_SPECIFIC_H_

#include "server_wola_connection_handle.h"

/**
 * Receive request specific.
 *
 * @param wolaClientConnectionHandle_p Client connection handle
 * @param requestServiceName_p         Pointer to request service name.
 * @param requestServiceNameLength_p   Pointer to request service name length.
 * @param requestDataLength_p          Pointer to output area to get data length.
 * @param async                        When zero, caller is waited until a request is received. When 1, the caller wants control returned immediately.
 * @param callerKey                    Key of the caller. Used when using service name.
 * @param reasonCode_p                 Output area to get the reason code.
 *
 * @return 0 on success. non 0 on failure.
 */
// TODO ensure wolaReceiveRequestSpecificCommon  sets output parms requestServiceName_p
unsigned int wolaReceiveRequestSpecificCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                              char* requestServiceName_p,               // MUST use source key and destination key
                                              unsigned int* requestServiceNameLength_p,
                                              unsigned long long* requestDataLength_p,
                                              unsigned int async,
                                              unsigned char callerKey, unsigned int* reasonCode_p);


#endif /* SERVER_WOLA_CLIENT_RECEIVE_REQUEST_SPECIFIC_H_ */
