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
 * Assorted routines that interface with z/OS security products via the
 * SAF router (@c RACROUTE) to perform authorization tasks.
 *
 * RACROUTE API: http://publib.boulder.ibm.com/infocenter/zos/v1r12/topic/com.ibm.zos.r12.ichc600/racri.htm#racri
 */

#include <metal.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "include/common_defines.h"
#include "include/common_mc_defines.h"
#include "include/gen/ichsafp.h"
#include "include/gen/irrprxtw.h"
#include "include/mvs_storage.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/security_saf_authorization.h"
#include "include/security_saf_acee.h"


//---------------------------------------------------------------------
// RAS related constants
//---------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_SECURITY_SAF_AUTHORIZATION
#define TP_RACROUTE_AUTH_CALL                           1
#define TP_RACROUTE_AUTH_RETURN                         2
#define TP_RACROUTE_STAT_CLASS_CALL                     3
#define TP_RACROUTE_STAT_CLASS_RETURN                   4
#define TP_CHECK_AUTHORIZATION_CALLED                   5
#define TP_CHECK_AUTHORIZATION_RETURNS                  6
#define TP_IS_CLASS_ACTIVE_CALLED                       7
#define TP_IS_CLASS_ACTIVE_RETURNS                      8
#define TP_SAFEXTRACT_CALLED                            9
#define TP_SAFEXTRACT_RACROUTE_AREA                     10
#define TP_SAFEXTRACT_RACROUTE_RETURN                   11
#define TP_SAFEXTRACT_EXTWKEA                           12
#define TP_SAFEXTRACT_RESULTS                           13
#define TP_SAFEXTRACT_RETURNS                           14
#define TP_RACROUTE_FASTAUTH_CALL                       15
#define TP_RACROUTE_FASTAUTH_RETURN                     16
#define TP_CHECK_AUTHORIZATION_FASTAUTH_CALLED          17
#define TP_CHECK_AUTHORIZATION_FASTAUTH_RETURNS         18
#define TP_RACLIST_ENTRY                                19
#define TP_RACLIST_RACROUTE_AREA                        20
#define TP_RACLIST_RETURN                               21
#define TP_RACLIST_EXIT                                 22
#define TP_SAFUTOKENEXTRACT_CALLED                      23
#define TP_SAFUTOKENEXTRACT_RACROUTE_AREA               24
#define TP_SAFUTOKENEXTRACT_RACROUTE_RETURN             25
#define TP_SAFUTOKENEXTRACT_RETURNS                     26

//---------------------------------------------------------------------
// Error codes.
//---------------------------------------------------------------------
#define EXTRACT_UTOKEN_BAD_PARAMETER            (RAS_MODULE_SECURITY_SAF_AUTHORIZATION + 1)
#define EXTRACT_UTOKEN_NO_MEMORY                (RAS_MODULE_SECURITY_SAF_AUTHORIZATION + 2)
#define EXTRACT_UTOKEN_RACROUTE_TOKENXTR_FAILED (RAS_MODULE_SECURITY_SAF_AUTHORIZATION + 3)
//---------------------------------------------------------------------
// Module scoped helper functions and types
//---------------------------------------------------------------------


/**
 * Pad a string by appending the specified character to the end until the
 * string reaches the required length.
 *
 * @param string the string to pad
 * @param size the size of the area containing the string
 * @param pad the character to use for padding
 *
 * @return the number of characters that were inserted to pad the string
 */
static int
padRight(char* string, size_t size, unsigned char pad) {
    int stringLength = strlen(string);
    int padCount = 0;
    for (int i = stringLength; i < size - 1; i++, padCount++) {
        string[i] = pad;
    }
    if (size > 0) {
        string[size - 1] = '\0';
    }
    return padCount;
}

/**
 * Simple structure to hold a RACF style string.
 */
typedef struct racf_string {
    unsigned char length;  /*!< The length of the string */
    unsigned char str[0];  /*!< The C style null terminated string */
} racf_string;

/**
 * Create a string that's consumable by RACF from a null terminated C string.
 * This RACF format uses one byte at the front to declare the length of the
 * string followed by the actual string data.
 *
 * Storage is allocated by this function and it is the responsibility of
 * the caller to release it with @c free.
 *
 * @param string a C style @c NULL terminated string
 *
 * @return a newly allocated RACF format string in below the bar storage
 */
static racf_string*
createRacfString(const char* string) {
    size_t stringLength = strlen(string);
    size_t size = sizeof(racf_string) + stringLength + 1;

    racf_string* racfString = __malloc31(size);

    if (racfString) {
        racfString->length = stringLength;
        strncpy(racfString->str, string, stringLength + 1);
    }

    return racfString;
}

/**
 * Simple structure to map a RACF @c ENTITY.  According to the @c RACROUTE
 * documentation, the @c bufferLength field is only needed when a profile
 * is to be returned.
 */
typedef struct racf_entity {
    unsigned short bufferLength;   /*!< The length of the buffer (up to 255 bytes) */
    unsigned short entityLength;   /*!< The length of the entity name (up to 255 bytes) */
    unsigned char  entityName[0];  /*!< The entity name */
} racf_entity;

/**
 * Build a SAF @c ENTITYX style structure from a null terminated string that
 * contains a resource name.
 *
 * @param resourceName the SAF resource name to use for authorization
 *
 * @return a newly allocated entity in below the bar storage
 */
static racf_entity*
createRacfEntity(const char* resourceName) {
    size_t entityNameLength = strlen(resourceName);
    size_t size = sizeof(racf_entity) + entityNameLength + 1;

    racf_entity* entity = __malloc31(size);

    if (entity) {
        entity->bufferLength = 0;
        entity->entityLength = entityNameLength;
        strncpy(entity->entityName, resourceName, entityNameLength + 1);
    }

    return entity;
}

/**
 * Simple structure to map a RACF @c FIELDS parm for the @c RACROUTE macro.
 */
typedef struct racf_fields {
    unsigned int fieldCount; /* number of fields defined in the struct.  Each field is 8-bytes wide, blank-padded. */
    unsigned char fieldNames[0];
} racf_fields;

/**
 * Build a SAF @c FIELDS style structure from a null terminated string that
 * contains a field name.  
 *
 * @param fieldName the SAF field name 
 *
 * @return a newly allocated racf_fields struct in below the bar storage
 */
static racf_fields*
createRacfFields(const char* fieldName) {
    size_t size = sizeof(racf_fields) + SAF_FIELD_LENGTH + 1;

    racf_fields* rf = __malloc31(size);

    if (rf) {
        strncpy(rf->fieldNames, fieldName, SAF_FIELD_LENGTH);
        padRight(rf->fieldNames, SAF_FIELD_LENGTH + 1, ' ');
        rf->fieldCount = 1;
    }

    return rf;
}

