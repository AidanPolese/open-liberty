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
#include <unistd.h>
#include <sys/time.h>

#include "include/jbatch_task_args.h"
#include "include/jbatch_json.h"
#include "include/jbatch_utils.h"
#include "include/jbatch_models.h"

#define MAX_FILE_NAME_LEN 1024


/**
 * Parse the arg line (--arg, --arg=value) and put it in the args object.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_parseArg( char * arg, cJSON * args ) {

    char *argNameAndValue[2] = {NULL, NULL};
    int rc = jnu_split( arg, "=", (char **)&argNameAndValue, 2);

    if (argNameAndValue[0] == NULL) {
        return jnu_error(__FUNCTION__, -2, "Argument cannot be parsed: [%s]", arg);

    } else if ( strncmp(argNameAndValue[0],"--",2)  &&  !jnu_strequals( "help", cJSON_GetObjectItemStringValue( args, "command", "") ) ) {
        return jnu_error(__FUNCTION__, -3, "Argument does not begin with '--': [%s]", arg);
    }
    
    if (jnu_strequals( "--queueName", argNameAndValue[0] )){
        return jnu_error(__FUNCTION__, -2, "Argument cannot be parsed: [%s]", arg);
    }

    if ( jnu_strequals( "--jobParameter", argNameAndValue[0] ) ) {
        // Handle special --jobParameter= arg.
        jnu_parseProperty( argNameAndValue[1], jnu_getOrCreateJobParameters(args) );

    } else {
        // Note: trim off the leading "--" from the arg name.  
        // This is to accommodate JobSubmissionModel et al on the server side.
        cJSON_AddStringToObject(args, &argNameAndValue[0][2], ( (argNameAndValue[1] != NULL) ? argNameAndValue[1] : argNameAndValue[0] ) );
    }

    return 0;
}

/**
 * Parse the <prop>=<value> from the givne line and put it in props.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_parseProperty(char * lineIn, cJSON * props) {

    char *argNameAndValue[2] = {NULL, NULL};
    int rc = jnu_split( lineIn, "=", (char **)&argNameAndValue, 2);

    if (argNameAndValue[1] == NULL) {
        return jnu_error(__FUNCTION__,-1, "Property must be specified in format {prop}={value}: %s", lineIn);
    } 

    cJSON_AddStringToObject(props, argNameAndValue[0], argNameAndValue[1]);
    return 0;
}

/**
 * Parse command-line args and populate them into retMe.
 *
 * @param argc
 * @param argv
 * @param retMe the cJSON object to populate with parsed args
 *
 * @return 0 if all is well;
 *         -1 if argc < 2 (no command provided)
 *         -2 if arg could not be parsed
 *         -3 if arg does not begin with "--"
 *         
 */
int jnu_parseCommandLineArgs(int argc, char ** argv, cJSON * retMe) {

    // 0: ./batchManagerZos
    // 1: {command}  (submit, restart)
    // 2..n: args

    if (argc < 2) {
        return -1;
    }

	cJSON_AddStringToObject(retMe, "command", argv[1]);

    for (int i=2; i < argc; ++i) {

        int rc = jnu_parseArg( argv[i], retMe );
        if (rc != 0) {
            return rc;
        }
    }

    return 0;
}

/**
 * @return non-zero if the line should be skipped (#comment or all blanks).
 */
int jnu_skipPropsFileLine( const char * lineIn ) {
    return ( jnu_strStartsWith( lineIn, "#" ) || jnu_isallspaces(lineIn) );
}

/**
 * Note: this function blows up with an 0C4 if you try to run batchManagerZos
 * from a dataset.
 *
 * @return non-zero if the fd is ready to be read; 0 otherwise.
 */
