/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_SERVER_WOLA_CICS_LINK_SERVER_H
#define _BBOZ_SERVER_WOLA_CICS_LINK_SERVER_H

#pragma pack(1)                                         /* 15@576822*/
 /*******************  BEGIN BBOACGAA CONTROL BLOCK  ****************/
 /******** BELOW THE BAR ** BELOW THE BAR ** BELOW THE BAR ** *******/
 typedef struct BBOACGAA  {
    char            acgaaeye[8];      /*+000  Eye Catcher 'BBOACGAA'*/
    short           acgaaver;         /*+008  Version               */
    short           acgaasiz;         /*+00A  Size of this cb       */
    struct {                          /*+00C  Flags                 */
      int                   :31,      /*+00C Reserved               */
            acgaaflg_avail  :1;       /*+00F.8 Available            */
    } acgaaflags;
    int             acgaa_pcnum;      /*+010  PC number             */
    int             acgaa_tracelvl;   /*+014  Trace level 0|1|2     */
    int             acgaa_tracelvlx;  /*+018  TRUE Exit trace level */
    char            acgaa_ctl_tran[4];/*+01C  Name of BBOACNTL tran */
 };                                   /* 020  Length of BBOACGAA    */
 /*******************  END   BBOACGAA CONTROL BLOCK  ****************/
#pragma pack(reset)

#define BBOATRUE_GALSIZE 4096
#define BBOATRUE_TALSIZE 8192

#pragma pack(1)
 /*******************  BEGIN BBOACSRV CONTROL BLOCK  ****************/
 /******** BELOW THE BAR ** BELOW THE BAR ** BELOW THE BAR ** *******/
 typedef struct BBOACSRV  {
    char            acsrveye[8];      /*+000  Eye Catcher 'BBOACSRV'*/
    short           acsrvver;         /*+008  Version               */
    short           acsrvsiz;         /*+00A  Size of this cb       */
    struct {                          /*+00C  Flags                 */
        int                 :26,      /*+00C Reserved               */
            acsrvflg_retry  :1,       /*+00F.3 Retry start @PI52665A*/
            acsrvflg_rtxp   :1,       /*+00F.4 Rtx propgate@PI52665A*/
            acsrvflg_lsync  :1,       /*+00F.5 SyncOnRetrn @PM70002A*/
            acsrvflg_reuse  :1,       /*+00F.6 Reuse BBO#s  @579234A*/
            acsrvflg_W2Csec :1,       /*+00F.7 Propagate security   */
            acsrvflg_tx     :1;       /*+00F.8 Transactional        */
    } acsrvflags;
    char            acsrv_tasknum[8]; /*+010  CICS BBOC task#       */
    char            acsrv_regname[12];/*+018  Register name         */
    char            acsrv_svcname[8]; /*+024  Service/program name  */
    char            acsrv_linktx[4];  /*+02C  Program LINK transid  */
    int             acsrv_tracelvl;   /*+030  Trace level 0|1|2     */
    char            acsrv_tracetdq[4];/*+034  Trace TDQ name        */
    struct {
      char            acsrv_dgn[8];   /*+038  Daemon group name     */
      char            acsrv_ndn[8];   /*+040  Node name             */
      char            acsrv_svn[8];   /*+048  Server name           */
      unsigned int    acsrv_reuc;     /*+050  Reuse count   @656134A*/
      unsigned int    acsrv_reut;     /*+054  Reuse timeout @656134A*/
    } v2;
    struct {
      char            acsrv_rtx[4];   /*+058 Remote Link Tx @PI52665A*/
      char            acsrv_rtxsys[4];/*+05C Remote SYSID   @PI52665A*/
      int             acsrv_retcnt;   /*+060 Retry Count    @PI52665A*/
      int             acsrv_retint;   /*+064 Retry Interval @PI52665A*/
    } v3;
 };                                   /* 068  Length of BBOACSRV    */
 /*******************  END   BBOACSRV CONTROL BLOCK  ****************/
#pragma pack(reset)

#define BBOACSRV_V1 1
#define BBOACSRV_V2 2
#define BBOACSRV_V3 3

