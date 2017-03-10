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
#ifndef _BBOZ_RAS_ABEND_CODES_H
#define _BBOZ_RAS_ABEND_CODES_H

/**
 * Abend completion code that should be used when a server or client terminates abnormally.
 */
#define ABEND_TYPE_SERVER 0xEC3

//-----------------------------------------------------------------------------
// Abend reason codes for Liberty fall into the range x20000000 thru x20FFFFFF.
// This range is not used by traditional WAS.
//
// The range x20000000 thru x20EFFFFF are defined in this part.
// The range x20F00000 thru x20FFFFFF are defined in macros/RASABEND.
//-----------------------------------------------------------------------------

// Routine: angel_server_pc.mc -- x200000xx
/**
 * Abend code: EC3 hex
 * Explanation: The end eye catcher of the BBGZSAFM module did not match the
 *              expected eye catcher.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The BBGZSAFM module is corrupted or the register
 *                       service was invoked improperly.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_BAD_END_EYE           0x20000000

/**
 * Abend code: EC3 hex
 * Explanation: There are more than the defined maximum number of entries in
 *              the BBGZSAFM vector table.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The BBGZSAFM module is corrupted or the register
 *                       service was invoked improperly.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_TOO_MANY_ENTRIES      0x20000001

/**
 * Abend code: EC3 hex
 * Explanation: The eye catcher on the vector table in the BBGZSAFM module
 *              did not match the expected eye catcher.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The BBGZSAFM module is corrupted or the register
 *                       service was invoked improperly.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_BAD_VECTOR_EYE        0x20000002

/**
 * Abend code: EC3 hex
 * Explanation: The module name inside the BBGZSAFM did not match the file name
 *              in the HFS.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The BBGZSAFM module is corrupted or the register
 *                       service was invoked improperly.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_MODULE_NAME_NOT_EQUAL 0x20000003

/**
 * Abend code: EC3 hex
 * Explanation: The length of the name of the BBGZSAFM module in the HFS did
 *              not pass the length check.  The length of a module name must
 *              fit in the eye catcher field, which is 8 bytes long.  For the
 *              BBGZSAFM module, the module name in the HFS must be BBGZSAFM.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The register service was invoked improperly or the
 *                       Liberty profile was installed incorrectly.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_MODULE_NAME_INCORRECT 0x20000004

/**
 * Abend code: EC3 hex
 * Explanation: The BBGZSAFM module could not be loaded from the file system.
 * Suggested Action: Check that the installation of the Liberty profile
 *                   completed without errors, and that the user ID which is
 *                   running the Liberty server has permission to read the files
 *                   in the Liberty installation.  If the problem persists,
 *                   contact your next level of support.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_LOAD_FAILURE          0x20000005

/**
 * Abend code: EC3 hex
 * Explanation: The BBGZSAFM module is not APF authorized.
 * Suggested Action: Check that the installation of the Liberty profile
 *                   completed without errors.  The BBGZSAFM module should have
 *                   been installed with the APF authorization bit set.  If the
 *                   problem persists, contact your next level of support.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_NOT_APF_AUTHORIZED    0x20000006

/**
 * Abend code: EC3 hex
 * Explanation: A call to Unix System Services stat function to the BBGZSAFM
 *              load module failed.
 * Suggested Action: Check that the installation of the Liberty profile
 *                   completed without errors, and that the user ID which is
 *                   running the Liberty server has permission to read the files
 *                   in the Liberty installation.  If the problem persists,
 *                   contact your next level of support.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_STAT_FAILURE          0x20000007

/**
 * Abend code: EC3 hex
 * Explanation: The path name to the BBGZSAFM module in the file system is too
 *              long.  The path should not be more than 4096 characters.
 * Suggested Action: Install the Liberty profile to location in the file system
 *                   which can be accessed with a shorter PATH.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_HFS_PATH_TOO_LONG     0x20000008

/**
 * Abend code: EC3 hex
 * Explanation: The size of the BBGZSAFM module is smaller than the minimum
 *              possible size for this load module.
 * Suggested Action: Check that the installation of the Liberty profile
 *                   completed without errors.  If the problem persists, contact
 *                   your next level of support.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_TOO_SMALL             0x20000009

/**
 * Abend code: EC3 hex
 * Explanation: The size of the BBGZSAFM module is smaller than the minimum
 *              possible size for this load module based on the number of
 *              functions it contains.
 * Suggested Action: Check that the installation of the Liberty profile
 *                   completed without errors.  If the problem persists, contact
 *                   your next level of support.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_TOO_SMALL2            0x2000000A

/**
 * Abend code: EC3 hex
 * Explanation: One of the VCON addresses in the BBGZSAFM load module resolved
 *              to an address outside of the load module.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The BBGZSAFM load module may be corrupted.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_BAD_VCON              0x2000000B

/**
 * Abend code: EC3 hex
 * Explanation: An error was encountered while unloading the BBGZSAFM load
 *              module.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: If the unload failed, BBGZSAFM may have been unloaded
 *                       by some other task, which is unexpected.  A malicious
 *                       user may be attempting to unload the module for us in
 *                       hopes of creating an unexpected condition and gaining
 *                       some level of authorization on the system.
 */
#define KRSN_ANGEL_SERVER_PC_SAFM_UNLOAD_FAIL           0x2000000C

// Routine: angel_fixed_shim_pc.mc -- x200001xx

/**
 * Abend code: EC3 hex
 * Explanation: A PC routine which has not been implemented yet has been
 *              invoked.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: A code level mismatch between the Liberty server and
 *                       the Angel has occurred.  Register 2 contains the PC
 *                       number in the fixed shim that was attempted.
 */
