/*----------------------------------------------------------------------
 *
 *  Module Name:  BBOACNTL.CPP
 *
 *  Descriptive Name: Optimized Adapters CICS Control transaction
 *      Acronym:  N/A
 *
 *  Proprietary Statement
 *
 * IBM Confidential
 * OCO Source Materials
 * 5655-I35 (C) Copyright IBM Corp. 2008
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * Status = H28W700
 *
 *  Function:  Provide operational capabilities for adapters support
 *             under CICS.  This includes starting and stopping the
 *             Task Related User Exit, and starting/stopping WAS OLA
 *             CICS Link Server tasks. Can also do REGISTER and
 *             UNREGISTER and set tracing levels and the output TDQ
 *             with this transaction.  The CICS transaction id this
 *             executes under is 'BBOC'. There is no specific
 *             requirement in this code that it run with this name,
 *             so customers may change it.
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
 *   BBOA8000I - BBOA8002I: Debug messages
 *   BBOA8003x - BBOA8199x: Error/Warning messages
 *
 *  Change Activity:
 *   $LI4799        , H28W700, 20081230, JTM : Initial Implementation
 *   $LI4798I7-08   , H28W700, 20090213, JTM : Support PLTPI and EC LINK
 *                                             with COMMAREA callers.
 *   $576822        , H28W700, 20090223, JTM : Fixup tracing under CICS.
 *   $LI4798I7-01   , H28W700, 20090228, FGOL: Change sec propagation flag name
 *   $578679        , H28W700, 20090306, JTM : Performance, reduce tracing
 *                                             overhead in main path.
 *                                             Cleanup messages.
 *   $579234        , H28W700, 20090310, JTM : Support for reusing BBO# tasks
 *                                             when REU=Y is requested.
 *   $F003691       , H28W700, 20091109, PDFG: Use commarea len, don't depend
 *                                             on parm area ending in " "
 *   $F003691-20607 , H28W700, 20100203, PDFG: Keep track of link servers
 *   $656134        , H28W700, 20100608, JTM : Add reuse count and time parms.
 *   $633440        , H28W800, 20101014, PDFG: Don't truncate BBOQ messages
 *   $PM70002       , H28W800, 20120730, JTM:  Add SYNCONRETURN option to
 *                                             support TXN=N link server that
 *                                             DPLs to programs that EC SYNC.
 *   $PM70002       , H28W800, 20120809, PDFG: Restructure for service.
 *   $PM74646       , H28W700, 20121023, PDFG: Transaction resync/retry
 *   $PM79156       , H28W800, 20121227, PDFG: Unregister force 8/8 is not
 *                                             really an error.
 *   $PM88133       , H28W800, 20130501, PDFG: eibtaskn is packed decimal
 *   $PM90865       , H28W800, 20130610, PDFG: Externalize COMMAREA
 *                                       JTM:  Add LIST_SRVR command
 *   $PI27022       , H28W855, 20141016, MJA:  Add WAS build level message
 *   $PI32026       , H2W8555, 20150417, MJA:  Add additional diagnostics
 *                                             to CICS Function call errors
 *   $PI24358       , H28W800, 20140904, PDFG: Deal with pending unregister
 *   $PI53321       , H27W855, 20160119, MJA:  Support for CICS TS 53
 *   $PI52665       , H2W8555, 20151108, JTM:  Add new options to pass TRANSID/SYSID on
 *                                             EC LINK, new options to retry start link server
 *                                             (RETRY), and the ability to accept BBOC
 *                                             parameters using DOC templates.
 *
 *----------------------------------------------------------------------*/
 #pragma langlvl(extended)
 #define TRUE 1
 #define FALSE 0

 #include <stdio.h>
 #include <cics.h>
 #include <time.h>
 #include <string.h>
 #include <stdlib.h>
 #include <stdbool.h>
 #include <stdarg.h>
 #include "include/bboaapi.h"
 #include "include/server_wola_cics_link_server.h"
 #include <ctype.h>                              /* @578679A*/

#ifndef _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#include "include/bboaclst.h"
#define _BBOA_PRIVATE_BBOACLST_H_INCLUDE
#endif

#ifndef _BBOA_PRIVATE_BBOACSVL_H_INCLUDE                  /* @F003691A*/
#include "include/bboacsvl.h"                             /* @F003691A*/
#define _BBOA_PRIVATE_BBOACSVL_H_INCLUDE                  /* @F003691A*/
#endif                                                    /* @F003691A*/

/* ----------------------------------------------------------------- */
 /* Constants                                                         */
 /* ----------------------------------------------------------------- */
#define REGNAME_KEY "RGN="
#define SERVERNAME_KEY "SVN="
#define NODENAME_KEY "NDN="
#define DAEMONGROUPNAME_KEY "DGN="
#define MINCONN_KEY "MNC="
#define MAXCONN_KEY "MXC="
#define TRANSACTIONAL_KEY "TXN="
#define SECURITY_KEY "SEC="
#define SERVERTRANID_KEY "STX="
#define LINKTRANID_KEY "LTX="
#define LINKSYNC_KEY "LSYNC="
#define SERVICENAME_KEY "SVC="
#define LINKTASKREUSE_KEY "REU="
#define LINKTASKREUSECOUNT_KEY "REUC="
#define LINKTASKREUSETIME_KEY "REUT="
#define EXITTRACE_KEY "XTR="
#define TRACE_KEY "TRC="
#define TDQ_KEY "TDQ="
#define UOW_KEY "UOW="                                    /* @PM74646A*/
#define LTSQ_KEY "LTSQ="                                  /* @PM90865A*/
#define REMOTETRANID_KEY "RTX="                           /* @PI52665A*/
#define REMOTESYSID_KEY "RTXSYS="                         /* @PI52665A*/
#define REMOTETXPROPAGATE_KEY "RTXP="                     /* @PI52665A*/
#define RETRY_KEY "RETRY="                                /* @PI52665A*/
#define RETRY_MODE_KEY "RETMODE="                         /* @PI52665A*/
#define RETRY_COUNT_KEY "RETCNT="                         /* @PI52665A*/
#define RETRY_INTERVAL_KEY "RETINT="                      /* @PI52665A*/
#define DOC_TEMPLATE_NAME_KEY "NAME="                     /* @PI52665A*/

#define DEBUG0(...) DEBUG0_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#define DEBUG1(...) DEBUG1_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#define DEBUG2(...) DEBUG2_BASE(&((PROCESS_INFO_PTR)->baseInfo), (PROCESS_INFO_PTR)->messageBuffer, sizeof((PROCESS_INFO_PTR)->messageBuffer), __VA_ARGS__)
#undef PROCESS_INFO_PTR

/* ----------------------------------------------------------------- */
/* Parameters supplied on control task.                              */
/* ----------------------------------------------------------------- */
struct controlParm {
  union {
    int intData;           /* Parameter stored as an integer. */
    char charData[16];     /* Parameter stored as a string.   */
  };

  unsigned char charDataLen; /* Set charData length */
  unsigned char defaulted; /* Set if the default value was used. */
  unsigned char invalid; /* Set if requested value was invalid. */
};

struct controlParms {
  struct controlParm txn;     /* Transaction support */
  struct controlParm xtr;     /* TRUE tracing in assembler */
  struct controlParm trc;     /* Regular tracing level */
  struct controlParm sec;     /* Security support */
  struct controlParm reu;     /* Link task reuse support */
  struct controlParm lsync;   /* Link with SYNCONRETURN support */
  struct controlParm reuc;    /* Link task reuse counter */
  struct controlParm reut;    /* Link task reuse time */
  struct controlParm stx;     /* Server task transaction ID */
  struct controlParm ltx;     /* Link task transaction ID */
  struct controlParm dgn;     /* Daemon group name */
  struct controlParm nodename; /* Node name */
  struct controlParm svrname; /* Server name */
  struct controlParm regname; /* Register name */
  struct controlParm svcname; /* Service name */
  struct controlParm minconn; /* Minimum connection count */
  struct controlParm maxconn; /* Maximum connection count */
  struct controlParm wrtqname; /* TD Queue */
  struct controlParm uowid;   /* UOW ID for resync */    /* @PM74646A*/
  struct controlParm ltsq;    /* List servers TSQ name      @PM90865A*/
  struct controlParm rtx;     /* Remote Transaction ID      @PI52665A*/
  struct controlParm rtxsys;  /* Remote SYSID               @PI52665A*/
  struct controlParm rtxp;    /* Remote Tran.propagate Y|N  @PI52665A*/
  struct controlParm retry;   /* Retry start server Y|N     @PI52665A*/
  struct controlParm retmode; /* Retry mode                 @PI52665A*/
  struct controlParm retcnt;  /* Retry count                @PI52665A*/
  struct controlParm retint;  /* Retry interval (secs)      @PI52665A*/
  struct controlParm doctname;/* Document template name     @PI52665A*/
};

 /* ----------------------------------------------------------------- */
 /* COMMAREA mapping                                                  */
 /* ----------------------------------------------------------------- */
#define BBOACNTL_COMMAREA_V1 1

#define BBOACNTL_COMMAREA_RC_SKIP -1
#define BBOACNTL_COMMAREA_RC_NORMAL 0
#define BBOACNTL_COMMAREA_RC_WARN 4
#define BBOACNTL_COMMAREA_RC_ERROR 8

