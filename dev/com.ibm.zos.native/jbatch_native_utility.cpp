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
#include <stdarg.h>
#include <string.h>
#include <stdio.h>
#include <pthread.h>

#include "include/jbatch_json.h"
#include "include/jbatch_task_args.h"
#include "include/jbatch_messages.h"
#include "include/jbatch_models.h"
#include "include/jbatch_native_utility.h"
#include "include/jbatch_wola.h"
#include "include/jbatch_utils.h"
#include "include/jbatch_mq.h"

#include "include/cmqc.h"

/**
 * @return a JSON request payload for the getJobExecutions request.
 */
cJSON * jnu_createRequestGetJobExecutions( int instanceId ) {

    cJSON * payload = cJSON_CreateObject_ns();
    if (payload == NULL) {
        return NULL;
    }

    cJSON_AddStringToObject(payload, "command", "getJobExecutions");
    cJSON_AddNumberToObject(payload, "instanceId", instanceId);
    return payload;
}

/**
 * @return a JSON request payload for the getJobExecution request.
 */
cJSON * jnu_createRequestGetJobExecution( int executionId ) {

    cJSON * payload = cJSON_CreateObject_ns();
    if (payload == NULL) {
        return NULL;
    }

    cJSON_AddStringToObject(payload, "command", "getJobExecution");
    cJSON_AddNumberToObject(payload, "executionId", executionId);
    return payload;
}

/**
 * Either instanceId or executionId may be specified. 
 * Pass -1 for whichever one you aren't specifying.
 *
 * @return a JSON request payload for the getJobInstance request.
 */
cJSON * jnu_createRequestGetJobInstance( int instanceId, int executionId ) {

    cJSON * payload = cJSON_CreateObject_ns();
    if (payload == NULL) {
        return NULL;
    }

    cJSON_AddStringToObject(payload, "command", "getJobInstance");

    // One of these two must be set.
    cJSON_AddNumberToObject(payload, "executionId", executionId );
    cJSON_AddNumberToObject(payload, "instanceId", instanceId );

    return payload;
}

/**
 *
 * @return the list of jobexecutions associated with the given job instanceId.  Must be free'd.
 */
cJSON * jnu_getJobExecutions( WolaConn * wolaConn, int instanceId ) {

    cJSON * payload = jnu_createRequestGetJobExecutions( instanceId );
    if (payload == NULL) {
        return NULL;
    }

    cJSON * jobExecutions = jnu_doJsonRequest( wolaConn, payload );
    cJSON_Delete_ns(payload);

    return jobExecutions;
}

/**
 *
 * @return the jobexecution with the given executionid.  Must be free'd.
 */
cJSON * jnu_getJobExecution( WolaConn * wolaConn, int executionId) {

    cJSON * payload = jnu_createRequestGetJobExecution( executionId );
    if (payload == NULL) {
        return NULL;
    }

    cJSON * jobExecution = jnu_doJsonRequest( wolaConn, payload );
    cJSON_Delete_ns(payload);

    return jobExecution;
}

/**
 * Either instanceId or executionId may be specified. 
 * Pass -1 for whichever one you aren't specifying.
 *
 * @return the jobinstance record.  Must be free'd.  
 */
cJSON * jnu_getJobInstance( WolaConn * wolaConn, int instanceId, int executionId ) {

    cJSON * payload = jnu_createRequestGetJobInstance( instanceId, executionId );
    if (payload == NULL) {
        return NULL;
    }

    cJSON * jobInstance = jnu_doJsonRequest( wolaConn, payload );
    cJSON_Delete_ns(payload);

    return jobInstance;
}

/**
 * @return the instanceId specified on the command line;
 *         or, if an executionId was specified instead, lookup the jobinstance
 *         for that execution and return its instanceId.
 */
int jnu_resolveJobInstanceId( BatchWolaClient * batchWolaClient ) {

    int retMe = jnu_getInstanceId(batchWolaClient->args);

    if (retMe < 0) {
        // InstanceId not specified.  Call getJobInstance.
        cJSON * jobInstance = jnu_getJobInstance( batchWolaClient->wolaConn, 
                                                  jnu_getInstanceId(batchWolaClient->args),
                                                  jnu_getExecutionId(batchWolaClient->args) );
        if (jobInstance == NULL) {
            return jnu_error(__FUNCTION__,-1, "getJobInstance returned NULL");
        }

        retMe = jnu_getInstanceId( jobInstance );
        cJSON_Delete_ns(jobInstance);
    }

    return retMe;
}


/**
 * @return either the job's batchstatus RC or the RC parsed from its exitStatus
 *         if --returnExitStatus was specified in the given args.
 */
int jnu_resolveExitCode( cJSON * args, cJSON * jobExecution ) {
    return ( jnu_getReturnExitStatus( args ) != NULL) 
                    ?  jnu_parseExitStatusReturnCode( jnu_getExitStatus( jobExecution ) )
                    :  jnu_getBatchStatusReturnCode( jnu_getBatchStatus(jobExecution) );
}


/**
 * @return the latest jobExecution record for the given jobInstance ID.
 */
cJSON * jnu_getLatestJobExecution(BatchWolaClient * batchWolaClient, int instanceId) {

    cJSON * jobExecutions = jnu_getJobExecutions( batchWolaClient->wolaConn, instanceId );

    // Return the first jobExecution in the list (they're always returned in order).
    // TODO: eventually check the sequence values for ordering, once we have them.
    return (jobExecutions != NULL) ?  cJSON_DupArrayItemAndFreeArray(jobExecutions,0) : NULL;
}

/**
 * Note: this method will poll (possibly forever) waiting for a jobexecution 
 *       record to show up.
 *
 * @return the latest jobExecution record for the given jobInstance.
 */
cJSON * jnu_waitForLatestJobExecution(BatchWolaClient * batchWolaClient, cJSON * jobInstance) {

    cJSON * jobExecutions = jnu_getJobExecutions( batchWolaClient->wolaConn, jnu_getInstanceId(jobInstance) );

    // Loop forever until the first jobexecution record shows up.
    while (jobExecutions != NULL && cJSON_isArrayEmpty( jobExecutions )) {

        if (jnu_getVerbose(batchWolaClient->args) != NULL) {
            jnu_issueWaitingForLatestJobExecutionMessage( jnu_getInstanceId(jobInstance) ) ;
        }

        jnu_sleep( jnu_getPollingInterval(batchWolaClient->args) );

        cJSON_Delete_ns(jobExecutions);

        jobExecutions = jnu_getJobExecutions( batchWolaClient->wolaConn, jnu_getInstanceId(jobInstance) );
    }

    // Return the first jobExecution in the list (they're always returned in order).
    return (jobExecutions != NULL) ?  cJSON_DupArrayItemAndFreeArray(jobExecutions,0) : NULL;
}

