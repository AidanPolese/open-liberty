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

#ifndef SECURITY_SAF_AUTHORIZATION_H_
#define SECURITY_SAF_AUTHORIZATION_H_

#include "gen/ihaacee.h"
#include "security_saf_common.h"
#include "security_saf_acee.h"

#define SAF_SERVER_CLASS "SERVER"
#define SAF_CBIND_CLASS "CBIND"

//---------------------------------------------------------------------
// SAF related sizes
//---------------------------------------------------------------------

/**
 * The length of the area referenced by the @c RACROUTE @c APPL= keyword.
 */
#define SAF_APPLNAME_LENGTH 8

/**
 * The length of the area referenced by the @c RACROUTE @c REQUEST=STAT
 * @c CLASS= keyword.
 */
#define SAF_CLASSNAME_LENGTH 8

/**
 * The length of the area referenced by the @c RACROUTE @c REQSTOR= field.
 */
#define SAF_REQUESTOR_LENGTH 8

/**
 * The length of the area referenced by the @c RACROUTE @c SUBSYS= field.
 */
#define SAF_SUBSYS_LENGTH 8

/**
 * The required size of the @c RACROUTE work area.
 */
#define SAF_WORKAREA_SIZE 512

/**
 * The required size of the @c RACROUTE WKAREA.
 */
#define SAF_WKAREA_SIZE 64

/**
 * The length of a field specified in a @c RACROUTE @c FIELDS= keyword.
 */
#define SAF_FIELD_LENGTH 8

/**
 * Enumeration of access levels that can be requested through SAF.  Higher
 * access levels imply access lower levels.  
 *
 * NOTE: This enum is also defined in Java in SAFAccessLevel.java.
 */
typedef enum saf_access_level {
    READ    = 0x02,
    UPDATE  = 0x04,
    CONTROL = 0x08,
    ALTER   = 0x80
} saf_access_level;

/**
 * Indicate what type of access intent should be recorded in the SMF data set.
 *
 * NOTE: This enum is also defined in Java in SAFLogOption.java.  The two enums
 *       must be consistent with each other.
 */
typedef enum saf_log_option {
    ASIS    = 1,    //!< Record the event as specified by the profile that protects
                    //!< the resource or via options such as @c SETROPTS.
    NOFAIL  = 2,    //!< If the authorization fails, the attempt is not recorded.
                    //!< If authorization is successful, the event is recorded as in
                    //!< @c ASIS.
    NONE    = 3,    //!< The authorization event is not recorded and messages are
                    //!< suppressed regardless of whether or not @c MSGSUPP=NO is
                    //!< specified.
    NOSTAT  = 4     //!< The attempt is not recorded and no statistics are updated.
} saf_log_option;

/**
 * Indicate the ENVIR option for RACROUTE REQUEST=LIST.
 */
typedef enum saf_envir_option {
    CREATE  = 1,    //!< Build the in-storage profiles
    DELETE  = 2     //!< Delete the in-storage profiles
} saf_envir_option;

/**
 * Request authorization to the specified resource at the specified access
 * level via the @c RACROUTE @c REQUEST=AUTH macro.
 *
 * @param serviceResults the return and reason codes from SAF and the security
 *        product
 * @param suppressMessages indication of whether or not to suppress security
 *        product messages related to the authorization check
 * @param logOption the requested SMF logging policy for this authorization
 * @param requestor a string to identify the code requesting authorization
 * @param acee a pointer to a specific credential to use for authorization.
 *        If null, the acee associated with the task (if present) or address
 *        space is used.
 * @param accessLevel the access level requested for the specified resource
 * @param applName the name of the application requesting authorization
 *        checking
 * @param className the name of the resource class
 * @param entityName the name of resource in the specified class to authorize
 *        access to
 *
 * @return 0 if the call was successfully processed by the security product
 *         and a negative value if an error ocurred
 */