/** 
 * Simple structure to map the header of EXTWKEA, to make it easier
 * to access the EXTWLN and EXTWOFF fields.
 */
#pragma pack(1)
typedef struct extwkea_header {
    unsigned int    extwsp : 8;
    unsigned int    extwln : 24;
    unsigned short  extwoff;
} extwkea_header;
#pragma pack(reset)

//---------------------------------------------------------------------
// List form of the REQUEST=AUTH
//---------------------------------------------------------------------
__asm(" RACROUTE REQUEST=AUTH,DECOUPL=YES,RELEASE=7760,MF=L" : "DS"(auth_list));

//---------------------------------------------------------------------
// Fairly generic mechanism to call RACROUTE REQUEST=AUTH with a caller
// specified credential, log options, access level, appl name, class,
// and entity
//---------------------------------------------------------------------
int
checkAuthorization(
    saf_results*     serviceResults,
    unsigned char    suppressMessages,
    saf_log_option   logOption,
    const char*      requestor,
    acee*            acee,
    saf_access_level accessLevel,
    const char*      applName,
    const char*      className,
    const char*      entityName
)
{
    // Map a section of below the bar storage to pass to the SAF router
    struct racroute_auth_storage {
        int           safReturnCode;
        unsigned char savearea[SAVE_AREA_SIZE];
        unsigned char workarea[SAF_WORKAREA_SIZE];
        unsigned char    applName[SAF_APPLNAME_LENGTH + 1];
        unsigned char    requestor[SAF_REQUESTOR_LENGTH + 1];
        unsigned char    subsystemId[SAF_SUBSYS_LENGTH + 1];
        struct acee*     acee;
        saf_access_level accessLevel;
        racf_string*     className;
        racf_entity*     entityName;
        char             authDynamic[sizeof(auth_list)];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CHECK_AUTHORIZATION_CALLED),
                    "checkAuthorization called",
                    TRACE_DATA_PTR(serviceResults, "serviceResults"),
                    TRACE_DATA_INT(suppressMessages, "suppresMessages"),
                    TRACE_DATA_INT(logOption, "logOption"),
                    TRACE_DATA_STRING(requestor, "requestor"),
                    TRACE_DATA_PTR(acee, "acee"),
                    TRACE_DATA_INT(accessLevel, "accessLevel"),
                    TRACE_DATA_STRING(applName, "applName"),
                    TRACE_DATA_STRING(className, "className"),
                    TRACE_DATA_STRING(entityName, "entityName"),
                    TRACE_DATA_END_PARMS);
    }

    // Verify required parameters are present
    if (className == NULL || entityName == NULL) {
        return -4;
    }

    // Allocate storage for the parameters below the bar
    struct racroute_auth_storage* racrouteArea_p = __malloc31(sizeof(struct racroute_auth_storage));
    if (racrouteArea_p == NULL) {
        return -8;
    }

    // Map safp on top of allocated parameter list
    safp* safParameterList = (safp*) racrouteArea_p->authDynamic;

    // Copy the list form of the parameter list into dynamic
    memcpy(&racrouteArea_p->authDynamic, &auth_list, sizeof(racrouteArea_p->authDynamic));

    // Specify our subsystem as BBGZ to differentiate from tWAS
    strncpy(racrouteArea_p->subsystemId, "BBGZ", sizeof(racrouteArea_p->subsystemId));
    padRight(racrouteArea_p->subsystemId, sizeof(racrouteArea_p->subsystemId), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,SUBSYS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->authDynamic), "m"(racrouteArea_p->subsystemId));

    // Enable message suppression if requested
    if (suppressMessages) {
        __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,MSGSUPP=YES,MF=(M,(%0))") : :
              "r"(&racrouteArea_p->authDynamic));
    }

    // Specify the log option assuming a default of ASIS
    switch (logOption) {
        case NOFAIL:
            __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,LOG=NOFAIL,MF=(M,(%0))") ::
                  "r"(&racrouteArea_p->authDynamic));
            break;
        case NONE:
            __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,LOG=NONE,MF=(M,(%0))") ::
                  "r"(&racrouteArea_p->authDynamic));
            break;
        case NOSTAT:
            __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,LOG=NOSTAT,MF=(M,(%0))") ::
                  "r"(&racrouteArea_p->authDynamic));
            break;
        default:
            __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,LOG=ASIS,MF=(M,(%0))") ::
                  "r"(&racrouteArea_p->authDynamic));
            break;
    }

    // Set the function requestor
    if (requestor) {
        strncpy(racrouteArea_p->requestor, requestor, sizeof(racrouteArea_p->requestor));
        padRight(racrouteArea_p->requestor, sizeof(racrouteArea_p->requestor), ' ');
        __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,REQSTOR=%1,MF=(M,(%0))") : :
              "r"(&racrouteArea_p->authDynamic), "m"(racrouteArea_p->requestor));
    }

    // Specify the ACEE to use for authorization (if specified)
    racrouteArea_p->acee = acee;
    if (acee) {
        __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,ACEE=%1,MF=(M,(%0))") : :
              "r"(&racrouteArea_p->authDynamic), "m"(*(racrouteArea_p->acee)));
    }

    // Specify the access level we're checking
    racrouteArea_p->accessLevel = accessLevel;
    __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,ATTR=(%1),MF=(M,(%0))") : :
          "r"(&racrouteArea_p->authDynamic), "r"(racrouteArea_p->accessLevel));


    // Specify the requesting application name
    if (applName) {
        strncpy(racrouteArea_p->applName, applName, sizeof(racrouteArea_p->applName));
        padRight(racrouteArea_p->applName, sizeof(racrouteArea_p->applName), ' ');
        __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,APPL=%1,MF=(M,(%0))") : :
              "r"(&racrouteArea_p->authDynamic), "m"(racrouteArea_p->applName));
    }

    // Specify the class
    racrouteArea_p->className = createRacfString(className);
    __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,CLASS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->authDynamic), "m"(*(racrouteArea_p->className)));

    // Specifiy the entity
    racrouteArea_p->entityName = createRacfEntity(entityName);
    __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,ENTITYX=(%1,NONE),MF=(M,(%0))") : :
          "r"(&racrouteArea_p->authDynamic), "m"(*(racrouteArea_p->entityName)));

    // Provide the SAF work area
    __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,WORKA=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->authDynamic), "m"(racrouteArea_p->workarea));

        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                    TP(TP_RACROUTE_AUTH_CALL),
                    "RACROUTE REQUEST=AUTH call",
                    TRACE_DATA_PTR(racrouteArea_p, "racrouteArea_p"),
                        TRACE_DATA_END_PARMS);
        }

    int returnCode = 0;

    // Execute the AUTH request if we successfully allocated storage
    if (racrouteArea_p->entityName && racrouteArea_p->className) {
        __asm(" LGR 2,13 Save above bar dynamic area\n"
              " LA 13,%2 Load below bar save area\n"
              AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,SYSTEM=NO,MF=(E,(%1))\n"
                       " ST 15,%0\n")
              " LGR 13,2 Restore above bar dynamic area" :
              "=m"(racrouteArea_p->safReturnCode) :
              "r"(&racrouteArea_p->authDynamic), "m"(racrouteArea_p->savearea) :
              "r2");

    } else {
        returnCode = -8;
    }

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_RACROUTE_AUTH_RETURN),
                    "RACROUTE REQUEST=AUTH return",
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_INT(racrouteArea_p->safReturnCode, "safReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprret, "racfReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprrea, "racfReasonCode"),
                    TRACE_DATA_END_PARMS);
    }

    if (serviceResults) {
        serviceResults->safReturnCode = racrouteArea_p->safReturnCode;
        serviceResults->racfReturnCode = safParameterList->safprret;
        serviceResults->racfReasonCode = safParameterList->safprrea;
    }

    // Cleanup the allocated storage
    free(racrouteArea_p->className);
    free(racrouteArea_p->entityName);
    free(racrouteArea_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CHECK_AUTHORIZATION_RETURNS),
                    "checkAuthorization returns",
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_END_PARMS);
    }

    return returnCode;
}