#pragma pack(1)
 /*******************  BEGIN BBOACLNK CONTROL BLOCK  ****************/
 /******** BELOW THE BAR ** BELOW THE BAR ** BELOW THE BAR ** *******/
 /*******************************************************************/
 /* WARNING: When changing the offsets, changes should also be      */
 /*          made to assembler mapping in bboaclnk.mac              */
 /*******************************************************************/

 typedef struct BBOACLNK  {
    char            aclnkeye[8];      /*+000  Eye Catcher 'BBOACLNK'*/
    short           aclnkver;         /*+008  Version               */
    short           aclnksiz;         /*+00A  Size of this cb       */
    struct {                          /*+00C  Flags                 */
      int                      :23,   /*+00C  Reserved              */
            aclnkflg_rtxp      :1,    /*+00E.8 Rmt TX prop.@PI52665A*/
            aclnkflg_lsync     :1,    /*+00F.1 SyncOnRetrn @PM70002A*/
            aclnkflg_channel   :1,    /*+00F.2 Pass channel         */
            aclnkflg_reuse     :1,    /*+00F.3 Reuse BBO#s  @579234A*/
            aclnkflg_commarea  :1,    /*+00F.4 Pass commarea        */
            aclnkflg_container :1,    /*+00F.5 Pass container       */
            aclnkflg_W2Csec    :1,    /*+00F.6 Propagate security   */
            aclnkflg_tx        :1,    /*+00F.7 Reg'd transactional  */
            aclnkflg_globaltx  :1;    /*+00F.8 WAS app in global tx */
    } aclnkflags;
    char         aclnk_tasknum[8];    /*+010  CICS BBO$ task#       */
    char         aclnk_regname[12];   /*+018  Register name         */
    char         aclnk_connhdl[12];   /*+024  Client conn. handle   */
    char         aclnk_servname[8];   /*+030  Service/program name  */
    char         aclnk_reqcontainerid[16];/*+038  Req. cont. name   */
    int          aclnk_reqcontainertype;  /*+048  Req. cont. type   */
    char         aclnk_rspcontainerid[16];/*+04C  Rsp. Cont. name*/
    int          aclnk_rspcontainertype;  /*+05C  Req. cont. type   */
    unsigned int aclnk_reqdatalen;    /*+060  Request data length   */
    int          aclnk_tracelvl;      /*+064  Trace level 0|1|2     */
    char         aclnk_tracetdq[4];   /*+068  Trace TDQ name        */
    char         aclnk_bboc_svcname[8];/*+06C BBOC SVCname  @579234A*/
    char         aclnk_rtx[4];        /*+074 Rem. Link Tx  @PI52665A*/
    char         aclnk_rtxsys[4];     /*+078 Rem. SYSID    @PI52665A*/
    unsigned char _rsvd1[8];          /*+07C Free          @PI52665C*/
    struct {                                                           /* @F003691A*/
      int aclnk_tx_formatid;          /*+084  XID format ID         */ /* @F003691A*/
      int aclnk_tx_tidlen;            /*+088  XID tid length        */ /* @F003691A*/
      int aclnk_tx_bquallen;          /*+08C  XID bqual length      */ /* @F003691A*/
      char aclnk_tx_tid[128];         /*+090  XID TID               */ /* @F003691A*/
      char aclnk_tx_urid[16];         /*+110  Caller's RRS URID     */ /* @F003691A*/
      char aclnk_tx_urtok[16];        /*+120  Caller's RRS UR Token */ /* @F003691A*/
      struct {                                                         /* @F003691A*/
        int aclnkflg_tx_unused :26,   /*+130  TX Flags              */ /* @F003691A*/
            aclnkflg_tx_tidlist :1,   /*+131.3 Get list of TIDs     */ /* @F003691A*/
            aclnkflg_tx_rec     :1,   /*+131.4 Recovery work        */ /* @F003691A*/
            aclnkflg_tx_inactive:1,   /*+131.5 Inactivity timer pop */ /* @F003691A*/
            aclnkflg_tx_prepare: 1,   /*+131.6 Prepare request      */ /* @F003691A*/
            aclnkflg_tx_commit : 1,   /*+131.7 Commit request       */ /* @F003691A*/
            aclnkflg_tx_backout: 1;   /*+131.8 Backout request      */ /* @F003691A*/
      } aclnk_tx_flags;                                                /* @F003691A*/
    } aclnk_tx_data;                                                   /* @F003691A*/
    unsigned char aclnk_conn_svr[27]; /*+134  DGN/NDN/SVN null term */ /* @F003691A*/
    unsigned char _rsvd2[5];          /*+14F  Free                  */ /* @F003691A*/
    unsigned int aclnk_reuc;          /*+154  Reuse count   @656134A*/
    unsigned int aclnk_reut;          /*+158  Reuse timeout @656134A*/
    char aclnk_corr_id[64];           /*+15C  Correlator ID         */ /* @711599A*/
    char aclnk_corr_data1[64];        /*+160  Correlator data 1     */ /* @711599A*/
    char aclnk_corr_data2[64];        /*+1A0  Correlator data 2     */ /* @711599A*/
    char aclnk_corr_data3[64];        /*+200  Correlator data 3     */ /* @711599A*/
 };                                   /* 240  Length of BBOACLNK    */ /* @711599C*/
 /*******************  END   BBOACLNK CONTROL BLOCK  ****************/
