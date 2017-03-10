#pragma nosequence nomargins
#ifndef BBO_ANGEL_BGVT_SERVICES_H
#define BBO_ANGEL_BGVT_SERVICES_H
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

/*-------------------------------------------------------------------*/
/**@file
  Mapping of the BGVT
*/
#include <metal.h>
#include <string.h>

#include "gen/cvt.h"
#include "gen/ihapsa.h"
#include "gen/ihaecvt.h"

/*-------------------------------------------------------------------*/
#pragma pack(1)

typedef struct bgvt bgvt;
struct bgvt {
  unsigned char  bbodbgvt_eyecatcher[8];            /* Dataarea's eyecatcher                     */
  int            bbodbgvt_version;                  /* Dataarea's version                        */
  void * __ptr32 bbodbgvt_pctable;                  /* Pointer to the system          @266987.M1C*/
  struct {
    int            _sequence_number;
    void * __ptr32 _asr_pointer;                                                   /* @266987.M1C*/
    } bbodbgvt_asr;
  void * __ptr32 bbodbgvt_old_asr;                                                 /* @266987.M1C*/
  void * __ptr32 bbodbgvt_bmdt;                     /* Pointer to Boss Daemon         @266987.M1C*/
  void * __ptr32 bbodbgvt_estae_extension;          /* Pointer to                     @266987.M1C*/
  void * __ptr32 bbodbgvt_daemon_ior;               /* Pointer to Daemon IOR          @266987.M1C*/
  void * __ptr32 bbodbgvt_daemon_ascb;              /* Pointer to Daemon ASCB         @266987.M1C*/
  void * __ptr32 bbodbgvt_frr;                      /* Pointer to general FRR         @266987.M1C*/
  void * __ptr32 bbodbgvt_admp;                     /* Pointer to general ARR         @266987.M1C*/
  int            bbodbgvt_ctrace_define_done   : 1, /* Ctrace define done for top level          */
                 bbodbgvt_deleted2             : 1, /*                                  @WS14419C*/
                 bbodbgvt_no_pbs :1,                /* no PB reporting                       @LDC*/
                 bbodbgvt_modules_loaded_to_common : 1, /*                              @WS14419A*/
                 bbodbgvt_glue_rtn_to_estae    : 1, /* if on
                                                       BBODBGVT_ESTAE_EXTENSION points
                                                       to a glue routine                @MD15365A*/
                 bbodbgvt_pcrt_loaded_common : 1,   /* pcrt in common directed load    @LI4175.2A*/
                 bbodbgvt_multi_cell_mode    : 1,                                   /*  @L413463A*/
                 bbodbgvt_pcrt_key0          : 1,   /* pcrt in key 0 storage         @LI4689-1.1A*/
                 bbodbgvt_prev_pcrt_key0     : 1, /* prev pcrt in key 0 storage@LI4689-1.1A*/
                 bboodbgvt_noswap_in_ppt     : 1, /*  BPXBATA2 PPT entry has noswap      @489686A*/
                                               : 22;                                /*   @489686C*/
  void * __ptr32 bbodbgvt_bbortsrb;                 /* Pointer to TRACE SRB ROUTINE   @266987.M1C*/
  void * __ptr32 bbodbgvt_bborlcrm;              /* Ptr to local communication resmgr @266987.M1C*/
  int            bbodbgvt_daemon_resmgr_token;      /* Daemon                                    */
  unsigned char  bbodbgvt_daemon_resmgr_parm[8];    /* Parameters                                */
  int            daemon_active                 : 1, /* The Daemon is active                      */
                 daemon_starting               : 1, /* The Daemon is starting                    */
                 daemon_failed                 : 1, /* The Daemon has failed                     */
                 daemon_stopped                : 1, /* The Daemon has stopped                    */
                 daemon_data_sharing           : 1, /* data sharing supported                @P1A*/
                 daemon_stopping               : 1, /* The Daemon is stopping                @P7A*/
                                              : 26; /*                                       @P1C,@P7C*/
  void * __ptr32 bbodbgvt_bborrmgr;                 /* Pointer to res manager         @266987.M1C*/
  void * __ptr32 bbodbgvt_daemon_ior_ascii;         /* Pointer to daemon ascii IOR    @266987.M1C*/
  unsigned int   bbodbgvt_sid_cpool_v2;             /* Cpool V2 for server instance    @TODO0162C*/
  void * __ptr32 bbodbgvt_bbooschd;                 /* Ptr to SCHED frontend rtn      @266987.M1C*/
  void * __ptr32 bbodbgvt_bbotetcn;                 /* OTS rtn to perform ETCON       @266987.M1C*/
  void * __ptr32 bbodbgvt_bbotetds;                 /* OTS rtn to perform ETDIS       @266987.M1C*/
  void * __ptr32 bbodbgvt_bboosrqa;                 /* SRB to queue SR ACRW           @266987.M1C*/
  void * __ptr32 bbodbgvt_bbotetrm;                 /* OTS RMTR routine               @266987.M1C*/
  unsigned int  bbodbgvt_ORBR_ID;                  /* ORB_Request ID (4-bytes)              @LAA*/
  void * __ptr32 bbodbgvt_ORBR_SF_CP_Info;          /* ORB_Request State Flag Cell Pool
                                                                                 Info @266987.M1C*/
  void * __ptr32 bbodbgvt_naming_ior;               /* Naming Binary IOR              @266987.M1C*/
  void * __ptr32 bbodbgvt_ir_ior;                   /* Interface Repository IOR       @266987.M1C*/
  void * __ptr32 bbodbgvt_sm_smo_ior;             /* Systems Management Server SMO IOR@266987.M1C*/
  char           bbodbgvt_daemon_ipname[256];       /* Daemon IP Name from env variable      @P4C*/
  char           bbodbgvt_resolve_ipname[256];      /* Resolve IP Name from env variable     @P5C*/
  unsigned int  bbodbgvt_srb_parm_cpool;           /* cpool for srb parameter list          @P6A*/
  unsigned int  bbodbgvt_srbf_dyna_cpool;          /* cpool for srbf dynamic area           @P8A*/
  unsigned char  bbodbgvt_deleted1[2];              /*                                  @WS14419C*/
  unsigned char  bbodbgvt_deleted[8];               /*                                  @WS14419C*/
  unsigned short bbodbgvt_srb_parm_cpool_ver;       /* cpool for SRB Parmlist version   @PQ62702A*/
  long long      bbodbgvt_bboaocnp;                 /* OTMA name table                   @645942A*/
  unsigned int bbodbgvt_bboamcst_count;             /* @F003703A*/
  void * __ptr32 bbodbgvt_bboamcst_ptr;             /* @F003703A*/ /* @PM77869 */
  void* __ptr32  bbodbgvt_bbgzcgoo;                 /* Lexington Common Server GOO       @xxxxxxA*/
  void * __ptr32 bbodbgvt_bbgzachk;                  /* The entry point of the angel check module
                                                       loaded by the angel, as reported by 
                                                       BPX4LDX.                                  */
  int            bbodbgvt_bbgzachk_version;          /* The version of the angel check module    */
  /* !!! NOTE !!! when adding new fields, ensure consistency with bgvt mapping in tWAS! */
  unsigned char  bbodbgvt_rsvd[52];                 /* Reserved space for 3.0 service   @RTC227529C*/
  void * __ptr32 BBODBGVT_BBOOPCS_HEAD_PTR;         /* BBOOPCRT stack head              @PM46552A*/
  unsigned int   BBODBGVT_BBOOPCS_SEQUENCE;         /* BBOOPCRT stack sequence          @PM46552A*/
  unsigned char  bbodbgvt_rsvd1[4];                 /* Reserved space for service       @PM46552A*/
  unsigned char  BBODBGVT_FRCA_CsvdylpaDeleteToken[8]; /* token needed when taking frca dll
                                                       out of lpa                       @PM22959A*/
  unsigned char  BBODBGVT_TS70_CsvdylpaDeleteToken[8]; /* token needed when taking bborts70
                                                       out of lpa                       @PM22959A*/
  unsigned char  bbodbgvt_pcrt_csvdylpadeletetoken[8]; /* token needed when taking pcrt out of
                                                       lpa                              @PK77358A*/
  void * __ptr32 bbodbgvt_state_change_map;         /*                                  @PK77358A*/
  unsigned int   bbodbgvt_pcrt_length;              /* pcrt length                     @LI4175.2A*/
  unsigned int   bbodbgvt_prev_pcrt_length;         /* previous pcrt length            @LI4175.2A*/
  void * __ptr32 bbodbgvt_prev_pcrt;                /* previous pcrt ptr               @LI4175.2A*/
  void * __ptr32 bbodbgvt_pcq_ptr;                  /* PCQ pointer                     @LI4175.2A*/
  unsigned int   bbodbgvt_pcqe_cpool_version;       /* PCQE cpool id version           @LI4175.2A*/
  unsigned int   bbodbgvt_pcqe_cpool;               /* PCQE cpool id                   @LI4175.2A*/
  void * __ptr32 BBODBGVT_PRE_V61_BBORLEXT_PTR;      /* address of
                                                       BBORLEXT used for versions
                                                       earlier than V61                @LI4175.2A*/
  void * __ptr32 BBODBGVT_PCRT;                     /* daemon PC routine table         @LI4175.2A*/
  void * __ptr32 bbodbgvt_bbocwsmp;                 /* Pointer to WLM Sampling Exit   @266987.M1C*/
                                                    /* (bbocwsmp.plx)                   @PQ72135A*/
  void * __ptr32 bbodbgvt_bbocwbal;                 /* Pointer to WLM Rebalance Exit  @266987.M1C*/
                                                    /* (bbocwbal.plx)                   @PQ72135A*/
  void * __ptr32 bbodbgvt_bbortrcd;                 /* Ptr to PL/X trace routine      @266987.M1C*/
  unsigned int   bbodbgvt_sid_cpool;                /* SID cpool ID -- exists to be compatible
                                                       with pre-V8 GA level code       @TODO0162C*/
  unsigned int   bbodbgvt_sid_cpool_version;        /* bbodbgvt_sid_cpool_v2 Cpool ver @TODO0162C*/
  char           bbodbgvt_daemon_group_name[8];     /* Identify daemon group. Max 8 chars.
                                                       Null terminated IF LESS than 8 chars. @WS14419A*/
  void * __ptr32 bbodbgvt_common_bmdt_ptr;          /*                                @266987.M1C*/
  void * __ptr32 bbodbgvt_old_bmdt_ptr;             /*                                @266987.M1C*/
  int            bbodbgvt_daemon_routing;           /* smart routing: 0=wlm, 1=none, 2=daemon @WS14419A */
  void * __ptr32 bbodbgvt_bborlext_ptr;             /* estae extension                @266987.M1C*/
  unsigned int  bbodbgvt_srb_parm_cpool2;          /* cpool for srb parameter list     @MD15365A*/
  char * __ptr32 bbodbgvt_security_realm_name;      /* SAF Realm name                 @266987.M1C*/
  void * __ptr32 bbodbgvt_bbodasrs;                 /* asr services                   @266987.M1C*/
  void * __ptr32 bbodbgvt_bboushqd;                /* Ptr to bboushqe.bboushqeDestroy @266987.M1C*/
                                                    /* routine (bboushqd.plx)                @MD14685A*/
  unsigned int   bbodbgvt_xcf_status_field_cpool;   /* DCS XCF status field cpool            @LI4310A*/
  long long      bbodbgvt_bboashr;                 /* New connector shared memory area  @LI4798I7A*/
  void * __ptr32 bbodbgvt_extension;                /* not set yet. this is to be used
                                                       to point to an extension if we
                                                       ever need more room in the bgvt
                                                       that hangs off the ecvt.  Since
                                                       multiple 5.0 nodes and 401 will
                                                       be sharing the bgvt there is no
                                                       easy safe way to delete the
                                                       bgvt hung off the ecvt. Note in
                                                       4.01 and 5.0 LE uses the
                                                       BBODBGVT_ESTAE_EXTENSION field
                                                       of the bgvt pointed to by the
                                                       ecvt.                          @266987.M1C*/
  };

#define sequence_number bbodbgvt_asr._sequence_number
#define asr_pointer     bbodbgvt_asr._asr_pointer

#pragma pack(reset)


/*-------------------------------------------------------------------*/
/* BGVT utility functions                                            */
/*-------------------------------------------------------------------*/
/**
 * Gets a reference to the global BGVT.
 *
 * @return A pointer to the BGVT, or NULL if not found.
 */
#pragma inline(findBGVT)
static bgvt* __ptr32 findBGVT(void) {
    psa*  psa_p = NULL;
    cvt*  cvt_p = (cvt* __ptr32) psa_p->flccvt;
    ecvt* ecvt_p = (ecvt* __ptr32) cvt_p->cvtecvt;
    return (bgvt* __ptr32) ecvt_p->ecvtbcba;
}

/**
 * Gets a reference to the global BGVT.  If no global BGVT exists, one
 * will be created.
 *
 * @return A pointer to the BGVT, or NULL if one could not be created.
 */
bgvt* __ptr32 findOrCreateBGVT(void);

#endif
