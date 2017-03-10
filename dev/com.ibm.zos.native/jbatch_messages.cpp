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
#include "include/jbatch_models.h"
#include "include/jbatch_utils.h"


/**
 * Help text for task options.
 */
static char * batchManager = 
"    --batchManager=[WOLA 3-Part Name]\n"
"       The WOLA 3-Part Name of the batchManager to connect to.        \n"
"       The parts may be separated by ' ' or '+'.  For example,        \n"
"       --batchManager=LIBERTY+BATCH+MANAGER";

static char * controlPropertiesFile = 
"    --controlPropertiesFile=[control-properties-file] \n"
"       A properties file containing control parameters, such as the   \n"
"       --batchManager. These parameters are overridden by parameters  \n"
"       specified directly on the command line.";

static char * wait = 
"   --wait\n"
"       If specified, the program will wait for the job to complete    \n"
"       before exiting. The exit code is set according to the job's    \n"
"       batch status (unless --returnExitStatus is specified).         \n"
"       Batch status exit codes:                                       \n"
"           BatchStatus.STOPPED = 33                                   \n"
"           BatchStatus.FAILED = 34                                    \n"
"           BatchStatus.COMPLETED = 35                                 \n"
"           BatchStatus.ABANDONED = 36";

static char * queueManagerName =
"   --queueManagerName=[queueManagerName].\n"
"       Name of the MQ queue manager to connect to in order to receive  \n"
"       Job Event messages. This option must be combined with \"--wait\"\n"
"       The program waits for the job to complete by listening for a    \n"
"       Job Event message indicating the job has finished.";

static char * queueName =
"   --queueName=[queueName].\n"
"       Name of the MQ queue to connect to in order to receive Job Event\n"
"       messages. This option must be combined with \"--wait\" and      \n"
"       \"--queueManagerName\" The program waits for the job to complete\n"
"       by listening for a Job Event message indicating the job has \n"
"       finished.";

static char * topicRoot =
"   --topicRoot=[New-Topic-Root].\n"
"       (Optional) Set the batch events topic root with a               \n"
"       \"New-Topic-Root\".                                             \n"
"       For Example:                                                    \n"
"         Passing \"value1\" results in the program subscribing to      \n"
"         topic tree: \"value1/jobs/...\" replacing \"batch/jobs/...\". \n"
"       This option must be combined with \"--queueManagerName\" and    \n"
"        \"--wait\"";

static char * pollingInterval_s = 
"   --pollingInterval_s=[polling interval in seconds]\n"
"       The interval of time at which to poll for job status.          \n"
"       The default is 30 seconds.";

static char * verbose = 
"   --verbose\n"
"       If specified, the program will log a message every time it     \n"
"       polls for job status.";

static char * returnExitStatus = 
"   --returnExitStatus\n"
"       Use the job's exit status as this program's exit code. This    \n"
"       option must be combined with --wait. If the exit status matches\n"
"       a BatchStatus name (e.g. COMPLETED), then the exit code is set \n"
"       according to the mapping described by option --wait. Otherwise \n"
"       the exit code is parsed from the beginning of the exit status  \n"
"       string. For example:                                           \n"
"           exitStatus='0', exit code: 0                               \n"
"           exitStatus='8:failure message can go here', exit code: 8";

static char * applicationName = 
"   --applicationName=[applicationName]\n"
"       The name of the batch application.                             \n"
"       Note: Either --applicationName or --moduleName must be         \n"
"       specified. If --applicationName is not specified it defaults to\n"
"       [moduleName] without the '.war' or '.jar' extension.";

static char * moduleName = 
"   --moduleName=[moduleName]\n"
"       Identifies a WAR or EJB module within the batch application.   \n"
"       The job is submitted under the module's component context.     \n"
"       Note: Either --applicationName or --moduleName must be         \n"
"       specified. If --moduleName is not specified it defaults to     \n"
"       '[applicationName].war'.";

static char * componentName = 
"   --componentName=[componentName]\n"
"       Identifies an EJB component within the batch application EJB   \n"
"       module. The job is submitted under the EJB's component context.";

static char * jobXMLName = 
"   --jobXMLName=[jobXMLName]\n"
"       The name of the job XML describing the job. The file is read   \n"
"       from the batch-jobs directory in the application module.       \n"
"       Note: Either --jobXMLName or --jobXMLFile must be specified. "; 

