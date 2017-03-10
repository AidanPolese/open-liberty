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
#include <string.h>
#include <stdio.h>

#include "include/angel_armv_services.h"
#include "include/angel_client_process_data.h"
#include "include/angel_dynamic_replaceable_module.h"
#include "include/angel_process_data.h"
#include "include/angel_task_data.h"
#include "include/bbgzsgoo.h"
#include "include/ieantc.h"
#include "include/mvs_enq.h"
#include "include/mvs_estae.h"
#include "include/mvs_iarv64.h"
#include "include/mvs_resmgr.h"
#include "include/mvs_storage.h"
#include "include/mvs_user_token_manager.h"
#include "include/mvs_utils.h"

#include "include/gen/ihapsa.h"
#include "include/gen/ihaassb.h"


// This is a RESMGR recovery routine.  Note that the prolog and epilog
// for this routine are very important, since RESMGR gives control in
// 31 bit mode, and we must change to 64 bit mode in the prolog (and back
// to 31 bit mode in the epilog).
#pragma prolog(fixedShimRESMGR,"ARMGRPRL")
#pragma epilog(fixedShimRESMGR,"ARMGREPL")
void fixedShimRESMGR(rmpl* rmpl_p, long long* user_p)
{
  int estaex_rc = -1;
  int estaex_rsn = -1;

  // Retry variable must be volatile
  volatile struct
  {
    int tried_to_access_sgoo : 1;
    int accessed_sgoo : 1;
    int tried_to_get_enq : 1;
    int got_enq : 1;
    int tried_to_release_enq : 1;
    int released_enq : 1;
    int tried_to_detach_sgoo : 1;
    int detached_sgoo: 1;
    int tried_to_call_drm : 1;
    int called_drm : 1;
    int tried_to_detach_all_armv : 1;
    int detached_all_armv : 1;
    int tried_to_access_apd : 1;
    int accessed_apd : 1;
    int tried_to_detach_shared_for_server :1 ;
    int detached_shared_for_server : 1;
    int _available : 15;
    int halt_recovery : 1;
  } retry_bits;


  retry_parms angel_retry_area;
  enqtoken enq_token;

  // -------------------------------------------------------------------------
  // Note that the trace level pointer in the stack prefix area is not set
  // up, so traces won't be enabled in any environment.  We could trace if
  // we were in the angel, and this was the task level RESMGR, but in all
  // other environments, we are either running in MASTER, or we're running
  // in code that doesn't belong to the server and therefore don't want to
  // trace.
  // -------------------------------------------------------------------------

  // -------------------------------------------------------------------------
  // The angel task data hung off of R13 is only valid for the duration of the
  // RESMGR.  Be sure that we don't create a thread level heap cache on this
  // task data.  Later, we'll be cleaning up the 'real' thread level heap
  // cache on the 'real' angel task data for this task, hung off the STCBBCBA.
  // -------------------------------------------------------------------------
  angel_task_data* atd_p = getAngelTaskData();
  atd_p->noTaskLevelHeapCache = 1;

  // -------------------------------------------------------------------------
  // Establish a user token that we can use with IARV64 services.
  // Any connections that we make are just for the life of this task
  // so use the task level user token.
  // -------------------------------------------------------------------------
  long long user_token = getTaskSupervisorStateUserToken();

  struct __csysenv_s rmgr_env;
  initenv(&rmgr_env, user_token, NULL);

  // -------------------------------------------------------------------------
  // Get some information about who we're recovering for.
  // -------------------------------------------------------------------------
  ascb* recovering_ascb_p = rmpl_p->rmplascb;
  assb* recovering_assb_p = recovering_ascb_p->ascbassb;

  // -------------------------------------------------------------------------
  // Note that we can't access the SGOO or angel_process_data yet because we
  // may not be connected to the shared memory segment yet.  Just look up the
  // addresses.
  // -------------------------------------------------------------------------
  angel_process_data* apd_p = getAngelProcessDataByStoken(&(recovering_assb_p->assbstkn));

  AngelPCParmArea_t* latentParms_p = (AngelPCParmArea_t*)(*user_p);
  bbgzsgoo* sgoo_p = latentParms_p->sgoo_p;

  if ((sgoo_p != NULL) && (apd_p != NULL)) {
      // ---------------------------------------------------------------------
      // Establish an ESTAE to retry in case we can't find our way to
      // the RESMGR in the dynamically replaceable module.
      // ---------------------------------------------------------------------
      memset((void*)(&retry_bits), 0, sizeof(retry_bits));
      memset(&angel_retry_area, 0, sizeof(angel_retry_area));
      establish_estaex_with_retry(&angel_retry_area,
                                  &estaex_rc,
                                  &estaex_rsn);

      // ---------------------------------------------------------------------
      // Get an ENQ so that more than one address space isn't trying
      // to connect MASTER to our shared memory segment.
      // ---------------------------------------------------------------------
      if ((rmpl_p->rmplflg1 & rmplterm) == rmplterm) {
          SET_RETRY_POINT(angel_retry_area);

          if (retry_bits.tried_to_get_enq == 0) {
              retry_bits.tried_to_get_enq = 1;
              get_enq_exclusive_system(BBGZ_ENQ_QNAME,
                                       ANGEL_BBGZSGOO_ENQ,
                                       NULL,
                                       &enq_token);
              retry_bits.got_enq = 1;
          } else {
              retry_bits.halt_recovery = 1;
          }

          SET_RETRY_POINT(angel_retry_area);

          if (retry_bits.tried_to_access_sgoo == 0)
          {
              retry_bits.tried_to_access_sgoo = 1;
              accessSharedAbove(sgoo_p, user_token);
              retry_bits.accessed_sgoo = 1;
          } else {
              retry_bits.halt_recovery = 1;
          }
      }

      // ---------------------------------------------------------------------
      // Try to copy some storage out of the angel process data.
      // ---------------------------------------------------------------------
      unsigned char armv_watermark = 0, as_type = 0;
      void* iptTask_p = NULL;
      SET_RETRY_POINT(angel_retry_area);
      if (retry_bits.tried_to_access_apd == 0) {
          retry_bits.tried_to_access_apd = 1;
          armv_watermark = apd_p->cur_armv_seq;
          as_type = apd_p->as_type;
          iptTask_p = apd_p->initial_pthread_creating_task_p;
          retry_bits.accessed_apd = 1;
      } else {
          retry_bits.halt_recovery = 1;
      }

      // ---------------------------------------------------------------------
      // Only try to branch to the dynamically replaceable module
      // once.  We should really be connecting to the ARMV before we
      // drive the RESMGR, but we may be running in MASTER which isn't
      // a Liberty address space.  So we rely on the ESTAE to save us
      // if something bad happens while we're in there.
      // ---------------------------------------------------------------------
      if (retry_bits.halt_recovery == 0) {
          SET_RETRY_POINT(angel_retry_area);
          if (retry_bits.tried_to_call_drm == 0) {
              retry_bits.tried_to_call_drm = 1;
              bbgzarmv* armv_p = (bbgzarmv*) sgoo_p->bbgzsgoo_armv;
              if (armv_p != NULL) {
                  struct bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
                  if (drm_p != NULL) {
                      drm_p->resmgr(rmpl_p, apd_p);
                  }
              }
              retry_bits.called_drm = 1;
          }

          // -----------------------------------------------------------------
          // If we're recovering for the IPT or for the overall address space,
          // we are done with the DRM, and should detach from it.  Don't
          // bother if we don't have angel process data, we probably didn't
          // get far enough to have an attachment to an ARMV.  Note that we
          // can't reference the angel process data directly because it may
          // have been freed by the DRM RESMGR.
          // -----------------------------------------------------------------
          if ((apd_p != NULL) && (as_type == ANGEL_PROCESS_TYPE_SERVER)) {
              SET_RETRY_POINT(angel_retry_area);

              if (retry_bits.tried_to_detach_all_armv == 0) {
                  retry_bits.tried_to_detach_all_armv = 1;
                  if (((rmpl_p->rmplflg1 & rmplterm) == rmplterm) ||
                      (iptTask_p == (rmpl_p->rmpltcba))) {
                      detachAllARMVs(armv_watermark, recovering_assb_p, FALSE);
                  }
                  retry_bits.detached_all_armv = 1;
              }

              // -------------------------------------------------------------
              // If recovering for the IPT, detach from the shared memory.
              // We'll be running in the server's address space.
              // -------------------------------------------------------------
              SET_RETRY_POINT(angel_retry_area);
              if (retry_bits.tried_to_detach_shared_for_server == 0) {
                  retry_bits.tried_to_detach_shared_for_server = 1;
                  if (((rmpl_p->rmplflg1 & rmplterm) != rmplterm) &&
                      (iptTask_p == (rmpl_p->rmpltcba))) {
                      detachSharedAbove(sgoo_p, getAddressSpaceSupervisorStateUserToken(), 0);
                  }
                  retry_bits.detached_shared_for_server = 1;
              }
          }
      }

      // ---------------------------------------------------------------
      // Cleanup
      // ---------------------------------------------------------------

      // ---------------------------------------------------------------
      // Disconnect from the shared memory if we connected.
      // ---------------------------------------------------------------
      SET_RETRY_POINT(angel_retry_area);
    
      if (retry_bits.accessed_sgoo == 1) {
          if (retry_bits.tried_to_detach_sgoo == 0) {
              retry_bits.tried_to_detach_sgoo = 1;
              detachSharedAbove(sgoo_p, user_token, 0);
              retry_bits.detached_sgoo = 1;
          }
      }

      // ---------------------------------------------------------------
      // Release the ENQ.
      // ---------------------------------------------------------------
      SET_RETRY_POINT(angel_retry_area);

      if (retry_bits.got_enq == 1) {
          if (retry_bits.tried_to_release_enq == 0) {
              retry_bits.tried_to_release_enq = 1;
              release_enq(&enq_token);
              retry_bits.released_enq = 1;
          }
      }

      // ---------------------------------------------------------------
      // Remove the ESTAE that we set up.
      // ---------------------------------------------------------------
      remove_estaex(&estaex_rc, &estaex_rsn);
  }

  termenv();
}

