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

/**
 * @file
 *
 * Common structs and constants for SAF services.
 */

#ifndef SECURITY_SAF_COMMON_H_
#define SECURITY_SAF_COMMON_H_

#define SECURITY_AUTH_RC_INVALID_USERNAME_LENGTH     1 //!< The username parameter length is less than 1 or greater than 8.
#define SECURITY_AUTH_RC_INVALID_PASSWORD_LENGTH     2 //!< The password parameter length is less than 1 or greater than 8.
#define SECURITY_AUTH_RC_INVALID_AUDIT_STRING_LENGTH 3 //!< The audit string parameter length is greater than 255.
#define SECURITY_AUTH_RC_INVALID_APPLNAME_LENGTH     4 //!< The application name parameter length is greater than 8.
#define SECURITY_AUTH_RC_OUT_OF_MEMORY               5 //!< Failure to allocate storage.
#define SECURITY_AUTH_RC_IRRSIA00_FAILED             6 //!< IRRSIA00 returned a non-zero return code.  See SAF_return_code,
                                                       //!<   RACF_return_code, and RACF_reason_code for more details.
#define SECURITY_AUTH_RC_UNAUTHORIZED                7 //!< A failure occurred invoking an authorized function in BBGZSAFM.
                                                       //!<   This can occur for a number of reasons; the Angel may not be
                                                       //!<   running, or the server's user ID may not have access to the
                                                       //!<   required RACF resource for invoking this function.  However,
                                                       //!<   in these scenarios the runtime code should not be using the
                                                       //!<   authorized registry, so this return code likely indicates an
                                                       //!<   unexpected state.
#define SECURITY_AUTH_RC_SAFNSC_MGMT_ERROR           8 //!< A @c SAFNSC type @c RegistryElement became out of sync and an
                                                       //!<   unexpected return code was received from the registry.  An
                                                       //!<   example of this is if someone attempted to decrement the use
                                                       //!<   counter but the element was not in use or was already freed.
                                                       //!<   This means that there's a code bug somewhere.

/**
 * Return codes and reason codes from a call to BPX4TLS.
 */
typedef struct bpx4tls_results {
    int bpx4tlsReturnValue;  //!< BPX4TLS return value
    int bpx4tlsReturnCode;  //!< BPX4TLS return code
    int bpx4tlsReasonCode;  //!< BPX4TLS reason code
} bpx4tls_results;

/**
 * Return codes and reason codes from a service invocation through the SAF
 * router.
 */
typedef struct saf_results {
    int safReturnCode;   //!< SAF router return code
    int racfReturnCode;  //!< Security product return code
    int racfReasonCode;  //!< Security product reason code
} saf_results;

/**
 * SAF service function codes representing the service invocation through the SAF router.
 * !! NOTE: The enum is also defined in SAFServiceResult.java and the two must be kept in sync !!
 * Some of the values in the enum match the IRRSIA00FunctionCode in security_saf_acee.h
 */
#pragma enum(4) // specify that the size of each element in the enum is 4-bytes
typedef enum {
    IRRSIA00_CREATE           = 1,  //!< Create an ACEE/RACO.
    IRRSIA00_DELETE           = 2,  //!< Delete an ACEE.
    IRRSIA00_PURGE            = 3,  //!< Purge all managed ACEEs.
    IRRSIA00_REGISTER         = 4,  //!< Register a certificate.
    IRRSIA00_DEREGISTER       = 5,  //!< Deregister a certificate.
    IRRSIA00_QUERY            = 6,  //!< Query a certificate.
    RACROUTE_AUTH             = 7,  //!< RACROUTE REQUEST=AUTH.
    RACROUTE_EXTRACT          = 8,  //!< RACROUTE REQUEST=EXTRACT.
    RACROUTE_FASTAUTH         = 9,  //!< RACROUTE REQUEST=FASTAUTH.
    BPX4TLS_DELETE_THREAD_SEC = 10, //!< BPX4TLS function TLS_DELETE_THREAD_SEC#.
    BPX4TLS_TASK_ACEE         = 11, //!< BPX4TLS function TLS_TASK_ACEE#.
    RACROUTE_TOKENXTR         = 12  //!< RACROUTE REQUEST=TOKENXTR.
} SAFService;
#pragma enum(reset)

/**
 * Struct used to hold the SAF return code and RACF return and reason codes 
 * returned by a SAF service invocation.
 */
#pragma pack(packed)
typedef struct {
    int wasReturnCode;  //!< The WAS internal return code for the SAF invocation.
    union {             
        saf_results safResults; //!< Results can be accessed either thru this struct, or by the fields below.
        struct {
            int safReturnCode;  //!< The @c SAF_return_code from IRRSIA00.
            int racfReturnCode; //!< The @c RACF_return_code from IRRSIA00.
            int racfReasonCode; //!< The @c RACF_reason_code from IRRSIA00.
        };
        bpx4tls_results bpx4tlsResults; //!< BPX4TLS return and reason codes.
    };
    SAFService safServiceCode;
} SAFServiceResult;
#pragma pack(reset)

#define RACF_EXTRACT_RESULTS_MAX_LENGTH 256 //!< The max length of data that can be extracted into a racf_extract_results struct.

/**
 * Simple structure to map the data extracted via @c RACROUTE @c EXTRACT.
 */
typedef struct racf_extract_results {
    int length;
    char data[RACF_EXTRACT_RESULTS_MAX_LENGTH]; 
} racf_extract_results;

typedef struct {
    unsigned char tokenLength[1];
    unsigned char tokenVersion[1];
    unsigned char tokenData[78];
} ExtractedUtoken;

#endif /* SECURITY_SAF_COMMON_H_ */
