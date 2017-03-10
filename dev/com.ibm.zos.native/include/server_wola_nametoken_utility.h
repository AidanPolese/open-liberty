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
#ifndef _BBOZ_SERVER_WOLA_NAMETOKEN_UTILITY_H
#define _BBOZ_SERVER_WOLA_NAMETOKEN_UTILITY_H

/**
 * Lookup a primary level name token that was set by an authorized program.
 *
 * This works around a bug in the 64 bit name token services where the high
 * half of R1 is not cleared, resulting in an ABEND 0C4 PIC10 inside the
 * name token service.
 *
 * @param name_p The 16 byte token name
 * @param token_p A 16 byte area where the token will be copied if RC=0
 * @param auth Set to zero if the name token can be created by an unauthorized
 *             program.
 *
 * @return The return code from iean4rt (RC = 0 means the lookup was successful),
 *         or -1 if out of memory.
 */
int lookupPrimaryNameToken(char* name_p, char* token_p, int auth);

/**
 * Get the registration for the specified register name.
 *
 * @param registerName_p      pointer to register name
 * @param registrationData_p  pointer to output area that gets updated with registration data when registration is found.
 *
 * @return Pointer to registration. 0 if not found.
 */
int  getRegisterNameToken(char* registerName_p, char* token);

#endif
