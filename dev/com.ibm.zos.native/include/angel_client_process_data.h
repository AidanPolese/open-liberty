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
#ifndef _BBGZ_ANGEL_CLIENT_PROCESS_DATA_H
#define _BBGZ_ANGEL_CLIENT_PROCESS_DATA_H

#include "bbgzsgoo.h"

#include "gen/ikjtcb.h"
#include "client_dynamic_area_cell_pool.h"

/**@file
 * Process Level Data used by the Angel in a client process.
 */

/** String used to look up the default angel client process data via name token. */
#define ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME "BBGZACPD"

/** String used to look up a named angel client process data via name token.  Use snprintf to fill in */
#define ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED "BBGZACPD%3.3X"

/** Prefix used to look up a named angel client process data via system level name token. */
#define ANGEL_CLIENT_PROCESS_DATA_TOKEN_NAME_NAMED_PREFIX "BBGZAC"

/** String used to set the angel client process data eye catcher. */
#define ANGEL_CLIENT_PROCESS_DATA_EYE_CATCHER "BBGZACPD"

/**
 * Struct used to hold process level data used by the angel in a client process,
 * primarily to keep track of binds to Liberty servers.  These control blocks
 * are allocated from a cell pool in the BBGZSGOO, in shared above the bar
 * storage.  They are created by a client process on first bind, and are
 * returned to the pool on last unbind.
 */
typedef struct angelClientProcessData {
    /**
     *  The eye catcher (BBGZACPD).
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
     * The identifier representing which instance of the ARMV this client is
     * using.  Each ARMV has a sequence number, which is incremented each time
     * a new ARMV is loaded.  The angel process keeps the sequence number in its
     * private storage.  curArmvSeq is the sequence number that this client is
     * currently using.  If the current ARMV sequence is greater than this number,
     * then this client should attach itself to the current ARMV and store the
     * sequence of that ARMV here.
     */
    volatile unsigned char   curArmvSeq;                         /* 0x00C */

    /**
     * Storage key used to allocate cenvParms_p.
     */
    unsigned char            cenvParmsKey;                       /* 0x00D */

    /**
     * Subpool used to allocate cenvParms_p.
     */
    unsigned short           cenvParmsSubpool;                   /* 0x00E */

    /**
     * A pointer to the metal C environment used by the client while running
     * code owned by the angel (fixed shim and dynamic replaceable modules).
     */
    struct __csysenvtoken_s* cenv_p;                             /* 0x010 */

    /**
     * A pointer to the parameters used to create the metal C environment
     * pointed to by cenv_p.  This storage is allocated using storageObtain
     * and must be freed by storageRelease.
     */
    struct __csysenv_s*      cenvParms_p;                        /* 0x018 */

    /**
     * Cached pointer to the SGOO.
     */
    bbgzsgoo*                sgoo_p;                             /* 0x020 */

    /**
     * The stoken of the client process.
     */
    SToken                   stoken;                             /* 0x028 */

    /**
     * Head of the list of all client binds for this process.
     */
    struct angelClientBindDataNode* bindHead_p;                  /* 0x030 */

    /**
     * RESMGR watching the IPT or jobstep task of the client process.
     */
    int                      taskResmgrToken;                    /* 0x038 */

    /**
     * RESMGR watching the client address space.
     */
    int                      addressSpaceResmgrToken;            /* 0x03C */

    /**
     * The TCB address of the task being monitored by the task RESMGR.
     */
    tcb*                     tcbForTaskResmgr;                   /* 0x040 */

    /**
     * Dynamic area pool for clients calling invoke.
     */
    unsigned long long       clientDynAreaPool;                  /* 0x048 */

    /**
     * The TTOKEN of the task being monitored by the task RESMGR.
     */
    TToken                   ttokenForTaskResmgr;                /* 0x050 */

    /**
     * Client dynamic area cell pool information.
     */
    ClientDynamicAreaCellPoolInfo_t clientDynAreaPoolInfo;       /* 0x060 */

    /**
     * Available for use.
     */
    unsigned char            _available[384];                    /* 0x080 */
} AngelClientProcessData_t;                                      /* 0x200 */

#include "angel_client_bind_data.h"
#include "angel_client_pc_recovery.h"

