                   ??=ifndef BBOAAPI_INCLUDED
                   ??=ifdef __COMPILER_VER__
                    ??=pragma filetag("IBM-1047")
                   ??=endif
                   #define BBOAAPI_INCLUDED
                   #pragma nomargins nosequence
                   #pragma checkout(suspend)
 /********************************************************************
 *  <BBOAAPI.h> header file                                          *
 *                                                                   *
 * LICENSED MATERIALS - PROPERTY OF IBM                              *
 *                                                                   *
 * NOTE NOTE NOTE - THIS IS THE INTERNAL VERSION OF BBOAAPI          *
 * The client version is in include/client/bboaapi.h.                *
 * The client version does not contain BBOA1GTX and BBOA1INF.        *
 *                                                                   *
 *   Header name: bboaapi.h                                          *
 *   Component:   WAS z/OS                                           *
 *                                                                   *
 *   Descriptive name:  WAS Optimized Local Adapters API header file *
 *                                                                   *
 *   Proprietary statement:                                          *
 *   IBM Confidential                                                *
 *   OCO Source Materials                                            *
 *   5655-N01 Copyright IBM Corp. 2008                               *
 *   The source code for this program is not published or otherwise  *
 *   divested of its trade secrets, irrespective of what has been    *
 *   deposited with the U.S. Copyright Office.                       *
 *   Status = H28W700                                                *
 *                                                                   *
 *   Change activity:                                                *
 *   $L0=LI4798,  H28W700,20080623, JTM : High speed connectors APIs *
 *   $537906,     H28W700, 20090203, JTM : Support 12 byte connhdls. *
 *   $LI4798I7-01,H28W700,20090228, FGOL: Add flag for outbound CICS *
 *                                              security propagation *
 *   $579234,     H28W700, 20090310, JTM : Add waittime to BBOA1RCA. *
 *   $F003691-01  H28W700, 20090911, FGOL: Add reg trace level flags *
 *   $F003703     H28W800, 20101130, FGOL/JTM:  Enable 61-bit API.   *
 *                                                                   *
 ********************************************************************/

  #ifdef __cplusplus
  extern "C" {
  #endif

 /* #ifndef _LP64 */

   typedef signed short int _INT2;

   typedef signed       int _INT4;
   typedef signed long  int _LONGINT;

   typedef signed       int _RC;
   typedef signed       int _RSN;
   typedef signed       int _RV;

   typedef float            _FLOAT4;
   typedef double           _FLOAT8;
   typedef long double      _FLOAT16;
   typedef void *           _POINTER;
   typedef char             _CHAR4   [  4];
   typedef char             _CHAR8   [  8];
   typedef char             _CHAR12  [ 12];
   typedef char             _CHAR16  [ 16];
   typedef char             _CHAR256 [256];

   struct _REGFLAGS {
      int       reg_flag_trcmod:  1,  /* Modify the OLA trace
                                                   level @F003691-01A*/
                reg_flag_trcmore: 1,  /* OLA trace lvl 2 @F003691-01A*/
                reg_flag_trcsome: 1,  /* OLA trace lvl 1 @F003691-01A*/
                reg_flag_1_rsv:   5,  /* Reserved.       @F003691-01C*/
                reg_flag_2:       8,  /* Reserved.                   */
                reg_flag_3:       8,  /* Reserved.                   */
                reg_flag_4_rsvd1: 1,  /* Reserved.                   */
                reg_flag_4_rsvd2: 1,  /* Reserved.                   */
                reg_flag_4_rsvd3: 1,  /* Reserved.                   */
                reg_flag_4_rsvd4: 1,  /* Reserved.                   */
                reg_flag_4_rsvd5: 1,  /* Reserved.                   */
                reg_flag_C2Wprop: 1,  /* Propagate task
                                               security @LI4798I7-01C*/
                reg_flag_trans:   1,  /* Transactional ON/OFF        */
                reg_flag_W2Cprop: 1;  /* Assert WAS security in CICS
                                                        @LI4798I7-01C*/
   };
   typedef struct _REGFLAGS  _REGFLAGS;

   struct _UNREGFLAGS {
      int       unreg_flag_1:       8,  /* Reserved.                   */
                unreg_flag_2:       8,  /* Reserved.                   */
                unreg_flag_3:       8,  /* Reserved.                   */
                unreg_flag_4_rsvd1: 1,  /* Reserved.                   */
                unreg_flag_4_rsvd2: 1,  /* Reserved.                   */
                unreg_flag_4_rsvd3: 1,  /* Reserved.                   */
                unreg_flag_4_rsvd4: 1,  /* Reserved.                   */
                unreg_flag_4_rsvd5: 1,  /* Reserved.                   */
                unreg_flag_4_rsvd6: 1,  /* Reserved.                   */
                unreg_flag_4_rsvd7: 1,  /* Reserved.                   */
                unreg_flag_force:   1;  /* Force request               */
   };
   typedef struct _UNREGFLAGS  _UNREGFLAGS;

   #ifndef NULL
     #ifdef __cplusplus
       #define NULL 0
     #else
       #define NULL  ((void *)(0))
     #endif
   #endif

   /* REGISTER API                                                  */
   /* REGISTER API                                                  */
   /* REGISTER API                                                  */
   #pragma map(BBOA1REG,"BBOA1REG")

   void BBOA1REG (_CHAR8 *,      /* Daemon group name         */
                  _CHAR8 *,      /* Node name                 */
                  _CHAR8 *,      /* Server name               */
                  _CHAR12 *,     /* Register name             */
                  _INT4 *,       /* Minimum # connections     */
                  _INT4 *,       /* Maximum # connections     */
                  _REGFLAGS *,   /* Register flags (tx, sec)  */
                  _RC *, _RSN *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1REG, OS31_NOSTACK)
   #endif

   /* 64-bit REGISTER API                                      @F003703A*/
   /* 64-bit REGISTER API                                      @F003703A*/
   /* 64-bit REGISTER API                                      @F003703A*/
   #pragma map(BBGA1REG,"BBGA1REG")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1REG (_CHAR8 *,      /* Daemon group name          @F003703A*/
                  _CHAR8 *,      /* Node name                  @F003703A*/
                  _CHAR8 *,      /* Server name                @F003703A*/
                  _CHAR12 *,     /* Register name              @F003703A*/
                  _INT4 *,       /* Minimum # connections      @F003703A*/
                  _INT4 *,       /* Maximum # connections      @F003703A*/
                  _REGFLAGS *,   /* Register flags (tx, sec)   @F003703A*/
                  _RC *, _RSN *);                        /*    @F003703A*/
   }
   #endif

   /* UNREGISTER API                                                */
   /* UNREGISTER API                                                */
   /* UNREGISTER API                                                */
   #pragma map(BBOA1URG,"BBOA1URG")

   void BBOA1URG (_CHAR12 *,     /* Register name             */
                  _UNREGFLAGS *,
                  _RC *, _RSN *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1URG, OS31_NOSTACK)
   #endif

   /* 64-bit UNREGISTER API                                @F003703A*/
   /* 64-bit UNREGISTER API                                @F003703A*/
   /* 64-bit UNREGISTER API                                @F003703A*/
   #pragma map(BBGA1URG,"BBGA1URG")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1URG (_CHAR12 *,     /* Register name          @F003703A*/
                  _UNREGFLAGS *,
                  _RC *, _RSN *);

   }
   #endif

   /* INVOKE API                                                    */
   /* INVOKE API                                                    */
   /* INVOKE API                                                    */
   #pragma map(BBOA1INV,"BBOA1INV")

   void BBOA1INV (_CHAR12 *,     /* Register name             */
                  _INT4 *,       /* Request type              */
                  _CHAR256 *,    /* Service name              */
                  _INT4 *,       /* Service name length       */
                  _POINTER *,    /* Request data address      */
                  _INT4 *,       /* Request data length       */
                  _POINTER *,    /* Response data address     */
                  _INT4 *,       /* Response data length      */
                  _INT4 *,       /* Wait time                 */
                  _RC *, _RSN *, _RV *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1INV, OS31_NOSTACK)
   #endif

   /* 64-bit INVOKE API                                    @F003703A*/
   /* 64-bit INVOKE API                                    @F003703A*/
   /* 64-bit INVOKE API                                    @F003703A*/
   #pragma map(BBGA1INV,"BBGA1INV")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1INV (_CHAR12 *,     /* Register name          @F003703A*/
                  _INT4 *,       /* Request type           @F003703A*/
                  _CHAR256 *,    /* Service name           @F003703A*/
                  _INT4 *,       /* Service name length    @F003703A*/
                  _POINTER *,    /* Request data address   @F003703A*/
                  _LONGINT *,    /* Request data length    @F003703A*/
                  _POINTER *,    /* Response data address  @F003703A*/
                  _LONGINT *,    /* Response data length   @F003703A*/
                  _INT4 *,       /* Wait time              @F003703A*/
                  _RC *, _RSN *, _RV *);                /* @F003703A*/

   }
   #endif

   /* CONNECTION GET API                                            */
   /* CONNECTION GET API                                            */
   /* CONNECTION GET API                                            */
   #pragma map(BBOA1CNG,"BBOA1CNG")
   void BBOA1CNG (_CHAR12 *,     /* Register name             */
                  _CHAR12 *,     /* Connection handle         */
                  _INT4 *,       /* Wait time                 */
                  _RC *, _RSN *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1CNG, OS31_NOSTACK)
   #endif

   /* 64-bit CONNECTION GET API                            @F003703A*/
   /* 64-bit CONNECTION GET API                            @F003703A*/
   /* 64-bit CONNECTION GET API                            @F003703A*/
   #pragma map(BBGA1CNG,"BBGA1CNG")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1CNG (_CHAR12 *,     /* Register name          @F003703A*/
                  _CHAR12 *,     /* Connection handle      @F003703A*/
                  _INT4 *,       /* Wait time              @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   }
   #endif

   /* SEND REQUEST API                                              */
   /* SEND REQUEST API                                              */
   /* SEND REQUEST API                                              */
   #pragma map(BBOA1SRQ,"BBOA1SRQ")
   void BBOA1SRQ (_CHAR12 *,  /* Connection handle         */
                  _INT4 *,    /* Request type              */
                  _CHAR256 *, /* Service name              */
                  _INT4 *,    /* Service name length       */
                  _POINTER *, /* Request data address      */
                  _INT4 *,    /* Request data length       */
                  _INT4 *,    /* Async flag                */
                  _INT4 *,    /* Response data length      */
                  _RC *, _RSN *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1SRQ, OS31_NOSTACK)
   #endif

   /* 64-bit SEND REQUEST API                              @F003703A*/
   /* 64-bit SEND REQUEST API                              @F003703A*/
   /* 64-bit SEND REQUEST API                              @F003703A*/
   #pragma map(BBGA1SRQ,"BBGA1SRQ")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1SRQ (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _INT4 *,    /* Request type              @F003703A*/
                  _CHAR256 *, /* Service name              @F003703A*/
                  _INT4 *,    /* Service name length       @F003703A*/
                  _POINTER *, /* Request data address      @F003703A*/
                  _LONGINT *, /* Request data length       @F003703A*/
                  _INT4 *,    /* Async flag                @F003703A*/
                  _LONGINT *, /* Response data length      @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   }
   #endif

   /* RECEIVE RESPONSE LENGTH API                                   */
   /* RECEIVE RESPONSE LENGTH API                                   */
   /* RECEIVE RESPONSE LENGTH API                                   */
   #pragma map(BBOA1RCL,"BBOA1RCL")
   void BBOA1RCL (_CHAR12 *,  /* Connection handle         */
                  _INT4 *,    /* Async flag                */
                  _INT4 *,    /* Response data length      */
                  _RC *, _RSN *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1RCL, OS31_NOSTACK)
   #endif

   /* 64-bit RECEIVE RESPONSE LENGTH API                   @F003703A*/
   /* 64-bit RECEIVE RESPONSE LENGTH API                   @F003703A*/
   /* 64-bit RECEIVE RESPONSE LENGTH API                   @F003703A*/
   #pragma map(BBGA1RCL,"BBGA1RCL")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1RCL (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _INT4 *,    /* Async flag                @F003703A*/
                  _LONGINT *, /* Response data length      @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   }
   #endif

   /* GET DATA API                                                  */
   /* GET DATA API                                                  */
   /* GET DATA API                                                  */
   #pragma map(BBOA1GET,"BBOA1GET")
   void BBOA1GET (_CHAR12 *,  /* Connection handle         */
                  _POINTER *, /* Response data address     */
                  _INT4 *,    /* Response data length      */
                  _RC *, _RSN *, _RV *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1GET, OS31_NOSTACK)
   #endif

   /* 64-bit GET DATA API                                  @F003703A*/
   /* 64-bit GET DATA API                                  @F003703A*/
   /* 64-bit GET DATA API                                  @F003703A*/
   #pragma map(BBGA1GET,"BBGA1GET")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1GET (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _POINTER *, /* Response data address     @F003703A*/
                  _LONGINT *, /* Response data length      @F003703A*/
                  _RC *, _RSN *, _RV *);                /* @F003703A*/

   }
   #endif

   /* CONNECTION RELEASE API                                        */
   /* CONNECTION RELEASE API                                        */
   /* CONNECTION RELEASE API                                        */
   #pragma map(BBOA1CNR,"BBOA1CNR")
   void BBOA1CNR (_CHAR12 *,  /* Connection handle         */
                  _RC *, _RSN *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1CNR, OS31_NOSTACK)
   #endif

   /* 64-bit CONNECTION RELEASE API                        @F003703A*/
   /* 64-bit CONNECTION RELEASE API                        @F003703A*/
   /* 64-bit CONNECTION RELEASE API                        @F003703A*/
   #pragma map(BBGA1CNR,"BBGA1CNR")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1CNR (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   }
   #endif

   /* HOST SERVICE API                                              */
   /* HOST SERVICE API                                              */
   /* HOST SERVICE API                                              */
   #pragma map(BBOA1SRV,"BBOA1SRV")
   void BBOA1SRV (_CHAR12 *,     /* Register name                   */
                  _CHAR256*,     /* Service name                    */
                  _INT4 *,       /* Service name length             */
                  _POINTER *,    /* Request data address            */
                  _INT4 *,       /* Request data length             */
                  _CHAR12 *,     /* Connection handle               */
                  _INT4 *,       /* Wait time                       */
                  _INT4 *,       /* Return code                     */
                  _INT4 *,       /* Reason code                     */
                  _INT4 *);      /* Return value                    */
   #ifndef __cplusplus
   #pragma linkage(BBOA1SRV, OS31_NOSTACK)
   #endif

   /* 64-bit HOST SERVICE API                              @F003703A*/
   /* 64-bit HOST SERVICE API                              @F003703A*/
   /* 64-bit HOST SERVICE API                              @F003703A*/
   #pragma map(BBGA1SRV,"BBGA1SRV")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1SRV (_CHAR12 *,     /* Register name          @F003703A*/
                  _CHAR256*,     /* Service name           @F003703A*/
                  _INT4 *,       /* Service name length    @F003703A*/
                  _POINTER *,    /* Request data address   @F003703A*/
                  _LONGINT *,    /* Request data length    @F003703A*/
                  _CHAR12 *,     /* Connection handle      @F003703A*/
                  _INT4 *,       /* Wait time              @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *,       /* Reason code            @F003703A*/
                  _INT4 *);      /* Return value           @F003703A*/
   }
   #endif

   /* SEND RESPONSE API                                             */
   /* SEND RESPONSE API                                             */
   /* SEND RESPONSE API                                             */
   #pragma map(BBOA1SRP,"BBOA1SRP")
   void BBOA1SRP (_CHAR12 *,     /* Connection handle               */
                  _POINTER *,    /* Response data address           */
                  _INT4 *,       /* Response data length            */
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifndef __cplusplus
   #pragma linkage(BBOA1SRP, OS31_NOSTACK)
   #endif

   /* 64-bit SEND RESPONSE API                             @F003703A*/
   /* 64-bit SEND RESPONSE API                             @F003703A*/
   /* 64-bit SEND RESPONSE API                             @F003703A*/
   #pragma map(BBGA1SRP,"BBGA1SRP")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1SRP (_CHAR12 *,     /* Connection handle      @F003703A*/
                  _POINTER *,    /* Response data address  @F003703A*/
                  _LONGINT *,    /* Response data length   @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/
   }
   #endif

   /* SEND EXC RESPONSE API                                         */
   /* SEND EXC RESPONSE API                                         */
   /* SEND EXC RESPONSE API                                         */
   #pragma map(BBOA1SRX,"BBOA1SRX")
   void BBOA1SRX (_CHAR12 *,      /* Connection handle               */
                  _POINTER *,    /* Exc Response data address       */
                  _INT4 *,       /* Exc Response data length        */
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifndef __cplusplus
   #pragma linkage(BBOA1SRX, OS31_NOSTACK)
   #endif

   /* 64-bit SEND EXC RESPONSE API                         @F003703A*/
   /* 64-bit SEND EXC RESPONSE API                         @F003703A*/
   /* 64-bit SEND EXC RESPONSE API                         @F003703A*/
   #pragma map(BBGA1SRX,"BBGA1SRX")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1SRX (_CHAR12 *,      /* Connection handle     @F003703A*/
                  _POINTER *,    /* Exc Response data addr @F003703A*/
                  _LONGINT *,    /* Exc Response data len  @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/
   }
   #endif

   /* RECEIVE REQUEST ANY API                                       */
   /* RECEIVE REQUEST ANY API                                       */
   /* RECEIVE REQUEST ANY API                                       */
   #pragma map(BBOA1RCA,"BBOA1RCA")
   void BBOA1RCA (_CHAR12 *,     /* Register name                   */
                  _CHAR12 *,     /* Connection handle               */
                  _CHAR256 *,    /* Service name                    */
                  _INT4 *,       /* Service name length             */
                  _INT4 *,       /* Request data length             */
                  _INT4 *,       /* Wait time               @579234A*/
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifndef __cplusplus
   #pragma linkage(BBOA1RCA, OS31_NOSTACK)
   #endif

   /* 64-bit RECEIVE REQUEST ANY API                       @F003703A*/
   /* 64-bit RECEIVE REQUEST ANY API                       @F003703A*/
   /* 64-bit RECEIVE REQUEST ANY API                       @F003703A*/
   #pragma map(BBGA1RCA,"BBGA1RCA")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1RCA (_CHAR12 *,     /* Register name          @F003703A*/
                  _CHAR12 *,     /* Connection handle      @F003703A*/
                  _CHAR256 *,    /* Service name           @F003703A*/
                  _INT4 *,       /* Service name length    @F003703A*/
                  _LONGINT *,    /* Request data length    @F003703A*/
                  _INT4 *,       /* Wait time              @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/
   }
   #endif

   /* RECEIVE REQUEST SPECIFIC API                                  */
   /* RECEIVE REQUEST SPECIFIC API                                  */
   /* RECEIVE REQUEST SPECIFIC API                                  */
   #pragma map(BBOA1RCS,"BBOA1RCS")
   void BBOA1RCS (_CHAR12 *,     /* Connection handle               */
                  _CHAR256 *,    /* Service name                    */
                  _INT4 *,       /* Service name length             */
                  _INT4 *,       /* Request data length             */
                  _INT4 *,       /* Async flag                      */
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifndef __cplusplus
   #pragma linkage(BBOA1RCS, OS31_NOSTACK)
   #endif

   /* 64-bit RECEIVE REQUEST SPECIFIC API                  @F003703A*/
   /* 64-bit RECEIVE REQUEST SPECIFIC API                  @F003703A*/
   /* 64-bit RECEIVE REQUEST SPECIFIC API                  @F003703A*/
   #pragma map(BBGA1RCS,"BBGA1RCS")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1RCS (_CHAR12 *,     /* Connection handle      @F003703A*/
                  _CHAR256 *,    /* Service name           @F003703A*/
                  _INT4 *,       /* Service name length    @F003703A*/
                  _LONGINT *,    /* Request data length    @F003703A*/
                  _INT4 *,       /* Async flag             @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/
   }
   #endif

   /* GET CONTEXT DATA API                                          */
   /* GET CONTEXT DATA API                                          */
   /* GET CONTEXT DATA API                                          */
   #pragma map(BBOA1GTX,"BBOA1GTX")
   void BBOA1GTX (_CHAR12 *,      /* Connection handle              */
                  _POINTER *,    /* Address of context buffer       */
                  _INT4 *,       /* Address of context length       */
                  _INT4 *,       /* Return code                     */
                  _INT4 *,       /* Reason code                     */
                  _INT4 *);      /* Return value                    */
   #ifndef __cplusplus
   #pragma linkage(BBOA1GTX, OS31_NOSTACK)
   #endif

   /* 64-bit GET CONTEXT DATA API                          @F003703A*/
   /* 64-bit GET CONTEXT DATA API                          @F003703A*/
   /* 64-bit GET CONTEXT DATA API                          @F003703A*/
   #pragma map(BBGA1GTX,"BBGA1GTX")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1GTX (_CHAR12 *,      /* Connection handle     @F003703A*/
                  _POINTER *,    /* Addr of context buffer @F003703A*/
                  _LONGINT *,    /* Addr of context length @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *,       /* Reason code            @F003703A*/
                  _INT4 *);      /* Return value           @F003703A*/
   }
   #endif

   /* INFORMATION GET API                                           */
   /* INFORMATION GET API                                           */
   /* INFORMATION GET API                                           */
   #pragma map(BBOA1INF,"BBOA1INF")

   void BBOA1INF (_CHAR12 *,     /* Register name             */
                  _CHAR8 *,      /* Daemon group name         */
                  _CHAR8 *,      /* Node name                 */
                  _CHAR8 *,      /* Server name               */
                  _FLOAT8 *,     /* Adapters shared mem ptr   */
                  _FLOAT8 *,     /* Adapters trace header ptr */
                  _FLOAT8 *,     /* Adapters reg. entry ptr   */
                  _POINTER *,    /* Adapters conn. info ptr   */
                  _RC *, _RSN *);

   #ifndef __cplusplus
   #pragma linkage(BBOA1INF, OS31_NOSTACK)
   #endif

   /* 64-bit INFORMATION GET API                           @F003703A*/
   /* 64-bit INFORMATION GET API                           @F003703A*/
   /* 64-bit INFORMATION GET API                           @F003703A*/
   #pragma map(BBGA1INF,"BBGA1INF")

   #ifdef _LP64
   extern "OS_NOSTACK" {
   void BBGA1INF (_CHAR12 *,     /* Register name          @F003703A*/
                  _CHAR8 *,      /* Daemon group name      @F003703A*/
                  _CHAR8 *,      /* Node name              @F003703A*/
                  _CHAR8 *,      /* Server name            @F003703A*/
                  _FLOAT8 *,     /* Adapters shared mem@   @F003703A*/
                  _FLOAT8 *,     /* Adapters trace header@ @F003703A*/
                  _FLOAT8 *,     /* Adapters reg. entry@   @F003703A*/
                  _POINTER *,    /* Adapters conn. info@   @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   }
   #endif
 /* #endif    !defined(_LP64)             */

  #ifdef __cplusplus
  }
  #endif

                   #pragma checkout(resume)
                   ??=endif   /* BBOAAPI_INCLUDED */