/**
 * @param jobExecutions - it is assumed the jobExecutions are in reverse order (latest first)
 * @param afterThisJobExecution - it is assumed this job execution exists in the list of jobExecutions.
 *
 * @return a dup (must be free'd) of the jobexexecution in the list that comes after afterThisJobExecution;
 *         or NULL if no such jobexecution exists.
 */
cJSON * jnu_getNextJobExecution( cJSON * jobExecutions, cJSON * afterThisJobExecution ) {

    cJSON * prevJobExecution = NULL;

    for (int i=0; i < cJSON_GetArraySize_ns(jobExecutions); ++i) {
        cJSON * jobExecution = cJSON_GetArrayItem(jobExecutions, i);

        if ( jnu_getExecutionId(jobExecution) == jnu_getExecutionId(afterThisJobExecution) ) {
            return cJSON_Duplicate(prevJobExecution, 1);
        }

        prevJobExecution = jobExecution;
    }

    jnu_error(__FUNCTION__,0, "afterThisJobExecution (%s) not in the list of jobExecutions: %s", 
              cJSON_PrintUnformatted_ns(afterThisJobExecution),
              cJSON_PrintUnformatted_ns(jobExecutions));

    return NULL;
}

/**
 * Note: this method will poll (possibly forever) waiting for the next jobexecution 
 *       record to show up.
 *
 * @param afterThisJobExecution return the job execution record after this one.
 *                              If NULL, then the latest job execution record is returned.
 *
 * @return the next jobExecution record 
 */
cJSON * jnu_waitForNextJobExecution(BatchWolaClient * batchWolaClient, 
                                    cJSON * jobInstance,
                                    cJSON * afterThisJobExecution ) {

    if (afterThisJobExecution == NULL) {
        return jnu_waitForLatestJobExecution( batchWolaClient, jobInstance );
    }

    cJSON * jobExecutions = jnu_getJobExecutions( batchWolaClient->wolaConn, jnu_getInstanceId(jobInstance) );
    cJSON * nextJobExecution = jnu_getNextJobExecution( jobExecutions,  afterThisJobExecution );

    // Loop until the next jobexecution record shows up.
    while (jobExecutions != NULL && nextJobExecution == NULL) {

        if (jnu_getVerbose(batchWolaClient->args) != NULL) {
            jnu_issueWaitForNextJobExecutionMessage(afterThisJobExecution);
        }

        jnu_sleep( jnu_getPollingInterval(batchWolaClient->args) );

        cJSON_Delete_ns(jobExecutions);

        jobExecutions = jnu_getJobExecutions( batchWolaClient->wolaConn, jnu_getInstanceId(jobInstance) );
        nextJobExecution = jnu_getNextJobExecution( jobExecutions,  afterThisJobExecution );
    }

    cJSON_Delete_ns(jobExecutions);

    // Return the nextJobExecution (it's a dup of the enry in the jobexecutions list we just deleted).
    return nextJobExecution;
}

/**
 * @param jobExecution - wait for this execution to finish.
 *                       Note: this object will be deleted (if it's not already finished). 
 *                       A new, refreshed jobExecution record is returned (must be free'd).
 *
 * @return the refreshed jobExecution record (must be free'd)
 */
cJSON * jnu_waitForTermination(BatchWolaClient * batchWolaClient, cJSON * jobExecution) {

    while ( jobExecution != NULL && ! jnu_isDone( jnu_getBatchStatus(jobExecution) ) ) {

        if (jnu_getVerbose(batchWolaClient->args) != NULL) {
            jnu_issueWaitingForTerminationMessage(jobExecution) ;
        }

        jnu_sleep( jnu_getPollingInterval(batchWolaClient->args) );

        int executionId = jnu_getExecutionId(jobExecution);
        cJSON_Delete_ns(jobExecution);

        jobExecution = jnu_getJobExecution( batchWolaClient->wolaConn, executionId );
    }

    return jobExecution;
}

/**
 * Wait for the job to terminate.
 *
 * Note: jobInstance and jobExecution are both free'd by this method.
 *
 * @param jobExecution should be the most recent execution (the one we're waiting for).
 *
 * @return the RC according to jnu_resolveExitCode.
 */
int jnu_handleWait( BatchWolaClient * batchWolaClient, cJSON * jobInstance, cJSON * jobExecution) {

    // Wait for the job to finish.
    jobExecution = jnu_waitForTermination(batchWolaClient, jobExecution);
    if ( jobExecution == NULL ) {
        cJSON_Delete_ns(jobInstance);
        return jnu_error(__FUNCTION__, 255, "after jnu_waitForTermination");
    }

    if ( jnu_isRestartable(jnu_getBatchStatus(jobExecution))) {
        // Token will only get written if restartTokenFile was specified and exists
        jnu_writeRestartToken(batchWolaClient->args,  jnu_getInstanceId(jobInstance));
    } else {
        jnu_clearRestartTokenFromFile(batchWolaClient->args);
    }

    // Issue some messages.
    jnu_issueJobFinishedMessage(jobInstance, jobExecution);
    jnu_issueJobExecutionMessage(jobExecution);

    // Get the native utility's return code (either batchstatus or exitstatus)
    int rc = jnu_resolveExitCode( batchWolaClient->args, jobExecution );
    if (rc < 0) {
        rc = 255;   // RC for the process.
    }

    cJSON_Delete_ns(jobExecution);
    cJSON_Delete_ns(jobInstance);

    return rc;
}

/** Parms to pass to the monitor thread run routine. */
typedef struct {
    char * correlIdString; // Correlation ID
    char * qmName;         // Queue manager name
    char * eventTopicRoot; // JMS Events Topic Root
    char ** instanceState; // instance state
    int maxMessageLength;  // Maximum length of a message
    pthread_cond_t* threadInitializationCompleteCond_p; //Init complete condition, posted when complete.
    pthread_mutex_t* threadInitializationCompleteMutex_p; // Init complete mutex, posted when complete.
} joblogs_thread_run_parms;

