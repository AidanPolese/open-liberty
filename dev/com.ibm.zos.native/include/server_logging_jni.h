/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef SERVER_LOGGING_JNI_H_
#define SERVER_LOGGING_JNI_H_

#include <ieac.h>

#include "stack_services.h"

/**
 * Stack element containing trace data.
 */
typedef struct traceStackElement traceStackElement;
struct traceStackElement {
    concurrent_stack_element traceStackElement_header;                 /* 0x000*/
    unsigned char            traceStackElement_eyecatcher[8];          /* 0x040*/
    iea_PEToken              traceStackElement_pet;                    /* 0x048*/
    void *                   traceStackElement_tracedata_p;            /* 0x058*/
    int                      traceStackElement_traceLevel;             /* 0x060*/
    int                      traceStackElement_tracePoint;             /* 0x064*/
    unsigned long long       traceStackElement_createTime;             /* 0x068*/
    int                      traceStackElement_createTcb;              /* 0x070*/
    int                      traceStackElement_createState;            /* 0x074*/
    int                      traceStackElement_createKey;              /* 0x078*/
    unsigned int             traceStackElement_old_cs_area;            /* 0x07C*/
    unsigned int             traceStackElement_new_cs_area;            /* 0x080*/
    int                      traceStackElement_csRetCode;              /* 0x084*/
};                                                                     /* 0x088*/

/**
 * Trace thread processing data.
 */
#define TRACETHREADPROCESSINGDATA_EYE "BBGZTTPD"
typedef struct traceThreadProcessingData TraceThreadProcessingData;
struct traceThreadProcessingData {
    unsigned char            traceThreadProcessingData_eyecatcher[8];    /* 0x000*/
    unsigned char            traceThreadProcessingData_reserved[8];      /* 0x008*/
    iea_PEToken              traceThreadProcessingData_pet;              /* 0x010*/
    iea_PEToken              traceThreadProcessingData_requestor_pet;    /* 0x020*/
    concurrent_stack         traceThreadProcessingData_traceStack;       /* 0x030*/
};                                                                       /* 0x040*/



#endif /* SERVER_LOGGING_JNI_H_ */