#define BBOACNTL_COMMAREA_RSN_NONE 0

 struct inputCommarea {
   char command[512];                                    /* @PI52665C*/
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


 void writeToCommareaV0(struct cicsControlTaskInfo*, char* buffer, int rc);

/* ----------------------------------------------------------------- */
/* Information about this task.                                      */
/* ----------------------------------------------------------------- */
struct cicsControlTaskInfo {
  struct cicsLinkServerProcessInfo baseInfo; /* Common information   */
  struct BBOACNTL_Commarea* commarea_p; /* Pointer to the COMM area, */
                                        /* if any           @PM90865A*/
  int commarea_l;   /* Length of the COMM area */
  int commarea_mcpy_size;
  void (*commarea_write_fcn)(struct cicsControlTaskInfo*, char*, int); /*= writeToCommareaV0();*/
  unsigned char sendOK; /* Is it OK to issue EXEC CICS SEND? */
  unsigned char commareaOK; /* Is there a COMMAREA?         @PI52665A*/
  unsigned char _available[2]; /* Available for use */
  char messageBuffer[512]; /* Message buffer */
};

 /* Write to a V0 commarea (legacy) */
 void writeToCommareaV0(struct cicsControlTaskInfo* info_p, char* buffer, int rc) {
   strncpy(info_p->commarea_p->output.message, buffer,
           info_p->commarea_mcpy_size);
 }

 /* Write to a V1 (or greater) commarea */
 void writeToCommareaV1(struct cicsControlTaskInfo* info_p, char* buffer, int rc) {
   if (info_p->commarea_p->output.returnCode == BBOACNTL_COMMAREA_RC_NORMAL) {
     writeToCommareaV0(info_p, buffer, rc);
     info_p->commarea_p->output.returnCode = rc;
   }
 }

#define PROCESS_INFO_PTR info_p
 /* ----------------------------------------------------------------- */
 /* Write a message to the comm area or to the terminal.              */
 /* TODO: Now that we're not calling DEBUG0 in here anymore, can we   */
 /*       use the buffer hung off info_p instead of buffer_p?         */
 /* ----------------------------------------------------------------- */
void ISSUE_MSG(struct cicsControlTaskInfo* info_p, char* buffer_p, short buflen,
               int commareaReturnCode, int messageID, char* defaultMessage,  ...) {
  char* translatedMessage =
      getTranslatedMessageById(&(info_p->baseInfo), messageID, defaultMessage);
  va_list vargs;
  va_start(vargs, defaultMessage);
  vsnprintf(buffer_p, buflen, translatedMessage, vargs);
  va_end(vargs);
  write_tdq(&(info_p->baseInfo), buffer_p);
  if (commareaReturnCode != BBOACNTL_COMMAREA_RC_SKIP) {
    if (info_p->sendOK == '1') {
      EXEC CICS SEND TEXT FROM(buffer_p) LENGTH(buflen) ERASE FREEKB;
    } else {
      if (info_p->commareaOK == '1') {                   /* @PI52665A*/
        info_p->commarea_write_fcn(info_p, buffer_p, commareaReturnCode);
      }
    }
  }
}
#undef PROCESS_INFO_PTR


/* ------------------------------------------------------------------*/
/* Convert EIBRCODE to a null terminated hex string that can be      */
/* traced.  The returned character string should be deleted with     */
/* a call to free() after it is printed.                             */
/* ----------------------------------------------------------------- */
char* eibrcodeToHexString(char* data_p)                  /* PI32026 */
{                                                        /* PI32026 */
  char* string_p = NULL;                                 /* PI32026 */
  string_p = (char*)malloc(13);                          /* PI32026 */
  if( string_p != NULL ) {
    sprintf(string_p,"%02x%02x%02x%02x%02x%02x",
       data_p[0],
       data_p[1],
       data_p[2],
       data_p[3],
       data_p[4],
       data_p[5]);
  } else {
     perror("BBOACLNK");
  }
  return  string_p;                                       /* PI32026 */
}

 /* ----------------------------------------------------------------- */
 /* Parses a string parameter.                                        */
 /* ----------------------------------------------------------------- */
 void parseStringParameter(char* rawParms_p, struct controlParm* parm_p,
                          char* key_p, unsigned int maxLen,
                          char* defaultValue) {
   char* curParm_p = strstr(rawParms_p, key_p);
   char* curParmEnd_p = NULL;
   int   curParmLen = 0;
   int   keyLen = strlen(key_p);

   if (curParm_p > 0) {
     curParmEnd_p = strstr(curParm_p + keyLen, " ");     /* look for a blank */
     if (curParmEnd_p == 0)
       curParmEnd_p = strchr(curParm_p + keyLen, 0x00);  /* look for a null  */

     curParmLen = int (curParmEnd_p - (curParm_p + keyLen));
     if ((curParmLen > maxLen) ||
         (curParmEnd_p == 0)) { /* TODO: Check lower bound */
       parm_p->invalid = TRUE;
     } else {
       parm_p->charDataLen = curParmLen;
       memcpy(parm_p->charData, curParm_p + keyLen, curParmLen);
       memset(&(parm_p->charData[curParmLen]), ' ', sizeof(parm_p->charData) - curParmLen);
     }
   } else {
     int defaultValueLen = (defaultValue != NULL) ? strlen(defaultValue) : 0;
     if (defaultValueLen > 0) {
       memcpy(parm_p->charData, defaultValue, defaultValueLen);
     }

     memset(&(parm_p->charData[defaultValueLen]), ' ',
            sizeof(parm_p->charData) - defaultValueLen);
     parm_p->charDataLen = defaultValueLen;
     parm_p->defaulted = TRUE;
   }
 }

 /* ----------------------------------------------------------------- */
 /* Parses an integer parameter.                                      */
 /* ----------------------------------------------------------------- */
 void parseIntegerParameter(char* rawParms_p, struct controlParm* parm_p,
                            char* key_p, int minValue, int maxValue,
                            int defaultValue) {
   char* curParm_p = strstr(rawParms_p, key_p);
   char* curParmEnd_p = NULL;
   int   curParmLen = 0;
   int   keyLen = strlen(key_p);

   if (curParm_p > 0) {
     curParmEnd_p = strstr(curParm_p + keyLen, " ");     /* look for a blank */
     if (curParmEnd_p == 0)
       curParmEnd_p = strchr(curParm_p + keyLen, 0x00);  /* look for a null  */

     curParmLen = int (curParmEnd_p - (curParm_p + keyLen));
     if ((curParmLen > 8) ||
         (curParmEnd_p == 0)) { /* TODO: Check lower bound */
       parm_p->invalid = TRUE;
     } else {
       char tempValue[9];
       memcpy(tempValue, curParm_p + keyLen, curParmLen);
       tempValue[curParmLen] = NULL;
       parm_p->intData = atoi(tempValue); /* TODO: Check for non-numerics */
       if ((parm_p->intData < minValue) || (parm_p->intData > maxValue)) {
         parm_p->invalid = TRUE;
       }
     }
   } else {
     parm_p->intData = defaultValue;
     parm_p->defaulted = TRUE;
   }
 }

 /* ----------------------------------------------------------------- */
 /* Parses a YES/NO parameter.  The first char of data will be Y or N */
 /* ----------------------------------------------------------------- */
 void parseYesNoParameter(char* rawParms_p, struct controlParm* parm_p,
                          char* key_p, unsigned char yesDefault) {
   char* defaultValue = (yesDefault == TRUE) ? "YES" : "NO";
   parseStringParameter(rawParms_p, parm_p, key_p, 3, defaultValue);
   if ((parm_p->charData[0] != 'Y') && (parm_p->charData[0] != 'N')) {
     parm_p->invalid = TRUE;
   }
 }

 /* ----------------------------------------------------------------- */
 /* Populates the supplied parameter data with values supplied by the */
 /* caller.                                                           */
 /* ----------------------------------------------------------------- */
 void parseParameters(char* rawParms_p, struct controlParms* parms_p) {
   memset(parms_p, 0, sizeof(*parms_p));

   /* Process the register name parameter. */
   parseStringParameter(rawParms_p, &(parms_p->regname), REGNAME_KEY, 12, NULL);
   if ((parms_p->regname.invalid == FALSE) && (parms_p->regname.defaulted == TRUE)) {
     EXEC CICS ASSIGN APPLID(parms_p->regname.charData) NOHANDLE;
     memset(&(parms_p->regname.charData[8]), ' ', sizeof(parms_p->regname.charData) - 8);
     parms_p->regname.charDataLen = 8;
   }

   /* Process minimum and maximum connections setting. */
   parseIntegerParameter(rawParms_p, &(parms_p->minconn), MINCONN_KEY, 0, 9999, 1);
   parseIntegerParameter(rawParms_p, &(parms_p->maxconn), MAXCONN_KEY, 0, 9999, 10);

   /* Process transactional and security settings. */
   parseYesNoParameter(rawParms_p, &(parms_p->txn), TRANSACTIONAL_KEY, FALSE);
   parseYesNoParameter(rawParms_p, &(parms_p->sec), SECURITY_KEY, FALSE);
   parseYesNoParameter(rawParms_p, &(parms_p->lsync), LINKSYNC_KEY, FALSE);

   /* Process the daemon/node/server settings. */
   parseStringParameter(rawParms_p, &(parms_p->dgn), DAEMONGROUPNAME_KEY, 8, NULL);
   parseStringParameter(rawParms_p, &(parms_p->nodename), NODENAME_KEY, 8, NULL);
   parseStringParameter(rawParms_p, &(parms_p->svrname), SERVERNAME_KEY, 8, NULL);

   /* Process the service name setting. */
   parseStringParameter(rawParms_p, &(parms_p->svcname), SERVICENAME_KEY, 8, NULL);

   /* Process transaction IDs. */
   parseStringParameter(rawParms_p, &(parms_p->stx), SERVERTRANID_KEY, 4,
                        "BBO$");
   parseStringParameter(rawParms_p, &(parms_p->ltx), LINKTRANID_KEY, 4, "BBO#");

   /* Process link task reuse settings. */
   parseYesNoParameter(rawParms_p, &(parms_p->reu), LINKTASKREUSE_KEY, FALSE);
   parseIntegerParameter(rawParms_p, &(parms_p->reuc), LINKTASKREUSECOUNT_KEY, 0, 99999999, 0);
   parseIntegerParameter(rawParms_p, &(parms_p->reut), LINKTASKREUSETIME_KEY, 0, 99999999, 0);

   /* Process trace settings. */
   parseYesNoParameter(rawParms_p, &(parms_p->xtr), EXITTRACE_KEY, FALSE);
   parseIntegerParameter(rawParms_p, &(parms_p->trc), TRACE_KEY, 0, 2, 0);
   parseStringParameter(rawParms_p, &(parms_p->wrtqname), TDQ_KEY, 4, "BBOQ");

   /* Process transaction recovery parms */
   parseStringParameter(rawParms_p, &(parms_p->uowid), UOW_KEY, /* @PM74646A*/
                        16, NULL);                              /* @PM74646A*/

   /* Process List server TSQ setting                              @PM90865A*/
   parseStringParameter(rawParms_p, &(parms_p->ltsq), LTSQ_KEY, 8, NULL); /* @PM90865A*/

   /* Process Remote Transaction ID parm                           @PI52665A*/
   parseStringParameter(rawParms_p, &(parms_p->rtx),
                       REMOTETRANID_KEY, 4, NULL);              /* @PI52665A*/

   /* Process Remote SYSID parm                                    @PI52665A*/
   parseStringParameter(rawParms_p, &(parms_p->rtxsys),
                       REMOTESYSID_KEY, 4, NULL);               /* @PI52665A*/

   /* Process Remote Transaction ID propagate parm (default NO)    @PI52665A*/
   parseYesNoParameter(rawParms_p, &(parms_p->rtxp),
                       REMOTETXPROPAGATE_KEY, FALSE);           /* @PI52665A*/

   /* Process Retry parm (default NO)                              @PI52665A*/
   parseYesNoParameter(rawParms_p, &(parms_p->retry),
                       RETRY_KEY, FALSE);                       /* @PI52665A*/

   /* Process Retry Mode parm (default NO)                         @PI52665A*/
   parseYesNoParameter(rawParms_p, &(parms_p->retmode),
                       RETRY_MODE_KEY, FALSE);                   /* @PI52665A*/

   /* Process Retry Count parm (default is 10 retries)             @PI52665A*/
   parseIntegerParameter(rawParms_p, &(parms_p->retcnt),
                       RETRY_COUNT_KEY, 0, 99999999, 10);       /* @PI52665A*/

   /* Process Retry Interval parm (default is 30 second delay)     @PI52665A*/
   parseIntegerParameter(rawParms_p, &(parms_p->retint),
                       RETRY_INTERVAL_KEY, 0, 359999, 30);      /* @PI52665A*/

  /* Process Document Template Name                                @PI52665A*/
   parseStringParameter(rawParms_p, &(parms_p->doctname),
                        DOC_TEMPLATE_NAME_KEY, 48, NULL);       /* @PI52665A*/
 }

 /* ----------------------------------------------------------------- */
 /* Convert a hex string with an even number of characters into its   */
 /* hexidecmal numeric equivalent.                                    */
 /* Returns 0 on success.                                             */
 /* ----------------------------------------------------------------- */
 int stringToHex(char* stringDataIn, char* hexDataOut, int stringDataLen) {
   /* --------------------------------------------------------------- */
   /* It would be nice to just call "strtoll" here, but we can't      */
   /* because "strtoll" sets errno, and we can't reference errno      */
   /* because CICS is not POSIX(ON).                                  */
   /* --------------------------------------------------------------- */
   int rc = 0;
   if ((stringDataLen % 2) == 0) {
     memset(hexDataOut, 0, stringDataLen / 2);
     for (int x = 0; x < stringDataLen; x++) {
       unsigned char curNibble = 0;
       if (((*(stringDataIn + x)) >= '0') &&
           ((*(stringDataIn + x)) <= '9')) {
         curNibble = (*(stringDataIn + x)) - '0';
       } else if (((*(stringDataIn + x)) >= 'A') &&
                  ((*(stringDataIn + x)) <= 'F')) {
         curNibble = (*(stringDataIn + x)) - 'A' + 10;
       } else if (((*(stringDataIn + x)) >= 'a') &&
                  ((*(stringDataIn + x)) <= 'f')) {
         curNibble = (*(stringDataIn + x)) - 'a' + 10;
       } else {
         rc = -1;
       }

       if ((x % 2) == 0) {
         curNibble = curNibble << 4;
       }

       *(hexDataOut + (x / 2)) |= curNibble;
     }
   } else {
     rc = -1;
   }

   return rc;
 }

 /* ------------------------------------------------------------------*/
 /* txresync() : Reschedule a transaction for recovery.               */
 /* ------------------------------------------------------------------*/
#define PROCESS_INFO_PTR info_p
 int txresync(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   int respcode2=0;
   char screenmsg[256];
   short smsglen= 256;
   int gwa_size=0;
   BBOACGAA * gwa_p = 0;
   char uowid[8];
   char* uowidList[1];
   short uowidListLen = 4; /* In bytes */

   memset(screenmsg,' ',sizeof(screenmsg));

   /* Process UOW parameter */
   if (parms_p->uowid.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_UOW_PARM_LENGTH_ERROR,
               "The link server generated a UOW parameter value that is not a supported length.  The UOW recovery request could not be processed.");
     rc = 12;
   } else if (parms_p->uowid.defaulted == TRUE)  {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_RESYNC_UOW_MISSING,
               "CICS could not run the RESYNC command because the link server did not generate a value for the UOW parameter.");
     rc = 12;
   } else if (stringToHex(parms_p->uowid.charData, uowid,
                          parms_p->uowid.charDataLen) != 0) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_RESYNC_UOW_NOT_HEXDATA,
               "The link server generated UOW parameter data that is not valid.  The UOW recovery request could not be processed.");
     rc = 12;
   } else {
     uowidList[0] = uowid;
     DEBUG2("Calling EXEC CICS RESYNC, UOWID %8.8x %8.8x ",
            *((int*)uowid), *((int*)(&(uowid[4]))));

     EXEC CICS RESYNC ENTRYNAME("BBOATRUE") IDLIST(uowidList)
       IDLISTLENGTH(uowidListLen) PARTIAL RESP(respcode) RESP2(respcode2);

     if (respcode != dfhresp(NORMAL)) {

       char respstr[8];
       char respstr2[8];
       char* eibrcode = eibrcodeToHexString((char *)dfheiptr->eibrcode);
       snprintf(respstr, sizeof(respstr), "%d", respcode);
       snprintf(respstr2, sizeof(respstr2), "%d", respcode2);

       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_RESYNC_ERROR,
                 "The EXEC CICS RESYNC command did not determine the status of the unit of work. The response code is %s.",
                 respstr);

       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, CICS_ERROR_RESULTS,
                 "An error occurred in CICS for the %s function with the following values:  EIBRCODE = %s; RESP = %s; RESP2 = %s.\n", "RESYNC", eibrcode, respstr, respstr2);

       rc = 12;
     }
   }

   return rc;
 }

 /* ----------------------------------------------------------------- */
 /* enable_true() : Activate WAS task related user exit.              */
 /* ----------------------------------------------------------------- */
 int enable_true(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   int respcode2=0;
   char screenmsg[256];
   short smsglen= 256;
   int gwa_size=0;                             /* @576822A*/
   BBOACGAA * gwa_p = 0;                       /* @576822A*/

   memset(screenmsg,' ',sizeof(screenmsg));

   /* Process Exit trace parameter                @576822A*/
   if (parms_p->xtr.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, XTR_PARM_INCORRECT,
               "The system-level tracing parameter, XTR, was set to a value other than Y or N.");
     rc=12;
     exit(1);
   } else if (parms_p->xtr.defaulted == FALSE) {
     DEBUG1("Exit trace: %.3s ", parms_p->xtr.charData);
   }

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_1,
             "WOLA TRACE 1: %s", "Enabling BBOATRUE exit.");

   EXEC CICS ENABLE PROGRAM("BBOATRUE") LINKEDITMODE
       TALENGTH(BBOATRUE_TALSIZE)
       GALENGTH(BBOATRUE_GALSIZE)
       INDOUBTWAIT OPENAPI START RESP(respcode) RESP2(respcode2);

   if (respcode == 0) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_1,
               "WOLA TRACE 1: %s", "Exit enabled successfully.");
   } else {
     rc = respcode;
     char respstr[8];
     char respstr2[8];
     char* eibrcode = eibrcodeToHexString((char *)dfheiptr->eibrcode);
     snprintf(respstr, sizeof(respstr), "%d", respcode);
     snprintf(respstr2, sizeof(respstr2), "%d", respcode2);


     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, ENABLE_TRUE_ERROR,
               "The task-related user exit (TRUE) was not enabled. The reason code is %s.",
               respstr);
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, CICS_ERROR_RESULTS,
               "An error occurred in CICS for the %s function with the following values:  EIBRCODE = %s; RESP = %s; RESP2 = %s.\n", "ENABLE PROGRAM", eibrcode, respstr, respstr2);
     free(eibrcode);
   }

   EXEC CICS EXTRACT EXIT PROGRAM("BBOATRUE")
       GALENGTH(gwa_size)
       GASET(gwa_p) RESP(respcode);                  /* @576822A*/

   if ((respcode == 0) &
       (gwa_p > 0)) {                                /* @576822A*/
     /* Set up BBOACGAA -global work area. */
     if (info_p->baseInfo.verbose >= 1) {
       DEBUG1("Exit extract successful.");
       DEBUG1("BBOAGAA address %.8x", gwa_p);
     }
     strncpy(gwa_p->acgaaeye,"BBOACGAA",8);          /* @576822A*/
     gwa_p->acgaasiz = sizeof(BBOACGAA);             /* @576822A*/
     gwa_p->acgaaver = 1;                            /* @576822A*/
     gwa_p->acgaa_tracelvl = info_p->baseInfo.verbose;
     if (parms_p->xtr.charData[0] == 'Y')
       gwa_p->acgaa_tracelvlx = 1;                   /* @576822A*/
     else
       gwa_p->acgaa_tracelvlx = 0;                   /* @576822A*/
     memcpy(gwa_p->acgaa_ctl_tran, dfheiptr->eibtrnid,    /* @PM74646A*/
            sizeof(gwa_p->acgaa_ctl_tran));               /* @PM74646A*/
   }                                                 /* @576822A*/
   else {                                            /* @576822A*/
     rc = respcode;                                  /* @576822A*/
     char respstr[8];
     snprintf(respstr, sizeof(respstr), "%d", respcode);
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, EXTRACT_TRUE_ERROR,
               "Information about the task-related user exit (TRUE) was not extracted from CICS. The reason code is %s.",
               respstr);
   }                                                 /* @576822A*/

   return(rc);
 }

 /* ----------------------------------------------------------------- */
 /* disable_true() : Deactivate WAS task related user exit.           */
 /* ----------------------------------------------------------------- */
 int disable_true(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   int respcode2=0;
   char screenmsg[256];
   short smsglen= 256;

   memset(screenmsg,' ',sizeof(screenmsg));

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", "Disabling BBOATRUE exit.");

   EXEC CICS DISABLE PROGRAM("BBOATRUE") EXITALL RESP(respcode) RESP2(respcode2);

   if (respcode == 0) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
               "WOLA TRACE 0: %s", "Exit disabled Successfully.");
   }
   else {
     rc = respcode;
     char respstr[8];
     char respstr2[8];
     char* eibrcode = eibrcodeToHexString((char *)dfheiptr->eibrcode);
     snprintf(respstr2, sizeof(respstr2), "%d", respcode2);
     snprintf(respstr, sizeof(respstr), "%d", respcode);

     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, DISABLE_TRUE_ERROR,
               "The task-related user exit (TRUE) was not disabled. The reason code is %s.", respstr);

     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, CICS_ERROR_RESULTS,
               "An error occurred in CICS for the %s function with the following values:  EIBRCODE = %s; RESP = %s; RESP2 = %s.\n", "DISABLE PROGRAM", eibrcode, respstr, respstr2);
     free(eibrcode);
   }

   return(rc);
 }

  /* ----------------------------------------------------------------------- */
  /* doregister() : Call BBOA1REG API and register with WAS adapters.        */
  /* ------------------------------------------------------------ @PI52665A  */
  int doregister(struct cicsControlTaskInfo* info_p,
                 struct controlParms* parms_p,
                 char* registername_p,
                 int* minconn_p,
                 int* maxconn_p,
                 int* regrc_p,
                 int* regrsn_p) {

    char dgname[8];
    char registername[12];
    int rc=0;

    _REGFLAGS regflags= {0x00,0x00,0x00,0x00};

    memset(dgname, 0x00, sizeof(dgname));
    memcpy(dgname, parms_p->dgn.charData, parms_p->dgn.charDataLen);
    memcpy(registername, registername_p, sizeof(registername));

    DEBUG1("doregister() register name: %.12s",registername);
    DEBUG1("doregister() minconn: %i maxconn: %i ",*minconn_p, *maxconn_p);
    DEBUG1("doregister() dgname: %.8s",dgname);

    if (memcmp(registername, "$WASDEFAULT$", sizeof(registername)) != 0 ) {
        DEBUG2("doregister() Not default register name. Set up regflags.");

        if (parms_p->sec.charData[0] == 'Y') {
          regflags.reg_flag_W2Cprop = 1;                   // @LI4798I7-01C
          regflags.reg_flag_C2Wprop = 1;                   /* @579234A*/
        }
        if (parms_p->txn.charData[0] == 'Y')
          regflags.reg_flag_trans = 1;
    }

    BBOA1REG((_CHAR8*)dgname,
             (_CHAR8*)parms_p->nodename.charData,
             (_CHAR8*)parms_p->svrname.charData,
             (_CHAR12*)registername,
             minconn_p,
             maxconn_p,
             &regflags,
             regrc_p,
             regrsn_p);

    DEBUG1("doregister() BBOA1REG returned rc: %i rsn %i ",*regrc_p, *regrsn_p);

    if (*regrc_p != 0)
      rc=16;

    return(rc);
 }

 /* ----------------------------------------------------------------------- */
 /* regserver() : Call BBOA1REG and register with WAS adapters.             */
 /* ----------------------------------------------------------------------- */
 int regserver(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   int respcode2=0;
   char screenmsg[256];
   short smsglen= 256;

   char dgname[8];
   char nodename[8];                                 /* @PM90865A*/
   char svrname[8];                                  /* @PM90865A*/
   char truepgm[8];
   char registername[12];                            /* @PI52665C*/
   int  minconn=0;                                   /* @PI52665C*/
   int  maxconn=1;                                   /* @PI52665C*/
   int  i= 0;
   _REGFLAGS regflags= {0x00,0x00,0x00,0x00};

   memset(dgname, 0x00, sizeof(dgname));
   memcpy(dgname, parms_p->dgn.charData, parms_p->dgn.charDataLen);
   memset(nodename, ' ', sizeof(nodename));
   memcpy(nodename, parms_p->nodename.charData, parms_p->nodename.charDataLen);
   memset(svrname, ' ', sizeof(svrname));
   memcpy(svrname, parms_p->svrname.charData, parms_p->svrname.charDataLen);
   memset(screenmsg,' ',sizeof(screenmsg));
   memcpy(truepgm,"BBOATRUE",8);

   EXEC CICS INQUIRE EXITPROGRAM(truepgm) RESP(respcode) RESP2(respcode2);

   if (respcode == 0) {
     DEBUG1("TRUE BBOATRUE is active.");
   }
   else {
       char respstr[8];
       char respstr2[8];
       char* eibrcode = eibrcodeToHexString((char *)dfheiptr->eibrcode);
       snprintf(respstr, sizeof(respstr2), "%d", respcode);
       snprintf(respstr2, sizeof(respstr2), "%d", respcode2);

       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, INACTIVE_TRUE,
               "The task-related user exit program, BBOATRUE, is not active.");

       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, CICS_ERROR_RESULTS,
                 "An error occurred in CICS for the %s function with the following values:  EIBRCODE = %s; RESP = %s; RESP2 = %s.\n", "INQUIRE EXITPROGRAM", eibrcode, respstr, respstr2);
       free(eibrcode);
     exit(16);
   }

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", "Processing a REGISTER API request.");

   /* Process Register name                               */
   if (parms_p->regname.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REGNAME_TOO_LONG,
               "The register name is more than 12 characters.");
     rc=12;
   } else if (parms_p->regname.defaulted == TRUE) {
     DEBUG0("No register name passed. Using CICS APPLID: %.12s",
            parms_p->regname.charData);
   } else {
     DEBUG0("Register name: %.12s string len: %d",
            parms_p->regname.charData, parms_p->regname.charDataLen); /* @PM90865C*/
   }

   DEBUG0("WAS Daemon: %.8s WAS Node: %.8s WAS Server: %.8s",
          dgname, nodename, svrname);                               /* @PM90865A*/

   /* Process Minimum connections setting                 */
   if (parms_p->minconn.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, MIN_CONNECTIONS_TOO_HIGH,
               "The transaction cannot proceed because the minimum connections value is greater than 9999.");
     rc=12;
   } else if (parms_p->minconn.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_DEFAULT_PARM_VALUE,
               "The %s parameter was not passed and is set to the following default value:  %s",
               "MNC", "1");
   } else {
     DEBUG0("Min. connections: %i ", parms_p->minconn.intData); /* @PM90865C*/
   }

   /* Process Maximum connections setting                 */
   if (parms_p->maxconn.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, MAX_CONNECTIONS_TOO_HIGH,
               "The transaction cannot proceed because the maximum connections value is greater than 9999.");
     rc=12;
   } else if (parms_p->maxconn.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_DEFAULT_PARM_VALUE,
               "The %s parameter was not passed and is set to the following default value:  %s",
               "MXC", "10");
   } else {
     DEBUG0("Max. connections: %i ", parms_p->maxconn.intData); /* @PM90865C*/
   }

   /* Process Transactional parameter                     */
   if (parms_p->txn.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, TXN_INCORRECT_VALUE,
               "The transaction cannot proceed because an unrecognized value was specified for the TXN parameter.");
     rc=12;
   } else if (parms_p->txn.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_DEFAULT_PARM_VALUE,
               "The %s parameter was not passed and is set to the following default value:  %s",
               "TXN", "NO");
   } else {
     DEBUG0("Transactional: %.3s string len: %d",
            parms_p->txn.charData, parms_p->txn.charDataLen);  /* @PM90865C*/
   }

   /* Process Security parameter                          */
   if (parms_p->sec.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, SEC_INCORRECT_VALUE,
               "The transaction cannot proceed because an unrecognized value was specified for the SEC parameter.");
     rc=12;
   } else if (parms_p->sec.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_DEFAULT_PARM_VALUE,
               "The %s parameter was not passed and is set to the following default value:  %s",
               "SEC", "NO");
   } else {
     DEBUG0("Security propagation: %.3s string len: %d",
            parms_p->sec.charData, parms_p->sec.charDataLen);  /* @PM90865C*/
   }

   if (rc != 0)
     return(rc);

   memset(screenmsg,' ',sizeof(screenmsg));
   char tracemsg[128];
   snprintf(tracemsg, sizeof(tracemsg), "Invoking OLA Register API for %.12s.",
            parms_p->regname.charData);
   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", tracemsg);

   memset(screenmsg,' ',sizeof(screenmsg));                  /* @579234A*/

   int regrc=0;
   int regrsn=0;
   int retryInterval= parms_p->retint.intData;
   int retryCount= parms_p->retcnt.intData;
   int retried=0;

   /* ----------------------------------------------------------------- */
   /* Call doregister() passing default registration $WASDEFAULT$. We   */
   /* ignore an Rc8 Rsn8 on this. This registration is done and left    */
   /* out there to ensure we remain connected with the OLA shared       */
   /* memory buffers/trace after the unregister for the oldest          */
   /* registration completes. A BBOC UNREGISTER RGN=$WASDEFAULT$ will   */
   /* remove this registration and then as long as no other             */
   /* registrations are active, unhook from the OLA shared memory.      */
   /*                                                                   */
   /* TODO: We need to make this registration for each unique daemon    */
   /*       group we will be connecting to.                             */
   /* --------------------------------------------------------- @579234A*/

   /* Call REGISTER API with default Register name - ignore Rc8/Rsn8.   */
   memcpy(registername, "$WASDEFAULT$", sizeof(registername));/* @PI52665C*/

   DEBUG1("Calling doregister() for register name: %.12s ",registername);

   rc = doregister(info_p, parms_p, registername,
                    &minconn, &maxconn,
                    &regrc, &regrsn);                       /* @PI52665A*/

   if ((rc == 0) &&
       ((regrc == 0) || ((regrc == 8)  && (regrsn == 8)))) {/* @PI52665C*/
     DEBUG1("Default Register completed successfully.");
   }
   else {                                                    /* @579234A*/

     /* Support for Register and Start Server Retry            @PI52665A*/
     if (((regrc == 12) && ((regrsn == 10) || (regrsn == 16))) &&
       ((parms_p->retry.charDataLen > 0) &&
        (parms_p->retry.charData[0] == 'Y'))) {

       /* Retry requested. Are we started in 'Retry Mode'? If not leave now */
       /* start again in retry mode (RETMODE=Y).                            */
       if ((parms_p->retmode.defaulted == TRUE) ||
           ((parms_p->retmode.charDataLen > 0) &&
            (parms_p->retmode.charData[0] == 'N'))) {
           DEBUG1("Retry requested. Set RC=20. Return and restart in Retry Mode");
           return(20);
       }

       DEBUG1("Retry mode enabled. Retry count parameter : %i", retryCount);
       DEBUG1("Retry interval parameter : %i", retryInterval);

       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP,
           RETRY_ENABLED,
           "Command RETRY mode is enabled. Registration will be retried.");

       memcpy(registername, "$WASDEFAULT$", sizeof(registername));

       while (retried < retryCount) {
           EXEC CICS DELAY FOR SECONDS(retryInterval)
               RESP(respcode) RESP2(respcode2);

           if (respcode != dfhresp(NORMAL)) {
               /* Should not occur- issue a message just in case @PI52665A*/
               DEBUG0("EXEC CICS DELAY ERROR EIBRESP: %i EIBRESP2: %i",
                respcode, respcode2);
               exit(12);
           }

           regrc=0;
           regrsn=0;
           rc = doregister(info_p, parms_p, registername,
                            &minconn, &maxconn,
                            &regrc, &regrsn);                        /* @PI52665A*/

           if ((regrc == 0) ||                                       /* @PI52665C*/
               ((regrc == 8) && (regrsn == 8))) {                    /* @PI52665C*/
             DEBUG1("Default Register completed successfully.");
             break;
           }
           retried++;
       }
       DEBUG1("Retry mode disabled.");

       if ((regrc > 0) &&                                            /* @PI52665C*/
           !((regrc == 8) && (regrsn == 8))) {                       /* @PI52665C*/
         char rcstr[9];
         char rsnstr[9];
         snprintf(rcstr, sizeof(rcstr), "%d", regrc);
         snprintf(rsnstr, sizeof(rsnstr), "%d", regrsn);
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR,
             DFLT_REGISTER_ERROR,
             "The default link server could not register with the Liberty profile server. The return code is %s, and the reason code %s.",
             rcstr,rsnstr);
         exit(rc);
       }
     }
   }
   /* ----------------------------------------------------------------- */
   /* Call REGISTER API with real registration name.                    */
   /* ----------------------------------------------------------------- */
   regrc=0;
   regrsn=0;
   minconn=parms_p->minconn.intData;
   maxconn=parms_p->maxconn.intData;
   memcpy(registername, parms_p->regname.charData, sizeof(registername));/* @PI52665A*/

   DEBUG1("Calling doregister() for register name: %.12s",registername);

   rc = doregister(info_p, parms_p, registername,
                    &minconn, &maxconn,
                    &regrc, &regrsn);                       /* @PI52665A*/

   memset(screenmsg,' ',sizeof(screenmsg));
   if ((rc == 0) && (regrc == 0)) {                         /* @PI52665C*/
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
               "WOLA TRACE 0: %s", "Register completed successfully.", registername);
   }
   else {

       /* Support for Register and Start Server Retry            @PI52665A*/
       if (((regrc == 12) && ((regrsn == 10) || (regrsn == 16))) &&
         ((parms_p->retry.charDataLen > 0) &&
          (parms_p->retry.charData[0] == 'Y'))) {

         /* Retry requested. Are we started in 'Retry Mode'? If not leave now */
         /* start again in retry mode (RETMODE=Y).                            */
         if ((parms_p->retmode.defaulted == TRUE) ||
             ((parms_p->retmode.charDataLen > 0) &&
              (parms_p->retmode.charData[0] == 'N'))) {
             DEBUG1("Retry requested. Set RC=20. Return and restart in Retry Mode");
             return(20);
         }

         DEBUG1("Retry mode enabled. Retry count parameter : %i",
               parms_p->retcnt.intData);
         DEBUG1("Retry interval parameter : %i",
              parms_p->retint.intData);

         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP,
           RETRY_ENABLED,
           "Command RETRY mode is enabled. Registration will be retried.");

         memcpy(registername, parms_p->regname.charData, sizeof(registername));/* @PI52665A*/
         minconn=parms_p->minconn.intData;
         maxconn=parms_p->maxconn.intData;

         while (retried < retryCount) {
           EXEC CICS DELAY FOR SECONDS(retryInterval)
               RESP(respcode) RESP2(respcode2);

           if (respcode != dfhresp(NORMAL)) {
               /* Should not occur- issue a message just in case @PI52665A*/
               DEBUG0("EXEC CICS DELAY ERROR EIBRESP: %i EIBRESP2: %i",
                respcode, respcode2);
               exit(12);
           }

           regrc=0;
           regrsn=0;
           rc = doregister(info_p, parms_p, registername,
                            &minconn, &maxconn,
                            &regrc, &regrsn);                        /* @PI52665A*/

           if (regrc == 0) {                                         /* @PI52665C*/
             DEBUG1("Register completed successfully.");
             break;
           }
           retried++;
         }
         DEBUG1("Retry mode disabled.");
       }

       if (regrc > 0) {                                            /* @PI52665C*/
         char rcstr[9];
         char rsnstr[9];
         snprintf(rcstr, sizeof(rcstr), "%d", regrc);
         snprintf(rsnstr, sizeof(rsnstr), "%d", regrsn);
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR,
                 REGISTER_ERROR,
                "The registration with the Liberty profile server was not successful. The return code is %s, and the reason code %s.",
                rcstr,rsnstr);
         exit(rc);
       }
   }

   return(rc);
 }

 /* ----------------------------------------------------------------- */
 /* unregserver() : Call BBOA1URG and Unregister with WAS adapters.   */
 /* ----------------------------------------------------------------- */
 int unregserver(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   int respcode2=0;
   int delaysecs=0;
   char screenmsg[256];
   short smsglen= 256;

   char truepgm[8];
   int  i= 0;
   _UNREGFLAGS unregflags= {0x00,0x00,0x00,0x00};

   memset(screenmsg,' ',sizeof(screenmsg));
   memcpy(truepgm, "BBOATRUE", 8);

   EXEC CICS INQUIRE EXITPROGRAM(truepgm) RESP(respcode) RESP2(respcode2);

   if (respcode == 0) {
     DEBUG1("TRUE BBOATRUE is active.");
   }
   else {

     memset(screenmsg,' ',sizeof(screenmsg));
     char respstr[8];
     char respstr2[8];
     char* eibrcode = eibrcodeToHexString((char *)dfheiptr->eibrcode);
     snprintf(respstr, sizeof(respstr), "%d", respcode);
     snprintf(respstr2, sizeof(respstr2), "%d", respcode2);

     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, INACTIVE_TRUE,
               "The task-related user exit program, BBOATRUE, is not active.");

     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, CICS_ERROR_RESULTS,
               "An error occurred in CICS for the %s function with the following values:  EIBRCODE = %s; RESP = %s; RESP2 = %s.\n", "INQUIRE EXITPROGRAM", eibrcode, respstr, respstr2);

     exit(16);
   }

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", "Processing UNREGISTER API request.");

   /* Process Register name                               */
   if (parms_p->regname.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REGISTER_NAME_TOO_LONG,
               "The specified register name is more than 12 characters.");
     rc=12;
     return(rc);
   } else if (parms_p->regname.defaulted == TRUE) {
     DEBUG0("No register name passed. Use CICS APPLID: %.12s",
            parms_p->regname.charData);
   } else {
     DEBUG1("Unregister for name: %.12s string len: %d",
            parms_p->regname.charData, parms_p->regname.charDataLen);
   }

   char tracemsg[128];
   snprintf(tracemsg, sizeof(tracemsg), "Invoking OLA Unregister API for %.12s.",
            parms_p->regname.charData);
   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", tracemsg);

   /* Select UNREGISTER normally.                                     */

   /* ----------------------------------------------------------------- */
   /* Call UNREGISTER API.                                            */
   /* ----------------------------------------------------------------- */
   BBOA1URG((_CHAR12*)parms_p->regname.charData,
            &unregflags,
            &rc,
            &rsn);

   memset(screenmsg,' ',sizeof(screenmsg));
   if (rc != 0) {
     /*---------------------------------------------------------------*/
     /* Drive an unregister force if the call to unregister is        */
     /* pending due to open connections, or if the call to unregister */
     /* failed because it had already been driven.  The former gives  */
     /* the link tasks a chance to exit, the latter gives the         */
     /* customer a way out if the original register call somehow got  */
     /* mangled.                                                      */
     /*---------------------------------------------------------------*/
     if ((rc == 4) || ((rc == 8) && (rsn == 82))) {       /* @PI24358C*/
       delaysecs = 3;

       /* Sleep a few seconds and issue UNREGISTER FORCE.             */
       EXEC CICS DELAY FOR SECONDS(delaysecs);

       /* Select UNREGISTER FORCE.                                    */
       unregflags.unreg_flag_force = 1;

       /* Call UNREGISTER API.                                        */
       BBOA1URG((_CHAR12*)parms_p->regname.charData,
                &unregflags,
                &rc,
                &rsn);
       memset(screenmsg,' ',sizeof(screenmsg));

       /*---------------------------------------------------------------*/
       /* RC=0 is success.  RC 8/8 is not found, which means the        */
       /* original unregister with warning finally completed.           */
       /*---------------------------------------------------------------*/
       if (!((rc == 0) || ((rc == 8) && (rsn == 8)))) {        /* @PM79156A*/
         char rcstr[9];
         char rsnstr[9];
         snprintf(rcstr, sizeof(rcstr), "%d", rc);
         snprintf(rsnstr, sizeof(rsnstr), "%d", rsn);
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, UNREGISTER_ERROR,
                   "CICS could not unregister with the optimized local adapters. The return code is %s and reason code is %s.",
                   rcstr,rsnstr);
         exit(rc);
       }

       rc = 0;
     }
     else {
       char rcstr[9];
       char rsnstr[9];
       snprintf(rcstr, sizeof(rcstr), "%d", rc);
       snprintf(rsnstr, sizeof(rsnstr), "%d", rsn);
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, UNREGISTER_ERROR,
                 "CICS could not unregister with the optimized local adapters. The return code is %s and reason code is %s.",
                 rcstr,rsnstr);
       exit(rc);
     }
   }

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", "Unregister completed successfully.");

   return(rc);
 }

 /* ----------------------------------------------------------------- */
 /* process_register() : Processes register parms and calls register  */
 /* --------------------------------------------------------@F003691A */
 int process_register(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   char screenmsg[256];
   short smsglen= 256;

   memset(screenmsg,' ',sizeof(screenmsg));

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", "Processing a REGISTER request.");

   memset(screenmsg,' ',sizeof(screenmsg));

   /* Process Daemon group name                           */
   if (parms_p->dgn.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, WOLA_GROUP_TOO_LONG,
               "The specified WOLA group name has more than 8 characters.");
     return 12;
   } else if (parms_p->dgn.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_REQUIRED_PARM_MISSING,
               "The %s parameter is required, but it was not specified.", "DGN");
     return 12;
   } else {
     DEBUG1("Daemon group name: %.8s string len: %d",
            parms_p->dgn.charData, parms_p->dgn.charDataLen);
   }

   /* Process Node name                                   */
   if (parms_p->nodename.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, WOLA_NAME2_TOO_LONG,
               "The second part of the WOLA name has more than 8 characters.");
     return 12;
   } else if (parms_p->nodename.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_REQUIRED_PARM_MISSING,
               "The %s parameter is required, but it was not specified.", "NDN");
     return 12;
   } else {
     DEBUG1("Node name: %.8s string len: %d",
            parms_p->nodename.charData, parms_p->nodename.charDataLen);
   }

   /* Process Server name                                 */
   if (parms_p->svrname.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, WOLA_NAME3_TOO_LONG,
               "The third part of the WOLA name has more than 8 characters.");
     return 12;
   } else if (parms_p->svrname.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_REQUIRED_PARM_MISSING,
               "The %s parameter is required, but it was not specified.", "SVN");
     return 12;
   } else {
     DEBUG1("Server name: %.8s string len: %d",
            parms_p->svrname.charData, parms_p->svrname.charDataLen);
   }

   /* Process Retry parameter                                   14@PI52665A*/
   if (parms_p->retry.invalid == TRUE) {
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RETRY_PARM_INCORRECT,
                 "The parameter RETRY was set to a value other than Yes (Y) or No (N).");
     rc=12;
     return(rc);
   } else {
           DEBUG1("Retry parameter : %.3s string len: %d",
             parms_p->retry.charData, parms_p->retry.charDataLen);
   }

   /* Process Retry Mode parameter                               14@PI52665A*/
   if (parms_p->retmode.invalid == TRUE) {
     DEBUG0("Retry mode parameter must be Y!N. Value passed : %.3s string len: %d",
         parms_p->retmode.charData, parms_p->retmode.charDataLen);
     rc=12;
     return(rc);
   } else {
           DEBUG1("Retry mode parameter : %.3s string len: %d",
             parms_p->retmode.charData, parms_p->retmode.charDataLen);
   }

   /* Process Retry Interval                                     9@PI52665A*/
   if (parms_p->retint.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RETRY_INTERVAL_INVALID,
               "The value specified for the RETINT parameter is invalid.");
     rc=12;
     return(rc);
   } else if (parms_p->retint.defaulted == FALSE) {
       if (parms_p->retint.intData == 0) {
           parms_p->retint.intData = 359999;
       }
       DEBUG1("Retry Interval : %i",
            parms_p->retint.intData);
   }

   /* Process Retry Count                                       9@PI52665A*/
   if (parms_p->retcnt.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RETRY_COUNT_INVALID,
               "The value specified for the RETCNT parameter is not valid.");
     rc=12;
     return(rc);
   } else if (parms_p->retcnt.defaulted == FALSE) {
       if (parms_p->retcnt.intData == 0) {
           parms_p->retcnt.intData = 99999999;
       }
     DEBUG1("Retry Count : %i",
            parms_p->retcnt.intData);
   }

   DEBUG1("Calling REGISTER with input parms.");

   /* ----------------------------------------------------------------- */
   /* Call regserver() and ensure we can register with the passed name. */
   /* ----------------------------------------------------------------- */
   rc = regserver(info_p, parms_p);

   if ((rc != 0) && (rc != 20)) {                           /* @PI52665C*/
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_REGISTER_FAILED,
               "The registration was not successful.");
     exit(rc);
   }

   return(rc);
 }

 /* ----------------------------------------------------------------- */
 /* startserver() : Start up an adapters CICS program link server.    */
 /* ----------------------------------------------------------------- */
 int startserver(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   int warn = 0;
   char screenmsg[256];
   short smsglen= 256;
   BBOACSRV srvrparms;
   short int srvrparmsl=0;
   char defaultTaskNum[8] = "DEFAULT";                    /* @PI53321A*/
   struct bboa_link_srvr_status_node* node_p = NULL;      /* @F003691A*/

   strncpy(srvrparms.acsrveye,"BBOACSRV",8);              /* @F003691A*/
   srvrparms.acsrvsiz = sizeof(srvrparms);                /* @F003691A*/
   srvrparms.acsrvver = BBOACSRV_V3;                      /* @PI52665C*/
   memset(&srvrparms.acsrvflags,0x00000000,4);            /* @F003691A*/

   memset(srvrparms.v2.acsrv_dgn, 0x00,                   /* @F003691A*/
          sizeof(srvrparms.v2.acsrv_dgn));                /* @F003691A*/
   memset(srvrparms.v2.acsrv_ndn, ' ',                    /* @F003691A*/
          sizeof(srvrparms.v2.acsrv_ndn));                /* @F003691A*/
   memset(srvrparms.v2.acsrv_svn, ' ',                    /* @F003691A*/
          sizeof(srvrparms.v2.acsrv_svn));                /* @F003691A*/

   memset(srvrparms.v3.acsrv_rtx, ' ',                    /* @PI52665A*/
          sizeof(srvrparms.v3.acsrv_rtx));                /* @PI52665A*/
   memset(srvrparms.v3.acsrv_rtxsys, ' ',                 /* @PI52665A*/
          sizeof(srvrparms.v3.acsrv_rtxsys));             /* @PI52665A*/

   memset(screenmsg,' ',sizeof(screenmsg));

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_1,
             "WOLA TRACE 1: %s", "Processing a START SERVER request.");

   memset(screenmsg,' ',sizeof(screenmsg));

   /* Process Daemon group name */                        /* @F003691A*/
   if (parms_p->dgn.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, WOLA_GROUP_TOO_LONG,
               "The specified WOLA group name has more than 8 characters.");
     return 12;                                         /* @F003691A*/
   } else if (parms_p->dgn.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_REQUIRED_PARM_MISSING,
               "The %s parameter is required, but it was not specified.", "DGN");
     return 12;
   } else {                                               /* @F003691A*/
     memcpy(srvrparms.v2.acsrv_dgn, parms_p->dgn.charData,
            parms_p->dgn.charDataLen);
     DEBUG1("WOLA group name: %.8s string len: %d",     /* @F003691A*/
            srvrparms.v2.acsrv_dgn, parms_p->dgn.charDataLen);  /* @F003691A*/
   }                                                      /* @F003691A*/

   /* Process Node name */                                /* @F003691A*/
   if (parms_p->nodename.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, WOLA_NAME2_TOO_LONG,
               "The second part of the WOLA name has more than 8 characters.");
     return 12;                                         /* @F003691A*/
   } else if (parms_p->nodename.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_REQUIRED_PARM_MISSING,
               "The %s parameter is required, but it was not specified.", "NDN");
     return 12;                                           /* @F003691A*/
   } else {
     memcpy(srvrparms.v2.acsrv_ndn, parms_p->nodename.charData,
            parms_p->nodename.charDataLen);
     DEBUG1("WOLA name part 2: %.8s string len: %d",           /* @F003691A*/
            srvrparms.v2.acsrv_ndn, parms_p->nodename.charDataLen);
   }

   /* Process Server name */                              /* @F003691A*/
   if (parms_p->svrname.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, WOLA_NAME3_TOO_LONG,
               "The third part of the WOLA name has more than 8 characters.");
     return 12;                                         /* @F003691A*/
   } else if (parms_p->svrname.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, BBOC_REQUIRED_PARM_MISSING,
               "The %s parameter is required, but it was not specified.", "SVN");
     return 12;                                           /* @F003691A*/
   } else {
     memcpy(srvrparms.v2.acsrv_svn, parms_p->svrname.charData,
            parms_p->svrname.charDataLen);
     DEBUG1("Server name: %.8s string len: %d", /* @F003691A*/
            srvrparms.v2.acsrv_svn, parms_p->svrname.charDataLen);
   }

   /* Process Service name */
   if (parms_p->svcname.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, SERVICE_NAME_TOO_LONG,
               "The specified service name is more than 8 characters.");
     exit(12);
   } else if (parms_p->svcname.defaulted == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_REQUIRED_PARM_MISSING,
               "The %s parameter is required, but it was not specified.", "SVC");
     exit(12);
   } else {
     DEBUG1("Service name: %.8s string len: %d",
            parms_p->svcname.charData, parms_p->svcname.charDataLen);
   }

   /* Process Server task transaction id                  */
   if (parms_p->stx.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, SERVER_TASK_TRAN_TOO_LONG,
               "The name of the server task transaction is more than 4 characters.");
     rc=12;
     return(rc);
   } else if (parms_p->stx.defaulted == FALSE) {
     DEBUG1("Server trans. name: %.4s string len: %d",
            parms_p->stx.charData, parms_p->stx.charDataLen);
   }

   /* Process Link task transaction id                    */
   if (parms_p->ltx.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, LINK_TASK_TRAN_TOO_LONG,
               "The name of the link task transaction is more than 4 characters.");
     rc=12;
     return(rc);
   } else if (parms_p->ltx.defaulted == FALSE) {
     DEBUG1("Link task trans. name: %.4s string len: %d",
            parms_p->ltx.charData, parms_p->ltx.charDataLen);
   }

   /* Process Reuse parameter                   15@579234A*/
   if (parms_p->reu.invalid == TRUE) {
     if (parms_p->reu.charDataLen > 0) {
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REU_PARM_INCORRECT,
                 "The link invocation task reuse parameter, REU, was set to a value other than Y or N.");
     } else {
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REU_PARM_TOO_LONG,
                 "The reuse length is more than 4 characters.");
     }

     rc=12;
     return(rc);
   } else if (parms_p->reu.defaulted == FALSE) {
     DEBUG1("Reuse: %.3s string len: %d",
            parms_p->reu.charData, parms_p->reu.charDataLen);
   }

   /* Process Reuse Count parameter             24@656134A*/
   if (parms_p->reuc.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REU_PARM_TOO_BIG,
               "The value specified for the REUC parameter is greater than 8 digits.");
     rc=12;
     return(rc);
   } else if (parms_p->reuc.defaulted == FALSE) {
     DEBUG1("Reuse count passed: %i ",
            parms_p->reuc.intData);
   }

   /* Process Reuse Time parameter              24@656134A*/
   if (parms_p->reut.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REU_TIMEOUT_TOO_LONG,
               "The value specified for the REUT parameter is greater than 8 digits.");
     rc=12;
     return(rc);
   } else if (parms_p->reut.defaulted == FALSE) {
     DEBUG1("Reuse timeout passed: %i ",
            parms_p->reut.intData);
   }

   /* Process Link Sync on Return parameter    @PM70002A   */
   if (parms_p->lsync.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, LSYNC_PARM_INCORRECT,
               "The LSYNC parameter was set to a value other than Y or N.");
     rc=12;
     return(rc);
   } else if ((parms_p->lsync.charData[0] == 'Y') &&
              (parms_p->txn.charData[0] == 'Y')) { /* TDK TODO: Move below */
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, LSYNC_PARM_COLLISION,
               "The LSYNC parameter and the TXN parameter are both set to Y, which is not supported.");
     rc=12;
     return(rc);
   } else if (parms_p->txn.defaulted == FALSE) {
     DEBUG1("Link Sync on Return: %.3s string len: %d",
            parms_p->lsync.charData, parms_p->lsync.charDataLen);
   } else if (parms_p->ltsq.defaulted == FALSE) {
     DEBUG1("List TSQ passed: %.8s string len: %d",
            parms_p->ltsq.charData, parms_p->ltsq.charDataLen); /* @PM90865A*/
   }

   /* Process Link remote transaction ID propagate parameter     10@PI52665A*/
   if (parms_p->rtxp.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RTXP_PARM_INCORRECT,
                 "The link invocation task parameter RTXP was set to a value other than Yes (Y) or No (N).");
     rc=12;
     return(rc);
   } else if (parms_p->rtxp.defaulted == FALSE) {
     DEBUG1("Remote transaction ID propagate : %.3s string len: %d",
            parms_p->rtxp.charData, parms_p->rtxp.charDataLen);
   }

   /* Process Link remote TRANSID                                10@PI52665A*/
   if (parms_p->rtx.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, LINK_TASK_REMOTE_TRAN_TOO_LONG,
               "The name of the link remote transaction is more than 4 characters.");
     rc=12;
     return(rc);
   } else if (parms_p->rtx.defaulted == FALSE) {
     DEBUG1("Link task remote TRANSID: %.4s string len: %d",
            parms_p->rtx.charData, parms_p->rtx.charDataLen);
   }

   /* Process Link remote SYSID                                  10@PI52665A*/
   if (parms_p->rtxsys.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, LINK_TASK_REMOTE_SYSID_TOO_LONG,
               "The name of the link remote sysid is more than 4 characters.");
     rc=12;
     return(rc);
   } else if (parms_p->rtxsys.defaulted == FALSE) {
     DEBUG1("Link task remote SYSID: %.4s string len: %d",
            parms_p->rtxsys.charData, parms_p->rtxsys.charDataLen);
   }

   /* Process Retry parameter                                    10@PI52665A*/
   if (parms_p->retry.invalid == TRUE) {
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RETRY_PARM_INCORRECT,
                 "The start server parameter RETRY was set to a value other than Yes (Y) or No (N).");
     rc=12;
     return(rc);
   } else {
           DEBUG1("Retry parameter : %.3s string len: %d",
             parms_p->retry.charData, parms_p->retry.charDataLen);
   }

   /* Process Retry Mode parameter                               10@PI52665A*/
   if (parms_p->retmode.invalid == TRUE) {
     DEBUG0("Retry mode parameter must be Y|N. Value passed : %.3s string len: %d",
         parms_p->retmode.charData, parms_p->retmode.charDataLen);
     rc=12;
     return(rc);
   } else {
           DEBUG1("Retry mode parameter : %.3s string len: %d",
             parms_p->retmode.charData, parms_p->retmode.charDataLen);
   }

   /* Process Retry Interval                                     14@PI52665A*/
   if (parms_p->retint.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RETRY_INTERVAL_INVALID,
               "The value specified for the RETINT parameter is invalid.");
     rc=12;
     return(rc);
   } else if (parms_p->retint.defaulted == FALSE) {
       if (parms_p->retint.intData == 0) {
           parms_p->retint.intData = 359999;
       }
       DEBUG1("Retry Interval : %i",
            parms_p->retint.intData);
   }

   /* Process Retry Count                                       9@PI52665A*/
   if (parms_p->retcnt.invalid == TRUE) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RETRY_COUNT_INVALID,
               "The value specified for the RETCNT parameter is not valid.");
     rc=12;
     return(rc);
   } else if (parms_p->retcnt.defaulted == FALSE) {
       if (parms_p->retcnt.intData == 0) {
           parms_p->retcnt.intData = 99999999;
       }
     DEBUG1("Retry Count : %i",
            parms_p->retcnt.intData);
   }

   DEBUG0("Starting WAS adapters Server task ...");
   DEBUG0("Server transaction id will be: %.4s ",
          parms_p->stx.charData);
   DEBUG0("Link transaction defaults to: %.4s",
          parms_p->ltx.charData);
   DEBUG0("Service name will be: %.8s", parms_p->svcname.charData);
   DEBUG0("Trace TDQ: %.4s", parms_p->wrtqname.charData);

   /* ---------------------------------------------------------------- */
   /* We only permit the REUse option when Security is set to N and we */
   /* are not going to assert the passed identity from WAS. This is    */
   /* because we have to do an EC START TRAN(BBO#) USERID() in BBO$    */
   /* to assert the identity and the REUse option does not guarantee   */
   /* that will happen (since BBO# tasks remain and are reused).       */
   /* -------------------------------------------------------- @579234A*/
   if (parms_p->reu.charData[0] == 'Y') {
     srvrparms.acsrvflags.acsrvflg_reuse= 1;          /* @579234A*/
     DEBUG1("Request to start link server with REU=Y");
   }

   if (parms_p->sec.charData[0] == 'Y') {
     srvrparms.acsrvflags.acsrvflg_W2Csec= 1;      // @LI4798I7-01C
     if (parms_p->reu.charData[0] == 'Y') {
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REUSE_SECURITY_CONFLICT,
                 "The Reuse request is disabled because security was enabled with the SEC(Y) parameter.");
       warn = 1;
     }                                                /* @579234A*/
     srvrparms.acsrvflags.acsrvflg_reuse= 0;          /* @579234A*/
   }

   if (parms_p->txn.charData[0] == 'Y')
     srvrparms.acsrvflags.acsrvflg_tx= 1;

   if (parms_p->lsync.charData[0] == 'Y')
     srvrparms.acsrvflags.acsrvflg_lsync= 1;          /* @PM70002A*/

   /* For some reason we always print 7 character task numbers. */
   strncpy(srvrparms.acsrv_tasknum, (info_p->baseInfo.tskno_for_tsq) + 1,
           sizeof(srvrparms.acsrv_tasknum));          /* @PM88133C*/
   DEBUG1("BBOC current CICS task number: %.7s",
          srvrparms.acsrv_tasknum);

   strncpy(srvrparms.acsrv_regname,parms_p->regname.charData,12);
   memset(srvrparms.acsrv_svcname,0x00,sizeof(srvrparms.acsrv_svcname));
   memcpy(srvrparms.acsrv_svcname, parms_p->svcname.charData,
          parms_p->svcname.charDataLen);
   memcpy(srvrparms.acsrv_linktx, parms_p->ltx.charData,
          sizeof(srvrparms.acsrv_linktx));
   memcpy(srvrparms.acsrv_tracetdq, parms_p->wrtqname.charData,
          sizeof(srvrparms.acsrv_tracetdq));
   srvrparms.acsrv_tracelvl = info_p->baseInfo.verbose;
   srvrparmsl = sizeof(srvrparms);
   srvrparms.v2.acsrv_reuc = parms_p->reuc.intData;
   srvrparms.v2.acsrv_reut = parms_p->reut.intData;

   if (parms_p->retry.charData[0] == 'Y') {
     srvrparms.acsrvflags.acsrvflg_retry= 1;                /* @PI52665A*/
     DEBUG1("Request to start link server with RETRY=Y. Retry start server enabled.");
   }

   /* Setup for remote transaction and sysid here.             @PI52665A*/
   if (parms_p->rtxp.charData[0] == 'Y') {
     srvrparms.acsrvflags.acsrvflg_rtxp= 1;                 /* @PI52665A*/
     DEBUG1("Request to start link server with RTXP=Y. Use TRANSID on EC LINK.");
   }

   memcpy(srvrparms.v3.acsrv_rtx, parms_p->rtx.charData,
          sizeof(srvrparms.v3.acsrv_rtx));                  /* @PI52665A*/
   memcpy(srvrparms.v3.acsrv_rtxsys, parms_p->rtxsys.charData,
          sizeof(srvrparms.v3.acsrv_rtxsys));               /* @PI52665A*/
   srvrparms.v3.acsrv_retcnt = parms_p->retcnt.intData;     /* @PI52665A*/
   srvrparms.v3.acsrv_retint = parms_p->retint.intData;     /* @PI52665A*/

   DEBUG1("Calling REGISTER with input parms.");

   /* ----------------------------------------------------------------- */
   /* Call regserver() and ensure we can register with the passed name. */
   /* ----------------------------------------------------------------- */
   rc = regserver(info_p, parms_p);

   if (rc == 20) {                                         /* 3@PI52665A*/
     return(rc);
   }

   if (rc != 0) {
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, START_SERVER_REGISTER_FAILED,
        "The link server was not started because it could not be registered.");

     return(rc);
   }

   /* ----------------------------------------------------------------- */
   /* At this point we are registered.  Even though the link server has */
   /* not been started yet, go ahead and add it to the list of link     */
   /* servers.  No one else is going to be able to start a link server  */
   /* with this name because we have already registered it.             */
   /* ----------------------------------------------------------------- */
   node_p = createLinkServer(parms_p->regname.charData,
                           parms_p->dgn.charData,
                           parms_p->nodename.charData,
                           parms_p->svrname.charData,
                           srvrparms.acsrv_svcname,
                           parms_p->reu.charData,
                           parms_p->reuc.intData,
                           parms_p->reut.intData,
                           parms_p->txn.charData,
                           parms_p->sec.charData,
                           parms_p->ltx.charData,
                           parms_p->lsync.charData,
                           parms_p->minconn.intData,
                           parms_p->maxconn.intData,
                           parms_p->rtxp.charData,
                           parms_p->rtx.charData,
                           parms_p->rtxsys.charData,
                           parms_p->retry.charData,
                           parms_p->retmode.charData,
                           parms_p->retcnt.intData,
                           parms_p->retint.intData,       /* @PI52665C*/
                           defaultTaskNum); /* @PI53321C*/
                           

   if (node_p == NULL)                                      /* @F003691A*/
   {                                                        /* @F003691A*/
     unregserver(info_p, parms_p);                          /*2@F003691A*/
     char regnameNullTerm[13];
     snprintf(regnameNullTerm, sizeof(regnameNullTerm), "%.12s", parms_p->regname.charData);
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, ADD_LINK_SERVER_ERROR,
               "The %s link server could not be added to this CICS region.",
               regnameNullTerm);
     exit(16);                                              /* @F003691A*/
   }                                                        /* @F003691A*/

   if (rc == 0) {

     EXEC CICS START TRANSID(parms_p->stx.charData) FROM(&srvrparms)
       LENGTH(srvrparmsl) RESP(respcode);

     if (respcode == dfhresp(NORMAL)) {
       if (warn == 0) {                                              /* @656134A*/
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
                   "WOLA TRACE 0: %s", "Start server completed successfully.");
       }                                                             /* @656134A*/
       else {                                                        /* @656134A*/
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_WARN, BBOC_START_SERVER_WARNING,
                   "The start server command completed, but warning messages were issued. ");
       }                                                             /* @656134A*/
     }
     else {
       char nullTermTxId[5];
       char respstr[9];
       char resp2str[9];
       snprintf(nullTermTxId, sizeof(nullTermTxId), "%.4s", parms_p->stx.charData);
       snprintf(respstr, sizeof(respstr), "%d", dfheiptr->eibresp);
       snprintf(resp2str, sizeof(resp2str), "%d", dfheiptr->eibresp2);
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOACNTL_START_TRAN_ERROR,
                 "The %s link server transaction could not be started. The EIBRESP value is %s, and the EIBRESP2 value is %s",
                 nullTermTxId, respstr, resp2str);
     }
   }
   else {
     char rcstr[9];
     char rsnstr[9];
     snprintf(rcstr, sizeof(rcstr), "%d", rc);
     snprintf(rsnstr, sizeof(rsnstr), "%d", rsn);
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_START_SERVER_ERROR,
               "The link server could not be started. The return code is %s, and the reason code is %s.",
               rcstr, rsnstr);
   }
                                                   /* @LI4798I7-08A*/
   return(rc);
 }

 /* stopserver() : Stop adapters CICS program link server task.     */
 int stopserver(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   char screenmsg[256];
   short smsglen= 256;
   int pending = 0;                                                    /* @PI53321A */
   memset(screenmsg,' ',sizeof(screenmsg));

   DEBUG1("Calling UNREGISTER with input parms.");

   /* ----------------------------------------------------------------- */
   /* Before calling unregister, remove the register name from the link */
   /* server list.  When the unregister completes, the tasks will wake  */
   /* up and see that the link server has been removed from this list.  */
   /* This will be their cue to exit rather than wait for a new         */
   /* connection.                                                       */
   /* ----------------------------------------------------------------- */
   /* Process Register name                               */
   if (parms_p->regname.invalid == TRUE) {

     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, REGNAME_TOO_LONG,
               "The register name is more than 12 characters.");
     rc=12;                                               /* @F003691A*/
     return(rc);                                          /* @F003691A*/
   } else if (parms_p->regname.defaulted == TRUE) {
     DEBUG0("No register name passed. Use CICS APPLID: %.12s",
            parms_p->regname.charData);
   } else {
     DEBUG1("Stop server for name: %.12s string len: %d",
            parms_p->regname.charData, parms_p->regname.charDataLen);
   }                                                        /* @F003691A*/

   /* ----------------------------------------------------------------- */
   /* Set the 'pending unregister bit' in the link server node.  This   */
   /* tells the link server tasks that an unregister is being attempted */
   /* but is not complete yet.  This allows the link server to recover  */
   /* if something bad happens during unregister processing.            */
   /* ----------------------------------------------------------------- */
   pending = setLinkServersUregPendingFlg(parms_p->regname.charData, 1);  /* @PI53321C*/

   /* ----------------------------------------------------------------- */
   /* Call unregserver() to unregister - this will wake up the server   */
   /* task running under this name and it will then terminate.          */
   /* ----------------------------------------------------------------- */
   rc = unregserver(info_p, parms_p);

   /* ----------------------------------------------------------------- */
   /* Now if everything went OK, remove the link server node.           */
   /* ----------------------------------------------------------------- */
   if (rc == 0) {                                           /* @PI24358A*/
     removeLinkServers(parms_p->regname.charData);          /* @PI53321C*/
   } else {                                                 /* @PI24358A*/
     if (pending != 0 )                                    /* @PI24358A*/
         pending = setLinkServersUregPendingFlg(parms_p->regname.charData, 0); /* @PI53321C */

     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_STOP_UNREGISTER_ERROR,
               "The link server could not be stopped because it could not be unregistered.");
     exit(rc);
   }

   ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
             "WOLA TRACE 0: %s", "Stop server completed successfully.");
   return(rc);
 }

 /* matchserver() : identify if the server register name matches    */
 /*   the passed in one from the LIST_SRVR RGN= command  80@PM90865A*/
 bool match_server(char *regname, char *matchreg) {
     enum MatchVal {
         Duplicate,           /* matched exactly           */
         Any,                 /* found ? - any single char */
         Asterisk             /* found * - any char repeat */
     };

     int j = 0;
     char regnamestr[13];
     char matchregstr[13];

     memset(regnamestr,0,sizeof(regnamestr));
     memset(matchregstr,0,sizeof(matchregstr));

     /* Eliminate white space in parms before matching     */
     for (int i = 0, j = 0; i<strlen(regname); i++,j++) {
       if (regname[i]!=' ')
         regnamestr[j]=regname[i];
       else
         j--;
     }
     for (int i = 0, j = 0; i<strlen(matchreg); i++,j++) {
       if (matchreg[i]!=' ')
         matchregstr[j]=matchreg[i];
       else
         j--;
     }

     char *s = regnamestr;
     char *p = matchregstr;
     char *q = 0;
     int state = 0;

     bool match = true;
     bool matchfinal = false;

     while (match && *p) {
         if (*p == '*') {
             state = Asterisk;
             q = p+1;
         }
         else if (*p == '?')
             state = Any;
         else
             state = Duplicate;

         if (*s == 0) break;

         switch (state) {
           case Duplicate:
               match = *s == *p;
               s++;
               p++;
               break;
           case Any:
               match = true;
               s++;
               p++;
               break;
           case Asterisk:
               match = true;
               s++;
               if (*s == *q) p++;
               break;
         }
     }

     if (state == Asterisk) {
       matchfinal = (*s == *q);
       return (matchfinal);
     }
     else if (state == Any) {
       matchfinal = (*s == *p);
       return (matchfinal);
     }
     else {
       matchfinal = match && (*s == *p);
       return (matchfinal);
     }
 }

 /* listserver() : List servers running that match the passed in register   */
 /*                name.                                        275@PM90865A*/
 int listserver(struct cicsControlTaskInfo* info_p, struct controlParms* parms_p) {
   int rc=0;
   int rsn=0;
   int respcode=0;
   int matchcnt=0;
   char jobname[8];
   char dgname[8];
   char regnamestr[13];
   char matchstr[13];
   char tsqname[8];
   int tsqversion_V2=2; /* Version 2 @PI52665C*/
   char screenmsg[256];
   short smsglen= 256;
   double shrmem_p= NULL;
   double trchdr_p= NULL;
   double regentry_p= NULL;
   char svcname8[8];

   char name[16];
   char inf_dgname[8];
   char inf_nodename[8];
   char inf_srvname[8];

   struct
   {
     void* _ptr1;
     void* _ptr2;
     void* _ptr3;
     bboa_link_srvr_status_node** head_p;
   } token;

 #pragma pack(1)
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

   bboaconn conninfo;
   bboaconn* conninfo_p= &conninfo;

   tsq_record tsqdata;

   memset(screenmsg,' ',sizeof(screenmsg));
   memset(jobname,' ',sizeof(jobname));
   memset(tsqname,' ',sizeof(tsqname));
   memset(svcname8,' ',sizeof(svcname8));
   memset(matchstr,0,sizeof(matchstr));
   memset(regnamestr,0,sizeof(regnamestr));
   memset(dgname, 0x00, sizeof(dgname));
   memcpy(dgname, parms_p->dgn.charData, parms_p->dgn.charDataLen);
   memset(&conninfo,0,sizeof(conninfo));
   memset(inf_dgname, 0, sizeof(inf_dgname));
   memset(inf_nodename, 0, sizeof(inf_nodename));
   memset(inf_srvname, 0, sizeof(inf_srvname));

   DEBUG1("Entered listserver()");

   EXEC CICS INQUIRE SYSTEM JOBNAME(jobname) RESP(respcode);

   if (respcode == 0) {
     DEBUG2("List Servers INQUIRE SYSTEM JOBNAME successful.");
   }
   else {
     char respstr[9];
     snprintf(respstr, sizeof(respstr), "%d", respcode);
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, INQUIRE_JOBNAME_ERROR,
               "The link server could not query the job name for the current address space. The EIBRESP value is  %s.",
               respstr);
     rc=20;
     return(rc);
   }

   char jobnameNullTerm[9];
   snprintf(jobnameNullTerm, sizeof(jobnameNullTerm), "%.8s", jobname);

   if (parms_p->ltsq.defaulted == FALSE) {
     memcpy(tsqname, parms_p->ltsq.charData, parms_p->ltsq.charDataLen);
     DEBUG2("List TSQ name %.8s",tsqname);
   }

   /* ----------------------------------------------------------------- */
   /* Before calling listLinkServer, validate the passed register       */
   /* name.                                                             */
   /* ----------------------------------------------------------------- */
   if ((parms_p->regname.defaulted != TRUE) &&
       (parms_p->regname.invalid == TRUE)) {
     ISSUE_MSG(info_p, screenmsg, sizeof(screenmsg), BBOACNTL_COMMAREA_RC_ERROR, REGNAME_TOO_LONG,
            "The register name is more than 12 characters.");
     rc=12;
     return(rc);
   }
   else {

     /* ----------------------------------------------------------------- */
     /* !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION       */
     /* We are getting the LinkServerListLock here and HAVE TO release    */
     /* this or no more link servers will be able to start/stop.          */
     /* DO NOT issue any return() calls from inside the block below.      */
     /* Set the RC and drop through and allow the lock to be released.    */
     /* !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION       */
     /* ----------------------------------------------------------------- */
     getLinkServerListLock();
     /* ----------------------------------------------------------------- */
     /* !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION       */
     /* ----------------------------------------------------------------- */

     bboa_link_srvr_status_node** head_p = getHeadNode();

     bboa_link_srvr_status_node* cur_p = *head_p;


     if ((parms_p->regname.defaulted == TRUE) ||
         (parms_p->regname.charData[0] == '*')) {
       memset(&(parms_p->regname.charData), ' ', parms_p->regname.charDataLen);
       parms_p->regname.charData[0] = '*';
       parms_p->regname.charDataLen = 1;

       ISSUE_MSG(info_p, screenmsg, sizeof(screenmsg), BBOACNTL_COMMAREA_RC_SKIP, BBOACNTL_LIST_LINK_SERVERS,
                 "All link servers for the %s job name will be listed.", jobnameNullTerm);
     }

     memcpy(matchstr, parms_p->regname.charData, parms_p->regname.charDataLen);

     if (cur_p == NULL) {
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_WARN, BBOACNTL_LIST_NO_LINK_SERVERS,
                 "No link servers are running in the %s job name.", jobnameNullTerm);
       rc=4;

       if (parms_p->ltsq.defaulted == FALSE) {
         EXEC CICS WRITEQ TS QUEUE(tsqname)
                             FROM(screenmsg)
                             LENGTH(smsglen)
                             RESP(respcode);

         if (respcode == 0) {
           DEBUG2("EXEC CICS WRITEQ TS successful. Queue name: %.8s",
              tsqname);
         }
         else {
           char queueNameNullTerm[9];
           char respstr[9];
           snprintf(queueNameNullTerm, sizeof(queueNameNullTerm), "%.8s", tsqname);
           snprintf(respstr, sizeof(respstr), "%d", respcode);
           ISSUE_MSG(info_p, screenmsg, sizeof(screenmsg), BBOACNTL_COMMAREA_RC_ERROR, LINK_TASK_WRITE_TSQ_ERROR,
                     "The EXEC CICS WRITEQ TS MAIN command could not write data to the %s temporary storage queue. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                     queueNameNullTerm, respstr, "0");
           rc = 16;
         }
       }

     }
     else {

       DEBUG0("===========================================================");
       DEBUG0("Listing Active WOLA Link Servers for region %.8s ",jobname);
       DEBUG0("List server for register name : %.12s string len: %d",
            parms_p->regname.charData, parms_p->regname.charDataLen);

       /* Walk through the registrations and list those that match the passed */
       /* in register name.                                                   */
       while ((cur_p != NULL) &&
              (rc == 0))
       {
         memset(regnamestr,0,sizeof(regnamestr));
         memcpy(regnamestr, cur_p->regname, sizeof(cur_p->regname));

         if (match_server(regnamestr, matchstr)) {
           matchcnt++;

           /* Call GETINFO API with current Register name and get             */
           /* active connection info                                          */
           BBOA1INF((_CHAR12*)&cur_p->regname,
                    (_CHAR8*)&inf_dgname,
                    (_CHAR8*)&inf_nodename,
                    (_CHAR8*)&inf_srvname,
                    &shrmem_p,                 /* 64 bit pointer to ashr      */
                    &trchdr_p,                 /* 64 bit pointer to trace hdr */
                    &regentry_p,               /* 64 bit pointer to RGE       */
                    (_POINTER*)&conninfo_p,    /* 31 bit pointer to @conninfo */
                    &rc,
                    &rsn);

           memset(screenmsg,' ',sizeof(screenmsg));
           if (rc != 0) {
             char rcstr[9];
             char rsnstr[9];
             snprintf(rcstr, sizeof(rcstr), "%d", rc);
             snprintf(rsnstr, sizeof(rsnstr), "%d", rsn);
             ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOACNTL_GET_INFO_ERROR,
                       "The system could not retrieve the WOLA connection pool context data. The return code is %s, and the reason code is %s.",
                       rcstr, rsnstr);
             rc = 16;
             break;
           }

           DEBUG0("---------->>> Register Name: %.12s      <<<---------",
                       cur_p->regname);
           DEBUG0("Daemon name: %.8s",cur_p->dgname);
           DEBUG0("Node name: %.8s",cur_p->ndname);
           DEBUG0("Server name: %.8s",cur_p->svrname);
           DEBUG0("Service name: %.8s",cur_p->svcname);
           DEBUG0("Min Connections: %.08d",cur_p->minconn);
           DEBUG0("Max Connections: %.08d",cur_p->maxconn);
           DEBUG0("Active Connections: %.08llu",conninfo.aconn_info_active);
           DEBUG0("Connection Pool state (0=Ready|1=Quiescing|2=Destroying): %d",conninfo.aconn_info_state.aconn_state2);
           DEBUG0("Reuse? %.1s",cur_p->reu);
           DEBUG0("Txn?: %.1s",cur_p->txn);
           DEBUG0("Sec?: %.1s",cur_p->sec);
           DEBUG0("Link SyncOnReturn?: %.1s",cur_p->lsync);
           DEBUG0("Reuse count: %.08d",cur_p->reuc);
           DEBUG0("Reuse time: %.08d",cur_p->reut);
           DEBUG0("Remote Transaction ID: %.4s",cur_p->rtx);           /* @PI52665A*/
           DEBUG0("Remote SYSID: %.4s",cur_p->rtxsys);                 /* @PI52665A*/
           DEBUG0("Remote Transaction propagate?: %.1s",cur_p->rtxp);  /* @PI52665A*/
           DEBUG0("Retry enabled?: %.1s",cur_p->retry);                /* @PI52665A*/
           DEBUG0("Retry mode?: %.1s",cur_p->retmode);                 /* @PI52665A*/
           DEBUG0("Retry count: %.08d",cur_p->retcnt);                 /* @PI52665A*/
           DEBUG0("Retry interval: %.08d",cur_p->retint);              /* @PI52665A*/
           DEBUG0("-----------------------------------------------------------");

           /* If LTSQ is provided, write each link server's info to the named */
           /* CICS temporary storage queue.                                   */
           if (parms_p->ltsq.defaulted == FALSE) {
             memset(&tsqdata,' ',sizeof(tsqdata));
             memset(svcname8,' ',sizeof(svcname8));
             memcpy(svcname8,cur_p->svcname,strlen(cur_p->svcname));

             /* IMPORTANT:  TSQ Version is set here -- if we add data to this record      */
             /* IMPORTANT:  we need to be sure to increment this version and update any   */
             /* IMPORTANT:  external mappings we ship for this.                           */
             tsqdata.tsq_record_ver= tsqversion_V2;   /* V2 @PI52665C*/
             memcpy(&tsqdata.tsq_record_jobname, jobname, sizeof(tsqdata.tsq_record_jobname));
             memcpy(&tsqdata.tsq_record_regname, cur_p->regname, sizeof(tsqdata.tsq_record_regname));
             memcpy(&tsqdata.tsq_record_dgname, cur_p->dgname, sizeof(tsqdata.tsq_record_dgname));
             memcpy(&tsqdata.tsq_record_ndname, cur_p->ndname, sizeof(tsqdata.tsq_record_ndname));
             memcpy(&tsqdata.tsq_record_srvname, cur_p->svrname, sizeof(tsqdata.tsq_record_srvname));
             memcpy(&tsqdata.tsq_record_svcname, svcname8, sizeof(tsqdata.tsq_record_svcname));
             tsqdata.tsq_record_minconn= cur_p->minconn;
             tsqdata.tsq_record_maxconn= cur_p->maxconn;
             tsqdata.tsq_record_actconn= conninfo.aconn_info_active.aconn_active_p2;
             tsqdata.tsq_record_status= conninfo.aconn_info_state.aconn_state2;
             memcpy(&tsqdata.tsq_record_reuse, cur_p->reu, sizeof(tsqdata.tsq_record_reuse));
             memcpy(&tsqdata.tsq_record_txn, cur_p->txn, sizeof(tsqdata.tsq_record_txn));
             memcpy(&tsqdata.tsq_record_sec, cur_p->txn, sizeof(tsqdata.tsq_record_sec));
             memcpy(&tsqdata.tsq_record_lsync, cur_p->lsync, sizeof(tsqdata.tsq_record_lsync));
             tsqdata.tsq_record_reuc= cur_p->reuc;
             tsqdata.tsq_record_reut= cur_p->reut;
             memcpy(&tsqdata.v2.tsq_record_rtx, cur_p->rtx, sizeof(tsqdata.v2.tsq_record_rtx));          /* @PI52665A*/
             memcpy(&tsqdata.v2.tsq_record_rtxsys, cur_p->rtxsys, sizeof(tsqdata.v2.tsq_record_rtxsys)); /* @PI52665A*/
             memcpy(&tsqdata.v2.tsq_record_rtxp, cur_p->rtxp, sizeof(tsqdata.v2.tsq_record_rtxp));       /* @PI52665A*/
             memcpy(&tsqdata.v2.tsq_record_retry, cur_p->retry, sizeof(tsqdata.v2.tsq_record_retry));    /* @PI52665A*/
             memcpy(&tsqdata.v2.tsq_record_retmode, cur_p->retmode,
                    sizeof(tsqdata.v2.tsq_record_retmode));                                              /* @PI52665A*/
             tsqdata.v2.tsq_record_retcnt= cur_p->retcnt;                                                /* @PI52665A*/
             tsqdata.v2.tsq_record_retint= cur_p->retint;                                                /* @PI52665A*/
             memset(&tsqdata.v2.tsq_record_reserve, ' ', sizeof(tsqdata.v2.tsq_record_reserve));         /* @PI52665C*/

             EXEC CICS WRITEQ TS QUEUE(tsqname)
                                 FROM(tsqdata)
                                 LENGTH(sizeof(tsqdata))
                                 RESP(respcode);

             if (respcode == 0) {
               DEBUG2("EXEC CICS WRITEQ TS successful. Queue name: %.8s",
                  tsqname);
             }
             else {
               char queueNameNullTerm[9];
               char respstr[9];
               snprintf(queueNameNullTerm, sizeof(queueNameNullTerm), "%.8s", tsqname);
               snprintf(respstr, sizeof(respstr), "%d", respcode);
               ISSUE_MSG(info_p, screenmsg, sizeof(screenmsg), BBOACNTL_COMMAREA_RC_ERROR, LINK_TASK_WRITE_TSQ_ERROR,
                         "The EXEC CICS WRITEQ TS MAIN command could not write data to the %s temporary storage queue. The EIBRESP value is %s, and the EIBRESP2 value is %s.",
                         queueNameNullTerm, respstr, "0");
               rc = 16;
             }
           }
         }
         else {
           DEBUG2("Bump to next looking for match on: %.12s string len: %d",
              parms_p->regname.charData, parms_p->regname.charDataLen);
         }
         cur_p = cur_p->next_p;
       }

       DEBUG0("Match count for register name %.12s was %d",
            parms_p->regname.charData, matchcnt);
       DEBUG0("===========================================================");
     }

     /* ----------------------------------------------------------------- */
     /* !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION       */
     /* Release the lock. We HAVE TO RELEASE this lock or bad things      */
     /* will happen.                                                      */
     /* ----------------------------------------------------------------- */
     releaseLinkServerListLock();

     if (matchcnt == 0) {                        /* if no matches set RC4 */
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_WARN, BBOACNTL_LIST_NO_MATCH_LINK_SERVERS,
                 "No link servers with the registration name specified on the BBOC LIST_SRVR command are running in the %s job name.",
                 jobnameNullTerm);
       rc = 4;
     }
   }

   if (rc == 0) {
     char tracebuf[128];
     snprintf(tracebuf, sizeof(tracebuf), "List server request completed for region %.8s",jobname);
     ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL, TRACE_LEVEL_0,
               "WOLA TRACE 0: %s", tracebuf);
   }

   return(rc);
 }

 /*----------------------------------------------PI55321----*/
 /* validate the CICS level is supported                    */
 /*---------------------------------------------------------*/
 void checkCICSLevel(struct cicsControlTaskInfo* info_p, char* currlvl){
      char wrklvl[5];
      int intlvl = 0;
      memcpy(wrklvl,currlvl,4);
      wrklvl[4]='\0';
      intlvl = atoi(wrklvl);
      if(intlvl < 301 || intlvl > 503)
         DEBUG0("The current CICS level %.4s is not supported\n", wrklvl);
  }

 unsigned char isEnableExitCall(char* parms_p) {
   return ((strstr(parms_p, " STRT_TRUE ") != NULL) ||
           (strstr(parms_p, " START_TRUE ") != NULL) ||
           (strstr(parms_p, " STR ") != NULL));
 }

 unsigned char isDisableExitCall(char* parms_p) {
   return ((strstr(parms_p, " STOP_TRUE ") != NULL) ||
           (strstr(parms_p, " PTR ") != NULL));
 }

 unsigned char isUnregisterCall(char* parms_p) {
   return ((strstr(parms_p, " UNREGISTER ") != NULL) ||
           (strstr(parms_p, " URG ") != NULL));
 }

 unsigned char isRegisterCall(char* parms_p) {
   return ((strstr(parms_p, " REGISTER ") != NULL) ||
           (strstr(parms_p, " REG ") != NULL));
 }

 unsigned char isStartServerCall(char* parms_p) {
   return ((strstr(parms_p, " START_SRVR ") != NULL) ||
           (strstr(parms_p, " STRT_SRVR ") != NULL) ||
           (strstr(parms_p, " STA ") != NULL));
 }

 unsigned char isStopServerCall(char* parms_p) {
   return ((strstr(parms_p, " STOP_SRVR ") != NULL) ||
           (strstr(parms_p, " STP ") != NULL));
 }

 unsigned char isListServerCall(char* parms_p) {
   return ((strstr(parms_p, " LIST_SRVR ") != NULL) ||
           (strstr(parms_p, " LST ") != NULL));            /* @PM90865*/
 }

 unsigned char isDocTemplateCall(char* parms_p) {
   return ((strstr(parms_p, " DOCTEMPLATE ") != NULL) ||
           (strstr(parms_p, " DOC ") != NULL));           /* @PI52665A*/
 }

 unsigned char isTxResyncCall(char* parms_p) {
   return (strstr(parms_p, " TXRESYNC ") != NULL);
 }

