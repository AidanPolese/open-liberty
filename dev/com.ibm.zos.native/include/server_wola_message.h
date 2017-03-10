/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_SERVER_WOLA_MESSAGE_H
#define _BBOZ_SERVER_WOLA_MESSAGE_H

#include "gen/bboapc1p.h"
#include "server_local_comm_api.h"
#include "server_wola_connection_handle.h"
#include "server_wola_registration.h"

/** @file
 * Defines a WOLA message, passed from the authorized client side WOLA code to the
 * authorized server side WOLA code, via local comm.  This structure is based on
 * the BBOAMSG structure from tWAS, but is not identical.
 */

/** WolaMessage_t.messageType field values. */
#define WOLA_MESSAGE_TYPE_REQUEST 0
#define WOLA_MESSAGE_TYPE_RESPONSE 1

/**
 * A WOLA message, passed from the authorized client side WOLA code to the authorized
 * server side WOLA code, via local comm.
 *
 * The WOLA message is followed immediately by optional contexts and data.  If contexts
 * are present, their total length is described in the contextAreaLength field.
 * Immediately following the message will be a structure WolaMessageContextAreaHeader_t.
 * This describes the number of contexts.  That structure is followed immediately by one
 * or more WolaMessageContextHeader_t, which describe the individual contexts.  The
 * context type is determined by inspecting the contextId field.  Once the type is
 * determined, the correct context type can be mapped over the context header.
 *
 * The contexts are followed immediately by data, which is not mapped by any structure.
 * The data length is described in the dataAreaLength field.  If contexts were not
 * present, then the data immediately follows the WOLA message.
 *
 * Examples:
 *   <WolaMessage_t> <WolaMessageContextAreaHeader_t> <WolaServiceNameContext_t> *data*
 *   <WolaMessage_t> *data*
 */
typedef struct wolaMessage
{
    /** Eye catcher 'BBOAMSG' */
    unsigned char  eye[8];                                  /* 0x000 */

    /** Version of the message structure */
    unsigned short amsgver;                                 /* 0x008 */

    /** Message type (e.g. request, response) */
    unsigned short messageType;                             /* 0x00A */

    /** Length of the complete message, including contexts and data. */
    unsigned int   totalMessageSize;                        /* 0x00C */

    /** Flag bytes */
    unsigned int                  :31,                      /* 0x010 */
    /** Flag indicating response is an exception sent by BBOA1SRX.   */
                 responseException:1;                     /* 0x013.8 */

    /** Request ID.  For matching up responses with requests (which should
     *  both have the same requestId).  This field is used only for 
     *  server-to-client outbound requests. */
    unsigned int   requestId;                             /* 0x014 */

    /** Return code.  This is reported back to the client service. */
    unsigned int   returnCode;                              /* 0x018 */

    /** Reason code.  This is reported back to the client service. */
    unsigned int   reasonCode;                              /* 0x01C */

    /**
     * Offset to the message data.  When added to the starting address
     * of this message structure, this will point to the first byte
     * of application data.  If this field is zero, no application
     * data is present.  In practice, the message data will immediately
     * follow this message structure, unless there are also contexts
     * present, in which case the message data will immediately follow
     * the contexts.
     */
    unsigned int   dataAreaOffset;                          /* 0x020 */

    /**
     * Length of the message data.  If this field is zero, no application
     * data is present.
     */
    unsigned int   dataAreaLength;                          /* 0x024 */

    /**
     * Offset to the context area.  When added to the starting address
     * of this message structure, this will point to the begining of the
     * context area header.  If this field is zero, no context data is
     * present.  In practice, the context area will immediately follow
     * this message structure.
     */
    unsigned int   contextAreaOffset;                       /* 0x028 */

    /**
     * Length of the context area.  If this field is zero, no context
     * data is present.
     */
    unsigned int   contextAreaLength;                       /* 0x02C */

    /** The MVS user ID which created this request.  Blank padded. */
    unsigned char  mvsUserID[8];                            /* 0x030 */

    /** The JCA connection ID which created this request (if JCA). */
    struct wolaJcaConnectionID jcaConnectionID;             /* 0x038 */

    /** The second part of the WOLA name of the target registration. */
    unsigned char wolaNamePartTwo[8];                       /* 0x044 */

    /** The third part of the WOLA name of the target registration. */
    unsigned char wolaNamePartThree[8];                     /* 0x04C */

    /** The work type.  1 for local, 2 for remote. */
    unsigned int workType;                                  /* 0x054 */

    /** Available for future. 
     * TODO: do we need to reserve this space?  WolaMessage_t is just a 
     * mapping, not a permanent control block.  If we never use it then
     * it's just an extra 40 bytes that we're copying around. */
    unsigned char _rsvd2[40];                               /* 0x058 */

    /* Request and context data follow immediately.            0x080 */
} WolaMessage_t;


