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
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../include/ieantc.h"
#include "../include/server_wola_client.h"
#include "../include/server_wola_shared_memory_anchor.h"

#include "include/CuTest.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 *
 */

extern void println(char * messgae_p, ...);
extern int  getRegisterNameToken(char* registerName_p, char* token);
extern void getRegisterTokenName(char * token_name_p, char * registerName_p);
extern int createRegistrationNameToken(WolaRegistration_t* registration_p, char* registerName_p);
extern WolaRegistration_t* getWolaRegistration(char* registerName_p, GetWolaRegistrationData_t* registrationData_p);

int deleteRegistrationNameToken(char* registerName_p) {
    int rc = -1;
    char token_name[16];

    getRegisterTokenName(token_name, registerName_p);

    iean4dl(IEANT_PRIMARY_LEVEL,    // TODO twas used this
            token_name,
            &rc);

    return rc;
}

/**
 * Test.
 */
void test_getRegisterTokenName(CuTest * tc) {

    println(__FUNCTION__ ": entry");
    char regname[12];
    char token_name[16];
    memset(regname,' ',sizeof(regname)); /* regname is blank padded  */
    memcpy(regname,"OLABC001",8);
    getRegisterTokenName(token_name, regname) ;
    println(__FUNCTION__ ": built wola tokenname: %s", token_name);
    CuAssertMemEquals(tc, "BBOZOLABC001", token_name, 12);
    println(__FUNCTION__ ": exit");
}

/**
 * Test.
 */
void test_getRegisterNameToken(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    struct register_name_token_map token;
    WolaRegistration_t* registration_p;
    WolaRegistration_t registration;
    char* regname0 = "REGISTRATION";
    int skrc =-1;
    skrc = getRegisterNameToken(regname0, (char*)&token);
    CuAssertIntEquals(tc, 4, skrc); // token shouldnot exist now

    //create registration Name token
    memset(&registration, 0, sizeof(registration));
    strcpy(registration.registrationName, regname0);
    println(__FUNCTION__ ": Registration name:%s", registration.registrationName);
    int rc =  createRegistrationNameToken(&registration, regname0);
    CuAssertIntEquals(tc, 0, rc);


    skrc = getRegisterNameToken(regname0, (char*)&token);
    CuAssertIntEquals(tc, 0, skrc); // token should exist now

    // get the wolaRegistration object based on the registration name
    GetWolaRegistrationData_t wolaRegistrationData;
    WolaRegistration_t* regEntry_p = getWolaRegistration(regname0, &wolaRegistrationData);
    CuAssertPtrNotNull(tc,regEntry_p);
    println(__FUNCTION__ ": RegisterName from the created token: %s",regEntry_p->registrationName);

    //delete the name token
    int delrc = deleteRegistrationNameToken(regname0);
    CuAssertIntEquals(tc, 0, delrc); // token should be deleted

    //verify that token is not there
    skrc = getRegisterNameToken(regname0, (char*)&token);
    CuAssertIntEquals(tc, 4, skrc); // token shouldnot exist now
    println(__FUNCTION__ ": exit");
}

/**
 * Test.
 */
