#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/time.h>

#include "include/cmqc.h"
#include "include/jbatch_utils.h"

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

void mq_inquireQname(MQHCONN Hconn, MQHOBJ Hobj, MQCHAR48 qName) {

    #define _selectors 1
    #define _intAttrs 1

    MQLONG select[_selectors] = {MQCA_Q_NAME};
    MQLONG intAttrs[_intAttrs];
    MQLONG CompCode, Reason;
    MQINQ(Hconn, Hobj, _selectors, select, _intAttrs, intAttrs, MQ_Q_NAME_LENGTH, qName, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        jnu_trace(__FUNCTION__,"MQINQ failed with Condition code %d and Reason %d\n", CompCode, Reason);
        strcpy(qName, "unknown queue");
    }

    return;
}

int mq_inquireMaxMsgLength(MQHCONN Hconn, MQHOBJ Hobj) {

    #define _selectors 1
    #define _intAttrs 1

    MQLONG select[_selectors] = {MQIA_MAX_MSG_LENGTH};
    MQLONG intAttrs[_intAttrs];
    MQLONG CompCode, Reason;
    MQINQ(Hconn, Hobj, _selectors, select, _intAttrs, intAttrs, 0, NULL, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        jnu_trace(__FUNCTION__,"MQINQ failed with Condition code %d and Reason %d\n", CompCode, Reason);
    }

    return intAttrs[0];
}

/**
 * Generates a correlation identifier.  This value will be passed as a job parameter with the property name com.ibm.jbatch.correlator.
 * The value will be used as the correlationId on the JMS message sent by the batch runtime.
 *
 * The MQ correlation identifier will be 24 bytes.
 *
 * The format of the 24 correlation identifier is:
 *
 * 8 bytes: Time of day value from __stck()
 * 4 bytes: Process id value from getpid()
 * 4 bytes: Randomly generated number
 * 4 bytes: Host id value from gethostid()
 * 4 bytes: Randomly generated number
 *
 * The srandom() is used to seed the random generator using a function of the
 * time of day value and the pid
 */
void generateCorrelationId(MQBYTE* buff) {

    unsigned long long timestamp;
    __stck(&timestamp);

    int pid = (int) getpid();

    // Seed the random generator
    srandom( (unsigned int) timestamp * pid);

    memset(buff, 0, 24);

    // First 8 bytes are time value
    memcpy(buff,&timestamp,8);

    // Next 4 bytes from pid value
    memcpy(buff+8,&pid,4);

    // Next 4 bytes from random number
    unsigned long numA = random();
    memcpy(buff+12,&numA,4);

    // Next 4 bytes from host id value
    unsigned int hostid = (unsigned int)gethostid ();
    memcpy(buff+16,&hostid,4);

    // Last 4 bytes from random number
    unsigned long numB = random();
    memcpy(buff+20,&numB,4);
}

