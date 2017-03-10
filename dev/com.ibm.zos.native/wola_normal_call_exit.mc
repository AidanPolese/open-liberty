/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <metal.h>

#include "include/gen/bboaxvec.h"

#include "include/angel_bgvt_services.h"
#include "include/common_mc_defines.h"
#include "include/ras_abend_codes.h"
#include "include/server_wola_client.h"
#include "include/server_wola_services.h"
#include "include/wola_normal_call_exit.h"

/**
 * !!! WARNING !!!
 * This routine does not use the usual 1MB stack and metal C environment.
 * Be conscious of stack usage and know that certain functions may be unavailable.
 */

/**
 * Assembler macro to call a 31-bit TWAS WOLA client function.
 */
#define CALL_31BIT(function_p, parms_p)     \
    __asm(AMODE_31(" LR   1,%1\n"           \
                   " LR   15,%0\n"          \
                   " BASR 14,15")           \
          : : "r"(function_p), "r"(parms_p) \
          : "r0","r1","r14","r15");

/**
 * Normal call exit for IMS inbound into WOLA.
 *
 * @param eevtPrefix_p Not used.
 * @param essplist_p Pointer to external subsystem parameter list.
 * @param callerParm_p Pointer to list of parms to forward to the called WOLA function.
 * @param recToken_p Not used.
 * @param auth_p Not used.
 */
