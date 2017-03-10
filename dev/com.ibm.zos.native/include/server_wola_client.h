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
#ifndef _BBOZ_SERVER_WOLA_CLIENT_H
#define _BBOZ_SERVER_WOLA_CLIENT_H

#include "server_wola_connection_handle.h"
#include "server_wola_message.h"
#include "server_wola_registration.h"

/* Function strings within the server common function module */
#define WOLA_REGISTER_FUNCTION_STRING   "WOLA1REG"
#define WOLA_UNREGISTER_FUNCTION_STRING "WOLA1URG"
#define WOLA_CONNGET_FUNCTION_STRING    "WOLA1CNG"
#define WOLA_CONNREL_FUNCTION_STRING    "WOLA1CNR"
#define WOLA_SENDREQ_FUNCTION_STRING    "WOLA1SRQ"
#define WOLA_SENDRESP_FUNCTION_STRING   "WOLA1SRP"
#define WOLA_SENDEXC_FUNCTION_STRING    "WOLA1SRX"
#define WOLA_RECEIVEANY_FUNCTION_STRING "WOLA1RCA"
#define WOLA_RECEIVESPC_FUNCTION_STRING "WOLA1RCS"
#define WOLA_RECEIVELEN_FUNCTION_STRING "WOLA1RCL"
#define WOLA_GETDATA_FUNCTION_STRING    "WOLA1GET"
#define WOLA_GETCTX_FUNCTION_STRING     "WOLA1GTX"
#define WOLA_INVOKE_FUNCTION_STRING     "WOLA1INV"
#define WOLA_HOST_FUNCTION_STRING       "WOLA1SRV"
#define WOLA_REBIND_FUNCTION_STRING     "WOLABIND"

/* Return codes from the BBOA1* services */
#define WOLA_RC_WARN4    4
#define WOLA_RC_ERROR8   8
#define WOLA_RC_SEVERE12 12
#define WOLA_RC_BAD_PARM 16

/* Reason codes from the BBOA1* services which are in common with tWAS. */
#define WOLA_RSN_NO_TRANS 4      /* Global tran not available */
#define WOLA_RSN_REGDONE  8      /* Already registered / unregistered */
#define WOLA_RSN_REGISTER 8      /* Unable to locate registration */
#define WOLA_RSN_MAXCONN  10     /* Conn pool reached max limit */
#define WOLA_RSN_BADGRP   10     /* Find WOLA group failed */
#define BBOAAPI_RSN_BADBGVT 10   /* find BGVT failed */
#define WOLA_RSN_REGCONNERR 12   /* Register min conn > max conn */
#define WOLA_RSN_RGEOOM 14       /* Out of shared memory */
#define WOLA_RSN_SAF_UNAUTH 14   /* Missing CBIND */
#define WOLA_RSN_SERVICE_NAME_LEN 16 /* Service name length invalid */
#define WOLA_RSN_BADSRVR 16      /* Problem with wola name part 2 or 3 */
#define WOLA_RSN_SYSTEM_LIMIT 18 /* WOLA message too big */
#define WOLA_RSN_LC_EXIST 19     /* local comm error */
#define WOLA_RSN_LC_PREVIEW 21   /* local comm client preview failed */
#define WOLA_RSN_REGBADPROP 21   /* CICS security prop requested but not supported by server */
#define WOLA_RSN_BADNAME_TOKN 23 /* Error in creating registration name token   */
#define WOLA_RSN_BADCONN 24      /* Connection get unknown problem */
#define WOLA_RSN_REG_INACT 28    /* Client registration is inactive */
#define WOLA_RSN_NAMTOK_RGE_ERR 28 /* Duplicate name token */
#define WOLA_RSN_INVAL_REQ_TYPE 32
#define WOLA_RSN_INVALID_CONN_STATE 36 /* Wrong WOLA connhdl state */
#define WOLA_RSN_INVALID_CONN_HDL 38   /* Client connhdl is bad */
#define WOLA_RSN_CONN_LCOM_ERROR 40    /* Local comm error */
#define WOLA_RSN_LC_SEND 46            /* Local comm error */
#define WOLA_RSN_FORCE_NOT_ALLOWED 64  /* Unregister force before unregister */
#define WOLA_RSN_FORCE_CONN_ACTIVE 66  /* Unregister with active connhdl */
#define WOLA_RSN_ATTACH_FAIL 68        /* Client IARV64 problem */
#define WOLA_RSN_MSGLEN_SMALLER 72 /* Passed message length is smaller than received msg    */
#define WOLA_RSN_REG_NAMENULL 74   /* Register name contains a 0x00 */
#define WOLA_RSN_DUP_UNREG 82      /* Unregister already called */
#define WOLA_RSN_IMS_STOPPING 84   /* Exit wait loop due to IMS stop */
#define WOLA_RSN_NO_AMCSS 90       /* TWAS AMCSS slot not loaded */
#define WOLA_RSN_BAD_BUFFER_REQ1 98  /* Buffer check error */
#define WOLA_RSN_BAD_BUFFER_REQ2 100 /* Buffer check error */
#define WOLA_RSN_BAD_BUFFER_RSP1 102 /* Buffer check error */
#define WOLA_RSN_BAD_BUFFER_RSP2 104 /* Buffer check error */

