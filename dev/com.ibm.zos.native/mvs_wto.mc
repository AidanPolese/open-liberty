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

#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

#include "include/mvs_wto.h"

#include "include/common_defines.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"

#include "include/gen/native_messages_mc.h"

//---------------------------------------------------------------------
// RAS constants
//---------------------------------------------------------------------
#define RAS_MODULE_CONST  RAS_MODULE_MVS_WTO
#define TP_MVS_UTILS_WTO_CMDRESPONSE_ENTRY          1
#define TP_MVS_UTILS_WTO_CMDRESPONSE_FIRST_DTYPE    2
#define TP_MVS_UTILS_WTO_CMDRESPONSE_FIRST_CONT     3
#define TP_MVS_UTILS_WTO_CMDRESPONSE_SPLIT_DETYPE   4
#define TP_MVS_UTILS_WTO_CMDRESPONSE_SPLIT_DTYPE    5
#define TP_MVS_UTILS_WTO_CMDRESPONSE_PRIOR_WTO_ML   6
#define TP_MVS_UTILS_WTO_CMDRESPONSE_RETURN_WTO_ML  7
#define TP_MVS_UTILS_WTO_ENTRY                      8
#define TP_MVS_UTILS_WTO_FIRST_DTYPE                9
#define TP_MVS_UTILS_WTO_FIRST_CONT                 10
#define TP_MVS_UTILS_WTO_SPLIT_DETYPE               11
#define TP_MVS_UTILS_WTO_SPLIT_DTYPE                12
#define TP_MVS_UTILS_WTO_PRIOR_WTO_ML               13
#define TP_MVS_UTILS_WTO_RETURN_WTO_ML              14

//---------------------------------------------------------------------
// List forms of WTO
//---------------------------------------------------------------------
__asm(" WTO TEXT=,DESC=(4),ROUTCDE=(2),MF=L" : "DS"(list_wto));
__asm(" WTO TEXT=((,D),(,D),(,D),(,D),(,D),(,D),(,D),(,D),(,D),(,DE)),"
      "DESC=(4),ROUTCDE=(2),MF=L" : "DS"(list_wto_ml));

__asm(" WTO TEXT=,DESC=(5),CONSID=,CART=,MCSFLAG=(RESP),MF=L" : "DS"(list_wtp_resp));
__asm(" WTO TEXT=((,D),(,D),(,D),(,D),(,D),(,D),(,D),(,D),(,D),(,DE)),"
      "DESC=(5),CONSID=,CART=,MCSFLAG=(RESP),MF=L" : "DS"(list_wtp_resp_ml));

__asm(" WTO TEXT=,ROUTCDE=(11),MCSFLAG=(HRDCPY),MF=L" : "DS"(list_wtp_hardcopy));
__asm(" WTO TEXT=((,D),(,D),(,D),(,D),(,D),(,D),(,D),(,D),(,D),(,DE)),"
      "ROUTCDE=(11),MCSFLAG=(HRDCPY),MF=L" : "DS"(list_wtp_hardcopy_ml));

/*
 * Maximum message supported is 10 lines. This is because
 * unauthorized multiline WTO limits it to 10.  We'll issue a set of 10
 * line multiline WTOs but stop at a 1000.
 */

#define MAXIMUMMSGLEN1LINE 70          //!< Maximum characters in each line of a multiline message
#define MAXIMUMMULTILINES  10          //!< Maximum number of lines in an unauthorized multiline WTO
#define CONTINUEMAX        1000        //!< Maximum multiline WTOs issued for a single command response message

/*
 * Identifiers for the pieces of the mult-line WTO
 */
#define WTOMLINETYPE_DATA     0x2000   //!< "D"  type: data
#define WTOMLINETYPE_DATAEND  0x3000   //!< "DE" type: data, plus end of message


/**
 * Structure of the message text when supplied by reference (ex. pointed to by WPX)
 */
#pragma pack(1)
typedef struct WTO_Line  {
    unsigned short     wto_textLen;      //!< 2-Byte length preceeding message text
    char               wto_text[MAXIMUMMSGLEN1LINE];  //!< Message text
} WTO_Line;
#pragma pack(reset)

/**
 * Structure of the nth line of a multiline message proceeding the WPX area
 */
#pragma pack(1)
typedef struct WtoMLine_t  {
    unsigned short     wtoMLineTextLen;  //!< Text field length + 4
    unsigned short     wtoMlineType;     //!< Line type for nth line
    void* __ptr32      wtoMText2;        //!< Text part of nth piece of WTO (Len followed by text
} WtoMLine_t;
#pragma pack(reset)

/**
 * WTO parmlist for 10 line multiline for command response
 */