extern "C" void* pthread_joblogs_thread_run(void* data_p) {

    joblogs_thread_run_parms* parms = (joblogs_thread_run_parms*) data_p;

    char topicStringInstanceWildcard[] = "batch/jobs/execution/jobLogPart";   /* topic to subscribe to */
    MQCHAR48 qName = "";                                            /* allocate for queue name later */
    char   publicationBuffer[parms->maxMessageLength];                                 /* Allocate to receive messages */
    char   resTopicStrBuffer[64];                                   /* Allocate to resolve topic string */

    MQHCONN Hconn = MQHC_UNUSABLE_HCONN;    /* connection handle       */
    MQHOBJ  Hobj = MQHO_NONE;               /* publication queue handle   */
    MQHOBJ  Hsub = MQSO_NONE;               /* subscription handle      */
    MQLONG  CompCode = MQCC_OK;             /* completion code        */
    MQLONG  Reason = MQRC_NONE;             /* reason code          */
    MQLONG  messlen = 0;                    /* received message length */
    MQSD   sd = {MQSD_DEFAULT};             /* Subscription Descriptor    */
    MQMD   md = {MQMD_DEFAULT};             /* Message Descriptor      */
    MQGMO  gmo = {MQGMO_DEFAULT};           /* get message options      */
    MQHMSG   Hmsg = MQHM_UNUSABLE_HMSG;     /* message handle */
    MQCMHO cmho = {MQCMHO_DEFAULT};         /* create message handle options */

    char *  topicString = resolveTopicRoot( parms->eventTopicRoot, topicStringInstanceWildcard );
    jnu_trace(__FUNCTION__, "Subscribing to batch events at JMS topic root: %s", topicString);

    char *  publication = publicationBuffer;
    char *  resTopicStr = resTopicStrBuffer;
    char *  selectorStr;

    memset(resTopicStr, 0, sizeof(resTopicStrBuffer));
    /** **/
    // Connect to queue manager
    MQCONN(parms->qmName, &Hconn, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        jnu_error(__FUNCTION__, 255, "MQCONN failed. Completion code %d and Return code %d\n", CompCode, Reason);
    } else {

       // Create message handle (needed to handle the RFH2 header in the message, basically
       // we just want to toss it out, don't need the header)
       MQCRTMH(Hconn, &cmho, &Hmsg, &CompCode, &Reason);
       if (CompCode != MQCC_OK) {
           jnu_error(__FUNCTION__, 255, "MQCRTMH failed. Completion code %d and Return code %d\n", CompCode, Reason);
       } else {

          /**
           * Create the selector string to use with MQSUB.  Need to use selector string,
           * as we can't get message by correlation id when using a managed subscription
           *
           * Format: JMSCorrelatorID = '<correlation id>'
           */
          char selectorString[80];
          strcpy(selectorString,"JMSCorrelationID = ");
          strcat(selectorString,"'");
          strcat(selectorString,parms->correlIdString);
          strcat(selectorString,"'");
          selectorStr = selectorString;

          /**
           * Set up for MQSUB. The options set here are:
           *
           * MQSO_CREATE: Creates a new subscription
           * MQSO_MANAGED: The queue manager will manage the storage of messages sent to this subscription
           * MQSO_NON_DURABLE: This subscription to the topic is removed when the application's connection to the queue manager is closed
           * MQSO_FAIL_IF_QUIESCING: The MQSUB call fails if the queue manager is in quiescing state
           * MQSO_WILDCARD_TOPIC: Select topics to subscribe to using the topic-based wildcard scheme.
           */
          sd.ObjectString.VSPtr = topicString;
          sd.ObjectString.VSLength = MQVS_NULL_TERMINATED;
          sd.Options = MQSO_CREATE | MQSO_MANAGED | MQSO_NON_DURABLE | MQSO_FAIL_IF_QUIESCING | MQSO_WILDCARD_TOPIC;
          sd.ResObjectString.VSPtr = resTopicStr;
          sd.ResObjectString.VSBufSize = sizeof(resTopicStrBuffer)-1;
          sd.SelectionString.VSPtr = selectorStr;
          sd.SelectionString.VSLength = MQVS_NULL_TERMINATED;

          MQSUB(Hconn, &sd, &Hobj, &Hsub, &CompCode, &Reason);
          if (CompCode != MQCC_OK) {
             jnu_error(__FUNCTION__, 255, "MQSUB failed. Completion code %d and Return code %d\n", CompCode, Reason);

          } else {
              jnu_trace(__FUNCTION__,
              "Subscribed to publications matching \"%s\" and \"%s\"\n",resTopicStr, selectorStr);
              // Notify main thread that the subscription is ready
              if (pthread_mutex_lock(parms->threadInitializationCompleteMutex_p) == 0) {
                  pthread_cond_signal(parms->threadInitializationCompleteCond_p);
                  pthread_mutex_unlock(parms->threadInitializationCompleteMutex_p);
              }

              /**
               * Set up for MQGET.  The options set here are:
               *
               * MQGMO_WAIT: Wait until a suitable message arrives
               * MQGMO_CONVERT: This option converts the application data in the message to conform to the CodedCharSetId
               *    and Encoding values specified in the MsgDesc parameter on the MQGET call. The data is converted before
               *    it is copied to the Buffer parameter.
               * MQGMO_PROPERTIES_IN_HANDLE: Put message properties in message handle object instead of with message in RFH2 header
               */
              gmo.Options = MQGMO_WAIT | MQGMO_CONVERT | MQGMO_PROPERTIES_IN_HANDLE;
              gmo.WaitInterval = 2000;   /* time that MQGET call waits for a suitable message to arrive */
              gmo.Version = MQGMO_VERSION_4;       /* Need version 4 for MsgHandle */
              gmo.MsgHandle = Hmsg;


              //  Find out what queue name is being used for trace purposes
              if(jnu_isTraceEnabled())
                  mq_inquireQname(Hconn, Hobj, qName);
              jnu_trace(__FUNCTION__,
                        "Waiting for publications matching \"%s\" from \"%-0.48s\"\n",resTopicStr, qName);
              int finalLog = 0;
              while(!jnu_isInstanceDone(*(parms->instanceState)) || !finalLog) {
                  memcpy(md.MsgId, MQMI_NONE, sizeof(md.MsgId));
                  memcpy(md.CorrelId, MQCI_NONE, sizeof(md.CorrelId));
                  md.Encoding    = MQENC_NATIVE;
                  md.CodedCharSetId = 1047;  // Needed to translate [] correctly.
                  memset(publicationBuffer, 0, sizeof(publicationBuffer));

                  MQGET(Hconn, Hobj, &md, &gmo, sizeof(publicationBuffer)-1, publication, &messlen, &CompCode, &Reason);
                  if (CompCode != MQCC_OK ) {
                      if (CompCode == MQCC_FAILED && Reason == MQRC_NO_MSG_AVAILABLE) {
                          continue;
                      }
                      jnu_error(__FUNCTION__, 255, "MQGET failed. Completion code %d and Return code %d\n", CompCode, Reason);
                  } else {
                      jnu_trace(__FUNCTION__,"Received job log publication");
                      finalLog = jnu_printJobLog(cJSON_Parse(publication));
                  }
              }

              jnu_trace(__FUNCTION__, "Job has reached a final state.  Job logs are complete.");
          }
       }
    }

    // Close the connection
    MQCLOSE(Hconn, &Hsub, MQCO_REMOVE_SUB, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        jnu_error(__FUNCTION__, 255, "MQCLOSE failed. Completion code %d and Return code %d\n", CompCode, Reason);
    }

    // Disconnect
    MQDISC(&Hconn, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        jnu_error(__FUNCTION__, 255, "MQDISC failed. Completion code %d and Return code %d\n", CompCode, Reason);
    }
    return NULL;
}


