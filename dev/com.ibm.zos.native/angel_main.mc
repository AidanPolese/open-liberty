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

#include "include/angel_bgvt_services.h"
#include "include/angel_check_main.h"
#include "include/angel_check_module.h"
#include "include/angel_dynamic_replaceable_module.h"
#include "include/angel_fixed_shim_module.h"
#include "include/angel_pc_initialization.h"
#include "include/angel_sgoo_services.h"
#include "include/angel_task_data.h"
#include "include/bbgzsgoo.h"
#include "include/bpx_load.h"
#include "include/common_defines.h"
#include "include/ieantc.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_contents_supervisor.h"
#include "include/mvs_resmgr.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/mvs_wto.h"
#include "include/ras_tracing.h"

#include "include/gen/csvlpret.h"
#include "include/gen/ihaassb.h"
#include "include/gen/native_messages_mc.h"

#define RAS_MODULE_CONST  RAS_MODULE_ANGEL_MAIN

#define _TP_ANGEL_MAIN_CALL_BBGZADRM_REINITIALIZE       1
#define _TP_ANGEL_MAIN_RETURN_BBGZADRM_REINITIALIZE     2
#define _TP_ANGEL_MAIN_NOMEM_PARSE_ARGS                 3
#define _TP_ANGEL_MAIN_CALL_BBGZADRM_INITIALIZE         4
#define _TP_ANGEL_MAIN_RETURN_BBGZADRM_INITIALIZE       5
#define _TP_ANGEL_MAIN_NOMEM_NEW_ARMV                   6
#define _TP_ANGEL_MAIN_FREEING_OLD_ARMV                 7
#define _TP_ANGEL_MAIN_OLD_ARM_IN_USE                   8
#define _TP_ANGEL_MAIN_ERROR_NAME_TOKEN_OLD_ARMV        9
#define _TP_ANGEL_MAIN_INFO_BBGZAFSM                   10
#define _TP_ANGEL_MAIN_LOAD_BBGZAFSM                   11
#define _TP_ANGEL_MAIN_LOAD_BBGZADRM                   12
#define _TP_ANGEL_MAIN_CALL_BBGZADRM_RUN               13
#define _TP_ANGEL_MAIN_RETURN_BBGZADRM_RUN             14
#define _TP_ANGEL_MAIN_ERROR_RELOAD_BBGZADRM           15
#define _TP_ANGEL_MAIN_ERROR_BBGZADRM_CLEANUP          16
#define _TP_ANGEL_MAIN_RETURN_BBGZADRM_CLEANUP         17
#define _TP_ANGEL_MAIN_ERROR_LOAD_BBGZADRM             18
#define _TP_ANGEL_MAIN_ERROR_LOAD_BBGZAFSM             19
#define _TP_ANGEL_MAIN_FAIL_CREATE_SGOO                20
#define _TP_ANGEL_MAIN_THROW_AWAY_ARMV_POOL            21
#define _TP_ANGEL_MAIN_FAIL_CREATE_APD                 22
#define _TP_ANGEL_MAIN_ANGEL_ENQ_FAIL                  23
#define _TP_ANGEL_MAIN_ERROR_LOAD_BBGZACHK             24

#define _TP_LOAD_ANGEL_CHECK_LOAD_BBGZACHK             40
#define _TP_LOAD_ANGEL_CHECK_INFO_BBGZACHK             41
#define _TP_LOAD_ANGEL_CHECK_ERROR_LOAD_BBGZACHK       42

#define LOAD_DRM_OK 0
#define LOAD_DRM_FAIL 4



/**
 * Prepares the ARMV cell pool for use with a new angel instance.  If an ARMV
 * was loaded by a previous angel instance, the cell will be copied into the
 * new ARMV cell pool.
 *
 * @param sgoo_p A pointer to the SGOO control block.
 * @param cur_armv_sequence_p A pointer to the current ARMV sequence number.
 */
static void prepareArmvCellPoolForNewAngelInstance(bbgzsgoo* sgoo_p, unsigned char* cur_armv_sequence_p) {
    // -----------------------------------------------------------------------
    // On the initial load, make sure the ARMV cell pool is created.  We
    // allocate space for 256 cells.  We clobber this storage on every angel
    // restart.  Cells are never returned to the pool because they may be
    // referenced at any time by another server who has yet to register with
    // the angel.  On an angel restart, the storage is reused with the
    // understanding that no servers are started.
    // -----------------------------------------------------------------------

    // -------------------------------------------------------------------
    // Make a copy of the current ARMV if there is one.
    // -------------------------------------------------------------------
    bbgzarmv* cur_armv_p = NULL;
    bbgzarmv cur_armv;
    if (sgoo_p->bbgzsgoo_armv != 0L) {
        memcpy(&cur_armv, (void*)(sgoo_p->bbgzsgoo_armv), sizeof(cur_armv));
        cur_armv_p = &cur_armv;
    }

    // -------------------------------------------------------------------
    // Figure out how big the ARMV cell pool is supposed to be.
    // -------------------------------------------------------------------
    long long number_of_armv_cells = 256L;
    long long armv_cell_size = sizeof(bbgzarmv);

    if (sgoo_p->bbgzsgoo_armv_cellpool_id != 0L) {
        long long current_num_armv_cells = getCellPoolTotalCells(sgoo_p->bbgzsgoo_armv_cellpool_id);
        long long current_num_armv_extents = getNumberOfExtentsInCellPool(sgoo_p->bbgzsgoo_armv_cellpool_id);
        long long current_armv_cell_size = getCellPoolCellSize(sgoo_p->bbgzsgoo_armv_cellpool_id);

        // -------------------------------------------------------------------
        // Avoid the case where the cell pool was allocated by an early
        // version of Liberty.  Throw it away if necessary.
        // -------------------------------------------------------------------
        if ((current_num_armv_cells != number_of_armv_cells) ||
            (current_num_armv_extents != 1L) ||
            (current_armv_cell_size != armv_cell_size)) {

            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(_TP_ANGEL_MAIN_THROW_AWAY_ARMV_POOL),
                            "Throwing away invalid ARMV cell pool",
                            TRACE_DATA_PTR(sgoo_p->bbgzsgoo_armv_cellpool_stg, "Cell pool storage"),
                            TRACE_DATA_LONG(sgoo_p->bbgzsgoo_armv_cellpool_len, "Cell pool storage length"),
                            TRACE_DATA_HEX_LONG(sgoo_p->bbgzsgoo_armv_cellpool_id, "Cell pool token"),
                            TRACE_DATA_LONG(current_num_armv_cells, "Number of cells"),
                            TRACE_DATA_LONG(current_num_armv_extents, "Number of extents"),
                            TRACE_DATA_LONG(current_armv_cell_size, "Size of each cell"),
                            TRACE_DATA_END_PARMS);
            }

            sgoo_p->bbgzsgoo_armv_cellpool_id = 0L;
            sgoo_p->bbgzsgoo_armv_cellpool_stg = NULL;
            sgoo_p->bbgzsgoo_armv_cellpool_len = 0L;
        }
    }

    // -------------------------------------------------------------------
    // Make sure that there is storage hung off the SGOO for the cell pool.
    // -------------------------------------------------------------------
    if (sgoo_p->bbgzsgoo_armv_cellpool_stg == NULL) {
        long long storage_for_armv_cell_pool_len = computeCellPoolStorageRequirement(number_of_armv_cells, armv_cell_size);
        void* storage_for_armv_cell_pool =
            allocateSGOOsharedStorage(sgoo_p, storage_for_armv_cell_pool_len);
        if (storage_for_armv_cell_pool != NULL) {
            sgoo_p->bbgzsgoo_armv_cellpool_stg = storage_for_armv_cell_pool;
            sgoo_p->bbgzsgoo_armv_cellpool_len = storage_for_armv_cell_pool_len;
        }
    }

    // -------------------------------------------------------------------
    // Clobber the cell pool.
    // -------------------------------------------------------------------
    buildCellPoolFlags armv_cell_pool_flags;
    memset(&armv_cell_pool_flags, 0, sizeof(armv_cell_pool_flags));
    sgoo_p->bbgzsgoo_armv_cellpool_id =
        buildCellPool(sgoo_p->bbgzsgoo_armv_cellpool_stg,
                      sgoo_p->bbgzsgoo_armv_cellpool_len,
                      sizeof(bbgzarmv),
                      "BBGZARMP",
                      armv_cell_pool_flags);

    // -------------------------------------------------------------------
    // If we made a copy of the ARMV, get an ARMV cell, copy the ARMV, and
    // hang it off the SGOO.  Be sure to reset the sequence number now that
    // we've re-started the angel.
    // -------------------------------------------------------------------
    if (cur_armv_p != NULL) {
        bbgzarmv* shared_armv_p = (bbgzarmv*) getCellPoolCell(sgoo_p->bbgzsgoo_armv_cellpool_id);
        memcpy(shared_armv_p, cur_armv_p, sizeof(bbgzarmv));
        shared_armv_p->bbgzarmv_instancecount = *cur_armv_sequence_p;
        *cur_armv_sequence_p = (*cur_armv_sequence_p) + 1;
        sgoo_p->bbgzsgoo_armv = (long long)shared_armv_p;
    }

}