#pragma pack(reset)

#define BBOACLNK_V1 1
#define BBOACLNK_V2 2
#define BBOACLNK_V3 3                                                  /* @711599A*/
#define BBOACLNK_V4 4                                                 /* @PI52665A*/

#pragma pack(1)
 /*******************  BEGIN BBOAUSEC CONTROL BLOCK  ****************/
 /******** BELOW THE BAR ** BELOW THE BAR ** BELOW THE BAR ** *******/
 typedef struct BBOAUSEC
 {
   unsigned char  auseceye[8];        /*+000  Eye Catcher 'BBOAUSEC'*/
   unsigned short ausecver;           /*+008  Version               */
   unsigned short _rsvd1;             /*+00A                        */
    struct {                          /*+00C  Flags                 */
      int                      :31,   /*+00C  Reserved              */
            ausecflg_propsec   :1;    /*+00F.8 Propagate sec. ctx On*/
    } ausecflags;
   unsigned char  ausecuserid[8];     /*+010  Propagated userid     */
   unsigned char  _rsvd2[24];         /*+018  Free                  */
 };                                   /* 030  Length of BBOAUSEC    */
 /*******************  END   BBOAUSEC CONTROL BLOCK  ****************/
#pragma pack(reset)

#define BBOAUSEC_EYE "BBOAUSEC"
#define BBOAUSEC_VER_1 1

#pragma pack(1)
 /*******************  BEGIN BBOAUTXN CONTROL BLOCK  ****************/
 /******** BELOW THE BAR ** BELOW THE BAR ** BELOW THE BAR ** *******/
 typedef struct BBOAUTXN
 {
   unsigned char  autxneye[8];        /*+000  Eye Catcher 'BBOAUTXN'*/
   unsigned short autxnver;           /*+008  Version               */
   unsigned short _rsvd1;             /*+00A                        */
    struct {                          /*+00C  Flags                 */
      int                      :25,   /*+00C  Reserved              */ /* @F003691C*/
            autxnflg_tidlist   :1,    /*+00F.2 Get list of TIDs     */ /* @F003691A*/
            autxnflg_rec       :1,     /*+00F.3 Recovery work        */ /* @F003691A*/
            autxnflg_prepare   :1,    /*+00F.4 Prepare signal rcvd  */ /* @F003691A*/
            autxnflg_inact_rb  :1,    /*+00F.5 Rollback result of tx*/ /* @F003691A*/
                                      /*        inactivity timer    */ /* @F003691A*/
            autxnflg_rollback  :1,    /*+00F.6 Rollback signal rcvd */
            autxnflg_commit    :1,    /*+00F.7 Commit signal rcvd   */
            autxnflg_globaltx  :1;    /*+00F.8 WAS app in global txn*/
    } autxnflags;
   unsigned char  _rsvd2[16];         /*+010  Free                  */
 };                                   /* 020  Length of BBOAUTXN    */
 /*******************  END   BBOAUTXN CONTROL BLOCK  ****************/
#pragma pack(reset)

#define BBOAUTXN_EYE "BBOAUTXN"
#define BBOAUTXN_VER_1 1