static char * jobXMLFile = 
"   --jobXMLFile=[jobXMLFile]\n"
"       The name of a file containing the JSL for the job. The file is \n"
"       read by the batchManagerZos utility and submitted with the     \n"
"       request, rather than being read from the batch-jobs directory  \n"
"       in the application module.                                     \n"
"       Note: Either --jobXMLName or --jobXMLFile must be specified. "; 

static char * jobParametersFile = 
"   --jobParametersFile=[job-parameters-file]\n"
"       A properties file containing job parameters.                   \n"
"       This is an alias of the option --jobPropertiesFile.";

static char * jobPropertiesFile = 
"   --jobPropertiesFile=[job-properties-file]\n"
"       A properties file containing job parameters.                   \n"
"       This is an alias of the option --jobParametersFile.";

static char * jobParameter = 
"   --jobParameter=[name]=[value]\n"
"       Specify a job parameter. More than one --jobParameter option   \n"
"       can be specified. The --jobParameter option overrides similarly\n"
"       named properties in --jobParametersFile";

static char * jobInstanceId = 
"   --jobInstanceId=[jobInstanceId]\n"
"       The job instance.\n"
"       Note: Either --jobInstanceId or --jobExecutionId must be       \n"
"       specified.";

static char * purge_jobInstanceId =
"   --jobInstanceId=[jobInstanceId]\n"
"       The job instance id filter applied to the purge of job instance records.\n"
"       For example:\n"
"           --jobInstanceId=10:20 purges records 10 to 20.\n"
"           --jobInstanceId=\">10\" purges records greater than equal to 10.\n"
"           --jobInstanceId=\"<10\" purges records less than equal to 10.\n"
"           --jobInstanceId=10,12,15 purges records 10, 12, and 15.\n"
"           If --page and --pageSize not specified, a default of 50 max\n"
"           records are purged.";

static char * listJobs_jobInstanceId =
"   --jobInstanceId=[jobInstanceId]\n"
"       The job instance id filter applied to the job instance records.\n"
"       For example:\n"
"           --jobInstanceId=10:20 returns records 10 to 20.\n"
"           --jobInstanceId=\">10\" returns records greater than equal to 10.\n"
"           --jobInstanceId=\"<10\" returns records less than equal to 10.\n"
"           --jobInstanceId=10,12,15 returns records 10, 12, and 15.\n"
"           If --page and --pageSize not specified, a default of 50 max\n"
"           records are returned.";

static char * page =
"   --page=[page]\n"
"       The page of job instance records to return. Page numbers start at 0.\n"
"       For example:\n"
"           --page=0 --pageSize=10 returns the first 10 records.\n"
"           --page=2 --pageSize=10 returns records 20 through 29.\n"
"       If not specified, the default is 0.";

static char * pageSize =
"   --pageSize=[pageSize]\n"
"       The size of the page of job instance records to return.\n"
"       For example:\n"
"           --page=0 --pageSize=10 returns the first 10 records.\n"
"           --page=1 --pageSize=20 returns records 20 through 39.\n"
"       If not specified, the default is 50.";

static char * purgeJobStoreOnly =
"   --purgeJobStoreOnly\n"
"       Indicates that the purge operation should only delete entries from \n"
"       the job store database. No attempt will be made to delete the job logs from \n"
"       the file system.";

static char * purge_createTime =
"   --createTime=[createTime]\n"
"       The create time filter applied to the purge of job instance records.\n"
"       For example:\n"
"           --createTime=2015-09-10:2015-09-27 purges records between\n"
"           2015-09-10 and 2015-09-27.\n"
"           --createTime=\">3d\" purges records greater than equal to 3 days ago UTC.\n"
"           --createTime=\"<3d\" purges records less than equal to 3 days ago UTC.\n"
"           --createTime=2015-09-15 purges all records on 2015-09-15.\n"
"       If --page and --pageSize not specified, a default of 50 max\n"
"       records are purged. Whenever specifying createTime=>Xd or\n"
"       createTime<Xd the date will be calculated on the dispatcher server\n"
"       in UTC time.";

static char * listJobs_createTime =
"   --createTime=[createTime]\n"
"       The create time filter applied to the job instance records.\n"
"       For example:\n"
"           --createTime=2015-09-10:2015-09-27 returns records between\n"
"           2015-09-10 and 2015-09-27.\n"
"           --createTime=\">3d\" returns records greater than equal to 3 days ago UTC.\n"
"           --createTime=\"<3d\" returns records less than equal to 3 days ago UTC.\n"
"           --createTime=2015-09-15 returns all records on 2015-09-15.\n"
"       If --page and --pageSize not specified, a default of 50 max\n"
"       records are returned. Whenever specifying createTime=>Xd or\n"
"       createTime<Xd the date will be calculated on the dispatcher server\n"
"       in UTC time.";