int jnu_isReady(int fd) {

    fd_set fds;
    FD_ZERO(&fds);
    FD_SET(fd, &fds);

    struct timeval timeout;
    memset(&timeout,0,sizeof(timeout));
    timeout.tv_usec = 1000;     // micro-seconds

    int rc = select(fd+1, &fds, NULL, NULL, &timeout);
    if (rc < 0) {
        return jnu_error(__FUNCTION__,0,"select(%d) returned %d. errno:%d", fd, rc, errno);
    }

    return FD_ISSET(fd, &fds);
}

/**
 * @param f a file stream
 * @param props populated with the parsed properties
 * @param parseFunction the function to use to parse the property from the file line
 *
 * @return 0 if all's well. non-zero otherwise.
 */
int jnu_parsePropsStream( FILE * f, cJSON * props, int (*parseFunction)(char *,cJSON *) ) {

    // Note: the jnu_isReady check blows up with an 0C4 if you try to run batchManagerZos 
    // from a dataset.
    // -rx- if (!jnu_isReady( fileno(f) ) ) {
    // -rx-     jnu_trace(__FUNCTION__, "stream for fd %d is not ready to read", fileno(f));
    // -rx-     return 0;
    // -rx- }

    char line[1024];
    for ( char * lineIn = fgets(line, 1024, f);
          lineIn != NULL;
          lineIn = fgets(line, 1024, f) ) {

        // Remove newline, if present.
        lineIn = jnu_chomp( lineIn );
        lineIn = jnu_trim( lineIn );
        if ( !jnu_skipPropsFileLine( lineIn ) ) {

            jnu_trace(__FUNCTION__, "parsing line: %s", lineIn);

            (*parseFunction)(lineIn, props);

        } else {
            jnu_trace(__FUNCTION__, "skipping line: %s", lineIn);
        }
    }

    return 0;
}

/**
 * @param props populated with the parsed properties
 * @param parseFunction the function to use to parse the property from the file line
 *
 * @return 0 if all's well. non-zero otherwise.
 */
int jnu_parsePropsFile( const char * fileName, cJSON * props, int (*parseFunction)(char *,cJSON *) ) {

    FILE * f = fopen(fileName, "r");
    if (f == NULL) {
        return jnu_error(__FUNCTION__,-1, "fopen(%s) returned NULL. errno: %d", fileName, errno);
    }

    jnu_parsePropsStream(f, props, parseFunction);

    if ( fclose(f) != 0 ) {
        jnu_error(__FUNCTION__,0, "fclose(%s) failed. errno: %d", fileName, errno);
    }

    return 0;
}

/**
 * Open --controlPropertiesFile, parse it, and merge it with the
 * command-line args provided in args.  Command-line args supercede
 * controlProps.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_resolveControlProps(cJSON * args) {

    // Look for --controlPropertiesFile
    char * controlPropsFiles = jnu_getControlPropertiesFile( args );
    char * sp[20] = {NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL};
    int numsplit = jnu_split( controlPropsFiles, ",", (char **)&sp, 20);

    int i=0;
    char  * controlPropsFile = sp[i];
    while(i<numsplit && i<20) {
        char  * controlPropsFile = sp[i];

        cJSON * controlProps = cJSON_CreateObject_ns();
        if (controlProps == NULL) {
            return 255;
        }

        int rc = jnu_parsePropsFile( controlPropsFile, controlProps, &jnu_parseArg);
        if (rc != 0) {
            return rc;
        }

        cJSON_MergeObjects( args, controlProps, 1);//Overwrite for now and then overwrite cmd line args

        cJSON_Delete_ns(controlProps);
        i=i+1;
    }

    return 0;
}

/**
 * Open --jobParametersFile and parse it, adding the "jobParameters"
 * object to the given args.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_resolveJobParams( cJSON * args ) {

    char * jobParamsFileNames = jnu_getJobParametersFile( args );
    char * sp[20] = {NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL};
    int numsplit = jnu_split( jobParamsFileNames, ",", (char **)&sp, 20);

    int i=0;
    char  * jobParamsFileName = sp[i];
    while(i<numsplit && i<20) {
        char  * jobParamsFileName = sp[i];

        cJSON * jobParamsFromFile = cJSON_CreateObject_ns();
        if (jobParamsFromFile == NULL) {
            return 255;
        }

        int rc = jnu_parsePropsFile( jobParamsFileName, jobParamsFromFile, &jnu_parseProperty);
        if (rc != 0) {
            return rc;
        }

        cJSON * jobParams = jnu_getOrCreateJobParameters(args);
        cJSON_MergeObjects( jobParams, jobParamsFromFile, 1);//Overwrite for now and then overwrite cmd line args

        cJSON_Delete_ns(jobParamsFromFile);
        i=i+1;
    }
    
    return 0;
}

/**
 * @param props populated with the parsed properties
 * @param parseFunction the function to use to parse the property from the file line
 *
 * @return 0 if all's well. non-zero otherwise.
 */