// This is the client RESMGR recovery routine.  Note that the prolog and epilog
// for this routine are very important, since RESMGR gives control in
// 31 bit mode, and we must change to 64 bit mode in the prolog (and back
// to 31 bit mode in the epilog).
#pragma prolog(fixedShimClientRESMGR,"ARMGRPRL")
#pragma epilog(fixedShimClientRESMGR,"ARMGREPL")
void fixedShimClientRESMGR(rmpl* rmpl_p, long long* user_p)
{
  int estaexRC = -1;
  int estaexRSN = -1;

  // Retry variable must be volatile
  volatile struct
  {
    int triedToAccessSgoo : 1;
    int accessedSgoo : 1;
    int triedToGetEnq : 1;
    int gotEnq : 1;
    int triedToReleaseEnq : 1;
    int releasedEnq : 1;
    int triedToDetachSgoo : 1;
    int detachedSgoo: 1;
    int triedToCallDrm : 1;
    int calledDrm : 1;
    int triedToDetachAllArmv : 1;
    int detachedAllArmv : 1;
    int triedToAccessApd : 1;
    int accessedApd : 1;
    int triedToDetachSharedForServer :1 ;
    int detachedSharedForServer : 1;
    int triedToFreeAngelMetalCEnv : 1;
    int freedAngelMetalCEnv : 1;
    int _available : 13;
    int haltRecovery : 1;
  } retryBits;


  retry_parms angelClientRetryArea;
  enqtoken enqToken;

  // -------------------------------------------------------------------------
  // The angel task data hung off of R13 is only valid for the duration of the
  // RESMGR.  Be sure that we don't create a thread level heap cache on this
  // task data.  The angel client heap is not set up to cache storage, but
  // we'll set this bit anyway.
  // -------------------------------------------------------------------------
  angel_task_data* atd_p = getAngelTaskData();
  atd_p->noTaskLevelHeapCache = 1;

  // -------------------------------------------------------------------------
  // Establish a user token that we can use with IARV64 services.
  // Any connections that we make are just for the life of this task
  // so use the task level user token.
  // -------------------------------------------------------------------------
  long long userToken = getTaskSupervisorStateUserToken();

  struct __csysenv_s rmgr_env;
  void* resmgrCEnv_p = initenv(&rmgr_env, userToken, NULL);

  // -------------------------------------------------------------------------
  // Get some information about who we're recovering for.
  // -------------------------------------------------------------------------
  ascb* recovering_ascb_p = rmpl_p->rmplascb;
  assb* recovering_assb_p = recovering_ascb_p->ascbassb;

  // -------------------------------------------------------------------------
  // Note that we can't access the SGOO or angel_client_process_data yet
  // because we may not be connected to the shared memory segment yet.  Just
  // look up the addresses.
  // -------------------------------------------------------------------------
  AngelPCParmArea_t* latentParms_p = (AngelPCParmArea_t*)(*user_p);
  AngelClientProcessData_t* acpd_p = getAngelClientProcessDataByStoken(&(recovering_assb_p->assbstkn), latentParms_p->angelAnchor_p);
  bbgzsgoo* sgoo_p = latentParms_p->sgoo_p;

  if ((sgoo_p != NULL) && (acpd_p != NULL)) {
      // ---------------------------------------------------------------------
      // Establish an ESTAE to retry in case we can't find our way to
      // the RESMGR in the dynamically replaceable module.
      // ---------------------------------------------------------------------
      memset((void*)(&retryBits), 0, sizeof(retryBits));
      memset(&angelClientRetryArea, 0, sizeof(angelClientRetryArea));
      establish_estaex_with_retry(&angelClientRetryArea,
                                  &estaexRC,
                                  &estaexRSN);

      // ---------------------------------------------------------------------
      // Get an ENQ so that more than one address space isn't trying
      // to connect MASTER to our shared memory segment.
      // ---------------------------------------------------------------------
      if ((rmpl_p->rmplflg1 & rmplterm) == rmplterm) {
          SET_RETRY_POINT(angelClientRetryArea);

          if (retryBits.triedToGetEnq == 0) {
              retryBits.triedToGetEnq = 1;
              get_enq_exclusive_system(BBGZ_ENQ_QNAME, ANGEL_BBGZSGOO_ENQ, NULL, &enqToken);
              retryBits.gotEnq = 1;
          } else {
              retryBits.haltRecovery = 1;
          }

          SET_RETRY_POINT(angelClientRetryArea);

          if (retryBits.triedToAccessSgoo == 0)
          {
              retryBits.triedToAccessSgoo = 1;
              accessSharedAbove(sgoo_p, userToken);
              retryBits.accessedSgoo = 1;
          } else {
              retryBits.haltRecovery = 1;
          }
      }

      // ---------------------------------------------------------------------
      // Try to copy some storage out of the angel process data.
      // ---------------------------------------------------------------------
      unsigned char armvWatermark = 0;
      tcb* iptOrJobstepTCB_p = NULL;
      struct __csysenvtoken_s* angelMetalCEnv_p = NULL;
      struct __csysenv_s*      angelMetalCEnvParms_p = NULL;
      unsigned short angelMetalCEnvParmsSubpool = 0;
      unsigned char angelMetalCEnvParmsKey = 0;
      SET_RETRY_POINT(angelClientRetryArea);
      if (retryBits.triedToAccessApd == 0) {
          retryBits.triedToAccessApd = 1;
          armvWatermark = acpd_p->curArmvSeq;
          iptOrJobstepTCB_p = acpd_p->tcbForTaskResmgr;
          angelMetalCEnv_p = acpd_p->cenv_p;
          angelMetalCEnvParms_p = acpd_p->cenvParms_p;
          angelMetalCEnvParmsSubpool = acpd_p->cenvParmsSubpool;
          angelMetalCEnvParmsKey = acpd_p->cenvParmsKey;
          retryBits.accessedApd = 1;
      } else {
          retryBits.haltRecovery = 1;
      }

      // ---------------------------------------------------------------------
      // Only try to branch to the dynamically replaceable module
      // once.  We should really be connecting to the ARMV before we
      // drive the RESMGR, but we may be running in MASTER which isn't
      // a Liberty address space.  So we rely on the ESTAE to save us
      // if something bad happens while we're in there.
      // ---------------------------------------------------------------------
      if (retryBits.haltRecovery == 0) {
          SET_RETRY_POINT(angelClientRetryArea);
          if (retryBits.triedToCallDrm == 0) {
              retryBits.triedToCallDrm = 1;
              bbgzarmv* armv_p = (bbgzarmv*) sgoo_p->bbgzsgoo_armv;
              if (armv_p != NULL) {
                  struct bbgzadrm* drm_p = armv_p->bbgzarmv_drm;
                  if (drm_p != NULL) {
                      // -----------------------------------------------------
                      // If we're running a task RESMGR, put the client's
                      // metal C environment on the task.
                      // -----------------------------------------------------
                      if (((rmpl_p->rmplflg1 & rmplterm) != rmplterm) &&
                          (iptOrJobstepTCB_p == (rmpl_p->rmpltcba))) {
                          setenvintoR12(angelMetalCEnv_p);
                      }
                      drm_p->clientResmgr(rmpl_p, acpd_p);
                      setenvintoR12(resmgrCEnv_p);
                  }
              }
              retryBits.calledDrm = 1;
          }

          // -----------------------------------------------------------------
          // If we're recovering for the IPT, jobstep task, or for the overall
          // client address space, we are done with the DRM, and should detach
          // from it.  Don't bother if we don't have angel process data, we
          // probably didn't get far enough to have an attachment to an ARMV.
          // Note that we can't reference the angel process data directly
          // because it may have been freed by the DRM RESMGR.
          // -----------------------------------------------------------------
          if (acpd_p != NULL) {
              SET_RETRY_POINT(angelClientRetryArea);

              if (retryBits.triedToDetachAllArmv == 0) {
                  retryBits.triedToDetachAllArmv = 1;
                  if (((rmpl_p->rmplflg1 & rmplterm) == rmplterm) ||
                      (iptOrJobstepTCB_p == (rmpl_p->rmpltcba))) {
                      detachAllARMVs(armvWatermark, recovering_assb_p, TRUE);
                  }
                  retryBits.detachedAllArmv = 1;
              }

              // -------------------------------------------------------------
              // If recovering for the IPT or jobstep, detach from the shared
              // memory.  We'll be running in the server's address space.
              // -------------------------------------------------------------
              SET_RETRY_POINT(angelClientRetryArea);
              if (retryBits.triedToDetachSharedForServer == 0) {
                  retryBits.triedToDetachSharedForServer = 1;
                  if (((rmpl_p->rmplflg1 & rmplterm) != rmplterm) &&
                      (iptOrJobstepTCB_p == (rmpl_p->rmpltcba))) {
                      detachSharedAbove(sgoo_p, getAddressSpaceSupervisorStateUserTokenWithBias((UserTokenBias_t*)&CLIENT_SGOO_BIAS), 0);
                  }
                  retryBits.detachedSharedForServer = 1;
              }

              // -------------------------------------------------------------
              // If recovering for the IPT or jobstep, destroy the metal C
              // environment and heap used by the angel owned client code.
              // -------------------------------------------------------------
              SET_RETRY_POINT(angelClientRetryArea);
              if (retryBits.triedToFreeAngelMetalCEnv == 0) {
                  retryBits.triedToFreeAngelMetalCEnv = 1;
                  if (((rmpl_p->rmplflg1 & rmplterm) != rmplterm) &&
                      (iptOrJobstepTCB_p == (rmpl_p->rmpltcba))) {
                      setenvintoR12(angelMetalCEnv_p);
                      termenv();
                      setenvintoR12(resmgrCEnv_p);
                      storageRelease(angelMetalCEnvParms_p, sizeof(struct __csysenv_s), angelMetalCEnvParmsSubpool, angelMetalCEnvParmsKey);
                  }
                  retryBits.freedAngelMetalCEnv = 1;
              }
          }
      }

      // ---------------------------------------------------------------
      // Cleanup
      // ---------------------------------------------------------------

      // ---------------------------------------------------------------
      // Disconnect from the shared memory if we connected.
      // ---------------------------------------------------------------
      SET_RETRY_POINT(angelClientRetryArea);

      if (retryBits.accessedSgoo == 1) {
          if (retryBits.triedToDetachSgoo == 0) {
              retryBits.triedToDetachSgoo = 1;
              detachSharedAbove(sgoo_p, userToken, 0);
              retryBits.detachedSgoo = 1;
          }
      }

      // ---------------------------------------------------------------
      // Release the ENQ.
      // ---------------------------------------------------------------
      SET_RETRY_POINT(angelClientRetryArea);

      if (retryBits.gotEnq == 1) {
          if (retryBits.triedToReleaseEnq == 0) {
              retryBits.triedToReleaseEnq = 1;
              release_enq(&enqToken);
              retryBits.releasedEnq = 1;
          }
      }

      // ---------------------------------------------------------------
      // Remove the ESTAE that we set up.
      // ---------------------------------------------------------------
      remove_estaex(&estaexRC, &estaexRSN);
  }

  termenv();
}

#pragma insert_asm(" IHAPSA")
#pragma insert_asm(" IKJTCB")
#pragma insert_asm(" IHASTCB")
#pragma insert_asm(" IEANTASM")
