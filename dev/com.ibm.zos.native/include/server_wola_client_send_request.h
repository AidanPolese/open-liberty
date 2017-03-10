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

#ifndef SERVER_WOLA_CLIENT_SEND_REQUEST_H_
#define SERVER_WOLA_CLIENT_SEND_REQUEST_H_

#include "gen/bboapc1p.h"
#include "server_wola_connection_handle.h"
#include "server_wola_message.h"

/**
 * Send request using input connection.
 *
 * @param wolaClientConnectionHandle_p client connection handle
 * @param requestType                  request type
 * @param requestDataLength            length of request data
 * @param requestData_p                pointer to request data
 * @param requestDataKey               key of request data
 * @param wolaServiceNameContextArea_p pointer to service name context area
 * @param cicsParms_p                  Pointer to cics-specific parms.
 * @param reasonCode_p                 output pointer to reason code
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaSendRequestCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                   unsigned int requestType,
                                   unsigned long long requestDataLength,
                                   char* requestData_p,
                                   unsigned char requestDataKey,
                                   WolaServiceNameContextArea_t* wolaServiceNameContextArea_p,
                                   struct bboapc1p* cicsParms_p,
                                   unsigned int* reasonCode_p);


#endif /* SERVER_WOLA_CLIENT_SEND_REQUEST_H_ */
