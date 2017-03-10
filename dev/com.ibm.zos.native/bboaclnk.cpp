/*----------------------------------------------------------------------
 *
 *  Module Name:  BBOACLNK.CPP
 *
 *  Descriptive Name: Optimized Adapters CICS Link-to Program task (BBO#)
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
 *  Function:  Provide EC LINK to target program as passed in the
 *             service name.  This task is started by BBO$ and runs
 *             under the default transaction id of BBO#.
 *             It provides either a COMMAREA or CONTAINER for passing
 *             parms to the target program.
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
 *   BBOA8700 - BBOA8702: Debug messages (0/1/2)
 *   BBOA8703 - BBOA8899: Error/Warning/Info messages
 *
 *  Change Activity:
 *   $LI4799        , H28W700, 20081230, JTM : Initial Implementation
 *   $574703        , H28W700, 20090220, JTM : Capture abends in LINK to
 *                                             program and send exception
 *                                             response message.
 *   $578077        , H28W700, 20090303, FGOL: Fix incorrect flag name
 *   $578689        , H28W700, 20090306, JTM : Performance, reduce tracing
 *                                             overhead in main path.
 *                                             Cleanup messages.
 *   $579005        , H28W700, 20090316, PDFG: Exception in EBCDIC
 *   $579234        , H28W700, 20090310, JTM : Support for reusing BBO# tasks
 *                                             when REU=Y is requested.
 *   $580336        , H28W700, 20090324, JTM : With REU=Y, write out call
 *                                             count when stop server occurs.
 *   $584331        , H28W700, 20090408, JTM : Fix regression from 578689-
 *                                             not showing CICS task # in
 *                                             messages when TRC=0.
 *   $584680        , H28W700, 20090409, JTM : Bug with 32K message response.
 *   $F003691       , H28W700, 20090709, PDFG: Pass XID in global TX
 *   $F003691       , H28W700, 20100118, PDFG: 2PC First pass
 *   $F003691       , H28W700, 20100122, PDFG: Detect 1PC RB
 *   $F003691-20607 , H28W700, 20100202, PDFG: Fail server if unreg while
 *                                             in-doubt
 *   $F003691-20604 , H28W700, 20100224, PDFG: Recovery task
 *   $F003691-20605 , H28W700, 20100225, PDFG: Return TIDs to WAS
 *   $F003691-20606 , H28W700, 20100309, PDFG: Recovery commit/backout
 *   $642208        , H28W700, 20100325, PDFG: Always use TS queue
 *   $650153        , H28W700, 20100422, PDFG: 2PC support on 4.1 only
 *   $656134        , H28W700, 20100608, JTM : Add reuse count and time parms.
 *   $633440        , HBBO800, 20101014, PDFG: BBOQ message truncation fix
 *   $711599        , HBBO800, 20110722, JTM : Support CICS TS 4.2
 *   $F014448       , H28W800, 20120828, CRP : WOLA Multi-container support
 *   $739358        , H28W800, 20120919, CRP : Fix WOLA Multi-container outbound path
 *   $PM70002       , HBBO800, 20120730, JTM:  Add SYNCONRETURN option to
 *                                             support TXN=N link server that
 *                                             DPLs to programs that EC SYNC.
 *   $PM70002       , H28W800, 20120815, PDFG: Restructure for service
 *   $PM88131       , H28W800, 20130508, PDFG: Recovery UOW in correct state
 *   $PM88209       , HBBO800, 20130603, PDFG: Support CICS TS 5.1
 *   $PM90865       , H28W855, 20130925, JTM : change findLinkServer parms
 *   $PI23547       , H28W855, 20140905, PDFG: Propagated tran id check
 *   $PI27022       , H28W855, 20141016, MJA:  Add WAS build level message
 *
 *   $PI24358       , H28W855, 20140904, PDFG: Deal with pending unregister
 *   $PI52665       , H2W8555, 20161108, JTM:  Add support for passing
 *                                             TransID/SYSID on EC LINK in
 *                                             link invocation task.
 *
 *----------------------------------------------------------------------*/
#pragma runopts(stack(8192,,,))                            /* @F003691A*/

 #define TRUE 1
 #define FALSE 0

 #include <stdio.h>
 #include <cics.h>
 #include <time.h>
 #include <string.h>
 #include <stdarg.h>
 #include <stdlib.h>
 #include <stdbool.h>
#ifndef _BBOT_LIMITS_H_INCLUDED
#include <limits.h>            /* INT_MAX                            */
#define _BBOT_LIMITS_H_INCLUDED
#endif /* _BBOT_LIMITS_H_INCLUDED */                     /* @WS14564A*/
 #include "include/bboaapi.h"
 #include "include/server_wola_cics_link_server.h"

#ifndef _BBOA_PRIVATE_BBOACSVL_H_INCLUDE                  /* @F003691A*/
#include "include/bboacsvl.h"
#define _BBOA_PRIVATE_BBOACSVL_H_INCLUDE                  /* @F003691A*/
#endif                                                    /* @F003691A*/


#ifndef _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#include <bboaclst.h>
#define _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#endif

 #define OLA_MIN(a,b) (((a) < (b)) ? (a) : (b))           /* @F003691A*/

 /* ----------------------------------------------------------------- */
 /* PLX Functions                                                     */
 /* ----------------------------------------------------------------- */
 extern "OS" {
   typedef int (*osEntryPoint_t)(void* parm1, void* parm2,       
                                 void* parm3, void* parm4,
                                 void* parm5, void* parm6,
                                 void* parm7, void* parm8);
 }

 extern "OS" int BBOAC2PC(void* parm1, void* parm2,       /*4@F003691A*/
                          void* parm3 = NULL, void* parm4 = NULL,
                          void* parm5 = NULL, void* parm6 = NULL,
                          void* parm7 = NULL, void* parm8 = NULL);

 extern "OS" int BBOAC242(void* parm1, void* parm2,       /*4@711599A*/
                          void* parm3 = NULL, void* parm4 = NULL,
                          void* parm5 = NULL, void* parm6 = NULL,
                          void* parm7 = NULL, void* parm8 = NULL);

 extern "OS" int BBOAC251(void* parm1, void* parm2,       /*4@PM88209A*/
                          void* parm3 = NULL, void* parm4 = NULL,
                          void* parm5 = NULL, void* parm6 = NULL,
                          void* parm7 = NULL, void* parm8 = NULL);

 extern "OS" int BBOAC252(void* parm1, void* parm2,
                          void* parm3 = NULL, void* parm4 = NULL,
                          void* parm5 = NULL, void* parm6 = NULL,
                          void* parm7 = NULL, void* parm8 = NULL);

 /* Functions must match bboac2pc.plx */
 enum BBOAC2PC_Functions { BBOAC2PC_FuncCodeImport = 1,   /* @F003691A*/
                           BBOAC2PC_FuncCodeCommitOnePhase,/*@F003691A*/
                           BBOAC2PC_FuncCodePrepare,      /* @F003691A*/
                           BBOAC2PC_FuncCodeCommit,       /* @F003691A*/
                           BBOAC2PC_FuncCodeBackout,      /* @F003691A*/
                           BBOAC2PC_Max = INT_MAX } ;     /* @F003691A*/

 enum BBOAC2PC_PrepareVote { PrepareVoteOK = 0,           /* @F003691A*/
                             PrepareVoteBackout,          /* @F003691A*/
                             PrepareVoteReadOnly,         /* @F003691A*/
                             PrepareVoteHeuristicMixed,   /* @F003691A*/
                             PrepareVoteMax = INT_MAX } ; /* @F003691A*/

 enum BBOAC2PC_RC { BBOAC2PC_Return_OK = 0,               /* @F003691A*/
                    BBOAC2PC_Return_InternalError = 8,    /* @F003691A*/
                    BBOAC2PC_Return_CICSError = 12,       /* @F003691A*/
                    BBOAC2PC_Return_MAX = INT_MAX } ;     /* @F003691A*/

 #define CommitRsnCode_BackedOut 4                        /* @F003691A*/

 /* ----------------------------------------------------------------- */
 /* Transaction states                                                */
 /* ----------------------------------------------------------------- */
 enum TransactionState { NO_TRANSACTION = 0,              /* @F003691A*/
                         ACTIVE_TRANSACTION,              /* @F003691A*/
                         PREPARED_TRANSACTION,            /* @F003691A*/
                         STATE_MAX = INT_MAX } ;          /* @F003691A*/

 /* ----------------------------------------------------------------- */
 /* Version of the TID which we log with CICS.                        */
 /* ----------------------------------------------------------------- */
#pragma pack(1)
typedef struct                                            /* @F003691A*/
 {                                                        /* @F003691A*/
   unsigned char gtrid_len;                               /* @F003691A*/
   unsigned char bqual_len;                               /* @F003691A*/
   unsigned char tid[126];                                /* @F003691A*/
 } cics_tid;                                              /* @F003691A*/
#pragma pack(reset)

 /* ----------------------------------------------------------------- */
 /* Node for a linked list of XIDs.                                   */
 /* ----------------------------------------------------------------- */
 struct xid_list_node                                     /* @F003691A*/
 {                                                        /* @F003691A*/
   xid_list_node* next_p;                                 /* @F003691A*/
   struct bboaxid xid;                                    /* @F003691A*/
 };                                                       /* @F003691A*/
 typedef struct xid_list_node XidListNode;                /* @F003691A*/

 /* ----------------------------------------------------------------- */
 /* Constants                                                         */
 /* ----------------------------------------------------------------- */
 const char* C_HOSTNAME_PREFIX = "WOLA:";                 /* @F003691A*/
 const char* C_HOSTNAME_SEPARATOR = ":";                  /* @F003691A*/

 const int   OTSFORMATID = 0xC3C20186;                /* 'CB' hex 390 */
 const int   OTSLTFORMATID = 0xC3C2D3E3;              /* 'CBLT'       */

 /* ----------------------------------------------------------------- */
 /* strnlen                                                           */
 /* ----------------------------------------------------------------- */
#ifndef strnlen
 size_t strnlen(const char* s, size_t maxlen)             /* @F003691A*/
 {                                                        /* @F003691A*/
   /* note that size_t is unsigned */
   size_t len;                                            /* @F003691A*/
   const char* cur_p = s;                                 /* @F003691A*/

   for (len = 0; ((len < maxlen) && (*cur_p != NULL)); len++) /* @F003691A*/
   {                                                      /* @F003691A*/
     cur_p++;                                             /* @F003691A*/
   }                                                      /* @F003691A*/

   return len;                                            /* @F003691A*/
 }                                                        /* @F003691A*/
#endif

 /* ----------------------------------------------------------------- */
 /* debug trace macros                                                */
 /* ----------------------------------------------------------------- */
#define DEBUG0(...) DEBUG0_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#define DEBUG1(...) DEBUG1_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#define DEBUG2(...) DEBUG2_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#undef PROCESS_INFO_PTR

#define ASCIIPAGE 1208
#define EBCDICPAGE 37

 /* ----------------------------------------------------------------- */
 /* Information about this task.                                      */
 /* ----------------------------------------------------------------- */
 struct cicsLinkTaskInfo {
   struct cicsLinkServerProcessInfo baseInfo; /* Common information */
   char connhdl[12]; /* Connection handle used by this link task */
   char tsqname[9]; /* Name of the TSQ used to store parameters */
   char _available[3]; /* Available for use */
   char messageBuffer[512]; /* Message buffer */
   struct { /* Filled in if VERBOSE >= 1 */
     time_t start; /* Starting time */
     double time1; /* Starting time */
   } verboseData;
 };

