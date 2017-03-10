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
#ifndef __jbatch_models_h__
#define __jbatch_models_h__

#include "jbatch_json.h"

/**
 * @return "--wait" if specified, otherwise NULL.
 */
char * jnu_getWait(cJSON * args) ;

/**
 * @return "--getJobLog" if specified, otherwise NULL.
 */
char * jnu_getJobLog(cJSON * args) ;


/**
 * @return "--verbose" if specified, otherwise NULL.
 */
char * jnu_getVerbose(cJSON * args) ;

/**
 * @return the value for "--queueManagerName" if specified, otherwise NULL.
 */
char * jnu_getQueueManagerName(cJSON * args) ;

/**
 * @return the value for "--queueName" if specified, otherwise NULL.
 */
char * jnu_getQueueName(cJSON * args);

/**
 * @return the value for "--topicRoot" if specified, otherwise NULL.
 */
char * jnu_getTopicRoot(cJSON * args) ;

/**
 * @return the value for "batchManager" if specified, otherwise NULL.
 */
char * jnu_getBatchManager(cJSON * args) ;

/**
 * @return the value for "command" if specified, otherwise NULL.
 */
char * jnu_getCommand(cJSON * args) ;

/**
 * @return the int value for "pollingInterval_s" if specified, otherwise the default is 30.
 */
int jnu_getPollingInterval(cJSON * args) ;

/**
 * @return the value for "--controlPropertiesFile", or NULL
 */
char * jnu_getControlPropertiesFile( cJSON * args ) ;

/**
 * @return the value for "--jobXMLFile", or NULL
 */
char * jnu_getJobXMLFile( cJSON * args ) ;

/**
 * @return the "jobParameters" object. If it doesn't exist it will be created.
 */
cJSON * jnu_getOrCreateJobParameters( cJSON * args ) ;

/**
 * @return the value for "--jobPropertiesFile" or "--jobParametersFile",
 *         or NULL if neither is specified.
 */
char * jnu_getJobParametersFile( cJSON * args ) ;

/**
 * @return "returnExitStatus" if specified, otherwise NULL.
 */
char * jnu_getReturnExitStatus(cJSON * args) ;

/**
 * @return the value for "batchStatus" if specified, otherwise NULL.
 */
char * jnu_getBatchStatus(cJSON * json) ;

/**
 * @return the value for "exitStatus" if specified, otherwise NULL.
 */
char * jnu_getExitStatus(cJSON * json) ;

/**
 * @return the value for "jobName"
 */
char * jnu_getJobName(cJSON * jobInstance) ;

/**
 * @return the value for "appName"
 */
char * jnu_getAppName(cJSON * args);

/**
 * @return the job log part number
 */
int jnu_getPartNumber(cJSON * args);

/**
 * @return the partition number
 */
int jnu_getPartitionNumber(cJSON * args);

/**
 * @return the value for "stepName"
 */
char * jnu_getStepName(cJSON * args);

/**
 * @return the value for "splitName"
 */
char * jnu_getSplitName(cJSON * args);

/**
 * @return the value for "flowName"
 */
char * jnu_getFlowName(cJSON * args);

/**
 * Print the contents of the job log
 */
int jnu_printJobLog(cJSON * jobLog);

/**
 * @return the value for "purgeStatus"
 */
char * jnu_getPurgeStatus(cJSON * purgeResponse);

/**
 * @return the value for "message"
 */
char * jnu_getPurgeMessage(cJSON * purgeResponse);


/**
 * TODO: instanceIds are longs not ints.
 * @return the instanceId value
 */
int jnu_getInstanceId(cJSON * jobInstance) ;

/**
 * @return the the value for "jobInstanceId"
 */
char * jnu_getJobInstanceId(cJSON * args);

/**
 * @return the instanceId (or jobInstanceId) value as a string
 */
char * jnu_getInstanceIdString(cJSON * jobInstance);

/**
 * @return the page number for multi-purge operation
 */
int jnu_getPage(cJSON * args);

/**
 * @return the page size for multi-purge operation
 */
int jnu_getPageSize(cJSON * args);

/**
 * @return the create time for multi-purge operation
 */
char * jnu_getCreateTime(cJSON * args);

/**
 * @return the instance state for multi-purge operation
 */
char * jnu_getInstanceState(cJSON * args);

/**
 * @return the exit status for multi-purge operation
 */
char * jnu_getExitStatusForPurge(cJSON * args);

/**
 * TODO: executionIds are longs not ints.
 * @return the executionId value
 */
int jnu_getExecutionId(cJSON * jobExecution) ;


/**
 * @return 33 STOPPED, 
 *         34 FAILED, 
 *         35 COMPLETED, 
 *         36 ABANDONED
 *         -1 if NULL
 *         -2 if not in a finished state
 */
int jnu_getBatchStatusReturnCode( const char * batchStatus ) ;

/**
 * @return the value for "--restartTokenFile", or NULL
 */
char * jnu_getRestartTokenFile( cJSON * args );

/**
 * @return atoi(exitStatus), if it begins with a parseable int;
 *         otherwise treat it as a batchStatus and call jnu_getBatchStatusReturnCode
 */
int jnu_parseExitStatusReturnCode( const char * exitStatus ) ;

/**
 * @return non-zero if the given BatchStatus is one of the "done" states; zero otherwise.
 */
int jnu_isDone( const char * batchStatus ) ;

/**
 * @return non-zero if the given BatchStatus is one of the "restartable" states; zero otherwise.
 */
int jnu_isRestartable( const char * batchStatus ) ;

/**
 * @return non-zero if the given instance state is one of the "done" states; zero otherwise
 */
int jnu_isInstanceDone (const char * instanceState);

/**
 * @return non-zero if the given instance state is one of the "restartable" states; zero otherwise
 */
int jnu_isInstanceRestartable (const char * instanceState);

/**
 * @return the int value for "instanceId" if specified, otherwise the default is -1.
 */
int jnu_parseJobInstanceId(cJSON * args) ;

#endif