/**
 *
 * @return The new 'topic_root/ + tree' or the default 'batch/ + tree' for event subscription.
 */
char * resolveTopicRoot( BatchWolaClient * batchWolaClient, char * defaultTopicString){

    char * topicString = jnu_getTopicRoot( batchWolaClient->args );

    return resolveTopicRoot( topicString, defaultTopicString);

}

/**
 *
 * @return The new 'topic_root/ + tree' or the default 'batch/ + tree' for event subscription.
 */
char * resolveTopicRoot( char * topicRootString, char * defaultTopicString){

    int startCopy = 0;

    if( topicRootString == NULL ||
            jnu_strequals(topicRootString,"--topicRoot")) {
                    return defaultTopicString;
        }

    //Empty string means remove the 'batch/' as root.
    if ( jnu_strequals(topicRootString,"")){
        startCopy = strlen("batch/");
    }else{
        startCopy = strlen("batch");
    }

    // Copy topicString + (topic Tree without 'batch')
    strcat( topicRootString, (char *)&defaultTopicString[startCopy]);

    jnu_trace(__FUNCTION__, "Suffix topic Root String is: %s", (char *)&defaultTopicString[startCopy]);

    return topicRootString;

}

/**
 * This makes use of the Managed MQ subscriber pattern here:
 * http://www-01.ibm.com/support/knowledgecenter/SSFKSJ_7.1.0/com.ibm.mq.doc/ps10437_.htm?lang=en
 *
 * There is no administrative definition of queues or topics, and the subscription only lasts
 * as long as the subscription handle from the MQSUB call.
 *
 * @return The native utility's return code (either batchstatus or exitstatus) or 255
 */
