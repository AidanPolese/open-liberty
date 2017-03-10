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
 * Assorted routines that interface with z/OS security products via
 * IRRSIA00 to perform authentication tasks.
 */

#import "util_registry.h"
#import "security_saf_common.h"

#ifndef SECURITY_SAF_AUTHENTICATION_H_
#define SECURITY_SAF_AUTHENTICATION_H_

/**
 * Parameter structure used by the @c createPasswordCredential routine.
 */
typedef struct {
    char*             usernamePtr;      //!< Input  - Pointer to the username to create the credential for.
    int               usernameLen;      //!< Input  - The username length
    char*             passwordPtr;      //!< Input  - Pointer to the password for @c username.
    int               passwordLen;      //!< Input  - The password length
    char*             auditStringPtr;   //!< Input  - Pointer to an audit string used for writing SMF records.
    int               auditStringLen;   //!< Input  - The audit string length
    char*             applNamePtr;      //!< Input  - Pointer to the application name.
    int               applNameLen;      //!< Input  - The application name length
    RegistryToken*    outputToken;      //!< Output - A token associated with the native security credential that was created.
                                        //!<          Only applicable if @c returnCode is zero.
    SAFServiceResult* safServiceResult; //!< Output - Contains the SAF return code and RACF return and reason codes.
} CreatePasswordCredentialParms;

/**
 * Creates a native security credential from a username and password.
 *
 * @param parms A @c CreatePasswordCredentialParms structure containing
 *                the input and output parameters to this function.
 *
 * @return A return code indicating whether the credential was successfully created
 *           or, if not, the reason why it could not be created.
 */
void createPasswordCredential(CreatePasswordCredentialParms * parms);

/**
 * Parameter structure used by the @c createAssertedCredential routine.
 */
typedef struct {
    char*     usernamePtr;    //!< Input  - Pointer to the username to create the credential for.
    int       usernameLen;    //!< Input  - The username length
    char*     auditStringPtr; //!< Input  - Pointer to an audit string used for writing SMF records.
    int       auditStringLen; //!< Input  - The audit string length
    char*     applNamePtr;    //!< Input  - Pointer to the application name.
    int       applNameLen;    //!< Input  - The application name length
    RegistryToken* outputToken;    //!< Output - A token associated with the native security credential that was created.
    SAFServiceResult* safServiceResult; //!< Output - Contains the SAF return code and RACF return and reason codes.
} CreateAssertedCredentialParms;

/**
 * Creates a native security credential for a username.
 *
 * @param parms A @c CreateAssertedCredentialParms structure containing
 *                the input and output parameters to this function.
 */
void createAssertedCredential(CreateAssertedCredentialParms * parms);

/**
 * Parameter structure used by the @c createCertificateCredential routine.
 */
typedef struct {
    char*             certificatePtr;    //!< Input  - Pointer to the certificate to use for creating the credential.
    int               certificateLen;    //!< Input  - Length of the certificate.
    char*             auditStringPtr;    //!< Input  - Pointer to the audit string used for writing SMF records.
    int               auditStringLen; //!< Input  - The audit string length
    char*             applNamePtr;       //!< Input  - Pointer to the application name.
    int               applNameLen;    //!< Input  - The application name length
    char*             outputUsernamePtr; //!< Output - The user ID that the certificate was mapped to.
    RegistryToken*    outputToken;       //!< Output - A token associated with the native security credential that was created.
    SAFServiceResult* safServiceResult;  //!< Output - Contains the SAF return code and RACF return and reason codes.
} CreateCertificateCredentialParms;

/**
 * Creates a native security credential for a certificate.
 *
 * @param parms A @c CreateCertificateCredentialParms structure containing
 *                the input and output parameters to this function.
 */
void createCertificateCredential(CreateCertificateCredentialParms * parms);

/**
 * Parameter structure used by the @c createMappedCredential routine.
 */
typedef struct {
    char*             userNamePtr;       //!< Input  - Pointer to User's distinguished name.
    int               userNameLen;       //!< Input  - Length of the User's distinguished name.
    char*             registryNamePtr;   //!< Input  - Pointer to Registry's name.
    int               registryNameLen;   //!< Input  - Length of the Registry's name.
    char*             auditStringPtr;    //!< Input  - Pointer to the audit string used for writing SMF records.
    int               auditStringLen;    //!< Input  - The audit string length
    char*             applNamePtr;       //!< Input  - Pointer to the application name.
    int               applNameLen;       //!< Input  - The application name length
    char*             outputUserNamePtr; //!< Output - The user ID that the user/registry name was mapped to.
    RegistryToken*    outputToken;       //!< Output - A token associated with the native security credential that was created.
    SAFServiceResult* safServiceResult;  //!< Output - Contains the SAF return code and RACF return and reason codes.
} CreateMappedCredentialParms;

