/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <string.h>

#include "include/mvs_plo.h"

/* PLO function codes */
#define PLO_CLG      1
#define PLO_CSG      5
#define PLO_CSX      7
#define PLO_DCSG     9
#define PLO_CSSTG   13
#define PLO_CSSTX   15
#define PLO_CSDSTG  17
#define PLO_CSDSTX  19
#define PLO_CSTSTG  21
#define PLO_CSTSTX  23

/** Mapping of the condition code. */
typedef struct ipm_map {
    unsigned int _rsvd1 : 32;
    unsigned int _rsvd2 : 2;
    unsigned int cond_code   : 2;
    unsigned int _rsvd3 : 28;
} ipm_map;

/** Compare and load, double word area. */
int ploCompareAndLoadDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* compare_p, PloLoadAreaDoubleWord_t* load_p) {
    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long Op1CompareValue;                        /* 08 */
        unsigned char _rsvd1[24];                                  /* 16 */
        unsigned long long Operand3;                               /* 40 */
        unsigned char _rsvd2[24];                                  /* 48 */
        void* Op4Address;                                          /* 72 */
    } ploParmList;                                                 /* 80 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_CLG;
    memset(ploParmList._rsvd, 0, sizeof(ploParmList));
    ploParmList.Op1CompareValue = compare_p->expectedValue;
    ploParmList.Op4Address = load_p->loadLocation_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(compare_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(compare_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(compare_p->expectedValue));
    } else {
        load_p->loadValue = ploParmList.Operand3;
    }

    return condCode.cond_code;
}

/** Compare and Swap, double word area. */
int ploCompareAndSwapDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p) {
    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long Op1CompareValue;                        /* 08 */
        unsigned char _rsvd1[8];                                   /* 16 */
        unsigned long long Op1ReplaceValue;                        /* 24 */
    } ploParmList;                                                 /* 32 */


    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSG;
    memset(ploParmList._rsvd, 0, sizeof(ploParmList));
    ploParmList.Op1CompareValue = swap_p->expectedValue;
    ploParmList.Op1ReplaceValue = swap_p->replaceValue;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}

/** Compare and Swap, quad word area. */
int ploCompareAndSwapQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p) {
    struct {
        PloQuadWord_t Op1CompareValue __attribute__((aligned(8))); /* 00 */
        PloQuadWord_t Op1ReplaceValue;                             /* 16 */
    } ploParmList;                                                 /* 32 */


    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSX;
    ploParmList.Op1CompareValue = swap_p->expectedValue;
    ploParmList.Op1ReplaceValue = swap_p->replaceValue;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}


