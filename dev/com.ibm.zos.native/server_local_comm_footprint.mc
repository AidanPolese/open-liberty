/*
 * server_local_comm_footprint.mc
 *
 *  Created on: Sep 19, 2013
 *      Author: ginnick
 */

#include <ctype.h>
#include <ieac.h>
#include <metal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "include/common_defines.h"
#include "include/common_mc_defines.h"
#include "include/server_local_comm_client.h"
#include "include/server_local_comm_footprint.h"
#include "include/server_local_comm_shared_memory.h"
#include "include/server_local_comm_stimer.h"

#include "include/ras_tracing.h"

#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ikjtcb.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_LCOM_FOOT

#define TP_SERVER_LCOM_QUEUE_CREATETBLE_ENTRY             1




/**********************************************************************/
/*                                                                    */
/* CF Footprint Services                                              */
/*                                                                    */
/**********************************************************************/
int getFootprintStorageSize(int inNumEntries);



/**
 * Purpose: Create Channel Framework Footprint Table
 *
 *
 * Returns: Pointer to created table.
 *
 * Notes/Dependencies:
 *
 */
CF_FootprintTable* createLocalCommFootprintTable(LocalCommSharedMemoryInfo_t* info_p) {

  if (info_p == NULL) {
      return NULL;  // Bad parms
  }

  // The number of entries
  int localNumberEntries = LOCAL_COMM_FOOTPRINT_DEF_NUMENTRIES;
  int tableSize          = getFootprintStorageSize(localNumberEntries);

  CF_FootprintTable * localFootprintTable_Ptr = getLocalCommSharedStorage(info_p, tableSize);

  if (localFootprintTable_Ptr) {

    // Clear Table and first entry
    memset(localFootprintTable_Ptr, 0, tableSize);

    // Eyecatcher
    memcpy(localFootprintTable_Ptr->m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_TABLE_EYE,
           sizeof(localFootprintTable_Ptr->m_eyecatcher));

    // Set number of entries & starting entry index in the table
    localFootprintTable_Ptr->m_totalEntries   = localNumberEntries;
    localFootprintTable_Ptr->m_availableEntry = 0;

  }


  return localFootprintTable_Ptr;

} // end, createLocalCommFootprintTable

/**
 * Purpose: Create Channel Framework Footprint Table for a Connection
 *
 *
 * Returns: Pointer to created table.
 *
 * Notes/Dependencies:
 *
 */
void createLocalCommConnectionFootprintTable(LocalCommConnectionHandle_t* bbgzlhdl_p) {

  if (bbgzlhdl_p == NULL) {
      return;  // Bad parms
  }

  bbgzlhdl_p->footprintTable = createLocalCommFootprintTable(&(bbgzlhdl_p->bbgzlscl_p->localCommClientControlBlock_p->info));

} // end, createLocalCommChannelFootprintTable

/*
 * Purpose: Get next available Footprint Entry index
 *
 *
 * Returns: Footprint entry index
 *
 * Notes/Dependencies:
 *
 */
static int getNewFootprintEntry(CF_FootprintTable* inFootprintTable_Ptr)
{

  int oldEntryIndex, newEntryIndex;

  oldEntryIndex = inFootprintTable_Ptr->m_availableEntry;
  do
  {
    newEntryIndex = (oldEntryIndex+1) % inFootprintTable_Ptr->m_totalEntries;
  } while(cs((cs_t *) &oldEntryIndex,
             (cs_t *) &inFootprintTable_Ptr->m_availableEntry,
             (cs_t)   newEntryIndex
             ));

  // Remember if we wrapped.
  if (newEntryIndex == 0) {
      inFootprintTable_Ptr->m_flags.m_tableWrapped = 1;
  }

  return oldEntryIndex;

} // end, getNewFootprintEntry


/*
 * Purpose: Update a specific entry in the table
 *
 *
 * Returns: 0
 *
 * Notes/Dependencies:
 *
 */