void test_getServiceName(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    println(__FUNCTION__ ": test getServiceName return code 4");
    char inputRequestServiceName[257];
    char outputRequestServiceName[257];
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    unsigned int rc = getServiceName(sizeof(inputRequestServiceName)+1, inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 4", 4, rc);

    println(__FUNCTION__ ": test getServiceName return code 8");
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    rc = getServiceName(0, inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 8", 8, rc);

    println(__FUNCTION__ ": test getServiceName one byte null terminated, input length of 0");
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    strcpy(inputRequestServiceName, "A");
    memset(outputRequestServiceName, 0, sizeof(inputRequestServiceName));
    rc = getServiceName(0, inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "getServiceName output service name should be A", "A", outputRequestServiceName);
    CuAssertStrEquals_Msg(tc, "getServiceName input service name should be A", "A", inputRequestServiceName);

    println(__FUNCTION__ ": test getServiceName one byte, input length of one");
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    strcpy(inputRequestServiceName, "B");
    memset(outputRequestServiceName, 0, sizeof(inputRequestServiceName));
    rc = getServiceName(strlen(inputRequestServiceName), inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "getServiceName output service name should be B", "B", outputRequestServiceName);
    CuAssertStrEquals_Msg(tc, "getServiceName input service name should be B", "B", inputRequestServiceName);

    println(__FUNCTION__ ": test getServiceName 5 bytes null terminated, input length of 0");
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    strcpy(inputRequestServiceName, "A2345");
    memset(outputRequestServiceName, 0, sizeof(inputRequestServiceName));
    rc = getServiceName(0, inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "getServiceName output service name should be A2345", "A2345", outputRequestServiceName);
    CuAssertStrEquals_Msg(tc, "getServiceName input service name should be A2345", "A2345", inputRequestServiceName);

    println(__FUNCTION__ ": test getServiceName 5 bytes, input length of five");
    memset(inputRequestServiceName, 0, sizeof(inputRequestServiceName));
    strcpy(inputRequestServiceName, "12345");
    memset(outputRequestServiceName, 0, sizeof(inputRequestServiceName));
    rc = getServiceName(strlen(inputRequestServiceName), inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "getServiceName output service name should be 12345", "12345", outputRequestServiceName);
    CuAssertStrEquals_Msg(tc, "getServiceName input service name should be 12345", "12345", inputRequestServiceName);

    println(__FUNCTION__ ": test getServiceName 256 bytes, input length of 0");
    memset(inputRequestServiceName, 'Z', sizeof(inputRequestServiceName));
    memset(outputRequestServiceName, 0, sizeof(inputRequestServiceName));
    char expectedRequestServiceName[257];
    memset(expectedRequestServiceName, 0, sizeof(expectedRequestServiceName));
    memset(expectedRequestServiceName, 'Z', sizeof(expectedRequestServiceName)-1);
    rc = getServiceName(0, inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "getServiceName output service name should be 256 Zs", expectedRequestServiceName, outputRequestServiceName);

    println(__FUNCTION__ ": test getServiceName 256 bytes, input length of 256");
    memset(inputRequestServiceName, 'G', sizeof(inputRequestServiceName));
    memset(outputRequestServiceName, 0, sizeof(inputRequestServiceName));
    memset(expectedRequestServiceName, 0, sizeof(expectedRequestServiceName));
    memset(expectedRequestServiceName, 'G', sizeof(expectedRequestServiceName)-1);
    rc = getServiceName(256, inputRequestServiceName, 2, outputRequestServiceName);
    CuAssertIntEquals_Msg(tc, "getServiceName return code should be 0", 0, rc);
    CuAssertStrEquals_Msg(tc, "getServiceName output service name should be 256 Gs", expectedRequestServiceName, outputRequestServiceName);

}

/**
 * Test.
 */
void test_ploUsedInClient(CuTest * tc) {

    println(__FUNCTION__ ": entry");


    println(__FUNCTION__ ": Test getHandleState success");
    WolaClientConnectionHandle_t clientConnectionHandle;
    WolaConnectionHandle_t wolaConnectionHandle;

    unsigned long long returnedState = 0;
    clientConnectionHandle.instanceCount = 99;
    clientConnectionHandle.handle_p = &wolaConnectionHandle;

    memset(&(wolaConnectionHandle.ploArea), 0, sizeof(wolaConnectionHandle.ploArea));
    wolaConnectionHandle.ploArea.instanceCount = 99;
    wolaConnectionHandle.state = 16;

    int rc = getHandleState(&clientConnectionHandle, &returnedState);
    CuAssertIntEquals_Msg(tc, "getHandleState rc should be 0", 0, rc);
    CuAssertLongLongEquals_Msg(tc, "getHandleState state should be 16", 16, returnedState);
    CuAssertIntEquals_Msg(tc, "getHandleState instance count should be 99", 99, wolaConnectionHandle.ploArea.instanceCount);

    println(__FUNCTION__ ": Test getHandleState fail");
    clientConnectionHandle.instanceCount = 98;
    returnedState = 0;
    rc = getHandleState(&clientConnectionHandle, &returnedState);
    CuAssertIntEquals_Msg(tc, "getHandleState fail rc should be 1", 1, rc);
    CuAssertLongLongEquals_Msg(tc, "getHandleState state should be 0", 0, returnedState);
    CuAssertIntEquals_Msg(tc, "getHandleState instance count should be 99", 99, wolaConnectionHandle.ploArea.instanceCount);

    //----------------------------------------------------------

    println(__FUNCTION__ ": Test setMessageAndContextAreas success");
    clientConnectionHandle.instanceCount = 99;
    wolaConnectionHandle.ploArea.instanceCount = 99;
    wolaConnectionHandle.cachedMessage_p = (void*) 0;
    wolaConnectionHandle.cachedContexts_p = (void*) 0;
    void* message_p = (void*) 11;
    void* contexts_p = (void*) 22;
    rc = setMessageAndContextAreas(&clientConnectionHandle, message_p, contexts_p);
    CuAssertIntEquals_Msg(tc, "setMessageAndContextAreas rc should be 0", 0, rc);
    CuAssertIntEquals_Msg(tc, "setMessageAndContextAreas instance count should be 99", 99, wolaConnectionHandle.ploArea.instanceCount);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas message_p should be 11", (void*) 11, message_p);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas contexts_p should be 22", (void*) 22, contexts_p);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas cachedMessage_p should be 11", (void*) 11, wolaConnectionHandle.cachedMessage_p);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas cachedContexts_p should be 22", (void*) 22, wolaConnectionHandle.cachedContexts_p);

    println(__FUNCTION__ ": Test setMessageAndContextAreas fail");
    clientConnectionHandle.instanceCount = 92;
    wolaConnectionHandle.ploArea.instanceCount = 99;
    wolaConnectionHandle.cachedMessage_p = (void*) 0;
    wolaConnectionHandle.cachedContexts_p = (void*) 0;
    message_p = (void*) 11;
    contexts_p = (void*) 22;
    rc = setMessageAndContextAreas(&clientConnectionHandle, message_p, contexts_p);
    CuAssertIntEquals_Msg(tc, "setMessageAndContextAreas rc should be 1", 1, rc);
    CuAssertIntEquals_Msg(tc, "setMessageAndContextAreas instance count should be 99", 99, wolaConnectionHandle.ploArea.instanceCount);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas message_p should be 11", (void*) 11, message_p);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas contexts_p should be 22", (void*) 22, contexts_p);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas cachedMessage_p should be 0", (void*) 0, wolaConnectionHandle.cachedMessage_p);
    CuAssertPtrEquals_Msg(tc, "setMessageAndContextAreas cachedContexts_p should be 0", (void*) 0, wolaConnectionHandle.cachedContexts_p);


    //----------------------------------------------------------

    println(__FUNCTION__ ": Test getMessageAndContextAreas success");
    clientConnectionHandle.instanceCount = 99;
    wolaConnectionHandle.ploArea.instanceCount = 99;
    wolaConnectionHandle.cachedMessage_p = (void*) 1;
    wolaConnectionHandle.cachedContexts_p = (void*) 2;
    message_p = 0;
    contexts_p = 0;
    rc = getMessageAndContextAreas(&clientConnectionHandle, &message_p, &contexts_p);
    CuAssertIntEquals_Msg(tc, "getMessageAndContextAreas rc should be 0", 0, rc);
    CuAssertIntEquals_Msg(tc, "getMessageAndContextAreas instance count should be 99", 99, wolaConnectionHandle.ploArea.instanceCount);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas message_p should be 1", (void*) 1, message_p);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas contexts_p should be 2", (void*) 2, contexts_p);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas cachedMessage_p should be 0", (void*) 0, wolaConnectionHandle.cachedMessage_p);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas cachedContexts_p should be 0", (void*) 0, wolaConnectionHandle.cachedContexts_p);

    println(__FUNCTION__ ": Test getMessageAndContextAreas fail");
    clientConnectionHandle.instanceCount = 91;
    wolaConnectionHandle.ploArea.instanceCount = 99;
    wolaConnectionHandle.cachedMessage_p = (void*) 1;
    wolaConnectionHandle.cachedContexts_p = (void*) 2;
    message_p = 0;
    contexts_p = 0;
    rc = getMessageAndContextAreas(&clientConnectionHandle, &message_p, &contexts_p);
    CuAssertIntEquals_Msg(tc, "getMessageAndContextAreas rc should be 1", 1, rc);
    CuAssertIntEquals_Msg(tc, "getMessageAndContextAreas  client instance count should be 91", 91, clientConnectionHandle.instanceCount);
    CuAssertIntEquals_Msg(tc, "getMessageAndContextAreas instance count should be 99", 99, wolaConnectionHandle.ploArea.instanceCount);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas message_p should be 0", (void*) 0, message_p);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas contexts_p should be 0", (void*) 0, contexts_p);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas cachedMessage_p should be 1", (void*) 1, wolaConnectionHandle.cachedMessage_p);
    CuAssertPtrEquals_Msg(tc, "getMessageAndContextAreas cachedContexts_p should be 2", (void*) 2, wolaConnectionHandle.cachedContexts_p);

    //----------------------------------------------------------

    println(__FUNCTION__ ": test changeHandleState success");

    WolaConnectionHandle_t wolaConnectionHandle2;
    WolaClientConnectionHandle_t clientConnectionHandle2;
    memset(&wolaConnectionHandle2, 0, sizeof(wolaConnectionHandle2));
    wolaConnectionHandle2.ploArea.instanceCount = 16;
    wolaConnectionHandle2.state = 1;

    clientConnectionHandle2.instanceCount = 16;
    clientConnectionHandle2.handle_p = &wolaConnectionHandle2;

    rc = changeHandleState(&clientConnectionHandle2, 1, 2);
    CuAssertIntEquals_Msg(tc, "changeHandleState rc should be 0", 0, rc);
    CuAssertIntEquals_Msg(tc, "changeHandleState count should be 16", 16, clientConnectionHandle2.instanceCount);
    CuAssertIntEquals_Msg(tc, "changeHandleState plo count should be 16", 16, wolaConnectionHandle2.ploArea.instanceCount);
    CuAssertIntEquals_Msg(tc, "changeHandleState state should be 2", 2, wolaConnectionHandle2.state);

    println(__FUNCTION__ ": test changeHandleState fail");

    memset(&wolaConnectionHandle2, 0, sizeof(wolaConnectionHandle2));
    wolaConnectionHandle2.ploArea.instanceCount = 15;
    wolaConnectionHandle2.state = 1;

    clientConnectionHandle2.instanceCount = 16;
    clientConnectionHandle2.handle_p = &wolaConnectionHandle2;

    rc = changeHandleState(&clientConnectionHandle2, 1, 2);
    CuAssertIntEquals_Msg(tc, "changeHandleState rc should be 1", 1, rc);
    CuAssertIntEquals_Msg(tc, "changeHandleState count should be 16", 16, clientConnectionHandle2.instanceCount);
    CuAssertIntEquals_Msg(tc, "changeHandleState plo count should be 15", 15, wolaConnectionHandle2.ploArea.instanceCount);
    CuAssertIntEquals_Msg(tc, "changeHandleState state should be 1", 1, wolaConnectionHandle2.state);


    println(__FUNCTION__ ": exit");
}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_wola_client_test_suite() {

    CuSuite* suite = CuSuiteNew("server_wola_client_test");
    SUITE_ADD_TEST(suite, test_getRegisterTokenName);
    SUITE_ADD_TEST(suite, test_getRegisterNameToken);
    SUITE_ADD_TEST(suite, test_getServiceName);
    SUITE_ADD_TEST(suite, test_ploUsedInClient);
    return suite;
}


