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
#ifndef __jbatch_native_utility_h__
#define __jbatch_native_utility_h__

#include "jbatch_json.h"
#include "jbatch_wola.h"

#define JESJOBNAME_JOB_PROP "com.ibm.ws.batch.submitter.jobName"
#define JESJOBID_JOB_PROP "com.ibm.ws.batch.submitter.jobId"

/**
 * A BatchWolaClient contains refs to the command-line args and the WolaConn.
 */
typedef struct {
    cJSON * args;
    WolaConn * wolaConn;
} BatchWolaClient;

/**
 * Map z/OS Control Blocks
 */

/* Map PSA */
typedef struct psa {
    char psastuff[548];
    struct ascb *psaaold;
    /* Ignore the rest */
} psa;

/* Map ASCB */
typedef struct ascb {
    char ascbstuff[336];
    struct assb *ascbassb;
    /* Ignore the rest */
} ascb;

/* Map ASSB */
typedef struct assb {
    char assbstuff[168];
    struct jsab *assbjsab;
    /* Ignore the rest */
} assb;

/* Map JSAB */
typedef struct jsab {
    char assbstuff[20];
    char jsabjbid[8];
    char jsabjbnm[8];
    /* Ignore the rest */
} jsab;

/**
 * The main routine for the jbatch native utility.
 */
int jnu_main(int argc, char ** argv) ;

/**
 *
 *@return The new 'topic_root/ + tree' or the default 'batch/ + tree' for event subscription.
 */
char * resolveTopicRoot( BatchWolaClient *, char *);
char * resolveTopicRoot(char *, char *);

#endif
