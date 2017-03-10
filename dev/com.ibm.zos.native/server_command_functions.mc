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
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>

#include "include/common_defines.h"
#include "include/mvs_extract.h"
#include "include/mvs_qedit.h"
#include "include/mvs_utils.h"
#include "include/mvs_wait.h"

#include "include/gen/iezcib.h"
#include "include/gen/iezcom.h"
#include "include/ras_tracing.h"
#include "include/server_command_functions.h"

#define RAS_MODULE_CONST  RAS_MODULE_SERVER_COMMAND_FUNCTIONS

#define CMD_POSTCODE_STOPLISTENING 0x00000088

/* Get Command to process                                            */
/* Return: CommandInfoArea, representing a new Console command, or   */
/*         or  just status of what happened.                         */
int getConsoleCommand(GetConsoleCommandParms * parms)
{
    cib* __ptr32 cib_ptr;
    CommandInfoArea  localCmdInfoArea = {{0}};
    int postCode = 0;
    struct local_ecb_list {
            unsigned int * __ptr32 ecb1_ptr;  // pointer to stop/modify ecb
            unsigned int * __ptr32 ecb2_ptr;  // pointer to stop listening ecb
    };

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(1),
                    "getConsoleCommand, entry",
                    TRACE_DATA_RAWDATA(
                        sizeof(*parms),
                        parms,
                        "GetConsoleCommandParms"),
                        TRACE_DATA_END_PARMS);
    }

    if (parms &&
        parms->inCom_ptr &&
        parms->inStopListenECB_ptr &&
        parms->outCommandInfoArea_ptr)
    {
        if (TraceActive(trc_level_detailed)) {
            TraceRecord(trc_level_detailed,
                        TP(2),
                        "getConsoleCommand, before wait, ecb",
                        TRACE_DATA_HEX_INT(parms->inCom_ptr->comecbpt,
                                           "comecbpt"),
                        TRACE_DATA_END_PARMS);
        }

        /*-----------------------------------------------------------*/
        /* Wait for new command to show up                           */
        /*-----------------------------------------------------------*/
        struct local_ecb_list * __ptr32 local_ecb_list_ptr =
            __malloc31(sizeof(struct local_ecb_list));
        local_ecb_list_ptr->ecb1_ptr = parms->inCom_ptr->comecbpt;
        local_ecb_list_ptr->ecb2_ptr = parms->inStopListenECB_ptr;
        local_ecb_list_ptr->ecb2_ptr = (unsigned int * __ptr32)
                                  ((unsigned int)local_ecb_list_ptr->ecb2_ptr | 0x80000000);

        postCode = waitlist(local_ecb_list_ptr);
        free(local_ecb_list_ptr);

        /* Check if we have been posted to stop listening            */
        if ((*(parms->inStopListenECB_ptr)) & 0x40000000)
        {
            localCmdInfoArea.cia_commandType = CIA_COMMANDTYPE_ENDING;

            memcpy_dk(parms->outCommandInfoArea_ptr, &localCmdInfoArea, sizeof(localCmdInfoArea), 8);

            if (TraceActive(trc_level_detailed)) {
                TraceRecord(trc_level_detailed,
                            TP(15),
                            "getConsoleCommand, exit",
                            TRACE_DATA_RAWDATA(
                                sizeof(localCmdInfoArea),
                                &localCmdInfoArea,
                                "localCmdInfoArea"),
                                TRACE_DATA_END_PARMS);
            }
            return 0;
        }

        cib_ptr = parms->inCom_ptr->comcibpt;
        if (cib_ptr != NULL)
        {
            if (cib_ptr->cibverb == cibstop)
            {
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(3),
                                "getConsoleCommand, received STOP verb",
                                TRACE_DATA_HEX_INT(cib_ptr->cibverb,
                                                   "cib_ptr->cibverb"),
                                TRACE_DATA_END_PARMS);
                }

                /*---------------------------------------------------*/
                /* Build "STOP" command response for caller          */
                /*---------------------------------------------------*/
                localCmdInfoArea.cia_commandType = CIA_COMMANDTYPE_STOP;

                cibx* __ptr32 cibx_ptr = (cibx* __ptr32)
                                         (((char*)cib_ptr) + cib_ptr->cibxoff);
                localCmdInfoArea.cia_consoleID = cibx_ptr->cibxcnid;
                memcpy(localCmdInfoArea.cia_consoleName,
                       cibx_ptr->cibxcnnm,
                       sizeof(localCmdInfoArea.cia_consoleName));
            }
            else if (cib_ptr->cibverb == cibmodfy)
            {
                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(4),
                                "getConsoleCommand, received MODIFY verb",
                                TRACE_DATA_HEX_INT(cib_ptr->cibverb,
                                                   "cib_ptr->cibverb"),
                                TRACE_DATA_END_PARMS);
                }

                /*---------------------------------------------------*/
                /* Build "MODIFY" command response for caller        */
                /*---------------------------------------------------*/
                cibx* __ptr32 cibx_ptr = (cibx* __ptr32)
                                         (((char*)cib_ptr) + cib_ptr->cibxoff);


                localCmdInfoArea.cia_commandType = CIA_COMMANDTYPE_MODIFY;
                localCmdInfoArea.cia_consoleID = cibx_ptr->cibxcnid;
                memcpy(localCmdInfoArea.cia_consoleName,
                       cibx_ptr->cibxcnnm,
                       sizeof(localCmdInfoArea.cia_consoleName));
                memcpy(localCmdInfoArea.cia_commandCART,
                       cibx_ptr->cibxcart,
                       sizeof(localCmdInfoArea.cia_commandCART));

                /* Get the rest of the command to process            */
                /* Note: we will truncate at the max allowed for us  */
                localCmdInfoArea.cia_commandRestOfCommandLength =
                            BBGZ_min(cib_ptr->cibdatln,
                                     (CIA_MAX_COMMAND_PARMS-1));
                memcpy(localCmdInfoArea.cia_commandRestOfCommand,
                       cib_ptr->cibdata,
                       localCmdInfoArea.cia_commandRestOfCommandLength);
                localCmdInfoArea.cia_commandRestOfCommand[localCmdInfoArea.cia_commandRestOfCommandLength] = '\0';

                if (TraceActive(trc_level_detailed)) {
                    TraceRecord(trc_level_detailed,
                                TP(5),
                                "getConsoleCommand, packaged MODIFY command",
                                TRACE_DATA_INT(localCmdInfoArea.cia_commandRestOfCommandLength,
                                               "cia_commandRestOfCommandLength"),
                                TRACE_DATA_END_PARMS);
                }
            }
            else
            {
                /*---------------------------------------------------*/
                /* TODO:Unknown command -- print an error message.   */
                /* TODO: Use CART when issuing response              */
                /*---------------------------------------------------*/
                if (TraceActive(trc_level_exception)) {
                    TraceRecord(trc_level_exception,
                                TP(6),
                                "BBOZxxxxE UNRECOGNIZED COMMAND: ",
                                TRACE_DATA_RAWDATA((cib_ptr->cibdatln),
                                                   cib_ptr->cibdata,
                                                   "cib_ptr->cibdata"),
                                TRACE_DATA_END_PARMS);
                }

                localCmdInfoArea.cia_commandType = CIA_COMMANDTYPE_UNKNOWN;
            }

            int rc = free_cib_from_chain(parms->inCom_ptr, parms->inCom_ptr->comcibpt);

            if (rc != 0)
            {
                if (TraceActive(trc_level_exception)) {
                    TraceRecord(trc_level_exception,
                                TP(7),
                                "QEDIT FREE loop bad return code",
                                TRACE_DATA_INT(rc,
                                               "rc"),
                                TRACE_DATA_END_PARMS);
                }
            }
        }
    }
    else
    {
        localCmdInfoArea.cia_commandType = CIA_COMMANDTYPE_ERROR;

        if (TraceActive(trc_level_exception)) {
            TraceRecord(trc_level_exception,
                        TP(8),
                        "missing com_ptr or output CmdArea",
                        TRACE_DATA_END_PARMS);
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                    TP(9),
                    "getConsoleCommand, exit",
                    TRACE_DATA_RAWDATA(
                        sizeof(localCmdInfoArea),
                        &localCmdInfoArea,
                        "localCmdInfoArea"),
                        TRACE_DATA_END_PARMS);
    }

    if (parms && parms->outCommandInfoArea_ptr)
    {
        memcpy_dk(parms->outCommandInfoArea_ptr, &localCmdInfoArea, sizeof(localCmdInfoArea), 8);
    }
    else
    {
        // TODO:  Hmm.   Should we throw?? write error message ??
    }

    return 0;
}   /* end, getConsoleCommand                                        */


