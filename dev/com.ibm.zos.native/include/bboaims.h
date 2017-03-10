#ifndef ASUVOTMA
#define ASUVOTMA
 /*--------------------------------------------------------------------
 *
 *   Header name: bboaims.h
 *   Macro name:  bboaims
 *   DSECT name:  None
 *   Component:   WAS z/OS
 *
 *   Descriptive name:  WAS Optimized local adapters IMS control blocks
 *
 * ***** IMPORTANT ***** IMPORTANT ***** IMPORTANT *****
 *
 * This header was created from the IMS OTMA header DFSYC0 that is
 * available in the IMS ADFSMAC dataser. We needed to copy and alter
 * this to support running in a 64 bit C environment.
 *
 * ***** IMPORTANT ***** IMPORTANT ***** IMPORTANT *****
 *
 *
 *   Proprietary statement:
 *
 * IBM Confidential
 * OCO Source Materials
 * 5655-N01 Copyright IBM Corp. 2000, 2009
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * Status = H28W700
 *
 *
 *   Created by:  WAS on z/OS Optimized local adapters
 *
 *   Serialization:
 *
 *   Function:
 *
 *    This header provides mappings for OLA structures.
 *
 *   External classification: none
 *   End of external classification:
 *
 *   Method of access:
 *     C++: #include bboaims.h
 *
 *
 *   Deleted by: User or System
 *
 *   Frequency:
 *
 *   Dependencies:  None
 *
 *   Distribution library:  n/a
 *
 *   Change activity:
 *   $L0=F003694 H28W700, 20100120, JTM:  Created from IMS DFSYC0 header.
 *   $667477     H28W700, 20100820, JTM:  Add multi-segment message supt.
 *   $670111     H28W700, 20100925, JTM:  32K+ messages.
 *
 *--------------------------------------------------------------------*/

/*********************************************************************/
/*   Parameter Data type Definitions for OTMA Client Code Interface  */
/*   calls.                                                          */
/*********************************************************************/

typedef unsigned char  otma_anchor_t[8]; /* subsystem anchor         */
                                 /* Required on every OTMA API call  */

typedef struct otma_retrsn       /* required on every OTMA call      */
{                                /* reports results info about call  */
  int ret;                       /* API return code                  */
  int rsn[4];                    /* API reason codes                 */
}otma_retrsn_t;

typedef unsigned      int  ecb_t;/* MVS Event Control Block          */
                                 /* Used on OPEN and Receive         */

                 /* Parameters unique to OPEN                        */

typedef unsigned char otma_grp_name_t[8]; /* XCF Group Member Name   */
typedef unsigned char otma_srv_name_t[16];/* XCF Server Member Name  */
typedef unsigned char otma_clt_name_t[16];/* XCF Client Member Name  */
typedef unsigned char otma_dru_name_t[8]; /* IMS OTMA DRU exit name  */

                 /* Parameters unique to ALLOC                       */

typedef unsigned char  tran_name_t[8];   /* IMS transaction name     */
typedef unsigned char  racf_uid_t[8];    /* RACF User ID -           */
typedef unsigned char  racf_psw_t[8];    /* RACF password for User ID*/
typedef unsigned char  racf_prf_t[8];    /* SAF profile/RACF group   */
typedef unsigned char  sess_handle_t[8]; /* correlator id substitute */
typedef unsigned char  context_t[16];    /* RRS Context ID           */
typedef          int   ioseg_list_t[1];  /* Not used         @670111C*/
typedef unsigned char  mod_name_t[8];    /* Msg Output Descriptor    */
typedef unsigned char  lterm_name_t[8];  /* Logical TERMinal name    */
typedef unsigned char  tpipe_prfx_t[4];  /* 4-byte tpipe name for    */
                                         /* otma_create or otma_open */
typedef unsigned char  tpipe_name_t[8];  /* User specified tpipe name*/
                                         /* for otma_send_async and  */
                                         /* otma_receive_async       */
typedef unsigned char  otma_user_t[8];   /* 1022-byte user data      */
                                         /* passed to or received    */
                                         /* from IMS         @670111C*/
typedef signed int sessions_t;

typedef struct otma_prf          /* structure defining options       */
{
  unsigned SyncOnReturn     : 1 ;/* Ask IMS to process the message   */
                                 /* without the context token, but   */
                                 /* userid will be obtained from the */
                                 /* RRS CTXRDTA invocation.          */
  unsigned SyncLevel1       : 1 ;/* Use OTMA CM1 SYNCLEVEL 1         */
                                 /* instead of the default SYNCLEVEL */
                                 /* 0. This flag has no meaning to   */
                                 /* the async API.                   */
}otma_profile_t;

                 /* Parameters unique to SEND_RECEIVE                */

