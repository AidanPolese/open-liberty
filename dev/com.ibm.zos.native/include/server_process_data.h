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
#ifndef _BBOZ_SERVER_PROCESS_DATA_H
#define _BBOZ_SERVER_PROCESS_DATA_H

#include <crgc.h>

#include "server_task_data.h"
#include "server_wola_services.h"

/**@file
 * Defines Server process GOO Functions
 */

/**
 * Subpool used by server process data.  Job-step, not FP, private high.
 */
#define SERVER_PROCESS_DATA_SUBPOOL 249

/**
 * Storage key of server process data.
 */
#define SERVER_PROCESS_DATA_KEY 2

/**
 * Maximum number of "Registered" cleanup routines
 */
#define SERVER_PROCESS_DATA_MAX_REGISTERED_CLEANUPROUTINES 7

/**
 * The process level data structure for the server.
 */
#pragma pack(packed)
typedef struct server_process_data {
    /**
     * The eye catcher for this control block.
     */
    unsigned char            eyecatcher[8];             /* 0x000 */

    /**
     * The version number of this control block.
     */
    short                    version;                   /* 0x008 */

    /**
     * The length of the storage allocated for this control block.
     */
    short                    length;                    /* 0x00A */

    /**
     * Available for use.
     */
    unsigned char            _reserved0[4];             /* 0x00C */

    /**
     * A pointer to the structure used to hold the parameters for the
     * authorized metal C environment pointer to by auth_metalc_env_p.
     */
    struct __csysenv_s*      auth_metalc_env_parms_p;   /* 0x010 */

    /**
     * A pointer to the metal C environment used by the server for authorized
     * functions.
     */
    void*                    auth_metalc_env_p;         /* 0x018 */

    /**
     * A token that references the registry's cell pool.  This cell poll should be used
     * by the REGISTRY ONLY.  This is because the registry stores an instance count in
     * each cell of the pool and relies on that instance count not being overwritten
     * in between allocations.
     */
    long long                registry_cell_pool_token;  /* 0x020 */

    /**
     * A pointer to the cache(s) used to store penalty box data for the server.
     */
    void*                    penalty_box_cache_p;       /* 0x028 */

    /**
     * Flags.
     */
    unsigned int checked_for_control_access : 1, //!< checked for control access  0x030
                 skipping_surrogat_checks : 1,   //!< when on SURROGAT checking is not needed
                 checked_for_read_access : 1,    //!< checked for read access
                 sync_to_thread_enabled : 1,     //!< when on sync to thread is enabled
                 available1 : 28;                //!< available

    /**
     * Serialized Flags.  Use of compare and swap is required for updates.
     */
    struct serializedFlags {                            /* 0x034 */
         int        unusedBits  :31,

         serverHardFailureDetected  :1;          //!< Server failing with Hard failure
    } serializedFlags;
    
    /**
     * A pointer to the Local Comm main control structure for the server.
     */
    void*                    lcom_BBGZLOCL_p;           /* 0x038 */

    /**
     * A bounded stack used by PetVet.
     * Needs to be 16 byte boundary aligned.
     */
    unsigned char            petvet[40];                /* 0x040 */

    /**
     * A pointer to the server's BBOARGE element (WolaRegistration_t) in 
     * the BBOARGE chain. We save it here in case the server dies unexpectedly 
     * and we must deactivate the server's BBOARGE from the RESMGR.
     */
    void * wola_bboarge_p;                              /* 0x068 */

    /**
     * The resource manager token to deregister from RRS.
     * The token is set when the z/OS transaction manager/service is enabled.
     * The token is cleared when the z/OS transaction manager/service is disabled or when
     * the server stops cleanly.
     */
    crg_resource_manager_token          resMgrToken;    /* 0x070 */

    crg_resource_manager_name           resMgrName;     /* 0x080 */

    /**
     * A copy of the server common function module header that the server uses to
     * create OSGi services representing each client function.
     */
    struct bbgzasvt_header*             localScfmHeader_p; /* 0x0A0 */
    
    struct pc_addOTMAAnchorToSPD_parms * wola_otma_anchors_p;   /* 0x0A8 */

    /**
     * A pointer to the async io completion queue.
     *
     * WARNING: mvs_aio_services's aio_exit_routine()'s entry linkage, SAIOPRL, hard codes this
     * offset.
     */
    void*                    asyncio_completion_data_p; /* 0x0B0 */

    /**
     * Hard failure cleanup routine registry.
     */
    void (* hardFailureRegisteredCleanupRtn[SERVER_PROCESS_DATA_MAX_REGISTERED_CLEANUPROUTINES])(void);  /* 0x0B8 */

    /**
     * Available for use.
     */
    unsigned char            _reserved2[272];           /* 0x0F0 */

} server_process_data;                                  /* 0x200 */
#pragma pack(reset)

#define SERVER_PROCESS_DATA_TOKEN_NAME "BBGZSPD_"

// Mask values for server_process_data.serializedFlags
#define SPD_SERIALIZEDFLAGS_serverHardFailureDetected 0x00000001 // Bit 31

// ----------------------------------------------------------------------------
// Skip code if desired by includer
// ----------------------------------------------------------------------------
#ifndef _BBOZ_SERVER_PROCESS_DATA_NOCODE_H

/**
 * Return a pointer to the process level data for this server.  The process
 * level name token is used to look up the process level data.
 *
 * @return a pointer to the process level data for this server, or NULL if none.
 */
server_process_data* getServerProcessDataFromNameToken(void);

// Prevent LE enabled C from picking up this inlined function.
#ifdef __IBM_METAL__

/**
 * Return a pointer to the process level data for this server.
 *
 * @return a pointer to the process level data for this server, or NULL if none.
 */
#pragma inline(getServerProcessData)
static server_process_data* getServerProcessData(void) {
    server_task_data* std_p = getServerTaskData();

    if (std_p->spd_p == NULL) {
        std_p->spd_p = getServerProcessDataFromNameToken();
    }

    return std_p->spd_p;
}
#endif

/**
 * Return the current server_process_data address
 *
 * @param outPGOO_PtrPtr pointer to the pointer variable to update with the PGOO address
 */
int getServerProcessDataUnauth(const server_process_data** outPGOO_PtrPtr);

/**
 * Create the process level data for the server, and its associated lookup mechanisms.
 *
 * @param auth_csysenv_p A pointer to the __csysenv_s struct which was used to
 *                       create the metal C environment which the authorized
 *                       server code should use.
 * @param auth_cenv_p A pointer to the metal C environment which the authorized
 *                    server code should use.
 *
 * @return A pointer to the process level data for this server, or NULL if unable to create.
 */
server_process_data* createServerProcessData(struct __csysenv_s* auth_csysenv_p, void* auth_cenv_p);

/**
 * Destroys the server process data control block and associated name token.
 *
 * @param spd_p A pointer to the server process data.  This function will
 *              verify that the server process data for this process matches
 *              the server process data passed in spd_p.
 */
void destroyServerProcessData(server_process_data* spd_p);

#endif // _BBOZ_SERVER_PROCESS_DATA_NOCODE_H

#endif