//---------------------------------------------------------------------
// List form of the REQUEST=STAT
//---------------------------------------------------------------------
__asm(" RACROUTE REQUEST=STAT,DECOUPL=YES,RELEASE=7760,MF=L" : "DS"(stat_list));

//---------------------------------------------------------------------
// Check if the specified resource class is active.
//---------------------------------------------------------------------
int
isClassActive(const char* className, const char* requestor) {
    // Map a section of below the bar storage to pass to the SAF router
    struct racroute_stat_storage {
        int              safReturnCode;
        unsigned char    savearea[SAVE_AREA_SIZE];
        unsigned char    workarea[SAF_WORKAREA_SIZE];
        unsigned char    requestor[SAF_REQUESTOR_LENGTH + 1];
        char             copyArea[8];
        unsigned char    className[SAF_CLASSNAME_LENGTH + 1];
        char             statDynamic[sizeof(stat_list)];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_IS_CLASS_ACTIVE_CALLED),
                    "isClassActive called",
                    TRACE_DATA_STRING(className, "className"),
                    TRACE_DATA_STRING(requestor, "requestor"),
                    TRACE_DATA_END_PARMS);
    }

    // Bail out early if the class wasn't specified or is bogus
    if (className == NULL || strlen(className) > SAF_CLASSNAME_LENGTH) {
        return -1;
    }

    // Allocate storage for the parameters below the bar
    struct racroute_stat_storage* racrouteArea_p = __malloc31(sizeof(struct racroute_stat_storage));
    if (racrouteArea_p == NULL) {
        return -8;
    }

    // Map safp on top of allocated parameter list
    safp* safParameterList = (safp*) racrouteArea_p->statDynamic;

    // Copy the list form of the parameter list into dynamic
    memcpy(&racrouteArea_p->statDynamic, &stat_list, sizeof(racrouteArea_p->statDynamic));

    // Massage the class name into the correct format and provide it
    strcpy(racrouteArea_p->className, className);
    padRight(racrouteArea_p->className, sizeof(racrouteArea_p->className), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=STAT,RELEASE=7760,CLASS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->statDynamic), "m"(racrouteArea_p->className));

    // Set the function requestor
    if (requestor) {
        strncpy(racrouteArea_p->requestor, requestor, sizeof(racrouteArea_p->requestor));
        padRight(racrouteArea_p->requestor, sizeof(racrouteArea_p->requestor), ' ');
        __asm(AMODE_31(" RACROUTE REQUEST=STAT,RELEASE=7760,REQSTOR=%1,MF=(M,(%0))") : :
              "r"(&racrouteArea_p->statDynamic), "m"(racrouteArea_p->requestor));
    }

    // Specify copy just in case
    __asm(AMODE_31(" RACROUTE REQUEST=STAT,RELEASE=7760,COPY=%1,COPYLEN=0,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->statDynamic), "m"(racrouteArea_p->copyArea));

    // Provide the SAF work area
    __asm(AMODE_31(" RACROUTE REQUEST=STAT,RELEASE=7760,WORKA=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->statDynamic), "m"(racrouteArea_p->workarea));

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_RACROUTE_STAT_CLASS_CALL),
                    "RACROUTE REQUEST=STAT call",
                    TRACE_DATA_PTR(racrouteArea_p, "racrouteArea_p"),
                    TRACE_DATA_END_PARMS);
    }

    __asm(" LGR 2,13 Save above bar dynamic area\n"
          " LA 13,%2 Load below bar save area\n"
          AMODE_31(" RACROUTE REQUEST=STAT,RELEASE=7760,MF=(E,(%1))\n"
                   " ST 15,%0\n")
          " LGR 13,2 Restore above bar dynamic area" :
          "=m"(racrouteArea_p->safReturnCode) :
          "r"(&racrouteArea_p->statDynamic), "m"(racrouteArea_p->savearea) :
          "r2");


    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_RACROUTE_STAT_CLASS_RETURN),
                    "RACROUTE REQUEST=STAT return",
                    TRACE_DATA_INT(racrouteArea_p->safReturnCode, "safReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprret, "racfReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprrea, "racfReasonCode"),
                    TRACE_DATA_END_PARMS);
    }

    int returnCode = -1; // Unknown state or error
    
    // Class is active
    if (racrouteArea_p->safReturnCode == 0 && safParameterList->safprret == 0) {
        returnCode = 1;
    }
    
    // Class is inactive
    if (racrouteArea_p->safReturnCode == 4 && safParameterList->safprret == 4) {
        returnCode = 0;
    }

    free(racrouteArea_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_IS_CLASS_ACTIVE_RETURNS),
                    "isClassActive returns",
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_END_PARMS);
    }

    return returnCode;
}

