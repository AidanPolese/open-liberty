/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/ieantc.h"
#include "include/server_trgoo_services.h"

/**
 * This routine gets the trace goo associated with this process.
 * @return bbgztrgoo*  pointer to process trace goo
 *
 */
bbgztrgoo * getTRGOO(void) {
    char trgoo_name[16];
    char trgoo_token[16];
    int trgoo_name_token_rc;
    bbgztrgoo * bbgztrgoo_p = 0;

    memset(trgoo_name, 0, sizeof(trgoo_name));
    memcpy(trgoo_name, BBGZTRGOO_TOKEN_NAME, sizeof(trgoo_name));

    iean4rt(IEANT_HOME_LEVEL,
            trgoo_name,
            trgoo_token,
            &trgoo_name_token_rc);

    if (trgoo_name_token_rc == 0) {
        memcpy(&bbgztrgoo_p, trgoo_token, sizeof(bbgztrgoo_p));
    }
    return bbgztrgoo_p;
}