#pragma pack(1)
 /*******************  BEGIN BBOAUCIC CONTROL BLOCK  ****************/
 /******** BELOW THE BAR ** BELOW THE BAR ** BELOW THE BAR ** *******/

 /* NOTE: if you change this struct, update the code in bbgajni.cpp
          that builds this area for the SMF record.
          If you change the size then you will need to update the
          SMF record and SmfSt10P2CICSContext in bboosmfo.mac
          so bbooboam.plx will accepdt the parms            @F003701-36909A*/
 typedef struct BBOAUCIC
 {
   unsigned char  aucicseye[8];       /*+000  Eye Catcher 'BBOAUCIC'*/
   unsigned short aucicsver;          /*+008  Version               */
   unsigned short _rsvd1;             /*+00A                        */
    struct {                          /*+00C  Flags                 */
      int                      :29,   /*+00C  Reserved              */
            aucicsflg_channel   :1,   /*+00F.6 Pass channel to pgm  */
            aucicsflg_commarea  :1,   /*+00F.7 Pass commarea to pgm */
            aucicsflg_container :1;   /*+00F.8 Pass container to pgm*/
    } aucicsflags;
   unsigned char  aucicslnktranid[4]; /*+010  Link transaction id   */
   unsigned char  aucicslnkreqcontid[16];/*+014  Name of req. cont. */
   int            aucicslnkreqconttype;  /*+024  Req. cont. type    */
   unsigned char  aucicslnkrspcontid[16];/*+028  Name of rsp. cont. */
   int            aucicslnkrspconttype;  /*+038  Rsp. cont. type    */
   unsigned char  _rsvd2[20];         /*+03C  Free                  */
 };                                   /* 050  Length of BBOAUCIC    */
 /* NOTE: if you change this struct, update the code in bbgajni.cpp
          that builds this area for the SMF record.
          If you change the size then you will need to update the
          SMF record and SmfSt10P2CICSContext in bboosmfo.mac
          so bbooboam.plx will accepdt the parms            @F003701-36909A*/

 /*******************  END   BBOAUCIC CONTROL BLOCK  ****************/
#pragma pack(reset)

#define BBOAUCIC_EYE "BBOAUCIC"
#define BBOAUCIC_VER_1 1

#pragma pack(1)
 /*******************  BEGIN BBOAUCTX CONTROL BLOCK  ****************/
 /******** BELOW THE BAR ** BELOW THE BAR ** BELOW THE BAR ** *******/
 typedef struct BBOAUCTX  {
    char            auctxeye[8];      /*+000  Eye Catcher 'BBOAUCTX'*/
    short           auctxver;         /*+008  Version               */
    short           auctxsiz;         /*+00A  Size of this cb       */
    struct {                          /*+00C  Flags                 */
      int                      :31,   /*+00C  Reserved              */
            auctxflg_8         :1;    /*+00F.8                      */
    } auctxflags;
    unsigned int    auctxsecoffs;     /*+010  Offs to security sect */
    unsigned int    auctxtxnoffs;     /*+014  Offs to trans. sect.  */
    unsigned int    auctxcicsoffs;    /*+018  Offs to CICS section  */
    unsigned int    auctxvaroffs;     /*+01C  Offs to var len sec.  */ /* @F003691C*/
    unsigned int    _rsvd2;           /*+020  Free                  */
    unsigned int    _rsvd3;           /*+024  Free                  */
    unsigned int    _rsvd4;           /*+028  Free                  */
    unsigned int    _rsvd5;           /*+02C  Free                  */
    unsigned char   _rsvd6[16];       /*+030  Free                  */
    struct BBOAUSEC auctxsecdata;     /*+040  Security data         */
    struct BBOAUTXN auctxtxndata;     /*+070  Transactional data    */
    struct BBOAUCIC auctxcicsdata;    /*+090  CICS Link data        */
 };                                   /* 0E0  Length of BBOAUCTX    */
 /*******************  END   BBOAUCTX CONTROL BLOCK  ****************/
#pragma pack(reset)

#define BBOAUCTX_EYE "BBOAUCTX"
#define BBOAUCTX_VER_1 1

#pragma pack(1)

/*-------------------------------------------------------------------*/
/* OLA Context area header                                           */
/*-------------------------------------------------------------------*/
typedef struct bboactx
{
  unsigned char  actxteye[8];        /*+000  Eye Catcher 'BBOACTX'   */
  unsigned short actxtver;           /*+008  Version                 */
  unsigned short actxflags;          /*+00A  Flags                   */
  int            actxtnum;           /*+00C  Number of contexts      */
                                     /*+010  Start of data           */
};