/**
 * Initialize, or re-initialize, the dynamic replaceable module, and the ARMV
 * which will point to it.
 *
 * @param sgoo_p A pointer to the SGOO control block.
 * @param drm_details_p A pointer to the structure returned from load_from_hfs,
 *                      which describes the location where the DRM was loaded
 *                      and its length.
 * @param drm_p A pointer to the DRM entry point, that was loaded by
 *              load_from_hfs.
 * @param armv_p A pointer to the ARMV which is to be initialized.
 * @param cur_armv_sequence_p A pointer to the current ARMV sequence number.  The sequence
 *                            is incremented by one each time an ARMV is created.  This helps
 *                            servers know if they need to attach to a new ARMV when one is
 *                            available.
 * @param initialize A flag which is set to 1 if this is the first DRM loaded
 *                   for this angel instance, and initialize() should be called
 *                   on the DRM to initialize it.  Otherwise, this is a
 *                   refresh of the DRM, and reinitialize() should be called.
 *
 * @return 0 if the DRM was initialized successfully.  If the DRM was not
 *         initialized successfully, the ARMV should be returned to the ARMV
 *         pool and the DRM should be unloaded.
 */
static initializeARMVandDRM(bbgzsgoo* sgoo_p, loadhfs_details* drm_details_p, bbgzadrm* drm_p, bbgzarmv* armv_p, unsigned char* cur_armv_sequence_p, unsigned char initialize) {
    int init_rc = -1;

    // -----------------------------------------------------------------------
    // Initialize the new ARMV
    // -----------------------------------------------------------------------
    memset(armv_p, 0, sizeof(*armv_p));
    memcpy(armv_p->bbgzarmv_eyecatcher, "BBGZARMV", 8);
    armv_p->bbgzarmv_version = 1;
    armv_p->bbgzarmv_length = sizeof(*armv_p);
    armv_p->bbgzarmv_drm_len = drm_details_p->mod_len;
    armv_p->bbgzarmv_drm_mod_p = drm_details_p->mod_p;
    armv_p->bbgzarmv_drm = drm_p;
    memcpy(armv_p->bbgzarmv_drm_del_token,
           drm_details_p->delete_token,
           sizeof(armv_p->bbgzarmv_drm_del_token));
    armv_p->bbgzarmv_instancecount = *cur_armv_sequence_p;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(_TP_ANGEL_MAIN_CALL_BBGZADRM_INITIALIZE),
                "Calling bbgzdrm initialize",
                TRACE_DATA_INT(initialize, "Initialize"),
                TRACE_DATA_PTR((initialize == 1) ? (void*)(drm_p->initialize) : (void*)(drm_p->reinitialize), "entry point"),
                TRACE_DATA_END_PARMS);
    }

    if (initialize == 1) {
        init_rc = drm_p->initialize();
    } else {
        init_rc = drm_p->reinitialize((bbgzarmv*)(sgoo_p->bbgzsgoo_armv), armv_p);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(_TP_ANGEL_MAIN_RETURN_BBGZADRM_INITIALIZE),
                "Returned from bbgzdrm initialize",
                TRACE_DATA_INT(init_rc, "initialize return code"),
                TRACE_DATA_END_PARMS);
    }

    if (init_rc == 0) {
        *cur_armv_sequence_p = (*cur_armv_sequence_p) + 1;
    }

    return init_rc;
}


/**
 * Cleans up an old ARMV.
 *
 * @param old_armv_p A pointer to the old ARMV which is being cleaned up.
 */
