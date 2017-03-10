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

#include "include/common_defines.h"
#include "include/mvs_enq.h"
#include "include/mvs_utils.h"

#include "include/gen/isgycon.h"

#define QNAME_MAX_LEN 8
#define RNAME_MAX_LEN 255

/**
 * Builds a QNAME in the designated area.  The QNAME is blank padded to eight
 * characters.
 *
 * @param qname A pointer to a null terminated string containing the desired qname.
 *              If the string is longer than 8 bytes, the first 8 bytes will be
 *              used as the QNAME.
 * @param qname_target A pointer to an 8 byte area where the blank padded qname
 *                     will be built.
 */
static void
build_qname(char* qname, char* qname_target) {
    char* qname_end = memchr(qname, 0, QNAME_MAX_LEN);
    int qname_len = QNAME_MAX_LEN;

    if (qname_end != NULL) {
        qname_len = qname_end - qname;
    }

    memset(qname_target, ' ', QNAME_MAX_LEN);
    memcpy(qname_target, qname, qname_len);
}

void
get_enq_exclusive_system(char* qname, char* rname, char* ottoken, enqtoken* token_p) {
    char plist[256];

    char qname_fixed[8];
    build_qname(qname, qname_fixed);

    char* rname_end = memchr(rname, 0, RNAME_MAX_LEN);
    unsigned char rname_len = (rname_end != NULL) ? (rname_end - rname) : RNAME_MAX_LEN;


    if (ottoken == NULL) {
        __asm(" ISGENQ REQUEST=OBTAIN,QNAME=(%0),RNAME=(%1),RNAMELEN=(%2),SCOPE=SYSTEM,"
              "CONTROL=EXCLUSIVE,ENQTOKEN=(%3),COND=NO,PLISTVER=1,MF=(E,(%4),COMPLETE)" : :
              "r"(&(qname_fixed[0])),"r"(rname),"r"(&rname_len),"r"(token_p),"r"(plist) :
              "r0","r1","r14","r15");
    } else {
        __asm(" ISGENQ REQUEST=OBTAIN,QNAME=(%0),RNAME=(%1),RNAMELEN=(%2),SCOPE=SYSTEM,"
              "CONTROL=EXCLUSIVE,ENQTOKEN=(%3),OWNINGTTOKEN=(%4),CONTENTIONACT=FAIL,"
              "COND=NO,PLISTVER=1,MF=(E,(%5),COMPLETE)" : :
              "r"(&(qname_fixed[0])),"r"(rname),"r"(&rname_len),"r"(token_p),"r"(ottoken),"r"(plist) :
              "r0","r1","r14","r15");
    }
}

/*
 * Mix of get enq exclusive and get enq exclusive conditional. Allow passing of extra token to search on
 */
int
get_enq_exclusive_system_conditional_token(char* qname, char* rname, char* ottoken, enqtoken* token_p) {
    char plist[256];

    char qname_fixed[8];
    build_qname(qname, qname_fixed);
    int enq_rc = -1, enq_rsn = -1;


    char* rname_end = memchr(rname, 0, RNAME_MAX_LEN);
    unsigned char rname_len = (rname_end != NULL) ? (rname_end - rname) : RNAME_MAX_LEN;


    if (ottoken == NULL) {
        __asm(" ISGENQ REQUEST=OBTAIN,QNAME=(%2),RNAME=(%3),RNAMELEN=(%4),CONTENTIONACT=FAIL,"
              "SCOPE=SYSTEM,CONTROL=EXCLUSIVE,ENQTOKEN=(%5),COND=YES,RETCODE=(R15),"
              "RSNCODE=(R0),PLISTVER=1,MF=(E,(%6),COMPLETE)\n"
              " ST 15,%0\n"
              " ST 0,%1": "=m"(enq_rc),"=m"(enq_rsn) :
              "r"(&(qname_fixed[0])),"r"(rname),"r"(&rname_len),"r"(token_p),"r"(plist) :
              "r0","r1","r14","r15");
    } else {
        __asm(" ISGENQ REQUEST=OBTAIN,QNAME=(%2),RNAME=(%3),RNAMELEN=(%4),CONTENTIONACT=FAIL,"
              "SCOPE=SYSTEM,CONTROL=EXCLUSIVE,ENQTOKEN=(%5),OWNINGTTOKEN=(%6),COND=YES,RETCODE=(R15),"
              "RSNCODE=(R0),PLISTVER=1,MF=(E,(%7),COMPLETE)\n"
              " ST 15,%0\n"
              " ST 0,%1": "=m"(enq_rc),"=m"(enq_rsn) :
              "r"(&(qname_fixed[0])),"r"(rname),"r"(&rname_len),"r"(token_p),"r"(ottoken),"r"(plist) :
              "r0","r1","r14","r15");
    }
    // ENQ obtained.
    if (enq_rc == 0) {
        return 0;
    }

    // ENQ held by another task.
    if ((enq_rc == isgenqrc_warn) && ((enq_rsn & 0x0000FFFF) == isgenqrsn_notimmediatelyavailable)) {
        return 1;
    }

    // Unknown error, ENQ not obtained.
    return -1;
}
int
get_enq_exclusive_system_conditional(char* qname, char* rname, enqtoken* token_p) {

    return get_enq_exclusive_system_conditional_token(qname, rname, NULL, token_p);

}

