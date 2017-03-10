/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_SERVER_LOCAL_COMM_DATA_STORE_H
#define _BBOZ_SERVER_LOCAL_COMM_DATA_STORE_H

#include "common_defines.h"
#include "server_local_comm_shared_memory.h"

/** Eyecatcher for local comm client data store. */
#define BBGZLDAT_EYE "BBGZLDAT"
#define RELEASED_BBGZLDAT_EYE "LDATBBGZ"

/**
 * Control block representing the message data cell pools used between a client
 * and server instance.  This storage is shared above the bar between a single
 * client and a single server.
 */
typedef struct localCommClientDataStore {
    /**
     * WARNING / WARNING / WARNING
     * This structure is mapped from Java in the introspection code.  If the
     * "length" offsets and/or offsets to anchors of other structures pointed
     * to from this structure change, then updates will neeed to be made to
     * the introspection code (ex., com.ibm.ws.zos.channel.local.queuing.internal.IntrospectHelper.java).
     */

    /** Eyecatcher 'BBGZLDAT' */
    unsigned char eyecatcher[8];                                     /* 0x000*/

    /** Version of this control block. */
    unsigned short version;                                          /* 0x008*/

    /** Size of this control block. */
    unsigned short length;                                           /* 0x00A*/

    /** Flags. */
    unsigned int flags;                                              /* 0x00C*/

    /** Shared memory book-keeping. */
    LocalCommSharedMemoryInfo_t info;                                /* 0x010*/

    /** Stoken for the server we are connected to. */
    SToken serverStoken;                                             /* 0x028*/

    /** Pointer to the BBGZLOCL control block for the client. */
    void* localCommClientControlBlock_p;                             /* 0x030*/

    /** Pointer to the next BBGZLDAT for the next client/server pair. */
    void* nextLDAT_p;                                                /* 0x038*/

    /** Anchor for the 4K message cell pool. */
    unsigned long long anchor4K;                                     /* 0x040*/

    /** Anchor for the 8K message cell pool. */
    unsigned long long anchor8K;                                     /* 0x048*/

    /** Anchor for the 32K message cell pool. */
    unsigned long long anchor32K;                                    /* 0x050*/

    /** Anchor for the 128K message cell pool. */
    unsigned long long anchor128K;                                   /* 0x058*/

    /** Anchor for the TBD large message cell pool. */
    unsigned long long anchorLARGE;                                  /* 0x060*/

    /** Available for future use. */
    unsigned char _available[408];                                   /* 0x068*/

} LocalCommClientDataStore_t;                                        /* 0x200*/

/** Eyecatcher for local comm client data store. */
#define BBGZLMSG_EYE "BBGZLMSG"
#define BBGZLMSG_LARGEDATA_EYE "BBGZLGMG"
#define RELEASED_BBGZLMSG_LARGEDATA_EYE "LGMGBBGZ"

/** Version definitions messages */
#define BBGZLMSGL_INITIAL_VERSION 1
#define BBGZLMSG_LARGEDATA_CUR_VER 1

/** Information needed to manage partial reads of the related data */
typedef struct readInfo_PLO_Area {
    /** Number of bytes already read. */
    unsigned long long bytesAlreadyRead;                             /* 0x000*/

    unsigned int _available1;                                        /* 0x008*/

    /** Flags. */
    struct {
        /** The dataAreaPtr is NOT contained within the cellpools.  */
        int dataAreaNotInCellpools : 1;

        /**
         * The reader/receiver of this LMSG has established access to the shared memory
         * obtained outside of the LDAT pools.
         */
        int readerObtainedAccess : 1;

        /** Available flag space. */
        int _available : 30;
    } flags;                                                         /* 0x00C*/
} ReadInfo_PLO_Area_t;                                               /* 0x010*/

/** Information needed to manage data within shared memory outside of the set of LDAT cellpools. */
typedef struct largeDataAttachmentInfo {
    unsigned long long sharingUserToken;
    unsigned long long owningUserToken;
} LargeDataAttachmentInfo_t;

/**
 * Local comm Large Data Message header (Message too large for cellpools).
 */
typedef struct largeDataMessageHeader {
    /** Eyecatcher 'BBGZLGMG' */
    unsigned char eyecatcher[8];                                     /* 0x000 */

    /** Version of this control block. */
    unsigned short version;                                          /* 0x008 */

    /** Size of this control block. */
    unsigned short length;                                           /* 0x00A */

    /** Available for use. */
    char _available1[4];                                             /* 0x00C */

    /** Info for managing a Shared Memory data area outside of the LDAT cellpools. */
    LargeDataAttachmentInfo_t largeData;                             /* 0x010 */

} LargeDataMessageHeader_t;                                          /* 0x020 */


/**
 * Local comm data cell.
 */
typedef struct localCommDataCell {
    /** Eyecatcher 'BBGZLMSG' */
    unsigned char eyecatcher[8];                                     /* 0x000*/

    /** Version of this control block. */
    unsigned short version;                                          /* 0x008*/

    /** Size of this control block. */
    unsigned short length;                                           /* 0x00A*/

    /** Available for use. */
    char _available1[4];                                             /* 0x00C*/

    /**
     * Serialized read information.
     * WARNING: Target of PLO.  Needs to remain on Quadword.
     */
    ReadInfo_PLO_Area_t readInfo;                                    /* 0x010*/

    /** Info for managing a Shared Memory data area outside of the LDAT cellpools. */
    LargeDataAttachmentInfo_t largeData;                             /* 0x020*/

    /** Pool where the LMSG cell needs to be returned to. */
    unsigned long long owningPoolID;                                 /* 0x030*/

    /** Size of the message area pointed to by dataAreaPtr, in bytes. */
    unsigned long long dataAreaSize;                                 /* 0x038*/

    /**
     * Pointer to the data.  Points immediately after this structure OR to a Shared
     * memory object obtained outside of the available set of LDAT cellpools
     * (managed by "largeData" info).
     */
    void * dataAreaPtr;                                              /* 0x040*/

    /** Available for use. */
    unsigned char _available2[184];                                  /* 0x048*/
} LocalCommDataCell_t;                                               /* 0x100*/


/* Forward declares of structs */
struct localCommClientAnchor;

/**
 * Allocate an initialize a data store control block.
 *
 * @param serverStoken_p Pointer to the STOKEN of the server the client is connecting to.
 * @param anchor_p Pointer to the client's LOCL.
 * @param allocationUserToken The user token to use when allocating shared memory.
 * @param sharingUserToken The user token to use when accessing a shared memory object.
 *
 * @return A pointer to the data store, or NULL if the data store could not be created.
 */
LocalCommClientDataStore_t* createLocalCommDataStore(SToken* serverStoken_p, struct localCommClientAnchor* anchor_p, long long allocationUserToken, long long sharingUserToken);

/**
 * Gets a cell from the data store.
 *
 * @param dataStore_p The data store
 * @param datalen The size of the cell, in bytes.
 *
 * @return A pointer to the cell, or NULL if no cell was available.
 */
LocalCommDataCell_t* getLocalCommDataStoreCell(LocalCommClientDataStore_t* dataStore_p, unsigned long long datalen);

/**
 * Frees a cell from the data store.
 *
 * @param dataStore_p The data store
 * @return None.
 */
 void freeLocalCommDataStoreCell(LocalCommDataCell_t* msgCell_p);

 /**
  * Returns 1 if we can handle the input data length.
  *
  * @param dataLen Length of the data to for a LMSG.
  * @return 1 if OK, 0 if not.
  */
 int dataLengthSupported(unsigned long long dataLen);
#endif
