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

#include "../include/server_wola_cics_link_server.h"
#include "../include/server_wola_client.h"
#include "../include/server_wola_message.h"

#include "include/CuTest.h"

extern void println(char * message_p, ...);
extern void initializeBboauctx(struct BBOAUCTX * bboauctx_p);
extern void processSecurityContext(WolaMessage_t * wolaMessage_p, struct BBOAUCTX * bboauctx_p);
extern void processCicsLinkServerContext (WolaCicsLinkServerContext_t * wolaCicsLinkServerContext_p, struct BBOAUCTX * bboauctx_p);

#define testMvsUserID "WOLATEST"
#define testTranID "1111"
#define testChanID "2222222222222222"
#define testReqContID "3333333333333333"
#define testRspContID "4444444444444444"
#define testChanType 5
#define testReqContType 6
#define testRspContType 7

int isAllBlanks(unsigned char * inField, int length) {
    for (int i = 0; i < length; i++) {
        if (!(inField[i] == ' ')) {
            return 0;
        }
    }
    return 1;
}

int isAllNulls(unsigned char * inField, int length) {
    for (int i = 0; i < length; i++) {
        if (!(inField[i] == 0)) {
            return 0;
        }
    }
    return 1;
}

void test_getSecurityContext(CuTest * tc) {
    struct BBOAUCTX bboauctx;
    initializeBboauctx(&bboauctx);
    bboauctx.auctxsecdata.ausecflags.ausecflg_propsec = 1;

    struct wolaMessage testMessage;
    memset(&testMessage, 0, sizeof(testMessage));

    // Load the security context data, should see no userid and set the security propagation flag to 0
    processSecurityContext(&testMessage, &bboauctx);
    CuAssertTrue(tc, bboauctx.auctxsecdata.ausecflags.ausecflg_propsec == 0);

    memcpy(&(testMessage.mvsUserID), &testMvsUserID, sizeof(testMessage.mvsUserID));
    initializeBboauctx(&bboauctx);

    // Reload security context, now should see the userid present, set the flag to 1 and copy the userid
    processSecurityContext(&testMessage, &bboauctx);
    CuAssertTrue(tc, bboauctx.auctxsecdata.ausecflags.ausecflg_propsec == 1);
    CuAssertTrue(tc, memcmp(&(bboauctx.auctxsecdata.ausecuserid), &testMvsUserID,
                            sizeof(bboauctx.auctxsecdata.ausecuserid)) == 0);
}

void test_getCicsLinkServerContext(CuTest * tc) {
    struct BBOAUCTX bboauctx;
    initializeBboauctx(&bboauctx);

    struct wolaCicsLinkServerContext cicsContext;
    memset(&cicsContext, 0, sizeof(cicsContext));

    // All null/defaults
    processCicsLinkServerContext(&cicsContext, &bboauctx);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_commarea == 1);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_container == 0);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_channel == 0);
    CuAssertTrue(tc, isAllBlanks(bboauctx.auctxcicsdata.aucicslnktranid, sizeof(bboauctx.auctxcicsdata.aucicslnktranid)));
    CuAssertTrue(tc, isAllNulls(bboauctx.auctxcicsdata.aucicslnkreqcontid, sizeof(bboauctx.auctxcicsdata.aucicslnkreqcontid)));
    CuAssertTrue(tc, isAllNulls(bboauctx.auctxcicsdata.aucicslnkrspcontid, sizeof(bboauctx.auctxcicsdata.aucicslnkrspcontid)));
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicslnkreqconttype == 0);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicslnkrspconttype == 0);

    // Set some actual data
    memcpy(cicsContext.linkTaskTranID, testTranID, sizeof(cicsContext.linkTaskTranID));
    memcpy(cicsContext.linkTaskReqContID, testReqContID, sizeof(cicsContext.linkTaskReqContID));
    memcpy(cicsContext.linkTaskRspContID, testRspContID, sizeof(cicsContext.linkTaskRspContID));
    cicsContext.linkTaskReqContType = testReqContType;
    cicsContext.linkTaskRspContType = testRspContType;
    cicsContext.useCICSContainer = 1;

    initializeBboauctx(&bboauctx);

    // Should detect container mode and copy in appropriate fields
    processCicsLinkServerContext(&cicsContext, &bboauctx);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_commarea == 0);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_container == 1);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_channel == 0);
    CuAssertTrue(tc, memcmp(bboauctx.auctxcicsdata.aucicslnktranid, testTranID, sizeof(bboauctx.auctxcicsdata.aucicslnktranid)) == 0);
    CuAssertTrue(tc, memcmp(bboauctx.auctxcicsdata.aucicslnkreqcontid, testReqContID, sizeof(bboauctx.auctxcicsdata.aucicslnkreqcontid)) == 0);
    CuAssertTrue(tc, memcmp(bboauctx.auctxcicsdata.aucicslnkrspcontid, testRspContID, sizeof(bboauctx.auctxcicsdata.aucicslnkrspcontid)) == 0);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicslnkreqconttype == testReqContType);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicslnkrspconttype == testRspContType);

    // Add channel data
    memcpy(cicsContext.linkTaskChanID, testChanID, sizeof(cicsContext.linkTaskChanID));
    cicsContext.linkTaskChanType = testChanType;

    initializeBboauctx(&bboauctx);

    // With channel data present it should detect channel mode
    processCicsLinkServerContext(&cicsContext, &bboauctx);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_commarea == 0);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_container == 0);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicsflags.aucicsflg_channel == 1);
    CuAssertTrue(tc, memcmp(bboauctx.auctxcicsdata.aucicslnktranid, testTranID, sizeof(bboauctx.auctxcicsdata.aucicslnktranid)) == 0);
    CuAssertTrue(tc, memcmp(bboauctx.auctxcicsdata.aucicslnkreqcontid, testChanID, sizeof(bboauctx.auctxcicsdata.aucicslnkreqcontid)) == 0);
    CuAssertTrue(tc, memcmp(bboauctx.auctxcicsdata.aucicslnkrspcontid, testRspContID, sizeof(bboauctx.auctxcicsdata.aucicslnkrspcontid)) == 0);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicslnkreqconttype == testChanType);
    CuAssertTrue(tc, bboauctx.auctxcicsdata.aucicslnkrspconttype == testRspContType);
}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_wola_client_get_context_test_suite() {
    CuSuite* suite = CuSuiteNew("server_wola_client_get_context_test");
    SUITE_ADD_TEST(suite, test_getSecurityContext);
    SUITE_ADD_TEST(suite, test_getCicsLinkServerContext);
    return suite;
}