void
get_enq_exclusive_step(char* qname, char* rname, enqtoken* token_p) {
    char plist[256];

    char qname_fixed[8];
    build_qname(qname, qname_fixed);

    char* rname_end = memchr(rname, 0, RNAME_MAX_LEN);
    unsigned char rname_len = (rname_end != NULL) ? (rname_end - rname) : RNAME_MAX_LEN;

    __asm(" ISGENQ REQUEST=OBTAIN,QNAME=(%0),RNAME=(%1),RNAMELEN=(%2),SCOPE=STEP,CONTROL=EXCLUSIVE,ENQTOKEN=(%3),COND=NO,PLISTVER=1,MF=(E,(%4),COMPLETE)" : :
          "r"(&(qname_fixed[0])),"r"(rname),"r"(&rname_len),"r"(token_p),"r"(plist) :
          "r0","r1","r14","r15");
}

int
test_enq_step(char* qname, char* rname, enqtoken* token_p) {
    char plist[256];

    char qname_fixed[8];
    build_qname(qname, qname_fixed);

    char* rname_end = memchr(rname, 0, RNAME_MAX_LEN);
    unsigned char rname_len = (rname_end != NULL) ? (rname_end - rname) : RNAME_MAX_LEN;

    int rc, rsn;

    __asm(" ISGENQ REQUEST=OBTAIN,TEST=YES,QNAME=(%2),RNAME=(%3),RNAMELEN=(%4),"
          "SCOPE=STEP,CONTROL=EXCLUSIVE,COND=YES,OWNINGTTOKEN=CURRENT_TASK,"
          "ENQTOKEN=(%5),RETCODE=(R15),RSNCODE=(R0),PLISTVER=1,MF=(E,(%6),COMPLETE)\n"
          " ST 15,%0\n"
          " ST 0,%1" : "=m"(rc),"=m"(rsn) :
          "r"(&(qname_fixed[0])),"r"(rname),"r"(&rname_len),"r"(token_p),"r"(plist) :
          "r0","r1","r14","r15");

    if ((rc == isgenqrc_warn) && ((rsn & 0x0000FFFF) == isgenqrsn_taskownsexclusive)) {
        return TASK_OWNS_EXCLUSIVE;
    } else if ((rc == isgenqrc_warn) && ((rsn & 0x0000FFFF) == isgenqrsn_taskownsshared)) {
        return TASK_OWNS_SHARED;
    }

    return TASK_DOES_NOT_OWN;
}

void
release_enq(enqtoken* token_p) {
    char plist[256];
    __asm(" ISGENQ REQUEST=RELEASE,ENQTOKEN=(%0),COND=NO,PLISTVER=1,MF=(E,(%1),COMPLETE)" : :
          "r"(token_p),"r"(plist) : "r0","r1","r14","r15");
}

void
release_enq_owning(enqtoken* token_p, char* owningTToken_p) {
    char plist[256];
    __asm(" ISGENQ REQUEST=RELEASE,ENQTOKEN=(%0),OWNINGTTOKEN=(%1),COND=NO,PLISTVER=1,MF=(E,(%2),COMPLETE)" : :
          "r"(token_p),"r"(owningTToken_p),"r"(plist) : "r0","r1","r14","r15");
}

isgyquaahdr*
scan_enq_system(char* qname, char* rname_pattern, int* rc_p, int* rsn_p) {
    char isgquery_stg[256];

    char qname_fixed[8];
    build_qname(qname, qname_fixed);

    char* rname_end = memchr(rname_pattern, 0, RNAME_MAX_LEN);
    unsigned char rname_len = (rname_end != NULL) ? (rname_end - rname_pattern) : RNAME_MAX_LEN;

    // ISGQUERY supports 64 bit parameters, but the mappings of the macros
    // generated by EDCDSECT for the answer area have 31 bit pointers in
    // them.  So we'll allocate the answer area below the bar.
    int ans_area_size = 1024 * 16; /* Start with 16 KB */
    isgyquaahdr* ans_area_p = NULL;
    int rc, rsn;

    do {
        ans_area_p = __malloc31(ans_area_size);
        if (ans_area_p != NULL){
            __asm(" ISGQUERY REQINFO=QSCAN,SCANACTION=START,ANSAREA=%2,"
                  "ANSLEN=%3,ANSDETAIL=FULL,GATHERFROM=SYSTEM,"
                  "SEARCH=BY_FILTER,QNAMEMATCH=SPECIFIC,QNAME=%4,"
                  "RNAMEMATCH=PATTERN,RNAME=%5,RNAMELEN=%6,SCOPE=SYSTEM,"
                  "SERIALIZEBY=ENQ_ONLY,ASID=ANY_ASID,RETCODE=%0,RSNCODE=%1,"
                  "PLISTVER=1,MF=(E,%7,COMPLETE)" :
                  "=m"(rc),"=m"(rsn) :
                  "m"(*ans_area_p),"m"(ans_area_size),"m"(qname_fixed[0]),
                  "m"(*rname_pattern),"m"(rname_len),"m"(isgquery_stg[0]) :
                  "r0","r1","r14","r15");
            if ((rc == isgqueryrc_warn) && (rsn == isgqueryrsn_answerareafull)) {
                free(ans_area_p);
                ans_area_size = ans_area_size * 2;
            }
        } else {
            *rc_p = -1;
            *rsn_p = -1;
        }
    } while ((rc == isgqueryrc_warn) && (rsn == isgqueryrsn_answerareafull));

    if (rc != 0) {
        free(ans_area_p);
        ans_area_p = NULL;
    }

    if (rc_p != NULL) *rc_p = rc;
    if (rsn_p != NULL) *rsn_p = rsn;

    return ans_area_p;
}

