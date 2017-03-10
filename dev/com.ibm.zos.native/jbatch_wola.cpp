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
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>

#include "include/client/bboaapi.h"

#include "include/jbatch_json.h"
#include "include/jbatch_utils.h"
#include "include/jbatch_wola.h"


/**
 * Note: WOLA APIs: http://www-01.ibm.com/support/knowledgecenter/was_beta_liberty/com.ibm.websphere.wlp.nd.multiplatform.doc/ae/rwlp_dat_olaapis.html
 *
 */

/**
 *
 * @return 0 if all is well;
 *         -1 if name is NULL.
 *         10+x where x is result of jnu_split and x != 3
 */
int jnu_parseWolaName(const char * name, WolaName * wolaName) {

    if (name == NULL) {
        return jnu_error(__FUNCTION__, -1, "wolaName is NULL");
    }

    memset(wolaName, 0, sizeof(WolaName));
    char * local_name = strdup(name);

    char * results[4] = {NULL,NULL,NULL,NULL};
    int rc = jnu_split( local_name, " +", (char **)&results, 4);

    if ( rc != 3 ) {
        free(local_name);
        return jnu_error(__FUNCTION__,  10 + rc, "WOLA name does not have 3 parts: [%s], #parts: %d", name, rc );
    }

    for (int i=0; i < 3; ++i) {
        if (strlen(results[i]) > 8) {
            int rc = (i+1)*100 + strlen(results[i]);
            jnu_error(__FUNCTION__, rc, "WOLA part #%d name is too long: [%s]. Must be <= 8 chars",i+1,results[i]);
            free(local_name);
            return rc;
        }
        
        jnu_strcpypad(wolaName->part[i], results[i], ' ', 8);
    }

    free(local_name);
    return 0;
}

/**
 * Initialize a WolaConn object with the given wola 3-part name.
 *
 * @return 0 if all is well; non-zero otherwise.
 */
int jnu_initWolaConn( WolaConn * wolaConn, char * wola3NameStr ) {

    // All blanks.
    memset(wolaConn->registrationName,' ',sizeof(wolaConn->registrationName)); 
    memcpy(wolaConn->registrationName,"batchManager",12);

    // All nulls.
    memset(wolaConn->serviceName, 0, sizeof(wolaConn->serviceName));
    strcpy(wolaConn->serviceName, "com.ibm.ws.jbatch.wola.BatchWolaListener");
    wolaConn->serviceNameLen = 0; // cuz it's null-terminated

    // Parse wolaName.
    return jnu_parseWolaName( wola3NameStr, &(wolaConn->wolaName) );
}


/**
 * Calls BBOA1REG to register with the given wola name.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaRegister( WolaName * wolaName, _CHAR12 * registrationName ) {

    // TODO: assert( strlen((const char *) registrationName) == 12 );
    assert( strlen(wolaName->part[0]) == 8 );
    assert( strlen(wolaName->part[1]) == 8 );
    assert( strlen(wolaName->part[2]) == 8 );

    int minConnections = 1;
    int maxConnections = 1;

    _REGFLAGS regflags= {0x00,0x00,0x00,0x00};
    
    int rc = -1;
    int rsn = -1;

    jnu_trace(__FUNCTION__, 
              "entry: calling BBOA1REG, reggistrationName:%x %12.12s, wolaName:[%s] [%s] [%s]", 
              registrationName, 
              registrationName,
              wolaName->part[0],
              wolaName->part[1],
              wolaName->part[2]);

    BBOA1REG (  (char(*)[8]) &wolaName->part[0],
                (char(*)[8]) &wolaName->part[1],
                (char(*)[8]) &wolaName->part[2],
                registrationName,
                &minConnections,
                &maxConnections,
                &regflags,
                &rc,
                &rsn );
    
    jnu_trace(__FUNCTION__, "exit: BBOA1REG RC:%d, RSN:%d, registrationName:%x %12.12s", rc, rsn, registrationName, registrationName);

    if (rc != 0) {
        return jnu_error(__FUNCTION__,  
                         rc, 
                         "BBOA1REG RC:%d, RSN:%d, registrationName:%x %12.12s, wolaName:[%s] [%s] [%s]",
                          rc,
                          rsn,
                          registrationName,
                          registrationName,
                          wolaName->part[0],
                          wolaName->part[1],
                          wolaName->part[2]);
    }

    return rc;
}

/**
 * Calls BBOA1CNG to obtain a connection for the given registration.
 * 
 * @param connHandle output parm, must be passed on subsequent calls for this connection
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaGetConnection( _CHAR12 * registrationName, int waitTime, _CHAR12 * connHandle ) {

    assert( waitTime >= 0 );

    jnu_trace(__FUNCTION__, "entry: calling BBOA1CNG registrationName:%x %12.12s, connHandle:%x", registrationName, registrationName, connHandle );

    int rc = -1;
    int rsn = -1;

    BBOA1CNG (  registrationName, 
                connHandle,
                &waitTime,
                &rc,
                &rsn) ;

    jnu_trace(__FUNCTION__, "exit: BBOA1CNG RC:%d, RSN:%d, registrationName:%x %12.12s, connHandle:%x ", rc, rsn, registrationName, registrationName, connHandle );

    if (rc != 0) {
        return jnu_error( __FUNCTION__, 
                          rc, 
                          "BBOA1CNG RC:%d, RSN:%d, registrationName:%x %12.12s, waitTime:%d, connHandle:%x",
                          rc,
                          rsn,
                          registrationName,
                          registrationName,
                          waitTime,
                          connHandle);
    }

    return rc;
}
    
/**
 * Register with WOLA and get a connection.
 *
 * @return 0 if all's well; non-zero otherwise
 */