#pragma pack(1)
typedef struct WTO_Multiline  {
    unsigned short     wtoM_msg1Len;     //!< Text field length + 4
    unsigned char      wtoM_wplmcsf1;    //!< MCS Flag1
    unsigned char      wtoM_wplmcsf2;    //!< MCS Flag2
    void* __ptr32      wtoM_msg1Addr;    //!< Message1 Text (Text length proceeding Text)

    unsigned char      wtoM_wpxvrsn;     //!< Version Level (02)
    unsigned char      wtoM_wpxflags;    //!< Subsystem Flags
    unsigned char      wtoM_wpxrpyln;    //!< Length of Reply Buffer
    unsigned char      wtoM_wpxlngths;   //!< Length of WPX

    unsigned char      wtoM_wpxmcs1;     //!< First Byte of Extended MCS Flags
    unsigned char      wtoM_wpxmcs2;     //!< Second Byte of Extended MCS Flags
    unsigned char      wtoM_wpxcpfl1;    //!< Flags for control program use byte1
    unsigned char      wtoM_wpxcpfl2;    //!< Flags for control program use byte2

    void* __ptr32      wtoM_wpxrpbuf;    //!< Reply buffer Address
    void* __ptr32      wtoM_wpxecbp;     //!< Reply ECB Address

    int                wtoM_wpxseqn;     //!< Connect ID

    unsigned char      wtoM_wpxdesc1;    //!< First byte of Descriptor codes
    unsigned char      wtoM_wpxdesc2;    //!< Second byte of Descriptor codes
    unsigned char      wtoM_wpxdesc3;    //!< Reserved
    unsigned char      wtoM_wpxdesc4;    //!< Reserved

    unsigned char      wtoM_wpxrout[16]; //!< Routing codes
    unsigned char      wtoM_wpxmsgty[2]; //!< Message Type Flags

    short              wtoM_rsv73;       //!< Reserved, was wpxprty
    char               wtoM_wpxjobid[8]; //!< Job ID
    char               wtoM_wpxjobnm[8]; //!< Job name
    char               wtoM_wpxkey[8];   //!< Key retrieval
    void* __ptr32      wtoM_wpxtokn;     //!< Token for DOM
    void* __ptr32      wtoM_wpxcnid;     //!< Console ID
    char               wtoM_wpxsysna[8]; //!< System name
    char               wtoM_wpxcnnme[8]; //!< Console name
    void* __ptr32      wtoM_wpxrcna;     //!< Address of 12-byte field for replying
    void* __ptr32      wtoM_wpxcart;     //!< Address of CART
    void* __ptr32      wtoM_wpxwsprm;    //!< Address of wait state parm list

    unsigned short     wtoM_wplltf;      //!< Message 1, data type
    unsigned char      wtoM_wplarea;     //!< Area ID
    unsigned char      wtoM_wpllines;    //!< Number of lines in Multi-line

    WtoMLine_t         wtoMLine[MAXIMUMMULTILINES-1]; //!< Add'l lines of multiline WTO
} WTO_Multiline;
#pragma pack(reset)

/**
 * Find the index within the text string of the place to split the
 * string.  The result is the character before the last blank.
 * Note that blanks includes characters which might become a blank.
 *
 * @param strPtr pointer to the start of the text string to process.
 * @param strLen length of the text string to process.
 *
 * @return Index to character where the split is done.  The final
 *         message should NOT include this character
 */
static int
findSplitLen(const char* strPtr, int strLen) {
    const char* strEndPtr = strPtr + strLen -1;

    // Search for <newlines> first
    for (const char* currentPtr = strEndPtr; currentPtr >= strPtr; currentPtr = currentPtr-1) {
        if (*currentPtr == '\n') {
            return (currentPtr - strPtr);
        }
    }

    // If no <newlines> then look for spaces
    for (const char* currentPtr = strEndPtr; currentPtr >= strPtr; currentPtr = currentPtr-1) {
        if (*currentPtr == ' ') {
            return (currentPtr - strPtr);
        }
    }

    return 0;
}

/**
 * Find the number of trailing spaces to remove
 *
 * @param strPtr pointer to the start of the text string.
 * @param strLen length of the text string to process.
 *
 * @return number of trailing spaces to remove.
 */
static int
findTrailingSpaces(const char* strPtr, int strLen) {
    int numSpaces = 0;
    const char* strEndPtr = strPtr + strLen -1;

    // look for spaces at end
    for (const char* currentPtr = strEndPtr; currentPtr >= strPtr; currentPtr = currentPtr-1) {
        if (*currentPtr != ' ') {
            return (numSpaces);
        }
        numSpaces++;
    }

    return numSpaces;
}

/**
 * Find the number of leading spaces to remove
 *
 * @param strPtr pointer to the start of the text string.
 * @param strLen length of the text string to process.
 *
 * @return number of leading spaces to remove.
 */
static int
findLeadingSpaces(const char* strPtr, int strLen) {
    int numSpaces = 0;
    const char* strEndPtr = strPtr + strLen -1;

    // look for spaces at beginning
    for (const char* currentPtr = strPtr; currentPtr <= strEndPtr; currentPtr = currentPtr+1) {
        if (*currentPtr != ' ') {
            return (numSpaces);
        }
        numSpaces++;
    }
    return numSpaces;
}


/**
 * Issue message to the requested location
 *
 * @param message_p message to be issued (null terminated string).
 * @param cart_p    MVS Command and response token (CART).
 * @param location  location where the message is to be issued
 *
 * @retval 0 no errors
 * @retval -4 unable to obtain storage to process the request
 * @retval -8 input message was null
 * @retval -12 command response exceeded 1000 multi line WTOs
 *
 * @return > 0 return code from WTO
 *
 * @note made available as an unauthorized service (via UNAUTH_DEF).
 */
