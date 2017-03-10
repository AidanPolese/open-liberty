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
#ifndef _BBOZ_ANGEL_DYNAMIC_REPLACEABLE_MODULE_H
#define _BBOZ_ANGEL_DYNAMIC_REPLACEABLE_MODULE_H
/**@file
 * Mapping of the bbgzadrm (Angel Dynamically Replaceable Module)
 */
#include "bbgzsgoo.h"
#include "bbgzarmv.h"
#include "angel_process_data.h"

#include "gen/iharmpl.h"
#include "gen/ihasdwa.h"

#include "angel_client_pc_recovery.h"
#include "angel_server_pc_recovery.h"

/*-------------------------------------------------------------------*/
/* Return codes for angel functions.                                 */
/*-------------------------------------------------------------------*/
#define BBGZDRM_Run_Quit 0
#define BBGZDRM_Run_Reload 4

typedef struct bbgzadrm bbgzadrm;

/**
 * Version of the dynamic replaceable module code.  This should be
 * increased by one, by the first code change in this module in this
 * PTF, and the minor version should be set to 0.  For subsequent
 * changes in the same PTF, the minor version should increased by
 * one.
 *
 * These ensure that the angel process reloads the dynamic replaceable
 * module.  A customer should only ever see major version changes.  Our
 * test systems rely on the minor version changes to ensure personal
 * builds run with the correct level of code.
 */
#define BBGZ_DYN_MODULE_CODE_MAJOR_VERSION 7
#define BBGZ_DYN_MODULE_CODE_MINOR_VERSION 0

/**
 * The Angel "dynamically replaceable module".  This structure is basically
 * a vector table that represents location of functions that will service
 * the various Angel functions.  In order to prevent outages across the system,
 * updated versions of the code can be loaded and wired in at runtime.
 */
struct bbgzadrm {

    /**
     * Eyecatcher.
     */
    char  eyecatcher[8];

    /**
     * Build date stamp in YYYYMMDD format.
     */
    char  build_date[8];

    /**
     * Build time stamp in hh:mm:ss format.
     */
    char  build_time[8];

    /**
     * Initialization routine called after the module has been loaded.
     *
     * @retval 0 on success, run will be invoked
     * @retval non-zero on error, run will not be invoked
     */
    int (*initialize)(void);

    /**
     * Main routine that is responsible for the angel's command processing
     * loop.
     *
     * @retval BBGZDRM_Run_Quit if the angel should terminate
     * @retval BBGZDRM_Run_Reload if the angel should begin reload processing
     */
    int (*run)(void);

    /**
     * Cleanup routine that is responsible for cleanup and termination at
     * normal angel shutdown.  This is called when the @c run routine returns
     * with @c BBGZDRM_Run_Quit.
     *
     * @return the angel exit code
     */
    int (*cleanup)(void);

    /**
     * Reinitialization routine that is responsible for "fixing up" the
     * environment when we transition code levels in the angel without
     * restarting.  This could be used to define new PCs, initialize values
     * in reserved sections of control blocks, or whatever else is necessary
     * but in most cases it will do nothing.
     *
     * It's important to note that the future module may be a level that
     * is older than the current module and that levels can be skipped.
     *
     * @retval 0 if successful and the new vector can be wired
     * @retval non-zero if unsuccessful
     */
    int (*reinitialize)(bbgzarmv* exsting, bbgzarmv* future);

    /**
     * Get the human readable code version information of this module.
     *
     * @return a string fit for end user messages
     */
    char* (*getVersionString)(void);

    /**
     * Get the code version number of this module.  Version numbers should be
     * increasing across fix packs and releases.
     *
     * @return the version number of this module
     */
    int   (*getVersionNumber)(void);

    /** Reserved: Angel internal entry point slot 7. */
    void* _angelReserved7;

    /** Reserved: Angel internal entry point slot 8. */
    void* _angelReserved8;

    /** Reserved: Angel internal entry point slot 9. */
    void* _angelReserved9;

    /** Reserved: Angel internal entry point slot 10. */
    void* _angelReserved10;

    /** Reserved: Angel internal entry point slot 11. */
    void* _angelReserved11;

    /** Reserved: Angel internal entry point slot 12. */
    void* _angelReserved12;

    /** Reserved: Angel internal entry point slot 13. */
    void* _angelReserved13;

    /** Reserved: Angel internal entry point slot 14. */
    void* _angelReserved14;

    /** Angel internal entry point slot 15 -- Reserved. */
    void* _angelReserved15;

    /** Version information for the struct (not the module) */
    struct {
        unsigned short minorVersion;
        unsigned short _available;
        unsigned int   length; /* Length of struct */
    } verInfo;

