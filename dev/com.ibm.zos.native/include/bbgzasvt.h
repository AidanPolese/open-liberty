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
#ifndef _BBOZ_BBGZASVT_H
#define _BBOZ_BBGZASVT_H
/**@file
 * Angel Service Vector Table
 */
#define BBGZASVT_EYE "BBGZASVT"
#define BBGZASVT_EYE_END "BBGZASVT END END"
#define BBGZASVT_MAX_ENTRIES 1000

#pragma pack(1)

/** Version 1 of the ASVT has only server functions. */
#define BBGZASVT_VERSION_1 1

/**
 * Version 2 of the ASVT has server and client functions.  Some of the functions
 * take an extra parameter, but this is otherwise functionally equivalent to
 * a version 1 ASVT.
 */
#define BBGZASVT_VERSION_2 2

/**
 * Structure of a generic vector table header.  All valid vector
 * tables will start with this header, and end with a 16 byte field
 * containing the END eye catcher.
 */
typedef struct bbgzasvt_header {
    /**
     * The name of the module which the vector table is for.  IE. this is set
     * to BBGZSAFM for the BBGZSAFM load module.
     */
    char module_name[8];

    /**
     * Eye catcher for the control block (BBGZASVT).
     */
    char eyecatcher[8];

    /**
     * Version number for the control block.
     */
    int  version;

    /**
     * Flags for the control block.
     */
    struct {
        /**
         * When the angel calls the server's common function module, it should generate a
         * clientToken rather than use the server's STOKEN as the clientToken.
         */
        int generateClientToken:1;

        /**
         * Available for use.
         */
        int _available : 31;
    } flags;

    /**
     * The size of the control block, starting from the eyecatcher (the length
     * of module_name is not included in the size).
     */
    int  size;

    /**
     * The number of entries contained in the vector table following the header.
     */
    unsigned int  num_entries;

    /**
     * A string representing the version number of the module.
     */
    char version_string[32]; /* Use a multiple of 16 bytes */

    /**
     * A function which performs process related initialization for the module.
     *
     * For modules that run in the server process, this is called when the
     * module is loaded by the angel.  The function may be called more than
     * once if the server deregisters with the angel, and then registers again.
     *
     * For modules that run in a client process, this is called when the client
     * establishes its first bind to the server process who loaded the module.
     * The function may be called more than once if the client unbinds all
     * binds to the server, and then re-binds to the server.
     *
     * @param token An 8 byte token which can be used in a client address space
     *              to cache process level information.  For example, if the
     *              code wishes to establish a process level name token, part
     *              of the name should could contain this token to prevent a
     *              collision which could occur if the client established a
     *              bind to two different servers running two different
     *              versions of the module.  For modules which run in the
     *              server process, this token is not filled in and should not
     *              be used.
     *
     * @return 0 on success, nonzero on failure.
     */
    int (* process_initialization_routine_ptr)(unsigned long long token);

    /**
     * A function which constructs the environment for an invokable service,
     * and calls the service.  Setting up the environment may consist of
     * validating task related control blocks and establishing a metal C
     * environment for the invokable service to use.
     *
     * @param fcn_ptr The pointer to the function this routine should invoke
     *                after setting up the environment.
     * @param parms_p The parameters to pass to fcn_ptr.
     * @param token For modules which run in the client process, the 8 byte
     *              token which was provided on the process initialization
     *              routine.  For modules which run in the server process, this
     *              token is not filled in and should not be used.
     */
    void (* setupEnvironmentAndCallInvokableService)(void (* fcn_ptr)(void*), void* parms_p, unsigned long long token);

    /**
     * A function which performs process related cleanup for the module.
     *
     * For modules which run in the server process, this is called immediately
     * before the module is deleted by the angel.  The function may be called
     * more than once if the server deregisters with the angel, and then
     * registers again (and subsequently deregisters again).
     *
     * For modules which run in the client process, this is called when the
     * last unbind occurs for a client server pair.  The function may be called
     * more than once if the client unbinds, then re-binds (and subsequently
     * unbinds again).
     *
     * @param token For modules which run in the client process, the 8 byte
     *              token which was provided on the process initialization
     *              routine.  For modules which run in the server process, this
     *              token is not filled in and should not be used.
     */
    void (* process_cleanup_routine_ptr)(unsigned long long token);

    /**
     * A function which performs task related cleanup for the module.  The
     * function can be invoked in one of two ways, depending on the value of
     * the tcb_p parameter.  When tcb_p is NULL, cleanup is occurring for the
     * current task, during task termination.  When tcb_p is not NULL, cleanup
     * is occurring for another task.  In this case, the caller must hold
     * the local lock, and the caller guarantees that the task being cleaned
     * up will not be dispatched during cleanup.
     *
     * This function is only called for modules which run in the server process.
     *
     * @param tcb_p A pointer to the TCB being cleaned up.  If NULL, the current
     *              TCB is being cleaned up.  If non-NULL, the local lock is held
     *              and this varible points to the TCB being cleaned up.
     */
    void (* task_cleanup_routine_ptr)(void* tcb_p);
} bbgzasvt_header;
#pragma pack(reset)

#pragma pack(1)
/**
 * Structure of a vector table entry.  This goes at the bottom of
 * the dll header and occurs once per function that the DLL provides
 * ASVE: Authorized Services Vector table Entry
 */
typedef struct bbgzasve {
    /**
     * A descriptive name of the function.
     */
    char bbgzasve_name[8];

    /**
     * The name which is appended to the SAF profile name for this module, to
     * determine if the server is allowed to use this particular function.
     */
    char bbgzasve_auth_name[8];

    /**
     * A pointer to the metal C function implementing this function.
     */
    void (* bbgzasve_fcn_ptr)(void*);

    /**
     * A pointer to an error routine to call in the event the function pointer
     * cannot be used.  This is currently unimplemented.
     */
    void (* bbgzasve_err_ptr)();

    /**
     * An area to put flags for this function.  These flags are set in the load
     * module and are not modified by the angel process.
     */
    struct {
        /**
         * Available for use.
         */
        int _available : 16;
    } bbgzasve_loadmod_bits;

    union {
        /**
         * An area to put flags for this function.  These flags are not set in
         * the load module and are intended to be modified by the angel process.
         */
        struct {
            /**
             * Flag set if this function passes the SAF authorization check.
             */
            int authorized_to_use : 1;

            /**
             * Available for use.
             */
            int : 15;
        } bbgzasve_runtime_bits;

        /**
         * Convenience field to use when clearing the runtime bits.
         */
        unsigned short bbgzasve_runtime_bits_zeros;
    };

    /**
     * Available for use.
     */
    unsigned char bbgzasve_rsvd1[12];
} bbgzasve;

#pragma pack(reset)

#endif