int jnu_submitAndWaitForJobEndEvent( BatchWolaClient * batchWolaClient ) {

    char topicStringInstanceWildcard[] = "batch/jobs/instance/+";   /* topic to subscribe to */
    char * qmName = jnu_getQueueManagerName( batchWolaClient->args );           /* queue manager name */
    MQCHAR48 qName = "";                                /* allocate for queue name later */
    char * qNameStr = jnu_getQueueName( batchWolaClient->args );
    if(qNameStr != NULL){
        strcpy(qName,jnu_getQueueName( batchWolaClient->args ));
    }
    char   publicationBuffer[2048];                                 /* Allocate to receive messages */
    char   resTopicStrBuffer[64];                                   /* Allocate to resolve topic string */

    MQHCONN Hconn = MQHC_UNUSABLE_HCONN;    /* connection handle       */
    MQHOBJ  Hobj = MQHO_NONE;               /* publication queue handle   */
    MQHOBJ  Hsub = MQSO_NONE;               /* subscription handle      */
    MQLONG  CompCode = MQCC_OK;             /* completion code        */
    MQLONG  Reason = MQRC_NONE;             /* reason code          */
    MQLONG  messlen = 0;                    /* received message length */
    MQSD   sd = {MQSD_DEFAULT};             /* Subscription Descriptor    */
    MQOD   od = {MQOD_DEFAULT};             /* Unmanaged subscription queue */
    MQMD   md = {MQMD_DEFAULT};             /* Message Descriptor      */
    MQGMO  gmo = {MQGMO_DEFAULT};           /* get message options      */
    MQHMSG   Hmsg = MQHM_UNUSABLE_HMSG;     /* message handle */
    MQCMHO cmho = {MQCMHO_DEFAULT};         /* create message handle options */

    char *  topicString = resolveTopicRoot( batchWolaClient, topicStringInstanceWildcard );

    jnu_issueTopicRootMessage( topicString );
    jnu_trace(__FUNCTION__, "Subscribing to batch events at JMS topic root: %s", topicString);

    char *  publication = publicationBuffer;
    char *  resTopicStr = resTopicStrBuffer;
    char *  selectorStr;

    pthread_cond_t threadInitializationCompleteCond = {0x0};
    pthread_mutex_t threadInitializationCompleteMutex = PTHREAD_MUTEX_INITIALIZER;

    /** The job log thread. */
    pthread_t jobLogThread;

    memset(resTopicStr, 0, sizeof(resTopicStrBuffer));
    /** **/

    // Connect to queue manager
    MQCONN(qmName, &Hconn, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
       return jnu_error(__FUNCTION__, 255, "MQCONN failed. Completion code %d and Return code %d\n", CompCode, Reason);
    }

    // Create message handle (needed to handle the RFH2 header in the message, basically
    // we just want to toss it out, don't need the header)
    MQCRTMH(Hconn, &cmho, &Hmsg, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        return jnu_error(__FUNCTION__, 255, "MQCRTMH failed. Completion code %d and Return code %d\n", CompCode, Reason);
    }

    /**
     * Generate a correlation id (see generateCorrelationId method for doc, in jbatch_mq.cpp)
     */
    MQBYTE correlator[24];
    generateCorrelationId(correlator);

    /**
     * 24 bytes converted to a 48 character string of the hex values to be used as the value for the job parameter.
     */
    char correlIdString[49];
    for(int i = 0; i < 24; i++)
        sprintf(correlIdString+2*i, "%02x", correlator[i]);
    correlIdString[48] = '\0';
    jnu_trace(__FUNCTION__, "generated correlation id: %s", correlIdString);

    /**
     * Add it as a job parameter, and the batch java code will use it as the correlation id on the
     * job event messages it sends
     */
    cJSON_AddStringToObject(jnu_getOrCreateJobParameters(batchWolaClient->args),
                                CORRELATOR_JOB_PROP, correlIdString);

    /**
     * Create the selector string to use with MQSUB.  Need to use selector string,
     * as we can't get message by correlation id when using a managed subscription
     *
     * Format: JMSCorrelatorID = '<correlation id>'
     */
    char selectorString[80];
    strcpy(selectorString,"JMSCorrelationID = ");
    strcat(selectorString,"'");
    strcat(selectorString,correlIdString);
    strcat(selectorString,"'");
    selectorStr = selectorString;

    /**
      * Set up for MQSUB. The options set here are:
      *
      * MQSO_CREATE: Creates a new subscription
      * MQSO_MANAGED: The queue manager will manage the storage of messages sent to this subscription
      * MQSO_NON_DURABLE: This subscription to the topic is removed when the application's connection to the queue manager is closed
      * MQSO_FAIL_IF_QUIESCING: The MQSUB call fails if the queue manager is in quiescing state
      * MQSO_WILDCARD_TOPIC: Select topics to subscribe to using the topic-based wildcard scheme.
      */
    sd.ObjectString.VSPtr = topicString;
    sd.ObjectString.VSLength = MQVS_NULL_TERMINATED;
    //If the user specifies a queue
    if(strcmp(qName,"")!=0){
        sd.Options = MQSO_CREATE | MQSO_NON_DURABLE | MQSO_FAIL_IF_QUIESCING | MQSO_WILDCARD_TOPIC ;
    }else{
        sd.Options = MQSO_CREATE | MQSO_MANAGED | MQSO_NON_DURABLE | MQSO_FAIL_IF_QUIESCING | MQSO_WILDCARD_TOPIC ;
    }
    sd.ResObjectString.VSPtr = resTopicStr;
    sd.ResObjectString.VSBufSize = sizeof(resTopicStrBuffer)-1;
    sd.SelectionString.VSPtr = selectorStr;
    sd.SelectionString.VSLength = MQVS_NULL_TERMINATED;

    //If the user specifies a queue
    if(strcmp(qName,"")!=0){
        strncpy(od.ObjectName, qName, MQ_Q_NAME_LENGTH);
        MQOPEN(Hconn, &od, MQOO_FAIL_IF_QUIESCING | MQOO_INPUT_AS_Q_DEF, &Hobj, &CompCode, &Reason);
        if (CompCode != MQCC_OK) {
            return jnu_error(__FUNCTION__, 255, "MQOPEN failed. Likely due to a bad queueName. Completion code %d and Return code %d\n", CompCode, Reason);
        }
    }
    MQSUB(Hconn, &sd, &Hobj, &Hsub, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        return jnu_error(__FUNCTION__, 255, "MQSUB failed. Completion code %d and Return code %d\n", CompCode, Reason);
    }

    char * state = "UNKNOWN";
    if (jnu_getJobLog(batchWolaClient->args) != NULL) {

        int maxMessageLength = mq_inquireMaxMsgLength(Hconn, Hobj);
        if (maxMessageLength <= 0) {
            maxMessageLength = 4096;
        }

        jnu_trace(__FUNCTION__, "Maximum message length: %d", maxMessageLength);

        pthread_cond_init(&threadInitializationCompleteCond, NULL);
        pthread_mutex_lock(&threadInitializationCompleteMutex);

        joblogs_thread_run_parms thread_parms;
        thread_parms.correlIdString = correlIdString;
        thread_parms.qmName = qmName;
        thread_parms.instanceState = &state;
        thread_parms.maxMessageLength = maxMessageLength;
        thread_parms.threadInitializationCompleteCond_p = &threadInitializationCompleteCond;
        thread_parms.threadInitializationCompleteMutex_p = &threadInitializationCompleteMutex;
        thread_parms.eventTopicRoot = jnu_getTopicRoot( batchWolaClient->args );


        // Activate job log subscription
        if (pthread_create(&jobLogThread, NULL, &pthread_joblogs_thread_run, &thread_parms) != 0) {
            pthread_mutex_unlock(&threadInitializationCompleteMutex);
            return jnu_error(__FUNCTION__, 255, "Job logs cannot be retrieved");
        }
        if (pthread_cond_wait(&threadInitializationCompleteCond, &threadInitializationCompleteMutex) != 0) {
            jnu_error(__FUNCTION__, 255, "Wait condition failed with return code %d\n");
        }
    }

    /**
     * Set up for MQGET.  The options set here are:
     *
     * MQGMO_WAIT: Wait until a suitable message arrives
     * MQGMO_CONVERT: This option converts the application data in the message to conform to the CodedCharSetId
     *    and Encoding values specified in the MsgDesc parameter on the MQGET call. The data is converted before
     *    it is copied to the Buffer parameter.
     * MQGMO_PROPERTIES_IN_HANDLE: Put message properties in message handle object instead of with message in RFH2 header
     */
    gmo.Options = MQGMO_WAIT | MQGMO_CONVERT | MQGMO_PROPERTIES_IN_HANDLE;
    gmo.WaitInterval = MQWI_UNLIMITED;   /* time that MQGET call waits for a suitable message to arrive */
    gmo.Version = MQGMO_VERSION_4;       /* Need version 4 for MsgHandle */
    gmo.MsgHandle = Hmsg;

    cJSON * jobInstance = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );
    if ( jobInstance == NULL ) {
        return jnu_error(__FUNCTION__, 255, "after jnu_doJsonRequest(submit)");
    }
    int instanceId = jnu_getInstanceId(jobInstance);

    // Should only be getting here on a submit or restart...
    if (jnu_strequals( "submit", jnu_getCommand(batchWolaClient->args)))
        if (jnu_getInstanceId(batchWolaClient->args) == -1)
            jnu_issueJobSubmittedMessage(jobInstance);
        else
            jnu_issueRestartSubmittedMessage(jobInstance);
    else if (jnu_strequals( "restart", jnu_getCommand(batchWolaClient->args)))
        jnu_issueRestartSubmittedMessage( jobInstance );

    jnu_issueJobInstanceMessage(jobInstance);

    //  Find out what queue name is being used for trace purposes
    if(jnu_isTraceEnabled())
        mq_inquireQname(Hconn, Hobj, qName);
    jnu_trace(__FUNCTION__,
              "Waiting for publications matching \"%s\" from \"%-0.48s\"\n",resTopicStr, qName);

    while(true) {
        memcpy(md.MsgId, MQMI_NONE, sizeof(md.MsgId));
        memcpy(md.CorrelId, MQCI_NONE, sizeof(md.CorrelId));
        md.Encoding    = MQENC_NATIVE;
        md.CodedCharSetId = MQCCSI_Q_MGR;
        memset(publicationBuffer, 0, sizeof(publicationBuffer));

        MQGET(Hconn, Hobj, &md, &gmo, 2047,publication, &messlen, &CompCode, &Reason);
        if (CompCode != MQCC_OK) {
            cJSON_Delete_ns(jobInstance);
            return jnu_error(__FUNCTION__, 255, "MQGET failed. Completion code %d and Return code %d\n", CompCode, Reason);
        }

        cJSON * event = cJSON_Parse(publication);

        if(jnu_isInstanceDone(jnu_getInstanceState(event))) {
            // Terminate the job log thread
            state = jnu_getInstanceState(event);
            // Only write out instance id if job is in a "restartable" state
            if (jnu_isInstanceRestartable(jnu_getInstanceState(event))) {
                // Token will only get written if restartTokenFile was specified and exists

                jnu_writeRestartToken(batchWolaClient->args,  jnu_getInstanceId(jobInstance));
            } else {
                jnu_clearRestartTokenFromFile(batchWolaClient->args);
            }
            break;
        }


    }

    // Close the connection
    MQCLOSE(Hconn, &Hsub, MQCO_REMOVE_SUB, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        cJSON_Delete_ns(jobInstance);
        return jnu_error(__FUNCTION__, 255, "MQCLOSE failed. Completion code %d and Return code %d\n", CompCode, Reason);
    }

    // Disconnect
    MQDISC(&Hconn, &CompCode, &Reason);
    if (CompCode != MQCC_OK) {
        cJSON_Delete_ns(jobInstance);
        return jnu_error(__FUNCTION__, 255, "MQDISC failed. Completion code %d and Return code %d\n", CompCode, Reason);
    }

    pthread_join(jobLogThread, NULL);
    jnu_trace(__FUNCTION__, "Job log monitoring thread has completed.");

    cJSON * jobExecutions = jnu_getJobExecutions( batchWolaClient->wolaConn, instanceId );
    cJSON * jobExecution =  cJSON_DupArrayItemAndFreeArray(jobExecutions,0);

    // Issue some messages.
    jnu_issueJobFinishedMessage(jobInstance, jobExecution);
    jnu_issueJobExecutionMessage(jobExecution);

    // Get the native utility's return code (either batchstatus or exitstatus)
    int rc = jnu_resolveExitCode( batchWolaClient->args, jobExecution );
    if (rc < 0) {
        rc = 255;   // RC for the process.
    }

    cJSON_Delete_ns(jobExecution);
    cJSON_Delete_ns(jobInstance);

    return rc;
}

