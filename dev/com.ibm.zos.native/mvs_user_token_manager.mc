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
#include <string.h>

#include "include/angel_bgvt_services.h"
#include "include/mvs_user_token_manager.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihaascb.h"
#include "include/gen/ihaassb.h"

//---------------------------------------------------------------------------
// Get a user token at the address space level for a problem state program.
//---------------------------------------------------------------------------
long long
getAddressSpaceProblemStateUserToken(void) {
    //-----------------------------------------------------------------------
    // Problem state programs need to have the left word of the user token
    // be zero.  The right word can be anything.  We'd like to use the PGOO
    // address, but there might not be one, and it's also 8 bytes which
    // won't fit in the right word.  We'll use the ASCB address instead.
    //-----------------------------------------------------------------------
    union {
        struct {
            void* __ptr32 null_p;
            void* __ptr32 ascb_p;
        } usertoken_s;

        long long usertoken_l;
    } usertoken_u;

    psa* psa_p = NULL;
    usertoken_u.usertoken_s.null_p = NULL;
    usertoken_u.usertoken_s.ascb_p = psa_p->psaaold;

    return usertoken_u.usertoken_l;
}

//---------------------------------------------------------------------------
// Get a user token at the task level for a problem state program.
//---------------------------------------------------------------------------
long long
getTaskProblemStateUserToken(void) {
    //-----------------------------------------------------------------------
    // Problem state programs need to have the left word of the user token
    // be zero.  The right word can be anything.  We'd like to use the TGOO
    // address, but there might not be one.  We'll use the TCB address
    // instead.
    //-----------------------------------------------------------------------
    union {
        struct {
            void* __ptr32 null_p;
            void* __ptr32 tcb_p;
        } usertoken_s;

        long long usertoken_l;
    } usertoken_u;

    psa* psa_p = NULL;
    usertoken_u.usertoken_s.null_p = NULL;
    usertoken_u.usertoken_s.tcb_p = psa_p->psatold;

    return usertoken_u.usertoken_l;
}

//---------------------------------------------------------------------------
// Get a user token at the address space level for a supervisor state program.
//---------------------------------------------------------------------------
long long
getAddressSpaceSupervisorStateUserToken(void) {
    //-----------------------------------------------------------------------
    // Supervisor state programs need to have the left word of the user
    // token be non-zero.  The right word can be anything.  We'll use the
    // STOKEN for the address space, since it will fit into 8 bytes.
    //-----------------------------------------------------------------------
    union {
        char stoken[8];
        long long usertoken_l;
    } usertoken_u;

    psa* psa_p = NULL;
    ascb* ascb_p = (ascb*) psa_p->psaaold;
    assb* assb_p = (assb*) ascb_p->ascbassb;

    memcpy(usertoken_u.stoken, &(assb_p->assbstkn), sizeof(usertoken_u.stoken));

    return usertoken_u.usertoken_l;
}

//---------------------------------------------------------------------------
// Get a user token at the address space level for a supervisor state program.
//---------------------------------------------------------------------------
long long getAddressSpaceSupervisorStateUserTokenWithBias(UserTokenBias_t* bias_p){
    //-----------------------------------------------------------------------
    // Supervisor state programs need to have the left word of the user
    // token be non-zero.  The right word can be anything.  We'll use the
    // STOKEN for the address space, since it will fit into 8 bytes.  We then
    // add the bias, which goes in the first 4 bits.  The MVS data areas book
    // says that the first 4 bits of the STOKEN are reserved and will always
    // be zero for address space STOKENs.  We'll use these 4 bits for the
    // bias.
    //-----------------------------------------------------------------------
    struct biasStruct {
        int bias:4,
            _rsvd:28;
        int _rsvd2;
    };

    union {
        char stoken[8];
        long long usertoken_l;
        struct biasStruct bias;
    } usertoken_u;

    psa* psa_p = NULL;
    ascb* ascb_p = (ascb*) psa_p->psaaold;
    assb* assb_p = (assb*) ascb_p->ascbassb;

    memcpy(usertoken_u.stoken, &(assb_p->assbstkn), sizeof(usertoken_u.stoken));
    usertoken_u.bias.bias = bias_p->bias;

    return usertoken_u.usertoken_l;
}

//---------------------------------------------------------------------------
// Get a user token at the task level for a supervisor state program.
//---------------------------------------------------------------------------
long long
getTaskSupervisorStateUserToken(void) {
    //-----------------------------------------------------------------------
    // Supervisor state programs need to have the left word of the user
    // token be non-zero.  The right word can be anything.  The TTOKEN is
    // too long.  We might not have a TGOO yet.  Use the TCB address for now,
    // knowing that the TCB address could be re-used within the address
    // space.
    //-----------------------------------------------------------------------
    union {
        struct {
            void* __ptr32 tcb_p;
            void* __ptr32 null_p;
        } usertoken_s;
        long long usertoken_l;
    } usertoken_u;

    psa* psa_p = NULL;
    usertoken_u.usertoken_s.null_p = NULL;
    usertoken_u.usertoken_s.tcb_p = psa_p->psatold;

    return usertoken_u.usertoken_l;
}

//---------------------------------------------------------------------------
// Get a user token at the system level for a supervisor state program.
//---------------------------------------------------------------------------
long long
getSystemSupervisorStateUserToken(void) {
    //-----------------------------------------------------------------------
    // Supervisor state programs need to have the left word of the user
    // token be non-zero.  The right word can be anything.  We'll use the
    // master BGVT address in the left word, because we are guaranteed to
    // have one, and we'll leave the right half blank.
    //-----------------------------------------------------------------------
    union {
        struct {
            void* __ptr32 bgvt_p;
            void* __ptr32 null_p;
        } usertoken_s;

        long long usertoken_l;
    } usertoken_u;

    bgvt* __ptr32 bgvt_p = findBGVT();
    usertoken_u.usertoken_s.bgvt_p = bgvt_p;
    usertoken_u.usertoken_s.null_p = 0L;

    return usertoken_u.usertoken_l;
}
