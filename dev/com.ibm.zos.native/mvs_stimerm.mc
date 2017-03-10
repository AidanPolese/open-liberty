/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#include <metal.h>
#include <stdlib.h>
#include <string.h>

#include "include/mvs_stimerm.h"
#include "include/mvs_utils.h"
#include "include/common_defines.h"

//                         0123456789012345
#define TIMER_ID_EYE      "BBGZ_STIMERM_ID "
#define STIMERM_PARMS_EYE "BBGZ_STIMERM_PRM"

/* List forms of macros */
__asm(" STIMERM SET,MF=L" : "DS"(set_list_form));
__asm(" STIMERM CANCEL,MF=L" : "DS"(cancel_list_form));

/** The timer ID structure */
struct timerID {
    char eyecatcher[16]; //!< Eyecatcher.
    struct stimermParms* below_p; //!< Below-the-bar storage used by STIMERM.
    PetVet* petvet_p; //!< The caller's PETVET reference.
};

/** Below-the-bar storage used by STIMERM. */
struct stimermParms {
    char eyecatcher[16]; //!< Eyecatcher.
    void* parms_p; //!< The parms to be passed to the timer exit.
    setTimerExitFunc_t* func_p; //!< Caller's exit function.
    iea_PEToken pet; //!< The PET for the timer.
    int stimermId; //!< The ID returned by STIMERM
    int waitTimeInHundreths; //!< The wait time, in hundreths of a second.
    int timeRemaining; //!< Time remaining when cancel called.
    int _available1; //!< Available for use (alignment).
    void* genericExit_p; //!< Internal exit function.
    void* metalC_env_p; //!< Metal C environment to use in exit.
    char set_dynamic[sizeof(set_list_form)]; //!< Area to build STIMERM SET execute form.
    char cancel_dynamic[sizeof(cancel_list_form)]; //!< Area to build STIMERM CANCEL execute form.
};

/**
 * Common routine called by stimerm generic exits.
 */
static void stimermGenericExitCommon(int* timerId_p, struct stimermParms* parms_p) {
    void* oldR12_p = getenvfromR12();
    setenvintoR12(parms_p->metalC_env_p);
    parms_p->func_p(parms_p->parms_p);

    // Release the PET.
    iea_return_code releaseRC;
    iea_auth_type authType = IEA_AUTHORIZED;
    iea_release_code releaseCode;
    memset(releaseCode, 0, sizeof(releaseCode));
    unsigned char currentKey = switchToKey0();
    iea4rls(&releaseRC, authType, parms_p->pet, releaseCode);
    switchToSavedKey(currentKey);

    setenvintoR12(oldR12_p);
}

/**
 * Generic exit function which obtains dynamic area and calls the real exit function
 * provided by the caller who set the timer.
 *
 * @param timerId_p Pointer to a full word containing the timer ID provided
 *                  by the STIMERM macro.
 * @param parms_p Pointer to the parameters provided to the STIMERM macro in
 *                the PARM field.
 */
#ifdef ANGEL_COMPILE
#pragma prolog(stimermGenericExit," STMRMPRL ENVIRON=ANGEL")
#elif SERVER_COMPILE
#pragma prolog(stimermGenericExit," STMRMPRL ENVIRON=SERVER,CLIENT=NO")
#else
#warning "Compiling in an unsupported environment"
#endif
#pragma epilog(stimermGenericExit,"STMRMEPL")
void stimermGenericExit(int* timerId_p, struct stimermParms* parms_p) {
    stimermGenericExitCommon(timerId_p, parms_p);
}

/**
 * Generic exit function which obtains dynamic area and calls the real exit function
 * provided by the caller who set the timer.  Tracing is disabled (for use in the
 * client where no tracing exists).
 *
 * @param timerId_p Pointer to a full word containing the timer ID provided
 *                  by the STIMERM macro.
 * @param parms_p Pointer to the parameters provided to the STIMERM macro in
 *                the PARM field.
 */
#ifdef ANGEL_COMPILE
#pragma prolog(stimermGenericExitNoTracing," STMRMPRL ENVIRON=ANGEL")
#elif SERVER_COMPILE
#pragma prolog(stimermGenericExitNoTracing," STMRMPRL ENVIRON=SERVER,CLIENT=YES")
#else
#warning "Compiling in an unsupported environment"
#endif
#pragma epilog(stimermGenericExitNoTracing,"STMRMEPL")
void stimermGenericExitNoTracing(int* timerId_p, struct stimermParms* parms_p) {
    stimermGenericExitCommon(timerId_p, parms_p);
}

