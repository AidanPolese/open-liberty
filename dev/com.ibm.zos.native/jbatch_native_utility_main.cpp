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

#pragma runopts(POSIX(ON))
#pragma runopts(ENVAR("_CEE_ENVFILE=DD:STDENV"))

#include "include/jbatch_utils.h"
#include "include/jbatch_native_utility.h"

/**
 * Main entry point for the jbatch native utility (batchManagerZos).
 */
int main(int argc, char** argv)
{
    jnu_printArgs(argc, argv);

    int rc = jnu_main(argc, argv);

    jnu_trace(__FUNCTION__, "batchManagerZos RC:%d", rc);

    return rc;
}
 