#pragma prolog(normalCallExit,"ESAFPRLG")
#pragma epilog(normalCallExit,"ESAFEPLG")
int normalCallExit(void* eevtPrefix_p,
                   externalSubsystemParameterList* essplist_p,
                   callerParmListP* callerParmP_p,
                   void* recToken_p,
                   void* auth_p) {

    // Find the client stub name we were called from
    wolaStubObjectCode* wolaStubObjectCode_p = NULL;
    if (essplist_p != NULL) {
        stubEPA* stubEPA_p = essplist_p->epa_p;
        if (stubEPA_p != NULL) {
            wolaStubObjectCode_p = stubEPA_p->objectCode_p;
        }
    }
    if (wolaStubObjectCode_p == NULL) {
        __asm(" ABEND (%0),REASON=(%1),DUMP,STEP,SYSTEM" : :
              "r"(ABEND_TYPE_SERVER),"r"(KRSN_WOLA_CLIENT_NORMAL_CALL_FAIL));
    }

    // Find the client stubs hung off the MCST
    psa*  psa_p = NULL;
    cvt*  cvt_p = (cvt* __ptr32) psa_p->flccvt;
    ecvt* ecvt_p = (ecvt* __ptr32) cvt_p->cvtecvt;
    bgvt* bgvt_p = (bgvt* __ptr32) ecvt_p->ecvtbcba;
    bboamcst* bboamcst_p = (bboamcst* __ptr32) bgvt_p->bbodbgvt_bboamcst_ptr;
    // Liberty stubs are in slot 2
    bboamcss* bboamcss_p = bboamcst_p->amcst_slots[1];
    bboacall* bboacall_p = bboamcss_p->amcss_vector_ptr;
    // TWAS stubs are in slot 1, if they've been loaded
    bboaxvec_twas* bboaxvec_twas_p = NULL;
    bboamcss* bboamcss_twas_p = bboamcst_p->amcst_slots[0];
    if (bboamcss_twas_p != NULL) {
        bboaxvec_twas_p = bboamcss_twas_p->amcss_vector_ptr;
    }

    int* rc_p = NULL;
    int* rsn_p = NULL;
    callerParmList* callerParm_p = callerParmP_p->callerParmList_p;

    // Determine which WOLA stub was called, then use the appropriate callerParmList
    // mapping to supply the parms to the WOLA client function.

    // BBOA1REG - register
    if (memcmp(wolaStubObjectCode_p->stubName, "BBOASREG", 8) == 0) {
        bboacall_p->wola_register(callerParm_p->parmREG.dgname_p,
                                  callerParm_p->parmREG.nodename_p,
                                  callerParm_p->parmREG.servername_p,
                                  callerParm_p->parmREG.regionname_p,
                                  callerParm_p->parmREG.minconn_p,
                                  callerParm_p->parmREG.maxconn_p,
                                  callerParm_p->parmREG.regflags_p,
                                  NULL,
                                  callerParm_p->parmREG.rc_p,
                                  callerParm_p->parmREG.rsn_p);
        rc_p = callerParm_p->parmREG.rc_p;
        rsn_p = callerParm_p->parmREG.rsn_p;
        if (*rc_p == WOLA_RC_SEVERE12 && *rsn_p == WOLA_RSN_BADGRP) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_reg_, callerParm_p);
            } // else report original ret & rsn
        }
    }
    // BBOA1URG - unregister
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASURG", 8) == 0) {
        bboacall_p->wola_unregister(callerParm_p->parmURG.registername,
                                    callerParm_p->parmURG.unregflags_p,
                                    NULL,
                                    callerParm_p->parmURG.rc_p,
                                    callerParm_p->parmURG.rsn_p);
        rc_p = callerParm_p->parmURG.rc_p;
        rsn_p = callerParm_p->parmURG.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_ureg_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1CNG - connection get
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASCNG", 8) == 0) {
        bboacall_p->wola_conn_get(callerParm_p->parmCNG.registername,
                                  callerParm_p->parmCNG.connhandle_p,
                                  callerParm_p->parmCNG.waittime_p,
                                  NULL,
                                  callerParm_p->parmCNG.rc_p,
                                  callerParm_p->parmCNG.rsn_p);
        rc_p = callerParm_p->parmCNG.rc_p;
        rsn_p = callerParm_p->parmCNG.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_cong_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1CNR - connection release
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASCNR", 8) == 0) {
        bboacall_p->wola_conn_release(callerParm_p->parmCNR.connhandle_p,
                                      NULL,
                                      callerParm_p->parmCNR.rc_p,
                                      callerParm_p->parmCNR.rsn_p);
        rc_p = callerParm_p->parmCNR.rc_p;
        rsn_p = callerParm_p->parmCNR.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_conr_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1SRQ - send request
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASSRQ", 8) == 0) {
        bboacall_p->wola_send_request(callerParm_p->parmSRQ.connectionhdl_p,
                                      callerParm_p->parmSRQ.reqtype_p,
                                      callerParm_p->parmSRQ.servicename,
                                      callerParm_p->parmSRQ.servicenamelen_p,
                                      *(callerParm_p->parmSRQ.reqdata_p),
                                      callerParm_p->parmSRQ.reqdatalen_p,
                                      callerParm_p->parmSRQ.async_p,
                                      callerParm_p->parmSRQ.rspdatalen_p,
                                      NULL,
                                      callerParm_p->parmSRQ.rc_p,
                                      callerParm_p->parmSRQ.rsn_p);
        rc_p = callerParm_p->parmSRQ.rc_p;
        rsn_p = callerParm_p->parmSRQ.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_snrq_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1SRP - send response
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASSRP", 8) == 0) {
        bboacall_p->wola_send_response(callerParm_p->parmSRP.connhandle_p,
                                       *(callerParm_p->parmSRP.rspdata_p),
                                       callerParm_p->parmSRP.rspdatalen_p,
                                       NULL,
                                       callerParm_p->parmSRP.rc_p,
                                       callerParm_p->parmSRP.rsn_p);
        rc_p = callerParm_p->parmSRP.rc_p;
        rsn_p = callerParm_p->parmSRP.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_snrp_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1SRX - send response exception
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASSRX", 8) == 0) {
        bboacall_p->wola_send_resp_exc(callerParm_p->parmSRX.connhandle_p,
                                       *(callerParm_p->parmSRX.excRspdata_p),
                                       callerParm_p->parmSRX.excRspdatalen_p,
                                       NULL,
                                       callerParm_p->parmSRX.rc_p,
                                       callerParm_p->parmSRX.rsn_p);
        rc_p = callerParm_p->parmSRX.rc_p;
        rsn_p = callerParm_p->parmSRX.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_snrx_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1RCA - receive request any
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASRCA", 8) == 0) {
        bboacall_p->wola_rcv_req_any(callerParm_p->parmRCA.registername,
                                     callerParm_p->parmRCA.connhandle_p,
                                     callerParm_p->parmRCA.servicename,
                                     callerParm_p->parmRCA.servicenamelen_p,
                                     callerParm_p->parmRCA.reqdatalen_p,
                                     callerParm_p->parmRCA.waittime_p,
                                     NULL,
                                     callerParm_p->parmRCA.rc_p,
                                     callerParm_p->parmRCA.rsn_p);
        rc_p = callerParm_p->parmRCA.rc_p;
        rsn_p = callerParm_p->parmRCA.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_rrqa_, callerParm_p);
            }
        }
    }
    // BBOA1RCS - receive request specific
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASRCS", 8) == 0) {
        bboacall_p->wola_rcv_req_spec(callerParm_p->parmRCS.connectionhdl_p,
                                      callerParm_p->parmRCS.servicename,
                                      callerParm_p->parmRCS.servicenamelen_p,
                                      callerParm_p->parmRCS.reqdatalen_p,
                                      callerParm_p->parmRCS.async_p,
                                      NULL,
                                      callerParm_p->parmRCS.rc_p,
                                      callerParm_p->parmRCS.rsn_p);
        rc_p = callerParm_p->parmRCS.rc_p;
        rsn_p = callerParm_p->parmRCS.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_rrqs_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1RCL - receive response length
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASRCL", 8) == 0) {
        bboacall_p->wola_rcv_respl(callerParm_p->parmRCL.connhandle_p,
                                   callerParm_p->parmRCL.async_p,
                                   callerParm_p->parmRCL.rspdatalen_p,
                                   NULL,
                                   callerParm_p->parmRCL.rc_p,
                                   callerParm_p->parmRCL.rsn_p);
        rc_p = callerParm_p->parmRCL.rc_p;
        rsn_p = callerParm_p->parmRCL.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_rcrl_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1GET - get data
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASGET", 8) == 0) {
        bboacall_p->wola_get_data(callerParm_p->parmGET.connhandle_p,
                                  *(callerParm_p->parmGET.msgdata_p),
                                  callerParm_p->parmGET.msgdatalen_p,
                                  NULL,
                                  callerParm_p->parmGET.rc_p,
                                  callerParm_p->parmGET.rsn_p,
                                  callerParm_p->parmGET.rval_p);
        rc_p = callerParm_p->parmGET.rc_p;
        rsn_p = callerParm_p->parmGET.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_getd_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1INV - invoke
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASINV", 8) == 0) {
        bboacall_p->wola_invoke(callerParm_p->parmINV.registername,
                                callerParm_p->parmINV.reqtype,
                                callerParm_p->parmINV.servicename,
                                callerParm_p->parmINV.servicenamelen,
                                *(callerParm_p->parmINV.reqdata),
                                callerParm_p->parmINV.reqdatalen,
                                *(callerParm_p->parmINV.rspdata),
                                callerParm_p->parmINV.rspdatalen,
                                callerParm_p->parmINV.waittime,
                                NULL,
                                callerParm_p->parmINV.rc_p,
                                callerParm_p->parmINV.rsn_p,
                                callerParm_p->parmINV.rval_p);
        rc_p = callerParm_p->parmINV.rc_p;
        rsn_p = callerParm_p->parmINV.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_invk_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1SRV - host service
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASSRV", 8) == 0) {
        bboacall_p->wola_host_service(callerParm_p->parmSRV.registername,
                                      callerParm_p->parmSRV.servicename,
                                      callerParm_p->parmSRV.servicenamelen_p,
                                      *(callerParm_p->parmSRV.reqdata_p),
                                      callerParm_p->parmSRV.reqdatalen_p,
                                      callerParm_p->parmSRV.connhandle_p,
                                      callerParm_p->parmSRV.waittime_p,
                                      NULL,
                                      callerParm_p->parmSRV.rc_p,
                                      callerParm_p->parmSRV.rsn_p,
                                      callerParm_p->parmSRV.rval_p);
        rc_p = callerParm_p->parmSRV.rc_p;
        rsn_p = callerParm_p->parmSRV.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_host_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1INF - get info
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASINF", 8) == 0) {
        bboacall_p->wola_get_info(callerParm_p->parmINF.registername,
                                  callerParm_p->parmINF.wolaGroup,
                                  callerParm_p->parmINF.wolaName2,
                                  callerParm_p->parmINF.wolaName3,
                                  *(callerParm_p->parmINF.connInfo_p),
                                  NULL,
                                  callerParm_p->parmINF.rc_p,
                                  callerParm_p->parmINF.rsn_p);
        rc_p = callerParm_p->parmINF.rc_p;
        rsn_p = callerParm_p->parmINF.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_info_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // BBOA1GTX - get context
    else if (memcmp(wolaStubObjectCode_p->stubName, "BBOASGTX", 8) == 0) {
        bboacall_p->wola_get_context(callerParm_p->parmGTX.connhandle_p,
                                     *(callerParm_p->parmGTX.ctxdata_p),
                                     callerParm_p->parmGTX.ctxdatalen_p,
                                     NULL,
                                     callerParm_p->parmGTX.rc_p,
                                     callerParm_p->parmGTX.rsn_p,
                                     callerParm_p->parmGTX.rval_p);
        rc_p = callerParm_p->parmGTX.rc_p;
        rsn_p = callerParm_p->parmGTX.rsn_p;
        if (*rc_p == WOLA_RC_ERROR8 && *rsn_p == WOLA_RSN_INTERNAL_TRY_TWAS_STUB) {
            if (bboaxvec_twas_p != NULL) {
                CALL_31BIT(bboaxvec_twas_p->avectwas_gtx_, callerParm_p);
            } else {
                *rc_p = WOLA_RC_SEVERE12;
                *rsn_p = WOLA_RSN_NO_AMCSS;
            }
        }
    }
    // Unknown API
    else {
        __asm(" ABEND (%0),REASON=(%1),DUMP,STEP,SYSTEM" : :
              "r"(ABEND_TYPE_SERVER),"r"(KRSN_WOLA_CLIENT_NORMAL_CALL_UNKNOWN_API));
    }

    return *rc_p;
}
