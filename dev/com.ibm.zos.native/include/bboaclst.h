/*----------------------------------------------------------------------
 *
 *  Module Name:  BBOACLST.H
 *
 *  Descriptive Name: Optimized Adapters CICS Link Server Tracing
 *      Acronym:  N/A
 *
 *  Proprietary Statement
 *
 * IBM Confidential
 * OCO Source Materials
 * 5655-I35 (C) Copyright IBM Corp. 2010
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * Status = H28W800
 *
 *  Function:  Tracing routines for the link server.
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
 *    C++: #include <private/bboaclst.h>
 *
 *  Deleted by: System
 *
 *  Frequency:
 *    One per system
 *
 *  Dependencies:  None
 *
 *  No Message IDs used in this part:
 *
 *  Change Activity:
 *   $PM70002, H28W800, 20120814, PDFG: Creation
 *   $PM88133, H28W800, 20130501, PDFG: eibtaskn is packed decimal
 *
 *----------------------------------------------------------------------*/
#ifndef _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#define _BBOA_PRIVATE_BBOACLST_H_INCLUDE

#ifndef _BBOA_PRIVATE_BBOACVB_H_INCLUDE                   /* @PM88133A*/
#include "bboacvb.h"                              /* @PM88133A*/
#define _BBOA_PRIVATE_BBOACVB_H_INCLUDE                   /* @PM88133A*/
#endif                                                    /* @PM88133A*/

#include "gen/wola_cics_messages.h"

#include <nl_types.h>

 /* Information about a CICS link server task */
 struct cicsLinkServerProcessInfo {
   unsigned char eyecatcher[8]; /* BBOACLPI */
   unsigned char queried_tdq; /* Whether we queried the TDQ record len */
   unsigned char wrtqname[4]; /* The name of the TDQ to write to */
   char tskno_for_tsq[9]; /* Printable task number used for TSQ */
   unsigned char tdqwarn;     /* Have we warned about TDQ problems? */
   unsigned char verbose;     /* Trace level */
   unsigned char tried_catalog; /* Tried to open message catalog. */
   unsigned short writemsgl;  /* The length of the TQD records */
   nl_catd msg_catalog;       /* The message catalog */
 };

