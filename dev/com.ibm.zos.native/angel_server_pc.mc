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
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <ieac.h>

#include "include/angel_server_pc.h"

#include "include/angel_bgvt_services.h"
#include "include/angel_client_bind_data.h"
#include "include/angel_dynamic_replaceable_module.h"
#include "include/angel_functions.h"
#include "include/angel_process_data.h"
#include "include/angel_server_pc_stub.h"
#include "include/angel_task_data.h"
#include "include/bbgzasvt.h"
#include "include/bbgzsgoo.h"
#include "include/bpx_ipt.h"
#include "include/bpx_load.h"
#include "include/bpx_stat.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/mvs_abend.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_contents_supervisor.h"
#include "include/mvs_estae.h"
#include "include/mvs_setlock.h"
#include "include/mvs_storage.h"
#include "include/mvs_svcdump_services.h"
#include "include/mvs_utils.h"
#include "include/mvs_tcb_iterator.h"
#include "include/ras_tracing.h"
#include "include/security_saf_authorization.h"

#include "include/gen/bpxystat.h"
#include "include/gen/bpxzotcb.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ikjtcb.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"

#ifndef CHAR_NULL
#define CHAR_NULL '\0'
#endif

#define SAFM_NAME_MAX_LEN 4096
#define SCFM_NAME_MAX_LEN 4096
#define INVOKE_ARG_STRUCT_MAX_BYTES 65536
#define INVOKE_ARG_STRUCT_REASONABLE_SIZE 4096

#define LOCAL_SAFM_SUBPOOL 249
#define LOCAL_SAFM_KEY 2

#define SAF_ANGEL_REQUESTOR_NAME "BBGZANGL"
#define SAF_ANGEL_APPLICATION_NAME "BBGZANGL"
#define SAF_ANGEL_PROFILE "BBG.ANGEL"

#define REASON_CURRENT_PROCESS_ENDING 0x00000143
#define RETURN_CODE_EMVSINITIAL 0x0000009C  // Process Initialization error.

/** Client token for ASVT.  There is no token since we're in the server. */
#define NO_CLIENT_TOKEN 0L

/**
 * Called by the angel registration code to determine if the user
 * associated with caller is allowed to use the angel infrastructure.
 * @param the angel name. This will be null if the default angel is being used.
 * @return TRUE if the server is authorized to complete registration false if not.
 */
static unsigned char
isServerAuthorized(char* angelName) {
    saf_results results;
    char angelProfile[65];
    results.safReturnCode = 0;
    int rc = 0;

    if(angelName != NULL) {
        // Allocate profile name + angel name + . + null
        //angelProfile = malloc(strlen(SAF_ANGEL_PROFILE) + strlen(angelName)+2);
        // copy in angel Profile suffix
        if( (strlen(SAF_ANGEL_PROFILE)+strlen(angelName)) < 65) {
           memcpy( angelProfile, SAF_ANGEL_PROFILE, strlen(SAF_ANGEL_PROFILE) );
           memcpy( angelProfile + strlen(SAF_ANGEL_PROFILE), ".", 1);
           memcpy( angelProfile + strlen(SAF_ANGEL_PROFILE) + 1 , angelName, strlen(angelName)+1 );
        } else {
            rc=12;
        }
    } else {
        memcpy(angelProfile, SAF_ANGEL_PROFILE,strlen(SAF_ANGEL_PROFILE)+1);
    }
    if(rc == 0)
          rc = checkAuthorization(&results,
                                1,                          // Suppress messages
                                NONE,                       // Log option
                                SAF_ANGEL_REQUESTOR_NAME,   // Requestor
                                NULL,                       // ACEE
                                READ,                       // Access level
                                SAF_ANGEL_APPLICATION_NAME, // Application name
                                SAF_SERVER_CLASS,           // Class
                                  angelProfile);              // Profile

    // Good return codes, we're good to go
    if (rc == 0 && results.safReturnCode == 0) {
        return 1;
    }

    // Bad return codes, not so good to go
    // Server is not authorized
    return 0;
}

/**
 * Called by the angel registration code to determine if the user
 * associated with the caller is allowed to use a particular authorized
 * function module.  A SAF profile name is built from the
 * module and function name and checked against the server class:
 *   BBG.AUTHMOD.{module_name}
 *
 * @param module_name A string representing the name of the module that contains
 *                    the functions which are being loaded.  The module name
 *                    becomes part of the SAF profile name that is checked.
 *
 * @return TRUE if the server is authorized to use the function set, FALSE if not.
 */
