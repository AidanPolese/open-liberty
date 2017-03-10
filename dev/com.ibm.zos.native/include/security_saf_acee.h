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
 * Assorted routines that interface with IRRSIA00 (initACEE) for creating,
 * deleting, and managing ACEEs and RACOs. 
 *
 * initACEE doc: http://publib.boulder.ibm.com/infocenter/zos/v1r12/index.jsp?topic=%2Fcom.ibm.zos.r12.ichd100%2Fintacee.htm
 */

#ifndef SECURITY_SAF_ACEE_H_
#define SECURITY_SAF_ACEE_H_

#include "common_defines.h"
#include "gen/ihaacee.h"
#include "security_saf_common.h"
#include "gen/irrpidid.h"

//---------------------------------------------------------------------
// IRRSIA00 related constants
//---------------------------------------------------------------------
#define RACO_STORAGE_KEY       2    //!< The storage key that RACF will use to allocate storage for RACOs.
#define RACO_STORAGE_SUBPOOL   229  //!< The storage subpool that RACF will use to allocate storage for RACOs.
#define IRRSIA00_WORKAREA_SIZE 1024 //!< The required size of the IRRSIA00 work area.

/**
 * Enumeration of function codes that can be supplied to IRRSIA00.
 */
#pragma enum(1) // specify that the size of each element in the enum is 1-byte
typedef enum {
    IRRSIA00_FUNCTION_CREATE     = 1, //!< Create an ACEE.
    IRRSIA00_FUNCTION_DELETE     = 2, //!< Delete an ACEE.
    IRRSIA00_FUNCTION_PURGE      = 3, //!< Purge all managed ACEEs.
    IRRSIA00_FUNCTION_REGISTER   = 4, //!< Register a certificate.
    IRRSIA00_FUNCTION_DEREGISTER = 5, //!< Deregister a certificate.
    IRRSIA00_FUNCTION_QUERY      = 6  //!< Query a certificate.
} IRRSIA00FunctionCode;
#pragma enum(reset)

// Bits that can be supplied to the Attributes parameter of IRRSIA00.
#define INTA_MANAGED     0x80000000 //!< Create an ACEE for the user ID that is cached by RACF.
#define INTA_USP         0x40000000 //!< Create a USP for the user ID.
#define INTA_TASK_LEVEL  0x20000000 //!< For create function code, create an ACEE and attach to
                                    //!<   the current TCB. For delete function code, delete the
                                    //!<   ACEE attached to the current TCB.
#define INTA_UNAUTH_CLNT 0x10000000 //!< Create an ACEE for an unauthenticated client.
#define INTA_AUTH_CLNT   0x08000000 //!< Create an ACEE for an authenticated client.
#define INTA_MSG_SUPP    0x04000000 //!< Suppress RACF messages produced as a result of creating
                                    //!<   a user’s security context.
#define INTA_ENVR_RET    0x02000000 //!< Return an ENVR object for the ACEE created by this request.
#define INTA_NO_TIMEOUT  0x01000000 //!< Create a managed ACEE that does not time out. When this bit
                                    //!<   and INTA_MANAGED are set for the creation of a new
                                    //!<   managed ACEE, the ACEE is cached and does not expire after
                                    //!<   5 minutes.
#define INTA_OUSP_RET    0x00800000 //!< Return an OUSP in the output area.
#define INTA_X500_RET    0x00400000 //!< Return an X500 name pair.

/**
 * A RACO representation.  The struct contains a pointer to the actual RACO storage
 * and the length of that storage area.
 */
#pragma pack(packed)
typedef struct {
    int            length;      //!< Length of the storage area where the RACO resides.
    void* __ptr32  address;     //!< Pointer to the RACO storage area.
} RACO;
#pragma pack(reset)

/**
 * A RACO control block.  The RACO_CB contains a RACO struct, which in turn contains
 * a pointer to the actual RACO. Most SAF services deal with RACO_CBs; e.g. this 
 * structure is passed to IRRSIA00 (initACEE) for either supplying a RACO (via the 
 * input parameter ENVR_in) or for obtaining a RACO (via the output parameter ENVR_out).  
 * 
 * For details, see the z/OS V1R10.0 Security Server RACF Callable Services documentation 
 * for IRRSIA00.
 *
 * A reference to a RACO_CB is stored in the RegistryDataArea for SAFNSC registry elements.
 */
