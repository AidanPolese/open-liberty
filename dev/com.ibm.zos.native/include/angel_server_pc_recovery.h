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
#ifndef _BBOZ_ANGEL_SERVER_PC_RECOVERY_H
#define _BBOZ_ANGEL_SERVER_PC_RECOVERY_H
/**@file
 * Defines structures used by Angel recovery
 */

#define ANGEL_SERVER_PC_RECOVERY_EYE "BBGZASPR"

/**
 * This struct is used by the fixed shim and dynamic replaceable modules
 * to perform recovery in the ARR.  Be careful when adding fields to the
 * struct, since the dynamic replaceable module might be updated while
 * the fixed shim is not.  Always add to free space and don't move items
 * around.  Remember that recovery in the DRM might run at a different
 * code level than the struct that was created.
 *
 * This struct should NOT be used by server functions to perform recovery.
 * Those functions should establish an ESTAE if they need recovery.  Only
 * code in the fixed shim and dynamic replaceable module should be using
 * this struct.
 */
typedef struct angel_server_pc_recovery {
    /**
     * Eyecatcher to help us validate the object in memory.
     */
    char eyecatcher[8];                              /* 0x000*/

    /**
     * Set if we need to free the caller's dynamic area (stack) in the ARR.  If
     * a PC is executed and the ARR gets control, the ARR will obtain a new
     * stack for its use.  The angel_server_pc_recovery object is allocated in
     * the caller's stack, and once we're done using it, we need to free the
     * caller's dynamic area.  In most cases the caller's dynamic area will go
     * away when the caller's task goes away, which is likely since we are in
     * the ARR, but not guaranteed, so we clean it up to be safe.
     */
    unsigned char free_dynamic_area_if_no_tgoo;

    /**
     * Reset the bit in the angel_task_data which indicates that this task is
     * processing an invoke request.  This bit was set before the requested
     * routine was invoked and we should set it back to 0.
     */
    unsigned char drm_reset_invoking_bit;

    /**
     * The local lock was obtained and needs to be released.  The save area
     * passed to releaseLocalLock should be stored in release_local_lock_save_area_p.
     */
    unsigned char release_local_lock;

    /**
     * The caller attached to the SGOO using IARV64, and needs to detach.
     */
    unsigned char detach_from_shared;

    /**
     * The caller incremented the use count in the ARMV saved in the
     * armv_to_decrement field during an angel registration.  The count needs
     * to be decremented because the registration did not complete.
     */
    unsigned char decrement_armv_use_count;

    /**
     * The bit in the angel process data was reset to indicate that we are
     * deregistered with the angel.  This processing failed and the bit
     * needs to be changed to inidicate we are still registered.
     */
    unsigned char mark_apd_deregistered;

    /**
     * The bit in the angel process data was set to indicate that we are
     * registered with the angel.  This processing failed and the bit needs
     * to be changed to inidcate we are not registered.
     */
    unsigned char mark_apd_registered;

    /**
     * The angel_task_data control block was created for this task and attached
     * to the Common Task Data Anchor hung off the STCBBCBA.  The request failed
     * and we need to remove the angel_task_data from the CTDA.
     */
    unsigned char drm_cleanup_tgoo;

    /**
     * Pointer to the ARMV to decrement if decrement_armv_use_count is set.
     */
    void* armv_to_decrement;                         /* 0x010*/

    /**
     * The resource manager token for the jobstep task.  This is set during
     * register when we register the resource manager, and needs to be unset
     * in the ARR.
     */
    int drm_task_resmgr_token;

    /**
     * The resource manager token for the address space.  This is set during
     * register when we register the resource manager, and needs to be unset
     * in the ARR.
     */
    int drm_as_resmgr_token;

    /**
     * A pointer to the metal C environment created by the calling task.  It
     * should be used for any free() requests to be done on behalf of the
     * calling task.
     */
    void* fsm_cenv_p;                                /* 0x020*/

    /**
     * A pointer to the parameters used to create the metal C environment in
     * fsm_cenv_p.
     */
    struct __csysenv_s* fsm_csysenv_p;

    /**
     * Unavailable -- used in previous release.
     */
    void* _unavailable1;                             /* 0x030*/

    /**
     * A pointer to the local copy of the BBGZSAFM header and function table.
     * If the drm_cleanup_safm or drm_cleanup_safm_release flags are set, this
     * stoarge should be freed using storage release.
     */
    void* drm_safm_func_table_p;

    /**
     * Used in previous release -- do not use.
     */
    unsigned char _reserved1;                             /* 0x040*/

    /**
     * The metal C environment stored in fsm_cenv_p was created by the calling
     * task, and the metal C environment is not cached in the
     * angel_process_data.  The metal C environment and the parameters used to
     * create it need to be freed.
     */
    unsigned char fsm_free_cenv;

    /**
     * This flag is set if the ARR should clean up the BBGZSAFM load module.
     * The processing is performed by calling cleanup_safm() in
     * angel_server_pc.mc.  This includes calling BPX4DEL and freeing the
     * local copy of the BBGZSAFM header and function table.
     */
    unsigned char drm_cleanup_safm;

    /**
     * This flag is set if the ARR should unload the BBGZSAFM load module using
     * BPX4DEL.  This processing is performed by cleanup_safm() in
     * angel_server_pc.mc.
     */
    unsigned char drm_cleanup_safm_unload;

    /**
     * The size of the local copy of the BBGZSAFM header and function table.
     * The length is used in storage release when freeing the table.
     */
    int drm_safm_func_table_size;

    /**
     * A pointer to the local copy of the invoke parameters, which needs to be
     * freed by the ARR using fsm_cenv_p.
     */
    void* drm_invoke_parm_struct_p;

    /**
     * A pointer to the entry point of BBGZSAFM, as returned by BPX4LOD.  If the
     * drm_cleanup_safm or drm_cleanup_safm_unload flags are set, this should
     * be freed by calling BPX4DEL (as a part of cleanup_safm() processing).
     */
    void* drm_safm_entrypt_p;                        /* 0x050*/

    /**
     * The user token used to connect to the shared memory using IARV64.
     */
    long long fsm_sgoo_usertoken;

    /**
     * Flag is set to indicate the local copy of the BBGZSAFM function table
     * and header needs to be freed using storage release.
     */
    unsigned char drm_cleanup_safm_release;          /* 0x060*/

    /**
     * Flag which tells us we need to clean up the SCFM, both freeing the
     * storage it occupies (in common) and unloading it from contents supervisor
     * services.
     */
    unsigned char drm_cleanup_scfm;                  /* 0x061*/

    /**
     * Available for use.
     */
    unsigned char _available1[6];                    /* 0x062*/

    /**
     * Pointer to the initial pthread creating task (IPT).  Register processing
     * establishes a RESMGR here.
     */
    void* drm_initial_pthread_creating_task_p;       /* 0x068*/

    /**
     * Save area used when calling releaseLocalLock.  This should be freed
     * after the local lock is released.
     */
    void* release_local_lock_save_area_p;            /* 0x070*/

    /**
     * The length of the SCFM module to be cleaned up.
     */
    unsigned int drm_scfmModuleLength;               /* 0x078*/

    /**
     * The size of the local copy of the BBGZSCFM header and function table.
     * The length is used in storage release when freeing the table.
     */
    unsigned int drm_scfm_func_table_size;           /* 0x07C*/

    /**
     * The starting address of the SCFM module to delete.
     */
    void* drm_scfmModule_p;                          /* 0x080*/

    /**
     * The delete token for the SCFM module to delete.
     */
    unsigned char drm_scfmModuleDeleteToken[8];      /* 0x088*/

    /**
     * A pointer to the local copy of the BBGZSCFM header and function table.
     * If the drm_cleanup_scfm or drm_cleanup_scfm_release flags are set, this
     * stoarge should be freed using storage release.
     */
    void* drm_scfm_func_table_p;                     /* 0x0090*/

    /**
     * The SGOO that the ARR should detach from if it runs after we attach
     * to the SGOO but before we create the angel_process_data.  Used with
     * the detach_from_shared bit.
     */
    void* fsm_attached_sgoo_p;                       /* 0x098*/

    /**
     * Available for use.
     */
    unsigned char _available2[354];                  /* 0x0A0*/
} angel_server_pc_recovery;

#endif