int
write_to_operator_location(char* message_p,
                               char* cart_p,
                               WtoLocation  location) {

    struct parm31 {
        char wto_text[73];
        char cart[8];
        int rc;
        int outMsgId;
        WTO_Line wto_textML[MAXIMUMMULTILINES];
        char execute_wto[BBGZ_max(sizeof(list_wto_ml), sizeof(list_wtp_hardcopy_ml))];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(TP_MVS_UTILS_WTO_ENTRY),
                "write_to_operator_location, entry",
                TRACE_DATA_STRING((message_p ? message_p : "null"), "message_p"),
                TRACE_DATA_END_PARMS);
    }

    int returnCode = 0;

    if (message_p != NULL) {
        bbgz_psw my_psw;
        extractPSW(&my_psw);

        int subpool = (my_psw.key == 8) ? 0 : 229;

        // We are doing a storage obtain here instead of __malloc31 so that the
        // caller does not need to have a metal C environment set up before
        // calling this method.  If the storage obtain is too expensive, we
        // should sweep the code and make sure that all callers have a metal
        // C environment before changing this to __malloc31.
        struct parm31* parm_p = storageObtain(sizeof(struct parm31), subpool, my_psw.key, NULL);

        if (parm_p != NULL) {
            int message_len = strlen(message_p);
            if (message_len <= MAXIMUMMSGLEN1LINE) {
                // Issue a single-line WTO
                if (location == WTO_PROGRAMMER_HARDCOPY) {
                    memcpy(parm_p->execute_wto, &list_wtp_hardcopy, sizeof(list_wtp_hardcopy));
                } else {
                    memcpy(parm_p->execute_wto, &list_wto, sizeof(list_wto));
                }
                parm_p->wto_text[0] = 0;
                parm_p->wto_text[1] = message_len;

                strncpy(&(parm_p->wto_text[2]), message_p, parm_p->wto_text[1]);

                if ((location == WTO_OPERATOR_CONSOLE) && (cart_p != NULL)) {
                    memcpy(parm_p->cart, cart_p, sizeof(parm_p->cart));
                    __asm(" SAM31\n"
                        " SLR 0,0\n"
                        " SYSSTATE AMODE64=NO\n"
                        " WTO TEXT=((%2)),CART=(%3),MF=(E,(%1))\n"
                        " SYSSTATE AMODE64=YES\n"
                        " ST 15,%0\n"
                        " SAM64" :
                        "=m"(parm_p->rc) :
                        "r"(parm_p->execute_wto), "r"(parm_p->wto_text), "r"(parm_p->cart) :
                        "r0","r1","r14","r15");
                } else {
                    __asm(" SAM31\n"
                        " SLR 0,0\n"
                        " SYSSTATE AMODE64=NO\n"
                        " WTO TEXT=((%2)),MF=(E,(%1))\n"
                        " SYSSTATE AMODE64=YES\n"
                        " ST 15,%0\n"
                        " SAM64" :
                        "=m"(parm_p->rc) :
                        "r"(parm_p->execute_wto),"r"(parm_p->wto_text) :
                        "r0","r1","r14","r15");
                }
                returnCode = parm_p->rc;
            } else {
                char firstMultlineMsgID[16];
                // Issue a Muli-line WTO command
                WTO_Multiline* ml_Wto_Ptr = (WTO_Multiline *)&(parm_p->execute_wto[0]);
                if (location == WTO_PROGRAMMER_HARDCOPY) {
                    memcpy(parm_p->execute_wto, &list_wtp_hardcopy_ml, sizeof(list_wtp_hardcopy_ml));
                } else {
                    memcpy(parm_p->execute_wto, &list_wto_ml, sizeof(list_wto_ml));
                }
                memset(parm_p->wto_textML, ' ', sizeof(parm_p->wto_textML));

                WTO_Line* currentMsgPtr = &(parm_p->wto_textML[0]);

                // Put first part of message string into WTO parm list.
                int splitLen = findSplitLen(message_p, MAXIMUMMSGLEN1LINE);
                if (splitLen <= 12) {
                    // Split msg to fit string into entire buffer
                    splitLen = MAXIMUMMSGLEN1LINE;
                }

                int leadingSpaces = 0;
                int trailingSpaces = findTrailingSpaces(message_p, splitLen);

                // Build and Set info for Message 1 into the WTO parmlist
                memset(currentMsgPtr->wto_text, ' ', MAXIMUMMSGLEN1LINE);
                memcpy(currentMsgPtr->wto_text, message_p, (splitLen-trailingSpaces));
                currentMsgPtr->wto_textLen = splitLen-trailingSpaces;

                ml_Wto_Ptr->wtoM_msg1Len = sizeof(ml_Wto_Ptr->wtoM_msg1Addr) + 4;
                ml_Wto_Ptr->wtoM_msg1Addr = currentMsgPtr;
                ml_Wto_Ptr->wtoM_wplltf = WTOMLINETYPE_DATA;

                int remainingLen = message_len - splitLen;
                const char * textPtr = message_p + splitLen;

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(TP_MVS_UTILS_WTO_FIRST_DTYPE),
                                "write_to_operator_location, formatted first D-Type",
                                TRACE_DATA_PTR32((message_p ? message_p : NULL),
                                                 "message_p"),
                                TRACE_DATA_HEX_INT(message_len,
                                                   "message_len"),
                                TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                 "textPtr"),
                                TRACE_DATA_HEX_INT(leadingSpaces,
                                                   "leadingSpaces"),
                                TRACE_DATA_HEX_INT(trailingSpaces,
                                                   "trailingSpaces"),
                                TRACE_DATA_HEX_INT(remainingLen,
                                                   "remainingLen"),
                                TRACE_DATA_END_PARMS);
                }

                // The Following loop is to break up the message into multiple
                // messages if the message will not fit into one 10 line
                // message.
                int continue_count = 0;
                for (;
                     ((remainingLen > 0) && (continue_count < CONTINUEMAX) && (returnCode == 0));
                     continue_count++) {

                    // Nth time thru loop build and insert message that connects subsequent
                    // multiline messages together
                    if (continue_count > 0) {
                        if (location == WTO_PROGRAMMER_HARDCOPY) {
                            memcpy(parm_p->execute_wto, &list_wtp_hardcopy_ml, sizeof(list_wtp_hardcopy_ml));
                        } else {
                            memcpy(parm_p->execute_wto, &list_wto_ml, sizeof(list_wto_ml));
                        }

                        memset(parm_p->wto_textML, ' ', sizeof(parm_p->wto_textML));

                        currentMsgPtr = &(parm_p->wto_textML[0]);

                        // Build the "Continuation" message as first line of multiline
                        char continue_count_string[16];
                        snprintf(continue_count_string, sizeof(continue_count_string), "%u", continue_count);
                        snprintf(currentMsgPtr->wto_text,
                                 MAXIMUMMSGLEN1LINE, //sizeof(currentMsgPtr->wto_text),
                                 UTILITIES_WTO_CMDRESPONSE_CONTINUE,
                                 continue_count_string,
                                 firstMultlineMsgID);
                        currentMsgPtr->wto_textLen = sizeof(currentMsgPtr->wto_text);

                        ml_Wto_Ptr->wtoM_msg1Len = sizeof(ml_Wto_Ptr->wtoM_msg1Addr) + 4;
                        ml_Wto_Ptr->wtoM_msg1Addr = currentMsgPtr;
                        ml_Wto_Ptr->wtoM_wplltf = WTOMLINETYPE_DATA;

                        if (TraceActive(trc_level_detailed)) {
                            TraceRecord(trc_level_detailed,
                                        TP(TP_MVS_UTILS_WTO_FIRST_CONT),
                                        "write_to_operator_location, formatted first Cont",
                                        TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                         "textPtr"),
                                        TRACE_DATA_HEX_INT(leadingSpaces,
                                                           "leadingSpaces"),
                                        TRACE_DATA_HEX_INT(trailingSpaces,
                                                           "trailingSpaces"),
                                        TRACE_DATA_HEX_INT(remainingLen,
                                                           "remainingLen"),
                                        TRACE_DATA_END_PARMS);
                        }
                    }

                    // Put the remaining parts of the message into the parm list.
                    // For each portion of the message:
                    // (1) Find the place to split the text,
                    // (2) Copy the portion of the input text into WTO parm list,
                    //     without split characters
                    // (3) Remove leading AND trailing spaces
                    int lineIndex = 1;
                    for (;(remainingLen > 0) && (lineIndex < MAXIMUMMULTILINES);) {
                        // If the remaining text fits in the parm list
                        if (remainingLen <= MAXIMUMMSGLEN1LINE) {
                            splitLen = remainingLen;
                            trailingSpaces = findTrailingSpaces(textPtr, splitLen);
                            leadingSpaces  = findLeadingSpaces(textPtr, splitLen);

                            if (TraceActive(trc_level_detailed)) {
                                TraceRecord(trc_level_detailed,
                                            TP(TP_MVS_UTILS_WTO_SPLIT_DETYPE),
                                            "write_to_operator_location, splitting for DE-type",
                                            TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                             "textPtr"),
                                            TRACE_DATA_HEX_INT(leadingSpaces,
                                                              "leadingSpaces"),
                                            TRACE_DATA_HEX_INT(trailingSpaces,
                                                               "trailingSpaces"),
                                            TRACE_DATA_HEX_INT(remainingLen,
                                                               "remainingLen"),
                                            TRACE_DATA_RAWDATA(remainingLen,
                                                               textPtr,
                                                               "remaining message"),
                                            TRACE_DATA_END_PARMS);
                            }

                            // Skip if all blanks
                            if (trailingSpaces != splitLen) {
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMLineTextLen =
                                    sizeof(ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2) + 4;
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMlineType =
                                    WTOMLINETYPE_DATAEND;

                                currentMsgPtr = &(parm_p->wto_textML[lineIndex]);
                                currentMsgPtr->wto_textLen = remainingLen-trailingSpaces-leadingSpaces;
                                memcpy(currentMsgPtr->wto_text,
                                       textPtr + leadingSpaces,
                                       currentMsgPtr->wto_textLen);
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2 = currentMsgPtr;

                                lineIndex++;
                            }

                            remainingLen = 0;
                        }
                        else {
                            // Remaining length does not fit.  Split text string
                            splitLen = findSplitLen(textPtr, MAXIMUMMSGLEN1LINE);
                            if (splitLen == 0) {
                                // Split msg to fit string into entire buffer
                                splitLen = MAXIMUMMSGLEN1LINE;
                            }

                            trailingSpaces = findTrailingSpaces(textPtr, splitLen);
                            leadingSpaces  = findLeadingSpaces(textPtr, splitLen);

                            if (TraceActive(trc_level_detailed)) {
                                TraceRecord(trc_level_detailed,
                                            TP(TP_MVS_UTILS_WTO_SPLIT_DTYPE),
                                            "write_to_operator_location, splitting for D-type",
                                            TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                             "textPtr"),
                                            TRACE_DATA_HEX_INT(leadingSpaces,
                                                             "leadingSpaces"),
                                            TRACE_DATA_HEX_INT(trailingSpaces,
                                                             "trailingSpaces"),
                                            TRACE_DATA_HEX_INT(remainingLen,
                                                             "remainingLen"),
                                            TRACE_DATA_HEX_INT(splitLen,
                                                             "splitLen"),
                                            TRACE_DATA_RAWDATA(remainingLen,
                                                               textPtr,
                                                               "remaining message"),
                                            TRACE_DATA_END_PARMS);
                            }

                            // Skip if all blanks
                            if (trailingSpaces != splitLen) {
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMLineTextLen =
                                    sizeof(ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2) + 4;
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMlineType =
                                    WTOMLINETYPE_DATA;

                                currentMsgPtr = &(parm_p->wto_textML[lineIndex]);
                                currentMsgPtr->wto_textLen = splitLen-trailingSpaces-leadingSpaces;
                                memcpy(currentMsgPtr->wto_text,
                                       textPtr + leadingSpaces,
                                       currentMsgPtr->wto_textLen);

                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2 = currentMsgPtr;

                                lineIndex++;
                            }
                            remainingLen = remainingLen - splitLen;
                            textPtr = textPtr + splitLen;
                        }
                    }   // end for, split text into a multiline

                    // If more than one line in message issue message.
                    // If only one line in message because rest was just blanks
                    // and first message issue message.
                    ml_Wto_Ptr->wtoM_wpllines = lineIndex;

                    if (lineIndex == 1) {
                        ml_Wto_Ptr->wtoM_wplltf = WTOMLINETYPE_DATAEND;
                    }
                    if (lineIndex == MAXIMUMMULTILINES) {
                        // Mark the previous line as last if we hit
                        ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMlineType = WTOMLINETYPE_DATAEND;
                    }

                    if (TraceActive(trc_level_detailed)) {
                        int sizeToTrace;
                        if (location == WTO_PROGRAMMER_HARDCOPY) {
                            sizeToTrace = sizeof(list_wtp_hardcopy_ml);
                        } else {
                            sizeToTrace = sizeof(list_wto_ml);
                        }
                        TraceRecord(trc_level_detailed,
                                    TP(TP_MVS_UTILS_WTO_PRIOR_WTO_ML),
                                    "write_to_operator_location, prior to WTO ML",
                                    TRACE_DATA_INT(lineIndex,"lineIndex"),
                                    TRACE_DATA_RAWDATA(sizeof(parm_p->wto_textML), parm_p->wto_textML, "message area"),
                                    TRACE_DATA_RAWDATA(sizeToTrace, parm_p->execute_wto, "WPL"),
                                    TRACE_DATA_END_PARMS);
                    }
                    if ((location == WTO_OPERATOR_CONSOLE) && (cart_p != NULL)) {
                        memcpy(parm_p->cart, cart_p, sizeof(parm_p->cart));
                        __asm(" SAM31\n"
                              " SLR 0,0\n"
                              " SYSSTATE AMODE64=NO\n"
                              " WTO CART=(%3),MF=(E,(%2))\n"
                              " SYSSTATE AMODE64=YES\n"
                              " ST 15,%0\n"
                              " ST 1,%1\n"
                              " SAM64" :
                              "=m"(parm_p->rc), "=m"(parm_p->outMsgId) :
                              "r"(parm_p->execute_wto), "r"(parm_p->cart) :
                              "r0","r1","r14","r15");
                    } else {
                        __asm(" SAM31\n"
                              " SLR 0,0\n"
                              " SYSSTATE AMODE64=NO\n"
                              " WTO MF=(E,(%2))\n"
                              " SYSSTATE AMODE64=YES\n"
                              " ST 15,%0\n"
                              " ST 1,%1\n"
                              " SAM64" :
                              "=m"(parm_p->rc), "=m"(parm_p->outMsgId) :
                              "r"(parm_p->execute_wto) :
                              "r0","r1","r14","r15");
                    }
                    returnCode = parm_p->rc;

                    // Save WTO Message ID from first multiline WTO to use in continuation message
                    if (continue_count == 0) {
                        snprintf(firstMultlineMsgID, sizeof(firstMultlineMsgID), "%u", parm_p->outMsgId);
                    }

                    if (TraceActive(trc_level_detailed)) {
                        TraceRecord(trc_level_detailed,
                                    TP(TP_MVS_UTILS_WTO_RETURN_WTO_ML),
                                    "write_to_operator_location, return from WTO ML",
                                    TRACE_DATA_INT(parm_p->rc,"parm_p->rc"),
                                    TRACE_DATA_INT(parm_p->outMsgId,"parm_p->outMsgId"),
                                    TRACE_DATA_INT(continue_count, "continue_count"),
                                    TRACE_DATA_END_PARMS);
                    }
                }   // end for, each continuation of 10-line multlline WTOs

                if (continue_count >= CONTINUEMAX) {
                    returnCode = -12;
                }
            }
            storageRelease(parm_p, sizeof(struct parm31), subpool, my_psw.key);
        } else {
            // Could not obtain enough storage below the Bar for parms.
            returnCode = -4;
        }
    } else {
        // No input message
        returnCode = -8;
    }
    return returnCode;
}

