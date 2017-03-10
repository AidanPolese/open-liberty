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
#ifndef _BBOZ_ANGEL_CLIENT_PC_RECOVERY_H
#define _BBOZ_ANGEL_CLIENT_PC_RECOVERY_H

#include "angel_process_data.h"
#include "mvs_enq.h"

/**@file
 * Defines structures used by Angel recovery in the client process.
 */

#define ANGEL_CLIENT_PC_RECOVERY_EYE "BBGZACPR"
#define ANGEL_CLIENT_PC_RECOVERY_PROLOG_EYE "BBGZACPP"

/**
 * This struct is used by the fixed shim and dynamic replaceable modules
 * to perform recovery for the client address space in the ARR.  Be careful
 * when adding fields to the struct, since the dynamic replaceable module might
 * be updated while the fixed shim is not.  Always add to free space and don't
 * move items around.  Remember that recovery in the DRM might run at a
 * different code level than the struct that was created.
 *
 * This struct should NOT be used by server functions to perform recovery.
 * Those functions should establish an ESTAE if they need recovery.  Only
 * code in the fixed shim and dynamic replaceable module should be using
 * this struct.
 */
typedef struct angel_client_pc_recovery {
    /**
     * Eyecatcher to help us validate the object in memory.
     */
    char eyecatcher[8];                              /* 0x000*/

    /**
     * Set if we need to free the bind ENQ.  The bind ENQ token is found int
     * shr_bindEnqToken.
     */
    unsigned char shr_freeBindEnq;                   /* 0x008*/

    /**
     * The caller incremented the use count in the ARMV saved in the
     * fsm_armvToDecrement field during an angel registration.  The count needs
     * to be decremented because the registration did not complete.
     */
    unsigned char fsm_decrementArmvUseCount;         /* 0x009*/

    /**
     * The caller added bind data to the angel process data, and it needs to
     * be removed.
     */
    unsigned char drm_removeBindDataFromApd;         /* 0x00A*/

    /**
     * The caller added bind data to the angel client process data, and it needs
     * to be removed.
     */
    unsigned char drm_removeBindDataFromAcpd;        /* 0x00B*/

    /**
     * Subpool used to allocate fsm_cenvParms_p.
     */
    unsigned short fsm_cenvParmsSubpool;             /* 0x00C*/

    /**
     * Key used to allocate fsm_cenvParms_p.
     */
    unsigned char fsm_cenvParmsKey;                  /* 0x00E*/

    /**
     * Flag set if we should decrement the bind count in the bind data
     * represented by drm_bindDataToBindTo.
     */
    unsigned char drm_decrementBindCountInBindData;  /* 0x00F*/

    /**
     * The ENQ token for the bind ENQ.
     */
    enqtoken shr_bindEnqToken;                       /* 0x010*/

    /**
     * The metal C environment that was used by the failing function.
     */
    struct __csysenvtoken_s* fsm_cenv_p;             /* 0x030 */

    /**
     * The parms used to create the metal C environment in fsm_cenv_p.  This
     * storage was allocated using storageObtain.  If this is set, then we will
     * free both the parms and metal C environment (in fsm_cenv_p) on a
     * failure.
     */
    struct __csysenv_s* fsm_cenvParms_p;             /* 0x038 */

    /**
     * The user token used to connect to the SGOO shared memory.  If this field
     * is set, we will detach from the shared memory on a failure.
     */
    long long fsm_sgooUserToken;                     /* 0x040 */

    /**
     * Pointer to the ARMV to decrement if fsm_decrementArmvUseCount is set.
     */
    void* fsm_armvToDecrement;                       /* 0x048*/

    /**
     * Pointer to the angel process data whose bind count we need to decrement.
     * If this pointer is set, we will decrement the bind count.
     */
    angel_process_data* drm_apdToDecrementBindCount_p; /* 0x050*/

    /**
     * Pointer to the bind data we created during bind.  This was allocated
     * from a cell pool in the SGOO.
     */
    struct angelClientBindData* drm_bindData_p;      /* 0x058*/

    /**
     * Pointer to storage used to create the bind token pool inside the
     * client bind data control block.  This storage is allocated using malloc
     * on the cenv used by the client authorized code.
     */
    void* drm_bindDataTokenPool_p;                   /* 0x060*/

    /**
     * Pointer to the cell used to create the angel client process data
     * control block.  This was allocated froma cell pool in the SGOO.
     */
    struct angelClientProcessData* drm_clientProcessData_p; /* 0x068*/

    /**
     * Storage used to copy the server client function module into local
     * storage.  This storage is released using free().
     */
    bbgzasvt_header* scfmCopy_p;                     /* 0x070*/

    /**
     * The bind data node used to insert into the angel process data bind list.
     */
    struct angelClientBindDataNode* apdBindDataNode_p;    /* 0x078*/

    /**
     * The ENQ token for the angel process data bind list.
     */
    enqtoken apdBindDataListEnqToken;                /* 0x080*/

    /**
     * The cell from the bind data token cell pool.  This should be returned
     * to the bind data referenced in the token.
     */
    struct angelClientBindToken* drm_bindToken_p;    /* 0x0A0*/

    /**
     * Pointer to the client bind data whose invoke count should be decremented.
     */
    struct angelClientBindData* drm_bindDataToDecrementInvoke_p; /* 0x0A8*/

    /**
     * Pointer to the local invoke parameter, which must be freed back to the
     * metal C heap.
     */
    void* drm_localInvokeArgStruct_p;                /* 0x0B0*/

    /**
     * Pointer to the cell pool used by clients to obtain dynamic area.
     * This should be released to the heap by calling free().
     */
    void* drm_clientDynAreaPool_p;                   /* 0x0B8*/

    /**
     * A pointer to the ARMV used to call the routine in the DRM.  This ARMV
     * can be safely used to call the DRM inside the ARR because its count
     * reflects the client's attachment to it.
     */
    void* fsm_armvUsedOnMethodCall_p;                /* 0x0C0*/

    /**
     * Pointer to the client bind data used to issue a client bind.  This bind
     * data may have its bind count decremented and bind token returned to the
     * pool of bind tokens.
     */
    struct angelClientBindData* drm_bindDataToBindTo_p; /* 0x0C8*/

    /**
     * Bit is set if the bind ENQ was held on entry to the ARR.
     */
    unsigned char shr_heldBindEnqOnEntry;            /* 0x0D0*/

    /**
     * Watermark to use when detaching from all ARMVs.
     */
    unsigned char fsm_armvWatermark;                 /* 0x0D1*/

    /**
     * Flag set if we should try to detach from all ARMVs.
     */
    unsigned char fsm_detachFromAllARMVs;            /* 0x0D2*/

    /**
     * Flag set if we called the SCFM initialization routine during bind, and
     * need to call the uninitialize routine in the ARR.
     */
    unsigned char drm_callScfmUninitialize;          /* 0x0D3*/

    /**
     * Available for use.
     */
    unsigned char _available1[4];                    /* 0x0D4*/

    /**
     * Pointer to the SGOO that we connected to.  This is used in
     * conjunction with fsm_sgooUserToken.
     */
    void* fsm_sgoo_p;                                /* 0x0D8*/

    /**
     * Pointer to the latent parameter area that was passed to us
     * when the PC was called.
     */
    AngelPCParmArea_t* fsm_latentPcParm_p;           /* 0x0E0*/

    /**
     * The SGOO that we used to obtain the angel client bind data.
     */
    void* drm_bindData_sgoo_p;                       /* 0x0E8*/

    /**
     * Available for use.
     */
    unsigned char _available2[272];                  /* 0x0F0*/
} angel_client_pc_recovery;                          /* 0x200*/

#endif