/**
 * Creates a native security credential for a Mapped identity.
 *
 * @param parms A @c CreateMappedCredentialParms structure containing
 *                the input and output parameters to this function.
 */
void createMappedCredential(CreateMappedCredentialParms * parms);


/**
 * Parameter structure used by the @c deleteCredential routine.
 */
typedef struct {
    RegistryToken* inputToken; //!< Input  - Pointer to the NSCToken associated with the native credential to delete.
    int*           returnCode; //!< Output - The return code from the @c deleteCredential routine.
} DeleteCredentialParms;

/**
 * Deletes a native security credential and underlying SAF credentials.
 *
 * @param parms A @c DeleteCredentialParms structure containing
 *                the input and output parameters to this function.
 *
 * @return A return code indicating whether the credential was successfully
 *           deleted or, if not, the reason why it could not be deleted.
 */
void deleteCredential(DeleteCredentialParms * parms);

/**
 * Parameter structure used for @c safExtractRealm.
 */
typedef struct {
    SAFServiceResult*       safServiceResult;
    racf_extract_results*   extractResults;
} SafExtractRealmParms;

/**
 * Extract the SAF product realm name from the APPLDATA field in the
 * SAFDFLT profile of the REALM class.
 *
 * @param parms A @c SafExtractRealmParms structure containing parameter data.
 *              The extracted data is passed back to the caller via this parm.
 */
void safExtractRealm(SafExtractRealmParms* parms);

/**
 * Parameter structure used by the @c isRESTRICTED routine.
 */
typedef struct {
    RegistryToken*      safCredentialToken; //!< Input - A token associated with the native security credential (RACO) to be authorized.
    int*                isRestrictedBit;    //!< Output - The value of the aceeraui bit (only valid if SAFServiceResult.wasReturnCode == 0).
    SAFServiceResult*   safServiceResult;   //!< Output - Contains the SAF return code and RACF return and reason codes.
} IsRESTRICTEDParms;

/**
 * PC routine checks if the user associated with the given native credential
 * has the RESTRICTED attribute set.  This is determined by checking the 
 * aceeraui bit of the ACEE.
 *
 * @param parms The IsRESTRICTEDParms structure, containing the native credential token
 *        to check and an int field for the aceeraui bit. The result of the operation is 
 *        contained in the safServiceResult field.
 */
void isRESTRICTED(IsRESTRICTEDParms* parms);

typedef struct {
    RegistryToken*      safCredentialToken; //!< Input - A token associated with the native security credential (RACO).
    ExtractedUtoken*    safExtractedUtoken; //!< Output - The extracted UTOKEN.
    SAFServiceResult*   safServiceResult;   //!< Output - Contains the SAF return code and RACF return and reason codes.
} ExtractUtokenParms;


/**
 * PC routine extracts the UTOKEN from the ACEE associated with the given SAFCredentialToken.
 *
 * @param parms The ExtractUtokenParms structure, containing the native credential token
 *        to extract the UTOKEN from.
 */
void extractUtoken(ExtractUtokenParms* parms);

/**
 * Length of pre-allocated (stack) storage area for profileName parms that
 * need to be copied to key2.  
 *
 * Used by SafRacrouteExtractParmsKey2.
 */
#define SAF_PROFILENAME_MAX_LENGTH 1024

/**
 * Parameter structure used for @c safRacrouteExtract.
 */
typedef struct {
    char*                   className;            //!< Input  - Pointer to User's distinguished name.
    int                     classNameLen;         //!< Input  - Length of the User's distinguished name.
    char*                   profileName;          //!< Input  - Pointer to Registry's name.
    int                     profileNameLen;       //!< Input  - Length of the Registry's name.
    char*                   fieldName;            //!< Input  - Pointer to the audit string used for writing SMF records.
    int                     fieldNameLen;         //!< Input  - The audit string length
    SAFServiceResult*       safServiceResult;
    racf_extract_results*   racfExtractResults;
} SafRacrouteExtractParms;

/**
 * Extract data from a field in a SAF resource profile.
 *
 * @param parms A @c SafRacrouteExtractParms structure containing parameter data.
 *              The extracted data is passed back to the caller via this parm.
 */
void safRacrouteExtract(SafRacrouteExtractParms* parms);


#endif /* SECURITY_SAF_AUTHENTICATION_H_ */
