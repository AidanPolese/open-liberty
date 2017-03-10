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
/*
 * server_command_functions.h
 *
 *  Created on: Aug 22, 2011
 *      Author: ginnick
 */

#ifndef SERVER_COMMAND_FUNCTIONS_H_
#define SERVER_COMMAND_FUNCTIONS_H_

#include "gen/iezcom.h"

/* Define room for command parms as 256 - sizeof structure.          */
/* TODO: Why I'm I apparently restricting the overall size of this structure? No reason...I
 * should be making sure it has enough room for the largest operator cmd.
 *  */
#define CIA_MAX_COMMAND_PARMS       256-sizeof(int)-8-sizeof(int)

/* The following define the values for the cia_commandType attribute of CommandInfoArea.
 * Note: that these values are mapped in Java in CommandProcessor.java
 */
#define CIA_COMMANDTYPE_STOP      0x00000001
#define CIA_COMMANDTYPE_MODIFY    0x00000002
#define CIA_COMMANDTYPE_UNKNOWN   0x00000003
#define CIA_COMMANDTYPE_ENDING    0x00000004
#define CIA_COMMANDTYPE_ERROR     0x00000005

/* The following are the native error codes set into "cia_errorCode"
 *
 */
#define CIA_ERRCODE_NO_ACCESS_SUFM1  0x00000001;

#pragma pack(1)
typedef struct CommandInfoArea CommandInfoArea;
struct CommandInfoArea
{
  int                      cia_commandType;                                  /* 0x00 */
  int                      cia_errorCode;                                    /* 0x04 */
  int                      cia_consoleID;                                    /* 0x08 */
  char                     cia_consoleName[8];                               /* 0x0C */
  unsigned char            cia_commandCART[8];                               /* 0x14 */
  int                      cia_commandRestOfCommandLength;                   /* 0x1C */
  char                     cia_commandRestOfCommand[CIA_MAX_COMMAND_PARMS];  /* 0x20 */
};                                                                           /* 0x100*/
#pragma pack(reset)


/* Get Command to process                                            */
/* Input: pointer to iezcom area                                     */
/* Return: CommandInfoArea, representing a new Console command, or   */
/*         or  just status of what happened.                         */
typedef struct GetConsoleCommandParms GetConsoleCommandParms;
struct GetConsoleCommandParms
{
    iezcom * inCom_ptr;
    unsigned int * inStopListenECB_ptr;
    CommandInfoArea * outCommandInfoArea_ptr;
};
int getConsoleCommand(GetConsoleCommandParms * parms);

/* Post a waiting CommandListeningThread to stop waiting for a       */
/*                                                                   */
/* Input: pointer to iezcom area,                                    */
/*        pointer to stop listening ECB                              */
/* return: post return code                                          */
typedef struct StopCommandListeningParms StopCommandListeningParms;
struct StopCommandListeningParms
{
    unsigned int * inECB_ptr;
    int * outPostRC_ptr;
};
int stopCommandListening(StopCommandListeningParms * parms);

/* Initialize the CIB queue and set the allowable command limit to   */
/* prepare for receiving console commands.  Also, punts the start    */
/* CIB if present                                                    */
typedef struct GetIEZCOM_referenceParms GetIEZCOM_referenceParms;
struct GetIEZCOM_referenceParms
{
    iezcom ** outCom_ptr;
};
int getIEZCOM_reference(GetIEZCOM_referenceParms * parms);


#endif /* SERVER_COMMAND_FUNCTIONS_H_ */
