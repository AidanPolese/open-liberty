/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBGZ_SERVER_COMMON_FUNCTION_MODULE_H
#define _BBGZ_SERVER_COMMON_FUNCTION_MODULE_H

#include "bbgzasvt.h"
#include "client_dynamic_area_cell_pool.h"
#include "server_task_data.h"

#define COMMON_DEF_INCLUDES
#include "server_common_functions.def"
#undef COMMON_DEF_INCLUDES
/*-------------------------------------------------------------------*/
/* Structure of the server common module vector table.               */
/*-------------------------------------------------------------------*/
struct bbgzscfm {
  struct bbgzasvt_header header;
#define COMMON_DEF(svc_name, auth_name, impl_name, arg_type) bbgzasve impl_name;
#include "server_common_functions.def"
#undef COMMON_DEF
  char end_eyecatcher[16];
};

/**
 * Structure containing data used by the code in the server common function
 * module.  This is a 'process level' control block for the client process,
 * however there is one of these per server common function module used by
 * the client.  For example, if the client is bound to Liberty server A and
 * B, then there will be two instances of this structure.  The first is used
 * by the code inside the common function module for A, and the second is
 * used by the code inside the common function module for B.
 *
 * This correct instance of this structure to use is accessed via the
 * server_task_data structure.  This is the only way that this should be
 * accessed from the code.
 */
typedef struct serverCommonFunctionModuleProcessData {
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
     * Token used to look up this structure via name token.  This token is also
     * available to callers who want to register their own name tokens that are
     * scoped only to the server common function module currently in control.
     */
    unsigned long long       clientProcessDataToken;    /* 0x020 */

    /**
     * Head of the list of WOLA shared memory anchors attached by this client.
     * This list is serialized by ENQ CLIENT_WOLA_ATTACH_SHMEM_ENQ_RNAME.
     */
    struct wolaClientSharedMemoryAttachmentInfo* wolaSharedMemoryAttachHead_p; /* 0x028 */

    /**
     * Dynamic area pool for clients calling invoke.
     * NOTE: This field is referenced in prolog SAUTHPRL.  Do not change the offset.
     */
    unsigned long long       clientDynAreaPool;         /* 0x030 */

    /**
     * Information used by the client dynamic area cell pool.
     */
    ClientDynamicAreaCellPoolInfo_t clientDynAreaPoolInfo; /* 0x038 */

    /**
     * Available for use.
     */
    unsigned char            _reserved2[168];           /* 0x058 */
} ServerCommonFunctionModuleProcessData_t;              /* 0x100 */


/**
 * Return a pointer to the process level data for this server common function
 * module.
 *
 * @return a pointer to the process level data for this server common function
 *         module, or NULL if we are not in a client process.
 */
#pragma inline(getServerCommonFunctionModuleProcessData)
static ServerCommonFunctionModuleProcessData_t* getServerCommonFunctionModuleProcessData(void) {
    server_task_data* std_p = getServerTaskData();
    return (std_p != NULL) ? std_p->scfmProcessData_p : NULL;
}

#endif
