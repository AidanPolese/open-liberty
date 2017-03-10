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
#include <metal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "../include/gen/bboapc1p.h"
#include "../include/gen/ihaacee.h"
#include "../include/gen/ihapsa.h"
#include "../include/gen/ikjtcb.h"
#include "../include/common_defines.h"
#include "../include/mvs_utils.h"
#include "../include/security_saf_acee.h"
#include "../include/server_wola_message.h"
#include "../include/server_wola_registration.h"

#include "include/CuTest.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 */

extern void println(char * messgae_p, ...);



#define TLS_DELETE_THREAD_SEC 2
#define TLS_TASK_ACEE 3

/**
 * Forward declares for functions not defined in server_wola_message.h.
 */
acee * __ptr32 getAceeFromTCB(void) ;

int populateCommonIRRSIA00Parms_sk(IRRSIA00Parms * __ptr32 irrsia00Parms,
                                   char* username,
                                   int usernameLen,
                                   char* password,
                                   int passwordLen,
                                   char* auditString,
                                   int auditStringLen,
                                   char* applName,
                                   int applNameLen,
                                   RACO* inRaco,
                                   acee* __ptr32 inAcee,
                                   int sourceKey) ;

int invokeBPX4TLS (int functionCode, SAFServiceResult* serviceResult_p) ;

WolaMessage_t * setMvsUserIdFromCics_sk(WolaMessage_t * messageHeader_p, 
                                        WolaRegistration_t* registration_p, 
                                        struct bboapc1p* cicsParms_p, 
                                        int srcKey);

int getWolaMessageContextTotalLength( WolaMessageContextHeader_t * wolaMessageContextHeader_p ) ;
WolaMessageContextHeader_t * getFirstContext(WolaMessageContextAreaHeader_t * wolaMessageContextAreaHeader_p) ;
WolaMessageContextHeader_t * getNextContext(WolaMessageContextHeader_t * wolaMessageContextHeader_p) ;
WolaMessageContextHeader_t * getWolaMessageContext( WolaMessageContextAreaHeader_t * wolaMessageContextAreaHeader_p, int contextId) ;
int extractServiceName( char* buffer_p, WolaServiceNameContext_t * wolaServiceNameContext_p ) ;


/**
 * Test.
 */