static int updateFootprintEntry(CF_FootprintTable* inFootprintTable_Ptr,
                         int inEntryIndex,
                         CF_FootprintEntry* entryPtr)
{

  // Copy entry into table
  memcpy(&(inFootprintTable_Ptr->m_footprintEntries[inEntryIndex]),
         entryPtr,
         sizeof(CF_FootprintEntry));

  return 0;

} // end, updateFootprintEntry

/**
 *  Calculate the amount of storage to use for the Footprint Table
 */
static int getFootprintStorageSize(int inNumEntries) {
    int localNumEntries = LOCAL_COMM_FOOTPRINT_DEF_NUMENTRIES;
    if (inNumEntries > 0) {
        localNumEntries = inNumEntries;
    }
    unsigned long long tableSize = sizeof(CF_FootprintTable) + (sizeof(CF_FootprintEntry) * (localNumEntries - 1));

    return tableSize;
}

/**
 * Build and add Footprint trace entry for ClientConnectEntry
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param serverStoken_p Pointer to the STOKEN of the server to connect to.
 * @param clientID Client identifier.
 * @return 0 on success.
 */
int createLocalCommFPEntry_ClientConnectEntry(CF_FootprintTable* inFootprintTable_Ptr,
                                              SToken* serverStoken_p,
                                              unsigned int clientID) {

    CF_FootprintEntry                   localEntry = {{0}};
    CFOOT_entryData_ClientConnectEntry* localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /** Create Footprint Entry for "ClientConnectEntry"                 */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_ClientConnectEntry;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);


    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_ClientConnectEntry *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */

    // Save current asid
    localEntryData_Ptr->m_invokingASID = ((ascb*)(((psa*)0)->psaaold))->ascbasid;

    // Clear the reserved field
    memset(localEntryData_Ptr->m_Reservedz,
           0,
           sizeof(localEntryData_Ptr->m_Reservedz));

    // Save client identifier
    localEntryData_Ptr->m_ClientID = clientID;

    // Save target server stoken
    localEntryData_Ptr->m_ServerStoken = *((long long *)serverStoken_p);

    // Set the current TTOKEN into the entry
    tcb* tcb_p = ((psa*)0)->psatold;
    if (tcb_p != NULL) {
        stcb* stcb_p = (stcb*)(((tcb*)tcb_p)->tcbstcb);
        if (stcb_p) {
            memcpy(&(localEntryData_Ptr->m_CreatingTToken),
                   &(stcb_p->stcbttkn),
                   sizeof(localEntryData_Ptr->m_CreatingTToken));
        }
    }


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_ClientConnectEntry


/**
 * Build and add Footprint trace entry for ClientConnectExit
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param serverStoken_p Pointer to the STOKEN of the server to connect to.
 * @param clientID Client identifier.
 * @param clientHandle_p Pointer to the connection handle created.
 * @param statusFlags Processing Status flags from client connect function.
 * @return 0 on success.
 */
int createLocalCommFPEntry_ClientConnectExit(CF_FootprintTable* inFootprintTable_Ptr,
                                              SToken* serverStoken_p,
                                              unsigned int clientID,
                                              void* clientHandle_p,
                                              unsigned int statusFlags) {

    CF_FootprintEntry                  localEntry = {{0}};
    CFOOT_entryData_ClientConnectExit* localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "ClientConnectExit"                   */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_ClientConnectExit;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);


    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_ClientConnectExit *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */

    // Save current asid
    localEntryData_Ptr->m_invokingASID = ((ascb*)(((psa*)0)->psaaold))->ascbasid;

    // Clear the reserved field
    memset(localEntryData_Ptr->m_reservedz,
           0,
           sizeof(localEntryData_Ptr->m_reservedz));

    // Save target server stoken
    localEntryData_Ptr->m_ServerStoken = *((long long *)serverStoken_p);

    // Save connection handle pointer
    localEntryData_Ptr->m_bbgzhdl_p = clientHandle_p;

    // Save client identifier
    localEntryData_Ptr->m_ClientID = clientID;

    // Save the connect status flags
    localEntryData_Ptr->m_statusFlags = statusFlags;


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_ClientConnectExit