static char * purge_instanceState =
"   --instanceState=[instanceState]\n"
"       The instance state filter applied to the purge of job instance records.\n"
"       For example:\n"
"           --instanceState=COMPLETED,FAILED,STOPPED purges records in the\n"
"           COMPLETED, FAILED, and STOPPED states.\n"
"       If --page and --pageSize not specified, a default of 50 max\n"
"       records are purged.";

static char * listJobs_instanceState =
"   --instanceState=[instanceState]\n"
"       The instance state filter applied to the job instance records.\n"
"       For example:\n"
"           --instanceState=COMPLETED,FAILED,STOPPED returns records in the\n"
"           COMPLETED, FAILED, and STOPPED states.\n"
"       If --page and --pageSize not specified, a default of 50 max\n"
"       records are returned.";

static char * purge_exitStatus =
"   --exitStatus=[exitStatus]\n"
"       The exit status filter applied to the job execution records that are\n"
"       associated with the purge of job instance records.\n"
"       For example:\n"
"           --exitStatus=*JOB* purges job instance records having execution\n"
"               records containing the word JOB in their exit status.\n"
"       Note: The criteria may utilize the wildcard (*) operator on\n"
"       either end.\n"
"       If --page and --pageSize not specified, a default of 50 max\n"
"       records are purged.";

static char * listJobs_exitStatus =
"   --exitStatus=[exitStatus]\n"
"       The exit status filter applied to the job execution records that are\n"
"       associated with job instance records.\n"
"       For example:\n"
"           --exitStatus=*JOB* returns job instance records having execution\n"
"               records containing the word JOB in their exit status.\n"
"       Note: The criteria may utilize the wildcard (*) operator on\n"
"       either end.\n"
"       If --page and --pageSize not specified, a default of 50 max\n"
"       records are returned.";

static char * jobExecutionId = 
"   --jobExecutionId=[jobExecutionId]\n"
"       The job execution.\n"
"       Note: Either --jobInstanceId or --jobExecutionId must be       \n"
"       specified.";

static char * reusePreviousParams = 
"   --reusePreviousParams\n"
"       If specified, the job will reuse the job parameters from the   \n"
"       previous execution of this job.                                \n";

static char * restartTokenFile =
"   --restartTokenFile=[restart-token-file]\n"
"       The name of a file which holds the instance Id of the job \n"
"       to be restarted.  The file is read by the batchManagerZos utility. \n"
"       If the file contains an instance Id, the job is restarted.  If not, \n"
"       a new job is submitted and the resulting instance Id is stored in \n"
"       the file.  The file can be a dataset name ('USER.MY.FILE'), \n"
"       a file (/u/user/myfile) or a DD (DD:RSTRTID) ";

static char * getJobLog =
"   --getJobLog\n"
"       If specified, the program will subscribe to job log events \n"
"       and print the message content to STDOUT as messages arrive. \n"
"       This option must be combined with --queueManagerName and --wait. ";

/**
 * Issue message "CWWKY0101I: Job %s with instance ID %d has been submitted.",
 */
int jnu_issueJobSubmittedMessage(cJSON * jobInstance) {
    return jnu_info("CWWKY0101I: Job %s with instance ID %d has been submitted.",
                    jnu_getJobName(jobInstance),
                    jnu_getInstanceId(jobInstance) );
}

/**
 * Issue message "CWWKY0115I: Job %s with instance ID %d has been successfully purged."
 */
int jnu_issueJobPurgedMessage(cJSON * jobInstance) {
    return jnu_info("CWWKY0115I: Job %s with instance ID %d has been successfully purged.",
                    jnu_getJobName(jobInstance),
                    jnu_getInstanceId(jobInstance) );

}

/**
 * Issue message "CWWKY0118I: "Subscribing to events at JMS topic root: %s"
 */
int jnu_issueTopicRootMessage(char * eventTopicRoot) {
    return jnu_info("CWWKY0118I: Subscribing to batch events at JMS topic root: %s.",
                    eventTopicRoot);

}

/**
 * Issue message "CWWKY0117I: Attempt to purge job with instance id %d returned status: %s. Message: %s"
 *
 */