int jnu_openConn( WolaConn * wolaConn ) {

    // Register with WOLA
    int rc = jnu_wolaRegister( &(wolaConn->wolaName), &(wolaConn->registrationName) );

    if ( rc != 0 ) {
        return rc;
    }

    // Obtain WOLA connection 
    rc = jnu_wolaGetConnection( &(wolaConn->registrationName), 30, &(wolaConn->connHandle) );
    
    if ( rc != 0 ) {
        jnu_wolaUnregister(&(wolaConn->registrationName));
        return rc;
    }

    return rc;
}

/**
 * Initialize, register with wola, and get a connection.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_initAndOpenWolaConn( WolaConn * wolaConn, char * wola3NameStr ) {

    int rc = jnu_initWolaConn( wolaConn, wola3NameStr );
    if (rc != 0) {
        return jnu_error(__FUNCTION__, rc, "after %s", "jnu_initWolaConn");
    }

    // Open a connection.
    rc = jnu_openConn( wolaConn );
    if ( rc != 0 ) {
        return jnu_error(__FUNCTION__, rc, "after %s", "jnu_openConn");
    }

    return rc;
}


/**
 * Calls BBOA1SRQ to send a WOLA request.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_sendRequest( WolaConn * wolaConn, char * payload, int * responseLen ) {

    int reqType = 1;
    int async = 0;

    int payloadLen = strlen(payload);

    int rc = -1;
    int rsn = -1;

    jnu_trace(__FUNCTION__, 
              "entry: calling BBOA1SRQ connHandle:%x, serviceName:%s, payloadLen:%d, payload: %x %s", 
              &(wolaConn->connHandle),
              wolaConn->serviceName,
              payloadLen,
              &payload,
              payload);

    BBOA1SRQ (  (char(*)[12]) &(wolaConn->connHandle),
                &reqType,
                &(wolaConn->serviceName),
                &(wolaConn->serviceNameLen),
                (void **) &payload,
                &payloadLen,
                &async,
                responseLen,  // output
                &rc,
                &rsn);

    jnu_trace(__FUNCTION__, 
              "exit: BBOA1SRQ RC:%d, RSN:%d, connHandle:%x, responseLen:%d ", 
              rc, 
              rsn, 
              &(wolaConn->connHandle), 
              wolaConn->serviceName, 
              *responseLen);

    if (rc != 0) {
        return jnu_error( __FUNCTION__,
                          rc, 
                          "BBOA1SRQ RC:%d, RSN:%d, connHandle:%x, serviceName:%s, payloadLen:%d, payload:%x %s",
                          rc,
                          rsn,
                          &(wolaConn->connHandle), 
                          wolaConn->serviceName, 
                          payloadLen,
                          &payload,
                          payload);
    }

    return rc;
}


/**
 * Send a JSON request to the WOLA server.
 */
int jnu_sendJsonRequest( WolaConn * wolaConn, cJSON * json ) {

    int responseLen = 0;
    char * payload = cJSON_Print(json);
    int rc = jnu_sendRequest( wolaConn, payload, &(wolaConn->responseLen) );
    free(payload);

    return rc;
}


