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

/**
 * @file
 *
 * Assorted routines that interface with z/OS Local Communication Services
 * which require an authorized caller.
 *
 */

#include <metal.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#include "include/gen/bpxzotcb.h"
#include "include/gen/ihapsa.h"
#include "include/gen/ihastcb.h"
#include "include/gen/ikjtcb.h"

#include "include/bpx_ipt.h"
#include "include/common_defines.h"
#include "include/common_mc_defines.h"
#include "include/ieantc.h"
#include "include/mvs_enq.h"
#include "include/mvs_estae.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"
#include "include/server_kernel_common.h"
#include "include/server_lcom_services.h"
#include "include/server_local_comm_api.h"
#include "include/server_local_comm_cleanup.h"
#include "include/server_local_comm_client.h"
#include "include/server_local_comm_data_store.h"
#include "include/server_local_comm_footprint.h"
#include "include/server_local_comm_global_lock.h"
#include "include/server_local_comm_queue.h"
#include "include/server_local_comm_stimer.h"
#include "include/server_process_data.h"
#include "include/ras_tracing.h"


#define RAS_MODULE_CONST  RAS_MODULE_SERVER_LCOM_SERVICES
#define TP_SERVER_LCOM_SERVICES_INIT_ENTRY          1
#define TP_SERVER_LCOM_SERVICES_INIT_ENQ            2
#define TP_SERVER_LCOM_SERVICES_INIT_EXIT           3
#define TP_SERVER_LCOM_SERVICES_UNINIT_ENTRY        4
#define TP_SERVER_LCOM_SERVICES_UNINIT_EXIT         5
#define TP_SERVER_LCOM_SERVICES_GETWRQ_ENTRY        8
#define TP_SERVER_LCOM_SERVICES_GETWRQ_SLD          9
#define TP_SERVER_LCOM_SERVICES_GETWRQ_EXIT        10
#define TP_SERVER_LCOM_SERVICES_FREEWRQES_ENTRY    11
#define TP_SERVER_LCOM_SERVICES_FREEWRQES_SLD      12
#define TP_SERVER_LCOM_SERVICES_FREEWRQES_EXIT     13
#define TP_SERVER_LCOM_SERVICES_STOPLIST_ENTRY     14
#define TP_SERVER_LCOM_SERVICES_STOPLIST_SLD       15
#define TP_SERVER_LCOM_SERVICES_STOPLIST_EXIT      16
#define TP_SERVER_LCOM_SERVICES_CONNRESP_ENTRY     17
#define TP_SERVER_LCOM_SERVICES_CONNRESP_EXIT      18
#define TP_SERVER_LCOM_SERVICES_CCLNTSHARED_ENTRY  19
#define TP_SERVER_LCOM_SERVICES_CCLNTSHARED_EXIT   20
#define TP_SERVER_LCOM_SERVICES_READ_ENTRY         21
#define TP_SERVER_LCOM_SERVICES_READ_EXIT          22
#define TP_SERVER_LCOM_SERVICES_RELLMSG_ENTRY      23
#define TP_SERVER_LCOM_SERVICES_RELLMSG_EXIT       24
#define TP_SERVER_LCOM_SERVICES_WRITE_ENTRY        25
#define TP_SERVER_LCOM_SERVICES_WRITE_EXIT         26
#define TP_SERVER_LCOM_SERVICES_CLOSE_ENTRY        27
#define TP_SERVER_LCOM_SERVICES_CLOSE_EXIT         28
#define TP_SERVER_LCOM_SERVICES_DISCCLNTSHARED_ENTRY 29
#define TP_SERVER_LCOM_SERVICES_DISCCLNTSHARED_EXIT  30
#define TP_SERVER_LCOM_SERVICES_CCLNTSHARED_ABENDED1 31
#define TP_SERVER_LCOM_SERVICES_CCLNTSHARED_ABENDED2 32
#define TP_SERVER_LCOM_SERVICES_CCLNTSHARED_NOESTAE  33
#define TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_ENTRY      34
#define TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_SLD        35
#define TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_FLAGS_PRE  36
#define TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_FLAGS_POST 37
#define TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_EXIT       38




static const int RSN_BAD_PARMS         =  8;
static const int RSN_NO_SPD            = 12;
static const int RSN_NO_IPT_TTOKEN     = 16;
static const int RSN_NO_BBGZLOCL       = 20;
static const int RSN_QUEUING_ERROR     = 24;
static const int RSN_NO_QUEUE_ELEMENT  = 28;

static const OpaqueClientConnectionHandle_t NULL_CONN_HANDLE = {
    .handle = {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    }
};

// --------------------------------------------------------------------
// Forward declares
// --------------------------------------------------------------------
void registerLCOMHardFailureRtn(void);
void deregisterLCOMHardFailureRtn(void);


