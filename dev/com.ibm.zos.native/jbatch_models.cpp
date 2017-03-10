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

#include "include/jbatch_json.h"
#include "include/jbatch_utils.h"
#include "include/jbatch_utils.h"


/**
 * @return "--wait" if specified, otherwise NULL.
 */
char * jnu_getWait(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "wait", NULL);
}

/**
 * @return "--getJobLog" if specified, otherwise NULL.
 */
char * jnu_getJobLog(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "getJobLog", NULL);
}

/**
 * @return "--verbose" if specified, otherwise NULL.
 */
char * jnu_getVerbose(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "verbose", NULL);
}

/**
 * @return the value for "--queueManagerName" if specified, otherwise NULL.
 */
char * jnu_getQueueManagerName(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "queueManagerName", NULL);
}

/**
 * @return the value for "--queueName" if specified, otherwise NULL.
 */
char * jnu_getQueueName(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "queueName", NULL);
}

/**
 * @return the value for "--TopicRoot" if specified, otherwise NULL.
 */
char * jnu_getTopicRoot(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "topicRoot", NULL);
}

/**
 * @return the value for "--batchManager" if specified, otherwise NULL.
 */
char * jnu_getBatchManager(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "batchManager", NULL) ;
}

/**
 * @return the value for "command" if specified, otherwise NULL.
 */
char * jnu_getCommand(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "command", NULL) ;
}

/**
 * @return the value for "pollingInterval" if specified, otherwise NULL.
 */
int jnu_getPollingInterval(cJSON * args) {
    char * s = cJSON_GetObjectItemStringValue(args, "pollingInterval_s", "30") ;
    return  jnu_isNumber(s) 
                ? jnu_parseInt(s,-1) 
                : jnu_error(__FUNCTION__, 30, "--pollingInterval_s is not a number: %s. Using default=30s", s);
}

/**
 * @return the value for "--controlPropertiesFile", or NULL
 */
char * jnu_getControlPropertiesFile( cJSON * args ) {
    return cJSON_GetObjectItemStringValue(args, "controlPropertiesFile", NULL) ;
}

/**
 * @return the value for "--jobXMLFile", or NULL
 */
char * jnu_getJobXMLFile( cJSON * args ) {
    return cJSON_GetObjectItemStringValue(args, "jobXMLFile", NULL) ;
}

/**
 * @return the value for "--restartTokenFile", or NULL
 */
char * jnu_getRestartTokenFile( cJSON * args ) {
    return cJSON_GetObjectItemStringValue(args, "restartTokenFile", NULL) ;
}


/**
 * @return the value for "--jobPropertiesFile" or "--jobParametersFile",
 *         or NULL if neither is specified.
 */
char * jnu_getJobParametersFile( cJSON * args ) {
    char * retMe = cJSON_GetObjectItemStringValue(args, "jobParametersFile", NULL) ;
    return (retMe != NULL) ? retMe : cJSON_GetObjectItemStringValue(args, "jobPropertiesFile", NULL) ;
}

/**
 * @return "--returnExitStatus" if specified, otherwise NULL.
 */
char * jnu_getReturnExitStatus(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "returnExitStatus", NULL) ;
}

/**
 * @return the value for "batchStatus" if specified, otherwise NULL.
 */
char * jnu_getBatchStatus(cJSON * json) {
    return cJSON_GetObjectItemStringValue(json, "batchStatus", NULL) ;
}

/**
 * @return the value for "jobInstanceId" if specified, otherwise NULL.
 */
char * jnu_getJobInstanceId(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "jobInstanceId", NULL) ;
}

/**
 * @return the value for "jobInstanceId" if specified, otherwise -1.
 */
int jnu_parseJobInstanceId(cJSON * args) {
    char * s = cJSON_GetObjectItemStringValue(args, "jobInstanceId", "-1") ;
    return  jnu_isNumber(s)
                ? jnu_parseInt(s,-1)
                : -1;
}

/**
 * @return the value for "exitStatus" if specified, otherwise NULL.
 */
char * jnu_getExitStatus(cJSON * json) {
    return cJSON_GetObjectItemStringValue(json, "exitStatus", NULL) ;
}

/**
 * @return the value for "jobName"
 */