int jnu_issueJobPurgedMessageMulti(cJSON * purgeResponse) {
    return jnu_info("CWWKY0117I: Attempt to purge job with instance id %d returned status: %s. Message: %s",
                    jnu_getInstanceId(purgeResponse),
                    jnu_getPurgeStatus(purgeResponse),
                    jnu_getPurgeMessage(purgeResponse));
}

/**
 * Issue "CWWKY0106I: JobInstance:%s"
 */
int jnu_issueJobInstanceMessage(cJSON * jobInstance) {
    return jnu_info("CWWKY0106I: JobInstance:%s", cJSON_PrintUnformatted(jobInstance));
}

/**
 * Issue "CWWKY0107I: JobExecution:{0}"
 */
int jnu_issueJobExecutionMessage(cJSON * jobExecution) {
    return jnu_info("CWWKY0107I: JobExecution:%s", cJSON_PrintUnformatted(jobExecution));
}

/**
 * Issue "CWWKY0108I: Waiting for termination of JobExecution:%s"
 */
int jnu_issueWaitingForTerminationMessage(cJSON * jobExecution) {
    return jnu_info( "CWWKY0108I: Waiting for termination of JobExecution:%s", cJSON_PrintUnformatted(jobExecution));
}

/**
 * Issue "CWWKY0109I: Waiting for the latest job execution for job with instance ID {0}"
 */
int jnu_issueWaitingForLatestJobExecutionMessage(int instanceId) {
    return jnu_info( "CWWKY0109I: Waiting for the latest job execution for job with instance ID %d", instanceId);
}

/**
 * Issue "CWWKY0110I: Waiting for the next job execution after JobExecution:{0}"
 */
int jnu_issueWaitForNextJobExecutionMessage(cJSON * prevJobExecution) {
    return jnu_info( "CWWKY0110I: Waiting for the next job execution after JobExecution:%s", cJSON_PrintUnformatted(prevJobExecution));
}
        
/**
 * Issue "CWWKY0102I: A restart request has been submitted for job {0} with instance ID {1}."
 */
int jnu_issueRestartSubmittedMessage( cJSON * jobInstance ) {
    return jnu_info("CWWKY0102I: A restart request has been submitted for job %s with instance ID %d.",
                    jnu_getJobName(jobInstance),
                    jnu_getInstanceId(jobInstance) );
}

/**
 * Issue "CWWKY0103I: Job {0} with instance ID {1} has stopped. Batch status: {2}. Exit status: {3}"
 */
int jnu_issueJobStoppedMessage(cJSON * jobInstance, cJSON * jobExecution) {
    return jnu_info("CWWKY0103I: Job %s with instance ID %d has stopped. Batch status: %s. Exit status: %s",
                    jnu_getJobName(jobInstance),
                    jnu_getInstanceId(jobInstance) ,
                    jnu_getBatchStatus(jobExecution) ,
                    jnu_getExitStatus(jobExecution) );
}

/**
 * issue "CWWKY0104I: A stop request has been submitted for job {0} with instance ID {1}."
 */
int jnu_issueStopSubmittedMessage(cJSON * jobInstance) {
    return jnu_info( "CWWKY0104I: A stop request has been submitted for job %s with instance ID %d.",
                    jnu_getJobName(jobInstance),
                    jnu_getInstanceId(jobInstance) );
}


/**
 * Issue "CWWKY0105I: Job %s with instance ID %d has finished. Batch status: %s. Exit status: %s",
 */
int jnu_issueJobFinishedMessage(cJSON * jobInstance, cJSON * jobExecution) {
    return jnu_info( "CWWKY0105I: Job %s with instance ID %d has finished. Batch status: %s. Exit status: %s",
                    jnu_getJobName(jobInstance),
                    jnu_getInstanceId(jobInstance) ,
                    jnu_getBatchStatus(jobExecution) ,
                    jnu_getExitStatus(jobExecution) );
}

/**
 * TODO: how to get the REST URL ?  
 * issue "CWWKY0113I: You can view the joblog by following the links here: {2}"
 */
int jnu_issueJobLogLocationMessage(cJSON * jobInstance) {
    return jnu_info("CWWKY0113I: You can view the joblog by following the links here: %s", "TODO");
}

// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
//      Usage and help messages
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------

/**
 * Contains help text for a task (name, description, options).
 */
typedef struct {
    const char * name;
    const char * description;
    char ** requiredArgs;
    char ** optionalArgs;
} jnu_Task;


/**
 * @return a jnu_Task with the given name and description filled in.  Must be free'd.
 */
