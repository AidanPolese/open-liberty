 /********************************************************************
 *                                                                   *
 *   Header name: bboaapic.h                                          *
 *   Component:   WebSphere Application Server for z/OS              *
 *                                                                   *
 *   Descriptive name:                                               *
 *                                                                   *
 *     WAS Optimized Local Adapters (OLA) API header file for C/C++  *
 *     describes parameter list for calling OLA CICS BBOACNTL        *
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
 *     Status = HBBO800                                              *
 *                                                                   *
 *   Change activity:                                                *
 *   $PM90865,    HBBO800, 20131001, JTM : OLA support for EC LINK   *
 *                                         BBOACNTL with COMMAREA    *
 *   $PI52665,    H2W8555, 20160115, JTM:  Add new options for RTX   *
 *                                         RETRY and BBOC DOC.       *
 *                                                                   *
 ********************************************************************/
#ifndef _OLAFVT_CICS_INCLUDES
#define _OLAFVT_CICS_INCLUDES

 /* ----------------------------------------------------------------- */
 /* COMMAREA mapping                                                  */
 /* ----------------------------------------------------------------- */
#define BBOACNTL_COMMAREA_V1 1

#define BBOACNTL_COMMAREA_RC_NORMAL 0
#define BBOACNTL_COMMAREA_RC_WARN 4
#define BBOACNTL_COMMAREA_RC_ERROR 8

#define BBOACNTL_COMMAREA_RSN_NONE 0

 struct inputCommarea {
   char command[256];
 };

 struct outputCommarea_v1 {
   char message[256];
   int version;
   int returnCode;
   int reasonCode;
 };

 struct BBOACNTL_Commarea {
   union {
     struct inputCommarea input;
     struct outputCommarea_v1 output;
   };
 };
   #pragma pack(1)

   /* CICS Link Server TSQ record (BBOC LTSQ=<name>)      */
   struct tsq_record
   {
     int    tsq_record_ver;           /*0x00    */
     char   tsq_record_jobname[8];    /*0x04    */
     char   tsq_record_regname[12];   /*0x0C    */
     char   tsq_record_dgname[8];     /*0x18    */
     char   tsq_record_ndname[8];     /*0x20    */
     char   tsq_record_srvname[8];    /*0x28    */
     char   tsq_record_svcname[8];    /*0x30    */
     int    tsq_record_minconn;       /*0x38    */
     int    tsq_record_maxconn;       /*0x3C    */
     int    tsq_record_actconn;       /*0x40    */
     int    tsq_record_status;        /*0x44    */
     char   tsq_record_reuse[1];      /*0x48    */
     char   tsq_record_txn[1];        /*0x49    */
     char   tsq_record_sec[1];        /*0x4A    */
     char   tsq_record_lsync[1];      /*0x4B    */
     int    tsq_record_reuc;          /*0x4C    */
     int    tsq_record_reut;          /*0x50    */
     struct {
       char   tsq_record_rtx[4];      /*0x54    @PI52665A*/
       char   tsq_record_rtxsys[4];   /*0x58    @PI52665A*/
       int    tsq_record_retcnt;      /*0x5C    @PI52665A*/
       int    tsq_record_retint;      /*0x60    @PI52665A*/
       char   tsq_record_rtxp[1];     /*0x64    @PI52665A*/
       char   tsq_record_retry[1];    /*0x65    @PI52665A*/
       char   tsq_record_retmode[1];  /*0x66    @PI52665A*/
       char   tsq_record_reserve[149];/*0x67    @PI52665C*/
     } v2;
                /* Size of TSQ record : 0x100   */
   };
   #pragma pack(reset)

#endif