/**
 * Drive Local Communication initialization
 *
 * @param parms pointer to <code>LCOM_InitParms</code>
 *
 * @return void
*/
void lcom_init(LCOM_InitParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_INIT_ENTRY),
                    "lcom_init, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Initialize a bunch of Stuff to allow clients to find us and to
            // be able to start taking requests (connects, receives, ...)
            //
            // 1) Initialize server-side BBGZLOCL structure
            // 2) Initialize the server Async Completion Queue (aka "Black Queue")
            // 3) Expose this server to Local Comm clients (get an MVS Enq that
            //    LCOM Clients expect to use to find this server)
            // ----------------------------------------------------------------

            // ----------------------------------------------------------------
            // 1) Initialize LOCL
            // ----------------------------------------------------------------

            // Get PGOO
            server_process_data* spd_p = getServerProcessData();
            if (spd_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_SPD;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            // Cause the initialization of the BBGZLOCL (and related pools)
            LocalCommClientAnchor_t* bbgzlocl_p = createServerLocalCommClientAnchor();


            // ----------------------------------------------------------------
            // 3) Expose this server to Local Comm clients (get an MVS Enq that
            //    Local Comm Clients expect to use to find this server)
            // ----------------------------------------------------------------

            // ----------------------------------------------------------------
            // Grab an ENQ whose name indicates that this server is "ready" for
            // Local Comm work.
            // ----------------------------------------------------------------
            // TODO:  -validate parms

            // TODO: Convert this ENQ to a name token.
            enqtoken local_enq_token;
            char enq_rname_buf[BBGZ_ENQ_MAX_RNAME_LEN];

            memset(enq_rname_buf, 0, sizeof(enq_rname_buf));
            snprintf(enq_rname_buf,
                     sizeof(enq_rname_buf),
                     SERVER_LCOM_READY_ENQ_RNAME_PATTERN,
                     bbgzlocl_p);

            // Get IPT TTOKEN
            // TODO: Cache it away somewhere???
            TToken ipt_ttoken;
            if (getIPT_TToken(&ipt_ttoken) == 1) {
                localRC  = -1;
                localRSN = RSN_NO_IPT_TTOKEN;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }


            get_enq_exclusive_system(
                BBGZ_ENQ_QNAME,
                enq_rname_buf,
                (char *)&ipt_ttoken,
                &(local_enq_token));

            // ----------------------------------------------------------------
            // TODO: save ENQ Token into BBGZLOCL ... later freed in uninit.
            // ----------------------------------------------------------------
            //ServerLComData* sld_p = (ServerLComData*) spd_p->lcom_BBGZLCOM_p;
            memcpy(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqToken,
                   &(local_enq_token),
                   sizeof(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqToken));
            memcpy(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqTokenOwningTToken,
                   &ipt_ttoken,
                   sizeof(bbgzlocl_p->serverSpecificInfo.serverIdentityEnqTokenOwningTToken));

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_INIT_ENQ),
                            "lcom_init, obtained LCOM ENQ",
                            TRACE_DATA_RAWDATA(BBGZ_ENQ_MAX_RNAME_LEN,
                                               enq_rname_buf,
                                               "local_enq name"),
                            TRACE_DATA_RAWDATA(sizeof(local_enq_token),
                                               &local_enq_token,
                                               "local_enq_token"),
                            TRACE_DATA_END_PARMS);
            }

            // "Register" an LCOM hard failure cleanup routine.
            registerLCOMHardFailureRtn();

            localRC  = 0;
            localRSN = 0;

        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_INIT_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_INIT_RC_NOESTAE;
    }

    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_INIT_EXIT),
                    "lcom_init, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_init

/**
 * Drive Local Communication uninitialization
 *
 * @param parms pointer to <code>LCOM_UninitParms</code>
 *
 * @return void
*/
void lcom_uninit(LCOM_UninitParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_UNINIT_ENTRY),
                    "lcom_uninit, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // "DeRegister" the LCOM server Hard failure cleanup routine.
    deregisterLCOMHardFailureRtn();

    // Cleanup the server LOCL resources.
    localRC = cleanupServerLOCL(&localRSN);

    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);


    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_UNINIT_EXIT),
                    "lcom_uninit, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_uninit



/**
 * Grab the current list of WRQEs that on the Work Request queue (WRQ)
 * and return a pointer to the list to the caller.  Park this pointer
 * in the BBGZLOCL area to later use it to free the elements (actually
 * on this call will free the previously returned list prior to
 * retrieving a new list)
 *
 * @param parms pointer to <code>LCOM_GetWRQParms</code>
 *
 * @return A pointer to a list of WRQEs to process
*/
void lcom_getWRQ(LCOM_GetWRQParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_GETWRQ_ENTRY),
                    "lcom_getWRQ, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;
    LocalCommWorkQueueElement* newWRQE_p = NULL;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((parms->outWRQE_Ptr == 0)             ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // 1) Free the prior list of WRQEs
            // 2) Get the current list &
            //    Anchor current list into BBGZLCOM area
            // ----------------------------------------------------------------

            // Get PGOO
            server_process_data* spd_p = getServerProcessData();
            if (spd_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_SPD;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            // Get BBGZLOCL
            LocalCommClientAnchor_t* bbgzlocl_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;

            if (bbgzlocl_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_BBGZLOCL;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_GETWRQ_SLD),
                            "lcom_getWRQ, BBGZLOCL before changes",
                            TRACE_DATA_RAWDATA(sizeof(LocalCommWorkQueue),
                                               &(bbgzlocl_p->serverSpecificInfo.serverAsClientWorkQ),
                                               "Server LocalCommWorkQueue"),
                            TRACE_DATA_END_PARMS);
            }


            createLocalCommFPEntry_Server_getWQES_Entry(bbgzlocl_p->footprintTable);

            LocalCommWorkQueue * localWorkQueuePtr = &(bbgzlocl_p->serverSpecificInfo.serverAsClientWorkQ);

            // 1) Free the prior list of WRQEs
            releaseReturnedWRQEs(localWorkQueuePtr);

            // 2) Get a new list of WRQEs to process.  No time limit.
            localRSN = waitOnWork(localWorkQueuePtr, &newWRQE_p, 0, (void*) NULL, (LocalCommStimerParms_t*) NULL, LCOM_WRQ_WAITONWORK_MULTREQUEST, parms->otherWorkToDo);

            createLocalCommFPEntry_Server_getWQES_Exit(bbgzlocl_p->footprintTable, newWRQE_p, localRSN);

            localRC  = 0;
            if (localRSN != 0) {
                // In this case, not calling pause because we asked not to is not an error.
                if (localRSN == LCOM_WRQ_WAITONWORK_RC_SKIP_PAUSE) {
                    localRSN = 0;
                } else {
                    localRC = -1;
                }
            }
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_GETWRQ_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_GETWRQ_RC_NOESTAE;
    }

    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    memcpy_dk(parms->outWRQE_Ptr,
              &newWRQE_p,
              sizeof(newWRQE_p),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_GETWRQ_EXIT),
                    "lcom_getWRQ, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_getWRQ

