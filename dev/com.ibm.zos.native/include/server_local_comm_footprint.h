/*
 * server_local_comm_footprint.h
 *
 *  Created on: Sep 17, 2013
 *      Author: ginnick
 */

#ifndef SERVER_LOCAL_COMM_FOOTPRINT_H_
#define SERVER_LOCAL_COMM_FOOTPRINT_H_

#include <limits.h>


#include "common_defines.h"



/**
 * I'm thinking about having 2 footprint table types.  1 scoped at the
 * server-level and 1 per connection.
 *
 * The server-level footprint table would contain entries like lcom_init,
 * activity and perhaps entries during the server-side of a newConnection.
 *
 * The connection-level footprint table would contain the activity over
 * an existing connection...traces for the sends, receives, ...
 *
 * The Close process will most likely generate footprint entries targeted
 * to both footprint tables.
 */

/** Eyecatcher for local comm client data store.                      */
#define LOCAL_COMM_FOOTPRINT_TABLE_EYE "BBGZLFTB"
#define LOCAL_COMM_FOOTPRINT_ENTRY_EYE "CFFP"

#define LOCAL_COMM_FOOTPRINT_DEF_NUMENTRIES 1000

/*--------------------------------------------------------------------*/
/* Channel Framework Footprint Entry                                  */
/*                                                                    */
/*  Each entry must be the same                                       */
/*   +------+------+-----------+---------------------+                */
/*   | EyeC | Type | TimeStamp | Type Dependent data |                */
/*   +------+------+-----------+---------------------+                */
/*--------------------------------------------------------------------*/
typedef
   enum {
    CF_Entry_Type_ClientConnectEntry                     =  1,
    CF_Entry_Type_ClientConnectExit                      =  2,
    CF_Entry_Type_getWQES_Entry                          =  3,
    CF_Entry_Type_getWQES_Exit                           =  4,
    CF_Entry_Type_SendExit                               =  5,
    CF_Entry_Type_ClientPreview                          =  6,
    CF_Entry_Type_Receive                                =  7,
    CF_Entry_Type_CloseEntry                             =  8,
    CF_Entry_Type_CloseExit                              =  9,
    CF_Entry_Type_addToDataQueue                         = 10,
    CF_Entry_Type_issueConnectRequest_AddToWorkQFailed   = 11,
    CF_Entry_Type_ClientConnectFailedSec                 = 12,
    CF_Entry_Type_ClientConnectCreateLSCL                = 13,
    CF_Entry_Type_WaitForDataTO_Rtn                      = 14,
    CF_Entry_Type_CleanupClientServerGone                = 15,
    CF_Entry_Type_CleanupClientClientTerm                = 16,

    EntryTypesMAX = INT_MAX // force to 4-bytes
    } LocalCommEntryTypes;

typedef
  struct CF_FootprintEntryData {
      char m_typeDependentData[32];
  } CF_FootprintEntryData;                                   /* 0x020 */

#pragma pack(1)
typedef struct CF_FootprintEntry {

  // Eyecatcher
  char m_eyecatcher[4];                                      /* 0x000 */

  // Entry Type
  LocalCommEntryTypes m_entryType;                           /* 0x004 */

  // Entry Timestamp
  unsigned long long m_entryTimestamp;                       /* 0x008 */

  // Specific data dependent on entry type
  CF_FootprintEntryData m_entryData;                         /* 0x010 */

} CF_FootprintEntry; // end Struct, CF_FootprintEntry           0x030
#pragma pack(reset)

