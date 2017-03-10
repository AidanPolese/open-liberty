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

#ifndef SERVER_WOLA_CLIENT_GET_DATA_H_
#define SERVER_WOLA_CLIENT_GET_DATA_H_

#include "server_wola_connection_handle.h"

/**
 * Get data associated with the input connection.
 *
 * @param wolaClientConnectionHandle_p client connection handle
 * @param messageData_p     pointer to message data
 * @param messageDataLength length of message data
 * @param callerKey         Key of the caller. Used when using message data pointer.
 * @param reasonCode_p      Output area to get the reason code.
 * @param reasonValue_p     Output area to get the reason value.
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int wolaGetDataCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, char* messageData_p,
                               unsigned long long messageDataLength, unsigned char callerKey,
                               unsigned int* reasonCode_p, unsigned int* reasonValue_p);


#endif /* SERVER_WOLA_CLIENT_GET_DATA_H_ */