/**
 *
 * @param batchWolaClient already initialized, registered, and opened a WOLA connection
 *
 * @return 255 if something went badly;
 *         0 if all is well;
 *         batchStatus (33-36) if --wait is specified;
 *         atoi(exitStatus) if --returnExitStatus is specified
 *
 */
int jnu_submitTask( BatchWolaClient * batchWolaClient ) {

    int rc = 0;

    // Check to see if job should be restarted
    int restartId = jnu_getInstanceId(batchWolaClient->args);
    if (restartId != -1) {
        // restart token file contained an instance id
        cJSON * exec = jnu_getLatestJobExecution(batchWolaClient, restartId);
        if ( exec != NULL && !jnu_isRestartable(jnu_getBatchStatus(exec))) {
            jnu_removeRestartToken(batchWolaClient->args);
        }
    }


    /**
     * Propagate JES jobname and jobid as job parameters for inclusion
     * in SMF data.
     */
    static struct psa * psa_ptr = 0;
    static struct ascb * ascb_ptr = (struct ascb *)psa_ptr->psaaold;
    static struct assb * assb_ptr = (struct assb *)ascb_ptr->ascbassb;
    static struct jsab * jsab_ptr = (struct jsab *)assb_ptr->assbjsab;

    char jesJobName[9], jesJobId[9];

    strncpy(jesJobName, &(jsab_ptr->jsabjbnm[0]),8);
    jesJobName[8] = '\0';
    jnu_trace(__FUNCTION__,
                      "JES Job name is \"%s\"",jesJobName);
    strncpy(jesJobId, &(jsab_ptr->jsabjbid[0]),8);
    jesJobId[8] = '\0';
    jnu_trace(__FUNCTION__,
                      "JES Job Id is \"%s\"",jesJobId);

    cJSON_AddStringToObject(jnu_getOrCreateJobParameters(batchWolaClient->args),
                                JESJOBNAME_JOB_PROP, jesJobName);
    cJSON_AddStringToObject(jnu_getOrCreateJobParameters(batchWolaClient->args),
                                JESJOBID_JOB_PROP, jesJobId);

    // If we're waiting for MQ event, submit is done after the subscribe,
    // so handle it in another method.
    if ( jnu_getWait( batchWolaClient->args ) != NULL  &&
        jnu_getQueueManagerName( batchWolaClient->args ) != NULL ) {
        rc = jnu_submitAndWaitForJobEndEvent(batchWolaClient);
        return rc;
    }

    // Otherwise, its a polling wait, or no wait at all

    // Send the submit request.
    cJSON * jobInstance = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );
    if ( jobInstance == NULL ) {
        return jnu_error(__FUNCTION__, 255, "after jnu_doJsonRequest(submit)");
    }

    if (jnu_getInstanceId(batchWolaClient->args) == -1)
        jnu_issueJobSubmittedMessage(jobInstance);
    else
        jnu_issueRestartSubmittedMessage(jobInstance);

    // Issue some messages.
    jnu_issueJobInstanceMessage(jobInstance);
    // TODO: jnu_issueJobLogLocationMessage(jobInstance);*/

    // wait for termination
    if ( jnu_getWait( batchWolaClient->args ) != NULL ) {

        // Get the latest job execution
        cJSON * jobExecution = jnu_waitForLatestJobExecution(batchWolaClient, jobInstance);
        if ( jobExecution == NULL ) {
            cJSON_Delete_ns(jobInstance);
            return jnu_error(__FUNCTION__, 255, "after jnu_waitForLatestJobExecution");
        }

        // Wait for the job to finish.
        rc = jnu_handleWait( batchWolaClient, jobInstance, jobExecution );

    } else {
        // Token will only get written if restartTokenFile was specified and exists
        jnu_writeRestartToken(batchWolaClient->args,  jnu_getInstanceId(jobInstance));
        cJSON_Delete_ns(jobInstance);
    }

    return rc;
}

/**
 * Restart a job.
 */