static unsigned char
isServerAuthorizedForModule(char* module_name) {
    saf_results results;

    // -----------------------------------------------------------------------
    // Make the RACF entity name that we'll be using.
    // -----------------------------------------------------------------------
    char racf_entity_name[64];
    snprintf(racf_entity_name, sizeof(racf_entity_name), "BBG.AUTHMOD.%s", module_name);

    int rc = checkAuthorization(&results,
                                1,                          // Suppress messages
                                NONE,                       // Log option
                                SAF_ANGEL_REQUESTOR_NAME,   // Requestor
                                NULL,                       // ACEE
                                READ,                       // Access level
                                SAF_ANGEL_APPLICATION_NAME, // Application name
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    // Good return codes, we're good to go
    if (rc == 0 && results.safReturnCode == 0) {
        return 1;
    }

    // Bad return codes, not so good to go
    // Server is not authorized
    return 0;
}

/**
 * Called by the angel registration code to determine if the user
 * associated with the caller is allowed to use a particular function
 * in an authorized function module.  A SAF profile name is built from the
 * module and function name and checked against the server class:
 *   BBG.AUTHMOD.{module_name}.{function_name}
 *
 * @param module_name A string representing the name of the module that contains
 *                    the functions which are being loaded.  The module name
 *                    becomes part of the SAF profile name that is checked.
 * @param function_name A string representing the name of the function set that
 *                      is being checked.  The function name becomes part of the
 *                      SAF profile name that is checked.
 *
 * @return TRUE if the server is authorized to use the function set, FALSE if not.
 */
static unsigned char
isServerAuthorizedForFunction(char* module_name, char* function_name) {
    saf_results results;

    // -----------------------------------------------------------------------
    // Make the RACF entity name that we'll be using.
    // -----------------------------------------------------------------------
    char racf_entity_name[64];
    snprintf(racf_entity_name, sizeof(racf_entity_name), "BBG.AUTHMOD.%s.%s", module_name, function_name);

    int rc = checkAuthorization(&results,
                                1,                          // Suppress messages
                                NONE,                       // Log option
                                SAF_ANGEL_REQUESTOR_NAME,   // Requestor
                                NULL,                       // ACEE
                                READ,                       // Access level
                                SAF_ANGEL_APPLICATION_NAME, // Application name
                                SAF_SERVER_CLASS,           // Class
                                racf_entity_name);          // Profile

    // Good return codes, we're good to go
    if (rc == 0 && results.safReturnCode == 0) {
        return 1;
    }

    // Bad return codes, not so good to go
    // Server is not authorized
    return 0;
}

/**
 * Loads the Server Common Functions Module.
 *
 * @param scfmName The name of the server common function module in the HFS.
 *
 * @return The details of the SCFM module, loaded into common storage.
 */
static loadhfs_details* load_scfm(char* scfmName) {
    bbgzasvt_header* scfm_p = NULL;

    // -----------------------------------------------------------------
    // Make sure the SCFM name is not too long.
    // -----------------------------------------------------------------
    if (memchr(scfmName, 0, SCFM_NAME_MAX_LEN) == NULL) {
        return NULL;
    }

    // -----------------------------------------------------------------------
    // See if the server common functions module is APF
    // authorized.
    // -----------------------------------------------------------------------
    struct stat scfmInfo;
    memset(&scfmInfo, 0, sizeof(scfmInfo));
    int statRC = stat(scfmName, &scfmInfo);
    if (statRC != 0) {
        return NULL;
    }

    unsigned char isScfmApfAuth = ((scfmInfo.st_visible & st_apfauth) == st_apfauth);
    unsigned long long fileSize;
    memcpy(&fileSize, &(scfmInfo.st_size), sizeof(scfmInfo.st_size));

    if (isScfmApfAuth != TRUE) {
        return NULL;
    }

    // -----------------------------------------------------------
    // Make sure that the module we are about to load is at least
    // as big as the header plus one function entry.
    // -----------------------------------------------------------
    int minLength = sizeof(bbgzasvt_header) + sizeof(bbgzasve) +
                    strlen(BBGZASVT_EYE_END);
    if (fileSize < minLength) {
        return NULL;
    }

    // -----------------------------------------------------------
    // Load the server common functions module into common storage.
    //-----------------------------------------------------------
    loadhfs_details* scfmDetails = load_from_hfs(scfmName);

    if (scfmDetails == NULL) {
        return NULL;
    }

    // ---------------------------------------------------------
    // Try to verify the structure of the DLL.  We don't know
    // how big it was so try to do this from the begining of
    // the module in storage to the end.
    // ---------------------------------------------------------
    scfm_p = (bbgzasvt_header*)(((int)(scfmDetails->entry_p)) & 0x7FFFFFFE);

    // ---------------------------------------------------------
    // See if the module name matches the eye catcher of the
    // module we loaded.
    // ---------------------------------------------------------
    char* pointerToExpectedModuleName = scfmName;
    char* pointerToLastSlash = strrchr(scfmName, (int)'/');
    if (pointerToLastSlash != NULL) {
        pointerToExpectedModuleName = pointerToLastSlash + 1;
    }

    int lenOfExpectedModuleName = strlen(pointerToExpectedModuleName);
    if ((lenOfExpectedModuleName >= 1) &&
        (lenOfExpectedModuleName <= 8)) {
        char upperExpectedModuleName[8];
        memset(upperExpectedModuleName, ' ', sizeof(upperExpectedModuleName));
        memcpy(upperExpectedModuleName,
               pointerToExpectedModuleName,
               lenOfExpectedModuleName);
        unsigned char stringsEqual = TRUE;

        for (int x = 0; x < 8; x++) {
            if (toupper(upperExpectedModuleName[x]) !=
                toupper(scfm_p->module_name[x])) {
                stringsEqual = FALSE;
            }
        }

        if (stringsEqual == TRUE) {
            // -----------------------------------------------------
            // See if the vector table eye catcher is correct.
            // -----------------------------------------------------
            if (memcmp(scfm_p->eyecatcher, BBGZASVT_EYE,
                       sizeof(scfm_p->eyecatcher)) == 0) {
                // ---------------------------------------------------
                // Make sure we have a sane number of entries in the
                // table.
                // ---------------------------------------------------
                if ((scfm_p->num_entries >= 0) &&
                    (scfm_p->num_entries <= BBGZASVT_MAX_ENTRIES)) {

                    // -----------------------------------------------
                    // Make sure the module is long enough to support
                    // the number of entries we think we have.  Note
                    // that we are not taking into account that there
                    // has to be some code behind these entries.
                    // -----------------------------------------------
                    minLength = minLength +
                        (((scfm_p->num_entries) - 1) * sizeof(bbgzasve));
                    if (scfmDetails->mod_len >= minLength) {
                        // -------------------------------------------------
                        // Make sure that all of the VCONs point into the
                        // load module.
                        // -------------------------------------------------
                        bbgzasve* curEntry_p = (bbgzasve*)(((char*)scfm_p) + sizeof(bbgzasvt_header));
                        void* endingAddr = ((char*)(scfmDetails->mod_p)) + scfmDetails->mod_len;
                        unsigned char validVCONs = TRUE;
                        for (int x = 0; x < scfm_p->num_entries; x++) {
                            if (((void*)(curEntry_p->bbgzasve_fcn_ptr) <= (scfmDetails->mod_p)) ||
                                (curEntry_p->bbgzasve_fcn_ptr >= endingAddr)) {
                                validVCONs = FALSE;
                            }
                            curEntry_p = curEntry_p + 1;
                        }

                        if (validVCONs == TRUE) {
                            // -------------------------------------------------
                            // Check the eye catcher at the end of the table.
                            // -------------------------------------------------
                            char* endEyecatcher = ((char*)scfm_p) +
                                sizeof(bbgzasvt_header) +
                                ((scfm_p->num_entries) * sizeof(bbgzasve));
                            if (memcmp(endEyecatcher,
                                       BBGZASVT_EYE_END,
                                       strlen(BBGZASVT_EYE_END)) == 0) {
                                // -----------------------------------------------
                                // Success.  Tell contents supervisor about it.
                                // -----------------------------------------------
                                lpmea modinfo;
                                int addReturnCode = contentsSupervisorAddToDynamicLPA(scfmName,
                                                                                      scfmDetails->entry_p,
                                                                                      scfmDetails->mod_p,
                                                                                      scfmDetails->mod_len,
                                                                                      &modinfo,
                                                                                      NULL);
                                if (addReturnCode == 0) {
                                    memcpy(scfmDetails->delete_token, modinfo.lpmeadeletetoken, sizeof(scfmDetails->delete_token));
                                } else {
                                    // TODO: Remove me
                                    abend(ABEND_TYPE_SERVER, 0xDEADBEEF);
                                }

                            } else {
                                unload_from_hfs(scfmDetails);
                                free(scfmDetails);
                                scfmDetails = NULL;
                            }
                        } else {
                            unload_from_hfs(scfmDetails);
                            free(scfmDetails);
                            scfmDetails = NULL;
                        }
                    } else {
                        unload_from_hfs(scfmDetails);
                        free(scfmDetails);
                        scfmDetails = NULL;
                    }
                } else {
                    unload_from_hfs(scfmDetails);
                    free(scfmDetails);
                    scfmDetails = NULL;
                }
            } else {
                unload_from_hfs(scfmDetails);
                free(scfmDetails);
                scfmDetails = NULL;
            }
        } else {
            unload_from_hfs(scfmDetails);
            free(scfmDetails);
            scfmDetails = NULL;
        }
    } else {
        unload_from_hfs(scfmDetails);
        free(scfmDetails);
        scfmDetails = NULL;
    }

    return scfmDetails;
}


/**
 * Loads the Server Authorized Functions Module.
 *
 * @param safm_name The name of the server authorized function module in the HFS.
 *
 * @return A pointer to the ASVT header contained in the module, pointed to by
 *         the entry point of the module.
 */
static bbgzasvt_header* load_safm(char* safm_name) {
    bbgzasvt_header* original_safm_p = NULL;
    bbgzasvt_header* safm_p = NULL;

    // -----------------------------------------------------------------
    // Make sure the SAFM name is not too long.
    // -----------------------------------------------------------------
    if (memchr(safm_name, 0, SAFM_NAME_MAX_LEN) != NULL) {
        // ---------------------------------------------------------------
        // See if the server authorized functions module is APF
        // authorized.
        // ---------------------------------------------------------------
        struct stat safm_info;
        memset(&safm_info, 0, sizeof(safm_info));
        int stat_rc = stat(safm_name, &safm_info);
        if (stat_rc == 0) {
            unsigned char is_safm_apf_auth =
                ((safm_info.st_visible & st_apfauth) == st_apfauth);
            unsigned long long file_size;
            memcpy(&file_size, &(safm_info.st_size), sizeof(safm_info.st_size));

            if (is_safm_apf_auth == TRUE) {

                // -----------------------------------------------------------
                // Make sure that the module we are about to load is at least
                // as big as the header plus one function entry.
                // -----------------------------------------------------------
                int min_length = sizeof(bbgzasvt_header) + sizeof(bbgzasve) +
                                 strlen(BBGZASVT_EYE_END);
                if (file_size >= min_length) {
                    // -----------------------------------------------------------
                    // Load the server authorized functions module.  This gets
                    // loaded into private storage.
                    //-----------------------------------------------------------
                    loadhfs_details* safm_details = (loadhfs_details*)
                        load_from_hfs_private(safm_name);

                    if (safm_details != NULL) {
                        // ---------------------------------------------------------
                        // Try to verify the structure of the DLL.  We don't know
                        // how big it was so try to do this from the begining of
                        // the module in storage to the end.
                        // ---------------------------------------------------------
                        original_safm_p = (bbgzasvt_header*)(safm_details->entry_p);
                        safm_p = (bbgzasvt_header*)
                            (((int)(safm_details->entry_p)) & 0x7FFFFFFE);

                        // ---------------------------------------------------------
                        // See if the module name matches the eye catcher of the
                        // module we loaded.
                        // ---------------------------------------------------------
                        char* pointer_to_expected_module_name = safm_name;
                        char* pointer_to_last_slash = strrchr(safm_name, (int)'/');
                        if (pointer_to_last_slash != NULL) {
                            pointer_to_expected_module_name = pointer_to_last_slash + 1;
                        }

                        int len_of_expected_module_name =
                            strlen(pointer_to_expected_module_name);
                        if ((len_of_expected_module_name >= 1) &&
                            (len_of_expected_module_name <= 8)) {
                            char upper_expected_module_name[8];
                            memset(upper_expected_module_name, ' ',
                                   sizeof(upper_expected_module_name));
                            memcpy(upper_expected_module_name,
                                   pointer_to_expected_module_name,
                                   len_of_expected_module_name);
                            unsigned char strings_equal = TRUE;

                            for (int x = 0; x < 8; x++) {
                                if (toupper(upper_expected_module_name[x]) !=
                                    toupper(safm_p->module_name[x])) {
                                    strings_equal = FALSE;
                                }
                            }

                            if (strings_equal == TRUE) {
                                // -----------------------------------------------------
                                // See if the vector table eye catcher is correct.
                                // -----------------------------------------------------
                                if (memcmp(safm_p->eyecatcher, BBGZASVT_EYE,
                                           sizeof(safm_p->eyecatcher)) == 0) {
                                    // ---------------------------------------------------
                                    // Make sure we have a sane number of entries in the
                                    // table.
                                    // ---------------------------------------------------
                                    if ((safm_p->num_entries >= 0) &&
                                        (safm_p->num_entries <= BBGZASVT_MAX_ENTRIES)) {

                                        // -----------------------------------------------
                                        // Make sure the module is long enough to support
                                        // the number of entries we think we have.  Note
                                        // that we are not taking into account that there
                                        // has to be some code behind these entries.
                                        // -----------------------------------------------
                                        min_length = min_length +
                                            (((safm_p->num_entries) - 1) * sizeof(bbgzasve));
                                        if (safm_details->mod_len >= min_length) {
                                            // -------------------------------------------------
                                            // Make sure that all of the VCONs point into the
                                            // load module.
                                            // -------------------------------------------------
                                            bbgzasve* cur_entry_p = (bbgzasve*)(((char*)safm_p) + sizeof(bbgzasvt_header));
                                            void* ending_addr = ((char*)(safm_details->mod_p)) + safm_details->mod_len;
                                            for (int x = 0; x < safm_p->num_entries; x++) {
                                                if (((void*)(cur_entry_p->bbgzasve_fcn_ptr) <= (safm_details->mod_p)) ||
                                                    (cur_entry_p->bbgzasve_fcn_ptr >= ending_addr)) {
                                                    abend_with_data(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_BAD_VCON, (void*)(cur_entry_p->bbgzasve_fcn_ptr), safm_p);
                                                }
                                                cur_entry_p = cur_entry_p + 1;
                                            }

                                            // -------------------------------------------------
                                            // Check the eye catcher at the end of the table.
                                            // -------------------------------------------------
                                            char* end_eyecatcher = ((char*)safm_p) +
                                                sizeof(bbgzasvt_header) +
                                                ((safm_p->num_entries) * sizeof(bbgzasve));
                                            if (memcmp(end_eyecatcher,
                                                       BBGZASVT_EYE_END,
                                                       strlen(BBGZASVT_EYE_END)) == 0) {
                                                // -----------------------------------------------
                                                // Success
                                                // -----------------------------------------------
                                            } else {
                                                abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_BAD_END_EYE);
                                            }
                                        } else {
                                            abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_TOO_SMALL2);
                                        }
                                    } else {
                                        abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_TOO_MANY_ENTRIES);
                                    }
                                } else {
                                    abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_BAD_VECTOR_EYE);
                                }
                            } else {
                                abend(ABEND_TYPE_SERVER,KRSN_ANGEL_SERVER_PC_SAFM_MODULE_NAME_NOT_EQUAL);
                            }
                        } else {
                            abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_MODULE_NAME_INCORRECT);
                        }

                        free(safm_details);
                    } else {
                        abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_LOAD_FAILURE);
                    }
                } else {
                    abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_TOO_SMALL);
                }
            } else {
                // Fail registration if the module isn't APF authorized.  Don't abend.
                original_safm_p =  (bbgzasvt_header*) -1;
            }
        } else {
            abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_STAT_FAILURE);
        }
    } else {
        abend(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_HFS_PATH_TOO_LONG);
    }

    return original_safm_p;
}

/**
 * Cleans up an old SAFM
 *
 * @param recovery_p A recovery area (used by the mainline and the ARR) which
 *                   contains a pointer to the SAFM that was loaded, a pointer
 *                   to the entry point of the SAFM, which contains the function
 *                   (vector) table of authorized functions, the size of the
 *                   SAFM module, as well as flags which tell us which part of
 *                   the cleanup to do.
 */
