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

/**
 * @file
 * Defines functions for obtaining and releasing storage using the MVS IARV64 macro.
 */
#include <metal.h>
#include <string.h>
#include <stdlib.h>

#include "include/mvs_iarv64.h"

/* Get a chunk of shared above the bar storage */
void*
getSharedAbove(long long megs, unsigned char fetchProtected, long long userToken) {
    void* storage_p = NULL;
    char plist[256];

    if (fetchProtected == 0) {
        __asm(" IARV64 REQUEST=GETSHARED,SEGMENTS=%1,KEY=CALLERKEY,"
              "FPROT=NO,USERTKN=%2,ORIGIN=%0,COND=NO,"
              "CHANGEACCESS=GLOBAL,MF=(E,%3,COMPLETE)" :
              "=m"(storage_p) :
              "m"(megs),"m"(userToken),"m"(plist) :
              "r0","r1","r14","r15");
    } else {
        __asm(" IARV64 REQUEST=GETSHARED,SEGMENTS=%1,KEY=CALLERKEY,"
              "FPROT=YES,USERTKN=%2,ORIGIN=%0,COND=NO,"
              "CHANGEACCESS=GLOBAL,MF=(E,%3,COMPLETE)" :
              "=m"(storage_p) :
              "m"(megs),"m"(userToken),"m"(plist) :
              "r0","r1","r14","r15");
    }

    return storage_p;
}

/* Request access to a piece of shared above the bar storage */
void
accessSharedAbove(void* storage_p, long long userToken) {
    char plist[256];

    struct {
        void* storage_p;
        char reserved[8];
    } rangelist;

    void* rangelist_p = NULL;

    memset(&rangelist, 0, sizeof(rangelist));
    rangelist.storage_p = storage_p;
    rangelist_p = (void*)(&rangelist);

    __asm(" IARV64 REQUEST=SHAREMEMOBJ,USERTKN=%0,RANGLIST=%1,NUMRANGE=1,"
          "SVCDUMPRGN=YES,COND=NO,MF=(E,%2,COMPLETE)" : :
          "m"(userToken),"m"(rangelist_p),"m"(plist) :
          "r0","r1","r14","r15");
}

/* Request access to a piece of shared above the bar storage */
int accessSharedAboveConditional(void* storage_p, long long userToken, int* rsn_p) {
    char plist[256];
    int rc = 0, rsn = 0;

    struct {
        void* storage_p;
        char reserved[8];
    } rangelist;

    void* rangelist_p = NULL;

    memset(&rangelist, 0, sizeof(rangelist));
    rangelist.storage_p = storage_p;
    rangelist_p = (void*)(&rangelist);

    __asm(" IARV64 REQUEST=SHAREMEMOBJ,USERTKN=%2,RANGLIST=%3,NUMRANGE=1,"
          "SVCDUMPRGN=YES,COND=YES,RETCODE=%0,RSNCODE=%1,MF=(E,%4,COMPLETE)" :
          "=m"(rc),"=m"(rsn) :
          "m"(userToken),"m"(rangelist_p),"m"(plist) :
          "r0","r1","r14","r15");

    if (rsn_p != NULL) *rsn_p = rsn;
    return rc;
}


/* Detach from a piece of shared above the bar storage */
void
detachSharedAbove(void* storage_p, long long userToken, unsigned char systemAffinity) {
    char plist[256];

    if (systemAffinity == 0) {
        __asm(" IARV64 REQUEST=DETACH,MATCH=SINGLE,MEMOBJSTART=%0,"
              "USERTKN=%1,AFFINITY=LOCAL,COND=NO,MF=(E,%2,COMPLETE)" : :
              "m"(storage_p),"m"(userToken),"m"(plist) :
              "r0","r1","r14","r15");
    } else {
        __asm(" IARV64 REQUEST=DETACH,MATCH=SINGLE,MEMOBJSTART=%0,"
              "USERTKN=%1,AFFINITY=SYSTEM,COND=NO,MF=(E,%2,COMPLETE)" : :
              "m"(storage_p),"m"(userToken),"m"(plist) :
              "r0","r1","r14","r15");
    }
}

