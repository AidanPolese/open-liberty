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

/**
 * @file
 *
 * process goo unauthorized key 8 control block
 *
 */
#ifndef _BBOZ_BBGZPGOU_H
#define _BBOZ_BBGZPGOU_H

#include "stack_services.h"

#define BBGZPGOU_EYE "BBGZPGOU"

#pragma pack(1)

/**
 * The PGOU is a key 8 extension to the PGOO (which is in key 2).  It is a
 * place where the Liberty server can put things that it needs to get to
 * from unauthorized code.
 */
typedef struct bbgzpgou_flags_s bbgzpgou_flags_s;
struct bbgzpgou_flags_s
{
  int bbgzpgou_skipped_8_bytes : 1;
  int : 31;
};

typedef struct bbgzpgou bbgzpgou;
struct bbgzpgou {
  unsigned char            bbgzpgou_eyecatcher[8];             /* 0x00 */
  short                    bbgzpgou_version;                   /* 0x08 */
  short                    bbgzpgou_length;                    /* 0x0A */
  bbgzpgou_flags_s         bbgzpgou_flags;                     /* 0x0C */
  concurrent_stack         bbgzpgou_trace_stack;               /* 0x10 */
  concurrent_stack         bbgzpgou_trace_threads_stack;       /* 0x20 */
  unsigned char            _reserved2[80];                     /* 0x30 */
};                                                             /* 0x80 */

#pragma pack(reset)

#endif