typedef int data_leng_t;         /* data buffer length               */
typedef unsigned char * data_addr_t; /* data buffer address          */

                 /* Parameters unique to otma_send_async             */

typedef struct otma_prf_2        /* structure defining options       */
{
  unsigned send_cancel      : 1 ;/* cancel the send request          */
}otma_profile2_t;
                 /* Parameters unique to otma_receive_async          */

typedef struct otma_prf_3        /* structure defining options       */
{
  unsigned rec_cancel       : 1 ;/* cancel the receive request       */
}otma_profile3_t;
                 /* Parameters unique to otma_openx                  */

typedef struct otma_prf_4        /* structure defining options       */
{
  unsigned open_dummy_flag  : 1 ;/* reserved flag                    */
}otma_profile4_t;

/*********************************************************************/
/*       vestigial - stuff used in the old otma and still referenced */
/*********************************************************************/

typedef unsigned char   resrcenm_t[32];  /* IMS Resource Name        */
typedef struct xcfnames
{
  otma_grp_name_t grp_name;      /* API XCF Group Member Name        */
  otma_srv_name_t clt_name;      /* API XCF Server Member Name       */
  otma_clt_name_t srv_name;      /* API XCF Client Member Name       */
} xcfnames_t;

/*********************************************************************/
/*            defaults -- Macros use these values                    */
/*********************************************************************/

#pragma map(otma_create,        "DFSYCRET")
#pragma map(otma_open,          "DFSYOPN1")
#pragma map(otma_openx,         "DFSYOPN2")
#pragma map(otma_alloc,         "DFSYALOC")
#pragma map(otma_send_receive,  "DFSYSEND")
#pragma map(otma_send_receivex, "DFSYSNDX")
#pragma map(otma_free,          "DFSYFREE")
#pragma map(otma_close,         "DFSYCLSE")
#pragma map(otma_send_async,    "DFSYSNDA")
#pragma map(otma_receive_async, "DFSYRCVA")
#pragma map(qinit,              "DFSYQINT")
#pragma map(qget ,              "DFSYQGET")