#define BBOACTX_ID "BBOACTX "                            /* @F003691A*/

/*-------------------------------------------------------------------*/
/* OLA Context header                                                */
/*-------------------------------------------------------------------*/
typedef struct bboactxh
{
  unsigned char  actxheye[8];        /*+000  Eye Catcher             */
  unsigned int   actxhid;            /*+008  4-byte context ID       */
  unsigned int   actxhlen;           /*+00C  Length of context data, */
                                     /*      not including this hdr  */
                                     /*+010  Start of data           */
};

#define BBOATXC_Identifier 1
#define BBOASEC_Identifier 2
#define BBOAWLMC_Identifier 3
#define BBOACORC_Identifier 4                             /* @F003701-36909@*/

#define BBOACORC_ID "BBOACORC"                            /* @F003701-36909A*/

#define BBOATXC_ID "BBOATXC "                            /* @F003691A*/

/*-------------------------------------------------------------------*/
/* OLA Transaction context version 1 data                            */
/*-------------------------------------------------------------------*/
typedef struct bboatxc_V1
{
  unsigned char atxcurid[16];        /*+000   RRS URID               */
  unsigned char atxcurtok[16];       /*+010   RRS UR Token           */
  unsigned int  atxcxidfid;          /*+020   XID Format ID          */
  unsigned int  atxcxidgtlen;        /*+024   XID Gtrid Length       */
  unsigned int  atxcxidbqlen;        /*+028   XID Bqual Length       */
  unsigned char atxcxiddata[128];    /*+02C   XID Gtrid/Bqual        */
                                     /*+0AC                          */
};

/*-------------------------------------------------------------------*/
/* OLA Transaction context                                           */
/*-------------------------------------------------------------------*/
typedef struct bboatxc
{
  struct bboactxh atxchdr;           /*+000   Header                 */
  unsigned short  atxcver;           /*+010   Version                */
  unsigned short  _rsvd1;            /*+012   Available (bits?)      */
  struct bboatxc_V1 atxcver1;        /*+014   Version 1 data         */
                                     /*+0C0   Length BBOATXC         */
};

/* beging @F003701-36909A*/
#define BBOACORC_ADAPTER_ID "ID=WebSphere Application Server for z/OS"
#define BBOACORC_DATA1_CELL_ID "CELL="
#define BBOACORC_DATA1_NODE_ID "NODE="
#define BBOACORC_DATA1_CLUSTER_ID "CLUSTER="
#define BBOACORC_DATA1_SERVER_ID "SERVER="
#define BBOACORC_DATA2_ID   "INSTANCE="

typedef struct bboacorctxCorelatorData1
{
  unsigned char acorctxCorelatorData1CellId[5];
  unsigned char acorctxCorelatorData1Cell[8];
  unsigned char acorctxCorelatorData1NodeId[5];
  unsigned char acorctxCorelatorData1Node[8];
  unsigned char acorctxCorelatorData1ClusterId[8];
  unsigned char acorctxCorelatorData1Cluster[8];
  unsigned char acorctxCorelatorData1ServerId[7];
  unsigned char acorctxCorelatorData1Server[8];
  unsigned char acorctxCorelatorData1Filler[7];
};

typedef struct bboacorctxCorelatorData2
{
  unsigned char acorctxCorelatorData2Id[9];
  unsigned char acorctxCorelatorData2Ttoken[16];
  unsigned char acorctxCorelatorData2Stcke[16];
  unsigned char acorctxCorelatorData2Filler[23];
};

typedef struct bboacorctx_V1
{
  unsigned char acorctxAdapterIdentifier[64]; /* Adapter Identifier    */
  struct bboacorctxCorelatorData1 acorctxCorelatorData1;
  struct bboacorctxCorelatorData2 acorctxCorelatorData2;
  unsigned char acorctxCorelatorData3[64];
};

/*-------------------------------------------------------------------*/
/* OLA CICS correlator context                                       */
/*-------------------------------------------------------------------*/
typedef struct bboacorctx
{
  struct bboactxh acorctxchdr;       /*+000   Header                 */
  unsigned short  acorctxver;        /*+010   Version                */
  unsigned short  _rsvd1;            /*+012   Available (bits?)      */
  struct bboacorctx_V1 acorctxcver1; /*+014   Version 1 data         */
                                     /*+114   Length bboacorctx      */
};

