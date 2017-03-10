#ifndef _WOLA_IMS_ESMT_INIT_H
#define _WOLA_IMS_ESMT_INIT_H

typedef void (*esmtFunc)();

//IMS DFSEEVT prefix control block
 typedef struct EEVTP {
     char          EEVPNAME[4];  // x'0'  eyecatcher - 'EEVP' x'
     char          fill[4];      // x'4'  fill
     void* __ptr32 EEVPEEA;      // x'8'  EEVT Address
     char          fill2[4];     // X'C'  fill
     void* __ptr32 EEVPEWA;      // x'10' Available for external subsystem
     void* __ptr32 EEVPRTA;      // x'14' Recovery token address
     char          fill3[4];     // x'18' fill
     void* __ptr32 EEVPRTTA;     // x'1C' Resource translation table address
     void* __ptr32 EEVTLDIR;     // x'20' Available for external subsystem
     char          fill4[4];     // x'24' fill
     void* __ptr32 EEVPESGL;     // x'28' DFSESGL0 address
     char          fill5[2];     // x'2C' fill
     char  EEVPF1;               // x'2E' Environment indicators
     char  EEVPF2;               // x'2F' Environment indicators
     char          fill6[4];     // x'30' fill
     void* __ptr32 EEVPSOTN;     // x'34' Signon token
     char          fill7[4];     // x'38' fill
     void* __ptr32 EEVPESMT;     // x'3C' ESMT address
     void* __ptr32 EEVPSVA;      // x'40' EESV address
  } EEVTP;

//IMS DFSEEVT control block
 typedef struct EEVT {
     char             EEVTNAME[4]; // Eye catcher - 'void* EEVT'
     esmtFunc __ptr32 EEVTINIT; // Initialization exit address
     esmtFunc __ptr32 EEVTID;   // Identify exit address
     esmtFunc __ptr32 EEVTRID;  // Resolve indoubt exit address
     esmtFunc __ptr32 EEVTSO;   // Signon exit address
     esmtFunc __ptr32 EEVTCT;   // Create thread exit address
     esmtFunc __ptr32 EEVTCP;   // Commit prepare exit address
     esmtFunc __ptr32 EEVTCC;   // Commit continue exit address
     esmtFunc __ptr32 EEVTA;    // Abort exit address
     esmtFunc __ptr32 EEVTTT;   // Terminate thread exit address
     esmtFunc __ptr32 EEVTSF;   // Signoff exit address
     esmtFunc __ptr32 EEVTTI;   // Terminate identify exit address
     esmtFunc __ptr32 EEVTSNO;  // Subsystem not operational exit address
     esmtFunc __ptr32 EEVTSST;  // Subsystem termination exit address
     esmtFunc __ptr32 EEVTNC;   // Normal call exit address
     esmtFunc __ptr32 EEVTECHO; // Echo exit address
     esmtFunc __ptr32 EEVTCMD;  // Command exit address
     esmtFunc __ptr32 EEVTCV;   // Commit verify exit address
     esmtFunc __ptr32 EEVTIC;   // Not used
     esmtFunc __ptr32 EEVTABE;  // Not used
     esmtFunc __ptr32 RESRVE1;  // Not used
     esmtFunc __ptr32 RESRVE2;  // Not used
 } EEVT;

int initESMTExit(EEVTP* eevtPrefix_p, void* region_error_option);

#endif
