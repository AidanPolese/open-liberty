#include <metal.h>
#include <stdlib.h>
#include <string.h>
#include "include/wola_ims_esmt_init.h"
#include "include/mvs_abend.h"

#pragma prolog(initESMTExit,"ESAFPRLG")
#pragma epilog(initESMTExit,"ESAFEPLG")

/**
 * !!! WARNING !!!
 * This routine does not use the usual 1MB stack and metal C environment.
 * Be conscious of stack usage and know that certain functions may be unavailable.
 */

/*
 * WOLA initialization of the IMS EEVT table containing address of ESMT functions
 * Only functions implemented are the normal call exit and the dummy exit.
 * @param EEVTP* - pointer the the EEVT Prefix
 * @param void* - Address of the 1-byte alphabetic region error option (REO) character defined by the installation (not used)
 */

// dummy routine just returns a rc = 0
extern void BBOAIDMY();
// Normal call exit
extern void normalCallExit();



int initESMTExit(EEVTP* eevtPrefix_p, void* region_error_option)
{
    int rc = 0;

    if( eevtPrefix_p != NULL ) {
        if( strncmp( eevtPrefix_p->EEVPNAME,"EEVP",4) == 0) {
            EEVT* eevt_p = eevtPrefix_p->EEVPEEA;
            if(strncmp(eevt_p->EEVTNAME,"EEVT",4)== 0) {
                  eevt_p->EEVTINIT = &BBOAIDMY;
                  eevt_p->EEVTID   = &BBOAIDMY;
                  eevt_p->EEVTRID  = &BBOAIDMY;
                  eevt_p->EEVTSO   = &BBOAIDMY;
                  eevt_p->EEVTCT   = &BBOAIDMY;
                  eevt_p->EEVTCP   = &BBOAIDMY;
                  eevt_p->EEVTCC   = &BBOAIDMY;
                  eevt_p->EEVTA    = &BBOAIDMY;
                  eevt_p->EEVTTT   = &BBOAIDMY;
                  eevt_p->EEVTSF   = &BBOAIDMY;
                  eevt_p->EEVTTI   = &BBOAIDMY;
                  eevt_p->EEVTSNO  = &BBOAIDMY;
                  eevt_p->EEVTSST  = &BBOAIDMY;
                  //   replace with Normal Call exit below
                  eevt_p->EEVTNC   = &normalCallExit;
                  eevt_p->EEVTECHO = &BBOAIDMY;
                  eevt_p->EEVTCMD  = &BBOAIDMY;
                  eevt_p->EEVTCV   = &BBOAIDMY;
                  eevt_p->EEVTIC   = &BBOAIDMY;
                  eevt_p->EEVTABE  = &BBOAIDMY;

            } else {
                rc = 12;
                abend(ABEND_TYPE_SERVER,KRSN_SERVER_WOLA_ESMT_INIT_EXIT_EEVT_INVALID);
            }

        } else {
            rc = 12;
            abend(ABEND_TYPE_SERVER,KRSN_SERVER_WOLA_ESMT_INIT_EXIT_EEVTP_INVALID);

        }
    } else {
        rc = 12;
        abend(ABEND_TYPE_SERVER,KRSN_SERVER_WOLA_ESMT_INIT_EXIT_EEVTP_NULL);

    }
    return rc;
}
