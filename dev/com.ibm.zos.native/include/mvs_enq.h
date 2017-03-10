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
#ifndef _BBOZ_MVS_ENQ_H
#define _BBOZ_MVS_ENQ_H

#include "gen/isgyquaa.h"

#define BBGZ_ENQ_QNAME "SYSZBBO"
#define BBGZ_UNAUTH_ENQ_QNAME "BBGZ"

#define BBGZ_ENQ_MAX_RNAME_LEN 255

/**
 * ENQ RNAME for the default angel process, enforcing a single default
 * (un-named) angel process.
 */
#define ANGEL_PROCESS_RNAME "BBG:AENQ1:MAIN"

/**
 * ENQ RNAME used to serialize access to the SGOO control block from the MASTER address space.
 * This is used in the various RESMGRs when running in MASTER.
 */
#define ANGEL_BBGZSGOO_ENQ "BBG:AENQ2:BBGZSGOO"

/**
 * ENQ RNAME used to synchronize registration with the Angel
 */
#define REGISTRATION_ENQ_RNAME "BBG:AENQ3:REGISTER"

/**
 * ENQ RNAME pattern used to identify a registered server.  The stoken
 * of the server follows the final colon.
 */
#define ANGEL_PROCESS_SERVER_ENQ_RNAME_PATTERN "BBG:AENQ4:STOKEN:%llx"

/**
 * This RNAME pattern can be used with GQSCAN to find all registered servers.
 */
#define ANGEL_PROCESS_SERVER_ENQ_RNAME_QUERY   "BBG:AENQ4:STOKEN:*"

/**
 * ENQ RNAME used to synchronize connection to the ARMV (for when the Angel code changes underneath the server)
 */
#define ANGEL_ARMV_CONNECT_ENQ "BBG:AENQ5:ARMV"

/**
 * ENQ RNAME used to synchronize creation of the unauthorized metal C environment.
 */
#define SERVER_UNAUTH_MCRTL_ENQ "BBG:AENQ6:UNAUTH_MCRTL"

/**
 * ENQ RNAME used to serialize the client bind list in the angel process
 * data.  The Liberty server STOKEN follows the final colan.
 */
#define ANGEL_PROCESS_CLIENT_BIND_LIST_ENQ_PATTERN "BBG:AENQ7:LIBERTY:BINDLIST:%llx"

/**
 * ENQ RNAME used in Local Comm to "find" active server.  First fillin is the address
 * of the BBGZLCOM of the server (shared memory anchor address).
 * TODO: Replace this ENQ with a system level name token used by the client to find the
 *       server's LOCL address.
 */
#define SERVER_LCOM_READY_ENQ_RNAME_PATTERN "BBG:AENQ8:LCOM:%p"

/**
 * This RNAME pattern can be used in a client with GQSCAN to find a Liberty
 * server.
 */
#define SERVER_LCOM_READY_ENQ_RNAME_QUERY "BBG:AENQ8:LCOM:*"

/**
 * The ENQ Rname pattern used to advertise the Wola server's presence.
 * Clients can lookup this ENQ using the 3-part WOLA name.  The ENQ registration
 * contains the Liberty server's STOKEN.  The client uses the STOKEN to find
 * and connect to the server.
 *
 * BBG:AENQ8:LCOM:<wolaGroup><wolaName2><wolaName3>
 *
 */
#define ADVERTISE_WOLA_SERVER_ENQ_RNAME_PATTERN "BBG:AENQ9:LCOM:%.8s%.8s%.8s"

/**
 * The ENQ used to serialize creation of the CGOO, SGOO and the named angel anchors
 * on angel startup.
 */
#define ANGEL_CGOO_SGOO_ANCHOR_INITIALIZATION_ENQ_RNAME "BBG:AENQ10:CREATESGOO"

/**
 * The RNAME pattern held by a named angel, enforcing a single angel per name.
 */
#define ANGEL_NAMED_PROCESS_RNAME "BBG:AENQ11:MAIN:%s"