/**
 * The WOLA message context area header.  This header prefixes all WOLA contexts
 * defined by WolaMessageContextHeader_t.  This header is followed immediately in
 * storage by the first WolaMessageContextHeader_t (or by the message if there are
 * no contexts).
 */
typedef struct wolaMessageContextAreaHeader {
    /** Eye catcher 'BBOACTX' */
    unsigned char  eye[8];                                    /* 0x000 */

    /** Version of the context area header structure. */
    unsigned short version;                                   /* 0x008 */

    /** Flags */
    unsigned short flags;                                     /* 0x00A */

    /** The number of contexts following this header. */
    int            numContexts;                               /* 0x00C */

    /* First context header follows immediately */
} WolaMessageContextAreaHeader_t;

/**
 * A WOLA message context header.  A WOLA message can contain zero or more contexts,
 * each of which are prefixed with the following header.  This header is followed
 * immediately in storage by the context data.
 */
typedef struct wolaMessageContextHeader {
    /** Eye catcher (depends on what contextId is). */
    unsigned char  eye[8];                                    /* 0x000 */

    /** The ID of the context. */
    unsigned int   contextId;                                 /* 0x008 */

    /** The length of the context data following this header. */
    unsigned int   contextLen;                                /* 0x00C */

    /* Context data follows immediately */
} WolaMessageContextHeader_t;

/** Context ID for transaction context. */
#define BBOATXC_Identifier 1

/** Context ID for security context. */
#define BBOASEC_Identifier 2

/** Context ID for WLM context. */
#define BBOAWLMC_Identifier 3

/** Context ID for correlation context. */
#define BBOACORC_Identifier 4

/** Context ID for service name context. */
#define BBOASNC_Identifier 5

/** Context ID for CICS Link Server Context. */
#define CicsLinkServerContextId 6


/**
 * Service context for the WOLA service name.  In practice, this context
 * will be present in every WOLA request message.  For inbound requests
 * to the Liberty server, this will contain the target EJB JNDI name.
 * For outbound requests to a subsystem, this will contain the service
 * name (from BBOA1SRV BBOA1RCA or BBOA1RCS) or the program name (from
 * the CICS link server or IMS program name).
 *
 * This context is a variable length context.  The length varies
 * depending on the service name length.
 */
typedef struct wolaServiceNameContext
{
    /** The context header. */
    WolaMessageContextHeader_t header;                      /* 0x000 */

    /** Version of the service name context. */
    unsigned short version;                                 /* 0x010 */

    /** The length of the service or program name. */
    unsigned short nameLength;                              /* 0x012 */

    /* Name follows - up to 256 bytes.                               */
} WolaServiceNameContext_t;

/**
 * Same as WolaServiceNameContext_t except it includes a char[256] area for the service name.
 */
typedef struct wolaServiceNameContextArea {
    /** The context header, version and nameLength */
    WolaServiceNameContext_t serviceNameContext;

    /* 256 is the max. We do not necessarily send this much data. */
    unsigned char name[256]; 
} WolaServiceNameContextArea_t;

/**
 * CICS link server context.
 * TODO add more description
 */
 #pragma pack(1)
typedef struct wolaCicsLinkServerContext
{
    /** The context header. */
    WolaMessageContextHeader_t header;                      /* 0x000 */

    /** Connection timeout (in seconds). */
    int connectionWaitTimeout;                              /* 0x010 */

    /** The link task transaction ID. */
    unsigned char linkTaskTranID[4];                        /* 0x014 */

    /** The link task request container ID. */
    unsigned char linkTaskReqContID[16];                    /* 0x018 */

    /** The link task request container type. */
    int linkTaskReqContType;                                /* 0x028 */

    /** The link task response container ID. */
    unsigned char linkTaskRspContID[16];                    /* 0x02C */

    /** The link task response container type. */
    int linkTaskRspContType;                                /* 0x03C */

    /** The link task channel ID. */
    unsigned char linkTaskChanID[16];                       /* 0x040 */

    /** The link task channel type. */
    int linkTaskChanType;                                   /* 0x050 */

    /** The "use containers" flag. */
    int useCICSContainer;                                   /* 0x054 */
} WolaCicsLinkServerContext_t;                              /* 0x058 */
#pragma pack(reset)