/* end @F003701-36909A*/


/*-------------------------------------------------------------------*/
/* OLA WLM context version 1 data                                    */
/*-------------------------------------------------------------------*/
typedef struct bboawlmc_V1
{
  unsigned int awlmcpbt:1;           /*+000.0 Has PBToken            */
  unsigned int awlmctxn:1;           /*+000.1 Has Txn name           */
  unsigned int awlmcbits:30;         /*+000.2 Avail bits             */
  unsigned int awlmc_pbt;            /*+004   PB Token               */
  unsigned char awlmc_txn[8];        /*+008   Txn Name               */
                                     /*+010                          */
};

/*-------------------------------------------------------------------*/
/* OLA WLM Context                                                   */
/*-------------------------------------------------------------------*/
typedef struct bboawlmc
{
  struct bboactxh awlmchdr;          /*+000   Header                 */
  unsigned short  awlmcver;          /*+010   Version                */
  unsigned short  _rsvd1;            /*+012   Available (bits?)      */
  struct bboawlmc_V1 awlmcver1;      /*+014   Version 1 data         */
                                     /*+024   Length BBOAWLMC        */
};


#define TRACE_DATA_ATXC(data_p) TRACE_DATA_RAWDATA(sizeof(bboatxc::atxchdr), \
                      &((data_p)->atxchdr), \
                      "Header"), \
    TRACE_DATA_SHORT((data_p)->atxcver, "Version"), \
    TRACE_DATA_RAWDATA(sizeof(bboatxc_V1::atxcurid), \
                       (data_p)->atxcver1.atxcurid, \
                       "URID"), \
    TRACE_DATA_RAWDATA(sizeof(bboatxc_V1::atxcurtok), \
                       (data_p)->atxcver1.atxcurtok, \
                       "UR Token"), \
    TRACE_DATA_HEX_INT((data_p)->atxcver1.atxcxidfid, "XID Format ID"), \
    TRACE_DATA_HEX_INT((data_p)->atxcver1.atxcxidgtlen, "XID Gtrid Len"), \
    TRACE_DATA_HEX_INT((data_p)->atxcver1.atxcxidbqlen, "XID Bqual Len"), \
    TRACE_DATA_RAWDATA((data_p)->atxcver1.atxcxidgtlen + \
                       (data_p)->atxcver1.atxcxidbqlen, \
                       (data_p)->atxcver1.atxcxiddata, \
                       "XID TID")

/*-------------------------------------------------------------------*/
/* XID mapping                                                       */
/*-------------------------------------------------------------------*/
typedef struct bboaxid                                   /* @F003691A*/
{                                                        /* @F003691A*/
  unsigned int formatid;                                 /* @F003691A*/
  unsigned int gtridlen;                                 /* @F003691A*/
  unsigned int bquallen;                                 /* @F003691A*/
  char tid[128];                                         /* @F003691A*/
};                                                       /* @F003691A*/

/*-------------------------------------------------------------------*/
/* STOKEN and connection ID                                          */
/*-------------------------------------------------------------------*/
typedef struct bboastoid                                 /* @F003691M*/
{
  int            _assbstyp;          /*+000  Stoken                  */
  int            _assbisqn;          /*+004  Address Space ID        */
  unsigned int   stoidid;            /*+008  Connection ID           */
                                     /*+00A  Length of bboastoid     */
};

/*-------------------------------------------------------------------*/
/* CICS Transaction Notification Message                             */
/*-------------------------------------------------------------------*/
typedef struct bboatrmg
{
  struct bboastoid atrmgconnid;      /*+000 Full Connection ID       */ /* @F003691C*/
  unsigned int  atrmgflg_rsv1:2;    /*+00C Reserved Flags           */ /* @F003691C*/
  unsigned int  atrmgflg_recovery:1;/*+00C Recovery flag set in CR  */ /* @F003691A*/
  unsigned int  atrmgflg_gettid:1;  /*+00C Get TIDs Flag            */ /* @F003691A*/
  unsigned int  atrmgflg_global:1;  /*+00C Global transaction       */ /* @F003691A*/
  unsigned int  atrmgflg_backout:1; /*+00C Backout flag             */ /* @F003691A*/
  unsigned int  atrmgflg_prepare:1; /*+00C Prepare Flag             */ /* @F003691C*/
  unsigned int  atrmgflg_commit:1;  /*+00C Commit Flag              */
  unsigned int  atrmgflg_rsv2[24];  /*+00D Reserved Flags           */ /* @F003691C*/
  struct bboaxid atrmgxid;           /*+010 XID                      */ /* @F003691A*/
};