static void cleanupOldARMV(bbgzarmv* old_armv_p) {
    bbgzarmv_usecount_s old_usecount;
    bbgzarmv_usecount_s new_usecount;

    int cs_rc = -1;

    memcpy(&old_usecount,
            &(old_armv_p->bbgzarmv_usecount),
            sizeof(old_usecount));

    while (cs_rc != 0) {
        memcpy(&new_usecount, &old_usecount, sizeof(new_usecount));
        new_usecount.inactive = 1;
        cs_rc = __cs1(&old_usecount, &(old_armv_p->bbgzarmv_usecount), &new_usecount);
    }

    // ---------------------------------------------------------
    // If no one was using the old ARMV, we can free the old
    // copy of the DRM.  We don't want to free the ARMV because
    // someone could be referencing it (it was just hung off
    // of the SGOO a second ago).
    // ---------------------------------------------------------
    if (new_usecount.count == 0) {
        loadhfs_details old_drm_details;

        if (TraceActive(trc_level_detailed)) {
          TraceRecord(
            trc_level_detailed,
            TP(_TP_ANGEL_MAIN_FREEING_OLD_ARMV),
            "angel_main Freeing old ARMV",
            TRACE_DATA_RAWDATA(sizeof(bbgzarmv), old_armv_p, "ARMV"),
            TRACE_DATA_END_PARMS);
        }

        old_drm_details.mod_len = old_armv_p->bbgzarmv_drm_len;
        old_drm_details.mod_p = old_armv_p->bbgzarmv_drm_mod_p;
        old_drm_details.entry_p = old_armv_p->bbgzarmv_drm;
        memcpy(old_drm_details.delete_token,
               old_armv_p->bbgzarmv_drm_del_token,
               sizeof(old_drm_details.delete_token));
        unload_from_hfs(&old_drm_details);
    } else {
        // -------------------------------------------------------
        // Someone is still using the old ARMV so we can't free
        // it yet.  Make a name token so that we can find it in
        // a dump if we need to.
        // -------------------------------------------------------
        char armv_name[16];
        char armv_token[16];

        if (TraceActive(trc_level_detailed)) {
          TraceRecord(
            trc_level_detailed,
            TP(_TP_ANGEL_MAIN_OLD_ARM_IN_USE),
            "angel_main Can't free old ARMV yet, making name token",
            TRACE_DATA_RAWDATA(sizeof(bbgzarmv), old_armv_p, "ARMV"),
            TRACE_DATA_END_PARMS);
        }

        memset(armv_name, 0, sizeof(armv_name));
        memcpy(armv_name, "BBGZARMV", 8);
        armv_name[8] = old_armv_p->bbgzarmv_instancecount;

        memset(armv_token, 0, sizeof(armv_token));
        memcpy(armv_token, &old_armv_p, sizeof(old_armv_p));

        int nametoken_rc;
        iean4cr(IEANT_HOME_LEVEL,
                armv_name,
                armv_token,
                IEANT_NOPERSIST,
                &nametoken_rc);

        if (nametoken_rc != 0) {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(
                    trc_level_exception,
                    TP(_TP_ANGEL_MAIN_ERROR_NAME_TOKEN_OLD_ARMV),
                    "angel_main error creating name token for old ARMV",
                    TRACE_DATA_INT(nametoken_rc, "RC"),
                    TRACE_DATA_END_PARMS);
            }
        }
    }

}


/**
 * Loads the dynamic replaceable module and creates and ARMV for it.  The version number of the
 * module is checked, and if the version number is the same as the version number currently
 * loaded, no ARMV is created.
 *
 * @param drm_path The path in the file system to the BBGZADRM module.
 * @param sgoo_p A pointer to the SGOO control block
 * @param cur_armv_sequence_p A pointer to the current ARMV sequence number.  The sequence
 *                            is incremented by one each time an ARMV is created.  This helps
 *                            servers know if they need to attach to a new ARMV when one is
 *                            available.
 * @param reload Set to TRUE if this is a reload request, or FALSE if this is the initial load
 *               of the BBGZADRM module.
 *
 * @return 0 if the DRM was loaded successfully, nonzero if not
 */
static int
load_drm(char* drm_path, bbgzsgoo* sgoo_p, unsigned char* cur_armv_sequence_p, unsigned char reload) {
    loadhfs_details* drm_details_p;
    int load_drm_rc = LOAD_DRM_OK;

    // -----------------------------------------------------------------------
    // On the initial load, make sure the ARMV cell pool is created.  We
    // allocate space for 256 cells.  We clobber this storage on every angel
    // restart.  Cells are never returned to the pool because they may be
    // referenced at any time by another server who has yet to register with
    // the angel.  On an angel restart, the storage is reused with the
    // understanding that no servers are started.
    // -----------------------------------------------------------------------
    if (reload == FALSE) {
        prepareArmvCellPoolForNewAngelInstance(sgoo_p, cur_armv_sequence_p);
    }

    // -----------------------------------------------------------------
    // Load the dynamically replaceable module
    // -----------------------------------------------------------------
    drm_details_p = load_from_hfs(drm_path);
    if (drm_details_p != NULL) {
        bbgzadrm* drm_p = (bbgzadrm*) (((long long)(drm_details_p->entry_p)) & 0xFFFFFFFFFFFFFFFEL);

        // ---------------------------------------------------------------
        // Check the version number of the DRM against the one that is
        // hung off the ARMV of the SGOO.  If this is a new SGOO it
        // won't have an ARMV yet.
        // ---------------------------------------------------------------
        int old_drm_major_version = 0;
        unsigned short old_drm_minor_version = 0;
        int new_drm_major_version = 0;
        unsigned short new_drm_minor_version = 0;

        unsigned char keep_new_drm = 0;
        unsigned char initialize_drm = 0;

        if (sgoo_p->bbgzsgoo_armv != 0L) {
            old_drm_major_version = ((bbgzarmv*)(sgoo_p->bbgzsgoo_armv))->bbgzarmv_drm->getVersionNumber();
            old_drm_minor_version = ((bbgzarmv*)(sgoo_p->bbgzsgoo_armv))->bbgzarmv_drm->verInfo.minorVersion;

            new_drm_major_version = drm_p->getVersionNumber();
            new_drm_minor_version = drm_p->verInfo.minorVersion;

            if ((old_drm_major_version != new_drm_major_version) ||
                (old_drm_minor_version != new_drm_minor_version)) {
                keep_new_drm = 1;
            }
        } else {
            keep_new_drm = 1;
            initialize_drm = 1;
        }

        if (keep_new_drm == 1) {
            lpmea modinfo;
            int addReturnCode = contentsSupervisorAddToDynamicLPA(drm_path,
                                                                  drm_details_p->entry_p,
                                                                  drm_details_p->mod_p,
                                                                  drm_details_p->mod_len,
                                                                  &modinfo,
                                                                  NULL);
            if (addReturnCode == 0) {
                memcpy(drm_details_p->delete_token, modinfo.lpmeadeletetoken, sizeof(drm_details_p->delete_token));
            }
            bbgzarmv* armv_p = (bbgzarmv*) getCellPoolCell(sgoo_p->bbgzsgoo_armv_cellpool_id);

            if (armv_p != NULL) {
                if (initializeARMVandDRM(sgoo_p, drm_details_p, drm_p, armv_p, cur_armv_sequence_p, initialize_drm) == 0) {
                    // -----------------------------------------------------------
                    // Set the new ARMV into the SGOO.  No need to compare and
                    // swap since the angel main is the only task which writes
                    // to this variable.
                    // -----------------------------------------------------------
                    bbgzarmv* old_armv_p = (bbgzarmv*) sgoo_p->bbgzsgoo_armv;
                    unsigned char old_armv_instancecount = old_armv_p->bbgzarmv_instancecount;
                    sgoo_p->bbgzsgoo_armv = (long long)armv_p;

                    // -----------------------------------------------------------
                    // If we had an old ARMV, we need to deactivate it and
                    // possibly clean it up.
                    // -----------------------------------------------------------
                    if (old_armv_p != NULL) {
                        cleanupOldARMV(old_armv_p);
                    }
                } else {
                    freeCellPoolCell(sgoo_p->bbgzsgoo_armv_cellpool_id, armv_p);
                    unload_from_hfs(drm_details_p);

                    load_drm_rc = LOAD_DRM_FAIL;
                }
            } else {
                if (TraceActive(trc_level_exception)) {
                    TraceRecord(trc_level_exception,
                            TP(_TP_ANGEL_MAIN_NOMEM_NEW_ARMV),
                            "Could not allocate memory for ARMV",
                            TRACE_DATA_END_PARMS);
                }

                load_drm_rc = LOAD_DRM_FAIL;
            }
        } else {
            unload_from_hfs(drm_details_p);
            bbgzarmv* armv_p = (bbgzarmv*)(sgoo_p->bbgzsgoo_armv);
            drm_p = armv_p->bbgzarmv_drm;
        }

        free(drm_details_p);
    } else {
        load_drm_rc = LOAD_DRM_FAIL;
    }

    return load_drm_rc;
}

