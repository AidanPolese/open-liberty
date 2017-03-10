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

#include <metal.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "include/mvs_ifausage.h"
#include "include/mvs_utils.h"
#include "include/ras_tracing.h"
#include "include/server_product_registration.h"


/**
 * @file
 * Call z/OS IFAUSAGE service to register product presence
 */

//-----------------------------------------------------------------------------
// RAS trace constants.
//-----------------------------------------------------------------------------
#define RAS_MODULE_CONST RAS_MODULE_MVS_IFAUSAGE
#define TP_PROD_MGR_REGISTER_ENTRY                                  1
#define TP_PROD_MGR_REGISTER_EXIT                                   2
#define TP_PROD_MGR_DEREGISTER_ENTRY                                3
#define TP_PROD_MGR_DEREGISTER_INPUT_DATA                           4
#define TP_PROD_MGR_DEREGISTER_EXIT                                 5
#define TP_PROD_MGR_REGISTER_INPUT_DATA                             6

// List form of IFAUSAGE macro 
__asm(" IFAUSAGE MF=(L,LISTIFA)" : "DS"(listifa));

/**
 * Register a product with z/OS as authorized.
 * All parameters must be padded with blanks to their full length.  Null termination is optional.
 *
 * regParms:
 * owner_p      : A pointer to a 16 byte EBCDIC string identifying the product owner (e.g. IBM)
 * name_p       : A pointer to a 16 byte EBCDIC string identifying the product (e.g. WAS FOR Z/OS)
 * version_p    : A pointer to an 8 byte EBCDIC string identifying the product version (e.g. 8.5)
 * id_p         : A pointer to an 8 byte EBDDIC string containing the product identifier (e.g. 5655-W65)
 * qualifier_p  : A pointer to an 8 byte EBCDIC string containing a product qualifier or feature name (e.g. WAS Z/OS)
 * returnCode_p : The return code from the IFAUSAGE service. A zero is good.  A four means you've registered for something
 * else already which could be ok.  An eight means you are calling from unauthorized code and tried to register for
 * more than two products.  A negative one means we couldn't get below-the-bar storage for our parms.  Anything else is just really bad.
 *
 */
void pc_registerProduct(regParms* p) {

    registerProduct(p);
}
/**
 * Register a product with z/OS as unauthorized.
 * All parameters must be padded with blanks to their full length.  Null termination is optional.
 *
 * regParms:
 * owner_p      : A pointer to a 16 byte EBCDIC string identifying the product owner (e.g. IBM)
 * name_p       : A pointer to a 16 byte EBCDIC string identifying the product (e.g. WAS FOR Z/OS)
 * version_p    : A pointer to an 8 byte EBCDIC string identifying the product version (e.g. 8.5)
 * id_p         : A pointer to an 8 byte EBDDIC string containing the product identifier (e.g. 5655-W65)
 * qualifier_p  : A pointer to an 8 byte EBCDIC string containing a product qualifier or feature name (e.g. WAS Z/OS)
 * returnCode_p : The return code from the IFAUSAGE service. A zero is good.  A four means you've registered for something
 * else already which could be ok.  An eight means you are calling from unauthorized code and tried to register for
 * more than two products.  A negative one means we couldn't get below-the-bar storage for our parms.  Anything else is just really bad.
 *
 */
int registerProduct(regParms* p) {

    if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed, TP(TP_PROD_MGR_REGISTER_ENTRY),
                "registerProduct. Entry",
                TRACE_DATA_RAWDATA(sizeof(regParms), p, "regParms"),
                TRACE_DATA_END_PARMS);
        }

    struct parm31 {
        char buffifa[sizeof(listifa)];
        char prodOwner[16];
        char prodName[16];
        char prodVersion[8];
        char prodId[8];
        char prodQual[8];
        int rc;
    };

    int rc = PRODUCT_REGISTRATION_NO_31_BIT_STORAGE;

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));

    if (parm_p != NULL) {
        memcpy(parm_p->buffifa, &listifa, sizeof(listifa));
        memcpy_sk(parm_p->prodOwner, p->owner_p, sizeof(parm_p->prodOwner), 8);
        memcpy_sk(parm_p->prodName, p->name_p, sizeof(parm_p->prodName), 8);
        memcpy_sk(parm_p->prodVersion, p->version_p, sizeof(parm_p->prodVersion), 8);
        memcpy_sk(parm_p->prodId, p->id_p, sizeof(parm_p->prodId), 8);
        memcpy_sk(parm_p->prodQual, p->qualifier_p, sizeof(parm_p->prodQual), 8);

        if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(TP_PROD_MGR_REGISTER_INPUT_DATA),
                                "registerProduct. Input data",
                                TRACE_DATA_RAWDATA(sizeof(parm_p->prodOwner),   parm_p->prodOwner,   "Owner"),
                                TRACE_DATA_RAWDATA(sizeof(parm_p->prodName),    parm_p->prodName,    "Name"),
                                TRACE_DATA_RAWDATA(sizeof(parm_p->prodVersion), parm_p->prodVersion, "Version"),
                                TRACE_DATA_RAWDATA(sizeof(parm_p->prodId),      parm_p->prodId,      "ID"),
                                TRACE_DATA_RAWDATA(sizeof(parm_p->prodQual),    parm_p->prodQual,    "Qualifier"),
                                TRACE_DATA_END_PARMS);
                }

        __asm(" SAM31\n"
                  " SYSSTATE AMODE64=NO\n"
                  " IFAUSAGE REQUEST=REGISTER,PRODOWNER=(%1),"
                  "PRODNAME=(%2),PRODVERS=(%3),PRODID=(%4),"
                  "PRODQUAL=(%5),DOMAIN=ADDRSP,MF=(E,(%6))\n"
                  " ST 15,%0\n"
                  " SYSSTATE AMODE64=YES\n"
                  " SAM64":
                  "=m"(parm_p->rc) :
                  "r"(&(parm_p->prodOwner)),"r"(parm_p->prodName),
                  "r"(parm_p->prodVersion),"r"(parm_p->prodId),
                  "r"(parm_p->prodQual),"r"(parm_p->buffifa) :
                  "r0","r1","r14","r15");

        rc = parm_p->rc;
        free(parm_p);
        memcpy_dk(p->returnCode_p, &rc, sizeof(int), 8);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_PROD_MGR_REGISTER_EXIT),
                        "registerProduct. Exit",
                        TRACE_DATA_INT(rc, "Return code"),
                        TRACE_DATA_END_PARMS);
        }
    }

    return rc;
}