/* Reason codes from the BBOA1* services which are specific to liberty.  */
/* Try to keep this range >= 222.  Let tWAS common use < 222. */
#define WOLA_RSN_LC_PREVIEW3 222   /* local comm client preview length returned smaller than message header */
#define WOLA_RSN_MISSING_BBOARGE_NAMETOK 224 /* Got into authorized code but could not find name token */
#define WOLA_RSN_CONNHDL_NO_REGISTRATION 226 /* WOLA conn handle invalid, missing registration ptr */
#define WOLA_RSN_REGISTRATION_REUSED 228 /* RGE re-used during unregister */
#define WOLA_RSN_CONN_POOL_CLEANUP_FAIL 230 /* Unregister cleanup failed to clean up connection pool */
#define WOLA_RSN_BIND_INFO_OOM 232 /* Storage obtain failed during bind info creation. */
#define WOLA_RSN_BIND_FAILED 234   /* Initial angel client bind to server call failed. */
#define WOLA_RSN_NAME_PART_MISSING 236 /* Either part 2 or part 3 of WOLA name is missing. */
#define WOLA_RSN_REGISTER_NO_SERVER_RGE 238 /* Could not find the server's RGE. */

#define WOLA_RSN_NO_MEMORY 444     /* Out of memory */
#define WOLA_RSN_LC_RECEIVE 445    /* Local comm error */
#define WOLA_RSN_LIBERTY_INVOKE_ERROR 480 /* Problem in angel invoke */
#define WOLA_RSN_LIBERTY_SERVER_NOT_ENABLED_FOR_WOLA 481 /* Server is not exposing authorized client services for WOLA */
#define WOLA_RSN_REBIND_FAILED 482 /* Problem binding to replacement server */
#define WOLA_RSN_INTERNAL_AFTER_REBIND 483 /* Problem binding to replacement server */

/* Reason codes from the BBOA1* services which are handled internally. */
/* If you issue one of these, you must make sure that you handle it before it makes it */
/* back to the client, in all cases. */
#define WOLA_RSN_INTERNAL_BASE 512 /* Base of all internal-only reason codes. */
#define WOLA_RSN_INTERNAL_TRY_TWAS_STUB 512
#define WOLA_RSN_INTERNAL_REBIND_REQUIRED 513
#define WOLA_RSN_INTERNAL_END 640 /* End of all internal-only reason codes. */

// Return codes for the WOLA rebind service.
#define WOLA_RC_INTERNAL_REBIND_NOT_NECESSARY 0 /* Rebind was not required */
#define WOLA_RC_INTERNAL_REBIND_OK 4 /* Rebind was performed.  Caller should update bind info. */
#define WOLA_RC_INTERNAL_REBIND_ERROR_STOKEN 8 /* Rebind problem - stoken did not match current server stoken. */
#define WOLA_RC_INTERNAL_REBIND_ERROR_NAME 12 /* Rebind problem - register name or 3 part name did not match. */
#define WOLA_RC_INTERNAL_REBIND_ERROR_STATE 16 /* Could not modify client registration. */

/* WOLA registration flags */
#define WOLA_REGISTER_FLAGS_C2WPROP 0x00000004  // bit 29
#define WOLA_REGISTER_FLAGS_TRANS   0x00000002  // bit 30
#define WOLA_REGISTER_FLAGS_W2CPROP 0x00000001  // bit 31

/* WOLA unregistration flags */
#define WOLA_UNREGISTER_FLAGS_FORCE 0x00000001

static const char  zero_wola_register_name[8] = {{0}};

