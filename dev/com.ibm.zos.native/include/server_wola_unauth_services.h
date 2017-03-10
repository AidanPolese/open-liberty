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

/**
 * Unauthorized services needed by the WOLA channel.
 */
#ifndef SERVER_WOLA_UNAUTH_SERVICES_H_
#define SERVER_WOLA_UNAUTH_SERVICES_H_

#include "bboaims.h"
#include "server_process_data.h"

#define OTMA_ERROR_SIZE 120

/**
 * Params for @c pc_OpenOTMAConnection
 */
typedef struct  {
    otma_anchor_t  otma_anchor;
    otma_retrsn_t otma_retrsn;
    ecb_t ecb;
    otma_grp_name_t otma_group_name;
    otma_srv_name_t otma_member_name;
    otma_clt_name_t otma_partner_name;
    sessions_t sessions;
    tpipe_prfx_t tpipe_prfx;
    otma_anchor_t* __ptr32 otma_anchor_p;
    otma_retrsn_t* __ptr32 otma_retrsn_p;
    ecb_t* __ptr32 ecb_p;
    otma_grp_name_t* __ptr32 otma_group_name_p;
    otma_srv_name_t* __ptr32 otma_member_name_p;
    otma_clt_name_t* __ptr32 otma_partner_name_p;
    sessions_t* __ptr32 sessions_p;
    tpipe_prfx_t* __ptr32 tpipe_prfx_p;
    unsigned char dynamic_area[72];
} OpenOTMAParms;

typedef struct {
    otma_anchor_t           anchor;
    otma_retrsn_t           retrsn;
    sess_handle_t           session_handle;
    otma_profile_t          options;
    tran_name_t             transaction;
    racf_uid_t              user_id;
    racf_prf_t              user_grp;
    otma_anchor_t*  __ptr32 anchor_p;
    otma_retrsn_t*  __ptr32 retrsn_p;
    sess_handle_t*  __ptr32 session_handle_p;
    otma_profile_t* __ptr32 options_p;
    tran_name_t*    __ptr32 transaction_p;
    racf_uid_t*     __ptr32 user_id_p;
    racf_prf_t*     __ptr32 user_grp_p;
    unsigned char           dynamic_area[72];
} otma_alloc_parms;

typedef struct {
    unsigned int seg_count;
    unsigned int seg_length[];
} otma_seg_list;

typedef struct {
    otma_anchor_t           anchor;
    otma_retrsn_t           retrsn;
    unsigned int            ecb;
    sess_handle_t           session_handle;
    lterm_name_t            lterm;
    mod_name_t              modname;
    data_leng_t             send_length;
    data_leng_t             recv_length_max;
    data_leng_t             recv_length;
    context_t               context_id;
    unsigned char           error_message[OTMA_ERROR_SIZE];
    void*          __ptr32  error_message_ptr;
    otma_user_t             otma_userdata;
    int                     postCode;
    otma_anchor_t* __ptr32  anchor_p;
    otma_retrsn_t* __ptr32  retrsn_p;
    unsigned int * __ptr32  ecb_p;
    sess_handle_t* __ptr32  session_handle_p;
    lterm_name_t*  __ptr32  lterm_p;
    mod_name_t*    __ptr32  modname_p;
    char *         __ptr32  send_buffer_p;
    data_leng_t*   __ptr32  send_length_p;
    otma_seg_list* __ptr32  send_seg_list_p;
    char *         __ptr32  recv_buffer_p;
    data_leng_t*   __ptr32  recv_length_max_p;
    data_leng_t*   __ptr32  recv_length_p;
    otma_seg_list* __ptr32  recv_seg_list_p;
    context_t*     __ptr32  context_id_p;
    void*          __ptr32  error_message_ptr_ptr;
    otma_user_t*   __ptr32  otma_userdata_p;
    //lterm_name_t*  __ptr32  lterm2_p;
    unsigned char           dynamic_area[72];
} otma_sendrcv_parms;

typedef struct {
    otma_anchor_t          anchor;
    otma_retrsn_t          retrsn;
    sess_handle_t          session_handle;
    otma_anchor_t* __ptr32 anchor_p;
    otma_retrsn_t* __ptr32 retrsn_p;
    sess_handle_t* __ptr32 session_handle_p;
    unsigned char          dynamic_area[72];
} otma_free_parms;

typedef struct {
    otma_anchor_t          anchor;
    otma_retrsn_t          retrsn;
    otma_anchor_t* __ptr32 anchor_p;
    otma_retrsn_t* __ptr32 retrsn_p;
    unsigned char          dynamic_area[72];
} otma_close_parms;

/**
 * Call OTMA Open and wait until the open is successful
 *
 * @param pc_OpenOTMAParms
*/
int openOTMAConnection(OpenOTMAParms* parms);
int otmaAllocate(otma_alloc_parms * parms);
int otmaSendReceive(otma_sendrcv_parms * parms);
int otmaFree(otma_free_parms * parms, void* ecb_p, int* postCode_p);
int closeOtmaConnection(otma_close_parms * parms);
void cleanupOTMAAnchors(server_process_data * spd_p);

void call31Bit(void* fcn_p, void* parms_p, void* dynArea_p);

#endif /* SERVER_WOLA_UNAUTH_SERVICES_H_ */