/* Post a waiting CommandListeningThread to stop waiting for a       */
/*                                                                   */
/* Input: pointer to ECB for waiting Command listener thread         */
/* return: post return code                                          */
int stopCommandListening(StopCommandListeningParms * parms)
{
    int postRC = 99;
    int postCode = CMD_POSTCODE_STOPLISTENING;


    /* Need to wake up the command listener thread so it could end   */
    if (parms && parms->inECB_ptr)
    {
        postRC = post(parms->inECB_ptr, postCode);
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(10),
                "stopCommandListening, returning",
                TRACE_DATA_RAWDATA(
                    sizeof(*parms),
                    parms,
                    "parms"),
                TRACE_DATA_END_PARMS);
    }

    memcpy_dk(parms->outPostRC_ptr, &postRC, sizeof(postRC), 8);

    return 0;
}


/* Initialize the CIB queue and set the allowable command limit to   */
/* prepare for receiving console commands.  ALso, punts the start    */
/* CIB if present                                                    */
int getIEZCOM_reference(GetIEZCOM_referenceParms * parms)
{
    iezcom * com_ptr = 0;
    cib* __ptr32 cib_ptr;
    char* message_p;
    int rc;

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(11),
                "getIEZCOM_reference, entry",
                TRACE_DATA_RAWDATA(
                    sizeof(*parms),
                    parms,
                    "parms"),
                TRACE_DATA_END_PARMS);
    }

    /*---------------------------------------------------------------*/
    /* Do initial setup for receiving commands                       */
    /*---------------------------------------------------------------*/
    com_ptr = extract_comm();
    if (com_ptr != NULL)
    {
        cib_ptr = com_ptr->comcibpt;
        /*-----------------------------------------------------------*/
        /* If launched from a started proc we have an initial        */
        /* start cib...punt it                                       */
        /*-----------------------------------------------------------*/
        if ((cib_ptr != NULL) && (cib_ptr->cibverb == cibstart))
        {
            rc = free_cib_from_chain(com_ptr, com_ptr->comcibpt);

            if (rc != 0)
            {
                if (TraceActive(trc_level_exception)) {
                    TraceRecord(trc_level_exception,
                                TP(12),
                                "getIEZCOM_reference: QEDIT FREE bad return code",
                                TRACE_DATA_INT(rc,
                                               "rc"),
                                TRACE_DATA_END_PARMS);
                }
            }
        }

        rc = set_cib_limit(com_ptr, 1);

        if (rc != 0)
        {
            if (TraceActive(trc_level_exception)) {
                TraceRecord(trc_level_exception,
                            TP(13),
                            "getIEZCOM_reference: QEDIT SET LIMIT bad return code",
                            TRACE_DATA_INT(rc,
                                           "rc"),
                            TRACE_DATA_END_PARMS);
            }
        }
    }

    if (TraceActive(trc_level_detailed)) {
        TraceRecord(trc_level_detailed,
                TP(14),
                "getIEZCOM_reference, exit",
                TRACE_DATA_RAWDATA(
                    sizeof(*com_ptr),
                    com_ptr,
                    "IEZCOM"),
                TRACE_DATA_END_PARMS);
    }

    memcpy_dk(parms->outCom_ptr, &com_ptr, sizeof(com_ptr), 8);

    return 0;
}   /* end, getIEZCOM_reference                                      */