#pragma pack(packed)
typedef struct {
    // variable names match the ones specified in the IRRSIA00 documentation
    int             ENVR_object_length;
    union {
        RACO                ENVR_RACO;
        struct {
            int             ENVR_object_storage_area_length;
            char* __ptr32   ENVR_object_storage_area_address;
        };
    };
    char            ENVR_object_storage_area_subpool;
    char            ENVR_object_storage_area_key;
} RACO_CB;  
#pragma pack(reset)

/**
 * Structure used to pass parameters to IRRSIA00.  The first half of the structure contains
 * a list of sequential pointers to the actual parameters.  The second half of the structure
 * contains storage for many of the parameters.  Many of the parameters are used as both
 * input and output parameters, and the usage of each parameter can vary based on the supplied
 * function code.  For details, see the z/OS V1R10.0 Security Server RACF Callable Services
 * documentation for IRRSIA00.
 */
#pragma pack(packed)
typedef struct {
    // sequential list of pointers to parameters; parameters names match the ones specified in the IRRSIA00 documentation
    char*                 __ptr32 Work_area_ptr;             // ptr to 1024 bytes
    int*                  __ptr32 SAF_return_code_ALET_ptr;
    int*                  __ptr32 SAF_return_code_ptr;
    int*                  __ptr32 RACF_return_code_ALET_ptr;
    int*                  __ptr32 RACF_return_code_ptr;
    int*                  __ptr32 RACF_reason_code_ALET_ptr;
    int*                  __ptr32 RACF_reason_code_ptr;
    IRRSIA00FunctionCode* __ptr32 Function_code_ptr;         // ptr to 1 byte
    int*                  __ptr32 Attributes_ptr;
    char*                 __ptr32 RACF_userid_ptr;           // ptr to 9 bytes (1 byte length, up to 8 bytes userid)
    acee* __ptr32*        __ptr32 ACEE_ptr_ptr;              // ptr to a 4-byte area that contains the ACEE ptr (i.e. ptr to a ptr).
    char*                 __ptr32 APPL_id_ptr;               // ptr to 9 bytes (1 byte length, up to 8 bytes applid)
    char*                 __ptr32 Password_ptr;              // ptr to 9 bytes (1 byte length, up to 8 bytes password)
    char*                 __ptr32 Logstring_ptr;             // ptr to variable length (1 byte length, plus data up to 255 bytes)
    char*                 __ptr32 Certificate_ptr;           // ptr to variable length (4 bytes length, plus data)
    RACO_CB*              __ptr32 ENVR_in_ptr;               // ptr to an INPUT RACO.
    RACO_CB*              __ptr32 ENVR_out_ptr;              // ptr to an OUTPUT RACO.
    int*                  __ptr32 Output_area_ptr;           // ptr to 4 bytes where service routine will save a pointer to user data
    int*                  __ptr32 X500name_ptr;              // ptr to 4 bytes where service routine will save a pointer to X500 struct
    char*                 __ptr32 Variable_list_ptr;         // ptr to variable length (4 bytes number of entries, followed by entries,
                                                            //     each entry is 8 bytes value name, 4 bytes value length, plus value data
    char*                 __ptr32 Security_label_ptr;        // ptr to 9 bytes (1 byte length, up to 8 bytes security label)
    char*                 __ptr32 SERVAUTH_name_ptr;         // ptr to variable length (1 byte length, up to 64 bytes name of resource in SERVAUTH)
    char*                 __ptr32 Password_phrase_ptr;       // ptr to variable length (1 byte length, 9-100 characters of passphrase data)
    char*                 __ptr32 Distributed_identity_ptr_ptr;  // ptr to fullword containing the address of a distributed identity data structure

    // storage for most of the actual parameters to IRRSIA00
    char Work_area[IRRSIA00_WORKAREA_SIZE];
    int  SAF_return_code_ALET;
    int  SAF_return_code;
    int  RACF_return_code_ALET;
    int  RACF_return_code;
    int  RACF_reason_code_ALET;
    int  RACF_reason_code;
    IRRSIA00FunctionCode Function_code;
    char RACF_userid[10];   // 1-byte length, the value (max 8 bytes), and a null-term
    char APPL_id[10];       // 1-byte length, the value (max 8 bytes), and a null-term
    char Password[10];      // 1-byte length, the value (max 8 bytes), and a null-term
    char padding[1];        // get us back on a word boundary.
    int  Attributes;
    acee* __ptr32 ACEE_ptr;
    idid* __ptr32 Distributed_identity_ptr;
    char Logstring[256];
    RACO_CB ENVR_in; 
    RACO_CB ENVR_out;  
    int  Output_area;
    int  X500name;
    char Security_label[9];
    char SERVAUTH_name[65];
    char Password_phrase[101];
    int  zero;

    // save area passed to IRRSIA00 in reg 13
    unsigned char savearea[SAVE_AREA_SIZE];
} IRRSIA00Parms;
#pragma pack(reset)