char * jnu_getJobName(cJSON * jobInstance) {
    return cJSON_GetObjectItemStringValue(jobInstance, "jobName", NULL) ;
}

/**
 * @return the value for "appName"
 */
char * jnu_getAppName(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "appName", NULL) ;
}

/**
 * @return the job log part number
 */
int jnu_getPartNumber(cJSON * args) {
    return cJSON_GetObjectItemIntValue(args, "partNumber", -1);
}

/**
 * @return the partition number
 */
int jnu_getPartitionNumber(cJSON * args) {
    return cJSON_GetObjectItemIntValue(args, "partitionNumber", -1);
}

/**
 * @return the value for "stepName"
 */
char * jnu_getStepName(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "stepName", NULL) ;
}

/**
 * @return the value for "splitName"
 */
char * jnu_getSplitName(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "splitName", NULL) ;
}

/**
 * @return the value for "flowName"
 */
char * jnu_getFlowName(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "flowName", NULL) ;
}

/**
 * @return the value for "purgeStatus"
 */
char * jnu_getPurgeStatus(cJSON * purgeResponse) {
    return cJSON_GetObjectItemStringValue(purgeResponse, "purgeStatus", NULL) ;
}

/**
 * @return the value for "message"
 */
char * jnu_getPurgeMessage(cJSON * purgeResponse) {
    return cJSON_GetObjectItemStringValue(purgeResponse, "message", NULL) ;
}

/**
 * TODO: instanceIds are longs not ints.
 * @return the instanceId (or jobInstanceId) value
 */
int jnu_getInstanceId(cJSON * jobInstance) {
    int retMe = cJSON_GetObjectItemIntValue(jobInstance, "instanceId", -1) ;
    return (retMe != -1) ? retMe : cJSON_GetObjectItemIntValue(jobInstance, "jobInstanceId", -1) ;
}

/**
 * @return 1 (true) or 0 (false)
 */
int jnu_isFinalLog(cJSON * finalLog) {
    return cJSON_GetObjectItemBoolValue(finalLog, "finalLog", 1) ;
}

/**
 * @return the page number for multi-purge operation
 */
int jnu_getPage(cJSON * args) {
    return cJSON_GetObjectItemIntValue(args, "page", -1);
}

/**
 * @return the page size for multi-purge operation
 */
int jnu_getPageSize(cJSON * args) {
    return cJSON_GetObjectItemIntValue(args, "pageSize", -1);
}

/**
 * @return the create time for multi-purge operation
 */
char * jnu_getCreateTime(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "createTime", NULL) ;
}

/**
 * @return the instance state for multi-purge operation
 */
char * jnu_getInstanceState(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "instanceState", NULL) ;
}

/**
 * @return the exit status for multi-purge operation
 */
char * jnu_getExitStatusForPurge(cJSON * args) {
    return cJSON_GetObjectItemStringValue(args, "exitStatus", NULL) ;
}

/**
 * TODO: executionIds are longs not ints.
 * @return the executionId (or jobExecutionId) value
 */
int jnu_getExecutionId(cJSON * jobExecution) {
    int retMe = cJSON_GetObjectItemIntValue(jobExecution, "executionId", -1) ;
    return (retMe != -1) ? retMe : cJSON_GetObjectItemIntValue(jobExecution, "jobExecutionId", -1) ;
}

/**
 * @return the "jobParameters" object. If it doesn't exist it will be created.
 */
cJSON * jnu_getOrCreateJobParameters( cJSON * args ) {
    cJSON * retMe = cJSON_GetObjectItem( args, "jobParameters" );
    if (retMe == NULL) {
        cJSON_AddItemToObject( args, "jobParameters", cJSON_CreateObject_ns());
    }
    return cJSON_GetObjectItem( args, "jobParameters" );
}

/**
 * @return 30 STARTING
 *         31 STARTED
 *         32 STOPPING
 *         33 STOPPED, 
 *         34 FAILED, 
 *         35 COMPLETED, 
 *         36 ABANDONED
 *         -1 if NULL
 *         -2 if unrecongized
 */
