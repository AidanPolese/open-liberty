/*----------------------------------------------------------------------
 *
 *  Module Name:  BBOACSRV.CPP
 *
 *  Descriptive Name: Optimized Adapters CICS Program Link Server (BBO$)
 *      Acronym:  N/A
 *
 *  Proprietary Statement
 *
 * IBM Confidential
 * OCO Source Materials
 * 5655-I35 (C) Copyright IBM Corp. 2008, 2014
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * Status = H28W700
 *
 *  Function:  Runs as a server, accepting messages from WAS over the
 *             adapters BBOA1RCA API and starting BBO# tasks where the
 *             actual EC LINK to the target programs occurs. Supports
 *             asserting the propagated userid on the BBO# task. Started
 *             with BBOC START_SRVR and runs until BBOC STOP_SRVR.
 *
 *   Eye Catcher:
 *     Offset:
 *     Length:
 *
 *  Storage Attributes:
 *    Subpool:
 *    Key:
 *    Residency:
 *
 *  Size:   XXXX Bytes
 *
 *  Created by: User or System
 *
 *  Pointed to by: User or System
 *
 *  Serialization: None Required.
 *
 *  Headers included:
 *    C++: #include <private/bboaapi.h>
 *         #include <private/bboashr.h>
 *
 *  Deleted by: System
 *
 *  Frequency:
 *    One per system
 *
 *  Dependencies:  None
 *
 *  Message IDs used in this part:
 *   BBOA8500I - BBOA8502I: Debug messages
 *   BBOA8503x - BBOA8699x: Error/Warning messages
 *
 *  Change Activity:
 *   $LI4799        , H28W700, 20081230, JTM : Initial Implementation
 *   $574294        , H28W700, 20090209, JTM : Bug with handling userid
 *   $574703        , H28W700, 20090219, JTM : Support for trapping
 *                                             abends in linked to prog.
 *   $577009        , H28W700, 20090303, JTM : Make sure to call BBOA1URG
 *                                             after error in BBOA1RCA
 *                                             and terminating this task.
 *   $LI4798I7-01   , H28W700, 20090228, FGOL: Change name of security prop flag
 *   $577009.1      , H28W700, 20090304, FGOL: Re-add LI4798I7-01
 *   $578689        , H28W700, 20090306, JTM : Performance, reduce tracing
 *                                             overhead in main path.
 *                                             Cleanup messages.
 *   $579005        , H28W700, 20090316, PDFG: Exception in EBCDIC
 *   $579234        , H28W700, 20090310, JTM : Support for reusing BBO# tasks
 *                                             when REU=Y is requested.
 *   $580366        , H28W700, 20090324, JTM : Fallout from new latch code.
 *   $584331        , H28W700, 20090408, JTM : Fix regression from 577009-
 *                                             BBOC STOP_SRVR RC8 RSN8.
 *   $584680        , H28W700, 20090415, JTM : Error with REU=Y and link
 *                                             tranid passed from WAS.
 *   $F003691       , H28W700, 20090709, PDFG: Pass XID to link task
 *   $F003691       , H28W700, 20091211, PDFG: Read XID out of contexts
 *   $PK99821       , H28W700, 20100125, PDFG: Remove ATTACH parm
 *   $F003691.20607 , H28W700, 20100204, PDFG: Don't stop on COMM failure
 *   $F003691.20604 , H28W700, 20100224, PDFG: Recovery task
 *   $F003691.20605 , H28W700, 20100225, PDFG: Give WAS svr name to lnk
 *   $656134        , H28W700, 20100608, JTM : Add reuse count and time parms.
 *   $633440        , H28W800, 20101014, PDFG: Don't truncate BBOQ messages
 *   $F003701-36909 , H28W800, 20110425, PDLS: WOLA outbound SMF
 *   $711599        , H28W800, 20110720, JTM : CICS TS 4.2 / WOLA correlator.
 *   $728847        , H28W800, 20120221, JTM : Bug handling Tx context ptr.
 *   $F014448       , H28W800, 20120828, CRP : WOLA multi-container support
 *   $PM70002       , HBBO800, 20120730, JTM:  Add SYNCONRETURN option to
 *                                             support TXN=N link server that
 *                                             DPLs to programs that EC SYNC.
 *   $PM70002       , H28W800, 20120815, PDFG: Restructure for service
 *   $PM88133       , H28W800, 20130501, PDFG: eibtaskn is packed decimal
 *   $PM90865       , H28W855, 20130925, JTM : change findLinkServer parms
 *   $PI23547       , H28W855, 20140905, PDFG: Link task tran ID check
 *   $PI19688       , H28W800, 20140612, PDFG: Pass REQID on START TRANSID
 *   $PI27022       , H28W855, 20141207, MJA:  Add WAS build level message
 *   $PI24358       , H28W855, 20140904, PDFG: Deal with pending unregister
 *   $PI53321       , H28W855, 20160119, MJA:  Add support for CICS TS 53
 *   $PI55413       , H28W855, 20160114, MJA:  Create unique CICS TS Queue
 *                                             name for link server
 *   $PI52665       , H2W8555, 20161108, JTM:  Add support for long commands
 *                                             with new DOC request type and
 *                                             passing TransID/SYSID on EC LINK in
 *                                             link invocation task. Also adds
 *                                             support for command RETRY.
 *----------------------------------------------------------------------*/

 #define TRUE 1
 #define FALSE 0

 #include <stdio.h>
 #include <cics.h>
 #include <time.h>
 #include <string.h>
 #include <stdarg.h>
 #include <stdlib.h>
 #include <stdbool.h>
 #include <bboaapi.h>
 #include "include/server_wola_cics_link_server.h"

#ifndef _BBOA_PRIVATE_BBOACSVL_H_INCLUDE                  /* @F003691A*/
#include "include/bboacsvl.h"
#define _BBOA_PRIVATE_BBOACSVL_H_INCLUDE                  /* @F003691A*/
#endif                                                    /* @F003691A*/

#ifndef _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#include "include/bboaclst.h"
#define _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#endif

#define DEBUG0(...) DEBUG0_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#define DEBUG1(...) DEBUG1_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#define DEBUG2(...) DEBUG2_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#undef PROCESS_INFO_PTR

 /* ----------------------------------------------------------------- */
 /* Information about this task.                                      */
 /* ----------------------------------------------------------------- */
 struct cicsServerTaskInfo {
   struct cicsLinkServerProcessInfo baseInfo; /* Common information */
   char messageBuffer[512]; /* Message buffer */
 };

#define PROCESS_INFO_PTR info_p
 /* ----------------------------------------------------------------- */
 /* Write a message to the BBOQ queue.                                */
 /* ----------------------------------------------------------------- */
void ISSUE_MSG(struct cicsServerTaskInfo* info_p, int messageID, char* defaultMessage,  ...) {
  char* translatedMessage =
      getTranslatedMessageById(&(info_p->baseInfo), messageID, defaultMessage);
  va_list vargs;
  va_start(vargs, defaultMessage);
  vsnprintf(info_p->messageBuffer, sizeof(info_p->messageBuffer), translatedMessage, vargs);
  va_end(vargs);
  write_tdq(&(info_p->baseInfo), info_p->messageBuffer);
}