/**
 * Free the list of WRQEs that were previously returned from the
 * Work Request queue (WRQ).
 *
 * @param parms pointer to <code>LCOM_FreeWRQEsParms</code>
 *
*/
void lcom_freeWRQEs(LCOM_FreeWRQEsParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_FREEWRQES_ENTRY),
                    "lcom_freeWRQEs, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Free the prior list of WRQEs anchored in BBGZLCOM
            // ----------------------------------------------------------------

            // Get PGOO
            server_process_data* spd_p = getServerProcessData();
            if (spd_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_SPD;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            // Get BBGZLOCL from PGOO
            LocalCommClientAnchor_t* bbgzlocl_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;

            if (bbgzlocl_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_BBGZLOCL;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_FREEWRQES_SLD),
                            "lcom_freeWRQEs, BBGZLOCL before changes",
                            TRACE_DATA_RAWDATA(sizeof(*bbgzlocl_p),
                                               bbgzlocl_p,
                                               "BBGZLOCL"),
                            TRACE_DATA_END_PARMS);
            }

            LocalCommWorkQueue * localWorkQueuePtr = &(bbgzlocl_p->serverSpecificInfo.serverAsClientWorkQ);

            // 1) Free the prior list of WRQEs
            releaseReturnedWRQEs(localWorkQueuePtr);

            localRC  = 0;
            localRSN = 0;
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_FREEWRQES_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_FREEWRQES_RC_NOESTAE;
    }

    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_FREEWRQES_EXIT),
                    "lcom_freeWRQEs, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_freeWRQEs

/**
 * Wake up the Local Comm listener thread with an indication to
 * stop listening for work.
 *
 * @param parms pointer to <code>LCOM_StopListeningOnWRQParms</code>
 *
*/
void lcom_stopListeningOnWRQ(LCOM_StopListeningOnWRQParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_STOPLIST_ENTRY),
                    "lcom_stopListeningOnWRQ, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Wake up LCOM Listener thread
            // ----------------------------------------------------------------

            // Get PGOO
            server_process_data* spd_p = getServerProcessData();
            if (spd_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_SPD;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            // Get BBGZLOCL from PGOO
            LocalCommClientAnchor_t* bbgzlocl_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;

            if (bbgzlocl_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_BBGZLOCL;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_STOPLIST_SLD),
                            "lcom_stopListeningOnWRQ, BBGZLOCL before changes",
                            TRACE_DATA_RAWDATA(sizeof(*bbgzlocl_p),
                                               bbgzlocl_p,
                                               "BBGZLOCL"),
                            TRACE_DATA_END_PARMS);
            }

            // 1) Free the prior list of WRQEs
            LocalCommWorkQueue * localWorkQueuePtr = &(bbgzlocl_p->serverSpecificInfo.serverAsClientWorkQ);
            releaseReturnedWRQEs(localWorkQueuePtr);

            // Release Listener thread with stop release code.
            initiateClosingServerQueue(localWorkQueuePtr);

            localRC  = 0;
            localRSN = 0;

        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_STOPLIST_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_STOPLIST_RC_NOESTAE;
    }

    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_STOPLIST_EXIT),
                    "lcom_stopListeningOnWRQ, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_stopListeningOnWRQ

/**
 * Build a connect response Work queue element and queue it to the
 * client's inbound work queue
 *
 * @param parms pointer to <code>LCOM_ConnectResponseParms</code>
 *
*/
void lcom_connectResponse(LCOM_ConnectResponseParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_CONNRESP_ENTRY),
                    "LCOM_ConnectResponseParms, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((memcmp(&NULL_CONN_HANDLE, &(parms->inConnHandle), sizeof(NULL_CONN_HANDLE)) == 0) ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Passing thru client handle to buildAndQueue, so he must validate
            // ----------------------------------------------------------------
            LocalCommClientConnectionHandle_t* clientConnHandle_p = (LocalCommClientConnectionHandle_t*)(&(parms->inConnHandle));
            int queueRC = buildAndQueueConnectResponse(clientConnHandle_p, REQUESTTYPE_CONNECTRESPONSE_OK);

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_CONNRESP_EXIT),
                            "lcom_connectResponse, buildAndQueueConnectResponse return",
                            TRACE_DATA_HEX_INT(queueRC, "queueRC"),
                            TRACE_DATA_END_PARMS);
            }

            if (queueRC == LOCAL_COMM_ADD_QUEUE_OK) {
                localRC  = 0;
                localRSN = 0;
            }
            else if (queueRC == LOCAL_COMM_ADD_QUEUE_NO_CELL) {
                localRSN = RSN_NO_QUEUE_ELEMENT;
            } else {
                localRSN = RSN_QUEUING_ERROR;
            }
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_CONNRESP_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_CONNRESP_RC_NOESTAE;
    }

    // Tell the caller what happened
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_CONNRESP_EXIT),
                    "lcom_connectResponse, exit",
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN,"localRSN"),
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_connectResponse