/** Parameters to rebind with server. */
typedef struct wolaRebindParms {
    /**
     * An entry variable or entry constant used along with nodeName and serverName to identify the server to be joined.
     * (input)
     */
    char daemonGroupName[8];                                /* 0x000 */

    /**
     * An entry variable or entry constant used along with daemonGroupName and serverName to identify the server to be joined.
     * his must be exactly 8 characters and blank padded.
     * (input)
     */
    char nodeName[8];                                       /* 0x008 */

    /**
     * An entry variable or entry constant used along with daemonGroupName and nodeName to identify the server to be joined.
     * This must be exactly 8 characters and blank padded.
     * (input)
     */
    char serverName[8];                                     /* 0x010 */

    /**
     * An entry variable or entry constant containing the name to be used to register a set of local connections.
     * Later calls require this name to identify the pool of connections to use.
     * This must be exactly 12 characters and blank padded.
     * (input)
     */
    char registerName[12];                                  /* 0x018 */

    SToken serverStoken; //!< The STOKEN that we think currently belongs to the server we want to connect to. (input)

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)

} WOLARebindParms_t;

/** Rebind to the server. */
void wolaClientRebind(WOLARebindParms_t* parms_p);

/** Parameters to register with server. */
typedef struct wolaRegisterParms {
    /**
     * An entry variable or entry constant used along with nodeName and serverName to identify the server to be joined.
     * (input)
     */
    char daemonGroupName[8];                                /* 0x000 */

    /**
     * An entry variable or entry constant used along with daemonGroupName and serverName to identify the server to be joined.
     * his must be exactly 8 characters and blank padded.
     * (input)
     */
    char nodeName[8];                                       /* 0x008 */

    /**
     * An entry variable or entry constant used along with daemonGroupName and nodeName to identify the server to be joined.
     * This must be exactly 8 characters and blank padded.
     * (input)
     */
    char serverName[8];                                     /* 0x010 */

    /**
     * An entry variable or entry constant containing the name to be used to register a set of local connections.
     * Later calls require this name to identify the pool of connections to use.
     * This must be exactly 12 characters and blank padded.
     * (input)
     */
    char registerName[12];                                  /* 0x018 */

    /**
     * An integer containing the initial minimum number of connections to allocate for this registration.
     * The adapter attempts to reserve this number of connections with the associated server during registration.
     * Important: At minimum, one connection is allocated to bind with the target server,
     * even if MINCONN is specified as zero (0). Specifying 0 is the same as 1.
     * (input)
     */
    unsigned int minConn;                                   /* 0x024 */

    /**
     * An integer containing the maximum number of connections to allocate for this registration.
     * The adapter attempts to extend the local connection pool up to this number during a Connection Get
     * request when the minimum number of connections are all in use.
     * (input)
     */
    unsigned int maxConn;                                   /* 0x028 */

    /**
     * A 32 bit flag word containing registration flags.
     * (input)
     */
    unsigned int registerFlags;                             /* 0x02C */

    /**
     * Un-available until the RGE version is incremented.  Old clients (8.5.5.2 thru 8.5.5.8) will fill
     * this in with the client bind token and PC indexes, and expect this to be cached in the RGE.
     */
    unsigned char _reserved1[0x50];                         /* 0x030 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x080 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x088 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x090 */
} WOLARegisterParms_t;                                      /* 0x098 */

/**
 * Register with server.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaRegister(WOLARegisterParms_t* parms_p);

/** Parameters to unregister with server. */
typedef struct wolaUnregisterParms {

    /**
     * An entry variable or entry constant containing the name to be used to unregister a set of local connections.
     * This must be exactly 12 characters, blank padded, and the same name used on BBOA1REG.
     * (input)
     */
    char registerName[12];                                  /* 0x000 */

    /**
     * A 32 bit flag word that contains unregistration flags.
     * Reserved - bit 0-30
     * Force (0|1) - bit 31
     * Contains a 1 if the unregister request should be forced. By default, an unregister request is complete
     * if all the connections have been returned to the connection pool. If all the connections are not returned to the pool,
     * a warning is returned to the caller. The unregister process is complete when the last connection has been returned
     * to the pool. A second unregister request can be made with the force bit set to 1, which forces the unregister process
     * to complete and all remaining connection handles for that registration are invalidated.
     * (input)
     *
     */
    unsigned int unregisterFlags;                           /* 0x00C */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x010 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x018 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x020 */
} WOLAUnregisterParms_t;                                    /* 0x028 */