/**
 * This RNAME pattern can be used to query all active angels on the system.
 */
#define ANGEL_NAMED_PROCESS_RNAME_QUERY "BBG:AENQ11:MAIN:*"


/**
 * ENQ RNAME pattern used to identify server that registered with a named angel.
 * The STOKEN of the server follows the third colon.  The angel name follows the
 * fourth colon, and is padded out to 54 characters.
 */
#define ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_PATTERN "BBG:AENQ12:STOKEN:%16.16llX:%.54s"

/**
 * This RNAME pattern can be used with SSCANF to parse only the STOKEN from a server
 * registered with a named angel.  Note that the angel name is ignored.
 */
#define ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_PATTERN2 "BBG:AENQ12:STOKEN:%llx"

/**
 * This RNAME pattern can be used with GQSCAN to find all servers that have
 * registered with a named angel.  The STOKEN follows the third colon and should
 * be filled in using snprintf before scanning.
 */
#define ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_QUERY   "BBG:AENQ12:STOKEN:%16.16llX:*"

/**
 * This RNAME query can be used with GQSCAN to find all servers that are registered
 * with a named angel.  The angel name follows the fourth colon and should be filled
 * in using snprintf before scanning.
 */
#define ANGEL_NAMED_PROCESS_SERVER_ENQ_RNAME_QUERY2  "BBG:AENQ12:STOKEN:*:%.54s"

/**
 * ENQ RNAME used by client bind, to ensure there is only one client bind
 * or unbind occurring at any given time in the client.
 */
#define CLIENT_BIND_ENQ_RNAME "BBG:CENQ1:BIND"

/**
 * ENQ RNAME used by clients when attaching or detaching from WOLA shared
 * memory.
 */
#define CLIENT_WOLA_ATTACH_SHMEM_ENQ_RNAME "BBG:CENQ2:WOLA_ATTACH"

/**
 * ENQ RNAME used by WOLA clients when re-binding to a Liberty server instance
 * after the server is recycled.
 */
#define CLIENT_WOLA_REBIND_ENQ_RNAME "BBG:CENQ3:WOLA_REBIND"

/**
 * ENQ RNAME used by the client when expanding a BBGZLOCL.
 */
#define CLIENT_LOCAL_COMM_LOCL_EXPANSION_ENQ_RNAME "BBG:CENQ4:LOCAL_COMM_LOCL_EXPAND"

/**
 * The enqtoken is returned when an ENQ is obtained, and is used to free
 * the ENQ later.
 */
typedef struct enqtoken enqtoken;
struct enqtoken {
  char token[32];
};

/**
 * Obtains the named ENQ is exclusive mode with a scope of SYSTEM, so that no
 * other task in the z/OS system can obtain the ENQ.
 *
 * @param qname The QNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 8 bytes.
 * @param rname The RNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 255 bytes.
 * @param ottoken The task token (TToken) of the task to own the ENQ. If null,
 *                CURRENT_TASK will be used.
 * @param token_p A pointer to an enqtoken which will be filled in after the
 *                ENQ is obtained.  The token must be provided when freeing
 *                the ENQ.
 */
void get_enq_exclusive_system(char* qname,
                              char* rname,
                              char* ottoken,
                              enqtoken* token_p);


/**
 * Obtains the named ENQ in exclusive mode with a scope of SYSTEM, if it is not
 * already held by another task on the system. Will Obtain the enqueue with
 * ottoken if it is not null. If the ENQ is obtained, no other
 * task in the z/OS system can obtain the ENQ until the ENQ is released.
 *
 * @param qname The QNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 8 bytes.
 * @param rname The RNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 255 bytes.
 * @param ottoken The task token (TToken) of the task to own the ENQ. If null,
 *                CURRENT_TASK will be used.
 * @param token_p A pointer to an enqtoken which will be filled in after the
 *                ENQ is obtained.  The token must be provided when freeing
 *                the ENQ.
 */