/* ----------------------------------------------------------------- */
/* Write a message to the BBOQ queue with a return and reason code.  */
/* ----------------------------------------------------------------- */
void ISSUE_MSG_RC_RSN(struct cicsServerTaskInfo* info_p, int messageID, char* defaultMessage,  int rc, int rsn) {
 char rcstr[9];
 char rsnstr[9];
 snprintf(rcstr, sizeof(rcstr), "%d", rc);
 snprintf(rsnstr, sizeof(rsnstr), "%d", rsn);
 ISSUE_MSG(info_p, messageID, defaultMessage, rcstr, rsnstr);
}
#undef PROCESS_INFO_PTR

 /* ----------------------------------------------------------------- */
 /* release_conn() : Release the current connection and return.       */
 /* ----------------------------------------------------------------- */
#define PROCESS_INFO_PTR processInfo_p
 int release_conn(struct cicsServerTaskInfo* processInfo_p,
                  char* connhdl_p) {
   int rc=0;
   int rsn=0;
   char connhdl[12];

   /* ----------------------------------------------------- */
   /* Release the connection back to the pool.              */
   /* ----------------------------------------------------- */
   DEBUG1("Releasing connection back to pool.");

   memcpy(connhdl, connhdl_p, sizeof(connhdl));
   BBOA1CNR ( &connhdl
            , &rc
            , &rsn
            );

   if (rc == 0) {
     DEBUG1("Released conn back to pool. connhdl: %8.8x%8.8x%8.8x",
            *((int*)(&(connhdl[0]))),
            *((int*)(&(connhdl[4]))),
            *((int*)(&(connhdl[8]))));

     return(0);
   }
   else {
     ISSUE_MSG_RC_RSN(processInfo_p, LINK_SERVER_RELEASE_CONNECTION_ERROR,
                      "A Release Connection error occurred. The return code is %s.  The reason code is %s.",
                      rc, rsn);
     return(8);
   }

 }

 /* ----------------------------------------------------------------- */
 /* send_exc_resp() : Send exception data back to requestor           */
 /* ----------------------------------------------------------------- */
  int send_exc_resp (struct cicsServerTaskInfo* processInfo_p,
                     char* connhdl_p, void * excdata_p, int exclen)
  {
    int rc  = 0;
    int rsn = 0;
    char exccontainerid[16];
    char excmessage[256];
    void * excmessage_p = &excmessage;
    int excmessagelen = 128;
    char connhdl[12];

    /* ----------------------------------------------------- */
    /* Send the exception data back                          */
    /* ----------------------------------------------------- */
    memset(excmessage,' ',sizeof(excmessage));
    memcpy(excmessage,excdata_p,128);

    if (processInfo_p->baseInfo.verbose>=1) {                  /* @578689A*/
      DEBUG1("Calling Send Exception Response API ...");

      DEBUG1("Calling Send Exception Response, sending data@... %x",
        excmessage_p);
      DEBUG1("Calling Send Exception Response, sending %d bytes...",
        excmessagelen);
    }

    memcpy(connhdl, connhdl_p, sizeof(connhdl));
    BBOA1SRX ( &connhdl
             , &excmessage_p
             , &excmessagelen
             , &rc
             , &rsn
            );

    if (rc == 0) {
      DEBUG1("Send Exc. Resp. completed. connhdl: %8.8x%8.8x%8.8x",
             *((int*)(&(connhdl[0]))),
             *((int*)(&(connhdl[4]))),
             *((int*)(&(connhdl[8]))));
    }
    else {
      ISSUE_MSG_RC_RSN(processInfo_p, LINK_SERVER_SEND_RESPONSE_EXCEPTION_ERROR,
                       "A Send Exception Response error occurred.  The return code is %s.  The reason code is %s.",
                       rc, rsn);
    }

    /* ----------------------------------------------------- */
    /* Release the connection back to the pool now.          */
    /* ----------------------------------------------------- */
    rc = release_conn(processInfo_p, connhdl);

    return(rc);
  }