#define PROCESS_INFO_PTR info_p
 /* ----------------------------------------------------------------- */
 /* Write a message to the BBOQ queue.                                */
 /* ----------------------------------------------------------------- */
 void ISSUE_MSG(struct cicsLinkTaskInfo* info_p, int messageID, char* defaultMessage,  ...) {
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
 void ISSUE_MSG_RC_RSN(struct cicsLinkTaskInfo* info_p, int messageID, char* defaultMessage,  int rc, int rsn) {
   char rcstr[9];
   char rsnstr[9];
   snprintf(rcstr, sizeof(rcstr), "%d", rc);
   snprintf(rsnstr, sizeof(rsnstr), "%d", rsn);
   ISSUE_MSG(info_p, messageID, defaultMessage, rcstr, rsnstr);
 }
#undef PROCESS_INFO_PTR

 /* ----------------------------------------------------------------- */
 /* Convert binary data into a null terminated hex string that can be */
 /* traced.  The returned character string should be deleted with     */
 /* a call to free() after it is printed.                             */
 /* ----------------------------------------------------------------- */
 char* binaryToHexString(void* data_p, int length)        /* @F003691A*/
 {                                                        /* @F003691A*/
   char* string_p = NULL;                                 /* @F003691A*/
   char* cur_pos_string_p = NULL;                         /* @F003691A*/
   int paddedLength = 0;                                  /* @F003691A*/
   int words = 0;                                         /* @F003691A*/
   int* cur_pos_data_p = (int*)data_p;                    /* @F003691A*/
   int x = 0;                                             /* @F003691A*/

   words = (length + 3) / 4;                              /* @F003691A*/
   paddedLength = words * 4;                              /* @F003691A*/

   string_p = (char*)malloc((paddedLength * 2) + 1);      /* @F003691A*/
   if (string_p != NULL)                                  /* @F003691A*/
   {                                                      /* @F003691A*/
     cur_pos_string_p = string_p;                         /* @F003691A*/

     for (x = 0; x < words; x++)                          /* @F003691A*/
     {                                                    /*2@F003691A*/
       sprintf(cur_pos_string_p, "%.8x", *(cur_pos_data_p));
       cur_pos_string_p += 8;                             /* @F003691A*/
       cur_pos_data_p += 1;                               /* @F003691A*/
     }                                                    /* @F003691A*/
   }                                                      /* @F003691A*/
   else                                                   /* @F003691A*/
   {                                                      /* @F003691A*/
     perror("BBOACLNK");                                  /* @F003691A*/
   }                                                      /* @F003691A*/

   return string_p;                                       /* @F003691A*/
 }                                                        /* @F003691A*/

 /* ----------------------------------------------------------------- */
 /* Prints a list of all UOWs currently active.                       */
 /* ----------------------------------------------------------------- */
#define PROCESS_INFO_PTR processInfo_p
 void printTransactions(struct cicsLinkTaskInfo* processInfo_p)
 {                                                        /* @F003691A*/
   int respcode = 0;                                      /* @F003691A*/
   char uow[16];                                          /* @F003691A*/
   char otstid[128];                                      /* @F003691A*/
   int age = 0;                                           /* @F003691A*/
   char transid[5];                                       /* @F003691A*/
   int state = 0;                                         /* @F003691A*/
   int waitcause = 0;                                     /* @F003691A*/
   int waitstate = 0;                                     /* @F003691A*/

   DEBUG2("Printing UOWs");                               /* @F003691A*/

   EXEC CICS INQUIRE UOW START RESP(respcode);            /* @F003691A*/

   while (respcode == dfhresp(NORMAL))                    /* @F003691A*/
   {                                                      /*4@F003691A*/
     EXEC CICS INQUIRE UOW(uow) AGE(age) OTSTID(otstid) TRANSID(transid)
       UOWSTATE(state) WAITCAUSE(waitcause) WAITSTATE(waitstate)
       NEXT RESP(respcode);

     if (respcode == dfhresp(NORMAL))                     /* @F003691A*/
     {                                                    /*3@F003691A*/
       char* uowString = binaryToHexString((void*)uow, sizeof(uow));
       char* otstidString = binaryToHexString((void*)otstid, sizeof(otstid));
       transid[4] = 0;                                    /* @F003691A*/

       DEBUG2("UOW %s AGE %i TRANSID %s STATE %i ",
              uowString, age, transid, state);            /*2@F003691A*/

       DEBUG2(" WAITCAUSE %i WAITSTATE %i", waitcause, waitstate);
       for (int i = 0; i < 8; i++)                        /*2@F003691A*/
       {                                                  /* @F003691A*/
         if (i == 0)                                      /* @F003691A*/
         {                                                /*2@F003691A*/
           DEBUG2(" OTS TID %.8s %.8s %.8s %.8s",
                  otstidString + (i * 32) + 0,            /* @F003691A*/
                  otstidString + (i * 32) + 8,            /* @F003691A*/
                  otstidString + (i * 32) + 16,           /* @F003691A*/
                  otstidString + (i * 32) + 24);          /* @F003691A*/
         }                                                /* @F003691A*/
         else                                             /* @F003691A*/
         {                                                /*2@F003691A*/
           DEBUG2("         %.8s %.8s %.8s %.8s",
                  otstidString + (i * 32) + 0,            /* @F003691A*/
                  otstidString + (i * 32) + 8,            /* @F003691A*/
                  otstidString + (i * 32) + 16,           /* @F003691A*/
                  otstidString + (i * 32) + 24);          /* @F003691A*/
         }                                                /* @F003691A*/
       }                                                  /* @F003691A*/

       free(otstidString);                                /* @F003691A*/
       free(uowString);                                   /* @F003691A*/
     }                                                    /* @F003691A*/
     else                                                 /* @F003691A*/
     {                                                    /* @F003691A*/
       DEBUG2("Terminating trace, RESPCODE = %x (hex)",
              respcode);                                  /*2@F003691A*/
     }                                                    /* @F003691A*/
   }                                                      /* @F003691A*/

   EXEC CICS INQUIRE UOW END;                             /* @F003691A*/
 }                                                        /* @F003691A*/
#undef PROCESS_INFO_PTR


 /* ----------------------------------------------------------------- */
 /* Converts blanks in an input string to nulls.                      */
 /* --------------------------------------------------------@F003691A */
 void convertBlanksToNulls(char* src, int len)            /* @F003691A*/
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
 /* Gets a list of XIDs belonging to OLA on this CICS isntance.       */
 /* ----------------------------------------------------------------- */
#define PROCESS_INFO_PTR processInfo_p
 XidListNode* getXids(struct cicsLinkTaskInfo* processInfo_p,
                      char* our_hostname)                 /* @F003691A*/
 {
   int respcode_getmain = 0;
   int respcode_uowlink = 0;
   int respcode_uow = 0;
   char uow[16];
   int state = 0;
   int waitstate = 0;
   char host[255];
   int role;
   int our_hostname_len;
   char link[4];

   XidListNode* xid_head_p = NULL;

   union
   {
     char otstid[128];
     cics_tid massaged_tid;
   };

   DEBUG2("Iterating UOWLINKs");
   our_hostname_len = strlen(our_hostname);

   /* --------------------------------------------------------------- */
   /* Iterate over all UOW LINKs.                                     */
   /* --------------------------------------------------------------- */
   EXEC CICS INQUIRE UOWLINK START RESP(respcode_uowlink);

   while (respcode_uowlink == dfhresp(NORMAL))
   {
     EXEC CICS INQUIRE UOWLINK(link) HOST(host) ROLE(role) UOW(uow)
       NEXT RESP(respcode_uowlink);

     if (respcode_uowlink == dfhresp(NORMAL))
     {
       DEBUG2("New host ptr %p", host);
       convertBlanksToNulls(&(host[0]), sizeof(host));
       DEBUG2("UOWLINK %.8X UOW %.8X%.8X Role %i Host %.10s",
              *(int*)(&(link[0])),
              *(int*)(&(uow[0])),
              *(int*)(&(uow[4])),
              role,
              host);
       DEBUG2("Host %.40s", host);

       /* ----------------------------------------------------------- */
       /* We only care about coordinator links.  Even though we would */
       /* technically be a subordinate to WebSphere, we are the       */
       /* coordinator for CICS.                                       */
       /* ----------------------------------------------------------- */
       if (dfhvalue(COORDINATOR) == role)
       {
         /* --------------------------------------------------------- */
         /* Filter further by links which we created.  We check this  */
         /* by looking at the "hostname" and comparing it to our own. */
         /* --------------------------------------------------------- */
         DEBUG2("host ptr %p", host);
         DEBUG2("Host: %.80s", host);
         DEBUG2("strlen = %d", strlen(host));
         DEBUG2("strnlen = %d", strnlen(host, sizeof(host)));
         DEBUG2("strlen = %d",  strlen(host));
         DEBUG2("memcmp = %d",
                memcmp(host, our_hostname, our_hostname_len));

         if ((strnlen(host, sizeof(host)) == our_hostname_len) &&
             (memcmp(host, our_hostname, our_hostname_len) == 0))
         {
           /* ------------------------------------------------------- */
           /* Grab the UOW details for this matching UOW link.        */
           /* ------------------------------------------------------- */
           EXEC CICS INQUIRE UOW(uow) OTSTID(otstid) UOWSTATE(state)
             WAITSTATE(waitstate) RESP(respcode_uow);

           if (processInfo_p->baseInfo.verbose >= 2)
           {
             DEBUG2("STATE %i WAITSTATE %i", state, waitstate);

             char* otstidString =
               binaryToHexString((void*)otstid, sizeof(otstid));

             for (int i = 0; i < 8; i++)
             {
               if (i == 0)
               {
                 DEBUG2(" OTS TID %.8s %.8s %.8s %.8s",
                        otstidString + (i * 32) + 0,
                        otstidString + (i * 32) + 8,
                        otstidString + (i * 32) + 16,
                        otstidString + (i * 32) + 24);
               }
               else
               {
                 DEBUG2("         %.8s %.8s %.8s %.8s",
                        otstidString + (i * 32) + 0,
                        otstidString + (i * 32) + 8,
                        otstidString + (i * 32) + 16,
                        otstidString + (i * 32) + 24);
               }
             }
           }

           if (respcode_uow == dfhresp(NORMAL))
           {
             /* ----------------------------------------------------- */
             /* Make sure the UOW is in-doubt.                        */
             /* ----------------------------------------------------- */
             if (dfhvalue(INDOUBT) == state)
             {
               XidListNode* cur_node_p = NULL;
               int storlen = sizeof(XidListNode);

               EXEC CICS GETMAIN SET(cur_node_p) FLENGTH(storlen)
                 RESP(respcode_getmain);

               if (respcode_getmain == dfhresp(NORMAL))
               {
                 cur_node_p->xid.formatid =
                   OTSFORMATID;
                 cur_node_p->xid.gtridlen = massaged_tid.gtrid_len;
                 cur_node_p->xid.bquallen = massaged_tid.bqual_len;
                 memcpy(cur_node_p->xid.tid, massaged_tid.tid,
                        massaged_tid.gtrid_len + massaged_tid.bqual_len);
                 cur_node_p->next_p = xid_head_p;
                 xid_head_p = cur_node_p;
               }
               else
               {
                 char uowString[17];
                 char respstr[9];
                 snprintf(uowString, sizeof(uowString), "%.8X%.8X", *((int*)(&(uow[0]))), *((int*)(&(uow[4]))));
                 snprintf(respstr, sizeof(respstr), "%d", respcode_getmain);
                 ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_XIDS_NO_STORAGE2,
                           "The system cannot recover the %s unit of work (UOW) due to the following error from the EC GETMAIN command: %s",
                           uowString, respstr);
               }
             }
           }
           else
           {
             char uowString[17];
             char respstr[9];
             snprintf(uowString, sizeof(uowString), "%.8X%.8X", *((int*)(&(uow[0]))), *((int*)(&(uow[4]))));
             snprintf(respstr, sizeof(respstr), "%d", respcode_uow);
             ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_INQUIRE_UOW_ERROR,
                       "The system cannot recover the %s unit of work (UOW) due to the following error from the EC INQUIRE UOW command: %s",
                       uowString, respstr);
           }
         }
       }
     }
     else if (dfhresp(END) != respcode_uowlink)
     {
       char respstr[9];
       snprintf(respstr, sizeof(respstr), "%d", respcode_uowlink);
       ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_UOWLINK_ITERATION_ERROR2,
                 "The recovery of units of work (UOWs) ended prematurely due to the following error from the EC INQUIRE UOWLINK NEXT command: %s",
                 respstr);
     }
   }

   EXEC CICS INQUIRE UOWLINK END;

   return xid_head_p;
 }

 /* ----------------------------------------------------------------- */
 /* Print the exit time information.                                  */
 /* ----------------------------------------------------------------- */
 void print_exit_time(struct cicsLinkTaskInfo* processInfo_p,
                      int rc, int rsn) {
   time_t finish;
   double timedif;
   struct tm *newtime;
   char msg1[64];
   char msg2[64];

   if (processInfo_p->baseInfo.verbose >= 1) {
     timedif = (((double) clock()) / CLOCKS_PER_SEC) -
       (processInfo_p->verboseData.time1);

     time(&finish);
     newtime = localtime(&finish);

     DEBUG1("Return Code: %d rsn Code: %d", rc, rsn);
     DEBUG1("Elapsed time: %f seconds",
            difftime(finish, processInfo_p->verboseData.start));
     DEBUG1("Elapsed CPU time: %f seconds", timedif);
     DEBUG1("<++++++++++  ADAPTERS CICS LINK TASK END    +++  %.20s  ++>", asctime(newtime));
   }
 }

 /* ----------------------------------------------------------------- */
 /* release_exit() : Release the current connection, issue EC SYNC    */
 /* ROLLBACK and exit.                                                */
 /* ----------------------------------------------------------------- */
 int release_exit(struct cicsLinkTaskInfo* processInfo_p, int exitcode, TransactionState tx_state) { /* @F003691C*/
   int rc=0;
   int rsn=0;
   int respcode;
   char connhdl[12];

   /* ----------------------------------------------------- */
   /* Release the connection back to the pool.              */
   /* ----------------------------------------------------- */
   DEBUG1("Releasing connection back to pool.");

   memcpy(connhdl, processInfo_p->connhdl, sizeof(connhdl));
   BBOA1CNR ( &connhdl
            , &rc
            , &rsn
            );

   if (rc == 0) {
     DEBUG1("Release connection back to pool completed.");
   }
   else {
     ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_RELEASE_CONNECTION_ERROR,
                      "The Connection Release API could not return the connection to the pool. The return code is %s, and the reason code is %s.",
                      rc, rsn);
   }

   /* --------------------------------------------------------------- */
   /* If the transaction is prepared, we have to abend the task to    */
   /* shunt the transaction.                                          */
   /* --------------------------------------------------------------- */
   if (tx_state == PREPARED_TRANSACTION)                  /* @F003691A*/
   {                                                      /* @F003691A*/
     EXEC CICS ABEND ABCODE("BBOX") CANCEL;               /* @F003691A*/
   }                                                      /* @F003691A*/
   else                                                   /* @F003691A*/
   {                                                      /* @F003691A*/
     EXEC CICS SYNCPOINT ROLLBACK;
   }                                                      /* @F003691A*/

   if (processInfo_p->baseInfo.verbose >= 1) {
     print_exit_time(processInfo_p, rc, rsn);
   }

   EXEC CICS DELETEQ TS QUEUE(processInfo_p->tsqname)
     RESP(respcode);                                      /* @F003691A*/

   closeWolaMessageCatalog(&((PROCESS_INFO_PTR)->baseInfo));

   exit(exitcode);

   // Should not get here
   return exitcode;                                       /* @F003691A*/
 }

 /* ----------------------------------------------------------------- */
 /* send_exc_resp() : Send exception data back to requestor           */
 /* ----------------------------------------------------------------- */
 int send_exc_resp (struct cicsLinkTaskInfo* processInfo_p, void * excdata_p, int exclen)
  {
    int rc  = 0;
    int rsn = 0;
    char connhdl[12];

    /* ----------------------------------------------------- */
    /* Send the exception data back                          */
    /* ----------------------------------------------------- */
    memcpy(connhdl, processInfo_p->connhdl, sizeof(connhdl));
    BBOA1SRX ( &connhdl
             , &excdata_p                                  /* @F003691C*/
             , &exclen                                     /* @F003691C*/
             , &rc
             , &rsn
            );

    if (rc == 0) {
      DEBUG1("Send Response completed.");
    }
    else {
      ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_SEND_RESPONSE_EXCEPTION_ERROR,
                       "A Send Response Exception occurred. The return code is %s, and the reason code is %s.",
                       rc, rsn);
    }

    return(rc);
  }

 /* ----------------------------------------------------------------- */
 /* Converts a state CVDA value to a string.                          */
 /* ----------------------------------------------------------------- */
 char* getTxStateString(int state)                        /* @F003691A*/
 {
   if (state == dfhvalue(BACKOUT)) return "BACKOUT";
   if (state == dfhvalue(COMMIT)) return "COMMIT";
   if (state == dfhvalue(FORCE)) return "FORCE";
   if (state == dfhvalue(HEURBACKOUT)) return "HEURBACKOUT";
   if (state == dfhvalue(HEURCOMMIT)) return "HEURCOMMIT";
   if (state == dfhvalue(INDOUBT)) return "INDOUBT";
   if (state == dfhvalue(INFLIGHT)) return "INFLIGHT";
   return "UNKNOWN";
 }

 /* ----------------------------------------------------------------- */
 /* Converts a waitstate CVDA value to a string.                      */
 /* ----------------------------------------------------------------- */
 char* getTxWaitStateString(int waitstate)                /* @F003691A*/
 {
   if (waitstate == dfhvalue(ACTIVE)) return "ACTIVE";
   if (waitstate == dfhvalue(SHUNTED)) return "SHUNTED";
   if (waitstate == dfhvalue(WAITING)) return "WAITING";
   return "UNKNOWN";
 }

 /* ----------------------------------------------------------------- */
 /* Resolves an in-doubt shunted UOW.                                 */
 /* ----------------------------------------------------------------- */
 void resolve_indoubt(struct cicsLinkTaskInfo* processInfo_p,
                      char* hostname, int format_id,      /* @F003691A*/
                      int gtrid_len, int bqual_len,
                      char* tid, unsigned char commit,
                      struct bboatrsp* tx_response_p)
 {
   unsigned char found_uow = FALSE;
   char uow[16];
   char connhdl[12];
   int respcode = 0;
   int hostname_len = strlen(hostname);
   int state = 0;                                         /* @PM88131M*/

   memcpy(connhdl, processInfo_p->connhdl, sizeof(connhdl));
   memset(tx_response_p, 0, sizeof(struct bboatrsp));
   memcpy(tx_response_p->atrspeye, BBOATRSP_EYE,
          sizeof(tx_response_p->atrspeye));
   tx_response_p->atrspver = BBOATRSP_VER_1;

   /* --------------------------------------------------------------- */
   /* Only attempt to resolve global XA transactions.                 */
   /* --------------------------------------------------------------- */
   if (format_id != OTSLTFORMATID)
   {
     union
     {
       char otstid[128];
       cics_tid massaged_tid;
     };

     int waitstate = 0;

     EXEC CICS INQUIRE UOW START RESP(respcode);

     while (respcode == dfhresp(NORMAL))
     {
       EXEC CICS INQUIRE UOW(uow) OTSTID(otstid) UOWSTATE(state)
         WAITSTATE(waitstate) NEXT RESP(respcode);

       /* ----------------------------------------------------------- */
       /* If we got the next UOW, see if the TID matches.             */
       /* ----------------------------------------------------------- */
       if (respcode == dfhresp(NORMAL))
       {
         if ((gtrid_len == massaged_tid.gtrid_len) &&
             (bqual_len == massaged_tid.bqual_len) &&
             (memcmp(tid, massaged_tid.tid, gtrid_len + bqual_len) == 0))
         {
           /* ------------------------------------------------------- */
           /* Make sure the UOW state is correct.  It should be       */
           /* in-doubt and shunted, or it should be in the correct    */
           /* state for our outcome (commit or backout).              */
           /* ------------------------------------------------------- */
           if (((state == dfhvalue(INDOUBT)) &&           /* @PM88131C*/
                (waitstate == dfhvalue(SHUNTED))) ||      /* @PM88131C*/
               (state == dfhvalue(COMMIT)) ||             /* @PM88131C*/
               (state == dfhvalue(BACKOUT)))              /* @PM88131C*/
           {
             /* ----------------------------------------------------- */
             /* We have found our UOW.  Stop searching.               */
             /* ----------------------------------------------------- */
             found_uow = TRUE;
             respcode = dfhresp(END);
           }
           else
           {
             ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_RECOVERY_UOW_STATE_ERROR,
                       "The system cannot resolve the state of the %s recovery unit of work (UOW) or the state of the %s UOW waitstate.",
                       getTxStateString(state),
                       getTxWaitStateString(waitstate));
             strncpy(tx_response_p->atrsp_msg, (PROCESS_INFO_PTR)->messageBuffer, sizeof(tx_response_p->atrsp_msg) - 1); // Last byte remains null
             tx_response_p->atrsp_msglen = strlen(tx_response_p->atrsp_msg);

             char uowString[17];
             snprintf(uowString, sizeof(uowString), "%.8X%.8X",
                      (*(int*)(&(uow[0]))),
                      (*(int*)(&(uow[4]))));              /* @PM88131A*/
             ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_RECOVERY_UOW_STATE_ERROR_DETAILS,
                       "The CICS unit of work ID is %s.", uowString);

             char* gtridString = binaryToHexString(
               massaged_tid.tid, gtrid_len);              /* @PM88131A*/

             if (gtridString != NULL)                     /* @PM88131A*/
             {                                            /* @PM88131A*/
               int charsToPrint = gtrid_len * 2;          /* @PM88131A*/
               char gtridStringNullTerm[65];
               snprintf(gtridStringNullTerm, sizeof(gtridStringNullTerm), "%.*s",
                        OLA_MIN(charsToPrint, 64),
                        gtridString);                     /* @PM88131A*/
               ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_RECOVERY_UOW_STATE_ERROR_DETAILS2,
                         "XID GTRID = %s", gtridStringNullTerm);

               if (charsToPrint > 64)                     /* @PM88131A*/
               {                                          /* @PM88131A*/
                 charsToPrint = charsToPrint - 64;        /* @PM88131A*/
                 snprintf(gtridStringNullTerm, sizeof(gtridStringNullTerm), "%.*s",
                          OLA_MIN(charsToPrint, 64),
                          gtridString + 64);              /* @PM88131A*/
                 ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_RECOVERY_UOW_STATE_ERROR_DETAILS2,
                           "XID GTRID = %s", gtridStringNullTerm);
               }                                          /* @PM88131A*/

               free(gtridString);                         /* @PM88131A*/
             }                                            /* @PM88131A*/

             send_exc_resp(processInfo_p, tx_response_p,
                           sizeof(struct bboatrsp));
             release_exit(processInfo_p, 16, NO_TRANSACTION);
           }
         }
       }
       else if (respcode != dfhresp(END))
       {
         char respstr[9];
         snprintf(respstr, sizeof(respstr), "%d", respcode);
         ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_UOW_ITERATION_ERROR,
                   "The system cannot find the recovery unit of work (UOW) in the %s unit of work list (UOWLIST).",
                   respstr);
         strncpy(tx_response_p->atrsp_msg, (PROCESS_INFO_PTR)->messageBuffer, sizeof(tx_response_p->atrsp_msg) - 1); // Last byte remains NULL
         tx_response_p->atrsp_msglen = strlen(tx_response_p->atrsp_msg);
         send_exc_resp(processInfo_p, tx_response_p, sizeof(struct bboatrsp));
         release_exit(processInfo_p, 16, NO_TRANSACTION);
       }
     }

     EXEC CICS INQUIRE UOW END;
   }

   /* --------------------------------------------------------------- */
   /* If we found the UOW, resolve it.                                */
   /* --------------------------------------------------------------- */
   if (found_uow == TRUE)
   {
     if (state == dfhvalue(INDOUBT))                      /* @PM88131A*/
     {
       if (commit == TRUE)
       {
         EXEC CICS SET UOW(uow) COMMIT RESP(respcode);
       }
       else
       {
         EXEC CICS SET UOW(uow) BACKOUT RESP(respcode);
       }

       /* ----------------------------------------------------------- */
       /* If there was a problem during resolution, tell the caller.  */
       /* ----------------------------------------------------------- */
       if (respcode != dfhresp(NORMAL))
       {
         char* action = (commit == TRUE) ? "commit" : "backout";
         char respstr[9];
         snprintf(respstr, sizeof(respstr), "%d", respcode);
         ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_SET_UOW_RESOLUTION_ERROR,
                   "The system cannot %s the recovery unit of work (UOW) because of the following error from the EC SET UOW command: %s",
                   action, respstr);
         strncpy(tx_response_p->atrsp_msg, (PROCESS_INFO_PTR)->messageBuffer, sizeof(tx_response_p->atrsp_msg)-1); // Last byte remains NULL
         tx_response_p->atrsp_msglen = strlen(tx_response_p->atrsp_msg);
         send_exc_resp(processInfo_p, tx_response_p,
                       sizeof(struct bboatrsp));
         release_exit(processInfo_p, 16, NO_TRANSACTION);
       }
     }
     else if (((state == dfhvalue(COMMIT)) && (commit == FALSE)) ||
              ((state == dfhvalue(BACKOUT)) && (commit == TRUE)))
     {
       /* ----------------------------------------------------------- */
       /* If the outcome was not what we expected it to be, tell the  */
       /* caller that we have a heuristic outcome.                    */
       /* ----------------------------------------------------------- */
       tx_response_p->atrspflg_heur = 1;                  /* @PM88131A*/
     }

     /* ------------------------------------------------------------- */
     /* After resolving the in-doubt, the UOW will be in a            */
     /* heuristic state.  We need to delete the UOWLINK to finish the */
     /* resolution.  Since the UOW is technically resolved at this    */
     /* point, we will not send an exception back to the caller if    */
     /* anything goes wrong deleting the UOWLINK.                     */
     /* ------------------------------------------------------------- */
     EXEC CICS INQUIRE UOWLINK START RESP(respcode);

     if (respcode == dfhresp(NORMAL))
     {
       char link[4];
       char host[255];
       int role;
       char uow_from_link[16];
       unsigned char found_link = FALSE;
       int respcode2 = 0;

       while (respcode == dfhresp(NORMAL))
       {
         EXEC CICS INQUIRE UOWLINK(link) HOST(host) ROLE(role)
           UOW(uow_from_link) NEXT RESP(respcode);

         if (respcode == dfhresp(NORMAL))
         {
           convertBlanksToNulls(&(host[0]), sizeof(host));

           /* ------------------------------------------------------- */
           /* If the UOW in the LINK matches the UOW we resolved,     */
           /* and this is the coordinator LINK, and the LINK has our  */
           /* host name, then we have found the LINK that we want to  */
           /* delete.                                                 */
           /* ------------------------------------------------------- */
           if (memcmp(uow, uow_from_link, sizeof(uow)) == 0)
           {
             if (dfhvalue(COORDINATOR) == role)
             {
               if ((strnlen(host, sizeof(host)) == hostname_len) &&
                   (memcmp(host, hostname, hostname_len) == 0))
               {
                 respcode = dfhresp(END);
                 found_link = TRUE;
               }
             }
           }
         }
         else if (respcode != dfhresp(END))
         {
           char respstr[9];
           snprintf(respstr, sizeof(respstr), "%d", respcode);
           ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_UOWLINK_ITERATION_ERROR,
                     "Deleting the unit of work link (UOWLINK) failed during the following iteration: %s",
                     respstr);
         }
       }

       EXEC CICS INQUIRE UOWLINK END;

       /* ----------------------------------------------------------- */
       /* If we found our UOWLINK during the iteration, delete it.    */
       /* ----------------------------------------------------------- */
       if (found_link == TRUE)
       {
         EXEC CICS SET UOWLINK(link) DELETE RESP(respcode)
           RESP2(respcode2);

         if (respcode != dfhresp(NORMAL))
         {
           char uowLinkString[9];
           char uowString[18];
           char respstr[9];
           snprintf(uowLinkString, sizeof(uowLinkString), "%.8X",
                    *((int*)(&(link[0]))));
           snprintf(uowString, sizeof(uowString), "%.8X %.8X",
                    *((int*)(&(uow[0]))),
                    *((int*)(&(uow[4]))));
           snprintf(respstr, sizeof(respstr), "%d", respcode);
           ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_DELETE_UOWLINK_ERROR,
                     "The system cannot delete the %s unit of work link (UOWLINK) in the %s UOW. The response code is %s.",
                     uowLinkString, uowString, respstr);
         }
       }
       else
       {
         if (state == dfhvalue(INDOUBT))                  /* @PM88131A*/
         {                                                /* @PM88131A*/
           char uowString[18];
           snprintf(uowString, sizeof(uowString), "%.8X %.8X",
                    *((int*)(&(uow[0]))),
                    *((int*)(&(uow[4]))));
           ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_UOWLINK_NOT_FOUND,
                     "The system cannot find the unit of work link (UOWLINK) to delete in the following unit of work (UOW): %s ",
                     uowString);
         }                                                /* @PM88131A*/
       }
     }
     else
     {
       char uowString[30];
       snprintf(uowString, sizeof(uowString), "%.8X %.8X (%d)",
                *((int*)(&(uow[0]))),
                *((int*)(&(uow[4]))),
                respcode);
       ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_UOWLINK_ITERATION_ERROR,
                 "Deleting the unit of work link (UOWLINK) failed during the following iteration: %s",
                 uowString);
     }
   }
   else
   {
     /* ------------------------------------------------------------- */
     /* If we did not find our UOW, tell the caller.  This may be an  */
     /* expected case.                                                */
     /* ------------------------------------------------------------- */
     tx_response_p->atrspflg_nota = 1;
   }

   tx_response_p->atrspflg_complete = 1;
 }