/**
 * Unregister with server.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaUnregister(WOLAUnregisterParms_t* parms_p);

/** Parameters to get a connection. */
typedef struct wolaConnectionGetParms {
    /**
     * An entry variable or entry constant that contains the name to be used to locate the connection pool from which
     * to retrieve a connection. This must be a blank padded to 12 characters.
     * (input)
     */
    char registerName[12];  // TODO  12 came from info center. I thought I saw code that said it was 16?
                                                            /* 0x000 */

    /**
     *  An integer containing the number of seconds to wait for the connection to complete before returning
     *  a connection unavailable reason code. A value of 0 implies there is no wait time and the API waits indefinitely.
     *  (input)
     */
    unsigned int waitTime;                                  /* 0x00C */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x010 */

    /**
     * A 12 byte connection handle that must be passed on later requests for actions on this connection.
     * (output)
     */
    char* connectionHandle_p;                               /* 0x018 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x020 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x028 */

} WOLAConnectionGetParms_t;                                 /* 0x028 */

/**
 * Get a connection.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaConnectionGet(WOLAConnectionGetParms_t* parms_p);

/** Parameters to release a connection. */
typedef struct wolaConnectionReleaseParms {

    /**
     * A 12 byte connection handle indicating the previously obtained
     * connection that is to be released back into the connection pool.
     * (input)
     */
    char connectionHandle[12];                              /* 0x000 */

    /** Reserved */
    unsigned char _rsvd[4];                                 /* 0x00C */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x010 */


    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x018 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x020 */

} WOLAConnectionReleaseParms_t;                             /* 0x028 */

/**
 * Release a connection.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaConnectionRelease(WOLAConnectionReleaseParms_t* parms_p);

/** Parameters to send a request. */
typedef struct wolaSendRequestParms {

    char connectionHandle[12]; //!<  A connection handle that is to be used for this request. (input)
                                                            /* 0x000 */

    /**
     * An integer containing the request type that indicates the type of work request to process.
     * Supported type values: 1 = local EJB work requests, 2 = Remote EJB work requests.
     * (input)
     */
    unsigned int requestType;                               /* 0x00C */

    /**
     * An EBCDIC character string up to 256 bytes in length containing the name of the service to invoke.
     * For Type=1, EJB and Type 2, remote EJB, this is the JNDI Home name for the target.
     * (input)
     */
    char* requestServiceName_p;                             /* 0x010 */

    /**
     * An integer value that when set to 1 indicates that the caller wants control returned immediately,
     * even though the response length might not yet be known. For async(0), the current thread is requesting to wait
     * for the response to be returned from the server and the response length is returned in the
     * responseDataLen output argument.
     * (input)
     *
     */
    unsigned int async;                                     /* 0x018 */

    /**
     * An integer containing the length of the service name to start or 0 (zero) if the service name is null terminated.
     * (input)
     */
    unsigned int requestServiceNameLength;                  /* 0x01C */

    char * requestData_p; //!< address of the start of the request data to send. (input)
                                                            /* 0x020 */

    unsigned long long requestDataLength; //!< 64 bit unsigned value containing the length of the data to send. (input)
                                                            /* 0x028 */

    /**
     * 64 bit unsigned value that contains the length of the response.
     * This length can then be used by the caller to acquire storage before calling the Get Data API to copy it in.
     * When async is set to 1, indicating the caller wants control back immediately,
     * this is set to all 0xFFs if the response is not yet received.
     * (output)
     *
     */
    unsigned long long* responseDataLength_p;               /* 0x030 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x038 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x040 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x048 */

} WOLASendRequestParms_t;                                   /* 0x050 */

/**
 * Send a request.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaSendRequest(WOLASendRequestParms_t* parms_p);

/** Parameters to send a response. */
typedef struct wolaSendResponseParms {

    char connectionHandle[12]; //!<  A connection handle that is to be used for this response. (input)
                                                            /* 0x000 */

    /** Reserved */
    unsigned char _rsvd[4];                                 /* 0x00C */

    /**
     * Address of the start of the response data to send.
     * (input)
     */
    char* responseData_p;                                   /* 0x010 */

    /**
     *  A 64 bit unsigned value containing the length of the data to send.
     * (input)
     */
    unsigned long long responseDataLength;                  /* 0x018 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x020 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x028 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x030 */

} WOLASendResponseParms_t;                                  /* 0x038 */

/**
 * Send a response.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaSendResponse(WOLASendResponseParms_t* parms_p);

/**
 * Send response using input connection.
 *
 * @param wolaClientConnectionHandle_p client connection handle
 * @param responseDataLength           length of response data
 * @param responseData_p               pointer to response data
 * @param responseDataKey              key of response data
 * @param responseException            set to 1 when response is an exception.
 * @param reasonCode_p                 output pointer to reason code
 *
 * @return 0 on success. non 0 on failure.
 *
 */