/**
 * Connect to a local comm client's shared memory (bbgzlhdl / bbgzlocl, bbgzldat, ...)
 *
 * @param parms pointer to <code>LCOM_ConnectClientSharedMemoryParms</code>
 *
*/
void lcom_connectClientSharedMemory(LCOM_ConnectClientSharedMemoryParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_CCLNTSHARED_ENTRY),
                    "lcom_connectClientSharedMemory, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((memcmp(&NULL_CONN_HANDLE, &(parms->inConnHandle), sizeof(NULL_CONN_HANDLE)) == 0) ||
        (parms->inBBGZLDAT_p == 0)            ||
        (parms->inBBGZLOCL_p == 0)            ||
        (parms->inSharingUserToken == 0)      ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;

    volatile struct {
                 int tryToSetESTAE : 1,
                     setESTAE : 1,
                     abendedAndRetried: 1,
                     haltRecovery : 1,

                     tryToAttachToClientMem: 1,
                     attachedToClientMem: 1,
                     failedMemValidation: 1,
                     detachedFromClientMem : 1,

                     attemptRegisterClientCleanup: 1,

                     _available : 23;
    } retryFootprints;
    memset((void*)&retryFootprints, 0, sizeof(retryFootprints));

    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));

    SET_RETRY_POINT(retryParms);
    if (retryFootprints.tryToSetESTAE == 0) {
        retryFootprints.tryToSetESTAE = 1;
        establish_estaex_with_retry(&retryParms, &estaex_rc, &estaex_rsn);
        retryFootprints.setESTAE = (estaex_rc == 0);
    }

    // Extract info from input parms.
    LocalCommClientServerPair_t* inTargetLSCL_p     = (LocalCommClientServerPair_t*) (parms->inSharingUserToken);
    LocalCommClientAnchor_t*     inTargetBBGZLOCL_p = (LocalCommClientAnchor_t *)(parms->inBBGZLOCL_p);
    LocalCommClientDataStore_t*  inTargetBBGZLDAT_p = (LocalCommClientDataStore_t*) (parms->inBBGZLDAT_p);

    if (retryFootprints.setESTAE == 1) {
        SET_RETRY_POINT(retryParms);
        if (retryFootprints.tryToAttachToClientMem == 0) {
            retryFootprints.tryToAttachToClientMem = 1;

            // Get Server's LOCL from PGOO
            LocalCommClientAnchor_t* serverLOCL_p = NULL;
            server_process_data* spd_p = getServerProcessData();
            if (spd_p != NULL) {
                serverLOCL_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;
            }

            if (serverLOCL_p != NULL) {
                // -----------------------------------------------------------------------
                // Try to attach to the client's shared memory objects.
                // -----------------------------------------------------------------------

                // Attach to client shared memory objects (LOCL and LDAT)
                accessSharedAbove(inTargetBBGZLOCL_p, parms->inSharingUserToken);
                accessSharedAbove(inTargetBBGZLDAT_p, parms->inSharingUserToken);

                retryFootprints.attachedToClientMem = 1;

                // Validate Eyecatchers of passed areas: LSCL, LOCL, and LDAT.
                if (memcmp(inTargetLSCL_p->eyecatcher, BBGZLSCL_EYE, sizeof(inTargetLSCL_p->eyecatcher)) != 0) {
                    localRC = -1;
                    localRSN = LOCAL_COMM_CONSHAREDMEM_RC_LSCL_BADEYE;
                } else if (memcmp(inTargetBBGZLOCL_p->eyecatcher, BBGZLOCL_EYE, sizeof(inTargetBBGZLOCL_p->eyecatcher)) != 0) {
                    localRC = -1;
                    localRSN = LOCAL_COMM_CONSHAREDMEM_RC_LOCL_BADEYE;
                } else if (memcmp(inTargetBBGZLDAT_p->eyecatcher, BBGZLDAT_EYE, sizeof(inTargetBBGZLDAT_p->eyecatcher)) != 0) {
                    localRC = -1;
                    localRSN = LOCAL_COMM_CONSHAREDMEM_RC_LDAT_BADEYE;
                } else {
                    localRC  = 0;
                    localRSN = 0;
                }

                // If the validation of the areas are not what we expected, then detach from them.
                if (localRC == -1) {
                    int detachRC = 0, detachRSN = 0;
                    retryFootprints.attachedToClientMem = 0;
                    retryFootprints.failedMemValidation = 1;
                    detachRC = detachSharedAboveConditional(inTargetBBGZLOCL_p, parms->inSharingUserToken, FALSE, &detachRSN);
                    detachRC = detachSharedAboveConditional(inTargetBBGZLDAT_p, parms->inSharingUserToken, FALSE, &detachRSN);
                }

                // -----------------------------------------------------------------------
                // Register this client for cleanup if server ends abnormally.
                // -----------------------------------------------------------------------
                if (localRC == 0) {
                    SET_RETRY_POINT(retryParms);
                    if (retryFootprints.attemptRegisterClientCleanup == 0) {
                        retryFootprints.attemptRegisterClientCleanup == 1;
                        localRSN = registerClientCleanup(serverLOCL_p, inTargetBBGZLOCL_p, inTargetBBGZLDAT_p, inTargetLSCL_p);
                    } else {
                        if (TraceActive(trc_level_exception)) {
                            TraceRecord(trc_level_exception,
                                        TP(TP_SERVER_LCOM_SERVICES_CCLNTSHARED_ABENDED1),
                                        "lcom_connectClientSharedMemory, abended twice",
                                        TRACE_DATA_HEX_INT(*((int*)&retryFootprints), "retryFootprints"),
                                        TRACE_DATA_HEX_INT(localRSN, "registerClientCleanup rc"),
                                        TRACE_DATA_PTR(serverLOCL_p, "serverLOCL_p"),
                                        TRACE_DATA_PTR(inTargetBBGZLOCL_p, "inTargetBBGZLOCL_p"),
                                        TRACE_DATA_PTR(inTargetBBGZLDAT_p, "inTargetBBGZLDAT_p"),
                                        TRACE_DATA_LONG(parms->inSharingUserToken, "parms->inSharingUserToken(LSCL)"),
                                        TRACE_DATA_END_PARMS);
                        }
                    }
                }   // end if, attached and validated
            }  // end if, have server's LOCL
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_CONSHAREDMEM_RC_ABENDED;
        }

        // Attempt to detach from Client Shared Memory if we abended after obtaining access.
        if ((localRSN == LOCAL_COMM_CONSHAREDMEM_RC_ABENDED) && (retryFootprints.attachedToClientMem == 1) ) {
            SET_RETRY_POINT(retryParms);

            int detachRC = 0, detachRSN = 0;
            if (retryFootprints.detachedFromClientMem == 0) {
                retryFootprints.detachedFromClientMem == 1;
                detachRC = detachSharedAboveConditional(inTargetBBGZLOCL_p, parms->inSharingUserToken, FALSE, &detachRSN);
                detachRC = detachSharedAboveConditional(inTargetBBGZLDAT_p, parms->inSharingUserToken, FALSE, &detachRSN);
            } else {
                // Abended again.
                if (TraceActive(trc_level_exception)) {
                    TraceRecord(trc_level_exception,
                                TP(TP_SERVER_LCOM_SERVICES_CCLNTSHARED_ABENDED2),
                                "lcom_connectClientSharedMemory, abended twice",
                                TRACE_DATA_HEX_INT(*((int*) &retryFootprints), "retryFootprints"),
                                TRACE_DATA_HEX_INT(detachRC, "detachRC"),
                                TRACE_DATA_HEX_INT(detachRSN, "detachRSN"),
                                TRACE_DATA_PTR(inTargetBBGZLOCL_p, "inTargetBBGZLOCL_p"),
                                TRACE_DATA_PTR(inTargetBBGZLDAT_p, "inTargetBBGZLDAT_p"),
                                TRACE_DATA_LONG(parms->inSharingUserToken, "parms->inSharingUserToken(LSCL)"),
                                TRACE_DATA_END_PARMS);
                }
            }
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_CONSHAREDMEM_RC_NOESTAE;

        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(TP_SERVER_LCOM_SERVICES_CCLNTSHARED_NOESTAE),
                        "lcom_connectClientSharedMemory, failed to establish ESTAE",
                        TRACE_DATA_HEX_INT(*((int*) &retryFootprints), "retryFootprints"),
                        TRACE_DATA_END_PARMS);
        }
    }

    // Tell the caller what happened
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_CCLNTSHARED_EXIT),
                    "lcom_connectClientSharedMemory, exit",
                    TRACE_DATA_HEX_INT(*((int*) &retryFootprints), "retryFootprints"),
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_connectClientSharedMemory

