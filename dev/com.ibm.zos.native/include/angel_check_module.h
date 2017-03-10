/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_ANGEL_CHECK_MODULE_H
#define _BBOZ_ANGEL_CHECK_MODULE_H

#include "angel_check_main.h"

/** @file
 * Maps the bbgzachk (Angel Check Module)
 */

/*-------------------------------------------------------------------*/
/* The angel check module vector table is referenced from 31 bit     */
/* code which sets up the PC routines (since those services must     */
/* be called in 31 bit mode).  31 bit C code does not recognize the  */
/* __ptr64 directive so we need to be creative.                      */
/*-------------------------------------------------------------------*/
#ifdef _LP64
#define PAD31
#else
#define PAD31 int :32;
#endif

typedef struct bbgzachk bbgzachk;
/**
 * The Angel Check Module Vector Table
 */
struct bbgzachk {

    /**
     * Eyecatcher.
     */
    char eyecatcher[8];                              // 0x00
                                                     //
    /**                                              //
     * The angel_check function's version number     //
     */                                              //
    int angel_check_version;                         // 0x08
                                                     //
    /**
     * Filler
     */
    int _avail;                                      // 0x0C

    /**                                              //
     * The function that checks whether an angel     //
     * is running                                    //
     *                                               //
     * @param angel_name The name of the angel       //
     * to check, or NULL for the default angel       //
     */                                              //
    PAD31 int (* angel_check)(char*);                // 0x10
                                                     //
    /**                                              //
     * Save some space, just in case.                //
     */                                              //
    char reserved[104];                              // 0x18
};                                                   // 0x80

#endif