/* Register a timer. */
int setTimer(setTimerExitFunc_t* inFunc_p, void* inParm_p, int seconds, PetVet* inPetVet_p, unsigned char inClient, MvsTimerID_t* outID_p) {

    if (seconds <= 0) {
        // Negative values are invalid and 0 typically means no timer.
        // So just return.  Null out the MvsTimerID in case cancelTimer is called.
        memset(outID_p, 0, sizeof(*outID_p));
        return 0;
    }

    // Allocate storage for the return struct.
    struct timerID* returnStruct_p = malloc(sizeof(*returnStruct_p));
    if (returnStruct_p == NULL) {
        return -1;
    }

    // Get a PET from the PETVET.
    iea_PEToken thePet;
    int petVetRC = pickup(inPetVet_p, &thePet);
    if (petVetRC != 0) {
        free(returnStruct_p);
        return -2;
    }

    // We must pass storage to STIMERM that is below the bar.  Go get
    // some storage that's below the bar.
    struct stimermParms* __ptr32 parmsBelow_p = __malloc31(sizeof(*parmsBelow_p));
    if (parmsBelow_p == NULL) {
        board(inPetVet_p, thePet);
        free(returnStruct_p);
        return -3;
    }

    // Set up the below-the-bar area to pass to STIMERM.
    memset(parmsBelow_p, 0, sizeof(parmsBelow_p));
    memcpy(parmsBelow_p->eyecatcher, STIMERM_PARMS_EYE, sizeof(parmsBelow_p->eyecatcher));
    parmsBelow_p->parms_p = inParm_p;
    parmsBelow_p->func_p = inFunc_p;
    memcpy(&(parmsBelow_p->pet), &thePet, sizeof(parmsBelow_p->pet));
    parmsBelow_p->waitTimeInHundreths = (int) BBGZ_min( (long long) seconds * 100, 0x7FFFFFFFL );
    parmsBelow_p->genericExit_p = (inClient == TRUE) ? ((void*)&stimermGenericExitNoTracing) : ((void*) &stimermGenericExit);
    parmsBelow_p->metalC_env_p = getenvfromR12();
    memcpy(parmsBelow_p->set_dynamic, &set_list_form, sizeof(set_list_form));
    memcpy(parmsBelow_p->cancel_dynamic, &cancel_list_form, sizeof(cancel_list_form));

    // Call STIMERM
    int rc = -4;
    __asm(" STIMERM SET,ID=%1,BINTVL=%2,EXIT=(%3),WAIT=NO,PARM=(%4),ERRET=STIMERR1,MF=(E,%5)\n"
          "STIMERR1 DS 0H\n"
          " ST 15,%0" : "=m"(rc) :
          "m"(parmsBelow_p->stimermId),"m"(parmsBelow_p->waitTimeInHundreths),
          "r"(parmsBelow_p->genericExit_p),"r"(&parmsBelow_p),"m"(parmsBelow_p->set_dynamic) :
          "r0","r1","r14","r15");

    if (rc == 0) {
        memcpy(returnStruct_p->eyecatcher, TIMER_ID_EYE, sizeof(returnStruct_p->eyecatcher));
        returnStruct_p->below_p = parmsBelow_p;
        returnStruct_p->petvet_p = inPetVet_p;
        memcpy(outID_p, &returnStruct_p, sizeof(*outID_p));
    } else {
        free(parmsBelow_p);
        board(inPetVet_p, thePet);
        free(returnStruct_p);
    }

    return rc;
}

/* Cancel / reclaim a timer. */
int cancelTimer(MvsTimerID_t* inID_p) {

    struct timerID* returnStruct_p;
    memcpy(&returnStruct_p, inID_p, sizeof(returnStruct_p));

    if (returnStruct_p == NULL) {
        // A NULL MvsTimerID normally means that setTimer was called with seconds <= 0
        // and so no timer was actually started, so we should just return.
        // Or in the abnormal case it means the caller passed us a bad MvsTimerID.  
        // We'll ABEND if we try to use it now. Might as well just return and if the caller 
        // really needed that timer cancelled, well, maybe something will blow up later 
        // because of it, but no need to blow up now.
        return 0;
    }

    // Call STIMERM
    int rc = -1;
    __asm(" STIMERM CANCEL,ID=%1,ERRET=STIMERR2,TU=%2,MF=(E,%3)\n"
          "STIMERR2 DS 0H\n"
          " ST 15,%0" : "=m"(rc) :
          "m"(returnStruct_p->below_p->stimermId),"m"(returnStruct_p->below_p->timeRemaining),
          "m"(returnStruct_p->below_p->cancel_dynamic) :
          "r0","r1","r14","r15");

    // If we did not cancel the timer, we assume that the exit routine ran and
    // resumed the PET.  So we must pause on the PET.
    if ((rc > 4) ||
        ((rc == 0x00) && (returnStruct_p->below_p->timeRemaining == 0))) {
        // Issue pause, waiting for the exit to finish.
        iea_return_code pauseRC;
        iea_auth_type pauseAuthType = IEA_AUTHORIZED;
        iea_release_code releaseCode;
        iea_PEToken newPET;
        unsigned char currentKey = switchToKey0();
        iea4pse(&pauseRC, pauseAuthType, returnStruct_p->below_p->pet, newPET, releaseCode);
        switchToSavedKey(currentKey);

        // Put new PET back in the PETVET
        if (pauseRC == 0) {
            board(returnStruct_p->petvet_p, newPET);
        }

        rc = 1;  // We waited for the exit to run.
    } else {
        board(returnStruct_p->petvet_p, returnStruct_p->below_p->pet);
    }

    // Reclaim resources.
    free(returnStruct_p->below_p);
    free(returnStruct_p);

    return rc;
}

#pragma insert_asm(" IEANTASM")