int checkAuthorization(saf_results*     serviceResults,
                       unsigned char    suppressMessages,
                       saf_log_option   logOption,
                       const char*      requestor,
                       acee*            acee,
                       saf_access_level accessLevel,
                       const char*      applName,
                       const char*      className,
                       const char*      entityName);

/**
 * Check the class descriptor table to see if the specified class is
 * marked as active in the security product.
 *
 * @param className the resource class name to check
 * @param requestor a string to identify the service requestor
 *
 * @returns 1 if the specified class is active
 * @returns 0 if the specified class is inactive
 * @returns -1 if an error was encountered
 */
int isClassActive(const char* className, const char* requestor);

/**
 * Fairly generic mechanism to call RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT
 * with a caller-specified CLASS, ENTITY, and FIELD for extraction.
 *
 * Note that while it's possible to extract several fields via EXTRACT, 
 * this routine is currently written to extract only the single field  
 * specified in the fieldName parm.
 *
 * @param serviceResults the return and reason codes from SAF and the security
 *        product
 * @param requestor a string to identify the code requesting authorization
 * @param className the name of the resource class
 * @param entityName the name of resource in the specified class to authorize
 *        access to
 * @param fieldName the name of field in the specified resource to extract 
 * @param extractResults the extracted data
 *
 * @return 0 if the EXTRACT call was made; an error code otherwise.  If 0,
 *         check serviceResults for the result of the EXTRACT call.  If the 
 *         EXTRACT was successful, the extracted data is passed back to the 
 *         caller via the extractResults parameter.
 */
int safExtract(saf_results* serviceResults,
               const char* requestor,
               const char* className,
               const char* entityName,
               const char* fieldName,
               racf_extract_results* extractResults);

/**
 * Request authorization to the specified resource at the specified access
 * level via the @c RACROUTE @c REQUEST=FASTAUTH macro.
 *
 * @param serviceResults the return and reason codes from SAF and the security
 *        product
 * @param suppressMessages indication of whether or not to suppress security
 *        product messages related to the authorization check
 * @param logOption the requested SMF logging policy for this authorization
 * @param requestor a string to identify the code requesting authorization
 * @param raco_cb a pointer to a specific credential to use for authorization.
 * @param acee_p a pointer to an acee
 * @param accessLevel the access level requested for the specified resource
 * @param applName the name of the application requesting authorization
 *        checking
 * @param className the name of the resource class
 * @param entityName the name of resource in the specified class to authorize
 *        access to
 *
 * @return 0 if the call was successfully processed by the security product
 *         and a negative value if an error ocurred
 */
int checkAuthorizationFast(saf_results*     serviceResults,
                           unsigned char    suppressMessages,
                           saf_log_option   logOption,
                           const char*      requestor,
                           RACO_CB*         raco_cb,
                           acee*            acee_p,
                           saf_access_level accessLevel,
                           const char*      applName,
                           const char*      className,
                           const char*      entityName);

/**
 * Fairly generic mechanism to call RACROUTE REQUEST=LIST 
 * with a caller-specified CLASS and ENVIR option.
 *
 * @param serviceResults the return and reason codes from SAF and the security
 *        product
 * @param requestor a string to identify the code requesting authorization
 * @param envirOption the requested ENVIR operation, either CREATE or DELETE
 * @param className the name of the resource class
 *
 * @return 0 if the LIST call was made; an error code otherwise.  If 0,
 *         check serviceResults for the result of the LIST call.  
 */
int raclist(saf_results*        serviceResults,
            const char*         requestor,
            saf_envir_option    envirOption,
            const char*         className);

//---------------------------------------------------------------------
// Invoke RACROUTE REQUEST=TOKENXTR to get the utoken from the input ACEE.
// TODO: Documentation.
//---------------------------------------------------------------------
int safUtokenExtract(
    saf_results*            serviceResults_p,
    acee*                   acee_p,
    ExtractedUtoken*        extractedUtoken_p);

#endif /* SECURITY_SAF_AUTHORIZATION_H_ */