#undef PROCESS_INFO_PTR

 /* ----------------------------------------------------------------- */
 /* main() : Main procedure                                           */
 /* ----------------------------------------------------------------- */
  main ()
  {
    BBOACLNK linkparms;
    short int linkparmsl=0;
    long int i=0;
    long int respcode=0;
    int rc=0;
    int rsn=0;
    int rv=0;
    int  reqsrvnamel;
    char linkpgmname[8];
    char reqsrvname[256];
    char msgbuff4k[4096];
    void * reqdata_p = &msgbuff4k;
    short  reqlens;
    int  reqlen;
    void * rspdata_p = &msgbuff4k;
    int  rsplen;
    int  async=0;
    int loop=TRUE;
    int link_count=0;
    char global_tran_uow_id[8] = {0x00, 0x00, 0x00, 0x00, /* @F003691A*/
                                  0x00, 0x00, 0x00, 0x00};/* @F003691A*/
    TransactionState tx_state = NO_TRANSACTION;           /* @F003691A*/
    int cur_formatid = 0;                                 /* @F003691A*/
    int cur_bqual_len = 0;                                /* @F003691A*/
    int cur_tid_len = 0;                                  /* @F003691A*/
    char cur_tid[128];                                    /* @F003691A*/
    char c2pc_dyn_area[768];                              /* @F003691A*/
    struct bboatrsp* tx_response_p =                      /* @F003691A*/
      (struct bboatrsp*)msgbuff4k;                        /* @F003691A*/
    osEntryPoint_t tran_func = NULL;                      /* @PI24444A*/

    char hostname_string[64];                             /* @F003691A*/
    int hostname_string_len = 0;                          /* @F003691A*/
    char cics_ts_version[6];                               /* @650153A*/
    int cics_version;                                      /* @650153A*/
    int cics_release;                                      /* @650153A*/
    int cics_mod_lvl;                                      /* @650153A*/
    int sscanf_rc;                                         /* @650153A*/
    int reuc = 0;                                          /* @656134A*/
    int reut = 0;                                          /* @656134A*/

    struct tm *newtime;
    time_t reut_start, reut_currtime;                      /* @656134C*/
    clock_t clock(void);
    double timedif, reutf;                                 /* @656134C*/
    char msgdata[256];

    struct cicsLinkTaskInfo processInfo;
#define PROCESS_INFO_PTR &processInfo

    memset(msgdata,' ',sizeof(msgdata));
    linkparmsl = sizeof(linkparms);

    /* Get addressability to the EIB first.                  */
    EXEC CICS ADDRESS EIB(dfheiptr);
    initializeCicsLinkServerProcessInfo(&(processInfo.baseInfo));
    snprintf(processInfo.tsqname, sizeof(processInfo.tsqname), "BBO%.5s",
             (processInfo.baseInfo.tskno_for_tsq) + 3);

    /* Retrieve the tasknum and register name passed by      */
    /* the BBOC START SERVER request.                        */
    EXEC CICS RETRIEVE INTO(&linkparms) LENGTH(linkparmsl);
    memcpy(processInfo.baseInfo.wrtqname, linkparms.aclnk_tracetdq,
           sizeof(processInfo.baseInfo.wrtqname));
    processInfo.baseInfo.verbose = linkparms.aclnk_tracelvl;

    /* Check the version of the received link task parms     */
    if (linkparms.aclnkver != BBOACLNK_V4)                /* @PI52665C*/
    {                                                     /* @F003691C*/
      ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_PARM_VERSION_MISMATCH,
        "The system expected the %d version of the link server, but instead found the %h link version.",
        BBOACLNK_V4, linkparms.aclnkver);  /* @PI52665C*/
      rc=16;                                              /* @F003691C*/
      release_exit(&processInfo, rc, tx_state);           /* @F003691C*/
    }                                                     /* @F003691C*/

    if (linkparms.aclnkflags.aclnkflg_reuse == TRUE) {     /* @656134A*/
      reuc = linkparms.aclnk_reuc;                         /* @656134A*/
      reut = linkparms.aclnk_reut;                         /* @656134A*/
      if (reut > 0) {                                      /* @656134A*/
        time(&reut_start);                                 /* @656134A*/
      }                                                    /* @656134A*/
    }                                                      /* @656134A*/

    /* Big debug trace section */
    if (processInfo.baseInfo.verbose>=1) {                 /* @578689A*/
      char msg1[64];
      char msg2[64];
      char userid[8];
      EXEC CICS ASSIGN USERID(userid);                     /* @578689M*/

      time(&(processInfo.verboseData.start));              /* @578689M*/
      newtime = localtime(&(processInfo.verboseData.start));/* 4@578689M*/
      processInfo.verboseData.time1 = (double) clock(); /* Get initial time */
      processInfo.verboseData.time1 =
        processInfo.verboseData.time1 / CLOCKS_PER_SEC;  /*    in seconds    */

      DEBUG1("<++++++++++  ADAPTERS CICS LINK TASK START  +++  %.20s  ++>", asctime(newtime));

      /* Write message with WAS build level      @PI27022*/
      DEBUG1("Build Level =  %s \n",LIBERTY_BUILD_LABEL);             /*@PI27022*/

      if (processInfo.baseInfo.verbose>=2) {
        DEBUG2("Start parm cb eyec: %.8s ", linkparms.aclnkeye);
      }

      DEBUG1("Initiated by server task number: %.7s",
            linkparms.aclnk_tasknum);
      DEBUG1("Connected to WAS server %.27s", linkparms.aclnk_conn_svr);
      DEBUG1("Initiated with register name: %.12s", linkparms.aclnk_regname);
      DEBUG1("Initiated for service name: %.8s", linkparms.aclnk_servname);
      DEBUG1("Initiated with connection handle: %8.8x%8.8x%8.8x",
             *((int*)(&(linkparms.aclnk_connhdl[0]))),
             *((int*)(&(linkparms.aclnk_connhdl[4]))),
             *((int*)(&(linkparms.aclnk_connhdl[8]))));
      DEBUG1("Initiated with trace level setting: %d ", linkparms.aclnk_tracelvl);
      DEBUG1("Initiated with trace TDQ name: %.4s", linkparms.aclnk_tracetdq);
      DEBUG2("Input message data length: %d", linkparms.aclnk_reqdatalen);
      DEBUG1("Initiated under userid: %.8s", userid);
      if (linkparms.aclnkflags.aclnkflg_reuse == TRUE) {
        DEBUG1("Initiated with REU=Y requested."); /* @579234A*/
        DEBUG1("Initiated with REUC: %d",linkparms.aclnk_reuc);  /* @656134A*/
        DEBUG1("Initiated with REUT: %d",linkparms.aclnk_reut);  /* @656134A*/
      }
      if (linkparms.aclnkflags.aclnkflg_lsync == TRUE) {
        DEBUG1("Initiated with LSYNC=Y requested."); /* @PM70002A*/
      }
      if (linkparms.aclnkflags.aclnkflg_container == TRUE) {
        DEBUG1("Data to be passed in a CONTAINER: %.16s ", linkparms.aclnk_reqcontainerid);
        DEBUG1("Request Container Type : %d ", linkparms.aclnk_reqcontainertype);
        DEBUG1("Get response using CONTAINER: %.16s ", linkparms.aclnk_rspcontainerid);
        DEBUG1("Response Container Type : %d ", linkparms.aclnk_rspcontainertype);
      }
      else if (linkparms.aclnkflags.aclnkflg_channel == TRUE) {
        DEBUG1("Data to be passed in a CHANNEL: %.16s ", linkparms.aclnk_reqcontainerid);
        DEBUG1("Channel Type : %d", linkparms.aclnk_reqcontainertype);
      }
      else
        DEBUG1("Data to be passed in a COMMAREA");

      if (linkparms.aclnkflags.aclnkflg_globaltx == 1)    /* @F003691A*/
      {                                                   /* @F003691A*/
        char* xid_tid_string_p = binaryToHexString(       /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_tid,           /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_tidlen);       /* @F003691A*/

        if (xid_tid_string_p != NULL)                     /* @F003691A*/
        {                                                 /*2@F003691A*/
          int gtridLen = linkparms.aclnk_tx_data.aclnk_tx_tidlen -
            linkparms.aclnk_tx_data.aclnk_tx_bquallen;    /*2@F003691A*/
          int charsToPrint = 0;                           /* @F003691A*/

          DEBUG1("XID FormatID %.8x : TidLen %.8x : BqualLen %.8x",
                 linkparms.aclnk_tx_data.aclnk_tx_formatid,/*@F003691A*/
                 linkparms.aclnk_tx_data.aclnk_tx_tidlen, /* @F003691A*/
                 linkparms.aclnk_tx_data.aclnk_tx_bquallen);/*@F003691A*/

          charsToPrint = gtridLen * 2;                    /* @F003691A*/

          DEBUG1("XID GTRID: %.*s",                       /* @F003691A*/
                 OLA_MIN(charsToPrint, 64),               /* @F003691A*/
                 xid_tid_string_p);                       /* @F003691A*/

          if (charsToPrint > 64)                          /* @F003691A*/
          {                                               /* @F003691A*/
            DEBUG1("XID GTRID: %.*s",                     /* @F003691A*/
                   charsToPrint - 64,                     /* @F003691A*/
                   xid_tid_string_p + 64);                /* @F003691A*/
          }                                               /* @F003691A*/

          charsToPrint = linkparms.aclnk_tx_data.aclnk_tx_bquallen * 2; /* @F003691A*/

          DEBUG1("XID BQUAL: %.*s",                       /* @F003691A*/
                 OLA_MIN(charsToPrint, 64),               /* @F003691A*/
                 xid_tid_string_p + (gtridLen * 2));      /* @F003691A*/

          if (charsToPrint > 64)                          /* @F003691A*/
          {                                               /* @F003691A*/
            DEBUG1("XID BQUAL: %.*s",                     /* @F003691A*/
                   charsToPrint - 64,                     /* @F003691A*/
                   xid_tid_string_p + (gtridLen * 2) + 64);/*@F003691A*/
          }                                               /* @F003691A*/

          free(xid_tid_string_p);                         /* @F003691A*/
        }                                                 /* @F003691A*/

        char* rrs_urid_string_p = binaryToHexString(      /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_urid,          /* @F003691A*/
          sizeof(linkparms.aclnk_tx_data.aclnk_tx_urid)); /* @F003691A*/

        if (rrs_urid_string_p != NULL)                    /* @F003691A*/
        {                                                 /*2@F003691A*/
          DEBUG1("Caller RRS URID: %s ", rrs_urid_string_p);
          free(rrs_urid_string_p);                        /* @F003691A*/
        }                                                 /* @F003691A*/

        char* rrs_urtok_string_p = binaryToHexString(     /* @F003691A*/
          linkparms.aclnk_tx_data.aclnk_tx_urtok,         /* @F003691A*/
          sizeof(linkparms.aclnk_tx_data.aclnk_tx_urtok));/* @F003691A*/
        if (rrs_urtok_string_p != NULL)                   /* @F003691A*/
        {                                                 /*2@F003691A*/
          DEBUG1("Caller RRS UR Token: %s ", rrs_urtok_string_p);
          free(rrs_urtok_string_p);                       /* @F003691A*/
        }                                                 /* @F003691A*/

        DEBUG1("TX Flags: %.8x ",                         /*2@F003691A*/
               *((int*)&linkparms.aclnk_tx_data.aclnk_tx_flags));
      }                                                   /* @F003691A*/
    }                                                     /* @F003691A*/

    /* ----------------------------------------------------------------- */
    /* Save the connection handle                                        */
    /* ----------------------------------------------------------------- */
    memcpy(processInfo.connhdl,&(linkparms.aclnk_connhdl),   /* @F003691A*/
           sizeof(processInfo.connhdl));                     /* @F003691A*/

    /* ----------------------------------------------------------------- */
    /* Build a hostname string to log with the CICS transaction manager  */
    /* in the event we start a transaction.                              */
    /*   WOLA:svrname:regname                                            */
    /*    4  1  27   1  12     = 45 bytes max (plus null term)           */
    /* ----------------------------------------------------------------- */
    memset(hostname_string, 0x00, sizeof(hostname_string));  /* @F003691A*/
    strcat(hostname_string, C_HOSTNAME_PREFIX);              /*2@F003691A*/
    strcat(hostname_string, (const char*)(&(linkparms.aclnk_conn_svr[0])));
    strcat(hostname_string, C_HOSTNAME_SEPARATOR);           /* @F003691A*/
    strncat(hostname_string, linkparms.aclnk_regname,        /* @F003691A*/
            sizeof(linkparms.aclnk_regname));                /*2@F003691A*/
    convertBlanksToNulls(&(hostname_string[0]), sizeof(hostname_string));
    hostname_string_len = strlen(hostname_string);           /* @F003691A*/

    /* ----------------------------------------------------------------- */
    /* Get the CICS version.  We use this to limit functionality to      */
    /* specific CICS versions.                                           */
    /* ----------------------------------------------------------------- */
    EXEC CICS INQUIRE SYSTEM CICSTSLEVEL(cics_ts_version)     /* @650153A*/
      RESP(respcode);                                         /* @650153A*/
    if (respcode == dfhresp(NORMAL)) {                        /* @650153A*/
      DEBUG2("Using CICS version %.6s", cics_ts_version);     /* @650153A*/
    }                                                         /* @650153A*/
    else {                                                    /* @650153A*/
      char respstr[9];
      snprintf(respstr, sizeof(respstr), "%d", respcode);
      ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_INQUIRE_SYSTEM_LEVEL_ERROR,
                "An error occurred when the EC INQUIRE SYSTEM CICSTSLEVEL command was invoked. The error code is %s.",
                respstr);
      rc=16;                                                  /* @650153A*/
      release_exit(&processInfo, rc, tx_state);               /* @650153A*/
    }                                                         /* @650153A*/

    sscanf_rc = sscanf(cics_ts_version, "%2d%2d%2d",          /* @650153A*/
                       &cics_version,                         /* @650153A*/
                       &cics_release,                         /* @650153A*/
                       &cics_mod_lvl);                        /* @650153A*/

    if (sscanf_rc != 3)                                       /* @650153A*/
    {                                                         /* @650153A*/
      char cicsVerNullTerm[7];
      snprintf(cicsVerNullTerm, sizeof(cicsVerNullTerm), "%.6s", cics_ts_version);
      ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_PARSE_ERROR_CICS_VERSION,
                "The system cannot parse CICS Transaction Server version %s.",
                cicsVerNullTerm);
      rc=16;                                                  /* @650153A*/
      release_exit(&processInfo, rc, tx_state);               /* @650153A*/
    }                                                         /* @650153A*/

    /* Figure out which function to call for tran support.      @PI24444A*/
    if (cics_version == 4) {                                 /* @PI24444A*/
      if (cics_release == 1) {
        tran_func = BBOAC2PC;
      } else {
        tran_func = BBOAC242;
      }
    } else if (cics_version == 5) {
      if (cics_release == 1) {
        tran_func = BBOAC251;
      } else {
        tran_func = BBOAC252;
      }
    } else { /* Not sure... just set something. */
      tran_func = BBOAC2PC;
    }

    /* ------------------------------------------------------- */
    /* We loop and do GET/LINK to target/SRP/RCS when we       */
    /* are running with REU=Y. When REU=N, we call CNR and     */
    /* return- instead of RCS. With that, the connection goes  */
    /* back to the available pool and this task ends.          */
    /* ----------------------------------------------- @579234A*/
    while (loop == 1) {                              /*@579234A*/

      /* ----------------------------------------------------- */
      /* Acquire a buffer to copy the message data into.       */
      /* We keep a 4k buffer in our local data so we won't     */
      /* need to call getmain for smaller messages.            */
      /* ----------------------------------------------------- */
      if (linkparms.aclnk_reqdatalen <= 4096) {
        DEBUG2("Requested %d bytes. Starting addr: %X",
          linkparms.aclnk_reqdatalen,reqdata_p);
        reqdata_p = &msgbuff4k;                          /*@584680A*/
      }
      else {
        EXEC CICS GETMAIN SET(reqdata_p)
                          FLENGTH(linkparms.aclnk_reqdatalen)
                          RESP(respcode);

        if (respcode == dfhresp(NORMAL)) {
          DEBUG2("Requested %d bytes. Starting addr: %X",
            linkparms.aclnk_reqdatalen,reqdata_p);
        }
        else {
          char sizestr[9];
          snprintf(sizestr, sizeof(sizestr), "%d", linkparms.aclnk_reqdatalen);
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GETMAIN_ERROR2,
                    "The EXEC CICS GETMAIN command could not allocate storage for a request buffer of %s bytes.",
                    sizestr);
          ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_GETMAIN_ERROR,
                               "The EXEC CICS GETMAIN command could not allocate storage. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                               dfheiptr->eibresp,dfheiptr->eibresp2);
          rc=16;
          release_exit(&processInfo, rc, tx_state);            /* @F003691A*/
        }
      }

      reqlen = linkparms.aclnk_reqdatalen;

      /* ----------------------------------------------------- */
      /* Get the message data into our buffer.                 */
      /* ----------------------------------------------------- */
      DEBUG1("Calling Get Data API with connhdl: %8.8x%8.8x%8.8x",
             *((int*)(&(processInfo.connhdl[0]))),
             *((int*)(&(processInfo.connhdl[4]))),
             *((int*)(&(processInfo.connhdl[8]))));

      BBOA1GET ( &processInfo.connhdl
               , &reqdata_p
               , &reqlen
               , &rc
               , &rsn
               , &rv
               );

      if (rc == 0) {
        DEBUG1("Get data request completed");
      }
      else {
        ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_GET_DATA_ERROR,
                         "The Get Data Request API could not remove the message from the adapter message cache. The return code is %s, and the reason code is %s.",
                         rc, rsn);
        release_exit(&processInfo, rc, tx_state);          /* @F003691A*/
      }

      /* ----------------------------------------------------- */
      /* We need to save the linkparms in a TSQ now before     */
      /* we issue the EC LINK. This will give our TRUE end     */
      /* task routine the ability to retrieve this data        */
      /* and send an exception response back to WAS.           */
      /* ----------------------------------------------------- */
      if (link_count == 0)                                /* @F003691A*/
      {                                                   /* @F003691A*/
        EXEC CICS WRITEQ TS QUEUE(processInfo.tsqname) MAIN
          FROM(&linkparms) LENGTH(linkparmsl) RESP(respcode);
      }                                                   /* @F003691A*/
      else                                                /* @F003691A*/
      {                                                   /* @F003691A*/
        short first_entry = 1;                            /*3@F003691A*/
        EXEC CICS WRITEQ TS QUEUE(processInfo.tsqname) MAIN ITEM(first_entry)
          REWRITE FROM(&linkparms) LENGTH(linkparmsl) RESP(respcode);
      }                                                   /* @F003691A*/

      if (respcode ^= dfhresp(NORMAL)) {
        char stopsrvr[25];
        short stopsrvrl=sizeof(stopsrvr);

        char tsqNameNullTerm[9];
        char respstr[9];
        char resp2str[9];
        char regnameNullTerm[13];
        snprintf(tsqNameNullTerm, sizeof(tsqNameNullTerm), "%.8s", processInfo.tsqname);
        snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
        snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
        snprintf(regnameNullTerm, sizeof(regnameNullTerm), "%.12s", linkparms.aclnk_regname);
        ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_WRITE_TSQ_ERROR,
                  "The EXEC CICS WRITEQ TS MAIN command could not write data to the %s temporary storage queue. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                  tsqNameNullTerm, respstr, resp2str);
        strncpy(msgdata, (PROCESS_INFO_PTR)->messageBuffer, sizeof(msgdata));
        send_exc_resp(&processInfo, &msgdata, sizeof(msgdata));

        ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_STOPPING_ERRORS,
                  "The %s link server stopped.", regnameNullTerm);

        EXEC CICS DELETEQ TS QUEUE(processInfo.tsqname) RESP(respcode);

        sprintf(stopsrvr,"BBOC STP RGN=%.12s",linkparms.aclnk_regname);

        EXEC CICS LINK PROGRAM("BBOACNTL")
          COMMAREA(stopsrvr) LENGTH(stopsrvrl) RESP(respcode);

        EXEC CICS ABEND ABCODE("BBOX") CANCEL;
      }

      DEBUG1("Copied linkparms to tsq: %.8s", processInfo.tsqname);

      /* ----------------------------------------------------------------- */
      /* See if the caller is part of a global transaction                 */
      /* ----------------------------------------------------------------- */
      if ((linkparms.aclnkflags.aclnkflg_globaltx == 1) &&     /*2@F003691C*/
          (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_rec == 0))
      {
        /* --------------------------------------------------------------- */
        /* Make sure this version of CICS can handle transactional         */
        /* requests via OLA.  CICS TS 4.1 is the minimum requirement.      */
        /* We now support CICS TS 4.1 thru 5.2.                            */
        /* --------------------------------------------------------------- */
        DEBUG1("Found CICS ver: %d rel %d",cics_version,cics_release);
        if (cics_version < 4)                                   /* @711599C*/
        {                                                       /* @650153A*/
          char cicsVerNullTerm[7];
          snprintf(cicsVerNullTerm, sizeof(cicsVerNullTerm), "%.6s", cics_ts_version);
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_TX_NOT_SUPPORTED,
                    "CICS version %s does not support transaction propagation over optimized local adapters.",
                    cicsVerNullTerm);
          send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer, strlen((PROCESS_INFO_PTR)->messageBuffer));
          release_exit(&processInfo, 16, tx_state);             /* @650153A*/
        }                                                       /* @650153A*/

        /* --------------------------------------------------------------- */
        /* If there is already a transaction running, something is wrong   */
        /* and we should fail the request.                                 */
        /* --------------------------------------------------------------- */
        if (tx_state == NO_TRANSACTION)                        /* @F003691A*/
        {                                                      /* @F003691A*/
          int func_code = BBOAC2PC_FuncCodeImport;             /* @F003691A*/
          int c2pc_rc = 0;                                     /* @F003691A*/
          int c2pc_rsn = 0;                                    /* @F003691A*/

          /*---------------------------------------------------------------*/
          /* We need to massage the XID a little bit.  CICS does not       */
          /* provide a service to get the gtrid and bqual length out of    */
          /* the TID.  We need to prepend these lengths to the front of    */
          /* the TID.  CICS development has told us that they do not use   */
          /* the TID for anything except diplay to the customer on the     */
          /* UOW INQUIRE command, so altering is not going to impact       */
          /* branch registries or anything like that.  The down side of    */
          /* doing this is that the gtrid + bqual can only be 126 bytes.   */
          /* Since WAS is creating the bqual, and the WAS bqual is < 62    */
          /* bytes, we can be confident that the TID will be OK.           */
          /*---------------------------------------------------------------*/
          cics_tid massaged_tid;                               /* @F003691A*/
          int massaged_tid_len;                                /* @F003691A*/

          if (linkparms.aclnk_tx_data.aclnk_tx_tidlen <=       /* @F003691A*/
              sizeof(massaged_tid.tid))                        /* @F003691A*/
          {                                                    /*2@F003691A*/
            massaged_tid.gtrid_len = linkparms.aclnk_tx_data.aclnk_tx_tidlen -
              linkparms.aclnk_tx_data.aclnk_tx_bquallen;       /*3@F003691A*/
            massaged_tid.bqual_len = linkparms.aclnk_tx_data.aclnk_tx_bquallen;
            memcpy(&(massaged_tid.tid), linkparms.aclnk_tx_data.aclnk_tx_tid,
                   linkparms.aclnk_tx_data.aclnk_tx_tidlen);   /*3@F003691A*/
            massaged_tid_len = linkparms.aclnk_tx_data.aclnk_tx_tidlen +
              sizeof(massaged_tid.gtrid_len) + sizeof(massaged_tid.bqual_len);
          }                                                    /* @F003691A*/
          else                                                 /* @F003691A*/
          {                                                    /* @F003691A*/
            char lenstr[9];
            snprintf(lenstr, sizeof(lenstr), "%d", linkparms.aclnk_tx_data.aclnk_tx_tidlen);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_IMPORT_TID_LENGTH_ERROR,
                      "The %s transaction TID is too long to import.",
                      lenstr);
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);          /* @F003691A*/
          }                                                    /* @F003691A*/

          c2pc_rc = tran_func(                                 /* @PI24444C*/
                (void*)c2pc_dyn_area,                            /*@711599A*/
                (void*)&func_code,                               /*@711599A*/
                (void*)&linkparms.aclnk_tx_data.aclnk_tx_formatid,/*@711599A*/
                (void*)&massaged_tid_len,                        /*@711599A*/
                (void*)&linkparms.aclnk_tx_data.aclnk_tx_bquallen,/*@711599A*/
                (void*)&(massaged_tid),                          /*@711599A*/
                (void*)global_tran_uow_id, /* Output */          /*@711599A*/
                (void*)&c2pc_rsn); /* Output */                  /*@711599A*/

          if (c2pc_rc == 0)                                    /* @F003691A*/
          {                                                    /* @F003691A*/
            if (processInfo.baseInfo.verbose >= 2)             /* @F003691A*/
            {                                                  /*3@F003691A*/
              char* uow_string_p = binaryToHexString(global_tran_uow_id,
                                               sizeof(global_tran_uow_id));
              if (uow_string_p != 0)                           /* @F003691A*/
              {                                                /*2@F003691A*/
                DEBUG2("Global transaction import, UOW %s", uow_string_p);
                free(uow_string_p);                            /* @F003691A*/
              }                                                /* @F003691A*/
            }                                                  /* @F003691A*/

            tx_state = ACTIVE_TRANSACTION;                     /*5@F003691A*/
            cur_formatid = linkparms.aclnk_tx_data.aclnk_tx_formatid;
            cur_bqual_len = linkparms.aclnk_tx_data.aclnk_tx_bquallen;
            cur_tid_len = linkparms.aclnk_tx_data.aclnk_tx_tidlen;
            memcpy(cur_tid, linkparms.aclnk_tx_data.aclnk_tx_tid,
                   cur_tid_len);                               /* @F003691A*/
          }                                                    /* @F003691A*/
          else                                                 /* @F003691A*/
          {                                                    /* @F003691A*/
            char rcstr[9];
            char rsnstr[9];
            snprintf(rcstr, sizeof(rcstr), "%.8x", c2pc_rc);
            snprintf(rsnstr, sizeof(rsnstr), "%.8x", c2pc_rsn);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_IMPORT_GLOBAL_TRAN_ERROR,
                      "The link task could not import the global transaction, so the transaction was backed out. The hexidecimal return code is %s, and the hexidecimal reason code is %s.",
                      rcstr, rsnstr);
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer, strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);          /* @F003691A*/
          }                                                    /* @F003691A*/
        }                                                      /* @F003691A*/
        else                                                   /* @F003691A*/
        {                                                      /* @F003691A*/
          /* ------------------------------------------------------------- */
          /* Use existing transaction                                      */
          /* ----------------------------------------------------5@F003691A*/
          if ((cur_formatid != linkparms.aclnk_tx_data.aclnk_tx_formatid) ||
              (cur_bqual_len != linkparms.aclnk_tx_data.aclnk_tx_bquallen) ||
              (cur_tid_len != linkparms.aclnk_tx_data.aclnk_tx_tidlen) ||
              (memcmp(cur_tid, linkparms.aclnk_tx_data.aclnk_tx_tid,
                      cur_tid_len) != 0))
          {                                                    /* @F003691A*/
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_INCORRECT_TX_AFFINITY2,
                      "The link task could not process the request because the transaction ID (TID) of the request does not match the existing transaction.");
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer, strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);          /* @F003691A*/
          }                                                    /* @F003691A*/
        }                                                      /* @F003691A*/
      }                                                        /* @F003691A*/
      else                                                     /* @F003691A*/
      {                                                        /* @F003691A*/
        if (tx_state != NO_TRANSACTION)                        /* @F003691A*/
        {                                                      /* @F003691A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_INCORRECT_TX_AFFINITY,
                    "The request could not be processed because the link server task can process methods only for the existing transaction ID (TID).");
          send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer, strlen((PROCESS_INFO_PTR)->messageBuffer));
          release_exit(&processInfo, 16, tx_state);            /* @F003691A*/
        }                                                      /* @F003691A*/
      }                                                        /* @F003691A*/

      /* ----------------------------------------------------------------- */
      /* Determine if this is a syncpoint request, or a regular method.    */
      /* --------------------------------------------------------2@F003691A*/
      if (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_prepare == 1)
      {                                                        /* @F003691A*/
        DEBUG1("Called for TX PREPARE");                       /* @F003691A*/

        if (tx_state == ACTIVE_TRANSACTION)                    /* @F003691A*/
        {                                                      /* @F003691A*/
          BBOAC2PC_PrepareVote vote;                           /* @F003691A*/
          int c2pc_rc = 0;                                     /* @F003691A*/
          int c2pc_rsn = 0;                                    /* @F003691A*/
          int func_code = BBOAC2PC_FuncCodePrepare;            /* @F003691A*/

          c2pc_rc = tran_func(                                 /* @PI24444C*/
                (void*)c2pc_dyn_area,                          /* @F003691A*/
                (void*)&func_code,                             /* @F003691A*/
                (void*)&hostname_string_len,                   /* @F003691A*/
                (void*)hostname_string,                        /* @F003691A*/
                (void*)&vote,      /* Output */                /* @F003691A*/
                (void*)&c2pc_rsn,  /* Output */                /* @F003691A*/
                NULL, NULL);                                   /* @PI24444C*/

          if (c2pc_rc == 0)                                    /* @F003691A*/
          {                                                    /* @F003691A*/
            /*-------------------------------------------------------------*/
            /* Prepare the prepare response message.                       */
            /*-------------------------------------------------------------*/
            rspdata_p = tx_response_p;                         /* @F003691A*/
            rsplen = sizeof(*tx_response_p);                   /* @F003691A*/
            memset(tx_response_p, 0, rsplen);                  /* @F003691A*/
            memcpy(tx_response_p->atrspeye, BBOATRSP_EYE,      /* @F003691A*/
                   sizeof(tx_response_p->atrspeye));           /* @F003691A*/
            tx_response_p->atrspver = BBOATRSP_VER_1;          /* @F003691A*/

            /*-------------------------------------------------------------*/
            /* If things worked out, just continue as normal.              */
            /*-------------------------------------------------------------*/
            if (vote == PrepareVoteOK)                         /* @F003691A*/
            {                                                  /* @F003691A*/
              tx_state = PREPARED_TRANSACTION;                 /* @F003691A*/
              tx_response_p->atrspflg_ok = 1;                  /* @F003691A*/
            }                                                  /* @F003691A*/
            /*-------------------------------------------------------------*/
            /* If we got a read-only vote....                              */
            /*-------------------------------------------------------------*/
            else if (vote == PrepareVoteReadOnly)              /* @F003691A*/
            {                                                  /* @F003691A*/
              tx_state = NO_TRANSACTION;                       /* @F003691A*/
              tx_response_p->atrspflg_readonly = 1;            /* @F003691A*/
              tx_response_p->atrspflg_complete = 1;            /* @F003691A*/
            }                                                  /* @F003691A*/
            /*-------------------------------------------------------------*/
            /* If we got a backout vote, indicate it in the response and   */
            /* send it as an exception.  We exit the link task, resulting  */
            /* in (at best) a syncpoint rollback, and at worst an abend.   */
            /*-------------------------------------------------------------*/
            else if (vote == PrepareVoteBackout)               /* @F003691A*/
            {                                                  /* @F003691A*/
              tx_response_p->atrspflg_backout = 1;             /* @F003691A*/
              tx_response_p->atrspflg_complete = 1;            /* @F003691A*/
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_PREPARE_BACKOUT_VOTE,
                        "CICS backed out of preparing the unit of work.");
              strncpy(tx_response_p->atrsp_msg, (PROCESS_INFO_PTR)->messageBuffer,
                      sizeof(tx_response_p->atrsp_msg));
              tx_response_p->atrsp_msglen = strlen(tx_response_p->atrsp_msg);
              send_exc_resp(&processInfo, tx_response_p, rsplen);/* @F003691A*/
              release_exit(&processInfo, 16, tx_state);        /* @F003691A*/
            }                                                  /* @F003691A*/
            /*-------------------------------------------------------------*/
            /* If we got a heuristic vote, indicate it in the response and */
            /* send it as an exception.  We exit the link task, resulting  */
            /* in (at best) a syncpoint rollback, and at worst an abend.   */
            /*-------------------------------------------------------------*/
            else if (vote == PrepareVoteHeuristicMixed)        /* @F003691A*/
            {                                                  /* @F003691A*/
              tx_response_p->atrspflg_heur = 1;                /* @F003691A*/
              tx_response_p->atrspflg_complete = 1;            /* @F003691A*/
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_PREPARE_HEURISTIC,
                        "CICS reported a heuristic commit or backout when preparing a unit of work.");
              strncpy(tx_response_p->atrsp_msg, (PROCESS_INFO_PTR)->messageBuffer,
                      sizeof(tx_response_p->atrsp_msg));
              tx_response_p->atrsp_msglen = strlen(tx_response_p->atrsp_msg);
              send_exc_resp(&processInfo, tx_response_p, rsplen); /* @F003691A*/
              release_exit(&processInfo, 16, tx_state);        /* @F003691A*/
            }                                                  /* @F003691A*/

          }                                                    /* @F003691A*/
          else                                                 /* @F003691A*/
          {                                                    /* @F003691A*/
            ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_PREPARE_FAILURE,
                             "The CICS unit of work was not prepared. The return code is %s, and the reason code is %s.",
                             c2pc_rc, c2pc_rsn);
            strncpy(tx_response_p->atrsp_msg, (PROCESS_INFO_PTR)->messageBuffer,
                    sizeof(tx_response_p->atrsp_msg));
            tx_response_p->atrsp_msglen = strlen(tx_response_p->atrsp_msg);
            send_exc_resp(&processInfo, &msgdata, sizeof(msgdata));/*@F003691A*/
            release_exit(&processInfo, 16, tx_state);          /* @F003691A*/
          }                                                    /* @F003691A*/
        }                                                      /* @F003691A*/
        else                                                   /* @F003691A*/
        {                                                      /* @F003691A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_TRANSACTION_STATE_MISMATCH,
                    "The unit of work is not in the correct state to process a %s operation.",
                    "prepare");
          send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                        strlen((PROCESS_INFO_PTR)->messageBuffer));
          release_exit(&processInfo, 16, tx_state);            /* @F003691C*/
        }                                                      /* @F003691A*/
      }                                                        /*2@F003691A*/
      else if (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_commit == 1)
      {                                                        /* @F003691A*/
        DEBUG1("Called for TX COMMIT");                        /* @F003691A*/

        if (tx_state == ACTIVE_TRANSACTION)                    /* @F003691A*/
        {                                                      /* @F003691A*/
          int c2pc_rc = 0;                                     /* @F003691A*/
          int c2pc_rsn = 0;                                    /* @F003691A*/
          int func_code = BBOAC2PC_FuncCodeCommitOnePhase;     /* @F003691A*/

          c2pc_rc = tran_func(                                 /* @PI24444C*/
                (void*)c2pc_dyn_area,                          /* @F003691A*/
                (void*)&func_code,                             /* @F003691A*/
                (void*)&c2pc_rsn,  /* Output */                /* @F003691A*/
                NULL, NULL, NULL, NULL, NULL);                 /* @PI24444C*/

          if (c2pc_rc == 0)                                    /* @F003691A*/
          {                                                    /* @F003691A*/
            rspdata_p = tx_response_p;                         /* @F003691A*/
            rsplen = sizeof(*tx_response_p);                   /* @F003691A*/
            memset(tx_response_p, 0, rsplen);                  /* @F003691A*/
            memcpy(tx_response_p->atrspeye, BBOATRSP_EYE,      /* @F003691A*/
                   sizeof(tx_response_p->atrspeye));           /* @F003691A*/
            tx_response_p->atrspver = BBOATRSP_VER_1;          /* @F003691A*/
            tx_response_p->atrspflg_ok = 1;                    /* @F003691A*/
            tx_response_p->atrspflg_complete = 1;              /* @F003691A*/

            tx_state = NO_TRANSACTION;                         /* @F003691A*/
          }                                                    /* @F003691A*/
          else if ((c2pc_rc == BBOAC2PC_Return_InternalError)  /* @F003691A*/
                   && (c2pc_rsn == CommitRsnCode_BackedOut))   /* @F003691A*/
          {                                                    /* @F003691A*/
            rspdata_p = tx_response_p;                         /* @F003691A*/
            rsplen = sizeof(*tx_response_p);                   /* @F003691A*/
            memset(tx_response_p, 0, rsplen);                  /* @F003691A*/
            memcpy(tx_response_p->atrspeye, BBOATRSP_EYE,      /* @F003691A*/
                   sizeof(tx_response_p->atrspeye));           /* @F003691A*/
            tx_response_p->atrspver = BBOATRSP_VER_1;          /* @F003691A*/
            tx_response_p->atrspflg_backout = 1;               /* @F003691A*/
            tx_response_p->atrspflg_complete = 1;              /* @F003691A*/

            tx_state = NO_TRANSACTION;                         /* @F003691A*/
          }                                                    /* @F003691A*/
          else                                                 /* @F003691A*/
          {                                                    /* @F003691A*/
            ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_COMMIT_ONE_PHASE_FAILURE,
                             "The CICS unit of work was not committed. The return code is %s, and the reason code is %s.",
                             c2pc_rc, c2pc_rsn);
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);          /* @F003691C*/
          }                                                    /* @F003691A*/
        }                                                      /* @F003691A*/
        else if (tx_state == PREPARED_TRANSACTION)             /* @F003691A*/
        {                                                      /* @F003691A*/
          int c2pc_rc = 0;                                     /* @F003691A*/
          int c2pc_rsn = 0;                                    /* @F003691A*/
          int func_code = BBOAC2PC_FuncCodeCommit;             /* @F003691A*/

          c2pc_rc = tran_func(                                 /* @PI24444C*/
              (void*)c2pc_dyn_area,                            /* @F003691A*/
              (void*)&func_code,                               /* @F003691A*/
              (void*)&c2pc_rsn,  /* Output */                  /* @F003691A*/
              NULL, NULL, NULL, NULL, NULL);                   /* @PI24444C*/

          if (c2pc_rc == 0)                                    /* @F003691A*/
          {                                                    /* @F003691A*/
            rspdata_p = tx_response_p;                         /* @F003691A*/
            rsplen = sizeof(*tx_response_p);                   /* @F003691A*/
            memset(tx_response_p, 0, rsplen);                  /* @F003691A*/
            memcpy(tx_response_p->atrspeye, BBOATRSP_EYE,      /* @F003691A*/
                   sizeof(tx_response_p->atrspeye));           /* @F003691A*/
            tx_response_p->atrspver = BBOATRSP_VER_1;          /* @F003691A*/
            tx_response_p->atrspflg_ok = 1;                    /* @F003691A*/
            tx_response_p->atrspflg_complete = 1;              /* @F003691A*/

            tx_state = NO_TRANSACTION;                         /* @F003691A*/
          }                                                    /* @F003691A*/
          else                                                 /* @F003691A*/
          {                                                    /* @F003691A*/
            /*-------------------------------------------------------------*/
            /* Need to abend, prepared transaction in unknown state.  If   */
            /* the transaction is still in-doubt, it will be shunted.      */
            /*-------------------------------------------------------------*/
            char rcstr[9];
            char rsnstr[9];
            snprintf(rcstr, sizeof(rcstr), "%.8x", c2pc_rc);
            snprintf(rsnstr, sizeof(rsnstr), "%.8x", c2pc_rsn);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_COMMIT_FAILURE,
                      "The system received a request to commit a unit of work (UOW) that failed.  The return code is %s.  The reason code is %s.",
                      rcstr, rsnstr);
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);          /* @F003691A*/
          }                                                    /* @F003691A*/
        }                                                      /* @F003691A*/
        else if ((tx_state == NO_TRANSACTION) &&               /* @F003691A*/
                 (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_rec == 1))
        {                                                      /*2@F003691A*/
          /*---------------------------------------------------------------*/
          /* Recovery commit request.                                      */
          /*---------------------------------------------------------------*/
          resolve_indoubt(&processInfo,
                          &(hostname_string[0]), /* @F003691A*/
                          linkparms.aclnk_tx_data.aclnk_tx_formatid,
                          linkparms.aclnk_tx_data.aclnk_tx_tidlen -
                           linkparms.aclnk_tx_data.aclnk_tx_bquallen,
                          linkparms.aclnk_tx_data.aclnk_tx_bquallen,
                          &(linkparms.aclnk_tx_data.aclnk_tx_tid[0]),
                          TRUE, /* commit */
                          tx_response_p);

          rspdata_p = tx_response_p;                           /* @F003691A*/
          rsplen = sizeof(*tx_response_p);                     /* @F003691A*/
        }                                                      /* @F003691A*/
        else                                                   /* @F003691A*/
        {                                                      /* @F003691A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_TRANSACTION_STATE_MISMATCH,
                    "The unit of work is not in the correct state to process a %s operation.",
                    "commit");
          send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                        strlen((PROCESS_INFO_PTR)->messageBuffer));
          release_exit(&processInfo, 16, tx_state);            /* @F003691C*/
        }                                                      /* @F003691A*/
      }                                                        /*2@F003691A*/
      else if (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_backout == 1)
      {                                                        /* @F003691A*/
        DEBUG1("Called for TX BACKOUT");                       /* @F003691A*/

        if ((tx_state == ACTIVE_TRANSACTION) ||                /* @F003691A*/
            (tx_state == PREPARED_TRANSACTION))                /* @F003691A*/
        {                                                      /* @F003691A*/
          int c2pc_rc = 0;                                     /* @F003691A*/
          int c2pc_rsn = 0;                                    /* @F003691A*/
          int func_code = BBOAC2PC_FuncCodeBackout;            /* @F003691A*/

          c2pc_rc = tran_func(                                 /* @PI24444C*/
                (void*)c2pc_dyn_area,                            /*@711599A*/
                (void*)&func_code,                               /*@711599A*/
                (void*)&c2pc_rsn,  /* Output */                  /*@711599A*/
                NULL, NULL, NULL, NULL, NULL);                 /* @PI24444C*/

          if (c2pc_rc == 0)                                    /* @F003691A*/
          {                                                    /* @F003691A*/
            rspdata_p = tx_response_p;                         /* @F003691A*/
            rsplen = sizeof(*tx_response_p);                   /* @F003691A*/
            memset(tx_response_p, 0, rsplen);                  /* @F003691A*/
            memcpy(tx_response_p->atrspeye, BBOATRSP_EYE,      /* @F003691A*/
                   sizeof(tx_response_p->atrspeye));           /* @F003691A*/
            tx_response_p->atrspver = BBOATRSP_VER_1;          /* @F003691A*/
            tx_response_p->atrspflg_ok = 1;                    /* @F003691A*/
            tx_response_p->atrspflg_complete = 1;              /* @F003691A*/

            tx_state = NO_TRANSACTION;                         /* @F003691A*/
          }                                                    /* @F003691A*/
          else                                                 /* @F003691A*/
          {                                                    /* @F003691A*/
            char rcstr[9];
            char rsnstr[9];
            snprintf(rcstr, sizeof(rcstr), "%.8x", c2pc_rc);
            snprintf(rsnstr, sizeof(rsnstr), "%.8x", c2pc_rsn);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_BACKOUT_FAILURE,
                      "The CICS unit of work was not backed out. The return code is %s, and the reason code is %s.",
                      rcstr, rsnstr);

            if (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_inactive
                == 0)                                          /*2@F003691A*/
            {                                                  /* @F003691A*/
              send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                            strlen((PROCESS_INFO_PTR)->messageBuffer));
            }

            release_exit(&processInfo, 16, tx_state);          /* @F003691C*/
          }                                                    /* @F003691A*/
        }                                                      /* @F003691A*/
        else if ((tx_state == NO_TRANSACTION) &&               /*2@F003691A*/
                 (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_rec == 1))
        {                                                      /* @F003691A*/
          /*---------------------------------------------------------------*/
          /* Recovery backout request.                                     */
          /*---------------------------------------------------------------*/
          resolve_indoubt(&processInfo,
                          &(hostname_string[0]), /* @F003691A*/
                          linkparms.aclnk_tx_data.aclnk_tx_formatid,
                          linkparms.aclnk_tx_data.aclnk_tx_tidlen -
                           linkparms.aclnk_tx_data.aclnk_tx_bquallen,
                          linkparms.aclnk_tx_data.aclnk_tx_bquallen,
                          &(linkparms.aclnk_tx_data.aclnk_tx_tid[0]),
                          FALSE, /* backout */
                          tx_response_p);

          rspdata_p = tx_response_p;                           /* @F003691A*/
          rsplen = sizeof(*tx_response_p);                     /* @F003691A*/
        }                                                      /* @F003691A*/
        else                                                   /* @F003691A*/
        {                                                      /* @F003691A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_TRANSACTION_STATE_MISMATCH,
                    "The unit of work is not in the correct state to process a %s operation.",
                    "backout");

          if (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_inactive == 0)
          {                                                    /*2@F003691A*/
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
          }                                                    /* @F003691A*/

          release_exit(&processInfo, 16, tx_state);            /* @F003691C*/
        }                                                      /* @F003691A*/
      }                                                        /* @F003691A*/
      else if (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_tidlist == 1)
      {                                                        /* @F003691A*/
        DEBUG1("Called for TX TIDLIST");                       /* @F003691A*/

        if (tx_state == NO_TRANSACTION)                        /* @F003691A*/
        {                                                      /* @F003691A*/
          XidListNode* xid_list_p = NULL;                      /* @F003691A*/
          XidListNode* cur_xid_node_p = NULL;                  /* @F003691A*/
          int xid_count = 0;                                   /* @F003691A*/
          int xid_ret_stor_size = 0;                           /* @F003691A*/

          struct bboaxid* cur_xid_in_response_p;               /* @F003691A*/

          DEBUG2("Recovery Tidlist Request");                  /* @F003691A*/

          /* ------------------------------------------------------------- */
          /* Get a list of XIDs.                                           */
          /* ------------------------------------------------------------- */
          xid_list_p = getXids(&processInfo, &(hostname_string[0]));

          /* ------------------------------------------------------------- */
          /* Count them.                                                   */
          /* ------------------------------------------------------------- */
          cur_xid_node_p = xid_list_p;                         /* @F003691A*/
          while (cur_xid_node_p != NULL)                       /* @F003691A*/
          {                                                    /* @F003691A*/
            xid_count = xid_count + 1;                         /* @F003691A*/
            cur_xid_node_p = cur_xid_node_p->next_p;           /* @F003691A*/
          }                                                    /* @F003691A*/

          /* ------------------------------------------------------------- */
          /* Allocate some storage to put them.                            */
          /* ------------------------------------------------------------- */
          xid_ret_stor_size = sizeof(struct bboatrsp) +        /* @F003691A*/
            (xid_count * sizeof(struct bboaxid));              /* @F003691A*/

          if (xid_ret_stor_size > sizeof(msgbuff4k))           /* @F003691A*/
          {                                                    /* @F003691A*/
            EXEC CICS GETMAIN SET(tx_response_p)               /* @F003691A*/
              FLENGTH(xid_ret_stor_size) RESP(respcode);       /* @F003691A*/

            if (respcode != dfhresp(NORMAL))                   /* @F003691A*/
            {                                                  /* @F003691A*/
              ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_XIDS_NO_STORAGE,
                               "The system cannot return %s XIDs to the application server because of the following EC GETMAIN error: %s",
                               xid_count, respcode);
              tx_response_p = (struct bboatrsp*)msgbuff4k;     /* @F003691A*/
              memset(tx_response_p, 0, sizeof(struct bboatrsp));/*@F003691A*/
              strncpy(tx_response_p->atrsp_msg, (PROCESS_INFO_PTR)->messageBuffer,
                      sizeof(tx_response_p->atrsp_msg) - 1); // Null term
              tx_response_p->atrsp_msglen = strlen(tx_response_p->atrsp_msg);
              send_exc_resp(&processInfo, tx_response_p,
                            sizeof(struct bboatrsp));
              release_exit(&processInfo, 16, tx_state);        /* @F003691A*/
            }                                                  /* @F003691A*/
          }                                                    /* @F003691A*/

          /* ------------------------------------------------------------- */
          /* Fill in the response and XID data.                            */
          /* ------------------------------------------------------------- */
          rspdata_p = tx_response_p;                           /* @F003691A*/
          rsplen = xid_ret_stor_size;                          /* @F003691C*/
          memset(tx_response_p, 0, rsplen);                    /* @F003691A*/
          memcpy(tx_response_p->atrspeye, BBOATRSP_EYE,        /* @F003691A*/
                 sizeof(tx_response_p->atrspeye));             /* @F003691A*/
          tx_response_p->atrspver = BBOATRSP_VER_1;            /* @F003691A*/
          tx_response_p->atrspflg_ok = 1;                      /* @F003691A*/
          tx_response_p->atrsp_num_tid = xid_count;            /* @F003691A*/

          if (xid_count > 0)                                   /* @F003691A*/
          {                                                    /*2@F003691A*/
            cur_xid_in_response_p = (struct bboaxid*)(tx_response_p + 1);
            cur_xid_node_p = xid_list_p;                       /* @F003691A*/
            while (cur_xid_node_p != NULL)                     /* @F003691A*/
            {                                                  /* @F003691A*/
              void* free_p = cur_xid_node_p;                   /* @F003691A*/
              memcpy(cur_xid_in_response_p,                    /* @F003691A*/
                     &(cur_xid_node_p->xid),                   /* @F003691A*/
                     sizeof(struct bboaxid));                  /* @F003691A*/
              cur_xid_node_p = cur_xid_node_p->next_p;         /* @F003691A*/
              cur_xid_in_response_p += 1;                      /* @F003691A*/
              EXEC CICS FREEMAIN DATAPOINTER(free_p) NOHANDLE; /* @F003691A*/
            }                                                  /* @F003691A*/
          }                                                    /* @F003691A*/

          /* ------------------------------------------------------------- */
          /* Always reset tx_response_p back to it's original state.       */
          /* ------------------------------------------------------------- */
          tx_response_p = (struct bboatrsp*)msgbuff4k;         /* @F003691A*/
        }                                                      /* @F003691A*/
        else                                                   /* @F003691A*/
        {                                                      /* @F003691A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_TRANSACTION_STATE_MISMATCH,
                    "The unit of work is not in the correct state to process a %s operation.",
                    "tidlist");

          release_exit(&processInfo, 16, tx_state);            /* @F003691C*/
        }                                                      /* @F003691A*/
      }
      else                                                     /* @F003691A*/
      {                                                        /* @F003691A*/
        /*-----------------------------------------------------------------*/
        /* Make sure that if there is a transaction, it is not prepared.   */
        /*-----------------------------------------------------------------*/
        if (tx_state == PREPARED_TRANSACTION)                  /* @F003691A*/
        {                                                      /* @F003691A*/
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_NEW_WORK_DURING_PREPARE,
                    "CICS could not run the requested program in a unit of work that was already prepared.");
          send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                        strlen((PROCESS_INFO_PTR)->messageBuffer));
          release_exit(&processInfo, 16, tx_state);            /* @F003691A*/
        }                                                      /* @F003691A*/

        /* ----------------------------------------------------- */
        /* Issue CICS LINK to target program.                    */
        /* We replace any 0x00s with blanks.                     */
        /* ----------------------------------------------------- */
        strncpy(linkpgmname,linkparms.aclnk_servname,8);
        for (i=0 ; i<8 ; i++) {
          if (linkpgmname[i] == 0x00)
            linkpgmname[i] = ' ';
        }

        DEBUG1("EXEC CICS LINK to PROGRAM: %.8s", linkpgmname);

        char rtx[4];
        char rtxsys[4];

        memcpy(rtx, linkparms.aclnk_rtx, sizeof(linkparms.aclnk_rtx));
        memcpy(rtxsys, linkparms.aclnk_rtxsys, sizeof(linkparms.aclnk_rtxsys));

        /* Remote transaction and SYSID processing: When RTXP is passed     */
        /* we will set the TRANSID option on the EC LINK. If the RTX option */
        /* is also passed, we use that on the TRANSID(), if not, we use the */
        /* EIBTRNID (current transaction running).  For the RTXSYS option,  */
        /* we DO NOT pass the TRANSID() option, just the SYSID() option.    */
        /*                                                         @PI52665A*/
        DEBUG2("Remote TransID: %.4s  Remote Sysid: %.4s", rtx, rtxsys);

        /* Processing for COMMAREA.                         */
        if (linkparms.aclnkflags.aclnkflg_commarea == TRUE) {
          reqlens = reqlen;

          if (linkparms.aclnkflags.aclnkflg_lsync == TRUE) {
             if (linkparms.aclnkflags.aclnkflg_rtxp == FALSE) {  /* @PI52665C*/
                 if (memcmp(rtxsys, "    ", sizeof(rtxsys)) == 0) {
                      EXEC CICS LINK PROGRAM(linkpgmname)
                          COMMAREA(reqdata_p) LENGTH(reqlens) RESP(respcode)
                          SYNCONRETURN;                         /* @PM70002A*/
                 } else {
                     EXEC CICS LINK PROGRAM(linkpgmname)
                         COMMAREA(reqdata_p) LENGTH(reqlens) RESP(respcode)
                         SYSID(rtxsys) SYNCONRETURN;            /* @PI52665A*/
                 }
             } else {  /* RTXP = TRUE */
                 if (memcmp(rtx, "    ", sizeof(rtx)) == 0) {
                     memcpy(rtx, dfheiptr->eibtrnid, sizeof(rtx));
                 }
                 EXEC CICS LINK PROGRAM(linkpgmname)
                     COMMAREA(reqdata_p) LENGTH(reqlens) RESP(respcode)
                     TRANSID(rtx) SYNCONRETURN;                 /* @PI52665A*/
              }
          } else { /* No LSYNC - no SYNCONRETURN */
              if (linkparms.aclnkflags.aclnkflg_rtxp == FALSE) { /* @PI52665A*/
                  if (memcmp(rtxsys, "    ", sizeof(rtxsys)) == 0) {
                       EXEC CICS LINK PROGRAM(linkpgmname)
                           COMMAREA(reqdata_p) LENGTH(reqlens) RESP(respcode);
                  } else {
                      EXEC CICS LINK PROGRAM(linkpgmname)
                          COMMAREA(reqdata_p) LENGTH(reqlens) RESP(respcode)
                          SYSID(rtxsys);                         /* @PI52665A*/
                  }
              } else {  /* RTXP = TRUE */
                  if (memcmp(rtx, "    ", sizeof(rtx)) == 0) {
                      memcpy(rtx, dfheiptr->eibtrnid, sizeof(rtx));
                  }
                  EXEC CICS LINK PROGRAM(linkpgmname)
                      COMMAREA(reqdata_p) LENGTH(reqlens) RESP(respcode)
                      TRANSID(rtx);                             /* @PI52665A*/
              }
          }                                                     /* @PM70002A*/

          if (respcode == dfhresp(NORMAL)) {
            DEBUG1("EXEC CICS LINK to %.8s with Commarea Successful!",
                   linkpgmname);
            rsplen = reqlens;
            rspdata_p = reqdata_p;                              /*@584680A*/
          }
          else {
            char programNullTerm[9];
            char respstr[9];
            char resp2str[9];
            snprintf(programNullTerm, sizeof(programNullTerm), "%.8s", linkpgmname);
            snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
            snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_LINK_PROGRAM_ERROR,
                      "The EXEC CICS LINK PROGRAM %s command could not link from the link server program in the CICS region to the program that the client requested. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                      programNullTerm, respstr, resp2str);
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);         /* @F003691C*/
          }
        }
        /* Processing for CHANNEL.                          */
        else if (linkparms.aclnkflags.aclnkflg_channel == TRUE) {
            char channelid[16];

            rspdata_p = &msgbuff4k;
            strncpy(channelid, linkparms.aclnk_reqcontainerid, 16);
            /* Default container is CHAR.                   */
            if (linkparms.aclnk_reqcontainertype == 0) {
                 void* cursor = reqdata_p;
                 void* end_cursor = (char*)reqdata_p + reqlen;
                 while (cursor < end_cursor) {
                     // Pull out the container name
                     char* container_p = (char*)cursor;
                     cursor = (char*)cursor + 16;

                     // Determine container name size
                     int containerLen = strnlen(container_p, 16);
                     if( containerLen < 16 ) {
                        // Container name isn't 16 bytes, blank pad it
                        container_p = container_p + containerLen;
                        memset(container_p, ' ', (16 - containerLen));

                        // Reset the cursor
                        container_p = (char*)container_p - containerLen;
                     }

                     // Pull out the data length
                     int* dataLen_p = (int*)cursor;
                     cursor = (char*)cursor + 4;

                     // Debug statement to see container info
                     DEBUG2("Before an EXEC CICS PUT CONTAINER statment with:\n\tContainer Name: %.16s\n\tData Length   : %d",
                            container_p, *dataLen_p);

                     // Put the data into the container
                     EXEC CICS PUT CONTAINER(container_p)
                        CHANNEL(channelid) FROM(cursor) FLENGTH(*dataLen_p)
                        CHAR FROMCCSID(ASCIIPAGE)
                        RESP(respcode);

                    if (respcode == dfhresp(NORMAL)) {
                        DEBUG2("EXEC CICS PUT CONTAINER Successful!");
                    }
                    else {
                        char containerNullTerm[17];
                        char respstr[9];
                        char resp2str[9];
                        snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", linkparms.aclnk_reqcontainerid);
                        snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                        snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                        ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_PUT_CONTIANER_ERROR,
                                  "The EXEC CICS PUT CONTAINER %s command did not add data to the specified container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                  containerNullTerm, respstr, resp2str);
                        send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                      strlen((PROCESS_INFO_PTR)->messageBuffer));
                        release_exit(&processInfo, 16, tx_state);
                    }

                    // Move to the end of the current block of data
                    cursor = (char*)cursor + *dataLen_p;
                 }
            }
            /* Type 1 indicates request container is BIT.   */
            else {
                void* cursor = reqdata_p;
                void* end_cursor = (char*)reqdata_p + reqlen;
                while (cursor != end_cursor) {
                    // Pull out the container name
                    char* container_p = (char*)cursor;
                    cursor = (char*)cursor + 16;

                     // Determine container name size
                     int containerLen = strnlen(container_p, 16);
                     if( containerLen < 16 ) {
                        // Container name isn't 16 bytes, blank pad it
                        container_p = container_p + containerLen;
                        memset(container_p, ' ', (16 - containerLen));

                        // Reset the cursor
                        container_p = (char*)container_p - containerLen;
                     }

                    // Pull out the data length
                    int* dataLen_p = (int*)cursor;
                    cursor = (char*)cursor + 4;

                    // Debug statement to see container info
                    DEBUG2("Before an EXEC CICS PUT CONTAINER statment with:\n\tContainer Name: %.16s\n\tData Length   : %d",
                           container_p, *dataLen_p);

                    // Put the data into the container
                    EXEC CICS PUT CONTAINER(container_p)
                        CHANNEL(channelid) FROM(cursor) FLENGTH(*dataLen_p)
                        BIT RESP(respcode);

                    if (respcode == dfhresp(NORMAL)) {
                        DEBUG2("EXEC CICS PUT CONTAINER Successful!");
                    }
                    else {
                        char containerNullTerm[17];
                        char respstr[9];
                        char resp2str[9];
                        snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", linkparms.aclnk_reqcontainerid);
                        snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                        snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                        ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_PUT_CONTIANER_ERROR,
                                  "The EXEC CICS PUT CONTAINER %s command did not add data to the specified container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                  containerNullTerm, respstr, resp2str);
                        send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                      strlen((PROCESS_INFO_PTR)->messageBuffer));
                        release_exit(&processInfo, 16, tx_state);
                    }

                    // Move to the end of the curent block of data
                    cursor = (char*)cursor + *dataLen_p;
                }
            }

            if (linkparms.aclnkflags.aclnkflg_lsync == TRUE) {
               if (linkparms.aclnkflags.aclnkflg_rtxp == FALSE) { /* @PI52665C*/
                   if (memcmp(rtxsys, "    ", sizeof(rtxsys)) == 0) {
                        EXEC CICS LINK PROGRAM(linkpgmname)
                            CHANNEL(channelid) RESP(respcode)
                            SYNCONRETURN;                         /* @PM70002A*/
                   } else {
                       EXEC CICS LINK PROGRAM(linkpgmname)
                           CHANNEL(channelid) RESP(respcode)
                           SYSID(rtxsys) SYNCONRETURN;            /* @PI52665A*/
                   }
               } else {  /* RTXP = TRUE */
                   if (memcmp(rtx, "    ", sizeof(rtx)) == 0) {
                       memcpy(rtx, dfheiptr->eibtrnid, sizeof(rtx));
                   }
                   EXEC CICS LINK PROGRAM(linkpgmname)
                       CHANNEL(channelid) RESP(respcode)
                       TRANSID(rtx) SYNCONRETURN;                 /* @PI52665A*/
                }
            } else { /* No LSYNC - no SYNCONRETURN */
                if (linkparms.aclnkflags.aclnkflg_rtxp == FALSE) {/* @PI52665A*/
                    if (memcmp(rtxsys, "    ", sizeof(rtxsys)) == 0) {
                         EXEC CICS LINK PROGRAM(linkpgmname)
                              CHANNEL(channelid) RESP(respcode);
                    } else {
                        EXEC CICS LINK PROGRAM(linkpgmname)
                            CHANNEL(channelid) RESP(respcode)
                            SYSID(rtxsys);                        /* @PI52665A*/
                    }
                } else {  /* RTXP = TRUE */
                    if (memcmp(rtx, "    ", sizeof(rtx)) == 0) {
                        memcpy(rtx, dfheiptr->eibtrnid, sizeof(rtx));
                    }
                    EXEC CICS LINK PROGRAM(linkpgmname)
                         CHANNEL(channelid) RESP(respcode)
                         TRANSID(rtx);                            /* @PI52665A*/
                }
            }                                                     /* @PM70002A*/

            if (respcode == dfhresp(NORMAL)) {
                DEBUG1("EXEC CICS LINK with Channel Successful!");

                int btoken = 0;
                char containerName_p[16];
                char blankName[16];
                memset(blankName, ' ', 16);
                memset(containerName_p, ' ', 16);
                long int respcode2 = 0;
                int responseLength = 0;
                void* cursor = rspdata_p;

                // Obtain the browse token
                EXEC CICS STARTBROWSE CONTAINER
                    CHANNEL(channelid) BROWSETOKEN(btoken) RESP(respcode);

                if (respcode == dfhresp(NORMAL)) {
                    DEBUG2("EXEC CICS STARTBROWSE CONTAINER Successful!");
                }
                else {
                    ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_BROWSE_CONTAINER_ERROR,
                                     "The link task did not retrieve a list of containers in the channel because the EXEC CICS STARTBROWSE CONTAINER command did not initialize a browse token. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                     dfheiptr->eibresp, dfheiptr->eibresp2);
                    send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                  strlen((PROCESS_INFO_PTR)->messageBuffer));
                    release_exit(&processInfo, 16, tx_state);
                }

                /*
                 * Determine the length of storage needed.
                 * This is done by finding each container name and
                 * doing a GET CONTAINER NODATA while keeping a running total.
                 */
                EXEC CICS GETNEXT CONTAINER(containerName_p)
                    BROWSETOKEN(btoken)
                    RESP(respcode) RESP2(respcode2);

                rsplen = 0;
                if( (respcode == DFHRESP(END)) && (memcmp(containerName_p, blankName, 16) == 0) ) {
                    // There are no containers on the channel!
                }
                else {
                    unsigned char sawEnd = FALSE;
                    do {
                        // Check response code for previous GETNEXT CONTAINER.  If it's END, mark it.
                        if( respcode == DFHRESP(END) ) {
                            sawEnd = TRUE;
                        }
                        else if( respcode != DFHRESP(NORMAL) ) {
                            ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_GETNEXT_CONTAINER_ERROR,
                                             "The EXEC CICS GETNEXT CONTAINER command did not return the name of the next container in the channel. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                             dfheiptr->eibresp, dfheiptr->eibresp2);
                            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                          strlen((PROCESS_INFO_PTR)->messageBuffer));
                            release_exit(&processInfo, 16, tx_state);
                        }

                        if( memcmp(containerName_p, blankName, 16) != 0 ) {
                            // Debug statement to see container info
                            memset(msgdata, ' ', sizeof(msgdata));
                            sprintf(msgdata,
                                    "Outbound container: %.16s", containerName_p);
                            DEBUG2(msgdata);

                            // GETNEXT returned a real container name, process it
                            // Do a GET CONTAINER NODATA to find the length of data
                            responseLength = 0;
                            EXEC CICS GET CONTAINER(containerName_p)
                                CHANNEL(channelid) NODATA FLENGTH(responseLength)
                                RESP(respcode);

                            if (respcode == dfhresp(NORMAL)) {
                              DEBUG2("EXEC CICS GET CONTAINER %.16s NODATA Successful!",
                                     containerName_p);
                              DEBUG2("Response message length: %d",responseLength);
                            }
                            else {
                              char containerNullTerm[17];
                              char respstr[9];
                              char resp2str[9];
                              snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", containerName_p);
                              snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                              snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                              ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GET_CONTAINER_ERROR,
                                        "The EXEC CICS GET CONTAINER %s NODATA command did not retrieve the length of the data. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                        containerNullTerm, respstr, resp2str);
                              send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                            strlen((PROCESS_INFO_PTR)->messageBuffer));
                              release_exit(&processInfo, 16, tx_state);
                            }

                            // Add to the total data length
                            rsplen += 16;
                            rsplen += 4;
                            rsplen += responseLength;

                            // Clear the container name
                            memset(containerName_p, ' ', 16);

                            // Determine the name of the next container (if there is one)
                            EXEC CICS GETNEXT CONTAINER(containerName_p)
                                BROWSETOKEN(btoken)
                                RESP(respcode) RESP2(respcode2);
                        }
                    } while( !sawEnd );
                }
                responseLength = rsplen;

                /*
                 * We now have to total length we're going to need to create
                 * the response data to send back up the line.  First, re-browse
                 * the channel so we can get the container names again. Then,
                 * allocate memory and slowly fill up the rspdata.
                 */
                EXEC CICS ENDBROWSE CONTAINER BROWSETOKEN(btoken);
                EXEC CICS STARTBROWSE CONTAINER
                    CHANNEL(channelid) BROWSETOKEN(btoken) RESP(respcode);

                if (respcode == dfhresp(NORMAL)) {
                    DEBUG2("EXEC CICS STARTBROWSE CONTAINER Successful!");
                }
                else {
                    ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_BROWSE_CONTAINER_ERROR,
                                     "The link task did not retrieve a list of containers in the channel because the EXEC CICS STARTBROWSE CONTAINER command did not initialize a browse token. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                     dfheiptr->eibresp, dfheiptr->eibresp2);
                    send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                  strlen((PROCESS_INFO_PTR)->messageBuffer));
                    release_exit(&processInfo, 16, tx_state);
                }

                if( rsplen > 4096 ) {
                    EXEC CICS GETMAIN SET(rspdata_p)
                        FLENGTH(rsplen)
                        RESP(respcode);

                    if (respcode == dfhresp(NORMAL)) {
                        DEBUG1("Requested %d bytes. Starting addr: %X",
                            rsplen, rspdata_p);
                        cursor = rspdata_p;
                    }
                    else {
                        char rsplenstr[9];
                        snprintf(rsplenstr, sizeof(rsplenstr), "%d", rsplen);
                        ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GETMAIN_RESPONSE_BUFFER_FAIL,
                                  "The EXEC CICS GETMAIN command could not allocate storage for a response buffer of %s bytes.",
                                  rsplenstr);
                        ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_GETMAIN_ERROR,
                                         "The EXEC CICS GETMAIN command could not allocate storage. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                         dfheiptr->eibresp, dfheiptr->eibresp2);
                        send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                      strlen((PROCESS_INFO_PTR)->messageBuffer));
                        release_exit(&processInfo, 16, tx_state);
                    }
                }

                memset(containerName_p, ' ', 16);
                EXEC CICS GETNEXT CONTAINER(containerName_p)
                    BROWSETOKEN(btoken)
                    RESP(respcode) RESP2(respcode2);

                if( (respcode == DFHRESP(END)) && (memcmp(containerName_p, blankName, 16) == 0) ) {
                    // There are no containers on the channel!
                }
                else {
                    unsigned char sawEnd = FALSE;
                    int remainingLen = rsplen;
                    do {
                        // Check response code for previous GETNEXT CONTAINER.  If it's END, mark it.
                        if( respcode == DFHRESP(END) ) {
                            sawEnd = TRUE;
                        }
                        else if( respcode != DFHRESP(NORMAL) ) {
                            ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_GETNEXT_CONTAINER_ERROR,
                                             "The EXEC CICS GETNEXT CONTAINER command did not return the name of the next container in the channel. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                             dfheiptr->eibresp, dfheiptr->eibresp2);
                            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                          strlen((PROCESS_INFO_PTR)->messageBuffer));
                            release_exit(&processInfo, 16, tx_state);
                        }

                        if( memcmp(containerName_p, blankName, 16) != 0 ) {
                            // Put the name of the container into the output
                            memcpy(cursor, containerName_p, 16);
                            cursor = (char*)cursor + 16;

                            // Move the cursor to allow space for the length of data
                            cursor = (char*)cursor + 4;

                            // Default channel is CHAR
                            if( linkparms.aclnk_reqcontainertype == 0 ) {
                                EXEC CICS GET CONTAINER(containerName_p)
                                    CHANNEL(channelid) INTO(cursor) FLENGTH(remainingLen)
                                    INTOCCSID(ASCIIPAGE)
                                    RESP(respcode);
                            }
                            // If channel is BIT, don't translate
                            else {
                                EXEC CICS GET CONTAINER(containerName_p)
                                    CHANNEL(channelid) INTO(cursor) FLENGTH(remainingLen)
                                    RESP(respcode);
                            }

                            // rsplen is now output.  Use it to copy in the size of the data accepted.
                            memcpy(((char*)cursor - 4), &remainingLen, sizeof(int));
                            cursor = (char*)cursor + remainingLen;

                            // remainingLen now holds how much data was returned, and rsplen holds the previous remainingLen
                            // remainingLen is updated to be the new size of the buffer, and rsplen caches this
                            // value for the next iteration of the loop
                            remainingLen = rsplen - remainingLen;
                            remainingLen -= 4;
                            remainingLen -= 16;
                            rsplen = remainingLen;

                            if (respcode == dfhresp(NORMAL)) {
                              DEBUG2("EXEC CICS GET CONTAINER Successful!");
                            }
                            else {
                                char containerNullTerm[17];
                                char respstr[9];
                                char resp2str[9];
                                snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", containerName_p);
                                snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                                snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                                ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GET_CONTAINER_ERROR2,
                                          "The EXEC CICS GET CONTAINER %s command did not retrieve data from the specified channel container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                          containerNullTerm, respstr, resp2str);
                                send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                              strlen((PROCESS_INFO_PTR)->messageBuffer));
                                release_exit(&processInfo, 16, tx_state);
                            }

                            EXEC CICS DELETE CONTAINER(containerName_p) CHANNEL(channelid);

                            if (respcode == dfhresp(NORMAL)) {
                                DEBUG2("EXEC CICS DELETE CONTAINER Successful!");
                            }
                            else {
                                char containerNullTerm[17];
                                char respstr[9];
                                char resp2str[9];
                                snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", containerName_p);
                                snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                                snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                                ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_DELETE_CONTAINER_ERROR,
                                          "The EXEC CICS DELETE CONTAINER %s command did not delete the specified channel container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                          containerNullTerm, respstr, resp2str);
                                send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                                              strlen((PROCESS_INFO_PTR)->messageBuffer));
                                release_exit(&processInfo, 16, tx_state);
                            }

                            memset(containerName_p, ' ', 16);
                            // Get the next container name
                            EXEC CICS GETNEXT CONTAINER(containerName_p)
                                BROWSETOKEN(btoken)
                                RESP(respcode) RESP2(respcode2);
                        }
                    } while( !sawEnd );
                }
                rsplen = responseLength;

                EXEC CICS ENDBROWSE CONTAINER BROWSETOKEN(btoken);
            }
            else {
                char programNullTerm[9];
                char respstr[9];
                char resp2str[9];
                snprintf(programNullTerm, sizeof(programNullTerm), "%.8s", linkpgmname);
                snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_LINK_PROGRAM_ERROR,
                          "The EXEC CICS LINK PROGRAM %s command could not link from the link server program in the CICS region to the program that the client requested. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                          programNullTerm, respstr, resp2str);
                send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                              strlen((PROCESS_INFO_PTR)->messageBuffer));
                release_exit(&processInfo, 16, tx_state);
            }
        }
        /* Processing for CONTAINER.                        */
        else {
          char channelid[16];

          rspdata_p = &msgbuff4k;                               /*@584680A*/
          strncpy(channelid,"IBM-WAS-ADAPTER ",16);
          /* Default container is CHAR.                     */
          if (linkparms.aclnk_reqcontainertype == 0) {
            EXEC CICS PUT CONTAINER(linkparms.aclnk_reqcontainerid)
              CHANNEL(channelid) FROM(reqdata_p) FLENGTH(reqlen)
              CHAR FROMCCSID(ASCIIPAGE)
              RESP(respcode);
          }
          /* Type 1 indicates request container is BIT.     */
          else {
            EXEC CICS PUT CONTAINER(linkparms.aclnk_reqcontainerid)
              CHANNEL(channelid) FROM(reqdata_p) FLENGTH(reqlen)
              BIT RESP(respcode);
          }

          if (respcode == dfhresp(NORMAL)) {
            DEBUG2("EXEC CICS PUT CONTAINER Successful!");
          }
          else {
            char containerNullTerm[17];
            char respstr[9];
            char resp2str[9];
            snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", linkparms.aclnk_reqcontainerid);
            snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
            snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_PUT_CONTIANER_ERROR,
                      "The EXEC CICS PUT CONTAINER %s command did not add data to the specified container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                      containerNullTerm, respstr, resp2str);
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);           /* @F003691C*/
          }

          if (linkparms.aclnkflags.aclnkflg_lsync == TRUE) {    /* @PM70002A*/
            EXEC CICS LINK PROGRAM(linkpgmname)
              CHANNEL(channelid) RESP(respcode)
              SYNCONRETURN;                                     /* @PM70002A*/
          }                                                     /* @PM70002A*/
          else {                                                /* @PM70002A*/
            EXEC CICS LINK PROGRAM(linkpgmname)
              CHANNEL(channelid) RESP(respcode);
          }                                                     /* @PM70002A*/

          if (respcode == dfhresp(NORMAL)) {
            DEBUG1("EXEC CICS LINK with Container Successful!");

            EXEC CICS GET CONTAINER(linkparms.aclnk_rspcontainerid)
              CHANNEL(channelid) NODATA FLENGTH(rsplen)
              RESP(respcode);

            if (respcode == dfhresp(NORMAL)) {
              DEBUG2("EXEC CICS GET CONTAINER %.16s NODATA Successful!",
                     linkparms.aclnk_rspcontainerid);
              DEBUG2("Response message length: %d",rsplen);
            }
            else {
              char containerNullTerm[17];
              char respstr[9];
              char resp2str[9];
              snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", linkparms.aclnk_rspcontainerid);
              snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
              snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GET_CONTAINER_ERROR,
                        "The EXEC CICS GET CONTAINER %s NODATA command did not retrieve the length of the data. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                        containerNullTerm, respstr, resp2str);
              send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                            strlen((PROCESS_INFO_PTR)->messageBuffer));
              release_exit(&processInfo, 16, tx_state);        /* @F003691C*/
            }

            if (rsplen > 4096) {
              EXEC CICS GETMAIN SET(rspdata_p)
                FLENGTH(rsplen)
                RESP(respcode);

              if (respcode == dfhresp(NORMAL)) {
                DEBUG1("Requested %d bytes. Starting addr: %X",
                       rsplen,rspdata_p);
              }
              else {
                char lenstr[9];
                snprintf(lenstr, sizeof(lenstr), "%d", linkparms.aclnk_reqdatalen);
                ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GETMAIN_RESPONSE_BUFFER_FAIL,
                          "The EXEC CICS GETMAIN command could not allocate storage for a response buffer of %s bytes.",
                          lenstr);
                ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_GETMAIN_ERROR,
                                 "The EXEC CICS GETMAIN command could not allocate storage. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                                 dfheiptr->eibresp,dfheiptr->eibresp2);
                send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                              strlen((PROCESS_INFO_PTR)->messageBuffer));
                release_exit(&processInfo, 16, tx_state);     /* @F003691C*/
              }
            }

            /* Default response container is CHAR.            */
            if (linkparms.aclnk_rspcontainertype == 0) {
              EXEC CICS GET CONTAINER(linkparms.aclnk_rspcontainerid)
                CHANNEL(channelid) INTO(rspdata_p) FLENGTH(rsplen)
                INTOCCSID(ASCIIPAGE)
                RESP(respcode);
            }
            /* If response container is BIT, do not xlate it. */
            else
              EXEC CICS GET CONTAINER(linkparms.aclnk_rspcontainerid)
                CHANNEL(channelid) INTO(rspdata_p) FLENGTH(rsplen)
                RESP(respcode);

            if (respcode == dfhresp(NORMAL)) {
              DEBUG2("EXEC CICS GET CONTAINER Successful!");
            }
            else {
              char containerNullTerm[17];
              char respstr[9];
              char resp2str[9];
              snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", linkparms.aclnk_rspcontainerid);
              snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
              snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GET_CONTAINER_ERROR2,
                        "The EXEC CICS GET CONTAINER %s command did not retrieve data from the specified channel container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                        containerNullTerm, respstr, resp2str);
              send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                            strlen((PROCESS_INFO_PTR)->messageBuffer));
              release_exit(&processInfo, 16, tx_state);        /* @F003691C*/
            }

            /* Delete the request and response containers that we allocated. */
            /* We are not going to bail out if the delete fails. */
            EXEC CICS DELETE CONTAINER(linkparms.aclnk_reqcontainerid)
              CHANNEL(channelid) RESP(respcode);

            if (respcode != dfhresp(NORMAL)) {
                char containerNullTerm[17];
                char respstr[9];
                char resp2str[9];
                snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", linkparms.aclnk_reqcontainerid);
                snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_DELETE_CONTAINER_ERROR,
                          "The EXEC CICS DELETE CONTAINER %s command did not delete the specified channel container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                          containerNullTerm, respstr, resp2str);
            }

            EXEC CICS DELETE CONTAINER(linkparms.aclnk_rspcontainerid)
              CHANNEL(channelid) RESP(respcode);

            if (respcode != dfhresp(NORMAL)) {
                char containerNullTerm[17];
                char respstr[9];
                char resp2str[9];
                snprintf(containerNullTerm, sizeof(containerNullTerm), "%.16s", linkparms.aclnk_rspcontainerid);
                snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
                snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
                ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_DELETE_CONTAINER_ERROR,
                          "The EXEC CICS DELETE CONTAINER %s command did not delete the specified channel container. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                          containerNullTerm, respstr, resp2str);
            }
          }
          else {
            char programNullTerm[9];
            char respstr[9];
            char resp2str[9];
            snprintf(programNullTerm, sizeof(programNullTerm), "%.8s", linkpgmname);
            snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
            snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_LINK_PROGRAM_ERROR,
                      "The EXEC CICS LINK PROGRAM %s command could not link from the link server program in the CICS region to the program that the client requested. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                      programNullTerm, respstr, resp2str);
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);           /* @F003691C*/
          }
        }
      }

      link_count++;                                 /* @580336A*/

      /* ----------------------------------------------------- */
      /* Send the response data back.                          */
      /* --------------------------------------------2@F003691A*/
      if (linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_inactive == 0)
      {                                            /* @F003691A*/
        if (processInfo.baseInfo.verbose>=1) {     /* @578699A*/
          DEBUG1("Calling Send Response API. connhdl: %8.8x%8.8x%8.8x",
                 *((int*)(&(processInfo.connhdl[0]))),
                 *((int*)(&(processInfo.connhdl[4]))),
                 *((int*)(&(processInfo.connhdl[8]))));

          DEBUG1("Calling Send Response, sending %d bytes...",
                 rsplen);
          memcpy(&msgdata,rspdata_p,80);
          DEBUG2("First 80 data bytes: %.80s",msgdata);
        }                                          /* @F003691A*/

        BBOA1SRP ( &(processInfo.connhdl)
                 , &rspdata_p
                 , &rsplen
                 , &rc
                 , &rsn
                 );

        if (rc == 0) {
          DEBUG1("Send Response completed.");
        }
        else {
          ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_SEND_RESPONSE_EXCEPTION_ERROR,
                           "A Send Response Exception occurred. The return code is %s, and the reason code is %s.",
                           rc, rsn);
          release_exit(&processInfo, rc,  tx_state); /* @F003691C*/
        }
      }                                                   /* @F003691A*/

      if (linkparms.aclnk_reqdatalen > 4096) {

        EXEC CICS FREEMAIN DATAPOINTER(reqdata_p) NOHANDLE; /*@584680C*/

        if ((dfheiptr->eibresp) == 0) {                     /*@584680C*/
          DEBUG1("Freed request data. Length %d data: %.8X",
            linkparms.aclnk_reqdatalen,reqdata_p);
        }
        else {
          // TDK TODO: The translated message is wrong (Request /response)
          char lenstr[9];
          snprintf(lenstr, sizeof(lenstr), "%d", linkparms.aclnk_reqdatalen);
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_FREEMAIN_REQUEST_BUFFER_FAIL,
                    "The EXEC CICS FREEMAIN command could not free storage for a response buffer of %s bytes.",
                    lenstr);
          ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_FREEMAIN_ERROR,
                           "The EXEC CICS FREEMAIN command could not free storage. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                           dfheiptr->eibresp,dfheiptr->eibresp2);
          release_exit(&processInfo, 16, tx_state);   /* @F003691C*/
        }
      }

      if ((rsplen > 4096) &&
          (reqdata_p != rspdata_p)) {                       /*@584680C*/

        EXEC CICS FREEMAIN DATAPOINTER(rspdata_p) NOHANDLE; /*@584680C*/

        if ((dfheiptr->eibresp) == 0) {                     /*@584680C*/
          DEBUG1("Freed response data. Length %d data: %X",
            linkparms.aclnk_reqdatalen,rspdata_p);          /*@584680C*/
        }
        else {
          char lenstr[9];
          char addrstr[9];
          snprintf(lenstr, sizeof(lenstr), "%d", linkparms.aclnk_reqdatalen);
          snprintf(addrstr, sizeof(addrstr), "%.8X", rspdata_p);
          ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_FREEMAIN_RESPONSE_BUFFER_FAIL,
                    "The EXEC CICS FREEMAIN command could not free storage for a response buffer of %s bytes at the %s address.",
                    lenstr, addrstr);
          ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_FREEMAIN_ERROR,
                           "The EXEC CICS FREEMAIN command could not free storage. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                           dfheiptr->eibresp,dfheiptr->eibresp2);
          release_exit(&processInfo, 16, tx_state); /* @F003691C*/
        }
      }


      if ((linkparms.aclnkflags.aclnkflg_reuse == TRUE) ||
          (tx_state != NO_TRANSACTION))              /*2@F003691C*/
      {
        /* ----------------------------------------------------- */
        /* If we are running with reuse BBO# tasks on (REU=Y),   */
        /* set up parameter list and call BBOA1RCS API and wait  */
        /* for more requests here instead of releasing the       */
        /* connection now and returning.                         */
        /* We issue an EC SYNCPOINT here to commit everything    */
        /* related to the request we just processed.             */
        /* --------------------------------------------- @579234A*/
        if (tx_state == NO_TRANSACTION)              /* @F003691A*/
        {                                            /* @F003691A*/
          EXEC CICS SYNCPOINT RESP(respcode);          /*@579234A*/
          /* TDK clear TX flags here */
          memset(&linkparms.aclnk_tx_data, 0, sizeof(linkparms.aclnk_tx_data));

          /* ----------------------------------------------------- */
          /* If REUC was specified, check the link task count and  */
          /* if it's reached, end this link task.          @656134A*/
          /* ----------------------------------------------------- */
          if ((reuc != 0) &&                            /* @656134A*/
              (link_count >= reuc)) {                   /* @656134A*/
            DEBUG1("Link count reached reuse count of %d. Terminating link task.",
              reuc);
            loop=0;                                     /* @656134A*/
            break;                                      /* @656134A*/
          }                                             /* @656134A*/

          /* ----------------------------------------------------- */
          /* If REUT was specified, check the link task time and   */
          /* if it's reached, end this link task.          @656134A*/
          /* ----------------------------------------------------- */
          if (reut > 0) {                               /* @656134A*/
            reutf = reut;                               /* @656134A*/
            time(&reut_currtime);                       /* @656134A*/
            DEBUG1("Time difference as of last received request: %f seconds.",
               difftime(reut_currtime,reut_start));     /* @656134A*/

            if ((difftime(reut_currtime,reut_start)) > reutf) {
              DEBUG1("Link task exceeded time limit of %f seconds. Terminating link task.",
                reutf);                                 /* @656134A*/
              loop=0;                                   /* @656134A*/
              break;                                    /* @656134A*/
            }                                           /* @656134A*/
          }                                             /* @656134A*/
        }

        memset(reqsrvname,0x00,sizeof(reqsrvname));    /*@579234A*/
        strncpy(reqsrvname,
                linkparms.aclnk_bboc_svcname,
                8); /* pad with NULLs */               /*@579234A*/
        reqsrvnamel = strlen(reqsrvname);              /*@579234A*/

        DEBUG1("Calling Receive request Specific API for '%.8s'",
          reqsrvname);                                 /*@579234A*/

        BBOA1RCS ( &(processInfo.connhdl)
                 , &reqsrvname
                 , &reqsrvnamel
                 , &reqlen
                 , &async
                 , &rc
                 , &rsn
                 );                                              /*@579234A*/

        if (rc == 0) {                                         /* @F003691C*/
          DEBUG1("Receive request completed. reqdatalen: %d #Links: %d",
               reqlen, link_count);                              /*@580336C*/

          BBOAUCTX* context_p = (BBOAUCTX*)&msgbuff4k;         /* @PI23547M*/
          int context_len = sizeof(msgbuff4k);                 /* @PI23547M*/

          BBOA1GTX(&(processInfo.connhdl), (void**)&context_p, /* @PI23547M*/
                   &context_len, &rc, &rsn, &rv);              /* @PI23547M*/

          if (rc == 0)
          {
            /* ----------------------------------------------------------- */
            /* If this is a transactional registration, see about the      */
            /* state of the transaction.                                   */
            /* ----------------------------------------------------------- */
            if (linkparms.aclnkflags.aclnkflg_tx == TRUE)      /* @F003691A*/
            {                                                  /* @F003691A*/
              BBOAUTXN* txndata_p = (BBOAUTXN*)                /*2@F003691A*/
                (((char*)context_p) + (context_p->auctxtxnoffs));
              struct bboatxc* tx_context_p = 0;                /* @F003691A*/

              /*-----------------------------------------------------------*/
              /* Look for the extended transaction context, containing an  */
              /* XID.                                                      */
              /*-----------------------------------------------------------*/
              if (context_p->auctxvaroffs != 0)                /* @F003691A*/
              {                                                /* @F003691A*/

                struct bboactx* varctxdata_p = (struct bboactx*)/*@F003691A*/
                  ((int)(context_p) +                          /* @F003691A*/
                   (int)(context_p->auctxvaroffs));            /* @F003691A*/
                int numvarctxts = varctxdata_p->actxtnum;      /*2@F003691A*/
                struct bboactxh* cur_ctx_p = (struct bboactxh*)(varctxdata_p + 1);
                while (numvarctxts > 0)                        /* @F003691A*/
                {                                              /* @F003691A*/
                  if (cur_ctx_p->actxhid == BBOATXC_Identifier)/* @F003691A*/
                  {                                            /* @F003691A*/
                    tx_context_p = (struct bboatxc*)cur_ctx_p; /* @F003691A*/
                  }                                            /* @F003691A*/

                  numvarctxts--;                               /* @F003691A*/
                  cur_ctx_p = (struct bboactxh*)               /*2@F003691A*/
                    (((char*)cur_ctx_p) + sizeof(struct bboactxh)
                     + cur_ctx_p->actxhlen);                   /* @F003691A*/
                }                                              /* @F003691A*/
              }                                                /* @F003691A*/

              /*-----------------------------------------------------------*/
              /* Copy in the transactional state.                          */
              /*-----------------------------------------------------------*/
              if (txndata_p->autxnflags.autxnflg_globaltx == 1)/* @F003691A*/
              {                                                /* @F003691A*/
                linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_prepare =
                  txndata_p->autxnflags.autxnflg_prepare;      /*2@F003691A*/
                linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_commit =
                  txndata_p->autxnflags.autxnflg_commit;       /*2@F003691A*/
                linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_backout =
                  txndata_p->autxnflags.autxnflg_rollback;     /*2@F003691A*/
                linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_inactive =
                  txndata_p->autxnflags.autxnflg_inact_rb;     /* @F003691A*/
                linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_tidlist =
                  txndata_p->autxnflags.autxnflg_tidlist;      /*2@F003691A*/

                /*---------------------------------------------------------*/
                /* If an extended transaction context was present, copy    */
                /* the XID out of it into the link parms.  Otherwise, we   */
                /* have a "local" transaction which does not use an XID.   */
                /*---------------------------------------------------------*/
                if (tx_context_p != NULL)                      /* @F003691A*/
                {                                              /* @F003691A*/
                  linkparms.aclnk_tx_data.aclnk_tx_formatid =  /* @F003691A*/
                    tx_context_p->atxcver1.atxcxidfid;         /* @F003691A*/
                  linkparms.aclnk_tx_data.aclnk_tx_tidlen =    /* @F003691A*/
                    tx_context_p->atxcver1.atxcxidgtlen +      /* @F003691A*/
                    tx_context_p->atxcver1.atxcxidbqlen;       /* @F003691A*/
                  linkparms.aclnk_tx_data.aclnk_tx_bquallen =  /* @F003691A*/
                    tx_context_p->atxcver1.atxcxidbqlen;       /* @F003691A*/
                  memcpy(linkparms.aclnk_tx_data.aclnk_tx_tid, /* @F003691A*/
                         tx_context_p->atxcver1.atxcxiddata,   /*2@F003691A*/
                         sizeof(linkparms.aclnk_tx_data.aclnk_tx_tid));
                }                                              /* @F003691A*/
                else                                           /* @F003691A*/
                {                                              /* @F003691A*/
                  /*-------------------------------------------------------*/
                  /* For an inactivity timeout, we need to manually put    */
                  /* the XID in the link parms since the message did not   */
                  /* originate from WAS but is on behalf of the WAS tran   */
                  /* in progress.                                          */
                  /*-------------------------------------------------------*/
                  if (txndata_p->autxnflags.autxnflg_inact_rb == 1) /* @F003691A*/
                  {                                            /*5@F003691A*/
                    linkparms.aclnk_tx_data.aclnk_tx_formatid = cur_formatid;
                    linkparms.aclnk_tx_data.aclnk_tx_bquallen = cur_bqual_len;
                    linkparms.aclnk_tx_data.aclnk_tx_tidlen = cur_tid_len;
                    memcpy(linkparms.aclnk_tx_data.aclnk_tx_tid, cur_tid,
                           cur_tid_len);                       /* @F003691A*/
                  }                                            /* @F003691A*/
                  else                                         /* @F003691A*/
                  {                                            /* @F003691A*/
                    /*-----------------------------------------------------*/
                    /* No XID means we have an RMLT.                       */
                    /*-----------------------------------------------------*/
                    linkparms.aclnk_tx_data.aclnk_tx_formatid = /* @F003691C*/
                      OTSLTFORMATID; /* @F003691C*/
                    linkparms.aclnk_tx_data.aclnk_tx_tidlen = 8;/*4@F003691A*/
                    linkparms.aclnk_tx_data.aclnk_tx_bquallen = 4;
                    memcpy(linkparms.aclnk_tx_data.aclnk_tx_tid,
                           "CBLTCBLT", 8);
                  }
                }

                /*---------------------------------------------------------*/
                /* "globaltx" is used loosely here to mean any transaction */
                /*---------------------------------------------------------*/
                linkparms.aclnkflags.aclnkflg_globaltx = 1;    /* @F003691A*/

                /*---------------------------------------------------------*/
                /* See if this request is for recovery work.  Recovery     */
                /* work deals with shunted transactions.                   */
                /*---------------------------------------------------------*/
                linkparms.aclnk_tx_data.aclnk_tx_flags.aclnkflg_tx_rec =
                  txndata_p->autxnflags.autxnflg_rec;          /*2@F003691A*/
              }                                                /* @F003691A*/
              else                                             /* @F003691A*/
              {                                                /* @F003691A*/
                linkparms.aclnkflags.aclnkflg_globaltx = 0;    /* @F003691A*/
              }                                                /* @F003691A*/
            }                                                  /* @F003691A*/

            /* ----------------------------------------------------------- */
            /* Make sure the link task transaction ID was not re-defined.  */
            /* If it was, we need to fail the request.                     */
            /* ----------------------------------------------------------- */
            BBOAUCIC* cicsdata_p = (BBOAUCIC *)                /* @PI23547A*/
              (((char*)context_p) + (context_p->auctxcicsoffs));/* @PI23547A*/
            if ((memcmp(cicsdata_p->aucicslnktranid, "    ", 4) != 0) &&
                (*((int*)(cicsdata_p->aucicslnktranid)) != 0) &&/* @760975A*/
                (memcmp(cicsdata_p->aucicslnktranid,           /* @PI23547A*/
                        dfheiptr->eibtrnid, 4) != 0))          /* @PI23547A*/
            {                                                  /* @PI23547A*/
              reqlen = 1;                                      /* @PI23547A*/
              char tranIdNullTerm[5];
              snprintf(tranIdNullTerm, sizeof(tranIdNullTerm), "%.4s", 
                       cicsdata_p->aucicslnktranid);
              BBOA1GET(&(processInfo.connhdl), &reqdata_p,     /* @PI23547A*/
                       &reqlen, &rc, &rsn, &rv);               /* @PI23547A*/
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_REUSE_WITH_TRAN_ID,
                        "The %s link transaction ID caused an error to occur with the link server set to REU=Y.", 
                        tranIdNullTerm);
              send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                            strlen((PROCESS_INFO_PTR)->messageBuffer));
              release_exit(&processInfo, 16, tx_state);        /* @PI23547A*/
            }                                                  /* @PI23547A*/
          }                                                    /* @F003691A*/
          else                                                 /* @PI23547M*/
          {                                                    /* @PI23547M*/
            /* ----------------------------------------------------------- */
            /* Could not get transactional flags.  Need to backout tran    */
            /* and close the connection.                                   */
            /* ----------------------------------------------------------- */
            reqlen = 1;                                        /*2@F003691A*/
            BBOA1GET(&(processInfo.connhdl), &reqdata_p, &reqlen, &rc, &rsn, &rv);
            ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_GET_CONTEXT_TX_ERROR,
                      "The link task could not retrieve the transaction context data for the next method, so the transaction was backed out.");
            send_exc_resp(&processInfo, (PROCESS_INFO_PTR)->messageBuffer,
                          strlen((PROCESS_INFO_PTR)->messageBuffer));
            release_exit(&processInfo, 16, tx_state);          /* @F003691C*/
          }                                                    /* @PI23547M*/

          /* ------------------------------------------------------------- */
          /* Update the service that we're supposed to link to.            */
          /* ------------------------------------------------------------- */
          memset(linkparms.aclnk_servname,' ',
                 sizeof(linkparms.aclnk_servname));              /*@584680A*/

          strncpy(linkparms.aclnk_servname,reqsrvname,
                  ((reqsrvnamel>8)?8:reqsrvnamel));              /*@584680A*/
          linkparms.aclnk_reqdatalen = reqlen;                   /*@579234A*/
          DEBUG1("Receive request service name: %.8s",
                 linkparms.aclnk_servname);                      /*@584680A*/

          /* ------------------------------------------------------------- */
          /* The channel/container information may have changed for this   */
          /* call, so update them.                                         */
          /* ------------------------------------------------------------- */
          BBOAUCIC* cicsdata_p = (BBOAUCIC *) ((int)(context_p) +
                                     (int)(context_p->auctxcicsoffs));
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

        }
        else                                                     /*@579234A*/
        {                                                        /*@579234A*/
          if ((rc == 8) && (rsn == 19)) {                        /*@579234A*/
            /* ----------------------------------------------------------- */
            /* A COMM error has occurred.  It could be that the link       */
            /* server is shutting down.  Look in the link server list to   */
            /* see if we are still there.                                  */
            /* ----------------------------------------------------------- */
            char countstr[9];
            snprintf(countstr, sizeof(countstr), "%d", link_count);
            struct bboa_link_srvr_status_node* ls_node =       /* @PI24358A*/
              findLinkServer((char*)linkparms.aclnk_regname,
                             (char*)linkparms.aclnk_tasknum);       /* @PI53321*/
            if ((ls_node == NULL) || (ls_node->urg_pend != 0)) /* @PI24358C*/
            {                                                   /*@F003691A*/
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_TERMINATING_WITH_COUNT,
                        "A request was received to stop the adapters server.  The server is stopping. The link count is %s.",
                        countstr);
            }                                                   /*@F003691A*/
            else                                                /*@F003691A*/
            {                                                   /*@F003691A*/
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_EXIT_COMM_FAILURE,
                        "The link task is stopping because of a COMM failure.  The link count is %s.",
                        countstr);
            }                                                   /*@F003691A*/

            if (tx_state == PREPARED_TRANSACTION)               /*@F003691A*/
            {                                                   /*@F003691A*/
              release_exit(&processInfo, 16, tx_state);         /*@F003691A*/
            }                                                   /*@F003691A*/

            rc  = 0;                                             /*@579234A*/
            rsn = 0;                                             /*@579234A*/
            loop = 0;                                          /* @F003691C*/
          }                                                      /*@579234A*/
          else {                                                 /*@579234A*/
              char rcstr[9];
              char rsnstr[9];
              char countstr[9];
              snprintf(rcstr, sizeof(rcstr), "%d", rc);
              snprintf(rsnstr, sizeof(rsnstr), "%d", rsn);
              snprintf(countstr, sizeof(countstr), "%d", link_count);
              ISSUE_MSG(PROCESS_INFO_PTR, LINK_SERVER_RECEIVE_REQUEST_ERROR_COUNT,
                        "The Receive Request Specific API could not receive a request from the Liberty profile server. The link count is %s. The return code is %s, and the reason code is %s.",
                        countstr, rcstr, rsnstr);

            release_exit(&processInfo, rc, tx_state);            /*@579234C*/
          }
        }
      }
      else /* No reuse, no current transaction */               /* @F003691C*/
      {                                                         /* @F003691A*/
        loop = 0;                                               /* @F003691A*/
      }                                                         /* @F003691A*/
    }

    /* ----------------------------------------------------- */
    /* Release the connection back to the pool.              */
    /* ----------------------------------------------------- */
    DEBUG1("Releasing conn back to pool. connhdl: %8.8x%8.8x%8.8x",
           *((int*)(&(processInfo.connhdl[0]))),
           *((int*)(&(processInfo.connhdl[4]))),
           *((int*)(&(processInfo.connhdl[8]))));

    BBOA1CNR ( &(processInfo.connhdl)
             , &rc
             , &rsn
            );

    if (rc == 0) {
      DEBUG1("Release connection back to pool completed.");
    }
    else {
      if ((rc == 8) && (rsn == 38)) {              /*@579234A*/
        ISSUE_MSG(PROCESS_INFO_PTR, LINK_TASK_STALE_HANDLE,
                  "The connection handle was not valid or a STOP request was received.");
        rc  = 0;                                   /*@579234A*/
        rsn = 0;                                   /*@579234A*/
      }                                            /*@579234A*/
      else {                                       /*@579234A*/
        ISSUE_MSG_RC_RSN(PROCESS_INFO_PTR, LINK_TASK_RELEASE_CONNECTION_ERROR,
                         "The Connection Release API could not return the connection to the pool. The return code is %s, and the reason code is %s. ",
                         rc, rsn);
      }                                            /*@579234A*/
    }

    if (processInfo.baseInfo.verbose>=1) {                /* @578699A*/
      print_exit_time(&processInfo, rc, rsn);
    }

    EXEC CICS DELETEQ TS QUEUE(processInfo.tsqname) RESP(respcode);/*@F003691C*/

    closeWolaMessageCatalog(&((PROCESS_INFO_PTR)->baseInfo));

    return(0);
  }
#undef PROCESS_INFO_PTR
