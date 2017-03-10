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
 * thread goo unauthorized key 8 control block
 */

#ifndef _BBOZ_BBGZTGOU_H
#define _BBOZ_BBGZTGOU_H

#include <ieac.h>

static const iea_PEToken      zero_pause_element_token = {{0}};
#define BBGZTGOU_EYE "BBGZTGOU"

#pragma pack(1)

/**
 * The TGOU control block is a key 8 extension to the TGOO control block.
 */
typedef struct bbgztgou bbgztgou;
struct bbgztgou
{
  unsigned char            bbgztgou_eyecatcher[8]; /*     0x000*/
  short                    bbgztgou_version;       /*     0x008*/
  short                    bbgztgou_length;        /*     0x00A*/
  int                      _reserved0;             /*     0x00C*/
  iea_PEToken              bbgztgou_trace_pet;     /* pause element token of thread creating a trace
                                                          0x010*/
  unsigned char            _reserved1[96];         /*     0x020*/
};                                                 /*     0x080*/

#pragma pack(reset)

#endif