// Detach from a piece of shared above the bar storage */
int detachSharedAboveConditional(void* storage_p,
                                 long long userToken,
                                 unsigned char systemAffinity,
                                 int* rsn_p) {
    char plist[256];
    int iarv64_rc = 0, iarv64_rsn = 0;

    if (systemAffinity == 0) {
        __asm(" IARV64 REQUEST=DETACH,MATCH=SINGLE,MEMOBJSTART=%2,"
              "USERTKN=%3,AFFINITY=LOCAL,COND=YES,RETCODE=%0,"
              "RSNCODE=%1,MF=(E,%4,COMPLETE)" :
              "=m"(iarv64_rc),"=m"(iarv64_rsn) :
              "m"(storage_p),"m"(userToken),"m"(plist) :
              "r0","r1","r14","r15");
    } else {
        __asm(" IARV64 REQUEST=DETACH,MATCH=SINGLE,MEMOBJSTART=%2,"
              "USERTKN=%3,AFFINITY=SYSTEM,COND=YES,RETCODE=%0,"
              "RSNCODE=%1,MF=(E,%4,COMPLETE)" :
              "=m"(iarv64_rc),"=m"(iarv64_rsn) :
              "m"(storage_p),"m"(userToken),"m"(plist) :
              "r0","r1","r14","r15");
    }

    if (rsn_p != NULL) *rsn_p = iarv64_rsn;
    return iarv64_rc;
}

/* Change access to a region of shared memory from one access level to another. */
int changeSharedAccessLevel(void* storage_p,
                            long long numSegments,
                            unsigned int access,
                            int* rsn_p) {

    char plist[256];
    int iarv64_rc = 0, iarv64_rsn = 0;

    struct {
        void* startAddress_p;
        unsigned long long numSegments;
    } changeList;

    changeList.startAddress_p = storage_p;
    changeList.numSegments = numSegments;

    void* changeList_p = &changeList;

    if (access == IARV64_SHARED_READONLY) {
        __asm(" IARV64 REQUEST=CHANGEACCESS,VIEW=READONLY,RANGLIST=%2,"
              "RETCODE=%0,RSNCODE=%1,MF=(E,%3,COMPLETE)" :
              "=m"(iarv64_rc),"=m"(iarv64_rsn) :
              "m"(changeList_p),"m"(plist) :
              "r0","r1","r14","r15");
    } else if (access == IARV64_SHARED_READWRITE) {
        __asm(" IARV64 REQUEST=CHANGEACCESS,VIEW=SHAREDWRITE,RANGLIST=%2,"
              "RETCODE=%0,RSNCODE=%1,MF=(E,%3,COMPLETE)" :
              "=m"(iarv64_rc),"=m"(iarv64_rsn) :
              "m"(changeList_p),"m"(plist) :
              "r0","r1","r14","r15");
    } else if (access == IARV64_SHARED_HIDDEN) {
        __asm(" IARV64 REQUEST=CHANGEACCESS,VIEW=HIDDEN,RANGLIST=%2,"
              "RETCODE=%0,RSNCODE=%1,MF=(E,%3,COMPLETE)" :
              "=m"(iarv64_rc),"=m"(iarv64_rsn) :
              "m"(changeList_p),"m"(plist) :
              "r0","r1","r14","r15");
    }

    if (rsn_p != NULL) *rsn_p = iarv64_rsn;
    return iarv64_rc;
}