/**
 * Structure wrapping the possible arguments for the angel main.
 */
struct angel_main_args {
    /**
     * The angel is performing a cold start and we should discard the global
     * angel-related control blocks and re-load any shared angel code.
     */
    int cold_start : 1;

    /**
     * The initial trace level for the angel.
     */
    int debug_trace_level : 3;

    /**
     * Available for use.
     */
    int _available : 28;

    /**
     * Null-terminated angel name.  0-54 characters.
     */
    char angel_name[56];
};

/**
 * Command line parameter string for a cold start
 */
#define COLD_START_ARG "COLD"

/**
 * Command line parameter string for debug tracing
 */
#define DEBUG_ARG "DEBUG"

/**
 * Command line parameter for angel name
 */
#define NAME_ARG "NAME"
#define NAME_ARG_MAX_LEN 54

/**
 * Converts a trace level string (0, 1, 2, 3, or 4) to its integer conterpart.
 * This function is used while the MCRTL atoi() is broken.
 *
 * @param A pointer to a single byte containing the EBCDIC trace level.  The
 *        only supported values are '0', '1', '2', '3' or '4'.
 *
 * @return The integer trace level.
 */
static int traceLevelATOI(char* traceLevelDigit) {
    if (*traceLevelDigit == '1') return 1;
    if (*traceLevelDigit == '2') return 2;
    if (*traceLevelDigit == '3') return 3;
    if (*traceLevelDigit == '4') return 4;
    return 0;
}

#define VALID_ANGEL_NAME_CHARS "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$+-/:<>=?@[]^_`{}|~"

/**
 * See if the angel name contains any invalid characters.  If it does, complain
 * about it.
 *
 * @param angelName The null-terminated angel name.
 *
 * @return 0 if the angel name contains any invalid characters.
 */
static int parseAngelNameForUnsupportedCharacters(char* angelName) {
    int rc = 0;

    // It would be nice to trace these things but we haven't set up tracing yet by
    // this point, because the trace settings are on the arguments that we're
    // parsing.
    int angelNameLen = strlen(angelName);
    int validLen = strspn(angelName, VALID_ANGEL_NAME_CHARS);
    if (validLen != angelNameLen) {
        char zbuf[128];
        char invalidChar[2];
        invalidChar[0] = *(angelName+validLen);
        invalidChar[1] = 0;
        char invalidPos[16];
        snprintf(invalidPos, sizeof(invalidPos), "%i", validLen + 1);
        snprintf(zbuf, sizeof(zbuf), ANGEL_NAME_UNSUPPORTED_CHARACTER, invalidChar, invalidPos);
        write_to_programmer(zbuf);
        rc = -1;
    }

    return rc;
}

/**
 * Parse the arguments from the angel main
 *
 * @param args_p A pointer to the argument struct that will be filled in by
 *               this method.
 * @param argc The number of arguments as reported by the C runtime.
 * @param argv A pointer to the command line arguments as provided by the C
 *             runtime.
 *
 * @return 0 if the arguments were parsed successfully, or if non-fatal errors
 *         were encountered.  Non-zero if the server should terminate.
 */
