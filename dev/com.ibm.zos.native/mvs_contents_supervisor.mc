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

#include <ctype.h>
#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_contents_supervisor.h"
#include "include/mvs_utils.h"

// List form of CSVQUERY
__asm(" CSVQUERY PLISTVER=5,MF=(L,LIST_CSVQUERY)" : "DS"(list_csvquery));

int
contentsSupervisorQueryFromEntryPoint(void* entrypt_p, unsigned long long* length_p, void** addr_p) {
    int csv_rc = -1;

    struct parm31 {
        void* entrypt_p;
        unsigned long long length;
        void* addr;
        int csv_rc;
        char execute_csvquery[sizeof(list_csvquery)];
    };

    struct parm31* parms_p = __malloc31(sizeof(struct parm31));
    if (parms_p != NULL) {
        memcpy(parms_p->execute_csvquery, &list_csvquery, sizeof(parms_p->execute_csvquery));
        parms_p->entrypt_p = entrypt_p;
        parms_p->csv_rc = -1;

        __asm(" CSVQUERY INADDR64=(%1),OUTLENGTH64=(%2),OUTLOADPT64=(%3),RETCODE=%0,PLISTVER=5,MF=(E,(%4))" :
              "=m"(parms_p->csv_rc) :
              "r"(&(parms_p->entrypt_p)),"r"(&(parms_p->length)),"r"(&(parms_p->addr)),"r"(parms_p->execute_csvquery) :
              "r0","r1","r14","r15");

        csv_rc = parms_p->csv_rc;
        *length_p = parms_p->length;
        *addr_p = parms_p->addr;

        free(parms_p);
    }

    return csv_rc;
}

int
contentsSupervisorAddToDynamicLPA(const char* pathName, void* entry_point, void* module_start, int module_length, lpmea* modinfo_p, int* rsn_p) {
    int csv_rc = -1;

    struct parm31 {
        int rc;
        int rsn;
        char clpaerr[8];
        lpmea modinfo;
        int pathNameLength;
        char pathName[1024];
        char clpareqr[16];
        char buffclpa[256];
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p == NULL) {
        return -1;
    }

    // Clear the parameter list
    memset(parm_p, 0, sizeof(*parm_p));
    // Setup the pathname
    parm_p->pathNameLength = strlen(pathName) <= sizeof(parm_p->pathName) ? strlen(pathName) : sizeof(parm_p->pathName);
    strncpy(parm_p->pathName, pathName, sizeof(parm_p->pathName));

    // Setup the entry point alias
    char* alias = strrchr(pathName, '/');
    alias = alias ? alias + 1 : "UNKNOWN ";
    memset(parm_p->modinfo.lpmeaname, ' ', sizeof(parm_p->modinfo.lpmeaname));
    for (int i = 0, aliasLength = strlen(alias); i < sizeof(parm_p->modinfo.lpmeaname) && i < aliasLength; i++) {
        parm_p->modinfo.lpmeaname[i] = toupper(alias[i]);
    }

    // Setup the entry point information
    parm_p->modinfo.lpmeaentrypointaddr = entry_point;
    parm_p->modinfo.lpmealoadpointaddr = module_start;
    parm_p->modinfo.lpmeamodlen = module_length;
    memcpy(parm_p->clpareqr, "BBOZXXXXXXXXXXXX", sizeof(parm_p->clpareqr));

    // Register the PC target with LPA
    __asm(" SAM31\n"
          " SYSSTATE AMODE64=NO\n"
          " CSVDYLPA REQUEST=ADD,MODINFOTYPE=MEMBERLIST,BYADDR=YES,"
          "MODINFO=%3,NUMMOD=1,REQUESTOR=%4,RETCODE=%0,RSNCODE=%1,"
          "BYPATH=YES,PATHNAMELEN=%6,PATHNAME=%7,SECMODCHECK=NO,"
          "ERRORDATA=%2,MF=(E,%5)\n"
          " SYSSTATE AMODE64=YES\n"
          " SAM64" : :
          "m"(parm_p->rc),"m"(parm_p->rsn),"m"(parm_p->clpaerr),
          "m"(parm_p->modinfo),"m"(parm_p->clpareqr),
          "m"(parm_p->buffclpa),
          "m"(parm_p->pathNameLength), "m"(parm_p->pathName) : "r0","r1","r14","r15");

    memcpy(modinfo_p, &(parm_p->modinfo), sizeof(*modinfo_p));
    if (rsn_p != NULL) {
        *rsn_p = parm_p->rsn;
    }
    csv_rc = parm_p->rc;

    free(parm_p);

    return csv_rc;
}

int contentsSupervisorDeleteFromDynamicLPA(const char* delete_token, int* rsn_p) {
    int returnCode = -1;
    struct parm31 {
        char buffclpa[256];
        int rc;
        int rsn;
        lpmea modinfo;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p != NULL) {
        memset(parm_p, 0, sizeof(*parm_p));
        memcpy(parm_p->modinfo.lpmeadeletetoken, delete_token, sizeof(parm_p->modinfo.lpmeadeletetoken));

        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " CSVDYLPA REQUEST=DELETE,MODINFO=%2,NUMMOD=1,TYPE=BYTOKEN,"
              "SECMODCHECK=NO,RETCODE=%0,RSNCODE=%1,MF=(E,%3,COMPLETE)\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64": :
              "m"(parm_p->rc),"m"(parm_p->rsn),"m"(parm_p->modinfo),
              "m"(parm_p->buffclpa) :
              "r0","r1","r14","r15");

        if (rsn_p) {
            *rsn_p = parm_p->rsn;
        }

        free(parm_p);
    }

    return returnCode;
}

#pragma insert_asm(" CSVLPRET")