static void cleanup_safm(angel_server_pc_recovery* recovery_p) {
    int rc, rsn, rv;
    union {
        struct {
            int rc;
            int rsn;
        } dataForDump_s;
        long long dataForDump_l;
    } dataForDump_u;

    // -----------------------------------------------------------------------
    // Trade in our big cleanup flag for smaller sub-cleanup steps.
    // -----------------------------------------------------------------------
    if (recovery_p->drm_cleanup_safm == 1) {
        recovery_p->drm_cleanup_safm = 0;
        recovery_p->drm_cleanup_safm_release = 1;
        recovery_p->drm_cleanup_safm_unload = 1;
    }

    // -----------------------------------------------------------------------
    // Unload if we're supposed to do that.
    // -----------------------------------------------------------------------
    if (recovery_p->drm_cleanup_safm_unload == 1) {
        recovery_p->drm_cleanup_safm_unload = 0;
        unload_from_hfs_private(recovery_p->drm_safm_entrypt_p, &rc, &rsn, &rv);

        // -------------------------------------------------------------------
        // If the unload failed for some reason, someone may be playing games
        // with us, so abend.
        // -------------------------------------------------------------------
        if (rv != 0) {
            // dump for everything but return code x9C reason code 0x00000143
            if (!((rc == RETURN_CODE_EMVSINITIAL) && ((rsn & 0x0000FFFF) == REASON_CURRENT_PROCESS_ENDING))) {
                dataForDump_u.dataForDump_s.rc = rc;
                dataForDump_u.dataForDump_s.rsn = rsn;
                abend_with_data(ABEND_TYPE_SERVER, KRSN_ANGEL_SERVER_PC_SAFM_UNLOAD_FAIL, (void *)dataForDump_u.dataForDump_l, recovery_p->drm_safm_entrypt_p);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Release the storage if we're supposed to do that.
    // -----------------------------------------------------------------------
    if (recovery_p->drm_cleanup_safm_release == 1) {
        recovery_p->drm_cleanup_safm_release = 0;
        int sr_rc = storageRelease(recovery_p->drm_safm_func_table_p,
                                   recovery_p->drm_safm_func_table_size,
                                   LOCAL_SAFM_SUBPOOL,
                                   LOCAL_SAFM_KEY);
    }
}

/**
 *  Performs the RACF checks on the individual functions in the function table.
 *
 *  @param safm_p A pointer to the function table of the SAFM module, in
 *                local, modifiable storage.
 */
static void perform_ASVT_authorization_checks(bbgzasvt_header* safm_p)
{
    struct fcn_name_node {
        struct fcn_name_node* next_p;
        char* name;
        unsigned char auth;
    };

    // -----------------------------------------------------------------------
    // Get the 'module name' out of the ASVT header.  It's part of the RACF
    // name that we check.  Make sure it's null terminated, and that we
    // remove any blanks on the end.
    // -----------------------------------------------------------------------
    char module_name[9];
    memcpy(module_name, safm_p->module_name, sizeof(safm_p->module_name));
    for (int x = sizeof(safm_p->module_name); module_name[x - 1] == ' '; x--) {
        module_name[x - 1] = CHAR_NULL;
    }
    module_name[8] = CHAR_NULL;

    // -----------------------------------------------------------------------
    // Auto-authorize any "KERNEL" function by pre-creating a cached version
    // for it with the auth bit on.
    // -----------------------------------------------------------------------
    struct fcn_name_node* head_p = malloc(sizeof(struct fcn_name_node));
    if (head_p != NULL) {
        head_p->name = malloc(strlen("KERNEL") + 1);
        if (head_p->name != NULL) {
            strcpy(head_p->name, "KERNEL");
            head_p->auth   = 1;
            head_p->next_p = NULL;
        } else {
            free(head_p);
        }
    }

    // -----------------------------------------------------------------------
    // For each function, do the RACF check and turn on its available bit if
    // it's available.
    // -----------------------------------------------------------------------
    bbgzasve* cur_function = (bbgzasve*)(((char*)safm_p) + sizeof(*safm_p));
    for (int x = 0; x < safm_p->num_entries; x++) {
        // -------------------------------------------------------------------
        // Get the SAF name for this function.
        // -------------------------------------------------------------------
        char function_name[9];
        memcpy(function_name, cur_function->bbgzasve_auth_name, sizeof(cur_function->bbgzasve_auth_name));
        for (int y = sizeof(cur_function->bbgzasve_auth_name); function_name[y - 1] == ' '; y--) {
            function_name[y - 1] = CHAR_NULL;
        }
        function_name[8] = CHAR_NULL;

        // -------------------------------------------------------------------
        // See if we've already checked this function name.
        // -------------------------------------------------------------------
        struct fcn_name_node* cached_node_p = NULL;
        if (head_p != NULL) {
            for (struct fcn_name_node* cur_node_p = head_p;
                 (cur_node_p != NULL) && (cached_node_p == NULL);
                 cur_node_p = cur_node_p->next_p) {
                if (strcmp(cur_node_p->name, function_name) == 0) {
                    cached_node_p = cur_node_p;
                }
            }
        }

        if (cached_node_p == NULL) {
            unsigned char is_function_authorized = isServerAuthorizedForFunction(module_name, function_name);
            cur_function->bbgzasve_runtime_bits.authorized_to_use = is_function_authorized;

            struct fcn_name_node* new_node_p = malloc(sizeof(struct fcn_name_node));
            if (new_node_p != NULL) {
                new_node_p->name = malloc(strlen(function_name) + 1);
                if (new_node_p->name != NULL) {
                    strcpy(new_node_p->name, function_name);
                    new_node_p->auth = is_function_authorized;
                    new_node_p->next_p = head_p;
                    head_p = new_node_p;
                } else {
                    free(new_node_p);
                }
            }
        } else {
            cur_function->bbgzasve_runtime_bits.authorized_to_use = cached_node_p->auth;
        }

        cur_function = cur_function + 1;
    }

    // -----------------------------------------------------------------------
    // Clean up the RACF check cache.
    // -----------------------------------------------------------------------
    struct fcn_name_node* cur_p = head_p;
    while (cur_p != NULL) {
        struct fcn_name_node* next_p = cur_p->next_p;
        free(cur_p->name);
        free(cur_p);
        cur_p = next_p;
    }
}

/**
 * Tells us if our task is a descendent of the IPT.
 *
 * @param apd_p A pointer to the angel process data, where the IPT TCB address
 *              should be saved.
 *
 * @return 1 if our task is a descendent of the IPT, 0 if not.
 */
#pragma inline(isIPTinOurParentTCBchain)
static unsigned char isIPTinOurParentTCBchain(angel_process_data* apd_p) {
    angel_task_data* atd_p = getAngelTaskData();

    if (atd_p->iptIsInParentTCBChain == FALSE) {
        psa* psa_p = NULL;
        tcb* current_tcb_p = psa_p->psatold;

        tcb* ipt_tcb_p = apd_p->initial_pthread_creating_task_p;

        if (ipt_tcb_p != NULL) {
            while ((current_tcb_p != NULL) && (atd_p->iptIsInParentTCBChain == FALSE)) {
                if (current_tcb_p == ipt_tcb_p) {
                    atd_p->iptIsInParentTCBChain = TRUE;
                }

                current_tcb_p = current_tcb_p->tcbotc;
            }
        }
    }

    return atd_p->iptIsInParentTCBChain;
}

/**
 * Removes resource managers for a Liberty server.
 *
 * @param recovery_p The ARR recovery parm area.
 */
static void destroyResourceManagersForServer(angel_server_pc_recovery* recovery_p) {
    int cur_token = recovery_p->drm_task_resmgr_token;
    recovery_p->drm_task_resmgr_token = 0;
    deleteResourceManager(&(cur_token), BBOZRMGR_TYPE_ALLTASKS);

    cur_token = recovery_p->drm_as_resmgr_token;
    recovery_p->drm_as_resmgr_token = 0;
    deleteResourceManager(&(cur_token), BBOZRMGR_TYPE_AS);
}

/**
 * Establishes resource managers for a Liberty server.
 *
 * @param recovery_p The ARR recovery parm area.
 * @param sgoo_p A pointer to the SGOO control block.
 *
 * @return 0 on success, nonzero on failure.
 */
static int establishResourceManagersForServer(angel_server_pc_recovery* recovery_p, bbgzsgoo* sgoo_p) {
    int resmgr_rc = -1;
    long long resmgrParms = (long long) sgoo_p->bbgzsgoo_pcLatentParmArea_p;

    resmgr_rc = addResourceManager(&recovery_p->drm_as_resmgr_token,
                                   &resmgrParms,
                                   BBOZRMGR_TYPE_AS,
                                   sgoo_p->bbgzsgoo_fsm->resmgr_stub);
    if (resmgr_rc == 0) {
        resmgr_rc = addResourceManager(&recovery_p->drm_task_resmgr_token,
                                       &resmgrParms,
                                       BBOZRMGR_TYPE_ALLTASKS,
                                       sgoo_p->bbgzsgoo_fsm->resmgr_stub);
        if (resmgr_rc != 0) {
            deleteResourceManager(&(recovery_p->drm_as_resmgr_token), BBOZRMGR_TYPE_AS);
            recovery_p->drm_as_resmgr_token = 0;
        }
    }

    return resmgr_rc;
}

// Register a server with the angel
int dynamicReplaceablePC_Register(long long usertoken,
                                  bbgzsgoo* sgoo_p,
                                  angel_process_data* apd_p,
                                  bbgzarmv* armv_p,
                                  char* server_authorized_function_module_name,
                                  angel_server_pc_recovery* recovery_p) {
    int register_rc = ANGEL_REGISTER_DRM_FAIL;

    unsigned char reregistration = (apd_p != NULL);

    psa* psa_p = NULL;
    ascb* ascb_p = psa_p->psaaold;
    assb* assb_p = ascb_p->ascbassb;

    // -----------------------------------------------------------------------
    // See if we are authorized to connect to the angel.
    // -----------------------------------------------------------------------

    char* angelName = NULL;
    if(sgoo_p->bbgzsgoo_angelAnchor_p != NULL && sgoo_p->bbgzsgoo_angelAnchor_p->name != NULL) {
        angelName = sgoo_p->bbgzsgoo_angelAnchor_p->name;
    }

    if (isServerAuthorized(angelName) == FALSE) {
        // ---------------------------------------------------------------
        // Not authorized.  TODO: Design doc says to issue a WTO here if
        // not authorized, and to do something else if started from the
        // UNIX shell.  Need to do something here (not sure how to tell
        // if started from the shell).
        // ---------------------------------------------------------------
        return ANGEL_REGISTER_DRM_NOT_AUTHORIZED;
    }

    // -------------------------------------------------------------------
    // The load modules that we'll be loading are tied to the initial
    // pthread creating task (IPT).  Go find the IPT and make sure we're
    // related to it.
    // -------------------------------------------------------------------
    recovery_p->drm_initial_pthread_creating_task_p = getIPTandVerifyCallerIsRelated();
    if (recovery_p->drm_initial_pthread_creating_task_p == NULL) {
        return ANGEL_REGISTER_DRM_NOT_IN_IPT_CHAIN;
    }

    // -------------------------------------------------------------------
    // Setup a RESMGR to watch over this space, and the IPT.
    // -------------------------------------------------------------------
    int resmgr_rc = (reregistration == FALSE) ? establishResourceManagersForServer(recovery_p, sgoo_p) : 0;
    if (resmgr_rc != 0) {
        return ANGEL_REGISTER_DRM_RESMGR_FAIL;
    }

    // ---------------------------------------------------------------
    // The authorized module name is in the caller's key 8 storage.
    // Move it into key 2 storage.
    // From this point on, we won't try to make a quick exit if something
    // goes wrong.  There is too much cleanup logic.
    // ---------------------------------------------------------------
    char local_server_authorized_function_module_name[SAFM_NAME_MAX_LEN];
    strncpy_sk(local_server_authorized_function_module_name,
               server_authorized_function_module_name,
               SAFM_NAME_MAX_LEN, 8);

    // -----------------------------------------------------------------------
    // Create an angel process data control block.
    // -----------------------------------------------------------------------
    unsigned char ipt_has_not_moved = TRUE;

    if (reregistration == FALSE) {
        apd_p = createAngelProcessData(sgoo_p, ANGEL_PROCESS_TYPE_SERVER);

        if (apd_p != NULL) {
            // ---------------------------------------------------------------
            // Finish filling in the data with fields that are specific to
            // authorized processes creating the angel process data.
            // ---------------------------------------------------------------
            apd_p->as_resmgr_token = recovery_p->drm_as_resmgr_token;
            apd_p->js_task_resmgr_token = recovery_p->drm_task_resmgr_token;
            apd_p->cur_armv_seq = armv_p->bbgzarmv_instancecount;
            apd_p->key2_env_parms_p = recovery_p->fsm_csysenv_p;
            apd_p->key2_env_p = recovery_p->fsm_cenv_p;
            apd_p->initial_pthread_creating_task_p = recovery_p->drm_initial_pthread_creating_task_p;
        }
    } else {
        // -------------------------------------------------------------------
        // Make sure that the IPT is still where it was before.
        // -------------------------------------------------------------------
        ipt_has_not_moved = (apd_p->initial_pthread_creating_task_p == recovery_p->drm_initial_pthread_creating_task_p);
    }

    if ((ipt_has_not_moved == TRUE) && (apd_p != NULL)){

        // -------------------------------------------------------------------
        // Create the angel task level control blocks.
        // -------------------------------------------------------------------
        angel_task_data* atd_p = getAngelTaskDataFromSTCB();

        if (atd_p == NULL) {
            atd_p = initializeAngelTaskData();

            if (reregistration == FALSE) {
                recovery_p->drm_cleanup_tgoo = TRUE;
            }
        }

        if (atd_p != NULL) {
            // ---------------------------------------------------------------
            // See if we're allowed to load up the server authorized
            // functions module (BBGZSAFM).
            // ---------------------------------------------------------------
            if (isServerAuthorizedForModule("BBGZSAFM")) {
                // -----------------------------------------------------------
                // Try to load up the server authorized functions module.
                // If this doesn't work, we'll abend unless BBGZSAFM is not
                // APF authorized in which case we will allow falling back
                // to unauthorized.
                // -----------------------------------------------------------
                bbgzasvt_header* original_safm_p =
                    load_safm(local_server_authorized_function_module_name);
                if ((int) original_safm_p != -1) {
                    bbgzasvt_header* safm_p = (bbgzasvt_header*)
                        (((int)original_safm_p) & 0x7FFFFFFE);
                    recovery_p->drm_safm_entrypt_p = original_safm_p;
                    recovery_p->drm_cleanup_safm_unload = 1;

                    // -----------------------------------------------------------
                    // Copy the module to local storage so that we can turn on
                    // the bits that say which functions are available for the
                    // server to use.
                    // -----------------------------------------------------------
                    int safm_size = sizeof(*safm_p) +
                        ((safm_p->num_entries) * sizeof(bbgzasve)) +
                        strlen(BBGZASVT_EYE_END);

                    int obtainReturnCode = 0;
                    bbgzasvt_header* local_safm_p = storageObtain(safm_size,
                                                                  LOCAL_SAFM_SUBPOOL,
                                                                  LOCAL_SAFM_KEY,
                                                                  &obtainReturnCode);
                    if (local_safm_p != NULL) {
                        recovery_p->drm_safm_func_table_p = local_safm_p;
                        recovery_p->drm_safm_func_table_size = safm_size;
                        recovery_p->drm_cleanup_safm = 1;
                        recovery_p->drm_cleanup_safm_unload = 0;

                        memcpy(local_safm_p, safm_p, safm_size);

                        // -------------------------------------------------------
                        // For each function, do the RACF check and turn on its
                        // available bit if it's available.
                        // -------------------------------------------------------
                        perform_ASVT_authorization_checks(local_safm_p);

                        // -------------------------------------------------------
                        // Set the new SAFM information into the angel process
                        // data.  If there was previous information in there, it
                        // was the deregistering task (or the task charged with
                        // cleanup) who cleaned these up.
                        // -------------------------------------------------------
                        apd_p->safm_function_table_p = local_safm_p;
                        apd_p->safm_function_table_size = safm_size;
                        apd_p->safm_entry_p = original_safm_p;

                        // ---------------------------------------------------------------
                        // See if we're allowed to load up the server common
                        // authorized functions module (BBGZCAFM).  If not, just don't
                        // load it.  We won't export any client functions.
                        // ---------------------------------------------------------------
                        if (isServerAuthorizedForModule("BBGZSCFM")) {
                            char* scfmName = strdup(local_server_authorized_function_module_name);
                            if (scfmName != NULL) {
                                int scfmNameLen = strlen(scfmName);
                                *(scfmName + (scfmNameLen - 3)) = 'c';
                                loadhfs_details* scfmDetails = load_scfm(scfmName);
                                if (scfmDetails != NULL) {
                                    bbgzasvt_header* scfm_p = (bbgzasvt_header*)
                                        ((int)(scfmDetails->entry_p) & 0x7FFFFFFE);

                                    int scfm_size = sizeof(*scfm_p) +
                                        ((scfm_p->num_entries) * sizeof(bbgzasve)) +
                                         strlen(BBGZASVT_EYE_END);

                                    int obtainReturnCode = 0;
                                    bbgzasvt_header* local_scfm_p = storageObtain(scfm_size,
                                                                                  LOCAL_SCFM_SUBPOOL,
                                                                                  LOCAL_SCFM_KEY,
                                                                                  &obtainReturnCode);
                                    if (local_scfm_p != NULL) {
                                        memcpy(local_scfm_p, scfm_p, scfm_size);

                                        perform_ASVT_authorization_checks(local_scfm_p);

                                        apd_p->scfmModuleLength = scfmDetails->mod_len;
                                        apd_p->scfmModule_p = scfmDetails->mod_p;
                                        apd_p->scfmModuleEntryPoint_p = scfmDetails->entry_p;
                                        memcpy(apd_p->scfmModuleDeleteToken, scfmDetails->delete_token, sizeof(apd_p->scfmModuleDeleteToken));

                                        apd_p->scfm_function_table_p = local_scfm_p;
                                        apd_p->scfm_function_table_size = scfm_size;
                                    }

                                    free(scfmDetails);
                                    scfmDetails = NULL;
                                }

                                free(scfmName);
                            }
                        }

                        // -------------------------------------------------------
                        // Grab an ENQ whose name indicates that we are
                        // officially connected to the angel.  The angel uses
                        // this ENQ during stop processing to determine that
                        // there are servers still connected to it.  Note that
                        // the PC router also incremented a use count in the
                        // ARMV, but this does not help the angel know who is
                        // connected, just that someone is connected.
                        // -------------------------------------------------------
                        if (reregistration == FALSE) {
                            char enq_rname_buf[BBGZ_ENQ_MAX_RNAME_LEN];
                            if (sgoo_p->bbgzsgoo_angelAnchor_p == NULL) {
                            snprintf(enq_rname_buf, sizeof(enq_rname_buf),
                                     ANGEL_PROCESS_SERVER_ENQ_RNAME_PATTERN,
                                     *(long long*)(&(assb_p->assbstkn)));
                            } else {
                                snprintf(enq_rname_buf, sizeof(enq_rname_buf),
                                         ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_PATTERN,
                                         *(long long*)(&(assb_p->assbstkn)),
                                         sgoo_p->bbgzsgoo_angelAnchor_p->name);
                            }

                            get_enq_exclusive_system(
                                BBGZ_ENQ_QNAME,
                                enq_rname_buf,
                                NULL,
                                &(apd_p->server_enq_token));
                        }

                        // -------------------------------------------------------
                        // Call the server to let it initialize any process wide
                        // authorized control blocks that it needs to.
                        // -------------------------------------------------------
                        void* metalc_env_p = getenvfromR12();
                        setenvintoR12(NULL);
                        int server_init_rc = 0;
                        if (local_safm_p->process_initialization_routine_ptr != NULL) {
                            server_init_rc = local_safm_p->process_initialization_routine_ptr(NO_CLIENT_TOKEN);
                        }
                        // TODO: Check return code
                        setenvintoR12(metalc_env_p);

                        // -------------------------------------------------------
                        // If we're re-registering, mark the angel process data
                        // as registered. This will allow invokers to get in and
                        // drive requests.  Note that if we can't mark the angel
                        // process data, this is really bad because we've
                        // already clobbered several fields in the angel process
                        // data.  We are relying on checks in the fixed shim
                        // module that won't let us get this far if we're
                        // already registered and/or there are invoking threads,
                        // and on an ENQ which only lets one task try to
                        // register at a time for this address space.
                        // -------------------------------------------------------
                        unsigned char apd_activated = TRUE;
                        if (reregistration == TRUE) {
                            AngelProcessInvokeCount_t old_invoke_count;
                            cs_t new_invoke_count;

                            memcpy(&old_invoke_count, &(apd_p->invokecount), sizeof(old_invoke_count));
                            old_invoke_count.curResmgrCount = 0; // Assert RESMGRs are not running.
                            old_invoke_count.allTasksCleanedUp = 1; // Assert cleanup was complete.
                            old_invoke_count.deregistered = 1; // Assert we are deregistered.
                            new_invoke_count = 0;

                            if (__cs1(&old_invoke_count, &(apd_p->invokecount), &new_invoke_count) != 0) {
                                apd_activated = FALSE;
                            }
                        }

                        // -------------------------------------------------------
                        // Since the angel process data is sticking around,
                        // don't free the key 2 metal C environment that we
                        // created.
                        // -------------------------------------------------------
                        if (apd_activated == TRUE) {
                            recovery_p->drm_as_resmgr_token = 0;
                            recovery_p->drm_task_resmgr_token = 0;
                            recovery_p->drm_cleanup_tgoo = FALSE;
                            recovery_p->drm_cleanup_safm = 0;

                            register_rc = ANGEL_REGISTER_OK;
                        } else {
                            apd_p->safm_function_table_p = NULL;
                            cleanup_safm(recovery_p); // Will also unset cleanup bit

                            if (recovery_p->drm_cleanup_tgoo == TRUE) {
                                recovery_p->drm_cleanup_tgoo = FALSE;
                                destroyAngelTaskData();
                            }

                            if (reregistration == FALSE) {
                                deleteAngelProcessDataNameTokens(apd_p, NULL);
                                destroyAngelProcessData(apd_p);
                                destroyResourceManagersForServer(recovery_p);
                            }

                            register_rc = ANGEL_REGISTER_DRM_MARK_PGOO;
                        }
                    } else {
                        cleanup_safm(recovery_p); // Will also unset cleanup bit

                        if (recovery_p->drm_cleanup_tgoo == TRUE) {
                            recovery_p->drm_cleanup_tgoo = FALSE;
                            destroyAngelTaskData();
                        }

                        if (reregistration == FALSE) {
                            deleteAngelProcessDataNameTokens(apd_p, NULL);
                            destroyAngelProcessData(apd_p);
                            destroyResourceManagersForServer(recovery_p);
                        }

                        register_rc = ANGEL_REGISTER_DRM_ALLOCATE_ASVT;
                    }
                } else {
                    if (recovery_p->drm_cleanup_tgoo == TRUE) {
                        recovery_p->drm_cleanup_tgoo = FALSE;
                        destroyAngelTaskData();
                    }

                    if (reregistration == FALSE) {
                        deleteAngelProcessDataNameTokens(apd_p, NULL);
                        destroyAngelProcessData(apd_p);
                        destroyResourceManagersForServer(recovery_p);
                    }

                    register_rc = ANGEL_REGISTER_DRM_SAFM_NOT_APF_AUTHORIZED;
                }
            } else {
                if (recovery_p->drm_cleanup_tgoo == TRUE) {
                    recovery_p->drm_cleanup_tgoo = FALSE;
                    destroyAngelTaskData();
                }
                if (reregistration == FALSE) {
                    deleteAngelProcessDataNameTokens(apd_p, NULL);
                    destroyAngelProcessData(apd_p);
                    destroyResourceManagersForServer(recovery_p);
                }

                register_rc = ANGEL_REGISTER_DRM_NOT_AUTHORIZED_BBGZSAFM;
            }
        } else {
            if (reregistration == FALSE) {
                deleteAngelProcessDataNameTokens(apd_p, NULL);
                destroyAngelProcessData(apd_p);
                destroyResourceManagersForServer(recovery_p);
            }

            register_rc = ANGEL_REGISTER_DRM_ALLOCATE_TGOO;
        }
    } else {
        if (reregistration == FALSE) {
            destroyResourceManagersForServer(recovery_p);
        }

        if (ipt_has_not_moved == FALSE) {
            register_rc = ANGEL_REGISTER_DRM_IPT_MOVED;
        } else {
            register_rc = ANGEL_REGISTER_DRM_ALLOCATE_PGOO;
        }
    }

    return register_rc;
}

/**
 * Resets the invoking bit in the TGOO and notifies the deregistering task
 * if necessary.
 */
static int resetInvokingBit(angel_task_data* atd_p, angel_process_data* apd_p, angel_server_pc_recovery* recovery_p) {
    int rc = 0;

    // --------------------------------------------------------------
    // Reset the invoking bit.  If someone called deregister while
    // we were invoking, the 'noMoreInvoke' bit will be set, and
    // we have to notify the deregistering task to let it know that
    // we are finished.
    // --------------------------------------------------------------
    AngelCSFlagArea_t oldFlags, newFlags;
    memcpy(&oldFlags, &(atd_p->invokeFlags), sizeof(oldFlags));
    for (int cs_rc = -1; cs_rc != 0;) {
        newFlags = oldFlags;
        newFlags.invoking = 0;
        cs_rc = __cs1(&oldFlags, &(atd_p->invokeFlags), &newFlags);
    }
    recovery_p->drm_reset_invoking_bit = 0;

    if (oldFlags.noMoreInvoke == 1) {
        InvokeCompleteNotification_t oldArea, newArea;
        memcpy(&oldArea, &(apd_p->threadCompleteNotificationArea), sizeof(oldArea));
        for (int cs_rc = -1; cs_rc != 0;) {
            newArea.completedThreadCount = oldArea.completedThreadCount + 1;
            newArea.completedThreadPET_p = NULL;
            cs_rc = __cdsg(&oldArea, &(apd_p->threadCompleteNotificationArea), &newArea);
        }

        if (oldArea.completedThreadPET_p != NULL) {
            iea_return_code releaseRc;
            iea_auth_type authType = IEA_AUTHORIZED;
            iea_release_code releaseCode;
            memset(releaseCode, 0, sizeof(releaseCode));
            unsigned char currentKey = switchToKey0();
            iea4rls(&releaseRc, authType, oldArea.completedThreadPET_p, releaseCode);
            switchToSavedKey(currentKey);
            if (releaseRc != 0) {
                // -----------------------------------------------------------
                // We could not release the deregister task... we're probably
                // hung now.  Leave a couple of bread crumbs for later.
                // -----------------------------------------------------------
                rc = 1;

                AngelProcessInvokeCount_t oldInvokeCount, newInvokeCount;
                memcpy(&oldInvokeCount, &(apd_p->invokecount), sizeof(oldInvokeCount));
                for (int cs_rc = -1; cs_rc != 0;) {
                    newInvokeCount = oldInvokeCount;
                    newInvokeCount.releaseFailed = 1;
                    cs_rc = __cs1(&oldInvokeCount, &(apd_p->invokecount), &newInvokeCount);
                }
            }
        }
    }

    return rc;
}


int dynamicReplaceablePC_Invoke(unsigned int function_index,
                                unsigned int arg_struct_size,
                                void* arg_struct_p,
                                angel_process_data* apd_p,
                                angel_server_pc_recovery* recovery_p) {
  int invoke_rc = ANGEL_INVOKE_DRM_FAIL;

  // -------------------------------------------------------------------------
  // Make sure we're a descendant of the IPT.
  // -------------------------------------------------------------------------
  if (isIPTinOurParentTCBchain(apd_p) == FALSE) {
      return ANGEL_INVOKE_DRM_NOT_IN_IPT_CHAIN;
  }

  // -------------------------------------------------------------------------
  // Lets make sure that we can access the SAFM to do the invoke.  We check
  // the process data to make sure we're not shutting down, then we set the
  // invoking bit in our task data.  If we don't have task data yet, we need
  // to create it, but we then need to check the process data again before
  // using it in case we are shutting down.
  // -------------------------------------------------------------------------
  unsigned char allowedToInvoke = (apd_p->invokecount.deregistered == 0);
  angel_task_data* atd_p = getAngelTaskDataFromSTCB();
  if ((atd_p == NULL) && (allowedToInvoke)) {
      atd_p = initializeAngelTaskData();
      allowedToInvoke = (apd_p->invokecount.deregistered == 0);

      if ((allowedToInvoke == FALSE) && (atd_p != NULL)) {
          // -----------------------------------------------------------------
          // No need for this task data since the server is deregistering.  We
          // need to get the local lock before ripping out the STCBBCBA
          // because otherwise the TCB scanning code might find this task and
          // be looking at the task data while the exit linkage runs and frees
          // the task data.
          // -----------------------------------------------------------------
          recovery_p->release_local_lock_save_area_p = __malloc31(LOCAL_LOCK_SAVE_AREA_SIZE);
          if (recovery_p->release_local_lock_save_area_p != NULL) {
              int local_lock_rc = getLocalLock(recovery_p->release_local_lock_save_area_p);
              recovery_p->release_local_lock = (local_lock_rc == 0);

              if ((local_lock_rc == 0) || (local_lock_rc == 4)) {
                  destroyAngelTaskData();
              }

              if (recovery_p->release_local_lock != 0) {
                  recovery_p->release_local_lock = 0;
                  releaseLocalLock(recovery_p->release_local_lock_save_area_p);
              }

              void* tempStg_p = recovery_p->release_local_lock_save_area_p;
              recovery_p->release_local_lock_save_area_p = NULL;
              free(tempStg_p);
          }
      }
  }

  if (allowedToInvoke) {
      if (atd_p != NULL) {
          AngelCSFlagArea_t oldFlags, newFlags;
          memcpy(&oldFlags, &(atd_p->invokeFlags), sizeof(oldFlags));
          for (int cs_rc = -1; cs_rc != 0;) {
              if (oldFlags.noMoreInvoke == 0) {
                  newFlags = oldFlags;
                  newFlags.invoking = 1;
                  cs_rc = __cs1(&oldFlags, &(atd_p->invokeFlags), &newFlags);
              } else {
                  cs_rc = 0; // Don't bother -- can't invoke anyway.
              }
          }

          if (oldFlags.noMoreInvoke == 0) {
              recovery_p->drm_reset_invoking_bit = 1;

              // --------------------------------------------------------------
              // Find the SAFM pointer off of the angel process data, and find
              // the index.
              // ---------------------------------------------------------------------
              bbgzasvt_header* safm_header_p = (bbgzasvt_header*)(apd_p->safm_function_table_p);

              if (function_index < safm_header_p->num_entries) {
                  bbgzasve* first_entry_p = (bbgzasve*)
                  (((char*)safm_header_p) + sizeof(*safm_header_p));
                  bbgzasve* desired_entry_p = first_entry_p + function_index;

                  if (desired_entry_p->bbgzasve_runtime_bits.authorized_to_use == 1) {
                      // -------------------------------------------------------------
                      // Copy the caller's structure argument (still in key 8) to
                      // local storage.
                      // -------------------------------------------------------------
                      if ((arg_struct_size != 0) && (arg_struct_size <= INVOKE_ARG_STRUCT_MAX_BYTES)) {
                          char reasonableSizedInvokeParmsBuffer[INVOKE_ARG_STRUCT_REASONABLE_SIZE];
                          void* localInvokeArgStruct_p = NULL;
                          if (arg_struct_size <= INVOKE_ARG_STRUCT_REASONABLE_SIZE) {
                              localInvokeArgStruct_p = reasonableSizedInvokeParmsBuffer;
                          } else {
                              recovery_p->drm_invoke_parm_struct_p = malloc(arg_struct_size);
                              localInvokeArgStruct_p = recovery_p->drm_invoke_parm_struct_p;
                          }

                          if (localInvokeArgStruct_p != NULL) {
                              memcpy_sk(localInvokeArgStruct_p, arg_struct_p, arg_struct_size, 8);

                              // ----------------------------------------------
                              // Remove our metal C environment so that the
                              // server does not accidentally use it.
                              // ----------------------------------------------
                              void* metalc_env_p = getenvfromR12();
                              setenvintoR12(NULL);

                              // ----------------------------------------------
                              // Invoke the service.
                              // ----------------------------------------------
                              if (safm_header_p->setupEnvironmentAndCallInvokableService != NULL) {
                                  safm_header_p->setupEnvironmentAndCallInvokableService(desired_entry_p->bbgzasve_fcn_ptr, localInvokeArgStruct_p, NO_CLIENT_TOKEN);
                              } else {
                                  desired_entry_p->bbgzasve_fcn_ptr(localInvokeArgStruct_p);
                              }

                              // ----------------------------------------------
                              // Put back our metal C environment and clean up.
                              // ----------------------------------------------
                              setenvintoR12(metalc_env_p);
                              if (recovery_p->drm_invoke_parm_struct_p != NULL) {
                                  void* temp_p = recovery_p->drm_invoke_parm_struct_p;
                                  recovery_p->drm_invoke_parm_struct_p = NULL;
                                  free(temp_p);
                              }
                              invoke_rc = ANGEL_INVOKE_OK;
                          } else {
                              invoke_rc = ANGEL_INVOKE_DRM_NO_STORAGE_PARMS;
                          }
                      } else {
                          invoke_rc = ANGEL_INVOKE_DRM_BAD_PARM_SIZE;
                      }
                  } else {
                      invoke_rc = ANGEL_INVOKE_DRM_UNAUTH_FUNC;
                  }
              } else {
                  invoke_rc = ANGEL_INVOKE_DRM_UNREG_FUNC;
              }

              // --------------------------------------------------------------
              // Reset the invoking bit.  If someone called deregister while
              // we were invoking, the 'noMoreInvoke' bit will be set, and
              // we have to notify the deregistering task to let it know that
              // we are finished.
              // --------------------------------------------------------------
              resetInvokingBit(atd_p, apd_p, recovery_p);
          } else {
              invoke_rc = ANGEL_INVOKE_DRM_UNREGISTERED;
          }
      } else {
          invoke_rc = ANGEL_INVOKE_DRM_NO_TGOO;
      }
  } else {
      invoke_rc = ANGEL_INVOKE_DRM_UNREGISTERED;
  }

  return invoke_rc;
}

/** Parameter area for markDoNotInvokeBitInTaskData function. */
typedef struct markDoNotInvokeBitInTaskDataParms {
    unsigned long long token; //!< Scan token.
    unsigned int invokingTcbCount; //!< Count of TCBs currently invoking.
} MarkDoNotInvokeBitInTaskDataParms_t;

/**
 * Callback function for callFunctionForEachTCB which marks the 'do not invoke'
 * bit for each task.  Note that the MVS local lock is held during this
 * function call.
 *
 * @param token_p The token for this scan.
 * @param flags_p Flags pertaining to the state of the scan.
 * @param tcb_p The current TCB being processed.
 * @param parm_p A pointer to our parameter area.
 *
 * @return Returns 0 if successful and we want the scan to continue.
 */
static int markDoNotInvokeBitInTaskData(unsigned long long* token_p, ScanTCBFlags_t* flags_p, void* tcb_p, void* voidParm_p) {
    int rc = 0;
    MarkDoNotInvokeBitInTaskDataParms_t* parm_p = (MarkDoNotInvokeBitInTaskDataParms_t*)voidParm_p;

    // If the scan is starting, fill in the token in the parm struct.
    if (flags_p->startScan == 1) {
        if (parm_p->token == 0) {
            parm_p->token = *token_p;
        } else {
            rc = -1;
        }
    }

    // Process the current TCB.
    if (tcb_p != NULL) {
        angel_task_data* atd_p = getAngelTaskDataFromAlternateTCB(tcb_p);

        if (atd_p != NULL) {
            // Set the 'doNotInvoke' bit in the task area.
            AngelCSFlagArea_t oldArea, newArea;
            memcpy(&oldArea, &(atd_p->invokeFlags), sizeof(oldArea));
            for (int cs_rc = -1; cs_rc != 0;) {
                if (oldArea.noMoreInvoke == 0) {
                    newArea = oldArea;
                    newArea.noMoreInvoke = 1;
                    cs_rc = __cs1(&oldArea, &(atd_p->invokeFlags), &newArea);
                } else {
                    cs_rc = 0;
                }
            }

            // If the invoking bit was also set, we need to increment the
            // count of tasks we need to wait for.
            if ((oldArea.noMoreInvoke == 0) && (newArea.invoking == 1)) {
                parm_p->invokingTcbCount = parm_p->invokingTcbCount + 1;
            }
        }
    }

    return rc;
}

/**
 * Callback function for callFunctionForEachTCB which cleans up the server task
 * data for each task.  Note that the MVS local lock is held during this
 * function call.
 *
 * @param token_p The token for this scan.
 * @param flags_p Flags pertaining to the state of the scan.
 * @param tcb_p The current TCB being processed.
 * @param apd_p A pointer to the angel process data.
 *
 * @return Returns 0 if successful and we want the scan to continue.
 */
static int cleanupServerTaskData(unsigned long long* token_p, ScanTCBFlags_t* flags_p, void* tcb_p, void* voidAPD_p) {
    int rc = 0;
    angel_process_data* apd_p = (angel_process_data*)voidAPD_p;

    // Process the current TCB.
    if (tcb_p != NULL) {
        angel_task_data* atd_p = getAngelTaskDataFromAlternateTCB(tcb_p);
        if (atd_p != NULL) {
            // ------------------------------------------------------------
            // Set the 'cleanup complete' bit in the task area.  Only process
            // those tasks for which we set the 'noMoreInvoke' bit.  It's
            // possible we will find other tasks in our scan that had started
            // to create their task data while we were starting deregister,
            // and those tasks are responsible for cleaning up their own mess.
            // ------------------------------------------------------------
            AngelCSFlagArea_t oldArea, newArea;
            memcpy(&oldArea, &(atd_p->invokeFlags), sizeof(oldArea));
            for (int cs_rc = -1; cs_rc != 0;) {
                if ((oldArea.noMoreInvoke == 1) && (oldArea.droveServerCleanup == 0)) {
                    newArea = oldArea;
                    newArea.droveServerCleanup = 1;
                    cs_rc = __cs1(&oldArea, &(atd_p->invokeFlags), &newArea);

                    // -------------------------------------------------------
                    // Invoke the cleanup routine if we were able to set the bit.
                    // If the cleanup routine abends, the TCB scan will fail.
                    // We can't establish our own ESTAE because we can't get
                    // storage below the bar with the local lock held.  This
                    // could change if the caller passed us storage and if
                    // the ESTAE routines changes to take storage as a parm.
                    // -------------------------------------------------------
                    if (cs_rc == 0) {
                        bbgzasvt_header* safm_header_p = (bbgzasvt_header*)(apd_p->safm_function_table_p);
                        if ((safm_header_p != NULL) && (safm_header_p->task_cleanup_routine_ptr != NULL)) {
                            safm_header_p->task_cleanup_routine_ptr(tcb_p);
                        }
                    }
                } else {
                    cs_rc = 0;
                }
            }
        }
    }

    return rc;
}

/** Maximum attempts to scan the TCB chain and clean up the server task data. */
#define MAX_SERVER_TASK_CLEANUP_TRIES 25

int dynamicReplaceablePC_Deregister(angel_process_data* apd_p, angel_server_pc_recovery* recovery_p) {
    int deregister_rc = ANGEL_DEREGISTER_DRM_FAIL;

    // -----------------------------------------------------------------------
    // Get the SAFM information out of the angel process data before we mark
    // the data as deregistering, because once we mark it as deregistered,
    // it could be re-registered immediately.
    // -----------------------------------------------------------------------
    recovery_p->drm_safm_entrypt_p = apd_p->safm_entry_p;
    recovery_p->drm_safm_func_table_p = apd_p->safm_function_table_p;
    recovery_p->drm_safm_func_table_size = apd_p->safm_function_table_size;

    // -----------------------------------------------------------------------
    // Mark the angel process data as deregistering.  This is a clue to
    // other tasks that it is not OK to invoke into the server module.
    // These tasks should fail their requests.
    // -----------------------------------------------------------------------
    AngelProcessInvokeCount_t oldInvokeCount, newInvokeCount;
    memcpy(&oldInvokeCount, &(apd_p->invokecount), sizeof(oldInvokeCount));
    for (int cs_rc = -1; cs_rc != 0;) {
        if (oldInvokeCount.deregistered == 0) {
            newInvokeCount = oldInvokeCount;
            newInvokeCount.deregistered = 1;
            cs_rc = __cs1(&oldInvokeCount, &(apd_p->invokecount), &newInvokeCount);
        } else {
            // Don't bother -- already deregistered
            cs_rc = 0;
        }
    }

    if (oldInvokeCount.deregistered == 0) {
        // -------------------------------------------------------------------
        // Mark the angel process data indicating no new binds are allowed.
        // -------------------------------------------------------------------
        AngelClientBindCount_t oldBindCount, newBindCount;
        memcpy(&oldBindCount, &(apd_p->clientBindArea), sizeof(oldBindCount));
        for (int csRC = -1; csRC != 0;) {
            memcpy(&newBindCount, &oldBindCount, sizeof(newBindCount));
            newBindCount.noMoreBinds = 1;
            csRC = __cdsg(&oldBindCount, &(apd_p->clientBindArea), &newBindCount);
        }

        // -------------------------------------------------------------------
        // Mark all of the client binds indicating that the bind count can
        // only be decremented.  Continue to allow invokes for existing
        // binds so that clients can clean up.
        // -------------------------------------------------------------------
        char clientBindEnqName[255];
        snprintf(clientBindEnqName, sizeof(clientBindEnqName), ANGEL_PROCESS_CLIENT_BIND_LIST_ENQ_PATTERN, *((long long*)(&(apd_p->clientBindArea.serverStoken))));
        enqtoken clientBindEnqToken; // TODO: Enq token in recovery area
        get_enq_exclusive_system(BBGZ_ENQ_QNAME, clientBindEnqName, NULL, &clientBindEnqToken);

        AngelClientBindDataNode_t* curBindDataNode_p = apd_p->clientBindHead_p;
        AngelClientBindDataNode_t* prevBindDataNode_p = NULL;
        while (curBindDataNode_p != NULL) {
            AngelClientBindData_t* curBindData_p = curBindDataNode_p->data_p;
            AngelClientDataBindCount_t oldCount, newCount;
            memcpy(&oldCount, &(curBindData_p->bindCount), sizeof(oldCount));
            for (int csRC = -1; csRC != 0;) {
                memcpy(&newCount, &oldCount, sizeof(newCount));
                newCount.serverIsEnding = 1;
                csRC = __cds1(&oldCount, &(curBindData_p->bindCount), &newCount);
            }

            // ---------------------------------------------------------------
            // Removed code that removes the bind from the bind list.  We
            // don't want to remove the bind until the client has had a chance
            // to clean up.
            // ---------------------------------------------------------------
            prevBindDataNode_p = curBindDataNode_p;
            curBindDataNode_p = curBindDataNode_p->next_p;
        }

        // -------------------------------------------------------------------
        // If we've cleaned up all of the clients, set the bit in the flag
        // word that says so.
        // -------------------------------------------------------------------
        if (newBindCount.count == 0) {
            memcpy(&oldInvokeCount, &(apd_p->invokecount), sizeof(oldInvokeCount));
            for (int cs_rc = -1; cs_rc != 0;) {
                newInvokeCount = oldInvokeCount;
                newInvokeCount.allClientsUnbound = 1;
                cs_rc = __cs1(&oldInvokeCount, &(apd_p->invokecount), &newInvokeCount);
            }

            // ---------------------------------------------------------------
            // Delete the SCFM.  Don't drive the cleanup routine since we're
            // running in the server process (or in MASTER for AS RESMGR).
            // ---------------------------------------------------------------
            recovery_p->drm_cleanup_scfm = 1;
            cleanupSCFM(apd_p);
        }

        release_enq(&clientBindEnqToken);

        // -------------------------------------------------------------------
        // Start the cleanup process by running the TCB chain and marking all
        // the tasks as ending.  This is a signal that the task should not
        // invoke into the server code anymore.  We will also keep a count of
        // the tasks that we've marked and are currently invoking.  This will
        // be the number of tasks that are responsible for telling us when
        // they are finished invoking.  Since tasks can end but still be in
        // the TCB chain while we have the local lock, we'll need to be able
        // to retry a limited number of times in case we take a protection
        // exception reading the angel task data for the task we're currently
        // processing.
        // -------------------------------------------------------------------
        MarkDoNotInvokeBitInTaskDataParms_t doNotInvokeParmArea;
        memset(&doNotInvokeParmArea, 0, sizeof(doNotInvokeParmArea));
        unsigned char markTaskEndingComplete = FALSE;
        for (int x = 0; ((x < MAX_SERVER_TASK_CLEANUP_TRIES) && (markTaskEndingComplete == FALSE)); x++) {
            int scanRC = callFunctionForEachTCB(markDoNotInvokeBitInTaskData, &doNotInvokeParmArea, TRUE);
            markTaskEndingComplete = (scanRC == 0);
        }

        if (markTaskEndingComplete == TRUE) {
            // ---------------------------------------------------------------
            // Now that the 'doNotInvoke' bits are set, we check how many
            // tasks are currently in invoke.  If there are any, we'll wait
            // for them to finish before proceeding.
            // ---------------------------------------------------------------
            InvokeCompleteNotification_t oldNotificationArea, newNotificationArea;
            iea_PEToken pauseToken;
            unsigned char allocatedPauseToken = FALSE, pauseFailure = FALSE;
            memset(pauseToken, 0, sizeof(pauseToken));
            for (int taskCount = doNotInvokeParmArea.invokingTcbCount; ((taskCount > 0) && (pauseFailure == FALSE));) {
                memcpy(&oldNotificationArea, &(apd_p->threadCompleteNotificationArea), sizeof(oldNotificationArea));
                if (oldNotificationArea.completedThreadCount > 0) {
                    memset(&newNotificationArea, 0, sizeof(newNotificationArea));
                    if (__cdsg(&oldNotificationArea, &(apd_p->threadCompleteNotificationArea), &newNotificationArea) == 0) {
                        taskCount = taskCount - oldNotificationArea.completedThreadCount;
                    }
                } else {
                    // -------------------------------------------------------
                    // Get a pause element.  We obtain the first one explicitly,
                    // the remaining ones are provided on release.
                    // -------------------------------------------------------
                    if (allocatedPauseToken == FALSE) {
                        iea_return_code petRc;
                        iea_auth_type petAuthType = IEA_AUTHORIZED;
                        unsigned char currentKey = switchToKey0();
                        iea4ape(&petRc, petAuthType, pauseToken);
                        switchToSavedKey(currentKey);
                        allocatedPauseToken = (petRc == 0);
                    }

                    if (allocatedPauseToken == TRUE) {
                        newNotificationArea.completedThreadCount = 0;
                        newNotificationArea.completedThreadPET_p = pauseToken;
                        if (__cdsg(&oldNotificationArea, &(apd_p->threadCompleteNotificationArea), &newNotificationArea) == 0) {
                            // ------------------------------------------------
                            // Pause, waiting for a task to finish invoke.
                            // ------------------------------------------------
                            iea_return_code pauseRc;
                            iea_auth_type pauseAuthType = IEA_AUTHORIZED;
                            iea_release_code releaseCode;
                            unsigned char currentKey = switchToKey0();
                            iea4pse(&pauseRc, pauseAuthType, pauseToken, pauseToken, releaseCode);
                            switchToSavedKey(currentKey);
                            if (pauseRc != 0) {
                                pauseFailure = TRUE;
                            }
                        }
                    } else {
                        pauseFailure = TRUE;
                    }
                }
            }

            if (allocatedPauseToken == TRUE) {
                iea_return_code deallocPauseTokenRC;
                iea_auth_type deallocAuthType = IEA_AUTHORIZED;
                unsigned char currentKey = switchToKey0();
                iea4dpe(&deallocPauseTokenRC, deallocAuthType, pauseToken);
                switchToSavedKey(currentKey);
            }

            // ---------------------------------------------------------------
            // Now that all of the threads are out of invoke, go ahead and
            // clean up the threads that we marked in the first TCB scan.  Try
            // multiple times if the scan fails.  We'll clean up as many
            // tasks as we can.
            // ---------------------------------------------------------------
            if (pauseFailure == FALSE) {
                unsigned char taskCleanupComplete = FALSE;
                for (int x = 0; ((x < MAX_SERVER_TASK_CLEANUP_TRIES) && (taskCleanupComplete == FALSE)); x++) {
                    int scanRC = callFunctionForEachTCB(cleanupServerTaskData, apd_p, TRUE);
                    taskCleanupComplete = (scanRC == 0);
                }

                // ---------------------------------------------------------------
                // Finally, mark the process data as having completed server task
                // cleanup.  Here we are competing with tasks running the server
                // task RESMGR.  If there are no RESMGRs running, and cleanup is
                // complete, go ahead and clean up the SAFM.
                // ---------------------------------------------------------------
                memcpy(&oldInvokeCount, &(apd_p->invokecount), sizeof(oldInvokeCount));
                for (int cs_rc = -1; cs_rc != 0;) {
                    newInvokeCount = oldInvokeCount;
                    newInvokeCount.allTasksCleanedUp = 1;
                    cs_rc = __cs1(&oldInvokeCount, &(apd_p->invokecount), &newInvokeCount);
                }

                if (oldInvokeCount.curResmgrCount == 0) {
                    bbgzasvt_header* safm_header_p = (bbgzasvt_header*)(recovery_p->drm_safm_func_table_p);
                    if (safm_header_p->process_cleanup_routine_ptr != NULL) {
                        safm_header_p->process_cleanup_routine_ptr(NO_CLIENT_TOKEN);
                    }

                    recovery_p->drm_cleanup_safm = 1;
                    cleanup_safm(recovery_p);

                    if (oldInvokeCount.allClientsUnbound == 1) {
                        deregister_rc = ANGEL_DEREGISTER_OK;
                    } else {
                        deregister_rc = ANGEL_DEREGISTER_OK_PENDING;
                    }
                } else {
                    deregister_rc = ANGEL_DEREGISTER_OK_PENDING;
                }
            } else {
                // Pause failed... just stop.  We can't clean up.
                deregister_rc = ANGEL_DEREGISTER_UNDEFINED;

                memcpy(&oldInvokeCount, &(apd_p->invokecount), sizeof(oldInvokeCount));
                for (int cs_rc = -1; cs_rc != 0;) {
                    newInvokeCount = oldInvokeCount;
                    newInvokeCount.pauseFailed = 1;
                    cs_rc = __cs1(&oldInvokeCount, &(apd_p->invokecount), &newInvokeCount);
                }
            }
        } else {
            // Could not set the 'thread ending' bits...  Just stop.  We can't clean up.
            deregister_rc = ANGEL_DEREGISTER_UNDEFINED;

            memcpy(&oldInvokeCount, &(apd_p->invokecount), sizeof(oldInvokeCount));
            for (int cs_rc = -1; cs_rc != 0;) {
                newInvokeCount = oldInvokeCount;
                newInvokeCount.tcbScanFailed = 1;
                cs_rc = __cs1(&oldInvokeCount, &(apd_p->invokecount), &newInvokeCount);
            }
        }
    } else {
        deregister_rc = ANGEL_DEREGISTER_DRM_ALR_DEREG;
    }

    return deregister_rc;
}

static int getAbendCodeFromSdwa(struct sdwa* sdwa_p) {
    int abendCode = 0;
    for (int i = 0; i < sizeof(sdwa_p->sdwacmpc); abendCode = abendCode << 8 | sdwa_p->sdwacmpc[i++]);
    abendCode >>= 12;
    return abendCode;
}

static int abendCodeShouldBeDumped(sdwa* sdwa_p) {

    int takeDump = 1;
    if (sdwa_p != NULL) {
        switch (getAbendCodeFromSdwa(sdwa_p)) {
            case 0x222:
            case 0x322:
            case 0x422:
            case 0x522:
            case 0x622:
            case 0x722:
            case 0xA22:
                takeDump = 0;
                break;
        }
    }
    return takeDump;
}

static int okToDump(sdwa* sdwa_p) {
    int takeDump = 0;
    if ((sdwa_p != NULL) &&                            // sdwa esists
        ((sdwa_p->sdwaerrc & sdwaeas)  != sdwaeas) &&  // Dump not taken by lower level ESTAE
        ((sdwa_p->sdwaerrd & sdwacts)  != sdwacts) &&  // Abend not driven in job step task
        ((sdwa_p->sdwaerrd & sdwamabd) != sdwamabd)) { // Abend not driven in ancestor task
        if (abendCodeShouldBeDumped(sdwa_p)) {
            takeDump = 1;
       }
    }
    return takeDump;
}


void dynamicReplaceableARR(sdwa* sdwa_p, angel_server_pc_recovery* recovery_p) {
    // ----------------------------------------------------------------------
    // Retry variable must be volatile so that updates are written directly
    // to memory and not cached in a register.
    // ----------------------------------------------------------------------
    volatile struct {
        unsigned int tried_to_decrement_apd_invoke_count : 1;
        unsigned int decremented_apd_invoke_count : 1;
        unsigned int tried_to_free_invoke_parm_struct : 1;
        unsigned int freed_invoke_parm_struct : 1;
        unsigned int tried_to_destroy_as_resmgr : 1;
        unsigned int destroyed_as_resmgr : 1;
        unsigned int tried_to_destroy_task_resmgr : 1;
        unsigned int destroyed_task_resmgr : 1;
        unsigned int tried_to_reset_tgoo_invoke_bit : 1;
        unsigned int reset_tgoo_invoke_bit : 1;
        unsigned int tried_to_free_tgoo : 1;
        unsigned int freed_tgoo : 1;
        unsigned int tried_to_cleanup_safm_count : 2;
        unsigned int cleaned_up_safm : 1;
        unsigned int tried_to_free_local_lock : 1;
        unsigned int freed_local_lock : 1;
        unsigned int tried_to_free_local_lock_save_area : 1;
        unsigned int freed_local_lock_save_area : 1;
        unsigned int _available : 13;
    } retry_bits;

    // -----------------------------------------------------------------------
    // Set up an ESTAE in case we run into trouble.
    // -----------------------------------------------------------------------
    memset((void*)(&retry_bits), 0, sizeof(retry_bits));
    retry_parms angel_retry_area;
    int estaex_rc, estaex_rsn;
    memset(&angel_retry_area, 0, sizeof(angel_retry_area));
    establish_estaex_with_retry(&angel_retry_area,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {

        // Release the local lock if held
        if (recovery_p->release_local_lock == 1) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_free_local_lock == 0) {
                retry_bits.tried_to_free_local_lock = 1;
                releaseLocalLock(recovery_p->release_local_lock_save_area_p);
                retry_bits.freed_local_lock = 1;
            }
        }

        // Release the local lock storage
        if (recovery_p->release_local_lock_save_area_p != NULL) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_free_local_lock_save_area == 0) {
                retry_bits.tried_to_free_local_lock_save_area = 1;
                free(recovery_p->release_local_lock_save_area_p);
                retry_bits.freed_local_lock_save_area = 1;
            }
        }

        if (okToDump(sdwa_p)) {
            takeSvcdump("ARR");
        }

        // Clean up the SAFM.  This is a multi-step process and may need to be
        // executed a number of times before we give up.
        if ((recovery_p->drm_cleanup_safm == 1) || (recovery_p->drm_cleanup_safm_unload == 1) ||
            (recovery_p->drm_cleanup_safm_release == 1)){
            SET_RETRY_POINT(angel_retry_area);
            if ((retry_bits.tried_to_cleanup_safm_count < 3) && (retry_bits.cleaned_up_safm == 0)) {
                retry_bits.tried_to_cleanup_safm_count = retry_bits.tried_to_cleanup_safm_count + 1;
                cleanup_safm(recovery_p);
                retry_bits.cleaned_up_safm = 1;
            }
        }

        // Free storage used by invoke to copy parm list
        if (recovery_p->drm_invoke_parm_struct_p != NULL) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_free_invoke_parm_struct == 0) {
                retry_bits.tried_to_free_invoke_parm_struct = 1;
                free(recovery_p->drm_invoke_parm_struct_p);
                recovery_p->drm_invoke_parm_struct_p = NULL;
                retry_bits.freed_invoke_parm_struct = 1;
            }
        }

        // See about resetting the invoke bit in the TGOO.
        angel_process_data* apd_p = getAngelProcessData();
        angel_task_data* atd_p = getAngelTaskDataFromSTCB();
        if ((recovery_p->drm_reset_invoking_bit != 0) && (atd_p != NULL)) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_reset_tgoo_invoke_bit == 0) {
                retry_bits.tried_to_reset_tgoo_invoke_bit = 1;
                resetInvokingBit(atd_p, apd_p, recovery_p);
                retry_bits.reset_tgoo_invoke_bit = 1;
            }
        }

        // If we created a TGOO, and we need to free it, do so now.
        if (recovery_p->drm_cleanup_tgoo == TRUE) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_free_tgoo == 0) {
                retry_bits.tried_to_free_tgoo = 1;
                recovery_p->drm_cleanup_tgoo = FALSE;
                destroyAngelTaskData();
                retry_bits.freed_tgoo = 1;
            }
        }

        // Free RESMGRs if set
        if (recovery_p->drm_as_resmgr_token != 0) {
            SET_RETRY_POINT(angel_retry_area);

            if (retry_bits.tried_to_destroy_as_resmgr == 0) {
                retry_bits.tried_to_destroy_as_resmgr = 1;
                int rm_rc = deleteResourceManager(&(recovery_p->drm_as_resmgr_token), BBOZRMGR_TYPE_AS);
                recovery_p->drm_as_resmgr_token = 0;
                retry_bits.destroyed_as_resmgr = 1;
            }
        }

        if (recovery_p->drm_task_resmgr_token != 0) {
            SET_RETRY_POINT(angel_retry_area);
            if (retry_bits.tried_to_destroy_task_resmgr == 0) {
                retry_bits.tried_to_destroy_task_resmgr = 1;
                int rm_rc = deleteResourceManager(&(recovery_p->drm_task_resmgr_token), BBOZRMGR_TYPE_ALLTASKS);
                recovery_p->drm_task_resmgr_token = 0;
                retry_bits.destroyed_task_resmgr = 1;
            }
        }

        // -----------------------------------------------------------
        // Cancel the ESTAE
        // -----------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    }
}

