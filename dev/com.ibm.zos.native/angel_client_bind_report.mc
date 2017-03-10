/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

// See entry point "main" for description of function.
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>

#include "include/angel_bgvt_services.h"
#include "include/angel_client_bind_data.h"
#include "include/angel_process_data.h"
#include "include/angel_sgoo_services.h"
#include "include/mvs_cell_pool_services.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_psw.h"
#include "include/mvs_user_token_manager.h"

register void* myenvtkn __asm("r12");

// Note - this is defined here to avoid including angel_process_data.o into
// this module (and all its friends).
angel_process_data* getAngelProcessDataFromNameToken(void) {
    return NULL;
}


// -----------------------------------------------------------------------
// Note - these structures are duplicated from mvs_cell_pool_services.mc
//
// They are internal mappings of the cell pools and are not intended for
// general consumption.
// -----------------------------------------------------------------------
struct cellpool_extent_internal_mapping {
  char eyecatcher[8];
  void* extent_p;
  void* cells_p;
  long long num_cells;
  struct cellpool_extent_internal_mapping* next_p;
  struct cellpool_internal_mapping* pool_p;
};

struct cellpool_internal_mapping {
  char eyecatcher[8];
  void* anchor_p;
  struct cellpool_extent_internal_mapping* extent_p;
  long long cell_size;
  getGrowCellPoolStorage_t* auto_grow_func;
  struct {
      int auto_grow : 1;
      int _availableflags : 31;
  } flags;
  char _available[20];
};

// -------------------------------
// End cell pool service mappings.
// -------------------------------

// A list of all the binds that we keep internally.
struct bindListNode {
    struct bindListNode* next_p;
    SToken clientStoken;
    SToken serverStoken;
    unsigned char angelProcessDataBind;
    unsigned char clientProcessDataBind;
    unsigned char serverIsUp;
    unsigned char clientIsUp;
};

/**
 * See if a bind has been found between the client and server in the specified list.
 */
static struct bindListNode* findNode(struct bindListNode* head_p, SToken* clientStoken_p, SToken* serverStoken_p) {
    struct bindListNode* curNode_p = head_p;
    struct bindListNode* foundNode_p = NULL;
    while ((curNode_p != NULL) && (foundNode_p == NULL)) {
        if ((memcmp(clientStoken_p, &(curNode_p->clientStoken), sizeof(SToken)) == 0) &&
            (memcmp(serverStoken_p, &(curNode_p->serverStoken), sizeof(SToken)) == 0)) {
            foundNode_p = curNode_p;
        }
        curNode_p = curNode_p->next_p;
    }

    return foundNode_p;
}

/** See if the address space represented by the STOKEN is still up.
 * @return 0 if up, non-zero if not.
 */
static int isUp(void* stoken_p) {
    int rc = -1;

    struct parm31 {
        char stoken[8];
        void* __ptr32 ascb_p;
        int rc;
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));

    if (parm_p != NULL) {
        memcpy(parm_p->stoken, stoken_p, sizeof(parm_p->stoken));

        __asm(" SAM31\n"
              " SYSSTATE AMODE64=NO\n"
              " LOCASCB STOKEN=%2\n"
              " ST 1,%0\n"
              " ST 15,%1\n"
              " SYSSTATE AMODE64=YES\n"
              " SAM64" :
              "=m"(parm_p->ascb_p),"=m"(parm_p->rc) :
              "m"(parm_p->stoken[0]) :
              "r0","r1","r14","r15");

        rc = parm_p->rc;

        free(parm_p);
    }

    return rc;
}

#pragma linkage(BPX4WRT,OS_NOSTACK)
void BPX4WRT(int filedes, void* buffer, int alet, int bufsize, int* retval, int* retcode, int* rsnval);

/* Print a string */
static void printString(const char* message_p) {
    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int filedes = 1; // stdout

    BPX4WRT(filedes, &message_p, 0, strlen(message_p), &retval, &retcode, &rsnval);
}