//---------------------------------------------------------------------
// Write to operator
//---------------------------------------------------------------------
int
write_to_operator(char* message_p, char* cart_p) {
    int rc = write_to_operator_location(message_p, cart_p, WTO_OPERATOR_CONSOLE);
    return rc;
}

/**
 * Issue command response using multiline WTO as necessary
 *
 * @param message_p command response message to be issued (null terminated string).
 * @param responseCart MVS Command and response token (CART).
 * @param conId target console identifier.
 * @param rc output return code
 *
 * @retval 0 no errors
 * @retval -4 unable to obtain storage to process the request
 * @retval -8 input message was null
 * @retval -12 command response exceeded 1000 multi line WTOs
 *
 * @return > 0 return code from WTO
 *
 * @note made available as an unauthorized service (via UNAUTH_DEF).
 */
int
write_to_operator_response(const char* message_p, const long* responseCart, const int* conId, int* rc) {
    *rc = 0;

    struct parm31 {
        char swto_text[73];
        int console_id;
        char cart[8];
        int rc;
        int outMsgId;
        WTO_Line wto_textML[MAXIMUMMULTILINES];
        char execute_wto[sizeof(list_wtp_resp_ml)];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(TP_MVS_UTILS_WTO_CMDRESPONSE_ENTRY),
                "write_to_operator_response, entry",
                TRACE_DATA_STRING((message_p ? message_p : "null"),
                                  "message_p"),
                TRACE_DATA_LONG((responseCart ? *responseCart : 0),
                                "responseCart"),
                TRACE_DATA_INT((conId ? *conId : 0),
                               "conId"),
                TRACE_DATA_END_PARMS);
    }

    if (message_p != NULL) {
        struct parm31* parm_p = __malloc31(sizeof(struct parm31));
        if (parm_p != NULL) {
            int message_len = strlen(message_p);

            memcpy(parm_p->cart, responseCart, sizeof(parm_p->cart));
            memcpy(&(parm_p->console_id), conId, sizeof(parm_p->console_id));

            if (message_len <= MAXIMUMMSGLEN1LINE) {
                // Issue a single-line WTO response

                memcpy(parm_p->execute_wto, &list_wtp_resp, sizeof(list_wtp_resp));

                parm_p->swto_text[0] = 0;
                parm_p->swto_text[1] = message_len;
                if (parm_p->swto_text[1] > MAXIMUMMSGLEN1LINE)
                    parm_p->swto_text[1] = MAXIMUMMSGLEN1LINE; /* Max size of WTO text */

                strncpy(&(parm_p->swto_text[2]), message_p, parm_p->swto_text[1]);


                __asm(" SAM31\n"
                    " SLR 0,0\n"
                    " SYSSTATE AMODE64=NO\n"
                    " WTO TEXT=((%2)),MCSFLAG=(RESP),CONSID=(%3),CART=(%4),MF=(E,(%1))\n"
                    " SYSSTATE AMODE64=YES\n"
                    " ST 15,%0\n"
                    " SAM64" :
                      "=m"(parm_p->rc) :
                      "r"(parm_p->execute_wto), "r"(parm_p->swto_text),
                      "r"(parm_p->console_id), "r"(parm_p->cart) :
                      "r0","r1","r14","r15");

                *rc = parm_p->rc;
            }
            else {
                char firstMultlineMsgID[16];

                // Issue a Muli-line WTO command response
                WTO_Multiline* ml_Wto_Ptr = (WTO_Multiline *)&(parm_p->execute_wto[0]);

                memcpy(parm_p->execute_wto, &list_wtp_resp_ml, sizeof(list_wtp_resp_ml));
                memset(parm_p->wto_textML, ' ', sizeof(parm_p->wto_textML));

                WTO_Line* currentMsgPtr = &(parm_p->wto_textML[0]);

                // Put first part of message string into WTO parm list.
                int splitLen = findSplitLen(message_p, MAXIMUMMSGLEN1LINE);
                if (splitLen <= 12) {
                    // Split msg to fit string into entire buffer
                    splitLen = MAXIMUMMSGLEN1LINE;
                }

                int leadingSpaces = 0;
                int trailingSpaces = findTrailingSpaces(message_p, splitLen);

                // Build and Set info for Message 1 into the WTO parmlist
                memset(currentMsgPtr->wto_text, ' ', MAXIMUMMSGLEN1LINE);
                memcpy(currentMsgPtr->wto_text, message_p, (splitLen-trailingSpaces));
                currentMsgPtr->wto_textLen = splitLen-trailingSpaces;

                ml_Wto_Ptr->wtoM_msg1Len = sizeof(ml_Wto_Ptr->wtoM_msg1Addr) + 4;
                ml_Wto_Ptr->wtoM_msg1Addr = currentMsgPtr;
                ml_Wto_Ptr->wtoM_wplltf = WTOMLINETYPE_DATA;

                int remainingLen = message_len - splitLen;
                const char * textPtr = message_p + splitLen;

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(TP_MVS_UTILS_WTO_CMDRESPONSE_FIRST_DTYPE),
                                "write_to_operator_response, formatted first D-Type",
                                TRACE_DATA_PTR32((message_p ? message_p : NULL),
                                                 "message_p"),
                                TRACE_DATA_HEX_INT(message_len,
                                                   "message_len"),
                                TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                 "textPtr"),
                                TRACE_DATA_HEX_INT(leadingSpaces,
                                                   "leadingSpaces"),
                                TRACE_DATA_HEX_INT(trailingSpaces,
                                                   "trailingSpaces"),
                                TRACE_DATA_HEX_INT(remainingLen,
                                                   "remainingLen"),
                                TRACE_DATA_END_PARMS);
                }

                // The Following loop is to break up the message into multiple
                // messages if the message will not fit into one 10 line
                // message.
                int continue_count = 0;
                for (;
                     ((remainingLen > 0) && (continue_count < CONTINUEMAX) && (*rc == 0));
                     continue_count++) {

                    // Nth time thru loop build and insert message that connects subsequent
                    // multiline messages together
                    if (continue_count > 0) {
                        memcpy(parm_p->execute_wto, &list_wtp_resp_ml, sizeof(list_wtp_resp_ml));
                        memset(parm_p->wto_textML, ' ', sizeof(parm_p->wto_textML));

                        currentMsgPtr = &(parm_p->wto_textML[0]);

                        // Build the "Continuation" message as first line of multiline
                        char continue_count_string[16];
                        snprintf(continue_count_string, sizeof(continue_count_string), "%u", continue_count);
                        snprintf(currentMsgPtr->wto_text,
                                 MAXIMUMMSGLEN1LINE, //sizeof(currentMsgPtr->wto_text),
                                 UTILITIES_WTO_CMDRESPONSE_CONTINUE,
                                 continue_count_string,
                                 firstMultlineMsgID);
                        currentMsgPtr->wto_textLen = sizeof(currentMsgPtr->wto_text);

                        ml_Wto_Ptr->wtoM_msg1Len = sizeof(ml_Wto_Ptr->wtoM_msg1Addr) + 4;
                        ml_Wto_Ptr->wtoM_msg1Addr = currentMsgPtr;
                        ml_Wto_Ptr->wtoM_wplltf = WTOMLINETYPE_DATA;


                        if (TraceActive(trc_level_detailed)) {
                            TraceRecord(trc_level_detailed,
                                        TP(TP_MVS_UTILS_WTO_CMDRESPONSE_FIRST_CONT),
                                        "write_to_operator_response, formatted first Cont",
                                        TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                         "textPtr"),
                                        TRACE_DATA_HEX_INT(leadingSpaces,
                                                           "leadingSpaces"),
                                        TRACE_DATA_HEX_INT(trailingSpaces,
                                                           "trailingSpaces"),
                                        TRACE_DATA_HEX_INT(remainingLen,
                                                           "remainingLen"),
                                        TRACE_DATA_END_PARMS);
                        }
                    }

                    // Put the remaining parts of the message into the parm list.
                    // For each portion of the message:
                    // (1) Find the place to split the text,
                    // (2) Copy the portion of the input text into WTO parm list,
                    //     without split characters
                    // (3) Remove leading AND trailing spaces
                    int lineIndex = 1;
                    for (;(remainingLen > 0) && (lineIndex < MAXIMUMMULTILINES);) {
                        // If the remaining text fits in the parm list
                        if (remainingLen <= MAXIMUMMSGLEN1LINE) {
                            splitLen = remainingLen;
                            trailingSpaces = findTrailingSpaces(textPtr, splitLen);
                            leadingSpaces  = findLeadingSpaces(textPtr, splitLen);

                            if (TraceActive(trc_level_detailed)) {
                                TraceRecord(trc_level_detailed,
                                            TP(TP_MVS_UTILS_WTO_CMDRESPONSE_SPLIT_DETYPE),
                                            "write_to_operator_response, splitting for DE-type",
                                            TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                             "textPtr"),
                                            TRACE_DATA_HEX_INT(leadingSpaces,
                                                              "leadingSpaces"),
                                            TRACE_DATA_HEX_INT(trailingSpaces,
                                                               "trailingSpaces"),
                                            TRACE_DATA_HEX_INT(remainingLen,
                                                               "remainingLen"),
                                            TRACE_DATA_RAWDATA(remainingLen,
                                                               textPtr,
                                                               "remaining message"),
                                            TRACE_DATA_END_PARMS);
                            }

                            // Skip if all blanks
                            if (trailingSpaces != splitLen) {
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMLineTextLen =
                                    sizeof(ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2) + 4;
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMlineType =
                                    WTOMLINETYPE_DATAEND;

                                currentMsgPtr = &(parm_p->wto_textML[lineIndex]);
                                currentMsgPtr->wto_textLen = remainingLen-trailingSpaces-leadingSpaces;
                                memcpy(currentMsgPtr->wto_text,
                                       textPtr + leadingSpaces,
                                       currentMsgPtr->wto_textLen);
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2 = currentMsgPtr;

                                lineIndex++;
                            }

                            remainingLen = 0;
                        }
                        else {
                            // Remaining length does not fit.  Split text string
                            splitLen = findSplitLen(textPtr, MAXIMUMMSGLEN1LINE);
                            if (splitLen == 0) {
                                // Split msg to fit string into entire buffer
                                splitLen = MAXIMUMMSGLEN1LINE;
                            }

                            trailingSpaces = findTrailingSpaces(textPtr, splitLen);
                            leadingSpaces  = findLeadingSpaces(textPtr, splitLen);

                            if (TraceActive(trc_level_detailed)) {
                                TraceRecord(trc_level_detailed,
                                            TP(TP_MVS_UTILS_WTO_CMDRESPONSE_SPLIT_DTYPE),
                                            "write_to_operator_response, splitting for D-type",
                                            TRACE_DATA_PTR32((textPtr ? textPtr : NULL),
                                                             "textPtr"),
                                            TRACE_DATA_HEX_INT(leadingSpaces,
                                                             "leadingSpaces"),
                                            TRACE_DATA_HEX_INT(trailingSpaces,
                                                             "trailingSpaces"),
                                            TRACE_DATA_HEX_INT(remainingLen,
                                                             "remainingLen"),
                                            TRACE_DATA_HEX_INT(splitLen,
                                                             "splitLen"),
                                            TRACE_DATA_RAWDATA(remainingLen,
                                                               textPtr,
                                                               "remaining message"),
                                            TRACE_DATA_END_PARMS);
                            }

                            // Skip if all blanks
                            if (trailingSpaces != splitLen) {
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMLineTextLen =
                                    sizeof(ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2) + 4;
                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMlineType =
                                    WTOMLINETYPE_DATA;

                                currentMsgPtr = &(parm_p->wto_textML[lineIndex]);
                                currentMsgPtr->wto_textLen = splitLen-trailingSpaces-leadingSpaces;
                                memcpy(currentMsgPtr->wto_text,
                                       textPtr + leadingSpaces,
                                       currentMsgPtr->wto_textLen);

                                ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMText2 = currentMsgPtr;

                                lineIndex++;
                            }
                            remainingLen = remainingLen - splitLen;
                            textPtr = textPtr + splitLen;
                        }
                    }   // end for, split text into a multiline

                    // If more than one line in message issue message.
                    // If only one line in message because rest was just blanks
                    // and first message issue message.
                    ml_Wto_Ptr->wtoM_wpllines = lineIndex;

                    if (lineIndex == 1) {
                        ml_Wto_Ptr->wtoM_wplltf = WTOMLINETYPE_DATAEND;
                    }
                    if (lineIndex == MAXIMUMMULTILINES) {
                        // Mark the previous line as last if we hit
                        ml_Wto_Ptr->wtoMLine[lineIndex-1].wtoMlineType = WTOMLINETYPE_DATAEND;
                    }

                    if (TraceActive(trc_level_detailed)) {
                        TraceRecord(trc_level_detailed,
                                    TP(TP_MVS_UTILS_WTO_CMDRESPONSE_PRIOR_WTO_ML),
                                    "write_to_operator_response, prior to WTO ML",
                                    TRACE_DATA_INT(lineIndex,"lineIndex"),
                                    TRACE_DATA_RAWDATA(sizeof(parm_p->wto_textML), parm_p->wto_textML, "message area"),
                                    TRACE_DATA_RAWDATA(sizeof(list_wtp_resp_ml), parm_p->execute_wto, "WPL"),
                                    TRACE_DATA_END_PARMS);
                    }


                    __asm(" SAM31\n"
                        " SLR 0,0\n"
                        " SYSSTATE AMODE64=NO\n"
                        " WTO MCSFLAG=(RESP),CONSID=(%3),CART=(%4),MF=(E,(%2))\n"
                        " SYSSTATE AMODE64=YES\n"
                        " ST 15,%0\n"
                        " ST 1,%1\n"
                        " SAM64" :
                          "=m"(parm_p->rc), "=m"(parm_p->outMsgId) :
                          "r"(parm_p->execute_wto),
                          "r"(parm_p->console_id), "r"(parm_p->cart) :
                          "r0","r1","r14","r15");

                    *rc = parm_p->rc;

                    // Save WTO Message ID from first multiline WTO to use in continuation message
                    if (continue_count == 0) {
                        snprintf(firstMultlineMsgID, sizeof(firstMultlineMsgID), "%u", parm_p->outMsgId);
                    }

                    if (TraceActive(trc_level_detailed)) {
                        TraceRecord(trc_level_detailed,
                                    TP(TP_MVS_UTILS_WTO_CMDRESPONSE_RETURN_WTO_ML),
                                    "write_to_operator_response, return from WTO ML",
                                    TRACE_DATA_INT(parm_p->rc,"parm_p->rc"),
                                    TRACE_DATA_INT(parm_p->outMsgId,"parm_p->outMsgId"),
                                    TRACE_DATA_INT(continue_count, "continue_count"),
                                    TRACE_DATA_END_PARMS);
                    }
                }   // end for, each continuation of 10-line multlline WTOs

                if (continue_count >= CONTINUEMAX) {
                    *rc = -12;
                }
            }   // end if, need to issue message as a set of multiline WTOs

            free(parm_p);
            parm_p = 0;
        }
        else {
            // Could not obtain enough storage below the Bar for parms.
            *rc = -4;
        }
    }
    else {
        // No input message
        *rc = -8;
    }

    return *rc;
}

