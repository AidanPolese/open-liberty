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
#ifndef _BBGZ_ANGEL_CLIENT_BIND_DATA_H
#define _BBGZ_ANGEL_CLIENT_BIND_DATA_H

/**@file
 * Data describing the link between a client process and a Liberty server.
 */

#include "angel_client_process_data.h"
#include "angel_process_data.h"

#define ANGEL_CLIENT_BIND_DATA_EYECATCHER "BBGZACBD"

/**
 * Data structure holding a count of the number of binds driven against this
 * client/server pair.  The server can set the 'serverIsEnding' bit, which signals
 * that the server is ending and that only unbinds should be honored.
 *
 * The structure also keeps track of how many tasks are currently running code
 * in the SCFM for the server that is bound to.  This helps the server figure
 * out when its OK to delete the SCFM after a server termination.
 */
typedef struct angelClientDataBindCount {
    unsigned int serverIsEnding : 1, //!< Set when no more binds or invokes should be allowed.
                 _available : 7,     //!< Available for use
                 bindCount : 24;     //!< Count of active binds.
    unsigned int invokeCount;        //!< Count of tasks currently in invoke.
} AngelClientDataBindCount_t;

/**
 * Data structure describing the link between a client process and a Liberty
 * server.  There is one client bind data object for each client/server link.
 * A client can bind to an individual Liberty server more than once.  A bind
 * count is contained in this object to keep track of the number of binds.  The
 * bind is not broken until the number of unbind calls equals the number of
 * bind calls.
 *
 * Client bind data structs are chained together in a single-threaded list,
 * headed in the angel client process data, and chained by the next_p pointer.
 * Users of this list (read or modify) should hold the ENQ used for bind/unbind.
 *
 * The storage for this control block is obtained from shared above the bar
 * storage, from a cell pool hung off the SGOO.
 */
typedef struct angelClientBindData {
    /**
     *  The eye catcher (BBGZACBD).
     */
    unsigned char            eyecatcher[8];                      /* 0x000 */

    /**
     * Version number of the control block.
     */
    unsigned short           version;                            /* 0x008 */

    /**
     * Length of the control block (size of storage obtained).
     */
    unsigned short           length;                             /* 0x00A */

    /**
     * Available for use.
     */
    unsigned char            _available1[2];                     /* 0x00C */

    /**
     * Version number of the bind token contained in the bindTokenCellPool.
     * If angel maintenance is applied after this control block is created,
     * and a new (larger) version of the bind token is available, the angel
     * must continue to use the original version for this bind.
     */
    unsigned short           bindTokenVersion;                   /* 0x00E */

    /**
     * Cell pool of bind tokens.
     */
    unsigned long long       bindTokenCellPool;                  /* 0x010 */

    /**
     * The SToken of the server we are connected to.
     */
    SToken                   serverStoken;                       /* 0x018 */

    /**
     * The number of client binds outstanding.  When this number returns to
     * zero, the bind data can be cleaned up.
     */
    AngelClientDataBindCount_t bindCount;                        /* 0x020 */

    /**
     * A pointer to the angel client process data.
     */
    AngelClientProcessData_t* clientProcessData_p;               /* 0x028 */

    /**
     * A copy of the common function module.
     */
    bbgzasvt_header*          scfmCopy_p;                        /* 0x030 */

    /**
     * A pointer to the angel process data for the server we are bound to.
     */
    angel_process_data*       apd_p;                             /* 0x038*/

    /**
     * An instance counter, uniquely representing this bind data generated
     * on this system until the system is IPLed.  The current instance count
     * is stored in the SGOO.
     */
    unsigned int              instanceCount;                     /* 0x040 */

    /**
     * The instance count from the PGOO of the server we are connected to.
     * This lets us distinguish two server instances that were both assigned
     * the same STOKEN because they ran in the same BPXAS.
     */
    int                       serverInstanceCount;               /* 0x044 */

    /**
     * The client token that we pass to the server owned code.  We could use
     * the serverInstanceCount, but some server services use the client token
     * as the user token to IARV64, and authorized callers must ensure that
     * both words are non-zero.
     */
    unsigned long long        clientToken;                       /* 0x048 */

    /**
     * Available for use.
     */
    unsigned char            _available3[176];                   /* 0x050 */
} AngelClientBindData_t;                                         /* 0x100 */

#define ANGEL_CLIENT_BIND_TOKEN_EYECATCHER "BBGZACBT"

/**
 * The bind token represents a client attachment to a Liberty server.  Since
 * more than one client API could trigger a bind, there can be more than one
 * bind token per bind to a Liberty server.  The overall bind is represented
 * by the angel client bind data.
 *
 * This structure is opaque to the caller.  The caller only receives a pointer
 * to this structure, and the bind data instance counter, stored a a quad word
 * (16 bytes).
 *
 * The storage for this control block is allocated in the caller's private area.
 */
typedef struct angelClientBindToken {
    /**
     * The eye catcher (BBGZACBT).
     */
    unsigned char            eyecatcher[8];                      /* 0x000 */

    /**
     * Version number of the control block.
     */
    unsigned short           version;                            /* 0x008 */

    /**
     * Length of the control block (size of storage obtained).
     */
    unsigned short           length;                             /* 0x00A */

    /**
     * Available for use.
     */
    unsigned char            _available1[4];                     /* 0x00C */

    /**
     * Pointer to the angel client bind data.
     */
    AngelClientBindData_t*   clientBindData_p;                   /* 0x010 */

    /**
     * Timestamp from when this bind token was created.
     */
    unsigned long long       timestamp;                          /* 0x018 */

    /**
     * Available for use.
     */
    unsigned char            _available2[32];                    /* 0x020 */
} AngelClientBindToken_t;                                        /* 0x040 */

/** Data structure used to keep a list of client binds. */
typedef struct angelClientBindDataNode {
    struct angelClientBindDataNode* next_p; //!< The next bind.
    AngelClientBindData_t* data_p; //!< Data for the current bind.
} AngelClientBindDataNode_t;
#endif