/**
 * Build and add Footprint trace entry for PreSend
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param data_p Pointer to the data to send.
 * @param dataLen Length of the data pointed to by data_p.
 * @param dataKey Storage key of data to send.
 * @param msgCell_p Pointer to bbgzlmsg to carry this data.
 * @param retCode Return code for send.
 * @return 0 on success.
 */
int createLocalCommFPEntry_SendExit(CF_FootprintTable* inFootprintTable_Ptr,
                                void* data_p,
                                unsigned long long dataLen,
                                unsigned char dataKey,
                                void* msgCell_p,
                                unsigned int retCode) {

    CF_FootprintEntry         localEntry  = {{0}};
    CFOOT_entryData_SendExit* localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "SendExit"                            */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_SendExit;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);


    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_SendExit *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */

    localEntryData_Ptr->m_data_p = data_p;
    localEntryData_Ptr->m_dataLen = dataLen;
    localEntryData_Ptr->m_dataKey = dataKey;

    localEntryData_Ptr->m_invokingASID = ((ascb*)(((psa*)0)->psaaold))->ascbasid;
    localEntryData_Ptr->m_msgCell_p = msgCell_p;
    localEntryData_Ptr->m_retCode = retCode;


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_SendExit

/**
 * Build and add Footprint trace entry for ClientPreview
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param clientHandle_p Pointer to the Local Comm Connection handle (bbgzlhdl).
 * @param waitForData Indication to wait for data on the preview call (non-zero indicates to wait).
 * @param timeToWait Amount of seconds to wait for data to arrive.
 * @param dataLen_p Pointer to Length of the available bytes to read returned from preview call.
 * @param previewRC Return code from preview.
 * @return 0 on success.
 */
int createLocalCommFPEntry_ClientPreview(CF_FootprintTable* inFootprintTable_Ptr,
                                         void* clientHandle_p,
                                         unsigned char waitForData,
                                         int timeToWait,
                                         unsigned long long* dataLen_p,
                                         int previewRC) {

    CF_FootprintEntry               localEntry = {{0}};
    CFOOT_entryData_ClientPreview*  localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "ClientPreview"                       */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_ClientPreview;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);


    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_ClientPreview *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */

    localEntryData_Ptr->m_clientHandle_p = clientHandle_p;
    localEntryData_Ptr->m_dataLen = *dataLen_p;
    localEntryData_Ptr->m_waitForData = waitForData;
    localEntryData_Ptr->m_timeToWait = timeToWait;
    localEntryData_Ptr->m_invokingASID = ((ascb*)(((psa*)0)->psaaold))->ascbasid;
    localEntryData_Ptr->m_retCode = previewRC;


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_ClientPreview

/**
 * Build and add Footprint trace entry for Receive
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param clientHandle_p Pointer to the Local Comm Connection handle (bbgzlhdl).
 * @param returnDataSize Length of the data available to read.
 * @param clientAreaSize Length of the read target area supplied by caller.
 * @param receiveRC Return code from receive.
 * @param freeLastRC Return code from freeLastReadData call if made.
 * @return 0 on success.
 */
int createLocalCommFPEntry_Receive(CF_FootprintTable* inFootprintTable_Ptr,
                                   void* clientHandle_p,
                                   unsigned long long returnDataSize,
                                   unsigned long long clientAreaSize,
                                   int receiveRC,
                                   unsigned short freeLastRC) {

    CF_FootprintEntry         localEntry = {{0}};
    CFOOT_entryData_Receive*  localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "Receive"                             */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_Receive;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);


    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_Receive *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_invokingASID   = ((ascb*)(((psa*)0)->psaaold))->ascbasid;
    localEntryData_Ptr->m_freeLastRC     = freeLastRC;
    localEntryData_Ptr->m_retCode        = receiveRC;
    localEntryData_Ptr->m_clientHandle_p = clientHandle_p;
    localEntryData_Ptr->m_dataAvailLen   = returnDataSize;
    localEntryData_Ptr->m_clientAreaSize = clientAreaSize;


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_Receive