// List shared memory segments.
v64waheader* listSharedAbove(void) {
    char plist[256];

    unsigned int storageLen = 1024 * 256; // Start with 256K
    void* __ptr32 storage_p = NULL; // For some reason parm list copies 4 bytes only.
    unsigned char complete = FALSE;

    // Return code 0 is success.  Return code 2 is success but someone has
    // issued IARV64 since we called it (not a concern).
    while (complete == FALSE) {
        storage_p = __malloc31(storageLen); // Storage below the bar.
        if (storage_p != NULL) {
            int iarv64_rc = 0;
            memset(storage_p, 0x00, storageLen);
            __asm(" IARV64 REQUEST=LIST,V64LISTPTR=%1,V64LISTLENGTH=%2,"
                  "V64SHARED=YES,RETCODE=%0,PLISTVER=1,MF=(E,%3,COMPLETE)" :
                  "=m"(iarv64_rc) :
                  "m"(storage_p),"m"(storageLen),"m"(plist) :
                  "r0","r1","r14","r15");
            if ((iarv64_rc == v64warc_ok) ||
                (iarv64_rc == v64warc_changed)) {
                complete = TRUE; // Success.
            } else if ((iarv64_rc == v64warc_partial) ||
                       (iarv64_rc == v64warc_partialchanged)) {
                // Need more space.
                free(storage_p);
                storage_p = NULL;
                storageLen = storageLen * 2;
            } else {
                // Make something up.
                ((v64waheader*)storage_p)->v64wareturncode = iarv64_rc;
                ((v64waheader*)storage_p)->v64wanumdataareas = 0;
                complete = TRUE; // Success or failure.
            }
        } else {
            complete = TRUE; // Error case - no storage.
        }
    }

    return (v64waheader*) storage_p;
}


// Obtain non-shared above the bar storage.
void* obtain_iarv64(int segments, int guardSegments, TToken* ttoken_p, int* rc_p, int* rsn_p) {
    char plist[256];
    int rc, rsn;
    void* storage_p = NULL;
    long long segments_long = segments;

    __asm(" IARV64 REQUEST=GETSTOR,COND=YES,SEGMENTS=%3,FPROT=NO,GUARDSIZE=%4,"
          "GUARDLOC=HIGH,TTOKEN=%5,ORIGIN=%0,RETCODE=%1,RSNCODE=%2,"
          "MF=(E,%6,COMPLETE)" :
          "=m"(storage_p),"=m"(rc),"=m"(rsn) :
          "m"(segments_long),"m"(guardSegments),"m"(*ttoken_p),"m"(plist[0]) :
          "r0","r1","r14","r15");

    if (rc_p != NULL) *rc_p = rc;
    if (rsn_p != NULL) *rsn_p = rsn;
    if (rc != 0) storage_p = NULL;    // Don't pass back a 00000000_7FFFF000 just to 0C4 on.

    return storage_p;
}

/* Release non-shared above the bar storage obtained with IARV64 */
void
release_iarv64(void* storage_p, TToken* ttoken_p, int* rc_p, int* rsn_p) {
    char plist[256];
    int rc, rsn;

    __asm(" IARV64 REQUEST=DETACH,MATCH=SINGLE,MEMOBJSTART=(%2),"
          "USERTKN=NO_USERTKN,COND=YES,RETCODE=%0,RSNCODE=%1,"
          "PLISTVER=0,MF=(M,%3,COMPLETE)" :
          "=m"(rc),"=m"(rsn) :
          "r"(&storage_p),"m"(plist[0]) :
          "r0","r1","r14","r15");
    if (ttoken_p != NULL) {
        __asm(" IARV64 REQUEST=DETACH,TTOKEN=%0,MF=(M,%1,NOCHECK)" : :
              "m"(*ttoken_p),"m"(plist[0]) :
              "r0","r1","r14","r15");
    }
    __asm(" IARV64 REQUEST=DETACH,MF=(E,%0,NOCHECK)" : :
          "m"(plist[0]) :
          "r0","r1","r14","r15");

    if (rc_p != NULL) *rc_p = rc;
    if (rsn_p != NULL) *rsn_p = rsn;
}

/* Convert guard pages from a memory object created by IARV64. */
void
convertGuardToActive_iarv64(void* storageStart_p, int segmentsToRemove, int* rc_p, int* rsn_p) {
    char plist[256];
    int rc, rsn;

    __asm(" IARV64 REQUEST=CHANGEGUARD,CONVERT=FROMGUARD,COND=YES,MEMOBJSTART=(%2),"
          "CONVERTSIZE=%3,RETCODE=%0,RSNCODE=%1,PLISTVER=0,MF=(E,%4,COMPLETE)" :
          "=m"(rc),"=m"(rsn) :
          "r"(&storageStart_p),"m"(segmentsToRemove),"m"(plist[0]) :
          "r0","r1","r14","r15");

    if (rc_p != NULL) *rc_p = rc;
    if (rsn_p != NULL) *rsn_p = rsn;
}