unsigned int wolaSendResponseCommon(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p,
                                    unsigned long long responseDataLength,
                                    char* responseData_p,           // need to use move with source key
                                    unsigned char responseDataKey,
                                    unsigned int responseException,
                                    unsigned int* reasonCode_p);

WolaRegistration_t* getServerRegistration(WolaClientConnectionHandle_t* clientConnectionHandle_p);

/** Parameters to send a response exception. */
typedef struct wolaSendResponseExceptionParms {

    char connectionHandle[12]; //!<  A connection handle that is to be used for this response. (input)
                                                            /* 0x000 */

    /** Reserved */
    unsigned char _rsvd[4];                                 /* 0x00C */

    /**
     *  Specifies the address of the start of the exception response data to send.
     *  (input)
     */
    char* excResponseData_p;                                /* 0x010 */

    /**
     * Specifies a 64 bit unsigned value of the length of the exception response data to send. Exception response data is an EBCDIC string describing the error.
     * (input)
     */
    unsigned long long excResponseDataLength;               /* 0x018 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x020 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x028 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x030 */

} WOLASendResponseExceptionParms_t;                         /* 0x038 */

/**
 * Send a response exception.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaSendResponseException(WOLASendResponseExceptionParms_t* parms_p);

/** Parameters to receive request any. */
typedef struct wolaReceiveRequestAnyParms {

    /**
     * An entry variable or entry constant that contains the name to be used to locate
     * the connection pool from which to retrieve a connection. This must be a blank
     * padded to 12 characters.
     * (input)
     */
    char registerName[12];                                  /* 0x000 */
    
    /**
     * An integer that contains the number of seconds to wait for the connection to complete before
     * returning a connection unavailable reason code. A value of 0 indicates that there is no wait
     * time and that the API waits indefinitely.
     * (input)
     */
    unsigned int waitTime;                                  /* 0x00C */

    /**
     * A 12 byte connection handle that is returned and must be passed on later
     * requests for actions on this connection.
     * (output)
     */
    char* connectionHandle_p;                               /* 0x010 */

    /**
     * An EBCDIC character string up to 256 bytes containing the name of the service.
     * This is the name of the target service specified on the InteractionSpec by the server application.
     * A value of * indicates a receive request for all service names arriving under the current register name.
     * (input/output)
     */
    char* requestServiceName_p;                             /* 0x018 */

    /**
     * An integer containing the length of the service name to start or 0 if the service name is null terminated.
     * (input/output)
     */
    unsigned int* requestServiceNameLength_p;               /* 0x020 */

    /**
     * A 64 bit unsigned value is returned containing the length of the data to receive.
     * (output)
     */
    unsigned long long* requestDataLength_p;                /* 0x028 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x030 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x038 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x040 */

} WOLAReceiveRequestAnyParms_t;                             /* 0x040 */

/**
 * Receive request any.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaReceiveRequestAny(WOLAReceiveRequestAnyParms_t* parms_p);

/** Parameters to receive request specific. */
typedef struct wolaReceiveRequestSpecificParms {

    /**
     * A 12 byte connection handle that is to be used for the receive request.
     * (input)
     */
    char connectionHandle[12];                              /* 0x000 */

    /**
     * An integer value that when set to 1 indicates the caller wants control returned immediately,
     * even though the request length may not yet be known. When async is set to 0, this call waits
     * for a request from the server to be received.
     * (input)
     */
    unsigned int async;                                     /* 0x00C */

    /**
     * An EBCDIC character string up to 256 bytes containing the name of the service. This is the name of the
     * target service specified on the InteractionSpec by the server application. A value of * indicates that
     * set up as a server for all service names arriving under the current register name.
     * (input/output)
     */
    char* requestServiceName_p;                             /* 0x010 */

    /**
     * An integer containing the length of the service name to start or 0 if the service name is null terminated.
     * (input/output)
     */
    unsigned int* requestServiceNameLength_p;               /* 0x018 */

    /**
     * A 64 bit unsigned value is returned that contains the length of the request data received.
     * This length can then be used by the caller to acquire storage before calling the Get Data API to copy it in.
     * When async is set to 1, indicating the caller wants control back immediately,
     * this is set to all 0xFFs if the request data has not yet been received. In this case, the API
     * must be called again to retrieve an inbound request.
     * (output)
     */
    unsigned long long* requestDataLength_p;                /* 0x020 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x028 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x030 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x038 */

} WOLAReceiveRequestSpecificParms_t;