#ifdef __cplusplus  /* DEFINE "C++" INTERFACE DEFINITIONS            */

  /*  qinit and qget are for otma c/i internal debugging         */

  extern "OS" {
   void qinit (otma_anchor_t  *anchor, int *count, int *rc );
  }

  extern "OS" {
   void qget  (otma_anchor_t  *anchor, int *tag,
               char   *Text  , int *text_length, int *rc);
  }

  extern "OS" {
   void otma_create(
     otma_anchor_t   *anchor,         /* [out]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [in ]                       */
     otma_grp_name_t *group_name,     /* [in]   XCF group name       */
     otma_clt_name_t *member_name,    /* [in]   our XCF member name  */
     otma_srv_name_t *partner_name,   /* [in]   IMS XCF member name  */
     signed int *sessions,            /* [in]   number of threads    */
     tpipe_prfx_t    *tpipe_name      /* [in]   any 4-character name */
     );
  }

  extern "OS" {
   void otma_open(
     otma_anchor_t   *anchor,         /* [out]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */
     otma_grp_name_t *group_name,     /* [in]   XCF group name       */
     otma_clt_name_t *member_name,    /* [in]   our XCF member name  */
     otma_srv_name_t *partner_name,   /* [in]   IMS XCF member name  */
     signed int *sessions,            /* [in]   number of threads    */
     tpipe_prfx_t    *tpipe_name      /* [in]   any 4-character name */
     );
  }

     /*  otma_openx- connect to IMS function for OTMA client    */
     /*              and specify the name of IMS DRU0 user exit */
  extern "OS" {
   void otma_openx(
     otma_anchor_t   *anchor,         /* [out]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */
     otma_grp_name_t *group_name,     /* [in]   XCF group name       */
     otma_clt_name_t *member_name,    /* [in]   our XCF member name  */
     otma_srv_name_t *partner_name,   /* [in]   IMS XCF member name  */
     signed int *sessions,            /* [in]   number of threads    */
     tpipe_prfx_t    *tpipe_name,     /* [in]   any 4-character name */
     otma_dru_name_t *ims_dru_name,   /* [in]   IMS otma dru exit    */
     otma_profile4_t *special_options /* [in]   special options      */
     );
  }

  extern "OS" {
void otma_alloc(
     otma_anchor_t   *anchor,         /* [in]   anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */

     sess_handle_t   *sess_handle,    /* [out]  session id           */
     otma_profile_t  *special_options,/* [in]   special options      */

     tran_name_t     *transaction,    /* [in]   IMS trancode or cmd  */
     racf_uid_t      *username,       /* [in]   RACF userid          */
     racf_prf_t      *prfname         /* [in]   RACF group name      */
     );
  }

  extern "OS" {
   void otma_free(
     otma_anchor_t   *anchor,         /* [in ]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     sess_handle_t   *sess_handle     /* [in]   unique path id       */
     );
  }

  extern "OS" {
   void otma_send_receive(
     otma_anchor_t   *anchor,         /* [in ]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */

     sess_handle_t   *sess_handle,    /* [in]   unique path id       */
     lterm_name_t    *lterm,          /* [in,out] Lterm              */
     mod_name_t      *modname,        /* [in,out] MODname            */

     char            *send_buffer,    /* [in]  send buffer           */
     data_leng_t     *send_length,    /* [in]  send buffer length    */
     ioseg_list_t    *send_seg_list,  /* [in]  send multiple segment */
                                      /*       length vector         */

     char            *receive_buffer, /* [in]  receive buffer        */
     data_leng_t     *receive_length, /* [in]  receive buffer length */
     data_leng_t     *received_length,/* [out] data received         */
     ioseg_list_t    *receive_seg_list,/*[in/out] receive multiple   */
                                      /*       segment length vector */

     context_t       *context_id,     /* [in]  RRS context token     */
     char            *error_message   /* [out] msg from IMS @PQ63675 */
     );
  }

  extern "OS" {
   void otma_send_receivex(
     otma_anchor_t   *anchor,         /* [in ]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */

     sess_handle_t   *sess_handle,    /* [in]   unique path id       */
     lterm_name_t    *lterm,          /* [in,out] Lterm              */
     mod_name_t      *modname,        /* [in,out] MODname            */

     char            *send_buffer,    /* [in]  send buffer           */
     data_leng_t     *send_length,    /* [in]  send buffer length    */
     ioseg_list_t    *send_seg_list,  /* [in]  send multiple segment */
                                      /*       length vector         */

     char            *receive_buffer, /* [in]  receive buffer        */
     data_leng_t     *receive_length, /* [in]  receive buffer length */
     data_leng_t     *received_length,/* [out] data received         */
     ioseg_list_t    *receive_seg_list,/*[in/out] receive multiple   */
                                      /*       segment length vector */

     context_t       *context_id,     /* [in]  RRS context token     */
     char            *error_message,  /* [out] msg from IMS @PQ63675 */
     otma_user_t     *otma_userdata   /* [in,out] OTMA userdata      */
     );
  }

  extern "OS" {
   void otma_send_async(
     otma_anchor_t   *anchor,         /* [in ] anchor                */
     otma_retrsn_t   *retrsn,         /* [out] rc,reason[1-4]        */
     ecb_t           *ecb,            /* [out] are we done?          */

     tpipe_name_t    *async_tpipe_name,/* [in] user tpipe name       */
     tran_name_t     *transaction,    /* [in]  IMS trancode or cmd   */
     racf_uid_t      *username,       /* [in]  RACF userid           */
     racf_prf_t      *prfname,        /* [in]  RACF group name       */
     lterm_name_t    *lterm,          /* [in]  Lterm                 */
     mod_name_t      *modname,        /* [in]  MODname               */
     otma_user_t     *otma_userdata,  /* [in]  OTMA userdata         */

     char            *send_buffer,    /* [in]  send buffer           */
     data_leng_t     *send_length,    /* [in]  send buffer length    */
     ioseg_list_t    *send_seg_list,  /* [in]  send multiple segment */
                                      /*       length vector         */
     char            *error_message,  /* [out] msg from IMS @PQ63675 */
     otma_profile2_t  *special_options/* [in]  special options       */

     );
  }

  extern "OS" {
   void otma_receive_async(
     otma_anchor_t   *anchor,         /* [in ] anchor                */
     otma_retrsn_t   *retrsn,         /* [out] rc,reason[1-4]        */
     ecb_t           *ecb,            /* [out] are we done           */

     tpipe_name_t    *async_tpipe_name,/* [in] user tpipe name       */
     lterm_name_t    *lterm,          /* [out] Lterm                 */
     mod_name_t      *modname,        /* [out] MODname               */
     otma_user_t     *otma_userdata,  /* [out] OTMA userdata         */

     char            *receive_buffer, /* [out] receive buffer        */
     data_leng_t     *receive_length, /* [in]  receive buffer length */
     data_leng_t     *received_length,/* [out] data received         */
     ioseg_list_t    *receive_seg_list,/*[in/out] receive multiple   */
                                      /*       segment length vector */
     otma_profile3_t *special_options  /* [in]  special options      */

     );

  }

  extern "OS" {
   void otma_close(
     otma_anchor_t   *anchor,         /* [in,out] anchor             */
     otma_retrsn_t   *retrsn          /* [out]  rc,reason[1-4]       */
     );
  }

  extern "OS" {
   void DFSYCWAT(
     unsigned int  * ecb
     );
  }