#define KRSN_ANGEL_FIXED_SHIM_PC_UNDEFINED_PC           0x20000100


// Routine: server_local_comm_queue.mc -- x200002xx

/**
 * Abend code: EC3 hex
 * Explanation: The Local Comm routine waitOnWork was driven to obtain more work
 *              when the previous work had not been handled.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: A call to releaseReturnedWRQEs should have been made
 *                       prior to the waitOnWork call.
 */
#define KRSN_SERVER_LCOM_QUEUE_EXISTING_CL               0x20000200


/**
 * Abend Code: EC3 hex
 * Explanation: The Local Comm routine waitOnWork could not obtain a new MVS
 *              pause element token (PET).
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: A call to was made to the PET pool to obtain a PET.
 *              The PET pool routine, pickup, returned a non-zero return
 *              code. The return code is in register 2 at the time of the ABEND.
 *              Register 3 contains the pet vet instance storage.
 */
#define KRSN_SERVER_LCOM_QUEUE_NO_PET                    0x20000201

/**
 * Abend Code: EC3 hex
 * Explanation: The Local Comm routine dataAvailableOnDataQueue discovered an incorrect
 * state on the data queue.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: A call made to dataAvailableOnDataQueue, driven from the local
 * comm preview path, detected a BBGZLMSG at the head of queue that contained no available
 * data.  This would indicate the client had read all the available data but somehow the
 * local comm routines failed to remove the completely consumed LMSG from the queue.
 * Register 2 contains the bbgzlmsg address and register 3 contains the "data available"
 * value at the time of the ABEND.
 */
#define KRSN_SERVER_LCOM_QUEUE_DATAAVAIL_INVALID         0x20000202

// Routine: mvs_cell_pool_services.mc -- x200003xx
/**
 * Abend Code: EC3 hex
 * Explanation: The buildCellPool routine was passed a non-quadword aligned
 * storage address.  Cellpool services running under 64bit require
 * quadword alignment.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The questionable storage address is in register 2
 * at the time of the ABEND.  The cellpool name is in register 3 at the time of
 * the ABEND.
 */
#define KRSN_SERVER_MVSCELLSERVICES_BAD_STORAGE_PTR      0x20000300

// Routine: server_wola_client_rebind.mc -- x200004xx
/**
 * Abend Code: EC3 hex
 * Explanation: A WOLA client lost connectivity to a Liberty server instance.
 *              After the Liberty server instance was restarted, the client tried to
 *              rebind and establish a new local comm connection.  The local comm
 *              connection could not be established.
 * Suggested Action: The client process may need to be restarted.  If the problem
 *                   persists, contact your next level of support.
 */
#define KRSN_SERVER_WOLA_CLIENT_REBIND_GETCONN_FAIL      0x20000400

// Routine: wola_normal_call_exit.mc -- x200005xx
/**
 * Abend Code: EC3 hex
 * Explanation: The WOLA normal call exit routine was called and one or more
 *              null pointers were detected for the external subsystem parameter
 *              list, stub EPA, or WOLA stub object code.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: All of these pointers should be available if we are in
 *                       the correct environment, so it's possible we are in
 *                       the IMS inbound path but were not called from IMS.
 */
#define KRSN_WOLA_CLIENT_NORMAL_CALL_FAIL                0x20000500

/**
 * Abend Code: EC3 hex
 * Explanation: The WOLA normal call exit routine was called, but the client
 *              stub name did not match any known WOLA functions.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: Ensure that DFSESPR0 was called correctly from WOLA,
 *                       and that there is no WOLA client level mismatch.
 */
#define KRSN_WOLA_CLIENT_NORMAL_CALL_UNKNOWN_API         0x20000501

// Routine: mvs_aio_services.mc -- x200006xx
/**
 * Abend Code: EC3 hex
 * Explanation: The enqueueIO_PLO routine could not obtain a new cell from the
 * AsyncIO Element cellpool.  The cell is needed to queue the completed IO
 * event until a Java ResultHandler thread can request it for processing.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The AsyncIO Element cellpool is defined to "autogrow" with
 * the auto grow function getAIOCEStorage.  This function uses malloc'd storage.
 * Check the heap usage for a possible leak. Register 2 contains the address of the
 * AsyncIOCompletionData control block and register 3 contains the address of the
 * AIOCB of the completed IO.
 */
#define KRSN_MVS_AIO_SERVICES_NO_ELEMENT_CELL            0x20000600

// Routine: wola_ims_esmt_init.mc -- x200007xx
/**
 * Abend code: EC3 hex
 * Explanation: During initialization of the IMS ESMT for WOLA the pointer to the DFSEEVTP control
 *              block was null.
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The pointer to the EEVTP is null
 */
#define KRSN_SERVER_WOLA_ESMT_INIT_EXIT_EEVTP_NULL       0x20000700

/**
 * Abend code: EC3 hex
 * Explanation: During initialization of the IMS ESMT for WOLA the pointer to the DFSEEVTP control block
 *              passed to the initialization routine does not point to a valid DFSEEVTP control block
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The EYE Catcher for the DFSEEVTP control block does not contain the value "EEVTP"
 */
#define KRSN_SERVER_WOLA_ESMT_INIT_EXIT_EEVTP_INVALID       0x20000701

/**
 * Abend code: EC3 hex
 * Explanation: During initialization of the IMS ESMT for WOLA the pointer to the DFSEEVT control block
 *              passed to the initialization routine does not point to a valid DFSEEVT control block
 * Suggested Action: "IBM Internal Only"
 * Internal Explanation: The EYE Catcher for the DFSEEVT control block does not contain the value "EEVT"
 */
#define KRSN_SERVER_WOLA_ESMT_INIT_EXIT_EEVT_INVALID       0x20000702

#endif