int jnu_readInlineJsl( const char * fileName, cJSON * args ) {

    FILE * f = fopen(fileName, "r");
    if (f == NULL) {
        return jnu_error(__FUNCTION__,-1, "fopen(%s) returned NULL. errno: %d", fileName, errno);
    }

    char * fileContents = jnu_readFile( f );
    if (fileContents == NULL) {
        return jnu_error(__FUNCTION__,-2, "failed to read inline JSL from file %s. errno: %d", fileName, errno);
    }

    // Add inline JSL to the args object
    cJSON_AddStringToObject(args, "jobXML", fileContents);
    
    if ( fclose(f) != 0 ) {
        jnu_error(__FUNCTION__,0, "fclose(%s) failed. errno: %d", fileName, errno);
    }

    return 0;
}

/**
 * Read the --jobXMLFile and marshal its contents into the args object
 * under the jobXML field.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_resolveInlineJsl(cJSON * args) {

    // Look for --jobXMLFile
    char * jobXMLFile = jnu_getJobXMLFile( args ); 
    return (jobXMLFile != NULL) ?  jnu_readInlineJsl(jobXMLFile, args) : 0;
}

/**
 * Open --restartTokenFile and parse it, adding the "restartJob"
 * object to the given args.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_resolveRestartToken(cJSON * args) {

    // Look for --restartTokenFile
    char * restartTokenFileName = jnu_getRestartTokenFile( args );
    if ( restartTokenFileName != NULL ) {

        cJSON * restartTokenFromFile = cJSON_CreateObject_ns();
        if (restartTokenFromFile == NULL) {
            return 255;
        }

        char fname[MAX_FILE_NAME_LEN];
        memset(fname,0,MAX_FILE_NAME_LEN);
        strcpy(fname,"//");
        strcat(fname,restartTokenFileName);

        int rc = jnu_parsePropsFile( fname, restartTokenFromFile, &jnu_parseProperty );
        if (rc != 0) {
            return rc;
        }

        cJSON * instanceId = cJSON_GetObjectItem( restartTokenFromFile, "restartJob" );
        if (instanceId != NULL) {
            cJSON_AddItemToObject( args, "instanceId", instanceId);
        }

    }

    return 0;
}

/**
 * Remove the instance Id from the args and clear the
 * restart token file.
 *
 */
void jnu_removeRestartToken(cJSON * args) {
    cJSON_DeleteItemFromObject( args, "instanceId");
    jnu_clearRestartTokenFromFile(args);
}

int jnu_writeRestartToken(cJSON * args, int instanceId) {

    // Look for --restartTokenFile
    char * restartTokenFileName = jnu_getRestartTokenFile( args );
    if ( restartTokenFileName != NULL && instanceId != -1) {

        char fname[MAX_FILE_NAME_LEN];
        memset(fname,0,MAX_FILE_NAME_LEN);
        strcpy(fname,"//");
        strcat(fname,restartTokenFileName);

        FILE * stream = fopen(fname, "w");

        if (stream == NULL) {
            return jnu_error(__FUNCTION__,-1, "fopen(%s) returned NULL. errno: %d", fname, errno);
        }

        int num;
        if ( (num = fprintf(stream, "restartJob=%d", instanceId)) > 0) {
            fclose(stream);
        } else {
            fclose(stream);
            return jnu_error(__FUNCTION__,-1, "fprintf(%s) returned an error. errno: %d", fname, errno);
        }

    }

    return 0;

}

