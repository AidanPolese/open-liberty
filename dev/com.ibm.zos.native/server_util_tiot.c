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

/**
 * @file
 *
 * Functions related to reading the TIOT.
 */
#include <stdlib.h>
#include <string.h>

#include "include/gen/ihapsa.h"
#include "include/gen/ikjtcb.h"     
#include "include/server_util_tiot.h"
// TODO #include "include/ras_tracing.h"


/**
 * @return the TIOT ptr, retrieved from the TCBTIO field off the TCB.
 */
tiot * tiot_getTiot() {
    psa* psa_p = (psa *)0L;
    tcb* tcb_p = (tcb*) psa_p->psatold;
    return (tiot *) tcb_p->tcbtio;
}

/**
 * @param tiot_p the TIOT
 * @param prevTioentry_p the previous tioentry, or NULL to get the first tioentry.
 *
 * @return the next tioentry after the given prevTioentry_p, or NULL if no more entries.
 */
tiot_dd * tiot_getNextTioentry(tiot * tiot_p, tiot_dd * prevTioentry_p) {

    if (tiot_p == NULL) {
        return NULL;
    }

    tiot_dd * retMe = (prevTioentry_p == NULL) 
                            ?  &tiot_p->tioentry[0]
                            :  (tiot_dd *) ( ((char *)prevTioentry_p) + (int)prevTioentry_p->tioelngh ) ;

   return ( retMe->tioelngh == 0 ) ? NULL : retMe;
}

/**
 * Pad a string by appending the specified character to the end until the
 * string reaches the required length.
 *
 * Note: the resulting string is NOT null-terminated by this method.
 *
 * @param string the string to pad
 * @param len the length to pad out to
 * @param pad the character to use for padding
 *
 * @return the number of characters that were inserted to pad the string
 */
int tiot_strpad(char* string, size_t len, unsigned char pad) {
    int padCount = 0;
    for (int i = strlen(string); i < len; i++, padCount++) {
        string[i] = pad;
    }
    return padCount;
}


/**
 * @param tiot_p the TIOT
 * @param ddname - The ddname, must be either null-termed or at most 8 bytes long
 *
 * @return 1 if the given DD is defined; 0 otherwise.
 */
int tiot_isDDDefined( tiot * tiot_p, const char * ddname ) {

    if (ddname == NULL) {
        return 0;
    }

    // Blank-pad the input.
    char ddname_padded[8];
    strncpy( ddname_padded, ddname, 8 );
    tiot_strpad( ddname_padded, 8, ' ');

    tiot_dd * tiot_dd_p = NULL;
        
    for (tiot_dd_p = tiot_getNextTioentry(tiot_p, tiot_dd_p); 
         tiot_dd_p != NULL; 
         tiot_dd_p = tiot_getNextTioentry(tiot_p, tiot_dd_p) ) {

        if ( strncasecmp( tiot_dd_p->tioeddnm, ddname_padded, 8 ) == 0 ) {
            return 1;
        }
    }

    // If we got here, must not have found it.
    return 0;
}