//---------------------------------------------------------------------
// List form of the REQUEST=EXTRACT
//---------------------------------------------------------------------
__asm(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,DECOUPL=YES,RELEASE=7760,MF=L" : "DS"(extract_list));

//---------------------------------------------------------------------
// Fairly generic mechanism to call RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT
// with a caller-specified CLASS, ENTITY, and FIELD for extraction.
//
// Note that while it's possible to extract several fields via EXTRACT, 
// this routine is currently written to extract only the single field  
// specified in the fieldName parm.
//---------------------------------------------------------------------
int
safExtract(
    saf_results*            serviceResults,
    const char*             requestor,
    const char*             className,
    const char*             entityName,
    const char*             fieldName,
    racf_extract_results*   extractResults)
{
    // Map a section of below the bar storage to pass to the SAF router
    struct racroute_extract_storage {
        int              safReturnCode;
        extwkea* __ptr32 extwkea_p;
        unsigned char    savearea[SAVE_AREA_SIZE];
        unsigned char    workarea[SAF_WORKAREA_SIZE];
        unsigned char    className[SAF_CLASSNAME_LENGTH + 1];
        unsigned char    requestor[SAF_REQUESTOR_LENGTH + 1];
        unsigned char    subsystemId[SAF_SUBSYS_LENGTH + 1];
        racf_entity*     entityName;
        racf_fields*     fieldsList;
        char             extractDynamic[sizeof(extract_list)];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SAFEXTRACT_CALLED),
                    __FUNCTION__ " called",
                    TRACE_DATA_PTR(serviceResults, "serviceResults"),
                    TRACE_DATA_STRING(className, "className"),
                    TRACE_DATA_STRING(entityName, "entityName"),
                    TRACE_DATA_STRING(fieldName, "fieldName"),
                    TRACE_DATA_STRING(requestor, "requestor"),
                    TRACE_DATA_END_PARMS);
    }

    // Verify required parameters are present
    if (className == NULL || 
        strlen(className) > SAF_CLASSNAME_LENGTH || 
        entityName == NULL || 
        fieldName == NULL) {
        return -4;
    }

    // Allocate storage for the parameters below the bar
    struct racroute_extract_storage* racrouteArea_p = __malloc31(sizeof(struct racroute_extract_storage));
    if (racrouteArea_p == NULL) {
        return -8;
    }

    // Map safp on top of allocated parameter list
    safp* safParameterList = (safp*) racrouteArea_p->extractDynamic;

    // Copy the list form of the parameter list into dynamic
    memcpy(&racrouteArea_p->extractDynamic, &extract_list, sizeof(racrouteArea_p->extractDynamic));

    // Specify our subsystem as BBGZ to differentiate from tWAS
    strncpy(racrouteArea_p->subsystemId, "BBGZ", sizeof(racrouteArea_p->subsystemId));
    padRight(racrouteArea_p->subsystemId, sizeof(racrouteArea_p->subsystemId), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,RELEASE=7760,SUBSYS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(racrouteArea_p->subsystemId));

    // Set the function requestor
    if (requestor) {
        strncpy(racrouteArea_p->requestor, requestor, sizeof(racrouteArea_p->requestor));
        padRight(racrouteArea_p->requestor, sizeof(racrouteArea_p->requestor), ' ');
        __asm(AMODE_31(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,RELEASE=7760,REQSTOR=%1,MF=(M,(%0))") : :
              "r"(&racrouteArea_p->extractDynamic), "m"(racrouteArea_p->requestor));
    }

    // Specify the class
    strcpy(racrouteArea_p->className, className);
    padRight(racrouteArea_p->className, sizeof(racrouteArea_p->className), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,RELEASE=7760,CLASS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(racrouteArea_p->className));

    // Specifiy the entity
    racrouteArea_p->entityName = createRacfEntity(entityName);
    __asm(AMODE_31(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,RELEASE=7760,ENTITYX=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(*(racrouteArea_p->entityName)));

    // Specify the fields
    racrouteArea_p->fieldsList = createRacfFields(fieldName);
    __asm(AMODE_31(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,RELEASE=7760,FIELDS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(*(racrouteArea_p->fieldsList)));

    // Provide the SAF work area
    __asm(AMODE_31(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,RELEASE=7760,WORKA=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(racrouteArea_p->workarea));

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_SAFEXTRACT_RACROUTE_AREA),
                    "prior to RACROUTE REQUEST=EXTRACT call",
                    TRACE_DATA_PTR(racrouteArea_p, "racrouteArea_p"), 
                    TRACE_DATA_END_PARMS);
    }

    int returnCode = 0;

    // Execute the EXTRACT request if we successfully allocated storage
    if (racrouteArea_p->entityName && racrouteArea_p->fieldsList) {
        __asm(" LGR 2,13 Save above bar dynamic area\n"
              " LA 13,%3 Load below bar save area\n"
              AMODE_31(" RACROUTE REQUEST=EXTRACT,TYPE=EXTRACT,RELEASE=7760,MF=(E,(%2))\n"
                       " ST 15,%0\n"
                       " ST 1,%1\n")
              " LGR 13,2 Restore above bar dynamic area" :
              "=m"(racrouteArea_p->safReturnCode), "=m"(racrouteArea_p->extwkea_p) :
              "r"(&racrouteArea_p->extractDynamic), "m"(racrouteArea_p->savearea) :
              "r2");
    } else {
        returnCode = -16;
    }

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_SAFEXTRACT_RACROUTE_RETURN),
                    "RACROUTE REQUEST=EXTRACT returned",
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_INT(racrouteArea_p->safReturnCode, "safReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprret, "racfReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprrea, "racfReasonCode"),
                    TRACE_DATA_END_PARMS);
    }

    if (serviceResults) {
        serviceResults->safReturnCode = racrouteArea_p->safReturnCode;
        serviceResults->racfReturnCode = safParameterList->safprret;
        serviceResults->racfReasonCode = safParameterList->safprrea;
    }

    if (returnCode == 0 && racrouteArea_p->safReturnCode == 0 && racrouteArea_p->extwkea_p != NULL) {
        // Successful result.  The extracted data is passed back to the caller via
        // a storage area pointed to by R1.  EXTWKEA maps this storage area.  The 
        // extracted data is located at EXTWKEA + EXTWOFF.  We are responsible for 
        // the EXTWKEA storage area, so we must RELEASE it.
        
        // Map the EXTWKEA header to make it easier to access the EXTWLN / EXTWOFF fields
        extwkea_header* eh = (extwkea_header *) racrouteArea_p->extwkea_p;

        // Extracted data @ EXTWKEA + EXTWOFF
        racf_extract_results* extRslt = (racf_extract_results *) ((char *)eh + eh->extwoff);

        if (TraceActive(trc_level_basic)) {
            extwkea temp;
            memcpy(&temp, racrouteArea_p->extwkea_p, sizeof(extwkea)); // LE APAR OA37620 
            TraceRecord(trc_level_basic,
                        TP(TP_SAFEXTRACT_EXTWKEA),
                        "RACROUTE REQUEST=EXTRACT extracted data EXTWKEA",
                        TRACE_DATA_PTR32(racrouteArea_p->extwkea_p, "extwkea ptr"),  
                        TRACE_DATA_RAWDATA(sizeof(extwkea), &temp, "extwkea"),  
                        TRACE_DATA_INT(eh->extwln, "extwln"),
                        TRACE_DATA_INT(eh->extwoff, "extwoff"),
                        TRACE_DATA_PTR32(extRslt, "extracted data ptr"),
                        TRACE_DATA_END_PARMS);
        }

        if (extractResults != NULL && extRslt->length > 0) {
            // The extractResults->length is initially set by the caller with 
            // the total len (i.e max capacity) of extractResults->data.
            // Make sure the caller-supplied extractResults struct is big enough
            // to fit the entire result data.  If it is, then set its length to
            // the exact length of the result data, and copy back the result.
            // If it is NOT big enough, then keep its length field as it is, and 
            // copy back only that much data from the result.  
            //
            // In other words, after this little if block, the extractResults->length
            // field will indicate how much data we can safely memcpy.
            if (extRslt->length <= extractResults->length) {
                extractResults->length = extRslt->length;
            }

            // Copy back the extract results
            memcpy(extractResults->data, extRslt->data, extractResults->length);

            if (TraceActive(trc_level_basic)) {
                TraceRecord(trc_level_basic,
                            TP(TP_SAFEXTRACT_RESULTS),
                            "copied extracted data to extractResults",
                            TRACE_DATA_RAWDATA((sizeof(extractResults->length)+extractResults->length), extractResults, "extractResults"),
                            TRACE_DATA_END_PARMS);
            }
        }
        
        if (TraceActive(trc_level_basic)) {
            TraceRecord(trc_level_basic,
                        TP(TP_SAFEXTRACT_RESULTS),
                        "storageRelease EXTWKEA",
                        TRACE_DATA_PTR(eh, "EXTWKEA"),
                        TRACE_DATA_INT(eh->extwln, "EXTWKEA.EXTWLN"),
                        TRACE_DATA_INT(eh->extwsp, "EXTWKEA.EXTWSP"),
                        TRACE_DATA_END_PARMS);
        }

        // Free EXTWKEA (remember, eh = racrouteArea_p->extwkea_p)
        storageRelease(eh, eh->extwln, eh->extwsp, 2); 
    }

    // Cleanup the allocated storage
    free(racrouteArea_p->entityName);
    free(racrouteArea_p->fieldsList);
    free(racrouteArea_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SAFEXTRACT_RETURNS),
                    __FUNCTION__ " returns",
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_END_PARMS);
    }

    return returnCode;
}

