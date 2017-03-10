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

#include "include/angel_bgvt_services.h"
#include "include/angel_client_bind_data.h"
#include "include/angel_client_process_data.h"
#include "include/angel_dynamic_replaceable_module.h"
#include "include/angel_functions.h"
#include "include/angel_process_data.h"
#include "include/angel_server_pc.h"
#include "include/angel_sgoo_services.h"
#include "include/angel_task_data.h"
#include "include/bbgzasvt.h"
#include "include/bbgzsgoo.h"
#include "include/bpx_load.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_enq.h"
#include "include/mvs_estae.h"
#include "include/mvs_extract.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_qedit.h"
#include "include/mvs_utils.h"
#include "include/mvs_wait.h"
#include "include/mvs_wto.h"
#include "include/ras_tracing.h"

#include "include/gen/iezcib.h"
#include "include/gen/iezcom.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"
#include "include/gen/native_messages_mc.h"

#define RAS_MODULE_CONST  RAS_MODULE_ANGEL_FUNCTIONS

#define _TP_BAD_RC_1 1
#define _TP_BAD_RC_2 2
#define _TP_BAD_RC_3 3
#define _TP_BAD_RC_4 4
#define _TP_LIST_ACTIVE_QUERY 5
#define _TP_LIST_ACITVE_SSCANF 6

/**
 * Macro to allocate a shared memory cell pool in the updateSGOO function.
 * @param name A portion of the name of the cell pool in the SGOO control block.
 * @param type The type of the struct to make the cell size.
 * @param eye The eye catcher for the new cell pool.
 */
