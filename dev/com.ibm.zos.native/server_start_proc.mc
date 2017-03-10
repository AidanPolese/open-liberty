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

/**
 * Start a server via proc
 */

#include "include/bpx_stat.h"
#include "include/mvs_utils.h"
#include "include/security_saf_authorization.h"


//TODO Move to own file along with MGCRE call and call externally
#pragma linkage(BPX4SLP,OS_NOSTACK)
extern void BPX4SLP(int seconds,
                    int retval);


// List form of MGCRE.
__asm(" MGCRE MF=L" : "DS"(MGCRE_list));

#define PID_FILE_NOT_FOUND_RC     1
#define MAX_LENGTH_EXCEEDED_RC    2
#define MAX_COMMAND_LENGTH        126

//Main method declaration
int main(int argc, char** argv) {

    int rc = 0;

    if(argc >= 4){

        struct startCommand {
                short length;
                char command[MAX_COMMAND_LENGTH+1];
        };

        //Get the passed proc name argument
        const char* procName = argv[1];

        //Get the passed server name argument
        const char* serverName = argv[2];

        //Get the passed pid file argument
        char* pidFilePath = argv[3];

        //-----------------------------------------------------------------
        // Build start command -
        // Before adding parts to the start command, check our potential
        // length to make sure we do not exceed our 126 max size and cause
        // an overflow.
        //-----------------------------------------------------------------
        struct startCommand start;
        memset(&start, 0, sizeof(struct startCommand));

        //Check length of (START <procName>,PARMS='<serverName>)
        if(strlen(procName) + strlen(serverName) + 14 > MAX_COMMAND_LENGTH){
            return MAX_LENGTH_EXCEEDED_RC;
        }
        memcpy(&(start.command), "START ", 6);
        strcat(start.command, procName);
        strcat(start.command, ",PARMS='");
        strcat(start.command, serverName);

        //Add additional arguments
        if(argc > 4){
            for(int i = 4; i < argc; i++){
                if(strlen(start.command) + strlen(argv[i]) + 1 > MAX_COMMAND_LENGTH){
                    return MAX_LENGTH_EXCEEDED_RC;
                }
                strcat(start.command, " ");
                strcat(start.command, argv[i]);
            }
        }

        //Check length with additional script launch arg
        if(strlen(start.command) + 15 > MAX_COMMAND_LENGTH){
            return MAX_LENGTH_EXCEEDED_RC;
        }
        strcat(start.command, " -scriptLaunch'");

        //Set the command length
        start.length = strlen(start.command);


        //---------------------------------------------------------------------
        // RUN MGCRE - TODO move this to its own metal c file
        // We are relying on the MGCRE macro to perform the necessary SAF check
        // against the RACF user who is issuing this command.
        //---------------------------------------------------------------------
        int consid = 0;

        char MGCREDynamic[sizeof(MGCRE_list)];

        memcpy(MGCREDynamic, &MGCRE_list, sizeof(MGCRE_list));

        //Switch to supervisor state by calling mvs_utils method
        switchToSupervisorState();

        //Switch to key 0 by calling mvs_utils method
        unsigned char currentKey = switchToKey0();

        __asm(" MGCRE TEXT=(%1),CONSID=%3,MF=(E,(%2))\n"
              " ST 15,%0"
              : "=m"(rc)
              : "r"(&start),"r"(MGCREDynamic), "m"(consid)
              : "r0","r1","r14","r15");

        //Switch key and state back
        switchToSavedKey(currentKey);
        switchToProblemState();


        //Wait for PID file to be created
        if(rc == 0){
            struct stat info;
            for(int i = 0; i < 10; i++){
                if(stat(pidFilePath, &info) == 0){
                    return 0;
                }
                BPX4SLP(1, 0);  //TODO move this to its own file and call externally
            }
            //PID file was not created in the 10 second wait period
            return PID_FILE_NOT_FOUND_RC;
        } else{
            return rc;
        }

    } else{
        //Incorrect number of expected arguments. This should not happen.
        return -1;
    }


}