/**
 * Build and add Footprint trace entry for CloseEntry
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param handleOrLOCL_p Pointer to the Local Comm Connection handle (bbgzlhdl) if the target
 * footprint table is anchored to a BBGZLOCL or a Point to the BBGZLOCL if the target footprint
 * table is anchored to a Connection handle.
 * @param statusFlags Close related status flags (defined in localCommClose).
 * @param bbgzldat_p Pointer to the related LDAT.
 * @param bbgzlscl_p Pointer to the related LSCL.
 * @return 0 on success.
 */
int createLocalCommFPEntry_CloseEntry(CF_FootprintTable* inFootprintTable_Ptr,
                                      void* handleOrLOCL_p,
                                      unsigned int statusFlags,
                                      void* bbgzldat_p,
                                      void* bbgzlscl_p) {

    CF_FootprintEntry               localEntry = {{0}};
    CFOOT_entryData_CloseEntry*     localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "CloseEntry"                          */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_CloseEntry;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);


    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_CloseEntry *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_invokingASID = ((ascb*)(((psa*)0)->psaaold))->ascbasid;
    localEntryData_Ptr->m_closeStatusFlags = statusFlags;
    localEntryData_Ptr->m_handleOrLOCL_p = handleOrLOCL_p;
    localEntryData_Ptr->m_bbgzldat_p = bbgzldat_p;
    localEntryData_Ptr->m_bbgzlscl_p = bbgzlscl_p;

    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_CloseEntry

/**
 * Build and add Footprint trace entry for CloseExit
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param handleOrLOCL_p Pointer to the Local Comm Connection handle (bbgzlhdl) if the target
 * footprint table is anchored to a BBGZLOCL or a Point to the BBGZLOCL if the target footprint
 * table is anchored to a Connection handle.
 * @param statusFlags Close related status flags (defined in localCommClose).
 * @param bbgzldat_p Pointer to the related LDAT.
 * @param bbgzlscl_p Pointer to the related LSCL.
 * @return 0 on success.
 */
int createLocalCommFPEntry_CloseExit(CF_FootprintTable* inFootprintTable_Ptr,
                                     void* handleOrLOCL_p,
                                     unsigned int statusFlags,
                                     void* bbgzldat_p,
                                     void* bbgzlscl_p) {

    CF_FootprintEntry               localEntry = {{0}};
    CFOOT_entryData_CloseExit*      localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "CloseExit"                           */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_CloseExit;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);


    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_CloseExit *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_invokingASID = ((ascb*)(((psa*)0)->psaaold))->ascbasid;
    localEntryData_Ptr->m_closeStatusFlags = statusFlags;
    localEntryData_Ptr->m_handleOrLOCL_p = handleOrLOCL_p;
    localEntryData_Ptr->m_bbgzldat_p = bbgzldat_p;
    localEntryData_Ptr->m_bbgzlscl_p = bbgzlscl_p;


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_CloseExit

/**
 * Server call to get Work queue elements from Work Queue. Entering.
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @return 0 on success.
 */
int createLocalCommFPEntry_Server_getWQES_Entry(CF_FootprintTable* inFootprintTable_Ptr) {

    CF_FootprintEntry                  localEntry = {{0}};

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "getWQES_Entry"                       */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_getWQES_Entry;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);



    /**
     * Fill in type specific information
     */

    // None.
    memset(&(localEntry.m_entryData), 0, sizeof(localEntry.m_entryData));


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_Server_getWQES_Entry

/**
 * Server call to get Work queue elements from Work Queue. Exiting.
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @return 0 on success.
 */