    /**
     * The RESMGR implementation for the angel and server processes.  This
     * routine gets control from the RESMGR in the fixed shim module.  All
     * meaningful recovery logic should be placed here to allow it to be
     * updated by applying a new dynamic replaceable module.
     *
     * @param parameters The RMPL control block passed by MVS
     * @param apd_p A pointer to the angel process data control block, or NULL if one does
     *              not exist for the address space being recovered.
     */
    void (* resmgr)(rmpl* parameters, angel_process_data* apd_p); /* @@EXT@17 */

    /**
     * The server registration PC implementation.  This routine gets control
     * from the register function in the fixed shim module.  All meaningful
     * function should be placed here (as opposed to in the fixed shim) to
     * allow it to be updated by applying a new dynamic replaceable module.
     *
     * See dynamicReplaceablePC_Register for parameters and return types.
     */
    int  (* register_pc)(long long, bbgzsgoo*, angel_process_data*, bbgzarmv*, char*, angel_server_pc_recovery*); /* @@EXT@18 */

    /**
     * The invoke PC implementation.  This is used by a registered server to
     * invoke services in the server authorized function module (BBGZSAFM).
     * It gets control from the fixed shim module Invoke service.
     *
     * See dynamicReplaceablePC_Invoke for parameters and return types.
     */
    int  (* invoke_pc)(unsigned int, unsigned int, void*, angel_process_data*, angel_server_pc_recovery*); /* @@EXT@19 */

    /**
     * The deregister PC implementation.  This is used by the server to
     * deregister with the angel.  It gets control from the fixed shim module.
     *
     * See dynamicReplaceablePC_Deregister for parameters and return types.
     */
    int  (* deregister_pc)(angel_process_data*, angel_server_pc_recovery*); /* @@EXT@20 */

    /**
     * The ARR for the PC routines in the dynamic replable module.  This routine
     * gets control from the ARR in the fixed shim module.  It is responsible for
     * cleaning up anything created in the PC routines in the dynamic replaceable
     * module (such as register, invoke, deregister).
     *
     * See dynamicReplaceableARR for parameters and return types.
     */
    void (* arr)(sdwa*, angel_server_pc_recovery*); /* @@EXT@21 */

    /**
     * The client bind PC implementation.  This is used by the client to bind
     * to the server, allowing it to call the client invoke PC.  All meaningful
     * function should be placed here (as opposed to in the fixed shim) to
     * allow it to be updated by applying a new dynamic replaceable module.
     *
     * see dynamicReplaceablePC_ClientBind for parameters and return types.
     */
    int (* clientBind_pc)(SToken*, bbgzasvt_header**, void**, bbgzsgoo*, bbgzarmv*, struct angelClientProcessData*, angel_client_pc_recovery*); /* @@EXT@22 */

    /**
     * The client invoke PC implementation.  This is used by the client to call
     * functions provided by a server common function module.  All meaningful
     * function should be placed here (as opposed to in the fixed shim) to
     * allow it to be updated by applying a new dynamic replaceable module.
     *
     * see dynamicReplaceablePC_ClientInvoke for parameters and return types.
     */
    int (* clientInvoke_pc)(void*, unsigned int, unsigned int, void*, angel_client_pc_recovery*); /* @@EXT@23 */

    /**
     * The client unbind PC implementation.  This is used by the client to
     * destroy the bind to the server.  Once it is unbound, it cannot invoke
     * services in the server's common function module using clienInvoke.
     * All meaningful function should be placed here (as opposed to in the
     * fixed shim) to allow it to be updated by applying a new dynamic
     * replaceable module.
     *
     * see dynamicReplaceablePC_ClientUnbind for parameters and return types.
     */
    int (* clientUnbind_pc)(void*, angel_client_pc_recovery*, unsigned char*); /* @@EXT@24 */

    /**
     * The ARR for the client PC routines in the dynamic replaceable module.
     * This routine gets control from the ARR in the fixed shim module.  It is
     * responsible for cleaning up anything created in the PC routines in the
     * dynamic replaceable module (such as bind, clientInvoke, unbind).
     *
     * See dynamicReplaceableClientARR for parameters and return types.
     */
    void (* clientArr)(sdwa*, angel_client_pc_recovery*); /* @@EXT@25 */

    /**
     * The RESMGR implementation for the client process.  This
     * routine gets control from the RESMGR in the fixed shim module.  All
     * meaningful recovery logic should be placed here to allow it to be
     * updated by applying a new dynamic replaceable module.
     *
     * @param parameters The RMPL control block passed by MVS
     * @param apd_p A pointer to the angel client process data control block, or NULL if one does
     *              not exist for the address space being recovered.
     */
    void (* clientResmgr)(rmpl* parameters, struct angelClientProcessData* acpd_p); /* @@EXT@26 */
};

#endif
