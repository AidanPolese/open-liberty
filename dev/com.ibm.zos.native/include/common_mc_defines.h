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
#ifndef _BBGZ_COMMON_MC_DEFINES_H
#define _BBGZ_COMMON_MC_DEFINES_H

#ifndef AMODE_31
/**
 * Macro to wrap some assembler in the logic required to switch from
 * @c AMODE64 to @c AMODE31 and back.
 *
 * @param asm_data the assembler code that should run in @c AMODE31
 */
#define AMODE_31(asm_data) \
    " SAM31\n" \
    " SYSSTATE PUSH\n" \
    " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n" \
    asm_data "\n" \
    " SYSSTATE POP\n" \
    " SAM64\n"
#endif


#endif
