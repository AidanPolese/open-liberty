#ifndef _BBOIEANT_H_
#define _BBOIEANT_H_
/*
 *   Header Name: BBOIEANT
 *
 *   Descriptive Name:
 *       Acronym:  N/A
 *
 *   Proprietary Statement
 *
 * IBM Confidential
 * OCO Source Materials
 * 5655-I35 (C) Copyright IBM Corp. 2006.
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * Status = H28W700
 *
 *   Function:
 *    This header provides some of the 64-bit interface prototypes
 *    for the ieantc.h routines that are not available from ieantc.h
 *
 *   Method of Access:
 *     C++: #include bboieant.h
 *
 *   Change Activity:
 *    266987.W10.1  H28W700  20051117  KHP: 64bit compile/link update - part 2
 *
 */

#include <ieantc.h>

 #ifdef _LP64
  void ieantrt64 (
           const int __LEVEL, /* Input  - Level specification  */
           char *__NAME,      /* Input  - User supplied name   */
           char *__TOKEN,     /* Output - User supplied token  */
           int *__RC);        /* Output - Return code          */

  void ieantcr64 (
           const int __LEVEL, /* Input  - Level specification  */
           char *__NAME,      /* Input  - User supplied name   */
           char *__TOKEN,     /* Input  - User supplied token  */
           const int __POPT,  /* Input  - Persistence option   */
           int *__RC);        /* Output - Return code          */

  void ieantdl64 (
           const int __LEVEL, /* Input  - Level specification  */
           char *__NAME,      /* Input  - User supplied name   */
           int *__RC);        /* Output - Return code          */

 #endif
#endif