jnu_Task * jnu_mallocTask(const char * name, const char * description) {

    jnu_Task * retMe = (jnu_Task *) jnu_malloc( sizeof(jnu_Task) );
    if (retMe == NULL) {
        return NULL;
    }

    retMe->name = strdup(name);
    retMe->description = strdup(description);

    return retMe;
}


/**
 * @return a jnu_Task.  Must be free'd.
 */
jnu_Task * jnu_loadHelpTask() {
    return jnu_mallocTask( "help", "Use help [action] for detailed option information.");
}

/**
 * @return a jnu_Task. Must be free'd.
 */
jnu_Task * jnu_loadPurgeTask() {

    jnu_Task * retMe = jnu_mallocTask("purge", "Purge all records and logs for a job instance or purge a list of job instance records.");
    if (retMe == NULL) {
            return NULL;
    }

    retMe->requiredArgs = jnu_mallocStringArray(1, batchManager );

    retMe->optionalArgs = jnu_mallocStringArray(7,
                                                purge_jobInstanceId,
                                                page,
                                                pageSize,
                                                purgeJobStoreOnly,
                                                purge_createTime,
                                                purge_instanceState,
                                                purge_exitStatus);

    return retMe;
}

/**
 * @return a jnu_Task. Must be free'd.
 */
jnu_Task * jnu_loadListJobsTask() {
    jnu_Task * retMe = jnu_mallocTask("listJobs", "List job instances");

    if (retMe == NULL) {
                return NULL;
        }

        retMe->requiredArgs = jnu_mallocStringArray(1, batchManager );

        retMe->optionalArgs = jnu_mallocStringArray(6,
                                                    listJobs_jobInstanceId,
                                                    page,
                                                    pageSize,
                                                    listJobs_createTime,
                                                    listJobs_instanceState,
                                                    listJobs_exitStatus);

        return retMe;

}

/**
 * @return a jnu_Task. Must be free'd.
 */
jnu_Task * jnu_loadSubmitTask() {

    jnu_Task * retMe = jnu_mallocTask("submit", "Submit a new batch job.");
    if (retMe == NULL) {
        return NULL;
    }

    retMe->requiredArgs = jnu_mallocStringArray(1, batchManager );

    retMe->optionalArgs = jnu_mallocStringArray(17,
                                                applicationName,
                                                moduleName,
                                                componentName,
                                                jobXMLName,
                                                jobXMLFile,
                                                jobParameter,
                                                jobParametersFile,
                                                jobPropertiesFile,
                                                controlPropertiesFile,
                                                restartTokenFile,
                                                wait,
                                                queueManagerName,
                                                /*queueName,*/
                                                topicRoot,
                                                pollingInterval_s,
                                                getJobLog,
                                                verbose,
                                                returnExitStatus);
    return retMe;
}

/**
 * @return a jnu_Task. Must be free'd.
 */
jnu_Task * jnu_loadStopTask() {

    jnu_Task * retMe = jnu_mallocTask("stop", "Stop a batch job.");
    if (retMe == NULL) {
        return NULL;
    }

    retMe->requiredArgs = jnu_mallocStringArray(1, batchManager);

    retMe->optionalArgs = jnu_mallocStringArray(7, 
                                                jobInstanceId,
                                                jobExecutionId,
                                                controlPropertiesFile,
                                                wait,
                                                pollingInterval_s,
                                                verbose,
                                                returnExitStatus);
    return retMe;
}

/**
 * @return a jnu_Task. Must be free'd.
 */
jnu_Task * jnu_loadRestartTask() {

    jnu_Task * retMe = jnu_mallocTask("restart", "Restart a batch job.");
    if (retMe == NULL) {
        return NULL;
    }

    retMe->requiredArgs = jnu_mallocStringArray(1, batchManager);

    retMe->optionalArgs = jnu_mallocStringArray(13,
                                                jobInstanceId,
                                                jobExecutionId,
                                                jobParameter,
                                                jobParametersFile,
                                                jobPropertiesFile,
                                                reusePreviousParams,
                                                controlPropertiesFile,
                                                wait,
                                                topicRoot,
                                                pollingInterval_s,
                                                getJobLog,
                                                verbose,
                                                returnExitStatus);
    return retMe;
}

/**
 * @return a jnu_Task. Must be free'd.
 */
jnu_Task * jnu_loadStatusTask() {

    jnu_Task * retMe = jnu_mallocTask("status", "View the status of a job.");
    if (retMe == NULL) {
        return NULL;
    }

    retMe->requiredArgs = jnu_mallocStringArray(1, batchManager);

    retMe->optionalArgs = jnu_mallocStringArray(3, 
                                                jobInstanceId,
                                                jobExecutionId,
                                                controlPropertiesFile);
    return retMe;
}