int createLocalCommFPEntry_Server_getWQES_Exit(CF_FootprintTable* inFootprintTable_Ptr, LocalCommWorkQueueElement * newWRQE_p, int waitOnWorkRC) {

    CF_FootprintEntry                  localEntry = {{0}};
    CFOOT_entryData_ServerGetWQESExit* localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "getWQES_Exit"                        */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_getWQES_Exit;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_ServerGetWQESExit *)
                           &(localEntry.m_entryData);


    /**
     * Fill in type specific information
     */
    memset(&(localEntry.m_entryData), 0, sizeof(localEntry.m_entryData));
    if (newWRQE_p != NULL) {
        memcpy(localEntryData_Ptr->m_clientConnHandle, newWRQE_p->clientConnHandle, sizeof(localEntryData_Ptr->m_clientConnHandle));
        localEntryData_Ptr->m_createStck = newWRQE_p->createStck;
        localEntryData_Ptr->m_requestType = newWRQE_p->requestType;
    }

    localEntryData_Ptr->m_waitOnWorkRC = waitOnWorkRC;


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_Server_getWQES_Exit

/**
 * Foot at end of addToDataQueue.
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param dataQE_p Pointer to the data queue element to add
 * @param handlePLO_CS_p Pointer to the PLO CompareAndSwap area for the associated Handle
 * @param statusFlags Pointer to status flags set within the addToDataQueue routine
 * @param rc Return code value from addToDataQueue routine
 * @return 0 on success.
 */
int createLocalCommFPEntry_addToDataQueue(CF_FootprintTable* inFootprintTable_Ptr,
                                          void *dataQE_p,
                                          void *handlePLO_CS_p,
                                          unsigned int statusFlags,
                                          unsigned int rc) {

    CF_FootprintEntry                  localEntry = {{0}};
    CFOOT_entryData_addToDataQueue*    localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "addToDataQueue"                        */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_addToDataQueue;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_addToDataQueue *)
                           &(localEntry.m_entryData);


    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_addToDataQueueStatusFlags = statusFlags;
    localEntryData_Ptr->m_rc                        = rc;
    localEntryData_Ptr->m_dataQ_p                   = dataQE_p;
    memcpy(localEntryData_Ptr->m_handlePLO_CS_area, handlePLO_CS_p, sizeof(localEntryData_Ptr->m_handlePLO_CS_area));


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_addToDataQueue

/**
 * Foot in "issueConnectRequest" routine on a failed return from a call to addToWorkQueue
 * @param inFootprintTable_Ptr Pointer the Local Comm footprint table to add an entry.
 * @param clientConnHandle_p Pointer to the Local Comm Handle token
 * @param serverStoken_p Pointer to the stoken of the target server
 * @param workqRC Return code from either addToWorkQueue or timedWaitOnWorkQueue
 * @param connectRC Return code from issueConnectRequest.
 * @return 0 on success.
 */
int createLocalCommFPEntry_issueConnectRequest_AddToWorkQFailed(CF_FootprintTable* inFootprintTable_Ptr,
                                                                LocalCommClientConnectionHandle_t* clientConnHandle_p,
                                                                SToken* serverStoken_p,
                                                                int workqRC,
                                                                int connectRC) {

    CF_FootprintEntry                                        localEntry = {{0}};
    CFOOT_entryData_issueConnectRequest_AddToWorkQFailed*    localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "issueConnectRequest_AddToWorkQFailed"*/
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_issueConnectRequest_AddToWorkQFailed;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_issueConnectRequest_AddToWorkQFailed *)
                           &(localEntry.m_entryData);


    /**
     * Fill in type specific information
     */
    memcpy(localEntryData_Ptr->m_clientHandleToken, clientConnHandle_p, sizeof(localEntryData_Ptr->m_clientHandleToken));
    localEntryData_Ptr->m_serverStoken = *((long long *)serverStoken_p);
    localEntryData_Ptr->m_workqRC      = workqRC;
    localEntryData_Ptr->m_connectRC    = connectRC;

    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_issueConnectRequest_AddToWorkQFailed