#define CICS_LINK_SERVER_PROCESS_INFO_EYE "BBOACLPI"

 void write_tdq(struct cicsLinkServerProcessInfo* info_p, char* msg);

 /* ----------------------------------------------------------------- */
 /* Initialize the cics link server process info struct.  The caller  */
 /* must have already addressed the EIB.                              */
 /* ----------------------------------------------------------------- */
 void initializeCicsLinkServerProcessInfo(struct cicsLinkServerProcessInfo* info_p) {
   memset(info_p, 0, sizeof(*info_p));
   memcpy(info_p->eyecatcher, CICS_LINK_SERVER_PROCESS_INFO_EYE,
          sizeof(info_p->eyecatcher));
   
   snprintf(info_p->tskno_for_tsq,                        /* @PM88131C*/
            sizeof(info_p->tskno_for_tsq), 
            "%.8i",              
            convertPackedDecimalToInt(dfheiptr->eibtaskn, 7));
 }
   
 /* ----------------------------------------------------------------- */
 /* Obtain the translated messages from the message catalog.          */
 /* ----------------------------------------------------------------- */
 char* getTranslatedMessageById(struct cicsLinkServerProcessInfo* info_p,
                                int messageId, char * defaultMessage) {
     int rc = 0;
     char* translatedMessage = NULL;

     //TODO Use LANG= environment variable to derive the name argument of catopen() once
     //we actually have catalogs for languages other than ENGLISH. So basically
     //get the value of LANG and append that to the NATIVE_NLSCATALOG more or less.
     //Or investigate if we can exploit the %L in the NLSPATH, either way should work.
     //
     //The catopen relies on the NLSPATH being set as shown below.
     //NLSPATH = ${WLP_INSTALL_DIR}/lib/native/zos/s390x/nls/%N.cat:${NLSPATH}
     if (info_p->tried_catalog == 0) {
         info_p->msg_catalog = catopen("wola_cics_messages", 0);

         if (info_p->msg_catalog == (nl_catd) -1) {
             write_tdq(info_p, "Error: Could not open WOLA message catalog wola_cics_messages.cat");
         }

         info_p->tried_catalog = 1;
     }

     if (info_p->msg_catalog != (nl_catd) -1) {
         translatedMessage = catgets(info_p->msg_catalog, 1, messageId, defaultMessage);
         if (translatedMessage == defaultMessage) {
             char errormsg[128];
             snprintf(errormsg, sizeof(errormsg),
                      "Error: Could not retrieve message %i from wola_cics_messages.cat",
                      messageId);
             write_tdq(info_p, errormsg);
         }
     } else {
         translatedMessage = defaultMessage;
     }

     return translatedMessage;
 }

 /* ----------------------------------------------------------------- */
 /* Close the message catalog.                                        */
 /* ----------------------------------------------------------------- */
 void closeWolaMessageCatalog(struct cicsLinkServerProcessInfo* info_p) {
     if (info_p->tried_catalog == 1) {
         if (info_p->msg_catalog != (nl_catd) -1) {
             catclose(info_p->msg_catalog);
         }
         info_p->msg_catalog = NULL;
     }

     info_p->tried_catalog = 0;
 }

 /* ----------------------------------------------------------------- */
 /* write_tdq() : Write message to CICS TDQ. If unable to, write to   */
 /* stdout with printf.                                               */
 /* ----------------------------------------------------------------- */
 void write_tdq(struct cicsLinkServerProcessInfo* info_p, char* msg) {
   int respcode = 0;
   char tdqmsg[512];
   struct tm *xtime;
   time_t msgtime;
   char formatted_time[26];                                /* @633440A*/
   short i=1;                                              /* @633440C*/
   short try_again = FALSE;                                /* @633440A*/

   /*--------------------------------------------------------------------*/
   /* See how big the transient data queue records are, if we can.  If   */
   /* we try to write a record that is too big, we get an error code     */
   /* from CICS.                                                         */
   /*--------------------------------------------------------------------*/
   if (info_p->queried_tdq == FALSE) {
     int reclen = 0;
     info_p->writemsgl = sizeof(tdqmsg); /* Initialize */
     info_p->queried_tdq = TRUE;
     EXEC CICS INQUIRE TDQUEUE(info_p->wrtqname) RECORDLENGTH(reclen)
       RESP(respcode);

     if (respcode == dfhresp(NORMAL)) {
       if (reclen < info_p->writemsgl) {
         info_p->writemsgl = reclen - 5;
       }
     }
     else {
       char* tdqMessage_p = getTranslatedMessageById(info_p, TDQ_QUERY_ERROR,
         "Querying the size of the %s transient data queue (TDQ) failed.  The response codes are: eibresp %s eibresp2 %s");
       char tdqNullTerm[5];
       char respStr[9];
       char resp2Str[9];
       snprintf(tdqNullTerm, sizeof(tdqNullTerm), "%.4s", info_p->wrtqname);
       snprintf(respStr, sizeof(respStr), "%d", dfheiptr->eibresp);
       snprintf(resp2Str, sizeof(resp2Str), "%d", dfheiptr->eibresp2);
       printf(tdqMessage_p, tdqNullTerm, respStr, resp2Str);
     }
   }

   /*--------------------------------------------------------------------*/
   /* Format the current time.                                           */
   /*--------------------------------------------------------------------*/
   time(&msgtime);
   xtime = localtime(&msgtime);
   asctime_r(xtime, formatted_time);                          /* @633440C*/
   for (i = 0; i < sizeof(formatted_time); i++)               /* @633440C*/
   {                                                          /* @633440C*/
     if (formatted_time[i] == '\n') formatted_time[i] = ' ';  /* @633440C*/
   }                                                          /* @633440C*/

   /*--------------------------------------------------------------------*/
   /* Construct the message.  Make sure we'll be able to fit the message */
   /* in the record size of the transient data queue.  snprintf will     */
   /* write a NULL character at the end of the buffer, even if it had to */
   /* truncate the message.  So it's safe to use strlen on the buffer.   */
   /*--------------------------------------------------------------------*/
   snprintf(tdqmsg, info_p->writemsgl, "%.4s %.26s%.7s %s\n",
            dfheiptr->eibtrnid,
            formatted_time,                                   /* @633440C*/
            (info_p->tskno_for_tsq) + 1,                     /* @PM88133C*/
            msg);

   i = strlen(tdqmsg);

   /*--------------------------------------------------------------------*/
   /* Write the record.  If the write fails, use 'printf' instead.       */
   /*--------------------------------------------------------------------*/
   EXEC CICS WRITEQ TD QUEUE(info_p->wrtqname) FROM(tdqmsg) LENGTH(i)
       RESP(respcode);                                        /* @633440C*/

   if (respcode != dfhresp(NORMAL)) {
     if (info_p->tdqwarn == FALSE) {
       char* tdqMessage = getTranslatedMessageById(info_p, WRITE_BBOQ_ERROR,
         "The %s extra partition transient queue cannot log messages. The EIBRESP value is %s, and the EIBRESP2 value is %s.");
       char tdqNullTerm[5];
       char respStr[9];
       char resp2Str[9];
       snprintf(tdqNullTerm, sizeof(tdqNullTerm), "%.4s", info_p->wrtqname);
       snprintf(respStr, sizeof(respStr), "%d", dfheiptr->eibresp);
       snprintf(resp2Str, sizeof(resp2Str), "%d", dfheiptr->eibresp2);
       printf(tdqMessage, tdqNullTerm, respStr, resp2Str);
     }
     info_p->tdqwarn = TRUE;
     printf("%.7s ", (info_p->tskno_for_tsq) + 1);           /* @PM88133C*/
     printf(msg);
   }
 }

 /****************  DEBUG MACROS USED BY CICS LINK SERVER ***********/