#else /* DEFINE "C" INTERFACE DEFINITIONS                            */

#pragma linkage(otma_create,      OS)
#pragma linkage(otma_open,        OS)
#pragma linkage(otma_openx,       OS)
#pragma linkage(otma_alloc,       OS)
#pragma linkage(otma_send_receive,OS)
#pragma linkage(otma_send_receivex,OS)
#pragma linkage(otma_free,        OS)
#pragma linkage(otma_close,       OS)
#pragma linkage(otma_send_async,    OS)
#pragma linkage(otma_receive_async, OS)

#pragma linkage(SQINIT,  OS)
#pragma linkage(SQGET ,  OS)

 /*  qinit and qget are for otma c/i internal debugging         */
void qinit (otma_anchor_t  *anchor, int *count, int *rc );
void qget  (otma_anchor_t  *anchor, int *tag,
            char          *Text  , int *text_length, int *rc);

        /**********************************************/
        /*   Define OTMA Functions                    */
        /**********************************************/

      /*  otma_create - creator function for OTMA client data areas  */

void otma_create(
     otma_anchor_t   *anchor,         /* [out]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [in ]                       */
     otma_grp_name_t *group_name,     /* [in]   XCF group name       */
     otma_clt_name_t *member_name,    /* [in]   our XCF member name  */
     otma_srv_name_t *partner_name,   /* [in]   IMS XCF member name  */
     signed int *sessions,            /* [in]   number of threads    */
     tpipe_prfx_t    *tpipe_name      /* [in]   any 4-character name */
     );

     /*  otma_open - connect to IMS function for OTMA client */
void otma_open(
     otma_anchor_t   *anchor,         /* [out]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */
     otma_grp_name_t *group_name,     /* [in]   XCF group name       */
     otma_clt_name_t *member_name,    /* [in]   our XCF member name  */
     otma_srv_name_t *partner_name,   /* [in]   IMS XCF member name  */
     signed int *sessions,            /* [in]   number of threads    */
     tpipe_prfx_t    *tpipe_name      /* [in]   any 4-character name */
     );

     /*  otma_openx- connect to IMS function for OTMA client    */
     /*              and specify the name of IMS DRU0 user exit */
void otma_openx(
     otma_anchor_t   *anchor,         /* [out]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */
     otma_grp_name_t *group_name,     /* [in]   XCF group name       */
     otma_clt_name_t *member_name,    /* [in]   our XCF member name  */
     otma_srv_name_t *partner_name,   /* [in]   IMS XCF member name  */
     signed int *sessions,            /* [in]   number of threads    */
     tpipe_prfx_t    *tpipe_name,     /* [in]   any 4-character name */
     otma_dru_name_t *ims_dru_name,   /* [in]   IMS otma dru exit    */
     otma_profile4_t *special_options /* [in]   special options      */
     );
        /*  otma_alloc  - defines attributes of next msg interchange */

void otma_alloc(
     otma_anchor_t   *anchor,         /* [in]   anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */

     sess_handle_t   *sess_handle,    /* [out]  session id           */
     otma_profile_t  *special_options,/* [in]   special options      */

     tran_name_t     *transaction,    /* [in]   IMS trancode or cmd  */
     racf_uid_t      *username,       /* [in]   RACF userid          */
     racf_prf_t      *prfname         /* [in]   RACF group name      */
     );

        /*  otma_free  - reverse of ALLOC  */

void otma_free(
     otma_anchor_t   *anchor,         /* [in ]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     sess_handle_t   *sess_handle     /* [in]   unique path id       */
     );

        /*  otma_send_receive  - queues data to be sent to IMS       */

