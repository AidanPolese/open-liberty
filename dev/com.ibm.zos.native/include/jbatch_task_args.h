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
#ifndef __jbatch_task_args_h__
#define __jbatch_task_args_h__

#include <stdio.h>
#include "jbatch_json.h"


/**
 * Parse the arg line (--arg, --arg=value) and put it in the args object.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_parseArg( char * arg, cJSON * args ) ;

/**
 * Parse the <prop>=<value> from the givne line and put it in props.
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_parseProperty(char * lineIn, cJSON * props) ;

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
int jnu_parseCommandLineArgs(int argc, char ** argv, cJSON * retMe) ;

/**
 * @param f a file stream
 * @param props populated with the parsed properties
 * @param parseFunction the function to use to parse the property from the file line
 *
 * @return 0 if all's well. non-zero otherwise.
 */
int jnu_parsePropsStream( FILE * f, cJSON * props, int (*parseFunction)(char *,cJSON *) ) ;

/**
 * Resolve args like --controlPropertiesFile and --jobParametersFile.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_resolveArgs(cJSON * args) ;

/**
 * Write the restart token (instanceId) into the file specified by the
 * --restartTokenFile parameter.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_writeRestartToken(cJSON * args, int instanceId);

/**
 * Clear the restart token (instanceId) in the file specified by the
 * --restartTokenFile parameter.
 *
 * @return 0 if all's well; non-zero otherwise.
 */
int jnu_clearRestartTokenFromFile(cJSON * args);

/**
 * Remove the instance Id from the args and clear the
 * restart token file.
 *
 */
void jnu_removeRestartToken(cJSON * args);

#endif
