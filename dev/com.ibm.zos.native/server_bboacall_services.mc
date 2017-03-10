/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <stdlib.h>

#include "include/server_wola_services.h"
#include "include/client_wola_stubs.h"

 void BBOASREG(void);
 void BBOASURG(void);
 void BBOASINV(void);
 void BBOASSRV(void);
 void BBOASSRP(void);
 void BBOASCNR(void);
 void BBOASCNG(void);
 void BBOASSRQ(void);
 void BBOASGET(void);
 void BBOASRCL(void);
 void BBOASRCA(void);
 void BBOASRCS(void);
 void BBOASSRX(void);
 void BBOASGTX(void);
 void BBOASINF(void);

//-----------------------------------------------------------
// CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!!
//-----------------------------------------------------------
//  ANY CHANGES TO ANY OF THE PARTS LISTED IN THIS VECTOR
//  (bboasreg.s, bboasurg.s, bboasinv.s,
//  server_bboacall_services.mc or client_wola_stubs.mc )
//  need to have the Client Vector Slot
//  level below incremented. If this is not done, then
//  the code in server_wola_services.mc that loads BBOACALL
//  will NOT reload this module if there is already a BBOACALL
//  at an older level loaded.
//  While testing changes to modules in BBOACALL, you can
//  set this value to 9999 and server_wola_services.mc will
//  always replace an old BBOACALL with this one.
//  We started with level 101 for WAS 9 where this code
//  is introduced.
//-----------------------------------------------------------
// CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!! CAUTION !!!
//-----------------------------------------------------------

const struct bboacall BBGAXVEC = {
    .eyecatcher          = "BBOAXVEC",
    .build_date          = BUILD_DATE_STAMP,
    .build_time          = BUILD_TIME_STAMP,
    .wasVersion          = 9,
    .stubVecSlotlevel    = 148,
    .flags               = 0,
    .asm_wola_reg        = BBOASREG,
    .wola_register       = ClientRegister,
    .asm_wola_urg        = BBOASURG,
    .wola_unregister     = ClientUnRegister,
    .asm_wola_inv        = BBOASINV,
    .wola_invoke         = ClientInvoke,
    .asm_wola_srv        = BBOASSRV,
    .wola_host_service    = ClientHostService,
    .asm_wola_srp        = BBOASSRP,
    .wola_send_response  = ClientSendResponse,
    .asm_wola_cnr        = BBOASCNR,
    .wola_conn_release   = ClientConnectionRelease,
    .asm_wola_cng        = BBOASCNG,
    .wola_conn_get       = ClientConnectionGet,
    .asm_wola_srq        = BBOASSRQ,
    .wola_send_request   = ClientSendRequest,
    .asm_wola_get        = BBOASGET,
    .wola_get_data       = ClientGetData,
    .asm_wola_rcl        = BBOASRCL,
    .wola_rcv_respl      = ClientReceiveResponseLength,
    .asm_wola_rca        = BBOASRCA,
    .wola_rcv_req_any    = ClientReceiveRequestAny,
    .asm_wola_rcs        = BBOASRCS,
    .wola_rcv_req_spec   = ClientReceiveRequestSpecific,
    .asm_wola_srx        = BBOASSRX,
    .wola_send_resp_exc  = ClientSendResponseException,
    .asm_wola_gtx        = BBOASGTX,
    .wola_get_context    = ClientGetContext,
    .asm_wola_inf        = BBOASINF,
    .wola_get_info       = ClientInfoGet
};