/**
 * Calls BBOA1GET to fetch the response. The response is copied into the given responseBuffer.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_receiveResponse( _CHAR12 * connHandle, char * responseBuffer, int responseLen ) {

    assert( responseLen > 0 );

    int rc = -1;
    int rsn = -1;
    int rv = -1;

    jnu_trace(__FUNCTION__, "entry: calling BBOA1GET connHandle:%x, responseBuffer:%x, responseLen:%d ", connHandle, responseBuffer, responseLen);

    BBOA1GET (  connHandle,
                (void **) &responseBuffer,
                &responseLen,
                &rc,
                &rsn,
                &rv );

    jnu_trace(__FUNCTION__, "exit: BBOA1GET RC:%d, RSN:%d, connHandle:%x, responseLen:%d, responseBuffer:%s", rc, rsn, connHandle, responseLen, responseBuffer);

    if (rc != 0) {
        return jnu_error( __FUNCTION__,
                          rc, 
                          "BBOA1GET RC:%d, RSN:%d, RV:%d, connHandle:%x, responseLen:%d",
                          rc,
                          rsn,
                          rv,
                          connHandle,
                          responseLen);
    }

    return rc;
}

/**
 * Read a JSON response object from the conn.
 *
 * @return cJSON object; or NULL if something went wrong.
 */
cJSON * jnu_readJsonResponse(WolaConn * wolaConn) {

    if (wolaConn->responseLen <= 0) {
        jnu_error(__FUNCTION__, 0, "invalid responseLen:%d", wolaConn->responseLen);
        return NULL;
    }

    // Get Response
    char * response = (char *) jnu_malloc(wolaConn->responseLen + 1);
    int rc = jnu_receiveResponse( &(wolaConn->connHandle), response, wolaConn->responseLen);

    wolaConn->responseLen = 0;  // Reset.

    cJSON * retMe = cJSON_Parse(response);
    free(response);

    return retMe;
}

/**
 * Send the JSON request and wait for the response.
 *
 * @return the json response; or NULL if something went wrong.
 */
cJSON * jnu_doJsonRequest( WolaConn * wolaConn, cJSON * json ) {

    // Send the JSON request.
    int rc = jnu_sendJsonRequest( wolaConn, json);
    if ( rc != 0 ) {
        jnu_error(__FUNCTION__, rc, "jnu_sendJsonRequest rc:%d", rc);
        return NULL;
    }

    // Receive json response.
    cJSON * responseMessage = jnu_readJsonResponse( wolaConn);

    // Check for errors, return the embedded response object.
    char * errorMsg = cJSON_GetObjectItemStringValue(responseMessage, "error", NULL) ;
    if (errorMsg != NULL) {
        char * jsonStr = cJSON_Print(json);
        jnu_error(__FUNCTION__,0, "The request failed due to: %s\n-----------------------------------\nFor request: %s", errorMsg, jsonStr);
        free(jsonStr);
        cJSON_Delete(responseMessage);
        return NULL;
    }

    return cJSON_DupObjectItemAndFreeObject( responseMessage, "response" );
}

    
/**
 * Calls BBOA1CNR to release the given WOLA connHandle.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaReleaseConnection( _CHAR12 * connHandle ) {

    int rc = -1;
    int rsn = -1;

    jnu_trace(__FUNCTION__, "entry: calling BBOA1CNR, connHandle:%x ", connHandle);

    BBOA1CNR (  connHandle,
                &rc,
                &rsn );

    jnu_trace(__FUNCTION__, "exit: BBOA1CNR RC:%d, RSN:%d, connHandle:%x ", rc, rsn, connHandle);

    if (rc != 0) {
        return jnu_error( __FUNCTION__,
                          rc, 
                          "BBOA1CNR RC:%d, RSN:%d, RV:%d, connHandle:%x",
                          rc,
                          rsn,
                          connHandle);
    }

    return rc;
}

/**
 * Calls BBOA1URG to unregister the given WOLA registrationName.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_wolaUnregister( _CHAR12 * registrationName) {

    // TODO: assert( strlen(registrationName) == 12 );

    jnu_trace(__FUNCTION__, "entry: calling BBOA1URG, registrationName:%x %12.12s", registrationName, registrationName );

    _UNREGFLAGS unregflags= {0x00,0x00,0x00,0x00};

    int rc = -1;
    int rsn = -1;

    BBOA1URG (  registrationName,
                &unregflags,
                &rc,
                &rsn );

    jnu_trace(__FUNCTION__, "exit: BBOA1URG RC:%d, RSN:%d, registrationName:%x %12.12s", rc, rsn, registrationName, registrationName );

    if (rc != 0) {
        return jnu_error( __FUNCTION__,
                          rc, 
                          "BBOA1URG RC:%d, RSN:%d, registrationName:%x %12.12s",
                          rc,
                          rsn,
                          registrationName,
                          registrationName);
    }

    return rc;
}


/**
 * Calls jnu_wolaReleaseConnection and jnu_wolaUnregister.
 *
 * @return 0 if all is well; non-zero otherwise
 */
int jnu_closeAndUnregister(WolaConn * wolaConn) {
    jnu_wolaReleaseConnection(&(wolaConn->connHandle));
    return jnu_wolaUnregister(&(wolaConn->registrationName));
}