/**
 * Receive request specific.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaReceiveRequestSpecific(WOLAReceiveRequestSpecificParms_t* parms_p);

/** Parameters to receive response length. */
typedef struct wolaReceiveResponseLengthParms {

    /**
     * A 12 byte connection handle that is to be used for this request.
     * (input)
     */
    char connectionHandle[12];                              /* 0x000 */

    /**
     * An integer value that when set to 1 indicates the caller wants control returned immediately,
     * even though the response length might not be known. When async is set to 0, this call waits
     * for the response to be returned from the server and supply the response length in the
     * responseDataLength parameter value.
     * (input)
     */
    unsigned int async;                                     /* 0x00C */

    /**
     * A 64 bit unsigned value containing the length of the data received is returned.
     * This length can be used by the caller to acquire storage before calling the Get Data API to copy it in.
     * If async is 1, this might be returned as all FFs if the response data is not yet received.
     * (output)
     */
    unsigned long long* responseDataLength_p;               /* 0x010 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x018 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x020 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x028 */

    } WOLAReceiveResponseLengthParms_t;                     /* 0x030 */

/**
 * Receive response length.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaReceiveResponseLength(WOLAReceiveResponseLengthParms_t* parms_p);

/** Parameters to get context. */
typedef struct wolaGetContextParms {

    char connectionHandle[12]; //!< A 12 byte connection handle that is to be used for this request. (input)
                                                            /* 0x000 */

    /** Reserved */
    unsigned char _rsvd[4];                                 /* 0x00C */

    /**
     * A pointer to the address of the start of the context area to copy into.
     * The storage this points to must be in a key that is writable by the caller.
     * (input)
     */
    char* messageContext_p;                                 /* 0x010 */

    /**
     * A 64 bit unsigned value containing the length of the context area to be copied.
     * (input)
     */
    unsigned long long messageContextLength;                /* 0x018 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x020 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x028 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x030 */

    unsigned int* rv_p; //!< A pointer to an integer return value containing the size of the message data that was copied into the caller message data area. (output)
                                                            /* 0x038 */

} WOLAGetContextParms_t;                                    /* 0x038 */

/**
 * Get context.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaGetContext(WOLAGetContextParms_t* parms_p);

/** Parameters to get data. */
typedef struct wolaGetDataParms {

    char connectionHandle[12]; //!< A 12 byte connection handle that is to be used for this request. (input)
                                                            /* 0x000 */

    /** Reserved */
    unsigned char _rsvd[4];                                 /* 0x00C */

    /**
     * A pointer to the address of the start of the data area to copy into.
     * The storage this points to must be in a key that is writable by the caller.
     * (input)
     */
    char* messageData_p;                                    /* 0x010 */

    /**
     * A 64 bit unsigned value containing the length of the data to be copied.
     * (input)
     */
    unsigned long long messageDataLength;                   /* 0x018 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x020 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x028 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x030 */

    unsigned int* rv_p; //!< A pointer to an integer return value containing the size of the message data that was copied into the caller message data area. (output)
                                                            /* 0x038 */

} WOLAGetDataParms_t;

/**
 * Get data.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaGetData(WOLAGetDataParms_t* parms_p);

/** Parameters to invoke. */
typedef struct wolaInvokeParms {

    /**
     * An entry variable or entry constant containing the name to be used to locate
     * the connection pool to retrieve a connection for this invocation.
     * This must be a blank padded to 12 characters.
     * (input)
     */
    char registerName[12];                                  /* 0x000 */

    /**
     * An integer containing the request type that indicates the type of work request to process.
     * Supported type values: 1 = local EJB work requests, 2 = Remote EJB work requests.
     * (input)
     */
    unsigned int requestType;                               /* 0x00C */

    /**
     * An EBCDIC character string up to 256 bytes in length containing the name of the
     * service to invoke. For Type=1, EJB, this is the JNDI Home name for the target.
     * (input)
     */
    char* requestServiceName_p;                             /* 0x010 */

    /**
     * An integer containing the length of the service name to
     * start or 0 if the service name is null terminated.
     * (input)
     */
    unsigned int requestServiceNameLength;                  /* 0x018 */

    /**
     * An integer that contains the number of seconds to wait for the connection to complete before
     * returning a connection unavailable reason code. A value of 0 (zero) indicates that there
     * is no timeout and that this API waits indefinitely.
     * (input)
     */
    unsigned int waitTime;                                  /* 0x01C */

    /**
     * The address of the start of the request data to send.
     * (input)
     */
    char* requestData_p;                                    /* 0x020 */

    /**
     * A 64 bit unsigned value containing the length of the data to send.
     * (input)
     */
    unsigned long long requestDataLength;                   /* 0x028 */

    /**
     * The address of the start of the response data area to copy into.
     * The storage this points to must be in a key that is writable by the caller.
     * (input)
     */
    char* responseData_p;                                   /* 0x030 */

    /**
     * A 64 bit unsigned value containing the length of the data area to receive the response into.
     * (input)
     */
    unsigned long long responseDataLength;                  /* 0x038 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x040 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x048 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x050 */

    /**
     * A pointer to an integer containing the size of the message data that was
     * received and copied into the caller response area.
     * (output)
     */
    unsigned int* rv_p;                                     /* 0x058 */

} WOLAInvokeParms_t;                                        /* 0x060 */