#undef DEBUG0_BASE
#define DEBUG0_BASE(info_p, buf_p, buflen, ...) {  \
  char* traceDataBuf_p = ((char*)buf_p) + (buflen / 2); \
  snprintf(traceDataBuf_p, buflen / 2, __VA_ARGS__); \
  char* traceMessage = getTranslatedMessageById(info_p, TRACE_LEVEL_0, "WOLA TRACE 0: %s"); \
  snprintf(buf_p,buflen/2,traceMessage,traceDataBuf_p); \
  write_tdq((info_p), buf_p); }

#undef DEBUG1_BASE
#define DEBUG1_BASE(info_p, buf_p, buflen, ...)  {  \
  if ((info_p)->verbose >= 1) {                  \
    char* traceDataBuf_p = ((char*)buf_p) + (buflen / 2); \
    snprintf(traceDataBuf_p, buflen / 2, __VA_ARGS__); \
    char* traceMessage = getTranslatedMessageById(info_p, TRACE_LEVEL_1, "WOLA TRACE 1: %s"); \
    snprintf(buf_p, buflen/2,traceMessage,traceDataBuf_p); \
    write_tdq((info_p), buf_p);                  \
  }}

#undef DEBUG2_BASE
#define DEBUG2_BASE(info_p, buf_p, buflen, ...)  {  \
  if ((info_p)->verbose >= 2) {                  \
    char* traceDataBuf_p = ((char*)buf_p) + (buflen / 2); \
    snprintf(traceDataBuf_p, buflen / 2, __VA_ARGS__); \
    char* traceMessage = getTranslatedMessageById(info_p, TRACE_LEVEL_2, "WOLA TRACE 2: %s"); \
    snprintf(buf_p, buflen/2,traceMessage,traceDataBuf_p); \
    write_tdq((info_p), buf_p);                  \
  }}

#endif
