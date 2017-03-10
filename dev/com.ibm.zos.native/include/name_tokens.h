/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
#ifndef _BBOZ_NAME_TOKENS_H
#define _BBOZ_NAME_TOKENS_H

/**
 * To avoid collisions, we need to be careful when adding new name tokens.
 * Liberty name tokens should start with BBGZ.
 *
 * TWAS name tokens currently start with BBOA BBOO and BBOT.
 *
 */
#define BBGZ_BBOASHR_NAME_TOKEN_USER_NAME_PREIFX "BBGZSHR_"
/* Following is used by bbgzshrc utility to save a previous bboashr pointer */
#define BBGZ_BBOASHR_NAME_TOKEN_SAVE_USER_NAME_PREIFX "BBGZSHR1"

/**
 * WOLA registration name tokens are created by prefixing the
 * register name with BBOZ. To keep these name tokens from colliding
 * with other name tokens, no other name tokens should be created
 * starting with BBOZ.
 */
#define BBOZ_REGISTRATION_NAME_TOKEN_USER_NAME_PREIFX "BBOZ"

#endif