#undef PROCESS_INFO_PTR

 /* ------------------------------------------------------------------*/
 /* processrequest() : Process BBOC command string.                   */
 /* (this routine was added with PI52665 - for the DOC command supt)  */
 /* ------------------------------------------------------------------*/
#define PROCESS_INFO_PTR info_p
 int processrequest(struct cicsControlTaskInfo* info_p,
                    struct controlParms* parms_p,
                    char* requestparms) {
     int rcode=0;
     int respcode=0;
     int respcode2=0;
     char screenmsg[256];
     short smsglen= 256;

     memset(screenmsg,' ',sizeof(screenmsg));

     if (parms_p->retmode.defaulted == FALSE) {
        DEBUG1("BBOACNTL started in Retry Mode.");
        DEBUG2("processInfo.sendOK set to : %c", info_p->sendOK);
        DEBUG2("processInfo.commareaOK set to : %c", info_p->commareaOK);
     }

     /* Determine which request was passed by searching for   */
     /* it's string.                                          */
     if (isEnableExitCall(requestparms) == TRUE) {
       DEBUG2("calling enable_true() ...");
       rcode = enable_true(info_p, parms_p);

       if (rcode > 4) {
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, ENABLE_EXIT_ERROR,
                   "The task-related user exit (TRUE) was not enabled.");
       }

       DEBUG2("returned from enable_true() rc= %d", rcode);
     } else if (isDisableExitCall(requestparms) == TRUE) {
       DEBUG2("calling disable_true() ...");
       rcode = disable_true(info_p, parms_p);

       if (rcode > 4) {
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, DISABLE_EXIT_ERROR,
                   "The task-related user exit (TRUE) was not disabled.");
       }

       DEBUG2("returned from disable_true() rc= %d",  rcode);
     } else if (isStartServerCall(requestparms) == TRUE) {
       DEBUG2("calling startserver() ...");
       rcode = startserver(info_p, parms_p);

       if ((rcode > 4) &&
           (rcode != 20)) {                                 /* @PI52665C*/
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_START_SERVER_ERROR2,
                   "The link server was not started.");
       }
       DEBUG2("returned from startserver() rc= %d", rcode);
     } else if (isStopServerCall(requestparms) == TRUE) {
       DEBUG2("calling stopserver() ...");
       rcode = stopserver(info_p, parms_p);
       DEBUG2("returned from stopserver() rc= %d", rcode);
     } else if (isRegisterCall(requestparms) == TRUE) {
       DEBUG2("calling process_register() ...");
       rcode = process_register(info_p, parms_p);

       if (rcode > 4) {
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_REGISTER_ERROR,
                   "The registration with the Liberty profile server was not successful.");
       }

       DEBUG2("returned from regserver() rc= %d", rcode);
     } else if (isUnregisterCall(requestparms) == TRUE) {
       DEBUG2("calling unregserver() ...");
       rcode = unregserver(info_p, parms_p);

       if (rcode > 4) {
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_UNREGISTER_ERROR,
                   "CICS could not unregister with the optimized local adapters.");
       }

       DEBUG2("returned from unregserver() rc= %d", rcode);
     } else if (isTxResyncCall(requestparms) == TRUE) {       /* @PM74646A*/
       DEBUG2("calling txresync() ...");                      /* @PM74646A*/
       rcode = txresync(info_p, parms_p);     /* @PM74646A*/

       if (rcode > 4) {                                       /* @PM74646A*/
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOACNTL_TX_RESYNC_ERROR,
                   "An incomplete transaction could not resolved.");
       }                                                      /* @PM74646A*/

       DEBUG2("returned from txresync() rc= %d",
              rcode);                                         /* @PM74646A*/
     } else if (isListServerCall(requestparms) == TRUE) {     /* @PM90865A*/
       DEBUG2("calling listserver() ...");                    /* @PM90865A*/

       if (parms_p->ltsq.invalid == TRUE) {
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, LTSQ_TIMEOUT_TOO_LONG,
                   "The value specified for the LTSQ parameter is greater than 8 characters.");
         return(16);
       } else {
         if (parms_p->ltsq.defaulted == FALSE) {
           DEBUG2("LTSQ name passed and set to: %.8s",
              parms_p->ltsq.charData);
           EXEC CICS DELETEQ TS QUEUE(parms_p->ltsq.charData) NOHANDLE;
         }
       }

       rcode = listserver(info_p, parms_p);   /* @PM90865A*/

       if (rcode > 4) {                                       /* @PM90865A*/
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, BBOC_LIST_SERVER_ERROR,
                   "The list of link servers could not be generated.");
       }                                                      /* @PM90865A*/

       DEBUG2("returned from listserver() rc= %d",
              rcode);                                         /* @PM74646A*/
     }
     /* ----------------------------------------------------------------- */
     /* BEGIN @PI52665A                                                   */
     /* Document template support- provide the ability to read BBOC       */
     /* commands from datasets or files in the file system.               */
     /* ----------------------------------------------------------------- */
     else if (isDocTemplateCall(requestparms) == TRUE) {      /* @PI52665A*/
       DEBUG2("process document template request ...");       /* @PI52665A*/

       char docRequestParms[512];
       char docTemplateName[48];
       char docToken[16];
       int  docLength;
       int  docMaxLength;
       int  resp=0;
       int  resp2=0;
       int  i=0;
       char respstr[8];
       char respstr2[8];

       memset(screenmsg,' ',sizeof(screenmsg));

       if (parms_p->doctname.invalid == TRUE) {
         ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, DOCT_NAME_TOO_LONG,
                   "The value specified for the DOC NAME parameter is too long.");
         return(16);
       } else {

         memset(docRequestParms, ' ', sizeof(docRequestParms));

         memset(docTemplateName, ' ', sizeof(docTemplateName));
         memcpy(docTemplateName, parms_p->doctname.charData, parms_p->doctname.charDataLen);

         /*--------------------------------------------------------------------*/
         /* Create Document using provided template name                       */
         /*--------------------------------------------------------------------*/
         EXEC CICS DOCUMENT CREATE
             DOCTOKEN(docToken)
             TEMPLATE(docTemplateName)
             DOCSIZE(docLength)
             RESP(resp) RESP2(resp2) ;

         if (resp != dfhresp(NORMAL)) {
           snprintf(respstr, sizeof(respstr), "%d", resp);
           snprintf(respstr2, sizeof(respstr2), "%d", resp2);

           ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR,
               DOCT_CREATE_ERROR,
               "EXEC CICS DOCUMENT CREATE command failed. EIBRESP = %s. EIBRESP2 = %s.", respstr, respstr2);
           rcode=16;
           return(rcode);
         }
         DEBUG2("Document Create successful for document template: %.48s", docTemplateName);

         /*--------------------------------------------------------------------*/
         /* Retrieve Document data using document token                        */
         /*--------------------------------------------------------------------*/
         docMaxLength = sizeof(docRequestParms)-12;

         EXEC CICS DOCUMENT RETRIEVE
             DOCTOKEN(docToken)
             INTO(docRequestParms)
             LENGTH(docLength)
             MAXLENGTH(docMaxLength)
             DATAONLY
             RESP(resp) RESP2(resp2) ;

         if (resp != dfhresp(NORMAL)) {
           snprintf(respstr, sizeof(respstr), "%d", resp);
           snprintf(respstr2, sizeof(respstr2), "%d", resp2);

           ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR,
               DOCT_RETRIEVE_ERROR,
               "EXEC CICS DOCUMENT RETRIEVE command failed. EIBRESP = %s. EIBRESP2 = %s.", respstr, respstr2);
           rcode=16;
           return(rcode);
         }

         DEBUG2("Document Retrieve successful for document template: %.48s", docTemplateName);

         /*--------------------------------------------------------------------*/
         /* Replace CC (0x0d 0x15 0x25) with blanks if located                 */
         /*--------------------------------------------------------------------*/
         if (docLength > docMaxLength) {
             docLength = docMaxLength;
         }

         for (i=0; i < docLength; i++) {
           if ((docRequestParms[i] == '\x0d') ||
               (docRequestParms[i] == '\x15') ||
               (docRequestParms[i] == '\x25')) {
                 docRequestParms[i] = ' ';
                 i++;
           }
         }

         if ((parms_p->retmode.defaulted == FALSE) &&
             ((parms_p->retmode.charDataLen > 0) &&
              (parms_p->retmode.charData[0] == 'Y'))) {
             DEBUG2("Doc Parameters need RETMODE propagated. Adding this option...");
             int offs_lastchar = 0;
             for (int i=docLength-1; i >= 0; i--) {
                if ((docRequestParms[i] != ' ') &&
                    (docRequestParms[i] != 0x00)) {
                    offs_lastchar = i;
                    break;
                }
             }
             DEBUG2("Last non-blank offset: %d", offs_lastchar);
             memcpy((void *)((int)&docRequestParms+offs_lastchar+1),
                    " RETMODE=Y", 10);
             docLength = docLength + 10;
         }

         DEBUG0("doc Max Request length: %d",docMaxLength);
         DEBUG0("docRequestParms data 1: %.50s", docRequestParms);
         DEBUG0("docRequestParms data 2: %.50s", docRequestParms+50);
         DEBUG0("docRequestParms data 3: %.50s", docRequestParms+100);
         if (docMaxLength > 150) {
           DEBUG0("docRequestParms data 4: %.50s", docRequestParms+150);
         }
         if (docMaxLength > 200) {
           DEBUG0("docRequestParms data 5: %.50s", docRequestParms+200);
         }

         struct controlParms docParsedParameters;
         parseParameters(docRequestParms, &docParsedParameters);

         if (isDocTemplateCall(docRequestParms) == TRUE) {
                DEBUG2("recursed document template request - not supported");
                ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR,
                    DOCT_RECURSE_ERROR,
                    "BBOC DOC command cannot be used inside another DOC command.");
                rcode=16;
                return(rcode);
         }

         DEBUG2("Calling processrequest() ...");

         rcode = processrequest(info_p,
                        &docParsedParameters,
                        docRequestParms);

         DEBUG2("Returned from recursed processrequest() call. rcode= %i",rcode);

         DEBUG2("returning from document template request ...");

         /* ----------------------------------------------------------------- */
         /* END @PI52665A                                                     */
         /* ----------------------------------------------------------------- */
       }
     } else {
       ISSUE_MSG(info_p, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, INCORRECT_REQUEST,
                 "The BBOC command contained an unrecognized operation.");
       rcode = 16;
     }

   DEBUG2("Returning from processrequest() call. rcode= %i",rcode);
   return(rcode);
 }
