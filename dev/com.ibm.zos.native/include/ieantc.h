#ifndef __IEANT

#define __IEANT

/*********************************************************************
 *                                                                   *
 *  Name: IEANTC                                                     *
 *                                                                   *
 *  Descriptive Name: Name/Token Service C Declares                  *
 *                                                                   *
 ***PROPRIETARY_STATEMENT*********************************************
 *                                                                   *
 * LICENSED MATERIALS - PROPERTY OF IBM                              *
 * THIS MACRO IS "RESTRICTED MATERIALS OF IBM"                       *
 * 5694-A01 (C) COPYRIGHT IBM CORP. 1991,2002                        *
 *                                                                   *
 * Status: HBB7707                                                   *
 *                                                                   *
 ***END_OF_PROPRIETARY_STATEMENT**************************************
 *                                                                   *
 *01* EXTERNAL CLASSIFICATION: PI                                    *
 *01* END OF EXTERNAL CLASSIFICATION:                                *
 *                                                                   *
 *  Function:                                                        *
 *    IEANTC defines types, related constants, and function          *
 *    prototypes for the use of Name/Token Services from the C       *
 *    language                                                       *
 *                                                                   *
 *  Usage:                                                           *
 *    #include <IEANTC.H>                                            *
 *                                                                   *
 *  Notes:                                                           *
 *    1. This member should be copied from SAMPLIB to the            *
 *    appropriate local C library.                                   *
 *    2. The Name/Token services do not use a null character to      *
 *    terminate strings. All of the services expect name and token   *
 *    data to be a fixed-length 16-byte type.                        *
 *                                                                   *
 *  Change Activity:                                                 *
 *    $L0=TOKEN,   JBB4422, 910308, PD16JV: Name/Token Support       *
 *    $P1=PKI0261, JBB4422, 910517, PD16JV: Authorized levels        *
 *    $P2=PKI0302, JBB4422, 910621, PD16JV: Copyright info           *
 *    $L1=R69885   HBB7703, 991010, PD00XB: Checkpoint support       *
 *    $P3=PJK0097, HBB7709, 020614, U2IAXZ: Support C++ and map      *
 *                                          names to upper case.     *
 *                                                                   *
 *********************************************************************/
/*********************************************************************
 *         Type Definitions for User Specified Parameters            *
 *********************************************************************/

/*  Type for user supplied name and token                            */
typedef char IEANT_type??(17??);

/*********************************************************************
 *       Fixed Service Parameter and Return Code Defines             *
 *********************************************************************/

/*  Name/Token Level Constants                                       */
#define IEANT_TASK_LEVEL          1
#define IEANT_HOME_LEVEL          2
#define IEANT_PRIMARY_LEVEL       3
#define IEANT_SYSTEM_LEVEL        4
#define IEANT_TASKAUTH_LEVEL      11                           /*@P1A*/
#define IEANT_HOMEAUTH_LEVEL      12                           /*@P1A*/
#define IEANT_PRIMARYAUTH_LEVEL   13                           /*@P1A*/

/*  Name/Token Persistence                                           */
#define IEANT_NOPERSIST           0
#define IEANT_PERSIST             1

/*  Name/Token Checkpoint                                            */
#define IEANT_NOCHECKPOINT        0                           /* @L1A*/
#define IEANT_CHECKPOINTOK        2                           /* @L1A*/

/*  Service Return Codes                                             */
#define IEANT_OK                  0
#define IEANT_DUP_NAME            4
#define IEANT_NOT_FOUND           4
#define IEANT_24BIT_MODE          8
#define IEANT_NOT_AUTH           16
#define IEANT_SRB_MODE           20
#define IEANT_LOCK_HELD          24
#define IEANT_LEVEL_INVALID      28
#define IEANT_NAME_INVALID       32
#define IEANT_PERSIST_INVALID    36
#define IEANT_AR_INVALID         40
#define IEANT_UNEXPECTED_ERR     64

/*********************************************************************
 *           Function Prototypes for Service Routines                *
 *********************************************************************/

 #ifdef __cplusplus                                           /* @P3A */
                                                              /* @P3A */
    extern "OS" ??<                                           /* @P3A */
                                                              /* @P3A */
 #else                                                        /* @P3A */

#pragma linkage(ieantcr,OS31_NOSTACK)
#pragma linkage(ieantdl,OS31_NOSTACK)
#pragma linkage(ieantrt,OS31_NOSTACK)

#pragma linkage(iean4cr,OS64_NOSTACK)
#pragma linkage(iean4dl,OS64_NOSTACK)
#pragma linkage(iean4rt,OS64_NOSTACK)

#endif                                                       /* @P3A */

extern void ieantcr(
     const int __LEVEL, /* Input  - Level specification              */
     char *__NAME,      /* Input  - User supplied name               */
     char *__TOKEN,     /* Input  - User supplied token              */
     const int __POPT,  /* Input  - Persistence option               */
     int *__RC);        /* Output - Return code                      */

extern void ieantrt(
     const int __LEVEL, /* Input  - Level specification              */
     char *__NAME,      /* Input  - User supplied name               */
     char *__TOKEN,     /* Output - User supplied token              */
     int *__RC);        /* Output - Return code                      */

extern void ieantdl(
     const int __LEVEL, /* Input  - Level specification              */
     char *__NAME,      /* Input  - User supplied name               */
     int *__RC);        /* Output - Return code                      */

extern void iean4cr(
     const int __LEVEL, /* Input  - Level specification              */
     char *__NAME,      /* Input  - User supplied name               */
     char *__TOKEN,     /* Input  - User supplied token              */
     const int __POPT,  /* Input  - Persistence option               */
     int *__RC);        /* Output - Return code                      */

extern void iean4rt(
     const int __LEVEL, /* Input  - Level specification              */
     char *__NAME,      /* Input  - User supplied name               */
     char *__TOKEN,     /* Output - User supplied token              */
     int *__RC);        /* Output - Return code                      */

extern void iean4dl(
     const int __LEVEL, /* Input  - Level specification              */
     char *__NAME,      /* Input  - User supplied name               */
     int *__RC);        /* Output - Return code                      */

/*  End of Name/Token Services Header                                */

#ifdef __cplusplus                                           /* @P3A */
                                                             /* @P3A */
  ??>                                                        /* @P3A */
                                                             /* @P3A */
#endif                                                       /* @P3A */

/*********************************************************************
 *                   Pragma Maps for Binder Support                  *
 *********************************************************************/
#pragma map(ieantcr, "IEANTCR")                              /* @P3A */
#pragma map(ieantrt, "IEANTRT")                              /* @P3A */
#pragma map(ieantdl, "IEANTDL")                              /* @P3A */
#pragma map(iean4cr, "IEAN4CR")                              /* @P3A */
#pragma map(iean4rt, "IEAN4RT")                              /* @P3A */
#pragma map(iean4dl, "IEAN4DL")                              /* @P3A */

#endif