#define BUILD_SGOO_POOL(name, type, eye)                                     \
    if (sgoo_p->bbgzsgoo_##name == 0L) {                                     \
        sgoo_p->bbgzsgoo_##name##CellSize = sizeof(type);                    \
        int poolSize = computeCellPoolStorageRequirement(0, sgoo_p->bbgzsgoo_##name##CellSize); \
        void* poolStorage_p = allocateSGOOsharedStorage(sgoo_p, poolSize);   \
        if (poolStorage_p != NULL) {                                         \
            sgoo_p->bbgzsgoo_##name = buildCellPool(poolStorage_p, poolSize, sgoo_p->bbgzsgoo_##name##CellSize, eye, poolFlags); \
            if (sgoo_p->bbgzsgoo_##name == 0L) {                             \
                updateRC = -1;                                               \
            }                                                                \
        } else {                                                             \
            updateRC = -1;                                                   \
        }                                                                    \
    }

/**
 * Update the SGOO for this version of the DRM.
 *
 * @return 0 if the update was successful.
 */
static int updateSGOO(void) {
    int updateRC = 0;

    // -----------------------------------------------------------------------
    // Get the SGOO for this angel.
    // -----------------------------------------------------------------------
    angel_process_data* apd_p = getAngelProcessData();
    bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;

    if (sgoo_p != NULL) {
        // -------------------------------------------------------------------
        // Make sure that all the cell pools that we know about have been
        // created.
        // -------------------------------------------------------------------
        buildCellPoolFlags poolFlags;
        memset(&poolFlags, 0, sizeof(poolFlags));
        poolFlags.autoGrowCellPool = 1;
        poolFlags.skipInitialCellAllocation = 1;

        // These macros can set updateRC.
        BUILD_SGOO_POOL(angelClientDataPool, AngelClientProcessData_t, "BBGZACPP");
        BUILD_SGOO_POOL(clientBindDataPool, AngelClientBindData_t, "BBGZCBDP");
        BUILD_SGOO_POOL(clientBindDataNodePool, AngelClientBindDataNode_t, "BBGZCBDN");
        BUILD_SGOO_POOL(clientPreDynamicAreaPool, AngelClientPreDynArea_t, "BBGZCPDA");

        // -------------------------------------------------------------------
        // Update the shared memory cell pool in the SGOO with the name of the
        // routine that will allow the cell pool to grow when it runs out of
        // cells.  The user data for the cell pool must point to the SGOO
        // where the memory will come from.
        // -------------------------------------------------------------------
        if (updateRC == 0) {
            setCellPoolUserData(sgoo_p->bbgzsgoo_angel_process_data_cellpool_id, sgoo_p);
            setCellPoolAutoGrowFunction(sgoo_p->bbgzsgoo_angel_process_data_cellpool_id,
                                        getStorageToGrowSharedMemoryCellPool);
            setCellPoolUserData(sgoo_p->bbgzsgoo_angelClientDataPool, sgoo_p);
            setCellPoolAutoGrowFunction(sgoo_p->bbgzsgoo_angelClientDataPool,
                                        getStorageToGrowSharedMemoryCellPool);
            setCellPoolUserData(sgoo_p->bbgzsgoo_clientBindDataPool, sgoo_p);
            setCellPoolAutoGrowFunction(sgoo_p->bbgzsgoo_clientBindDataPool,
                                        getStorageToGrowSharedMemoryCellPool);
            setCellPoolUserData(sgoo_p->bbgzsgoo_clientBindDataNodePool, sgoo_p);
            setCellPoolAutoGrowFunction(sgoo_p->bbgzsgoo_clientBindDataNodePool,
                                        getStorageToGrowSharedMemoryCellPool);
            setCellPoolUserData(sgoo_p->bbgzsgoo_clientPreDynamicAreaPool, sgoo_p);
            setCellPoolAutoGrowFunction(sgoo_p->bbgzsgoo_clientPreDynamicAreaPool,
                                        getStorageToGrowSharedMemoryCellPool);
        }
    }

    return updateRC;
}

// Initialize the Angel
int BBGZDRM_Init(void) {
    return updateSGOO();
}

/**
 * Convenience macro to check for and trace a bad return code.
 *
 * @param rc The return code to trace
 * @param tp The trace point identifier
 * @param desc A string describing what is being traced
 */
#define TRACE_BAD_RC(rc, tp, desc)              \
    if (rc != 0) {                              \
        if (TraceActive(trc_level_exception)) { \
            TraceRecord(                        \
            trc_level_exception,                \
            TP(tp),                             \
            desc,                               \
            TRACE_DATA_INT(rc, "RC"),           \
            TRACE_DATA_END_PARMS);              \
        }                                       \
    }

/**
 * Prints the version of the dynamic replaceable module using WTO.
 *
 * @param cart_p A pointer to the CART from the modify command which causes this
 *               message to be issued.
 */
void printDRMVersionString(unsigned char* cart_p)
{
    char* versionString = (char*) getDynamicReplaceableVersionString();
    if (versionString != NULL) {
        int message_len = strlen(versionString) + 128;
        char versionMessage[message_len];
        snprintf(versionMessage, message_len, MODIFY_DISPLAY_ANGEL_VERSION, versionString);
        write_to_operator(versionMessage, cart_p);
        free(versionString);
    }
}

/**
 * Get the version of the dynamic replaceable module for a given angel name.
 */
static char* getDRMVersionForSpecificAngel(unsigned char* inAngelName) {

    // First, figure out who we are.  That's going to influence how we get
    // the DRM version string.
    angel_process_data* apd_p = getAngelProcessData();
    bbgzsgoo* ourSgoo_p = apd_p->bbgzsgoo_p;

    // Now figure out who we want.
    char* angelName = (inAngelName == NULL) ? "" : inAngelName;
    bbgzsgoo* targetSgoo_p = getSGOO(angelName);
    char* versionString = NULL;

    // If we couldn't find the target SGOO, just return unknown.
    if (targetSgoo_p == NULL) {
        versionString = "UNK";
    } else if (targetSgoo_p == ourSgoo_p) {
        // See if it's us.  If it's us, we just return our own version.
        versionString = getDynamicReplaceableVersionString();
    } else {
        // If it's not us, we need to attach to the shared memory for that
        // SGOO so that we can get to the ARMV and call the version function.
        long long access_sgoo_user_token = getAddressSpaceSupervisorStateUserToken();
        accessSharedAbove(targetSgoo_p, access_sgoo_user_token);
        bbgzarmv* armv_p = (bbgzarmv*) targetSgoo_p->bbgzsgoo_armv;
        struct bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
        versionString = drm_p->getVersionString();
        detachSharedAbove(targetSgoo_p, access_sgoo_user_token, FALSE);
    }

    return versionString;
}

/**
 * Prints the major and minor versions of the FSM and DRM modules.
 * This feature is intentionally un-documented, it is for use by service only.
 */
static void printAllVersionStrings(unsigned char* cart_p, bbgzsgoo* sgoo_p) {
    struct bbgzafsm* fsm_p = sgoo_p->bbgzsgoo_fsm;

    // Get the FSM major and minor versions (if available).
    int fsm_major_ver = fsm_p->getVersionNumber();
    unsigned short fsm_minor_ver = 0;
    if ((fsm_p->_reserved_PC7 == 0) &&
        (fsm_p->v1_verInfo.version >= 1)) {
        fsm_minor_ver = fsm_p->minorVersion;
    }

    // Get the DRM major and minor versions.
    bbgzarmv* armv_p = (bbgzarmv*) sgoo_p->bbgzsgoo_armv;
    struct bbgzadrm*  drm_p = armv_p->bbgzarmv_drm;
    int drm_major_ver = drm_p->getVersionNumber();
    unsigned short drm_minor_ver = drm_p->verInfo.minorVersion;

    // Print it out.
    char versionInfoDetails[128];
    snprintf(versionInfoDetails, sizeof(versionInfoDetails), "FSM %i.%i DRM %i.%i",
             fsm_major_ver, fsm_minor_ver, drm_major_ver, drm_minor_ver);

    char versionMessage[256];
    snprintf(versionMessage, sizeof(versionMessage), MODIFY_DISPLAY_ANGEL_VERSION, versionInfoDetails);
    write_to_operator(versionMessage, cart_p);
}

/**
 * Checks for active servers which are connected (or have been connected in
 * the past) to this angel process.  This function prints a message to the
 * console if there are servers active that are connected to this angel process.
 *
 * @param cart_p A pointer to the CART for the stop command which caused this
 *               method to be driven.
 * @param sgoo_p A pointer to the SGOO for this angel.
 *
 * @return 1 if there are no servers preventing a stop from being issued, 0 if
 *         a stop cannot be allowed at this time.
 */
char checkForActiveServers(unsigned char* cart_p, bbgzsgoo* sgoo_p, char* angel_name)
{
    char stop_issued = 0;

    int isgquery_rc = 0, isgquery_rsn = 0;
    char queryRNAME[255];
    char scanfRNAME[255];

    AngelAnchor_t* angelAnchor_p = sgoo_p->bbgzsgoo_angelAnchor_p;
    if (angelAnchor_p != NULL) {
        snprintf(queryRNAME, sizeof(queryRNAME), ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_QUERY2, angelAnchor_p->name);
        strcpy(scanfRNAME, ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_PATTERN2);
    } else {
        strcpy(queryRNAME, ANGEL_PROCESS_SERVER_ENQ_RNAME_QUERY);
        strcpy(scanfRNAME, ANGEL_PROCESS_SERVER_ENQ_RNAME_PATTERN);
    }

    isgyquaahdr* server_enqs_p = scan_enq_system(
        BBGZ_ENQ_QNAME, queryRNAME,
        &isgquery_rc, &isgquery_rsn);

    if (server_enqs_p != NULL) {
        if (server_enqs_p->isgyquaahdrnumrecords > 0) {
            // ---------------------------------------------------
            // There are servers up.  List them.
            // ---------------------------------------------------
            char message[80];
            if (strlen(angel_name) == 0) {
                write_to_operator(STOP_COMMAND_ACTIVE_SERVERS, cart_p);
            } else {
                char buffer[256];
                snprintf(buffer, sizeof(buffer), STOP_COMMAND_ACTIVE_SERVERS_NAME, angel_name);
                write_to_operator(buffer, cart_p);
            }
            isgyquaars* enq_p = server_enqs_p->isgyquaahdrfirstrecord31;
            while (enq_p != NULL) {
                char rname[256];
                memcpy(rname, enq_p->isgyquaarsrname31, enq_p->isgyquaarsrnamelen);
                rname[enq_p->isgyquaarsrnamelen] = 0;
                unsigned long long stoken = 0;
                if (sscanf(rname, scanfRNAME, &stoken) == 1) {
                    ascb* ascb_p = getAscbFromStoken((void*)(&stoken));
                    if (ascb_p != NULL) {
                        char jobname[9];
                        if (ascb_p->ascbjbni != NULL) {
                            memcpy(jobname, ascb_p->ascbjbni, 8);
                            jobname[8] = '\0';
                        } else if (ascb_p->ascbjbns != NULL) {
                            memcpy(jobname, ascb_p->ascbjbns, 8);
                            jobname[8] = '\0';
                        } else {
                            strcpy(jobname, "*UNKNOWN");
                        }

                        char asid_string[8];
                        snprintf(asid_string, sizeof(asid_string), "%hx", ascb_p->ascbasid);
                        snprintf(message, sizeof(message), STOP_COMMAND_ACTIVE_SERVERS_DETAIL, asid_string, jobname);
                        write_to_operator(message, cart_p);
                    }
                }
                enq_p = enq_p->isgyquaarsnext31;
            }
        } else {
            stop_issued = 1;
        }

        free(server_enqs_p);
    } else {
        stop_issued = 1;
    }

    return stop_issued;
}

/**
 * List active servers which are connected to the angel process.
 *
 * @param cart_p A pointer to the CART for the modify command which caused this
 *               method to be driven.
 * @param sgoo_p A pointer to the SGOO for this angel.
 */
void listActiveServers(unsigned char* cart_p, bbgzsgoo* sgoo_p)
{
    write_to_operator(MODIFY_DISPLAY_ACTIVE_SERVERS, cart_p);
    int isgquery_rc = 0, isgquery_rsn = 0;
    char queryRNAME[255];
    char scanfRNAME[255];

    AngelAnchor_t* angelAnchor_p = sgoo_p->bbgzsgoo_angelAnchor_p;
    if (angelAnchor_p != NULL) {
        snprintf(queryRNAME, sizeof(queryRNAME), ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_QUERY2, angelAnchor_p->name);
        strcpy(scanfRNAME, ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_PATTERN2);
    } else {
        strcpy(queryRNAME, ANGEL_PROCESS_SERVER_ENQ_RNAME_QUERY);
        strcpy(scanfRNAME, ANGEL_PROCESS_SERVER_ENQ_RNAME_PATTERN);
    }

    isgyquaahdr* server_enqs_p = scan_enq_system(
        BBGZ_ENQ_QNAME, queryRNAME,
        &isgquery_rc, &isgquery_rsn);

    if (TraceActive(trc_level_detailed)) {
            TraceRecord(
                trc_level_detailed,
                TP(_TP_LIST_ACTIVE_QUERY),
                "Server ENQ query",
                TRACE_DATA_STRING(queryRNAME, "Query RNAME"),
                TRACE_DATA_INT(isgquery_rc, "RC"),
                TRACE_DATA_INT(isgquery_rsn, "RSN"),
                TRACE_DATA_END_PARMS);
    }

    if (server_enqs_p != NULL) {
        if (server_enqs_p->isgyquaahdrnumrecords > 0) {
            // ---------------------------------------------------
            // There are servers up.  List them.
            // ---------------------------------------------------
            char message[80];
            isgyquaars* enq_p = server_enqs_p->isgyquaahdrfirstrecord31;
            while (enq_p != NULL) {
                char rname[256];
                memcpy(rname, enq_p->isgyquaarsrname31, enq_p->isgyquaarsrnamelen);
                rname[enq_p->isgyquaarsrnamelen] = 0;
                unsigned long long stoken = 0;

                int sscanfRC = sscanf(rname, scanfRNAME, &stoken);
                if (TraceActive(trc_level_detailed)) {
                        TraceRecord(
                            trc_level_detailed,
                            TP(_TP_LIST_ACITVE_SSCANF),
                            "Parsing STOKEN",
                            TRACE_DATA_STRING(scanfRNAME, "Query RNAME"),
                            TRACE_DATA_STRING(rname, "Actual RNAME"),
                            TRACE_DATA_INT(sscanfRC, "SSCANF RC"),
                            TRACE_DATA_RAWDATA(sizeof(stoken), &stoken, "STOKEN"),
                            TRACE_DATA_END_PARMS);
                }

                if (sscanfRC == 1) {
                    ascb* ascb_p = getAscbFromStoken((void*)(&stoken));
                    if (ascb_p != NULL) {
                        char jobname[9];
                        if (ascb_p->ascbjbni != NULL) {
                            memcpy(jobname, ascb_p->ascbjbni, 8);
                            jobname[8] = '\0';
                        } else if (ascb_p->ascbjbns != NULL) {
                            memcpy(jobname, ascb_p->ascbjbns, 8);
                            jobname[8] = '\0';
                        } else {
                            strcpy(jobname, "*UNKNOWN");
                        }

                        char asid_string[8];
                        snprintf(asid_string, sizeof(asid_string), "%hx", ascb_p->ascbasid);
                        snprintf(message, sizeof(message), STOP_COMMAND_ACTIVE_SERVERS_DETAIL, asid_string, jobname);
                        write_to_operator(message, cart_p);
                    }
                }
                enq_p = enq_p->isgyquaarsnext31;
            }
        }
        free(server_enqs_p);
    }
}

/**
 * List active angels on the system.
 *
 * @param cart_p A pointer to the CART for the modify command which caused this
 *               method to be driven.
 *
 */
void listActiveAngels(unsigned char* cart_p)
{
    write_to_operator(MODIFY_DISPLAY_ACTIVE_ANGELS, cart_p);
    int isgquery_rc = 0, isgquery_rsn = 0;

    // -----------------------------------------
    // Query for named angels
    // -----------------------------------------
    isgyquaahdr* angel_enqs_p = scan_enq_system(
        BBGZ_ENQ_QNAME, ANGEL_NAMED_PROCESS_RNAME_QUERY,
        &isgquery_rc, &isgquery_rsn);

    if (angel_enqs_p != NULL) {
        if (angel_enqs_p->isgyquaahdrnumrecords > 0) {
            // ---------------------------------------------------
            // There are active angels.  List them.
            // ---------------------------------------------------
            char message_details[80];
            char message_name[80];
            isgyquaars* enq_p = angel_enqs_p->isgyquaahdrfirstrecord31;
            while (enq_p != NULL) {

                //Get the angel name
                char name[256];
                char rname[256];

                memcpy(rname, enq_p->isgyquaarsrname31, enq_p->isgyquaarsrnamelen);
                rname[enq_p->isgyquaarsrnamelen] = 0;

                sscanf(rname, ANGEL_NAMED_PROCESS_RNAME, &name);

                isgyquaarq* enqRQ_p = (isgyquaarq*) enq_p->isgyquaarsfirstrq31;
                isgyquaarqx* enqRQX_p = (isgyquaarqx*) enqRQ_p->isgyquaarqrqx31;

                //Get the jobname
                char jobname[9];
                memcpy(&jobname, enqRQX_p->isgyquaarqxjobname, sizeof(jobname));
                jobname[8] = '\0';

                //Get the asid
                short asid = enqRQX_p->isgyquaarqxasid;
                char asid_string[4];
                snprintf(asid_string, 4, "%hx", asid);

                //Get the version
                char* version_string = getDRMVersionForSpecificAngel(name);

                //Print messages
                snprintf(message_details, sizeof(message_details), MODIFY_DISPLAY_ACTIVE_ANGEL_DETAILS, jobname, asid_string, version_string);
                write_to_operator(message_details, cart_p);

                snprintf(message_name, sizeof(message_name), MODIFY_DISPLAY_ACTIVE_ANGEL_NAME, name);
                write_to_operator(message_name, cart_p);

                free(version_string);
                enq_p = enq_p->isgyquaarsnext31;
            }
        }
        free(angel_enqs_p);
    }

    // -----------------------------------------------
    // Query for the defaut angel
    // -----------------------------------------------
    angel_enqs_p = scan_enq_system(
            BBGZ_ENQ_QNAME, ANGEL_PROCESS_RNAME,
            &isgquery_rc, &isgquery_rsn);

        if (angel_enqs_p != NULL) {

            if (angel_enqs_p->isgyquaahdrnumrecords > 0) {

                // ---------------------------------------------------
                // The default angel is active. List it
                // ---------------------------------------------------
                char message_details[80];
                char message_name[80];

                isgyquaars* enq_p = angel_enqs_p->isgyquaahdrfirstrecord31;

                while (enq_p != NULL) {

                    isgyquaarq* enqRQ_p = (isgyquaarq*) enq_p->isgyquaarsfirstrq31;
                    isgyquaarqx* enqRQX_p = (isgyquaarqx*) enqRQ_p->isgyquaarqrqx31;

                    //Get the jobname
                    char jobname[9];
                    memcpy(&jobname, enqRQX_p->isgyquaarqxjobname, sizeof(jobname));
                    jobname[8] = '\0';

                    //Get the asid
                    short asid = enqRQX_p->isgyquaarqxasid;
                    char asid_string[4];
                    snprintf(asid_string, 4, "%hx", asid);

                    //Get the version
                    char* version_string = getDRMVersionForSpecificAngel(NULL);

                    //Print messages
                    snprintf(message_details, sizeof(message_details), MODIFY_DISPLAY_ACTIVE_ANGEL_DETAILS, jobname, asid_string, version_string);
                    write_to_operator(message_details, cart_p);

                    snprintf(message_name, sizeof(message_name), MODIFY_DISPLAY_ACTIVE_ANGEL_NAME, "*Default");
                    write_to_operator(message_name, cart_p);

                    free(version_string);
                    enq_p = enq_p->isgyquaarsnext31;
                }
            }
            free(angel_enqs_p);
        }

}

// Run the Angel main processing loop (wait for command)
int BBGZDRM_Run(void) {
    iezcom* com_ptr;
    cib* __ptr32 cib_ptr;

    char stop_issued;
    char reload_module;

    int rc = 0;
    int run_rc = BBGZDRM_Run_Quit;

    // Indicate that the angel is active.  For the default (un-named) angel we use the
    // CGOO to indicate this.  For named angels we use the angel anchor control block.
    AngelStatusFlags_t* flags_p = NULL;
    angel_process_data* apd_p = getAngelProcessData();
    bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
    char* angel_name = NULL;
    if (sgoo_p->bbgzsgoo_angelAnchor_p != NULL) {
        AngelAnchor_t* aa_p = sgoo_p->bbgzsgoo_angelAnchor_p;
        flags_p = &(aa_p->flags);
	angel_name = aa_p->name;
    } else {
        bgvt* __ptr32 bgvt_p = findBGVT();
        bbgzcgoo* cgoo_p = (bbgzcgoo*) bgvt_p->bbodbgvt_bbgzcgoo;
        flags_p = &(cgoo_p->bbgzcgoo_flags);
    }

    AngelStatusFlags_t oldFlags, newFlags; // used for compare-and-swap.
    memcpy(&oldFlags, flags_p, sizeof(oldFlags));
    for (int cs_rc = -1; cs_rc != 0;) {
        memcpy(&newFlags, &oldFlags, sizeof(newFlags));
        newFlags.angel_active = 1;
        cs_rc = __cs1(&oldFlags, flags_p, &newFlags);
    }

    com_ptr = (iezcom*) extract_comm();

    if (com_ptr != NULL) {
        cib_ptr = com_ptr->comcibpt;
        if ((cib_ptr != NULL) && (cib_ptr->cibverb == cibstart)) {
            rc = free_cib_from_chain(com_ptr, com_ptr->comcibpt);
            TRACE_BAD_RC(rc, _TP_BAD_RC_1, "angel_functions BBGZDRM_Run QEDIT FREE Bad return code");
        }

        rc = set_cib_limit(com_ptr, 1);
        TRACE_BAD_RC(rc, _TP_BAD_RC_2, "angel_functions BBGZDRM_Run QEDIT SET LIMIT Bad return code");
    }

    // Wait on the CIB ECB
    stop_issued = 0;
    reload_module = 0;
    while ((stop_issued == 0) && (reload_module == 0)) {
        wait(com_ptr->comecbpt);

        cib_ptr = com_ptr->comcibpt;
        if (cib_ptr != NULL) {
            // ---------------------------------------------------------------
            // Save off the CART that goes with the message, we'll be using
            // it when we reply.
            // ---------------------------------------------------------------
            cibx* __ptr32 cibx_ptr = (cibx* __ptr32) (((char*)cib_ptr) + cib_ptr->cibxoff);
            unsigned char cart[8];

            memcpy(cart, cibx_ptr->cibxcart, sizeof(cart));

            if (cib_ptr->cibverb == cibstop) {
                // -----------------------------------------------------------
                // See if we can stop.  Do an ISGQUERY for active servers.
                // -----------------------------------------------------------
                stop_issued = checkForActiveServers(cart, sgoo_p, angel_name);
                if (stop_issued == 0) {
                    rc = set_cib_limit(com_ptr, 1); // Reset limit if still running.
                    TRACE_BAD_RC(rc, _TP_BAD_RC_4, "angel_functions BBGZDRM_Run QEDIT SET LIMIT STOP Bad return code");
                }
            } else {
                // -----------------------------------------------------------
                // See what we've got.  Right now we have four choices.
                // Three of them take no further arguments, and the forth
                // takes ON/OFF
                //
                //  RELOAD -- re-load the dynamic replaceable module
                //  VERSION -- print the Angel major version string
                //  VERSION,FULL -- print the Angel major and minor version
                //                  strings.  This is undocumented.
                //  DISPLAY,SERVERS -- list active servers
                //  TRACE = Y/N -- toggle tracing
                // -----------------------------------------------------------
                unsigned char modify_recognized = FALSE;
                int message_len = cib_ptr->cibdatln + 1;
                char message[message_len];
                memcpy(message, cib_ptr->cibdata, cib_ptr->cibdatln);
                message[cib_ptr->cibdatln] = 0;
                for (int x = 0; x < cib_ptr->cibdatln; x++) {
                    message[x] = toupper(message[x]);
                }

                if ((cib_ptr->cibdatln == strlen("RELOAD")) &&
                    (strncmp(message, "RELOAD", cib_ptr->cibdatln) == 0)) {
                    modify_recognized = TRUE;
                    reload_module = 1;
                } else if ((cib_ptr->cibdatln == strlen("DISPLAY,SERVERS")) &&
                           (strncmp(message, "DISPLAY,SERVERS", cib_ptr->cibdatln) == 0)) {
                    modify_recognized = TRUE;
                    listActiveServers(cart, sgoo_p);
                } else if ((cib_ptr->cibdatln == strlen("DISPLAY,ANGELS")) &&
                                           (strncmp(message, "DISPLAY,ANGELS", cib_ptr->cibdatln) == 0)) {
                                    modify_recognized = TRUE;
                                    listActiveAngels(cart);
                } else if ((cib_ptr->cibdatln == strlen("VERSION")) &&
                           (strncmp(message, "VERSION", cib_ptr->cibdatln) == 0)) {
                    // ---------------------------------------------------------
                    // Print the version string.
                    // ---------------------------------------------------------
                    modify_recognized = TRUE;
                    printDRMVersionString(cart);
                } else if ((cib_ptr->cibdatln == strlen("VERSION,FULL")) &&
                           (strncmp(message, "VERSION,FULL", cib_ptr->cibdatln) == 0)) {
                    // ---------------------------------------------------------
                    // Print the version string.
                    // ---------------------------------------------------------
                    modify_recognized = TRUE;
                    printAllVersionStrings(cart, sgoo_p);
                } else if ((cib_ptr->cibdatln == strlen("HELP")) &&
                           (strncmp(message, "HELP", cib_ptr->cibdatln) == 0)) {
                    // -------------------------------------------------------
                    // Print the help text.
                    // -------------------------------------------------------
                    modify_recognized = TRUE;
                    char helpMessage[128];
                    snprintf(helpMessage, 128, ANGEL_RELOAD_HELP_TEXT, "RELOAD");
                    write_to_operator(helpMessage, cart);
                    snprintf(helpMessage, 128, ANGEL_VERSION_HELP_TEXT, "VERSION");
                    write_to_operator(helpMessage, cart);
                    snprintf(helpMessage, 128, ANGEL_TRACE_HELP_TEXT, "TRACE=Y/N");
                    write_to_operator(helpMessage, cart);
                    snprintf(helpMessage, 128, ANGEL_SERVERS_HELP_TEXT, "DISPLAY,SERVERS");
                    write_to_operator(helpMessage, cart);
                } else {
                    // -------------------------------------------------------
                    // See if it's a key/value command.
                    // -------------------------------------------------------
                    char* equals_pos = strchr(message, '=');
                    if (equals_pos != NULL) {
                        int key_len = equals_pos - message;
                        if (key_len > 0) {
                            if (memcmp(message, "TRACE", key_len) == 0) {
                                angel_task_data* atd_p = getAngelTaskData();
                                if (strcmp(equals_pos + 1, "Y") == 0) {
                                    modify_recognized = TRUE;
                                    *(atd_p->trc_level_p) = trc_level_detailed;
                                } else if (strcmp(equals_pos + 1, "N") == 0) {
                                    modify_recognized = TRUE;
                                    *(atd_p->trc_level_p) = trc_level_exception;
                                }
                            }
                        }
                    }
                }

                if (modify_recognized == FALSE) {
                    // ---------------------------------------------------------
                    // Unknown command -- print an error message.
                    // ---------------------------------------------------------
                    int errorMessageLen = cib_ptr->cibdatln + 128;
                    char errorMessage[errorMessageLen];
                    snprintf(errorMessage, errorMessageLen, MODIFY_DISPLAY_ANGEL_UNRECOGNIZED, message);
                    write_to_operator(errorMessage, cart);
                }
            }

            rc = free_cib_from_chain(com_ptr, com_ptr->comcibpt);
            TRACE_BAD_RC(rc, _TP_BAD_RC_3, "angel_functions BBGZDRM_Run QEDIT FREE Bad return code");
        }
    }

    // -----------------------------------------------------------------
    // Tell the caller to reload if that's what we're going to do.
    // -----------------------------------------------------------------
    if (reload_module == 1) {
        run_rc = BBGZDRM_Run_Reload;
    }

    return run_rc;
}

// UnInitialize the Angel
int BBGZDRM_UnInit(void) {
    // Indicate that the angel is inactive
    AngelStatusFlags_t* flags_p = NULL;
    angel_process_data* apd_p = getAngelProcessData();
    bbgzsgoo* sgoo_p = apd_p->bbgzsgoo_p;
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

    return 0;
}

// ReInitialize the Angel at a new version of the DRM
int BBGZDRM_ReInit(bbgzarmv* exsting, bbgzarmv* future) {
    return updateSGOO();
}

// Return the version string
char* getDynamicReplaceableVersionString(void) {
    int maxVersionStringLen = 16;
    char* verString = malloc(maxVersionStringLen);
    if (verString != NULL) {
        snprintf(verString, maxVersionStringLen, "%i",
#ifdef ANGEL_GENERATE_VER
                 ANGEL_GENERATE_VER
#else
                 BBGZ_DYN_MODULE_CODE_MAJOR_VERSION
#endif
        );
    }
    return verString;
}

// Return the version integer
int getDynamicReplaceableVersionInt(void) {
#ifdef ANGEL_GENERATE_VER
    return ANGEL_GENERATE_VER;
#else
    return BBGZ_DYN_MODULE_CODE_MAJOR_VERSION;
#endif
}


