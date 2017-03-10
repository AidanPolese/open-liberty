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
 * Structs to allow us to navigate to the client stub name field.
 */
typedef struct wolaStubObjectCode {
    unsigned char jumpInstruction[4]; /* J Around */
    unsigned char stubName[8];        /* DC CL8'BBOA1CNG' */
    unsigned char stubLevel[8];       /* DC CL8' *OLA3* ' */
} wolaStubObjectCode;

typedef struct stubEPA {
    struct wolaStubObjectCode* __ptr32 objectCode_p;
} stubEPA;

typedef struct externalSubsystemParameterList {
    void* __ptr32 essplistAddr_p;
    void* __ptr32 lit_p;
    struct stubEPA* __ptr32 epa_p;
} externalSubsystemParameterList;

/**
 * Structs for passing parameters to the WOLA client function.
 */
typedef struct bboa1regParms {
    char* __ptr32 dgname_p;
    char* __ptr32 nodename_p;
    char* __ptr32 servername_p;
    char* __ptr32 regionname_p;
    int*  __ptr32 minconn_p;
    int*  __ptr32 maxconn_p;
    int*  __ptr32 regflags_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1regParms;

typedef struct bboa1urgParms {
    char* __ptr32 registername;
    int*  __ptr32 unregflags_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1urgParms;

typedef struct bboa1cngParms {
    char* __ptr32 registername;
    char* __ptr32 connhandle_p;
    int*  __ptr32 waittime_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1cngParms;

typedef struct bboa1cnrParms {
    char* __ptr32 connhandle_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1cnrParms;

typedef struct bboa1srqParms {
    char* __ptr32 connectionhdl_p;
    int*  __ptr32 reqtype_p;
    char* __ptr32 servicename;
    int*  __ptr32 servicenamelen_p;
    char* __ptr32 * __ptr32 reqdata_p;
    int*  __ptr32 reqdatalen_p;
    int*  __ptr32 async_p;
    int*  __ptr32 rspdatalen_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1srqParms;

typedef struct bboa1srpParms {
    char* __ptr32 connhandle_p;
    char* __ptr32 * __ptr32 rspdata_p;
    int*  __ptr32 rspdatalen_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1srpParms;

typedef struct bboa1srxParms {
    char* __ptr32 connhandle_p;
    char* __ptr32 * __ptr32 excRspdata_p;
    int*  __ptr32 excRspdatalen_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1srxParms;

typedef struct bboa1rcaParms {
    char* __ptr32 registername;
    char* __ptr32 connhandle_p;
    char* __ptr32 servicename;
    int*  __ptr32 servicenamelen_p;
    int*  __ptr32 reqdatalen_p;
    int*  __ptr32 waittime_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1rcaParms;

typedef struct bboa1rcsParms {
    char* __ptr32 connectionhdl_p;
    char* __ptr32 servicename;
    int*  __ptr32 servicenamelen_p;
    int*  __ptr32 reqdatalen_p;
    int*  __ptr32 async_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1rcsParms;

typedef struct bboa1rclParms {
    char* __ptr32 connhandle_p;
    int*  __ptr32 async_p;
    int*  __ptr32 rspdatalen_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1rclParms;

typedef struct bboa1getParms {
    char* __ptr32 connhandle_p;
    char* __ptr32 * __ptr32 msgdata_p;
    int*  __ptr32 msgdatalen_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
    int*  __ptr32 rval_p;
} bboa1getParms;

typedef struct bboa1invParms {
    char* __ptr32 registername;
    int*  __ptr32 reqtype;
    char* __ptr32 servicename;
    int*  __ptr32 servicenamelen;
    char* __ptr32 * __ptr32 reqdata;
    int*  __ptr32 reqdatalen;
    char* __ptr32 * __ptr32 rspdata;
    int*  __ptr32 rspdatalen;
    int*  __ptr32 waittime;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
    int*  __ptr32 rval_p;
} bboa1invParms;

typedef struct bboa1srvParms {
    char* __ptr32 registername;
    char* __ptr32 servicename;
    int*  __ptr32 servicenamelen_p;
    char* __ptr32 * __ptr32 reqdata_p;
    int*  __ptr32 reqdatalen_p;
    char* __ptr32 connhandle_p;
    int*  __ptr32 waittime_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
    int*  __ptr32 rval_p;
} bboa1srvParms;

typedef struct bboa1infParms {
    char* __ptr32 registername;
    char* __ptr32 wolaGroup;
    char* __ptr32 wolaName2;
    char* __ptr32 wolaName3;
    struct bboaconn* __ptr32 * __ptr32 connInfo_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
} bboa1infParms;

typedef struct bboa1gtxParms {
    char* __ptr32 connhandle_p;
    char* __ptr32 * __ptr32 ctxdata_p;
    int*  __ptr32 ctxdatalen_p;
    int*  __ptr32 rc_p;
    int*  __ptr32 rsn_p;
    int*  __ptr32 rval_p;
} bboa1gtxParms;

typedef struct callerParmList {
    union {
        bboa1regParms parmREG;
        bboa1urgParms parmURG;
        bboa1cngParms parmCNG;
        bboa1cnrParms parmCNR;
        bboa1srqParms parmSRQ;
        bboa1srpParms parmSRP;
        bboa1srxParms parmSRX;
        bboa1rcaParms parmRCA;
        bboa1rcsParms parmRCS;
        bboa1rclParms parmRCL;
        bboa1getParms parmGET;
        bboa1invParms parmINV;
        bboa1srvParms parmSRV;
        bboa1infParms parmINF;
        bboa1gtxParms parmGTX;
    };
} callerParmList;

typedef struct callerParmListP {
    callerParmList* __ptr32 callerParmList_p;
} callerParmListP;

int normalCallExit(void* eevtPrefix_p,
                   externalSubsystemParameterList* essplist_p,
                   callerParmListP* callerParmP_p,
                   void* recToken_p,
                   void* auth_p);
