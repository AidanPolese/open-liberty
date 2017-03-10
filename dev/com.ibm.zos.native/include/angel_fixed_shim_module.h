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
#ifndef _BBOZ_ANGEL_FIXED_SHIM_MODULE_H
#define _BBOZ_ANGEL_FIXED_SHIM_MODULE_H

#include "gen/ihasdwa.h"

#include "bbgzasvt.h"
#include "common_defines.h"
#include "mvs_resmgr.h"

/** @file
 * Maps the bbgzafsm (Angel Fixed Shim Module)
 */

/**
 * Version of the fixed shim module code.  This should be increased by
 * one, by the first code change in this module in this PTF, and the
 * minor version should be set to 0.  For subsequent changes in the
 * same PTF, the minor version should increased by one.
 *
 * These ensure that the angel process reloads the fixed shim module.
 * A customer should only ever see major version changes.  Our test
 * systems rely on the minor version changes to ensure personal builds
 * run with the correct level of code.
 */
#define BBGZ_FIXED_SHIM_MODULE_CODE_MAJOR_VERSION 7
#define BBGZ_FIXED_SHIM_MODULE_CODE_MINOR_VERSION 0

/*-------------------------------------------------------------------*/
/* The fixed shim module vector table is referenced from 31 bit      */
/* code which sets up the PC routines (since those services must     */
/* be called in 31 bit mode).  31 bit C code does not recognize the  */
/* __ptr64 directive so we need to be creative.                      */
/*-------------------------------------------------------------------*/
#ifdef _LP64
#define PAD31
#else
#define PAD31 int :32;
#endif

typedef struct bbgzafsm bbgzafsm;
/**
 * The Angel Fixed Shim Module Vector Table
 */
struct bbgzafsm {

    /**
     * Eyecatcher.
     */
    char eyecatcher[8];                                            /* 0x000 */

    /**
     * Build date stamp in YYYYMMDD format.
     */
    char build_date[8];                                            /* 0x008 */

    /**
     * Build time stamp in hh:mm:ss format.
     */
    char build_time[8];                                            /* 0x010 */

    /**
     * Get the human readable version information of this module.
     *
     * @return a string fit for end user messages
     */
    PAD31 char* (* getVersionString)(void);                        /* 0x018 */

    /**
     * Get the version number of this module.  Version numbers for the
     * fixed shim should only change if the code has changed.
     *
     * @return the version number of this module
     */
    PAD31 int (* getVersionNumber)(void);                          /* 0x020 */

    /**
     * The associated recovery routine (ARR) for the fixed shim PC targets.
     *
     * @param sdwa_p the "system diagnostic work area" related to the error
     */
    PAD31 void (* associatedRecoveryRoutine)(sdwa* sdwap_p);       /* 0x028 */

    /**
     * The server-side stub for the register PC.  This is a PC target which
     * looks up and branches to the register function in the dynamic replaceable
     * module.
     *
     * @param c The path name of the server authorized functions module to load
     *
     * @return 0 on success, nonzero on failure.
     */
    PAD31 int (* register_pc_stub)(char*);                         /* 0x030 */

    /**
     * The server-side stub for the invoke PC.  This is a PC target which
     * looks up and branches to the invoke function in the dynamic replaceable
     * module.
     *
     * @param index the index in the server authorized function module for the target
     *        of the invoke service
     * @param arg_struct_size the size of the struct passed to the PC target
     * @param arg_struct_p A pointer to the struct passed to the PC target
     *
     * @return 0 if the PC was dispatched successfully, nonzero if not.  Note that
     *         a return of 0 does not mean that the target function completed
     *         successfully, only that it was dispatched successfully.
     */
    PAD31 int (* invoke_pc_stub)(int, int, void*);                 /* 0x038 */

    /**
     * The server-side stub for the deregister PC.  This is a PC target which
     * looks up and branches to the deregister function in the dynamic replaceable
     * module.
     */
    PAD31 int (* deregister_pc_stub)(void);                        /* 0x040 */

    /**
     * PC routine called by a Liberty client to bind to a Liberty server.
     *
     * @param targetServerStoken The stoken of the server to bind to.
     * @param clientFunctionTablePtr_p A pointer to a double word where the pointer
     *                                 to the client function module is copied.
     * @param bindToken_p A pointer to a double word where the bind token is copied.
     *                    The bind token must be supplied on all clientInvoke and
     *                    unbind calls.
     *
     * @return 0 on success.
     */
    PAD31 int (* client_bind_stub)(SToken*, bbgzasvt_header**, void**); /* 0x048 */

    /**
     * PC routine called by a Liberty client to invoke a service in the common
     * module (BBGZSCFM).
     *
     * @param void* bindToken_p A pointer to the bind token return on client bind.
     * @param serviceIndex The index in the SCFM of the service to invoke.
     * @param parm_len The length of the data pointed to by parm_p.
     * @param parm_p A pointer to the parameter struct required by the called
     *               service.
     *
     * @return 0 if the client service was invoked.  The return code from the client
     *         service will be inside parm_p.
     */
    PAD31 int (* client_invoke_stub)(void*, int, int, void*);      /* 0x050 */

    /**
     * PC routine called by a Liberty client to unbind from a Liberty server.
     *
     * @param void* bindToken_p A pointer to the bind token return on client bind.
     *
     * @return 0 on success.
     */
    PAD31 int (* client_unbind_stub)(void*);                       /* 0x058 */

    /**
     * A version 0 fixed shim module will have a non-zero value in this field.
     * A version 1+ fixed shim module will have a zero in this field.
     */
    PAD31 int (* _reserved_PC7)(void);                             /* 0x060 */

    /**
     * A version 0 fixed shim module will have a non-zero value in this field.
     * A version 1+ fixed shim module will use this field for length/version
     * information.
     */
    union {
        struct {
            PAD31 int (* _reserved_PC8)(void); // PC #8 (un-used)
        } v0_PC8;
        struct {
            unsigned short version;    // Version of BBGZAFSM structure
            unsigned short _available;
            unsigned int   length;     // Size of BBGZAFSM structure
        } v1_verInfo;
    };                                                             /* 0x068 */

    /**
     * Stub for the RESMGR monitoring tasks and the address space.  The
     * stub looks up the RESMGR code in the dynamic replaceable module and
     * branches to it.
     */
    PAD31 rmgr_entry_t* resmgr_stub;                               /* 0x070 */

    /**
     * Stub for the client RESMGR monitoring tasks and the address space.  The
     * stub looks up the client RESMGR code in the dynamic replaceable module
     * and branches to it.
     */
    PAD31 rmgr_entry_t* client_resmgr_stub;                        /* 0x078 */

    /**
     * The minor version of this module.  The minor version can be appended to
     * the version as reported by getVersionNumber() to get the full version.
     * The minor version is generally not reported to customers.
     */
    unsigned short minorVersion;                                   /* 0x080 */

    /**
     * Available for use.
     */
    unsigned char available[62];                                   /* 0x082 */
};                                                                 /* 0x100 */

#endif
