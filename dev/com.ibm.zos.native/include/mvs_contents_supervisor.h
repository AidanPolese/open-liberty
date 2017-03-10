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
#ifndef _BBOZ_MVS_CONTENTS_SUPERVISOR_H
#define _BBOZ_MVS_CONTENTS_SUPERVISOR_H

#include "gen/bpxycons.h"
#include "gen/csvlpret.h"

/**
 * @file
 * Wrappers for the MVS contents supervisor macros used by the Liberty profile.
 */

/**
 * Obtains the address where a load module was loaded and the length of the
 * load module that was loaded, given the entry point of the module.\
 *
 * @param entrypt_p The entry point address to the module in question.
 * @param length_p A pointer to a double word field where the length of the
 *                 module is stored.
 * @param addr_p A pointer to a double word field where the address that the
 *               module was loaded is stored.
 *
 * @return The return code from CSVQUERY.
 */
int contentsSupervisorQueryFromEntryPoint(void* entrypt_p, unsigned long long* length_p, void** addr_p);

/**
 * Tells contents supervision about modules that have been loaded into common.
 *
 * @param pathName the null terminated path name of the module
 * @param entry_point the module entry point
 * @param module_start the starting address of the module
 * @param module_length the length of the module
 * @param modinfo_p Pointer to an lpmea struct which is filled in by CSVDYLPA.
 * @param rsn_p Pointer to a full word where the reason code from CSVDYLPA is
 *              stored.

 * @return The return code from the CSVDYLPA service.
 */
int contentsSupervisorAddToDynamicLPA(const char* pathName, void* entry_point, void* module_start, int module_length, lpmea* modinfo_p, int* rsn_p);

/**
 * Tell content supervision to delete a module that's been loaded into common.
 *
 * @param delete_token the token generated when the module was added
 * @param rsn_p a pointer to a full word to store the reason code
 *
 * @return the return code from CSVDYLPA
 */
int contentsSupervisorDeleteFromDynamicLPA(const char* delete_token, int* rsn_p);

#endif
