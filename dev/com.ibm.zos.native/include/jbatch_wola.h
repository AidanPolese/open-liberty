/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
#ifndef __jbatch_wola_h__
#define __jbatch_wola_h__

#include "jbatch_json.h"

typedef char             _CHAR12  [ 12];

/**
 * The WOLA 3-part name.
 */
typedef struct {
    char part[3][9];
} WolaName;

/**
 * All the pieces for a WOLA connection.
 */
typedef struct {

    WolaName wolaName;
    char registrationName[12];
    char connHandle[12];
    char serviceName[256]; 
    int serviceNameLen; 
    int responseLen;

} WolaConn;


/**
 * Initialize, register with wola, and get a connection.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_initAndOpenWolaConn( WolaConn * wolaConn, char * wola3NameStr ) ;

/**
 * Send the JSON request and wait for the response.
 *
 * @return the json response, or NULL if something went wrong.
 */
cJSON * jnu_doJsonRequest( WolaConn * wolaConn, cJSON * args );

/**
 * @param name the wola name, separated by either " " or "+"
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_parseWolaName(const char * name, WolaName * wolaName) ;

/**
 * Initialize the given WolaConn with the given 3 part name.
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_initWolaConn( WolaConn * wolaConn, char * wola3NameStr ) ;

/**
 * Register and get a connection.
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_openConn( WolaConn * wolaConn ) ;

/**
 * Send a JSON request to the wola service.
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_sendJsonRequest( WolaConn * wolaConn, cJSON * json ) ;


/**
 * @return a cJSON response object (free when finished).
 */
cJSON * jnu_readJsonResponse(WolaConn * wolaConn) ;

/**
 * Release the connection and unregister with WOLA.
 */
int jnu_closeAndUnregister(WolaConn * wolaConn) ;

/**
 * Calls BBOA1REG to register with the given wola name.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaRegister( WolaName * wolaName, _CHAR12 * registrationName ) ;

/**
 * Calls BBOA1CNG to obtain a connection for the given registration.
 * 
 * @param connHandle output parm, must be passed on subsequent calls for this connection
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaGetConnection( _CHAR12 * registrationName, int waitTime, _CHAR12 * connHandle ) ;
    
/**
 * Calls BBOA1SRQ to send a WOLA request.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_sendRequest( _CHAR12 * connHandle, char * payload, int * responseLen ) ;

/**
 * Calls BBOA1GET to fetch the response. The response is copied into the given responseBuffer.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_receiveResponse( _CHAR12 * connHandle, char * responseBuffer, int responseLen ) ;
    
/**
 * Calls BBOA1CNR to release the given WOLA connHandle.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaReleaseConnection( _CHAR12 * connHandle ) ;

/**
 * Calls BBOA1URG to unregister the given WOLA registrationName.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaUnregister(_CHAR12 * registrationName) ;


#endif