/**
 * Contains the message header (WolaMessage_t), the context area header (WolaMessageContextAreaHeader)
 * and the service name context (WolaServiceNameContextArea_t).
 */
typedef struct wolaMessageAndContextArea {
    WolaMessage_t messageHeader;
    WolaMessageContextAreaHeader_t contextHeader;
    WolaServiceNameContextArea_t serviceNameContextArea;
} WolaMessageAndContextArea_t;

/** Eye catcher for WolaServiceNameContext_t */
#define BBOASNC_EYE "BBOASNC "

/** Version for WolaServiceNameContext_t */
#define BBOASNC_VERSION_1 1

/** Maximum length of service or program name. */
#define BBOA_REQUEST_SERVICE_NAME_MAX 256

/** Eye catcher for WolaMessage_t */
#define BBOAMSG_EYE "BBOAMSG "

/** Version for WolaMessage_t */
#define BBOAMSG_VERSION_2 2

/** Eye catcher for WolaMessageContextAreaHeader_t */
#define BBOACTX_EYE "BBOACTX "

/** Version for WolaMessageContextAreaHeader_t */
#define BBOACTX_VERSION_1 1

/** Work types */
#define WOLA_REQUEST_TYPE_1_LOCAL_EJB_INV  1
#define WOLA_REQUEST_TYPE_2_REMOTE_EJB_INV 2


/**
 * Initialize the messageHeader_p as a WOLA message with the given workType 
 *
 * Called by wolaSendRequestCommon.
 *
 * @param messageHeader_p - a ptr to storage for the WOLA message header
 * @param workType - the wola message request type
 *
 * @return messageHeader_p
 */
WolaMessage_t * initializeMessageHeader(WolaMessage_t* messageHeader_p, unsigned int workType) ;

/**
 * Initialize the given contextHeader_p storage as a WolaMessageContextAreaHeader.
 * 
 * Called by wolaSendRequestCommon
 *
 * @param contextHeader_p - a ptr to storage for the WolaMessageContextAreaHeader.
 * @param numberOfContexts - the header's numContexts field value
 *
 * @return contextHeader_p
 */
WolaMessageContextAreaHeader_t * initializeContextHeader(WolaMessageContextAreaHeader_t* contextHeader_p, int numberOfContexts) ;

/**
 * Initialize the given serviceNameContext_p storage area as a service name context
 * with the given requestServiceName.
 *
 * Called by wolaSendRequestCommon
 *
 * @param serviceNameContext_p - output - a pointer to the serviceNameContext storage area
 * @param name_p - output - a ptr to the service name field within the service name context
 * @param requestServiceNameLength
 * @param requestServiceName_p - the service name to use (storage in callerStorageKey)
 * @param callerStorageKey - the calling address space's storage key, needed fro strncpy_sk.
 *
 * @return 0 if all good; 8 if requestServiceName_p is empty
 */
unsigned int buildServiceNameContext(WolaServiceNameContext_t* serviceNameContext_p,
                                     unsigned char* name_p,
                                     unsigned int requestServiceNameLength,
                                     char* requestServiceName_p,
                                     unsigned char callerStorageKey) ;

/**
 * Copy the server's WOLA name (part 2 and 3) into the given WolaMessage from the
 * given serverRegistration_p.
 *
 * @param messageHeader_p - the target WOLA message
 * @param serverRegistration_p - the server's WolaRegistration_t
 *
 * @return messageHeader_p
 */
WolaMessage_t * setWolaMessageServerNameFromRegistration( WolaMessage_t * messageHeader_p, 
                                                          WolaRegistration_t * serverRegistration_p );

/**
 * Build a WOLA message header and context area.
 *
 * Note: The wolaServiceNameContextArea_p is used to determine the contextAreaLength and 
 * and totalMessageSize;  it is NOT actually copied into the messageAndContextArea_p.
 *
 * @param messageAndContextArea_p - The message header and context area to build/initialize
 * @param workType - The message work type
 * @param wolaServiceNameContextArea_p - The service name context (needed to determine the total contextArea length)
 *
 * @return messageAndContextArea_p
 */
WolaMessageAndContextArea_t * buildWolaMessageAndContextArea(WolaMessageAndContextArea_t * messageAndContextArea_p, 
                                                             unsigned int workType, 
                                                             WolaServiceNameContextArea_t* wolaServiceNameContextArea_p );

/**
 * Set the contextAreaLength/contextAreaOffset fields within the given messageHeader_p.
 *
 * The contextAreaOffset is set to the current totalMessageSize (i.e. it's like we're "appending" the data).
 * The totalMessageSize is updated to include the contextAreaLength
 *
 * NOTE: it's assumed the WolaMessage header has already been set up and the contextArea will immediately 
 * follow the header.
 *
 * @param messageHeader_p
 * @param contextAreaLength 
 *
 * @return messageHeader_p
 */