/** Double Compare and swap, double word area. */
int ploDoubleCompareAndSwapDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p, PloCompareAndSwapAreaDoubleWord_t* swap2_p) {
    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long Op1CompareValue;                        /* 08 */
        unsigned char _rsvd1[8];                                   /* 16 */
        unsigned long long Op1ReplaceValue;                        /* 24 */
        unsigned char _rsvd2[8];                                   /* 32 */
        unsigned long long Op3CompareValue;                        /* 40 */
        unsigned char _rsvd3[8];                                   /* 48 */
        unsigned long long Op3ReplaceValue;                        /* 56 */
        unsigned char _rsvd4[8];                                   /* 64 */
        void* Op4Address;                                          /* 72 */
    } ploParmList;                                                 /* 80 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_DCSG;
    memset(ploParmList._rsvd, 0, sizeof(ploParmList._rsvd));
    ploParmList.Op1CompareValue = swap_p->expectedValue;
    memset(ploParmList._rsvd1, 0, sizeof(ploParmList._rsvd1));
    ploParmList.Op1ReplaceValue = swap_p->replaceValue;
    memset(ploParmList._rsvd2, 0, sizeof(ploParmList._rsvd2));

    ploParmList.Op3CompareValue = swap2_p->expectedValue;
    memset(ploParmList._rsvd3, 0, sizeof(ploParmList._rsvd3));
    ploParmList.Op3ReplaceValue = swap2_p->replaceValue;
    memset(ploParmList._rsvd4, 0, sizeof(ploParmList._rsvd4));

    ploParmList.Op4Address = swap2_p->compare_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
        memcpy(&(swap2_p->expectedValue), &(ploParmList.Op3CompareValue), sizeof(swap2_p->expectedValue));
    }

    return condCode.cond_code;
}

/** Compare and swap and store, double word area. */
int ploCompareAndSwapAndStoreDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p, PloStoreAreaDoubleWord_t* store_p) {
    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long Op1CompareValue;                        /* 08 */
        unsigned char _rsvd1[8];                                   /* 16 */
        unsigned long long Op1ReplaceValue;                        /* 24 */
        unsigned char _rsvd2[24];                                  /* 32 */
        unsigned long long Op3;                                    /* 56 */
        unsigned char _rsvd3[8];                                   /* 64 */
        void* Op4Address;                                          /* 72 */
    } ploParmList;                                                 /* 80 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSSTG;
    memset(ploParmList._rsvd, 0, sizeof(ploParmList._rsvd));
    ploParmList.Op1CompareValue = swap_p->expectedValue;
    memset(ploParmList._rsvd1, 0, sizeof(ploParmList._rsvd1));
    ploParmList.Op1ReplaceValue = swap_p->replaceValue;
    memset(ploParmList._rsvd2, 0, sizeof(ploParmList._rsvd2));
    ploParmList.Op3 = store_p->storeValue;
    memset(ploParmList._rsvd3, 0, sizeof(ploParmList._rsvd3));
    ploParmList.Op4Address = store_p->storeLocation_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}

/** Compare and swap and store, quad word area. */
int ploCompareAndSwapAndStoreQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p, PloStoreAreaQuadWord_t* store_p) {
    struct {
        PloQuadWord_t Op1CompareValue __attribute__((aligned(8))); /* 00 */
        PloQuadWord_t Op1ReplaceValue;                             /* 16 */
        unsigned char _rsvd[16];                                   /* 32 */
        PloQuadWord_t Op3;                                         /* 48 */
        unsigned char _rsvd1[8];                                   /* 64 */
        void* Op4Address;                                          /* 72 */
    } ploParmList;                                                 /* 80 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSSTX;
    memcpy(&(ploParmList.Op1CompareValue), &(swap_p->expectedValue), sizeof(PloQuadWord_t));
    memcpy(&(ploParmList.Op1ReplaceValue), &(swap_p->replaceValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd, 0, sizeof(ploParmList._rsvd));
    memcpy(&(ploParmList.Op3), &(store_p->storeValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd1, 0, sizeof(ploParmList._rsvd1));
    ploParmList.Op4Address = store_p->storeLocation_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}

/** Compare and swap and double store, double word area. */
int ploCompareAndSwapAndDoubleStoreDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p,
                                            PloStoreAreaDoubleWord_t* store1_p, PloStoreAreaDoubleWord_t* store2_p) {
    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long Op1CompareValue;                        /* 08 */
        unsigned char _rsvd1[8];                                   /* 16 */
        unsigned long long Op1ReplaceValue;                        /* 24 */
        unsigned char _rsvd2[24];                                  /* 32 */
        unsigned long long Op3;                                    /* 56 */
        unsigned char _rsvd3[8];                                   /* 64 */
        void* Op4Address;                                          /* 72 */
        unsigned char _rsvd4[8];                                   /* 80 */
        unsigned long long Op5;                                    /* 88 */
        unsigned char _rsvd5[8];                                   /* 96 */
        void* Op6Address;                                          /* 104 */
    } ploParmList;                                                 /* 112 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSDSTG;
    memset(ploParmList._rsvd, 0, sizeof(ploParmList._rsvd));
    ploParmList.Op1CompareValue = swap_p->expectedValue;
    memset(ploParmList._rsvd1, 0, sizeof(ploParmList._rsvd1));
    ploParmList.Op1ReplaceValue = swap_p->replaceValue;
    memset(ploParmList._rsvd2, 0, sizeof(ploParmList._rsvd2));
    ploParmList.Op3 = store1_p->storeValue;
    memset(ploParmList._rsvd3, 0, sizeof(ploParmList._rsvd3));
    ploParmList.Op4Address = store1_p->storeLocation_p;
    memset(ploParmList._rsvd4, 0, sizeof(ploParmList._rsvd4));
    ploParmList.Op5 = store2_p->storeValue;
    memset(ploParmList._rsvd5, 0, sizeof(ploParmList._rsvd5));
    ploParmList.Op6Address = store2_p->storeLocation_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}

/** Compare and swap and double store, quad word area. */
int ploCompareAndSwapAndDoubleStoreQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p,
                                            PloStoreAreaQuadWord_t* store1_p, PloStoreAreaQuadWord_t* store2_p) {
    struct {
        PloQuadWord_t Op1CompareValue __attribute__((aligned(8))); /* 00 */
        PloQuadWord_t Op1ReplaceValue;                             /* 16 */
        unsigned char _rsvd[16];                                   /* 32 */
        PloQuadWord_t Op3;                                         /* 48 */
        unsigned char _rsvd1[8];                                   /* 64 */
        void* Op4Address;                                          /* 72 */
        PloQuadWord_t Op5;                                         /* 80 */
        unsigned char _rsvd2[8];                                   /* 96 */
        void* Op6Address;                                          /* 104 */
    } ploParmList;                                                 /* 112 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSDSTX;
    memcpy(&(ploParmList.Op1CompareValue), &(swap_p->expectedValue), sizeof(PloQuadWord_t));
    memcpy(&(ploParmList.Op1ReplaceValue), &(swap_p->replaceValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd, 0, sizeof(ploParmList._rsvd));
    memcpy(&(ploParmList.Op3), &(store1_p->storeValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd1, 0, sizeof(ploParmList._rsvd1));
    ploParmList.Op4Address = store1_p->storeLocation_p;
    memcpy(&(ploParmList.Op5), &(store2_p->storeValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd2, 0, sizeof(ploParmList._rsvd2));
    ploParmList.Op6Address = store2_p->storeLocation_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}

/** Compare and swap and triple store, double word area. */
int ploCompareAndSwapAndTripleStoreDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p,
                                              PloStoreAreaDoubleWord_t* store1_p, PloStoreAreaDoubleWord_t* store2_p,
                                              PloStoreAreaDoubleWord_t* store3_p) {
    struct {
        unsigned char _rsvd[8] __attribute__((aligned(8)));        /* 00 */
        unsigned long long Op1CompareValue;                        /* 08 */
        unsigned char _rsvd1[8];                                   /* 16 */
        unsigned long long Op1ReplaceValue;                        /* 24 */
        unsigned char _rsvd2[24];                                  /* 32 */
        unsigned long long Op3;                                    /* 56 */
        unsigned char _rsvd3[8];                                   /* 64 */
        void* Op4Address;                                          /* 72 */
        unsigned char _rsvd4[8];                                   /* 80 */
        unsigned long long Op5;                                    /* 88 */
        unsigned char _rsvd5[8];                                   /* 96 */
        void* Op6Address;                                          /* 104 */
        unsigned char _rsvd6[8];                                   /* 112 */
        unsigned long long Op7;                                    /* 120 */
        unsigned char _rsvd7[8];                                   /* 128 */
        void* Op8Address;                                          /* 136 */
    } ploParmList;                                                 /* 144 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSTSTG;
    memset(ploParmList._rsvd, 0, sizeof(ploParmList._rsvd));
    ploParmList.Op1CompareValue = swap_p->expectedValue;
    memset(ploParmList._rsvd1, 0, sizeof(ploParmList._rsvd1));
    ploParmList.Op1ReplaceValue = swap_p->replaceValue;
    memset(ploParmList._rsvd2, 0, sizeof(ploParmList._rsvd2));
    ploParmList.Op3 = store1_p->storeValue;
    memset(ploParmList._rsvd3, 0, sizeof(ploParmList._rsvd3));
    ploParmList.Op4Address = store1_p->storeLocation_p;
    memset(ploParmList._rsvd4, 0, sizeof(ploParmList._rsvd4));
    ploParmList.Op5 = store2_p->storeValue;
    memset(ploParmList._rsvd5, 0, sizeof(ploParmList._rsvd5));
    ploParmList.Op6Address = store2_p->storeLocation_p;
    memset(ploParmList._rsvd6, 0, sizeof(ploParmList._rsvd6));
    ploParmList.Op7 = store3_p->storeValue;
    memset(ploParmList._rsvd7, 0, sizeof(ploParmList._rsvd7));
    ploParmList.Op8Address = store3_p->storeLocation_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}

/** Compare and swap and triple store, quad word area. */
int ploCompareAndSwapAndTripleStoreQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p,
                                              PloStoreAreaQuadWord_t* store1_p, PloStoreAreaQuadWord_t* store2_p,
                                              PloStoreAreaQuadWord_t* store3_p) {
    struct {
        PloQuadWord_t Op1CompareValue __attribute__((aligned(8))); /* 00 */
        PloQuadWord_t Op1ReplaceValue;                             /* 16 */
        unsigned char _rsvd[16];                                   /* 32 */
        PloQuadWord_t Op3;                                         /* 48 */
        unsigned char _rsvd1[8];                                   /* 64 */
        void* Op4Address;                                          /* 72 */
        PloQuadWord_t Op5;                                         /* 80 */
        unsigned char _rsvd2[8];                                   /* 96 */
        void* Op6Address;                                          /* 104 */
        PloQuadWord_t Op7;                                         /* 112 */
        unsigned char _rsvd3[8];                                   /* 128 */
        void* Op8Address;                                          /* 136 */
    } ploParmList;                                                 /* 144 */

    // Fill in the PLO parameter list.
    long long funcCode = PLO_CSTSTX;
    memcpy(&(ploParmList.Op1CompareValue), &(swap_p->expectedValue), sizeof(PloQuadWord_t));
    memcpy(&(ploParmList.Op1ReplaceValue), &(swap_p->replaceValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd, 0, sizeof(ploParmList._rsvd));
    memcpy(&(ploParmList.Op3), &(store1_p->storeValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd1, 0, sizeof(ploParmList._rsvd1));
    ploParmList.Op4Address = store1_p->storeLocation_p;
    memcpy(&(ploParmList.Op5), &(store2_p->storeValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd2, 0, sizeof(ploParmList._rsvd2));
    ploParmList.Op6Address = store2_p->storeLocation_p;
    memcpy(&(ploParmList.Op7), &(store3_p->storeValue), sizeof(PloQuadWord_t));
    memset(ploParmList._rsvd3, 0, sizeof(ploParmList._rsvd3));
    ploParmList.Op8Address = store3_p->storeLocation_p;

    ipm_map condCode;

    // Issue the PLO.  A condition code of zero indicates success.
    __asm(" LG 0,%1 PLO function code \n"
          " LG 1,%2 Lock word address \n"
          " PLO 0,0(%3),0,%4 \n"
          " IPM 7   Save the condition code \n"
          " STG 7,%0" :
          "=m"(condCode) : "m"(funcCode),"m"(lock_p),"r"(swap_p->compare_p),"m"(ploParmList) : "r0","r1","r7");

    // If the PLO failed, the PLO instruction will update the compare-and-swap compare value with
    // the value of op1 at the time of the compare.  We need to update the caller's parameter with
    // this updated compare value.
    if (condCode.cond_code != 0) {
        memcpy(&(swap_p->expectedValue), &(ploParmList.Op1CompareValue), sizeof(swap_p->expectedValue));
    }

    return condCode.cond_code;
}