/**
 * Disconnect from a local comm client's shared memory (bbgzlhdl / bbgzlocl, bbgzldat, ...)
 *
 * @param parms pointer to <code>LCOM_DisconnectClientSharedMemoryParms</code>
 *
*/
void lcom_disconnectClientSharedMemory(LCOM_DisconnectClientSharedMemoryParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_DISCCLNTSHARED_ENTRY),
                    "lcom_disconnectClientSharedMemory, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((memcmp(&NULL_CONN_HANDLE, &(parms->inConnHandle), sizeof(NULL_CONN_HANDLE)) == 0) ||
        (parms->inBBGZLDAT_p == 0)            ||
        (parms->inBBGZLOCL_p == 0)            ||
        (parms->inSharingUserToken == 0)      ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc  = -1;
    int estaex_rsn = -1;
    volatile struct {
                 int tryToSetESTAE : 1,
                     setESTAE : 1,
                     tryToVerifyEyecatchers: 1,
                     verifiedEyecatchers: 1,

                     tryToRemoveClientCleanup: 1,
                     removedClientCleanup: 1,
                     tryToCleanupLSCL : 1,
                     cleanedUpLSCL : 1,

                     tryToDetachSharedMem : 1,
                     detachedSharedMem : 1,
                     _available : 22;
    } retryFootprints;
    memset((void*)&retryFootprints, 0, sizeof(retryFootprints));
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));

    SET_RETRY_POINT(retryParms);
    if (retryFootprints.tryToSetESTAE == 0) {
        retryFootprints.tryToSetESTAE = 1;
        establish_estaex_with_retry(&retryParms, &estaex_rc, &estaex_rsn);
        retryFootprints.setESTAE = (estaex_rc == 0);
    }

    if (retryFootprints.setESTAE == 1) {
        SET_RETRY_POINT(retryParms);
        if (retryFootprints.tryToVerifyEyecatchers == 0) {
            retryFootprints.tryToVerifyEyecatchers = 1;

            // Get Server's LOCL from PGOO
            LocalCommClientAnchor_t* serverLOCL_p = NULL;
            server_process_data* spd_p = getServerProcessData();
            if (spd_p != NULL) {
                serverLOCL_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;
            }

            if (serverLOCL_p != NULL) {
                // ----------------------------------------------------------------
                // Can't Validate connection handle prior to detaching from the Client's
                // shared memory objects because close processing most likely put it
                // back in the pool (invalidating the instance count).
                // ----------------------------------------------------------------
                LocalCommClientServerPair_t* inTargetLSCL_p     = (LocalCommClientServerPair_t*) (parms->inSharingUserToken);
                LocalCommClientAnchor_t*     inTargetBBGZLOCL_p = (LocalCommClientAnchor_t *)(parms->inBBGZLOCL_p);
                LocalCommClientDataStore_t*  inTargetBBGZLDAT_p = (LocalCommClientDataStore_t*) (parms->inBBGZLDAT_p);

                // Validate Eyecatchers of passed areas: LSCL, LOCL, and LDAT.  The eyecatchers may have been flipped prior to the
                // server driving this disconnect routine (close processing).
                localRC = -1;
                if ((memcmp(inTargetLSCL_p->eyecatcher, BBGZLSCL_EYE, sizeof(inTargetLSCL_p->eyecatcher)) != 0) &&
                    (memcmp(inTargetLSCL_p->eyecatcher, RELEASED_BBGZLSCL_EYE, sizeof(inTargetLSCL_p->eyecatcher)) != 0)) {
                    localRSN = LOCAL_COMM_DISSHAREDMEM_RC_LSCL_BADEYE;
                } else if ((memcmp(inTargetBBGZLOCL_p->eyecatcher, BBGZLOCL_EYE, sizeof(inTargetBBGZLOCL_p->eyecatcher)) != 0) &&
                    (memcmp(inTargetBBGZLOCL_p->eyecatcher, RELEASED_BBGZLOCL_EYE, sizeof(inTargetBBGZLOCL_p->eyecatcher)) != 0)){
                    localRSN = LOCAL_COMM_DISSHAREDMEM_RC_LOCL_BADEYE;
                } else if ((memcmp(inTargetBBGZLDAT_p->eyecatcher, BBGZLDAT_EYE, sizeof(inTargetBBGZLDAT_p->eyecatcher)) != 0) &&
                    (memcmp(inTargetBBGZLDAT_p->eyecatcher, RELEASED_BBGZLDAT_EYE, sizeof(inTargetBBGZLDAT_p->eyecatcher)) != 0)){
                    localRSN = LOCAL_COMM_DISSHAREDMEM_RC_LDAT_BADEYE;
                } else {
                    retryFootprints.verifiedEyecatchers = 1;

                    SET_RETRY_POINT(retryParms);
                    if (retryFootprints.tryToRemoveClientCleanup == 0) {
                        retryFootprints.tryToRemoveClientCleanup = 1;

                        // Remove the client cleanup element for this client.
                        int dregRC = deregisterClientCleanup(serverLOCL_p,
                                                             inTargetBBGZLOCL_p,
                                                             inTargetBBGZLDAT_p,
                                                             inTargetLSCL_p);

                        retryFootprints.removedClientCleanup = 1;
                    } else {
                        // Abended and retried, from deregistering client cleanup
                        localRC = -1;
                        localRSN = LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_DCC;
                    }

                    SET_RETRY_POINT(retryParms);
                    if (retryFootprints.tryToCleanupLSCL == 0) {
                        retryFootprints.tryToCleanupLSCL = 1;

                        // Release the BBGZLSCL
                        cleanupLSCL(inTargetBBGZLOCL_p,
                                    inTargetLSCL_p);
                        retryFootprints.cleanedUpLSCL = 1;
                    } else {
                        // Abended and retried, from cleaning up LSCL
                        localRC = -1;
                        localRSN = LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_CL;
                    }

                    SET_RETRY_POINT(retryParms);
                    if (retryFootprints.tryToDetachSharedMem == 0) {
                        retryFootprints.tryToDetachSharedMem = 1;
                        // Detach from the LOCL and LDAT
                        int detachRC = detachFromLDATandLOCL(inTargetBBGZLOCL_p,
                                                             inTargetBBGZLDAT_p,
                                                             parms->inSharingUserToken);
                        retryFootprints.detachedSharedMem = 1;

                        if (localRSN == 0) {
                            // set rc/rsn based on detach result
                            localRC = 0;
                            if (detachRC < 0 ) {
                                localRC = -1;
                                localRSN = detachRC;
                            } else {
                                localRSN = 0;
                            }
                        }
                    } else {
                        // Abended and retried, detaching from shared mem
                        localRC = -1;
                        localRSN = LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_DS;
                    }
                }
            }   // end if, found server LOCL
        } else {
            // Abended and retried, verifying eyecatchers
            localRC = -1;
            localRSN = LOCAL_COMM_DISCCLNTSHARED_RC_ABENDED_VE;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_DISCCLNTSHARED_RC_NOESTAE;
    }

    // Tell the caller what happened
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_DISCCLNTSHARED_EXIT),
                    "lcom_disconnectClientSharedMemory, exit",
                    TRACE_DATA_HEX_INT(*((int*) &retryFootprints), "retryFootprints"),
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_disconnectClientSharedMemory