/**
 * @return a jnu_Task. Must be free'd.
 */
jnu_Task * jnu_loadPingTask() {

    jnu_Task * retMe = jnu_mallocTask("ping", "'Ping' the batch manager to test connectivity.");
    if (retMe == NULL) {
        return NULL;
    }

    retMe->requiredArgs = jnu_mallocStringArray(1, batchManager);

    return retMe;
}

/**
 * @return the jnu_Task from tasks with the given name.
 */
jnu_Task * jnu_findTask(jnu_Task ** tasks, const char * name) {
    for (int i=0; tasks[i] != NULL; ++i) {
        jnu_Task * task = tasks[i];
        if ( jnu_strequals(name, task->name) ) {
            return task;
        }
    }
    return NULL;
}

/**
 * @return an array of ptrs to jnu_Task objects for all tasks.  Must be free'd.
 *         The array of ptrs is NULL term'd.
 */        
jnu_Task ** jnu_loadTasks() {

    int NumTasks = 8;

    int mallocSize = sizeof(jnu_Task *) * (NumTasks + 1);  // 1 extra to NULL term. 
    jnu_Task ** retMe = (jnu_Task **) jnu_malloc(mallocSize);
    if (retMe == NULL) {
        return NULL;
    }

    retMe[0] = jnu_loadHelpTask();
    retMe[1] = jnu_loadPingTask();
    retMe[2] = jnu_loadSubmitTask();
    retMe[3] = jnu_loadStopTask();
    retMe[4] = jnu_loadRestartTask();
    retMe[5] = jnu_loadStatusTask();
    retMe[6] = jnu_loadPurgeTask();
    retMe[7] = jnu_loadListJobsTask();
    retMe[8] = NULL;

    return retMe;
}

/**
 * Print a short usage statement.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_printUsage() {
    
    jnu_Task ** tasks = jnu_loadTasks();
    if (tasks == NULL) {
        return -1;
    }

    // Build the command list.
    char commandList[1024];  // this should be more than enough room...
    memset(commandList, 0, sizeof(commandList));

    for (int i=0; tasks[i] != NULL; ++i) {
        if (i > 0) {
            strcat(commandList, "|");
        }
        strcat(commandList,tasks[i]->name);
    }

    jnu_println("Usage: %s {%s} [options]", "batchManagerZos", commandList);
    jnu_println("");

    // TODO: Should free(tasks), but who cares. The program is about to exit.
    return 0;
}

/**
 * Print the help text.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_printHelp() {
    jnu_printUsage();
    jnu_println("Actions:");
    jnu_println("");

    jnu_Task ** tasks = jnu_loadTasks();
    if (tasks == NULL) {
        return -1;
    }

    for (int i=0; tasks[i] != NULL; ++i) {
        jnu_Task * task = tasks[i];
        jnu_println("   %s", task->name);
        jnu_println("       %s", task->description);
        jnu_println("");
    }

    jnu_println("Options:");
    jnu_println("       Use help [action] for detailed option information.");
    jnu_println("");

    // TODO: Should free(tasks), but who cares. The program is about to exit.
    return 0;
}
 
/**
 * Print detailed help for the given command.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_printTaskHelp(char * command) {

    jnu_Task ** tasks = jnu_loadTasks();
    if (tasks == NULL) {
        return -1;
    }

    jnu_Task * task = jnu_findTask(tasks, command);

    if (task == NULL) {
        return jnu_println("Unknown task: %s", command);
    }

    jnu_println("Usage:");
    jnu_println("       %s %s [options]", "batchManagerZos", task->name);
    jnu_println("");
    jnu_println("Description:");
    jnu_println("       %s", task->description);
    jnu_println("");

    if (task->requiredArgs != NULL) {
        jnu_println("Required:");

        for (int i=0; task->requiredArgs[i] != NULL; ++i) {
            jnu_println("%s", task->requiredArgs[i]);
            jnu_println("");
        }
    }

    if (task->optionalArgs != NULL) {
        jnu_println("Options:");

        for (int i=0; task->optionalArgs[i] != NULL; ++i) {
            jnu_println("%s", task->optionalArgs[i]);
            jnu_println("");
        }
    }

    // TODO: Should free(tasks), but who cares. The program is about to exit.
    return 0;
}





