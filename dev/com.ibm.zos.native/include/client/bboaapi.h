                   ??=ifndef BBOAAPI_INCLUDED
                   ??=ifdef __COMPILER_VER__
                    ??=pragma filetag("IBM-1047")
                   ??=endif
                   #define BBOAAPI_INCLUDED
                   #pragma nomargins nosequence
                   #pragma checkout(suspend)
 /********************************************************************
 *                                                                   *
 *   Header name: bboaapi.h                                          *
 *   Component:   WebSphere Application Server for z/OS              *
 *                                                                   *
 *   Descriptive name:                                               *
 *                                                                   *
 *     WAS Optimized Local Adapters (OLA) API header file for C/C++  *
 *                                                                   *
 *   Proprietary statement:                                          *
 *                                                                   *
 *     Licensed Material - Property of IBM                           *
 *                                                                   *
 *     5655-N02 Copyright IBM Corp. 2008                             *
 *     All Rights Reserved.                                          *
 *     U.S. Government users - RESTRICTED RIGHTS - Use, Duplication, *
 *     or Disclosure restricted by GSA-ADP schedule contract with    *
 *     IBM Corp.                                                     *
 *                                                                   *
 *     Status = H28W800                                              *
 *                                                                   *
 *   Change activity:                                                *
 *   $L0=LI4798,  H28W700,20080623, JTM : High speed connectors APIs *
 *   $537906,     H28W700, 20090203, JTM : Support 12 byte connhdls. *
 *   $LI4798I7-01,H28W700,20090228, FGOL: Add flag for outbound CICS *
 *                                              security propagation *
 *   $579234,     H28W700, 20090310, JTM : Add waittime to BBOA1RCA. *
 *   $F003691-01  H28W700, 20090911, FGOL: Add reg trace level flags *
 *   $656134,     H28W700, 20100616, JTM : Use extern OS_NOSTACK for *
 *                                         CPP compiles.             *
 *   $F003703     H28W800, 20101130, FGOL/JTM:  Enable 64-bit API.   *
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

   struct _REGFLAGS {                  /* For use by BBOA1REG        */
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

   struct _UNREGFLAGS {                /* For use by BBOA1URG        */
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

   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1REG (_CHAR8 *,      /* Daemon group name         */
                  _CHAR8 *,      /* Node name                 */
                  _CHAR8 *,      /* Server name               */
                  _CHAR12 *,     /* Register name             */
                  _INT4 *,       /* Minimum # connections     */
                  _INT4 *,       /* Maximum # connections     */
                  _REGFLAGS *,   /* Register flags (tx, sec)  */
                  _RC *, _RSN *);
   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1REG, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit REGISTER API                                      @F003703A*/
   /* 64-bit REGISTER API                                      @F003703A*/
   /* 64-bit REGISTER API                                      @F003703A*/
   #pragma map(BBGA1REG,"BBGA1REG")
   #pragma linkage(BBGA1REG, OS_NOSTACK)

   void BBGA1REG (_CHAR8 *,      /* Daemon group name          @F003703A*/
                  _CHAR8 *,      /* Node name                  @F003703A*/
                  _CHAR8 *,      /* Server name                @F003703A*/
                  _CHAR12 *,     /* Register name              @F003703A*/
                  _INT4 *,       /* Minimum # connections      @F003703A*/
                  _INT4 *,       /* Maximum # connections      @F003703A*/
                  _REGFLAGS *,   /* Register flags (tx, sec)   @F003703A*/
                  _RC *, _RSN *);                        /*    @F003703A*/

   #endif

   /* UNREGISTER API                                                */
   /* UNREGISTER API                                                */
   /* UNREGISTER API                                                */
   #pragma map(BBOA1URG,"BBOA1URG")

   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1URG (_CHAR12 *,     /* Register name             */
                  _UNREGFLAGS *,
                  _RC *, _RSN *);

   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1URG, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit UNREGISTER API                                @F003703A*/
   /* 64-bit UNREGISTER API                                @F003703A*/
   /* 64-bit UNREGISTER API                                @F003703A*/
   #pragma map(BBGA1URG,"BBGA1URG")
   #pragma linkage(BBGA1URG, OS_NOSTACK)

   void BBGA1URG (_CHAR12 *,     /* Register name          @F003703A*/
                  _UNREGFLAGS *,
                  _RC *, _RSN *);

   #endif

   /* INVOKE API                                                    */
   /* INVOKE API                                                    */
   /* INVOKE API                                                    */
   #pragma map(BBOA1INV,"BBOA1INV")

   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
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

   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1INV, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit INVOKE API                                    @F003703A*/
   /* 64-bit INVOKE API                                    @F003703A*/
   /* 64-bit INVOKE API                                    @F003703A*/
   #pragma map(BBGA1INV,"BBGA1INV")
   #pragma linkage(BBGA1INV, OS_NOSTACK)

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

   #endif

   /* CONNECTION GET API                                            */
   /* CONNECTION GET API                                            */
   /* CONNECTION GET API                                            */
   #pragma map(BBOA1CNG,"BBOA1CNG")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1CNG (_CHAR12 *,     /* Register name             */
                  _CHAR12 *,     /* Connection handle         */
                  _INT4 *,       /* Wait time                 */
                  _RC *, _RSN *);

   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1CNG, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit CONNECTION GET API                            @F003703A*/
   /* 64-bit CONNECTION GET API                            @F003703A*/
   /* 64-bit CONNECTION GET API                            @F003703A*/
   #pragma map(BBGA1CNG,"BBGA1CNG")
   #pragma linkage(BBGA1CNG, OS_NOSTACK)

   void BBGA1CNG (_CHAR12 *,     /* Register name          @F003703A*/
                  _CHAR12 *,     /* Connection handle      @F003703A*/
                  _INT4 *,       /* Wait time              @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   #endif

   /* SEND REQUEST API                                              */
   /* SEND REQUEST API                                              */
   /* SEND REQUEST API                                              */
   #pragma map(BBOA1SRQ,"BBOA1SRQ")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1SRQ (_CHAR12 *,  /* Connection handle         */
                  _INT4 *,    /* Request type              */
                  _CHAR256 *, /* Service name              */
                  _INT4 *,    /* Service name length       */
                  _POINTER *, /* Request data address      */
                  _INT4 *,    /* Request data length       */
                  _INT4 *,    /* Async flag                */
                  _INT4 *,    /* Response data length      */
                  _RC *, _RSN *);

   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1SRQ, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit SEND REQUEST API                              @F003703A*/
   /* 64-bit SEND REQUEST API                              @F003703A*/
   /* 64-bit SEND REQUEST API                              @F003703A*/
   #pragma map(BBGA1SRQ,"BBGA1SRQ")
   #pragma linkage(BBGA1SRQ, OS_NOSTACK)

   void BBGA1SRQ (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _INT4 *,    /* Request type              @F003703A*/
                  _CHAR256 *, /* Service name              @F003703A*/
                  _INT4 *,    /* Service name length       @F003703A*/
                  _POINTER *, /* Request data address      @F003703A*/
                  _LONGINT *, /* Request data length       @F003703A*/
                  _INT4 *,    /* Async flag                @F003703A*/
                  _LONGINT *, /* Response data length      @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   #endif

   /* RECEIVE RESPONSE LENGTH API                                   */
   /* RECEIVE RESPONSE LENGTH API                                   */
   /* RECEIVE RESPONSE LENGTH API                                   */
   #pragma map(BBOA1RCL,"BBOA1RCL")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1RCL (_CHAR12 *,  /* Connection handle         */
                  _INT4 *,    /* Async flag                */
                  _INT4 *,    /* Response data length      */
                  _RC *, _RSN *);

   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1RCL, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit RECEIVE RESPONSE LENGTH API                   @F003703A*/
   /* 64-bit RECEIVE RESPONSE LENGTH API                   @F003703A*/
   /* 64-bit RECEIVE RESPONSE LENGTH API                   @F003703A*/
   #pragma map(BBGA1RCL,"BBGA1RCL")
   #pragma linkage(BBGA1RCL, OS_NOSTACK)

   void BBGA1RCL (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _INT4 *,    /* Async flag                @F003703A*/
                  _LONGINT *, /* Response data length      @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   #endif

   /* GET DATA API                                                  */
   /* GET DATA API                                                  */
   /* GET DATA API                                                  */
   #pragma map(BBOA1GET,"BBOA1GET")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1GET (_CHAR12 *,  /* Connection handle         */
                  _POINTER *, /* Response data address     */
                  _INT4 *,    /* Response data length      */
                  _RC *, _RSN *, _RV *);

   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1GET, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit GET DATA API                                  @F003703A*/
   /* 64-bit GET DATA API                                  @F003703A*/
   /* 64-bit GET DATA API                                  @F003703A*/
   #pragma map(BBGA1GET,"BBGA1GET")
   #pragma linkage(BBGA1GET, OS_NOSTACK)

   void BBGA1GET (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _POINTER *, /* Response data address     @F003703A*/
                  _LONGINT *, /* Response data length      @F003703A*/
                  _RC *, _RSN *, _RV *);                /* @F003703A*/

   #endif

   /* CONNECTION RELEASE API                                        */
   /* CONNECTION RELEASE API                                        */
   /* CONNECTION RELEASE API                                        */
   #pragma map(BBOA1CNR,"BBOA1CNR")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1CNR (_CHAR12 *,  /* Connection handle         */
                  _RC *, _RSN *);

   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1CNR, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit CONNECTION RELEASE API                        @F003703A*/
   /* 64-bit CONNECTION RELEASE API                        @F003703A*/
   /* 64-bit CONNECTION RELEASE API                        @F003703A*/
   #pragma map(BBGA1CNR,"BBGA1CNR")
   #pragma linkage(BBGA1CNR, OS_NOSTACK)

   void BBGA1CNR (_CHAR12 *,  /* Connection handle         @F003703A*/
                  _RC *, _RSN *);                       /* @F003703A*/

   #endif

   /* HOST SERVICE API                                              */
   /* HOST SERVICE API                                              */
   /* HOST SERVICE API                                              */
   #pragma map(BBOA1SRV,"BBOA1SRV")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
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
   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1SRV, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit HOST SERVICE API                              @F003703A*/
   /* 64-bit HOST SERVICE API                              @F003703A*/
   /* 64-bit HOST SERVICE API                              @F003703A*/
   #pragma map(BBGA1SRV,"BBGA1SRV")
   #pragma linkage(BBGA1SRV, OS_NOSTACK)

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

   #endif

   /* SEND RESPONSE API                                             */
   /* SEND RESPONSE API                                             */
   /* SEND RESPONSE API                                             */
   #pragma map(BBOA1SRP,"BBOA1SRP")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1SRP (_CHAR12 *,     /* Connection handle               */
                  _POINTER *,    /* Response data address           */
                  _INT4 *,       /* Response data length            */
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1SRP, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit SEND RESPONSE API                             @F003703A*/
   /* 64-bit SEND RESPONSE API                             @F003703A*/
   /* 64-bit SEND RESPONSE API                             @F003703A*/
   #pragma map(BBGA1SRP,"BBGA1SRP")
   #pragma linkage(BBGA1SRP, OS_NOSTACK)

   void BBGA1SRP (_CHAR12 *,     /* Connection handle      @F003703A*/
                  _POINTER *,    /* Response data address  @F003703A*/
                  _LONGINT *,    /* Response data length   @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/

   #endif

   /* SEND EXC RESPONSE API                                         */
   /* SEND EXC RESPONSE API                                         */
   /* SEND EXC RESPONSE API                                         */
   #pragma map(BBOA1SRX,"BBOA1SRX")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1SRX (_CHAR12 *,      /* Connection handle               */
                  _POINTER *,    /* Exc Response data address       */
                  _INT4 *,       /* Exc Response data length        */
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1SRX, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit SEND EXC RESPONSE API                         @F003703A*/
   /* 64-bit SEND EXC RESPONSE API                         @F003703A*/
   /* 64-bit SEND EXC RESPONSE API                         @F003703A*/
   #pragma map(BBGA1SRX,"BBGA1SRX")
   #pragma linkage(BBGA1SRX, OS_NOSTACK)

   void BBGA1SRX (_CHAR12 *,      /* Connection handle     @F003703A*/
                  _POINTER *,    /* Exc Response data addr @F003703A*/
                  _LONGINT *,    /* Exc Response data len  @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/

   #endif

   /* RECEIVE REQUEST ANY API                                       */
   /* RECEIVE REQUEST ANY API                                       */
   /* RECEIVE REQUEST ANY API                                       */
   #pragma map(BBOA1RCA,"BBOA1RCA")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1RCA (_CHAR12 *,     /* Register name                   */
                  _CHAR12 *,     /* Connection handle               */
                  _CHAR256 *,    /* Service name                    */
                  _INT4 *,       /* Service name length             */
                  _INT4 *,       /* Request data length             */
                  _INT4 *,       /* Wait time               @579234A*/
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1RCA, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit RECEIVE REQUEST ANY API                       @F003703A*/
   /* 64-bit RECEIVE REQUEST ANY API                       @F003703A*/
   /* 64-bit RECEIVE REQUEST ANY API                       @F003703A*/
   #pragma map(BBGA1RCA,"BBGA1RCA")
   #pragma linkage(BBGA1RCA, OS_NOSTACK)

   void BBGA1RCA (_CHAR12 *,     /* Register name          @F003703A*/
                  _CHAR12 *,     /* Connection handle      @F003703A*/
                  _CHAR256 *,    /* Service name           @F003703A*/
                  _INT4 *,       /* Service name length    @F003703A*/
                  _LONGINT *,    /* Request data length    @F003703A*/
                  _INT4 *,       /* Wait time              @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/

   #endif

   /* RECEIVE REQUEST SPECIFIC API                                  */
   /* RECEIVE REQUEST SPECIFIC API                                  */
   /* RECEIVE REQUEST SPECIFIC API                                  */
   #pragma map(BBOA1RCS,"BBOA1RCS")
   #ifdef __cplusplus
     extern "OS_NOSTACK" {
   #endif
   void BBOA1RCS (_CHAR12 *,     /* Connection handle               */
                  _CHAR256 *,    /* Service name                    */
                  _INT4 *,       /* Service name length             */
                  _INT4 *,       /* Request data length             */
                  _INT4 *,       /* Async flag                      */
                  _INT4 *,       /* Return code                     */
                  _INT4 *);      /* Reason code                     */
   #ifdef __cplusplus
     }
   #else
     #pragma linkage(BBOA1RCS, OS31_NOSTACK)
   #endif

   #ifdef _LP64
   /* 64-bit RECEIVE REQUEST SPECIFIC API                  @F003703A*/
   /* 64-bit RECEIVE REQUEST SPECIFIC API                  @F003703A*/
   /* 64-bit RECEIVE REQUEST SPECIFIC API                  @F003703A*/
   #pragma map(BBGA1RCS,"BBGA1RCS")
   #pragma linkage(BBGA1RCS, OS_NOSTACK)

   void BBGA1RCS (_CHAR12 *,     /* Connection handle      @F003703A*/
                  _CHAR256 *,    /* Service name           @F003703A*/
                  _INT4 *,       /* Service name length    @F003703A*/
                  _LONGINT *,    /* Request data length    @F003703A*/
                  _INT4 *,       /* Async flag             @F003703A*/
                  _INT4 *,       /* Return code            @F003703A*/
                  _INT4 *);      /* Reason code            @F003703A*/

   #endif



 /* #endif    !defined(_LP64)             */

  #ifdef __cplusplus
  }
  #endif

                   #pragma checkout(resume)
                   ??=endif   /* BBOAAPI_INCLUDED */