/**
 * Read data from the Inbound data queue
 *
 * @param parms pointer to <code>LCOM_ReadParms</code>
 *
 */
void lcom_read(LCOM_ReadParms *parms)
{
    int localRC, localRSN;

    LCOM_AvailableDataVector* dataVector_p = NULL;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_READ_ENTRY),
                    "lcom_read, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((memcmp(&NULL_CONN_HANDLE, &(parms->inConnHandle), sizeof(NULL_CONN_HANDLE)) == 0) ||
        (parms->outDataVector_p == 0)         ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Validate connection handle prior to invoking read
            // ----------------------------------------------------------------
            unsigned long long   requestedDataSize = 0;  // 0 identifies this as a server-side read...give me what is available

            LocalCommClientConnectionHandle_t* clientConnHandle_p = (LocalCommClientConnectionHandle_t*)(&(parms->inConnHandle));
            if (validateClientConnectionHandle(clientConnHandle_p) == 0)  {
                localRSN = issueReadRequest(clientConnHandle_p->handle_p, parms->inForceAsync, requestedDataSize, &dataVector_p);

                localRC = 0;
                if (localRSN < 0 ) {
                    localRC = -1;
                } else {
                    localRSN = 0;
                }
            } else {
                // Stale handle
                localRC = -1;
                localRSN = LOCAL_COMM_STALE_HANDLE;
            }
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_READ_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_READ_RC_NOESTAE;
    }

    // Tell the caller what happened
    memcpy_dk(parms->outDataVector_p,
              &dataVector_p,
              sizeof(dataVector_p),
              8);
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        int dataVectorSize = (dataVector_p == NULL) ? 0 : sizeof(LCOM_AvailableDataVector) + (sizeof(LCOM_ReadDataBlock) * dataVector_p->blockCount);
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_READ_EXIT),
                    "lcom_read, exit",
                    TRACE_DATA_RAWDATA(dataVectorSize, dataVector_p, "dataVector_p"),
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_read