/**
 * Invoke.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaInvoke(WOLAInvokeParms_t* parms_p);

/** Parameters to host service. */
typedef struct wolaHostServiceParms {

    /**
     * An entry variable or entry constant containing the name to be used to
     * locate the connection pool to retrieve a connection from for this call.
     * This must be a blank padded to 12 characters.
     * (input)
     */
    char registerName[12];                                  /* 0x000 */

    /**
     * An integer that contains the number of seconds to wait for the connection to complete
     * before returning a connection unavailable reason code. A value of 0 (zero) implies that
     * there is no timeout and that the API waits indefinitely.
     * (input)
     */
    unsigned int waitTime;                                  /* 0x00C */

    /**
     * An EBCDIC character string up to 256 bytes containing the name of the service.
     * This is the name of the target service specified on the InteractionSpec by the
     * server application. A value of * indicates set up as a server for all service
     * names arriving under the current register name.
     * (input/output)
     */
    char* requestServiceName_p;                             /* 0x010 */

    /**
     * An integer containing the length of the service name to start
     * or 0 if the service name is null terminated.
     * (input/output)
     */
    unsigned int* requestServiceNameLength_p;               /* 0x018 */

    /**
     * The address of the start of the request data received.
     * The storage this points to must be in a key that is writable by the caller.
     * (input)
     */
    char* requestData_p;                                    /* 0x020 */

    /**
     * A 64 bit unsigned value containing the length of the data area
     * to receive the message into.
     * (input)
     */
    unsigned long long requestDataLength;                   /* 0x028 */

    /**
     * A 12 byte connection handle that is returned to the caller and
     * used for sending a response for this request.
     * (output)
     */
    char* connectionHandle_p;                               /* 0x030 */

    /**
     * Pointer to CICS-specific parameters.  This pointer may be null.
     */
    struct bboapc1p* cicsParms_p;                           /* 0x038 */

    unsigned int* rc_p; //!< A pointer to an integer return code indicating success or failure of this call. (output)
                                                            /* 0x040 */

    unsigned int* rsn_p; //!< A pointer to an integer reason code describing the reason for a failure on this call. (output)
                                                            /* 0x048 */

    /**
     * A pointer to an integer containing the size of the message request data
     * that was received and copied into the caller area.
     * (output)
     */
    unsigned int* rv_p;                                     /* 0x050 */

} WOLAHostServiceParms_t;                                   /* 0x058 */

/**
 * Host a service.
 *
 * @param parms_p A pointer to the parameter structure.
 */
void wolaHostService(WOLAHostServiceParms_t* parms_p);

/**
 * Get a null terminated service name.
 *
 * @param requestServiceNameLength - The length of the request service name. When 0 area pointed to by requestServiceName_p must be null terminated.
 * @param requestServiceName_p - Pointer to the input request service name. maximum is 256 bytes.
 * @param callerStorageKey - The key of the input request server name.
 * @param outputRequestServiceName_p - Pointer to 257 byte output area get the null terminated request service name.
 *
 * @return zero when input request service name was successfully null terminated; non-zero otherwise.
 */
int getServiceName(unsigned int requestServiceNameLength, char* requestServiceName_p, unsigned char callerStorageKey, char* outputRequestServiceName_p);

/**
 * Use PLO to get the local comm connection handle from the wola connection handle.
 *
 * @param wolaClientConnectionhandle_p A pointer to the WOLA client connection handle.
 * @param localCommClientConnectionHandle_p A pointer to storage where the local comm connection handle should be copied.
 *
 * @return zero when the local comm connection handle was successfully copied, non-zero otherwise.
 */