/*--------------------------------------------------------------------*/
/* "m_entryData" -- entry Type specific data structures               */
/*                                                                    */
/* Note: must not be longer than CF_FootprintEntry.m_entryData        */
/*--------------------------------------------------------------------*/
#pragma pack(1)
typedef struct CFOOT_entryData_ClientConnectEntry {
  union {
    struct {
      // ASID issuing the Connect                            /* 0x000 */
      unsigned short m_invokingASID;

      // reserved
      char m_Reservedz[2];                                   /* 0x002 */

      // Client identifier
      unsigned int m_ClientID;                               /* 0x004 */

      // Server stoken
      long long m_ServerStoken;                              /* 0x008 */

      // Creating TTOKEN
      char m_CreatingTToken[16];                             /* 0x010 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_ClientConnectEntry;                        /* 0x020 */

typedef struct CFOOT_entryData_ClientConnectExit {
  union {
    struct {
      // ASID issuing the Connect                            /* 0x000 */
      unsigned short m_invokingASID;

      // reserved
      char m_reservedz[6];                                   /* 0x002 */

      // Server stoken
      long long m_ServerStoken;                              /* 0x008 */

      // Connection handle pointer
      void * m_bbgzhdl_p;                                    /* 0x010 */

      // Client identifier
      unsigned int m_ClientID;                               /* 0x018 */

      // Connect status flags
      unsigned int m_statusFlags;                            /* 0x01C */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_ClientConnectExit;                         /* 0x020 */

typedef struct CFOOT_entryData_ServerGetWQESExit {
  union {
    struct {
      // First WQE Handle token (LocalCommClientConnectionHandle_t)
      char m_clientConnHandle[16];                           /* 0x000 */

      // First WQE request create time
      unsigned long long m_createStck;                       /* 0x010 */

      // First WQE requestType
      unsigned short m_requestType;                          /* 0x018 */

      char m_reserved1[2];                                   /* 0x01A */

      // WaitOnWork return code
      int  m_waitOnWorkRC;                                   /* 0x01C */
    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_ServerGetWQESExit;                         /* 0x020 */

typedef struct CFOOT_entryData_SendExit {
  union {
    struct {
      // ASID issuing the Send                               /* 0x000 */
      unsigned short m_invokingASID;

      // Client data storage key
      unsigned char m_dataKey;                               /* 0x002 */

      // reserved
      char m_reserved1[1];                                   /* 0x003 */

      // Send return code
      unsigned int m_retCode;                                /* 0x004 */

      // Pointer to client data
      void * m_data_p;                                       /* 0x008 */

      // Client data length
      unsigned long long  m_dataLen;                         /* 0x010 */

      // BBGZLMSG used for this send
      void * m_msgCell_p;                                    /* 0x018 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_SendExit;                                  /* 0x020 */

typedef struct CFOOT_entryData_ClientPreview {
  union {
    struct {
      // ASID issuing the Preview
      unsigned short m_invokingASID;                         /* 0x000 */

      // Wait for data flag
      unsigned char m_waitForData;                           /* 0x002 */

      // reserved
      char m_reserved1[1];                                   /* 0x003 */

      // Preview return code
      unsigned int m_retCode;                                /* 0x004 */

      // Connection Handle Address
      void * m_clientHandle_p;                               /* 0x008 */

      // Previewed data length
      unsigned long long m_dataLen;                          /* 0x010 */

      // Preview time to wait for data
      int m_timeToWait;                                      /* 0x018 */

      char m_reserved2[4];                                   /* 0x01C */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_ClientPreview;                             /* 0x020 */

typedef struct CFOOT_entryData_Receive {
  union {
    struct {
      // ASID issuing the Receive                            /* 0x000 */
      unsigned short m_invokingASID;

      // RC from freeLastReadData call, if made
      unsigned short m_freeLastRC;                           /* 0x002 */

      // Receive return code
      unsigned int m_retCode;                                /* 0x004 */

      // Connection Handle Address
      void * m_clientHandle_p;                               /* 0x008 */

      // LMSG data length
      unsigned long long m_dataAvailLen;                     /* 0x010 */

      // Caller return area length
      unsigned long long m_clientAreaSize;                   /* 0x018 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_Receive;                                   /* 0x020 */

typedef struct CFOOT_entryData_CloseEntry {
  union {
    struct {
      // ASID issuing the Close                              /* 0x000 */
      unsigned short m_invokingASID;

      // reserved
      char m_reserved1[2];                                   /* 0x002 */

      // Close status flags (defined in localCommClose)
      unsigned int m_closeStatusFlags;                       /* 0x004 */

      // Connection Handle (if written to LOCL footprint table) or
      // BBGZLOCL Address (if written to BBGZLHDL footprint table).
      void * m_handleOrLOCL_p;                               /* 0x008 */

      // BBGZLDAT Address
      void * m_bbgzldat_p;                                   /* 0x010 */

      // BBGZLSCL Address
      void * m_bbgzlscl_p;                                   /* 0x018 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_CloseEntry;                                /* 0x020 */

typedef struct CFOOT_entryData_CloseExit {
  union {
    struct {
      // ASID issuing the Close                              /* 0x000 */
      unsigned short m_invokingASID;

      // reserved
      char m_reserved1[2];                                   /* 0x002 */

      // Close status flags (defined in localCommClose)
      unsigned int m_closeStatusFlags;                       /* 0x004 */

      // Connection Handle (if written to LOCL footprint table) or
      // BBGZLOCL Address (if written to BBGZLHDL footprint table).
      void * m_handleOrLOCL_p;                               /* 0x008 */

      // BBGZLDAT Address
      void * m_bbgzldat_p;                                   /* 0x010 */

      // BBGZLSCL Address
      void * m_bbgzlscl_p;                                   /* 0x018 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_CloseExit;                                 /* 0x020 */

typedef struct CFOOT_entryData_addToDataQueue {
  union {
    struct {
      // addToDataQueue status flags (defined in addToDataQueue routine)
      unsigned int m_addToDataQueueStatusFlags;              /* 0x000 */

      // addToDataQueue return code value
      unsigned int m_rc;                                     /* 0x004 */

      // DATA Queue element address                          /* 0x008 */
      void * m_dataQ_p;

      // Handle PLO Compare Swap Area (Seq#, flags, counts)
      char m_handlePLO_CS_area[16];                          /* 0x010 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_addToDataQueue;                            /* 0x020 */

typedef struct CFOOT_entryData_issueConnectRequest_AddToWorkQFailed {
  union {
    struct {
      // Client Handle token
      char m_clientHandleToken[16];                          /* 0x000 */

      // Server stoken
      long long m_serverStoken;                              /* 0x010 */

      // addToWorkQueue/timedWaitOnWorkQueue return code value
      int m_workqRC;                                         /* 0x018 */

      // issueConnectRequest return code
      int m_connectRC;                                       /* 0x01C */
    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_issueConnectRequest_AddToWorkQFailed;      /* 0x020 */

typedef struct CFOOT_entryData_ClientConnectFailedSec {
  union {
    struct {
      // Connection Handle address
      void*  m_handle_p;                                     /* 0x000 */

      // Connect processing status flags
      unsigned int m_statusFlags;                            /* 0x008 */

      // Connect return values
      unsigned int m_returnCode;                             /* 0x00C */
      unsigned int m_returnReason;                           /* 0x010 */
      unsigned int m_safReturnCode;                          /* 0x014 */
      unsigned int m_racfReturnCode;                         /* 0x018 */
      unsigned int m_racfReasonCode;                         /* 0x01C */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_ClientConnectFailedSec;                    /* 0x020 */

typedef struct CFOOT_entryData_ClientConnectCreateLSCL {
  union {
    struct {
      // Server stoken
      long long m_serverStoken;                              /* 0x000 */

      // LOCL PLO CS Area--saved state
      unsigned long long m_out_lsclRequiredState;            /* 0x008 */

      // LOCL PLO CS Area--current state
      unsigned long long m_currentLOCLState;                 /* 0x010 */

      // create LSCL return code
      unsigned int m_returnCode;                             /* 0x018 */

      char m_reserved1[4];                                   /* 0x01C */
    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_ClientConnectCreateLSCL;                   /* 0x020 */

typedef struct CFOOT_entryData_WaitForDataTO_Rtn {
  union {
    struct {
      // Target Client Connection Handle Token
      char m_clientHandleToken[16];                          /* 0x000 */

      // Timeout routine status flags
      unsigned int m_statusFlags;                            /* 0x010 */

      char m_reserved1[4];                                   /* 0x014 */

      // STCK when timer was started.
      unsigned long long m_startTime;                        /* 0x018 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_WaitForDataTO_Rtn;                         /* 0x020 */

typedef struct CFOOT_entryData_CleanupClientServerGone {
  union {
    struct {
      // Client LOCL address
      void* m_clientLOCL_p;                                  /* 0x000 */

      // Client LDAT address
      void* m_clientLDAT_p;                                  /* 0x008 */

      // Client LSCL address
      void* m_clientLSCL_p;                                  /* 0x010 */

      // cleanupClient_ServerGone routine status flags
      unsigned int m_statusFlags;                            /* 0x018 */

      char m_reserved1[4];                                   /* 0x01C */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_CleanupClientServerGone;                   /* 0x020 */

typedef struct CFOOT_entryData_CleanupClientClientTerm {
  union {
    struct {
      // Server stoken of this bind break
      unsigned long long m_serverStoken;                     /* 0x000 */

      // Client LDAT address
      unsigned long long m_cleanupFootprintInfo;             /* 0x008 */


      char m_reserved1[16];                                  /* 0x010 */

    }; // end Struct for specific entry                         0x020

    CF_FootprintEntryData DummyforUnion;                     /* 0x000 */
  }; // end Union with Generic Entry
} CFOOT_entryData_CleanupClientClientTerm;                   /* 0x020 */

#pragma pack(reset)

/**********************************************************************/
/* Channel Framework Footprint Table                                  */
/*                                                                    */
/**********************************************************************/
#pragma pack(1)
typedef struct CF_FootprintTable {

  // Table eyecatcher
  char m_eyecatcher[8];                                      /* 0x000 */

  // Current available entry
  int m_availableEntry;                                      /* 0x008 */

  // Number Footprint entries in table
  int m_totalEntries;                                        /* 0x00C */

  struct {
      // The table has wrapped at least once.
      int m_tableWrapped: 1,
          m_rsvd1       : 31;

  } m_flags;                                                 /* 0x010 */

  char m_rsvd2[12];                                          /* 0x014 */

  /*------------------------------------------------------------------*/
  /* Footprint Entries                                                */
  /* Important Note: This must be the last field definition in this   */
  /* struct.                                                          */
  /*                                                                  */
  /* Note: the following definition includes 1 CF_FootprintEntry in   */
  /* the size of CF_FootPrintTable struct.                            */
  /*------------------------------------------------------------------*/
  CF_FootprintEntry m_footprintEntries[1];                   /* 0x020 */

} CF_FootprintTable; // end Struct, CF_FootprintTable           0x050
#pragma pack(reset)


struct localComLOCL_PLO_CS_Area;
struct localCommClientAnchor;
struct LocalCommClientConnectionHandle;
struct localCommConnectionHandle;
struct localCommSharedMemoryInfo;
struct LocalCommWorkQueueElement;
struct LocalCommStimerParms;


// Initialize the FootPrintTable
CF_FootprintTable* createLocalCommFootprintTable(LocalCommSharedMemoryInfo_t* info_p);
void createLocalCommConnectionFootprintTable(LocalCommConnectionHandle_t* bbgzlhdl_p);

int createLocalCommFPEntry_ClientConnectEntry(CF_FootprintTable* targetTable,
                                              SToken* serverStoken_p,
                                              unsigned int clientID);

int createLocalCommFPEntry_ClientConnectExit(CF_FootprintTable* targetTable,
                                             SToken* serverStoken_p,
                                             unsigned int clientID,
                                             void* clientHandle_p,
                                             unsigned int statusFlags);

int createLocalCommFPEntry_SendExit(CF_FootprintTable* targetTable,
                                void* data_p,
                                unsigned long long dataLen,
                                unsigned char dataKey,
                                void* msgCell_p,
                                unsigned int retCode);

int createLocalCommFPEntry_ClientPreview(CF_FootprintTable* targetTable,
                                         void* clientHandle_p,
                                         unsigned char waitForData,
                                         int timeToWait,
                                         unsigned long long* dataLen_p,
                                         int previewRC);

int createLocalCommFPEntry_Receive(CF_FootprintTable* targetTable,
                                   void* clientHandle_p,
                                   unsigned long long returnDataSize,
                                   unsigned long long clientAreaSize,
                                   int receiveRC,
                                   unsigned short freeLastRC);

int createLocalCommFPEntry_CloseEntry(CF_FootprintTable* targetTable,
                                      void* handleOrLOCL_p,
                                      unsigned int statusFlags,
                                      void* bbgzldat_p,
                                      void* bbgzlscl_p);

int createLocalCommFPEntry_CloseExit(CF_FootprintTable* targetTable,
                                     void* connectionHandle_p,
                                     unsigned int statusFlags,
                                     void* bbgzldat_p,
                                     void* bbgzlscl_p);

int createLocalCommFPEntry_ServerConnectWQE(CF_FootprintTable* targetTable);  //TODO:

int createLocalCommFPEntry_Server_getWQES_Entry(CF_FootprintTable* targetTable);
int createLocalCommFPEntry_Server_getWQES_Exit(CF_FootprintTable* targetTable, LocalCommWorkQueueElement * newWRQE_p, int waitOnWorkRC);

int createLocalCommFPEntry_addToDataQueue(CF_FootprintTable* targetTable,
                                          void *dataQE_p,
                                          void *handlePLO_CS_p,
                                          unsigned int statusFlags,
                                          unsigned int rc);


int createLocalCommFPEntry_issueConnectRequest_AddToWorkQFailed(CF_FootprintTable* targetTable,
                                                                LocalCommClientConnectionHandle_t* clientConnHandle_p,
                                                                SToken* serverStoken_p,
                                                                int workqRC,
                                                                int connectRC);

int createLocalCommFPEntry_ClientConnectFailedSec(CF_FootprintTable* targetTable,
                                                  void* connectionHandle_p,
                                                  unsigned int statusFlags,
                                                  int   returnCode,
                                                  int   returnReason,
                                                  int   safReturnCode,
                                                  int   racfReturnCode,
                                                  int   racfReasonCode);

int createLocalCommFPEntry_ClientConnectCreateLSCL(CF_FootprintTable* targetTable,
                                                   LocalCommClientAnchor_t* clientAnchor_p,
                                                   SToken* serverStoken_p,
                                                   LocalComLOCL_PLO_CS_Area_t* out_lsclRequiredState_p,
                                                   int createLSCL_RC);

int createLocalCommFPEntry_WaitForDataTO_Rtn(CF_FootprintTable* targetTable,
                                             struct LocalCommStimerParms* timeOutRtnParms_p,
                                             unsigned int statusFlags);

int createLocalCommFPEntry_CleanupClientServerGone(CF_FootprintTable* targetTable,
                                                   LocalCommClientCleanupQueueElement_t* currentCCQE_p,
                                                   unsigned int statusFlags);

int createLocalCommFPEntry_CleanupClientClientTerm(CF_FootprintTable* targetTable,
                                                   unsigned long long serverToken,
                                                   unsigned long long* retryFootprints);


#endif /* SERVER_LOCAL_COMM_FOOTPRINT_H_ */