//---------------------------------------------------------------------
// List form of the REQUEST=FASTAUTH
//
// Note: REQUEST=FASTAUTH doesn't support the modify form.  In order
// to simulate the modify form, the execute form is used in conjunction
// with the following two assembler instructions:
//      " ORG *-2   FASTAUTH doesn't support modify form\n"
//      " BCR 0,0   Turn MF=(E into MF=(M"
//---------------------------------------------------------------------
__asm(" RACROUTE REQUEST=FASTAUTH,DECOUPL=YES,RELEASE=7760,MF=L" : "DS"(fastauth_list));

//---------------------------------------------------------------------
// Fairly generic mechanism to call RACROUTE REQUEST=FASTAUTH with a 
// caller specified RACO, log options, access level, appl name, class,
// and entity
//---------------------------------------------------------------------
int
checkAuthorizationFast(
    saf_results*     serviceResults,
    unsigned char    suppressMessages,
    saf_log_option   logOption,
    const char*      requestor,
    RACO_CB*         raco_cb,
    acee*            acee_p,
    saf_access_level accessLevel,
    const char*      applName,
    const char*      className,
    const char*      entityName)
{
    // Map a section of below the bar storage to pass to the SAF router
    struct racroute_fastauth_storage {
        int                 safReturnCode;
        unsigned char       savearea[SAVE_AREA_SIZE];
        unsigned char       workarea[SAF_WORKAREA_SIZE];
        unsigned char       wkarea[SAF_WKAREA_SIZE];
        unsigned char       applName[SAF_APPLNAME_LENGTH + 1];
        unsigned char       requestor[SAF_REQUESTOR_LENGTH + 1];
        unsigned char       subsystemId[SAF_SUBSYS_LENGTH + 1];
        RACO_CB             raco_cb;
        struct acee*        acee;
        saf_access_level    accessLevel;
        unsigned char       className[SAF_CLASSNAME_LENGTH + 1];
        racf_entity*        entityName;
        char                fastauthDynamic[sizeof(fastauth_list)];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CHECK_AUTHORIZATION_FASTAUTH_CALLED), 
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_PTR(serviceResults, "serviceResults"),
                    TRACE_DATA_INT(suppressMessages, "suppresMessages"),
                    TRACE_DATA_INT(logOption, "logOption"),
                    TRACE_DATA_STRING(requestor, "requestor"),
                    TRACE_DATA_PTR(raco_cb, "raco_cb"),
                    TRACE_DATA_PTR(acee_p, "acee"),
                    TRACE_DATA_INT(accessLevel, "accessLevel"),
                    TRACE_DATA_STRING(applName, "applName"),
                    TRACE_DATA_STRING(className, "className"),
                    TRACE_DATA_STRING(entityName, "entityName"),
                    TRACE_DATA_END_PARMS);
    }

    // Verify required parameters are present
    if (className == NULL 
        || strlen(className) > SAF_CLASSNAME_LENGTH 
        || entityName == NULL) {
        return -4;
    }

    // Allocate storage for the parameters below the bar
    struct racroute_fastauth_storage* racrouteArea_p = __malloc31(sizeof(struct racroute_fastauth_storage));
    if (racrouteArea_p == NULL) {
        return -8;
    }

    // Map safp on top of allocated parameter list
    safp* safParameterList = (safp*) racrouteArea_p->fastauthDynamic;

    // Copy the list form of the parameter list into dynamic
    memcpy(&racrouteArea_p->fastauthDynamic, &fastauth_list, sizeof(racrouteArea_p->fastauthDynamic));

    // Specify our subsystem as BBGZ to differentiate from tWAS
    strncpy(racrouteArea_p->subsystemId, "BBGZ", sizeof(racrouteArea_p->subsystemId));
    padRight(racrouteArea_p->subsystemId, sizeof(racrouteArea_p->subsystemId), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,SUBSYS=%1,MF=(E,(%0))\n"
                   " ORG *-2 FASTAUTH doesn't support modify form\n"
                   " BCR 0,0 Turn MF=(E into MF=(M") : :
          "r"(&racrouteArea_p->fastauthDynamic), "m"(racrouteArea_p->subsystemId));

    // Enable message suppression if requested
    if (suppressMessages) {
        __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,MSGSUPP=YES,MF=(E,(%0))\n"
                       " ORG *-2 FASTAUTH doesn't support modify form\n"
                       " BCR 0,0 Turn MF=(E into MF=(M") : :
              "r"(&racrouteArea_p->fastauthDynamic));
    }

    // Specify the log option assuming a default of ASIS
    switch (logOption) {
        case NOFAIL:
            __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,LOG=NOFAIL,MF=(E,(%0))\n" 
                           " ORG *-2 FASTAUTH doesn't support modify form\n"
                           " BCR 0,0 Turn MF=(E into MF=(M") : :
                  "r"(&racrouteArea_p->fastauthDynamic));
            break;
        case NONE:
            __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,LOG=NONE,MF=(E,(%0))\n" 
                           " ORG *-2 FASTAUTH doesn't support modify form\n"
                           " BCR 0,0 Turn MF=(E into MF=(M") : :
                  "r"(&racrouteArea_p->fastauthDynamic));
            break;
        default:
            __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,LOG=ASIS,MF=(E,(%0))\n" 
                           " ORG *-2 FASTAUTH doesn't support modify form\n"
                           " BCR 0,0 Turn MF=(E into MF=(M") : :
                  "r"(&racrouteArea_p->fastauthDynamic));
            break;
    }

    // Set the function requestor
    if (requestor) {
        strncpy(racrouteArea_p->requestor, requestor, sizeof(racrouteArea_p->requestor));
        padRight(racrouteArea_p->requestor, sizeof(racrouteArea_p->requestor), ' ');
        __asm(AMODE_31(" RACROUTE REQUEST=AUTH,RELEASE=7760,REQSTOR=%1,MF=(E,(%0))\n"
                       " ORG *-2 FASTAUTH doesn't support modify form\n"
                       " BCR 0,0 Turn MF=(E into MF=(M") : :
              "r"(&racrouteArea_p->fastauthDynamic), "m"(racrouteArea_p->requestor));
    }

    // Specify the RACO to use for authorization.
    if (raco_cb) {
        memcpy(&(racrouteArea_p->raco_cb), raco_cb, sizeof(racrouteArea_p->raco_cb));
        __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,ENVRIN=%1,MF=(E,(%0))\n" 
                       " ORG *-2 FASTAUTH doesn't support modify form\n"
                       " BCR 0,0 Turn MF=(E into MF=(M") : :
              "r"(&racrouteArea_p->fastauthDynamic), "m"(racrouteArea_p->raco_cb));
    }

    // Specify the ACEE to use for authorization (if specified)
    if (acee_p) {
        racrouteArea_p->acee = acee_p;
        __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,ACEE=%1,MF=(E,(%0))\n"
                       " ORG *-2 FASTAUTH doesn't support modify form\n"
                       " BCR 0,0 Turn MF=(E into MF=(M") : :
              "r"(&racrouteArea_p->fastauthDynamic), "m"(*(racrouteArea_p->acee)));
    }

    // Specify the access level we're checking
    racrouteArea_p->accessLevel = accessLevel;
    __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,ATTR=(%1),MF=(E,(%0))\n" 
                   " ORG *-2 FASTAUTH doesn't support modify form\n"
                   " BCR 0,0 Turn MF=(E into MF=(M") : :
          "r"(&racrouteArea_p->fastauthDynamic), "r"(racrouteArea_p->accessLevel));

    // Specify the requesting application name
    if (applName) {
        strncpy(racrouteArea_p->applName, applName, sizeof(racrouteArea_p->applName));
        padRight(racrouteArea_p->applName, sizeof(racrouteArea_p->applName), ' ');
        __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,APPL=%1,MF=(E,(%0))\n" 
                       " ORG *-2 FASTAUTH doesn't support modify form\n"
                       " BCR 0,0 Turn MF=(E into MF=(M") : :
              "r"(&racrouteArea_p->fastauthDynamic), "m"(racrouteArea_p->applName));
    }

    // Specify the class
    strcpy(racrouteArea_p->className, className);
    padRight(racrouteArea_p->className, sizeof(racrouteArea_p->className), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,CLASS=%1,MF=(E,(%0))\n" 
                   " ORG *-2 FASTAUTH doesn't support modify form\n"
                   " BCR 0,0 Turn MF=(E into MF=(M") : :
          "r"(&racrouteArea_p->fastauthDynamic), "m"(*(racrouteArea_p->className)));

    // Specifiy the entity
    racrouteArea_p->entityName = createRacfEntity(entityName);
    __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,ENTITYX=%1,MF=(E,(%0))\n" 
                   " ORG *-2 FASTAUTH doesn't support modify form\n"
                   " BCR 0,0 Turn MF=(E into MF=(M") : :
          "r"(&racrouteArea_p->fastauthDynamic), "m"(*(racrouteArea_p->entityName)));

    // Provide the SAF work area
    __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,WORKA=%1,MF=(E,(%0))\n" 
                   " ORG *-2 FASTAUTH doesn't support modify form\n"
                   " BCR 0,0 Turn MF=(E into MF=(M") : :
          "r"(&racrouteArea_p->fastauthDynamic), "m"(racrouteArea_p->workarea));

    // Provide the FASTAUTH wkarea
    __asm(AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,WKAREA=%1,MF=(E,(%0))\n"
                   " ORG *-2 FASTAUTH doesn't support modify form\n"
                   " BCR 0,0 Turn MF=(E into MF=(M") : :
          "r"(&racrouteArea_p->fastauthDynamic), "m"(racrouteArea_p->wkarea));

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_RACROUTE_FASTAUTH_CALL), 
                    "RACROUTE REQUEST=FASTAUTH call",
                    TRACE_DATA_PTR(racrouteArea_p, "racrouteArea_p"),
                    TRACE_DATA_END_PARMS);
    }

    int returnCode = 0;

    // Execute the FASTAUTH request if we successfully allocated storage
    if (racrouteArea_p->entityName && racrouteArea_p->className) {
        __asm(" LGR 2,13 Save above bar dynamic area\n"
              " LA 13,%2 Load below bar save area\n"
              AMODE_31(" RACROUTE REQUEST=FASTAUTH,RELEASE=7760,MF=(E,(%1))\n"
                       " ST 15,%0\n")
              " LGR 13,2 Restore above bar dynamic area" :
              "=m"(racrouteArea_p->safReturnCode) :
              "r"(&racrouteArea_p->fastauthDynamic), "m"(racrouteArea_p->savearea) :
              "r2");

    } else {
        returnCode = -8;
    }

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_RACROUTE_FASTAUTH_RETURN), 
                    "RACROUTE REQUEST=FASTAUTH return",
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_INT(racrouteArea_p->safReturnCode, "safReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprret, "racfReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprrea, "racfReasonCode"),
                    TRACE_DATA_END_PARMS);
    }

    if (serviceResults) {
        serviceResults->safReturnCode = racrouteArea_p->safReturnCode;
        serviceResults->racfReturnCode = safParameterList->safprret;
        serviceResults->racfReasonCode = safParameterList->safprrea;
    }

    // Cleanup the allocated storage
    free(racrouteArea_p->entityName);
    free(racrouteArea_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_CHECK_AUTHORIZATION_FASTAUTH_RETURNS), 
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_END_PARMS);
    }

    return returnCode;
}