int jnu_restartTask( BatchWolaClient * batchWolaClient ) {

    int rc = 0;

    // If we're waiting for MQ event, restart command is sent after the subscribe,
    // so handle it in another method.
    if ( jnu_getWait( batchWolaClient->args ) != NULL  &&
        jnu_getQueueManagerName( batchWolaClient->args ) != NULL ) {
        rc = jnu_submitAndWaitForJobEndEvent(batchWolaClient);
        return rc;
    }

    // First I need to get the latest job execution record, so I can detect when the new
    // job execution record shows up.
    cJSON * latestJobExecution = jnu_getLatestJobExecution( batchWolaClient, jnu_resolveJobInstanceId( batchWolaClient ) );

    // Send the restart request.
    cJSON * jobInstance = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );
    if ( jobInstance == NULL ) {
        return jnu_error(__FUNCTION__, 255, "after jnu_doJsonRequest(restart)");
    }

    // Issue some messages.
    jnu_issueRestartSubmittedMessage( jobInstance ) ;
    jnu_issueJobInstanceMessage(jobInstance);
    // TODO: jnu_issueJobLogLocationMessage(jobInstance);
    
    // wait for termination?
    if ( jnu_getWait( batchWolaClient->args ) != NULL ) {

        // Get the latest job execution
        cJSON * jobExecution = jnu_waitForNextJobExecution(batchWolaClient, jobInstance, latestJobExecution);
        cJSON_Delete_ns(latestJobExecution);
        if ( jobExecution == NULL ) {
            cJSON_Delete_ns(jobInstance);
            return jnu_error(__FUNCTION__, 255, "after jnu_waitForNextJobExecution");
        }

        // Wait for the job to finish.
        rc = jnu_handleWait( batchWolaClient, jobInstance, jobExecution );

    } else {
        cJSON_Delete_ns(jobInstance);
        cJSON_Delete_ns(latestJobExecution);
    }

    return rc;
}

/**
 * Stop a job.
 */
int jnu_stopTask( BatchWolaClient * batchWolaClient ) {

    int rc = 0;

    // Send the stop request.
    cJSON * jobExecution = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );
    if ( jobExecution == NULL ) {
        return jnu_error(__FUNCTION__, 255, "after jnu_doJsonRequest(stop)");
    }

    // Get the JobInstance record
    cJSON * jobInstance = jnu_getJobInstance( batchWolaClient->wolaConn, jnu_parseJobInstanceId(batchWolaClient->args), jnu_getExecutionId(jobExecution) );
    if ( jobInstance == NULL ) {
        cJSON_Delete_ns(jobExecution);
        return jnu_error(__FUNCTION__, 255, "after jnu_getJobInstance()");
    }

    // Issue some messages.
    jnu_issueStopSubmittedMessage( jobInstance ) ;
    jnu_issueJobInstanceMessage(jobInstance);
    
    // wait for termination?
    if ( jnu_getWait( batchWolaClient->args ) != NULL ) {
        if (jnu_getExecutionId(jobExecution) == -1) {
            jnu_issueJobStoppedMessage(jobInstance, jobExecution);
            cJSON_Delete_ns(jobExecution);
            cJSON_Delete_ns(jobInstance);
            rc = 0;
        } else {
            rc = jnu_handleWait( batchWolaClient, jobInstance, jobExecution );
        }
    } else {
        cJSON_Delete_ns(jobExecution);
        cJSON_Delete_ns(jobInstance);
    }

    return rc;
}


/**
 * Sends a "ping" request to the server, which simply echos back the request data.
 *
 * @return -1 if request failed
 */
int jnu_pingTask(BatchWolaClient * batchWolaClient) {

    // Send the request.
    cJSON * response = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );
    if ( response == NULL ) {
        return jnu_error(__FUNCTION__, -1, "after %s", "jnu_doJsonRequest");
    }

    jnu_trace(__FUNCTION__, "response.command: %s", jnu_getCommand(response) );
    cJSON_Delete_ns(response);

    return 0;
}

/**
 * List job instances
 */
int jnu_listJobsTask(BatchWolaClient * batchWolaClient) {

    int rc = 0;

    // send command
    cJSON * jobInstances = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );

    if ( jobInstances == NULL ) {
           cJSON_Delete_ns(jobInstances);
           return jnu_error(__FUNCTION__, 255, "jnu_listJobsTask() returned NULL");
       }

    for (int i=0 ; i < cJSON_GetArraySize_ns(jobInstances); ++i) {
        cJSON * jobInstance = cJSON_GetArrayItem(jobInstances, i);

        jnu_issueJobInstanceMessage( jobInstance );
    }

    cJSON_Delete_ns(jobInstances);
    return rc;
}

/**
 * @return 0
 */
int jnu_helpTask(cJSON * args) {

    if (args == NULL) {
        return jnu_printUsage();
    }

    // If "help <command>" was specified, then the next item in the
    // args object, after "command"="help", is the <command>.
    cJSON * helpCommand = cJSON_GetObjectItem(args, "command");
    cJSON * next = helpCommand->next;

    if (next != NULL) {
        return jnu_printTaskHelp(next->valuestring);
    } else {
        return jnu_printHelp();
    }
}

int jnu_purgeTask( BatchWolaClient * batchWolaClient ) {

    int rc = 0;

    //Check for multi-purge vs single purge here, as our output messages will be different based on whether
    //a single or multi purge is done (keeping in sync with with the java client, as that's how it is
    //done there)
    if(!jnu_isNumeric(jnu_getJobInstanceId(batchWolaClient->args)) || jnu_getCreateTime(batchWolaClient->args) != NULL ||
        jnu_getInstanceState(batchWolaClient->args) != NULL || jnu_getExitStatus(batchWolaClient->args) != NULL)
    {
        cJSON * purgeResponseArray = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );

        if(purgeResponseArray == NULL) {
            return jnu_error(__FUNCTION__, 255, "jnu_purgeTask() returned NULL");
        }

        for (int i=0 ; i < cJSON_GetArraySize_ns(purgeResponseArray); ++i) {
                cJSON * purgeResponse = cJSON_GetArrayItem(purgeResponseArray, i);

                jnu_issueJobPurgedMessageMulti( purgeResponse );
            }

        cJSON_Delete_ns(purgeResponseArray);

    } else { // single purge
         cJSON * jobInstance = jnu_getJobInstance( batchWolaClient->wolaConn,
                                                              jnu_getInstanceId(batchWolaClient->args),
                                                              jnu_getExecutionId(batchWolaClient->args) );
                if ( jobInstance == NULL ) {
                     return jnu_error(__FUNCTION__, 255, "jnu_getJobInstance() returned NULL");
                }

                cJSON * purgeResponse = jnu_doJsonRequest( batchWolaClient->wolaConn, batchWolaClient->args );

                if (purgeResponse != NULL) {
                    jnu_issueJobPurgedMessage( jobInstance ) ;
                    cJSON_Delete_ns(purgeResponse);
                }
                else
                    rc = 255;

                cJSON_Delete_ns(jobInstance);

     }

    return rc;
}