static int parseArguments(struct angel_main_args* args_p, int argc, char** argv)
{
    char zbuf[128];
    int rc = 0;

    for (int z = 1; z < argc; z++) {
        char* string = *(argv + z);
        char* equals_pos = strchr(string, '=');
        if (equals_pos != NULL) {
            // Key/value pair
            int key_len = equals_pos - string;
            if (key_len > 0) {
                char* key = malloc(key_len + 1);
                if (key != NULL) {
                    memcpy(key, string, key_len);
                    *(key + key_len) = 0;
                    if (strcmp(key, COLD_START_ARG) == 0) {
                        if (strcmp(equals_pos + 1, "Y") == 0) {
                            args_p->cold_start = 1;
                        } else if (strcmp(equals_pos + 1, "N") != 0) {
                            // Error: unsupported value
                            snprintf(zbuf, sizeof(zbuf), ANGEL_PROCESS_UNRECOGNIZED_KEY_VALUE_ARGV, equals_pos + 1, key);
                            write_to_programmer(zbuf);
                        }
                    } else if (strcmp(key, DEBUG_ARG) == 0) {
                        // ---------------------------------------------------
                        // Don't use atoi() to check the validity of the
                        // value, because it returns an undefined value if
                        // it can't parse the string.
                        // ---------------------------------------------------
                        if ((*(equals_pos + 1) >= '0') && (*(equals_pos + 1) <= '4') && (strlen(equals_pos + 1) == 1)) {
                            // TODO: replace traceLevelATOI() with atoi() once atoi() in the MCRTL is fixed.
                            args_p->debug_trace_level = traceLevelATOI(equals_pos + 1);
                        } else {
                            // Error: unsupported value
                            snprintf(zbuf, sizeof(zbuf), ANGEL_PROCESS_UNRECOGNIZED_KEY_VALUE_ARGV, equals_pos + 1, key);
                            write_to_programmer(zbuf);
                        }
                    } else if (strcmp(key, NAME_ARG) == 0) {
                        // ---------------------------------------------------
                        // Parse the angel name.  Make sure that:
                        // 1) It's <= 54 characters
                        // 2) It only uses valid characters.
                        // ---------------------------------------------------
                        char* val_p = equals_pos + 1;
                        if (strlen(val_p) > NAME_ARG_MAX_LEN) {
                            write_to_programmer(ANGEL_NAME_TOO_LONG);
                            rc = -1;
                        } else  if (parseAngelNameForUnsupportedCharacters(val_p) != 0) {
                            rc = -1;
                        } else {
                            strncpy(args_p->angel_name, val_p, NAME_ARG_MAX_LEN);
                        }
                    } else {
                        // Error: unrecognized arg
                        snprintf(zbuf, sizeof(zbuf), ANGEL_PROCESS_UNRECOGNIZED_VALUE_ARGV, string);
                        write_to_programmer(zbuf);
                    }
                    free(key);
                } else {
                    // -------------------------------------------------------
                    // Error: out of memory.  We are so early in startup...
                    // try tracing, but it probably won't work since it also
                    // needs memory.
                    // -------------------------------------------------------
                    if (TraceActive(trc_level_exception)) {
                      TraceRecord(
                          trc_level_exception,
                          TP(_TP_ANGEL_MAIN_NOMEM_PARSE_ARGS),
                          "angel_main out of memory parsing args",
                          TRACE_DATA_STRING(string, "ARG"),
                          TRACE_DATA_END_PARMS);
                    }

                }
            } else {
                // Error: value length is zero
                snprintf(zbuf, sizeof(zbuf), ANGEL_PROCESS_UNRECOGNIZED_VALUE_ARGV, string);
                write_to_programmer(zbuf);
            }
        } else {
            // Just a value.  Currently none supported.
            snprintf(zbuf, sizeof(zbuf), ANGEL_PROCESS_UNRECOGNIZED_VALUE_ARGV, string);
            write_to_programmer(zbuf);
        }
    }

    return rc;
}

// Return codes from the angel main.
// TODO: Find a place to document these.
#define ANGEL_MAIN_RETURN_OK             0
#define ANGEL_MAIN_FAIL_LOAD_DRM         4
#define ANGEL_MAIN_FAIL_LOAD_FSM         8
#define ANGEL_MAIN_FAIL_LOAD_SGOO       12
#define ANGEL_MAIN_FAIL_LOAD_BBGZACHK   14
#define ANGEL_MAIN_FAIL_CREATE_APD      16
#define ANGEL_MAIN_FAIL_DUPLICATE_ANGEL 20
#define ANGEL_MAIN_FAIL_PARSE_ARGS 24

#define LOAD_ANGEL_CHECK_RETURN_OK          0 
#define LOAD_ANGEL_CHECK_FAIL_LOAD_BBGZACHK 4

int load_angel_check(bgvt* __ptr32 bgvt_p, char* angel_home) {
  loadhfs_details* bbgzachk_details_p;
  loadhfs_details old_bbgzachk_details;
  bbgzachk* bbodbgvt_bbgzachk_p;
  int load_angel_check_rc = LOAD_ANGEL_CHECK_RETURN_OK;

  // ---------------------------------------------------------------
  // Load the angel check module.  This needs to be loaded in
  // common so that any process on the system can call it.
  // ---------------------------------------------------------------
  char* bbgzachk_path = malloc(strlen(angel_home) + strlen("bbgzachk") + 2);
  sprintf(bbgzachk_path, "%s/%s", angel_home, "bbgzachk");

  if (TraceActive(trc_level_detailed)) {
    TraceRecord(
      trc_level_detailed,
      TP(_TP_LOAD_ANGEL_CHECK_LOAD_BBGZACHK),
      "load_angel_check loading BBGZACHK",
      TRACE_DATA_STRING(bbgzachk_path, "bbgzachk path"),
      TRACE_DATA_END_PARMS);
  }

  bbgzachk_details_p = load_from_hfs(bbgzachk_path);

  if (NULL != bbgzachk_details_p) {
    bbgzachk* bbgzachk_p = (bbgzachk*) (((long long)(bbgzachk_details_p->entry_p)) & 0xFFFFFFFFFFFFFFFEL);
    bbodbgvt_bbgzachk_p = bgvt_p->bbodbgvt_bbgzachk;

    // ---------------------------------------------------------------
    // Get the bbgzachk version number from the loaded module
    // and reference the module in the bgvt if it's newer than what's there
    // ---------------------------------------------------------------
    int incoming_angel_check_version = bbgzachk_p->angel_check_version;
    int bgvt_angel_check_version = 0;
    if (NULL != bbodbgvt_bbgzachk_p) {
        bgvt_angel_check_version = bgvt_p->bbodbgvt_bbgzachk_version;
    }

    if (TraceActive(trc_level_detailed)) {
      TraceRecord(
        trc_level_detailed,
        TP(_TP_LOAD_ANGEL_CHECK_INFO_BBGZACHK),
        "load_angel_check BBGZACHK information",
        TRACE_DATA_INT(incoming_angel_check_version, "Incoming version"),
        TRACE_DATA_INT(bgvt_angel_check_version, "BGVT version"),
        TRACE_DATA_END_PARMS);
    }

    // ---------------------------------------------------------------
    // If the version numbers differ, use the newer version
    // ---------------------------------------------------------------
    if (incoming_angel_check_version > bgvt_angel_check_version) {
      old_bbgzachk_details.entry_p      = bgvt_p->bbodbgvt_bbgzachk;
      bgvt_p->bbodbgvt_bbgzachk         = bbgzachk_p;
      bgvt_p->bbodbgvt_bbgzachk_version = incoming_angel_check_version;

    } else {
      unload_from_hfs(bbgzachk_details_p);
    }
    free(bbgzachk_details_p);
  } else {
    if (TraceActive(trc_level_exception)) {
      TraceRecord(
        trc_level_exception,
        TP(_TP_LOAD_ANGEL_CHECK_ERROR_LOAD_BBGZACHK),
        "load_angel_check failed to load BBGZACHK",
        TRACE_DATA_END_PARMS);
    }

    load_angel_check_rc = LOAD_ANGEL_CHECK_FAIL_LOAD_BBGZACHK;
  }
  free(bbgzachk_path);
  return load_angel_check_rc;
}