/**
 * Foot for a returned failed Connection request from Security CBIND check.
 *
 * @param targetTable Pointer the Local Comm footprint table to add an entry.
 * @param connectionHandle_p Pointer the Local Comm Handle.
 * @param returnCode Connect return code from making security check.
 * @param returnReason Connect return reason from making security check.
 * @param safReturnCode Connect SAF return code from server.
 * @param racfReturnCode Connect RACF return code from server.
 * @param racfReasonCode Connect RACF return reason from server.
 * @return 0
 */
int createLocalCommFPEntry_ClientConnectFailedSec(CF_FootprintTable* inFootprintTable_Ptr,
                                                  void* connectionHandle_p,
                                                  unsigned int statusFlags,
                                                  int   returnCode,
                                                  int   returnReason,
                                                  int   safReturnCode,
                                                  int   racfReturnCode,
                                                  int   racfReasonCode) {

    CF_FootprintEntry                       localEntry = {{0}};
    CFOOT_entryData_ClientConnectFailedSec* localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "ClientConnectFailedSec"              */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_ClientConnectFailedSec;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_ClientConnectFailedSec *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_handle_p       = connectionHandle_p;
    localEntryData_Ptr->m_statusFlags    = statusFlags;
    localEntryData_Ptr->m_returnCode     = returnCode;
    localEntryData_Ptr->m_returnReason   = returnReason;
    localEntryData_Ptr->m_safReturnCode  = safReturnCode;
    localEntryData_Ptr->m_racfReturnCode = racfReturnCode;
    localEntryData_Ptr->m_racfReasonCode = racfReasonCode;


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_ClientConnectFailedSec

/**
 * Foot for a failed createLocalCommClientServerPair request from the client
 * @param targetTable Pointer the Local Comm footprint table to add an entry
 * @param clientAnchor_p Pointer the client LOCL
 * @param serverStoken_p Pointer to target Server stoken
 * @param out_lsclCurrentState Pointer to LOCL/LSCL PLO State info
 * @param createLSCL_RC Return code from createLocalCommClientServerPair call
 * @return 0
*/
int createLocalCommFPEntry_ClientConnectCreateLSCL(CF_FootprintTable* inFootprintTable_Ptr,
                                                   LocalCommClientAnchor_t* clientAnchor_p,
                                                   SToken* serverStoken_p,
                                                   LocalComLOCL_PLO_CS_Area_t* out_lsclRequiredState_p,
                                                   int createLSCL_RC) {

    CF_FootprintEntry                         localEntry = {{0}};
    CFOOT_entryData_ClientConnectCreateLSCL*  localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "ClientConnectCreateLSCL"             */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_ClientConnectCreateLSCL;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_ClientConnectCreateLSCL *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_serverStoken = *((long long *)serverStoken_p);
    memcpy(&(localEntryData_Ptr->m_out_lsclRequiredState), out_lsclRequiredState_p, sizeof(localEntryData_Ptr->m_out_lsclRequiredState));
    memcpy(&(localEntryData_Ptr->m_currentLOCLState), &(clientAnchor_p->locl_PLO_CS_Area), sizeof(localEntryData_Ptr->m_currentLOCLState));
    localEntryData_Ptr->m_returnCode   = createLSCL_RC;
    memset(localEntryData_Ptr->m_reserved1, 0, sizeof(localEntryData_Ptr->m_reserved1));


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_ClientConnectCreateLSCL

/**
 * Foot for a post when waiting for Data.
 * @param targetTable Pointer the Local Comm footprint table to add an entry
 * @param timeOutRtnParms_p Pointer the waitForData timeout routine parameters
 * @param statusFlags Flag word containing progress indicators within the timeout routine.
 * @return 0
*/
int createLocalCommFPEntry_WaitForDataTO_Rtn(CF_FootprintTable* inFootprintTable_Ptr,
                                             LocalCommStimerParms_t* timeOutRtnParms_p,
                                             unsigned int statusFlags) {

    CF_FootprintEntry                   localEntry = {{0}};
    CFOOT_entryData_WaitForDataTO_Rtn*  localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "WaitForDataTO_Rtn"                   */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_WaitForDataTO_Rtn;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_WaitForDataTO_Rtn *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    memcpy(localEntryData_Ptr->m_clientHandleToken, timeOutRtnParms_p->workQConnectionHandleToken, sizeof(localEntryData_Ptr->m_clientHandleToken));
    localEntryData_Ptr->m_statusFlags       = statusFlags;
    localEntryData_Ptr->m_startTime         = timeOutRtnParms_p->startTime;

    memset(localEntryData_Ptr->m_reserved1, 0, sizeof(localEntryData_Ptr->m_reserved1));


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_WaitForDataTO_Rtn

/**
 * Foot for cleanup of a client when the server is terminating.
 * @param targetTable Pointer the Local Comm footprint table to add an entry
 * @param currentCCQE_p Pointer the Client cleanup queue element with client info.
 * @param statusFlags Flag word containing progress indicators.
 * @return 0
*/
int createLocalCommFPEntry_CleanupClientServerGone(CF_FootprintTable* inFootprintTable_Ptr,
                                                   LocalCommClientCleanupQueueElement_t* currentCCQE_p,
                                                   unsigned int statusFlags) {

    CF_FootprintEntry                   localEntry = {{0}};
    CFOOT_entryData_CleanupClientServerGone*  localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "CleanupClientServerGone"             */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_CleanupClientServerGone;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_CleanupClientServerGone *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_clientLOCL_p      = currentCCQE_p->inClientBBGZLOCL_p;
    localEntryData_Ptr->m_clientLDAT_p      = currentCCQE_p->inClientBBGZLDAT_p;
    localEntryData_Ptr->m_clientLSCL_p      = currentCCQE_p->inClientBBGZLSCL_p;
    localEntryData_Ptr->m_statusFlags       = statusFlags;

    memset(localEntryData_Ptr->m_reserved1, 0, sizeof(localEntryData_Ptr->m_reserved1));


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_CleanupClientServerGone

/**
 * Foot for client cleanup when the bind is broken with a server.
 * @param targetTable Pointer the Local Comm footprint table to add an entry
 * @param serverToken Stoken of the server that the client has broke the bind with.
 * @param statusFlags Pointer to a Flag word containing progress indicators from the cleanup.
 * @return 0
*/
int createLocalCommFPEntry_CleanupClientClientTerm(CF_FootprintTable* inFootprintTable_Ptr,
                                                   unsigned long long serverToken,
                                                   unsigned long long* statusFlags_p) {

    CF_FootprintEntry                         localEntry = {{0}};
    CFOOT_entryData_CleanupClientClientTerm*  localEntryData_Ptr;

    if (inFootprintTable_Ptr == NULL) {
        return 0;
    }

    /********************************************************************/
    /* Create Footprint Entry for "CleanupClientClientTerm"             */
    /********************************************************************/

    // Entry eyecatcher
    memcpy(localEntry.m_eyecatcher,
           LOCAL_COMM_FOOTPRINT_ENTRY_EYE,
           sizeof(localEntry.m_eyecatcher));

    // Set the entry type
    localEntry.m_entryType = CF_Entry_Type_CleanupClientClientTerm;

    // Timestamp the entry
    __stck(&localEntry.m_entryTimestamp);

    // Get easy addressability to the Entries specific data
    localEntryData_Ptr = (CFOOT_entryData_CleanupClientClientTerm *)
                           &(localEntry.m_entryData);

    /**
     * Fill in type specific information
     */
    localEntryData_Ptr->m_serverStoken          = serverToken;
    localEntryData_Ptr->m_cleanupFootprintInfo  = *statusFlags_p;

    memset(localEntryData_Ptr->m_reserved1, 0, sizeof(localEntryData_Ptr->m_reserved1));


    /**
     * Add Footprint Entry to footprint Table
     */
    int entryIndex = getNewFootprintEntry(inFootprintTable_Ptr);
    updateFootprintEntry(inFootprintTable_Ptr,
                         entryIndex,
                         &localEntry);

    return 0;
} // end, createLocalCommFPEntry_CleanupClientClientTerm