/*-------------------------------------------------------------------*/
/* CICS Transaction Notification Response                            */
/* This must be kept common with tWAS to facilitate transaction      */
/* protocol messages.  It is bracketed with __cplusplus because the  */
/* metal C compiler doesn't let a char field be a bitfield.  This is */
/* only important for the link server itself, which is C++.          */
/*-------------------------------------------------------------------*/
#ifdef __cplusplus
typedef struct bboatrsp                                  /* @F003691A*/
{
  unsigned char  atrspeye[8];        /*+000  Eye Catcher 'BBOATRSP'  */
  unsigned short atrspver;           /*+008  Version                 */
  unsigned char  _rsvd1;             /*+00A                          */
  unsigned char  atrspflg_ok:1;      /*+00B  OK Flag                 */
  unsigned char  atrspflg_readonly:1;/*+00B  Read-only flag.         */
  unsigned char  atrspflg_backout:1; /*+00B  Backout flag            */
  unsigned char  atrspflg_heur:1;    /*+00B  Heuristic flag          */
  unsigned char  atrspflg_complete:1;/*+00B  TX Complete             */
  unsigned char  atrspflg_nota:1;    /*+00B  TID not found           */ /* @F003691A*/
  unsigned char  atrspflg_recovery:1;/*+00B  Processed by a          */ /* @F003691A*/
                                     /*       recovery task          */ /* @F003691A*/
  unsigned char  atrspflg_state_err:1;/*+00B Recovery TID is not     */ /* @F003691A*/
                                     /*       shunted.               */ /* @F003691A*/
  unsigned int   atrsp_msglen;       /*+00C  Message length          */
  char           atrsp_msg[128];     /*+010  Message (null term.)    */
  unsigned int   atrsp_num_tid;      /*+090  Number of TIDs if rec   */ /* @F003691A*/
                                     /*+094  TIDs follow.            */
                                     /*+???                          */
};
#endif

/*-------------------------------------------------------------------*/
/* BBOA1INF INFOGET connection state                                 */
/*-------------------------------------------------------------------*/
typedef struct bboaconn_state
{
  int aconn_state1;
  int aconn_state2;
};

/*-------------------------------------------------------------------*/
/* BBOA1INF INFOGET active connections                               */
/*-------------------------------------------------------------------*/
typedef struct bboaconn_active
{
  int aconn_active_p1;
  int aconn_active_p2;
};

/*-------------------------------------------------------------------*/
/* BBOA1INF INFOGET returned connection info                         */
/*-------------------------------------------------------------------*/
typedef struct bboaconn
{
  char aconn_eye[8];                 /*+000   Header                 */
  short aconn_version;               /*+008   Version                */
  short aconn_size;                  /*+00A   Size                   */
  unsigned int aconn_reserved;       /*+00C   pad                    */
  unsigned int aconn_min;            /*+010   Min connections        */
  unsigned int aconn_max;            /*+014   Max connections        */
  struct bboaconn_state aconn_info_state;   /*+018   Connection state       */
  struct bboaconn_active aconn_info_active; /*+020   Number active          */
  char   aconn_reserved2[128];       /*+028   Reserve                */
                                     /*+0A8   Size of bboaconn       */
};

#define BBOATRSP_EYE "BBOATRSP"                          /* @F003691A*/
#define BBOATRSP_VER_1 1                                 /* @F003691A*/
#define BBOARQTYPE_1_LOCAL_EJB_INV  1                   /*@F003703.2A*/
#define BBOARQTYPE_2_REMOTE_EJB_INV 2                   /*@F003703.2A*/
#pragma pack(reset)

#endif /* _BBOZ_SERVER_WOLA_CICS_LINK_SERVER_H */