//---------------------------------------------------------------------
// List form of the REQUEST=LIST
//---------------------------------------------------------------------
__asm(" RACROUTE REQUEST=LIST,DECOUPL=YES,RELEASE=7760,MF=L" : "DS"(list_list));

//---------------------------------------------------------------------
// Fairly generic mechanism to call RACROUTE REQUEST=LIST.
// with a caller-specified CLASS and list option (CREATE or DELETE).
//---------------------------------------------------------------------
int
raclist(
    saf_results*            serviceResults,
    const char*             requestor,
    saf_envir_option        envirOption,
    const char*             className)
{
    // Map a section of below the bar storage to pass to the SAF router
    struct racroute_list_storage {
        int              safReturnCode;
        unsigned char    savearea[SAVE_AREA_SIZE];
        unsigned char    workarea[SAF_WORKAREA_SIZE];
        unsigned char    requestor[SAF_REQUESTOR_LENGTH + 1];
        unsigned char    className[SAF_CLASSNAME_LENGTH + 1];
        unsigned char    subsystemId[SAF_SUBSYS_LENGTH + 1];
        char             listDynamic[sizeof(list_list)];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_RACLIST_ENTRY), 
                    TRACE_DESC_FUNCTION_ENTRY,
                    TRACE_DATA_STRING(className, "className"),
                    TRACE_DATA_INT(envirOption, "envirOption"),
                    TRACE_DATA_END_PARMS);
    }

    // Verify required parameters are present
    if (className == NULL || strlen(className) > SAF_CLASSNAME_LENGTH ) {
        return -4;
    }

    // Allocate storage for the parameters below the bar
    struct racroute_list_storage* racrouteArea_p = __malloc31(sizeof(struct racroute_list_storage));
    if (racrouteArea_p == NULL) {
        return -8;
    }

    // Map safp on top of allocated parameter list
    safp* safParameterList = (safp*) racrouteArea_p->listDynamic;

    // Copy the list form of the parameter list into dynamic
    memcpy(&racrouteArea_p->listDynamic, &list_list, sizeof(racrouteArea_p->listDynamic));

    // Specify our subsystem as BBGZ to differentiate from tWAS
    strncpy(racrouteArea_p->subsystemId, "BBGZ", sizeof(racrouteArea_p->subsystemId));
    padRight(racrouteArea_p->subsystemId, sizeof(racrouteArea_p->subsystemId), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=LIST,RELEASE=7760,SUBSYS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->listDynamic), "m"(racrouteArea_p->subsystemId));

    // Set the function requestor
    if (requestor) {
        strncpy(racrouteArea_p->requestor, requestor, sizeof(racrouteArea_p->requestor));
        padRight(racrouteArea_p->requestor, sizeof(racrouteArea_p->requestor), ' ');
        __asm(AMODE_31(" RACROUTE REQUEST=LIST,RELEASE=7760,REQSTOR=%1,MF=(M,(%0))") : :
              "r"(&racrouteArea_p->listDynamic), "m"(racrouteArea_p->requestor));
    }

    // Specify the envir option assuming a default of CREATE
    switch (envirOption) {
        case DELETE:
            __asm(AMODE_31(" RACROUTE REQUEST=LIST,RELEASE=7760,ENVIR=DELETE,MF=(M,(%0))") ::
                  "r"(&racrouteArea_p->listDynamic));
            break;
        default:
            __asm(AMODE_31(" RACROUTE REQUEST=LIST,RELEASE=7760,ENVIR=CREATE,MF=(M,(%0))") ::
                  "r"(&racrouteArea_p->listDynamic));
            break;
    }

    // Specify the class
    strcpy(racrouteArea_p->className, className);
    padRight(racrouteArea_p->className, sizeof(racrouteArea_p->className), ' ');
    __asm(AMODE_31(" RACROUTE REQUEST=LIST,RELEASE=7760,CLASS=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->listDynamic), "m"(racrouteArea_p->className));

    // Provide the SAF work area
    __asm(AMODE_31(" RACROUTE REQUEST=LIST,RELEASE=7760,WORKA=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->listDynamic), "m"(racrouteArea_p->workarea));

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_RACLIST_RACROUTE_AREA),
                    "prior to RACROUTE REQUEST=LIST call",
                    TRACE_DATA_PTR(racrouteArea_p, "racrouteArea_p"), 
                    TRACE_DATA_END_PARMS);
    }

    // Execute the LIST request 
    __asm(" LGR 2,13 Save above bar dynamic area\n"
          " LA 13,%2 Load below bar save area\n"
          AMODE_31(" RACROUTE REQUEST=LIST,RELEASE=7760,MF=(E,(%1))\n"
                   " ST 15,%0\n")
          " LGR 13,2 Restore above bar dynamic area" :
          "=m"(racrouteArea_p->safReturnCode) :
          "r"(&racrouteArea_p->listDynamic), "m"(racrouteArea_p->savearea) :
          "r2");

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_RACLIST_RETURN), 
                    "RACROUTE REQUEST=LIST return",
                    TRACE_DATA_INT(racrouteArea_p->safReturnCode, "safReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprret, "racfReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprrea, "racfReasonCode"),
                    TRACE_DATA_END_PARMS);
    }

    if (serviceResults) {
        serviceResults->safReturnCode = racrouteArea_p->safReturnCode;
        serviceResults->racfReturnCode = safParameterList->safprret;
        serviceResults->racfReasonCode = safParameterList->safprrea;
    }

    // Cleanup the allocated storage
    free(racrouteArea_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_RACLIST_EXIT),
                    TRACE_DESC_FUNCTION_EXIT,
                    TRACE_DATA_END_PARMS);
    }

    return 0;
}

