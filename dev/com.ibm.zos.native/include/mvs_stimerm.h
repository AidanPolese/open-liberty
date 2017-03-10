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
#ifndef _BBOZ_MVS_STIMERM_H
#define _BBOZ_MVS_STIMERM_H

#include "petvet.h"

/**
 * Function prototype to be used by the timer exit.
 *
 * @param inParm_p A pointer to storage provided on the setTimer function.
 */
typedef void setTimerExitFunc_t(void* inParm_p);


/** Timer ID */
typedef unsigned long long MvsTimerID_t;


/**
 * Registers a timer which will call the specified input function after
 * the number of seconds has elapsed, unless the timer is cancelled before
 * it has expired.
 *
 * The setTimer function is intended to be used by tasks which must wait
 * synchronously for some event to occur.  The cancelTimer function must
 * always be driven, to release system resources.  The cancelTimer function
 * should not be driven by the timer exit.  The timer exit may behave
 * unpredictably if the task which drove setTimer returns to its caller before
 * the exit has been driven.
 *
 * Example flow:
 *   int rc = setTimer(inFunc, inParms, petvet, id_p);
 *   if (rc == 0) {
 *     // do something that waits, which inFunc can break us out of...
 *     cancelTimer(id_p);
 *   }
 *
 * An example of what not to do:
 *   void methodA() {
 *     setTimerExitFunc_t* inFunc_p = methodB;
 *     int rc = setTimer(inFunc, inParms, petvet, id_p);
 *     // Save id_p somewhere
 *     return;
 *   }
 *
 *   void methodB(void* inParms) {
 *     // get id_p from somewhere
 *     cancelTimer(id_p);
 *   }
 *
 * @param inFunc_p The function that is to be driven when the timer expires.
 *                 The function may be driven on any task.
 * @param inParm_p A pointer to some storage which will be passed to inFunc when
 *                 it is driven.  This can be used to pass a parameter list to
 *                 inFunc.
 * @param seconds The number of seconds to schedule the timer to run in.
 * @param inPetVet_p A pointer to a PETVET which can be used to obtain a PET.
 * @param inClient Set to TRUE if we're running (or could be running) in a client
 *                 process.  We won't set up tracing.
 * @param outID_p A pointer to a double word which identifies the timer instance.
 *                this must be provided to the cancelTimer function.
 *
 * @return 0 if the timer was set successfully.  Non-zero if error (this is the
 *         return code from the STIMERM macro).
 */
int setTimer(setTimerExitFunc_t* inFunc_p, void* inParm_p, int seconds, PetVet* inPetVet_p, unsigned char inClient, MvsTimerID_t* outID_p);


/**
 * Cancels a timer which had been previously set.  This also reclaims system
 * resources associated with the timer, so it must always be called, even if the
 * timer exit has already been driven.
 *
 * @param inID_p
 *
 * @return 0 if the timer was cancelled successfully, before the exit was driven.
 *         1 if the exit was driven.
 *         Other return codes are error codes from the STIMERM macro.
 */
int cancelTimer(MvsTimerID_t* inID_p);

#endif