/**
 * Allocates and initializes an IRRSIA00_parm_data structure.  The
 * pointers in the parm list are all linked to their corresponding
 * parms, the high bit of the last parm is set to 1, and most things
 * are initialized to 0.
 *
 * IRRSIA00 understands and ignores NULL parameters, so callers of this
 * routine will only need to set values for the exact parameters that
 * are required for a given invocation of IRRSIA00.
 *
 * @return A newly allocated and initialized struct for passing parameters
 *         to IRRSIA00.  The caller is responsible for freeing this storage.
 */
IRRSIA00Parms* __ptr32 allocateIRRSIA00Parms(void) ;

/**
 * Populate an IRRSIA00_parm_data structure with the given data.
 *
 * IRRSIA00 understands and ignores NULL parameters, so callers of this
 * routine will only need to set values for the exact parameters that
 * are required for a given invocation of IRRSIA00.
 *
 * All input parms are validated to ensure they are of proper size and
 * format for IRRSIA00.  The method also checks the IRRSIA00Parms pointer 
 * against NULL, in the event that the caller failed to allocate storage
 * for it.
 *
 * @return 0 if all went well.  Non-zero if there was an error due to invalid data.
 */
int populateCommonIRRSIA00Parms(IRRSIA00Parms * __ptr32 irrsia00Parms,
                                char* username,
                                int usernameLen,
                                char* password,
                                int passwordLen,
                                char* auditString,
                                int auditStringLen,
                                char* applName,
                                int applNameLen,
                                RACO* inRaco,
                                acee* __ptr32 inAcee) ;

/**
 * Create an ACEE via initACEE (IRRSIA00).
 *
 * The caller is responsible for the ACEE storage allocated by initACEE.
 *
 * @param irrsia00Parms A struct containing all of the parameter data for the 
 *        IRRSIA00 invocation.  
 * @param safServiceResult  Optional output area for SAF return/reason codes.
 *
 * @return acee* A pointer to the ACEE.  The ACEE ptr is also copied into
 *         irrsia00Parms.ACEE_ptr.
 */
acee* createACEE(IRRSIA00Parms* __ptr32 irrsia00Parms, SAFServiceResult* safServiceResult) ;

/**
 * Create a RACO and ACEE via initACEE (IRRSIA00).
 *
 * The RACO is storageObtained via initACEE.  The caller is responsible for the storage.
 *
 * Usually we just want the RACO, but IRRSIA00 can't help but create the ACEE too.
 * Most callers will want to follow up with a call to deleteACEE.
 * 
 * @param irrsia00Parms A struct containing all of the parameter data for the 
 *        IRRSIA00 invocation.  The RACO is available at irrsia00Parms.ENVR_OUT.ENVR_RACO.
 * @param safServiceResult  Optional output area for SAF return/reason codes.
 *
 * @return 0 if IRRSIA00 is invoked.  Non-zero otherwise. 
 */
int createACEEAndRACO(IRRSIA00Parms* __ptr32 irrsia00Parms, SAFServiceResult* safServiceResult) ;

/**
 * Delete the ACEE referenced in the given IRRSIA00Parms struct.
 *
 * @param irrsia00parms A struct containing all of the parameter data for the IRRSIA00 invocation.
 * @param safServiceResult  Optional output area for SAF return/reason codes.
 */
void deleteACEE(IRRSIA00Parms* __ptr32 irrsia00Parms, SAFServiceResult* safServiceResult) ;

/**
 * Create an ACEE from the given RACO (via initACEE).
 *
 * @param theRaco           The RACO that will be used to construct the ACEE.
 * @param safServiceResult  Optional output area for SAF return/reason codes.
 *
 * @return acee The ACEE, or NULL if there was an error.  The caller is responsible for
 *         the ACEE. Eventually it should be released (also via initACEE).
 */
acee* createACEEFromRACO(RACO* theRaco, SAFServiceResult* safServiceResult) ;

/**
 * Delete the given ACEE (via initACEE).
 *
 * @param theAcee           The ACEE to delete.
 * @param safServiceResult  Optional output area for SAF return/reason codes.
 */
void deleteACEEObject(acee* __ptr32 theAcee, SAFServiceResult* safServiceResult) ;

/**
 * Deallocate the storage associated with the given RACO.
 *
 * @param theRaco The RACO to deallocate.
 */
void deallocateRACO(RACO* theRaco) ;

#endif /* SECURITY_SAF_ACEE_H_ */
