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
#include <errno.h>
#include <pthread.h>
#include <stdlib.h>
#include <string.h>

#include "include/ras_tracing.h"

#include "include/server_function_module_stub.h"

#pragma linkage(BPX4LOD,OS)
void BPX4LOD(int filename_len, char* filename,
             int flags,
             int libpath_len, char* libpath,
             void* etnry_point,
             int retval, int retcode, int reason);

#define RAS_MODULE_CONST 0x0F001000

struct subtask_parms {
    pthread_mutex_t* subthread_lock_p;
    struct server_function_stubs* stubs_p;
};

void test_commands(struct server_unauthorized_function_stubs* unauth_stubs_p)
{
    iezcom * com_ptr;
    GetIEZCOM_referenceParms getComParms ={
      .outCom_ptr = &com_ptr};
    unauth_stubs_p->getIEZCOM_reference(&getComParms);
    printf("getIEZCOM_reference invoked, com_ptr(%p)\n", com_ptr);

    CommandInfoArea cmdInfoArea;
    unsigned int * stopECB_ptr = __malloc31(4);
    GetConsoleCommandParms getCmdParms ={
        .inCom_ptr = com_ptr,
        .inStopListenECB_ptr = stopECB_ptr,
        .outCommandInfoArea_ptr = &cmdInfoArea};
    unauth_stubs_p->getConsoleCommand(&getCmdParms);
    printf("getConsoleCommand invoked\n");
    if (com_ptr != 0)
      {
         // long cartLong;
         // memcpy(&cartLong, cmdInfoArea.cia_commandCART, 8);
         printf("\tcia_commandType: %i \n"
                "\tcia_commandCART: %lx \n"
                "\tcia_commandRestOfCommandLength: %i\n"
                "\tcia_commandRestOfCommand: (%s)\n",
                cmdInfoArea.cia_commandType, *((long *)(&cmdInfoArea.cia_commandCART[0])), cmdInfoArea.cia_commandRestOfCommandLength,
                cmdInfoArea.cia_commandRestOfCommand);

         int postRC = 0;
         StopCommandListeningParms stopCmdListeningParms ={
             .inECB_ptr = stopECB_ptr,
             .outPostRC_ptr = &postRC};
         unauth_stubs_p->stopCommandListening(&stopCmdListeningParms);
         printf("stopCommandListening driven, post rc (%i)\n", postRC);
      }
}

int main(int argc, char** argv)
{
    /* TDK testing out the function table */
    char* sufm_filename = "/u/MSTONE1/wlp/lib/native/zos/s390x/bbgzsufm";
    struct server_function_stubs* server_funcs = NULL;
    int rv, rsn, rc;

    BPX4LOD(strlen(sufm_filename),
            sufm_filename,
            0,
            0,
            "",
            &server_funcs,
            rv,
            rc,
            rsn);

    server_funcs = (struct server_function_stubs*) (((long long)server_funcs) & 0xFFFFFFFFFFFFFFFEL);
    if (server_funcs != NULL){
      printf("Loaded SUFM at %p \n", server_funcs);
      
      struct server_authorized_function_stubs* auth_stubs_p =
        server_funcs->authorized_p;
      struct server_unauthorized_function_stubs* unauth_stubs_p =
        server_funcs->unauthorized_p;

      /* Test command processing */
      /* TDK test_commands(unauth_stubs_p); */

      printf("Trying to call register...");
      int pc_rc = unauth_stubs_p->angel_register_pc_client_stub("/u/MSTONE1/wlp/lib/native/zos/s390x/bbgzsafm", NULL);
      printf("Register RC is %i \n", pc_rc);


      /*
      printf("Sleeping for 60...\n");
      sleep(60);
      printf("Woke up!\n");
      */

      printf("Calling deregister function...");
      pc_rc = unauth_stubs_p->angel_deregister_pc_client_stub(NULL);
      printf("Deregister RC is %i \n", pc_rc);

    } else{
        printf("BPX4LOD failed, rc/rsn/rv %i / %i / %i \n", rc, rsn, rv);
    }
}
