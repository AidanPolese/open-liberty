/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

/**
 * @file
 *
 * Code to handle native NLS translations from message catalogs
 */

#include <errno.h>
#include <locale.h>
#include <nl_types.h>
#include <stdio.h>
#include <stdlib.h>
#include "include/gen/native_messages.h"
#include "include/server_nls_messages.h"

#define NATIVE_NLSCATALOG "native_messages"

static nl_catd catd = NULL;

/**
 * Obtain the translated messages from the message catalog. *
 */
char *
getTranslatedMessageById(int messageId, char * defaultMessage) {

    int rc = 0;
    char* translatedMessage = NULL;
    errno = 0;

    //TODO Use LANG= environment variable to derive the name argument of catopen() once
    //we actually have catalogs for languages other than ENGLISH. So basically
    //get the value of LANG and append that to the NATIVE_NLSCATALOG more or less.
    //Or investigate if we can exploit the %L in the NLSPATH, either way should work.
    //
    //The catopen relies on the NLSPATH being set as shown below.
    //NLSPATH = ${WLP_INSTALL_DIR}/lib/native/zos/s390x/nls/%N.cat:${NLSPATH}

    if (catd == NULL)
        catd = catopen(NATIVE_NLSCATALOG, 0);

    if (catd == (nl_catd) -1) //cannot open catalog
        rc = errno;
    else
    {
        translatedMessage = catgets(catd, 1, messageId, defaultMessage);
        if (errno != 0)  //cannot get message from catalog
            rc = errno;
    }

    if (rc != 0 )
    {
        //We should always be able to access the catalog and find the message.
        fprintf(stderr, "getTranslatedMessageById: Unable to access message catalog returning defaultMessage.  rc= %i \n", rc);
        return defaultMessage;
    }
    else
        return translatedMessage;

}

/**
 * Close the message catalog *
 */
void
closeMessageCatalog() {

    if (catd != NULL)
    {
        catclose(catd);
        catd = NULL;
    }

}


