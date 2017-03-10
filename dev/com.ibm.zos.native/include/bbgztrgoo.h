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
 * Process level control block used by non-LE trace code.
 *
 * @key 8
 */
#ifndef BBGZTRGOO_H_
#define BBGZTRGOO_H_

#include "stack_services.h"

#define BBGZTRGOO_EYE "BBGZTRGO"
#define BBGZTRGOO_VERSION_1 1

/**
 * The TRGOO is key 8 storage containing information needed for
 * tracing from native code.
 */
typedef struct bbgztrgoo {
    unsigned char            bbgztrgoo_eyecatcher[8];             /* 0x00 */
    short                    bbgztrgoo_version;                   /* 0x08 */
    short                    bbgztrgoo_length;                    /* 0x0A */
    int                      bbgztrgoo_reserved;                  /* 0x0C */
    concurrent_stack         bbgztrgoo_trace_stack;               /* 0x10 */
    void *                   bbgztrgoo_trace_thread_data_p;       /* 0x20 */
    unsigned int             bbgztrgoo_threadThreadReleaseLock;   /* 0x28 */
    unsigned char            _reserved2[84];                      /* 0x2C */
} bbgztrgoo;                                                      /* 0x80 */

#endif /* BBGZTRGOO_H_ */

