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

#ifndef _BBOZ_SERVER_WOLA_SHARED_MEMORY_ANCHOR_H
#define _BBOZ_SERVER_WOLA_SHARED_MEMORY_ANCHOR_H

#include <string.h>     // Needed for memcpy in inlined code

#include "ieantc.h"
#include "name_tokens.h"
#include "server_wola_registration.h"
#include "server_wola_shared_memory_manager.h"

/** @file
 * Defines the WOLA shared memory anchor.
 */

#define BBOASHR_EYE "BBOASHR "
#define BBOASHR_VERSION 2   // TODO: is this right?

/**
 * Size of the WOLA shared memory area (anchored by BBOASHR).
 */
#define WOLA_SMA_SIZE_MB 32         

/**
 * Defines the WOLA shared memory anchor.  The WOLA shared memory anchor is allocated by
 * a Liberty server in a WOLA group (where the WOLA group is defined to be the first
 * part of the WOLA three part name for Liberty).  It is used by all servers declaring
 * to be a member of the WOLA group, as well as any clients wishing to connect to a
 * Liberty server in that WOLA group.  The anchor occupies the first part of a shared
 * above the bar storage area allocated by a Liberty server in the WOLA group.  This
 * shared memory area lives forever (is not released until an IPL).
 *
 * The structure for the shared memory anchor is shared with the tWAS structure for the
 * BBOASHR.  Those fields which do not apply to Liberty are marked as reserved.  The
 * mapping is for convenience only, there is no technical reason why a new mapping could
 * not be created for Liberty, since tWAS and Liberty WOLA registrations cannot occupy
 * the same shared memory anchor.
 */
typedef struct wolaSharedMemoryAnchor {
    /** Eye catcher 'BBOASHR' */
    unsigned char  eye[8];                                    /* 0x000 */

    /** Version of the anchor control block. */
    unsigned short version;                                   /* 0x008 */

    /** Available for future use. */
    unsigned short _rsvd1;                                    /* 0x00A */

    /** Size of this control block. */
    unsigned int   size;                                      /* 0x00C */

    /** Flags in use by tWAS that do not apply to Liberty. */
    unsigned int  _tWAS_rsvd1:3,                            /* 0x010.1 */

    /** Remainder of flag byte 1 (available). */
                   flagByte1:5,                             /* 0x010.4 */

    /** Flag byte 2 */
                   flagByte2:8,                               /* 0x011 */

    /** Flag byte 3 */
                   flagByte3:8,                               /* 0x012 */

    /** Flag byte 4 */
                   flagByte4:8;                               /* 0x013 */

    /** Available for use. */
    unsigned char  _rsvd2[4];                                 /* 0x014 */

    /** Pointer to this control block. */
    void*          self_p;                                    /* 0x018 */

    /** CDSG counter for RGE chain. */
    unsigned long long rgeChainCounter;                       /* 0x020 */

    /** CDSG chain of RGEs. */
    struct wolaRegistration * firstRge_p;                     /* 0x028 */

    /** Unavailable - used in tWAS WOLA prototype. */
    unsigned char  _tWAS_rsvd2[8];                            /* 0x030 */

    /** WOLA group - first part of WOLA three part name (tWAS = DGN). */
    unsigned char  wolaGroup[8];                              /* 0x038 */

    /** Pointer to the WOLA shared memory manager. */
    WolaSharedMemoryManager_t* sharedMemoryManager_p;         /* 0x040 */

    /** Max number of services for any single RGE.  0 = unlimited. */
    unsigned int   maxServicesPerRegistration;                /* 0x048 */

    /** Unavailable - used in tWAS for NonSS PC number. */
    unsigned char  _twas_rsvd3[4];                            /* 0x04C */

    /** Cell pool for the entire BBOASHR storage area. */
    unsigned long long outerCellPoolID;                       /* 0x050 */

    /** Cell pool used for registrations. */
    unsigned long long registationCellPoolID;                 /* 0x058 */

    /** Unavailable - used in tWAS WOLA prototype. */
    unsigned char  _twas_rsvd4[4];                            /* 0x060 */

    /** Maximum connections for any single RGE (0 = unlimited) */
    unsigned int   maxConnectionsPerRegistration;             /* 0x064 */

    /** Cell pool used to allocate connection handles. */
    unsigned long long connectionHandleCellPoolID;            /* 0x068 */

    /** Cell pool used to allocate connection waiters. */
    unsigned long long connectionWaiterCellPoolID;            /* 0x070 */

    /** Available for use. */
    unsigned char  _rsvd4[24];                                /* 0x078 */

    /** Address of the internal trace table -- TBD for Liberty. */
    void*          traceTable_p;                              /* 0x090 */

    /**
     * The number of bytes requested when this anchor was created.  In tWAS
     * this was the number of bytes requested by the daemon.  In Liberty,
     * there is no daemon, so it's whatever Liberty server created this.
     */
    unsigned int   anchorRequestedSize;                       /* 0x098 */

    /** Available for use. */
    unsigned char  _rsvd5[4];                                 /* 0x09C */

    /** CDSG - Queue of shared memory blocks that are no longer in use. */
    unsigned long long deadMemoryCount;                       /* 0x0A0 */

    /** CDSG - Queue of shared memory blocks that are no longer in use. */
    void*          deadMemoryQueue_p;                         /* 0x0A8 */

    /** Address of trace table properties.  This is TBD in Liberty. */
    void*          traceTableProperties_p;                    /* 0x0B0 */

    /** Cell pool used to create the available services cell pool. */
    unsigned long long availServiceCellPoolID;           /* 0x0B8 */

    /** Cell pool used to create the waiting services cell pool. */
    unsigned long long waitServiceCellPoolID;         /* 0x0C0 */

} WolaSharedMemoryAnchor_t;                                   /* 0x0C8 */


