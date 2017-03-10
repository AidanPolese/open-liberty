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
#ifndef _BBOZ_ANGEL_FUNCTIONS_H
#define _BBOZ_ANGEL_FUNCTIONS_H

/** @file
 * Defines functions used by the Angel main
 */
#include "gen/iharmpl.h"
#include "bbgzarmv.h"
#include "angel_process_data.h"

/**
 * Initialize the Angel.  This is called from the Angel main() first.
 */
int BBGZDRM_Init(void);

/**
 * The main work loop for the Angel process.  This is called from the main()
 * and waits for commands from the operator.
 */
int BBGZDRM_Run(void);

/**
 * UnInitialize the Angel.  This is called before the Angel is stopped.
 */
int BBGZDRM_UnInit(void);

/**
 * ReInitialize the Angel.  This is called when a new dynamic replaceable
 * module is loaded, before the new Run method is called.
 */
int BBGZDRM_ReInit(bbgzarmv* exsting, bbgzarmv* future);

/**
 * Return the version string for the dynamic replaceable module.
 *
 * @return The version string for the dynamic replaceable module.  The caller
 *         is responsible for freeing the storage returned by this function
 *         using free().
 */
char* getDynamicReplaceableVersionString(void);

/** Return the version integer for the dynamic replaceable module. */
int getDynamicReplaceableVersionInt(void);

#endif
