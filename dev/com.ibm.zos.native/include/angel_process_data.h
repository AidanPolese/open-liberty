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
#ifndef _BBGZ_ANGEL_PROCESS_DATA_H
#define _BBGZ_ANGEL_PROCESS_DATA_H

/**@file
 * Process Level Data for the Angel
 */
#include "bbgzsgoo.h"
#include "bbgzasvt.h"

#include "angel_task_data.h"
#include "bpx_load.h"
#include "mvs_enq.h"

#define ANGEL_PROCESS_DATA_TOKEN_NAME "BBGZAPD_"

// Location to load SCFM module into.
#define LOCAL_SCFM_SUBPOOL 231 /* Common, fetch protected */
#define LOCAL_SCFM_KEY 2

// Address space types for createAngelProcessData
#define ANGEL_PROCESS_TYPE_ANGEL  1
#define ANGEL_PROCESS_TYPE_SERVER 2
#pragma pack(1)

/**
 * Struct representing a set of cleanup flags, telling us when it's OK to
 * complete deregistration and delete the authorized server code (BBGZSAFM)
 * and common code (BBGZSCFM).
 */
typedef struct angel_process_invokecount_s {
  unsigned int deregistered : 1, //!< Server has deregistered, no new authorized tasks allowed.
               allTasksCleanedUp : 1, //!< All known server tasks have been cleaned up.
               pauseFailed : 1, //!< Deregistration failed because curResmgrCount &gt 0 and pause failed.
               tcbScanFailed : 1, //!< Deregistration failed because the TCB scan did not complete.
               releaseFailed : 1, //!< Deregistration may fail because the deregister task could not be released.
               allClientsUnbound : 1, //!< Set when the last client has unbound.
               iptOrAsResmgrFinished : 1, //!< Set when the IPT or address space RESMGR is finished running.
               _available : 9, //!< Available for future use (flags)
               curResmgrCount : 16; //!< Count of the number of tasks currently invoking the server task RESMGR.
} AngelProcessInvokeCount_t;

/**
 * Struct representing the number of client binds to the server represented by
 * this process data.  The struct was designed to be updated by CDSG.
 */
typedef struct angelClientBindCount {
    unsigned int _available1; //!< Available for use.
    unsigned int noMoreBinds : 1, //!< Set when no more binds should be allowed.
                 count : 31; //!< Count of active binds.
    SToken serverStoken; //!< Stoken of this server.  Used to verify bind to correct PGOO.
} AngelClientBindCount_t;

/**
 * Struct used during deregistration when invoking tasks have finished
 * invoking and need to tell the deregistering task.  Updated with CDSG.  When
 * a task is marked as 'doNotInvoke' while it is invoking, the task bumps the
 * completed thread count when the invoke finishes.  If the PET is set, the
 * task releases the PET which will cause the deregistering task to wake up
 * and decrement its waiting-for-threads count.
 */
typedef struct invokeCompleteNotification {
    unsigned long long completedThreadCount; //!< Count of threads that have completed invoke.
    void* completedThreadPET_p; //!< Pointer to a PET to notify when an invoke completes.
} InvokeCompleteNotification_t;

/**
 * The angel_process_data control block represents an address space connected
 * to the Angel (a Liberty server) or the Angel itself.  The control block is
 * allocated by code owned by the angel and is intended for angel data only.
 * This control block should not be referenced by server code.
 *
 * @key 2
 */
