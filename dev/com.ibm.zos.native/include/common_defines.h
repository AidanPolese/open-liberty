/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBGZ_COMMON_DEFINES_H
#define _BBGZ_COMMON_DEFINES_H

#ifndef TRUE
#define TRUE 1
#endif

#ifndef FALSE
#define FALSE 0
#endif

#ifndef BBGZ_min
#define BBGZ_min(a,b) (((a) < (b)) ? (a) : (b))
#endif

#ifndef BBGZ_max
#define BBGZ_max(a,b) (((a) > (b)) ? (a) : (b))
#endif

#ifndef TCBJSTCB_ADDR
#define TCBJSTCB_ADDR *((void * __ptr32*) &(((((tcb *)(((psa *)0)->psatold))->tcbjstcb))))
#endif

#ifndef SAVE_AREA_SIZE
/**
 * The length of an MVS register save area.
 */
#define SAVE_AREA_SIZE 72
#endif

#ifndef BBGZ_resetHighOrderBit
#define BBGZ_resetHighOrderBit(a) ((a) & 0x7FFFFFFF)
#endif

/**
 * A 8-byte long buffer for holding STOKEN.
 */
typedef struct {
    char tkn[8];
} SToken;

/**
 * A 16-byte long buffer for holding a TTOKEN.
 */
typedef struct {
    char tkn[16];
} TToken;

#endif
