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
#ifndef _BBOZ_MVS_ABEND_H
#define _BBOZ_MVS_ABEND_H

#include "ras_abend_codes.h"

/**
 * Issues the ABEND macro with DUMP,STEP,SYSTEM parameters.
 *
 * @param comp The completion code.
 * @param reason The reason code.
 */
void abend(short comp, int reason);

/**
 * Issues the ABEND macro with DUMP,STEP,SYSTEM parameters.  Additional
 * data can be provided and is stored in registers before the abend
 * is issued.
 *
 * @param comp The completion code.
 * @param reason The reason code.
 * @param data1 Data to be stored in register 2 before the abend.
 * @param data2 Data to be stored in register 3 before the abend.
 */
void abend_with_data(short comp, int reason, void* data1, void* data2);

#endif