//
// Entry point
//
// This utility will report on the current client/server binds (between
// Liberty clients and Liberty servers).  Currently the only clients that
// exist are WOLA clients.
//
// Each Liberty server maintains a list of the bound clients in its angel
// process data.  Each client maintains a list of servers it is bound to in
// it's angel client process data.  We'll iterate both lists, making a view
// of who is bound to who.  If the client and servers both think that they
// are bound to the same processes, then that is good.  If there is an
// inconsistency, it will be noted in the report.
//
// The report will also show whether LOCASCB reports that the client and
// server processes are up or down.  Note that for servers or clients
// started at the USS Shell (via startServer, or other), the address space
// will live on beyond the life of the server, so the report may show that
// the process is still up, even though the server or client has stopped.
//
// Example output (informational messages excluded):
//  Client           Server           ClBnd SvBnd ClUP SvUP
//  0000014000000006 000001a000000003 X     X     X
//
// This shows there is one bind between a client and a server with the
// STOKENs shown.  ClBnd indicates the bind exists in the client's bind
// list, and SvBnd shows it exists in the server's bind list.  If either
// of these were blank, that would be bad, since the lists should be
// cleaned up at the same time, when the client unbinds or dies.  ClUP
// shows the client is up, SvUP shows the server is not up (since it
// does not have an X there).  In this case the server was a started task
// and had been stopped.  Since the client had not unbound yet, the bind
// is still present.
//
int main() {
    char msg[256];

    // -----------------------------------------------------------------------
    // Get into supervisor state.  This utility will run in supervisor
    // state key 2.
    // -----------------------------------------------------------------------
    bbgz_psw current_PSW;
    memset(&current_PSW, 0x00, sizeof(current_PSW));
    extractPSW(&current_PSW);

    if (current_PSW.key != 2) {
        printString("This program must be started in key 2.  Exiting...\n");
        return -1;
    }

    // -----------------------------------------------------------------------
    // Note: We're not using the switchToSupervisorState() in mvs_utils
    // because it requires us to drag in the heap manager right now.
    // -----------------------------------------------------------------------
    __asm(" MODESET MODE=SUP" : : : "r0","r1","r14","r15");

    memset(&current_PSW, 0x00, sizeof(current_PSW));
    extractPSW(&current_PSW);

    if (current_PSW.pbm_state == TRUE) {
        printString("This program could not switch to supervisor state.  Exiting...\n");
        return -1;
    }

    // Tell the metal C runtime library what parameters it should use if it has
    // to obtain storage on our behalf.
    struct __csysenv_s mysysenv;
    memset(&mysysenv, 0x00, sizeof(struct __csysenv_s));
    mysysenv.__cseversion = __CSE_VERSION_2;

    mysysenv.__csesubpool = 0;
    mysysenv.__cseheap64usertoken = getAddressSpaceSupervisorStateUserToken();

    /* Create a Metal C environment. */
    myenvtkn = (void * ) __cinit(&mysysenv);

    // Connect to the shared memory segment (BBGZSGOO)
    bgvt* __ptr32 bgvt_p = findBGVT();
    if (bgvt_p == NULL) {
        printString("There is no master BGVT.  Exiting...\n");
        __cterm((__csysenv_t) myenvtkn);
        return -1;
    }

    bbgzcgoo* __ptr32 cgoo_p = (bbgzcgoo* __ptr32) bgvt_p->bbodbgvt_bbgzcgoo;
    if (cgoo_p == NULL) {
        printString("There is no CGOO control block.  Exiting...\n");
        __cterm((__csysenv_t) myenvtkn);
        return -1;
    }

    bbgzsgoo* sgoo_p = (bbgzsgoo*) cgoo_p->bbgzcgoo_sgoo_p;
    if (sgoo_p == NULL) {
        printString("There is no SGOO control block.  Exiting...\n");
        __cterm((__csysenv_t) myenvtkn);
        return -1;
    }

    accessSharedAbove(sgoo_p, getAddressSpaceSupervisorStateUserToken());

    // Iterate the angel process data cells, and make a list of all binds that
    // servers know about.  If the bind is in an angel process data cell, then
    // the server knows about it.  The cells should not be returned to the
    // cell pool until there are no binds left.
    struct bindListNode* head_p = NULL;
    struct cellpool_internal_mapping* apdCellPool_p = (struct cellpool_internal_mapping*)
        sgoo_p->bbgzsgoo_angel_process_data_cellpool_id;

    struct cellpool_extent_internal_mapping* apdCellExtent_p = apdCellPool_p->extent_p;
    while (apdCellExtent_p != NULL) {
        angel_process_data* apd_p = (angel_process_data*) apdCellExtent_p->cells_p;
        for (long long x = 0; x < apdCellExtent_p->num_cells; x++) {
            if (memcmp(apd_p->eyecatcher, "BBGZAPD_", 8) == 0) {
                // See if the cell is allocated.  If it has binds, it should be allocated.
                long long cellAllocated = 0L;
                if (verifyCellInPool(sgoo_p->bbgzsgoo_angel_process_data_cellpool_id,
                                     apd_p, &cellAllocated) != 1L) {
                    snprintf(msg, sizeof(msg), "WARNING skipping APD cell %p because not within cell storage\n",
                             apd_p);
                    printString(msg);
                } else if (cellAllocated == 0L) {
                    if (apd_p->clientBindArea.count > 0) {
                        snprintf(msg, sizeof(msg), "WARNING un-allocated APD cell %p has a bind count of %i\n",
                                 apd_p, apd_p->clientBindArea.count);
                        printString(msg);
                    }

                    if (apd_p->clientBindHead_p != NULL) {
                        snprintf(msg, sizeof(msg), "WARNING un-allocated APD cell %p has a non-NULL bind list\n", apd_p);
                        printString(msg);
                    }

                    snprintf(msg, sizeof(msg), "Skipping APD cell %p because it is not allocated\n", apd_p);
                    printString(msg);
                } else {
                    int bindCount = 0;
                    AngelClientBindDataNode_t* clientBindDataNode_p = apd_p->clientBindHead_p;
                    while (clientBindDataNode_p != NULL) {
                        bindCount++;
                        AngelClientBindData_t* clientBindData_p = clientBindDataNode_p->data_p;
                        AngelClientProcessData_t* clientProcessData_p = clientBindData_p->clientProcessData_p;

                        struct bindListNode* node_p = findNode(head_p, &(clientProcessData_p->stoken),
                                                               &(apd_p->clientBindArea.serverStoken));

                        if (node_p != NULL) {
                            snprintf(msg, sizeof(msg), "WARNING duplicate client/server bind found %llx / %llx\n",
                                     *((long long*)(&(clientProcessData_p->stoken))),
                                     *((long long*)(&(apd_p->clientBindArea.serverStoken))));
                            printString(msg);
                        } else {
                            node_p = malloc(sizeof(struct bindListNode));
                            if (node_p == NULL) {
                                printString("ERROR out of memory processing APD.  Exiting...\n");
                                __cterm((__csysenv_t) myenvtkn);
                                return -1;
                            }

                            memset(node_p, 0, sizeof(*node_p));
                            memcpy(&(node_p->clientStoken), &(clientProcessData_p->stoken), sizeof(SToken));
                            memcpy(&(node_p->serverStoken), &(apd_p->clientBindArea.serverStoken), sizeof(SToken));
                            node_p->angelProcessDataBind = 1;
                            node_p->serverIsUp = (isUp(&(node_p->serverStoken)) == 0);
                            node_p->clientIsUp = (isUp(&(node_p->clientStoken)) == 0);

                            node_p->next_p = head_p;
                            head_p = node_p;
                        }
                        clientBindDataNode_p = clientBindDataNode_p->next_p;
                    }

                    if (bindCount != apd_p->clientBindArea.count) {
                        snprintf(msg, sizeof(msg), "WARNING bind count in APD cell %p did not match length of bind list %i / %i\n",
                                 apd_p, apd_p->clientBindArea.count, bindCount);
                        printString(msg);
                    }
                }
            } else {
                snprintf(msg, sizeof(msg), "Skipping APD at %p with bad eye catcher %.8s \n",
                         apd_p, apd_p->eyecatcher);
                printString(msg);
            }

            apd_p = apd_p + 1;
        }

        apdCellExtent_p = apdCellExtent_p->next_p;
    }

    // Now, iterate from the client side and match up the binds.  Hopefully we find them all.
    struct cellpool_internal_mapping* acpdCellPool_p = (struct cellpool_internal_mapping*)
        sgoo_p->bbgzsgoo_angelClientDataPool;

    struct cellpool_extent_internal_mapping* acpdCellExtent_p = acpdCellPool_p->extent_p;
    while (acpdCellExtent_p != NULL) {
        AngelClientProcessData_t* acpd_p = (AngelClientProcessData_t*) acpdCellExtent_p->cells_p;
        for (long long x = 0; x < acpdCellExtent_p->num_cells; x++) {
            if (memcmp(acpd_p->eyecatcher, "BBGZACPD", 8) == 0) {
                // See if the cell is allocated.  If it has binds, it should be allocated.
                long long cellAllocated = 0L;
                if (verifyCellInPool(sgoo_p->bbgzsgoo_angelClientDataPool,
                                     acpd_p, &cellAllocated) != 1L) {
                    snprintf(msg, sizeof(msg), "WARNING skipping ACPD cell %p because not within cell storage\n",
                             acpd_p);
                    printString(msg);
                } else if (cellAllocated == 0L) {
                    if (acpd_p->bindHead_p != NULL) {
                        snprintf(msg, sizeof(msg), "WARNING un-allocated ACPD cell %p has a non-NULL bind list\n",
                                 acpd_p);
                        printString(msg);
                    }

                    snprintf(msg, sizeof(msg), "Skipping ACPD cell %p because it is not allocated\n", acpd_p);
                    printString(msg);
                } else {
                    AngelClientBindDataNode_t* clientBindDataNode_p = acpd_p->bindHead_p;
                    while (clientBindDataNode_p != NULL) {
                        AngelClientBindData_t* clientBindData_p = clientBindDataNode_p->data_p;

                        struct bindListNode* node_p = findNode(head_p, &(acpd_p->stoken),
                                                               &(clientBindData_p->serverStoken));

                        if (node_p != NULL) {
                            node_p->clientProcessDataBind = 1;
                        } else {
                            node_p = malloc(sizeof(struct bindListNode));
                            if (node_p == NULL) {
                                printString("ERROR out of memory processing ACPD.  Exiting...\n");
                                __cterm((__csysenv_t) myenvtkn);
                                return -1;
                            }

                            memset(node_p, 0, sizeof(*node_p));
                            memcpy(&(node_p->clientStoken), &(acpd_p->stoken), sizeof(SToken));
                            memcpy(&(node_p->serverStoken), &(clientBindData_p->serverStoken), sizeof(SToken));
                            node_p->clientProcessDataBind = 1;
                            node_p->serverIsUp = (isUp(&(node_p->serverStoken)) == 0);
                            node_p->clientIsUp = (isUp(&(node_p->clientStoken)) == 0);

                            node_p->next_p = head_p;
                            head_p = node_p;
                        }
                        clientBindDataNode_p = clientBindDataNode_p->next_p;
                    }
                }
            } else {
                snprintf(msg, sizeof(msg), "Skipping ACPD at %p with bad eye catcher %.8s \n",
                         acpd_p, acpd_p->eyecatcher);
                printString(msg);
            }

            acpd_p = acpd_p + 1;
        }

        acpdCellExtent_p = acpdCellExtent_p->next_p;
    }

    // Now iterate our list and report any in-consistencies.
    if (head_p != NULL) {
        printString("Reporting client/server binds (stokens) and up/down status.");
        printString("ClUP and SvUP are reported by LOCASCB and may not be accurate for");
        printString("non-started tasks (ie started using the startServer command).");
        snprintf(msg, sizeof(msg), "Client           Server           ClBnd SvBnd ClUP SvUP \n");
        printString(msg);

        struct bindListNode* curNode_p = head_p;
        while (curNode_p != NULL) {
            snprintf(msg, sizeof(msg), "%16.16llx %16.16llx %.1s     %.1s     %.1s    %.1s \n",
                     *((long long*)(&(curNode_p->clientStoken))),
                     *((long long*)(&(curNode_p->serverStoken))),
                     ((curNode_p->clientProcessDataBind != 0) ? "X" : " "),
                     ((curNode_p->angelProcessDataBind != 0) ? "X" : " "),
                     ((curNode_p->clientIsUp != 0) ? "X" : " "),
                     ((curNode_p->serverIsUp != 0) ? "X" : " "));
            printString(msg);

            curNode_p = curNode_p->next_p;
        }
    } else {
        printString("WARNING No binds were found at the time this utility was run.\n");
    }

    // ----------------------------------------------------------------
    // Destroy the metal C environment.
    // ----------------------------------------------------------------
    __cterm((__csysenv_t) myenvtkn);

    return 0;
}