int get_enq_exclusive_system_conditional_token(char* qname,
                              char* rname,
                              char* ottoken,
                              enqtoken* token_p);
/**
 * Obtains the named ENQ in exclusive mode with a scope of SYSTEM, if it is not
 * already held by another task on the system.  If the ENQ is obtained, no other
 * task in the z/OS system can obtain the ENQ until the ENQ is released.
 *
 * @param qname The QNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 8 bytes.
 * @param rname The RNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 255 bytes.
 * @param token_p A pointer to an enqtoken which will be filled in after the
 *                ENQ is obtained.  The token must be provided when freeing
 *                the ENQ.
 *
 * @return 0 if the ENQ was obtained, 1 if the ENQ was held by another task,
 *         -1 if another error occurred.
 */
int get_enq_exclusive_system_conditional(char* qname, char* rname, enqtoken* token_p);

/**
 * Obtains the named ENQ is exclusive mode with a scope of STEP, so that no
 * other task in this address space can obtain the ENQ.
 *
 * @param qname The QNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 8 bytes.
 * @param rname The RNAME to use on the ENQ obtain.  This should be a null
 *              terminated string of no more than 255 bytes.
 * @param token_p A pointer to an enqtoken which will be filled in after the
 *                ENQ is obtained.  The token must be provided when freeing
 *                the ENQ.
 */
void get_enq_exclusive_step(char* qname, char* rname, enqtoken* token_p);

/** Returned by test_enq_step, task does not currently hold ENQ. */
#define TASK_DOES_NOT_OWN 0
/** Returned by test_enq_step, task holds ENQ in exclusive mode. */
#define TASK_OWNS_EXCLUSIVE 1
/** Returned by test_enq_step, task holds ENQ in shared mode. */
#define TASK_OWNS_SHARED 2

/**
 * Tests to see if the current task holds the ENQ with a scope of STEP.
 *
 * @param qname The QNAME to use on the ENQ test.  This should be a null
 *              terminated string of no more than 8 bytes.
 * @param rname The RNAME to use on the ENQ test.  This should be a null
 *              terminated string of no more than 255 bytes.
 * @param token_p A pointer to an enqtoken which will be filled in if the
 *                current task holds the ENQ.
 *
 * @return TASK_OWNS_EXCLUSIVE if this task owns the ENQ in exclusive mode,
 *         TASK_OWNS_SHARED if this task owns the ENQ in shared mode, or
 *         TASK_DOES_NOT_OWN if this task does not currently own the ENQ.
 */
int test_enq_step(char* qname, char* rname, enqtoken* token_p);

/**
 * Releases an ENQ.
 *
 * @param token_p A pointer to the enqtoken provided when the ENQ was
 *                obtained.
 */
void release_enq(enqtoken* token_p);

/**
 * Releases an ENQ.
 *
 * @param token_p A pointer to the enqtoken provided when the ENQ was
 *                obtained.
 * @param owningTToken_p A pointer to the TToken of the TCB owning this
 *                resource.
 */
void release_enq_owning(enqtoken* token_p, char* owningTToken_p);

/**
 * Performs an ISGQUERY, scanning for ENQs held on the system.
 *
 * @param qname The QNAME to scan for.  This should be a null terminated
 *              string of no more than 8 bytes.
 * @param rname_pattern The RNAME pattern to search for.  The RNAME can
 *                      contain an asterisk which will match zero or more
 *                      characters.
 * @param rc_p A pointer to an int which will contain the return code
 *             from the ISGQUERY macro.
 * @param rsn_p A pointer to an int which will contain the reason code
 *              from the ISGQUERY macro.
 *
 * @return A pointer to the isgyquaahdr structure returned by ISGQUERY, or
 *         NULL if the service failed.  The caller owns this storage and
 *         must free it using free().  The isgyquaahdr mapping in the
 *         generated headers can be used to traverse the ENQs.
 */
isgyquaahdr* scan_enq_system(char* qname, char* rname_pattern, int* rc_p, int* rsn_p);

#endif