int jnu_clearRestartTokenFromFile(cJSON * args) {

    // Look for --restartTokenFile
    char * restartTokenFileName = jnu_getRestartTokenFile( args );

    if ( restartTokenFileName != NULL ) {

        char fname[MAX_FILE_NAME_LEN];
        memset(fname,0,MAX_FILE_NAME_LEN);
        strcpy(fname,"//");
        strcat(fname,restartTokenFileName);

        FILE * stream = fopen(fname, "w");

        if (stream == NULL) {
            return jnu_error(__FUNCTION__,-1, "fopen(%s) returned NULL. errno: %d", fname, errno);
        }

        fclose(stream);

     }

    return 0;

}


/**
 * Convert args --jobExecutionId to --executionId, --jobInstanceId to --instanceId,
 * to accommodate the server-side model code.
 * @return 0
 */
int jnu_normalizeArgs(cJSON * args) {

    char * instanceId = cJSON_GetObjectItemStringValue(args, "jobInstanceId", NULL);
    if (instanceId != NULL) {
        cJSON_AddStringToObject(args, "instanceId", instanceId);
    }

    char * jobInstanceId = cJSON_GetObjectItemStringValue(args, "instanceId", NULL);
    if (jobInstanceId != NULL) {
        cJSON_AddStringToObject(args, "jobInstanceId", jobInstanceId);
    }

    char * executionId = cJSON_GetObjectItemStringValue(args, "jobExecutionId", NULL);
    if (executionId != NULL) {
        cJSON_AddStringToObject(args, "executionId", executionId);
        cJSON_DeleteItemFromObject(args,"jobExecutionId");
    }

    jnu_trace(__FUNCTION__, "normalized args: %s", cJSON_Print_ns(args));

    return 0;
}

/**
 * Resolve args like --controlPropertiesFile and --jobParametersFile.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_resolveArgs(cJSON * args) {

    cJSON * cmdjobparms = cJSON_Duplicate(jnu_getOrCreateJobParameters(args),1);
    cJSON_DeleteItemFromObject(args,"jobParameters");
    cJSON * origargs = cJSON_Duplicate(args,1);//copy origargs
    int rc = jnu_resolveControlProps(args);
    if (rc != 0) {
        return jnu_error(__FUNCTION__,rc, "jnu_resolveControlProps returned %d", rc);
    }

    cJSON_MergeObjects( args, origargs, 1); //have command line args overwrite the controlpropsfile

    rc = jnu_resolveJobParams(args);
    if (rc != 0) {
        return jnu_error(__FUNCTION__,rc, "jnu_resolveJobParams returned %d", rc);
    }

    rc = jnu_resolveInlineJsl(args);
    if (rc != 0) {
        return jnu_error(__FUNCTION__,rc, "jnu_resolveInlineJsl returned %d", rc);
    }

    cJSON_MergeObjects(jnu_getOrCreateJobParameters(args),cmdjobparms,1);
    if (jnu_getVerbose(args) != NULL) {
        jnu_info("These are your final job parameters:");
        jnu_info(cJSON_Print_ns(args));
    }
    // Check for optional restart property file (mac)
    rc = jnu_resolveRestartToken(args);
    if (rc != 0 ) {
        return jnu_error(__FUNCTION__,rc, "jnu_resolveRestartToken returned %d", rc);
    }

    cJSON * jobParms = cJSON_GetObjectItem(args, "jobParameters");
    if (cJSON_GetArraySize(jobParms) == 0){//In case of help have to delete an empty job parameters
        cJSON_DeleteItemFromObject(args,"jobParameters");
    }

    return jnu_normalizeArgs(args);
}
 