/**
 * The angel main.
 *
 * @param argc The number of arguments as provided by the C runtime.
 * @param argv A pointer to the argument list as provided by the C runtime.
 *
 * @return 0 on normal shutdown, nonzero if error.
 */
#pragma prolog(main, "AAUTHPRL")
#pragma epilog(main, "AAUTHEPL")
int main(int argc, char** argv) {

    loadhfs_details* fsm_details_p;
    loadhfs_details old_fsm_details;
    struct __csysenv_s mysysenv;
    unsigned int angel_main_rc = ANGEL_MAIN_RETURN_OK;

    void* sharedAboveStorage = NULL;
    unsigned char cur_armv_sequence = 1;

    // -----------------------------------------------------------------------
    // Get into supervisor state.  The angel process will run in supervisor
    // state.
    // -----------------------------------------------------------------------
    switchToSupervisorState();

    // -----------------------------------------------------------------------
    // Set the default trace level into the angel main stack prefix area.
    // **NOTE** This code assumes that this (the main) is the only task in
    // the angel process.  If there are other tasks, they need to also set
    // the trace level pointer.
    // -----------------------------------------------------------------------
    unsigned char trc_level = trc_level_exception;
    angel_task_data* atd_p = getAngelTaskData();
    atd_p->trc_level_p = &trc_level;

    // -----------------------------------------------------------------------
    // Set up our metal C environment.  We are single threaded in the angel
    // and so we will use the heap provided by the metal C runtime library.
    // -----------------------------------------------------------------------
    initenv(&mysysenv, getAddressSpaceSupervisorStateUserToken(), NULL);

    // -----------------------------------------------------------------------
    // Parse the arguments (if any).
    // -----------------------------------------------------------------------
    int parseArgsRC = 0;
    struct angel_main_args args;
    memset(&args, 0, sizeof(args));
    args.debug_trace_level = trc_level_exception;
    if (argc > 1) {
        parseArgsRC = parseArguments(&args, argc, argv);
    }

    if (parseArgsRC == 0) {
    // -----------------------------------------------------------------------
    // Load the initial trace value based on the debug flag.
    // -----------------------------------------------------------------------
    trc_level = args.debug_trace_level;

    // -----------------------------------------------------------------
    // Load the BGVT control block.
    // -----------------------------------------------------------------
    bgvt* __ptr32 bgvt_p = findOrCreateBGVT();

    // -----------------------------------------------------------------
    // Load the SGOO control block.  Create it if necessary, along with
    // the CGOO and the angel anchor if this is a named angel.  We hold
    // an ENQ here to serialize this against other angels which may be
    // starting at the same time.
    // -----------------------------------------------------------------
    enqtoken sgooCreationEnqToken;
    get_enq_exclusive_system(BBGZ_ENQ_QNAME, ANGEL_CGOO_SGOO_ANCHOR_INITIALIZATION_ENQ_RNAME, NULL, &sgooCreationEnqToken);
    bbgzsgoo* sgoo_p = createSGOO(bgvt_p, args.cold_start, args.angel_name);
    release_enq(&sgooCreationEnqToken);

    if (sgoo_p != NULL) {

      // ---------------------------------------------------------------
      // Make an angel process data.
      // ---------------------------------------------------------------
      angel_process_data* apd_p = createAngelProcessData(sgoo_p, ANGEL_PROCESS_TYPE_ANGEL);
      if (apd_p != NULL) {
        // -----------------------------------------------------------
        // Make sure that we are the only angel process with this
        // name.  If there is another one, we should stop here.  If we
        // got the ENQ, put our STOKEN into the SGOO so that other
        // angel processes can print the active angel job information
        // in a message.
        // -----------------------------------------------------------
        enqtoken angelProcessEnqToken;
        char angelProcessRNAME[256];
        if (strlen(args.angel_name) == 0) {
            strcpy(angelProcessRNAME, ANGEL_PROCESS_RNAME);
        } else {
            snprintf(angelProcessRNAME, sizeof(angelProcessRNAME), ANGEL_NAMED_PROCESS_RNAME, args.angel_name);
        }

        int enq_rc = get_enq_exclusive_system_conditional(BBGZ_ENQ_QNAME, angelProcessRNAME, &angelProcessEnqToken);
        if (enq_rc != 1) {
          if (enq_rc != 0) { // Trace if unknown error from ENQ services.
              if (TraceActive(trc_level_exception)){
                  TraceRecord(
                    trc_level_exception,
                    TP(_TP_ANGEL_MAIN_ANGEL_ENQ_FAIL),
                    "angel_main unable to check angel ENQ, possible duplicate angel process",
                    TRACE_DATA_END_PARMS);
              }
          } else { // Otherwise save STOKEN in SGOO
              psa* psa_p = NULL;
              ascb* ascb_p = psa_p->psaaold;
              assb* assb_p = ascb_p->ascbassb;
              sgoo_p->bbgzsgoo_angel_stoken = *((long long*)(&(assb_p->assbstkn)));
          }

          // ---------------------------------------------------------------
          // Load the fixed shim module.  This needs to be loaded in
          // common so that it can be a non-space-switching PC target.
          // ---------------------------------------------------------------
          char* angel_home = strdup(argv[0]);
          char* lastSlash = strrchr(angel_home, '/');
          if (lastSlash) {
            *lastSlash = '\0';
          }

          // Load the bbgzachk module whose API can be used to check whether the angel is running
          int load_angel_check_rc = load_angel_check(bgvt_p, angel_home);
	  if (load_angel_check_rc == LOAD_ANGEL_CHECK_RETURN_OK) {

            char* fsm_path = malloc(strlen(angel_home) + strlen("bbgzafsm") + 2);
            sprintf(fsm_path, "%s/%s", angel_home, "bbgzafsm");

            if (TraceActive(trc_level_basic)) {
              TraceRecord(
                trc_level_basic,
                TP(_TP_ANGEL_MAIN_LOAD_BBGZAFSM),
                "angel_main loading BBGZFSM",
                TRACE_DATA_STRING(fsm_path, "fsm path"),
                TRACE_DATA_END_PARMS);
            }

            fsm_details_p = load_from_hfs(fsm_path);

            if (fsm_details_p != NULL) {
              bbgzafsm* fsm_p = (bbgzafsm*) (((long long)(fsm_details_p->entry_p)) & 0xFFFFFFFFFFFFFFFEL);

              // ---------------------------------------------------------------
              // Check the version number of the fixed shim module and compare
              // it to the version number of the fixed shim module in the
              // SGOO.  If they are the same, unload the new one.  If they are
              // different, we need to use the new one and abandon the old
              // one.
              // ---------------------------------------------------------------
              int old_fsm_major_version = 0;
              unsigned short old_fsm_minor_version = 0;
              int new_fsm_major_version = 0;
              unsigned short new_fsm_minor_version = 0;

              if (sgoo_p->bbgzsgoo_fsm != NULL) {
                old_fsm_major_version = sgoo_p->bbgzsgoo_fsm->getVersionNumber();
                if ((sgoo_p->bbgzsgoo_fsm->_reserved_PC7 == 0) &&
                    (sgoo_p->bbgzsgoo_fsm->v1_verInfo.version >= 1)) {
                    old_fsm_minor_version = sgoo_p->bbgzsgoo_fsm->minorVersion;
              }
              }

              new_fsm_major_version = fsm_p->getVersionNumber();
              if ((fsm_p->_reserved_PC7 == 0) &&
                  (fsm_p->v1_verInfo.version >= 1)) {
                  new_fsm_minor_version = fsm_p->minorVersion;
              }


              if (TraceActive(trc_level_basic)) {
                TraceRecord(
                  trc_level_basic,
                  TP(_TP_ANGEL_MAIN_INFO_BBGZAFSM),
                  "angel_main BBGZFSM information",
                  TRACE_DATA_INT(old_fsm_major_version, "Old major version"),
                  TRACE_DATA_SHORT(old_fsm_minor_version, "Old minor version"),
                  TRACE_DATA_INT(new_fsm_major_version, "New major version"),
                  TRACE_DATA_SHORT(new_fsm_minor_version, "New minor version"),
                  TRACE_DATA_END_PARMS);
              }

              // ---------------------------------------------------------------
              // If the version numbers differ, use the version that we just
              // loaded from the HFS.
              // ---------------------------------------------------------------
              if ((old_fsm_major_version != new_fsm_major_version) ||
                  (old_fsm_minor_version != new_fsm_minor_version)) {
                old_fsm_details.mod_len = sgoo_p->bbgzsgoo_fsm_len;
                old_fsm_details.mod_p = sgoo_p->bbgzsgoo_fsm_mod_p;
                old_fsm_details.entry_p = sgoo_p->bbgzsgoo_fsm;
                memcpy(old_fsm_details.delete_token,
                       sgoo_p->bbgzsgoo_fsm_del_token,
                       sizeof(old_fsm_details.delete_token));

                // Tell MVS about the new module
                lpmea modinfo;
                int addReturnCode = contentsSupervisorAddToDynamicLPA(fsm_path,
                                                                      fsm_details_p->entry_p,
                                                                      fsm_details_p->mod_p,
                                                                      fsm_details_p->mod_len,
                                                                      &modinfo,
                                                                      NULL);
                if (addReturnCode == 0) {
                  memcpy(fsm_details_p->delete_token, modinfo.lpmeadeletetoken, sizeof(fsm_details_p->delete_token));
                }
                sgoo_p->bbgzsgoo_fsm_len = fsm_details_p->mod_len;
                sgoo_p->bbgzsgoo_fsm_mod_p = fsm_details_p->mod_p;
                sgoo_p->bbgzsgoo_fsm = fsm_p;
                memcpy(sgoo_p->bbgzsgoo_fsm_del_token,
                       fsm_details_p->delete_token,
                       sizeof(sgoo_p->bbgzsgoo_fsm_del_token));

                if (old_fsm_details.mod_len > 0) {
                  unload_from_hfs(&old_fsm_details);
                }
              } else {
                unload_from_hfs(fsm_details_p);
                fsm_p = sgoo_p->bbgzsgoo_fsm;
              }

              createPC(sgoo_p->bbgzsgoo_fsm, bgvt_p->bbodbgvt_bbgzcgoo, sgoo_p->bbgzsgoo_pcLatentParmArea_p);

              // ---------------------------------------------------------------
              // Create a pair of RESMGRs to watch over the Angel.
              // Pass the PC latent parm area to the RESMGR so it can find the
              // SGOO address when it runs.  The latent parm area is in common
              // and is never deleted so the RESMGR will always be able to
              // access it.  The left half is still available for use since the
              // address is 31 bit (common storage).
              // ---------------------------------------------------------------
              int token = -1;
              long long parms = (long long)sgoo_p->bbgzsgoo_pcLatentParmArea_p;
              int as_rm_rc = addResourceManager(&token,
                                                &parms,
                                                BBOZRMGR_TYPE_AS,
                                                fsm_p->resmgr_stub);
              int js_rm_rc = addResourceManager(&token,
                                                &parms,
                                                BBOZRMGR_TYPE_TASK,
                                                fsm_p->resmgr_stub);

              // ---------------------------------------------------------------
              // Load the dynamically replaceable module
              // ---------------------------------------------------------------
              char* drm_path = malloc(strlen(angel_home) + strlen("bbgzadrm") + 2);
              sprintf(drm_path, "%s/%s", angel_home, "bbgzadrm");

              if (TraceActive(trc_level_basic)) {
                TraceRecord(
                  trc_level_basic,
                  TP(_TP_ANGEL_MAIN_LOAD_BBGZADRM),
                  "angel_main loading BBGZADRM",
                  TRACE_DATA_STRING(drm_path, "DRM path"),
                  TRACE_DATA_END_PARMS);
              }

              int load_drm_rc = load_drm(drm_path, sgoo_p, &cur_armv_sequence, FALSE);
              if (load_drm_rc == LOAD_DRM_OK) {
                int run_rc = -1;
                int uninit_rc = -1;

                struct bbgzadrm* drm_p = ((bbgzarmv*)(sgoo_p->bbgzsgoo_armv))->bbgzarmv_drm;

                // ---------------------------------------------------------------
                // We're pretty much finished with the startup... tell the
                // operator so.
                // ---------------------------------------------------------------
                if (strlen(args.angel_name) == 0) {
                    write_to_operator(ANGEL_PROCESS_INITIALIZATION_COMPLETE, NULL);
                } else {
                    char buffer[256];
                    snprintf(buffer, sizeof(buffer), ANGEL_PROCESS_INITIALIZATION_COMPLETE_NAME, args.angel_name);
                    write_to_operator(buffer, NULL);
                }

                while (run_rc != BBGZDRM_Run_Quit) {
                  if (TraceActive(trc_level_detailed)) {
                    TraceRecord(
                      trc_level_detailed,
                      TP(_TP_ANGEL_MAIN_CALL_BBGZADRM_RUN),
                      "angel_main calling BBGZDRM run...",
                      TRACE_DATA_END_PARMS);
                  }

                  run_rc = drm_p->run();

                  if (TraceActive(trc_level_detailed)) {
                    TraceRecord(
                        trc_level_detailed,
                        TP(_TP_ANGEL_MAIN_RETURN_BBGZADRM_RUN),
                        "angel_main returned from BBGZDRM run",
                        TRACE_DATA_INT(run_rc, "RC"),
                        TRACE_DATA_END_PARMS);
                  }

                  if (run_rc == BBGZDRM_Run_Reload) {
                    load_drm_rc = load_drm(drm_path, sgoo_p, &cur_armv_sequence, TRUE);

                    if (load_drm_rc != LOAD_DRM_OK) {
                      if (TraceActive(trc_level_detailed)) {
                        TraceRecord(
                          trc_level_detailed,
                          TP(_TP_ANGEL_MAIN_ERROR_RELOAD_BBGZADRM),
                          "angel_main error reloading BBGZADRM",
                          TRACE_DATA_INT(load_drm_rc, "RC"),
                          TRACE_DATA_END_PARMS);
                      }
                    } else {
                      drm_p = ((bbgzarmv*)(sgoo_p->bbgzsgoo_armv))->bbgzarmv_drm;
                    }
                  }
                }

                if (TraceActive(trc_level_detailed)) {
                  TraceRecord(
                    trc_level_detailed,
                    TP(_TP_ANGEL_MAIN_ERROR_BBGZADRM_CLEANUP),
                    "angel_main calling BBGZDRM cleanup...",
                    TRACE_DATA_END_PARMS);
                }

                uninit_rc = drm_p->cleanup();

                if (TraceActive(trc_level_detailed)) {
                  TraceRecord(
                    trc_level_detailed,
                    TP(_TP_ANGEL_MAIN_RETURN_BBGZADRM_CLEANUP),
                    "angel_main returned from BBGZDRM cleanup",
                    TRACE_DATA_INT(uninit_rc, "RC"),
                    TRACE_DATA_END_PARMS);
                }

              } else {
                if (TraceActive(trc_level_exception)) {
                  TraceRecord(
                    trc_level_exception,
                    TP(_TP_ANGEL_MAIN_ERROR_LOAD_BBGZADRM),
                    "angel_main failed to load BBGZADRM",
                    TRACE_DATA_END_PARMS);
                }
                angel_main_rc = ANGEL_MAIN_FAIL_LOAD_DRM;
              }

              free(fsm_details_p);
            } else {
              if (TraceActive(trc_level_exception)) {
                TraceRecord(
                  trc_level_exception,
                  TP(_TP_ANGEL_MAIN_ERROR_LOAD_BBGZAFSM),
                  "angel_main failed to load BBGZAFSM",
                  TRACE_DATA_END_PARMS);
              }

              angel_main_rc = ANGEL_MAIN_FAIL_LOAD_FSM;
            }
            free(fsm_path);
	  } else {
            if (TraceActive(trc_level_exception)) {
              TraceRecord(
                trc_level_exception,
                TP(_TP_ANGEL_MAIN_ERROR_LOAD_BBGZACHK),
                "angel_main error loading bbgzachk",
                TRACE_DATA_INT(load_angel_check_rc, "load_angel_check_rc"),
                TRACE_DATA_END_PARMS);
            }
            angel_main_rc = ANGEL_MAIN_FAIL_LOAD_BBGZACHK;
	  }
        } else { // Unable to obtain the angel ENQ.
            char jobname[9];
            char asid_string[8];
            ascb* ascb_p = getAscbFromStoken(&(sgoo_p->bbgzsgoo_angel_stoken));
            if (ascb_p != NULL) {
                if (ascb_p->ascbjbni != NULL) {
                    memcpy(jobname, ascb_p->ascbjbni, 8);
                    jobname[8] = '\0';
                } else if (ascb_p->ascbjbns != NULL) {
                    memcpy(jobname, ascb_p->ascbjbns, 8);
                    jobname[8] = '\0';
                } else {
                    strcpy(jobname, "*UNKNOWN");
                }

                snprintf(asid_string, sizeof(asid_string), "%hx", ascb_p->ascbasid);
            } else {
                strcpy(jobname, "*UNKNOWN");
                strcpy(asid_string, "0");
            }

            if (strlen(args.angel_name) == 0) {
                write_to_operator(DUPLICATE_ANGEL_PROCESS, NULL);
            } else {
                char buffer[256];
                snprintf(buffer, sizeof(buffer), DUPLICATE_ANGEL_PROCESS_NAME, args.angel_name);
                write_to_operator(buffer, NULL);
            }
            char message[128];
            snprintf(message, sizeof(message), DUPLICATE_ANGEL_PROCESS_DETAIL, asid_string, jobname);
            write_to_operator(message, NULL);
            angel_main_rc = ANGEL_MAIN_FAIL_DUPLICATE_ANGEL;
        }
      } else {
        if (TraceActive(trc_level_exception)) {
          TraceRecord(
            trc_level_exception,
            TP(_TP_ANGEL_MAIN_FAIL_CREATE_APD),
            "angel_main could not create process data",
            TRACE_DATA_END_PARMS);
          }

          angel_main_rc = ANGEL_MAIN_FAIL_CREATE_APD;
      }
    } else {
      if (TraceActive(trc_level_exception)) {
        TraceRecord(
          trc_level_exception,
          TP(_TP_ANGEL_MAIN_FAIL_CREATE_SGOO),
          "angel_main could not create the SGOO",
          TRACE_DATA_END_PARMS);
      }

      angel_main_rc = ANGEL_MAIN_FAIL_LOAD_SGOO;
        }
    } else {
        angel_main_rc = ANGEL_MAIN_FAIL_PARSE_ARGS;
    }

    if (angel_main_rc == ANGEL_MAIN_RETURN_OK) {
        if (strlen(args.angel_name) == 0) {
            write_to_operator(ANGEL_PROCESS_END_NORMAL, NULL);
        } else {
            char buffer[256];
            snprintf(buffer, sizeof(buffer), ANGEL_PROCESS_END_NORMAL_NAME, args.angel_name);
            write_to_operator(buffer, NULL);
        }
    } else {
        char abnormal_message[256];
        char reason_code_string[16];
        snprintf(reason_code_string, sizeof(reason_code_string), "%X", angel_main_rc);
        if (strlen(args.angel_name) == 0) {
            snprintf(abnormal_message, sizeof(abnormal_message), ANGEL_PROCESS_END_ABNORMAL, reason_code_string);
        } else {
            snprintf(abnormal_message, sizeof(abnormal_message), ANGEL_PROCESS_END_ABNORMAL_NAME, args.angel_name, reason_code_string);
        }
        write_to_operator(abnormal_message, NULL);
    }

    termenv();

    return angel_main_rc;
}

#pragma insert_asm(" IEANTASM")