/**
 * Release a Message cell (LMSG)
 *
 * @param parms pointer to <code>LCOM_ReleaseDataMessageParms</code>
 *
 */
void lcom_releaseDataMessage(LCOM_ReleaseDataMessageParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_RELLMSG_ENTRY),
                    "lcom_releaseDataMessage, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((memcmp(&NULL_CONN_HANDLE, &(parms->inConnHandle), sizeof(NULL_CONN_HANDLE)) == 0) ||
        (parms->inDataVector_p == 0)    ||
        (parms->outRC == 0)           ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Validate connection handle prior to invoking freeLastReadData
            // ----------------------------------------------------------------
            LocalCommClientConnectionHandle_t* clientConnHandle_p = (LocalCommClientConnectionHandle_t*)(&(parms->inConnHandle));
            if (validateClientConnectionHandle(clientConnHandle_p) == 0)  {
                for (int x = 0; x < parms->inDataVector_p->blockCount; x++) {
                    // Release returned data LMSG (may free the LMSG or just prepare for next read).
                    LCOM_ReadDataBlock* dataBlock_p = ((LCOM_ReadDataBlock*)((parms->inDataVector_p) + 1)) + x;
                    unsigned short freeLastRC = freeLastReadData(clientConnHandle_p->handle_p, dataBlock_p->dataCellPointer_p);

                    if (freeLastRC == LCOM_FREELREAD_RC_OK) {
                        localRC  = 0;
                        localRSN = 0;
                    } else {
                        localRC  = -1;
                        localRSN = freeLastRC;
                        break;
                    }
                }
                free(parms->inDataVector_p);
            } else {
                // Stale handle
                localRC = -1;
                localRSN = LOCAL_COMM_STALE_HANDLE;
            }
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_RELLMSG_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_RELLMSG_RC_NOESTAE;
    }

    // Tell the caller what happened
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_RELLMSG_EXIT),
                    "lcom_releaseDataMessage, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_releaseDataMessage

/**
 * Send data to the Outbound data queue
 *
 * @param parms pointer to <code>LCOM_WriteParms</code>
 *
 */
void lcom_write(LCOM_WriteParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_WRITE_ENTRY),
                    "lcom_write, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((memcmp(&NULL_CONN_HANDLE, &(parms->inConnHandle), sizeof(NULL_CONN_HANDLE)) == 0) ||
        (parms->inData_p == 0)                ||
        (parms->inDataSize == 0)              ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Drive send
            // ----------------------------------------------------------------
            localRC = localCommSend((void *)&(parms->inConnHandle), parms->inDataSize, parms->inData_p, 8);

        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_WRITE_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_WRITE_RC_NOESTAE;
    }

    // Tell the caller what happened
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_WRITE_EXIT),
                    "lcom_write, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_write

/**
 * Initiate the close process for a connection.
 *
 * @param parms pointer to <code>LCOM_CloseParms</code>
 *
 */
void lcom_close(LCOM_CloseParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_CLOSE_ENTRY),
                    "lcom_close, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // ----------------------------------------------------------------
    // Validate input parms
    // ----------------------------------------------------------------
    if ((memcmp(&NULL_CONN_HANDLE, &(parms->inConnHandle), sizeof(NULL_CONN_HANDLE)) == 0) ||
        (parms->outRC == 0)                   ||
        (parms->outRSN == 0) )  {
        localRSN = RSN_BAD_PARMS;

        if (parms->outRC != 0) {
            memcpy_dk(parms->outRC,
                      &localRC,
                      sizeof(localRC),
                      8);
        }

        if (parms->outRSN != 0) {
            memcpy_dk(parms->outRSN,
                      &localRSN,
                      sizeof(localRSN),
                      8);
        }

        return;
    }

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // ----------------------------------------------------------------
            // Drive common close code.
            // ----------------------------------------------------------------
            localRC = localCommClose((void *)& (parms->inConnHandle));
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_WRITE_RC_ABENDED;
        }

        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_WRITE_RC_NOESTAE;
    }

    // Tell the caller what happened
    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_CLOSE_EXIT),
                    "lcom_close, exit",
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_close

/**
 * Reset the work queue closure flags
 */