#undef PROCESS_INFO_PTR


 /* ----------------------------------------------------------------- */
 /* main() : Main procedure                                           */
 /* ----------------------------------------------------------------- */
  main ()
  {
    char requestparms[512];                               /* @PI52665C*/
    char retryparms[512];                                 /* @PI52665A*/
    short int requestlgth= 512;                           /* @PI52665C*/
    short int retrylgth= 512;                             /* @PI52665A*/
    int retrydelay= 1;                                    /* @PI52665A*/

    char screenmsg[256];
    short smsglen= 256;
    long int i= 0;
    long int rcode=0;
    long int reason=0;
	int respcode = 0;                                     /* @PI52665M*/
	int respcode2 = 0;                                    /* @PI52665A*/
    struct tm *newtime;
    time_t ltime, start, finish;
    clock_t clock(void);
    double time1, timedif;
    char msg1[64];
    char msg2[64];
    char tslvl[6];

    struct cicsControlTaskInfo processInfo;
#define PROCESS_INFO_PTR &processInfo

     memset(screenmsg,' ',sizeof(screenmsg));
     memset(requestparms,' ',sizeof(requestparms));

     /* Get addressability to the EIB first.                  */
     EXEC CICS ADDRESS EIB(dfheiptr);
     initializeCicsLinkServerProcessInfo(&(processInfo.baseInfo));
     processInfo.sendOK = '1';
     processInfo.commareaOK = '1';                         /* @PI52665A*/
     memset(processInfo._available, 0, sizeof(processInfo._available));

     EXEC  CICS INQUIRE SYSTEM CICSTSLEVEL(tslvl);  /* @PI53321C*/
     checkCICSLevel(&processInfo, tslvl);           /* @PI53321C*/

     time(&start);
     newtime = localtime(&start);
     time1 = (double) clock();        /* Get initial time */
     time1 = time1 / CLOCKS_PER_SEC;  /*    in seconds    */

     /* Check for a COMMAREA. If we find one, then we were    */
     /* either invoked with EC LINK or one of our PLT programs*/
     /* with a string of request parms. If this is the case,  */
     /* then we do not do the EC RECEIVE or any EC SENDs.     */
     /*                                          @LI4798I7-08A*/
     EXEC CICS ADDRESS COMMAREA(processInfo.commarea_p);

     if ((processInfo.commarea_p > 0) &&
         (processInfo.commarea_p != ((void*)0xFF000000))) {

       processInfo.commarea_write_fcn = writeToCommareaV0; /* @PM90865A*/

       processInfo.commarea_l = dfheiptr->eibcalen;
       processInfo.commarea_mcpy_size =
         (smsglen < processInfo.commarea_l ? smsglen : processInfo.commarea_l);

       requestlgth = dfheiptr->eibcalen;                   /* @PI52665A*/
       if (requestlgth > sizeof(requestparms)) {           /* @PI52665A*/
             requestlgth = sizeof(requestparms);           /* @PI52665A*/
       }                                                   /* @PI52665A*/

       memcpy(&requestparms, processInfo.commarea_p,
              requestlgth);                                /* @PI52665C*/

       processInfo.sendOK = '0';

       /* If there's room, format the output commarea.        @PM90865A*/
       if (processInfo.commarea_l >= sizeof(struct outputCommarea_v1)) {
         memset(processInfo.commarea_p, 0, sizeof(struct outputCommarea_v1));
         processInfo.commarea_p->output.version = BBOACNTL_COMMAREA_V1;
         processInfo.commarea_write_fcn = writeToCommareaV1;
       }
     } else {                                   /* @LI4798I7-08A*/

       processInfo.commareaOK = '0';                  /* @PI52665A*/

       /* See if we were driven from START (in BBOATRUX).    @PM74646A */
       EXEC CICS RETRIEVE INTO(requestparms) LENGTH(requestlgth)
         RESP(respcode);

       if (respcode != dfhresp(NORMAL))               /* @PM74646A*/
       {                                              /* @PM74646A*/
         /* Read screen data into requestparms.                   */
         requestlgth = sizeof(requestparms);          /* @PM74646A*/
         EXEC CICS RECEIVE INTO(requestparms) LENGTH(requestlgth);
       }                                              /* @PM74646A*/
     }

     if (requestlgth > sizeof(requestparms)) {        /* @PI52665A*/
         requestlgth = sizeof(requestparms);          /* @PI52665A*/
     }                                                /* @PI52665A*/

     for (i = 0; i <= requestlgth; i++) {
       requestparms[i] = toupper(requestparms[i]);   /* @579234A*/
       /* Replace any passed in nulls with blanks       @PM90865A*/
       if (requestparms[i] == 0x00)                  /* @PM98065A*/
         requestparms[i] = ' ';                      /* @PM90865A*/
     }
     requestparms[sizeof(requestparms)-1]=0x00;      /* @PI52665C*/

    /* Process all parameters (even those we may not need). */
     struct controlParms parsedParameters;
     parseParameters(requestparms, &parsedParameters);

     /* ----------------------------------------------------------------- */
     /* Process Retry Mode parameter (internal undocumented parm)         */
     /* We need to see if we are in 'Retry mode'.  If so, then we were    */
	 /* started using an EXEC CICS START TRANSID by an earlier BBOC cmd   */
	 /* which means there's no terminal the request is running on. Because*/
	 /* of this, we need to set the Send OK flag off.                     */
     /* -------------------------------------------------------- @PI52665A*/
  	 if (parsedParameters.retmode.defaulted == FALSE) {
       processInfo.sendOK = '0';
     }

     if (parsedParameters.wrtqname.invalid == TRUE) {
       ISSUE_MSG(&processInfo, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, TDQ_PARM_TOO_LONG,
                 "The name of the transient data queue (TDQ) is more than 4 characters.");
       return(16);
     } else {
       memcpy(processInfo.baseInfo.wrtqname,
              parsedParameters.wrtqname.charData,
              sizeof(processInfo.baseInfo.wrtqname));
       if (parsedParameters.wrtqname.defaulted == FALSE) {
         DEBUG0("TDQ name passed and set to: %.4s",
                parsedParameters.wrtqname.charData);
       }
     }

     DEBUG0("<==========  ADAPTERS CONTROL TASK START ===  %.20s  =====>",
            asctime(newtime));

     /* Write message with WAS build level      @PI27022*/
     DEBUG0("<==== Build Level %s ====>\n",LIBERTY_BUILD_LABEL);             /*@PI27022*/


     /* Process Trace switch                                */
     if (parsedParameters.trc.invalid == TRUE) {
       ISSUE_MSG(&processInfo, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR, TRACE_PARM_INCORRECT,
                 "The tracing parameter, TRC, was not set to a valid value.");
       return(16);
     } else if (parsedParameters.trc.defaulted == FALSE) {
       processInfo.baseInfo.verbose = parsedParameters.trc.intData;
       DEBUG0("Trace Level requested and set at: %d ",
              processInfo.baseInfo.verbose);
     } else {
       DEBUG0("Trace Level defaults to 0. Use TRC=0|1|2 to control tracing.");
     }

     /* Display request parms at TRC=0.                             @PM90865C*/
     DEBUG1("Requestparms@: %x",requestparms);
     DEBUG0("Requestlgth: %d",requestlgth);
     DEBUG0("Requestparms data 1: %.50s", requestparms);
     DEBUG0("Requestparms data 2: %.50s", requestparms+50);
     DEBUG0("Requestparms data 3: %.50s", requestparms+100);
     if (requestlgth > 150) {
       DEBUG0("Requestparms data 4: %.50s", requestparms+150);
     }
     if (requestlgth > 200) {
       DEBUG0("Requestparms data 5: %.50s", requestparms+200);
     }

     /* ----------------------------------------------------------------- */
     /* BEGIN @PI52665A                                                   */
     /* Support for DOC Templates - we needed to move the main process    */
     /* loop for the command string into it's own method to allow us to   */
     /* read the BBOC command from a file using BBOC DOC NAME=xxxxxx      */
     /* and then call the doc template APIs to read the actual command    */
     /* and then process the command again by having processrequest()     */
     /* call itself recursively.                                          */
     /* -------------------------------------------------------- @PI52665A*/
     rcode = processrequest(&processInfo,
                    &parsedParameters,
                    requestparms);

     DEBUG2("Back in main() from processrequest() call. rcode= %i",rcode);

     /* ** NOTE ** parsedParameters still is associated with the original */
     /* BBOC command here -even if a BBOC DOC ran and we picked up another*/
     /* command string. It still maps the original BBOC DOC req. @PI52665A*/

     /* Start Server and Register Retry support                           */
     /* Return code 20 from the Register call tells us the target server  */
     /* is not available and we should queue a retry request.    @PI52665A*/
 	 if (rcode == 20) {

 	     rcode = 0;
         ISSUE_MSG(&processInfo, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_SKIP, RETRY_ENABLED,
         	"Command RETRY mode is enabled. Command will be retried.");

         DEBUG2("BBOC command length (requestlgth) : %i", requestlgth);

         retrylgth = requestlgth;

         DEBUG1("Request retry seconds set to : %i",
             parsedParameters.retint.intData);

         memcpy(&retryparms, &requestparms, sizeof(retryparms));

         // Here we are putting the Link server into 'retry mode'.
         // We need to make sure we will not exceed the max length
         // command string (500 chars) first. We need to make sure
         // there's enough room to insert the 'RETMODE=Y' string.
         if (requestlgth > 500) {
           ISSUE_MSG(&processInfo, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR,
                      RETRY_ERROR_DATA_TOO_LONG,
                      "Command RETRY error, parameter data too long.");
                      exit(rcode);
         }

         int offs_lastchar = 0;
         for (int i=requestlgth-1; i >= 0; i--) {
            if ((requestparms[i] != ' ') &&
                (requestparms[i] != 0x00)) {
                offs_lastchar = i;
                break;
            }
         }

         DEBUG2("Last non-blank offset: %d", offs_lastchar);

         memcpy((void *)((int)&retryparms+offs_lastchar+1),
                " RETMODE=Y", 10);

         retrylgth = retrylgth + 10;

         DEBUG2("Retryparms@: %x",retryparms);
         DEBUG2("Retrylgth: %d",retrylgth);
         DEBUG2("Retryparms data 1: %.50s", retryparms);
         DEBUG2("Retryparms data 2: %.50s", retryparms+50);
         DEBUG2("Retryparms data 3: %.50s", retryparms+100);
         DEBUG2("Retryparms data 4: %.50s", retryparms+150);
         DEBUG2("Retryparms data 5: %.50s", retryparms+200);

         /* Delay 1 second to ensure the rand() seed is unique */
         EXEC CICS DELAY FOR SECONDS(retrydelay)
             RESP(respcode) RESP2(respcode2);

         if (respcode != dfhresp(NORMAL)) {
             /* Should not occur- issue a message just in case @PI52665A*/
             DEBUG0("EXEC CICS DELAY ERROR EIBRESP: %i EIBRESP2: %i",
              respcode, respcode2);
             exit(12);
         }

         srand((unsigned) time(NULL));
         int reqid_int = rand() % 9999;

         char reqid[9];
         sprintf(reqid, "BBOC%04.4d", reqid_int);

         DEBUG2("REQID for EXEC CICS START is %.8s ", reqid); /* @PI52665A*/

         /* If BBOACNTL was EC Linked to, then we want do not want to start the retry  */
         /* mode link server with the current EIB transaction ID. We want to instead   */
         /* use the default BBOC transaction.                                          */

         char bboc_invokedby[8];
         memset (bboc_invokedby, ' ', sizeof(bboc_invokedby));
         EXEC CICS ASSIGN INVOKINGPROG(bboc_invokedby)
                RESP(respcode) RESP2(respcode2);

         if (respcode != dfhresp(NORMAL)) {
             /* Should not occur- issue a message just in case @PI52665A*/
             DEBUG0("EXEC CICS ASSIGN ERROR EIBRESP: %i EIBRESP2: %i",
              respcode, respcode2);
             exit(12);
         }

         DEBUG2("BBOACNTL was invoked by program: %.8s ", bboc_invokedby); /* @PI52665A*/

         char bbocTx[4];
         memcpy(bbocTx, dfheiptr->eibtrnid, sizeof(bbocTx));
         if (memcmp(bboc_invokedby, "        ",sizeof(bboc_invokedby)) != 0) {
             memcpy(bbocTx, "BBOC", sizeof(bbocTx));
         }

         DEBUG2("Starting command retry using transaction: %.4s ", bbocTx);

         EXEC CICS START TRANSID(bbocTx)
            	FROM(&retryparms) LENGTH(retrylgth)
                REQID(reqid)
           		RESP(respcode) RESP2(respcode2);

         if (respcode != dfhresp(NORMAL)) {
       	   char respstr[8];
   		   char respstr2[8];
       	   snprintf(respstr, sizeof(respstr), "%d", respcode);
       	   snprintf(respstr2, sizeof(respstr2), "%d", respcode2);
      	   ISSUE_MSG(&processInfo, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_ERROR,
      		RETRY_START_ERROR,
            "EXEC CICS START TRANSID command failed for the retry. EIBRESP = %s. and EIBRESP2 = %s",
                 	respstr, respstr2);
         } else {
            ISSUE_MSG(&processInfo, screenmsg, smsglen, BBOACNTL_COMMAREA_RC_NORMAL,
             RETRY_START_OK,
              "Request for command retry. A new BBOC task to be initiated in retry mode.");
         }
	 }  // End handle Retry (rc=20)

     /* ----------------------------------------------------------------- */
     /* END @PI52665A                                                     */
     /* END @PI52665A                                                     */
     /* END @PI52665A                                                     */
     /* ------------------------------------------------------------------*/

     timedif = ( ((double) clock()) / CLOCKS_PER_SEC) - time1;

     time(&finish);
     newtime = localtime(&finish);

     DEBUG0("Return Code: %d Reason Code: %d", rcode, reason);
     DEBUG0("Elapsed time: %f seconds", difftime(finish,start));
     DEBUG0("Elapsed CPU time: %f seconds", timedif);
     DEBUG0("<==========  ADAPTERS CONTROL TASK END   ===  %.20s  =====>", asctime(newtime));

     // There are exit() calls all over the place in here.... just close the catalog on
     // the main path.
     closeWolaMessageCatalog(&((PROCESS_INFO_PTR)->baseInfo));

     return(0);
  }
