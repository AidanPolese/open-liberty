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
#ifndef _BBGZ_CLIENT_WOLA_STUBS_H
#define _BBGZ_CLIENT_WOLA_STUBS_H

#include "gen/bboapc1p.h"

/*-------------------------------------------------------------------*/
/* Additional information about the registration returned by         */
/* BBOA1INF.                                                         */
/*-------------------------------------------------------------------*/
typedef struct bboaconn
{
  char aconn_eye[8];                 /*+000   Header                 */
  short aconn_version;               /*+008   Version                */
  short aconn_size;                  /*+00A   Size                   */
  unsigned int aconn_reserved;       /*+00C   pad                    */
  unsigned int aconn_min;            /*+010   Min connections        */
  unsigned int aconn_max;            /*+014   Max connections        */
  long long aconn_info_state;        /*+018   Connection state       */
  long long aconn_info_active;       /*+020   Number active          */
  char   aconn_reserved2[128];       /*+028   Reserve                */
                                     /*+0A8   Size of bboaconn       */
};

int ClientRegister(char* dgname, char* nodename, char* servername, char* regionname,
                   int* minconn_p, int* maxconn_p, int* regflags_p, struct bboapc1p* cicsParms_p,
                   int* rc_p, int* rsn_p);

int ClientUnRegister(char* registername, int* unregflags_p, struct bboapc1p*, int* rc_p, int* rsn_p);

int ClientInvoke(char* registername, int* reqtype, char* servicename, int* servicenamelen, char* reqdata, int* reqdatalen,
                 char* rspdata, int* rspdatalen, int* waittime, struct bboapc1p*, int* rc_p, int* rsn_p, int* rval_p);

int ClientHostService(char* registername, char* servicename, int* servicenamelen_p, char* reqdata_p, int* reqdatalen_p,
                      char* connhandle_p, int* waittime_p, struct bboapc1p*, int* rc_p, int* rsn_p, int* rval_p);

int ClientSendResponse(char* connhandle_p, char* rspdata_p, int* rspdatalen_p, struct bboapc1p*, int* rc_p, int* rsn_p);

int ClientConnectionRelease(char* connhandle_p, struct bboapc1p*, int* rc_p, int* rsn_p);

int ClientConnectionGet(char* registername, char* connhandle_p, int* waittime_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p);

int ClientSendRequest(char* connectionhdl_p, int* reqtype_p, char* servicename, int* servicenamelen_p, char* reqdata_p, int* reqdatalen_p,
                      int* async_p, int* rspdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p);

int ClientGetContext(char* connhandle_p, char* ctxdata_p, int* ctxdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p, int* rval_p);

int ClientGetData(char* connhandle_p, char* msgdata_p, int* msgdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p, int* rval_p);

int ClientReceiveResponseLength(char* connhandle_p, int* async_p, int* rspdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p);

int ClientReceiveRequestAny(char* registername, char* connhandle_p, char* servicename, int* servicenamelen_p, int* reqdatalen_p,
                            int* waittime_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p);

int ClientReceiveRequestSpecific(char* connectionhdl_p, char* servicename, int* servicenamelen_p, int* reqdatalen_p,
                                 int* async_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p);

int ClientSendResponseException(char* connhandle_p, char* excRspdata_p, int* excRspdatalen_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p);

int ClientInfoGet(char* registername, char* wolaGroup, char* wolaName2, char* wolaName3, struct bboaconn* connInfo_p, struct bboapc1p* cicsParms_p, int* rc_p, int* rsn_p);

#endif