/**
 * An element in a list of WOLA shared memory attachments.  The list is
 * anchored in the server common function module process data.
 */
typedef struct wolaClientSharedMemoryAttachmentInfo {
    /** Eye catcher 'BBOASMA ' */
    unsigned char eyecatcher[8];                            /* 0x000 */

    /** Version */
    unsigned short version;                                 /* 0x008 */

    /** Available for use. */
    unsigned short _rsvd1;                                  /* 0x00A */

    /** Size of this control block. */
    unsigned int size;                                      /* 0x00C */

    /** Pointer to the WOLA shared memory anchor. */
    struct wolaSharedMemoryAnchor* wolaAnchor_p;            /* 0x010 */

    /** Pointer to the next shared memory attachment info. */
    struct wolaClientSharedMemoryAttachmentInfo* next_p;    /* 0x018 */

    /** Count of attachments to this shared memory anchor. */
    unsigned long long attachCount;                         /* 0x020 */

    /** Available for use. */
    unsigned char _available1[216];                         /* 0x028 */

} WolaClientSharedMemoryAttachmentInfo_t;                            /* 0x100 */

#define BBOASMA_EYE "BBOASMA "


/**
 * Connect a client to some shared memory owned by a WOLA group.
 *
 * @param wolaGroup An 8 character WOLA group name.
 *
 * @return A pointer to the requested shared memory anchor.
 */
struct wolaSharedMemoryAnchor* clientConnectToWolaSharedMemoryAnchor(char* wolaGroup);


/**
 * Disconnect a client from some shared memory owned by a WOLA group.
 *
 * @param anchor_p A pointer to the shared memory anchor.
 *
 * @return 0 if the detach was successful, non-zero if not.
 */
int clientDisconnectFromWolaSharedMemoryAnchor(struct wolaSharedMemoryAnchor* anchor_p);


/**
 * Clean up any remaining attachments to shared memory.  This function is
 * invoked when the bbgzscfm is being unloaded from memory, after the last
 * client unbind, or on client termination.
 */
void cleanupClientWolaSharedMemoryAttachments(void);


/**
 *
 * Mapping of the MVS name token used to store the WOLA group shared memory area (BBOASHR) address.
 */
struct name_token_map {
    void * bboashr_p;
    long long unused;
};


/**
 * Note: the wola_group name is blank-padded to 8 bytes for the token_name.
 *
 * @param token_name - Output - will contain the token name (must be at least 16 bytes long)
 * @param wola_group - Input - The wola group name.
 */
#pragma inline(getBboashrTokenName)
static void getBboashrTokenName(char * token_name, char * wola_group) {
    memcpy(token_name, BBGZ_BBOASHR_NAME_TOKEN_USER_NAME_PREIFX, 8);

    for (int i = 0, foundNull = 0; i < 8; ++i) {
        foundNull = (foundNull || ((*(wola_group + i)) == 0));
        *(token_name + 8 + i) = ((foundNull) ? ' ' : (*(wola_group + i)));
    }
}


/**
 * @param wola_group - wola group, blank-padded to 8 chars.
 * @param rc - The rc from IEAN4RT (mvs name token retrieve)
 *
 * @return the bboashr_p for the given wola_group, or NULL if no BBOASHR exists
 *         for the wola group.
 */
#pragma inline(getBboashrForWolaGroup)
static void * getBboashrForWolaGroup( char * wola_group, int * rc ) {
    // Construct the MVS name token name.
    char token_name[16];
    getBboashrTokenName( token_name, wola_group );

    // Lookup the bboashr pointer in the name token
    struct name_token_map name_token;
    int iean4rt_rc;

    iean4rt(IEANT_SYSTEM_LEVEL,
            token_name,
            (char *) &name_token,
            &iean4rt_rc);

    *rc = iean4rt_rc;

    if (iean4rt_rc == 0) {
        return name_token.bboashr_p;
    } else {
        return NULL;
    }
}

#endif
