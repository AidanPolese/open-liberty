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
#ifndef __jbatch_messages_h__
#define __jbatch_messages_h__

#include "jbatch_json.h"

/**
 * Issue message "CWWKY0115I: Job %s with instance ID %d has been successfully purged."
 */
int jnu_issueJobPurgedMessage(cJSON * jobInstance);

/**
 * Issue message "CWWKY0117I: Attempt to purge job with instance id %d returned status: %s. Message: %s"
 *
 */
int jnu_issueJobPurgedMessageMulti(cJSON * purgeResponse);

/**
 * Issue message "CWWKY0118I: "Subscribing to events at JMS topic root: %s"
 */
int jnu_issueTopicRootMessage(char * topicRoot);

/**
 * Issue message "CWWKY0101I: Job %s with instance ID %d has been submitted.",
 */
int jnu_issueJobSubmittedMessage(cJSON * jobInstance) ;

/**
 * Issue "CWWKY0106I: JobInstance:%s"
 */
int jnu_issueJobInstanceMessage(cJSON * jobInstance) ;

/**
 * Issue "CWWKY0107I: JobExecution:{0}"
 */
int jnu_issueJobExecutionMessage(cJSON * jobExecution) ;

/**
 * Issue "CWWKY0108I: Waiting for termination of JobExecution:%s"
 */
int jnu_issueWaitingForTerminationMessage(cJSON * jobExecution) ;

/**
 * Issue "CWWKY0109I: Waiting for the latest job execution for job with instance ID {0}"
 */
int jnu_issueWaitingForLatestJobExecutionMessage(int instanceId) ;

/**
 * Issue "CWWKY0110I: Waiting for the next job execution after JobExecution:{0}"
 */
int jnu_issueWaitForNextJobExecutionMessage(cJSON * prevJobExecution) ;
        
/**
 * Issue "CWWKY0102I: A restart request has been submitted for job {0} with instance ID {1}."
 */
int jnu_issueRestartSubmittedMessage( cJSON * jobInstance ) ;

/**
 * Issue "CWWKY0103I: Job {0} with instance ID {1} has stopped. Batch status: {2}. Exit status: {3}"
 */
int jnu_issueJobStoppedMessage(cJSON * jobInstance, cJSON * jobExecution) ;

/**
 * issue "CWWKY0104I: A stop request has been submitted for job {0} with instance ID {1}."
 */
int jnu_issueStopSubmittedMessage(cJSON * jobInstance) ;

/**
 * Issue "CWWKY0105I: Job %s with instance ID %d has finished. Batch status: %s. Exit status: %s",
 */
int jnu_issueJobFinishedMessage(cJSON * jobInstance, cJSON * jobExecution) ;

/**
 * TODO: how to get the REST URL ?  
 * issue "CWWKY0113I: You can view the joblog by following the links here: {2}"
 */
int jnu_issueJobLogLocationMessage(cJSON * jobInstance) ;

/**
 * Print a short usage statement.
 * @return 0
 */
int jnu_printUsage() ;

/**
 * Print the help text.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_printHelp() ;

/**
 * Print detailed help for the given command.
 * @return 0
 */
int jnu_printTaskHelp(char * command) ;


#endif