#undef PROCESS_INFO_PTR

 /* ----------------------------------------------------------------- */
 /* Converts blanks in an input string to nulls.                      */
 /* --------------------------------------------------------@F003691A */
 void convertBlanksToNulls(char* src, int len)
 {
   int x;
   char* cur_char = src;

   for (x = 0; x < len; x++)
   {
     if (*cur_char == ' ') *cur_char = 0;
     cur_char = cur_char + 1;
   }
 }

 /* ----------------------------------------------------------------- */
 /* Generates a unique REQID to use on the START command.             */
 /* ------------------------------------------------------- @PI19688A */
 void generateUniqueReqid(int volatile* startCounter_p, char* buffer_p)
 {
   cs_t oldCount, newCount;
   do {
     oldCount = (cs_t)(*startCounter_p);
     newCount = (oldCount == 99999) ? 0 : oldCount + 1;
   } while (cs(&oldCount, (cs_t*)startCounter_p, newCount) != 0);

   sprintf(buffer_p, "BBO%.5i", newCount);
   // PI55413 conversion array. Create a req ID unique to bboacsrv.
   // will convert the number generated to a corresponding char
   // so for unique number 12345 will generate
   // a reqid of BBOABCDE
   int i;
   for(int i = 3; i < 8; i++) {           /* @PI54413 */
    if (buffer_p[i] == '0') {
        buffer_p[i] = 'J';
    } else {
        buffer_p[i] = buffer_p[i] - 0x30;
    }
   }

 }

 /* ----------------------------------------------------------------- */
 /* main() : Main procedure                                           */
 /* ----------------------------------------------------------------- */
  main ()
  {
    char screenmsg[256];
    short int srvrparmsl=0;
    short int linkparmsl=0;
    long int respcode=0;
    int rc=0;
    int exc_rc=0;
    int rsn=0;
    int rv=0;
    int loop=TRUE;
    char userid[8];
    BBOACSRV srvrparms;
    BBOACLNK linkparms;
    char ctxtdata[1024];                                   /*  @F003701-36909C*/
    BBOAUCTX * ctxtdata_p = (BBOAUCTX*) ctxtdata;          /* @F003691C*/
    BBOAUCIC * cicsdata_p = 0;
    BBOAUSEC * secdata_p = 0;
    BBOAUTXN * txndata_p = 0;
    struct bboactx* varctxdata_p = 0;                      /* @F003691A*/
    struct bboatxc* tx_context_p = 0;                      /* @F003691A*/
    struct bboacorctx* corr_context_p = 0;                 /* @711599A*/
    struct bboacorctx_V1* corr_contextV1_p = 0;            /* @711599A*/
    char waspropuserid[8];
    char wasproptranid[4];
    char ltx[4];
    int  reqsrvnamel;
    char reqsrvname[256];
    void * context_p = &ctxtdata;
    int  context_len = sizeof(ctxtdata);
    void * reqdata_p = NULL;
    int  reqlen;
    void * rspdata_p = NULL;
    int  rsplen;
    char connhdl[12];
    char reqbuff[16];
    char bbocdata[50];                                      /* @577009A*/
    short bbocdatal=sizeof(bbocdata);                       /* @577009A*/
    _UNREGFLAGS unregflags= {0x00,0x00,0x00,0x00};          /* @577009A*/
    int urgrc=0;                                            /* @577009A*/
    int urgrsn=0;                                           /* @577009A*/
    int delaysecs=0;                                        /* @577009A*/
    int waittime=0;                                         /* @579234A*/
    int lktran_prop_exc=0;                                  /* @579234A*/
    int volatile* startCounter_p = NULL;                   /* @PI19688A*/
    char reqid[9];                                         /* @PI19688A*/

    int connectivityEstablished = TRUE;                    /* @F003691A*/

    char was_connected_server[27];                         /* @F003691A*/
    char regname_nullterm[13];                             /* @F003691A*/

    int last_error_rc = 0;                                 /* @F003691A*/
    int last_error_rsn = 0;                                /* @F003691A*/
    unsigned int pendingUnregisterCount = 0;               /* @PI24358A*/

    struct tm *newtime;
    time_t ltime, start, finish;
    clock_t clock(void);
    double time1, timedif;

    struct cicsServerTaskInfo processInfo;
#define PROCESS_INFO_PTR &processInfo

    memset(screenmsg,' ',sizeof(screenmsg));
    srvrparmsl = sizeof(srvrparms);

    time(&start);
    newtime = localtime(&start);

    time1 = (double) clock();        /* Get initial time */
    time1 = time1 / CLOCKS_PER_SEC;  /*    in seconds    */

    /* Get addressability to the EIB first.                  */
    EXEC CICS ADDRESS EIB(dfheiptr);
    initializeCicsLinkServerProcessInfo(&(processInfo.baseInfo));

    EXEC CICS ASSIGN USERID(userid);

    /* Retrieve the tasknum and register name passed by      */
    /* the BBOC START SERVER request.                        */
    EXEC CICS RETRIEVE INTO(&srvrparms) LENGTH(srvrparmsl);

    memcpy(processInfo.baseInfo.wrtqname, srvrparms.acsrv_tracetdq,
           sizeof(processInfo.baseInfo.wrtqname));
    processInfo.baseInfo.verbose = srvrparms.acsrv_tracelvl;

    if (processInfo.baseInfo.verbose >= 1) {
      DEBUG1("CICS task number: %.7s",
             (processInfo.baseInfo.tskno_for_tsq) + 1); /* @PM88133C*/

      DEBUG1("<----------  ADAPTERS SERVER TASK START  ---  %.20s  ----->",
             asctime(newtime));

      /* Write message with WAS build level      @PI27022*/
      DEBUG1("<------ Build Level = %s ------->\n",LIBERTY_BUILD_LABEL);   /*@PI27022*/



      DEBUG1("%.4s current CICS task number: %.7s",
             dfheiptr->eibtrnid,
             (processInfo.baseInfo.tskno_for_tsq) + 1); /* @PM88133C*/

      DEBUG2("Start parm cb eyec: %.8s ", srvrparms.acsrveye);
      DEBUG1("Initiated by BBOC task number: %.7s", srvrparms.acsrv_tasknum);
      DEBUG1("Initiated with Register name: %.12s", srvrparms.acsrv_regname);
      DEBUG1("Initiated with Service name: %.8s", srvrparms.acsrv_svcname);
      DEBUG1("Initiated with Link transaction id: %.4s", srvrparms.acsrv_linktx);
      DEBUG1("Initiated under userid: %.8s", userid);
      DEBUG1("Initiated with trace level setting: %d", srvrparms.acsrv_tracelvl);
      DEBUG1("Initiated with trace TDQ name: %.4s", srvrparms.acsrv_tracetdq);
      if (srvrparms.acsrvflags.acsrvflg_reuse == TRUE) {
        DEBUG1("Initiated with REU=Y requested."); /* @579234A*/
        DEBUG1("Initiated with REUC: %d",srvrparms.v2.acsrv_reuc); /* @656134A*/
        DEBUG1("Initiated with REUT: %d",srvrparms.v2.acsrv_reut); /* @656134A*/
      }
      if (srvrparms.acsrvflags.acsrvflg_lsync == TRUE) {
        DEBUG1("Initiated with LSYNC=Y requested.");
      }
    }

    /* -------------------------------------------------------------------- */
    /* Figure out who we're connected to (for debug messages)               */
    /* -------------------------------------------------------------------- */
    memcpy(regname_nullterm, srvrparms.acsrv_regname,           /* @F003691A*/
           sizeof(srvrparms.acsrv_regname));                    /* @F003691A*/
    regname_nullterm[12] = 0;                                   /* @F003691A*/
    convertBlanksToNulls(regname_nullterm,                      /* @F003691A*/
                         sizeof(regname_nullterm));             /* @F003691A*/

    if (srvrparms.acsrvver == BBOACSRV_V3)                      /* @PI52665C*/
    {
      strncpy(was_connected_server, srvrparms.v2.acsrv_dgn,     /* @F003691A*/
              sizeof(srvrparms.v2.acsrv_dgn));                  /* @F003691A*/
      convertBlanksToNulls(was_connected_server,                /* @F003691A*/
                           sizeof(was_connected_server));       /* @F003691A*/
      strcat(was_connected_server, "/");                        /* @F003691A*/
      strncat(was_connected_server, srvrparms.v2.acsrv_ndn,     /* @F003691A*/
              sizeof(srvrparms.v2.acsrv_ndn));                  /* @F003691A*/
      convertBlanksToNulls(was_connected_server,                /* @F003691A*/
                           sizeof(was_connected_server));       /* @F003691A*/
      strcat(was_connected_server, "/");                        /* @F003691A*/
      strncat(was_connected_server, srvrparms.v2.acsrv_svn,     /* @F003691A*/
              sizeof(srvrparms.v2.acsrv_svn));                  /* @F003691A*/
      convertBlanksToNulls(was_connected_server,                /* @F003691A*/
                           sizeof(was_connected_server));       /* @F003691A*/
    }                                                           /* @F003691A*/
    else                                                        /* @F003691A*/
    {                                                           /* @F003691A*/
       ISSUE_MSG(PROCESS_INFO_PTR,
           LINK_SERVER_VERSION_MISMATCH,
           "Link Server parameter version mismatch. Received version: %h Expected version: %d ",
           srvrparms.acsrvver, BBOACSRV_V3);                    /* @PI52665A*/
       loop = FALSE;                                            /* @PI52665A*/
       rc=16;                                                   /* @PI52665A*/
    }                                                           /* @F003691A*/

    setLinkServerTaskid((char*)srvrparms.acsrv_regname,
                        (char*)processInfo.baseInfo.tskno_for_tsq+1); /* @PI53321C*/

    /* -------------------------------------------------------------------- */
    /* Get a hold of the start task counter.  We need to generate a unique  */
    /* REQID for each START we do, across all link servers.  This is a      */
    /* pointer to some common storage that we increment.                    */
    /* -------------------------------------------------------------------- */
    startCounter_p = getLinkServerStartCounter();                /* PI19688A*/
    if (startCounter_p == NULL) {                                /* PI19688A*/
        ISSUE_MSG(PROCESS_INFO_PTR, REQID_GENERATE_ERROR,
        "Error in generating a unique CICS REQID. Terminating.");/* @PI52665A*/
       loop = FALSE;                                             /* PI19688A*/
    }                                                            /* PI19688A*/

    while (loop == TRUE) {                                       /* PI19688C*/
      /* ----------------------------------------------------- */
      /* Set up parameter list and call BBOA1RCA API and wait  */
      /* for requests.                                         */
      /* ----------------------------------------------------- */
      memset(connhdl,0x00,sizeof(connhdl));
      strncpy(reqsrvname,
              srvrparms.acsrv_svcname,
              sizeof(reqsrvname)); /* pad with NULLs */
      reqsrvnamel = strlen(reqsrvname);
      lktran_prop_exc = 0;                                  /* @584680A*/

      if (pendingUnregisterCount == 0)
      {
        DEBUG1("Calling Receive request Any API for '%.8s'", reqsrvname);

        BBOA1RCA ( &(srvrparms.acsrv_regname)
                 , &connhdl
                 , &reqsrvname
                 , &reqsrvnamel
                 , &reqlen
                 , &waittime                                  /* @579234A*/
                 , &rc
                 , &rsn
                 );
      }

      if (rc == 0) {
        if (connectivityEstablished == FALSE)              /* @F003691A*/
        {                                                  /* @F003691A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_REGAINED_CONNECTIVITY,
                    "The %s link server re-established the connection to the following server instance: %s",
                    regname_nullterm, was_connected_server);  /* @F003691A*/
          connectivityEstablished = TRUE;                  /* @F003691A*/
          last_error_rc = 0;                               /* @F003691A*/
          last_error_rsn = 0;                              /* @F003691A*/
        }                                                  /* @F003691A*/

        if (processInfo.baseInfo.verbose>=1) {
          DEBUG1("Receive request completed. reqlen: %d",reqlen);
          DEBUG1("Service name %.8s and name length: %d",
                 reqsrvname,reqsrvnamel);
          DEBUG1("connhdl: %8.8x%8.8x%8.8x",
                 *(int*)(&(connhdl[0])),
                 *(int*)(&(connhdl[4])),
                 *(int*)(&(connhdl[8])));
        }
      }
      else
      {

        struct bboa_link_srvr_status_node* foundLinkServer = /* @PI24358C*/
          findLinkServer((char*)srvrparms.acsrv_regname,     /* @PM90865C*/
                         (char*)processInfo.baseInfo.tskno_for_tsq+1);  /* @PI53321C*/
        unsigned char pendingUnregister =                  /* @PI24358A*/
          ((foundLinkServer != NULL) && (foundLinkServer->urg_pend != 0));

        /*-------------------------------------------------------------*/
        /* Pending unregister is a special state.  We don't want to do */
        /* anything in hopes that the next time we come thru here, the */
        /* registration will be gone.                                  */
        /*----------------------------------------------------@PI24358A*/
        if (pendingUnregister == TRUE)
        {
          DEBUG1("A Receive Request error occurred with an "
                 "unregister pending; return code %d and reason code %d.\n", 
                 rc, rsn);
          
          /*-----------------------------------------------------------*/
          /* If we've been pending for more than 5 cycles, assume that */
          /* something went wrong with the control transaction, reset  */
          /* the pending unregister bit.                               */
          /*-----------------------------------------------------------*/
          if (pendingUnregisterCount > 5)
          {
            DEBUG1("The link server has reset a pending "
                   "unregister call.  \n");
            foundLinkServer->urg_pend = 0;
          }
          else 
          {
            pendingUnregisterCount++;
          }

          delaysecs = 2;
          EXEC CICS DELAY FOR SECONDS(delaysecs);

          continue;
        }

        pendingUnregisterCount = 0;                        /* @PI24358A*/

        if ((rc == 8) && ((rsn == 19) || (rsn == 8))) {    /* @PI24358C*/
          if (foundLinkServer == NULL)                     /* @F003691A*/
          {                                                /* @F003691A*/
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_STOP,
                      "The server is stopping because a request was received to stop the adapters server.");
            loop=FALSE;                                    /* @F003691M*/
          }                                                /* @F003691A*/
          else                                             /* @F003691A*/
          {                                                /* @F003691A*/
            DEBUG1("A Receive Request error occurred with return code %d and reason code %d.", rc, rsn); /* @F003691A*/
          }                                                /* @F003691A*/

          rc  = 0;
          rsn = 0;
        }
        else if ((rc == 8) && (rsn == 38)) {                /*@580336A*/
          if (foundLinkServer == NULL)                     /* @F003691A*/
          {                                                /* @F003691A*/
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_CONNHDL_STALE,
                      "The connection handle is not valid or a STOP request was received.");
            loop = FALSE;                                  /* @F003691M*/
          }                                                /* @F003691A*/
          else                                             /* @F003691A*/
          {                                                /* @F003691A*/
            DEBUG1("A Receive Request error occurred with return code %d and reason code %d.", rc, rsn); /* @F003691A*/
          }                                                /* @F003691A*/

          rc  = 0;                                         /* @580336A*/
          rsn = 0;                                         /* @580336A*/
        }                                                  /* @580336A*/
        else {                                             /* @580336A*/
          if (foundLinkServer == NULL)                     /* @F003691A*/
          {                                                /* @F003691A*/
            ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_SERVER_RECEIVE_REQUEST_ERROR_TERM,
                             "A Receive Request error occurred and the session is stopped.  The return code is %s.  The reason code is %s.",
                             rc, rsn);
            loop=FALSE;                                    /* @580336A*/
          }                                                /* @F003691A*/
          else                                             /* @F003691A*/
          {                                                /* @F003691A*/
            if ((last_error_rc != rc) ||                   /* @F003691A*/
                (last_error_rsn != rsn))                   /* @F003691A*/
            {                                              /* @F003691A*/
              ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_SERVER_RECEIVE_REQUEST_ERROR,
                               "A Receive Request error occurred.  The return code is %s.  The reason code is %s.",
                               rc, rsn);
            }                                              /* @F003691A*/
            else                                           /* @F003691A*/
            {                                              /* @F003691A*/
              DEBUG1("A Receive Request error occurred with return code %d and reason code %d.", rc, rsn); /* @F003691A*/
            }                                              /* @F003691A*/

            last_error_rc = rc;                            /* @F003691A*/
            last_error_rsn = rsn;                          /* @F003691A*/
          }                                                /* @F003691A*/
        }                                                   /* @579234A*/

        if (connectivityEstablished == TRUE)               /* @F003691A*/
        {                                                  /* @F003691A*/
          connectivityEstablished = FALSE;                 /* @F003691A*/
          if (loop == TRUE)                                /* @F003691A*/
          {                                                /* @F003691A*/
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_LOST_CONNECTIVITY,
                      "The %s link server lost the connection to the following server instance: %s",
                      regname_nullterm, was_connected_server);/* @F003691A*/
          }                                                /* @F003691A*/
        }                                                  /* @F003691A*/

        if (loop == TRUE)                                  /* @F003691A*/
        {                                                  /* @F003691A*/
          delaysecs = 5;                                   /* @F003691A*/
          EXEC CICS DELAY FOR SECONDS(delaysecs);          /* @F003691A*/
        }                                                  /* @F003691A*/

        continue;                                          /* @F003691A*/
      }

      /* ----------------------------------------------------- */
      /* Make sure to clear the context pointers before each   */
      /* get context call.                          @728847A   */
      /* ----------------------------------------------------- */
      tx_context_p = 0;                                    /* @728847A*/
      corr_context_p = 0;                                  /* @728847A*/
      corr_contextV1_p = 0;                                /* @728847A*/

      /* ----------------------------------------------------- */
      /* Call BBOA1GTX and retrieve the userid and other data  */
      /* (Link tranid, Container info, etc) as well as info    */
      /* about whether the WAS application is in a global tran.*/
      /* ----------------------------------------------------- */
      DEBUG1("Calling Get Context API ... connhdl: %8.8x%8.8x%8.8x",
             *((int*)(&(connhdl[0]))),
             *((int*)(&(connhdl[4]))),
             *((int*)(&(connhdl[8]))));

      BBOA1GTX ( &connhdl
               , &context_p
               , &context_len
               , &rc
               , &rsn
               , &rv
               );

      if (rc == 0)
      {
        DEBUG1("Get context req. completed. connhdl: %8.8x%8.8x%8.8x",
               *((int*)(&(connhdl[0]))),
               *((int*)(&(connhdl[4]))),
               *((int*)(&(connhdl[8]))));
      }
      else
      {
        ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_SERVER_GET_CONTEXT_ERROR,
                         "A Get Context Request error occurred. The return code is %s.  The reason code is %s.",
                         rc, rsn);
        loop=FALSE;
        break;
      }

      cicsdata_p = (BBOAUCIC *) ((int)(ctxtdata_p) +
                                 (int)(ctxtdata_p->auctxcicsoffs));
      secdata_p  = (BBOAUSEC *) ((int)(ctxtdata_p) +
                                 (int)(ctxtdata_p->auctxsecoffs));
      txndata_p  = (BBOAUTXN *) ((int)(ctxtdata_p) +
                                 (int)(ctxtdata_p->auctxtxnoffs));
      if (ctxtdata_p->auctxvaroffs != 0)                       /* @F003691A*/
      {                                                        /* @F003691A*/
        varctxdata_p = (struct bboactx*)((int)(ctxtdata_p) +   /*2@F003691A*/
                                         (int)(ctxtdata_p->auctxvaroffs));
        int numvarctxts = varctxdata_p->actxtnum;              /*2@F003691A*/
        struct bboactxh* cur_ctx_p = (struct bboactxh*)(varctxdata_p + 1);
        while (numvarctxts > 0)                                /* @F003691A*/
        {                                                      /* @F003691A*/
          if (cur_ctx_p->actxhid == BBOATXC_Identifier)        /* @F003691A*/
          {                                                    /* @F003691A*/
            tx_context_p = (struct bboatxc*)cur_ctx_p;         /* @F003691A*/
          }                                                    /* @F003691A*/

          if (cur_ctx_p->actxhid == BBOACORC_Identifier)       /* @711599A*/
          {                                                    /* @711599A*/
            corr_context_p = (struct bboacorctx*)cur_ctx_p;    /* @711599A*/
            corr_contextV1_p =  &(corr_context_p->acorctxcver1); /* @711599A*/
          }                                                    /* @711599A*/

          numvarctxts--;                                       /* @F003691A*/
          cur_ctx_p = (struct bboactxh*)                       /* @F003691A*/
            (((char*)cur_ctx_p) + sizeof(struct bboactxh)      /* @F003691A*/
                                + cur_ctx_p->actxhlen);        /* @F003691A*/
        }                                                      /* @F003691A*/
      }                                                        /* @F003691A*/

      if (processInfo.baseInfo.verbose>=1) {                    /* @578689A*/
        DEBUG2("ctxtdata_p %.8X",ctxtdata_p);
        DEBUG2("cicsdata_p %.8X",cicsdata_p);
        DEBUG2("ctxtdata_p->auctxcicsoffs %.8X", ctxtdata_p->auctxcicsoffs);
        DEBUG2("secdata_p %.8X",secdata_p);
        DEBUG2("ctxtdata_p->auctxsecoffs %.8X", ctxtdata_p->auctxsecoffs);
        DEBUG2("txndata_p %.8X",txndata_p);
        DEBUG2("ctxtdata_p->auctxtxnoffs %.8X", ctxtdata_p->auctxtxnoffs);

        DEBUG1("Userid propagated: %.8s", secdata_p->ausecuserid);
        DEBUG1("Link transaction id propagated: %.4s", cicsdata_p->aucicslnktranid);
        DEBUG1("Link request container id prop.: %.16s", cicsdata_p->aucicslnkreqcontid);
        DEBUG1("Link request container type: %d", cicsdata_p->aucicslnkreqconttype);
        DEBUG1("Link response container id prop.: %.16s", cicsdata_p->aucicslnkrspcontid);
        DEBUG1("Link response container type: %d", cicsdata_p->aucicslnkrspconttype);

        DEBUG1("Link tx/cmt/rb: %d/%d/%d",    /* @F003691A*/
          txndata_p->autxnflags.autxnflg_globaltx,        /* @F003691A*/
          txndata_p->autxnflags.autxnflg_commit,          /* @F003691A*/
          txndata_p->autxnflags.autxnflg_rollback);       /* @F003691A*/

        DEBUG1("Var len ctxt area: %p", varctxdata_p); /* @F003691A*/
        if (varctxdata_p != 0)                                  /* @F003691A*/
        {                                                       /*3@F003691A*/
          DEBUG1("Num var ctxts: %d", varctxdata_p->actxtnum);
          DEBUG1("Extended TX context: %p", tx_context_p);
          DEBUG1("Correlator context: %p", corr_context_p); /* @711599A*/
        }                                                       /* @F003691A*/
      }

      memcpy(&waspropuserid, secdata_p->ausecuserid, 8);
      memcpy(&wasproptranid, cicsdata_p->aucicslnktranid, 4);

      if (strncmp(wasproptranid,"    ",4) == 0) {
        strncpy(ltx,srvrparms.acsrv_linktx,4);
      }
      else {
        strncpy(ltx,wasproptranid,4);
        DEBUG1("Link tranid propagated from WAS");
        /* ----------------------------------------------------------- */
        /* Do not allow a propagated Link tranid when running Link     */
        /* server with REU=Y unless the propagated name is the same as */
        /* the name we would have used anyway.                         */
        /* --------------------------------------------------- @579234A*/
        if ((srvrparms.acsrvflags.acsrvflg_reuse == TRUE) &&/*@PI23547C*/
            (memcmp(wasproptranid,srvrparms.acsrv_linktx,4) != 0)) {
          char ltxNullTerm[5];
          snprintf(ltxNullTerm, sizeof(ltxNullTerm), "%.4s", ltx);
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_REUSE_WITH_TRAN_ID,
                    "The %s link transaction ID caused an error to occur with the link server set to REU=Y.",
                    ltxNullTerm);
          lktran_prop_exc = 1;
        }                                                   /* @579234A*/
      }                                                     /* @579234A*/

      /* Set up parameters for start of Link transaction. */
      strncpy(linkparms.aclnkeye,"BBOACLNK",8);
      linkparms.aclnksiz = sizeof(linkparms);
      linkparms.aclnkver = BBOACLNK_V4;                    /* @PI52665C*/
      memset(&linkparms.aclnkflags,0x00000000,4);
      memset(&linkparms.aclnk_tx_data, 0,                  /* @F003691A*/
             sizeof(linkparms.aclnk_tx_data));             /* @F003691A*/


      /* Copy context flags and data into parms for Link task.*/
      linkparms.aclnkflags.aclnkflg_W2Csec =
        srvrparms.acsrvflags.acsrvflg_W2Csec;            // @LI4798I7-01C
      linkparms.aclnkflags.aclnkflg_reuse =
        srvrparms.acsrvflags.acsrvflg_reuse;                /* @579234A*/
      linkparms.aclnkflags.aclnkflg_tx =
        srvrparms.acsrvflags.acsrvflg_tx;
      linkparms.aclnkflags.aclnkflg_lsync =
        srvrparms.acsrvflags.acsrvflg_lsync;                /* @PM70002A*/
      linkparms.aclnkflags.aclnkflg_globaltx =
        txndata_p->autxnflags.autxnflg_globaltx;
      linkparms.aclnkflags.aclnkflg_channel =               /* @F014448 */
        cicsdata_p->aucicsflags.aucicsflg_channel;          /* @F014448 */
      linkparms.aclnkflags.aclnkflg_container =
        cicsdata_p->aucicsflags.aucicsflg_container;
      linkparms.aclnkflags.aclnkflg_commarea =
        cicsdata_p->aucicsflags.aucicsflg_commarea;
      memcpy(&(linkparms.aclnk_reqcontainerid),
             &(cicsdata_p->aucicslnkreqcontid),16);
      memcpy(&(linkparms.aclnk_reqcontainertype),
             &(cicsdata_p->aucicslnkreqconttype),4);
      memcpy(&(linkparms.aclnk_rspcontainerid),
             &(cicsdata_p->aucicslnkrspcontid),16);
      memcpy(&(linkparms.aclnk_rspcontainertype),
             &(cicsdata_p->aucicslnkrspconttype),4);
      memcpy(linkparms.aclnk_conn_svr, was_connected_server,
             sizeof(linkparms.aclnk_conn_svr));          /* @F003691A*/

      strncpy(linkparms.aclnk_tasknum,
              (processInfo.baseInfo.tskno_for_tsq) + 1,
              sizeof(linkparms.aclnk_tasknum));             /* @PM88133C*/

      /* Copy Reuse count and time parms                     @656134A*/
      linkparms.aclnk_reuc = srvrparms.v2.acsrv_reuc;        /* @656134A*/
      linkparms.aclnk_reut = srvrparms.v2.acsrv_reut;        /* @656134A*/

      /* ----------------------------------------------------------- */
      /* Copy the XID into the link parms.                           */
      /* ----------------------------------------------------------- */
      if ((linkparms.aclnkflags.aclnkflg_tx == 1) &&     /* @F003691A*/
          (txndata_p->autxnflags.autxnflg_globaltx == 1))/* @F003691A*/
      {                                                  /*2@F003691A*/
        /*-----------------------------------------------------------*/
        /* Look for the real XID in the optional context section.    */
        /* If there is no optional context section, we have a local  */
        /* tran and we will "dummy up" an XID.                       */
        /*-----------------------------------------------------------*/
        if (tx_context_p != NULL)                        /* @F003691A*/
        {                                                /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_formatid =    /* @F003691A*/
            tx_context_p->atxcver1.atxcxidfid;           /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_tidlen =      /* @F003691A*/
            tx_context_p->atxcver1.atxcxidgtlen +        /* @F003691A*/
            tx_context_p->atxcver1.atxcxidbqlen;         /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_bquallen =    /* @F003691A*/
            tx_context_p->atxcver1.atxcxidbqlen;         /* @F003691A*/
          memcpy(linkparms.aclnk_tx_data.aclnk_tx_tid,   /* @F003691A*/
                 tx_context_p->atxcver1.atxcxiddata,     /*2@F003691A*/
                 sizeof(linkparms.aclnk_tx_data.aclnk_tx_tid));
        }                                                /* @F003691A*/
        else                                             /* @F003691A*/
        {                                                /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_formatid = 0xC3C2D3E3;
          linkparms.aclnk_tx_data.aclnk_tx_tidlen = 8;   /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_bquallen = 4; /*2@F003691A*/
          memcpy(linkparms.aclnk_tx_data.aclnk_tx_tid, "CBLTCBLT", 8);
        }                                                /* @F003691A*/

        linkparms.aclnkflags.aclnkflg_globaltx = 1;      /* @F003691A*/

        /*-----------------------------------------------------------*/
        /* See if this request is for recovery work.  Recovery work  */
        /* deals with shunted transactions.                          */
        /*-----------------------------------------------------------*/
        linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_rec =
          txndata_p->autxnflags.autxnflg_rec;            /*2@F003691A*/
        linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_tidlist =
          txndata_p->autxnflags.autxnflg_tidlist;        /*2@F003691A*/
        linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_commit =
          txndata_p->autxnflags.autxnflg_commit;         /*2@F003691A*/
        linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_backout =
          txndata_p->autxnflags.autxnflg_rollback;       /*2@F003691A*/
      }                                                  /* @F003691A*/

      /* ----------------------------------------------------------- */
      /* Copy the correlator data from WAS.                 @711599A */
      /* ----------------------------------------------------------- */
      if (corr_context_p != NULL) {                       /* @711599A*/
        memcpy(&(linkparms.aclnk_corr_id),
               &(corr_contextV1_p->acorctxAdapterIdentifier),
               sizeof(linkparms.aclnk_corr_id));          /* @711599A*/
        memcpy(&(linkparms.aclnk_corr_data1),
               &(corr_contextV1_p->acorctxCorelatorData1),
               sizeof(linkparms.aclnk_corr_data1));       /* @711599A*/
        memcpy(&(linkparms.aclnk_corr_data2),
               &(corr_contextV1_p->acorctxCorelatorData2),
               sizeof(linkparms.aclnk_corr_data2));       /* @711599A*/
        memcpy(&(linkparms.aclnk_corr_data3),
               &(corr_contextV1_p->acorctxCorelatorData3),
               sizeof(linkparms.aclnk_corr_data3));       /* @711599A*/
        if (processInfo.baseInfo.verbose>=1) {            /* @711599A*/
          DEBUG1("WAS correlator id: %s", linkparms.aclnk_corr_id);
          DEBUG1("WAS correlator data 1: %s", linkparms.aclnk_corr_data1);
          DEBUG1("WAS correlator data 2: %s", linkparms.aclnk_corr_data2);
          DEBUG1("Instance data: %8.8x%8.8x%8.8x%8.8x",
                 *((int*)(&(linkparms.aclnk_corr_data2[9]))),
                 *((int*)(&(linkparms.aclnk_corr_data2[13]))),
                 *((int*)(&(linkparms.aclnk_corr_data2[17]))),
                 *((int*)(&(linkparms.aclnk_corr_data2[21]))));
          DEBUG1("WAS correlator data 3: %s", linkparms.aclnk_corr_data3);
        }
      }                                                   /* @711599A*/

      /* ----------------------------------------------------------- */
      /* Pass over the original service name specified on the BBOC   */
      /* Start_Srvr                                                  */
      /* --------------------------------------------------- @579234A*/
      memset(linkparms.aclnk_bboc_svcname,0x00,
             sizeof(linkparms.aclnk_bboc_svcname));       /* @579234A*/
      strncpy(linkparms.aclnk_bboc_svcname,
              srvrparms.acsrv_svcname,
              8); /* pad with NULLs */                    /* @579234A*/

      if (processInfo.baseInfo.verbose>=1) {              /* @578689A*/
        if (linkparms.aclnkflags.aclnkflg_globaltx == TRUE) {
          DEBUG1("WAS application is IN a Global Transaction.");
        } else {
          DEBUG1("WAS application is NOT IN a Global Transaction.");
        }
      }

      if (linkparms.aclnkflags.aclnkflg_W2Csec == TRUE) {       // @LI4798I7-01C
        if (strncmp(waspropuserid,"        ",8) != 0)           /* @574294M*/
          strncpy(userid,waspropuserid,8);                      /* @574294M*/
        DEBUG1("Security propagation ON. Userid requested: %.8s", userid);
      }
      else
        DEBUG1("Security propagation NOT ON. Userid defaults %.8s", userid);

      if (processInfo.baseInfo.verbose>=1) {                  /* @578689A*/
        if (linkparms.aclnkflags.aclnkflg_container == TRUE) {
          DEBUG1("Container requested. Request container id: %.16s",
            linkparms.aclnk_reqcontainerid);
          DEBUG1("Request container type: %d", linkparms.aclnk_reqcontainertype);
          DEBUG1("Response container id: %.16s", linkparms.aclnk_rspcontainerid);
          DEBUG1("Response container type: %d", linkparms.aclnk_rspcontainertype);
        }
        else if (linkparms.aclnkflags.aclnkflg_channel == TRUE ) {
          DEBUG1("Channel requested. Channel id: %.16s", linkparms.aclnk_reqcontainerid);
          DEBUG1("Channel type: %d", linkparms.aclnk_reqcontainertype);
        }
        else
          DEBUG1("Container interface NOT requested. Use COMMAREA.");
      }

      linkparms.aclnk_reqdatalen = reqlen;
      strncpy(linkparms.aclnk_regname,srvrparms.acsrv_regname,12);
      memcpy(&(linkparms.aclnk_connhdl),&connhdl,sizeof(connhdl));
      strncpy(linkparms.aclnk_servname,"        ",8);
      memcpy(&(linkparms.aclnk_servname),&reqsrvname,reqsrvnamel);
      linkparms.aclnk_tracelvl = processInfo.baseInfo.verbose;
      strncpy(linkparms.aclnk_tracetdq,(char*)processInfo.baseInfo.wrtqname,4);

      /* ------------------------------------------------------------------- */
      /* Process Remote Transaction ID request- forward to Link invocation   */
      /* task.                                                               */
      /* ---------------------------------------------------------- @PI52665A*/
      if (srvrparms.acsrvflags.acsrvflg_rtxp == 1) {             /* @PI52665A*/
        DEBUG1("Link server processing RTXP=Y request.");
        linkparms.aclnkflags.aclnkflg_rtxp =
            srvrparms.acsrvflags.acsrvflg_rtxp;                  /* @PI52665A*/
        memcpy(linkparms.aclnk_rtx, srvrparms.v3.acsrv_rtx,
               sizeof(srvrparms.v3.acsrv_rtx));                  /* @PI52665A*/
        memcpy(linkparms.aclnk_rtxsys, srvrparms.v3.acsrv_rtxsys,
               sizeof(srvrparms.v3.acsrv_rtxsys));               /* @PI52665A*/
        DEBUG2("Link server propagating RTX: %.4s", linkparms.aclnk_rtx);
        DEBUG2("Link server propagating RTXSYS: %.4s", linkparms.aclnk_rtxsys);
      } else {
          memset(linkparms.aclnk_rtx, ' ',
                 sizeof(linkparms.aclnk_rtx));                       /* @PI52665A*/
          memset(linkparms.aclnk_rtxsys, ' ',
                 sizeof(linkparms.aclnk_rtxsys));                    /* @PI52665A*/
      }

      linkparmsl = sizeof(linkparms);

      if (processInfo.baseInfo.verbose>=1) {                   /* @578689A*/
        DEBUG1("Starting WAS adapters Link task ...");
        DEBUG1("Link transaction id: %.4s",ltx);
        DEBUG1("linkparms.aclnk_connhdl: %8.8x%8.8x%8.8x",
               *((int*)(&(linkparms.aclnk_connhdl[0]))),
               *((int*)(&(linkparms.aclnk_connhdl[4]))),
               *((int*)(&(linkparms.aclnk_connhdl[8]))));
      }

      /* ----------------------------------------------------------- */
      /* Generate a unique request ID.  If we don't do this, then    */
      /* CICS will use an internal TSQ to pass our parameters on the */
      /* START request, and if internal TSQs are defined as          */
      /* recoverable, the START will not run until the link server   */
      /* stops.                                                      */
      /* ------------------------------------------------- @PI19688A */
      generateUniqueReqid(startCounter_p, reqid);

      /* Start adapters Link task.                            */
      if (lktran_prop_exc == 0) {                     /* @579234A*/
        DEBUG1("Starting REQID %s\n", reqid);            /* @PI19688A*/

        if (srvrparms.acsrvflags.acsrvflg_W2Csec == TRUE) {   // @LI4798I7-01C
          DEBUG1("Starting with USERID %.8s",userid);

          EXEC CICS START TRANSID(ltx) FROM(&linkparms) LENGTH(linkparmsl)
            USERID(userid) RESP(respcode) REQID(reqid);  /* @PI19688C*/
        }
        else {
          DEBUG1("Starting without USERID parm.");
          EXEC CICS START TRANSID(ltx) FROM(&linkparms) LENGTH(linkparmsl)
            RESP(respcode) REQID(reqid);                 /* @PI19688C*/
        }
      }

      if ((lktran_prop_exc == 0) &&
          (respcode == dfhresp(NORMAL))) {
        if (processInfo.baseInfo.verbose>=1) {            /* @578689A*/
          sprintf(screenmsg,"Start server completed successfully.");
          DEBUG1(screenmsg);
        }
      }
      else {
        /* ----------------------------------------------------- */
        /* If we are running with REU=Y and a request arrives    */
        /* that wants to propagate a Link tranid, we need to     */
        /* reject it as we are unable to honor it.               */
        /* --------------------------------------------- @579234A*/
        char ltxNullTerm[5];
        snprintf(ltxNullTerm, sizeof(ltxNullTerm), "%.4s", ltx);
        if (lktran_prop_exc == 1) {                   /* @579234A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_REUSE_WITH_TRAN_ID,
                    "The %s link transaction ID caused an error to occur with the link server set to REU=Y.",
                    ltxNullTerm);
          strncpy(screenmsg, (PROCESS_INFO_PTR)->messageBuffer, sizeof(screenmsg));
        }
        else {
          char respstr[9];
          char resp2str[9];
          snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
          snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_START_TRANSID_ERROR,
                    "An error occurred for the %s CICS transaction identifier, CICS START TRANSID, for the following response codes: RESP: %s RESP2: %s.",
                    ltxNullTerm, respstr, resp2str);
          strncpy(screenmsg, (PROCESS_INFO_PTR)->messageBuffer, sizeof(screenmsg));
        }

        /* ----------------------------------------------------- */
        /* Set 16 byte request area- this is most surely too     */
        /* small and will get an error/rc indicating this.  But  */
        /* we don't care - we want to just get the connection    */
        /* into the proper state so we can send back a response  */
        /* with the exception info.                              */
        /* ----------------------------------------------------- */
        reqdata_p = &reqbuff;
        reqlen    = 16;

        /* ----------------------------------------------------- */
        /* Get the first 16 bytes of message data into our buff. */
        /* ----------------------------------------------------- */
        DEBUG1("Calling Get Data API ...");

        BBOA1GET ( &connhdl
                 , &reqdata_p
                 , &reqlen
                 , &rc
                 , &rsn
                 , &rv
                 );

        if ((rc == 0) || (rc == 8)) {
          DEBUG1("Get data req. completed. connhdl: %8.8x%8.8x%8.8x",
                 *((int*)(&(connhdl[0]))),
                 *((int*)(&(connhdl[4]))),
                 *((int*)(&(connhdl[8]))));
        }
        else {
          ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_SERVER_GET_DATA_ERROR,
                           "A Get Data Request error occurred.  The return code is %s.  The reason code is %s.",
                           rc, rsn);
          loop=FALSE;
          break;
        }

        /* ----------------------------------------------------- */
        /* Send exception response message back to WAS.          */
        /* This also releases the current connection, placing it */
        /* back in the available pool.                           */
        /* ----------------------------------------------------- */
        DEBUG1("Sending Exception Response ...");
        exc_rc = send_exc_resp(&processInfo, connhdl,
                               &screenmsg, sizeof(screenmsg));

        /* If the send exception failed, or the release failed,  */
        /* bad things are happening - terminate this server task.*/
        if (exc_rc != 0) {
          char regnameNullTerm[13];
          snprintf(regnameNullTerm, sizeof(regnameNullTerm), "%.12s", srvrparms.acsrv_regname);
          loop=FALSE;
          memset(bbocdata,' ',sizeof(bbocdata));                          /* @577009A*/
          sprintf(bbocdata,"BBOC STP RGN=%.12s",srvrparms.acsrv_regname); /* @577009C*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_STOP_ERROR,
                    "The link server has stopped due to a problem with the registration name:  Regname %s",
                    regnameNullTerm);

          EXEC CICS LINK PROGRAM("BBOACNTL")
                    COMMAREA(bbocdata) LENGTH(bbocdatal) RESP(respcode);  /* @577009C*/

          EXEC CICS ABEND ABCODE("BBOX") CANCEL;
        }

      }
    }

    /*---------------------------------------------------------------*/
    /* Only call unregister here if the link server is still present */
    /* in the link server list.  This implies the control            */
    /* transaction did not try to call unregister, and we're exiting */
    /* because of some other problem.  We're trying to prevent a     */
    /* situation where the control transaction is calling unregister */
    /* force, while we're calling unregister.                        */
    /*---------------------------------------------------------------*/
    struct bboa_link_srvr_status_node* urgLinkServer =   /* @PI24358A*/
      findLinkServer((char*)srvrparms.acsrv_regname,
                     (char*)processInfo.baseInfo.tskno_for_tsq+1); /*@PI53321*/
    if ((urgLinkServer != NULL) && (urgLinkServer->urg_pend == 0))
    {
      DEBUG1("Call Unregister API ...");

      /* Call UNREGISTER API.                                @577009A*/
      BBOA1URG (  &srvrparms.acsrv_regname
                , &unregflags
                , &urgrc
                , &urgrsn
                );                                        /* @577009A*/
      DEBUG1("Unregister Return Code: %d rsn Code: %d",
             urgrc, urgrsn);                              /* @577009A*/

      if (urgrc == 4) {                                   /* @577009A*/
        delaysecs = 5;                                    /* @584331C*/

        /* Sleep 5 seconds and issue UNREGISTER FORCE.       @577009A*/
        EXEC CICS DELAY FOR SECONDS(delaysecs);           /* @577009A*/

        /* Select UNREGISTER FORCE.                          @577009A*/
        unregflags.unreg_flag_force = 1;                  /* @577009A*/

        /* Call UNREGISTER API.                              @577009A*/
        BBOA1URG (  &srvrparms.acsrv_regname
                  , &unregflags
                  , &urgrc
                  , &urgrsn
                  );                                      /* @577009A*/
        DEBUG2("Unregister Force Return Code: %d rsn Code: %d",
               urgrc, urgrsn);                            /* @577009A*/

      }                                                   /* @577009A*/
      /* Remove any link servers with the above registration from the link server list @PI53321 */
      removeLinkServer((char*)srvrparms.acsrv_regname,
                       (char*)processInfo.baseInfo.tskno_for_tsq+1);  /* @PI53321C*/
    }

    if (processInfo.baseInfo.verbose>=1) {                             /* @578689A*/
      char msg1[64], msg2[64];
      timedif = ( ((double) clock()) / CLOCKS_PER_SEC) - time1;

      time(&finish);
      newtime = localtime(&finish);

      DEBUG1("Return Code: %d rsn Code: %d", rc, rsn);
      DEBUG1("Elapsed time: %f seconds", difftime(finish,start));
      DEBUG1("Elapsed CPU time: %f seconds", timedif);
      DEBUG1("<----------  ADAPTERS SERVER TASK END    ---  %.20s  ----->",
             asctime(newtime));
    }

    closeWolaMessageCatalog(&((PROCESS_INFO_PTR)->baseInfo));

    return(0);
  }