void test_setWolaMessageServerNameFromRegistration(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    WolaRegistration_t reg;
    WolaMessage_t msg;

    memset(&msg, 0, sizeof(msg));

    memcpy(reg.serverNameSecondPart, "SERVER2 ", 8);
    memcpy(reg.serverNameThirdPart,  "SERVER3 ", 8);

    setWolaMessageServerNameFromRegistration(&msg, &reg);

    CuAssertMemEquals( tc, "SERVER2 ", msg.wolaNamePartTwo, 8 );
    CuAssertMemEquals( tc, "SERVER3 ", msg.wolaNamePartThree, 8 );

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_initializeMessageHeader(CuTest * tc) {
        
    WolaMessage_t msg;
    WolaMessage_t * msg_p = initializeMessageHeader(&msg, WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV );

    CuAssertMemEquals(tc, BBOAMSG_EYE, msg.eye, 8 );
    CuAssertIntEquals(tc, BBOAMSG_VERSION_2, msg.amsgver );
    CuAssertIntEquals(tc, sizeof(WolaMessage_t), msg.totalMessageSize );
    CuAssertIntEquals(tc, WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV, msg.workType );
    CuAssertIntEquals(tc, 0, msg.requestId);
    CuAssertIntEquals(tc, 0, msg.messageType);
    CuAssertPtrEquals(tc, &msg, msg_p);
}

/**
 *
 */
void test_initializeContextHeader(CuTest * tc) {

    WolaMessageContextAreaHeader_t contextHeader;
    WolaMessageContextAreaHeader_t * contextHeader_p = initializeContextHeader(&contextHeader, 1);

    CuAssertMemEquals(tc, BBOACTX_EYE, contextHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOACTX_VERSION_1, contextHeader.version);
    CuAssertIntEquals(tc, 1, contextHeader.numContexts);
    CuAssertPtrEquals(tc, &contextHeader, contextHeader_p);
}

/**
 *
 */
void test_buildWolaMessageAndContextArea(CuTest * tc) {

    WolaServiceNameContextArea_t ctx;
    ctx.serviceNameContext.nameLength = 10;
    ctx.serviceNameContext.header.contextLen = sizeof(WolaServiceNameContext_t) 
                                               + 10 // nameLength
                                               - sizeof(WolaMessageContextHeader_t); // header not included in contextLen.

    WolaMessageAndContextArea_t msgAndCtx;
    WolaMessageAndContextArea_t * msgAndCtx_p = buildWolaMessageAndContextArea(&msgAndCtx, 
                                                                               WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV,
                                                                               &ctx);

    int contextAreaLength = sizeof(WolaMessageContextAreaHeader_t)
                            + sizeof(WolaServiceNameContext_t) 
                            + 10;   // length of service name

    int totalMessageSize = sizeof(WolaMessage_t) + contextAreaLength;

    CuAssertPtrEquals(tc, &msgAndCtx, msgAndCtx_p);
    CuAssertMemEquals(tc, BBOAMSG_EYE, msgAndCtx.messageHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOAMSG_VERSION_2, msgAndCtx.messageHeader.amsgver );
    CuAssertIntEquals(tc, totalMessageSize, msgAndCtx.messageHeader.totalMessageSize );
    CuAssertIntEquals(tc, WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV, msgAndCtx.messageHeader.workType );
    CuAssertIntEquals(tc, 0, msgAndCtx.messageHeader.requestId);
    CuAssertIntEquals(tc, 0, msgAndCtx.messageHeader.messageType);
    CuAssertIntEquals(tc, sizeof(WolaMessage_t), msgAndCtx.messageHeader.contextAreaOffset);
    CuAssertIntEquals(tc, contextAreaLength, msgAndCtx.messageHeader.contextAreaLength);

    CuAssertMemEquals(tc, BBOACTX_EYE, msgAndCtx.contextHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOACTX_VERSION_1, msgAndCtx.contextHeader.version);
    CuAssertIntEquals(tc, 1, msgAndCtx.contextHeader.numContexts);
}

/**
 * 
 */
void test_setContextAreaLengthAndOffset(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    WolaMessage_t msg;
    initializeMessageHeader(&msg, 0);

    CuAssertIntEquals(tc, sizeof(msg), msg.totalMessageSize);
    CuAssertIntEquals(tc, 0, msg.contextAreaOffset);
    CuAssertIntEquals(tc, 0, msg.contextAreaLength);

    int contextAreaLength = 30;
    int expectedContextAreaOffset = sizeof(msg) ;
    int expectedTotalMessageSize = msg.totalMessageSize + contextAreaLength;

    WolaMessage_t * msg_p = setContextAreaLengthAndOffset(&msg, contextAreaLength);

    CuAssertIntEquals(tc, contextAreaLength, msg.contextAreaLength);
    CuAssertIntEquals(tc, expectedContextAreaOffset, msg.contextAreaOffset);
    CuAssertIntEquals(tc, expectedTotalMessageSize, msg.totalMessageSize );

    CuAssertPtrEquals(tc, &msg, msg_p);   

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_setDataAreaLengthAndOffset(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    WolaMessage_t msg;
    msg.contextAreaLength = 20;
    msg.contextAreaOffset = sizeof(WolaMessage_t);
    msg.totalMessageSize = sizeof(WolaMessage_t) + msg.contextAreaLength;

    int requestDataLength = 30;
    int expectedDataAreaOffset = sizeof(WolaMessage_t) + msg.contextAreaLength;
    int expectedTotalMessageSize = msg.totalMessageSize + requestDataLength;

    WolaMessage_t * msg_p = setDataAreaLengthAndOffset(&msg, requestDataLength);

    CuAssertIntEquals(tc, requestDataLength, msg.dataAreaLength );
    CuAssertIntEquals(tc, expectedDataAreaOffset, msg.dataAreaOffset );
    CuAssertIntEquals(tc, expectedTotalMessageSize, msg.totalMessageSize );

    CuAssertPtrEquals(tc, &msg, msg_p);   

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_propagateMvsUserId(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    WolaRegistration_t reg;
    memset(&reg, 0, sizeof(reg));

    IRRSIA00Parms* __ptr32 irrsia00Parms = allocateIRRSIA00Parms();
    int rc = populateCommonIRRSIA00Parms_sk(irrsia00Parms,
                                            "MSTONE1",
                                            strlen("MSTONE1"),
                                            "M00NTEST",
                                            strlen("M00NTEST"),
                                            NULL,
                                            0,
                                            NULL,
                                            0,
                                            NULL,
                                            NULL,
                                            2);     // source key
    CuAssertIntEquals(tc, 0, rc);

    // Setup saf return area
    SAFServiceResult safServiceResult;
    memset(&safServiceResult, 0, sizeof(SAFServiceResult));

    // invoke initACEE
    acee* __ptr32 acee_p = (acee * __ptr32) createACEE(irrsia00Parms, &safServiceResult);
    free(irrsia00Parms);

    CuAssertPtrNotNull(tc, acee_p);

    // Set TCBSENV.
    psa* psa_p = (psa *)0L;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    memcpy_dk(&(tcb_p->tcbsenv), &acee_p, sizeof(tcb_p->tcbsenv), 0);

    // Create TLS environment
    rc = invokeBPX4TLS(TLS_TASK_ACEE, &safServiceResult);

    CuAssertIntEquals(tc, 0, rc);
    CuAssertPtrEquals(tc, acee_p, getAceeFromTCB() );

    // Dummy up a wola message
    WolaMessage_t messageHeader;
    memset(&messageHeader, 0, sizeof(messageHeader));

    // Set the ID and check it
    WolaMessage_t * messageHeader_p = setCallersMvsUserId(&messageHeader, &reg, NULL);

    CuAssertIntEquals(tc, 0, rc);   
    CuAssertPtrEquals(tc, &messageHeader, messageHeader_p);   
    CuAssertMemEquals(tc, "MSTONE1\0", messageHeader.mvsUserID, sizeof(messageHeader.mvsUserID) );
    CuAssertIntEquals(tc, 8, sizeof(messageHeader.mvsUserID) );     // just a sanity check

    // Delete TLS environment (leaves the ACEE alone), then clear out the acee.
    rc = invokeBPX4TLS(TLS_DELETE_THREAD_SEC, &safServiceResult);
    void * null_p = NULL;
    memcpy_dk(&(tcb_p->tcbsenv), &null_p, sizeof(tcb_p->tcbsenv), 0);

    // Reset the message's userID
    memset(messageHeader.mvsUserID, 0, sizeof(messageHeader.mvsUserID));

    // Try again, this time when TCBSENV is NULL.  Should pick up ASCBSENV (also MSTONE1)
    messageHeader_p = setCallersMvsUserId(&messageHeader, &reg, NULL);
    CuAssertPtrEquals(tc, &messageHeader, messageHeader_p);   
    CuAssertMemEquals(tc, "MSTONE1\0", messageHeader.mvsUserID, sizeof(messageHeader.mvsUserID) );

    println(__FUNCTION__ ": exit");
}


/**
 *
 */
void test_propagateCicsUserId(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // Dummy up a client registration with propAcee set
    WolaRegistration_t reg;
    memset(&reg, 0, sizeof(reg));
    reg.flags.propAceeFromTrueIntoServer = 1;

    // Dummy up an ACEE to use
    acee * __ptr32 theAcee_p = __malloc31(sizeof(acee));  // must be below the bar
    memset(theAcee_p, 0, sizeof(*theAcee_p));
    theAcee_p->aceeuser._aceeusrl = 8;
    strcpy(theAcee_p->aceeuser._aceeusri, "CICSUSER");

    // Dummy up some cicsParms
    struct bboapc1p cicsParms;
    memset(&cicsParms, 0, sizeof(cicsParms));
    cicsParms.bboapc1p_acee = (int) theAcee_p;

    // Dummy up a wola message
    WolaMessage_t messageHeader;
    memset(&messageHeader, 0, sizeof(messageHeader));

    // Set the ID and check it
    WolaMessage_t * messageHeader_p = setMvsUserIdFromCics_sk(&messageHeader, &reg, &cicsParms, 2);

    CuAssertPtrEquals(tc, &messageHeader, messageHeader_p);   
    CuAssertMemEquals(tc, "CICSUSER", messageHeader.mvsUserID, sizeof(messageHeader.mvsUserID) );
    CuAssertIntEquals(tc, 8, sizeof(messageHeader.mvsUserID) );     // just a sanity check

    println(__FUNCTION__ ": exit");
}


/**
 *
 */
void test_BBGZ_resetHighOrderBit(CuTest * tc) {
    CuAssertIntEquals(tc, 0x7FFF0000, BBGZ_resetHighOrderBit(0xFFFF0000) );
    CuAssertIntEquals(tc, 0x7FFF0000, BBGZ_resetHighOrderBit(0x7FFF0000) );
    CuAssertIntEquals(tc, 0x0FFF0000, BBGZ_resetHighOrderBit(0x8FFF0000) );
}


/**
 *
 */
void test_getServiceNameFromContext(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    WolaMessageAndContextArea_t msgAndCtx;
    char inputRequestServiceName[10];
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    memcpy(inputRequestServiceName, "ABCDEFGHI", 9);
    unsigned int  serviceNameLength = strlen(inputRequestServiceName);
    unsigned int rc = buildServiceNameContext(&(msgAndCtx.serviceNameContextArea.serviceNameContext),
                                              msgAndCtx.serviceNameContextArea.name,
                                              serviceNameLength,
                                              inputRequestServiceName,
                                              2 /* KEY */);
    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 0", 0, rc);

    WolaMessageAndContextArea_t * msgAndCtx_p = buildWolaMessageAndContextArea(&msgAndCtx,
                                                                               WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV,
                                                                               &msgAndCtx.serviceNameContextArea);

    int contextAreaLength = sizeof(WolaMessageContextAreaHeader_t)
                            + sizeof(WolaServiceNameContext_t)
                            + serviceNameLength;   // length of service name

    int totalMessageSize = sizeof(WolaMessage_t) + contextAreaLength;

    CuAssertPtrEquals(tc, &msgAndCtx, msgAndCtx_p);
    CuAssertMemEquals(tc, BBOAMSG_EYE, msgAndCtx.messageHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOAMSG_VERSION_2, msgAndCtx.messageHeader.amsgver );
    CuAssertIntEquals(tc, totalMessageSize, msgAndCtx.messageHeader.totalMessageSize );
    CuAssertIntEquals(tc, WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV, msgAndCtx.messageHeader.workType );
    CuAssertIntEquals(tc, 0, msgAndCtx.messageHeader.requestId);
    CuAssertIntEquals(tc, 0, msgAndCtx.messageHeader.messageType);
    CuAssertIntEquals(tc, sizeof(WolaMessage_t), msgAndCtx.messageHeader.contextAreaOffset);
    CuAssertIntEquals(tc, contextAreaLength, msgAndCtx.messageHeader.contextAreaLength);

    CuAssertMemEquals(tc, BBOACTX_EYE, msgAndCtx.contextHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOACTX_VERSION_1, msgAndCtx.contextHeader.version);
    CuAssertIntEquals(tc, 1, msgAndCtx.contextHeader.numContexts);

    char outputRequestServiceName[257];
    WolaClientConnectionHandle_t wolaClientConnectionHandle;
    WolaConnectionHandle_t wolaConnectionHandle;
    memset(&wolaConnectionHandle, 0, sizeof(wolaConnectionHandle));
    wolaConnectionHandle.ploArea.instanceCount = 5;
    wolaConnectionHandle.cachedMessage_p = msgAndCtx_p;
    wolaConnectionHandle.cachedContexts_p = ((char*)msgAndCtx_p) + msgAndCtx.messageHeader.contextAreaOffset;
    wolaClientConnectionHandle.instanceCount = 5;
    wolaClientConnectionHandle.handle_p = &wolaConnectionHandle;
    rc = getServiceNameFromContext(&wolaClientConnectionHandle, outputRequestServiceName);

    CuAssertIntEquals_Msg(tc, "getServiceNameFromContext return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "Request service name should be ABCDEFGHI", inputRequestServiceName, outputRequestServiceName);

    println(__FUNCTION__ ": exit");
}

typedef struct wolaMessageAndContextArea3Contexts {
    WolaMessageAndContextArea_t msgAndFirstContext;
    WolaServiceNameContext_t serviceNameContextArea2;
    char                     serviceNameContextArea2Name[8];
    WolaServiceNameContext_t serviceNameContextArea3;
    char                     serviceNameContextArea3Name[5];
} WolaMessageAndContextArea3Contexts_t;

/**
 *
 */
void test_getServiceNameFromContextMultipleContextsInMsg(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    WolaMessageAndContextArea3Contexts_t msgAndCtx;
    char inputRequestServiceName[257];
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    // need 256 A's because msgAndCtx.msgAndFirstContext.serviceNameContextArea has 256 byte array for the name
    memset(inputRequestServiceName, 'A', sizeof(inputRequestServiceName) - 1);
    unsigned int  serviceNameLength = strlen(inputRequestServiceName);
    unsigned int rc = buildServiceNameContext(&(msgAndCtx.msgAndFirstContext.serviceNameContextArea.serviceNameContext),
                                              msgAndCtx.msgAndFirstContext.serviceNameContextArea.name,
                                              serviceNameLength,
                                              inputRequestServiceName,
                                              2 /* KEY */);
    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 0", 0, rc);

    WolaMessageAndContextArea_t * msgAndCtx_p = buildWolaMessageAndContextArea(&(msgAndCtx.msgAndFirstContext),
                                                                               WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV,
                                                                               &msgAndCtx.msgAndFirstContext.serviceNameContextArea);

    msgAndCtx.msgAndFirstContext.serviceNameContextArea.serviceNameContext.header.contextId = BBOATXC_Identifier; // make it look like a different context so we loop thru all 3


    int contextAreaLength = sizeof(WolaMessageContextAreaHeader_t)
                                + sizeof(WolaServiceNameContext_t)
                                + serviceNameLength;   // length of service name


    // initialize 2nd context
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    memcpy(inputRequestServiceName, "TWOTWO22", 8);
    serviceNameLength = strlen(inputRequestServiceName);
    rc = buildServiceNameContext(&(msgAndCtx.serviceNameContextArea2),
                                 msgAndCtx.serviceNameContextArea2Name,
                                 serviceNameLength,
                                 inputRequestServiceName,
                                 2 /* KEY */);
    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 0", 0, rc);

    msgAndCtx.serviceNameContextArea2.header.contextId = BBOATXC_Identifier; // make it look like a different context so we loop thru all 3

    contextAreaLength = contextAreaLength + sizeof(WolaServiceNameContext_t)  + serviceNameLength;   // length of service name

    // adjust length of context area in message header
    msgAndCtx.msgAndFirstContext.messageHeader.contextAreaLength = msgAndCtx.msgAndFirstContext.messageHeader.contextAreaLength + sizeof(WolaServiceNameContext_t)  + serviceNameLength;
    // adjust length of total message in message header
    msgAndCtx.msgAndFirstContext.messageHeader.totalMessageSize = msgAndCtx.msgAndFirstContext.messageHeader.totalMessageSize + sizeof(WolaServiceNameContext_t)  + serviceNameLength;

    // initialize third context
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    memcpy(inputRequestServiceName, "THIRD", 5);
    serviceNameLength = strlen(inputRequestServiceName);
    rc = buildServiceNameContext(&(msgAndCtx.serviceNameContextArea3),
                                 msgAndCtx.serviceNameContextArea3Name,
                                 serviceNameLength,
                                 inputRequestServiceName,
                                 2 /* KEY */);
    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 0", 0, rc);

    contextAreaLength = contextAreaLength + sizeof(WolaServiceNameContext_t)  + serviceNameLength;   // length of service name

    // adjust length of context area in message header
    msgAndCtx.msgAndFirstContext.messageHeader.contextAreaLength = msgAndCtx.msgAndFirstContext.messageHeader.contextAreaLength + sizeof(WolaServiceNameContext_t)  + serviceNameLength;

    // adjust length of total message in message header
    msgAndCtx.msgAndFirstContext.messageHeader.totalMessageSize = msgAndCtx.msgAndFirstContext.messageHeader.totalMessageSize + sizeof(WolaServiceNameContext_t)  + serviceNameLength;

    msgAndCtx.msgAndFirstContext.contextHeader.numContexts = 3;

    int totalMessageSize = sizeof(WolaMessage_t) + contextAreaLength;

    CuAssertPtrEquals(tc, &msgAndCtx, msgAndCtx_p);
    CuAssertMemEquals(tc, BBOAMSG_EYE, msgAndCtx.msgAndFirstContext.messageHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOAMSG_VERSION_2, msgAndCtx.msgAndFirstContext.messageHeader.amsgver );
    CuAssertIntEquals(tc, totalMessageSize, msgAndCtx.msgAndFirstContext.messageHeader.totalMessageSize );
    CuAssertIntEquals(tc, WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV, msgAndCtx.msgAndFirstContext.messageHeader.workType );
    CuAssertIntEquals(tc, 0, msgAndCtx.msgAndFirstContext.messageHeader.requestId);
    CuAssertIntEquals(tc, 0, msgAndCtx.msgAndFirstContext.messageHeader.messageType);
    CuAssertIntEquals(tc, sizeof(WolaMessage_t), msgAndCtx.msgAndFirstContext.messageHeader.contextAreaOffset);
    CuAssertIntEquals(tc, contextAreaLength, msgAndCtx.msgAndFirstContext.messageHeader.contextAreaLength);

    CuAssertMemEquals(tc, BBOACTX_EYE, msgAndCtx.msgAndFirstContext.contextHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOACTX_VERSION_1, msgAndCtx.msgAndFirstContext.contextHeader.version);
    CuAssertIntEquals(tc, 3, msgAndCtx.msgAndFirstContext.contextHeader.numContexts);

    char outputRequestServiceName[257];
    WolaClientConnectionHandle_t wolaClientConnectionHandle;
    WolaConnectionHandle_t wolaConnectionHandle;
    memset(&wolaConnectionHandle, 0, sizeof(wolaConnectionHandle));
    wolaConnectionHandle.ploArea.instanceCount = 5;
    wolaConnectionHandle.cachedMessage_p = msgAndCtx_p;
    wolaConnectionHandle.cachedContexts_p = ((char*)msgAndCtx_p) + msgAndCtx_p->messageHeader.contextAreaOffset;
    wolaClientConnectionHandle.instanceCount = 5;
    wolaClientConnectionHandle.handle_p = &wolaConnectionHandle;
    rc = getServiceNameFromContext(&wolaClientConnectionHandle, outputRequestServiceName);

    CuAssertIntEquals_Msg(tc, "getServiceNameFromContext return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "Request service name should be THIRD", inputRequestServiceName, outputRequestServiceName);

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_buildServiceNameContext(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    WolaMessageAndContextArea_t msgAndCtx;
    memset(&msgAndCtx, 0, sizeof(msgAndCtx));
    char inputRequestServiceName[10];
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    memcpy(inputRequestServiceName, "ABCDEFGHI", 9);
    unsigned int serviceNameLength = strlen(inputRequestServiceName);
    int expectedTotalContextLen = sizeof(WolaServiceNameContext_t) + serviceNameLength;
    int expectedContextLen = expectedTotalContextLen - sizeof(WolaMessageContextHeader_t);

    // Test non zero length
    unsigned int rc = buildServiceNameContext(&(msgAndCtx.serviceNameContextArea.serviceNameContext),
                                              msgAndCtx.serviceNameContextArea.name,
                                              serviceNameLength,
                                              inputRequestServiceName,
                                              2 /* KEY */);

    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 0", 0, rc);
    CuAssertIntEquals_Msg(tc, "service name context version", BBOASNC_VERSION_1, msgAndCtx.serviceNameContextArea.serviceNameContext.version);
    CuAssertMemEquals(tc, BBOASNC_EYE, msgAndCtx.serviceNameContextArea.serviceNameContext.header.eye, 8 );
    CuAssertIntEquals(tc, BBOASNC_Identifier, msgAndCtx.serviceNameContextArea.serviceNameContext.header.contextId);
    CuAssertIntEquals(tc, expectedContextLen, msgAndCtx.serviceNameContextArea.serviceNameContext.header.contextLen);
    CuAssertIntEquals(tc, expectedTotalContextLen, getWolaMessageContextTotalLength( (WolaMessageContextHeader_t *) &msgAndCtx.serviceNameContextArea.serviceNameContext ));
    CuAssertIntEquals(tc, serviceNameLength, msgAndCtx.serviceNameContextArea.serviceNameContext.nameLength);
    CuAssertStrEquals_Msg(tc, "Request service name should be ABCDEFGHI", inputRequestServiceName, msgAndCtx.serviceNameContextArea.name);

    // Test zero length passed in and null string
    memset(&msgAndCtx, 0, sizeof(msgAndCtx));
    memset(inputRequestServiceName, 0, 1);    // null string
    rc = buildServiceNameContext(&(msgAndCtx.serviceNameContextArea.serviceNameContext),
                                 msgAndCtx.serviceNameContextArea.name,
                                 0, // pass in zero length
                                 inputRequestServiceName,
                                 2 /* KEY */);
    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 8", 8, rc);


    // Test zero length passed in with a valid string
    memset(&msgAndCtx, 0, sizeof(msgAndCtx));
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    memcpy(inputRequestServiceName, "ZBCDEFGHI", 9);
    serviceNameLength = strlen(inputRequestServiceName);
    rc = buildServiceNameContext(&(msgAndCtx.serviceNameContextArea.serviceNameContext),
                                 msgAndCtx.serviceNameContextArea.name,
                                 0, // pass in zero length
                                 inputRequestServiceName,
                                 2 /* KEY */);
    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 0", 0, rc);
    CuAssertIntEquals(tc, BBOASNC_VERSION_1, msgAndCtx.serviceNameContextArea.serviceNameContext.version);
    CuAssertMemEquals(tc, BBOASNC_EYE, msgAndCtx.serviceNameContextArea.serviceNameContext.header.eye, 8 );
    CuAssertIntEquals(tc, BBOASNC_Identifier, msgAndCtx.serviceNameContextArea.serviceNameContext.header.contextId);
    CuAssertIntEquals(tc, serviceNameLength, msgAndCtx.serviceNameContextArea.serviceNameContext.nameLength);
    CuAssertStrEquals_Msg(tc, "Request service name should be ZBCDEFGHI", inputRequestServiceName, msgAndCtx.serviceNameContextArea.name);




    // Test zero length passed in with a string larger than 256
    char inputRequestServiceNameBig[512];
    memset(&msgAndCtx, 0, sizeof(msgAndCtx));
    memset(inputRequestServiceNameBig, 0, sizeof(inputRequestServiceNameBig));
    memset(inputRequestServiceNameBig, 'A', sizeof(inputRequestServiceNameBig) - 1);

    rc = buildServiceNameContext(&(msgAndCtx.serviceNameContextArea.serviceNameContext),
                                 msgAndCtx.serviceNameContextArea.name,
                                 0, // pass in zero length
                                 inputRequestServiceNameBig,
                                 2 /* KEY */);
    CuAssertIntEquals_Msg(tc, "buildServiceNameContext return code should be 0", 0, rc);
    CuAssertIntEquals(tc, BBOASNC_VERSION_1, msgAndCtx.serviceNameContextArea.serviceNameContext.version);
    CuAssertMemEquals(tc, BBOASNC_EYE, msgAndCtx.serviceNameContextArea.serviceNameContext.header.eye, 8 );
    CuAssertIntEquals(tc, BBOASNC_Identifier, msgAndCtx.serviceNameContextArea.serviceNameContext.header.contextId);
    CuAssertMemEquals(tc, inputRequestServiceNameBig, msgAndCtx.serviceNameContextArea.name, BBOA_REQUEST_SERVICE_NAME_MAX );
    CuAssertIntEquals(tc, BBOA_REQUEST_SERVICE_NAME_MAX, msgAndCtx.serviceNameContextArea.serviceNameContext.nameLength);

    println(__FUNCTION__ ": exit");
}

/**
 *
 */
void test_buildWolaMessageResponseHeader(CuTest * tc) {

    WolaMessage_t messageHeader;
    memset(&messageHeader, 0, sizeof(messageHeader));

    int workType = 0;
    int requestId = 1;
    int responseDataLength = 8;
    int responseException = 0;

    WolaMessage_t * messageHeader_p = buildWolaMessageResponseHeader(&messageHeader,
                                                                     workType,
                                                                     requestId,
                                                                     responseDataLength,
                                                                     responseException);
    CuAssertMemEquals(tc, BBOAMSG_EYE, messageHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOAMSG_VERSION_2, messageHeader.amsgver );
    CuAssertIntEquals(tc, sizeof(WolaMessage_t) + responseDataLength, messageHeader.totalMessageSize );
    CuAssertIntEquals(tc, workType, messageHeader.workType );
    CuAssertIntEquals(tc, requestId, messageHeader.requestId);
    CuAssertIntEquals(tc, WOLA_MESSAGE_TYPE_RESPONSE, messageHeader.messageType);
    CuAssertIntEquals(tc, sizeof(WolaMessage_t), messageHeader.dataAreaOffset);
    CuAssertIntEquals(tc, responseDataLength, messageHeader.dataAreaLength);
    CuAssertIntEquals(tc, responseException, messageHeader.responseException);
    CuAssertIntEquals(tc, 0, messageHeader.contextAreaOffset);
    CuAssertIntEquals(tc, 0, messageHeader.contextAreaLength);

    CuAssertPtrEquals(tc, &messageHeader, messageHeader_p);
}

/**
 *
 */
void test_buildWolaMessageResponseHeaderWithException(CuTest * tc) {

    WolaMessage_t messageHeader;
    memset(&messageHeader, 0, sizeof(messageHeader));

    int workType = 0;
    int requestId = 2;
    int responseDataLength = 16; 
    int responseException = 1;

    WolaMessage_t * messageHeader_p = buildWolaMessageResponseHeader(&messageHeader,
                                                                     workType,
                                                                     requestId,
                                                                     responseDataLength,
                                                                     responseException);
    CuAssertMemEquals(tc, BBOAMSG_EYE, messageHeader.eye, 8 );
    CuAssertIntEquals(tc, BBOAMSG_VERSION_2, messageHeader.amsgver );
    CuAssertIntEquals(tc, sizeof(WolaMessage_t) + responseDataLength, messageHeader.totalMessageSize );
    CuAssertIntEquals(tc, workType, messageHeader.workType );
    CuAssertIntEquals(tc, requestId, messageHeader.requestId);
    CuAssertIntEquals(tc, WOLA_MESSAGE_TYPE_RESPONSE, messageHeader.messageType);
    CuAssertIntEquals(tc, sizeof(WolaMessage_t), messageHeader.dataAreaOffset);
    CuAssertIntEquals(tc, responseDataLength, messageHeader.dataAreaLength);
    CuAssertIntEquals(tc, responseException, messageHeader.responseException);
    CuAssertIntEquals(tc, 0, messageHeader.contextAreaOffset);
    CuAssertIntEquals(tc, 0, messageHeader.contextAreaLength);

    CuAssertPtrEquals(tc, &messageHeader, messageHeader_p);
}

/**
 * 
 */
void test_getWolaMessageContextTotalLength( CuTest * tc) {
    println(__FUNCTION__ ": enter");
   
    WolaMessageContextHeader_t context1;
    context1.contextLen = 8;

    int expectedTotalLen = sizeof(context1) + context1.contextLen;

    CuAssertIntEquals(tc, expectedTotalLen, getWolaMessageContextTotalLength( &context1 ));

    println(__FUNCTION__ ": exit");
}

/**
 * 
 */
void test_getFirstContext(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    typedef struct FullContextArea {
        WolaMessageContextAreaHeader_t contextAreaHeader;
        WolaMessageContextHeader_t context1;
        char context1Data[8];
        WolaMessageContextHeader_t context2;
        char context2Data[16];
    } FullContextArea_t;

    // Initialize the context area and contexts.
    // Only setting the stuff we need (don't care about the eyecatchers and stuff).
    FullContextArea_t fullContextArea;
    memset(&fullContextArea, 0, sizeof(fullContextArea));

    initializeContextHeader( &fullContextArea.contextAreaHeader, 2 );
    fullContextArea.context1.contextId = 1;
    fullContextArea.context1.contextLen = sizeof(fullContextArea.context1Data);
    fullContextArea.context2.contextId = 2;
    fullContextArea.context2.contextLen = sizeof(fullContextArea.context2Data);

    CuAssertPtrEquals(tc, &fullContextArea.context1, getFirstContext( &fullContextArea.contextAreaHeader ));
    CuAssertPtrEquals(tc, &fullContextArea.context1, getFirstContext( &fullContextArea.contextAreaHeader ));

    // Set numContexts to 0, verify getFirstContext returns null.
    fullContextArea.contextAreaHeader.numContexts = 0;
    CuAssertPtrIsNull(tc, getFirstContext( &fullContextArea.contextAreaHeader ));

    println(__FUNCTION__ ": exit");
}

/**
 * 
 */
void test_getNextContext( CuTest * tc ) {

    println(__FUNCTION__ ": entry");

    typedef struct FullContextArea {
        WolaMessageContextAreaHeader_t contextAreaHeader;
        WolaMessageContextHeader_t context1;
        char context1Data[8];
        WolaMessageContextHeader_t context2;
        char context2Data[16];
        WolaMessageContextHeader_t context3;
        char context3Data[12];
    } FullContextArea_t;

    // Initialize the context area and contexts.
    // Only setting the stuff we need (don't care about the eyecatchers and stuff).
    FullContextArea_t fullContextArea;
    memset(&fullContextArea, 0, sizeof(fullContextArea));

    initializeContextHeader( &fullContextArea.contextAreaHeader, 3 );
    fullContextArea.context1.contextId = 1;
    fullContextArea.context1.contextLen = sizeof(fullContextArea.context1Data);
    fullContextArea.context2.contextId = 2;
    fullContextArea.context2.contextLen = sizeof(fullContextArea.context2Data);
    fullContextArea.context3.contextId = 3;
    fullContextArea.context3.contextLen = sizeof(fullContextArea.context3Data);

    CuAssertPtrEquals(tc, &fullContextArea.context1, getFirstContext( &fullContextArea.contextAreaHeader ));
    CuAssertPtrEquals(tc, &fullContextArea.context2, getNextContext( getFirstContext( &fullContextArea.contextAreaHeader ) ));
    CuAssertPtrEquals(tc, &fullContextArea.context3, getNextContext( &fullContextArea.context2 ));

    println(__FUNCTION__ ": exit");
}

/**
 * 
 */
void test_extractServiceName( CuTest * tc ) {

    println(__FUNCTION__ ": entry");

    WolaServiceNameContextArea_t wolaServiceNameContextArea;
    memset(&wolaServiceNameContextArea, 0, sizeof(wolaServiceNameContextArea));

    char * serviceName = "abcdefg";
    
    buildServiceNameContext(&wolaServiceNameContextArea.serviceNameContext,
                            &wolaServiceNameContextArea.name[0],
                            strlen(serviceName),
                            serviceName,
                            2 /* KEY */);

    char serviceNameBuffer[ strlen(serviceName) + 1 ];

    int rc = extractServiceName( &serviceNameBuffer[0], &wolaServiceNameContextArea.serviceNameContext ) ;

    CuAssertIntEquals(tc, 0, rc);
    CuAssertStrEquals(tc, serviceName, serviceNameBuffer);

    // set name length too big
    wolaServiceNameContextArea.serviceNameContext.nameLength = 257;
    rc = extractServiceName( &serviceNameBuffer[0], &wolaServiceNameContextArea.serviceNameContext ) ;

    CuAssertIntEquals(tc, 8, rc);

    println(__FUNCTION__ ": exit");
}

/** 
 *
 */
void test_getWolaMessageContext(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    typedef struct FullContextArea {
        WolaMessageContextAreaHeader_t contextAreaHeader;
        WolaMessageContextHeader_t context1;
        char context1Data[8];
        WolaMessageContextHeader_t context2;
        char context2Data[16];
    } FullContextArea_t;

    // Initialize the context area and contexts.
    // Only setting the stuff we need (don't care about the eyecatchers and stuff).
    FullContextArea_t fullContextArea;
    memset(&fullContextArea, 0, sizeof(fullContextArea));

    initializeContextHeader( &fullContextArea.contextAreaHeader, 2 );
    fullContextArea.context1.contextId = 1;
    fullContextArea.context1.contextLen = sizeof(fullContextArea.context1Data);
    fullContextArea.context2.contextId = 2;
    fullContextArea.context2.contextLen = sizeof(fullContextArea.context2Data);

    CuAssertPtrEquals(tc, &fullContextArea.context2, getWolaMessageContext( &fullContextArea.contextAreaHeader, 2 ));
    CuAssertPtrEquals(tc, &fullContextArea.context2, getWolaMessageContext( &fullContextArea.contextAreaHeader, 2 ));
    CuAssertPtrEquals(tc, &fullContextArea.context1, getWolaMessageContext( &fullContextArea.contextAreaHeader, 1 ));
    CuAssertPtrEquals(tc, &fullContextArea.context1, getWolaMessageContext( &fullContextArea.contextAreaHeader, 1 ));
    CuAssertPtrIsNull(tc, getWolaMessageContext( &fullContextArea.contextAreaHeader, 3 ));
    CuAssertPtrIsNull(tc, getWolaMessageContext( &fullContextArea.contextAreaHeader, 0 ));

    println(__FUNCTION__ ": exit");
}

/**
 * Just making sure that printf/vsnprintf works like i think it should.
 */
void test_printf(CuTest * tc) {

    char buffer[16];
    snprintf(buffer, 16, "blah x%llx blah", 0x33L);

    CuAssertStrEquals(tc, "blah x33 blah", buffer);

    // TODO: has snprintf behavior changed? Not null-terminating if truncated?
    // snprintf(buffer, 16, "blah x%llx blah blah blah", 0x33L);
    // CuAssertStrEquals(tc, "blah x33 blah b", buffer);
    // CuAssertIntEquals(tc, 0, buffer[15]);     // verify it's null-term'ed.
    
    int rc = snprintf(buffer, 16, "blah x%llx blah blah blah", 0x33L);
    CuAssertStrEquals(tc, "blah x33 blah bl", buffer);
    CuAssertIntEquals(tc, 23, rc);     
}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_wola_message_test_suite() {

    CuSuite* suite = CuSuiteNew("server_wola_message_test");

    SUITE_ADD_TEST(suite, test_setWolaMessageServerNameFromRegistration);
    SUITE_ADD_TEST(suite, test_initializeMessageHeader);
    SUITE_ADD_TEST(suite, test_initializeContextHeader);
    SUITE_ADD_TEST(suite, test_setDataAreaLengthAndOffset);
    SUITE_ADD_TEST(suite, test_setContextAreaLengthAndOffset);
    SUITE_ADD_TEST(suite, test_buildWolaMessageAndContextArea);
    SUITE_ADD_TEST(suite, test_propagateMvsUserId);
    SUITE_ADD_TEST(suite, test_propagateCicsUserId);
    SUITE_ADD_TEST(suite, test_BBGZ_resetHighOrderBit);
    SUITE_ADD_TEST(suite, test_getServiceNameFromContext);
    SUITE_ADD_TEST(suite, test_getServiceNameFromContextMultipleContextsInMsg);
    SUITE_ADD_TEST(suite, test_buildServiceNameContext);
    SUITE_ADD_TEST(suite, test_buildWolaMessageResponseHeader);
    SUITE_ADD_TEST(suite, test_buildWolaMessageResponseHeaderWithException);
    SUITE_ADD_TEST(suite, test_getWolaMessageContext);
    SUITE_ADD_TEST(suite, test_getWolaMessageContextTotalLength);
    SUITE_ADD_TEST(suite, test_getFirstContext);
    SUITE_ADD_TEST(suite, test_getNextContext);
    SUITE_ADD_TEST(suite, test_extractServiceName);
    SUITE_ADD_TEST(suite, test_printf);

    return suite;
}