/**
 * Print a formatted string to wto.
 *
 * Note: max length of formatted string is 1024.
 *
 * @parms similar to printf
 *
 * @return the rc from write_to_programmer
 */
int printfWto(const char* str, ...) {

    va_list argp;
    char msg[1024];
    va_start(argp, str);
    vsnprintf(msg, 1024, str, argp);
    va_end(argp);

    return write_to_programmer(msg);
}

/**
 * Issue message to programmer
 *
 * @param message_p message to be issued (null terminated string).
 *
 * @retval 0 no errors
 * @retval -4 unable to obtain storage to process the request
 * @retval -8 input message was null
 * @retval -12 command response exceeded 1000 multi line WTOs
 *
 * @return > 0 return code from WTO
 *
 */
int
write_to_programmer(char* message_p) {
    return write_to_operator_location(message_p, NULL, WTO_PROGRAMMER_HARDCOPY);
}

/**
 * Issue message to the requested location
 *
 * @param message_p message to be issued (null terminated string).
 * @param location  location where the message is to be issued
 *
 * @retval 0 no errors
 * @retval -4 unable to obtain storage to process the request
 * @retval -8 input message was null
 * @retval -12 command response exceeded 1000 multi line WTOs
 *
 * @return > 0 return code from WTO
 *
 * @note made available as an unauthorized service (via UNAUTH_DEF).
 */
int
write_to_operator_unauthorized_routine(char* message_p, const WtoLocation* location) {
    return write_to_operator_location(message_p, NULL, *location);
}