int getLocalCommConnectionHandleFromWolaConnectionHandle(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, OpaqueClientConnectionHandle_t* localCommClientConnectionHandle_p);

/**
 * Use PLO to get connection state.
 *
 * @param clientConnectionHandle_p - The client connection handle.
 * @param connHdlState_p - Pointer to output area to get the connection state.
 *
 * @return zero when connection state was obtained; non-zero otherwise.
 */
int getHandleState(WolaClientConnectionHandle_t* clientConnectionHandle_p, unsigned long long* connHdlState_p);

/**
 * Use PLO to change the connection state.
 *
 * @param clientConnectionHandle_p - The client connection handle.
 * @param oldState - Old connection state.
 * @param newState - New connection state
 *
 * @return zero when connection state was changed; non-zero otherwise.
 */
int changeHandleState(WolaClientConnectionHandle_t* clientConnectionHandle_p, unsigned long long oldState, unsigned long long newState);

/**
 * Use PLO to set the WOLA message and context pointers in the wola client connection handle.
 *
 * @param clientConnectionHandle_p - The client connection handle.
 * @param message_p - Pointer to WOLA message.
 * @param contexts_p - Pointer to WOLA contexts.
 *
 * @return zero when message and context pointers are updated; non-zero otherwise.
 */
unsigned int setMessageAndContextAreas(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, void* message_p, void* contexts_p);

/**
 * Use PLO to validate the input connection handle and return the message and context area pointers.
 *
 * @param wolaClientConnectionHandle_p Client connection handle.
 * @param message_p   Output pointer to get the message area pointer.
 * @param contexts_p  Output pointer to get the context area pointer.
 *
 * @return 0 on success. non 0 on failure.
 */
unsigned int getMessageAndContextAreas(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p, void** message_p, void** contexts_p);

/**
 * Mapping of the MVS name token used to store the WOLA group shared memory area (BBOASHR) address.
 * Note - there used to be a 'type' and 'version' byte in the mapping, but these have been removed
 *        since the Liberty registration name prefix was changed to make it different from tWAS.
 *        The version byte may be added back in some day, since the initial reserved area is
 *        initialized to zeros and therefore any byte used would be version 0 by default.
 */
struct register_name_token_map {
    unsigned char reserved[8];                                    /* 0x000 */
    WolaRegistration_t* registration_p;                           /* 0x008 */
};                                                                /* 0x010 */

/**
 * Checks to see it the client connection handle references a valid connection handle.
 *
 * @param wolaClientConnectionHandle_p client connection handle to validate
 *
 * @return  Returns 1 if the client connection handle is valid, 0 if not.
 */
int connectionHandleValid(WolaClientConnectionHandle_t* wolaClientConnectionHandle_p);

typedef struct getWolaRegistrationData {
    WolaRegistrationFlags_t registrationflags;              /* 0x000 */
    unsigned long long stckLastStateChange;                 /* 0x008 */
} GetWolaRegistrationData_t;                                /* 0x010 */

/**
 * Get the registration for the specified register name.
 *
 * @param registerName_p      pointer to register name
 * @param registrationData_p  pointer to output area that gets updated with registration data when registration is found.
 *
 * @return Pointer to registration. 0 if not found.
 */
WolaRegistration_t* getWolaRegistration(char* registerName_p, GetWolaRegistrationData_t* registrationData_p);

/**
 * Get register token name for input register name.
 *
 * @param token_name_p   - Output - will contain the token name (must be at least 16 bytes long)
 * @param registerName_p - Input - Register name.
 *
 */
void getRegisterTokenName(char * token_name_p, char * registerName_p);

/**
 * Checks to see if the caller supplied buffer address is
 * good, and that it can be read from or written to.
 *
 * @param buffer_p  Address of buffer to check.
 * @param bufferLen Length of buffer.
 * @param readFlag  Read flag set to one if read only.
 * @param requestBuffer Request buffer set to one if request buffer, zero if response buffer.
 *
 * @return 0 if the buffer is good, 1 if the buffer start address
 *         was bad, 2 if the buffer end address was bad, 3 if the
 *         buffer is larger than the local comm limit.  This call
 *         may abend if buffer is bad, and in that case the ARR
 *         will set RC/RSN and exit.
 */
unsigned int checkBuffer(char* buffer_p, unsigned long long bufferLen,
                         unsigned char callerKey,
                         unsigned int readFlag, unsigned int requestBuffer);


#endif