typedef struct angel_process_data {
  /**
   *  The eye catcher (BBGZAPD_).
   */
  unsigned char            eyecatcher[8];                      /* 0x000 */

  /**
   * Version number of the control block.
   */
  short                    version;                            /* 0x008 */

  /**
   * Length of the control block (size of storage obtained).
   */
  short                    length;                             /* 0x00A */

  /**
   * The instance number of this control block.  Each time a process data
   * control block is created, a sequence counter is incremented in the SGOO.
   * instance_num represents this process data's instance number.
   */
  int                      instance_num;                       /* 0x00C */

  /**
   * A pointer to the SGOO control block.
   */
  bbgzsgoo*                bbgzsgoo_p;                         /* 0x010 */

  /**
   * The token returned by the RESMGR macro for the address space level
   * resource manager.
   */
  int                      as_resmgr_token;                    /* 0x018 */

  /**
   * The identifier representing which instance of the ARMV this server is
   * using.  Each ARMV has a sequence number, which is incremented each time
   * a new ARMV is loaded.  The angel process keeps the sequence number in its
   * private storage.  cur_armv_seq is the sequence number that this server is
   * currently using.  If the current ARMV sequence is greater than this number,
   * then this server should attach itself to the current ARMV and store the
   * sequence of that ARMV here.
   */
  volatile unsigned char   cur_armv_seq;                       /* 0x01C */

  /**
   * Variable representing the address space type (ANGEL_PROCESS_TYPE_ANGEL or
   * ANGEL_PROCESS_TYPE_SERVER).
   */
  unsigned char            as_type;                            /* 0x01D */

  /**
   * Available for use.
   */
  unsigned char            _reserved1[2];                      /* 0x01E */

  /**
   * A pointer to the copy of the SAFM function table used by this server.  The
   * copy has the SAF authorization bits set, and this copy is used to determine
   * whether or not a particular authorized function is available for use.
   */
  struct bbgzasvt_header*  safm_function_table_p;              /* 0x020 */

  /**
   * A pointer to the __csysenv_s struct used to create the metal C environment
   * stored in key2_env_p.
   */
  struct __csysenv_s*      key2_env_parms_p;                   /* 0x028 */

  /**
   * A pointer to the metal C environment used by the authorized code in the
   * fixed shim and dynamic replaceable modules.
   */
  void*                    key2_env_p;                         /* 0x030 */

  /**
   * Pointer to the TCB which is the initial pthread creating task for
   * a Liberty server process.  This is the task that "owns" the load modules
   * loaded by BPX4LOD, and therefore our cleanup is tied to the life of this
   * task.  This pointer is set during register processing.
   */
  void*                    initial_pthread_creating_task_p;    /* 0x038 */

  /**
   * The token returned by the ISGENQ macro for the ENQ representing this
   * server's existence.  Code in the angel process scans for this ENQ to
   * see if there are any servers running before the angel stops.
   */
  enqtoken                 server_enq_token;                   /* 0x040 */

  /**
   * A set of flags which help us determine when it's OK to clean up the BBGZSAFM.
   */
  AngelProcessInvokeCount_t invokecount;                       /* 0x060 */

  /**
   * The token returned by RESMGR when registering the resource manager for
   * the job step task of this server's address space.
   */
  int                      js_task_resmgr_token;               /* 0x064 */

  /**
   * The entry point of the BBGZSAFM load module, returned from BPX4LDX.
   */
  void*                    safm_entry_p;                       /* 0x068 */

  /**
   * The size of the function table in the BBGZSAFM load module.  This is
   * used to free the safm_function_table_p.
   */
  int                      safm_function_table_size;           /* 0x070 */

  /**
   * The length of the BBGZSCFM module, as reported by load_from_hfs.
   */
  unsigned int             scfmModuleLength;                   /* 0x074 */

  /**
   * The token used to delete BBGZSCFM from LPA using CSV services.
   */
  unsigned char            scfmModuleDeleteToken[8];           /* 0x078 */

  /**
   * The deregistration area used to count completed threads.
   */
  InvokeCompleteNotification_t threadCompleteNotificationArea; /* 0x080 */

  /**
   * The starting module address for BBGZSCFM, as reported by load_from_hfs.
   */
  void*                    scfmModule_p;                       /* 0x090 */

  /**
   * The entry point address for BBGZSCFM, as reported by load_from_hfs.
   */
  void*                    scfmModuleEntryPoint_p;             /* 0x098 */

  /**
   * A pointer to the copy of the SCFM function table used by this server.  The
   * copy has the SAF authorization bits set, and this copy is used to determine
   * whether or not a particular authorized function is available for use.
   * This is stored in common.
   */
  struct bbgzasvt_header*  scfm_function_table_p;              /* 0x0A0 */

  /**
   * The size of the function table in the BBGZSCFM load module.  This is
   * used to free the scfm_function_table_p.
   */
  int                      scfm_function_table_size;           /* 0x0A8 */

  /**
   * Available for use.
   */
  unsigned char            _reserved2[4];                      /* 0x0AC */

  /**
   * The client bind count for this server.  The angel process data and the
   * SCFM cannot be freed/pooled until this count returns to zero.  This field
   * is updated with CDSG.
   */
  AngelClientBindCount_t   clientBindArea;                     /* 0x0B0 */

  /**
   * A list of client binds.  This list must be read and updated while holding
   * the server bind list ENQ.
   */
  struct angelClientBindDataNode* clientBindHead_p;            /* 0x0C0 */

  /**
   * Available for use.
   */
  unsigned char            _reserved3[312];                    /* 0x0C8 */
} angel_process_data;                                          /* 0x200 */