/**
 * Retrieve the client process data from its name token.  There is one client
 * process data per client per angel instance that the client is connected to.
 * Therefore there can be more than one process data control block per client
 * if the client is connected to multiple named angels.
 *
 * @param angelAnchor_p A pointer to the angel anchor control block representing the
 *                      angel instance whose process data we are retrieving.
 *
 * @return A pointer to the client process data control block, or NULL if
 *         the client process data could not be found.
 */
AngelClientProcessData_t* getAngelClientProcessData(AngelAnchor_t* angelAnchor_p);

/**
 * Returns a pointer to the angel client process data for the process represented by
 * the input stoken.
 *
 * @param stoken_p A pointer to the 8 byte stoken which represents the process
 *                 whose angel client process data should be returned.
 * @param angelAnchor_p A pointer to the angel anchor control block representing the
 *                      angel whose process data should be returned.
 *
 * @return A pointer to the angel client process data for the requested process, or
 *         NULL if none.
 */
AngelClientProcessData_t* getAngelClientProcessDataByStoken(void* stoken_p, AngelAnchor_t* angelAnchor_p);

/**
 * Creates the angel client process data control block for a client process.
 * The caller should hold the ENQ used for client bind/unbind, and should have
 * verified that the process data does not already exist by calling
 * getAngelClientProcessData() and getting back a NULL return value.
 *
 * @param sgoo_p A pointer to the SGOO control block.
 * @param curArmvSequence The sequence number of the ARMV currently in use.
 * @param recovery_p A pointer to the ARR recovery area where we can store bits
 *                   for cleanup on an abend.
 *
 * @return A pointer to the angel client process data control block, or NULL
 *         if the control block could not be created.
 */
AngelClientProcessData_t* createAngelClientProcessData(bbgzsgoo* sgoo_p, unsigned char curArmvSequence, angel_client_pc_recovery* recovery_p);

/**
 * Destroys the angel client process data control block for a client process.
 * The caller should hold the ENQ used for client bind/unbind.
 *
 * @param acpd_p A pointer to the angel client process data to delete.
 * @param recovery_p A pointer to the ARR recovery area where we can store bits
 *                   for cleanup on an abend.  The portion of the ARR recording
 *                   the address of the angel client process data will be
 *                   cleared as the process data is destroyed.
 */
void destroyAngelClientProcessData(AngelClientProcessData_t* acpd_p, angel_client_pc_recovery* recovery_p);

/**
 * Checks for an existing bind to a specified server.  The caller should hold
 * the ENQ used for client bind/unbind when calling this function.
 *
 * @param acpd_p A pointer to the angel client process data for the client.
 * @param targetServerStoken_p A pointer to the stoken for the server we are
 *                             checking.
 * @param serverInstanceCount The instance of the PGOO associated with the target
 *                            server.  Since stokens can be re-used (_BPX_SHAREAS)
 *                            this becomes a way to distingiush between a server
 *                            that just stopped and a server that just replaced it.
 *
 * @return The client bind data object if a bind is found.
 */
struct angelClientBindData* checkForExistingBind(AngelClientProcessData_t* acpd_p, SToken* targetServerStoken_p, int serverInstanceCount);

/**
 * Adds a new bind to the list of binds in the angel client process data.  The
 * caller should hold the ENQ used for client bind/unbind when calling this
 * function.
 *
 * @param acpd_p A pointer to the angel client process data for the client
 *               performing the bind.
 * @param bindData_p A pointer to the bind data representing the bind from
 *                   the client to the liberty server.
 *
 * @return 0 if the bind was successfully added to the list of binds.
 */
int addBindToClientProcessData(AngelClientProcessData_t* acpd_p, struct angelClientBindData* bindData_p);

/**
 * Removes a bind from the list of binds in the angel client process data.  The
 * caller should hold the ENQ used for client bind/unbind when calling this
 * function.
 *
 * @param bindData_p A pointer to the bind data representing the bind from
 *                   the client to the liberty server, which is to be removed
 *                   from the list of binds for this client.
 *
 * @return 0 if the bind was successfully removed from the list of binds.
 */
int removeBindFromClientProcessData(struct angelClientBindData* bindData_p);

#endif