WolaMessage_t * setContextAreaLengthAndOffset(WolaMessage_t * messageHeader_p, unsigned int contextAreaLength) ;

/**
 * Set the dataAreaLength/dataAreaOffset fields within the given messageHeader_p.
 *
 * The dataAreaLength is equal to requestDataLength.
 * The dataAreaOffset is set to the current totalMessageSize (i.e. it's like we're "appending" the data).
 * The totalMessageSize is updated to include the requestDataLength.
 *
 * NOTE: it's assumed the WolaMessage header and context area have already been set up.
 *
 * @param messageHeader_p
 * @param requestDataLength - the dataAreaLength.
 *
 * @return messageHeader_p
 */
WolaMessage_t * setDataAreaLengthAndOffset(WolaMessage_t * messageHeader_p, unsigned int requestDataLength) ;

/**
 * Build a WolaMessage header to represent a response message.
 *
 * Note the responseData itself is not copied into the header; only the length and
 * offset to the responseData is set (this method assumes the responseData will
 * immediately follow the header).
 *
 * @param messageHeader_p - The WolaMessage header to populate
 * @param workType
 * @param requestId - The id of the request associated with this response
 * @param responseDataLength - The length of the response data payload
 * @param responseException - flag indicates whether the response is an exception
 *
 * @return messageHeader_p
 */
WolaMessage_t * buildWolaMessageResponseHeader(WolaMessage_t * messageHeader_p, 
                                               unsigned int workType,
                                               unsigned int requestId,
                                               unsigned int responseDataLength,
                                               unsigned int responseException);

/**
 * Set the MvsUserId field in the WolaMessage with the userId from the ACEE on the
 * TCB (TCBSENV).
 *
 * This method should be invoked on the client TCB (after PC-ing to our authorized code),
 * since we're pulling the ID from the TCB.
 *
 * Later on, the WOLA channel in the server will create a SAFCredential and a Subject 
 * for the ID and set it as the J2EE RunAs Subject prior to invoking the EJB.  The EJB
 * wrapper handles calling the security service with the Subject to perform J2EE authorization.
 *
 * Note: there are z/OS integrity/security concerns with this path, since the server will
 * create an asserted credential for whatever ID is in the WOLA message.  The powers that
 * be say we're OK with this, because we trust where we got the ID from (WOLA messages are
 * built only within our authorized code and delivered via localcomm, which is all trusted).
 *
 * @param messageHeader_p - The wola message
 * @param registration_p - A pointer to the client WOLA registration
 * @param cicsParms_p - A pointer to CICS-specific parms.
 *
 * @return messageHeader_p
 */
WolaMessage_t * setCallersMvsUserId(WolaMessage_t * messageHeader_p, WolaRegistration_t* registration_p, struct bboapc1p* cicsParms_p) ;

/**
 * Preview and read request or  response.
 *
 * @param wolaClientConnectionHandle_p Client connection handle.
 * @param lCommConnectionHandle_p      Local comm connection handle.
 * @param waitForData                  Wait for data flag.
 * @param dataLength_p                 Output area to get data length.
 * @param connHdlState                 Current connection handle state.
 * @param newConnHdlState              New connection handle state. Set after successful read.
 * @param reasonCode_p                 Output area to get the reason code.
 * @return 0 on success. non 0 on failure.
 */
unsigned int previewAndReadMessageAndContexts(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                              OpaqueClientConnectionHandle_t * lCommConnectionHandle_p,
                                              unsigned int waitForData,
                                              unsigned long long* dataLength_p,
                                              unsigned long long connHdlState,
                                              unsigned long long newConnHdlState,
                                              unsigned int* reasonCode_p );

/**
 * Parse the message for the service name context and return a null terminated service name.
 *
 * @param wolaClientConnectionHandle_p     Client connection handle.
 * @param requestServiceNameFromContext_p  Pointer to 257 byte output area to get the null terminated service name.
 * @return 0 on success. non 0 on failure.
 */
unsigned int getServiceNameFromContext(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, char* requestServiceNameFromContext_p);

/**
 * @return A ref to the WolaMessageContextHeader_t with the given contextId in the given context area;
 *         or NULL if no such context exists.
 */
WolaMessageContextHeader_t * getWolaMessageContext( WolaMessageContextAreaHeader_t * wolaMessageContextAreaHeader_p, int contextId);

#endif