/**
 * Turns off the 'angel active' bit in the CGOO or the anchor
 */
static void unsetAngelActiveFlag(bbgzsgoo* sgoo_p) {
    AngelStatusFlags_t* flags_p = NULL;
    if (sgoo_p->bbgzsgoo_angelAnchor_p != NULL) {
        AngelAnchor_t* aa_p = sgoo_p->bbgzsgoo_angelAnchor_p;
        flags_p = &(aa_p->flags);
    } else {
        bgvt* __ptr32 bgvt_p = findBGVT();
        bbgzcgoo* cgoo_p = (bbgzcgoo*) bgvt_p->bbodbgvt_bbgzcgoo;
        flags_p = &(cgoo_p->bbgzcgoo_flags);
    }

    AngelStatusFlags_t oldFlags, newFlags; // used for compare-and-swap.
    memcpy(&oldFlags, flags_p, sizeof(oldFlags));
    for (int cs_rc = -1; cs_rc != 0;) {
        memcpy(&newFlags, &oldFlags, sizeof(newFlags));
        newFlags.angel_active = 0;
        cs_rc = __cs1(&oldFlags, flags_p, &newFlags);
    }
}

// RESMGR
void dynamicReplaceableRESMGR(rmpl* rmpl_p, angel_process_data* apd_p) {
    ascb* ascb_p = rmpl_p->rmplascb;
    assb* assb_p = ascb_p->ascbassb;

    // -----------------------------------------------------------------------
    // See if this process had angel process data.  If it did, and we're
    // doing an address space termination, free it.
    //-----------------------------------------------------------------------
    if ((rmpl_p->rmplflg1 & rmplterm) == rmplterm) {
        if (apd_p != NULL) {
            if (apd_p->as_type == ANGEL_PROCESS_TYPE_ANGEL) {
                // ----------------------------------------------------------
                // Indicate that the angel is inactive by marking the flag in
                // either the CGOO for the default angel, or in the anchor in 
                // the case of a named angel.
                // There must be a CGOO or an anchor if we got far enough to 
                // create the angel process data, because the cell for the 
                // angel process data comes from the SGOO, hung off the CGOO
                // or the anchor.
                // ----------------------------------------------------------
                bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
                unsetAngelActiveFlag(sgoo_p);
            }

            // --------------------------------------------------------------
            // Delete the angel process data name tokens.  Even if we are not
            // deleting the process data yet, we should not be able to find
            // them by name any more..
            // --------------------------------------------------------------
            deleteAngelProcessDataNameTokens(apd_p, (SToken*)(&(assb_p->assbstkn)));

            // ---------------------------------------------------------------
            // Try to set the bit in the angel process data that indicates the
            // RESMGR has run.  If there are no clients invoking right now,
            // this means we can destroy the angel process data.
            // ---------------------------------------------------------------
            AngelProcessInvokeCount_t oldCount, newCount;
            memcpy(&oldCount, &(apd_p->invokecount), sizeof(oldCount));
            for (int csRC = -1; csRC != 0;) {
                memcpy(&newCount, &oldCount, sizeof(newCount));
                newCount.iptOrAsResmgrFinished = 1;
                csRC = __cs1(&oldCount, &(apd_p->invokecount), &newCount);
            }

            if (newCount.allClientsUnbound == 1) {
                // ---------------------------------------------------------------
                // We're done with the angel process data now, return it to the
                // cell pool.
                // ---------------------------------------------------------------
                destroyAngelProcessData(apd_p);
            }
        }
    } else {
        // -------------------------------------------------------------
        // Initial pthread creating task RESMGR.  We are going to do a
        // lot of the same things we do for the address space level RESMGR.
        // -------------------------------------------------------------
        if (apd_p != NULL) {
            if ((apd_p->initial_pthread_creating_task_p) == (rmpl_p->rmpltcba)) {
                if (apd_p->as_type == ANGEL_PROCESS_TYPE_ANGEL) {
                    // Indicate that the angel is inactive
                    bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
                    unsetAngelActiveFlag(sgoo_p);
                } else if (apd_p->as_type == ANGEL_PROCESS_TYPE_SERVER) {
                    // ----------------------------------------------------------
                    // No one should be using authorized services now that the
                    // IPT has gone away, because the BBGZSAFM is tied to the
                    // IPT.  So we do a deregister here.  It's possible that
                    // we're already deregistered, and that's OK.  Wrap this in
                    // an ESTAE because it's likely that calling deregister during
                    // task termination is going to fail, at least partially.
                    // ----------------------------------------------------------
                    retry_parms deregister_estae_parms;
                    int estae_rc = 0, estae_rsn = 0;
                    memset(&deregister_estae_parms, 0, sizeof(retry_parms));
                    establish_estaex_with_retry(&deregister_estae_parms, &estae_rc, &estae_rsn);
                    if (estae_rc == 0) {
                        volatile unsigned char retry = 0;
                        SET_RETRY_POINT(deregister_estae_parms);
                        if (retry == 0) {
                            retry = 1;
                            angel_server_pc_recovery fake_recovery_area;
                            memset(&fake_recovery_area, 0, sizeof(angel_server_pc_recovery));
                            dynamicReplaceablePC_Deregister(apd_p, &fake_recovery_area);
                        }
                        remove_estaex(&estae_rc, &estae_rsn);
                    }

                    // -------------------------------------------------------
                    // Get rid of the 'all tasks' RESMGR.  Since we drove
                    // deregister, all tasks will have gone through cleanup.
                    // -------------------------------------------------------
                    if ((apd_p->js_task_resmgr_token) != 0) {
                        deleteResourceManager(&(apd_p->js_task_resmgr_token), BBOZRMGR_TYPE_ALLTASKS);
                    }
                }

                // --------------------------------------------------------------
                // Delete the angel process data name tokens.  Even if we are not
                // deleting the process data yet, we should not be able to find
                // them by name any more..
                // --------------------------------------------------------------
                deleteAngelProcessDataNameTokens(apd_p, (SToken*)(&(assb_p->assbstkn)));

                // -----------------------------------------------------------
                // Set the bit in the angel process data that indicates the
                // RESMGR has run.  If there are no clients invoking right now,
                // this means we can destroy the angel process data.
                // -----------------------------------------------------------
                AngelProcessInvokeCount_t oldCount, newCount;
                memcpy(&oldCount, &(apd_p->invokecount), sizeof(oldCount));
                for (int csRC = -1; csRC != 0;) {
                    memcpy(&newCount, &oldCount, sizeof(newCount));
                    newCount.iptOrAsResmgrFinished = 1;
                    csRC = __cs1(&oldCount, &(apd_p->invokecount), &newCount);
                }

                if ((newCount.allClientsUnbound == 1) || (apd_p->as_type == ANGEL_PROCESS_TYPE_ANGEL)) {
                    // -------------------------------------------------------
                    // We're done with the angel process data now, return it
                    // to the cell pool.
                    // -------------------------------------------------------
                    destroyAngelProcessData(apd_p);
                }
            } else {
                // -----------------------------------------------------------
                // Just a regular task... drive the server's recovery, if
                // any.
                // TODO: I am pretty sure that there's a window here where a
                //       task ends and the server is deregistered and then
                //       re-registered, so that by the time we get here our
                //       task level cleanup is referencing the previous
                //       registration (ie. server task cleanup might release
                //       storage back to the wrong heap).  Maybe there needs
                //       to be a sequence number stored in the task data and
                //       also in the process data so that we can check for
                //       this?
                // -----------------------------------------------------------
                volatile struct {
                    int triedToSetESTAE : 1,
                        setESTAE : 1,
                        triedToIncrementResmgrCount : 1,
                        incrementedResmgrCount : 1,
                        triedToCleanupTask : 1,
                        cleanedUpTask : 1,
                        triedToDecrementResmgrCount : 1,
                        decrementedResmgrCount : 1,
                        triedToCleanupSAFM : 1,
                        cleanedUpSAFM : 1,
                        _available : 22;
                } retryBits;
                retry_parms taskCleanupEstaeParms;
                int estae_rc = 0, estae_rsn = 0;
                taskCleanupEstaeParms.setrp_opts.nodump = 1;
                memset((void*)&retryBits, 0, sizeof(retryBits));
                SET_RETRY_POINT(taskCleanupEstaeParms);
                if (retryBits.triedToSetESTAE == 0) {
                    retryBits.triedToSetESTAE = 1;
                    establish_estaex_with_retry(&taskCleanupEstaeParms, &estae_rc, &estae_rsn);
                    retryBits.setESTAE = (estae_rc == 0);
                }

                if (retryBits.setESTAE == 1) {
                    SET_RETRY_POINT(taskCleanupEstaeParms);
                    bbgzasvt_header* safm_header_p = NULL;
                    angel_task_data* atd_p = NULL;
                    if (retryBits.triedToIncrementResmgrCount == 0) {
                        retryBits.triedToIncrementResmgrCount = 1;
                        atd_p = getAngelTaskDataFromSTCB();
                        if (atd_p != NULL) {
                            safm_header_p = (bbgzasvt_header*)(apd_p->safm_function_table_p);
                            AngelProcessInvokeCount_t oldResmgrCount, newResmgrCount;
                            memcpy(&oldResmgrCount, &(apd_p->invokecount), sizeof(oldResmgrCount));
                            for (int cs_rc = -1; cs_rc != 0;) {
                                if ((oldResmgrCount.deregistered == 0) && (safm_header_p != NULL) && (safm_header_p->task_cleanup_routine_ptr != NULL)) {
                                    newResmgrCount = oldResmgrCount;
                                    newResmgrCount.curResmgrCount = oldResmgrCount.curResmgrCount + 1;
                                    cs_rc = __cs1(&oldResmgrCount, &(apd_p->invokecount), &newResmgrCount);
                                    retryBits.incrementedResmgrCount = (cs_rc == 0);
                                } else {
                                    cs_rc = 0;
                                }
                            }
                        }
                    }

                    // -------------------------------------------------------
                    // We incremented the RESMGR count and so we can be
                    // confident that the server code will not go away.  If
                    // the task has not had its server task cleanup driven,
                    // drive it now.  It may have already been driven if the
                    // deregister code drove it, but since we just checked the
                    // deregister bit, this is unlikely (but still possible).
                    // -------------------------------------------------------
                    if (retryBits.incrementedResmgrCount == 1) {
                        SET_RETRY_POINT(taskCleanupEstaeParms);
                        if (retryBits.triedToCleanupTask == 0) {
                            retryBits.triedToCleanupTask = 1;
                            AngelCSFlagArea_t oldTaskFlags, newTaskFlags;
                            unsigned char setTaskCleanupBit = FALSE;
                            memcpy(&oldTaskFlags, &(atd_p->invokeFlags), sizeof(oldTaskFlags));
                            for (int cs_rc = -1; cs_rc != 0;) {
                                if (oldTaskFlags.droveServerCleanup == 0) {
                                    newTaskFlags = oldTaskFlags;
                                    newTaskFlags.droveServerCleanup = 1;
                                    cs_rc = __cs1(&oldTaskFlags, &(atd_p->invokeFlags), &newTaskFlags);
                                    setTaskCleanupBit = (cs_rc == 0);
                                } else {
                                    cs_rc = 0;
                                }
                            }

                            if (setTaskCleanupBit) {
                                safm_header_p->task_cleanup_routine_ptr(NULL);
                                retryBits.cleanedUpTask = 1;
                            }
                        }

                        // -----------------------------------------------------------------------
                        // Get the SAFM information out of the angel process data before we
                        // decrement the resmgr count, because once we decrement, we could
                        // be re-registered immediately, and the SAFM information could be
                        // over-written.
                        // -----------------------------------------------------------------------
                        SET_RETRY_POINT(taskCleanupEstaeParms);
                        angel_server_pc_recovery fakeRecoveryData;
                        AngelProcessInvokeCount_t oldResmgrCount, newResmgrCount;
                        if (retryBits.triedToDecrementResmgrCount == 0) {
                            retryBits.triedToDecrementResmgrCount = 1;
                            fakeRecoveryData.drm_safm_entrypt_p = apd_p->safm_entry_p;
                            fakeRecoveryData.drm_safm_func_table_p = safm_header_p;
                            fakeRecoveryData.drm_safm_func_table_size = apd_p->safm_function_table_size;
                            fakeRecoveryData.drm_cleanup_safm = 1;

                            memcpy(&oldResmgrCount, &(apd_p->invokecount), sizeof(oldResmgrCount));
                            for (int cs_rc = -1; cs_rc != 0;) {
                                newResmgrCount = oldResmgrCount;
                                newResmgrCount.curResmgrCount = oldResmgrCount.curResmgrCount - 1;
                                cs_rc = __cs1(&oldResmgrCount, &(apd_p->invokecount), &newResmgrCount);
                            }
                            retryBits.decrementedResmgrCount = 1;
                        }

                        SET_RETRY_POINT(taskCleanupEstaeParms);
                        if ((retryBits.decrementedResmgrCount == 1) && (retryBits.triedToCleanupSAFM == 0)) {
                            retryBits.triedToCleanupSAFM = 1;
                            if ((oldResmgrCount.allTasksCleanedUp == 1) && (newResmgrCount.curResmgrCount == 0)) {
                                if (safm_header_p->process_cleanup_routine_ptr != NULL) {
                                    safm_header_p->process_cleanup_routine_ptr(NO_CLIENT_TOKEN);
                                }

                                cleanup_safm(&fakeRecoveryData);
                                retryBits.cleanedUpSAFM = 1;
                            }
                        }
                    }

                    remove_estaex(&estae_rc, &estae_rsn);
                }

                // -----------------------------------------------------------
                // Clean up our own task level heap cache.
                // -----------------------------------------------------------
                if (apd_p->key2_env_p != NULL) {
                    struct __csysenvtoken_s* cenv_p = (struct __csysenvtoken_s*)(apd_p->key2_env_p);
                    void* heapAnchor_p = (void*)(cenv_p->__csetheapuserdata);
                    if (heapAnchor_p != NULL) {
                        taskLevelHeapCleanup(heapAnchor_p, NULL);
                    }
                }

                // -----------------------------------------------------------
                // Remove the task data from the STCBBCBA, to avoid a race
                // condition between this RESMGR and another task calling
                // deregister.  MVS is going to free our task data since it
                // is owned by this task, and the deregistering task is going
                // to try to use it if it's set in the STCBBCBA.  There is
                // still a window here, we're trying to make it smaller.
                // -----------------------------------------------------------
                // Note that the task data in the STCBBCBA is not the task
                // data that is currently in R13 because the entry linkage for
                // the RESMGR unconditionally gets a new task data area.  This
                // needs to be fixed sometime, for task level RESMGR we can
                // probably use the existing angel task data (TODO).
                // -----------------------------------------------------------
                destroyAngelTaskData();
            }
        }
    }
}