//---------------------------------------------------------------------
// List form of the REQUEST=TOKENXTR
//---------------------------------------------------------------------
__asm(" RACROUTE REQUEST=TOKENXTR,DECOUPL=YES,RELEASE=7760,WORKA=0,ACEE=0,TOKNOUT=0,MF=L" : "DS"(extract_utoken_list));

//---------------------------------------------------------------------
// Invoke RACROUTE REQUEST=TOKENXTR to get the utoken from the input ACEE.
//---------------------------------------------------------------------
int
safUtokenExtract(
    saf_results*            serviceResults_p,
    acee*                   acee_p,
    ExtractedUtoken*        extractedUtoken_p) {
    int returnCode = 0;

    // Map a section of below the bar storage to pass to the SAF router
    struct racroute_extract_storage {
        int              safReturnCode;
        struct acee*     acee_p;
        ExtractedUtoken* uToken_p;
        unsigned char    savearea[SAVE_AREA_SIZE];
        unsigned char    workarea[SAF_WORKAREA_SIZE];
        ExtractedUtoken  uToken;
        char             extractDynamic[sizeof(extract_utoken_list)];
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SAFUTOKENEXTRACT_CALLED),
                    __FUNCTION__ " called",
                    TRACE_DATA_PTR(serviceResults_p, "serviceResults_p"),
                    TRACE_DATA_PTR(acee_p, "acee_p"),
                    TRACE_DATA_PTR(extractedUtoken_p, "extractedUtoken_p"),
                    TRACE_DATA_END_PARMS);
    }

    // Verify required parameters are present
    if (acee_p == NULL ||
        extractedUtoken_p == NULL) {
        return EXTRACT_UTOKEN_BAD_PARAMETER;
    }

    // Allocate storage for the parameters below the bar
    struct racroute_extract_storage* racrouteArea_p = __malloc31(sizeof(struct racroute_extract_storage));
    if (racrouteArea_p == NULL) {
        return EXTRACT_UTOKEN_NO_MEMORY;
    }

    // Map safp on top of allocated parameter list
    safp* safParameterList = (safp*) racrouteArea_p->extractDynamic;

    // Copy the list form of the parameter list into dynamic
    memcpy(&racrouteArea_p->extractDynamic, &extract_utoken_list, sizeof(racrouteArea_p->extractDynamic));

    // Provide the SAF work area
    __asm(AMODE_31(" RACROUTE REQUEST=TOKENXTR,RELEASE=7760,WORKA=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(racrouteArea_p->workarea));

    // Specify the ACEE to use
    racrouteArea_p->acee_p = acee_p;
    __asm(AMODE_31(" RACROUTE REQUEST=TOKENXTR,RELEASE=7760,ACEE=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(*(racrouteArea_p->acee_p)));

    // set utoken length and version
    memset(&(racrouteArea_p->uToken.tokenLength), 0x50, sizeof(racrouteArea_p->uToken.tokenLength));
    memset(&(racrouteArea_p->uToken.tokenVersion), 0x01, sizeof(racrouteArea_p->uToken.tokenVersion));
    racrouteArea_p->uToken_p = &racrouteArea_p->uToken;
    __asm(AMODE_31(" RACROUTE REQUEST=TOKENXTR,RELEASE=7760,TOKNOUT=%1,MF=(M,(%0))") : :
          "r"(&racrouteArea_p->extractDynamic), "m"(*(racrouteArea_p->uToken_p)));


    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_SAFUTOKENEXTRACT_RACROUTE_AREA),
                    "prior to RACROUTE REQUEST=TOKENXTR call",
                    TRACE_DATA_PTR(racrouteArea_p, "racrouteArea_p"),
                    TRACE_DATA_END_PARMS);
    }

    __asm(" LGR 2,13 Save above bar dynamic area\n"
          " LA 13,%2 Load below bar save area\n"
          AMODE_31(" RACROUTE REQUEST=TOKENXTR,RELEASE=7760,MF=(E,(%1))\n"
                   " ST 15,%0\n")
          " LGR 13,2 Restore above bar dynamic area" :
          "=m"(racrouteArea_p->safReturnCode) :
          "r"(&racrouteArea_p->extractDynamic), "m"(racrouteArea_p->savearea) :
          "r2");

    if (TraceActive(trc_level_basic)) {
        TraceRecord(trc_level_basic,
                    TP(TP_SAFUTOKENEXTRACT_RACROUTE_RETURN),
                    "RACROUTE REQUEST=TOKENXTR returned",
                    TRACE_DATA_INT(racrouteArea_p->safReturnCode, "safReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprret, "racfReturnCode"),
                    TRACE_DATA_INT(safParameterList->safprrea, "racfReasonCode"),
                    TRACE_DATA_RAWDATA(sizeof(*racrouteArea_p->uToken_p), racrouteArea_p->uToken_p, "utoken"),
                    TRACE_DATA_END_PARMS);
    }

    // looking at doc and from testing it seems both return codes and the reason code need to be checked.
    if (racrouteArea_p->safReturnCode == 0 &&
        safParameterList->safprret == 0 &&
        safParameterList->safprrea == 0) {
        // copy back utoken
        memcpy(extractedUtoken_p, racrouteArea_p->uToken_p, sizeof(*extractedUtoken_p));
    } else {
        returnCode = EXTRACT_UTOKEN_RACROUTE_TOKENXTR_FAILED;
    }

    if (serviceResults_p) {
        serviceResults_p->safReturnCode = racrouteArea_p->safReturnCode;
        serviceResults_p->racfReturnCode = safParameterList->safprret;
        serviceResults_p->racfReasonCode = safParameterList->safprrea;
    }

    // Cleanup the allocated storage
    free(racrouteArea_p);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SAFUTOKENEXTRACT_RETURNS),
                    __FUNCTION__ " returns",
                    TRACE_DATA_INT(returnCode, "returnCode"),
                    TRACE_DATA_END_PARMS);
    }

    return returnCode;
}

#if 0
__asm(" RACROUTE REQUEST=FASTAUTH,RELEASE=2.6,WORKA=0,WKAREA=0,"
      "DECOUPL=YES,ATTR=READ,ENVRIN=0,ENTITYX=0,CLASS=0,"
      "LOG=ASIS,MSGSUPP=NO,MF=L" : "DS"(fastauth_list_form));

void
funSecurityFunction() {
    __asm(" RACROUTE REQUEST=FASTAUTH,MF=L" : "DS"(fastauth_dynamic));

    fastauth_dynamic = fastauth_list_form;

    __asm(" RACROUTE REQUEST=FASTAUTH,MF=(E,(%0))" : : "r"(&fastauth_dynamic));
}


#endif