/**
 * @return 0.
 */
int jnu_statusTask( BatchWolaClient * batchWolaClient ) {

    // Get the JobInstance record
    cJSON * jobInstance = jnu_getJobInstance( batchWolaClient->wolaConn, 
                                              jnu_getInstanceId(batchWolaClient->args), 
                                              jnu_getExecutionId(batchWolaClient->args) );
    if ( jobInstance == NULL ) {
        return jnu_error(__FUNCTION__, 255, "jnu_getJobInstance() returned NULL");
    }

    // Issue message.
    jnu_issueJobInstanceMessage(jobInstance);

    // Get JobExecution records
    cJSON * jobExecutions = jnu_getJobExecutions( batchWolaClient->wolaConn, jnu_getInstanceId(jobInstance) );
    if ( jobExecutions == NULL ) {
        cJSON_Delete_ns(jobInstance);
        return jnu_error(__FUNCTION__, 255, "jnu_getJobExecutions() returned NULL");
    }

    // Issue messages and get return code (batch status of latest (first) execution record).
    int rc = 0;
    for (int i=0 ; i < cJSON_GetArraySize_ns(jobExecutions); ++i) {
        cJSON * jobExecution = cJSON_GetArrayItem(jobExecutions, i);
        jnu_issueJobExecutionMessage( jobExecution );

        rc = (i == 0) ? jnu_getBatchStatusReturnCode( jnu_getBatchStatus( jobExecution ) ) : rc;
    }

    cJSON_Delete_ns(jobInstance);
    cJSON_Delete_ns(jobExecutions);

    return rc;
}

/**
 * @return a new BatchWolaClient object. Must be free'd.
 */
BatchWolaClient * jnu_newBatchWolaClient(cJSON * args) {

    // Allocate and init the conn handle.
    WolaConn * wolaConn = (WolaConn *) jnu_malloc(sizeof(WolaConn));
    if (wolaConn == NULL) {
        return NULL;
    }
    int rc = jnu_initAndOpenWolaConn( wolaConn, jnu_getBatchManager(args) );
    if (rc != 0) {
        jnu_error(__FUNCTION__, rc, "after %s", "jnu_initAndOpenWolaConn");
        return NULL;
    }

    // Allocate and init the BatchWolaClient.
    BatchWolaClient * retMe = (BatchWolaClient *) jnu_malloc(sizeof(BatchWolaClient));
    if (retMe == NULL) {
        jnu_closeAndUnregister(wolaConn);
        return NULL;
    }

    retMe->args = args;
    retMe->wolaConn = wolaConn;

    return retMe;
}

/**
 * close the open connnection, unregster from WOLA, and free the batchWolaClient.
 *
 */
int jnu_closeBatchWolaClient(BatchWolaClient * batchWolaClient) {

    jnu_closeAndUnregister(batchWolaClient->wolaConn);
    jnu_free(batchWolaClient->wolaConn);

    cJSON_Delete_ns(batchWolaClient->args);
    jnu_free(batchWolaClient);
    return 0;
}


/**
 * Called by jbatch_native_utility_main::main.
 *
 * Main routine for the jbatch native utility (batchManagerZos).
 *
 * @return 255 if something went really bad
 *         0 if all is well
 *         20-40 reserved.
 *         33-36 (BatchStatus) if --wait is specified
 *         atoi(exitStatus) if --returnExitStatus is specified
 */
int jnu_main(int argc, char ** argv) {

    if (argc < 2) {
        jnu_helpTask(NULL);
        return 0;
    }

    cJSON * args = cJSON_CreateObject_ns();
    if (args == NULL) {
        return jnu_error(__FUNCTION__, 255, "exit: cJSON_CreateObject returned NULL");
    }

    // Parse args from command line.
    int rc = jnu_parseCommandLineArgs(argc, argv, args);
    if (rc != 0) {
        return jnu_error(__FUNCTION__, rc, "exit: jnu_parseCommandLineArgs returned %d", rc);
    }

    // Note: trying to read from STDIN blows up with an 0C4 if you try to run batchManagerZos 
    // from a dataset.
    // Parse args from stdin
    // -rx- rc = jnu_parsePropsStream(stdin, args, &jnu_parseArg);
    // -rx- if (rc != 0) {
    // -rx-     return jnu_error(__FUNCTION__, rc, "exit: jnu_parsePropsStream(stdin) returned %d", rc);
    // -rx- }

    // Resolve args (read job params file, etc).
    rc = jnu_resolveArgs(args);
    if (rc != 0) {
        return jnu_error(__FUNCTION__, rc, "exit: jnu_resolveArgs returned %d", rc);
    }
        
    // Switch on non-BatchWolaClient commands.
    if ( jnu_strequals( "help", jnu_getCommand(args) ) ) {
        return jnu_helpTask(args);
    }

    // Remaining commands all require a BatchWolaClient.
    BatchWolaClient * batchWolaClient = jnu_newBatchWolaClient(args);
    if (batchWolaClient == NULL) {
        return jnu_error(__FUNCTION__, 255, "exit: jnu_newBatchWolaClient returned NULL");
    }

    if ( jnu_strequals( "submit", jnu_getCommand(args) ) ) {
        rc = jnu_submitTask( batchWolaClient );
    } else if ( jnu_strequals( "restart", jnu_getCommand(args) ) ) {
        rc = jnu_restartTask( batchWolaClient ); 
    } else if ( jnu_strequals( "stop", jnu_getCommand(args) ) ) {
        rc = jnu_stopTask( batchWolaClient ); 
    } else if ( jnu_strequals( "status", jnu_getCommand(args) ) ) {
        rc = jnu_statusTask( batchWolaClient ); 
    } else if ( jnu_strequals( "ping", jnu_getCommand(args) ) ) {
        rc = jnu_pingTask( batchWolaClient );
    } else if ( jnu_strequals( "purge", jnu_getCommand(args) ) ) {
        rc = jnu_purgeTask( batchWolaClient );
    } else if ( jnu_strequals( "listJobs", jnu_getCommand(args) ) ) {
        rc = jnu_listJobsTask( batchWolaClient );
    } else {
        rc = jnu_error(__FUNCTION__, 255, "exit: unrecognized command: %s", jnu_ifNull( jnu_getCommand(args), "null") );
    }

    // Clean up.
    jnu_closeBatchWolaClient( batchWolaClient );

    return rc;
}