void lcom_initWRQFlags(LCOM_InitWRQFlagsParms *parms)
{
    int localRC, localRSN;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_ENTRY),
                    "lcom_initWRQFlags, entry",
                    TRACE_DATA_END_PARMS);
    }

    localRC  = -1;
    localRSN = 0;

    // -----------------------------------------------------------------------
    // Establish some recovery in case of an abend.  We would like to retry to
    // return back to the server with a failing rc/rsn code rather than a big
    // boom.
    // -----------------------------------------------------------------------
    int estaex_rc = -1;
    int estaex_rsn = -1;
    volatile int already_tried_it = 0;
    struct retry_parms retryParms;
    memset(&retryParms, 0, sizeof(retryParms));
    establish_estaex_with_retry(&retryParms,
                                &estaex_rc,
                                &estaex_rsn);

    if (estaex_rc == 0) {
        SET_RETRY_POINT(retryParms);
        if (already_tried_it == 0) {
            already_tried_it = 1;

            // Get PGOO
            server_process_data* spd_p = getServerProcessData();
            if (spd_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_SPD;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            // Get BBGZLOCL
            LocalCommClientAnchor_t* bbgzlocl_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;

            if (bbgzlocl_p == NULL) {
                localRC  = -1;
                localRSN = RSN_NO_BBGZLOCL;

                memcpy_dk(parms->outRC,
                          &localRC,
                          sizeof(localRC),
                          8);
                memcpy_dk(parms->outRSN,
                          &localRSN,
                          sizeof(localRSN),
                          8);

                remove_estaex(&estaex_rc, &estaex_rsn);

                return;
            }

            LocalCommWorkQueue * localWorkQueuePtr = &(bbgzlocl_p->serverSpecificInfo.serverAsClientWorkQ);

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_SLD),
                            "lcom_initWRQFlags, BBGZLOCL before changes",
                            TRACE_DATA_RAWDATA(sizeof(LocalCommWorkQueue),
                                               localWorkQueuePtr,
                                               "Server LocalCommWorkQueue"),
                            TRACE_DATA_END_PARMS);
            }

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_FLAGS_PRE),
                            "lcom_initWRQFlags, work queue closure flags, preupdate",
                            TRACE_DATA_HEX_INT(localWorkQueuePtr->wrqPLO_Area.flags.closingWorkQueue, "closingWorkQueue"),
                            TRACE_DATA_HEX_INT(localWorkQueuePtr->wrqPLO_Area.flags.closedDrainedWorkQueue, "closedDrainedWorkQueue"),
                            TRACE_DATA_END_PARMS);
            }

            // reset in case the queue was closed and reopened 
            initializeWRQFlags(localWorkQueuePtr);
            localRC = 0;

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_FLAGS_POST),
                            "lcom_initWRQFlags, work queue closure flags, postupdate",
                            TRACE_DATA_HEX_INT(localWorkQueuePtr->wrqPLO_Area.flags.closingWorkQueue, "closingWorkQueue"),
                            TRACE_DATA_HEX_INT(localWorkQueuePtr->wrqPLO_Area.flags.closedDrainedWorkQueue, "closedDrainedWorkQueue"),
                            TRACE_DATA_END_PARMS);
            }
        } else {
            // Abended and retried
            localRC = -1;
            localRSN = LOCAL_COMM_INITWRQFLAGS_RC_ABENDED;
        }


        // -----------------------------------------------------------------------
        // Remove the ESTAE.
        // -----------------------------------------------------------------------
        remove_estaex(&estaex_rc, &estaex_rsn);
    } else {
        // Couldn't establish an ESTAE
        localRC = -1;
        localRSN = LOCAL_COMM_INITWRQFLAGS_RC_NOESTAE;
    }


    memcpy_dk(parms->outRC,
              &localRC,
              sizeof(localRC),
              8);
    memcpy_dk(parms->outRSN,
              &localRSN,
              sizeof(localRSN),
              8);

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_SERVER_LCOM_SERVICES_INITWRQFLAGS_EXIT),
                    "lcom_initWRQFlags, exit",
                    TRACE_DATA_HEX_INT(localRC, "localRC"),
                    TRACE_DATA_HEX_INT(localRSN, "localRSN"),
                    TRACE_DATA_END_PARMS);
    }

} // end, lcom_initWRQFlags

/**
 * "Register" the LCOM hard failure cleanup routine to enable it to be called on
 * a hard failure for the server.
 */
void registerLCOMHardFailureRtn(void) {
    server_process_data* spd = getServerProcessData();

    if (spd != NULL) {
        spd->hardFailureRegisteredCleanupRtn[HARDFAILURE_REGISTRY_SLOT_LCOM] = hardFailureRegisteredCleanupRtn;
    }
}

/**
 * "DeRegister" the LCOM hard failure cleanup routine.
 */
void deregisterLCOMHardFailureRtn(void) {
    server_process_data* spd = getServerProcessData();

    if (spd != NULL) {
        spd->hardFailureRegisteredCleanupRtn[HARDFAILURE_REGISTRY_SLOT_LCOM] = NULL;
    }
}

/**
 * Cleanup routine driven from resmgr for a Task termination of a specially
 * marked thread.  This thread was started from Java during server startup to
 * cover a hard failure of the server (ex. a "kill -9") which bypassed normal
 * server shutdown.
 *
 * This routine Wake up the Local Comm listener thread with an indication to
 * stop listening for work because of a hard failure of the server.
 *
 */
static void hardFailureRegisteredCleanupRtn(void) {

    // ----------------------------------------------------------------
    // Wake up LCOM Listener thread
    // ----------------------------------------------------------------

    // Get PGOO
    server_process_data* spd_p = getServerProcessData();
    if (spd_p != NULL) {

        // Get BBGZLOCL from PGOO
        LocalCommClientAnchor_t* bbgzlocl_p = (LocalCommClientAnchor_t*) spd_p->lcom_BBGZLOCL_p;

        if (bbgzlocl_p != NULL) {
            // Set hard failure flag for local comm.


            // 1) Free the prior list of WRQEs
            LocalCommWorkQueue * localWorkQueuePtr = &(bbgzlocl_p->serverSpecificInfo.serverAsClientWorkQ);
            releaseReturnedWRQEs(localWorkQueuePtr);

            // Release Listener thread with stop release code.
            initiateClosingServerQueue(localWorkQueuePtr);
        }
    }


} // end, hardFailureRegisteredCleanupRtn
#pragma insert_asm(" CVT DSECT=YES")