void otma_send_receive(
     otma_anchor_t   *anchor,         /* [in ]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */

     sess_handle_t   *sess_handle,    /* [in]   unique path id       */
     lterm_name_t    *lterm,          /* [in,out] Lterm              */
     mod_name_t      *modname,        /* [in,out] MODname            */

     char            *send_buffer,    /* [in]  send buffer           */
     data_leng_t     *send_length,    /* [in]  send buffer length    */
     ioseg_list_t    *send_seg_list,  /* [in]  send multiple segment */
                                      /*       length vector         */

     char            *receive_buffer, /* [in]  receive buffer        */
     data_leng_t     *receive_length, /* [in]  receive buffer length */
     data_leng_t     *received_length,/* [out] data received         */
     ioseg_list_t    *receive_seg_list,/*[in/out] receive multiple   */
                                      /*       segment length vector */

     context_t       *context_id,     /* [in]  RRS context token     */
     char            *error_message   /* [out] msg from IMS @PQ63675 */
     );

        /*  otma_send_receivex - otma_send_receive + otma user data  */

   void otma_send_receivex(
     otma_anchor_t   *anchor,         /* [in ]  anchor               */
     otma_retrsn_t   *retrsn,         /* [out]  rc,reason[1-4]       */
     ecb_t           *ecb,            /* [out]  are we done          */

     sess_handle_t   *sess_handle,    /* [in]   unique path id       */
     lterm_name_t    *lterm,          /* [in,out] Lterm              */
     mod_name_t      *modname,        /* [in,out] MODname            */

     char            *send_buffer,    /* [in]  send buffer           */
     data_leng_t     *send_length,    /* [in]  send buffer length    */
     ioseg_list_t    *send_seg_list,  /* [in]  send multiple segment */
                                      /*       length vector         */

     char            *receive_buffer, /* [in]  receive buffer        */
     data_leng_t     *receive_length, /* [in]  receive buffer length */
     data_leng_t     *received_length,/* [out] data received         */
     ioseg_list_t    *receive_seg_list,/*[in/out] receive multiple   */
                                      /*       segment length vector */

     context_t       *context_id,     /* [in]  RRS context token     */
     char            *error_message,  /* [out] msg from IMS @PQ63675 */
     otma_user_t     *otma_userdata   /* [in,out] OTMA userdata      */
     );

        /*  otma_send_async    - queues data to be sent to IMS with  */
        /*                       8-char user specified tpipe name    */

void otma_send_async(
     otma_anchor_t   *anchor,         /* [in ] anchor                */
     otma_retrsn_t   *retrsn,         /* [out] rc,reason[1-4]        */
     ecb_t           *ecb,            /* [out] are we done?          */

     tpipe_name_t    *async_tpipe_name,/* [in] user tpipe name       */
     tran_name_t     *transaction,    /* [in]  IMS trancode or cmd   */
     racf_uid_t      *username,       /* [in]  RACF userid           */
     racf_prf_t      *prfname,        /* [in]  RACF group name       */
     lterm_name_t    *lterm,          /* [in]  Lterm                 */
     mod_name_t      *modname,        /* [in]  MODname               */
     otma_user_t     *otma_userdata,  /* [in]  OTMA userdata         */

     char            *send_buffer,    /* [in]  send buffer           */
     data_leng_t     *send_length,    /* [in]  send buffer length    */
     ioseg_list_t    *send_seg_list,  /* [in]  send multiple segment */
                                      /*       length vector         */
     char            *error_message,  /* [out] msg from IMS @PQ63675 */
     otma_profile2_t  *special_options/* [in]  special options       */

     );


        /*  otma_receive_async - receive data from IMS with the      */
        /*                       8-char user specified tpipe name    */

void otma_receive_async(
     otma_anchor_t   *anchor,         /* [in ] anchor                */
     otma_retrsn_t   *retrsn,         /* [out] rc,reason[1-4]        */
     ecb_t           *ecb,            /* [out] are we done           */

     tpipe_name_t    *async_tpipe_name,/* [in] user tpipe name       */
     lterm_name_t    *lterm,          /* [out] Lterm                 */
     mod_name_t      *modname,        /* [out] MODname               */
     otma_user_t     *otma_userdata,  /* [out] OTMA userdata         */

     char            *receive_buffer, /* [out] receive buffer        */
     data_leng_t     *receive_length, /* [in]  receive buffer length */
     data_leng_t     *received_length,/* [out] data received         */
     ioseg_list_t    *receive_seg_list,/*[in/out] receive multiple   */
                                      /*       segment length vector */
     otma_profile3_t *special_options  /* [in]  special options      */

     );


        /*  OTMA_CLOSE    - reverse of OPEN                          */

void otma_close(
     otma_anchor_t   *anchor,         /* [in,out] anchor             */
     otma_retrsn_t   *retrsn          /* [out]  rc,reason[1-4]       */
     );

void DFSYCWAT(
     unsigned int  * ecb
     );
#endif
#endif
