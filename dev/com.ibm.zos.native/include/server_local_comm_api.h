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
#ifndef _BBOZ_SERVER_LOCAL_COMM_API_H
#define _BBOZ_SERVER_LOCAL_COMM_API_H

#include "common_defines.h"

/** Client connection handle.  This is 16 bytes opaque data to the client. */
typedef struct {
    unsigned char handle[16];
} OpaqueClientConnectionHandle_t;

#include "gen/ihaacee.h"

/**
 * Establish a new local comm connection.
 *
 * @param inServerStoken_p A pointer to the stoken for the server to connect to.
 * @param inClientID The client identifier.  This is a constant that represents
 *                 the "channel" which handed the request to local comm.  The
 *                 value will be used on the server side during discrimination
 *                 to determine which channel will receive the data sent by the
 *                 client.
 * @param inTimeToWait A maximum amount of seconds to wait for data to arrive. A zero value indicates to wait indefinitely.
 * @param inACEE_p A pointer to the ACEE to extract identity from or NULL (will be pulled from current TCB).
 * @param inClientSafString A string containing the SAF profile component needed for a CBIND check ("BBG.<inClientSafString>").
 * @param outClientHandle_p A pointer to a quad word where the connection handle will be
 *                 copied.
 * @param outRSN_p Pointer to an int to contain the reason code from processing the connect request.
 * @param outSAFRC_p Pointer to an int to contain the SAF return code from the CBIND security check.
 * @param outRACFRC_p Pointer to an int to contain the RACF return code from the CBIND security check.
 * @param outRACFRSN_p Pointer to an int to contain the RACF reason code from the CBIND security check.
 *
 * @return 0 on success.  8 on failure. 12 if timed out waiting for a server response.
*/
#define LCOM_CLIENTCONNECT_RC_OK         0
#define LCOM_CLIENTCONNECT_RC_FAILED     8
#define LCOM_CLIENTCONNECT_RC_TIMEDOUT  12
#define LCOM_CLIENTCONNECT_RC_SAF       16
#define LCOM_CLIENTID_WOLA               1      // WOLA Client
int localCommClientConnect(SToken*        inServerStoken_p,
                           unsigned int   inClientID,
                           int            inTimeToWait,
                           acee*          inACEE_p,
                           char*          inClientSafString,
                           void*          outClientHandle_p,
                           int*           outRSN_p,
                           int*           outSAFRC_p,
                           int*           outRACFRC_p,
                           int*           outRACFRSN_p);

/**
 * Send data on a local comm connection.
 *
 * @param handle_p A pointer to the connection handle token, returned on connect.
 * @param dataLen The length of the data to send.
 * @param data_p A pointer to the data, in the caller's key.
 * @param dataKey The key the data is in.
 *
 * @return 0 on success. 8 if no available message cells to transport data.  12 if we could not
 * build and deliver the send request to the target.  16 if the input dataLen is too big for local comm.
 * @note Called by both client and server-sides of the connection.
 */
// TODO: Support IOVectors.
#define LCOM_SEND_RC_OK                 0
#define LCOM_SEND_RC_NOMSGCELL          8
#define LCOM_SEND_RC_BUILDREQ_FAILED   12
#define LCOM_SEND_RC_MSGTOOBIG         16
int localCommSend(void* handle_p, unsigned long long dataLen, void* data_p, unsigned char dataKey);

/**
 * Receive data synchronously on a local comm connection.
 *
 * @param handle_p A pointer to the connection handle token returned on connect.
 * @param dataLen The number of bytes to receive into the buffer pointed to by <param>data_p</param>.
 * @param data_p A pointer to the buffer where the data should be copied.
 * @param dataKey The key the buffer is in (specified by data_p).
 *
 * @return 0 read went async (only for server reads), less than zero if failed,  otherwise the amount of data read.
 */
int localCommReceive(void* handle_p, unsigned long long dataLen, void* data_p, unsigned char dataKey);

/**
 * See if data is available to receive on this connection.  The call can be asynchronous or synchronous
 * depending on the value of waitForData.  If synchronous, the call will block until at least one byte
 * of data is available or the timeToWait has been exceeded.
 *
 * @param handle_p A pointer to the connection handle token, returned on connect.
 * @param waitForData Set to 0 if we should return immediately with an answer, non-zero if we should wait until at least 1 byte is available.
 * @param timeToWait A maximum amount of seconds to wait for data to arrive. A zero value indicates to wait indefinitely.
 * @param dataLen_p A pointer to a double word where the number of bytes available is stored.
 * Only applicable if "waitForData" is non-zero.
 *
 * @return LCOM_PREVIEW_RC_OK on success, LCOM_PREVIEW_RC_TIMEDOUT if we timed out waiting for data, or
 * LCOM_PREVIEW_RC_PREVIEWREQ_FAILED otherwise.
 */
#define LCOM_PREVIEW_RC_OK                 0
#define LCOM_PREVIEW_RC_PREVIEWREQ_FAILED  8
#define LCOM_PREVIEW_RC_TIMEDOUT          12
int localCommClientPreview(void* handle_p, unsigned char waitForData, int timeToWait, unsigned long long* dataLen_p);

/**
 * Terminate a local comm connection.
 *
 * @param handle_p A pointer to the connection handle token, returned on connect.
 *
 * @return 0 on success. -8 if the connection was stale (failed validation).  -12 if the connection was already
 * driven for close.
 */
#define LCOM_CLOSE_RC_ALREADYCLOSING      -12
int localCommClose(void* handle_p);
#endif
