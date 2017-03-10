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
#ifndef _BBOZ_ANGEL_PC_INITIALIZATION_H
#define _BBOZ_ANGEL_PC_INITIALIZATION_H

#include "bbgzsgoo.h"
#include "angel_fixed_shim_module.h"
/**@file
 * Defines Angel Initialization functions
 */

/**
 * Registers the PC routine entry points (in the fixed shim module) with
 * z/OS, and sets the PC number for the Register service into the CGOO.
 *
 * @param fsm_p A pointer to the fixed shim module where the PC entry points
 *              can be found.
 * @param cgoo_p A pointer to the CGOO.  This is where the PC number for
 *               register is stored.
 * @param latentParm_p A pointer which will be set as a latent parm on the
 *                     PC routine.  The latent parm is passed to the PC caller
 *                     when they invoke the PC routine, in a register.  This
 *                     must be a pointer to 31 bit storage.
 */
void createPC(struct bbgzafsm* fsm_p, bbgzcgoo* cgoo_p, void* __ptr32 latentParm_p);

#endif