/**
 * Deregister a product with z/OS.
 * All parameters must be padded with blanks to their full length.  Null termination is optional.
 *
 * regParms:
 * owner_p      : A pointer to a 16 byte EBCDIC string identifying the product owner (e.g. IBM)
 * name_p       : A pointer to a 16 byte EBCDIC string identifying the product (e.g. WAS FOR Z/OS)
 * version_p    : A pointer to an 8 byte EBCDIC string identifying the product version (e.g. 8.5)
 * id_p         : A pointer to an 8 byte EBDDIC string containing the product identifier (e.g. 5655-W65)
 * qualifier_p  : A pointer to an 8 byte EBCDIC string containing a product qualifier or feature name (e.g. WAS Z/OS)
 * returnCode_p : The return code from the IFAUSAGE service.  A zero is good. An eight means you specified 
 *                REQUEST=DEREGISTER for a product that has not first specified REQUEST=REGISTER. A negative 
 *                one means we couldn't get below-the-bar storage for our parms.
 */
void pc_deregisterProduct(regParms* p) {

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed, TP(TP_PROD_MGR_DEREGISTER_ENTRY),
            "deregisterProduct. Entry",
            TRACE_DATA_RAWDATA(sizeof(regParms), p, "regParms"),
            TRACE_DATA_END_PARMS);
    }

    struct parm31 {
        char buffifa[sizeof(listifa)];
        char prodOwner[16];
        char prodName[16];
        char prodVersion[8];
        char prodId[8];
        char prodQual[8];
        int rc;
    };

    // Initialize the return code.
    int rc = PRODUCT_REGISTRATION_NO_31_BIT_STORAGE;

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));

    if (parm_p != NULL) {
        memcpy(parm_p->buffifa, &listifa, sizeof(listifa));
        memcpy_sk(parm_p->prodOwner, p->owner_p, sizeof(parm_p->prodOwner), 8);
        memcpy_sk(parm_p->prodName, p->name_p, sizeof(parm_p->prodName), 8);
        memcpy_sk(parm_p->prodVersion, p->version_p, sizeof(parm_p->prodVersion), 8);
        memcpy_sk(parm_p->prodId, p->id_p, sizeof(parm_p->prodId), 8);
        memcpy_sk(parm_p->prodQual, p->qualifier_p, sizeof(parm_p->prodQual), 8);

        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(TP_PROD_MGR_DEREGISTER_INPUT_DATA),
                        "deregisterProduct. Input data",
                        TRACE_DATA_RAWDATA(sizeof(parm_p->prodOwner),   parm_p->prodOwner,   "Owner"),
                        TRACE_DATA_RAWDATA(sizeof(parm_p->prodName),    parm_p->prodName,    "Name"),
                        TRACE_DATA_RAWDATA(sizeof(parm_p->prodVersion), parm_p->prodVersion, "Version"),
                        TRACE_DATA_RAWDATA(sizeof(parm_p->prodId),      parm_p->prodId,      "ID"),
                        TRACE_DATA_RAWDATA(sizeof(parm_p->prodQual),    parm_p->prodQual,    "Qualifier"),
                        TRACE_DATA_END_PARMS);
        }

        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " IFAUSAGE REQUEST=DEREGISTER,PRODOWNER=(%1),"
              "PRODNAME=(%2),PRODVERS=(%3),PRODID=(%4),"
              "PRODQUAL=(%5),MF=(E,(%6))\n"
              " ST 15,%0\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64":
              "=m"(parm_p->rc) :
              "r"(&(parm_p->prodOwner)),"r"(parm_p->prodName),
              "r"(parm_p->prodVersion),"r"(parm_p->prodId),
              "r"(parm_p->prodQual),"r"(parm_p->buffifa) :
              "r0","r1","r14","r15");

        rc = parm_p->rc;
        free(parm_p);
        memcpy_dk(p->returnCode_p, &rc, sizeof(int), 8);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(TP_PROD_MGR_DEREGISTER_EXIT),
                    "deregisterProduct. Exit",
                    TRACE_DATA_INT(rc, "Return code"),
                    TRACE_DATA_END_PARMS);
    }
}