#pragma pack(reset)

/**
 * Return a pointer to the angel_process_data for this process.  The process
 * level name token is used to look up the process data.
 *
 * @return a pointer to the angel process data for this process, or NULL if none.
 */
angel_process_data* getAngelProcessDataFromNameToken(void);

// Prevent LE enabled C from picking up this inlined function.
#ifdef __IBM_METAL__
#ifdef ANGEL_COMPILE
/**
 * Return a pointer to the angel_process_data for this process.
 *
 * @return a pointer to the angel process data for this process, or NULL if none.
 */
#pragma inline(getAngelProcessData)
static angel_process_data* getAngelProcessData(void) {
    angel_task_data* atd_p = getAngelTaskData();
    if (atd_p->apd_p == NULL) {
        atd_p->apd_p = getAngelProcessDataFromNameToken();
    }
    return atd_p->apd_p;
}
#endif
#endif

/**
 * Returns a pointer to the angel_process_data for the process represented by
 * the input stoken.
 *
 * @param stoken_p A pointer to the 8 byte stoken which represents the process
 *                 whose angel_process_data should be returned.
 *
 * @return A pointer to the angel_process_data for the requested process, or
 *         NULL if none.
 */
angel_process_data* getAngelProcessDataByStoken(void* stoken_p);

/**
 * Create angel_process_data and its associated lookup mechanisms.
 *
 * @param sgoo_p If the address space is connected to an angel process,
 *               a pointer to its SGOO should be provided in sgoo_p.
 *               Otherwise this parameter should be NULL.
 * @param type The angel_process_data type to create.
 *             ANGEL_PROCESS_TYPE_ANGEL for an Angel process.
 *             ANGEL_PROCESS_TYPE_SERVER for a server process.
 *
 * @return A pointer to the angel_process_data, or NULL if unable to create.
 */
angel_process_data* createAngelProcessData(bbgzsgoo* sgoo_p, unsigned char type);

/**
 * Destroys an angel_process_data control block.
 *
 * @param data_p A pointer to the angel_process_data to delete.
 */
void destroyAngelProcessData(angel_process_data* data_p);

/**
 * Deletes the name tokens that were created to help locate the angel process
 * data.  The tokens are deleted when the server RESMGR runs, even though the
 * angel process data may live on a bit longer due to clients still running
 * code in the invoke method.
 *
 * @param apd_p A pointer to the angel process data whose tokens should be
 *              deleted.
 * @param stoken_p If the supplied angel_process_data does not belong to
 *                 the current address space, stoken_p should point to the 8
 *                 byte stoken to which this angel_process_data does belong.
 *                 Otherwise, this parameter should be NULL.
 */
void deleteAngelProcessDataNameTokens(angel_process_data* apd_p, SToken* stoken_p);

/**
 * Increments the client bind count, provided that the angel process data
 * provided is owned by the STOKEN provided.
 *
 * @param apd_p The angel process data.
 * @param stoken_p The STOKEN for which the bind is intended.
 *
 * @return 0 if the increment was successful.
 */
int incrementBindCount(angel_process_data* apd_p, SToken* stoken_p);

/**
 * Decrements the client bind count, provided that the angel process data
 * provided is owned by the STOKEN provided.  In theory, the STOKEN should
 * always be correct provided it is the STOKEN used to increment the count
 * during incrementBindCount(), because the process data should not be re-used
 * until the bind count returns to zero.
 *
 * @param apd_p The angel process data.
 * @param stoken_p The STOKEN for which the unbind is intended.
 *
 * @return 0 if the decrement was successful.
 */
int decrementBindCount(angel_process_data* apd_p, SToken* stoken_p);

/**
 * Cleans up the SCFM module for the provided angel process data.
 *
 * @param apd_p The angel process data whose SCFM should be cleaned up.
 */
void cleanupSCFM(angel_process_data* apd_p);

#endif