int jnu_getBatchStatusReturnCode( const char * batchStatus ) {
    if (batchStatus == NULL) {
        return jnu_error(__FUNCTION__,-1,"batchStatus is NULL");
    } else if (jnu_strequals( "STARTING", batchStatus ) ) {
        return 30;
    } else if (jnu_strequals( "STARTED", batchStatus ) ) {
        return 31;
    } else if (jnu_strequals( "STOPPING", batchStatus ) ) {
        return 32;
    } else if (jnu_strequals( "STOPPED", batchStatus ) ) {
        return 33;
    } else if (jnu_strequals( "FAILED", batchStatus ) ) {
        return 34;
    } else if (jnu_strequals( "COMPLETED", batchStatus ) ) {
        return 35;
    } else if (jnu_strequals( "ABANDONED", batchStatus ) ) {
        return 36;
    } else {
        jnu_trace(__FUNCTION__, "unrecognized batchStatus: %s", batchStatus);
        return -2;
    }
}

/**
 * @return atoi(exitStatus), if it begins with a parseable int;
 *         otherwise treat it as a batchStatus and call jnu_getBatchStatusReturnCode
 */
int jnu_parseExitStatusReturnCode( const char * exitStatus ) {
    return jnu_isNumber(exitStatus)
                ? jnu_parseInt(exitStatus, -1)
                : jnu_getBatchStatusReturnCode( exitStatus );
}

/**
 * @return non-zero if the given BatchStatus is one of the "done" states; zero otherwise.
 */
int jnu_isDone( const char * batchStatus ) {
    int batchStatusCode = jnu_getBatchStatusReturnCode( batchStatus ) ;
    return (batchStatusCode >= 33 && batchStatusCode <= 36);
}

/**
 * @return non-zero if the given BatchStatus is one of the "restartable" states; zero otherwise.
 */
int jnu_isRestartable( const char * batchStatus ) {
    int batchStatusCode = jnu_getBatchStatusReturnCode( batchStatus ) ;
    return (batchStatusCode == 33 || batchStatusCode == 34);
}

/**
 * @return non-zero if the given instance status is one of the "done" states; zero otherwise
 */
int jnu_isInstanceDone (const char * instanceState) {
    if (jnu_strequals( "COMPLETED", instanceState) ||
        jnu_strequals( "FAILED", instanceState) ||
        jnu_strequals( "STOPPED",instanceState))
        return 1;
    else
        return 0;
}

/**
 * @return non-zero if the given instance status is one of the "restartable" states; zero otherwise
 */
int jnu_isInstanceRestartable (const char * instanceState) {
    if (jnu_strequals( "FAILED", instanceState) ||
        jnu_strequals( "STOPPED",instanceState))
        return 1;
    else
        return 0;
}

/*
 * Print the contents of the job log
 */
int jnu_printJobLog(cJSON * jobLog) {

    static char * dashes = "==========================================================";
    int finalJobLevelLog;

    jnu_println(dashes);
    jnu_println("JobInstance id = %d, JobExecution id = %d, Application Name = %s, Part Number = %d",
                jnu_getInstanceId(jobLog),
                jnu_getExecutionId(jobLog),
                jnu_getAppName(jobLog),
                jnu_getPartNumber(jobLog)
                );
    finalJobLevelLog = jnu_isFinalLog(jobLog);

    if (jnu_getFlowName(jobLog) != NULL) {
        jnu_println("Split Name = %s, Flow Name = %s",
                    jnu_getSplitName(jobLog),
                    jnu_getFlowName(jobLog)
                    );
        // Override value obtained above since this is a Split/Flow.
        finalJobLevelLog = 0;
    }

    if (jnu_getPartitionNumber(jobLog) != -1) {
        jnu_println("Step Name = %s, Partition Number = %d",
                    jnu_getStepName(jobLog),
                    jnu_getPartitionNumber(jobLog)
                    );
        // Override value obtained above since this is a partition.
        finalJobLevelLog = 0;
    }
    jnu_println(dashes);

    // Print out the actual job log contents
    cJSON * jobLogContents = cJSON_GetObjectItem(jobLog,"contents");
    for (int i=0; i < cJSON_GetArraySize_ns(jobLogContents); ++i) {
        jnu_println(cJSON_GetArrayItem(jobLogContents, i)->valuestring);
    }
    return finalJobLevelLog;
}
